package com.nv.financial.chart.indicator;

import com.nv.financial.chart.dto.IndicatorValue;
import com.nv.financial.chart.quote.QuotePrice;
import com.nv.financial.chart.quote.provider.IQuoteProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Envelopes Trading Bands. Base on
 * http://stockcharts.com/school/doku.php?id=chart_school:technical_indicators:moving_average_envel
 */
public class ETB extends AbstractIndicator {
    private static final Logger logger = LogManager.getLogger(ETB.class);
    public final static int UPPER = 0;
    public final static int LOWER = 1;
    private final int lenght;
    private final double percent;

    {
        longName = "Envelopes Trading Bands";
        shortName = "ETB";
        description = "unknow";
    }

    public ETB(IQuoteProvider quoteProvider,int length, double percent){
        super(quoteProvider);
        this.lenght = length;
        this.percent = percent;
    }

    @Override
    public IndicatorValue calculate(long time, QuotePrice quotePrice) {
        IndicatorValue ret = new IndicatorValue(time);
        IndicatorValue sma = new SMA(quoteProvider,lenght).calculate(time, quotePrice);

        ret.put("Upper_Envelope",sma.getValue(0)*(1+percent));
        ret.put("Lower_Envelope",sma.getValue(0)*(1-percent));

        return ret;
    }
}
