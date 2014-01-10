package com.nv.financial.chart.indicator;

import com.nv.financial.chart.dto.Quote;
import com.nv.financial.chart.quote.QuotePrice;
import com.nv.financial.chart.quote.provider.IQuoteProvider;
import com.tictactec.ta.lib.MInteger;
import com.tictactec.ta.lib.RetCode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.nv.financial.chart.dto.IndicatorValue;
import java.util.List;

/**
 * Accumulation Distribution Line/Index. Base on
 * http://stockcharts.com/school/doku.php?id=chart_school:technical_indicators:accumulation_distrib
 * http://en.wikipedia.org/wiki/Accumulation/distribution_index
 */
public class ADL extends AbstractIndicator {
    private static final Logger logger = LogManager.getLogger(ADL.class);

    {
        longName = "Accumulation Distribution Line";
        shortName = "ADL";
        description = "unknow";
    }

    public ADL(IQuoteProvider quoteProvider) {
        super(quoteProvider);
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
        RetCode retCode = talib.ad(0, size - 1, high, low, close,vol, outBegin, outNBElement, out);
        if(retCode == RetCode.Success && outNBElement.value > 0){
            double atr = out[outNBElement.value-1];
            ret.put(shortName, atr);
        } else {
            logger.error("TaLib error:" + retCode);
        }

        return ret;
    }
}
