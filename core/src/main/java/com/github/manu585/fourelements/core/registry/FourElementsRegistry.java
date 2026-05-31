package com.github.manu585.fourelements.core.registry;

import java.util.HashMap;
import java.util.Map;

public abstract class FourElementsRegistry <K, V> {

  public Map<K, V> register = new HashMap<>();

  public void register(K key, V value) {
    register.put(key, value);
  }

  public void unregister(K key) {
    register.remove(key);
  }

  public V get(K key) {
    return register.get(key);
  }

  public V remove(K key) {
    return register.remove(key);
  }

}
