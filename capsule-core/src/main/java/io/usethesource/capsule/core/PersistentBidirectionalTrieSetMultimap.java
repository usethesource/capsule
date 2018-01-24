/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.capsule.core;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;

import io.usethesource.capsule.BinaryRelation;
import io.usethesource.capsule.Set;
import io.usethesource.capsule.SetMultimap;
import io.usethesource.capsule.util.EqualityComparator;
import io.usethesource.capsule.util.function.TriFunction;

public class PersistentBidirectionalTrieSetMultimap<K, V> implements
    BinaryRelation.Immutable<K, V>, java.io.Serializable {

  private static final long serialVersionUID = 42L;

  private final SetMultimap.Immutable<K, V> fwd;
  private final SetMultimap.Immutable<V, K> bwd;

  public PersistentBidirectionalTrieSetMultimap(final SetMultimap.Immutable<K, V> fwd,
      final SetMultimap.Immutable<V, K> bwd) {
    this.fwd = fwd;
    this.bwd = bwd;
  }

  public static final <K, V> BinaryRelation.Immutable<K, V> of() {
    /*
     * NOTE: uses default multi-map to create nested forward and backward maps.
     *
     * TODO: make classes of nested multi-maps configurable.
     */
    return new PersistentBidirectionalTrieSetMultimap<K, V>(SetMultimap.Immutable.of(),
        SetMultimap.Immutable.of());
  }

  public static final <K, V> BinaryRelation.Transient<K, V> transientOf() {
    /*
     * NOTE: uses default multi-map to create nested forward and backward maps.
     *
     * TODO: make classes of nested multi-maps configurable.
     */
    return new TransientBidirectionalTrieSetMultimap<K, V>(SetMultimap.Transient.of(),
        SetMultimap.Transient.of());
  }

  private static <K, V, T> BinaryRelation.Immutable<K, V> wireTuple(T key,
      final Function<T, ? extends SetMultimap.Immutable<K, V>> fwdMerger,
      final Function<T, ? extends SetMultimap.Immutable<V, K>> bwdMerger) {

    return new PersistentBidirectionalTrieSetMultimap(
        fwdMerger.apply(key),
        bwdMerger.apply(key));
  }

  private static <K, V> BinaryRelation.Immutable<K, V> wireTuple(K key, V value,
      final BiFunction<K, V, ? extends SetMultimap.Immutable<K, V>> fwdMerger,
      final BiFunction<V, K, ? extends SetMultimap.Immutable<V, K>> bwdMerger) {

    return new PersistentBidirectionalTrieSetMultimap(fwdMerger.apply(key, value),
        bwdMerger.apply(value, key));
  }

//  private static <K, V, T, C> BinaryRelation.Immutable<K, V> wireTuple(T key, C cmp,
//      final BiFunction<T, C, ? extends SetMultimap.Immutable<K, V>> fwdMerger,
//      final BiFunction<T, C, ? extends SetMultimap.Immutable<V, K>> bwdMerger) {
//
//    return new PersistentBidirectionalTrieSetMultimap(fwdMerger.apply(key, cmp),
//        bwdMerger.apply(key, cmp));
//  }

  private static <K, V, C> BinaryRelation.Immutable<K, V> wireTuple(K key, V value, C cmp,
      final TriFunction<K, V, C, ? extends SetMultimap.Immutable<K, V>> fwdMerger,
      final TriFunction<V, K, C, ? extends SetMultimap.Immutable<V, K>> bwdMerger) {

    return new PersistentBidirectionalTrieSetMultimap(fwdMerger.apply(key, value, cmp),
        bwdMerger.apply(value, key, cmp));
  }

//  private static <K, V> BinaryRelation.Immutable<K, V> batchWireTuple(Set.Immutable<K> keys,
//      V value,
//      final BiFunction<Set.Immutable<K>, V, ? extends SetMultimap.Immutable<K, V>> fwdMerger,
//      final BiFunction<V, Set.Immutable<K>, ? extends SetMultimap.Immutable<V, K>> bwdMerger) {
//
//    return new PersistentBidirectionalTrieSetMultimap(fwdMerger.apply(keys, value),
//        bwdMerger.apply(value, keys));
//  }

  private static <K, V> BinaryRelation.Immutable<K, V> batchWireTuple(K key,
      Set.Immutable<V> values,
      final BiFunction<K, Set.Immutable<V>, ? extends SetMultimap.Immutable<K, V>> fwdMerger,
      final BiFunction<Set.Immutable<V>, K, ? extends SetMultimap.Immutable<V, K>> bwdMerger) {

    return new PersistentBidirectionalTrieSetMultimap(fwdMerger.apply(key, values),
        bwdMerger.apply(values, key));
  }

  private static <K, V, C> BinaryRelation.Immutable<K, V> batchWireTuple(Set.Immutable<K> keys,
      V value, C cmp,
      final TriFunction<Set.Immutable<K>, V, C, ? extends SetMultimap.Immutable<K, V>> fwdMerger,
      final TriFunction<V, Set.Immutable<K>, C, ? extends SetMultimap.Immutable<V, K>> bwdMerger) {

    return new PersistentBidirectionalTrieSetMultimap(fwdMerger.apply(keys, value, cmp),
        bwdMerger.apply(value, keys, cmp));
  }

  private static <K, V, C> BinaryRelation.Immutable<K, V> batchWireTuple(K key,
      Set.Immutable<V> values, C cmp,
      final TriFunction<K, Set.Immutable<V>, C, ? extends SetMultimap.Immutable<K, V>> fwdMerger,
      final TriFunction<Set.Immutable<V>, K, C, ? extends SetMultimap.Immutable<V, K>> bwdMerger) {

    return new PersistentBidirectionalTrieSetMultimap(fwdMerger.apply(key, values, cmp),
        bwdMerger.apply(values, key, cmp));
  }


  @Override
  public BinaryRelation.Immutable<V, K> inverse() {
    return new PersistentBidirectionalTrieSetMultimap<>(bwd, fwd);
  }

  @Override
  public SetMultimap<K, V> toSetMultimap() {
    return fwd;
  }

  @Override
  public int size() {
    return fwd.size();
  }

  @Override
  public int sizeDistinct() {
    return fwd.sizeDistinct();
  }

  @Override
  public boolean isEmpty() {
    return fwd.isEmpty();
  }

  @Override
  public boolean containsKey(Object o) {
    return fwd.containsKey(o);
  }

  @Override
  public boolean containsValue(Object o) {
    /*
     * hash lookup on inverse
     */
    return bwd.containsKey(o);
  }

  @Override
  public boolean containsEntry(Object o0, Object o1) {
    return fwd.containsEntry(o0, o1);
  }

  @Override
  public Set.Immutable<V> get(Object o) {
    return fwd.get(o);
  }

  @Override
  public java.util.Set<K> keySet() {
    return fwd.keySet();
  }

  @Override
  public Collection<V> values() {
    return fwd.values();
  }

  @Override
  public java.util.Set<Map.Entry<K, V>> entrySet() {
    return fwd.entrySet();
  }

  @Override
  public Iterator<K> keyIterator() {
    return fwd.keyIterator();
  }

  @Override
  public Iterator<V> valueIterator() {
    return fwd.valueIterator();
  }

  @Override
  public Iterator<Map.Entry<K, V>> entryIterator() {
    return fwd.entryIterator();
  }

  @Override
  public Iterator<Map.Entry<K, Object>> nativeEntryIterator() throws UnsupportedOperationException {
    return fwd.nativeEntryIterator();
  }

  @Override
  public <T> Iterator<T> tupleIterator(BiFunction<K, V, T> dataConverter) {
    return fwd.tupleIterator(dataConverter);
  }

  @Override
  public <T> Stream<T> tupleStream(BiFunction<K, V, T> dataConverter) {
    return fwd.tupleStream(dataConverter);
  }

  @Override
  public SetMultimap.Immutable<K, V> __put(K key, V value) {
    return wireTuple(key, value, fwd::__put, bwd::__put);
  }

  @Override
  public SetMultimap.Immutable<K, V> __put(K key, Set.Immutable<V> values) {
    return wireTuple(key,
        arg -> fwd.__put(arg, values),
        arg -> {
          final SetMultimap.Transient<V, K> tmp = bwd.asTransient();
          values.forEach(value -> tmp.__put(value, key));
          return tmp.freeze();
        }
    );
  }

  @Override
  public SetMultimap.Immutable<K, V> __insert(K key, V value) {
    return wireTuple(key, value, fwd::__insert, bwd::__insert);
  }

  /*
   * NOTE: transient counterpart not yet implemented
   */
  @Override
  public SetMultimap.Immutable<K, V> __insert(K key, Set.Immutable<V> values) {
    return batchWireTuple(key, values, fwd::__insert, bwd::__insert);
  }

//  @Override
//  public SetMultimap.Immutable<K, V> __insert(Set.Immutable<K> keys, V value) {
//    return batchWireTuple(keys, value,
//        arg -> fwd.__insert(keys, value), // fwd::__insert,
//        arg -> bwd.inverse().__insert(value, keys).inverse();
//  }

  @Override
  public SetMultimap.Immutable<K, V> __remove(K key) {
    return wireTuple(key,
        arg -> fwd.__remove(arg),
        arg -> bwd.__remove(fwd.get(arg), arg));
  }

  @Override
  public SetMultimap.Immutable<K, V> __remove(K key, V value) {
    return wireTuple(key, value, fwd::__remove, bwd::__remove);
  }

//  @Override
//  public SetMultimap.Immutable<K, V> union(SetMultimap<? extends K, ? extends V> setMultimap) {
//    return null;
//  }
//
//  @Override
//  public SetMultimap.Immutable<K, V> intersect(SetMultimap<? extends K, ? extends V> setMultimap) {
//    return null;
//  }
//
//  @Override
//  public SetMultimap.Immutable<K, V> complement(SetMultimap<? extends K, ? extends V> setMultimap) {
//    return null;
//  }

  @Override
  public int hashCode() {
    return fwd.hashCode();
  }

  @Override
  public boolean equals(Object other) {
    return fwd.equals(other);
  }

  @Override
  public String toString() {
    return fwd.toString();
  }

  @Override
  public boolean isTransientSupported() {
    return true;
  }

  @Override
  public BinaryRelation.Transient<K, V> asTransient() {
    return new TransientBidirectionalTrieSetMultimap<K, V>(fwd.asTransient(), bwd.asTransient());
  }

  @Override
  public boolean containsKeyEquivalent(Object o, EqualityComparator<Object> cmp) {
    return fwd.containsKeyEquivalent(o, cmp);
  }

  @Override
  public boolean containsValueEquivalent(Object o, EqualityComparator<Object> cmp) {
    return bwd.containsKeyEquivalent(o, cmp);
  }

  @Override
  public boolean containsEntryEquivalent(Object o0, Object o1,
      EqualityComparator<Object> cmp) {
    return fwd.containsEntryEquivalent(o0, o1, cmp);
  }

  @Override
  public Set.Immutable<V> getEquivalent(Object o, EqualityComparator<Object> cmp) {
    return fwd.getEquivalent(o, cmp);
  }

  @Override
  public SetMultimap.Immutable<K, V> __putEquivalent(K key, V value,
      EqualityComparator<Object> cmp) {
    return wireTuple(key, value, cmp, fwd::__putEquivalent, bwd::__putEquivalent);
  }

  @Override
  public SetMultimap.Immutable<K, V> __putEquivalent(K key, Set.Immutable<V> values,
      EqualityComparator<Object> cmp) {
    return batchWireTuple(key, values, cmp, fwd::__putEquivalent, bwd::__putEquivalent);
  }

  @Override
  public SetMultimap.Immutable<K, V> __insertEquivalent(K key, V value,
      EqualityComparator<Object> cmp) {
    return wireTuple(key, value, cmp, fwd::__insertEquivalent, bwd::__insertEquivalent);
  }

  @Override
  public SetMultimap.Immutable<K, V> __insertEquivalent(K key, Set.Immutable<V> values,
      EqualityComparator<Object> cmp) {
    return batchWireTuple(key, values, cmp, fwd::__insertEquivalent, bwd::__insertEquivalent);
  }

  @Override
  public SetMultimap.Immutable<K, V> __removeEquivalent(K key, EqualityComparator<Object> cmp) {
//     return wireTuple(key, cmp, fwd::__removeEquivalent, bwd::__removeEquivalent);

    return new PersistentBidirectionalTrieSetMultimap(
        fwd.__removeEquivalent(key, cmp),
        bwd.__removeEquivalent(fwd.getEquivalent(key, cmp), key, cmp)
    );

//    return wireTuple(key, cmp,
//        arg -> fwd.__removeEquivalent(arg, cmp),
//        arg -> bwd.__removeEquivalent(fwd.getEquivalent(arg, cmp), arg, cmp));
  }

  @Override
  public SetMultimap.Immutable<K, V> __removeEquivalent(K key, V value,
      EqualityComparator<Object> cmp) {
    return wireTuple(key, value, cmp, fwd::__removeEquivalent, bwd::__removeEquivalent);
  }

//  @Override
//  public SetMultimap.Immutable<K, V> unionEquivalent(
//      SetMultimap<? extends K, ? extends V> setMultimap,
//      EqualityComparator<Object> cmp) {
//    return wireTuple(setMultimap, cmp, fwd::unionEquivalent, bwd::unionEquivalent);
//  }
//
//  @Override
//  public SetMultimap.Immutable<K, V> intersectEquivalent(
//      SetMultimap<? extends K, ? extends V> setMultimap,
//      EqualityComparator<Object> cmp) {
//    return wireTuple(setMultimap, cmp, fwd::intersectEquivalent, bwd::intersectEquivalent);
//  }
//
//  @Override
//  public SetMultimap.Immutable<K, V> complementEquivalent(
//      SetMultimap<? extends K, ? extends V> setMultimap,
//      EqualityComparator<Object> cmp) {
//    return wireTuple(setMultimap, cmp, fwd::complementEquivalent, bwd::complementEquivalent);
//  }
}


