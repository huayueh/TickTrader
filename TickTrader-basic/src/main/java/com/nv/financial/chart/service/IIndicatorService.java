package com.nv.financial.chart.service;

import com.nv.financial.chart.dto.IndicatorValue;
import com.nv.financial.chart.quote.TimePeriod;

import java.util.List;
import java.util.Observer;

/**
 *
 */
public interface IIndicatorService {
    public List<IndicatorValue> get(int bars, long toTime, String indicator, String quoteProvider, String product, TimePeriod period);
    public void listen(String indicator, String quoteProvider, String product, TimePeriod period);
    public void removeListen(String indicator, String quoteProvider, String product, TimePeriod period);
}
