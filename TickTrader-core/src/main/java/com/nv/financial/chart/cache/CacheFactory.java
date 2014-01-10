package com.nv.financial.chart.cache;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.infinispan.Cache;
import org.infinispan.manager.DefaultCacheManager;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 *
 */
public class CacheFactory {
    public static String EVENT_QT_CACHE = "EventQtCache";
    public static String EVENT_IDC_CACHE = "EventIdcCache";
    public static String INDICATOR_CACHE = "IndicatorCache";
    private static final Logger logger = LogManager.getLogger(CacheFactory.class);
    private static DefaultCacheManager cacheManager;
    private static Set<String> caches;

    static {
        try {
//            cacheManager = new DefaultCacheManager("Infinispan.xml");
            cacheManager = new DefaultCacheManager(CacheFactory.class.getClassLoader().getResourceAsStream("Infinispan.xml"));
            caches = new HashSet<String>();
            caches.add(EVENT_QT_CACHE);
            caches.add(EVENT_IDC_CACHE);
            caches.add(INDICATOR_CACHE);
        } catch (IOException e) {
            logger.error("", e);
        }
    }

    public static ICache getCache(String cacheName) {
        if(!caches.contains(cacheName)){
            logger.warn("doesn't support this cache : " + cacheName);
        }
        Cache<Object, Object> cache = cacheManager.getCache(cacheName);
        return new InfiniCacheAdapter(cache);
    }

    public static void main(String arg[]){
        ICache cache = CacheFactory.getCache("IndicatorCache");
        System.out.println(cache);
        cache = CacheFactory.getCache("ABC");
        System.out.println(cache);
    }

}
