package com.nv.financial.chart.indicator;

import com.nv.financial.chart.dto.IndicatorValue;
import com.nv.financial.chart.dto.Quote;
import com.nv.financial.chart.quote.QuotePrice;
import com.nv.financial.chart.quote.provider.IQuoteProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

/**
 * Nonlinear Ehlers Filter. Base on
 * http://www.verysource.com/code/5074055_1/nonlinear%20ehlers%20filter.afl.html
 * http://www.earnforex.com/forum/f12/nonlinear-ehlers-filter-9508/
 */
public class NEF extends AbstractIndicator {
    private static final Logger logger = LogManager.getLogger(NEF.class);
    private final int length;

    {
        longName = "Nonlinear Ehlers Filter";
        shortName = "NEF";
        description = "unknow";
    }

    public NEF(IQuoteProvider quoteProvider,int length){
        super(quoteProvider);
        this.length = length;
    }

    @Override
    public IndicatorValue calculate(long time, QuotePrice quotePrice) {
        List<Quote> quotes = quoteProvider.getQuotesBefore(length,time);
        double priceMomSum = 0;
        double fiveMomSum = 0;

        for (Quote quote : quotes) {
            double price = quote.getMidpoint();
            IndicatorValue momentum = new Momentum(quoteProvider,5).calculate(quote.getTime(), quotePrice);
            double fiveMom = Math.abs(momentum.getValue(0));
            double priceMom = price * fiveMom;

            priceMomSum += priceMom;
            fiveMomSum += fiveMom;
        }
        double nlef = priceMomSum / fiveMomSum;
        IndicatorValue ret = new IndicatorValue(time);
        ret.put(shortName, nlef);

        return ret;
    }
}
