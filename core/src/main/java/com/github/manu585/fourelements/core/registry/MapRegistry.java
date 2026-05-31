package com.github.manu585.fourelements.core.registry;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MapRegistry<K, V> implements Registry<K, V> {

  protected final Map<K, V> registry = new ConcurrentHashMap<>();

  @Override
  public V get(K key) {
    return registry.get(key);
  }

  @Override
  public V put(K key, V value) {
    return registry.put(key, value);
  }

  @Override
  public V remove(K key) {
    return registry.remove(key);
  }

  @Override
  public Collection<V> values() {
    return Collections.unmodifiableCollection(registry.values());
  }

  @Override
  public boolean contains(K key) {
    return registry.containsKey(key);
  }

  @Override
  public void clear() {
    registry.clear();
  }

}
