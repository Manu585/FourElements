package com.github.manu585.fourelements.core.registry;

import java.util.Collection;

/**
 * Registry interface
 *
 * @param <K> Mapped Key
 * @param <V> Mapped Value
 */
public interface Registry<K, V> {

  /**
   * Get value by key.
   *
   * @param key Key
   * @return Value
   */
  V get(K key);

  /**
   * Store value in registry.
   *
   * @param key Key of value
   * @param value Value
   * @return Stored value
   */
  V put(K key, V value);

  /**
   * Remove value from registry.
   *
   * @param key Mapped Key
   * @return Removed value or null
   */
  V remove(K key);

  /**
   * Get a {@link Collection} of all values.
   *
   * @return all {@link V} via {@link Collection} interface
   */
  Collection<V> values();

  /**
   * Check if Key already exists in registry.
   *
   * @param key Mapped Key
   * @return <code>true</code> if it is in registry, else <code>false</code>
   */
  boolean contains(K key);

  /**
   * Clear whole registry.
   */
  void clear();

}
