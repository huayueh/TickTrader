package com.nv.financial.chart.service;

import com.nv.financial.chart.cache.CacheFactory;
import com.nv.financial.chart.cache.ICache;
import com.nv.financial.chart.concurrent.JobExecutor;
import com.nv.financial.chart.dto.IndicatorValue;
import com.nv.financial.chart.dto.Tick;
import com.nv.financial.chart.indicator.IIndicator;
import com.nv.financial.chart.indicator.IndicatorFactory;
import com.nv.financial.chart.quote.QuotePrice;
import com.nv.financial.chart.quote.TimePeriod;
import com.nv.financial.chart.quote.provider.IQuoteProvider;
import static com.nv.financial.chart.util.Utils.*;
import org.apache.log4j.Logger;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Main function:
 * 1. Create and keep indicator instance in cache.
 * 2. Invoke calculate of correct indicator instance when needed.
 * 3. Calculate latest value while on tick.
 */
public class IndicatorService extends Observable implements IIndicatorService,Observer{
    private static final Logger logger = Logger.getLogger(IndicatorService.class);
    private static IndicatorService Instance = new IndicatorService();
    private ICache<String,IIndicator> indicatorCache;
    private Map<String,Set<String>> interestMap;
    private IQuoteService quoteService = QuoteService.getInstance();

    private IndicatorService(){
        //keep indicator instance
        indicatorCache = CacheFactory.getCache(CacheFactory.INDICATOR_CACHE);

        //keep which indicator is interested by user
        interestMap = new ConcurrentHashMap<String, Set<String>>();
    }

    public static IndicatorService getInstance(){
        return Instance;
    }

    public Map<String, Set<String>> getInterestMap() {
        return interestMap;
    }

    public ICache<String, IIndicator> getIndicatorCache() {
        return indicatorCache;
    }

    public List<IndicatorValue> get(int bars, long toTime, String indicator, String group, String product, String period) {
        return get(bars, toTime, indicator, group, product, TimePeriod.valueOf(period));
    }

    @Override
    public List<IndicatorValue> get(int bars, long toTime, String indicator, String group, String product, TimePeriod period) {
        String cacheKey = group + PRODUCT_DELIMITER + product + PRODUCT_DELIMITER + period.name() + PRODUCT_DELIMITER + indicator;
        IIndicator ind = (IIndicator) indicatorCache.get(cacheKey);
        //can't get from cache. create instance
        if (ind == null){
            IQuoteProvider quotePro = quoteService.getQuoteProvider(group, product, period);
            ind = IndicatorFactory.create(quotePro, indicator);
            if(ind != null)
                indicatorCache.put(cacheKey, ind);
        }
        if(ind == null){
            return Collections.EMPTY_LIST;
        }
        return ind.calculate(bars, toTime, QuotePrice.CLOSE);
    }

    @Override
    public void listen(String indicator, String group, String product, TimePeriod period) {
        String key = group + INDICATOR_DELIMITER + product + INDICATOR_DELIMITER + period.name();
        Set<String> indicators = interestMap.get(key);
        if(indicators == null){
            indicators = new HashSet<String>();
            interestMap.put(key,indicators);
        }
        indicators.add(indicator);
    }

    @Override
    public void removeListen(String indicator, String group, String product, TimePeriod period) {
        String key = group + INDICATOR_DELIMITER + product + INDICATOR_DELIMITER + period.name();
        Set<String> indicators = interestMap.get(key);
        if(indicators != null){
            indicators.remove(indicator);
        }
    }

    @Override
    public void update(Observable o, final Object arg) {
        if (!(arg instanceof Tick))
            return;

        final Tick tick = (Tick)arg;

        for(final TimePeriod period : TimePeriod.values()){
            final String key = tick.getContract() + INDICATOR_DELIMITER + tick.getProductId() + INDICATOR_DELIMITER + period.name();
            Set<String> indSet = interestMap.get(key);

            //no one interest in this tick, no need to calculate
            if (indSet == null){
                continue;
            }

            for (final String indName : indSet){
                JobExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        IIndicator indicator = (IIndicator) indicatorCache.get(key + indName);
                        //can't get from cache. create instance
                        if (indicator == null) {
                            IQuoteProvider quoteProvider = quoteService.getQuoteProvider(tick.getContract(), tick.getProductId(), period);
                            indicator = IndicatorFactory.create(quoteProvider, indName);
                        }

                        IndicatorValue iv = null;
                        if (indicator != null) {
                            indicatorCache.put(key + indName, indicator);
                            iv = indicator.calculateLatest();
                        }
                        if (iv != null && iv.getName().length > 0 && iv.getValue().length > 0) {
                            setChanged();
                            notifyObservers(iv);
                        }
                    }
                });
            }
        }
    }
}
