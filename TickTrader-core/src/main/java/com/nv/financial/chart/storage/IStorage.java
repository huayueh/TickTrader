package com.nv.financial.chart.storage;

import com.nv.financial.chart.dto.Quote;
import com.nv.financial.chart.dto.Tick;
import com.nv.financial.chart.quote.TimePeriod;

import java.util.List;

/**
 * User: Harvey
 * Date: 2013/10/24
 * Time: 下午 2:46
 */
public interface IStorage {
    public void save(Tick tick);
    public void save(Quote quote);
    public Quote retrieveQuote(long time, String provider, String product, TimePeriod period);
    public List<Quote> retrieveQuotes(long fromTime, long toTime, String provider, String product, TimePeriod period);
    public List<Quote> retrieveQuotes(int nBars, long toTime, String provider, String product, TimePeriod period);
    public List<Quote> retrieveQuotes(String provider, String product, TimePeriod period);
}
