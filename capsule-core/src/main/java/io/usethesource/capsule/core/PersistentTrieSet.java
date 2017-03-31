/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.capsule.core;

import java.text.DecimalFormat;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import io.usethesource.capsule.Set;
import io.usethesource.capsule.core.trie.ArrayView;
import io.usethesource.capsule.core.trie.SetNode;
import io.usethesource.capsule.util.ArrayUtils;
import io.usethesource.capsule.util.EqualityComparator;

import static io.usethesource.capsule.core.trie.SetNode.Preference.INDIFFERENT;
import static io.usethesource.capsule.core.trie.SetNode.Preference.LEFT;
import static io.usethesource.capsule.core.trie.SetNode.Preference.RIGHT;
import static io.usethesource.capsule.util.BitmapUtils.isBitInBitmap;

public class PersistentTrieSet<K> implements Set.Immutable<K> {

  private static final PersistentTrieSet EMPTY_SET = new PersistentTrieSet(
      CompactSetNode.EMPTY_NODE, 0, 0);

  private static final boolean DEBUG = false;

  private final AbstractSetNode<K> rootNode;
  private final int cachedHashCode;
  private final int cachedSize;

  /*
   * TODO: visibility is currently public to allow set-multimap experiments. Must be set back to
   * `protected` when experiments are finished.
   */
  public /* protected */ PersistentTrieSet(AbstractSetNode<K> rootNode) {
    this.rootNode = rootNode;
//    this.cachedHashCode = rootNode.recursivePayloadHashCode();
//    this.cachedSize = rootNode.size();

    final Iterator<? extends AbstractSetNode<K>> it = nodeIterator();

    int size = 0;
    int hashCode = 0;
    while (it.hasNext()) {
      final AbstractSetNode<K> node = it.next();
      size += node.payloadArity();
      hashCode += node.recursivePayloadHashCode();
    }

    this.cachedHashCode = hashCode;
    this.cachedSize = size;
  }

  /*
   * TODO: visibility is currently public to allow set-multimap experiments. Must be set back to
   * `protected` when experiments are finished.
   */
  public /* protected */ PersistentTrieSet(AbstractSetNode<K> rootNode, int cachedHashCode,
      int cachedSize) {
    this.rootNode = rootNode;
    this.cachedHashCode = cachedHashCode;
    this.cachedSize = cachedSize;
    if (DEBUG) {
      assert checkHashCodeAndSize(cachedHashCode, cachedSize);
    }
  }

  public static final <K> Set.Immutable<K> of() {
    return PersistentTrieSet.EMPTY_SET;
  }

  public static final <K> Set.Immutable<K> of(K key0) {
    final int keyHash0 = key0.hashCode();

    final int nodeMap = 0;
    final int dataMap = CompactSetNode.bitpos(CompactSetNode.mask(keyHash0, 0));

    final CompactSetNode<K> newRootNode = CompactSetNode.nodeOf(null, nodeMap, dataMap, key0);

    return new PersistentTrieSet<K>(newRootNode, keyHash0, 1);
  }

  public static final <K> Set.Immutable<K> of(K key0, K key1) {
    assert !Objects.equals(key0, key1);

    final int keyHash0 = key0.hashCode();
    final int keyHash1 = key1.hashCode();

    CompactSetNode<K> newRootNode =
        CompactSetNode.mergeTwoKeyValPairs(key0, keyHash0, key1, keyHash1, 0);

    return new PersistentTrieSet<K>(newRootNode, keyHash0 + keyHash1, 2);
  }

  public static final <K> Set.Immutable<K> of(K... keys) {
    Set.Immutable<K> result = PersistentTrieSet.EMPTY_SET;

    for (final K key : keys) {
      result = result.__insert(key);
    }

    return result;
  }

  public static final <K> Set.Transient<K> transientOf() {
    return PersistentTrieSet.EMPTY_SET.asTransient();
  }

  public static final <K> Set.Transient<K> transientOf(K... keys) {
    final Set.Transient<K> result = PersistentTrieSet.EMPTY_SET.asTransient();

    for (final K key : keys) {
      result.__insert(key);
    }

    return result;
  }

  private static <K> int hashCode(AbstractSetNode<K> rootNode) {
    int hash = 0;

    for (Iterator<K> it = new SetKeyIterator<>(rootNode); it.hasNext(); ) {
      hash += it.next().hashCode();
    }

    return hash;
  }

  private static <K> int size(AbstractSetNode<K> rootNode) {
    int size = 0;

    for (Iterator<K> it = new SetKeyIterator<>(rootNode); it.hasNext(); it.next()) {
      size += 1;
    }

    return size;
  }

  private boolean checkHashCodeAndSize(final int targetHash, final int targetSize) {
    int hash = 0;
    int size = 0;

    for (Iterator<K> it = keyIterator(); it.hasNext(); ) {
      final K key = it.next();

      hash += key.hashCode();
      size += 1;
    }

    return hash == targetHash && size == targetSize;
  }

  public static final int transformHashCode(final int hash) {
    return hash;
  }

  @Override
  public boolean contains(final Object o) {
    try {
      final K key = (K) o;
      return rootNode.contains(key, transformHashCode(key.hashCode()), 0);
    } catch (ClassCastException unused) {
      return false;
    }
  }

  @Override
  public boolean containsEquivalent(final Object o, final Comparator<Object> cmp) {
    try {
      final K key = (K) o;
      return rootNode.contains(key, transformHashCode(key.hashCode()), 0, cmp);
    } catch (ClassCastException unused) {
      return false;
    }
  }

  @Override
  public K get(final Object o) {
    try {
      final K key = (K) o;
      final Optional<K> result = rootNode.findByKey(key, transformHashCode(key.hashCode()), 0);

      if (result.isPresent()) {
        return result.get();
      } else {
        return null;
      }
    } catch (ClassCastException unused) {
      return null;
    }
  }

  @Override
  public K getEquivalent(final Object o, final Comparator<Object> cmp) {
    try {
      final K key = (K) o;
      final Optional<K> result = rootNode.findByKey(key, transformHashCode(key.hashCode()), 0, cmp);

      if (result.isPresent()) {
        return result.get();
      } else {
        return null;
      }
    } catch (ClassCastException unused) {
      return null;
    }
  }

  @Override
  public Set.Immutable<K> __insert(final K key) {
    final int keyHash = key.hashCode();
    final SetResult<K> details = SetResult.unchanged();

    final CompactSetNode<K> newRootNode =
        rootNode.updated(null, key, transformHashCode(keyHash), 0, details);

    if (details.isModified()) {
      return new PersistentTrieSet<K>(newRootNode, cachedHashCode + keyHash, cachedSize + 1);
    }

    return this;
  }

  @Override
  public Set.Immutable<K> __insertEquivalent(final K key, final Comparator<Object> cmp) {
    final int keyHash = key.hashCode();
    final SetResult<K> details = SetResult.unchanged();

    final CompactSetNode<K> newRootNode =
        rootNode.updated(null, key, transformHashCode(keyHash), 0, details, cmp);

    if (details.isModified()) {
      return new PersistentTrieSet<K>(newRootNode, cachedHashCode + keyHash, cachedSize + 1);
    }

    return this;
  }

  @Override
  public Set.Immutable<K> __insertAll(final java.util.Set<? extends K> set) {
    final Set.Transient<K> tmpTransient = this.asTransient();
    tmpTransient.__insertAll(set);
    return tmpTransient.freeze();
  }

  @Override
  public Set.Immutable<K> __insertAllEquivalent(final java.util.Set<? extends K> set,
      final Comparator<Object> cmp) {
    final Set.Transient<K> tmpTransient = this.asTransient();
    tmpTransient.__insertAllEquivalent(set, cmp);
    return tmpTransient.freeze();
  }

  @Override
  public Set.Immutable<K> __remove(final K key) {
    final int keyHash = key.hashCode();
    final SetResult<K> details = SetResult.unchanged();

    final CompactSetNode<K> newRootNode =
        rootNode.removed(null, key, transformHashCode(keyHash), 0, details);

    if (details.isModified()) {
      return new PersistentTrieSet<K>(newRootNode, cachedHashCode - keyHash, cachedSize - 1);
    }

    return this;
  }

  @Override
  public Set.Immutable<K> __removeEquivalent(final K key, final Comparator<Object> cmp) {
    final int keyHash = key.hashCode();
    final SetResult<K> details = SetResult.unchanged();

    final CompactSetNode<K> newRootNode =
        rootNode.removed(null, key, transformHashCode(keyHash), 0, details, cmp);

    if (details.isModified()) {
      return new PersistentTrieSet<K>(newRootNode, cachedHashCode - keyHash, cachedSize - 1);
    }

    return this;
  }

  @Override
  public Set.Immutable<K> __removeAll(final java.util.Set<? extends K> set) {
    final Set.Transient<K> tmpTransient = this.asTransient();
    tmpTransient.__removeAll(set);
    return tmpTransient.freeze();
  }

  @Override
  public Set.Immutable<K> __removeAllEquivalent(final java.util.Set<? extends K> set,
      final Comparator<Object> cmp) {
    final Set.Transient<K> tmpTransient = this.asTransient();
    tmpTransient.__removeAllEquivalent(set, cmp);
    return tmpTransient.freeze();
  }

  @Override
  public Set.Immutable<K> __retainAll(final java.util.Set<? extends K> set) {
    final Set.Transient<K> tmpTransient = this.asTransient();
    tmpTransient.__retainAll(set);
    return tmpTransient.freeze();
  }

  @Override
  public Set.Immutable<K> __retainAllEquivalent(final Set.Transient<? extends K> transientSet,
      final Comparator<Object> cmp) {
    final Set.Transient<K> tmpTransient = this.asTransient();
    tmpTransient.__retainAllEquivalent(transientSet, cmp);
    return tmpTransient.freeze();
  }

  @Override
  public Set.Immutable<K> union(final Set.Immutable<K> other) {

    if (!(other instanceof PersistentTrieSet)) {
      return Set.Immutable.union(this, other);
    }

    if (this == other) {
      return this;
    }

    if (other == null) {
      return this;
    }

    if (this == EMPTY_SET || this.isEmpty()) {
      return other;
    }
    if (other == EMPTY_SET || other.isEmpty()) {
      return this;
    }

    final PersistentTrieSet<K> set1 = this;
    final PersistentTrieSet<K> set2 = (PersistentTrieSet<K>) other;

    final PersistentTrieSet<K> smaller;
    final PersistentTrieSet<K> bigger;

    final PersistentTrieSet<K> unmodified;

    if (set2.size() >= set1.size()) {
      unmodified = set2;
      smaller = set1;
      bigger = set2;
    } else {
      unmodified = set1;
      smaller = set2;
      bigger = set1;
    }

    final SetNode.IntersectionResult details = new SetNode.IntersectionResult();

    final AbstractSetNode<K> newRootNode = bigger.rootNode.union(null, smaller.rootNode, 0,
        details, EqualityComparator.EQUALS.toComparator(), INDIFFERENT);

    assert unmodified.cachedHashCode != details.getAccumulatedHashCode()
        || unmodified.rootNode == newRootNode || null == newRootNode;

    if (newRootNode == unmodified.rootNode || newRootNode == null) {
      return unmodified;
//      } else if (details.getAccumulatedSize() == 0) {
//        return Set.Immutable.of();
    } else {
      return new PersistentTrieSet(newRootNode,
          unmodified.cachedHashCode + details.getAccumulatedHashCode(),
          unmodified.cachedSize + details.getAccumulatedSize());
    }
  }

  @Override
  public Set.Immutable<K> subtract(final Set.Immutable<K> other) {

    if (!(other instanceof PersistentTrieSet)) {
      return Set.Immutable.subtract(this, other);
    }

    if (this == other) {
      return Set.Immutable.of();
    }

    if (other == null) {
      return this;
    }

    if (this == EMPTY_SET || this.isEmpty()) {
      return this;
    }

    if (other == EMPTY_SET || other.isEmpty()) {
      return this;
    }

    final PersistentTrieSet<K> set1 = this;
    final PersistentTrieSet<K> set2 = (PersistentTrieSet<K>) other;

    final PersistentTrieSet<K> unmodified = set1;

    final SetNode.IntersectionResult details = new SetNode.IntersectionResult();

    final AbstractSetNode<K> newRootNode = set1.rootNode.subtract(null, set2.rootNode, 0,
        details, EqualityComparator.EQUALS.toComparator(), INDIFFERENT);

    assert unmodified.cachedHashCode != details.getAccumulatedHashCode()
        || unmodified.rootNode == newRootNode || null == newRootNode;

    if (newRootNode == unmodified.rootNode || newRootNode == null) {
      return unmodified;
    } else if (details.getAccumulatedSize() == 0) {
      return Set.Immutable.of();
    } else {
      return new PersistentTrieSet(newRootNode,
          details.getAccumulatedHashCode(),
          details.getAccumulatedSize());
    }
  }

