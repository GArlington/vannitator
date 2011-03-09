package org.soqqo.vannitator.cache;

import java.util.List;

public interface MultiKeyCacheWrapper<K, V> {
    void put(K key, V value);

    List<V> getAll(K key);
}