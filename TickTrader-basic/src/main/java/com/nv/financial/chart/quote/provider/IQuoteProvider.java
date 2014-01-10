package com.nv.financial.chart.quote.provider;

import com.nv.financial.chart.dto.Quote;
import com.nv.financial.chart.dto.Tick;
import com.nv.financial.chart.quote.TimePeriod;

import java.util.List;
import java.util.Observer;

/**
 *
 */
public interface IQuoteProvider {
    public long latestQuoteTime();
    public long getNextTime(long time);
    public TimePeriod getTimePeriod();
    public Quote getQuote(long time);
    public Quote latestQuote();
    public List<Quote> getQuotes(long fromTime, long toTime);
    public List<Quote> getQuotesBefore(int nQuote, long toTime);
    public List<Quote> getQuotesAfter(int nQuote, long fromTime);
    public List<Long> getTimesBefore(int nTime, long toTime);
    public List<Long> getTimesAfter(int nTime, long fromTime);
    public String getProductName();
    public String getContract();
}
