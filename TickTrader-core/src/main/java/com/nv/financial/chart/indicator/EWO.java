package com.nv.financial.chart.indicator;

import com.nv.financial.chart.dto.IndicatorValue;
import com.nv.financial.chart.quote.QuotePrice;
import com.nv.financial.chart.quote.provider.IQuoteProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Elliott Wave Oscillator. Base on
 * http://www.investopedia.com/university/advancedwave/elliottwave3.asp
 */
public class EWO extends AbstractIndicator {
    private static final Logger logger = LogManager.getLogger(EWO.class);

    {
        longName = "Elliott Wave Oscillator";
        shortName = "EWO";
        description = "unknow";
    }

    public EWO(IQuoteProvider quoteProvider){
        super(quoteProvider);
    }

    @Override
    public IndicatorValue calculate(long time, QuotePrice quotePrice) {
        IndicatorValue sma5 = new SMA(quoteProvider,5).calculate(time, quotePrice);
        IndicatorValue sma35 = new SMA(quoteProvider,35).calculate(time, quotePrice);
        IndicatorValue ret = new IndicatorValue(time);
        ret.put(shortName, sma5.getValue(0)-sma35.getValue(0));
        return ret;
    }

}
