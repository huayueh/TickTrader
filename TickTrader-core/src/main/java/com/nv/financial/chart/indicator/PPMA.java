package com.nv.financial.chart.indicator;

import com.nv.financial.chart.dto.IndicatorValue;
import com.nv.financial.chart.quote.QuotePrice;
import com.nv.financial.chart.quote.provider.IQuoteProvider;
import com.tictactec.ta.lib.MInteger;
import com.tictactec.ta.lib.RetCode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

/**
 * Typical Price Moving Average (Pivot Point). Base on
 * http://www.onlinetradingconcepts.com/TechnicalAnalysis/MATypicalPrice.html
 */
public class PPMA  extends AbstractIndicator {
    private static final Logger logger = LogManager.getLogger(PPMA.class);
    private final int length;

    {
        longName = "Pivot Point Moving Average";
        shortName = "PPMA";
        description = "unknow";
    }

    public PPMA(IQuoteProvider quoteProvider,int length){
        super(quoteProvider);
        this.length = length;
    }

    @Override
    public IndicatorValue calculate(long time, QuotePrice quotePrice) {
        IndicatorValue ret = new IndicatorValue(time);
        PivotPoints pp = new PivotPoints(quoteProvider);
        List<IndicatorValue> list = pp.calculate(length, time, quotePrice);
        int size = list.size();
        int idx = 0;
        double[] in = new double[size];
        double[] out = new double[size];
        MInteger outBegin = new MInteger();
        MInteger outNBElement = new MInteger();

        for (IndicatorValue iv : list){
            in[idx] = iv.getValue(0);
            idx++;
        }
        RetCode retCode = talib.sma(0, list.size()-1, in, length, outBegin, outNBElement, out);
        if(retCode == RetCode.Success && outNBElement.value > 0){
            double avg = out[outNBElement.value-1];
            ret.put(shortName, avg);
        } else {
            logger.error("TaLib error:" + retCode);
        }
        return ret;
    }
}