  @Override
  public Set.Immutable<K> intersect(final Set.Immutable<K> other) {

    if (!(other instanceof PersistentTrieSet)) {
      return Set.Immutable.intersect(this, other);
    }

    if (this == other) {
      return this;
    }

    if (other == null) {
      return Set.Immutable.of();
    }

    if (this == EMPTY_SET || this.isEmpty()) {
      return this; // return Set.Immutable.of();
    }

    if (other == EMPTY_SET || other.isEmpty()) {
      return other; // return Set.Immutable.of();
    }

    final PersistentTrieSet<K> set1 = this;
    final PersistentTrieSet<K> set2 = (PersistentTrieSet<K>) other;

    final PersistentTrieSet<K> smaller;
    final PersistentTrieSet<K> bigger;

    final PersistentTrieSet<K> unmodified;

    if (set2.size() >= set1.size()) {
      unmodified = set1;
      smaller = set1;
      bigger = set2;
    } else {
      unmodified = set2;
      smaller = set2;
      bigger = set1;
    }

    final SetNode.IntersectionResult details = new SetNode.IntersectionResult();

    final AbstractSetNode<K> newRootNode = smaller.rootNode.intersect(null, bigger.rootNode, 0,
        details, EqualityComparator.EQUALS.toComparator(), INDIFFERENT);

    assert unmodified.cachedHashCode != details.getAccumulatedHashCode()
        || unmodified.rootNode == newRootNode || null == newRootNode;

    if (newRootNode == unmodified.rootNode || newRootNode == null) {
      return unmodified;
    } else if (details.getAccumulatedSize() == 0) {
      return Set.Immutable.of();
    } else {
      return new PersistentTrieSet(newRootNode,
          details.getAccumulatedHashCode(),
          details.getAccumulatedSize());
    }
  }

