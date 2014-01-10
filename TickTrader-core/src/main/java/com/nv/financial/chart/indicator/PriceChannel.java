package com.nv.financial.chart.indicator;

import com.nv.financial.chart.dto.IndicatorValue;
import com.nv.financial.chart.dto.Quote;
import com.nv.financial.chart.quote.QuotePrice;
import com.nv.financial.chart.quote.provider.IQuoteProvider;

import java.util.List;

/**
 * Price Channels. Base on
 * http://stockcharts.com/school/doku.php?id=chart_school:technical_indicators:price_channels
 */
public class PriceChannel extends AbstractIndicator {
    public final static int UPPER = 0;
    public final static int LOWER = 1;
    public final static int CENTER = 2;
    private final int length;

    {
        longName = "Price Channels";
        shortName = "Price Channels";
        description = "unknow";
    }

    public PriceChannel(IQuoteProvider quoteProvider,int length) {
        super(quoteProvider);
        this.length = length;
    }

    @Override
    public IndicatorValue calculate(long time, QuotePrice quotePrice) {
        IndicatorValue ret = new IndicatorValue();
        List<Quote> quotes = quoteProvider.getQuotesBefore(length,time);
        double up = Double.MIN_VALUE;
        double down = Double.MAX_VALUE;
        double center = (up + down)/2;
        for (Quote qt : quotes){
            up = Math.max(up, qt.getHigh());
            down = Math.min(down, qt.getLow());
            center = (up + down)/2;
        }
        ret.put("Upper_Channel",up);
        ret.put("Lower_Channel",down);
        ret.put("Centerline",center);

        return ret;
    }
}
