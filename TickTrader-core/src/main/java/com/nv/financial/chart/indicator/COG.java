package com.nv.financial.chart.indicator;

import com.nv.financial.chart.dto.IndicatorValue;
import com.nv.financial.chart.dto.Quote;
import com.nv.financial.chart.quote.QuotePrice;
import com.nv.financial.chart.quote.provider.IQuoteProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collections;
import java.util.List;

/**
 * Center Of Gravity. Base on
 * http://www.linnsoft.com/tour/techind/cog.htm
 */
public class COG extends AbstractIndicator {
    private static final Logger logger = LogManager.getLogger(COG.class);
    private final int length;

    {
        longName = "Center Of Gravity";
        shortName = "COG";
        description = "unknow";
    }

    public COG(IQuoteProvider quoteProvider,int length) {
        super(quoteProvider);
        this.length = length;
    }

    @Override
    public IndicatorValue calculate(long time, QuotePrice quotePrice) {
        IndicatorValue ret = new IndicatorValue(time);
        List<Quote> quotes = quoteProvider.getQuotesBefore(length, time);
        Collections.reverse(quotes);
        double num = 0;
        double den = 0;

        for(int idx = 0; idx < quotes.size(); idx++){
            num += quotePrice.getPrice(quotes.get(idx))* (idx+1);
            den += quotePrice.getPrice(quotes.get(idx));
        }

        double cog = -1 * num / den;
        ret.put(shortName,cog);

        return ret;
    }
}
