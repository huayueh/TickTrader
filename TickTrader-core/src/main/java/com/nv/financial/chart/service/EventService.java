package com.nv.financial.chart.service;

import com.nv.financial.chart.ChartSetting;
import com.nv.financial.chart.cache.CacheFactory;
import com.nv.financial.chart.cache.ICache;
import com.nv.financial.chart.dto.IndicatorValue;
import com.nv.financial.chart.dto.Quote;
import com.nv.financial.chart.dto.Tick;
import com.nv.financial.chart.quote.TimePeriod;
import com.nv.financial.chart.quote.provider.IQuoteProvider;
import com.nv.financial.chart.util.Utils;
import static com.nv.financial.chart.util.Utils.PRODUCT_DELIMITER;
import org.apache.log4j.Logger;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Main function:
 * 1. Facade to client api.
 * 2. Keep seeding heartbeat for others to monitor.
 * 3. Monitor previous event server. Take his job while it's invalidate.
 */
public class EventService extends Observable implements IEventService, Observer {
    private static final Logger logger = Logger.getLogger(EventService.class);
    private static EventService Instance = new EventService();
    private static IndicatorService indicatorService = IndicatorService.getInstance();
    private ICache<Integer, Map<String, AtomicInteger>> eventQtCache = CacheFactory.getCache(CacheFactory.EVENT_QT_CACHE);
    private ICache<Integer, Map<String, AtomicInteger>> eventIdcCache = CacheFactory.getCache(CacheFactory.EVENT_IDC_CACHE);
    private Map<String, AtomicInteger> interestQt = new ConcurrentHashMap();
    private Map<String, AtomicInteger> interestIdc = new ConcurrentHashMap();
    private IQuoteService quoteService = QuoteService.getInstance();

    private final int serverId = ChartSetting.getServerId();

    public static EventService getInstance() {
        return Instance;
    }

    private EventService() {
        eventQtCache.put(serverId, interestQt);
        eventIdcCache.put(serverId, interestIdc);
    }

    public ICache<Integer, Map<String, AtomicInteger>> getEventQtCache() {
        return eventQtCache;
    }

    public ICache<Integer, Map<String, AtomicInteger>> getEventIdcCache() {
        return eventIdcCache;
    }

    public Map<String, AtomicInteger> getInterestQt() {
        return interestQt;
    }

    public Map<String, AtomicInteger> getInterestIdc() {
        return interestIdc;
    }

    public int getServerId() {
        return serverId;
    }

    @Override
    public void onEvent(Tick tick) {
        if (tick == null)
            return;

        //on tick
        //markup on 20131101. still use multicast
//        logger.debug("market tick : " + tick);
//        setChanged();
//        notifyObservers(tick);

        //on Quote
        for (TimePeriod period : TimePeriod.values()) {
            IQuoteProvider quoteProvider = quoteService.getQuoteProvider(tick.getContract(), tick.getProductId(), period);
            if (quoteProvider == null){
                logger.warn("can't get quote contract " + tick.getContract() + tick.getProductId() + period);
                return;
            }
            Quote qt = quoteProvider.latestQuote();
            if (qt == null)
                continue;

            //pass to API
            if (interestQt.containsKey(qt.getContract() + qt.getProduct() + period.name())) {
                //pass to remote observer
                logger.debug("market quote : " + qt);
                setChanged();
                notifyObservers(qt);
            }
        }
    }

    @Override
    public void onEvent(IndicatorValue idcValue) {
        if (idcValue == null)
            return;
        if (idcValue.getTime() > 0)
            logger.debug("onIndicatorValue : " + idcValue);
        //pass to API
        if (interestIdc.containsKey(idcValue.getContract() + PRODUCT_DELIMITER +
                idcValue.getProduct() + PRODUCT_DELIMITER + idcValue.getIdcId() +
                PRODUCT_DELIMITER + idcValue.getPeriod())) {
            //pass to remote observer
            setChanged();
            notifyObservers(idcValue);
        }
    }

    public void addInterest(String group, String product, String period) {
        addInterest(group, product, TimePeriod.valueOf(period));
    }

