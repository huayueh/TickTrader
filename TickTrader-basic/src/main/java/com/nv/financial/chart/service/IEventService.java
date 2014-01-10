package com.nv.financial.chart.service;

import com.nv.financial.chart.dto.IndicatorValue;
import com.nv.financial.chart.dto.Quote;
import com.nv.financial.chart.dto.Tick;
import com.nv.financial.chart.quote.TimePeriod;

/**
 *
 */
public interface IEventService {
    public void onEvent(Tick tick);
    public void onEvent(IndicatorValue idcValue);
    public void addIndicator(String provider, String product, String indicator, TimePeriod period);
    public void removeIndicator(String provider, String product, String indicator, TimePeriod period);
    public void addInterest(String provider, String product, TimePeriod period);
    public void removeInterest(String provider, String product, TimePeriod period);
}