class TransientBidirectionalTrieSetMultimap<K, V> implements BinaryRelation.Transient<K, V> {

  private final SetMultimap.Transient<K, V> fwd;
  private transient final SetMultimap.Transient<V, K> bwd;

  public TransientBidirectionalTrieSetMultimap(final SetMultimap.Transient<K, V> fwd,
      final SetMultimap.Transient<V, K> bwd) {
    this.fwd = fwd;
    this.bwd = bwd;
  }

  private static <K, V> boolean wireTransientTuple(K key, V value,
      final BiFunction<K, V, Boolean> fwdMerger, final BiFunction<V, K, Boolean> bwdMerger) {
    boolean fwdResult = fwdMerger.apply(key, value);
    boolean bwdResult = bwdMerger.apply(value, key);
    return fwdResult || bwdResult;
  }

  private static <K, V, C> boolean wireTransientTuple(K key, V value, C cmp,
      final TriFunction<K, V, C, Boolean> fwdMerger, final TriFunction<V, K, C, Boolean> bwdMerger) {
    boolean fwdResult = fwdMerger.apply(key, value, cmp);
    boolean bwdResult = bwdMerger.apply(value, key, cmp);
    return fwdResult || bwdResult;
  }

  @Override
  public BinaryRelation.Transient<V, K> inverse() {
    return new TransientBidirectionalTrieSetMultimap<>(bwd, fwd);
  }

