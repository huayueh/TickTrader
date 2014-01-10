package com.nv.financial.chart.indicator;

import com.nv.financial.chart.dto.IndicatorValue;
import com.nv.financial.chart.dto.Quote;
import com.nv.financial.chart.quote.QuotePrice;
import com.nv.financial.chart.quote.provider.IQuoteProvider;
import com.tictactec.ta.lib.MAType;
import com.tictactec.ta.lib.MInteger;
import com.tictactec.ta.lib.RetCode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

/**
 * Stochastic Full. Base on
 * http://stockcharts.com/school/doku.php?id=chart_school:technical_indicators:stochastic_oscillator
 */
public class StochFull extends AbstractIndicator {
    private static final Logger logger = LogManager.getLogger(StochFull.class);
    private final int slowK;
    private final int fastK;
    private final int dPeriod;

    {
        longName = "Stochastic Full";
        shortName = "StochFull";
        description = "unknow";
    }

    public StochFull(IQuoteProvider quoteProvider,int fastK, int slowK, int dPeriod){
        super(quoteProvider);
        this.fastK = fastK;
        this.dPeriod = dPeriod;
        this.slowK = slowK;
    }

    @Override
    public IndicatorValue calculate(long time, QuotePrice quotePrice) {
        IndicatorValue ret = new IndicatorValue(time);
        List<Quote> qtSer = quoteProvider.getQuotesBefore(fastK *2, time);
        int size = qtSer.size();
        double[] high = new double[size];
        double[] low = new double[size];
        double[] close = new double[size];
        double[] outK = new double[size];
        double[] outD = new double[size];
        MInteger outBegin = new MInteger();
        MInteger outNBElement = new MInteger();
        int idx = 0;
        for (Quote qt : qtSer){
            high[idx] = qt.getHigh();
            low[idx] = qt.getLow();
            close[idx] = qt.getClose();
            idx++;
        }
        RetCode retCode = talib.stoch(0, size - 1, high, low, close, fastK, slowK, MAType.Sma, dPeriod, MAType.Sma, outBegin, outNBElement, outK, outD);
        if(retCode == RetCode.Success && outNBElement.value > 0){
            double k = outK[outNBElement.value-1];
            double d = outD[outNBElement.value-1];
            ret.put("K", k);
            ret.put("D", d);
        } else {
            logger.error("TaLib error:" + retCode);
        }

        return ret;

    }
}
