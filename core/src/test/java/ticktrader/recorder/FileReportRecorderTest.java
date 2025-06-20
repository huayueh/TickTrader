package ticktrader.recorder;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ticktrader.dto.FutureType;
import ticktrader.dto.Order;
import ticktrader.dto.Position;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class FileReportRecorderTest {

    private Path tempFile;
    private FileReportRecorder recorder;

    @Before
    public void setUp() throws IOException {
        // Ensure locale is consistent for double to string conversion if needed, though default is usually fine.
        Locale.setDefault(Locale.US);
        tempFile = Files.createTempFile("test_report_", ".rpt");
        recorder = new FileReportRecorder(tempFile);
    }

    private void closeRecorder() {
        try {
            if (recorder instanceof AutoCloseable) {
                ((AutoCloseable) recorder).close();
            }
            // Removed recorder.done() from here. done() is for generating report content, not for closing/flushing.
            // Flushing should be handled by AbstractFileRecorder's write/close mechanism.
        } catch (Exception e) {
            System.err.println("Error closing recorder: " + e.getMessage());
        }
    }

    @After
    public void tearDown() throws IOException {
        closeRecorder();
        if (tempFile != null) {
            Files.deleteIfExists(tempFile);
        }
    }

    private Position createPosition(String symbol, String contract, double openPrice, int qty, Order.Side side,
                                    LocalDateTime openTime, double closePrice, LocalDateTime closeTime,
                                    double pnl, double netPnl, FutureType type, int exPrice) {
        Order order = new Order.Builder()
                .symbol(symbol)
                .contract(contract)
                .price(openPrice)
                .qty(qty)
                .side(side)
                .putOrCall(type)
                .exercisePrice(exPrice)
                .build();
        Position position = new Position(order, openTime);
        position.fillAllQuantity(closePrice, closeTime);
        position.setPnl(pnl);
        position.setNetPnl(netPnl);
        return position;
    }

    @Test
    public void testRecordAndDone_NoPositions() throws IOException {
        recorder.done();
        closeRecorder(); // Ensure file is written

        // Based on FileReportRecorder, it will try to access pnl of null positions, etc.
        // And division by zero will result in NaN or Infinity for doubles.
        // MaxDrawDownPeriod and MaxDrawUpPeriod will cause NPE if their start/end positions are null.
        // This test will likely reveal several NPEs or NaN/Infinity values.

        // Expected lines based on current implementation with no data (potential for NPEs/NaN)
        // This is a "characterization test" for current behavior.
        // Actual output might vary based on how NPEs are handled or if they occur before writing a line.
        List<String> lines = Files.readAllLines(tempFile);
        // Expected lines after robust done() method
        assertEquals("TotalPnl : 0.0", lines.get(0));
        assertEquals("AverageWin : NaN", lines.get(1));
        assertEquals("AverageLose : NaN", lines.get(2));
        assertEquals("WinLoseRate : NaN", lines.get(3));
        assertEquals("WinningRate : 0.0", lines.get(4)); // Changed from NaN due to (cnt > 0) check
        assertEquals("MaxWin : N/A", lines.get(5));
        assertEquals("MaxLoss : N/A", lines.get(6));
        assertEquals("MaxDrawUp : 0.0", lines.get(7));
        assertEquals("MaxDrawUpPeriod : N/A", lines.get(8));
        assertEquals("MaxWinCnt : 0.0", lines.get(9));
        assertEquals("MaxDrawDown : 0.0", lines.get(10));
        assertEquals("MaxLoseCnt : 0.0", lines.get(11));
        assertEquals("MaxDrawDownPeriod : N/A", lines.get(12));
        assertEquals("Expected 13 lines in report for no positions", 13, lines.size());
    }

    @Test
    public void testRecordAndDone_SingleWin() throws IOException {
        LocalDateTime openTime = LocalDateTime.of(2023,1,1,10,0);
        LocalDateTime closeTime = LocalDateTime.of(2023,1,1,11,0);
        Position pos1 = createPosition("SYM", "CON1", 100, 1, Order.Side.Buy,
                openTime, 110, closeTime,
                10.0, 9.0, FutureType.FUTURE, 0);
        recorder.record(pos1);
        recorder.done();
        closeRecorder();

        List<String> lines = Files.readAllLines(tempFile);
        // for (String line : lines) { System.out.println(line); } // Manual debug

        assertEquals("TotalPnl : 10.0", lines.get(0));
        assertEquals("AverageWin : 10.0", lines.get(1));
        assertEquals("AverageLose : NaN", lines.get(2)); // No losses
        assertEquals("WinLoseRate : NaN", lines.get(3)); // AvgLoss is 0 (or NaN leading to NaN)
        assertEquals("WinningRate : 1.0", lines.get(4));
        String expectedMaxWinStringP1 = "MaxWin : Position[Order[SYM,CON1,100.0,1.0,Buy]," + openTime.toString() + ",110.0," + closeTime.toString() + "]10.0";
        assertEquals(expectedMaxWinStringP1, lines.get(5));
        // MaxLoss is also pos1 initially because it's the only trade
        String expectedMaxLossStringP1 = "MaxLoss : Position[Order[SYM,CON1,100.0,1.0,Buy]," + openTime.toString() + ",110.0," + closeTime.toString() + "]10.0";
        assertEquals(expectedMaxLossStringP1, lines.get(6));
        assertEquals("MaxDrawUp : 10.0", lines.get(7)); // Finalized current drawup
        assertEquals("MaxDrawUpPeriod : " + openTime + " ~ " + closeTime, lines.get(8));
        assertEquals("MaxWinCnt : 1.0", lines.get(9)); // Corrected expectation
        assertEquals("MaxDrawDown : 0.0", lines.get(10)); // No drawdown
        assertEquals("MaxLoseCnt : 0.0", lines.get(11)); // Corrected expectation
        assertEquals("MaxDrawDownPeriod : N/A", lines.get(12));
        assertEquals(13, lines.size());
    }

    @Test
    public void testRecordAndDone_SingleLoss() throws IOException {
        LocalDateTime openTime = LocalDateTime.of(2023,1,2,10,0);
        LocalDateTime closeTime = LocalDateTime.of(2023,1,2,11,0);
        Position pos1 = createPosition("SYM", "CONL1", 100, 1, Order.Side.Sell,
                openTime, 110, closeTime, // Sold at 100, covered at 110 -> loss
                -10.0, -11.0, FutureType.FUTURE, 0);
        recorder.record(pos1);
        recorder.done();
        closeRecorder();

        List<String> lines = Files.readAllLines(tempFile);
        // for (String line : lines) { System.out.println(line); }

        assertEquals("TotalPnl : -10.0", lines.get(0));
        assertEquals("AverageWin : NaN", lines.get(1)); // No wins
        assertEquals("AverageLose : -10.0", lines.get(2));
        assertEquals("WinLoseRate : 0.0", lines.get(3)); // Corrected: Math.abs(0 / -10.0) = 0.0
        assertEquals("WinningRate : 0.0", lines.get(4));
        // MaxWin is also pos1 initially
        String expectedMaxWinStringSL1 = "MaxWin : Position[Order[SYM,CONL1,100.0,1.0,Sell]," + openTime.toString() + ",110.0," + closeTime.toString() + "]-10.0";
        assertEquals(expectedMaxWinStringSL1, lines.get(5));
        String expectedMaxLossStringSL1 = "MaxLoss : Position[Order[SYM,CONL1,100.0,1.0,Sell]," + openTime.toString() + ",110.0," + closeTime.toString() + "]-10.0";
        assertEquals(expectedMaxLossStringSL1, lines.get(6));
        assertEquals("MaxDrawUp : 0.0", lines.get(7)); // No drawup
        assertEquals("MaxDrawUpPeriod : N/A", lines.get(8));
        assertEquals("MaxWinCnt : 0.0", lines.get(9)); // Corrected expectation
        assertEquals("MaxDrawDown : -10.0", lines.get(10)); // Finalized current drawdown
        assertEquals("MaxLoseCnt : 1.0", lines.get(11)); // Corrected expectation
        assertEquals("MaxDrawDownPeriod : " + openTime + " ~ " + closeTime, lines.get(12));
        assertEquals(13, lines.size());
    }

    @Test
    public void testRecordAndDone_MixedSequence() throws IOException {
        LocalDateTime t1o = LocalDateTime.of(2023,1,1,10,0); LocalDateTime t1c = LocalDateTime.of(2023,1,1,11,0);
        Position p1 = createPosition("S", "C1", 100, 1, Order.Side.Buy, t1o, 110, t1c, 10, 9, FutureType.FUTURE, 0); // W (+10)
        LocalDateTime t2o = LocalDateTime.of(2023,1,2,10,0); LocalDateTime t2c = LocalDateTime.of(2023,1,2,11,0);
        Position p2 = createPosition("S", "C2", 110, 1, Order.Side.Buy, t2o, 100, t2c, -10, -11, FutureType.FUTURE, 0); // L (-10)
        LocalDateTime t3o = LocalDateTime.of(2023,1,3,10,0); LocalDateTime t3c = LocalDateTime.of(2023,1,3,11,0);
        Position p3 = createPosition("S", "C3", 100, 1, Order.Side.Buy, t3o, 120, t3c, 20, 19, FutureType.FUTURE, 0); // W (+20)
        LocalDateTime t4o = LocalDateTime.of(2023,1,4,10,0); LocalDateTime t4c = LocalDateTime.of(2023,1,4,11,0);
        Position p4 = createPosition("S", "C4", 120, 1, Order.Side.Buy, t4o, 130, t4c, 10, 9, FutureType.FUTURE, 0); // W (+10)
        LocalDateTime t5o = LocalDateTime.of(2023,1,5,10,0); LocalDateTime t5c = LocalDateTime.of(2023,1,5,11,0);
        Position p5 = createPosition("S", "C5", 130, 1, Order.Side.Buy, t5o, 100, t5c, -30, -31, FutureType.FUTURE, 0); // L (-30)

        recorder.record(p1); // PNL: 10
        recorder.record(p2); // PNL: -10. curMaxDrawup=10 (p1), maxDrawUp=10 (p1~p1). curMaxWinCnt=1, maxWinCnt=1. curMaxDrawdown=-10 (p2), curMaxLoseCnt=1. curMaxDrawStart=p2
        recorder.record(p3); // PNL: 20. curMaxDrawdown=-10 (p2), maxDrawdown=-10 (p2~p2). curMaxLoseCnt=1, maxLoseCnt=1. curMaxDrawup=20 (p3), curMaxWinCnt=1. curMaxDrawStart=p3
        recorder.record(p4); // PNL: 10. curMaxDrawup=20+10=30 (p3,p4). curMaxWinCnt=2.
        recorder.record(p5); // PNL: -30. curMaxDrawup=30 (p3,p4), maxDrawUp=30 (p3~p4). curMaxWinCnt=2, maxWinCnt=2. curMaxDrawdown=-30 (p5), curMaxLoseCnt=1. curMaxDrawStart=p5

        recorder.done(); // Finalizes: maxDrawdown=-30 (p5~p5), maxLoseCnt=1. maxDrawup=30 (p3~p4), maxWinCnt=2.
        closeRecorder();

        List<String> lines = Files.readAllLines(tempFile);
        // for (String line : lines) { System.out.println(line); }

        assertEquals("TotalPnl : 0.0", lines.get(0)); // 10 - 10 + 20 + 10 - 30 = 0
        assertEquals("AverageWin : 13.333333333333334", lines.get(1)); // (10+20+10)/3 = 40/3
        assertEquals("AverageLose : -20.0", lines.get(2)); // (-10-30)/2 = -40/2
        assertEquals("WinLoseRate : 0.6666666666666667", lines.get(3)); // (40/3) / abs(-40/2) = (40/3) / 20 = 2/3
        assertEquals("WinningRate : 0.6", lines.get(4)); // 3/5
        String expectedMaxWinMixed = "MaxWin : Position[Order[S,C3,100.0,1.0,Buy]," + t3o.toString() + ",120.0," + t3c.toString() + "]20.0";
        assertEquals(expectedMaxWinMixed, lines.get(5));
        String expectedMaxLossMixed = "MaxLoss : Position[Order[S,C5,130.0,1.0,Buy]," + t5o.toString() + ",100.0," + t5c.toString() + "]-30.0";
        assertEquals(expectedMaxLossMixed, lines.get(6));
        assertEquals("MaxDrawUp : 30.0", lines.get(7)); // p3+p4 = 20+10=30
        assertEquals("MaxDrawUpPeriod : " + t3o + " ~ " + t4c, lines.get(8));
        assertEquals("MaxWinCnt : 2.0", lines.get(9));
        assertEquals("MaxDrawDown : -30.0", lines.get(10)); // p5 = -30
        assertEquals("MaxLoseCnt : 1.0", lines.get(11)); // p2 is one, p5 is one. Max is 1.
        assertEquals("MaxDrawDownPeriod : " + t5o + " ~ " + t5c, lines.get(12));
        assertEquals(13, lines.size());
    }
}
