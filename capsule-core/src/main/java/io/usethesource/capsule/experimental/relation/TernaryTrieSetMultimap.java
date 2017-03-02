/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.capsule.experimental.relation;

import java.util.Iterator;
import java.util.function.BiFunction;

import io.usethesource.capsule.DefaultTrieSetMultimap;
import io.usethesource.capsule.api.Set;
import io.usethesource.capsule.api.SetMultimap;
import io.usethesource.capsule.api.TernaryRelation;
import io.usethesource.capsule.api.Triple;
import io.usethesource.capsule.util.collection.AbstractImmutableSet;

public class TernaryTrieSetMultimap<T, U, V, R extends Triple<T, U, V>>
    extends AbstractImmutableSet<R> implements TernaryRelation.Immutable<T, U, V, R> {

  private final SetMultimap.Immutable<T, R> indexT;
  private final SetMultimap.Immutable<U, R> indexU;
  private final SetMultimap.Immutable<V, R> indexV;

  public TernaryTrieSetMultimap(final SetMultimap.Immutable<T, R> indexT,
      final SetMultimap.Immutable<U, R> indexU, final SetMultimap.Immutable<V, R> indexV) {
    this.indexT = indexT;
    this.indexU = indexU;
    this.indexV = indexV;
  }

  public static final <T, U, V, R extends Triple<T, U, V>> TernaryRelation.Immutable<T, U, V, R> of() {
    /*
     * NOTE: uses default multi-map to create nested forward and backward maps.
     *
     * TODO: make classes of nested multi-maps configurable.
     */
    return new TernaryTrieSetMultimap<>(DefaultTrieSetMultimap.of(), DefaultTrieSetMultimap.of(),
        DefaultTrieSetMultimap.of());
  }

  // @SuppressWarnings("unchecked")
  // public static final <T, U, V, R extends Triple<T, U, V>> TernaryRelation.Transient<T, U, V, R>
  // transientOf() {
  // /*
  // * NOTE: uses default multi-map to create nested forward and backward maps.
  // *
  // * TODO: make classes of nested multi-maps configurable.
  // */
  // return new TransientTernaryTrieSetMultimap<>(DefaultTrieSetMultimap.transientOf(),
  // DefaultTrieSetMultimap.transientOf(), DefaultTrieSetMultimap.transientOf());
  // }

  private static <T, U, V, R extends Triple<T, U, V>> TernaryTrieSetMultimap<T, U, V, R> wireTuple(
      R triple, final BiFunction<T, R, ? extends SetMultimap.Immutable<T, R>> fstMerger,
      final BiFunction<U, R, ? extends SetMultimap.Immutable<U, R>> sndMerger,
      final BiFunction<V, R, ? extends SetMultimap.Immutable<V, R>> trdMerger) {

    return new TernaryTrieSetMultimap(fstMerger.apply(triple._0(), triple),
        sndMerger.apply(triple._1(), triple), trdMerger.apply(triple._2(), triple));
  }

  @Override
  public int size() {
    return indexT.size();
  }

  @Override
  public boolean isEmpty() {
    return indexT.isEmpty();
  }

  @Override
  public boolean contains(Object o) {
    try {
      final Triple<?, ?, ?> triple = (Triple) o;
      return indexT.containsEntry(triple._0(), triple);
    } catch (ClassCastException e) {
      return false;
    }
  }

  @Override
  public R get(Object o) {
    try {
      final Triple<?, ?, ?> triple = (Triple) o;
      return indexT.get(triple._0()).get(triple);
    } catch (ClassCastException e) {
      return null;
    }
  }

  @Override
  public Iterator<R> iterator() {
    return indexT.valueIterator();
  }

  @Override
  public Iterator<R> keyIterator() {
    return this.iterator();
  }

  @Override
  public TernaryTrieSetMultimap<T, U, V, R> __insert(R triple) {
    return wireTuple(triple, indexT::__insert, indexU::__insert, indexV::__insert);
  }

  @Override
  public TernaryTrieSetMultimap<T, U, V, R> __remove(R triple) {
    return wireTuple(triple, indexT::__remove, indexU::__remove, indexV::__remove);
  }

  private static final <A, B> B foldLeft(final B start, final Iterable<A> items,
      final BiFunction<B, A, B> merger) {
    B result = start;
    for (A item : items) {
      result = merger.apply(result, item);
    }
    return result;
  }

  @Override
  public TernaryTrieSetMultimap<T, U, V, R> __insertAll(java.util.Set<? extends R> set) {
    /*
     * TODO: apply foldLeft to multi-maps before wiring.
     */
    return foldLeft(this, set, (xs, y) -> xs.__insert(y));
  }

  @Override
  public Set.Immutable<R> __removeAll(java.util.Set<? extends R> set) {
    /*
     * TODO: apply foldLeft to multi-maps before wiring.
     */
    return foldLeft(this, set, (xs, y) -> xs.__remove(y));
  }

  @Override
  public Set.Immutable<R> __retainAll(java.util.Set<? extends R> set) {
    throw new IllegalStateException("Not yet implemented.");
  }

  @Override
  public TernaryRelation.Transient<T, U, V, R> asTransient() {
    throw new IllegalStateException("Not yet implemented.");
  }

}
