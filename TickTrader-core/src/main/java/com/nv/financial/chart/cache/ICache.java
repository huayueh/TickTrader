package com.nv.financial.chart.cache;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 *
 */
public interface ICache<K, V> {

    public Set<K> keySet();

    public Collection<V> values();

    public Set<Map.Entry<K,V>> entrySet();

    public String getName();

    public int size();

    public boolean isEmpty();

    boolean containsKey(Object key);

    public boolean containsValue(V value);

    public Object get(K key);

    public Object put(K o, V o2);

    public V remove(Object key);

    public void putAll(Map<? extends K, ? extends V> m);

    public void clear();

//    public void addListener(Object o);
//
//    public void removeListener(Object o);
//
//    public Set<Object> getListeners();
}
