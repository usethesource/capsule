/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.capsule;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.BiFunction;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import io.usethesource.capsule.core.PersistentTrieSetMultimap;
import io.usethesource.capsule.util.EqualityComparator;

/**
 * Experimental interface for a multimap data type.
 */
public interface SetMultimap<K, V> {

  /**
   * Return the number of key-value pairs contained in this multimap.
   *
   * @return number of key-value pairs in this multimap
   */
  int size();

  default int sizeDistinct() {
    return (int) entrySet().stream().map(Entry::getKey).distinct().count();
  }

  boolean isEmpty();

  boolean containsKey(final Object o);

  boolean containsValue(final Object o);

  boolean containsEntry(final Object o0, final Object o1);

  Set.Immutable<V> get(final java.lang.Object o);

  java.util.Set<K> keySet();

  java.util.Collection<V> values();

  java.util.Set<Map.Entry<K, V>> entrySet();

  Iterator<K> keyIterator();

  Iterator<V> valueIterator();

  Iterator<Entry<K, V>> entryIterator();

  // TODO: Iterator<Map.Entry<K, Set<V>>> groupByKeyIterator();

  /**
   * Iterates over the raw internal structure. Optional operation.
   *
   * @return native iterator, if supported
   * @throws UnsupportedOperationException, if not supported
   */
  default Iterator<Entry<K, Object>> nativeEntryIterator() throws UnsupportedOperationException {
    throw new UnsupportedOperationException("Not yet implemented @ Multi-Map.");
  }

  <T> Iterator<T> tupleIterator(final BiFunction<K, V, T> dataConverter);

  default <T> Stream<T> tupleStream(final BiFunction<K, V, T> dataConverter) {
    final Iterator<T> iterator = tupleIterator(dataConverter);

    /**
     * TODO: differentiate characteristics between TRANSIENT and IMMUTABLE. For the latter case add
     * {@link Spliterator.IMMUTABLE}.
     */
    final int characteristics = Spliterator.DISTINCT | Spliterator.SIZED;
    final Spliterator<T> spliterator = Spliterators.spliterator(iterator, size(), characteristics);

    return StreamSupport.stream(spliterator, false);
  }

  /**
   * Returns the hash code for this multimap.
   *
   * The hash code is defined to equal the hash of a {@link Set<Map.Entry<K, V>>} view (rather than
   * to equal the hash code of {@link Map<K, Set<V>>}).
   *
   * @return the hash code for this multimap
   */
  @Override
  int hashCode();

  /**
   * Compares the specified object for equality against this multimap.
   *
   * The notion of equality is equal to the {@link Set<Map.Entry<K, V>>} view of a multimap, i.e.,
   * all key-value pairs have to equal.
   *
   * @param other the object that is checked for equality against this multimap
   * @return {@code true} if the specified object is equal to this map
   */
  @Override
  boolean equals(Object other);

  interface Immutable<K, V> extends SetMultimap<K, V>, SetMultimapEq.Immutable<K, V> {

    SetMultimap.Immutable<K, V> __put(final K key, final V value);

    default SetMultimap.Immutable<K, V> __put(final K key, final Set.Immutable<V> values) {
      throw new UnsupportedOperationException("Not yet implemented @ Multi-Map.");
    }

    SetMultimap.Immutable<K, V> __insert(final K key, final V value);

    default SetMultimap.Immutable<K, V> __insert(final K key, final Set.Immutable<V> values) {
      throw new UnsupportedOperationException("Not yet implemented @ Multi-Map.");
    }

    // removes all mappings with 'key'
    default SetMultimap.Immutable<K, V> __remove(final K key) {
      throw new UnsupportedOperationException("Not yet implemented @ Multi-Map.");
    }

    SetMultimap.Immutable<K, V> __remove(final K key, final V val);

    default SetMultimap.Immutable<K, V> union(
        final SetMultimap<? extends K, ? extends V> setMultimap) {
      throw new UnsupportedOperationException("Not yet implemented @ Multi-Map.");
    }

    default SetMultimap.Immutable<K, V> intersect(
        final SetMultimap<? extends K, ? extends V> setMultimap) {
      throw new UnsupportedOperationException("Not yet implemented @ Multi-Map.");
    }

    default SetMultimap.Immutable<K, V> complement(
        final SetMultimap<? extends K, ? extends V> setMultimap) {
      throw new UnsupportedOperationException("Not yet implemented @ Multi-Map.");
    }

    boolean isTransientSupported();

    SetMultimap.Transient<K, V> asTransient();

    static <K, V> SetMultimap.Immutable<K, V> of() {
      return PersistentTrieSetMultimap.of();
    }

    static <K, V> SetMultimap.Immutable<K, V> of(K key, V value) {
      return PersistentTrieSetMultimap.of(key, value);
    }

    static <K, V> SetMultimap.Immutable<K, V> of(K key0, V value0, K key1, V value1) {
      return PersistentTrieSetMultimap.of(key0, value0, key1, value1);
    }

    @Deprecated
    static <K, V> SetMultimap.Immutable<K, V> of(EqualityComparator<Object> cmp) {
      return PersistentTrieSetMultimap.of(cmp);
    }

  }

  /*
   * TODO: consider return types for observability: i.e., either returning Immutable<V> or boolean?
   */
  interface Transient<K, V> extends SetMultimap<K, V>, SetMultimapEq.Transient<K, V> {

    default boolean __put(final K key, final V value) {
      throw new UnsupportedOperationException("Not yet implemented @ Multi-Map.");
    }

    default boolean __put(final K key, final Set.Immutable<V> values) {
      throw new UnsupportedOperationException("Not yet implemented @ Multi-Map.");
    }

    boolean __insert(final K key, final V value);

    default boolean __insert(final K key, final Set.Immutable<V> values) {
      throw new UnsupportedOperationException("Not yet implemented @ Multi-Map.");
    }

    default boolean __remove(final K key) {
      throw new UnsupportedOperationException("Not yet implemented @ Multi-Map.");
    }

    boolean __remove(final K key, final V val);

    default boolean union(final SetMultimap<? extends K, ? extends V> setMultimap) {
      throw new UnsupportedOperationException("Not yet implemented @ Multi-Map.");
    }

    default boolean intersect(final SetMultimap<? extends K, ? extends V> setMultimap) {
      throw new UnsupportedOperationException("Not yet implemented @ Multi-Map.");
    }

    default boolean complement(final SetMultimap<? extends K, ? extends V> setMultimap) {
      throw new UnsupportedOperationException("Not yet implemented @ Multi-Map.");
    }

    SetMultimap.Immutable<K, V> freeze();

    static <K, V> SetMultimap.Transient<K, V> of() {
      return PersistentTrieSetMultimap.transientOf();
    }

    @Deprecated
    static <K, V> SetMultimap.Transient<K, V> of(EqualityComparator<Object> cmp) {
      return PersistentTrieSetMultimap.transientOf(cmp);
    }

  }

}
