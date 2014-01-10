package com.nv.financial.chart.indicator;

import com.nv.financial.chart.dto.IndicatorValue;
import com.nv.financial.chart.dto.Quote;
import com.nv.financial.chart.quote.QuotePrice;
import com.nv.financial.chart.quote.provider.IQuoteProvider;
import com.tictactec.ta.lib.MInteger;
import com.tictactec.ta.lib.RetCode;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Average True Range. Base on
 * http://stockcharts.com/school/doku.php?id=chart_school:technical_indicators:average_true_range_atr
 * http://en.wikipedia.org/wiki/Average_true_range
 * http://www.taindicators.com/2010/03/atr-average-true-range.html
 */
public class ATR extends AbstractIndicator {
    private static final Logger logger = LogManager.getLogger(ATR.class);
    private final int length;
    private final int k;

    {
        longName = "Average True Range";
        shortName = "ATR";
        description = "unknow";
    }

    public ATR(IQuoteProvider quoteProvider,int length){
        super(quoteProvider);
        this.length = length;
//        this.k = (int)Math.ceil(3.45 * (length + 1));
        this.k = Integer.MAX_VALUE;
    }

    @Override
    public IndicatorValue calculate(long time, QuotePrice quotePrice) {
        IndicatorValue ret = new IndicatorValue(time);
        int lookback = talib.atrLookback(length);
        List<Quote> qtSer = quoteProvider.getQuotesBefore(k, time);
        int size = qtSer.size();
        double[] high = new double[size];
        double[] low = new double[size];
        double[] close = new double[size];
        double[] out = new double[size-lookback];
        MInteger outBegin = new MInteger();
        MInteger outNBElement = new MInteger();
        int idx = 0;
        for (Quote qt : qtSer){
            high[idx] = qt.getHigh();
            low[idx] = qt.getLow();
            close[idx] = qt.getClose();
            idx++;
        }
        RetCode retCode = talib.atr(0, size - 1, high, low, close, length, outBegin, outNBElement, out);
        if(retCode == RetCode.Success && outNBElement.value > 0){
            double atr = out[outNBElement.value-1];
            ret.put(shortName, atr);
        } else {
            logger.error("TaLib error:" + retCode);
        }

        return ret;
    }
}
