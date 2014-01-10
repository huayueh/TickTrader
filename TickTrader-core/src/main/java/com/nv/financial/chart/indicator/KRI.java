package com.nv.financial.chart.indicator;

import com.nv.financial.chart.dto.IndicatorValue;
import com.nv.financial.chart.dto.Quote;
import com.nv.financial.chart.quote.QuotePrice;
import com.nv.financial.chart.quote.provider.IQuoteProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Kairi Relative Index. Base on
 * http://fxcodebase.com/wiki/index.php/Kairi_Relative_Index_(KRI)
 */
public class KRI extends AbstractIndicator {
    private static final Logger logger = LogManager.getLogger(KRI.class);
    private final int length;

    {
        longName = "Kairi Relative Index";
        shortName = "KRI";
        description = "unknow";
    }

    public KRI(IQuoteProvider quoteProvider,int length){
        super(quoteProvider);
        this.length = length;
    }

    @Override
    public IndicatorValue calculate(long time, QuotePrice quotePrice) {
        Quote quote = quoteProvider.getQuote(time);
        IndicatorValue ret = new IndicatorValue(time);
        IndicatorValue sma = new SMA(quoteProvider,length).calculate(time, quotePrice);
        double kri = (quote.getClose()-sma.getValue(0))/sma.getValue(0) * 100;
        ret.put(shortName,kri);

        return ret;
    }
}
