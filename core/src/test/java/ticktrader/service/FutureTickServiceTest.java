package ticktrader.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ticktrader.dto.FutureType;
import ticktrader.dto.Tick;
import ticktrader.strategy.Strategy;

import java.time.LocalDateTime;

import static org.junit.Assert.*;

public class FutureTickServiceTest {

    @Mock
    private Strategy mockStrategy;

    private FutureTickService futureTickService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        // Base folder and year are not strictly necessary for testing wrapTick directly,
        // but the constructor requires them.
        futureTickService = new FutureTickService("dummy_base_folder", 0, mockStrategy);
    }

    @Test
    public void testWrapTick_validFutureInput() {
        //交易日期,商品代號,到期月份(週別),成交時間,成交價格,成交數量(B+S),近月價格,遠月價格,開盤集合競價
        String line = "20230103,TX,202301,084501,14200,10,,,";
        Tick tick = futureTickService.wrapTick(line);
        assertNotNull(tick);
        assertEquals(LocalDateTime.of(2023, 1, 3, 8, 45, 1), tick.getTime());
        assertEquals("TX", tick.getSymbol());
        assertEquals("202301", tick.getContract());
        assertEquals(14200.0, tick.getPrice(), 0.001);
        assertEquals(10, tick.getQty());
        assertEquals(FutureType.FUTURE, tick.getFutureType());
        assertEquals(0, tick.getExPrice());
    }

    @Test
    public void testWrapTick_symbolNormalizationFIMTX() {
        String line = "20230103,FIMTX,202301,084501,14200,10,,,";
        Tick tick = futureTickService.wrapTick(line);
        assertNotNull(tick);
        assertEquals("MTX", tick.getSymbol());
    }

    @Test
    public void testWrapTick_symbolNormalizationMXF() {
        String line = "20230103,MXF,202301,084501,14200,10,,,";
        Tick tick = futureTickService.wrapTick(line);
        assertNotNull(tick);
        assertEquals("MTX", tick.getSymbol());
    }

    @Test
    public void testWrapTick_symbolNormalizationFITX() {
        String line = "20230103,FITX,202301,084501,14200,10,,,";
        Tick tick = futureTickService.wrapTick(line);
        assertNotNull(tick);
        assertEquals("TX", tick.getSymbol());
    }

    @Test
    public void testWrapTick_symbolNormalizationTXF() {
        String line = "20230103,TXF,202301,084501,14200,10,,,";
        Tick tick = futureTickService.wrapTick(line);
        assertNotNull(tick);
        assertEquals("TX", tick.getSymbol());
    }

    @Test
    public void testWrapTick_invalidDateNotDigits() {
        String line = "NOTADATE,TX,202301,084501,14200,10,,,";
        Tick tick = futureTickService.wrapTick(line);
        assertNull(tick);
    }

    @Test
    public void testWrapTick_incorrectNumberOfFields_tooFew() {
        String line = "20230103,TX,202301,084501,14200"; // Missing qty and other fields
        Tick tick = futureTickService.wrapTick(line);
        assertNull(tick);
    }

    @Test
    public void testWrapTick_timeFormatting() {
        String line = "20230103,TX,202301,134500,14200,10,,,"; // 13:45:00
        Tick tick = futureTickService.wrapTick(line);
        assertNotNull(tick);
        assertEquals(LocalDateTime.of(2023, 1, 3, 13, 45, 0), tick.getTime());
    }

    @Test
    public void testWrapTick_timeWithMoreThan6Digits() {
        // The method should only take the first 6 digits for time.
        String line = "20230103,TX,202301,084501789,14200,10,,,";
        Tick tick = futureTickService.wrapTick(line);
        assertNotNull(tick);
        assertEquals(LocalDateTime.of(2023, 1, 3, 8, 45, 1), tick.getTime());
    }
}
