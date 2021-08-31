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
import java.util.stream.Stream;

import io.usethesource.capsule.BinaryRelation;
import io.usethesource.capsule.Set;
import io.usethesource.capsule.SetMultimap;

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

  private static <K, V> BinaryRelation.Immutable<K, V> wireTuple(K key, V value,
      final BiFunction<K, V, ? extends SetMultimap.Immutable<K, V>> fwdMerger,
      final BiFunction<V, K, ? extends SetMultimap.Immutable<V, K>> bwdMerger) {

    return new PersistentBidirectionalTrieSetMultimap(fwdMerger.apply(key, value),
        bwdMerger.apply(value, key));
  }

  private static <K, V> BinaryRelation.Immutable<K, V> batchWireTuple(K key,
      Set.Immutable<V> values,
      final BiFunction<K, Set.Immutable<V>, ? extends SetMultimap.Immutable<K, V>> fwdMerger,
      final BiFunction<Set.Immutable<V>, K, ? extends SetMultimap.Immutable<V, K>> bwdMerger) {

    return new PersistentBidirectionalTrieSetMultimap(fwdMerger.apply(key, values),
        bwdMerger.apply(values, key));
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
    throw new UnsupportedOperationException("Map semantic unavailable @ Bidirectional Multi-Map.");
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

  @Override
  public SetMultimap.Immutable<K, V> __remove(K key) {
    throw new UnsupportedOperationException("Map semantic unavailable @ Bidirectional Multi-Map.");
  }

  @Override
  public SetMultimap.Immutable<K, V> __remove(K key, V value) {
    return wireTuple(key, value, fwd::__remove, bwd::__remove);
  }

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
  public boolean __insert(K key, V value) {
    return wireTransientTuple(key, value, fwd::__insert, bwd::__insert);
  }

  @Override
  public boolean __remove(K key, V value) {
    return wireTransientTuple(key, value, fwd::__remove, bwd::__remove);
  }

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
}
