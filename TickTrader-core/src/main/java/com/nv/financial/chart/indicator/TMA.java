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
 * Triangular Moving Average. Base on
 * http://www.onlinetradingconcepts.com/TechnicalAnalysis/MATriangular.html
 */
public class TMA extends AbstractIndicator {
    private static final Logger logger = LogManager.getLogger(TMA.class);
    private final int length;

    {
        longName = "Triangular Moving Average";
        shortName = "TMA";
        description = "unknow";
    }

    public TMA(IQuoteProvider quoteProvider,int length){
        super(quoteProvider);
        this.length = length;
    }

    @Override
    public IndicatorValue calculate(long time, QuotePrice quotePrice) {
        IndicatorValue ret = new IndicatorValue(time);
        List<Quote> qtSer = quoteProvider.getQuotesBefore(length, time);
        int size = qtSer.size();
        double[] in = new double[size];
        double[] out = new double[size];
        MInteger outBegin = new MInteger();
        MInteger outNBElement = new MInteger();
        int idx = 0;
        for (Quote qt : qtSer){
            in[idx] = quotePrice.getPrice(qt);
            idx++;
        }
        RetCode retCode = talib.trima(0, size - 1, in, length, outBegin, outNBElement, out);
        if(retCode == RetCode.Success && outNBElement.value > 0){
            double atr = out[outNBElement.value-1];
            ret.put(shortName, atr);
        } else {
            logger.error("TaLib error:" + retCode);
        }

        return ret;
    }
}