  @Override
  public SetMultimap<K, V> toSetMultimap() {
    return fwd;
  }

  @Override
  public int size() {
    return fwd.size();
  }

  @Override
  public int sizeDistinct() {
    return fwd.sizeDistinct();
  }

  @Override
  public boolean isEmpty() {
    return fwd.isEmpty();
  }

  @Override
  public boolean containsKey(Object o) {
    return fwd.containsKey(o);
  }

  @Override
  public boolean containsValue(Object o) {
    /*
     * hash lookup on inverse
     */
    return bwd.containsKey(o);
  }

  @Override
  public boolean containsEntry(Object o0, Object o1) {
    return fwd.containsEntry(o0, o1);
  }

  @Override
  public Set.Immutable<V> get(Object o) {
    return fwd.get(o);
  }

  @Override
  public java.util.Set<K> keySet() {
    return fwd.keySet();
  }

  @Override
  public Collection<V> values() {
    return fwd.values();
  }

  @Override
  public java.util.Set<Map.Entry<K, V>> entrySet() {
    return fwd.entrySet();
  }

  @Override
  public Iterator<K> keyIterator() {
    return fwd.keyIterator();
  }

  @Override
  public Iterator<V> valueIterator() {
    return fwd.valueIterator();
  }

  @Override
  public Iterator<Map.Entry<K, V>> entryIterator() {
    return fwd.entryIterator();
  }

