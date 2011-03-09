package org.soqqo.vannitator.cache;

import java.util.Collection;
import java.util.HashMap;

/**
 * This is a nasty piece of work. 
 * TODO come back to this and make it a better genericised single static cache wrapper.
 * We want static (why ?? I can't remember now)
 * 
 * @author rbuckland
 *
 * @param <K>
 * @param <V>
 */
public class StaticHashMapCache<K, V> implements CacheWrapper<K, V> {

    @SuppressWarnings("rawtypes")
    private final static HashMap cache = new HashMap();

    @SuppressWarnings("unchecked")
    public void put(final K key, final V value) {
        getCache().put(key, value);
    }

    @SuppressWarnings("unchecked")
    public V get(final K key) {
        Object element = getCache().get(key);
        if (element != null) {
            return (V) element;
        }
        return null;
    }

    @SuppressWarnings("rawtypes")
    public HashMap getCache() {
        return cache;
    }

    @SuppressWarnings( "unchecked" )
    public Collection<V> values() {
        return cache.values();
    }
}
