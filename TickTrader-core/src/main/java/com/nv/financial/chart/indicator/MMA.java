package com.nv.financial.chart.indicator;

import com.nv.financial.chart.dto.IndicatorValue;
import com.nv.financial.chart.dto.Quote;
import com.nv.financial.chart.quote.QuotePrice;
import com.nv.financial.chart.quote.provider.IQuoteProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

/**
 * Modified Moving Averages. Base on
 * http://www.aspenres.com/documents/help/userguide/help/aspenModified_Moving_Averages.html
 */
public class MMA extends AbstractIndicator {
    private static final Logger logger = LogManager.getLogger(MMA.class);
    private final int length;

    {
        longName = "Modified Moving Averages";
        shortName = "MMA";
        description = "unknow";
    }

    public MMA(IQuoteProvider quoteProvider,int length){
        super(quoteProvider);
        this.length = length;
    }

    @Override
    public IndicatorValue calculate(long time, QuotePrice quotePrice) {
        Quote quote = quoteProvider.getQuote(time);
        List<Long> times = quoteProvider.getTimesBefore(2,time);
        IndicatorValue sma = new SMA(quoteProvider,length).calculate(times.get(0),quotePrice);
        IndicatorValue ret = new IndicatorValue(time);
        double dSma = sma.getValue(0);
        double mma = dSma + 1/length*(quotePrice.getPrice(quote)-dSma);
        ret.put(shortName,mma);

        return ret;
    }
}
