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
 * Average Directional Movement Rating. Base on
 * http://baike.baidu.com/view/189274.htm?fromId=348882
 */
public class ADXR extends AbstractIndicator {
    private static final Logger logger = LogManager.getLogger(ADXR.class);
    private final int periodLength;
    //The weight omitted by stopping after k
    private final int k;

    {
        longName = "Average Directional Movement Rating";
        shortName = "ADXR";
        description = "unknow";
    }

    public ADXR(IQuoteProvider quoteProvider, int periodLength) {
        super(quoteProvider);
        this.periodLength = periodLength;
//        this.k = (int)Math.ceil(3.45 * (periodLength + 1));
        this.k = Integer.MAX_VALUE;
    }

    @Override
    public IndicatorValue calculate(long time, QuotePrice quotePrice) {
        IndicatorValue ret = new IndicatorValue(time);
        List<Quote> qtSer = quoteProvider.getQuotesBefore(k, time);
        int size = qtSer.size();
        double[] high = new double[size];
        double[] low = new double[size];
        double[] close = new double[size];
        double[] out = new double[size];
        MInteger outBegin = new MInteger();
        MInteger outNBElement = new MInteger();
        int idx = 0;
        for (Quote qt : qtSer){
            high[idx] = qt.getHigh();
            low[idx] = qt.getLow();
            close[idx] = qt.getClose();
            idx++;
        }
        RetCode retCode = talib.adxr(0, size - 1, high, low, close, periodLength, outBegin, outNBElement, out);
        if(retCode == RetCode.Success && outNBElement.value > 0){
            double taValue = out[outNBElement.value-1];
            ret.put(shortName, taValue);
        } else {
            logger.error("TaLib error:" + retCode);
        }

        return ret;
    }
}