  @Override
  public Iterator<Map.Entry<K, Object>> nativeEntryIterator() throws UnsupportedOperationException {
    return fwd.nativeEntryIterator();
  }

  @Override
  public <T> Iterator<T> tupleIterator(BiFunction<K, V, T> dataConverter) {
    return fwd.tupleIterator(dataConverter);
  }

  @Override
  public <T> Stream<T> tupleStream(BiFunction<K, V, T> dataConverter) {
    return fwd.tupleStream(dataConverter);
  }

  @Override
  public boolean __put(K key, V value) {
    return wireTransientTuple(key, value, fwd::__put, bwd::__put);
  }

  @Override
  public boolean __put(K key, Set.Immutable<V> values) {
//     return wireTransientTuple(key, values, fwd::__put, bwd::__put);

    boolean res0 = fwd.__put(key, values);
    boolean res1 = values.stream()
        .map(value -> bwd.__put(value, key))
        .reduce(Boolean::logicalOr)
        .orElse(false);

    return res0 || res1;
  }

  @Override
  public boolean __insert(K key, V value) {
    return wireTransientTuple(key, value, fwd::__insert, bwd::__insert);
  }

  @Override
  public boolean __insert(K key, Set.Immutable<V> values) {
//    return wireTransientTuple(key, values, fwd::__insert, bwd::__insert);

    boolean res0 = fwd.__insert(key, values);
    boolean res1 = values.stream()
        .map(value -> bwd.__insert(value, key))
        .reduce(Boolean::logicalOr)
        .orElse(false);

    return res0 || res1;
  }

