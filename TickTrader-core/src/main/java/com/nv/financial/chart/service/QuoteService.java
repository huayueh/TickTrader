package com.nv.financial.chart.service;

import com.nv.financial.chart.ChartSetting;
import com.nv.financial.chart.dto.Quote;
import com.nv.financial.chart.quote.TimePeriod;
import com.nv.financial.chart.quote.provider.*;
import org.apache.log4j.Logger;

import java.util.*;

/**
 *
 */
public class QuoteService extends Observable implements IQuoteService {
    private static final Logger logger = Logger.getLogger(QuoteService.class);
    private static QuoteService Instance = new QuoteService();
    private Map<String, IQuoteProvider> providers = new HashMap<String, IQuoteProvider>();
    private Map<String, IMemQuoteProvider> tickObProviders = new HashMap<String, IMemQuoteProvider>();

    private QuoteService(){
    }

    public static QuoteService getInstance(){
        return Instance;
    }

    @Override
    public boolean isExist(String contract, String product) {
        return providers.containsKey(contract + product + TimePeriod.DAY);
    }

    @Override
    public void creatQuoteProvider(String contract, String product) {
//        for (TimePeriod period : TimePeriod.values()) {
        TimePeriod period = TimePeriod.DAY;
            logger.info("create quote contract:" + contract + "_" + product + "_" + period);
            MemQuoteQuoteProvider mem = new MemQuoteQuoteProvider(contract, product, period);
            mem.setMaxInMem(ChartSetting.getMaxInMem());
//            CassandraQuoteProvider cassandra = new CassandraQuoteProvider(group, product, period);
//            QuoteProviderImp insProvider = new QuoteProviderImp(group, product, period);
//            insProvider.setMemProvider(mem);
//            insProvider.setStorageProvider(cassandra);
            tickObProviders.put(contract + product + period.name(), mem);
            providers.put(contract + product + period.name(), mem);
//        }
    }

    public IQuoteProvider getQuoteProvider(String group, String product, String period) {
        return getQuoteProvider(group, product, TimePeriod.valueOf(period));
    }

    @Override
    public IQuoteProvider getQuoteProvider(String contract, String product, TimePeriod period) {
        return providers.get(contract + product + period.name());
    }

    @Override
    public Collection<IMemQuoteProvider> getAllMemProvider() {
        return tickObProviders.values();
    }

    public List<Quote> getQuotesBefore(int bars, long toTime, String group, String product, String period) {
        return getQuotesBefore(bars, toTime, group, product, TimePeriod.valueOf(period));
    }

    @Override
    public List<Quote> getQuotesBefore(int bars, long toTime, String contract, String product, TimePeriod period) {
        IQuoteProvider provider = providers.get(contract + product + period.name());
        if(provider == null)
            return Collections.emptyList();
        return provider.getQuotesBefore(bars, toTime);
    }
}
