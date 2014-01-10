package com.nv.financial.chart.indicator;

import com.nv.financial.chart.dto.IndicatorValue;
import com.nv.financial.chart.dto.Quote;
import com.nv.financial.chart.quote.QuotePrice;
import com.nv.financial.chart.quote.provider.IQuoteProvider;
import com.tictactec.ta.lib.MAType;
import com.tictactec.ta.lib.MInteger;
import com.tictactec.ta.lib.RetCode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

/**
 * Stochastic Slow. Base on
 * http://stockcharts.com/school/doku.php?id=chart_school:technical_indicators:stochastic_oscillator
 */
public class StochSlow extends AbstractIndicator {
    private static final Logger logger = LogManager.getLogger(StochSlow.class);
    private final int dPeriod;
    private final int kPeriod;

    {
        longName = "Stochastic Slow";
        shortName = "StochSlow";
        description = "unknow";
    }

    public StochSlow(IQuoteProvider quoteProvider,int kPeriod, int dPeriod){
        super(quoteProvider);
        this.kPeriod = kPeriod;
        this.dPeriod = dPeriod;
    }

    @Override
    public IndicatorValue calculate(long time, QuotePrice quotePrice) {
        return new StochFull(quoteProvider,kPeriod, 3, dPeriod).calculate(time, quotePrice);
    }
}
