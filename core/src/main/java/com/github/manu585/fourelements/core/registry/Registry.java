package com.github.manu585.fourelements.core.registry;

import java.util.Collection;

public interface Registry<K, V> {

  V get(K key);

  V put(K key, V value);

  V remove(K key);

  Collection<V> values();

  boolean contains(K key);

  void clear();

}
