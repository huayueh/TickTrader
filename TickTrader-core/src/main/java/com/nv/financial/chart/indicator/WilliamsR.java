package com.nv.financial.chart.indicator;

import com.nv.financial.chart.dto.IndicatorValue;
import com.nv.financial.chart.dto.Quote;
import com.nv.financial.chart.quote.QuotePrice;
import com.nv.financial.chart.quote.provider.IQuoteProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

/**
 * William's%R. Base on
 * http://stockcharts.com/school/doku.php?id=chart_school:technical_indicators:williams_r
 */
public class WilliamsR extends AbstractIndicator {
    private static final Logger logger = LogManager.getLogger(WilliamsR.class);
    private final int length;

    {
        longName = "William's%R";
        shortName = "WilliamsR";
        description = "unknow";
    }

    public WilliamsR(IQuoteProvider quoteProvider,int length) {
        super(quoteProvider);
        this.length = length;
    }

    @Override
    public IndicatorValue calculate(long time, QuotePrice quotePrice) {
        List<Quote> periodHiLo = getPeriodHiLo(length, time, quoteProvider);
        Quote quote = quoteProvider.getQuote(time);
        Quote quoteEst = periodHiLo.get(periodHiLo.size()-1);
        double r = (quoteEst.getHigh()- quote.getClose())/(quoteEst.getHigh()-quote.getLow())*-100;
        IndicatorValue ret = new IndicatorValue();
        ret.put(shortName,r);

        return ret;
    }
}
