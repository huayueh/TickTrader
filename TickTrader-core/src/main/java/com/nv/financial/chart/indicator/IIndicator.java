package com.nv.financial.chart.indicator;

import com.nv.financial.chart.dto.IndicatorValue;
import com.nv.financial.chart.quote.QuotePrice;

import java.util.List;

/**
 */
public interface IIndicator {
    public String getLongName();
    public String getShortName();
    public String getDescription();
    public List<IndicatorValue> calculate(int nQuote, long toTime, QuotePrice quotePrice);
    public IndicatorValue calculateLatest();
}
