package com.nv.financial.chart.indicator;

import com.nv.financial.chart.dto.IndicatorValue;
import com.nv.financial.chart.quote.QuotePrice;
import com.nv.financial.chart.quote.provider.IQuoteProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Keltner Channels. Base on
 * http://stockcharts.com/school/doku.php?id=chart_school:technical_indicators:keltner_channels
 */
public class KC extends AbstractIndicator {
    private static final Logger logger = LogManager.getLogger(KC.class);
    public final static int UPPER = 0;
    public final static int LOWER = 1;
    private final int emaLen;
    private final int atrLen;
    private final double rate;

    {
        longName = "Keltner Channels";
        shortName = "KC";
        description = "unknow";
    }

    public KC(IQuoteProvider quoteProvider, int emaLen, int atrLen, double rate){
        super(quoteProvider);
        this.emaLen = emaLen;
        this.atrLen = atrLen;
        this.rate = rate;
    }

    @Override
    public IndicatorValue calculate(long time, QuotePrice quotePrice) {
        IndicatorValue ema = new EMA(quoteProvider, emaLen).calculate(time, quotePrice);
        IndicatorValue atr = new ATR(quoteProvider,atrLen).calculate(time, quotePrice);
        IndicatorValue ret = new IndicatorValue(time);
        double up = ema.getValue(0) + rate*atr.getValue(0);
        double down = ema.getValue(0) - rate*atr.getValue(0);
        ret.put("Upper_Channel",up);
        ret.put("Lower_Channel",down);

        return ret;
    }
}
