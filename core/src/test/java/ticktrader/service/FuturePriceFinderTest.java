package ticktrader.service;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ticktrader.dto.Tick;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

public class FuturePriceFinderTest {

    private Path tempOpenPricesFile;
    private Path tempClosePricesFile;
    private FuturePriceFinder futurePriceFinder;

    private final String openTickData1 = "2023-01-15T10:00:00,SYM1,CONA,100.50,10";
    private final String openTickData2 = "2023-01-15T10:01:00,SYM2,CONB,200.75,20";
    private final String openTickData3 = "2023-01-16T09:30:00,SYM1,CONC,102.00,15";

    private final String closeTickData1 = "2023-01-15T16:00:00,SYM1,CONA,101.50,12";
    private final String closeTickData2 = "2023-01-15T16:01:00,SYM2,CONB,201.25,22";
    private final String closeTickData3 = "2023-01-16T15:59:00,SYM1,CONC,103.00,18";


    @Before
    public void setUp() throws IOException {
        tempOpenPricesFile = Files.createTempFile("test_open_prices_", ".csv");
        tempClosePricesFile = Files.createTempFile("test_close_prices_", ".csv");

        Files.write(tempOpenPricesFile, Arrays.asList(openTickData1, openTickData2, openTickData3), StandardOpenOption.APPEND);
        Files.write(tempClosePricesFile, Arrays.asList(closeTickData1, closeTickData2, closeTickData3), StandardOpenOption.APPEND);

        futurePriceFinder = new FuturePriceFinder(tempOpenPricesFile, tempClosePricesFile);
    }

    @After
    public void tearDown() throws IOException {
        Files.deleteIfExists(tempOpenPricesFile);
        Files.deleteIfExists(tempClosePricesFile);
    }

    @Test
    public void testConstructor_DataLoadingAndFindExisting() {
        // Test finding an OPEN tick
        Optional<Tick> foundOpenTick1 = futurePriceFinder.find(LocalDate.of(2023, 1, 15), "SYM1", FuturePriceFinder.Type.OPEN);
        assertTrue("Should find SYM1 open tick on 2023-01-15", foundOpenTick1.isPresent());
        assertEquals(100.50, foundOpenTick1.get().getPrice(), 0.001);
        assertEquals(LocalDateTime.of(2023,1,15,10,0,0), foundOpenTick1.get().getTime());

        // Test finding a CLOSE tick
        Optional<Tick> foundCloseTick2 = futurePriceFinder.find(LocalDate.of(2023, 1, 15), "SYM2", FuturePriceFinder.Type.CLOSE);
        assertTrue("Should find SYM2 close tick on 2023-01-15", foundCloseTick2.isPresent());
        assertEquals(201.25, foundCloseTick2.get().getPrice(), 0.001);
        assertEquals(LocalDateTime.of(2023,1,15,16,1,0), foundCloseTick2.get().getTime());

        // Test finding tick from open file in close map (should not be found by Type.CLOSE)
        Optional<Tick> notInCloseMap = futurePriceFinder.find(LocalDate.of(2023, 1, 16), "SYM1", FuturePriceFinder.Type.CLOSE);
        assertTrue("SYM1 on 2023-01-16 should be in close map", notInCloseMap.isPresent()); // It IS in close map
        assertEquals(103.00, notInCloseMap.get().getPrice(), 0.001);


        // Test finding tick from close file in open map (should not be found by Type.OPEN)
        Optional<Tick> notInOpenMap = futurePriceFinder.find(LocalDate.of(2023, 1, 16), "SYM1", FuturePriceFinder.Type.OPEN);
        assertTrue("SYM1 on 2023-01-16 should be in open map", notInOpenMap.isPresent()); // It IS in open map
         assertEquals(102.00, notInOpenMap.get().getPrice(), 0.001);
    }

    @Test
    public void testFind_SpecificOpenTick() {
        Optional<Tick> foundTick = futurePriceFinder.find(LocalDate.of(2023, 1, 16), "SYM1", FuturePriceFinder.Type.OPEN);
        assertTrue("Should find SYM1 open tick on 2023-01-16", foundTick.isPresent());
        Tick tick = foundTick.get();
        assertEquals("SYM1", tick.getSymbol());
        assertEquals("CONC", tick.getContract());
        assertEquals(102.00, tick.getPrice(), 0.001);
        assertEquals(15, tick.getQty());
        assertEquals(LocalDateTime.of(2023,1,16,9,30,0), tick.getTime());
    }

    @Test
    public void testFind_SpecificCloseTick() {
        Optional<Tick> foundTick = futurePriceFinder.find(LocalDate.of(2023, 1, 15), "SYM1", FuturePriceFinder.Type.CLOSE);
        assertTrue("Should find SYM1 close tick on 2023-01-15", foundTick.isPresent());
        Tick tick = foundTick.get();
        assertEquals("SYM1", tick.getSymbol());
        assertEquals("CONA", tick.getContract());
        assertEquals(101.50, tick.getPrice(), 0.001);
        assertEquals(12, tick.getQty());
        assertEquals(LocalDateTime.of(2023,1,15,16,0,0), tick.getTime());
    }

    @Test
    public void testFind_NonExisting_Date() {
        Optional<Tick> notFound = futurePriceFinder.find(LocalDate.of(2023, 1, 17), "SYM1", FuturePriceFinder.Type.OPEN);
        assertFalse("Should not find tick for non-existing date", notFound.isPresent());
    }

    @Test
    public void testFind_NonExisting_Symbol() {
        Optional<Tick> notFound = futurePriceFinder.find(LocalDate.of(2023, 1, 15), "NOSYMBOL", FuturePriceFinder.Type.OPEN);
        assertFalse("Should not find tick for non-existing symbol", notFound.isPresent());
    }

    @Test
    public void testFind_NonExisting_SymbolForDate() {
        Optional<Tick> notFound = futurePriceFinder.find(LocalDate.of(2023, 1, 15), "SYM3", FuturePriceFinder.Type.OPEN);
        assertFalse("Should not find tick for symbol not present on that date", notFound.isPresent());
    }


    @Test
    public void testFind_NullInputs() {
        Optional<Tick> notFoundNullDate = futurePriceFinder.find(null, "SYM1", FuturePriceFinder.Type.OPEN);
        assertFalse("Should return empty for null date", notFoundNullDate.isPresent());

        Optional<Tick> notFoundNullSymbol = futurePriceFinder.find(LocalDate.of(2023, 1, 15), null, FuturePriceFinder.Type.OPEN);
        assertFalse("Should return empty for null symbol", notFoundNullSymbol.isPresent());

        Optional<Tick> notFoundNullAll = futurePriceFinder.find(null, null, FuturePriceFinder.Type.OPEN);
        assertFalse("Should return empty for all null inputs", notFoundNullAll.isPresent());
    }
}
