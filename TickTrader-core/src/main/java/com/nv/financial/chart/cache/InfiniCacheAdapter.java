package com.nv.financial.chart.cache;

import org.infinispan.Cache;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 *
 */
public class InfiniCacheAdapter implements ICache{
    Cache cache;

    public InfiniCacheAdapter(Cache cache){
        this.cache = cache;
    }

    @Override
    public Set keySet() {
        return cache.keySet();
    }

    @Override
    public Collection values() {
        return cache.values();
    }

    @Override
    public Set<Map.Entry> entrySet() {
        return cache.entrySet();
    }

    @Override
    public String getName() {
        return cache.getName();
    }

    @Override
    public int size() {
        return cache.size();
    }

    @Override
    public boolean isEmpty() {
        return cache.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return cache.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return cache.containsValue(value);
    }

    @Override
    public Object get(Object key) {
        return cache.get(key);
    }

    @Override
    public Object put(Object o, Object o2) {
        return cache.put(o,o2);
    }

    @Override
    public Object remove(Object o) {
        return cache.remove(o);
    }

    @Override
    public void putAll(Map m) {
        cache.putAll(m);
    }

    @Override
    public void clear() {
        cache.clear();
    }

//    @Override
//    public void addListener(Object o) {
//        cache.addListener(o);
//    }
//
//    @Override
//    public void removeListener(Object o) {
//        cache.removeListener(o);
//    }
//
//    @Override
//    public Set<Object> getListeners() {
//        return cache.getListeners();
//    }
}
