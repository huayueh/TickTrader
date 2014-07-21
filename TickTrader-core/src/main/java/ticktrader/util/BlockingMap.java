package ticktrader.util;

import com.nv.financial.chart.service.EventService;
import org.apache.log4j.Logger;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * User: Harvey
 * Date: 2014/7/21
 * Time: 下午 06:03
 */
public class BlockingMap<K, V> implements Map<K, V> {
    private static final Logger logger = Logger.getLogger(EventService.class);
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition condition = lock.newCondition();
    private final Map<K, V> map = new ConcurrentSkipListMap<K, V>();
    private final AtomicInteger count = new AtomicInteger(0);
    private final int capacity = 20;

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return map.containsValue(value);
    }

    @Override
    public V get(Object key) {
        throw new NotImplementedException();
    }

    @Override
    public V put(K key, V value) {
        try {
            lock.lockInterruptibly();
            while (count.get() == capacity) {
                condition.await();
            }
            count.getAndIncrement();
            map.put(key, value);
            logger.debug("put " + key);
            condition.signal();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }

        return value;
    }

    public V remove(Object key) {
        V x = null;

        try {
            lock.lockInterruptibly();
            while (count.get() == 0) {
                condition.await();
            }
            while (!map.containsKey(key)) {
                logger.debug("wait on " + key);
                condition.await();
            }
            x = map.remove(key);
            count.getAndDecrement();
            condition.signal();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }

        return x;
    }

    @Override
    public void putAll(Map m) {
        throw new NotImplementedException();
    }

    @Override
    public void clear() {
        map.clear();
    }

    @Override
    public Set keySet() {
        return map.keySet();
    }

    @Override
    public Collection values() {
        return map.values();
    }

    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        return map.entrySet();
    }
}
