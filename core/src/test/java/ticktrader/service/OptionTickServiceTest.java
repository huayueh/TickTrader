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

public class OptionTickServiceTest {

    @Mock
    private Strategy mockStrategy;

    private OptionTickService optionTickService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        // Base folder and year are not strictly necessary for testing wrapTick directly,
        // but the constructor requires them.
        optionTickService = new OptionTickService("dummy_base_folder", 0, mockStrategy);
    }

    @Test
    public void testWrapTick_validCallOptionInput() {
        //交易日期,商品代號,履約價格,到期月份(週別),買賣權別,成交時間,成交價格,成交數量(B or S)
        String line = "20230103,TXO,14000,202301,C,084501,200,5";
        Tick tick = optionTickService.wrapTick(line);
        assertNotNull(tick);
        assertEquals(LocalDateTime.of(2023, 1, 3, 8, 45, 1), tick.getTime());
        assertEquals("TXO", tick.getSymbol());
        assertEquals("202301", tick.getContract());
        assertEquals(14000, tick.getExPrice());
        assertEquals(FutureType.CALL, tick.getFutureType());
        assertEquals(200.0, tick.getPrice(), 0.001);
        assertEquals(5, tick.getQty());
    }

    @Test
    public void testWrapTick_validPutOptionInput() {
        String line = "20230103,TXO,14500,202301,P,090002,150,12";
        Tick tick = optionTickService.wrapTick(line);
        assertNotNull(tick);
        assertEquals(LocalDateTime.of(2023, 1, 3, 9, 0, 2), tick.getTime());
        assertEquals("TXO", tick.getSymbol());
        assertEquals("202301", tick.getContract());
        assertEquals(14500, tick.getExPrice());
        assertEquals(FutureType.PUT, tick.getFutureType());
        assertEquals(150.0, tick.getPrice(), 0.001);
        assertEquals(12, tick.getQty());
    }

    @Test
    public void testWrapTick_invalidDateNotDigits() {
        String line = "NOTADATE,TXO,14500,202301,P,090002,150,12";
        Tick tick = optionTickService.wrapTick(line);
        assertNull(tick);
    }

    @Test
    public void testWrapTick_incorrectNumberOfFields_tooFew() {
        String line = "20230103,TXO,14500,202301,P,090002,150"; // Missing qty
        Tick tick = optionTickService.wrapTick(line);
        assertNull(tick);
    }

    @Test
    public void testWrapTick_incorrectNumberOfFields_tooMany() {
         //交易日期,商品代號,履約價格,到期月份(週別),買賣權別,成交時間,成交價格,成交數量(B or S),開盤集合競價 (9 fields)
        String line = "20230103,TXO,14000,202301,C,084501,200,5,EXTRA_FIELD";
        Tick tick = optionTickService.wrapTick(line);
        assertNotNull(tick); // The method currently parses correctly if there are *at least* 8 fields
        assertEquals(LocalDateTime.of(2023, 1, 3, 8, 45, 1), tick.getTime());
        assertEquals("TXO", tick.getSymbol());
    }


    @Test
    public void testWrapTick_invalidPutCallValue() {
        // Using 'X' which is neither 'P' nor 'C'. It should default to CALL.
        String line = "20230103,TXO,14000,202301,X,084501,200,5";
        Tick tick = optionTickService.wrapTick(line);
        assertNotNull(tick);
        assertEquals(FutureType.CALL, tick.getFutureType());
    }

    @Test
    public void testWrapTick_timeWithMoreThan6Digits() {
        // The method should only take the first 6 digits for time.
        String line = "20230103,TXO,14000,202301,C,084501789,200,5";
        Tick tick = optionTickService.wrapTick(line);
        assertNotNull(tick);
        assertEquals(LocalDateTime.of(2023, 1, 3, 8, 45, 1), tick.getTime());
    }
}