    @Override
    public void addInterest(String group, String product, TimePeriod period) {
        if (group != null && product != null && period != null) {
            AtomicInteger cnt = interestQt.get(group + PRODUCT_DELIMITER + product + PRODUCT_DELIMITER + period.name());
            if (cnt == null) {
                cnt = new AtomicInteger(1);
                interestQt.put(group + product + period.name(), cnt);
            } else {
                cnt.addAndGet(1);
            }
            //to cluster cache
            Map<String, AtomicInteger> inter = (Map<String, AtomicInteger>) eventQtCache.get(serverId);
            inter.put(group + PRODUCT_DELIMITER + product + PRODUCT_DELIMITER + period.name(), cnt);
            eventQtCache.put(serverId, inter);
        }
    }

    public void removeInterest(String group, String product, String period) {
        removeInterest(group, product, TimePeriod.valueOf(period));
    }

    @Override
    public void removeInterest(String group, String product, TimePeriod period) {
        if (group != null && product != null && period != null) {
            AtomicInteger cnt = interestQt.get(group + PRODUCT_DELIMITER + product + PRODUCT_DELIMITER + period.name());
            if (cnt != null) {
                int count = cnt.addAndGet(-1);
                Map<String, AtomicInteger> inter = (Map<String, AtomicInteger>) eventQtCache.get(serverId);
                if (count == 0) {
                    interestQt.remove(group + PRODUCT_DELIMITER + product + PRODUCT_DELIMITER + period.name());
                    inter.remove(group + product + period.name());
                } else {
                    inter.put(group + PRODUCT_DELIMITER + product + PRODUCT_DELIMITER + period.name(), cnt);
                    eventQtCache.put(serverId, inter);
                }
            }
        }
    }

    public void addIndicator(String group, String product, String indicator, String period) {
        addIndicator(group, product, indicator, TimePeriod.valueOf(period));
    }

    @Override
    public void addIndicator(String group, String product, String indicator, TimePeriod period) {
        if (group != null && product != null && indicator != null && period != null) {
            AtomicInteger cnt = interestIdc.get(group + PRODUCT_DELIMITER + product + PRODUCT_DELIMITER + indicator + PRODUCT_DELIMITER + period.name());
            if (cnt == null) {
                cnt = new AtomicInteger(1);
                interestIdc.put(group + PRODUCT_DELIMITER + product + PRODUCT_DELIMITER + indicator + PRODUCT_DELIMITER + period.name(), cnt);
                indicatorService.listen(indicator, group, product, period);
            } else {
                cnt.addAndGet(1);
            }
            //to cluster cache
            Map<String, AtomicInteger> inter = (Map<String, AtomicInteger>) eventIdcCache.get(serverId);
            inter.put(group + PRODUCT_DELIMITER + product + PRODUCT_DELIMITER + indicator + PRODUCT_DELIMITER + period.name(), cnt);
            eventIdcCache.put(serverId, inter);
        }
    }

    public void removeIndicator(String group, String product, String indicator, String period) {
        removeIndicator(group, product, indicator, TimePeriod.valueOf(period));
    }

    @Override
    public void removeIndicator(String group, String product, String indicator, TimePeriod period) {
        if (group != null && product != null && indicator != null && period != null) {
            AtomicInteger cnt = interestIdc.get(group + PRODUCT_DELIMITER + product + PRODUCT_DELIMITER + indicator + PRODUCT_DELIMITER + period.name());
            if (cnt != null) {
                int count = cnt.addAndGet(-1);
                Map<String, AtomicInteger> inter = (Map<String, AtomicInteger>) eventIdcCache.get(serverId);
                if (count == 0) {
                    interestIdc.remove(group + PRODUCT_DELIMITER + product + PRODUCT_DELIMITER + indicator + PRODUCT_DELIMITER + period.name());
                    inter.remove(group + product + indicator + period.name());
                    indicatorService.removeListen(indicator, group, product, period);
                } else {
                    inter.put(group + PRODUCT_DELIMITER + product + PRODUCT_DELIMITER + indicator + PRODUCT_DELIMITER + period.name(), cnt);
                    eventIdcCache.put(serverId, inter);
                }
            }
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        if (arg instanceof Tick) {
            onEvent((Tick) arg);
        }

        if (arg instanceof IndicatorValue) {
            onEvent((IndicatorValue) arg);
        }
    }

    private int previousServer() {
        if (serverId > 0)
            return serverId - 1;
        else
            return ChartSetting.getEventServerNum() - 1;
    }
}
