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
import java.time.format.DateTimeFormatter; // Though not directly used for assertion string, good for reference
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class FilePositionRecorderTest {

    private Path tempFile;
    private FilePositionRecorder recorder;
    private final String expectedHeader = "Symbol,Contract,Open Time,Open Price,Qty,Side,Close Price,Close Time,Future Type, ExPrice,Net PNL, PNL";

    @Before
    public void setUp() throws IOException {
        tempFile = Files.createTempFile("test_positions_", ".csv");
        // Recorder is initialized here, which should write the header.
        recorder = new FilePositionRecorder(tempFile);
    }

    @Test
    public void testHeaderWritten() throws IOException {
        // Header is written in constructor. Close the recorder to ensure flush.
        closeRecorder();

        List<String> lines = Files.readAllLines(tempFile);
        assertTrue("File should not be empty after header write", !lines.isEmpty());
        assertEquals("Header row does not match", expectedHeader, lines.get(0));
    }

    @Test
    public void testRecordPosition() throws IOException {
        // 1. Create Order and Position objects
        Order order = new Order.Builder()
                .symbol("TESTSYM")
                .contract("TESTCON2403")
                .price(150.75)
                .qty(10)
                .side(Order.Side.Buy)
                .putOrCall(FutureType.FUTURE) // Explicitly FUTURE
                .exercisePrice(0) // Typically 0 for FUTURE
                .build();

        LocalDateTime openTime = LocalDateTime.of(2024, 1, 10, 9, 30, 0);
        Position position = new Position(order, openTime);

        LocalDateTime closeTime = LocalDateTime.of(2024, 1, 11, 14, 15, 30);
        position.fillAllQuantity(155.25, closeTime); // Sets closePrice and closeTime
        position.setPnl(45.0); // (155.25 - 150.75) * 10 (example, not exact calc)
        position.setNetPnl(40.0); // Example net PNL

        // 2. Record the position
        recorder.record(position);
        closeRecorder(); // Ensure data is flushed

        // 3. Read content and assert
        List<String> lines = Files.readAllLines(tempFile);
        assertEquals("File should contain header and one data line", 2, lines.size());
        assertEquals("Header row should be present", expectedHeader, lines.get(0));

        // Construct expected data string based on FilePositionRecorder's ToStringBuilder logic
        // Symbol,Contract,Open Time,Open Price,Qty,Side,Close Price,Close Time,Future Type,ExPrice,Net PNL,PNL
        String expectedDataLine =
            order.getSymbol() + "," +
            order.getContract() + "," +
            position.getOpenTime().toString() + "," + // Default LocalDateTime.toString() format
            order.getPrice() + "," +
            order.getQty() + "," + // Changed to use double directly
            order.getSide().name() + "," +
            position.getClosePrice() + "," +
            position.getCloseTime().toString() + "," + // Default LocalDateTime.toString() format
            order.getFutureType().name() + "," + // FutureType is enum, so .name()
            order.getExPrice() + "," +
            position.getNetPnl() + "," +
            position.getPnl();

        assertEquals(expectedDataLine, lines.get(1));
    }

    @Test
    public void testRecordOptionPosition() throws IOException {
        Order optionOrder = new Order.Builder()
                .symbol("OPTSYM")
                .contract("OPTCON2403C150")
                .price(5.25)
                .qty(5)
                .side(Order.Side.Sell)
                .putOrCall(FutureType.CALL)
                .exercisePrice(15000) // Exercise price for option
                .build();

        LocalDateTime openTime = LocalDateTime.of(2024, 1, 15, 10, 0, 0);
        Position optionPosition = new Position(optionOrder, openTime);

        LocalDateTime closeTime = LocalDateTime.of(2024, 1, 16, 11, 30, 0);
        optionPosition.fillAllQuantity(3.15, closeTime);
        optionPosition.setPnl(1050.0); // (5.25 - 3.15) * 5 * 100 (example option multiplier)
        optionPosition.setNetPnl(1000.0);

        recorder.record(optionPosition);
        closeRecorder();

        List<String> lines = Files.readAllLines(tempFile);
        assertEquals("File should contain header and one data line for option", 2, lines.size());
        assertEquals("Header row should be present", expectedHeader, lines.get(0));

        String expectedDataLine =
            optionOrder.getSymbol() + "," +
            optionOrder.getContract() + "," +
            optionPosition.getOpenTime().toString() + "," +
            optionOrder.getPrice() + "," +
            optionOrder.getQty() + "," + // Changed to use double directly
            optionOrder.getSide().name() + "," +
            optionPosition.getClosePrice() + "," +
            optionPosition.getCloseTime().toString() + "," +
            optionOrder.getFutureType().name() + "," +
            optionOrder.getExPrice() + "," +
            optionPosition.getNetPnl() + "," +
            optionPosition.getPnl();

        assertEquals(expectedDataLine, lines.get(1));
    }


    private void closeRecorder() {
        try {
            if (recorder instanceof AutoCloseable) {
                ((AutoCloseable) recorder).close();
            }
            // If AbstractFileRecorder has a specific public close/flush method, call that.
            // For now, relying on AutoCloseable or hoping writes are flushed.
        } catch (Exception e) {
            System.err.println("Error closing recorder: " + e.getMessage());
        }
    }

    @After
    public void tearDown() throws IOException {
        // Ensure recorder is closed before deleting, if not done by test.
        closeRecorder();

        if (tempFile != null) {
            Files.deleteIfExists(tempFile);
        }
    }
}