  @Override
  public boolean __remove(K key) {
    boolean res0 = fwd.__remove(key);
    boolean res1 = bwd.__remove(fwd.get(key), key);

    return res0 || res1;
  }

  @Override
  public boolean __remove(K key, V value) {
    return wireTransientTuple(key, value, fwd::__remove, bwd::__remove);
  }

//  @Override
//  public boolean union(SetMultimap<? extends K, ? extends V> setMultimap) {
//    return false;
//  }
//
//  @Override
//  public boolean intersect(SetMultimap<? extends K, ? extends V> setMultimap) {
//    return false;
//  }
//
//  @Override
//  public boolean complement(SetMultimap<? extends K, ? extends V> setMultimap) {
//    return false;
//  }

  @Override
  public int hashCode() {
    return fwd.hashCode();
  }

  @Override
  public boolean equals(Object other) {
    return fwd.equals(other);
  }

  @Override
  public String toString() {
    return fwd.toString();
  }

  @Override
  public BinaryRelation.Immutable<K, V> freeze() {
    return new PersistentBidirectionalTrieSetMultimap<>(fwd.freeze(), bwd.freeze());
  }

  @Override
  public boolean containsKeyEquivalent(Object o, EqualityComparator<Object> cmp) {
    return fwd.containsKeyEquivalent(o, cmp);
  }

  @Override
  public boolean containsValueEquivalent(Object o, EqualityComparator<Object> cmp) {
    return fwd.containsValueEquivalent(o, cmp);
  }

  @Override
  public boolean containsEntryEquivalent(Object o0, Object o1,
      EqualityComparator<Object> cmp) {
    return fwd.containsEntryEquivalent(o0, 01, cmp);
  }

  @Override
  public Set.Immutable<V> getEquivalent(Object o, EqualityComparator<Object> cmp) {
    return fwd.getEquivalent(o, cmp);
  }

  @Override
  public boolean __putEquivalent(K key, V value, EqualityComparator<Object> cmp) {
    return wireTransientTuple(key, value, cmp, fwd::__putEquivalent, bwd::__putEquivalent);
  }

  @Override
  public boolean __putEquivalent(K key, Set.Immutable<V> values,
      EqualityComparator<Object> cmp) {
    return wireTransientTuple(key, values, cmp, fwd::__putEquivalent, bwd::__putEquivalent);
  }

  @Override
  public boolean __insertEquivalent(K key, V value,
      EqualityComparator<Object> cmp) {
    return wireTransientTuple(key, value, cmp, fwd::__insertEquivalent, bwd::__insertEquivalent);

  }

  @Override
  public boolean __insertEquivalent(K key, Set.Immutable<V> values,
      EqualityComparator<Object> cmp) {
    return wireTransientTuple(key, values, cmp, fwd::__insertEquivalent, bwd::__insertEquivalent);
  }

  @Override
  public boolean __removeEquivalent(K key, EqualityComparator<Object> cmp) {
    boolean res0 = fwd.__removeEquivalent(key, cmp);
    boolean res1 = bwd.__removeEquivalent(fwd.getEquivalent(key, cmp), key, cmp);

    return res0 || res1;
  }

  @Override
  public boolean __removeEquivalent(K key, V value,
      EqualityComparator<Object> cmp) {
    return wireTransientTuple(key, value, cmp, fwd::__removeEquivalent, bwd::__removeEquivalent);
  }

//  @Override
//  public boolean unionEquivalent(SetMultimap<? extends K, ? extends V> setMultimap,
//      EqualityComparator<Object> cmp) {
//    return false;
//  }
//
//  @Override
//  public boolean intersectEquivalent(SetMultimap<? extends K, ? extends V> setMultimap,
//      EqualityComparator<Object> cmp) {
//    return false;
//  }
//
//  @Override
//  public boolean complementEquivalent(SetMultimap<? extends K, ? extends V> setMultimap,
//      EqualityComparator<Object> cmp) {
//    return false;
//  }
}
