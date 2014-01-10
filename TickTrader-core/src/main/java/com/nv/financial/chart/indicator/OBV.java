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
 * On-Balance Volume. Base on
 * http://en.wikipedia.org/wiki/On-balance_volume
 */
public class OBV extends AbstractIndicator {
    private static final Logger logger = LogManager.getLogger(OBV.class);

    {
        longName = "On-Balance Volume";
        shortName = "OBV";
        description = "unknow";
    }

    public OBV(IQuoteProvider quoteProvider){
        super(quoteProvider);
    }

    @Override
    public IndicatorValue calculate(long time, QuotePrice quotePrice) {
        IndicatorValue ret = new IndicatorValue(time);
        List<Quote> qtSer = quoteProvider.getQuotesBefore(Integer.MAX_VALUE, time);
        int size = qtSer.size();
        double[] in = new double[size];
        double[] vol = new double[size];
        double out[] = new double[size];
        MInteger outBegin = new MInteger();
        MInteger outNBElement = new MInteger();
        int idx = 0;
        for (Quote qt : qtSer){
            in[idx] = quotePrice.getPrice(qt);
            vol[idx] = qt.getVolume();
            idx++;
        }
        RetCode retCode = talib.obv(0, size - 1, in, vol, outBegin, outNBElement, out);
        if(retCode == RetCode.Success && outNBElement.value > 0){
            double mom = out[outNBElement.value-1];
            ret.put(shortName, mom);
        } else {
            logger.error("TaLib error:" + retCode);
        }

        return ret;
    }
}
