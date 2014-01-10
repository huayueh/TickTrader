package com.nv.financial.chart.indicator;

import java.util.List;

import com.nv.financial.chart.dto.IndicatorValue;
import com.nv.financial.chart.dto.Quote;
import com.nv.financial.chart.quote.QuotePrice;
import com.nv.financial.chart.quote.provider.IQuoteProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Commodity channel index. Base on
 * http://stockcharts.com/school/doku.php?id=chart_school:technical_indicators:commodity_channel_index_cci
 * http://en.wikipedia.org/wiki/Commodity_channel_index
 */
public class CCI extends AbstractIndicator {
    private static final Logger logger = LogManager.getLogger(CCI.class);
    private final int length;

    {
        longName = "Commodity Channel Index";
        shortName = "CCI";
        description = "unknow";
    }

    public CCI(IQuoteProvider quoteProvider,int length) {
        super(quoteProvider);
        this.length = length;
    }

    @Override
    public IndicatorValue calculate(long time, QuotePrice quotePrice) {
        IndicatorValue ret = new IndicatorValue(time);
        Quote quotePt = quoteProvider.getQuote(time);

        //caculate mean absolute deviation.
        List<Quote> quotes = quoteProvider.getQuotesBefore(length, time);
        SMA smaI = new SMA(quoteProvider,length);
        IndicatorValue sma = smaI.calculate(time,QuotePrice.TYPICAL);
        double mean = sma.getValue(0);
        double difSum = 0;

        for (Quote quote : quotes) {
            double dif = QuotePrice.TYPICAL.getPrice(quote)-mean;
            dif = Math.abs(dif);
            difSum += dif;
        }
        double md = difSum/quotes.size();

        //cci
        double cci = 1/0.015 * (QuotePrice.TYPICAL.getPrice(quotePt) - mean)/md;
        ret.put(shortName,cci);

        return ret;
    }
}