package com.nv.financial.chart.indicator;

import com.nv.financial.chart.dto.IndicatorValue;
import com.nv.financial.chart.dto.Quote;
import com.nv.financial.chart.quote.QuotePrice;
import com.nv.financial.chart.quote.provider.IQuoteProvider;
import com.tictactec.ta.lib.MInteger;
import com.tictactec.ta.lib.RetCode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Chaikin Oscillator. Base on
 * http://stockcharts.com/school/doku.php?id=chart_school:technical_indicators:chaikin_oscillator
 */
public class CHO extends AbstractIndicator {
    private static final Logger logger = LogManager.getLogger(CHO.class);
    private final int shortLength;
    private final int longLength;
    //The weight omitted by stopping after longK
    private final int longK;
    //The weight omitted by stopping after shortK
    private final int shortK;

    {
        longName = "Chaikin Oscillator";
        shortName = "CHO";
        description = "unknow";
    }

    public CHO(IQuoteProvider quoteProvider,int shortLength, int longLength){
        super(quoteProvider);
        this.shortLength = shortLength;
        this.longLength = longLength;
        this.longK = (int)Math.ceil(3.45 * (longLength + 1));
        this.shortK = (int)Math.ceil(3.45 * (shortLength + 1));
    }

    @Override
    public IndicatorValue calculate(long time, QuotePrice quotePrice) {
        IndicatorValue ret = new IndicatorValue(time);
        List<Quote> qtSer = quoteProvider.getQuotesBefore(Integer.MAX_VALUE, time);
        int size = qtSer.size();
        double[] high = new double[size];
        double[] low = new double[size];
        double[] close = new double[size];
        double[] vol = new double[size];
        double[] out = new double[size];
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
        RetCode retCode = talib.adOsc(0, size - 1, high, low, close, vol, shortLength, longLength, outBegin, outNBElement, out);
        if(retCode == RetCode.Success && outNBElement.value > 0){
            double atr = out[outNBElement.value-1];
            ret.put(shortName, atr);
        } else {
            logger.error("TaLib error:" + retCode);
        }

        return ret;
    }
}