  @Override
  public boolean add(final K key) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean addAll(final Collection<? extends K> c) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void clear() {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean remove(final Object key) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean removeAll(final Collection<?> c) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean retainAll(final Collection<?> c) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean containsAll(final Collection<?> c) {
    for (Object item : c) {
      if (!contains(item)) {
        return false;
      }
    }
    return true;
  }

  @Override
  public boolean containsAllEquivalent(final Collection<?> c, final Comparator<Object> cmp) {
    for (Object item : c) {
      if (!containsEquivalent(item, cmp)) {
        return false;
      }
    }
    return true;
  }

  @Override
  public int size() {
    return cachedSize;
  }

  @Override
  public boolean isEmpty() {
    return cachedSize == 0;
  }

  @Override
  public Iterator<K> iterator() {
    return keyIterator();
  }

  @Override
  public Iterator<K> keyIterator() {
    return new SetKeyIterator<>(rootNode);
  }

  @Override
  public Object[] toArray() {
    Object[] array = new Object[cachedSize];

    int idx = 0;
    for (K key : this) {
      array[idx++] = key;
    }

    return array;
  }

  @Override
  public <T> T[] toArray(final T[] a) {
    List<K> list = new ArrayList<K>(cachedSize);

    for (K key : this) {
      list.add(key);
    }

    return list.toArray(a);
  }

  @Override
  public boolean equals(final Object other) {
    if (other == this) {
      return true;
    }
    if (other == null) {
      return false;
    }

    if (other instanceof PersistentTrieSet) {
      PersistentTrieSet<?> that = (PersistentTrieSet<?>) other;

      if (this.cachedSize != that.cachedSize) {
        return false;
      }

      if (this.cachedHashCode != that.cachedHashCode) {
        return false;
      }

      return rootNode.equals(that.rootNode);
    } else if (other instanceof java.util.Set) {
      java.util.Set that = (java.util.Set) other;

      if (this.size() != that.size()) {
        return false;
      }

      return containsAll(that);
    }

    return false;
  }

  @Override
  public int hashCode() {
    return cachedHashCode;
  }

  @Override
  public String toString() {
    String body = stream().map(k -> k.toString()).reduce((o1, o2) -> String.join(", ", o1, o2))
        .orElse("");
    return String.format("{%s}", body);
  }

  @Override
  public boolean isTransientSupported() {
    return true;
  }

  @Override
  public Set.Transient<K> asTransient() {
    return new TransientTrieSet<K>(this);
  }

  /*
   * For analysis purposes only.
   *
   * TODO: visibility is currently public to allow set-multimap experiments. Must be set back to
   * `protected` when experiments are finished.
   */
  public /* protected */ AbstractSetNode<K> getRootNode() {
    return rootNode;
  }

  /*
   * For analysis purposes only.
   */
  protected Iterator<AbstractSetNode<K>> nodeIterator() {
    return new TrieSetNodeIterator<>(rootNode);
  }

  /*
   * For analysis purposes only.
   */

  protected int getNodeCount() {
    final Iterator<AbstractSetNode<K>> it = nodeIterator();
    int sumNodes = 0;

    for (; it.hasNext(); it.next()) {
      sumNodes += 1;
    }

    return sumNodes;
  }

  /*
   * For analysis purposes only. Payload X Node
   */
  protected int[][] arityCombinationsHistogram() {
    final Iterator<AbstractSetNode<K>> it = nodeIterator();
    final int[][] sumArityCombinations = new int[33][33];

    while (it.hasNext()) {
      final AbstractSetNode<K> node = it.next();
      sumArityCombinations[node.payloadArity()][node.nodeArity()] += 1;
    }

    return sumArityCombinations;
  }

  /*
   * For analysis purposes only.
   */
  protected int[] arityHistogram() {
    final int[][] sumArityCombinations = arityCombinationsHistogram();
    final int[] sumArity = new int[33];

    final int maxArity = 32; // TODO: factor out constant

    for (int j = 0; j <= maxArity; j++) {
      for (int maxRestArity = maxArity - j, k = 0; k <= maxRestArity - j; k++) {
        sumArity[j + k] += sumArityCombinations[j][k];
      }
    }

    return sumArity;
  }

  /*
   * For analysis purposes only.
   */
  public void printStatistics() {
    final int[][] sumArityCombinations = arityCombinationsHistogram();
    final int[] sumArity = arityHistogram();
    final int sumNodes = getNodeCount();

    final int[] cumsumArity = new int[33];
    for (int cumsum = 0, i = 0; i < 33; i++) {
      cumsum += sumArity[i];
      cumsumArity[i] = cumsum;
    }

    final float threshhold = 0.01f; // for printing results
    for (int i = 0; i < 33; i++) {
      float arityPercentage = (float) (sumArity[i]) / sumNodes;
      float cumsumArityPercentage = (float) (cumsumArity[i]) / sumNodes;

      if (arityPercentage != 0 && arityPercentage >= threshhold) {
        // details per level
        StringBuilder bldr = new StringBuilder();
        int max = i;
        for (int j = 0; j <= max; j++) {
          for (int k = max - j; k <= max - j; k++) {
            float arityCombinationsPercentage = (float) (sumArityCombinations[j][k]) / sumNodes;

            if (arityCombinationsPercentage != 0 && arityCombinationsPercentage >= threshhold) {
              bldr.append(String.format("%d/%d: %s, ", j, k,
                  new DecimalFormat("0.00%").format(arityCombinationsPercentage)));
            }
          }
        }
        final String detailPercentages = bldr.toString();

        // overview
        System.out.println(String.format("%2d: %s\t[cumsum = %s]\t%s", i,
            new DecimalFormat("0.00%").format(arityPercentage),
            new DecimalFormat("0.00%").format(cumsumArityPercentage), detailPercentages));
      }
    }
  }

  /*
   * TODO: visibility is currently public to allow set-multimap experiments. Must be set back to
   * `protected` when experiments are finished.
   */
  public static final class SetResult<K> {

    private K replacedValue;
    private boolean isModified;
    private boolean isReplaced;

    // update: inserted/removed single element, element count changed
    public void modified() {
      this.isModified = true;
    }

    public void updated(K replacedValue) {
      this.replacedValue = replacedValue;
      this.isModified = true;
      this.isReplaced = true;
    }

    // update: neither element, nor element count changed
    public static <K> SetResult<K> unchanged() {
      return new SetResult<>();
    }

    private SetResult() {
    }

    public boolean isModified() {
      return isModified;
    }

    public boolean hasReplacedValue() {
      return isReplaced;
    }

    public K getReplacedValue() {
      return replacedValue;
    }
  }

  /*
   * TODO: visibility is currently public to allow set-multimap experiments. Must be set back to
   * `protected` when experiments are finished.
   */
  public /* protected */ static abstract class AbstractSetNode<K> implements
      SetNode<K, AbstractSetNode<K>>, Iterable<K> {

    static final int TUPLE_LENGTH = 1;

    // factory method to construct trie from outer classes
    // TODO: find alternative solution that does not violate information hiding
    public static <K> AbstractSetNode<K> newHashCollisonNode(final int hash, K... keys) {
      return new HashCollisionSetNode<>(hash, keys);
    }

    // factory method to construct trie from outer classes
    // TODO: find alternative solution that does not violate information hiding
    public static <K> AbstractSetNode<K> newBitmapIndexedNode(final AtomicReference<Thread> mutator,
        final int nodeMap, final int dataMap, final Object[] content) {
      // content is assumed to be effectivle immutable to avoid defensive copying
      return new BitmapIndexedSetNode<>(mutator, nodeMap, dataMap, content);
    }

    /*
     * TODO: visibility is currently public to allow set-multimap experiments. Must be set back to
     * `protected` when experiments are finished.
     */
    public abstract boolean contains(final K key, final int keyHash, final int shift);

    abstract boolean contains(final K key, final int keyHash, final int shift,
        final Comparator<Object> cmp);

    abstract Optional<K> findByKey(final K key, final int keyHash, final int shift);

    abstract Optional<K> findByKey(final K key, final int keyHash, final int shift,
        final Comparator<Object> cmp);

    /*
     * TODO: visibility is currently public to allow set-multimap experiments. Must be set back to
     * `protected` when experiments are finished.
     */
    public /* protected */ abstract CompactSetNode<K> updated(final AtomicReference<Thread> mutator,
        final K key, final int keyHash, final int shift, final SetResult<K> details);

    abstract CompactSetNode<K> updated(final AtomicReference<Thread> mutator, final K key,
        final int keyHash, final int shift, final SetResult<K> details,
        final Comparator<Object> cmp);

    /*
     * TODO: visibility is currently public to allow set-multimap experiments. Must be set back to
     * `protected` when experiments are finished.
     */
    public /* protected */ abstract CompactSetNode<K> removed(final AtomicReference<Thread> mutator,
        final K key, final int keyHash, final int shift, final SetResult<K> details);

    abstract CompactSetNode<K> removed(final AtomicReference<Thread> mutator, final K key,
        final int keyHash, final int shift, final SetResult<K> details,
        final Comparator<Object> cmp);

    // static final <T> boolean isAllowedToEdit(AtomicReference<T> x, AtomicReference<T> y) {
    // return x != null && y != null && (x == y || x.get() == y.get());
    // }

    static final <T> boolean isAllowedToEdit(AtomicReference<?> x, AtomicReference<?> y) {
      return x != null && y != null && (x == y || x.get() == y.get());
    }

    /*
     * TODO: in future move to SetNode interface
     */
    public Optional<K> findFirst() {
      final ArrayView<K> elementArray = dataArray(0, 0);

      if (elementArray.isEmpty()) {
        return Optional.empty();
      } else {
        return Optional.of(elementArray.get(0));
      }
    }

    @Override
    public <T> ArrayView<T> dataArray(final int category, final int component) {
      if (category == 0 && component == 0) {
        return categoryArrayView0();
      } else {
        throw new IllegalArgumentException("Category %i is not supported.");
      }
    }

    private <T> ArrayView<T> categoryArrayView0() {
      return new ArrayView<T>() {
        @Override
        public int size() {
          return payloadArity();
        }

        @Override
        public T get(int index) {
          return (T) getKey(index);
        }
      };
    }

    @Override
    public abstract ArrayView<AbstractSetNode<K>> nodeArray();

    abstract boolean hasNodes();

    abstract int nodeArity();

    abstract AbstractSetNode<K> getNode(final int index);

//    /*
//     * TODO: visibility is currently public to allow set-multimap experiments. Must be set back to
//     * `protected` when experiments are finished.
//     */
//    public /* protected */ abstract void setNode(final AtomicReference<Thread> mutator,
//        final int index, final AbstractSetNode<K> node);

    @Deprecated
    Iterator<? extends AbstractSetNode<K>> nodeIterator() {
      return new Iterator<AbstractSetNode<K>>() {

        int nextIndex = 0;
        final int nodeArity = AbstractSetNode.this.nodeArity();

        @Override
        public void remove() {
          throw new UnsupportedOperationException();
        }

        @Override
        public AbstractSetNode<K> next() {
          if (!hasNext()) {
            throw new NoSuchElementException();
          }
          return AbstractSetNode.this.getNode(nextIndex++);
        }

        @Override
        public boolean hasNext() {
          return nextIndex < nodeArity;
        }
      };
    }

//    abstract boolean hasPayload();
//
//    abstract int payloadArity();
//
//    abstract K getKey(final int index);

    @Deprecated
    abstract boolean hasSlots();

    abstract int slotArity();

    abstract Object getSlot(final int index);

    /**
     * The arity of this trie node (i.e. number of values and nodes stored on this level).
     *
     * @return sum of nodes and values stored within
     */

    int arity() {
      return payloadArity() + nodeArity();
    }

    /*
     * TODO: visibility is currently public to allow set-multimap experiments. Must be set back to
     * `protected` when experiments are finished.
     */
    public /* protected */ final int size() {
//      final Iterator<K> it = new SetKeyIterator<>(this);
//
//      int size = 0;
//      while (it.hasNext()) {
//        size += 1;
//        it.next();
//      }
//
//      return size;

      final Iterator<? extends AbstractSetNode<K>> it = new TrieSetNodeIterator(this);

      int size = 0;
      while (it.hasNext()) {
        final AbstractSetNode<K> node = it.next();
        size += node.payloadArity();
      }

      return size;
    }

    abstract int localPayloadHashCode();

    final int recursivePayloadHashCode() {
      final Iterator<? extends AbstractSetNode<K>> it = new TrieSetNodeIterator(this);

      int hashCode = 0;
      while (it.hasNext()) {
        final AbstractSetNode<K> node = it.next();
        hashCode += node.localPayloadHashCode();
      }

      return hashCode;
    }

    @Override
    public Iterator<K> iterator() {
      return new SetKeyIterator<>(this);
    }

    @Override
    public Spliterator<K> spliterator() {
      return Spliterators.spliteratorUnknownSize(iterator(), Spliterator.DISTINCT);
    }

    public Stream<K> stream() {
      return StreamSupport.stream(spliterator(), false);
    }

  }

  protected static abstract class CompactSetNode<K> extends AbstractSetNode<K> {

    static final int HASH_CODE_LENGTH = 32;

    static final int BIT_PARTITION_SIZE = 5;
    static final int BIT_PARTITION_MASK = 0b11111;

    static final int mask(final int keyHash, final int shift) {
      return (keyHash >>> shift) & BIT_PARTITION_MASK;
    }

    static final int bitpos(final int mask) {
      return 1 << mask;
    }

    abstract int nodeMap();

    abstract int dataMap();

    @Override
    abstract CompactSetNode<K> getNode(final int index);

    boolean nodeInvariant() {
      boolean inv1 = (size() - payloadArity() >= 2 * (arity() - payloadArity()));
      boolean inv2 = (this.arity() == 0) ? sizePredicate() == SIZE_EMPTY : true;
      boolean inv3 =
          (this.arity() == 1 && payloadArity() == 1) ? sizePredicate() == SIZE_ONE : true;
      boolean inv4 = (this.arity() >= 2) ? sizePredicate() == SIZE_MORE_THAN_ONE : true;

      boolean inv5 = (this.nodeArity() >= 0) && (this.payloadArity() >= 0)
          && ((this.payloadArity() + this.nodeArity()) == this.arity());

      return inv1 && inv2 && inv3 && inv4 && inv5;
    }

    abstract CompactSetNode<K> copyAndInsertValue(final AtomicReference<Thread> mutator,
        final int bitpos, final K key);

    abstract CompactSetNode<K> copyAndRemoveValue(final AtomicReference<Thread> mutator,
        final int bitpos);

    abstract CompactSetNode<K> copyAndSetNode(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetNode<K> node);

    abstract CompactSetNode<K> copyAndMigrateFromInlineToNode(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetNode<K> node);

    abstract CompactSetNode<K> copyAndMigrateFromNodeToInline(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetNode<K> node);

    static final <K> CompactSetNode<K> mergeTwoKeyValPairs(final K key0, final int keyHash0,
        final K key1, final int keyHash1, final int shift) {
      assert !(key0.equals(key1));

      if (shift >= HASH_CODE_LENGTH) {
        // throw new
        // IllegalStateException("Hash collision not yet fixed.");
        return new HashCollisionSetNode<>(keyHash0, (K[]) new Object[]{key0, key1});
      }

      final int mask0 = mask(keyHash0, shift);
      final int mask1 = mask(keyHash1, shift);

      if (mask0 != mask1) {
        // both nodes fit on same level
        final int dataMap = bitpos(mask0) | bitpos(mask1);

        if (mask0 < mask1) {
          return nodeOf(null, (0), dataMap, new Object[]{key0, key1});
        } else {
          return nodeOf(null, (0), dataMap, new Object[]{key1, key0});
        }
      } else {
        final CompactSetNode<K> node =
            mergeTwoKeyValPairs(key0, keyHash0, key1, keyHash1, shift + BIT_PARTITION_SIZE);
        // values fit on next level

        final int nodeMap = bitpos(mask0);
        return nodeOf(null, nodeMap, (0), new Object[]{node});
      }
    }

    static final CompactSetNode EMPTY_NODE;

    static {
      EMPTY_NODE = new BitmapIndexedSetNode<>(null, (0), (0), new Object[]{});
    }

    static final <K> CompactSetNode<K> nodeOf(final AtomicReference<Thread> mutator,
        final int nodeMap, final int dataMap, final Object[] nodes) {
      return new BitmapIndexedSetNode<>(mutator, nodeMap, dataMap, nodes);
    }

    static final <K> CompactSetNode<K> nodeOf(AtomicReference<Thread> mutator) {
      return EMPTY_NODE;
    }

    static final <K> CompactSetNode<K> nodeOf(AtomicReference<Thread> mutator, final int nodeMap,
        final int dataMap, final K key) {
      assert nodeMap == 0;
      return nodeOf(mutator, (0), dataMap, new Object[]{key});
    }

    static final int index(final int bitmap, final int bitpos) {
      return java.lang.Integer.bitCount(bitmap & (bitpos - 1));
    }

    static final int index(final int bitmap, final int mask, final int bitpos) {
      return (bitmap == -1) ? mask : index(bitmap, bitpos);
    }

    int dataIndex(final int bitpos) {
      return java.lang.Integer.bitCount(dataMap() & (bitpos - 1));
    }

    int nodeIndex(final int bitpos) {
      return java.lang.Integer.bitCount(nodeMap() & (bitpos - 1));
    }

    CompactSetNode<K> nodeAt(final int bitpos) {
      return getNode(nodeIndex(bitpos));
    }

    /*
     * TODO: visibility is currently public to allow set-multimap experiments. Must be set back to
     * `protected` when experiments are finished.
     */
    @Override
    public /* protected */ boolean contains(final K key, final int keyHash, final int shift) {
      final int mask = mask(keyHash, shift);
      final int bitpos = bitpos(mask);

      final int dataMap = dataMap();
      if ((dataMap & bitpos) != 0) {
        final int index = index(dataMap, mask, bitpos);
        return getKey(index).equals(key);
      }

      final int nodeMap = nodeMap();
      if ((nodeMap & bitpos) != 0) {
        final int index = index(nodeMap, mask, bitpos);
        return getNode(index).contains(key, keyHash, shift + BIT_PARTITION_SIZE);
      }

      return false;
    }

    @Override
    boolean contains(final K key, final int keyHash, final int shift,
        final Comparator<Object> cmp) {
      final int mask = mask(keyHash, shift);
      final int bitpos = bitpos(mask);

      final int dataMap = dataMap();
      if ((dataMap & bitpos) != 0) {
        final int index = index(dataMap, mask, bitpos);
        return cmp.compare(getKey(index), key) == 0;
      }

      final int nodeMap = nodeMap();
      if ((nodeMap & bitpos) != 0) {
        final int index = index(nodeMap, mask, bitpos);
        return getNode(index).contains(key, keyHash, shift + BIT_PARTITION_SIZE, cmp);
      }

      return false;
    }

    @Override
    Optional<K> findByKey(final K key, final int keyHash, final int shift) {
      final int mask = mask(keyHash, shift);
      final int bitpos = bitpos(mask);

      if ((dataMap() & bitpos) != 0) { // inplace value
        final int index = dataIndex(bitpos);
        if (getKey(index).equals(key)) {
          return Optional.of(getKey(index));
        }

        return Optional.empty();
      }

      if ((nodeMap() & bitpos) != 0) { // node (not value)
        final AbstractSetNode<K> subNode = nodeAt(bitpos);

        return subNode.findByKey(key, keyHash, shift + BIT_PARTITION_SIZE);
      }

      return Optional.empty();
    }

    @Override
    Optional<K> findByKey(final K key, final int keyHash, final int shift,
        final Comparator<Object> cmp) {
      final int mask = mask(keyHash, shift);
      final int bitpos = bitpos(mask);

      if ((dataMap() & bitpos) != 0) { // inplace value
        final int index = dataIndex(bitpos);
        if (cmp.compare(getKey(index), key) == 0) {
          return Optional.of(getKey(index));
        }

        return Optional.empty();
      }

      if ((nodeMap() & bitpos) != 0) { // node (not value)
        final AbstractSetNode<K> subNode = nodeAt(bitpos);

        return subNode.findByKey(key, keyHash, shift + BIT_PARTITION_SIZE, cmp);
      }

      return Optional.empty();
    }

    /*
     * TODO: visibility is currently public to allow set-multimap experiments. Must be set back to
     * `protected` when experiments are finished.
     */
    @Override
    public /* protected */ CompactSetNode<K> updated(final AtomicReference<Thread> mutator,
        final K key, final int keyHash, final int shift, final SetResult<K> details) {
      final int mask = mask(keyHash, shift);
      final int bitpos = bitpos(mask);

      if ((dataMap() & bitpos) != 0) { // inplace value
        final int dataIndex = dataIndex(bitpos);
        final K currentKey = getKey(dataIndex);

        if (currentKey.equals(key)) {
          return this;
        } else {
          final CompactSetNode<K> subNodeNew = mergeTwoKeyValPairs(currentKey,
              transformHashCode(currentKey.hashCode()), key, keyHash, shift + BIT_PARTITION_SIZE);

          details.modified();
          return copyAndMigrateFromInlineToNode(mutator, bitpos, subNodeNew);
        }
      } else if ((nodeMap() & bitpos) != 0) { // node (not value)
        final CompactSetNode<K> subNode = nodeAt(bitpos);
        final CompactSetNode<K> subNodeNew =
            subNode.updated(mutator, key, keyHash, shift + BIT_PARTITION_SIZE, details);

        if (details.isModified()) {
          return copyAndSetNode(mutator, bitpos, subNodeNew);
        } else {
          return this;
        }
      } else {
        // no value
        details.modified();
        return copyAndInsertValue(mutator, bitpos, key);
      }
    }

    @Override
    CompactSetNode<K> updated(final AtomicReference<Thread> mutator, final K key, final int keyHash,
        final int shift, final SetResult<K> details, final Comparator<Object> cmp) {
      final int mask = mask(keyHash, shift);
      final int bitpos = bitpos(mask);

      if ((dataMap() & bitpos) != 0) { // inplace value
        final int dataIndex = dataIndex(bitpos);
        final K currentKey = getKey(dataIndex);

        if (cmp.compare(currentKey, key) == 0) {
          return this;
        } else {
          final CompactSetNode<K> subNodeNew = mergeTwoKeyValPairs(currentKey,
              transformHashCode(currentKey.hashCode()), key, keyHash, shift + BIT_PARTITION_SIZE);

          details.modified();
          return copyAndMigrateFromInlineToNode(mutator, bitpos, subNodeNew);
        }
      } else if ((nodeMap() & bitpos) != 0) { // node (not value)
        final CompactSetNode<K> subNode = nodeAt(bitpos);
        final CompactSetNode<K> subNodeNew =
            subNode.updated(mutator, key, keyHash, shift + BIT_PARTITION_SIZE, details, cmp);

        if (details.isModified()) {
          return copyAndSetNode(mutator, bitpos, subNodeNew);
        } else {
          return this;
        }
      } else {
        // no value
        details.modified();
        return copyAndInsertValue(mutator, bitpos, key);
      }
    }

    /*
     * TODO: visibility is currently public to allow set-multimap experiments. Must be set back to
     * `protected` when experiments are finished.
     */
    @Override
    public /* protected */ CompactSetNode<K> removed(final AtomicReference<Thread> mutator,
        final K key, final int keyHash, final int shift, final SetResult<K> details) {
      final int mask = mask(keyHash, shift);
      final int bitpos = bitpos(mask);

      if ((dataMap() & bitpos) != 0) { // inplace value
        final int dataIndex = dataIndex(bitpos);

        if (getKey(dataIndex).equals(key)) {
          details.modified();

          if (this.payloadArity() == 2 && this.nodeArity() == 0) {
            /*
             * Create new node with remaining pair. The new node will a) either become the new root
             * returned, or b) unwrapped and inlined during returning.
             */
            final int newDataMap =
                (shift == 0) ? (int) (dataMap() ^ bitpos) : bitpos(mask(keyHash, 0));

            if (dataIndex == 0) {
              return CompactSetNode.<K>nodeOf(mutator, 0, newDataMap, getKey(1));
            } else {
              return CompactSetNode.<K>nodeOf(mutator, 0, newDataMap, getKey(0));
            }
          } else {
            return copyAndRemoveValue(mutator, bitpos);
          }
        } else {
          return this;
        }
      } else if ((nodeMap() & bitpos) != 0) { // node (not value)
        final CompactSetNode<K> subNode = nodeAt(bitpos);
        final CompactSetNode<K> subNodeNew =
            subNode.removed(mutator, key, keyHash, shift + BIT_PARTITION_SIZE, details);

        if (!details.isModified()) {
          return this;
        }

        switch (subNodeNew.sizePredicate()) {
          case 0: {
            throw new IllegalStateException("Sub-node must have at least one element.");
          }
          case 1: {
            if (this.payloadArity() == 0 && this.nodeArity() == 1) {
              // escalate (singleton or empty) result
              return subNodeNew;
            } else {
              // inline value (move to front)
              return copyAndMigrateFromNodeToInline(mutator, bitpos, subNodeNew);
            }
          }
          default: {
            // modify current node (set replacement node)
            return copyAndSetNode(mutator, bitpos, subNodeNew);
          }
        }
      }

      return this;
    }

    @Override
    CompactSetNode<K> removed(final AtomicReference<Thread> mutator, final K key, final int keyHash,
        final int shift, final SetResult<K> details, final Comparator<Object> cmp) {
      final int mask = mask(keyHash, shift);
      final int bitpos = bitpos(mask);

      if ((dataMap() & bitpos) != 0) { // inplace value
        final int dataIndex = dataIndex(bitpos);

        if (cmp.compare(getKey(dataIndex), key) == 0) {
          details.modified();

          if (this.payloadArity() == 2 && this.nodeArity() == 0) {
            /*
             * Create new node with remaining pair. The new node will a) either become the new root
             * returned, or b) unwrapped and inlined during returning.
             */
            final int newDataMap =
                (shift == 0) ? (int) (dataMap() ^ bitpos) : bitpos(mask(keyHash, 0));

            if (dataIndex == 0) {
              return CompactSetNode.<K>nodeOf(mutator, 0, newDataMap, getKey(1));
            } else {
              return CompactSetNode.<K>nodeOf(mutator, 0, newDataMap, getKey(0));
            }
          } else {
            return copyAndRemoveValue(mutator, bitpos);
          }
        } else {
          return this;
        }
      } else if ((nodeMap() & bitpos) != 0) { // node (not value)
        final CompactSetNode<K> subNode = nodeAt(bitpos);
        final CompactSetNode<K> subNodeNew =
            subNode.removed(mutator, key, keyHash, shift + BIT_PARTITION_SIZE, details, cmp);

        if (!details.isModified()) {
          return this;
        }

        switch (subNodeNew.sizePredicate()) {
          case 0: {
            throw new IllegalStateException("Sub-node must have at least one element.");
          }
          case 1: {
            if (this.payloadArity() == 0 && this.nodeArity() == 1) {
              // escalate (singleton or empty) result
              return subNodeNew;
            } else {
              // inline value (move to front)
              return copyAndMigrateFromNodeToInline(mutator, bitpos, subNodeNew);
            }
          }
          default: {
            // modify current node (set replacement node)
            return copyAndSetNode(mutator, bitpos, subNodeNew);
          }
        }
      }

      return this;
    }

    /**
     * @return 0 <= mask <= 2^BIT_PARTITION_SIZE - 1
     */
    static byte recoverMask(int map, byte i_th) {
      assert 1 <= i_th && i_th <= 32;

      byte cnt1 = 0;
      byte mask = 0;

      while (mask < 32) {
        if ((map & 0x01) == 0x01) {
          cnt1 += 1;

          if (cnt1 == i_th) {
            return mask;
          }
        }

        map = map >> 1;
        mask += 1;
      }

      assert cnt1 != i_th;
      throw new RuntimeException("Called with invalid arguments.");
    }

    @Override
    public String toString() {
      final StringBuilder bldr = new StringBuilder();
      bldr.append('[');

      for (byte i = 0; i < payloadArity(); i++) {
        final byte pos = recoverMask(dataMap(), (byte) (i + 1));
        bldr.append(String.format("@%d<#%d>", pos, Objects.hashCode(getKey(i))));

        if (!((i + 1) == payloadArity())) {
          bldr.append(", ");
        }
      }

      if (payloadArity() > 0 && nodeArity() > 0) {
        bldr.append(", ");
      }

      for (byte i = 0; i < nodeArity(); i++) {
        final byte pos = recoverMask(nodeMap(), (byte) (i + 1));
        bldr.append(String.format("@%d: %s", pos, getNode(i)));

        if (!((i + 1) == nodeArity())) {
          bldr.append(", ");
        }
      }

      bldr.append(']');
      return bldr.toString();
    }

  }

  protected static abstract class CompactMixedSetNode<K> extends CompactSetNode<K> {

    private final int nodeMap;
    private final int dataMap;

    CompactMixedSetNode(final AtomicReference<Thread> mutator, final int nodeMap,
        final int dataMap) {
      this.nodeMap = nodeMap;
      this.dataMap = dataMap;
    }

    @Override
    final int nodeMap() {
      return nodeMap;
    }

    @Override
    final int dataMap() {
      return dataMap;
    }

  }

  private static final class BitmapIndexedSetNode<K> extends CompactMixedSetNode<K> {

    final AtomicReference<Thread> mutator;
    final Object[] nodes;

    private BitmapIndexedSetNode(final AtomicReference<Thread> mutator, final int nodeMap,
        final int dataMap, final Object[] nodes) {
      super(mutator, nodeMap, dataMap);

      this.mutator = mutator;
      this.nodes = nodes;

      if (DEBUG) {
        assert (TUPLE_LENGTH * java.lang.Integer.bitCount(dataMap)
            + java.lang.Integer.bitCount(nodeMap) == nodes.length);

        for (int i = 0; i < TUPLE_LENGTH * payloadArity(); i++) {
          assert ((nodes[i] instanceof CompactSetNode) == false);
        }
        for (int i = TUPLE_LENGTH * payloadArity(); i < nodes.length; i++) {
          assert ((nodes[i] instanceof CompactSetNode) == true);
        }

        assert nodeInvariant();
      }
    }

    @Override
    public ArrayView<AbstractSetNode<K>> nodeArray() {
      return new ArrayView<AbstractSetNode<K>>() {
        @Override
        public int size() {
          return BitmapIndexedSetNode.this.nodeArity();
        }

        @Override
        public AbstractSetNode<K> get(int index) {
          return BitmapIndexedSetNode.this.getNode(index);
        }

        /**
         * TODO: replace with {{@link #set(int, AbstractSetNode, AtomicReference)}}
         */
        @Override
        public void set(int index, AbstractSetNode<K> item) {
          // if (!isAllowedToEdit(BitmapIndexedSetNode.this.mutator, writeCapabilityToken)) {
          // throw new IllegalStateException();
          // }

          nodes[nodes.length - 1 - index] = item;
        }

        @Override
        public void set(int index, AbstractSetNode<K> item,
            AtomicReference<?> writeCapabilityToken) {
          if (!isAllowedToEdit(BitmapIndexedSetNode.this.mutator, writeCapabilityToken)) {
            throw new IllegalStateException();
          }

          nodes[nodes.length - 1 - index] = item;
        }
      };
    }

    @Override
    public K getKey(final int index) {
      return (K) nodes[TUPLE_LENGTH * index];
    }

    @Override
    public int getKeyHash(int index) {
      return getKey(index).hashCode();
    }

    @Override
    CompactSetNode<K> getNode(final int index) {
      return (CompactSetNode<K>) nodes[nodes.length - 1 - index];
    }

//    @Override
//    public void setNode(final AtomicReference<Thread> mutator, final int index,
//        final AbstractSetNode<K> node) {
//      if (isAllowedToEdit(this.mutator, mutator)) {
//        nodes[nodes.length - 1 - index] = node;
//      } else {
//        throw new IllegalStateException();
//      }
//    }

    @Override
    public boolean hasPayload() {
      return dataMap() != 0;
    }

    @Override
    public int payloadArity() {
      return java.lang.Integer.bitCount(dataMap());
    }

    @Override
    boolean hasNodes() {
      return nodeMap() != 0;
    }

    @Override
    int nodeArity() {
      return java.lang.Integer.bitCount(nodeMap());
    }

    @Override
    Object getSlot(final int index) {
      return nodes[index];
    }

    @Override
    boolean hasSlots() {
      return nodes.length != 0;
    }

    @Override
    int slotArity() {
      return nodes.length;
    }

    @Override
    int localPayloadHashCode() {
      final Stream<K> keyStream =
          StreamSupport.stream(this.<K>dataArray(0, 0).spliterator(), false);
      return keyStream.mapToInt(Object::hashCode).sum();
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 0;
      result = prime * result + (nodeMap());
      result = prime * result + (dataMap());
      result = prime * result + Arrays.hashCode(nodes);
      return result;
    }

    @Override
    public boolean equals(final Object other) {
      if (null == other) {
        return false;
      }
      if (this == other) {
        return true;
      }
      if (getClass() != other.getClass()) {
        return false;
      }
      BitmapIndexedSetNode<?> that = (BitmapIndexedSetNode<?>) other;
      if (nodeMap() != that.nodeMap()) {
        return false;
      }
      if (dataMap() != that.dataMap()) {
        return false;
      }
      if (!ArrayUtils.equals(nodes, that.nodes)) {
        return false;
      }
      return true;
    }

    @Override
    public byte sizePredicate() {
      if (this.nodeArity() == 0) {
        switch (this.payloadArity()) {
          case 0:
            return SIZE_EMPTY;
          case 1:
            return SIZE_ONE;
          default:
            return SIZE_MORE_THAN_ONE;
        }
      } else {
        return SIZE_MORE_THAN_ONE;
      }
    }

    @Override
    CompactSetNode<K> copyAndSetNode(final AtomicReference<Thread> mutator, final int bitpos,
        final CompactSetNode<K> node) {

      final int idx = this.nodes.length - 1 - nodeIndex(bitpos);

      if (isAllowedToEdit(this.mutator, mutator)) {
        // no copying if already editable
        this.nodes[idx] = node;
        return this;
      } else {
        final Object[] src = this.nodes;
        final Object[] dst = new Object[src.length];

        // copy 'src' and set 1 element(s) at position 'idx'
        System.arraycopy(src, 0, dst, 0, src.length);
        dst[idx + 0] = node;

        return nodeOf(mutator, nodeMap(), dataMap(), dst);
      }
    }

    @Override
    CompactSetNode<K> copyAndInsertValue(final AtomicReference<Thread> mutator, final int bitpos,
        final K key) {
      final int idx = TUPLE_LENGTH * dataIndex(bitpos);

      final Object[] src = this.nodes;
      final Object[] dst = new Object[src.length + 1];

      // copy 'src' and insert 1 element(s) at position 'idx'
      System.arraycopy(src, 0, dst, 0, idx);
      dst[idx + 0] = key;
      System.arraycopy(src, idx, dst, idx + 1, src.length - idx);

      return nodeOf(mutator, nodeMap(), dataMap() | bitpos, dst);
    }

    @Override
    CompactSetNode<K> copyAndRemoveValue(final AtomicReference<Thread> mutator, final int bitpos) {
      final int idx = TUPLE_LENGTH * dataIndex(bitpos);

      final Object[] src = this.nodes;
      final Object[] dst = new Object[src.length - 1];

      // copy 'src' and remove 1 element(s) at position 'idx'
      System.arraycopy(src, 0, dst, 0, idx);
      System.arraycopy(src, idx + 1, dst, idx, src.length - idx - 1);

      return nodeOf(mutator, nodeMap(), dataMap() ^ bitpos, dst);
    }

    @Override
    CompactSetNode<K> copyAndMigrateFromInlineToNode(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetNode<K> node) {

      final int idxOld = TUPLE_LENGTH * dataIndex(bitpos);
      final int idxNew = this.nodes.length - TUPLE_LENGTH - nodeIndex(bitpos);

      final Object[] src = this.nodes;
      final Object[] dst = new Object[src.length - 1 + 1];

      // copy 'src' and remove 1 element(s) at position 'idxOld' and
      // insert 1 element(s) at position 'idxNew' (TODO: carefully test)
      assert idxOld <= idxNew;
      System.arraycopy(src, 0, dst, 0, idxOld);
      System.arraycopy(src, idxOld + 1, dst, idxOld, idxNew - idxOld);
      dst[idxNew + 0] = node;
      System.arraycopy(src, idxNew + 1, dst, idxNew + 1, src.length - idxNew - 1);

      return nodeOf(mutator, nodeMap() | bitpos, dataMap() ^ bitpos, dst);
    }

    @Override
    CompactSetNode<K> copyAndMigrateFromNodeToInline(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetNode<K> node) {

      final int idxOld = this.nodes.length - 1 - nodeIndex(bitpos);
      final int idxNew = TUPLE_LENGTH * dataIndex(bitpos);

      final Object[] src = this.nodes;
      final Object[] dst = new Object[src.length - 1 + 1];

      // copy 'src' and remove 1 element(s) at position 'idxOld' and
      // insert 1 element(s) at position 'idxNew' (TODO: carefully test)
      assert idxOld >= idxNew;
      System.arraycopy(src, 0, dst, 0, idxNew);
      dst[idxNew + 0] = node.getKey(0);
      System.arraycopy(src, idxNew, dst, idxNew + 1, idxOld - idxNew);
      System.arraycopy(src, idxOld + 1, dst, idxOld + 1, src.length - idxOld - 1);

      return nodeOf(mutator, nodeMap() ^ bitpos, dataMap() | bitpos, dst);
    }

    private final int bitPattern(int dataMap, int nodeMap, int bitpos) {
      boolean isInDataMap = isBitInBitmap(dataMap, bitpos);
      boolean isInNodeMap = isBitInBitmap(nodeMap, bitpos);

      int bitPattern = 0;

      if (isInDataMap) {
        bitPattern = bitPattern + 2;
      }

      if (isInNodeMap) {
        bitPattern = bitPattern + 1;
      }

      return bitPattern;
    }

//    // @formatter:off
//    public final static int PATTERN_EMPTY           = 0b00;
//    public final static int PATTERN_NODE            = 0b01;
//    public final static int PATTERN_DATA_SINGLETON  = 0b10;
//    public final static int PATTERN_DATA_COLLECTION = 0b11;
//    // @formatter:on
//
//    // @formatter:off
//    public final static int PATTERN_EMPTY_AND_EMPTY = 0b0000;
//
//    public final static int PATTERN_EMPTY_AND_NODE  = 0b0001;
//    public final static int PATTERN_EMPTY_AND_DATA_SINGLETON  = 0b0010;
//    public final static int PATTERN_EMPTY_AND_DATA_COLLECTION = 0b0011;
//
//    public final static int PATTERN_NODE_AND_EMPTY  = 0b0100;
//    public final static int PATTERN_DATA_SINGLETON_AND_EMPTY  = 0b1000;
//    public final static int PATTERN_DATA_COLLECTION_AND_EMPTY = 0b1100;
//
//    public final static int PATTERN_DATA_SINGLETON_AND_DATA_SINGLETON  = 0b1010;
//    public final static int PATTERN_DATA_SINGLETON_AND_NODE = 0b1001;
//    public final static int PATTERN_NODE_AND_DATA_SINGLETON = 0b0110;
//
//    public final static int PATTERN_DATA_SINGLETON_AND_DATA_COLLECTION = 0b1011;
//    public final static int PATTERN_DATA_COLLECTION_AND_DATA_SINGLETON = 0b1110;
//
//    public final static int PATTERN_DATA_COLLECTION_AND_DATA_COLLECTION  = 0b1111;
//    public final static int PATTERN_DATA_COLLECTION_AND_NODE = 0b1101;
//    public final static int PATTERN_NODE_AND_DATA_COLLECTION = 0b0111;
//
//    public final static int PATTERN_NODE_AND_NODE = 0b0101;
//    // @formatter:on

    // @formatter:off
    public final static int PATTERN_EMPTY           = 0b00;
    public final static int PATTERN_NODE            = 0b01;
    public final static int PATTERN_DATA            = 0b10;
    // @formatter:on

    // @formatter:off

    public final static int PATTERN_EMPTY_AND_NODE  = 0b0001;
    public final static int PATTERN_NODE_AND_EMPTY  = 0b0100;

    public final static int PATTERN_EMPTY_AND_DATA  = 0b0010;
    public final static int PATTERN_DATA_AND_EMPTY  = 0b1000;

    public final static int PATTERN_DATA_AND_NODE   = 0b1001;
    public final static int PATTERN_NODE_AND_DATA   = 0b0110;

    public final static int PATTERN_EMPTY_AND_EMPTY = 0b0000;
    public final static int PATTERN_DATA_AND_DATA   = 0b1010;
    public final static int PATTERN_NODE_AND_NODE   = 0b0101;
    // @formatter:on

    /*
     * TODO: for incrementality: only consider duplicate elements
     */
    @Override
    public final AbstractSetNode<K> union(AtomicReference<Thread> mutator,
        AbstractSetNode<K> that, int shift, IntersectionResult details,
        Comparator<Object> cmp, Preference directionPreference) {

      final CompactSetNode<K> node0 = this;
      final CompactSetNode<K> node1 = (CompactSetNode<K>) that;

      if (node0 == node1) {
        return node0;
      }

      final int dataMap0 = node0.dataMap();
      final int nodeMap0 = node0.nodeMap();
      final int dataMap1 = node1.dataMap();
      final int nodeMap1 = node1.nodeMap();

      final int bitmap0 = dataMap0 | nodeMap0;
      final int bitmap1 = dataMap1 | nodeMap1;

      int unionedBitmap = bitmap0 | bitmap1;

      final Buffer<K, AbstractSetNode<K>> newBuffer = new Buffer<>();
      int newDataMap = 0;
      int newNodeMap = 0;

      boolean isNodeMapReferenceEqual0 = true;

      int bitsToSkip = Integer.numberOfTrailingZeros(unionedBitmap);

      while (bitsToSkip < 32) {
        final int bitpos = bitpos(bitsToSkip);

        final int bitPattern0 = bitPattern(dataMap0, nodeMap0, bitpos);
        final int bitPattern1 = bitPattern(dataMap1, nodeMap1, bitpos);

        final int bitPattern = (bitPattern0 << 2) | bitPattern1;

        switch (bitPattern) {
          case PATTERN_DATA_AND_DATA: {
            // case singleton x singleton
            final int dataIndex0 = index(dataMap0, bitpos);
            final int dataIndex1 = index(dataMap1, bitpos);

            final K key0 = node0.getKey(dataIndex0);
            final int keyHash0 = node0.getKeyHash(dataIndex0);

            final K key1 = node1.getKey(dataIndex1);
            final int keyHash1 = node1.getKeyHash(dataIndex1);

//            if (keyHash0 == keyHash1 && cmp.compare(key0, key1) == 0) {
            if (keyHash0 == keyHash1 && Objects.equals(key0, key1)) {
              // singleton -> singleton
              newBuffer.add(key0, keyHash0);
              newDataMap |= bitpos;
            } else {
              // singleton -> node (=bitmap change);
              final AbstractSetNode<K> node =
                  mergeTwoKeyValPairs(key0, keyHash0, key1, keyHash1, shift + BIT_PARTITION_SIZE);

              newBuffer.add(node);
              newNodeMap |= bitpos;

              // delta
              details.addSize(1);
              details.addHashCode(keyHash1);
            }
            break;
          }

          case PATTERN_NODE_AND_DATA: {
            // case node x singleton
            final int nodeIndex0 = index(nodeMap0, bitpos);
            final int dataIndex1 = index(dataMap1, bitpos);

            final AbstractSetNode<K> node = node0.getNode(nodeIndex0);

            final K key = node1.getKey(dataIndex1);
            final int keyHash = node1.getKeyHash(dataIndex1);

            boolean nodeContainsKey = node.contains(key, keyHash, shift + BIT_PARTITION_SIZE, cmp);

            if (nodeContainsKey) {
              // node -> node (=bitmap change)
              newBuffer.add(node);
              newNodeMap |= bitpos;
            } else {
              // node -> node (=bitmap change)
              final AbstractSetNode<K> newNode = node
                  .updated(mutator, key, keyHash, shift + BIT_PARTITION_SIZE, SetResult.unchanged(),
                      cmp);

              newBuffer.add(newNode);
              newNodeMap |= bitpos;

              // delta
              details.addSize(1);
              details.addHashCode(keyHash);

              isNodeMapReferenceEqual0 = false;
            }
            break;
          }

          case PATTERN_DATA_AND_NODE: {
            // case singleton x node
            final int dataIndex0 = index(dataMap0, bitpos);
            final int nodeIndex1 = index(nodeMap1, bitpos);

            final K key = node0.getKey(dataIndex0);
            final int keyHash = node0.getKeyHash(dataIndex0);

            final AbstractSetNode<K> node = node1.getNode(nodeIndex1);

            boolean nodeContainsKey = node.contains(key, keyHash, shift + BIT_PARTITION_SIZE, cmp);

            if (nodeContainsKey) {
              // singleton -> node
              newBuffer.add(node);
              newNodeMap |= bitpos;

              // delta
              details.addSize(node.size() - 1);
              details.addHashCode(node.recursivePayloadHashCode() - keyHash);
            } else {
              // singleton -> node (=bitmap change)
              final AbstractSetNode<K> newNode = node
                  .updated(mutator, key, keyHash, shift + BIT_PARTITION_SIZE, SetResult.unchanged(),
                      cmp);

              newBuffer.add(newNode);
              newNodeMap |= bitpos;

              // delta
              details.addSize(node.size());
              details.addHashCode(node.recursivePayloadHashCode());
            }
            break;
          }

          case PATTERN_NODE_AND_NODE: {
            // case node x node
            final int nodeIndex0 = index(nodeMap0, bitpos);
            final int nodeIndex1 = index(nodeMap1, bitpos);

            final AbstractSetNode<K> subNode0 = node0.getNode(nodeIndex0);
            final AbstractSetNode<K> subNode1 = node1.getNode(nodeIndex1);

            final AbstractSetNode<K> newNode = subNode0.union(mutator, subNode1,
                shift + BIT_PARTITION_SIZE, details, cmp, directionPreference);

            assert newNode.sizePredicate() == SIZE_MORE_THAN_ONE;

            // node -> node
            newBuffer.add(newNode);
            newNodeMap |= bitpos;

            if (newNode != subNode0) {
              isNodeMapReferenceEqual0 = false;
            }
            break;
          }

          case PATTERN_EMPTY_AND_DATA: {
            // case empty x singleton
            final int dataIndex1 = index(dataMap1, bitpos);

            final K key1 = node1.getKey(dataIndex1);
            final int keyHash1 = node1.getKeyHash(dataIndex1);

            newBuffer.add(key1, keyHash1);
            newDataMap |= bitpos;

            // delta
            details.addSize(1);
            details.addHashCode(keyHash1);
            break;
          }

          case PATTERN_DATA_AND_EMPTY: {
            // case singleton x empty
            final int dataIndex0 = index(dataMap0, bitpos);

            final K key0 = node0.getKey(dataIndex0);
            final int keyHash0 = node0.getKeyHash(dataIndex0);

            newBuffer.add(key0, keyHash0);
            newDataMap |= bitpos;
            break;
          }

          case PATTERN_EMPTY_AND_NODE: {
            // case empty x node
            final int nodeIndex1 = index(nodeMap1, bitpos);
            final AbstractSetNode<K> subNode1 = node1.getNode(nodeIndex1);

            // TODO: tackle incremental recalculation of hash + size
            newBuffer.add(subNode1);
            newNodeMap |= bitpos;

            // delta
            details.addSize(subNode1.size());
            details.addHashCode(subNode1.recursivePayloadHashCode());
            break;
          }

          case PATTERN_NODE_AND_EMPTY: {
            // case node x empty
            final int nodeIndex0 = index(nodeMap0, bitpos);
            final AbstractSetNode<K> subNode0 = node0.getNode(nodeIndex0);

            // TODO: tackle incremental recalculation of hash + size
            newBuffer.add(subNode0);
            newNodeMap |= bitpos;
            break;
          }
        }

        int trailingZeroCount = Integer
            .numberOfTrailingZeros(unionedBitmap >> (bitsToSkip + 1));
        bitsToSkip = bitsToSkip + 1 + trailingZeroCount;
      }

      boolean leftReferenceEqual =
          isNodeMapReferenceEqual0 && ((newDataMap == dataMap0) && (newNodeMap == nodeMap0));

      // TODO: introduce preferLeftOverRightOnEquality
      if (newBuffer.isEmpty()) {
        return EMPTY_NODE;
      }

      if (leftReferenceEqual) {
        assert node0.equals(new BitmapIndexedSetNode<K>(mutator, newNodeMap,
            newDataMap, newBuffer.compactBuffer()));
        return node0;
      }

      // TODO: create singelton node that can unboxed easily
      final CompactSetNode<K> newNode = new BitmapIndexedSetNode<K>(mutator, newNodeMap,
          newDataMap, newBuffer.compactBuffer()); // newBuffer.compactHashes()

      assert !newNode.equals(node0);
      assert !newNode.equals(node1);

      return newNode;
    }

    /*
     * TODO: for incrementality: only consider intersecting elements
     */
    @Override
    public final AbstractSetNode<K> intersect(AtomicReference<Thread> mutator,
        AbstractSetNode<K> that, int shift, IntersectionResult details,
        Comparator<Object> cmp, Preference directionPreference) {

      final CompactSetNode<K> node0 = this;
      final CompactSetNode<K> node1 = (CompactSetNode<K>) that;

      if (node0 == node1) {
        return node0;
      }

      final int dataMap0 = node0.dataMap();
      final int nodeMap0 = node0.nodeMap();
      final int dataMap1 = node1.dataMap();
      final int nodeMap1 = node1.nodeMap();

      final int bitmap0 = dataMap0 | nodeMap0;
      final int bitmap1 = dataMap1 | nodeMap1;

      int intersectedBitmap = bitmap0 & bitmap1;

      if (intersectedBitmap == 0) {
        return EMPTY_NODE;
      }

      final Buffer<K, AbstractSetNode<K>> newBuffer = new Buffer<>();
      int newDataMap = 0;
      int newNodeMap = 0;

      final boolean isDataMapIdentical0 = (dataMap0 & dataMap1) == dataMap0;
      final boolean isDataMapIdentical1 = (dataMap0 & dataMap1) == dataMap1;

      final boolean isNodeMapIdentical0 = (nodeMap0 & nodeMap1) == nodeMap0;
      final boolean isNodeMapIdentical1 = (nodeMap0 & nodeMap1) == nodeMap1;

      final boolean areBitmapsIdentical0 = isDataMapIdentical0 && isNodeMapIdentical0;
      final boolean areBitmapsIdentical1 = isDataMapIdentical1 && isNodeMapIdentical1;

      boolean isNodeMapReferenceEqual0 = true;
      boolean isNodeMapReferenceEqual1 = true;

      int bitsToSkip = Integer.numberOfTrailingZeros(intersectedBitmap);

      while (bitsToSkip < 32) {
        final int bitpos = bitpos(bitsToSkip);

        if (isBitInBitmap(dataMap0, bitpos)) {
          if (isBitInBitmap(dataMap1, bitpos)) {
            // case singleton x singleton
            final int dataIndex0 = index(dataMap0, bitpos);
            final int dataIndex1 = index(dataMap1, bitpos);

            final K key0 = node0.getKey(dataIndex0);
            final int keyHash0 = node0.getKeyHash(dataIndex0);

            final K key1 = node1.getKey(dataIndex1);
            final int keyHash1 = node1.getKeyHash(dataIndex1);

//            if (keyHash0 == keyHash1 && cmp.compare(key0, key1) == 0) {
            if (keyHash0 == keyHash1 && Objects.equals(key0, key1)) {
              // singleton -> singleton
              newBuffer.add(key0, keyHash0);
              newDataMap |= bitpos;
            } else {
              // singleton -> none (=bitmap change);
            }
          } else {
            // case singleton x node
            final int dataIndex0 = index(dataMap0, bitpos);
            final int nodeIndex1 = index(nodeMap1, bitpos);

            final K key = node0.getKey(dataIndex0);
            final int keyHash = node0.getKeyHash(dataIndex0);

            final AbstractSetNode<K> node = node1.getNode(nodeIndex1);

            boolean nodeContainsKey = node
                .contains(key, keyHash, shift + BIT_PARTITION_SIZE, cmp);

            if (nodeContainsKey) {
              // singleton -> singleton
              newBuffer.add(key, keyHash);
              newDataMap |= bitpos;
            } else {
              // singleton -> none (=bitmap change)
            }
          }
        } else {
          if (isBitInBitmap(dataMap1, bitpos)) {
            // case node x singleton
            final int nodeIndex0 = index(nodeMap0, bitpos);
            final int dataIndex1 = index(dataMap1, bitpos);

            final AbstractSetNode<K> node = node0.getNode(nodeIndex0);

            final K key = node1.getKey(dataIndex1);
            final int keyHash = node1.getKeyHash(dataIndex1);

            boolean nodeContainsKey = node
                .contains(key, keyHash, shift + BIT_PARTITION_SIZE, cmp);

            if (nodeContainsKey) {
              // node -> singleton (=bitmap change)
              newBuffer.add(key, keyHash);
              newDataMap |= bitpos;
            } else {
              // node -> none (=bitmap change)
            }
          } else {
            // case node x node
            final int nodeIndex0 = index(nodeMap0, bitpos);
            final int nodeIndex1 = index(nodeMap1, bitpos);

            final AbstractSetNode<K> subNode0 = node0.getNode(nodeIndex0);
            final AbstractSetNode<K> subNode1 = node1.getNode(nodeIndex1);

            final Preference recursiveDirectionPreference;
            if (areBitmapsIdentical0 && areBitmapsIdentical1) {
              recursiveDirectionPreference = directionPreference;
            } else if (areBitmapsIdentical0) {
              recursiveDirectionPreference = LEFT;
            } else if (areBitmapsIdentical1) {
              recursiveDirectionPreference = RIGHT;
            } else {
              recursiveDirectionPreference = INDIFFERENT;
            }

//            if (isNodeMapIdentical0 && !isNodeMapIdentical1) {
//              recursiveDirectionPreference = LEFT;
//            } else if (!isNodeMapIdentical0 && isNodeMapIdentical1) {
//              recursiveDirectionPreference = Preference.RIGHT;
//            } else {
//              recursiveDirectionPreference = INDIFFERENT;
//            }

            AbstractSetNode<K> newNode = subNode0.intersect(mutator, subNode1,
                shift + BIT_PARTITION_SIZE, details, cmp, recursiveDirectionPreference);

            switch (newNode == null ? SIZE_MORE_THAN_ONE : newNode.sizePredicate()) {
              case SIZE_EMPTY: {
                // node -> none (=bitmap change)
                break;
              }

              case SIZE_ONE: {
                // node -> singleton (=bitmap change)
                newBuffer.inline(newNode);
                newDataMap |= bitpos;
                break;
              }

              case SIZE_MORE_THAN_ONE: {
                // node -> node
//                newBuffer.add(newNode);
//                newNodeMap |= bitpos;

                if (newNode != null) {
                  newBuffer.add(newNode);
                  newNodeMap |= bitpos;

                  if (newNode != subNode0) {
                    isNodeMapReferenceEqual0 = false;
                  }

                  if (newNode != subNode1) {
                    isNodeMapReferenceEqual1 = false;
                  }
                } else {
                  if (directionPreference == LEFT || directionPreference == INDIFFERENT) {
                    newBuffer.add(subNode0);
                    newNodeMap |= bitpos;
                  } else {
                    newBuffer.add(subNode1);
                    newNodeMap |= bitpos;
                  }
                }

                break;
              }
            }
          }
        }

        int trailingZeroCount = Integer
            .numberOfTrailingZeros(intersectedBitmap >> (bitsToSkip + 1));
        bitsToSkip = bitsToSkip + 1 + trailingZeroCount;
      }

      // updated accumulated properties
      details.addSize(newBuffer.getPayloadSize());
      details.addHashCode(newBuffer.getPayloadHash());

      boolean leftReferenceEqual =
          isNodeMapReferenceEqual0 && ((newDataMap == dataMap0) && (newNodeMap == nodeMap0));

      boolean rightReferenceEqual =
          isNodeMapReferenceEqual1 && ((newDataMap == dataMap1) && (newNodeMap == nodeMap1));

      if (leftReferenceEqual && rightReferenceEqual) {
        return null;
      }

      // TODO: introduce preferLeftOverRightOnEquality
      if (newBuffer.isEmpty()) {
        return EMPTY_NODE;
      }

      if (directionPreference == LEFT || directionPreference == INDIFFERENT) {
        if (leftReferenceEqual) {
          assert node0.equals(new BitmapIndexedSetNode<K>(mutator, newNodeMap,
              newDataMap, newBuffer.compactBuffer()));
          return node0;
        } else if (rightReferenceEqual) {
          assert node1.equals(new BitmapIndexedSetNode<K>(mutator, newNodeMap,
              newDataMap, newBuffer.compactBuffer()));
          return node1;
        }
      } else {
        if (rightReferenceEqual) {
          assert node1.equals(new BitmapIndexedSetNode<K>(mutator, newNodeMap,
              newDataMap, newBuffer.compactBuffer()));
          return node1;
        } else if (leftReferenceEqual) {
          assert node0.equals(new BitmapIndexedSetNode<K>(mutator, newNodeMap,
              newDataMap, newBuffer.compactBuffer()));
          return node0;
        }
      }

      // TODO: create singelton node that can unboxed easily
      final CompactSetNode<K> newNode = new BitmapIndexedSetNode<K>(mutator, newNodeMap,
          newDataMap, newBuffer.compactBuffer()); // newBuffer.compactHashes()

      assert !newNode.equals(node0);
      assert !newNode.equals(node1);

      return newNode;
    }

    /*
     * TODO: for incrementality: only consider matching elements
     */
    @Override
    public final AbstractSetNode<K> subtract(AtomicReference<Thread> mutator,
        AbstractSetNode<K> that, int shift, IntersectionResult details,
        Comparator<Object> cmp, Preference directionPreference) {

      final CompactSetNode<K> node0 = this;
      final CompactSetNode<K> node1 = (CompactSetNode<K>) that;

      if (node0 == node1) {
        return EMPTY_NODE;
      }

      final int dataMap0 = node0.dataMap();
      final int nodeMap0 = node0.nodeMap();
      final int dataMap1 = node1.dataMap();
      final int nodeMap1 = node1.nodeMap();

      final int bitmap0 = dataMap0 | nodeMap0;
      final int bitmap1 = dataMap1 | nodeMap1;

      int intersectedBitmap = bitmap0 & bitmap1;

      if (intersectedBitmap == 0) {
        // updated accumulated properties
        details.addSize(node0.size());
        details.addHashCode(node0.recursivePayloadHashCode());

        return node0; // nothing to subtract
      }

      int unionedBitmap = bitmap0 | bitmap1;

      final Buffer<K, AbstractSetNode<K>> newBuffer = new Buffer<>();
      int newDataMap = 0;
      int newNodeMap = 0;

      boolean isNodeMapReferenceEqual0 = true;

      int bitsToSkip = Integer.numberOfTrailingZeros(unionedBitmap);

      while (bitsToSkip < 32) {
        final int bitpos = bitpos(bitsToSkip);

        final int bitPattern0 = bitPattern(dataMap0, nodeMap0, bitpos);
        final int bitPattern1 = bitPattern(dataMap1, nodeMap1, bitpos);

        final int bitPattern = (bitPattern0 << 2) | bitPattern1;

        switch (bitPattern) {
          case PATTERN_DATA_AND_DATA: {
            // case singleton x singleton
            final int dataIndex0 = index(dataMap0, bitpos);
            final int dataIndex1 = index(dataMap1, bitpos);

            final K key0 = node0.getKey(dataIndex0);
            final int keyHash0 = node0.getKeyHash(dataIndex0);

            final K key1 = node1.getKey(dataIndex1);
            final int keyHash1 = node1.getKeyHash(dataIndex1);

//            if (keyHash0 == keyHash1 && cmp.compare(key0, key1) == 0) {
            if (keyHash0 == keyHash1 && Objects.equals(key0, key1)) {
              // singleton -> none
            } else {
              // singleton -> singleton
              newBuffer.add(key0, keyHash0);
              newDataMap |= bitpos;
            }
            break;
          }

          case PATTERN_NODE_AND_DATA: {
            // case node x singleton
            final int nodeIndex0 = index(nodeMap0, bitpos);
            final int dataIndex1 = index(dataMap1, bitpos);

            final AbstractSetNode<K> node = node0.getNode(nodeIndex0);

            final K key = node1.getKey(dataIndex1);
            final int keyHash = node1.getKeyHash(dataIndex1);

            boolean nodeContainsKey = node
                .contains(key, keyHash, shift + BIT_PARTITION_SIZE, cmp);

            if (nodeContainsKey) {
              // node -> node (=bitmap change)
              final AbstractSetNode<K> newNode = node
                  .removed(mutator, key, keyHash, shift + BIT_PARTITION_SIZE,
                      SetResult.unchanged(),
                      cmp);

              switch (newNode.sizePredicate()) {
                case SIZE_ONE: {
                  // node -> singleton (=bitmap change)
                  newBuffer.inline(newNode);
                  newDataMap |= bitpos;

                  newBuffer.addPayloadSize(newNode.size());
                  newBuffer.addPayloadHash(newNode.recursivePayloadHashCode());
                  break;
                }

                case SIZE_MORE_THAN_ONE: {
                  // node -> node
                  newBuffer.add(newNode);
                  newNodeMap |= bitpos;

                  newBuffer.addPayloadSize(newNode.size());
                  newBuffer.addPayloadHash(newNode.recursivePayloadHashCode());
                  break;
                }
              }

              isNodeMapReferenceEqual0 = false; // move up to SIZE_MORE_THAN_ONE case?
            } else {
              // node -> node
              newBuffer.add(node);
              newNodeMap |= bitpos;

              newBuffer.addPayloadSize(node.size());
              newBuffer.addPayloadHash(node.recursivePayloadHashCode());
            }
            break;
          }

          case PATTERN_DATA_AND_NODE: {
            // case singleton x node
            final int dataIndex0 = index(dataMap0, bitpos);
            final int nodeIndex1 = index(nodeMap1, bitpos);

            final K key = node0.getKey(dataIndex0);
            final int keyHash = node0.getKeyHash(dataIndex0);

            final AbstractSetNode<K> node = node1.getNode(nodeIndex1);

            boolean nodeContainsKey = node
                .contains(key, keyHash, shift + BIT_PARTITION_SIZE, cmp);

            if (nodeContainsKey) {
              // singleton -> none
            } else {
              // singleton -> singleton
              newBuffer.add(key, keyHash);
              newDataMap |= bitpos;
            }
            break;
          }

          case PATTERN_NODE_AND_NODE: {
            // case node x node
            final int nodeIndex0 = index(nodeMap0, bitpos);
            final int nodeIndex1 = index(nodeMap1, bitpos);

            final AbstractSetNode<K> subNode0 = node0.getNode(nodeIndex0);
            final AbstractSetNode<K> subNode1 = node1.getNode(nodeIndex1);

            final AbstractSetNode<K> newNode = subNode0.subtract(mutator, subNode1,
                shift + BIT_PARTITION_SIZE, details, cmp, directionPreference);

            switch (newNode.sizePredicate()) {
              case SIZE_EMPTY: {
                // node -> none (=bitmap change)
                break;
              }

              case SIZE_ONE: {
                // node -> singleton (=bitmap change)
                newBuffer.inline(newNode);
                newDataMap |= bitpos;
                break;
              }

              case SIZE_MORE_THAN_ONE: {
                // node -> node
                newBuffer.add(newNode);
                newNodeMap |= bitpos;

                if (newNode != subNode0) {
                  isNodeMapReferenceEqual0 = false;
                }

                break;
              }
            }
            break;
          }

          case PATTERN_EMPTY_AND_DATA: {
            // case empty x singleton
            break;
          }

          case PATTERN_DATA_AND_EMPTY: {
            // case singleton x empty
            final int dataIndex0 = index(dataMap0, bitpos);

            final K key0 = node0.getKey(dataIndex0);
            final int keyHash0 = node0.getKeyHash(dataIndex0);

            newBuffer.add(key0, keyHash0);
            newDataMap |= bitpos;
            break;
          }

          case PATTERN_EMPTY_AND_NODE: {
            // case empty x node
            break;
          }

          case PATTERN_NODE_AND_EMPTY: {
            // case node x empty
            final int nodeIndex0 = index(nodeMap0, bitpos);
            final AbstractSetNode<K> subNode0 = node0.getNode(nodeIndex0);

            // TODO: tackle incremental recalculation of hash + size
            newBuffer.add(subNode0);
            newNodeMap |= bitpos;

            newBuffer.addPayloadSize(subNode0.size());
            newBuffer.addPayloadHash(subNode0.recursivePayloadHashCode());
            break;
          }
        }

        int trailingZeroCount = Integer
            .numberOfTrailingZeros(unionedBitmap >> (bitsToSkip + 1));
        bitsToSkip = bitsToSkip + 1 + trailingZeroCount;
      }

      // updated accumulated properties
      details.addSize(newBuffer.getPayloadSize());
      details.addHashCode(newBuffer.getPayloadHash());

      boolean leftReferenceEqual =
          isNodeMapReferenceEqual0 && ((newDataMap == dataMap0) && (newNodeMap == nodeMap0));

      if (newBuffer.isEmpty()) {
        return EMPTY_NODE;
      }

      if (leftReferenceEqual) {
        final CompactSetNode<K> newNode = new BitmapIndexedSetNode<K>(mutator, newNodeMap,
            newDataMap, newBuffer.compactBuffer()); // newBuffer.compactHashes()
        assert node0.equals(newNode);
        return node0;
      }

      // TODO: create singelton node that can unboxed easily
      final CompactSetNode<K> newNode = new BitmapIndexedSetNode<K>(mutator, newNodeMap,
          newDataMap, newBuffer.compactBuffer()); // newBuffer.compactHashes()

      assert !newNode.equals(node0);
      assert !newNode.equals(node1);

      return newNode;
    }

  }

  private static final class HashCollisionSetNode<K> extends CompactSetNode<K> {

    private final K[] keys;

    private final int hash;

    HashCollisionSetNode(final int hash, final K[] keys) {
      this.keys = keys;

      this.hash = hash;

      assert payloadArity() >= 2;
    }

    @Override
    public ArrayView<AbstractSetNode<K>> nodeArray() {
      return ArrayView.empty();
    }

    /*
     * TODO: visibility is currently public to allow set-multimap experiments. Must be set back to
     * `protected` when experiments are finished.
     */
    @Override
    public /* protected */ boolean contains(final K key, final int keyHash, final int shift) {
      if (this.hash == keyHash) {
        for (K k : keys) {
          if (k.equals(key)) {
            return true;
          }
        }
      }
      return false;
    }

    @Override
    boolean contains(final K key, final int keyHash, final int shift,
        final Comparator<Object> cmp) {
      if (this.hash == keyHash) {
        for (K k : keys) {
          if (cmp.compare(k, key) == 0) {
            return true;
          }
        }
      }
      return false;
    }

    @Override
    Optional<K> findByKey(final K key, final int keyHash, final int shift) {
      for (int i = 0; i < keys.length; i++) {
        final K _key = keys[i];
        if (key.equals(_key)) {
          return Optional.of(_key);
        }
      }
      return Optional.empty();
    }

    @Override
    Optional<K> findByKey(final K key, final int keyHash, final int shift,
        final Comparator<Object> cmp) {
      for (int i = 0; i < keys.length; i++) {
        final K _key = keys[i];
        if (cmp.compare(key, _key) == 0) {
          return Optional.of(_key);
        }
      }
      return Optional.empty();
    }

    /*
     * TODO: visibility is currently public to allow set-multimap experiments. Must be set back to
     * `protected` when experiments are finished.
     */
    @Override
    public /* protected */ CompactSetNode<K> updated(final AtomicReference<Thread> mutator,
        final K key, final int keyHash, final int shift, final SetResult<K> details) {
      assert this.hash == keyHash;

      for (int idx = 0; idx < keys.length; idx++) {
        if (keys[idx].equals(key)) {
          return this;
        }
      }

      final K[] keysNew = (K[]) new Object[this.keys.length + 1];

      // copy 'this.keys' and insert 1 element(s) at position
      // 'keys.length'
      System.arraycopy(this.keys, 0, keysNew, 0, keys.length);
      keysNew[keys.length + 0] = key;
      System.arraycopy(this.keys, keys.length, keysNew, keys.length + 1,
          this.keys.length - keys.length);

      details.modified();
      return new HashCollisionSetNode<>(keyHash, keysNew);
    }

    @Override
    CompactSetNode<K> updated(final AtomicReference<Thread> mutator, final K key,
        final int keyHash,
        final int shift, final SetResult<K> details, final Comparator<Object> cmp) {
      assert this.hash == keyHash;

      for (int idx = 0; idx < keys.length; idx++) {
        if (cmp.compare(keys[idx], key) == 0) {
          return this;
        }
      }

      final K[] keysNew = (K[]) new Object[this.keys.length + 1];

      // copy 'this.keys' and insert 1 element(s) at position
      // 'keys.length'
      System.arraycopy(this.keys, 0, keysNew, 0, keys.length);
      keysNew[keys.length + 0] = key;
      System.arraycopy(this.keys, keys.length, keysNew, keys.length + 1,
          this.keys.length - keys.length);

      details.modified();
      return new HashCollisionSetNode<>(keyHash, keysNew);
    }

    /*
     * TODO: visibility is currently public to allow set-multimap experiments. Must be set back to
     * `protected` when experiments are finished.
     */
    @Override
    public /* protected */ CompactSetNode<K> removed(final AtomicReference<Thread> mutator,
        final K key, final int keyHash, final int shift, final SetResult<K> details) {
      for (int idx = 0; idx < keys.length; idx++) {
        if (keys[idx].equals(key)) {
          details.modified();

          if (this.arity() == 1) {
            return nodeOf(mutator);
          } else if (this.arity() == 2) {
            /*
             * Create root node with singleton element. This node will be a) either be the new root
             * returned, or b) unwrapped and inlined.
             */
            final K theOtherKey = (idx == 0) ? keys[1] : keys[0];

            return CompactSetNode.<K>nodeOf(mutator).updated(mutator, theOtherKey, keyHash, 0,
                details);
          } else {
            final K[] keysNew = (K[]) new Object[this.keys.length - 1];

            // copy 'this.keys' and remove 1 element(s) at position
            // 'idx'
            System.arraycopy(this.keys, 0, keysNew, 0, idx);
            System.arraycopy(this.keys, idx + 1, keysNew, idx, this.keys.length - idx - 1);

            return new HashCollisionSetNode<>(keyHash, keysNew);
          }
        }
      }
      return this;
    }

    @Override
    CompactSetNode<K> removed(final AtomicReference<Thread> mutator, final K key,
        final int keyHash,
        final int shift, final SetResult<K> details, final Comparator<Object> cmp) {
      for (int idx = 0; idx < keys.length; idx++) {
        if (cmp.compare(keys[idx], key) == 0) {
          details.modified();

          if (this.arity() == 1) {
            return nodeOf(mutator);
          } else if (this.arity() == 2) {
            /*
             * Create root node with singleton element. This node will be a) either be the new root
             * returned, or b) unwrapped and inlined.
             */
            final K theOtherKey = (idx == 0) ? keys[1] : keys[0];

            return CompactSetNode.<K>nodeOf(mutator).updated(mutator, theOtherKey, keyHash, 0,
                details, cmp);
          } else {
            final K[] keysNew = (K[]) new Object[this.keys.length - 1];

            // copy 'this.keys' and remove 1 element(s) at position
            // 'idx'
            System.arraycopy(this.keys, 0, keysNew, 0, idx);
            System.arraycopy(this.keys, idx + 1, keysNew, idx, this.keys.length - idx - 1);

            return new HashCollisionSetNode<>(keyHash, keysNew);
          }
        }
      }
      return this;
    }

    @Override
    public boolean hasPayload() {
      return true;
    }

    @Override
    public int payloadArity() {
      return keys.length;
    }

    @Override
    boolean hasNodes() {
      return false;
    }

    @Override
    int nodeArity() {
      return 0;
    }

    @Override
    int arity() {
      return payloadArity();
    }

    @Override
    public byte sizePredicate() {
      return SIZE_MORE_THAN_ONE;
    }

    @Override
    public K getKey(final int index) {
      return keys[index];
    }

    @Override
    public int getKeyHash(int index) {
      return getKey(index).hashCode();
    }

    @Override
    public CompactSetNode<K> getNode(int index) {
      throw new IllegalStateException("Is leaf node.");
    }

//    @Override
//    public void setNode(AtomicReference<Thread> mutator, int index, AbstractSetNode<K> node) {
//      throw new IllegalStateException("Is leaf node.");
//    }

    @Override
    Object getSlot(final int index) {
      throw new UnsupportedOperationException();
    }

    @Override
    boolean hasSlots() {
      throw new UnsupportedOperationException();
    }

    @Override
    int slotArity() {
      throw new UnsupportedOperationException();
    }

    @Override
    int localPayloadHashCode() {
      return hash * keys.length;
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 0;
      result = prime * result + hash;
      result = prime * result + Arrays.hashCode(keys);
      return result;
    }

    @Override
    public boolean equals(Object other) {
      if (null == other) {
        return false;
      }
      if (this == other) {
        return true;
      }
      if (getClass() != other.getClass()) {
        return false;
      }

      HashCollisionSetNode<?> that = (HashCollisionSetNode<?>) other;

      if (hash != that.hash) {
        return false;
      }

      if (arity() != that.arity()) {
        return false;
      }

      /*
       * Linear scan for each key, because of arbitrary element order.
       */
      outerLoop:
      for (int i = 0; i < that.payloadArity(); i++) {
        final Object otherKey = that.getKey(i);

        for (int j = 0; j < keys.length; j++) {
          final K key = keys[j];

          if (key.equals(otherKey)) {
            continue outerLoop;
          }
        }
        return false;

      }

      return true;
    }

    @Override
    CompactSetNode<K> copyAndInsertValue(final AtomicReference<Thread> mutator, final int bitpos,
        final K key) {
      throw new UnsupportedOperationException();
    }

    @Override
    CompactSetNode<K> copyAndRemoveValue(final AtomicReference<Thread> mutator,
        final int bitpos) {
      throw new UnsupportedOperationException();
    }

    @Override
    CompactSetNode<K> copyAndSetNode(final AtomicReference<Thread> mutator, final int bitpos,
        final CompactSetNode<K> node) {
      throw new UnsupportedOperationException();
    }

    @Override
    CompactSetNode<K> copyAndMigrateFromInlineToNode(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetNode<K> node) {
      throw new UnsupportedOperationException();
    }

    @Override
    CompactSetNode<K> copyAndMigrateFromNodeToInline(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetNode<K> node) {
      throw new UnsupportedOperationException();
    }

    @Override
    final int nodeMap() {
      throw new UnsupportedOperationException();
    }

    @Override
    final int dataMap() {
      throw new UnsupportedOperationException();
    }

    /* TODO: support tracking of changes with 'IntersectionResult'. */
    @Override
    public AbstractSetNode<K> intersect(AtomicReference<Thread> mutator, AbstractSetNode<K> other,
        int shift, IntersectionResult details, Comparator<Object> cmp,
        Preference directionPreference) {
      assert other instanceof PersistentTrieSet.HashCollisionSetNode;

      final HashCollisionSetNode that = (HashCollisionSetNode) other;

      final List<K> buffer = new ArrayList<>();

      final K[] newKeys = (K[]) Stream.of(keys)
          .filter(key -> other.contains(key, hash, shift, cmp))
          .toArray();

      if (newKeys.length == 0) {
        return EMPTY_NODE;
      } else if (newKeys.length == 1) {
        return CompactSetNode.<K>nodeOf(mutator)
            .updated(mutator, newKeys[0], hash, 0, SetResult.unchanged(), cmp);
      } else {
        return new HashCollisionSetNode<>(hash, newKeys);
      }
    }

  }

  /**
   * Iterator skeleton that uses a fixed stack in depth.
   */
  private static abstract class AbstractSetIterator<K> {

    private static final int MAX_DEPTH = 7;

    protected int currentValueCursor;
    protected int currentValueLength;
    protected AbstractSetNode<K> currentValueNode;

    private int currentStackLevel = -1;
    private final int[] nodeCursorsAndLengths = new int[MAX_DEPTH * 2];

    AbstractSetNode<K>[] nodes = new AbstractSetNode[MAX_DEPTH];

    AbstractSetIterator(AbstractSetNode<K> rootNode) {
      if (rootNode.hasNodes()) {
        currentStackLevel = 0;

        nodes[0] = rootNode;
        nodeCursorsAndLengths[0] = 0;
        nodeCursorsAndLengths[1] = rootNode.nodeArity();
      }

      if (rootNode.hasPayload()) {
        currentValueNode = rootNode;
        currentValueCursor = 0;
        currentValueLength = rootNode.payloadArity();
      }
    }

    /*
     * search for next node that contains values
     */
    private boolean searchNextValueNode() {
      while (currentStackLevel >= 0) {
        final int currentCursorIndex = currentStackLevel * 2;
        final int currentLengthIndex = currentCursorIndex + 1;

        final int nodeCursor = nodeCursorsAndLengths[currentCursorIndex];
        final int nodeLength = nodeCursorsAndLengths[currentLengthIndex];

        if (nodeCursor < nodeLength) {
          final AbstractSetNode<K> nextNode = nodes[currentStackLevel].getNode(nodeCursor);
          nodeCursorsAndLengths[currentCursorIndex]++;

          if (nextNode.hasNodes()) {
            /*
             * put node on next stack level for depth-first traversal
             */
            final int nextStackLevel = ++currentStackLevel;
            final int nextCursorIndex = nextStackLevel * 2;
            final int nextLengthIndex = nextCursorIndex + 1;

            nodes[nextStackLevel] = nextNode;
            nodeCursorsAndLengths[nextCursorIndex] = 0;
            nodeCursorsAndLengths[nextLengthIndex] = nextNode.nodeArity();
          }

          if (nextNode.hasPayload()) {
            /*
             * found next node that contains values
             */
            currentValueNode = nextNode;
            currentValueCursor = 0;
            currentValueLength = nextNode.payloadArity();
            return true;
          }
        } else {
          currentStackLevel--;
        }
      }

      return false;
    }

    public boolean hasNext() {
      if (currentValueCursor < currentValueLength) {
        return true;
      } else {
        return searchNextValueNode();
      }
    }

    public void remove() {
      throw new UnsupportedOperationException();
    }
  }

  protected static class SetKeyIterator<K> extends AbstractSetIterator<K> implements Iterator<K> {

    SetKeyIterator(AbstractSetNode<K> rootNode) {
      super(rootNode);
    }

    @Override
    public K next() {
      if (!hasNext()) {
        throw new NoSuchElementException();
      } else {
        return currentValueNode.getKey(currentValueCursor++);
      }
    }

  }

  /**
   * Iterator that first iterates over inlined-values and then continues depth first recursively.
   */
  private static class TrieSetNodeIterator<K> implements Iterator<AbstractSetNode<K>> {

    final Deque<Iterator<? extends AbstractSetNode<K>>> nodeIteratorStack;

    TrieSetNodeIterator(AbstractSetNode<K> rootNode) {
      nodeIteratorStack = new ArrayDeque<>();
      nodeIteratorStack.push(Collections.singleton(rootNode).iterator());
    }

    @Override
    public boolean hasNext() {
      while (true) {
        if (nodeIteratorStack.isEmpty()) {
          return false;
        } else {
          if (nodeIteratorStack.peek().hasNext()) {
            return true;
          } else {
            nodeIteratorStack.pop();
            continue;
          }
        }
      }
    }

    @Override
    public AbstractSetNode<K> next() {
      if (!hasNext()) {
        throw new NoSuchElementException();
      }

      AbstractSetNode<K> innerNode = nodeIteratorStack.peek().next();

      if (innerNode.hasNodes()) {
        nodeIteratorStack.push(innerNode.nodeIterator());
      }

      return innerNode;
    }

    @Override
    public void remove() {
      throw new UnsupportedOperationException();
    }
  }

  static abstract class AbstractTransientTrieSet<K> implements Set.Transient<K> {

    protected AbstractSetNode<K> rootNode;
    protected int cachedHashCode;
    protected int cachedSize;

    AbstractTransientTrieSet(PersistentTrieSet<K> trieSet) {
      this.rootNode = trieSet.rootNode;
      this.cachedHashCode = trieSet.cachedHashCode;
      this.cachedSize = trieSet.cachedSize;
      if (DEBUG) {
        assert checkHashCodeAndSize(cachedHashCode, cachedSize);
      }
    }

    private boolean checkHashCodeAndSize(final int targetHash, final int targetSize) {
      int hash = 0;
      int size = 0;

      for (Iterator<K> it = keyIterator(); it.hasNext(); ) {
        final K key = it.next();

        hash += key.hashCode();
        size += 1;
      }

      return hash == targetHash && size == targetSize;
    }

    @Override
    public boolean add(final K key) {
      throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(final Collection<? extends K> c) {
      throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
      throw new UnsupportedOperationException();
    }

    @Override
    public boolean remove(final Object key) {
      throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(final Collection<?> c) {
      throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(final Collection<?> c) {
      throw new UnsupportedOperationException();
    }

    @Override
    public boolean contains(final Object o) {
      try {
        final K key = (K) o;
        return rootNode.contains(key, transformHashCode(key.hashCode()), 0);
      } catch (ClassCastException unused) {
        return false;
      }
    }

    @Override
    public boolean containsEquivalent(final Object o, final Comparator<Object> cmp) {
      try {
        final K key = (K) o;
        return rootNode.contains(key, transformHashCode(key.hashCode()), 0, cmp);
      } catch (ClassCastException unused) {
        return false;
      }
    }

    @Override
    public K get(final Object o) {
      try {
        final K key = (K) o;
        final Optional<K> result = rootNode.findByKey(key, transformHashCode(key.hashCode()), 0);

        if (result.isPresent()) {
          return result.get();
        } else {
          return null;
        }
      } catch (ClassCastException unused) {
        return null;
      }
    }

    @Override
    public K getEquivalent(final Object o, final Comparator<Object> cmp) {
      try {
        final K key = (K) o;
        final Optional<K> result =
            rootNode.findByKey(key, transformHashCode(key.hashCode()), 0, cmp);

        if (result.isPresent()) {
          return result.get();
        } else {
          return null;
        }
      } catch (ClassCastException unused) {
        return null;
      }
    }

    protected boolean __insertWithCapability(AtomicReference<Thread> mutator, K key) {
      if (mutator.get() == null) {
        throw new IllegalStateException("Transient already frozen.");
      }

      final int keyHash = key.hashCode();
      final SetResult<K> details = SetResult.unchanged();

      final CompactSetNode<K> newRootNode =
          rootNode.updated(mutator, key, transformHashCode(keyHash), 0, details);

      if (details.isModified()) {

        rootNode = newRootNode;
        cachedHashCode += keyHash;
        cachedSize += 1;

        if (DEBUG) {
          assert checkHashCodeAndSize(cachedHashCode, cachedSize);
        }
        return true;

      }

      if (DEBUG) {
        assert checkHashCodeAndSize(cachedHashCode, cachedSize);
      }
      return false;
    }

    protected boolean __insertEquivalentWithCapability(AtomicReference<Thread> mutator,
        final K key,
        final Comparator<Object> cmp) {
      if (mutator.get() == null) {
        throw new IllegalStateException("Transient already frozen.");
      }

      final int keyHash = key.hashCode();
      final SetResult<K> details = SetResult.unchanged();

      final CompactSetNode<K> newRootNode =
          rootNode.updated(mutator, key, transformHashCode(keyHash), 0, details, cmp);

      if (details.isModified()) {

        rootNode = newRootNode;
        cachedHashCode += keyHash;
        cachedSize += 1;

        if (DEBUG) {
          assert checkHashCodeAndSize(cachedHashCode, cachedSize);
        }
        return true;

      }

      if (DEBUG) {
        assert checkHashCodeAndSize(cachedHashCode, cachedSize);
      }
      return false;
    }

    @Override
    public boolean __insertAll(final java.util.Set<? extends K> set) {
      boolean modified = false;

      for (final K key : set) {
        modified |= this.__insert(key);
      }

      return modified;
    }

    @Override
    public boolean __insertAllEquivalent(final java.util.Set<? extends K> set,
        final Comparator<Object> cmp) {
      boolean modified = false;

      for (final K key : set) {
        modified |= this.__insertEquivalent(key, cmp);
      }

      return modified;
    }

    protected boolean __removeWithCapability(AtomicReference<Thread> mutator, final K key) {
      if (mutator.get() == null) {
        throw new IllegalStateException("Transient already frozen.");
      }

      final int keyHash = key.hashCode();
      final SetResult<K> details = SetResult.unchanged();

      final CompactSetNode<K> newRootNode =
          rootNode.removed(mutator, key, transformHashCode(keyHash), 0, details);

      if (details.isModified()) {
        rootNode = newRootNode;
        cachedHashCode = cachedHashCode - keyHash;
        cachedSize = cachedSize - 1;

        if (DEBUG) {
          assert checkHashCodeAndSize(cachedHashCode, cachedSize);
        }
        return true;
      }

      if (DEBUG) {
        assert checkHashCodeAndSize(cachedHashCode, cachedSize);
      }

      return false;
    }

    protected boolean __removeEquivalentWithCapability(AtomicReference<Thread> mutator,
        final K key,
        final Comparator<Object> cmp) {
      if (mutator.get() == null) {
        throw new IllegalStateException("Transient already frozen.");
      }

      final int keyHash = key.hashCode();
      final SetResult<K> details = SetResult.unchanged();

      final CompactSetNode<K> newRootNode =
          rootNode.removed(mutator, key, transformHashCode(keyHash), 0, details, cmp);

      if (details.isModified()) {
        rootNode = newRootNode;
        cachedHashCode = cachedHashCode - keyHash;
        cachedSize = cachedSize - 1;

        if (DEBUG) {
          assert checkHashCodeAndSize(cachedHashCode, cachedSize);
        }
        return true;
      }

      if (DEBUG) {
        assert checkHashCodeAndSize(cachedHashCode, cachedSize);
      }

      return false;
    }

    @Override
    public boolean __removeAll(final java.util.Set<? extends K> set) {
      boolean modified = false;

      for (final K key : set) {
        modified |= this.__remove(key);
      }

      return modified;
    }

    @Override
    public boolean __removeAllEquivalent(final java.util.Set<? extends K> set,
        final Comparator<Object> cmp) {
      boolean modified = false;

      for (final K key : set) {
        modified |= this.__removeEquivalent(key, cmp);
      }

      return modified;
    }

    @Override
    public boolean __retainAll(final java.util.Set<? extends K> set) {
      boolean modified = false;

      Iterator<K> thisIterator = iterator();
      while (thisIterator.hasNext()) {
        if (!set.contains(thisIterator.next())) {
          thisIterator.remove();
          modified = true;
        }
      }

      return modified;
    }

    @Override
    public boolean __retainAllEquivalent(final Set.Transient<? extends K> transientSet,
        final Comparator<Object> cmp) {
      boolean modified = false;

      Iterator<K> thisIterator = iterator();
      while (thisIterator.hasNext()) {
        if (!transientSet.containsEquivalent(thisIterator.next(), cmp)) {
          thisIterator.remove();
          modified = true;
        }
      }

      return modified;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
      for (Object item : c) {
        if (!contains(item)) {
          return false;
        }
      }
      return true;
    }

    @Override
    public boolean containsAllEquivalent(Collection<?> c, Comparator<Object> cmp) {
      for (Object item : c) {
        if (!containsEquivalent(item, cmp)) {
          return false;
        }
      }
      return true;
    }

    @Override
    public int size() {
      return cachedSize;
    }

    @Override
    public boolean isEmpty() {
      return cachedSize == 0;
    }

    @Override
    public Iterator<K> iterator() {
      return keyIterator();
    }

    @Override
    public Iterator<K> keyIterator() {
      return new TransientSetKeyIterator<>(this);
    }

    public static class TransientSetKeyIterator<K> extends SetKeyIterator<K> {

      final AbstractTransientTrieSet<K> collection;
      K lastKey;

      public TransientSetKeyIterator(final AbstractTransientTrieSet<K> collection) {
        super(collection.rootNode);
        this.collection = collection;
      }

      @Override
      public K next() {
        return lastKey = super.next();
      }

      @Override
      public void remove() {
        // TODO: test removal at iteration rigorously
        collection.__remove(lastKey);
      }
    }

    @Override
    public Object[] toArray() {
      Object[] array = new Object[cachedSize];

      int idx = 0;
      for (K key : this) {
        array[idx++] = key;
      }

      return array;
    }

    @Override
    public <T> T[] toArray(final T[] a) {
      List<K> list = new ArrayList<K>(cachedSize);

      for (K key : this) {
        list.add(key);
      }

      return list.toArray(a);
    }

    public boolean equals(final Object other) {
      if (other == this) {
        return true;
      }
      if (other == null) {
        return false;
      }

      if (other instanceof PersistentTrieSet.AbstractTransientTrieSet) {
        AbstractTransientTrieSet<?> that = (AbstractTransientTrieSet<?>) other;

        if (this.cachedSize != that.cachedSize) {
          return false;
        }

        if (this.cachedHashCode != that.cachedHashCode) {
          return false;
        }

        return rootNode.equals(that.rootNode);
      } else if (other instanceof java.util.Set) {
        java.util.Set that = (java.util.Set) other;

        if (this.size() != that.size()) {
          return false;
        }

        return containsAll(that);
      }

      return false;
    }

    @Override
    public int hashCode() {
      return cachedHashCode;
    }

  }

  static final class TransientTrieSet<K> extends AbstractTransientTrieSet<K> {

    final private AtomicReference<Thread> mutator;

    TransientTrieSet(PersistentTrieSet<K> trieSet) {
      super(trieSet);
      this.mutator = new AtomicReference<Thread>(Thread.currentThread());
    }

    @Override
    public boolean __insert(final K key) {
      return __insertWithCapability(this.mutator, key);
    }

    @Override
    public boolean __insertEquivalent(final K key, final Comparator<Object> cmp) {
      return __insertEquivalentWithCapability(this.mutator, key, cmp);
    }

    @Override
    public boolean __remove(final K key) {
      return __removeWithCapability(this.mutator, key);
    }

    @Override
    public boolean __removeEquivalent(final K key, final Comparator<Object> cmp) {
      return __removeEquivalentWithCapability(this.mutator, key, cmp);
    }

    @Override
    public Set.Immutable<K> freeze() {
      if (mutator.get() == null) {
        throw new IllegalStateException("Transient already frozen.");
      }

      mutator.set(null);
      return new PersistentTrieSet<K>(rootNode, cachedHashCode, cachedSize);
    }

  }

}
