package com.nv.financial.chart.service;

import com.nv.financial.chart.dto.Quote;
import com.nv.financial.chart.quote.TimePeriod;
import com.nv.financial.chart.quote.provider.IMemQuoteProvider;
import com.nv.financial.chart.quote.provider.IQuoteProvider;

import java.util.Collection;
import java.util.List;

/**
 *
 */
public interface IQuoteService {
    public boolean isExist(String contract, String product);

    public void creatQuoteProvider(String contract, String product);

    public IQuoteProvider getQuoteProvider(String contract, String product, TimePeriod period);

    public Collection<IMemQuoteProvider> getAllMemProvider();

    public List<Quote> getQuotesBefore(int bars, long toTime, String contract, String product, TimePeriod period);
}
