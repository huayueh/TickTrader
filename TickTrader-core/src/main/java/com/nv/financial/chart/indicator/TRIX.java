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
 * TRIX. Base on
 * http://stockcharts.com/school/doku.php?id=chart_school:technical_indicators:trix
 */
public class TRIX extends AbstractIndicator {
    private static final Logger logger = LogManager.getLogger(TRIX.class);
    private final int length;
    //The weight omitted by stopping after k
    private final int k;

    {
        longName = "TRIX";
        shortName = "TRIX";
        description = "unknow";
    }

    public TRIX(IQuoteProvider quoteProvider,int length){
        super(quoteProvider);
        this.length = length;
//        this.k = (int)Math.ceil(3.45 * (length + 1));
        this.k = Integer.MAX_VALUE;
    }

    @Override
    public IndicatorValue calculate(long time, QuotePrice quotePrice) {
        IndicatorValue ret = new IndicatorValue(time);
        List<Quote> qtSer = quoteProvider.getQuotesBefore(k, time);
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
        RetCode retCode = talib.trix(0, size - 1, in, length, outBegin, outNBElement, out);
        if(retCode == RetCode.Success && outNBElement.value > 0){
            double trix = out[outNBElement.value-1];
            ret.put(shortName, trix);
        } else {
            logger.error("TaLib error:" + retCode);
        }

        return ret;
    }
}
