/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.capsule;

import io.usethesource.capsule.util.EqualityComparator;

/**
 * This interface extends multi-maps for usage with custom data element comparators.
 */
@Deprecated
public interface SetMultimapEq<K, V> {

  boolean containsKeyEquivalent(final Object o, final EqualityComparator<Object> cmp);

  boolean containsValueEquivalent(final Object o, final EqualityComparator<Object> cmp);

  boolean containsEntryEquivalent(final Object o0, final Object o1,
      final EqualityComparator<Object> cmp);

  Set.Immutable<V> getEquivalent(final Object o, final EqualityComparator<Object> cmp);

  @Deprecated
  interface Immutable<K, V> extends SetMultimapEq<K, V>, AsTransient<SetMultimap.Transient<K, V>> {

    SetMultimap.Immutable<K, V> __putEquivalent(final K key, final V value,
        final EqualityComparator<Object> cmp);

    SetMultimap.Immutable<K, V> __putEquivalent(final K key, final Set.Immutable<V> values,
        final EqualityComparator<Object> cmp);

    @Deprecated
    default SetMultimap.Immutable<K, V> __putEquivalent(final Set.Immutable<K> keys, final V value,
        final EqualityComparator<Object> cmp) {
      final SetMultimap.Transient<K, V> tmp = this.asTransient();
      tmp.__putEquivalent(keys, value, cmp);
      return tmp.freeze();
    }

    SetMultimap.Immutable<K, V> __insertEquivalent(final K key, final V value,
        final EqualityComparator<Object> cmp);

    SetMultimap.Immutable<K, V> __insertEquivalent(final K key,
        final Set.Immutable<V> values, final EqualityComparator<Object> cmp);

    @Deprecated
    default SetMultimap.Immutable<K, V> __insertEquivalent(final Set.Immutable<K> keys, final V value,
        final EqualityComparator<Object> cmp) {
      final SetMultimap.Transient<K, V> tmp = this.asTransient();
      tmp.__insertEquivalent(keys, value, cmp);
      return tmp.freeze();
    }

    SetMultimap.Immutable<K, V> __removeEquivalent(final K key,
        final EqualityComparator<Object> cmp);

    SetMultimap.Immutable<K, V> __removeEquivalent(final K key, final V val,
        final EqualityComparator<Object> cmp);

    @Deprecated
    default SetMultimap.Immutable<K, V> __removeEquivalent(final Set.Immutable<K> keys, final V value,
        final EqualityComparator<Object> cmp) {
      final SetMultimap.Transient<K, V> tmp = this.asTransient();
      tmp.__removeEquivalent(keys, value, cmp);
      return tmp.freeze();
    }

    default SetMultimap.Immutable<K, V> unionEquivalent(
        final SetMultimap<? extends K, ? extends V> setMultimap, final EqualityComparator<Object> cmp) {
      final SetMultimap.Transient<K, V> builder = this.asTransient();

      setMultimap.entrySet().stream()
          .forEach(entry -> builder.__insertEquivalent(entry.getKey(), entry.getValue(), cmp));

      return builder.freeze();
    }

    default SetMultimap.Immutable<K, V> intersectEquivalent(
        final SetMultimap<? extends K, ? extends V> setMultimap, final EqualityComparator<Object> cmp) {
      final SetMultimap.Transient<K, V> builder = SetMultimap.Transient.of();

      setMultimap.entrySet().stream()
          .filter(entry -> this.containsEntryEquivalent(entry.getKey(), entry.getValue(), cmp))
          .forEach(entry -> builder.__insertEquivalent(entry.getKey(), entry.getValue(), cmp));

      return builder.freeze();
    }

    default SetMultimap.Immutable<K, V> complementEquivalent(
        final SetMultimap<? extends K, ? extends V> setMultimap, final EqualityComparator<Object> cmp) {
      final SetMultimap.Transient<K, V> builder = SetMultimap.Transient.of();

      setMultimap.entrySet().stream()
          .filter(entry -> !this.containsEntryEquivalent(entry.getKey(), entry.getValue(), cmp))
          .forEach(entry -> builder.__insertEquivalent(entry.getKey(), entry.getValue(), cmp));

      return builder.freeze();
    }

  }

  @Deprecated
  interface Transient<K, V> extends SetMultimapEq<K, V>, AsPersistent<SetMultimap.Immutable<K, V>> {

    boolean __putEquivalent(final K key, final V value, final EqualityComparator<Object> cmp);

    boolean __putEquivalent(final K key, final Set.Immutable<V> values,
        final EqualityComparator<Object> cmp);

    @Deprecated
    default boolean __putEquivalent(final Set.Immutable<K> keys, final V value,
        final EqualityComparator<Object> cmp) {
      return keys.stream()
          .map(key -> this.__putEquivalent(key, value, cmp))
          .reduce(Boolean::logicalOr)
          .orElse(false);
    }

    boolean __insertEquivalent(final K key, final V value, final EqualityComparator<Object> cmp);

    boolean __insertEquivalent(final K key, final Set.Immutable<V> values,
        final EqualityComparator<Object> cmp);

    @Deprecated
    default boolean __insertEquivalent(final Set.Immutable<K> keys, final V value,
        final EqualityComparator<Object> cmp) {
      return keys.stream()
          .map(key -> this.__insertEquivalent(key, value, cmp))
          .reduce(Boolean::logicalOr)
          .orElse(false);
    }

    boolean __removeEquivalent(final K key, final EqualityComparator<Object> cmp);

    boolean __removeEquivalent(final K key, final V val, final EqualityComparator<Object> cmp);

    @Deprecated
    default boolean __removeEquivalent(final Set.Immutable<K> keys, final V value,
        final EqualityComparator<Object> cmp) {
      return keys.stream()
          .map(key -> this.__removeEquivalent(key, value, cmp))
          .reduce(Boolean::logicalOr)
          .orElse(false);
    }

    @Deprecated
    default boolean unionEquivalent(final SetMultimap<? extends K, ? extends V> setMultimap,
        final EqualityComparator<Object> cmp) {
      return setMultimap.entrySet().stream()
          .map(entry -> this.__insertEquivalent(entry.getKey(), entry.getValue(), cmp))
          .reduce(Boolean::logicalOr)
          .orElse(false);
    }

//    @Deprecated
//    default boolean intersectEquivalent(final SetMultimap<? extends K, ? extends V> setMultimap,
//        final EqualityComparator<Object> cmp) {
//        // TODO: misses duplication of entrySet() operation in SetMultimapEq
//    }
//
//    @Deprecated
//    default boolean complementEquivalent(final SetMultimap<? extends K, ? extends V> setMultimap,
//        final EqualityComparator<Object> cmp) {
//      return setMultimap.entrySet().stream()
//          .map(entry -> this.__removeEquivalent(entry.getKey(), entry.getValue(), cmp))
//          .reduce(Boolean::logicalOr)
//          .orElse(false);
//    }

  }
}
