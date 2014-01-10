package com.nv.financial.chart.indicator;

import com.nv.financial.chart.dto.IndicatorValue;
import com.nv.financial.chart.dto.Quote;
import com.nv.financial.chart.quote.QuotePrice;
import com.nv.financial.chart.quote.provider.IQuoteProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

/**
 * Chaikin Money Flow. Base on
 * http://tradingsim.com/blog/chaikin-money-flow-indicator/
 * http://stockcharts.com/school/doku.php?id=chart_school:technical_indicators:chaikin_money_flow
 */
public class CMF extends AbstractIndicator {
    private static final Logger logger = LogManager.getLogger(CMF.class);
    private int length;

    {
        longName = "Chaikin Money Flow";
        shortName = "CMF";
        description = "unknow";
    }

    public CMF(IQuoteProvider quoteProvider,int length) {
        super(quoteProvider);
        this.length = length;
    }

    @Override
    public IndicatorValue calculate(long time, QuotePrice quotePrice) {
        IndicatorValue ret = new IndicatorValue(time);
        List<Quote> quotes = quoteProvider.getQuotesBefore(Integer.MAX_VALUE, time);
        double clv;
        double sumClv = 0;
        double sumVol = 0;

        for (Quote quote : quotes) {
            clv = (quote.getClose()-quote.getLow()) - (quote.getHigh()-quote.getClose()) / quote.getHigh()-quote.getLow();
            sumClv += quote.getVolume() * clv;
            sumVol += quote.getVolume();
        }

        ret.put(shortName,sumClv/sumVol);

        return ret;
    }
}
