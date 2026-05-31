package com.github.manu585.fourelements.core.registry;

public interface Cache <K, V> {

  V get(K key);

  V remove(K key);

  void put(K key, V value);

  void clear();

}
