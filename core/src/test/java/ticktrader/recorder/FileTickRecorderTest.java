package ticktrader.recorder;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ticktrader.dto.Tick;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class FileTickRecorderTest {

    private Path tempFile;
    private FileTickRecorder recorder;

    @Before
    public void setUp() throws IOException {
        // Create a temporary file for each test
        tempFile = Files.createTempFile("test_ticks_", ".csv");
        recorder = new FileTickRecorder(tempFile);
    }

    @Test
    public void testRecord() throws IOException {
        // 1. Create a Tick object with sample data
        Tick tick = new Tick();
        LocalDateTime tickTime = LocalDateTime.of(2023, 10, 26, 10, 30, 15);
        tick.setTime(tickTime);
        tick.setSymbol("TESTSYM");
        tick.setContract("TESTCON2312");
        tick.setPrice(123.45);
        tick.setQty(100);

        // 2. Record the tick
        recorder.record(tick);

        // 3. Explicitly close the recorder to ensure data is flushed to the file
        // Assuming AbstractFileRecorder or FileTickRecorder implements AutoCloseable or has a close() method.
        // If it doesn't, this test might require changes to FileTickRecorder or AbstractFileRecorder
        // to ensure data is written before assertion. For now, we'll try to call close().
        // If 'close()' is not available directly on FileTickRecorder, this might indicate a need to
        // call a method from AbstractFileRecorder if accessible, or it's handled by try-with-resources
        // if the recorder was used that way (not the case here for a single record call).
        // Let's assume a close method for now as good practice for file resources.
        try {
            // Attempt to call close() if it exists (e.g., if it implements AutoCloseable)
            if (recorder instanceof AutoCloseable) {
                ((AutoCloseable) recorder).close();
            } else {
                // If no direct close(), we hope the write is synchronous enough or AbstractFileRecorder handles it.
                // This is a potential point of failure if writes are heavily buffered and not flushed.
                // For robust testing, AbstractFileRecorder should ideally provide a flush() or ensure record() flushes.
            }
        } catch (Exception e) {
            // Handle exceptions during close, though for this test, we mainly care about flushing.
            System.err.println("Error closing recorder: " + e.getMessage());
        }


        // 4. Read the content of the temporary file
        List<String> lines = Files.readAllLines(tempFile);

        // 5. Assert that it matches the expected format and data
        assertEquals("Should be one line recorded", 1, lines.size());

        // Adjust expected line to match Tick.toString() behavior
        // ZonedDateTime.of(localDateTime, ZoneId.systemDefault()).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
        // + "," + symbol + "," + contract + "," + price + "," + qty + "," + futureType
        String expectedLine = tick.getTime().atZone(java.time.ZoneId.systemDefault()).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME) + "," +
                              tick.getSymbol() + "," +
                              tick.getContract() + "," +
                              tick.getPrice() + "," +
                              tick.getQty() + "," +
                              tick.getFutureType().toString(); // futureType is appended
        assertEquals(expectedLine, lines.get(0));
    }

    @Test
    public void testRecord_MultipleTicks() throws IOException {
        Tick tick1 = new Tick();
        LocalDateTime tickTime1LocalDateTime = LocalDateTime.of(2023, 10, 26, 10, 30, 15);
        tick1.setTime(tickTime1LocalDateTime);
        tick1.setSymbol("SYM1");
        tick1.setContract("CON1");
        tick1.setPrice(100.0);
        tick1.setQty(10);
        // tick1.setFutureType(FutureType.FUTURE); // Default

        Tick tick2 = new Tick();
        LocalDateTime tickTime2LocalDateTime = LocalDateTime.of(2023, 10, 26, 10, 31, 0);
        tick2.setTime(tickTime2LocalDateTime);
        tick2.setSymbol("SYM2");
        tick2.setContract("CON2");
        tick2.setPrice(200.50);
        tick2.setQty(20);
        // tick2.setFutureType(FutureType.FUTURE); // Default

        recorder.record(tick1);
        recorder.record(tick2);

        try {
            if (recorder instanceof AutoCloseable) {
                ((AutoCloseable) recorder).close();
            }
        } catch (Exception e) {
            System.err.println("Error closing recorder: " + e.getMessage());
        }

        List<String> lines = Files.readAllLines(tempFile);
        assertEquals("Should be two lines recorded", 2, lines.size());

        String expectedLine1 = tick1.getTime().atZone(java.time.ZoneId.systemDefault()).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME) +
                               ",SYM1,CON1,100.0,10," + tick1.getFutureType().toString();
        String expectedLine2 = tick2.getTime().atZone(java.time.ZoneId.systemDefault()).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME) +
                               ",SYM2,CON2,200.5,20," + tick2.getFutureType().toString();

        assertEquals(expectedLine1, lines.get(0));
        assertEquals(expectedLine2, lines.get(1));
    }


    @After
    public void tearDown() throws IOException {
        // Clean up the temporary file
        if (tempFile != null) {
            // Ensure recorder is closed before deleting the file, if not already closed by the test.
            // This handles cases where a test might fail before explicitly closing.
            try {
                 if (recorder instanceof AutoCloseable) {
                    ((AutoCloseable) recorder).close();
                }
            } catch (Exception e) {
                // Log or ignore, as we are primarily concerned with deleting the file.
                System.err.println("Error closing recorder in tearDown: " + e.getMessage());
            }
            Files.deleteIfExists(tempFile);
        }
    }
}
