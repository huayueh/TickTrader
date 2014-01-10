package com.nv.financial.chart.indicator;

import com.nv.financial.chart.dto.IndicatorValue;
import com.nv.financial.chart.dto.Quote;
import com.nv.financial.chart.quote.QuotePrice;
import com.nv.financial.chart.quote.provider.IQuoteProvider;
import com.tictactec.ta.lib.MInteger;
import com.tictactec.ta.lib.RetCode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

/**
 * Money Flow Index. Base on
 * http://en.wikipedia.org/wiki/Money_flow_index
 * http://codebase.mql4.com/303
 */
public class MFI extends AbstractIndicator {
    private static final Logger logger = LogManager.getLogger(MFI.class);
    private final int length;

    {
        longName = "Money Flow Index";
        shortName = "MFI";
        description = "unknow";
    }

    public MFI(IQuoteProvider quoteProvider,int length){
        super(quoteProvider);
        this.length = length;
    }

    @Override
    public IndicatorValue calculate(long time, QuotePrice quotePrice) {
        IndicatorValue ret = new IndicatorValue(time);
        int lookback = talib.atrLookback(length);
        List<Quote> qtSer = quoteProvider.getQuotesBefore(length+lookback, time);
        int size = qtSer.size();
        double[] high = new double[size];
        double[] low = new double[size];
        double[] close = new double[size];
        double[] vol = new double[size];
        double[] out = new double[size-lookback];
        MInteger outBegin = new MInteger();
        MInteger outNBElement = new MInteger();
        int idx = 0;
        for (Quote qt : qtSer){
            high[idx] = qt.getHigh();
            low[idx] = qt.getLow();
            close[idx] = qt.getClose();
            vol[idx] = qt.getVolume();
            idx++;
        }
        RetCode retCode = talib.mfi(0, size - 1, high, low, close, vol, length, outBegin, outNBElement, out);
        if(retCode == RetCode.Success && outNBElement.value > 0){
            double atr = out[outNBElement.value-1];
            ret.put(shortName, atr);
        } else {
            logger.error("TaLib error:" + retCode);
        }

        return ret;
    }
}
