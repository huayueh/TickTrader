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
 * Ultimate Oscillator. Base on
 * http://stockcharts.com/school/doku.php?id=chart_school:technical_indicators:ultimate_oscillator
 */
public class UOS extends AbstractIndicator {
    private static final Logger logger = LogManager.getLogger(UOS.class);
    private final int length1;
    private final int length2;
    private final int length3;

    {
        longName = "Ultimate Oscillator";
        shortName = "UOS";
        description = "unknow";
    }

    public UOS(IQuoteProvider quoteProvider,int length1, int length2, int length3) {
        super(quoteProvider);
        this.length1 = length1;
        this.length2 = length2;
        this.length3 = length3;
    }

    @Override
    public IndicatorValue calculate(long time, QuotePrice quotePrice) {
        IndicatorValue ret = new IndicatorValue(time);
        List<Quote> qtSer = quoteProvider.getQuotesBefore(length3+1, time);
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
        RetCode retCode = talib.ultOsc(0, size - 1, high, low, close, length1, length2, length3, outBegin, outNBElement, out);
        if(retCode == RetCode.Success && outNBElement.value > 0){
            double trix = out[outNBElement.value-1];
            ret.put(shortName, trix);
        } else {
            logger.error("TaLib error:" + retCode);
        }

        return ret;
    }
}
