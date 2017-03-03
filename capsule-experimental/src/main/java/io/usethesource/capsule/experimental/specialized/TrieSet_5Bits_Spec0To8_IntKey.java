/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.capsule.experimental.specialized;

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
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

@SuppressWarnings("rawtypes")
public class TrieSet_5Bits_Spec0To8_IntKey implements io.usethesource.capsule.Set.Immutable<Integer> {

  @SuppressWarnings("unchecked")
  private static final TrieSet_5Bits_Spec0To8_IntKey EMPTY_SET =
      new TrieSet_5Bits_Spec0To8_IntKey(CompactSetNode.EMPTY_NODE, 0, 0);

  private static final boolean DEBUG = false;

  private final AbstractSetNode rootNode;
  private final int hashCode;
  private final int cachedSize;

  TrieSet_5Bits_Spec0To8_IntKey(AbstractSetNode rootNode, int hashCode, int cachedSize) {
    this.rootNode = rootNode;
    this.hashCode = hashCode;
    this.cachedSize = cachedSize;
    if (DEBUG) {
      assert checkHashCodeAndSize(hashCode, cachedSize);
    }
  }

  @SuppressWarnings("unchecked")
  public static final io.usethesource.capsule.Set.Immutable<Integer> of() {
    return TrieSet_5Bits_Spec0To8_IntKey.EMPTY_SET;
  }

  @SuppressWarnings("unchecked")
  public static final io.usethesource.capsule.Set.Immutable<Integer> of(int... keys) {
    io.usethesource.capsule.Set.Immutable<Integer> result = TrieSet_5Bits_Spec0To8_IntKey.EMPTY_SET;

    for (final int key : keys) {
      result = result.__insert(key);
    }

    return result;
  }

  @SuppressWarnings("unchecked")
  public static final io.usethesource.capsule.Set.Transient<Integer> transientOf() {
    return TrieSet_5Bits_Spec0To8_IntKey.EMPTY_SET.asTransient();
  }

  @SuppressWarnings("unchecked")
  public static final io.usethesource.capsule.Set.Transient<Integer> transientOf(int... keys) {
    final io.usethesource.capsule.Set.Transient<Integer> result =
        TrieSet_5Bits_Spec0To8_IntKey.EMPTY_SET.asTransient();

    for (final int key : keys) {
      result.__insert(key);
    }

    return result;
  }

  private boolean checkHashCodeAndSize(final int targetHash, final int targetSize) {
    int hash = 0;
    int size = 0;

    for (Iterator<java.lang.Integer> it = keyIterator(); it.hasNext();) {
      final int key = it.next();

      hash += (int) key;
      size += 1;
    }

    return hash == targetHash && size == targetSize;
  }

  public static final int transformHashCode(final int hash) {
    return hash;
  }

  public boolean contains(final Object o) {
    try {
      @SuppressWarnings("unchecked")
      final int key = (int) o;
      return rootNode.contains(key, transformHashCode(key), 0);
    } catch (ClassCastException unused) {
      return false;
    }
  }

  public boolean containsEquivalent(final Object o, final Comparator<Object> cmp) {
    try {
      @SuppressWarnings("unchecked")
      final int key = (int) o;
      return rootNode.contains(key, transformHashCode(key), 0, cmp);
    } catch (ClassCastException unused) {
      return false;
    }
  }

  public java.lang.Integer get(final Object o) {
    try {
      @SuppressWarnings("unchecked")
      final int key = (int) o;
      final Optional<java.lang.Integer> result = rootNode.findByKey(key, transformHashCode(key), 0);

      if (result.isPresent()) {
        return result.get();
      } else {
        return null;
      }
    } catch (ClassCastException unused) {
      return null;
    }
  }

  public java.lang.Integer getEquivalent(final Object o, final Comparator<Object> cmp) {
    try {
      @SuppressWarnings("unchecked")
      final int key = (int) o;
      final Optional<java.lang.Integer> result =
          rootNode.findByKey(key, transformHashCode(key), 0, cmp);

      if (result.isPresent()) {
        return result.get();
      } else {
        return null;
      }
    } catch (ClassCastException unused) {
      return null;
    }
  }

  public io.usethesource.capsule.Set.Immutable<Integer> __insert(final java.lang.Integer key) {
    final int keyHash = key.hashCode();
    final SetResult details = SetResult.unchanged();

    final CompactSetNode newRootNode =
        rootNode.updated(null, key, transformHashCode(keyHash), 0, details);

    if (details.isModified()) {
      return new TrieSet_5Bits_Spec0To8_IntKey(newRootNode, hashCode + keyHash, cachedSize + 1);
    }

    return this;
  }

  public io.usethesource.capsule.Set.Immutable<Integer> __insertEquivalent(final java.lang.Integer key,
                                                                               final Comparator<Object> cmp) {
    final int keyHash = key.hashCode();
    final SetResult details = SetResult.unchanged();

    final CompactSetNode newRootNode =
        rootNode.updated(null, key, transformHashCode(keyHash), 0, details, cmp);

    if (details.isModified()) {
      return new TrieSet_5Bits_Spec0To8_IntKey(newRootNode, hashCode + keyHash, cachedSize + 1);
    }

    return this;
  }

  public io.usethesource.capsule.Set.Immutable<Integer> __insertAll(final Set<? extends java.lang.Integer> set) {
    final io.usethesource.capsule.Set.Transient<Integer> tmpTransient = this.asTransient();
    tmpTransient.__insertAll(set);
    return tmpTransient.freeze();
  }

  public io.usethesource.capsule.Set.Immutable<Integer> __insertAllEquivalent(
      final Set<? extends java.lang.Integer> set, final Comparator<Object> cmp) {
    final io.usethesource.capsule.Set.Transient<Integer> tmpTransient = this.asTransient();
    tmpTransient.__insertAllEquivalent(set, cmp);
    return tmpTransient.freeze();
  }

  public io.usethesource.capsule.Set.Immutable<Integer> __remove(final java.lang.Integer key) {
    final int keyHash = key.hashCode();
    final SetResult details = SetResult.unchanged();

    final CompactSetNode newRootNode =
        rootNode.removed(null, key, transformHashCode(keyHash), 0, details);

    if (details.isModified()) {
      return new TrieSet_5Bits_Spec0To8_IntKey(newRootNode, hashCode - keyHash, cachedSize - 1);
    }

    return this;
  }

  public io.usethesource.capsule.Set.Immutable<Integer> __removeEquivalent(final java.lang.Integer key,
                                                                               final Comparator<Object> cmp) {
    final int keyHash = key.hashCode();
    final SetResult details = SetResult.unchanged();

    final CompactSetNode newRootNode =
        rootNode.removed(null, key, transformHashCode(keyHash), 0, details, cmp);

    if (details.isModified()) {
      return new TrieSet_5Bits_Spec0To8_IntKey(newRootNode, hashCode - keyHash, cachedSize - 1);
    }

    return this;
  }

  public io.usethesource.capsule.Set.Immutable<Integer> __removeAll(final Set<? extends java.lang.Integer> set) {
    final io.usethesource.capsule.Set.Transient<Integer> tmpTransient = this.asTransient();
    tmpTransient.__removeAll(set);
    return tmpTransient.freeze();
  }

  public io.usethesource.capsule.Set.Immutable<Integer> __removeAllEquivalent(
      final Set<? extends java.lang.Integer> set, final Comparator<Object> cmp) {
    final io.usethesource.capsule.Set.Transient<Integer> tmpTransient = this.asTransient();
    tmpTransient.__removeAllEquivalent(set, cmp);
    return tmpTransient.freeze();
  }

  public io.usethesource.capsule.Set.Immutable<Integer> __retainAll(final Set<? extends java.lang.Integer> set) {
    final io.usethesource.capsule.Set.Transient<Integer> tmpTransient = this.asTransient();
    tmpTransient.__retainAll(set);
    return tmpTransient.freeze();
  }

  public io.usethesource.capsule.Set.Immutable<Integer> __retainAllEquivalent(
      final io.usethesource.capsule.Set.Transient<? extends Integer> transientSet, final Comparator<Object> cmp) {
    final io.usethesource.capsule.Set.Transient<Integer> tmpTransient = this.asTransient();
    tmpTransient.__retainAllEquivalent(transientSet, cmp);
    return tmpTransient.freeze();
  }

  public boolean add(final java.lang.Integer key) {
    throw new UnsupportedOperationException();
  }

  public boolean addAll(final Collection<? extends java.lang.Integer> c) {
    throw new UnsupportedOperationException();
  }

  public void clear() {
    throw new UnsupportedOperationException();
  }

  public boolean remove(final Object key) {
    throw new UnsupportedOperationException();
  }

  public boolean removeAll(final Collection<?> c) {
    throw new UnsupportedOperationException();
  }

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

  public int size() {
    return cachedSize;
  }

  public boolean isEmpty() {
    return cachedSize == 0;
  }

  public Iterator<java.lang.Integer> iterator() {
    return keyIterator();
  }

  public Iterator<java.lang.Integer> keyIterator() {
    return new SetKeyIterator(rootNode);
  }

  @Override
  public Object[] toArray() {
    Object[] array = new Object[cachedSize];

    int idx = 0;
    for (java.lang.Integer key : this) {
      array[idx++] = key;
    }

    return array;
  }

  @Override
  public <T> T[] toArray(final T[] a) {
    List<java.lang.Integer> list = new ArrayList<java.lang.Integer>(cachedSize);

    for (java.lang.Integer key : this) {
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

    if (other instanceof TrieSet_5Bits_Spec0To8_IntKey) {
      TrieSet_5Bits_Spec0To8_IntKey that = (TrieSet_5Bits_Spec0To8_IntKey) other;

      if (this.cachedSize != that.cachedSize) {
        return false;
      }

      if (this.hashCode != that.hashCode) {
        return false;
      }

      return rootNode.equals(that.rootNode);
    } else if (other instanceof Set) {
      Set that = (Set) other;

      if (this.size() != that.size())
        return false;

      return containsAll(that);
    }

    return false;
  }

  @Override
  public int hashCode() {
    return hashCode;
  }

  @Override
  public boolean isTransientSupported() {
    return true;
  }

  @Override
  public io.usethesource.capsule.Set.Transient<Integer> asTransient() {
    return new TransientTrieSet_5Bits_Spec0To8_IntKey(this);
  }

  /*
   * For analysis purposes only.
   */
  protected AbstractSetNode getRootNode() {
    return rootNode;
  }

  /*
   * For analysis purposes only.
   */
  protected Iterator<AbstractSetNode> nodeIterator() {
    return new TrieSet_5Bits_Spec0To8_IntKeyNodeIterator(rootNode);
  }

  /*
   * For analysis purposes only.
   */
  protected int getNodeCount() {
    final Iterator<AbstractSetNode> it = nodeIterator();
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
    final Iterator<AbstractSetNode> it = nodeIterator();
    final int[][] sumArityCombinations = new int[33][33];

    while (it.hasNext()) {
      final AbstractSetNode node = it.next();
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

  abstract static class Optional<T> {
    private static final Optional EMPTY = new Optional() {
      @Override
      boolean isPresent() {
        return false;
      }

      @Override
      Object get() {
        return null;
      }
    };

    @SuppressWarnings("unchecked")
    static <T> Optional<T> empty() {
      return EMPTY;
    }

    static <T> Optional<T> of(T value) {
      return new Value<T>(value);
    }

    abstract boolean isPresent();

    abstract T get();

    private static final class Value<T> extends Optional<T> {
      private final T value;

      private Value(T value) {
        this.value = value;
      }

      @Override
      boolean isPresent() {
        return true;
      }

      @Override
      T get() {
        return value;
      }
    }
  }

  static final class SetResult {
    private int replacedValue;
    private boolean isModified;
    private boolean isReplaced;

    // update: inserted/removed single element, element count changed
    public void modified() {
      this.isModified = true;
    }

    public void updated(int replacedValue) {
      this.replacedValue = replacedValue;
      this.isModified = true;
      this.isReplaced = true;
    }

    // update: neither element, nor element count changed
    public static SetResult unchanged() {
      return new SetResult();
    }

    private SetResult() {}

    public boolean isModified() {
      return isModified;
    }

    public boolean hasReplacedValue() {
      return isReplaced;
    }

    public int getReplacedValue() {
      return replacedValue;
    }
  }

  protected static interface INode<K, V> {
  }

  protected static abstract class AbstractSetNode
      implements INode<java.lang.Integer, java.lang.Void> {

    static final int TUPLE_LENGTH = 1;

    abstract boolean contains(final int key, final int keyHash, final int shift);

    abstract boolean contains(final int key, final int keyHash, final int shift,
        final Comparator<Object> cmp);

    abstract Optional<java.lang.Integer> findByKey(final int key, final int keyHash,
        final int shift);

    abstract Optional<java.lang.Integer> findByKey(final int key, final int keyHash,
        final int shift, final Comparator<Object> cmp);

    abstract CompactSetNode updated(final AtomicReference<Thread> mutator, final int key,
        final int keyHash, final int shift, final SetResult details);

    abstract CompactSetNode updated(final AtomicReference<Thread> mutator, final int key,
        final int keyHash, final int shift, final SetResult details, final Comparator<Object> cmp);

    abstract CompactSetNode removed(final AtomicReference<Thread> mutator, final int key,
        final int keyHash, final int shift, final SetResult details);

    abstract CompactSetNode removed(final AtomicReference<Thread> mutator, final int key,
        final int keyHash, final int shift, final SetResult details, final Comparator<Object> cmp);

    static final boolean isAllowedToEdit(AtomicReference<Thread> x, AtomicReference<Thread> y) {
      return x != null && y != null && (x == y || x.get() == y.get());
    }

    abstract boolean hasNodes();

    abstract int nodeArity();

    abstract AbstractSetNode getNode(final int index);

    @Deprecated
    Iterator<? extends AbstractSetNode> nodeIterator() {
      return new Iterator<AbstractSetNode>() {

        int nextIndex = 0;
        final int nodeArity = AbstractSetNode.this.nodeArity();

        @Override
        public void remove() {
          throw new UnsupportedOperationException();
        }

        @Override
        public AbstractSetNode next() {
          if (!hasNext())
            throw new NoSuchElementException();
          return AbstractSetNode.this.getNode(nextIndex++);
        }

        @Override
        public boolean hasNext() {
          return nextIndex < nodeArity;
        }
      };
    }

    abstract boolean hasPayload();

    abstract int payloadArity();

    abstract int getKey(final int index);

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

    int size() {
      final Iterator<java.lang.Integer> it = new SetKeyIterator(this);

      int size = 0;
      while (it.hasNext()) {
        size += 1;
        it.next();
      }

      return size;
    }
  }

  protected static abstract class CompactSetNode extends AbstractSetNode {

    static final int HASH_CODE_LENGTH = 32;

    static final int BIT_PARTITION_SIZE = 5;
    static final int BIT_PARTITION_MASK = 0b11111;

    static final int mask(final int keyHash, final int shift) {
      return (keyHash >>> shift) & BIT_PARTITION_MASK;
    }

    static final int bitpos(final int mask) {
      return (int) (1 << mask);
    }

    abstract int nodeMap();

    abstract int dataMap();

    static final byte SIZE_EMPTY = 0b00;
    static final byte SIZE_ONE = 0b01;
    static final byte SIZE_MORE_THAN_ONE = 0b10;

    /**
     * Abstract predicate over a node's size. Value can be either {@value #SIZE_EMPTY},
     * {@value #SIZE_ONE}, or {@value #SIZE_MORE_THAN_ONE}.
     * 
     * @return size predicate
     */
    abstract byte sizePredicate();

    @Override
    abstract CompactSetNode getNode(final int index);

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

    abstract CompactSetNode copyAndInsertValue(final AtomicReference<Thread> mutator,
        final int bitpos, final int key);

    abstract CompactSetNode copyAndRemoveValue(final AtomicReference<Thread> mutator,
        final int bitpos);

    abstract CompactSetNode copyAndSetNode(final AtomicReference<Thread> mutator, final int bitpos,
        final CompactSetNode node);

    abstract CompactSetNode copyAndMigrateFromInlineToNode(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetNode node);

    abstract CompactSetNode copyAndMigrateFromNodeToInline(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetNode node);

    CompactSetNode removeInplaceValueAndConvertToSpecializedNode(
        final AtomicReference<Thread> mutator, final int bitpos) {
      throw new UnsupportedOperationException();
    }

    static final CompactSetNode mergeTwoKeyValPairs(final int key0, final int keyHash0,
        final int key1, final int keyHash1, final int shift) {
      assert !(key0 == key1);

      if (shift >= HASH_CODE_LENGTH) {
        // throw new
        // IllegalStateException("Hash collision not yet fixed.");
        return new HashCollisionSetNode_5Bits_Spec0To8_IntKey(keyHash0,
            (int[]) new int[] {key0, key1});
      }

      final int mask0 = mask(keyHash0, shift);
      final int mask1 = mask(keyHash1, shift);

      if (mask0 != mask1) {
        // both nodes fit on same level
        final int dataMap = (int) (bitpos(mask0) | bitpos(mask1));

        if (mask0 < mask1) {
          return nodeOf(null, (int) 0, dataMap, key0, key1);
        } else {
          return nodeOf(null, (int) 0, dataMap, key1, key0);
        }
      } else {
        final CompactSetNode node =
            mergeTwoKeyValPairs(key0, keyHash0, key1, keyHash1, shift + BIT_PARTITION_SIZE);
        // values fit on next level

        final int nodeMap = bitpos(mask0);
        return nodeOf(null, nodeMap, (int) 0, node);
      }
    }

    static final CompactSetNode EMPTY_NODE;

    static {

      EMPTY_NODE = new Set0To0Node_5Bits_Spec0To8_IntKey(null, (int) 0, (int) 0);

    };

    static final CompactSetNode nodeOf(final AtomicReference<Thread> mutator, final int nodeMap,
        final int dataMap, final Object[] nodes) {
      return new BitmapIndexedSetNode(mutator, nodeMap, dataMap, nodes);
    }

    @SuppressWarnings("unchecked")
    static final CompactSetNode nodeOf(AtomicReference<Thread> mutator) {
      return EMPTY_NODE;
    }

    static final CompactSetNode nodeOf(final AtomicReference<Thread> mutator, final int nodeMap,
        final int dataMap) {
      return EMPTY_NODE;
    }

    static final CompactSetNode nodeOf(final AtomicReference<Thread> mutator, final int nodeMap,
        final int dataMap, final CompactSetNode node1) {
      return new Set0To1Node_5Bits_Spec0To8_IntKey(mutator, nodeMap, dataMap, node1);
    }

    static final CompactSetNode nodeOf(final AtomicReference<Thread> mutator, final int nodeMap,
        final int dataMap, final CompactSetNode node1, final CompactSetNode node2) {
      return new Set0To2Node_5Bits_Spec0To8_IntKey(mutator, nodeMap, dataMap, node1, node2);
    }

    static final CompactSetNode nodeOf(final AtomicReference<Thread> mutator, final int nodeMap,
        final int dataMap, final CompactSetNode node1, final CompactSetNode node2,
        final CompactSetNode node3) {
      return new Set0To3Node_5Bits_Spec0To8_IntKey(mutator, nodeMap, dataMap, node1, node2, node3);
    }

    static final CompactSetNode nodeOf(final AtomicReference<Thread> mutator, final int nodeMap,
        final int dataMap, final CompactSetNode node1, final CompactSetNode node2,
        final CompactSetNode node3, final CompactSetNode node4) {
      return new Set0To4Node_5Bits_Spec0To8_IntKey(mutator, nodeMap, dataMap, node1, node2, node3,
          node4);
    }

    static final CompactSetNode nodeOf(final AtomicReference<Thread> mutator, final int nodeMap,
        final int dataMap, final CompactSetNode node1, final CompactSetNode node2,
        final CompactSetNode node3, final CompactSetNode node4, final CompactSetNode node5) {
      return new Set0To5Node_5Bits_Spec0To8_IntKey(mutator, nodeMap, dataMap, node1, node2, node3,
          node4, node5);
    }

    static final CompactSetNode nodeOf(final AtomicReference<Thread> mutator, final int nodeMap,
        final int dataMap, final CompactSetNode node1, final CompactSetNode node2,
        final CompactSetNode node3, final CompactSetNode node4, final CompactSetNode node5,
        final CompactSetNode node6) {
      return new Set0To6Node_5Bits_Spec0To8_IntKey(mutator, nodeMap, dataMap, node1, node2, node3,
          node4, node5, node6);
    }

    static final CompactSetNode nodeOf(final AtomicReference<Thread> mutator, final int nodeMap,
        final int dataMap, final CompactSetNode node1, final CompactSetNode node2,
        final CompactSetNode node3, final CompactSetNode node4, final CompactSetNode node5,
        final CompactSetNode node6, final CompactSetNode node7) {
      return new Set0To7Node_5Bits_Spec0To8_IntKey(mutator, nodeMap, dataMap, node1, node2, node3,
          node4, node5, node6, node7);
    }

    static final CompactSetNode nodeOf(final AtomicReference<Thread> mutator, final int nodeMap,
        final int dataMap, final CompactSetNode node1, final CompactSetNode node2,
        final CompactSetNode node3, final CompactSetNode node4, final CompactSetNode node5,
        final CompactSetNode node6, final CompactSetNode node7, final CompactSetNode node8) {
      return new Set0To8Node_5Bits_Spec0To8_IntKey(mutator, nodeMap, dataMap, node1, node2, node3,
          node4, node5, node6, node7, node8);
    }

    static final CompactSetNode nodeOf(final AtomicReference<Thread> mutator, final int nodeMap,
        final int dataMap, final CompactSetNode node1, final CompactSetNode node2,
        final CompactSetNode node3, final CompactSetNode node4, final CompactSetNode node5,
        final CompactSetNode node6, final CompactSetNode node7, final CompactSetNode node8,
        final CompactSetNode node9) {
      // NOTE: reversed node argument list due to CHAMP encoding
      return nodeOf(mutator, nodeMap, dataMap,
          new Object[] {node9, node8, node7, node6, node5, node4, node3, node2, node1});
    }

    static final CompactSetNode nodeOf(final AtomicReference<Thread> mutator, final int nodeMap,
        final int dataMap, final int key1) {
      return new Set1To0Node_5Bits_Spec0To8_IntKey(mutator, nodeMap, dataMap, key1);
    }

    static final CompactSetNode nodeOf(final AtomicReference<Thread> mutator, final int nodeMap,
        final int dataMap, final int key1, final CompactSetNode node1) {
      return new Set1To1Node_5Bits_Spec0To8_IntKey(mutator, nodeMap, dataMap, key1, node1);
    }

    static final CompactSetNode nodeOf(final AtomicReference<Thread> mutator, final int nodeMap,
        final int dataMap, final int key1, final CompactSetNode node1, final CompactSetNode node2) {
      return new Set1To2Node_5Bits_Spec0To8_IntKey(mutator, nodeMap, dataMap, key1, node1, node2);
    }

    static final CompactSetNode nodeOf(final AtomicReference<Thread> mutator, final int nodeMap,
        final int dataMap, final int key1, final CompactSetNode node1, final CompactSetNode node2,
        final CompactSetNode node3) {
      return new Set1To3Node_5Bits_Spec0To8_IntKey(mutator, nodeMap, dataMap, key1, node1, node2,
          node3);
    }

    static final CompactSetNode nodeOf(final AtomicReference<Thread> mutator, final int nodeMap,
        final int dataMap, final int key1, final CompactSetNode node1, final CompactSetNode node2,
        final CompactSetNode node3, final CompactSetNode node4) {
      return new Set1To4Node_5Bits_Spec0To8_IntKey(mutator, nodeMap, dataMap, key1, node1, node2,
          node3, node4);
    }

    static final CompactSetNode nodeOf(final AtomicReference<Thread> mutator, final int nodeMap,
        final int dataMap, final int key1, final CompactSetNode node1, final CompactSetNode node2,
        final CompactSetNode node3, final CompactSetNode node4, final CompactSetNode node5) {
      return new Set1To5Node_5Bits_Spec0To8_IntKey(mutator, nodeMap, dataMap, key1, node1, node2,
          node3, node4, node5);
    }

    static final CompactSetNode nodeOf(final AtomicReference<Thread> mutator, final int nodeMap,
        final int dataMap, final int key1, final CompactSetNode node1, final CompactSetNode node2,
        final CompactSetNode node3, final CompactSetNode node4, final CompactSetNode node5,
        final CompactSetNode node6) {
      return new Set1To6Node_5Bits_Spec0To8_IntKey(mutator, nodeMap, dataMap, key1, node1, node2,
          node3, node4, node5, node6);
    }

    static final CompactSetNode nodeOf(final AtomicReference<Thread> mutator, final int nodeMap,
        final int dataMap, final int key1, final CompactSetNode node1, final CompactSetNode node2,
        final CompactSetNode node3, final CompactSetNode node4, final CompactSetNode node5,
        final CompactSetNode node6, final CompactSetNode node7) {
      return new Set1To7Node_5Bits_Spec0To8_IntKey(mutator, nodeMap, dataMap, key1, node1, node2,
          node3, node4, node5, node6, node7);
    }

    static final CompactSetNode nodeOf(final AtomicReference<Thread> mutator, final int nodeMap,
        final int dataMap, final int key1, final CompactSetNode node1, final CompactSetNode node2,
        final CompactSetNode node3, final CompactSetNode node4, final CompactSetNode node5,
        final CompactSetNode node6, final CompactSetNode node7, final CompactSetNode node8) {
      // NOTE: reversed node argument list due to CHAMP encoding
      return nodeOf(mutator, nodeMap, dataMap,
          new Object[] {key1, node8, node7, node6, node5, node4, node3, node2, node1});
    }

    static final CompactSetNode nodeOf(final AtomicReference<Thread> mutator, final int nodeMap,
        final int dataMap, final int key1, final int key2) {
      return new Set2To0Node_5Bits_Spec0To8_IntKey(mutator, nodeMap, dataMap, key1, key2);
    }

    static final CompactSetNode nodeOf(final AtomicReference<Thread> mutator, final int nodeMap,
        final int dataMap, final int key1, final int key2, final CompactSetNode node1) {
      return new Set2To1Node_5Bits_Spec0To8_IntKey(mutator, nodeMap, dataMap, key1, key2, node1);
    }

    static final CompactSetNode nodeOf(final AtomicReference<Thread> mutator, final int nodeMap,
        final int dataMap, final int key1, final int key2, final CompactSetNode node1,
        final CompactSetNode node2) {
      return new Set2To2Node_5Bits_Spec0To8_IntKey(mutator, nodeMap, dataMap, key1, key2, node1,
          node2);
    }

    static final CompactSetNode nodeOf(final AtomicReference<Thread> mutator, final int nodeMap,
        final int dataMap, final int key1, final int key2, final CompactSetNode node1,
        final CompactSetNode node2, final CompactSetNode node3) {
      return new Set2To3Node_5Bits_Spec0To8_IntKey(mutator, nodeMap, dataMap, key1, key2, node1,
          node2, node3);
    }

    static final CompactSetNode nodeOf(final AtomicReference<Thread> mutator, final int nodeMap,
        final int dataMap, final int key1, final int key2, final CompactSetNode node1,
        final CompactSetNode node2, final CompactSetNode node3, final CompactSetNode node4) {
      return new Set2To4Node_5Bits_Spec0To8_IntKey(mutator, nodeMap, dataMap, key1, key2, node1,
          node2, node3, node4);
    }

    static final CompactSetNode nodeOf(final AtomicReference<Thread> mutator, final int nodeMap,
        final int dataMap, final int key1, final int key2, final CompactSetNode node1,
        final CompactSetNode node2, final CompactSetNode node3, final CompactSetNode node4,
        final CompactSetNode node5) {
      return new Set2To5Node_5Bits_Spec0To8_IntKey(mutator, nodeMap, dataMap, key1, key2, node1,
          node2, node3, node4, node5);
    }

    static final CompactSetNode nodeOf(final AtomicReference<Thread> mutator, final int nodeMap,
        final int dataMap, final int key1, final int key2, final CompactSetNode node1,
        final CompactSetNode node2, final CompactSetNode node3, final CompactSetNode node4,
        final CompactSetNode node5, final CompactSetNode node6) {
      return new Set2To6Node_5Bits_Spec0To8_IntKey(mutator, nodeMap, dataMap, key1, key2, node1,
          node2, node3, node4, node5, node6);
    }

    static final CompactSetNode nodeOf(final AtomicReference<Thread> mutator, final int nodeMap,
        final int dataMap, final int key1, final int key2, final CompactSetNode node1,
        final CompactSetNode node2, final CompactSetNode node3, final CompactSetNode node4,
        final CompactSetNode node5, final CompactSetNode node6, final CompactSetNode node7) {
      // NOTE: reversed node argument list due to CHAMP encoding
      return nodeOf(mutator, nodeMap, dataMap,
          new Object[] {key1, key2, node7, node6, node5, node4, node3, node2, node1});
    }

    static final CompactSetNode nodeOf(final AtomicReference<Thread> mutator, final int nodeMap,
        final int dataMap, final int key1, final int key2, final int key3) {
      return new Set3To0Node_5Bits_Spec0To8_IntKey(mutator, nodeMap, dataMap, key1, key2, key3);
    }

    static final CompactSetNode nodeOf(final AtomicReference<Thread> mutator, final int nodeMap,
        final int dataMap, final int key1, final int key2, final int key3,
        final CompactSetNode node1) {
      return new Set3To1Node_5Bits_Spec0To8_IntKey(mutator, nodeMap, dataMap, key1, key2, key3,
          node1);
    }

    static final CompactSetNode nodeOf(final AtomicReference<Thread> mutator, final int nodeMap,
        final int dataMap, final int key1, final int key2, final int key3,
        final CompactSetNode node1, final CompactSetNode node2) {
      return new Set3To2Node_5Bits_Spec0To8_IntKey(mutator, nodeMap, dataMap, key1, key2, key3,
          node1, node2);
    }

    static final CompactSetNode nodeOf(final AtomicReference<Thread> mutator, final int nodeMap,
        final int dataMap, final int key1, final int key2, final int key3,
        final CompactSetNode node1, final CompactSetNode node2, final CompactSetNode node3) {
      return new Set3To3Node_5Bits_Spec0To8_IntKey(mutator, nodeMap, dataMap, key1, key2, key3,
          node1, node2, node3);
    }

    static final CompactSetNode nodeOf(final AtomicReference<Thread> mutator, final int nodeMap,
        final int dataMap, final int key1, final int key2, final int key3,
        final CompactSetNode node1, final CompactSetNode node2, final CompactSetNode node3,
        final CompactSetNode node4) {
      return new Set3To4Node_5Bits_Spec0To8_IntKey(mutator, nodeMap, dataMap, key1, key2, key3,
          node1, node2, node3, node4);
    }

    static final CompactSetNode nodeOf(final AtomicReference<Thread> mutator, final int nodeMap,
        final int dataMap, final int key1, final int key2, final int key3,
        final CompactSetNode node1, final CompactSetNode node2, final CompactSetNode node3,
        final CompactSetNode node4, final CompactSetNode node5) {
      return new Set3To5Node_5Bits_Spec0To8_IntKey(mutator, nodeMap, dataMap, key1, key2, key3,
          node1, node2, node3, node4, node5);
    }

    static final CompactSetNode nodeOf(final AtomicReference<Thread> mutator, final int nodeMap,
        final int dataMap, final int key1, final int key2, final int key3,
        final CompactSetNode node1, final CompactSetNode node2, final CompactSetNode node3,
        final CompactSetNode node4, final CompactSetNode node5, final CompactSetNode node6) {
      // NOTE: reversed node argument list due to CHAMP encoding
      return nodeOf(mutator, nodeMap, dataMap,
          new Object[] {key1, key2, key3, node6, node5, node4, node3, node2, node1});
    }

    static final CompactSetNode nodeOf(final AtomicReference<Thread> mutator, final int nodeMap,
        final int dataMap, final int key1, final int key2, final int key3, final int key4) {
      return new Set4To0Node_5Bits_Spec0To8_IntKey(mutator, nodeMap, dataMap, key1, key2, key3,
          key4);
    }

    static final CompactSetNode nodeOf(final AtomicReference<Thread> mutator, final int nodeMap,
        final int dataMap, final int key1, final int key2, final int key3, final int key4,
        final CompactSetNode node1) {
      return new Set4To1Node_5Bits_Spec0To8_IntKey(mutator, nodeMap, dataMap, key1, key2, key3,
          key4, node1);
    }

    static final CompactSetNode nodeOf(final AtomicReference<Thread> mutator, final int nodeMap,
        final int dataMap, final int key1, final int key2, final int key3, final int key4,
        final CompactSetNode node1, final CompactSetNode node2) {
      return new Set4To2Node_5Bits_Spec0To8_IntKey(mutator, nodeMap, dataMap, key1, key2, key3,
          key4, node1, node2);
    }

    static final CompactSetNode nodeOf(final AtomicReference<Thread> mutator, final int nodeMap,
        final int dataMap, final int key1, final int key2, final int key3, final int key4,
        final CompactSetNode node1, final CompactSetNode node2, final CompactSetNode node3) {
      return new Set4To3Node_5Bits_Spec0To8_IntKey(mutator, nodeMap, dataMap, key1, key2, key3,
          key4, node1, node2, node3);
    }

    static final CompactSetNode nodeOf(final AtomicReference<Thread> mutator, final int nodeMap,
        final int dataMap, final int key1, final int key2, final int key3, final int key4,
        final CompactSetNode node1, final CompactSetNode node2, final CompactSetNode node3,
        final CompactSetNode node4) {
      return new Set4To4Node_5Bits_Spec0To8_IntKey(mutator, nodeMap, dataMap, key1, key2, key3,
          key4, node1, node2, node3, node4);
    }

    static final CompactSetNode nodeOf(final AtomicReference<Thread> mutator, final int nodeMap,
        final int dataMap, final int key1, final int key2, final int key3, final int key4,
        final CompactSetNode node1, final CompactSetNode node2, final CompactSetNode node3,
        final CompactSetNode node4, final CompactSetNode node5) {
      // NOTE: reversed node argument list due to CHAMP encoding
      return nodeOf(mutator, nodeMap, dataMap,
          new Object[] {key1, key2, key3, key4, node5, node4, node3, node2, node1});
    }

    static final CompactSetNode nodeOf(final AtomicReference<Thread> mutator, final int nodeMap,
        final int dataMap, final int key1, final int key2, final int key3, final int key4,
        final int key5) {
      return new Set5To0Node_5Bits_Spec0To8_IntKey(mutator, nodeMap, dataMap, key1, key2, key3,
          key4, key5);
    }

    static final CompactSetNode nodeOf(final AtomicReference<Thread> mutator, final int nodeMap,
        final int dataMap, final int key1, final int key2, final int key3, final int key4,
        final int key5, final CompactSetNode node1) {
      return new Set5To1Node_5Bits_Spec0To8_IntKey(mutator, nodeMap, dataMap, key1, key2, key3,
          key4, key5, node1);
    }

    static final CompactSetNode nodeOf(final AtomicReference<Thread> mutator, final int nodeMap,
        final int dataMap, final int key1, final int key2, final int key3, final int key4,
        final int key5, final CompactSetNode node1, final CompactSetNode node2) {
      return new Set5To2Node_5Bits_Spec0To8_IntKey(mutator, nodeMap, dataMap, key1, key2, key3,
          key4, key5, node1, node2);
    }

    static final CompactSetNode nodeOf(final AtomicReference<Thread> mutator, final int nodeMap,
        final int dataMap, final int key1, final int key2, final int key3, final int key4,
        final int key5, final CompactSetNode node1, final CompactSetNode node2,
        final CompactSetNode node3) {
      return new Set5To3Node_5Bits_Spec0To8_IntKey(mutator, nodeMap, dataMap, key1, key2, key3,
          key4, key5, node1, node2, node3);
    }

    static final CompactSetNode nodeOf(final AtomicReference<Thread> mutator, final int nodeMap,
        final int dataMap, final int key1, final int key2, final int key3, final int key4,
        final int key5, final CompactSetNode node1, final CompactSetNode node2,
        final CompactSetNode node3, final CompactSetNode node4) {
      // NOTE: reversed node argument list due to CHAMP encoding
      return nodeOf(mutator, nodeMap, dataMap,
          new Object[] {key1, key2, key3, key4, key5, node4, node3, node2, node1});
    }

    static final CompactSetNode nodeOf(final AtomicReference<Thread> mutator, final int nodeMap,
        final int dataMap, final int key1, final int key2, final int key3, final int key4,
        final int key5, final int key6) {
      return new Set6To0Node_5Bits_Spec0To8_IntKey(mutator, nodeMap, dataMap, key1, key2, key3,
          key4, key5, key6);
    }

    static final CompactSetNode nodeOf(final AtomicReference<Thread> mutator, final int nodeMap,
        final int dataMap, final int key1, final int key2, final int key3, final int key4,
        final int key5, final int key6, final CompactSetNode node1) {
      return new Set6To1Node_5Bits_Spec0To8_IntKey(mutator, nodeMap, dataMap, key1, key2, key3,
          key4, key5, key6, node1);
    }

    static final CompactSetNode nodeOf(final AtomicReference<Thread> mutator, final int nodeMap,
        final int dataMap, final int key1, final int key2, final int key3, final int key4,
        final int key5, final int key6, final CompactSetNode node1, final CompactSetNode node2) {
      return new Set6To2Node_5Bits_Spec0To8_IntKey(mutator, nodeMap, dataMap, key1, key2, key3,
          key4, key5, key6, node1, node2);
    }

    static final CompactSetNode nodeOf(final AtomicReference<Thread> mutator, final int nodeMap,
        final int dataMap, final int key1, final int key2, final int key3, final int key4,
        final int key5, final int key6, final CompactSetNode node1, final CompactSetNode node2,
        final CompactSetNode node3) {
      // NOTE: reversed node argument list due to CHAMP encoding
      return nodeOf(mutator, nodeMap, dataMap,
          new Object[] {key1, key2, key3, key4, key5, key6, node3, node2, node1});
    }

    static final CompactSetNode nodeOf(final AtomicReference<Thread> mutator, final int nodeMap,
        final int dataMap, final int key1, final int key2, final int key3, final int key4,
        final int key5, final int key6, final int key7) {
      return new Set7To0Node_5Bits_Spec0To8_IntKey(mutator, nodeMap, dataMap, key1, key2, key3,
          key4, key5, key6, key7);
    }

    static final CompactSetNode nodeOf(final AtomicReference<Thread> mutator, final int nodeMap,
        final int dataMap, final int key1, final int key2, final int key3, final int key4,
        final int key5, final int key6, final int key7, final CompactSetNode node1) {
      return new Set7To1Node_5Bits_Spec0To8_IntKey(mutator, nodeMap, dataMap, key1, key2, key3,
          key4, key5, key6, key7, node1);
    }

    static final CompactSetNode nodeOf(final AtomicReference<Thread> mutator, final int nodeMap,
        final int dataMap, final int key1, final int key2, final int key3, final int key4,
        final int key5, final int key6, final int key7, final CompactSetNode node1,
        final CompactSetNode node2) {
      // NOTE: reversed node argument list due to CHAMP encoding
      return nodeOf(mutator, nodeMap, dataMap,
          new Object[] {key1, key2, key3, key4, key5, key6, key7, node2, node1});
    }

    static final CompactSetNode nodeOf(final AtomicReference<Thread> mutator, final int nodeMap,
        final int dataMap, final int key1, final int key2, final int key3, final int key4,
        final int key5, final int key6, final int key7, final int key8) {
      return new Set8To0Node_5Bits_Spec0To8_IntKey(mutator, nodeMap, dataMap, key1, key2, key3,
          key4, key5, key6, key7, key8);
    }

    static final CompactSetNode nodeOf(final AtomicReference<Thread> mutator, final int nodeMap,
        final int dataMap, final int key1, final int key2, final int key3, final int key4,
        final int key5, final int key6, final int key7, final int key8,
        final CompactSetNode node1) {
      // NOTE: reversed node argument list due to CHAMP encoding
      return nodeOf(mutator, nodeMap, dataMap,
          new Object[] {key1, key2, key3, key4, key5, key6, key7, key8, node1});
    }

    static final CompactSetNode nodeOf(final AtomicReference<Thread> mutator, final int nodeMap,
        final int dataMap, final int key1, final int key2, final int key3, final int key4,
        final int key5, final int key6, final int key7, final int key8, final int key9) {
      return nodeOf(mutator, nodeMap, dataMap,
          new Object[] {key1, key2, key3, key4, key5, key6, key7, key8, key9});
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

    CompactSetNode nodeAt(final int bitpos) {
      return getNode(nodeIndex(bitpos));
    }

    boolean contains(final int key, final int keyHash, final int shift) {
      final int mask = mask(keyHash, shift);
      final int bitpos = bitpos(mask);

      final int dataMap = dataMap();
      if ((dataMap & bitpos) != 0) {
        final int index = index(dataMap, mask, bitpos);
        return getKey(index) == key;
      }

      final int nodeMap = nodeMap();
      if ((nodeMap & bitpos) != 0) {
        final int index = index(nodeMap, mask, bitpos);
        return getNode(index).contains(key, keyHash, shift + BIT_PARTITION_SIZE);
      }

      return false;
    }

    boolean contains(final int key, final int keyHash, final int shift,
        final Comparator<Object> cmp) {
      final int mask = mask(keyHash, shift);
      final int bitpos = bitpos(mask);

      final int dataMap = dataMap();
      if ((dataMap & bitpos) != 0) {
        final int index = index(dataMap, mask, bitpos);
        return getKey(index) == key;
      }

      final int nodeMap = nodeMap();
      if ((nodeMap & bitpos) != 0) {
        final int index = index(nodeMap, mask, bitpos);
        return getNode(index).contains(key, keyHash, shift + BIT_PARTITION_SIZE, cmp);
      }

      return false;
    }

    Optional<java.lang.Integer> findByKey(final int key, final int keyHash, final int shift) {
      final int mask = mask(keyHash, shift);
      final int bitpos = bitpos(mask);

      if ((dataMap() & bitpos) != 0) { // inplace value
        final int index = dataIndex(bitpos);
        if (getKey(index) == key) {
          return Optional.of(getKey(index));
        }

        return Optional.empty();
      }

      if ((nodeMap() & bitpos) != 0) { // node (not value)
        final AbstractSetNode subNode = nodeAt(bitpos);

        return subNode.findByKey(key, keyHash, shift + BIT_PARTITION_SIZE);
      }

      return Optional.empty();
    }

    Optional<java.lang.Integer> findByKey(final int key, final int keyHash, final int shift,
        final Comparator<Object> cmp) {
      final int mask = mask(keyHash, shift);
      final int bitpos = bitpos(mask);

      if ((dataMap() & bitpos) != 0) { // inplace value
        final int index = dataIndex(bitpos);
        if (getKey(index) == key) {
          return Optional.of(getKey(index));
        }

        return Optional.empty();
      }

      if ((nodeMap() & bitpos) != 0) { // node (not value)
        final AbstractSetNode subNode = nodeAt(bitpos);

        return subNode.findByKey(key, keyHash, shift + BIT_PARTITION_SIZE, cmp);
      }

      return Optional.empty();
    }

    CompactSetNode updated(final AtomicReference<Thread> mutator, final int key, final int keyHash,
        final int shift, final SetResult details) {
      final int mask = mask(keyHash, shift);
      final int bitpos = bitpos(mask);

      if ((dataMap() & bitpos) != 0) { // inplace value
        final int dataIndex = dataIndex(bitpos);
        final int currentKey = getKey(dataIndex);

        if (currentKey == key) {
          return this;
        } else {
          final CompactSetNode subNodeNew = mergeTwoKeyValPairs(currentKey,
              transformHashCode(currentKey), key, keyHash, shift + BIT_PARTITION_SIZE);

          details.modified();
          return copyAndMigrateFromInlineToNode(mutator, bitpos, subNodeNew);
        }
      } else if ((nodeMap() & bitpos) != 0) { // node (not value)
        final CompactSetNode subNode = nodeAt(bitpos);
        final CompactSetNode subNodeNew =
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

    CompactSetNode updated(final AtomicReference<Thread> mutator, final int key, final int keyHash,
        final int shift, final SetResult details, final Comparator<Object> cmp) {
      final int mask = mask(keyHash, shift);
      final int bitpos = bitpos(mask);

      if ((dataMap() & bitpos) != 0) { // inplace value
        final int dataIndex = dataIndex(bitpos);
        final int currentKey = getKey(dataIndex);

        if (currentKey == key) {
          return this;
        } else {
          final CompactSetNode subNodeNew = mergeTwoKeyValPairs(currentKey,
              transformHashCode(currentKey), key, keyHash, shift + BIT_PARTITION_SIZE);

          details.modified();
          return copyAndMigrateFromInlineToNode(mutator, bitpos, subNodeNew);
        }
      } else if ((nodeMap() & bitpos) != 0) { // node (not value)
        final CompactSetNode subNode = nodeAt(bitpos);
        final CompactSetNode subNodeNew =
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

    CompactSetNode removed(final AtomicReference<Thread> mutator, final int key, final int keyHash,
        final int shift, final SetResult details) {
      final int mask = mask(keyHash, shift);
      final int bitpos = bitpos(mask);

      if ((dataMap() & bitpos) != 0) { // inplace value
        final int dataIndex = dataIndex(bitpos);

        if (getKey(dataIndex) == key) {
          details.modified();

          if (this.payloadArity() == 2 && this.nodeArity() == 0) {
            /*
             * Create new node with remaining pair. The new node will a) either become the new root
             * returned, or b) unwrapped and inlined during returning.
             */
            final int newDataMap =
                (shift == 0) ? (int) (dataMap() ^ bitpos) : bitpos(mask(keyHash, 0));

            if (dataIndex == 0) {
              return CompactSetNode.nodeOf(mutator, (int) 0, newDataMap, getKey(1));
            } else {
              return CompactSetNode.nodeOf(mutator, (int) 0, newDataMap, getKey(0));
            }
          } else if (this.arity() == 9) {
            return removeInplaceValueAndConvertToSpecializedNode(mutator, bitpos);
          } else {
            return copyAndRemoveValue(mutator, bitpos);
          }
        } else {
          return this;
        }
      } else if ((nodeMap() & bitpos) != 0) { // node (not value)
        final CompactSetNode subNode = nodeAt(bitpos);
        final CompactSetNode subNodeNew =
            subNode.removed(mutator, key, keyHash, shift + BIT_PARTITION_SIZE, details);

        if (!details.isModified()) {
          return this;
        }

        switch (subNodeNew.sizePredicate()) {
          case 0: {
            throw new IllegalStateException("Sub-node must have at least one element.");
          }
          case 1: {
            // inline value (move to front)
            details.modified();
            return copyAndMigrateFromNodeToInline(mutator, bitpos, subNodeNew);
          }
          default: {
            // modify current node (set replacement node)
            return copyAndSetNode(mutator, bitpos, subNodeNew);
          }
        }
      }

      return this;
    }

    CompactSetNode removed(final AtomicReference<Thread> mutator, final int key, final int keyHash,
        final int shift, final SetResult details, final Comparator<Object> cmp) {
      final int mask = mask(keyHash, shift);
      final int bitpos = bitpos(mask);

      if ((dataMap() & bitpos) != 0) { // inplace value
        final int dataIndex = dataIndex(bitpos);

        if (getKey(dataIndex) == key) {
          details.modified();

          if (this.payloadArity() == 2 && this.nodeArity() == 0) {
            /*
             * Create new node with remaining pair. The new node will a) either become the new root
             * returned, or b) unwrapped and inlined during returning.
             */
            final int newDataMap =
                (shift == 0) ? (int) (dataMap() ^ bitpos) : bitpos(mask(keyHash, 0));

            if (dataIndex == 0) {
              return CompactSetNode.nodeOf(mutator, (int) 0, newDataMap, getKey(1));
            } else {
              return CompactSetNode.nodeOf(mutator, (int) 0, newDataMap, getKey(0));
            }
          } else if (this.arity() == 9) {
            return removeInplaceValueAndConvertToSpecializedNode(mutator, bitpos);
          } else {
            return copyAndRemoveValue(mutator, bitpos);
          }
        } else {
          return this;
        }
      } else if ((nodeMap() & bitpos) != 0) { // node (not value)
        final CompactSetNode subNode = nodeAt(bitpos);
        final CompactSetNode subNodeNew =
            subNode.removed(mutator, key, keyHash, shift + BIT_PARTITION_SIZE, details, cmp);

        if (!details.isModified()) {
          return this;
        }

        switch (subNodeNew.sizePredicate()) {
          case 0: {
            throw new IllegalStateException("Sub-node must have at least one element.");
          }
          case 1: {
            // inline value (move to front)
            details.modified();
            return copyAndMigrateFromNodeToInline(mutator, bitpos, subNodeNew);
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

        map = (int) (map >> 1);
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

  protected static abstract class CompactMixedSetNode extends CompactSetNode {

    private final int nodeMap;
    private final int dataMap;

    CompactMixedSetNode(final AtomicReference<Thread> mutator, final int nodeMap,
        final int dataMap) {
      this.nodeMap = nodeMap;
      this.dataMap = dataMap;
    }

    @Override
    public int nodeMap() {
      return nodeMap;
    }

    @Override
    public int dataMap() {
      return dataMap;
    }

  }

  protected static abstract class CompactNodesOnlySetNode extends CompactSetNode {

    private final int nodeMap;

    CompactNodesOnlySetNode(final AtomicReference<Thread> mutator, final int nodeMap,
        final int dataMap) {
      this.nodeMap = nodeMap;
    }

    @Override
    public int nodeMap() {
      return nodeMap;
    }

    @Override
    public int dataMap() {
      return 0;
    }

  }

  protected static abstract class CompactValuesOnlySetNode extends CompactSetNode {

    private final int dataMap;

    CompactValuesOnlySetNode(final AtomicReference<Thread> mutator, final int nodeMap,
        final int dataMap) {
      this.dataMap = dataMap;
    }

    @Override
    public int nodeMap() {
      return 0;
    }

    @Override
    public int dataMap() {
      return dataMap;
    }

  }

  protected static abstract class CompactEmptySetNode extends CompactSetNode {

    CompactEmptySetNode(final AtomicReference<Thread> mutator, final int nodeMap,
        final int dataMap) {}

    @Override
    public int nodeMap() {
      return 0;
    }

    @Override
    public int dataMap() {
      return 0;
    }

  }

  private static final class BitmapIndexedSetNode extends CompactMixedSetNode {

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
      }

      assert arity() > 8;
      assert nodeInvariant();
    }

    @Override
    int getKey(final int index) {
      return (int) nodes[TUPLE_LENGTH * index];
    }

    @SuppressWarnings("unchecked")
    @Override
    CompactSetNode getNode(final int index) {
      return (CompactSetNode) nodes[nodes.length - 1 - index];
    }

    @Override
    boolean hasPayload() {
      return dataMap() != 0;
    }

    @Override
    int payloadArity() {
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
    public int hashCode() {
      final int prime = 31;
      int result = 0;
      result = prime * result + ((int) dataMap());
      result = prime * result + ((int) dataMap());
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
      BitmapIndexedSetNode that = (BitmapIndexedSetNode) other;
      if (nodeMap() != that.nodeMap()) {
        return false;
      }
      if (dataMap() != that.dataMap()) {
        return false;
      }
      if (!Arrays.equals(nodes, that.nodes)) {
        return false;
      }
      return true;
    }

    @Override
    byte sizePredicate() {
      return SIZE_MORE_THAN_ONE;
    }

    @Override
    CompactSetNode copyAndSetNode(final AtomicReference<Thread> mutator, final int bitpos,
        final CompactSetNode node) {

      final int idx = this.nodes.length - 1 - nodeIndex(bitpos);

      if (isAllowedToEdit(this.mutator, mutator)) {
        // no copying if already editable
        this.nodes[idx] = node;
        return this;
      } else {
        final Object[] src = this.nodes;
        final Object[] dst = (Object[]) new Object[src.length];

        // copy 'src' and set 1 element(s) at position 'idx'
        System.arraycopy(src, 0, dst, 0, src.length);
        dst[idx + 0] = node;

        return nodeOf(mutator, nodeMap(), dataMap(), dst);
      }
    }

    @Override
    CompactSetNode copyAndInsertValue(final AtomicReference<Thread> mutator, final int bitpos,
        final int key) {
      final int idx = TUPLE_LENGTH * dataIndex(bitpos);

      final Object[] src = this.nodes;
      final Object[] dst = (Object[]) new Object[src.length + 1];

      // copy 'src' and insert 1 element(s) at position 'idx'
      System.arraycopy(src, 0, dst, 0, idx);
      dst[idx + 0] = key;
      System.arraycopy(src, idx, dst, idx + 1, src.length - idx);

      return nodeOf(mutator, nodeMap(), (int) (dataMap() | bitpos), dst);
    }

    @Override
    CompactSetNode copyAndRemoveValue(final AtomicReference<Thread> mutator, final int bitpos) {
      final int idx = TUPLE_LENGTH * dataIndex(bitpos);

      final Object[] src = this.nodes;
      final Object[] dst = (Object[]) new Object[src.length - 1];

      // copy 'src' and remove 1 element(s) at position 'idx'
      System.arraycopy(src, 0, dst, 0, idx);
      System.arraycopy(src, idx + 1, dst, idx, src.length - idx - 1);

      return nodeOf(mutator, nodeMap(), (int) (dataMap() ^ bitpos), dst);
    }

    @Override
    CompactSetNode copyAndMigrateFromInlineToNode(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetNode node) {

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

      return nodeOf(mutator, (int) (nodeMap() | bitpos), (int) (dataMap() ^ bitpos), dst);
    }

    @Override
    CompactSetNode copyAndMigrateFromNodeToInline(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetNode node) {

      final int idxOld = this.nodes.length - 1 - nodeIndex(bitpos);
      final int idxNew = dataIndex(bitpos);

      final Object[] src = this.nodes;
      final Object[] dst = new Object[src.length - 1 + 1];

      // copy 'src' and remove 1 element(s) at position 'idxOld' and
      // insert 1 element(s) at position 'idxNew' (TODO: carefully test)
      assert idxOld >= idxNew;
      System.arraycopy(src, 0, dst, 0, idxNew);
      dst[idxNew + 0] = node.getKey(0);
      System.arraycopy(src, idxNew, dst, idxNew + 1, idxOld - idxNew);
      System.arraycopy(src, idxOld + 1, dst, idxOld + 1, src.length - idxOld - 1);

      return nodeOf(mutator, (int) (nodeMap() ^ bitpos), (int) (dataMap() | bitpos), dst);
    }

    @Override
    CompactSetNode removeInplaceValueAndConvertToSpecializedNode(
        final AtomicReference<Thread> mutator, final int bitpos) {
      final int valIndex = dataIndex(bitpos);

      final int nodeMap = (int) (this.nodeMap());
      final int dataMap = (int) (this.dataMap() ^ bitpos);

      switch (payloadArity()) { // 0 <= payloadArity <= 9 // or ts.nMax
        case 1: {

          switch (valIndex) {
            case 0: {

              break;
            }
            default:
              throw new IllegalStateException("Index out of range.");
          }

          final CompactSetNode node1 = getNode(0);
          final CompactSetNode node2 = getNode(1);
          final CompactSetNode node3 = getNode(2);
          final CompactSetNode node4 = getNode(3);
          final CompactSetNode node5 = getNode(4);
          final CompactSetNode node6 = getNode(5);
          final CompactSetNode node7 = getNode(6);
          final CompactSetNode node8 = getNode(7);

          return nodeOf(mutator, nodeMap, dataMap, node1, node2, node3, node4, node5, node6, node7,
              node8);

        }
        case 2: {
          int key1;

          switch (valIndex) {
            case 0: {

              key1 = getKey(1);

              break;
            }
            case 1: {

              key1 = getKey(0);

              break;
            }
            default:
              throw new IllegalStateException("Index out of range.");
          }

          final CompactSetNode node1 = getNode(0);
          final CompactSetNode node2 = getNode(1);
          final CompactSetNode node3 = getNode(2);
          final CompactSetNode node4 = getNode(3);
          final CompactSetNode node5 = getNode(4);
          final CompactSetNode node6 = getNode(5);
          final CompactSetNode node7 = getNode(6);

          return nodeOf(mutator, nodeMap, dataMap, key1, node1, node2, node3, node4, node5, node6,
              node7);

        }
        case 3: {
          int key1;
          int key2;

          switch (valIndex) {
            case 0: {

              key1 = getKey(1);

              key2 = getKey(2);

              break;
            }
            case 1: {

              key1 = getKey(0);

              key2 = getKey(2);

              break;
            }
            case 2: {

              key1 = getKey(0);

              key2 = getKey(1);

              break;
            }
            default:
              throw new IllegalStateException("Index out of range.");
          }

          final CompactSetNode node1 = getNode(0);
          final CompactSetNode node2 = getNode(1);
          final CompactSetNode node3 = getNode(2);
          final CompactSetNode node4 = getNode(3);
          final CompactSetNode node5 = getNode(4);
          final CompactSetNode node6 = getNode(5);

          return nodeOf(mutator, nodeMap, dataMap, key1, key2, node1, node2, node3, node4, node5,
              node6);

        }
        case 4: {
          int key1;
          int key2;
          int key3;

          switch (valIndex) {
            case 0: {

              key1 = getKey(1);

              key2 = getKey(2);

              key3 = getKey(3);

              break;
            }
            case 1: {

              key1 = getKey(0);

              key2 = getKey(2);

              key3 = getKey(3);

              break;
            }
            case 2: {

              key1 = getKey(0);

              key2 = getKey(1);

              key3 = getKey(3);

              break;
            }
            case 3: {

              key1 = getKey(0);

              key2 = getKey(1);

              key3 = getKey(2);

              break;
            }
            default:
              throw new IllegalStateException("Index out of range.");
          }

          final CompactSetNode node1 = getNode(0);
          final CompactSetNode node2 = getNode(1);
          final CompactSetNode node3 = getNode(2);
          final CompactSetNode node4 = getNode(3);
          final CompactSetNode node5 = getNode(4);

          return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, node1, node2, node3, node4,
              node5);

        }
        case 5: {
          int key1;
          int key2;
          int key3;
          int key4;

          switch (valIndex) {
            case 0: {

              key1 = getKey(1);

              key2 = getKey(2);

              key3 = getKey(3);

              key4 = getKey(4);

              break;
            }
            case 1: {

              key1 = getKey(0);

              key2 = getKey(2);

              key3 = getKey(3);

              key4 = getKey(4);

              break;
            }
            case 2: {

              key1 = getKey(0);

              key2 = getKey(1);

              key3 = getKey(3);

              key4 = getKey(4);

              break;
            }
            case 3: {

              key1 = getKey(0);

              key2 = getKey(1);

              key3 = getKey(2);

              key4 = getKey(4);

              break;
            }
            case 4: {

              key1 = getKey(0);

              key2 = getKey(1);

              key3 = getKey(2);

              key4 = getKey(3);

              break;
            }
            default:
              throw new IllegalStateException("Index out of range.");
          }

          final CompactSetNode node1 = getNode(0);
          final CompactSetNode node2 = getNode(1);
          final CompactSetNode node3 = getNode(2);
          final CompactSetNode node4 = getNode(3);

          return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key4, node1, node2, node3,
              node4);

        }
        case 6: {
          int key1;
          int key2;
          int key3;
          int key4;
          int key5;

          switch (valIndex) {
            case 0: {

              key1 = getKey(1);

              key2 = getKey(2);

              key3 = getKey(3);

              key4 = getKey(4);

              key5 = getKey(5);

              break;
            }
            case 1: {

              key1 = getKey(0);

              key2 = getKey(2);

              key3 = getKey(3);

              key4 = getKey(4);

              key5 = getKey(5);

              break;
            }
            case 2: {

              key1 = getKey(0);

              key2 = getKey(1);

              key3 = getKey(3);

              key4 = getKey(4);

              key5 = getKey(5);

              break;
            }
            case 3: {

              key1 = getKey(0);

              key2 = getKey(1);

              key3 = getKey(2);

              key4 = getKey(4);

              key5 = getKey(5);

              break;
            }
            case 4: {

              key1 = getKey(0);

              key2 = getKey(1);

              key3 = getKey(2);

              key4 = getKey(3);

              key5 = getKey(5);

              break;
            }
            case 5: {

              key1 = getKey(0);

              key2 = getKey(1);

              key3 = getKey(2);

              key4 = getKey(3);

              key5 = getKey(4);

              break;
            }
            default:
              throw new IllegalStateException("Index out of range.");
          }

          final CompactSetNode node1 = getNode(0);
          final CompactSetNode node2 = getNode(1);
          final CompactSetNode node3 = getNode(2);

          return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key4, key5, node1, node2,
              node3);

        }
        case 7: {
          int key1;
          int key2;
          int key3;
          int key4;
          int key5;
          int key6;

          switch (valIndex) {
            case 0: {

              key1 = getKey(1);

              key2 = getKey(2);

              key3 = getKey(3);

              key4 = getKey(4);

              key5 = getKey(5);

              key6 = getKey(6);

              break;
            }
            case 1: {

              key1 = getKey(0);

              key2 = getKey(2);

              key3 = getKey(3);

              key4 = getKey(4);

              key5 = getKey(5);

              key6 = getKey(6);

              break;
            }
            case 2: {

              key1 = getKey(0);

              key2 = getKey(1);

              key3 = getKey(3);

              key4 = getKey(4);

              key5 = getKey(5);

              key6 = getKey(6);

              break;
            }
            case 3: {

              key1 = getKey(0);

              key2 = getKey(1);

              key3 = getKey(2);

              key4 = getKey(4);

              key5 = getKey(5);

              key6 = getKey(6);

              break;
            }
            case 4: {

              key1 = getKey(0);

              key2 = getKey(1);

              key3 = getKey(2);

              key4 = getKey(3);

              key5 = getKey(5);

              key6 = getKey(6);

              break;
            }
            case 5: {

              key1 = getKey(0);

              key2 = getKey(1);

              key3 = getKey(2);

              key4 = getKey(3);

              key5 = getKey(4);

              key6 = getKey(6);

              break;
            }
            case 6: {

              key1 = getKey(0);

              key2 = getKey(1);

              key3 = getKey(2);

              key4 = getKey(3);

              key5 = getKey(4);

              key6 = getKey(5);

              break;
            }
            default:
              throw new IllegalStateException("Index out of range.");
          }

          final CompactSetNode node1 = getNode(0);
          final CompactSetNode node2 = getNode(1);

          return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key4, key5, key6, node1,
              node2);

        }
        case 8: {
          int key1;
          int key2;
          int key3;
          int key4;
          int key5;
          int key6;
          int key7;

          switch (valIndex) {
            case 0: {

              key1 = getKey(1);

              key2 = getKey(2);

              key3 = getKey(3);

              key4 = getKey(4);

              key5 = getKey(5);

              key6 = getKey(6);

              key7 = getKey(7);

              break;
            }
            case 1: {

              key1 = getKey(0);

              key2 = getKey(2);

              key3 = getKey(3);

              key4 = getKey(4);

              key5 = getKey(5);

              key6 = getKey(6);

              key7 = getKey(7);

              break;
            }
            case 2: {

              key1 = getKey(0);

              key2 = getKey(1);

              key3 = getKey(3);

              key4 = getKey(4);

              key5 = getKey(5);

              key6 = getKey(6);

              key7 = getKey(7);

              break;
            }
            case 3: {

              key1 = getKey(0);

              key2 = getKey(1);

              key3 = getKey(2);

              key4 = getKey(4);

              key5 = getKey(5);

              key6 = getKey(6);

              key7 = getKey(7);

              break;
            }
            case 4: {

              key1 = getKey(0);

              key2 = getKey(1);

              key3 = getKey(2);

              key4 = getKey(3);

              key5 = getKey(5);

              key6 = getKey(6);

              key7 = getKey(7);

              break;
            }
            case 5: {

              key1 = getKey(0);

              key2 = getKey(1);

              key3 = getKey(2);

              key4 = getKey(3);

              key5 = getKey(4);

              key6 = getKey(6);

              key7 = getKey(7);

              break;
            }
            case 6: {

              key1 = getKey(0);

              key2 = getKey(1);

              key3 = getKey(2);

              key4 = getKey(3);

              key5 = getKey(4);

              key6 = getKey(5);

              key7 = getKey(7);

              break;
            }
            case 7: {

              key1 = getKey(0);

              key2 = getKey(1);

              key3 = getKey(2);

              key4 = getKey(3);

              key5 = getKey(4);

              key6 = getKey(5);

              key7 = getKey(6);

              break;
            }
            default:
              throw new IllegalStateException("Index out of range.");
          }

          final CompactSetNode node1 = getNode(0);

          return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key4, key5, key6, key7, node1);

        }
        case 9: {
          int key1;
          int key2;
          int key3;
          int key4;
          int key5;
          int key6;
          int key7;
          int key8;

          switch (valIndex) {
            case 0: {

              key1 = getKey(1);

              key2 = getKey(2);

              key3 = getKey(3);

              key4 = getKey(4);

              key5 = getKey(5);

              key6 = getKey(6);

              key7 = getKey(7);

              key8 = getKey(8);

              break;
            }
            case 1: {

              key1 = getKey(0);

              key2 = getKey(2);

              key3 = getKey(3);

              key4 = getKey(4);

              key5 = getKey(5);

              key6 = getKey(6);

              key7 = getKey(7);

              key8 = getKey(8);

              break;
            }
            case 2: {

              key1 = getKey(0);

              key2 = getKey(1);

              key3 = getKey(3);

              key4 = getKey(4);

              key5 = getKey(5);

              key6 = getKey(6);

              key7 = getKey(7);

              key8 = getKey(8);

              break;
            }
            case 3: {

              key1 = getKey(0);

              key2 = getKey(1);

              key3 = getKey(2);

              key4 = getKey(4);

              key5 = getKey(5);

              key6 = getKey(6);

              key7 = getKey(7);

              key8 = getKey(8);

              break;
            }
            case 4: {

              key1 = getKey(0);

              key2 = getKey(1);

              key3 = getKey(2);

              key4 = getKey(3);

              key5 = getKey(5);

              key6 = getKey(6);

              key7 = getKey(7);

              key8 = getKey(8);

              break;
            }
            case 5: {

              key1 = getKey(0);

              key2 = getKey(1);

              key3 = getKey(2);

              key4 = getKey(3);

              key5 = getKey(4);

              key6 = getKey(6);

              key7 = getKey(7);

              key8 = getKey(8);

              break;
            }
            case 6: {

              key1 = getKey(0);

              key2 = getKey(1);

              key3 = getKey(2);

              key4 = getKey(3);

              key5 = getKey(4);

              key6 = getKey(5);

              key7 = getKey(7);

              key8 = getKey(8);

              break;
            }
            case 7: {

              key1 = getKey(0);

              key2 = getKey(1);

              key3 = getKey(2);

              key4 = getKey(3);

              key5 = getKey(4);

              key6 = getKey(5);

              key7 = getKey(6);

              key8 = getKey(8);

              break;
            }
            case 8: {

              key1 = getKey(0);

              key2 = getKey(1);

              key3 = getKey(2);

              key4 = getKey(3);

              key5 = getKey(4);

              key6 = getKey(5);

              key7 = getKey(6);

              key8 = getKey(7);

              break;
            }
            default:
              throw new IllegalStateException("Index out of range.");
          }

          return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key4, key5, key6, key7, key8);

        }
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }
  }

  private static final class HashCollisionSetNode_5Bits_Spec0To8_IntKey extends CompactSetNode {
    private final int[] keys;

    private final int hash;

    HashCollisionSetNode_5Bits_Spec0To8_IntKey(final int hash, final int[] keys) {
      this.keys = keys;

      this.hash = hash;

      assert payloadArity() >= 2;
    }

    boolean contains(final int key, final int keyHash, final int shift) {
      if (this.hash == keyHash) {
        for (int k : keys) {
          if (k == key) {
            return true;
          }
        }
      }
      return false;
    }

    boolean contains(final int key, final int keyHash, final int shift,
        final Comparator<Object> cmp) {
      if (this.hash == keyHash) {
        for (int k : keys) {
          if (k == key) {
            return true;
          }
        }
      }
      return false;
    }

    Optional<java.lang.Integer> findByKey(final int key, final int keyHash, final int shift) {
      for (int i = 0; i < keys.length; i++) {
        final int _key = keys[i];
        if (key == _key) {
          return Optional.of(_key);
        }
      }
      return Optional.empty();
    }

    Optional<java.lang.Integer> findByKey(final int key, final int keyHash, final int shift,
        final Comparator<Object> cmp) {
      for (int i = 0; i < keys.length; i++) {
        final int _key = keys[i];
        if (key == _key) {
          return Optional.of(_key);
        }
      }
      return Optional.empty();
    }

    CompactSetNode updated(final AtomicReference<Thread> mutator, final int key, final int keyHash,
        final int shift, final SetResult details) {
      assert this.hash == keyHash;

      for (int idx = 0; idx < keys.length; idx++) {
        if (keys[idx] == key) {
          return this;
        }
      }

      final int[] keysNew = new int[this.keys.length + 1];

      // copy 'this.keys' and insert 1 element(s) at position
      // 'keys.length'
      System.arraycopy(this.keys, 0, keysNew, 0, keys.length);
      keysNew[keys.length + 0] = key;
      System.arraycopy(this.keys, keys.length, keysNew, keys.length + 1,
          this.keys.length - keys.length);

      details.modified();
      return new HashCollisionSetNode_5Bits_Spec0To8_IntKey(keyHash, keysNew);
    }

    CompactSetNode updated(final AtomicReference<Thread> mutator, final int key, final int keyHash,
        final int shift, final SetResult details, final Comparator<Object> cmp) {
      assert this.hash == keyHash;

      for (int idx = 0; idx < keys.length; idx++) {
        if (keys[idx] == key) {
          return this;
        }
      }

      final int[] keysNew = new int[this.keys.length + 1];

      // copy 'this.keys' and insert 1 element(s) at position
      // 'keys.length'
      System.arraycopy(this.keys, 0, keysNew, 0, keys.length);
      keysNew[keys.length + 0] = key;
      System.arraycopy(this.keys, keys.length, keysNew, keys.length + 1,
          this.keys.length - keys.length);

      details.modified();
      return new HashCollisionSetNode_5Bits_Spec0To8_IntKey(keyHash, keysNew);
    }

    CompactSetNode removed(final AtomicReference<Thread> mutator, final int key, final int keyHash,
        final int shift, final SetResult details) {
      for (int idx = 0; idx < keys.length; idx++) {
        if (keys[idx] == key) {
          details.modified();

          if (this.arity() == 1) {
            return nodeOf(mutator);
          } else if (this.arity() == 2) {
            /*
             * Create root node with singleton element. This node will be a) either be the new root
             * returned, or b) unwrapped and inlined.
             */
            final int theOtherKey = (idx == 0) ? keys[1] : keys[0];

            return CompactSetNode.nodeOf(mutator).updated(mutator, theOtherKey, keyHash, 0,
                details);
          } else {
            final int[] keysNew = new int[this.keys.length - 1];

            // copy 'this.keys' and remove 1 element(s) at position
            // 'idx'
            System.arraycopy(this.keys, 0, keysNew, 0, idx);
            System.arraycopy(this.keys, idx + 1, keysNew, idx, this.keys.length - idx - 1);

            return new HashCollisionSetNode_5Bits_Spec0To8_IntKey(keyHash, keysNew);
          }
        }
      }
      return this;
    }

    CompactSetNode removed(final AtomicReference<Thread> mutator, final int key, final int keyHash,
        final int shift, final SetResult details, final Comparator<Object> cmp) {
      for (int idx = 0; idx < keys.length; idx++) {
        if (keys[idx] == key) {
          details.modified();

          if (this.arity() == 1) {
            return nodeOf(mutator);
          } else if (this.arity() == 2) {
            /*
             * Create root node with singleton element. This node will be a) either be the new root
             * returned, or b) unwrapped and inlined.
             */
            final int theOtherKey = (idx == 0) ? keys[1] : keys[0];

            return CompactSetNode.nodeOf(mutator).updated(mutator, theOtherKey, keyHash, 0, details,
                cmp);
          } else {
            final int[] keysNew = new int[this.keys.length - 1];

            // copy 'this.keys' and remove 1 element(s) at position
            // 'idx'
            System.arraycopy(this.keys, 0, keysNew, 0, idx);
            System.arraycopy(this.keys, idx + 1, keysNew, idx, this.keys.length - idx - 1);

            return new HashCollisionSetNode_5Bits_Spec0To8_IntKey(keyHash, keysNew);
          }
        }
      }
      return this;
    }

    @Override
    boolean hasPayload() {
      return true;
    }

    @Override
    int payloadArity() {
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
    byte sizePredicate() {
      return SIZE_MORE_THAN_ONE;
    }

    @Override
    int getKey(final int index) {
      return keys[index];
    }

    @Override
    public CompactSetNode getNode(int index) {
      throw new IllegalStateException("Is leaf node.");
    }

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

      HashCollisionSetNode_5Bits_Spec0To8_IntKey that =
          (HashCollisionSetNode_5Bits_Spec0To8_IntKey) other;

      if (hash != that.hash) {
        return false;
      }

      if (arity() != that.arity()) {
        return false;
      }

      /*
       * Linear scan for each key, because of arbitrary element order.
       */
      outerLoop: for (int i = 0; i < that.payloadArity(); i++) {
        final int otherKey = that.getKey(i);

        for (int j = 0; j < keys.length; j++) {
          final int key = keys[j];

          if (key == otherKey) {
            continue outerLoop;
          }
        }
        return false;

      }

      return true;
    }

    @Override
    CompactSetNode copyAndInsertValue(final AtomicReference<Thread> mutator, final int bitpos,
        final int key) {
      throw new UnsupportedOperationException();
    }

    @Override
    CompactSetNode copyAndRemoveValue(final AtomicReference<Thread> mutator, final int bitpos) {
      throw new UnsupportedOperationException();
    }

    @Override
    CompactSetNode copyAndSetNode(final AtomicReference<Thread> mutator, final int bitpos,
        final CompactSetNode node) {
      throw new UnsupportedOperationException();
    }

    @Override
    CompactSetNode copyAndMigrateFromInlineToNode(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetNode node) {
      throw new UnsupportedOperationException();
    }

    @Override
    CompactSetNode copyAndMigrateFromNodeToInline(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetNode node) {
      throw new UnsupportedOperationException();
    }

    @Override
    CompactSetNode removeInplaceValueAndConvertToSpecializedNode(
        final AtomicReference<Thread> mutator, final int bitpos) {
      throw new UnsupportedOperationException();
    }

    @Override
    int nodeMap() {
      throw new UnsupportedOperationException();
    }

    @Override
    int dataMap() {
      throw new UnsupportedOperationException();
    }

  }

  /**
   * Iterator skeleton that uses a fixed stack in depth.
   */
  private static abstract class AbstractSetIterator {

    private static final int MAX_DEPTH = 7;

    protected int currentValueCursor;
    protected int currentValueLength;
    protected AbstractSetNode currentValueNode;

    private int currentStackLevel = -1;
    private final int[] nodeCursorsAndLengths = new int[MAX_DEPTH * 2];

    @SuppressWarnings("unchecked")
    AbstractSetNode[] nodes = new AbstractSetNode[MAX_DEPTH];

    AbstractSetIterator(AbstractSetNode rootNode) {
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
          final AbstractSetNode nextNode = nodes[currentStackLevel].getNode(nodeCursor);
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

  protected static class SetKeyIterator extends AbstractSetIterator
      implements Iterator<java.lang.Integer> {

    SetKeyIterator(AbstractSetNode rootNode) {
      super(rootNode);
    }

    @Override
    public java.lang.Integer next() {
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
  private static class TrieSet_5Bits_Spec0To8_IntKeyNodeIterator
      implements Iterator<AbstractSetNode> {

    final Deque<Iterator<? extends AbstractSetNode>> nodeIteratorStack;

    TrieSet_5Bits_Spec0To8_IntKeyNodeIterator(AbstractSetNode rootNode) {
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
    public AbstractSetNode next() {
      if (!hasNext()) {
        throw new NoSuchElementException();
      }

      AbstractSetNode innerNode = nodeIteratorStack.peek().next();

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

  static final class TransientTrieSet_5Bits_Spec0To8_IntKey
      implements io.usethesource.capsule.Set.Transient<Integer> {
    final private AtomicReference<Thread> mutator;
    private AbstractSetNode rootNode;
    private int hashCode;
    private int cachedSize;

    TransientTrieSet_5Bits_Spec0To8_IntKey(
        TrieSet_5Bits_Spec0To8_IntKey trieSet_5Bits_Spec0To8_IntKey) {
      this.mutator = new AtomicReference<Thread>(Thread.currentThread());
      this.rootNode = trieSet_5Bits_Spec0To8_IntKey.rootNode;
      this.hashCode = trieSet_5Bits_Spec0To8_IntKey.hashCode;
      this.cachedSize = trieSet_5Bits_Spec0To8_IntKey.cachedSize;
      if (DEBUG) {
        assert checkHashCodeAndSize(hashCode, cachedSize);
      }
    }

    private boolean checkHashCodeAndSize(final int targetHash, final int targetSize) {
      int hash = 0;
      int size = 0;

      for (Iterator<java.lang.Integer> it = keyIterator(); it.hasNext();) {
        final int key = it.next();

        hash += (int) key;
        size += 1;
      }

      return hash == targetHash && size == targetSize;
    }

    public boolean add(final java.lang.Integer key) {
      throw new UnsupportedOperationException();
    }

    public boolean addAll(final Collection<? extends java.lang.Integer> c) {
      throw new UnsupportedOperationException();
    }

    public void clear() {
      throw new UnsupportedOperationException();
    }

    public boolean remove(final Object key) {
      throw new UnsupportedOperationException();
    }

    public boolean removeAll(final Collection<?> c) {
      throw new UnsupportedOperationException();
    }

    public boolean retainAll(final Collection<?> c) {
      throw new UnsupportedOperationException();
    }

    public boolean contains(final Object o) {
      try {
        @SuppressWarnings("unchecked")
        final int key = (int) o;
        return rootNode.contains(key, transformHashCode(key), 0);
      } catch (ClassCastException unused) {
        return false;
      }
    }

    public boolean containsEquivalent(final Object o, final Comparator<Object> cmp) {
      try {
        @SuppressWarnings("unchecked")
        final int key = (int) o;
        return rootNode.contains(key, transformHashCode(key), 0, cmp);
      } catch (ClassCastException unused) {
        return false;
      }
    }

    public java.lang.Integer get(final Object o) {
      try {
        @SuppressWarnings("unchecked")
        final int key = (int) o;
        final Optional<java.lang.Integer> result =
            rootNode.findByKey(key, transformHashCode(key), 0);

        if (result.isPresent()) {
          return result.get();
        } else {
          return null;
        }
      } catch (ClassCastException unused) {
        return null;
      }
    }

    public java.lang.Integer getEquivalent(final Object o, final Comparator<Object> cmp) {
      try {
        @SuppressWarnings("unchecked")
        final int key = (int) o;
        final Optional<java.lang.Integer> result =
            rootNode.findByKey(key, transformHashCode(key), 0, cmp);

        if (result.isPresent()) {
          return result.get();
        } else {
          return null;
        }
      } catch (ClassCastException unused) {
        return null;
      }
    }

    public boolean __insert(final java.lang.Integer key) {
      if (mutator.get() == null) {
        throw new IllegalStateException("Transient already frozen.");
      }

      final int keyHash = key.hashCode();
      final SetResult details = SetResult.unchanged();

      final CompactSetNode newRootNode =
          rootNode.updated(mutator, key, transformHashCode(keyHash), 0, details);

      if (details.isModified()) {

        rootNode = newRootNode;
        hashCode += keyHash;
        cachedSize += 1;

        if (DEBUG) {
          assert checkHashCodeAndSize(hashCode, cachedSize);
        }
        return true;

      }

      if (DEBUG) {
        assert checkHashCodeAndSize(hashCode, cachedSize);
      }
      return false;
    }

    public boolean __insertEquivalent(final java.lang.Integer key, final Comparator<Object> cmp) {
      if (mutator.get() == null) {
        throw new IllegalStateException("Transient already frozen.");
      }

      final int keyHash = key.hashCode();
      final SetResult details = SetResult.unchanged();

      final CompactSetNode newRootNode =
          rootNode.updated(mutator, key, transformHashCode(keyHash), 0, details, cmp);

      if (details.isModified()) {

        rootNode = newRootNode;
        hashCode += keyHash;
        cachedSize += 1;

        if (DEBUG) {
          assert checkHashCodeAndSize(hashCode, cachedSize);
        }
        return true;

      }

      if (DEBUG) {
        assert checkHashCodeAndSize(hashCode, cachedSize);
      }
      return false;
    }

    public boolean __insertAll(final Set<? extends java.lang.Integer> set) {
      boolean modified = false;

      for (final int key : set) {
        modified |= this.__insert(key);
      }

      return modified;
    }

    public boolean __insertAllEquivalent(final Set<? extends java.lang.Integer> set,
        final Comparator<Object> cmp) {
      boolean modified = false;

      for (final int key : set) {
        modified |= this.__insertEquivalent(key, cmp);
      }

      return modified;
    }

    public boolean __remove(final java.lang.Integer key) {
      if (mutator.get() == null) {
        throw new IllegalStateException("Transient already frozen.");
      }

      final int keyHash = key.hashCode();
      final SetResult details = SetResult.unchanged();

      final CompactSetNode newRootNode =
          rootNode.removed(mutator, key, transformHashCode(keyHash), 0, details);

      if (details.isModified()) {
        rootNode = newRootNode;
        hashCode = hashCode - keyHash;
        cachedSize = cachedSize - 1;

        if (DEBUG) {
          assert checkHashCodeAndSize(hashCode, cachedSize);
        }
        return true;
      }

      if (DEBUG) {
        assert checkHashCodeAndSize(hashCode, cachedSize);
      }

      return false;
    }

    public boolean __removeEquivalent(final java.lang.Integer key, final Comparator<Object> cmp) {
      if (mutator.get() == null) {
        throw new IllegalStateException("Transient already frozen.");
      }

      final int keyHash = key.hashCode();
      final SetResult details = SetResult.unchanged();

      final CompactSetNode newRootNode =
          rootNode.removed(mutator, key, transformHashCode(keyHash), 0, details, cmp);

      if (details.isModified()) {
        rootNode = newRootNode;
        hashCode = hashCode - keyHash;
        cachedSize = cachedSize - 1;

        if (DEBUG) {
          assert checkHashCodeAndSize(hashCode, cachedSize);
        }
        return true;
      }

      if (DEBUG) {
        assert checkHashCodeAndSize(hashCode, cachedSize);
      }

      return false;
    }

    public boolean __removeAll(final Set<? extends java.lang.Integer> set) {
      boolean modified = false;

      for (final int key : set) {
        modified |= this.__remove(key);
      }

      return modified;
    }

    public boolean __removeAllEquivalent(final Set<? extends java.lang.Integer> set,
        final Comparator<Object> cmp) {
      boolean modified = false;

      for (final int key : set) {
        modified |= this.__removeEquivalent(key, cmp);
      }

      return modified;
    }

    public boolean __retainAll(final Set<? extends java.lang.Integer> set) {
      boolean modified = false;

      Iterator<java.lang.Integer> thisIterator = iterator();
      while (thisIterator.hasNext()) {
        if (!set.contains(thisIterator.next())) {
          thisIterator.remove();
          modified = true;
        }
      }

      return modified;
    }

    public boolean __retainAllEquivalent(
        final io.usethesource.capsule.Set.Transient<? extends Integer> transientSet,
        final Comparator<Object> cmp) {
      boolean modified = false;

      Iterator<java.lang.Integer> thisIterator = iterator();
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

    public int size() {
      return cachedSize;
    }

    public boolean isEmpty() {
      return cachedSize == 0;
    }

    public Iterator<java.lang.Integer> iterator() {
      return keyIterator();
    }

    public Iterator<java.lang.Integer> keyIterator() {
      return new TransientSetKeyIterator(this);
    }

    public static class TransientSetKeyIterator extends SetKeyIterator {
      final TransientTrieSet_5Bits_Spec0To8_IntKey collection;
      int lastKey;

      public TransientSetKeyIterator(final TransientTrieSet_5Bits_Spec0To8_IntKey collection) {
        super(collection.rootNode);
        this.collection = collection;
      }

      public java.lang.Integer next() {
        return lastKey = super.next();
      }

      public void remove() {
        // TODO: test removal at iteration rigorously
        collection.__remove(lastKey);
      }
    }

    @Override
    public Object[] toArray() {
      Object[] array = new Object[cachedSize];

      int idx = 0;
      for (java.lang.Integer key : this) {
        array[idx++] = key;
      }

      return array;
    }

    @Override
    public <T> T[] toArray(final T[] a) {
      List<java.lang.Integer> list = new ArrayList<java.lang.Integer>(cachedSize);

      for (java.lang.Integer key : this) {
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

      if (other instanceof TransientTrieSet_5Bits_Spec0To8_IntKey) {
        TransientTrieSet_5Bits_Spec0To8_IntKey that =
            (TransientTrieSet_5Bits_Spec0To8_IntKey) other;

        if (this.cachedSize != that.cachedSize) {
          return false;
        }

        if (this.hashCode != that.hashCode) {
          return false;
        }

        return rootNode.equals(that.rootNode);
      } else if (other instanceof Set) {
        Set that = (Set) other;

        if (this.size() != that.size())
          return false;

        return containsAll(that);
      }

      return false;
    }

    @Override
    public int hashCode() {
      return hashCode;
    }

    @Override
    public io.usethesource.capsule.Set.Immutable<Integer> freeze() {
      if (mutator.get() == null) {
        throw new IllegalStateException("Transient already frozen.");
      }

      mutator.set(null);
      return new TrieSet_5Bits_Spec0To8_IntKey(rootNode, hashCode, cachedSize);
    }
  }

  private static final class Set0To0Node_5Bits_Spec0To8_IntKey extends CompactEmptySetNode {

    Set0To0Node_5Bits_Spec0To8_IntKey(final AtomicReference<Thread> mutator, final int nodeMap,
        final int dataMap) {
      super(mutator, nodeMap, dataMap);

      assert nodeInvariant();
    }

    @Override
    boolean hasSlots() {
      return false;
    }

    @Override
    int slotArity() {
      return 0;
    }

    @Override
    Object getSlot(final int index) {
      final int boundary = payloadArity();

      if (index < boundary) {
        return getKey(index);
      } else {
        return getNode(index - boundary);
      }
    }

    @Override
    CompactSetNode getNode(int index) {
      throw new IllegalStateException("Index out of range.");
    }

    @Override
    int getKey(int index) {
      throw new IllegalStateException("Index out of range.");
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
    boolean hasPayload() {
      return false;
    }

    @Override
    int payloadArity() {
      return 0;
    }

    @Override
    byte sizePredicate() {
      return SIZE_EMPTY;
    }

    @Override
    CompactSetNode copyAndInsertValue(AtomicReference<Thread> mutator, final int bitpos,
        final int key) {
      final int valIndex = dataIndex(bitpos);

      final int nodeMap = (int) (this.nodeMap());
      final int dataMap = (int) (this.dataMap() | bitpos);

      switch (valIndex) {
        case 0:
          return nodeOf(mutator, nodeMap, dataMap, key);
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    CompactSetNode copyAndRemoveValue(AtomicReference<Thread> mutator, final int bitpos) {
      throw new IllegalStateException("Index out of range.");
    }

    @Override
    CompactSetNode copyAndSetNode(AtomicReference<Thread> mutator, final int bitpos,
        CompactSetNode node) {
      throw new IllegalStateException("Index out of range.");
    }

    @Override
    CompactSetNode copyAndMigrateFromInlineToNode(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetNode node) {
      throw new IllegalStateException("Index out of range.");
    }

    @Override
    CompactSetNode copyAndMigrateFromNodeToInline(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetNode node) {
      throw new IllegalStateException("Index out of range.");
    }

    @Override
    public int hashCode() {
      int result = 1;

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

      return true;
    }

  }

  private static final class Set0To1Node_5Bits_Spec0To8_IntKey extends CompactNodesOnlySetNode {

    private final CompactSetNode node1;

    Set0To1Node_5Bits_Spec0To8_IntKey(final AtomicReference<Thread> mutator, final int nodeMap,
        final int dataMap, final CompactSetNode node1) {
      super(mutator, nodeMap, dataMap);
      this.node1 = node1;

      assert nodeInvariant();
    }

    @Override
    boolean hasSlots() {
      return true;
    }

    @Override
    int slotArity() {
      return 1;
    }

    @Override
    Object getSlot(final int index) {
      final int boundary = payloadArity();

      if (index < boundary) {
        return getKey(index);
      } else {
        return getNode(index - boundary);
      }
    }

    @Override
    CompactSetNode getNode(int index) {
      switch (index) {
        case 0:
          return node1;
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    int getKey(int index) {
      throw new IllegalStateException("Index out of range.");
    }

    @Override
    boolean hasNodes() {
      return true;
    }

    @Override
    int nodeArity() {
      return 1;
    }

    @Override
    boolean hasPayload() {
      return false;
    }

    @Override
    int payloadArity() {
      return 0;
    }

    @Override
    byte sizePredicate() {
      return SIZE_MORE_THAN_ONE;
    }

    @Override
    CompactSetNode copyAndInsertValue(AtomicReference<Thread> mutator, final int bitpos,
        final int key) {
      final int valIndex = dataIndex(bitpos);

      final int nodeMap = (int) (this.nodeMap());
      final int dataMap = (int) (this.dataMap() | bitpos);

      switch (valIndex) {
        case 0:
          return nodeOf(mutator, nodeMap, dataMap, key, node1);
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    CompactSetNode copyAndRemoveValue(AtomicReference<Thread> mutator, final int bitpos) {
      throw new IllegalStateException("Index out of range.");
    }

    @Override
    CompactSetNode copyAndSetNode(AtomicReference<Thread> mutator, final int bitpos,
        CompactSetNode node) {
      final int index = nodeIndex(bitpos);

      final int nodeMap = this.nodeMap();
      final int dataMap = this.dataMap();

      switch (index) {
        case 0:
          return nodeOf(mutator, nodeMap, dataMap, node);
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    CompactSetNode copyAndMigrateFromInlineToNode(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetNode node) {
      throw new IllegalStateException("Index out of range.");
    }

    @Override
    CompactSetNode copyAndMigrateFromNodeToInline(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetNode node) {
      final int bitIndex = nodeIndex(bitpos);
      final int valIndex = dataIndex(bitpos);

      final int nodeMap = (int) (this.nodeMap() ^ bitpos);
      final int dataMap = (int) (this.dataMap() | bitpos);

      final int key = node.getKey(0);

      switch (bitIndex) {
        case 0:
          switch (valIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;

      result = prime * result + ((int) nodeMap());
      result = prime * result + ((int) dataMap());

      result = prime * result + node1.hashCode();

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
      Set0To1Node_5Bits_Spec0To8_IntKey that = (Set0To1Node_5Bits_Spec0To8_IntKey) other;

      if (nodeMap() != that.nodeMap()) {
        return false;
      }
      if (dataMap() != that.dataMap()) {
        return false;
      }

      if (!(node1.equals(that.node1))) {
        return false;
      }

      return true;
    }

  }

  private static final class Set0To2Node_5Bits_Spec0To8_IntKey extends CompactNodesOnlySetNode {

    private final CompactSetNode node1;
    private final CompactSetNode node2;

    Set0To2Node_5Bits_Spec0To8_IntKey(final AtomicReference<Thread> mutator, final int nodeMap,
        final int dataMap, final CompactSetNode node1, final CompactSetNode node2) {
      super(mutator, nodeMap, dataMap);
      this.node1 = node1;
      this.node2 = node2;

      assert nodeInvariant();
    }

    @Override
    boolean hasSlots() {
      return true;
    }

    @Override
    int slotArity() {
      return 2;
    }

    @Override
    Object getSlot(final int index) {
      final int boundary = payloadArity();

      if (index < boundary) {
        return getKey(index);
      } else {
        return getNode(index - boundary);
      }
    }

    @Override
    CompactSetNode getNode(int index) {
      switch (index) {
        case 0:
          return node1;
        case 1:
          return node2;
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    int getKey(int index) {
      throw new IllegalStateException("Index out of range.");
    }

    @Override
    boolean hasNodes() {
      return true;
    }

    @Override
    int nodeArity() {
      return 2;
    }

    @Override
    boolean hasPayload() {
      return false;
    }

    @Override
    int payloadArity() {
      return 0;
    }

    @Override
    byte sizePredicate() {
      return SIZE_MORE_THAN_ONE;
    }

    @Override
    CompactSetNode copyAndInsertValue(AtomicReference<Thread> mutator, final int bitpos,
        final int key) {
      final int valIndex = dataIndex(bitpos);

      final int nodeMap = (int) (this.nodeMap());
      final int dataMap = (int) (this.dataMap() | bitpos);

      switch (valIndex) {
        case 0:
          return nodeOf(mutator, nodeMap, dataMap, key, node1, node2);
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    CompactSetNode copyAndRemoveValue(AtomicReference<Thread> mutator, final int bitpos) {
      throw new IllegalStateException("Index out of range.");
    }

    @Override
    CompactSetNode copyAndSetNode(AtomicReference<Thread> mutator, final int bitpos,
        CompactSetNode node) {
      final int index = nodeIndex(bitpos);

      final int nodeMap = this.nodeMap();
      final int dataMap = this.dataMap();

      switch (index) {
        case 0:
          return nodeOf(mutator, nodeMap, dataMap, node, node2);
        case 1:
          return nodeOf(mutator, nodeMap, dataMap, node1, node);
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    CompactSetNode copyAndMigrateFromInlineToNode(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetNode node) {
      throw new IllegalStateException("Index out of range.");
    }

    @Override
    CompactSetNode copyAndMigrateFromNodeToInline(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetNode node) {
      final int bitIndex = nodeIndex(bitpos);
      final int valIndex = dataIndex(bitpos);

      final int nodeMap = (int) (this.nodeMap() ^ bitpos);
      final int dataMap = (int) (this.dataMap() | bitpos);

      final int key = node.getKey(0);

      switch (bitIndex) {
        case 0:
          switch (valIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key, node2);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        case 1:
          switch (valIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key, node1);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;

      result = prime * result + ((int) nodeMap());
      result = prime * result + ((int) dataMap());

      result = prime * result + node1.hashCode();
      result = prime * result + node2.hashCode();

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
      Set0To2Node_5Bits_Spec0To8_IntKey that = (Set0To2Node_5Bits_Spec0To8_IntKey) other;

      if (nodeMap() != that.nodeMap()) {
        return false;
      }
      if (dataMap() != that.dataMap()) {
        return false;
      }

      if (!(node1.equals(that.node1))) {
        return false;
      }
      if (!(node2.equals(that.node2))) {
        return false;
      }

      return true;
    }

  }

  private static final class Set0To3Node_5Bits_Spec0To8_IntKey extends CompactNodesOnlySetNode {

    private final CompactSetNode node1;
    private final CompactSetNode node2;
    private final CompactSetNode node3;

    Set0To3Node_5Bits_Spec0To8_IntKey(final AtomicReference<Thread> mutator, final int nodeMap,
        final int dataMap, final CompactSetNode node1, final CompactSetNode node2,
        final CompactSetNode node3) {
      super(mutator, nodeMap, dataMap);
      this.node1 = node1;
      this.node2 = node2;
      this.node3 = node3;

      assert nodeInvariant();
    }

    @Override
    boolean hasSlots() {
      return true;
    }

    @Override
    int slotArity() {
      return 3;
    }

    @Override
    Object getSlot(final int index) {
      final int boundary = payloadArity();

      if (index < boundary) {
        return getKey(index);
      } else {
        return getNode(index - boundary);
      }
    }

    @Override
    CompactSetNode getNode(int index) {
      switch (index) {
        case 0:
          return node1;
        case 1:
          return node2;
        case 2:
          return node3;
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    int getKey(int index) {
      throw new IllegalStateException("Index out of range.");
    }

    @Override
    boolean hasNodes() {
      return true;
    }

    @Override
    int nodeArity() {
      return 3;
    }

    @Override
    boolean hasPayload() {
      return false;
    }

    @Override
    int payloadArity() {
      return 0;
    }

    @Override
    byte sizePredicate() {
      return SIZE_MORE_THAN_ONE;
    }

    @Override
    CompactSetNode copyAndInsertValue(AtomicReference<Thread> mutator, final int bitpos,
        final int key) {
      final int valIndex = dataIndex(bitpos);

      final int nodeMap = (int) (this.nodeMap());
      final int dataMap = (int) (this.dataMap() | bitpos);

      switch (valIndex) {
        case 0:
          return nodeOf(mutator, nodeMap, dataMap, key, node1, node2, node3);
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    CompactSetNode copyAndRemoveValue(AtomicReference<Thread> mutator, final int bitpos) {
      throw new IllegalStateException("Index out of range.");
    }

    @Override
    CompactSetNode copyAndSetNode(AtomicReference<Thread> mutator, final int bitpos,
        CompactSetNode node) {
      final int index = nodeIndex(bitpos);

      final int nodeMap = this.nodeMap();
      final int dataMap = this.dataMap();

      switch (index) {
        case 0:
          return nodeOf(mutator, nodeMap, dataMap, node, node2, node3);
        case 1:
          return nodeOf(mutator, nodeMap, dataMap, node1, node, node3);
        case 2:
          return nodeOf(mutator, nodeMap, dataMap, node1, node2, node);
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    CompactSetNode copyAndMigrateFromInlineToNode(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetNode node) {
      throw new IllegalStateException("Index out of range.");
    }

    @Override
    CompactSetNode copyAndMigrateFromNodeToInline(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetNode node) {
      final int bitIndex = nodeIndex(bitpos);
      final int valIndex = dataIndex(bitpos);

      final int nodeMap = (int) (this.nodeMap() ^ bitpos);
      final int dataMap = (int) (this.dataMap() | bitpos);

      final int key = node.getKey(0);

      switch (bitIndex) {
        case 0:
          switch (valIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key, node2, node3);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        case 1:
          switch (valIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key, node1, node3);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        case 2:
          switch (valIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key, node1, node2);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;

      result = prime * result + ((int) nodeMap());
      result = prime * result + ((int) dataMap());

      result = prime * result + node1.hashCode();
      result = prime * result + node2.hashCode();
      result = prime * result + node3.hashCode();

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
      Set0To3Node_5Bits_Spec0To8_IntKey that = (Set0To3Node_5Bits_Spec0To8_IntKey) other;

      if (nodeMap() != that.nodeMap()) {
        return false;
      }
      if (dataMap() != that.dataMap()) {
        return false;
      }

      if (!(node1.equals(that.node1))) {
        return false;
      }
      if (!(node2.equals(that.node2))) {
        return false;
      }
      if (!(node3.equals(that.node3))) {
        return false;
      }

      return true;
    }

  }

  private static final class Set0To4Node_5Bits_Spec0To8_IntKey extends CompactNodesOnlySetNode {

    private final CompactSetNode node1;
    private final CompactSetNode node2;
    private final CompactSetNode node3;
    private final CompactSetNode node4;

    Set0To4Node_5Bits_Spec0To8_IntKey(final AtomicReference<Thread> mutator, final int nodeMap,
        final int dataMap, final CompactSetNode node1, final CompactSetNode node2,
        final CompactSetNode node3, final CompactSetNode node4) {
      super(mutator, nodeMap, dataMap);
      this.node1 = node1;
      this.node2 = node2;
      this.node3 = node3;
      this.node4 = node4;

      assert nodeInvariant();
    }

    @Override
    boolean hasSlots() {
      return true;
    }

    @Override
    int slotArity() {
      return 4;
    }

    @Override
    Object getSlot(final int index) {
      final int boundary = payloadArity();

      if (index < boundary) {
        return getKey(index);
      } else {
        return getNode(index - boundary);
      }
    }

    @Override
    CompactSetNode getNode(int index) {
      switch (index) {
        case 0:
          return node1;
        case 1:
          return node2;
        case 2:
          return node3;
        case 3:
          return node4;
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    int getKey(int index) {
      throw new IllegalStateException("Index out of range.");
    }

    @Override
    boolean hasNodes() {
      return true;
    }

    @Override
    int nodeArity() {
      return 4;
    }

    @Override
    boolean hasPayload() {
      return false;
    }

    @Override
    int payloadArity() {
      return 0;
    }

    @Override
    byte sizePredicate() {
      return SIZE_MORE_THAN_ONE;
    }

    @Override
    CompactSetNode copyAndInsertValue(AtomicReference<Thread> mutator, final int bitpos,
        final int key) {
      final int valIndex = dataIndex(bitpos);

      final int nodeMap = (int) (this.nodeMap());
      final int dataMap = (int) (this.dataMap() | bitpos);

      switch (valIndex) {
        case 0:
          return nodeOf(mutator, nodeMap, dataMap, key, node1, node2, node3, node4);
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    CompactSetNode copyAndRemoveValue(AtomicReference<Thread> mutator, final int bitpos) {
      throw new IllegalStateException("Index out of range.");
    }

    @Override
    CompactSetNode copyAndSetNode(AtomicReference<Thread> mutator, final int bitpos,
        CompactSetNode node) {
      final int index = nodeIndex(bitpos);

      final int nodeMap = this.nodeMap();
      final int dataMap = this.dataMap();

      switch (index) {
        case 0:
          return nodeOf(mutator, nodeMap, dataMap, node, node2, node3, node4);
        case 1:
          return nodeOf(mutator, nodeMap, dataMap, node1, node, node3, node4);
        case 2:
          return nodeOf(mutator, nodeMap, dataMap, node1, node2, node, node4);
        case 3:
          return nodeOf(mutator, nodeMap, dataMap, node1, node2, node3, node);
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    CompactSetNode copyAndMigrateFromInlineToNode(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetNode node) {
      throw new IllegalStateException("Index out of range.");
    }

    @Override
    CompactSetNode copyAndMigrateFromNodeToInline(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetNode node) {
      final int bitIndex = nodeIndex(bitpos);
      final int valIndex = dataIndex(bitpos);

      final int nodeMap = (int) (this.nodeMap() ^ bitpos);
      final int dataMap = (int) (this.dataMap() | bitpos);

      final int key = node.getKey(0);

      switch (bitIndex) {
        case 0:
          switch (valIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key, node2, node3, node4);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        case 1:
          switch (valIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key, node1, node3, node4);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        case 2:
          switch (valIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key, node1, node2, node4);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        case 3:
          switch (valIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key, node1, node2, node3);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;

      result = prime * result + ((int) nodeMap());
      result = prime * result + ((int) dataMap());

      result = prime * result + node1.hashCode();
      result = prime * result + node2.hashCode();
      result = prime * result + node3.hashCode();
      result = prime * result + node4.hashCode();

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
      Set0To4Node_5Bits_Spec0To8_IntKey that = (Set0To4Node_5Bits_Spec0To8_IntKey) other;

      if (nodeMap() != that.nodeMap()) {
        return false;
      }
      if (dataMap() != that.dataMap()) {
        return false;
      }

      if (!(node1.equals(that.node1))) {
        return false;
      }
      if (!(node2.equals(that.node2))) {
        return false;
      }
      if (!(node3.equals(that.node3))) {
        return false;
      }
      if (!(node4.equals(that.node4))) {
        return false;
      }

      return true;
    }

  }

  private static final class Set0To5Node_5Bits_Spec0To8_IntKey extends CompactNodesOnlySetNode {

    private final CompactSetNode node1;
    private final CompactSetNode node2;
    private final CompactSetNode node3;
    private final CompactSetNode node4;
    private final CompactSetNode node5;

    Set0To5Node_5Bits_Spec0To8_IntKey(final AtomicReference<Thread> mutator, final int nodeMap,
        final int dataMap, final CompactSetNode node1, final CompactSetNode node2,
        final CompactSetNode node3, final CompactSetNode node4, final CompactSetNode node5) {
      super(mutator, nodeMap, dataMap);
      this.node1 = node1;
      this.node2 = node2;
      this.node3 = node3;
      this.node4 = node4;
      this.node5 = node5;

      assert nodeInvariant();
    }

    @Override
    boolean hasSlots() {
      return true;
    }

    @Override
    int slotArity() {
      return 5;
    }

    @Override
    Object getSlot(final int index) {
      final int boundary = payloadArity();

      if (index < boundary) {
        return getKey(index);
      } else {
        return getNode(index - boundary);
      }
    }

    @Override
    CompactSetNode getNode(int index) {
      switch (index) {
        case 0:
          return node1;
        case 1:
          return node2;
        case 2:
          return node3;
        case 3:
          return node4;
        case 4:
          return node5;
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    int getKey(int index) {
      throw new IllegalStateException("Index out of range.");
    }

    @Override
    boolean hasNodes() {
      return true;
    }

    @Override
    int nodeArity() {
      return 5;
    }

    @Override
    boolean hasPayload() {
      return false;
    }

    @Override
    int payloadArity() {
      return 0;
    }

    @Override
    byte sizePredicate() {
      return SIZE_MORE_THAN_ONE;
    }

    @Override
    CompactSetNode copyAndInsertValue(AtomicReference<Thread> mutator, final int bitpos,
        final int key) {
      final int valIndex = dataIndex(bitpos);

      final int nodeMap = (int) (this.nodeMap());
      final int dataMap = (int) (this.dataMap() | bitpos);

      switch (valIndex) {
        case 0:
          return nodeOf(mutator, nodeMap, dataMap, key, node1, node2, node3, node4, node5);
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    CompactSetNode copyAndRemoveValue(AtomicReference<Thread> mutator, final int bitpos) {
      throw new IllegalStateException("Index out of range.");
    }

    @Override
    CompactSetNode copyAndSetNode(AtomicReference<Thread> mutator, final int bitpos,
        CompactSetNode node) {
      final int index = nodeIndex(bitpos);

      final int nodeMap = this.nodeMap();
      final int dataMap = this.dataMap();

      switch (index) {
        case 0:
          return nodeOf(mutator, nodeMap, dataMap, node, node2, node3, node4, node5);
        case 1:
          return nodeOf(mutator, nodeMap, dataMap, node1, node, node3, node4, node5);
        case 2:
          return nodeOf(mutator, nodeMap, dataMap, node1, node2, node, node4, node5);
        case 3:
          return nodeOf(mutator, nodeMap, dataMap, node1, node2, node3, node, node5);
        case 4:
          return nodeOf(mutator, nodeMap, dataMap, node1, node2, node3, node4, node);
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    CompactSetNode copyAndMigrateFromInlineToNode(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetNode node) {
      throw new IllegalStateException("Index out of range.");
    }

    @Override
    CompactSetNode copyAndMigrateFromNodeToInline(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetNode node) {
      final int bitIndex = nodeIndex(bitpos);
      final int valIndex = dataIndex(bitpos);

      final int nodeMap = (int) (this.nodeMap() ^ bitpos);
      final int dataMap = (int) (this.dataMap() | bitpos);

      final int key = node.getKey(0);

      switch (bitIndex) {
        case 0:
          switch (valIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key, node2, node3, node4, node5);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        case 1:
          switch (valIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key, node1, node3, node4, node5);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        case 2:
          switch (valIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key, node1, node2, node4, node5);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        case 3:
          switch (valIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key, node1, node2, node3, node5);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        case 4:
          switch (valIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key, node1, node2, node3, node4);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;

      result = prime * result + ((int) nodeMap());
      result = prime * result + ((int) dataMap());

      result = prime * result + node1.hashCode();
      result = prime * result + node2.hashCode();
      result = prime * result + node3.hashCode();
      result = prime * result + node4.hashCode();
      result = prime * result + node5.hashCode();

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
      Set0To5Node_5Bits_Spec0To8_IntKey that = (Set0To5Node_5Bits_Spec0To8_IntKey) other;

      if (nodeMap() != that.nodeMap()) {
        return false;
      }
      if (dataMap() != that.dataMap()) {
        return false;
      }

      if (!(node1.equals(that.node1))) {
        return false;
      }
      if (!(node2.equals(that.node2))) {
        return false;
      }
      if (!(node3.equals(that.node3))) {
        return false;
      }
      if (!(node4.equals(that.node4))) {
        return false;
      }
      if (!(node5.equals(that.node5))) {
        return false;
      }

      return true;
    }

  }

  private static final class Set0To6Node_5Bits_Spec0To8_IntKey extends CompactNodesOnlySetNode {

    private final CompactSetNode node1;
    private final CompactSetNode node2;
    private final CompactSetNode node3;
    private final CompactSetNode node4;
    private final CompactSetNode node5;
    private final CompactSetNode node6;

    Set0To6Node_5Bits_Spec0To8_IntKey(final AtomicReference<Thread> mutator, final int nodeMap,
        final int dataMap, final CompactSetNode node1, final CompactSetNode node2,
        final CompactSetNode node3, final CompactSetNode node4, final CompactSetNode node5,
        final CompactSetNode node6) {
      super(mutator, nodeMap, dataMap);
      this.node1 = node1;
      this.node2 = node2;
      this.node3 = node3;
      this.node4 = node4;
      this.node5 = node5;
      this.node6 = node6;

      assert nodeInvariant();
    }

    @Override
    boolean hasSlots() {
      return true;
    }

    @Override
    int slotArity() {
      return 6;
    }

    @Override
    Object getSlot(final int index) {
      final int boundary = payloadArity();

      if (index < boundary) {
        return getKey(index);
      } else {
        return getNode(index - boundary);
      }
    }

    @Override
    CompactSetNode getNode(int index) {
      switch (index) {
        case 0:
          return node1;
        case 1:
          return node2;
        case 2:
          return node3;
        case 3:
          return node4;
        case 4:
          return node5;
        case 5:
          return node6;
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    int getKey(int index) {
      throw new IllegalStateException("Index out of range.");
    }

    @Override
    boolean hasNodes() {
      return true;
    }

    @Override
    int nodeArity() {
      return 6;
    }

    @Override
    boolean hasPayload() {
      return false;
    }

    @Override
    int payloadArity() {
      return 0;
    }

    @Override
    byte sizePredicate() {
      return SIZE_MORE_THAN_ONE;
    }

    @Override
    CompactSetNode copyAndInsertValue(AtomicReference<Thread> mutator, final int bitpos,
        final int key) {
      final int valIndex = dataIndex(bitpos);

      final int nodeMap = (int) (this.nodeMap());
      final int dataMap = (int) (this.dataMap() | bitpos);

      switch (valIndex) {
        case 0:
          return nodeOf(mutator, nodeMap, dataMap, key, node1, node2, node3, node4, node5, node6);
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    CompactSetNode copyAndRemoveValue(AtomicReference<Thread> mutator, final int bitpos) {
      throw new IllegalStateException("Index out of range.");
    }

    @Override
    CompactSetNode copyAndSetNode(AtomicReference<Thread> mutator, final int bitpos,
        CompactSetNode node) {
      final int index = nodeIndex(bitpos);

      final int nodeMap = this.nodeMap();
      final int dataMap = this.dataMap();

      switch (index) {
        case 0:
          return nodeOf(mutator, nodeMap, dataMap, node, node2, node3, node4, node5, node6);
        case 1:
          return nodeOf(mutator, nodeMap, dataMap, node1, node, node3, node4, node5, node6);
        case 2:
          return nodeOf(mutator, nodeMap, dataMap, node1, node2, node, node4, node5, node6);
        case 3:
          return nodeOf(mutator, nodeMap, dataMap, node1, node2, node3, node, node5, node6);
        case 4:
          return nodeOf(mutator, nodeMap, dataMap, node1, node2, node3, node4, node, node6);
        case 5:
          return nodeOf(mutator, nodeMap, dataMap, node1, node2, node3, node4, node5, node);
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    CompactSetNode copyAndMigrateFromInlineToNode(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetNode node) {
      throw new IllegalStateException("Index out of range.");
    }

    @Override
    CompactSetNode copyAndMigrateFromNodeToInline(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetNode node) {
      final int bitIndex = nodeIndex(bitpos);
      final int valIndex = dataIndex(bitpos);

      final int nodeMap = (int) (this.nodeMap() ^ bitpos);
      final int dataMap = (int) (this.dataMap() | bitpos);

      final int key = node.getKey(0);

      switch (bitIndex) {
        case 0:
          switch (valIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key, node2, node3, node4, node5, node6);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        case 1:
          switch (valIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key, node1, node3, node4, node5, node6);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        case 2:
          switch (valIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key, node1, node2, node4, node5, node6);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        case 3:
          switch (valIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key, node1, node2, node3, node5, node6);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        case 4:
          switch (valIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key, node1, node2, node3, node4, node6);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        case 5:
          switch (valIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key, node1, node2, node3, node4, node5);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;

      result = prime * result + ((int) nodeMap());
      result = prime * result + ((int) dataMap());

      result = prime * result + node1.hashCode();
      result = prime * result + node2.hashCode();
      result = prime * result + node3.hashCode();
      result = prime * result + node4.hashCode();
      result = prime * result + node5.hashCode();
      result = prime * result + node6.hashCode();

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
      Set0To6Node_5Bits_Spec0To8_IntKey that = (Set0To6Node_5Bits_Spec0To8_IntKey) other;

      if (nodeMap() != that.nodeMap()) {
        return false;
      }
      if (dataMap() != that.dataMap()) {
        return false;
      }

      if (!(node1.equals(that.node1))) {
        return false;
      }
      if (!(node2.equals(that.node2))) {
        return false;
      }
      if (!(node3.equals(that.node3))) {
        return false;
      }
      if (!(node4.equals(that.node4))) {
        return false;
      }
      if (!(node5.equals(that.node5))) {
        return false;
      }
      if (!(node6.equals(that.node6))) {
        return false;
      }

      return true;
    }

  }

  private static final class Set0To7Node_5Bits_Spec0To8_IntKey extends CompactNodesOnlySetNode {

    private final CompactSetNode node1;
    private final CompactSetNode node2;
    private final CompactSetNode node3;
    private final CompactSetNode node4;
    private final CompactSetNode node5;
    private final CompactSetNode node6;
    private final CompactSetNode node7;

    Set0To7Node_5Bits_Spec0To8_IntKey(final AtomicReference<Thread> mutator, final int nodeMap,
        final int dataMap, final CompactSetNode node1, final CompactSetNode node2,
        final CompactSetNode node3, final CompactSetNode node4, final CompactSetNode node5,
        final CompactSetNode node6, final CompactSetNode node7) {
      super(mutator, nodeMap, dataMap);
      this.node1 = node1;
      this.node2 = node2;
      this.node3 = node3;
      this.node4 = node4;
      this.node5 = node5;
      this.node6 = node6;
      this.node7 = node7;

      assert nodeInvariant();
    }

    @Override
    boolean hasSlots() {
      return true;
    }

    @Override
    int slotArity() {
      return 7;
    }

    @Override
    Object getSlot(final int index) {
      final int boundary = payloadArity();

      if (index < boundary) {
        return getKey(index);
      } else {
        return getNode(index - boundary);
      }
    }

    @Override
    CompactSetNode getNode(int index) {
      switch (index) {
        case 0:
          return node1;
        case 1:
          return node2;
        case 2:
          return node3;
        case 3:
          return node4;
        case 4:
          return node5;
        case 5:
          return node6;
        case 6:
          return node7;
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    int getKey(int index) {
      throw new IllegalStateException("Index out of range.");
    }

    @Override
    boolean hasNodes() {
      return true;
    }

    @Override
    int nodeArity() {
      return 7;
    }

    @Override
    boolean hasPayload() {
      return false;
    }

    @Override
    int payloadArity() {
      return 0;
    }

    @Override
    byte sizePredicate() {
      return SIZE_MORE_THAN_ONE;
    }

    @Override
    CompactSetNode copyAndInsertValue(AtomicReference<Thread> mutator, final int bitpos,
        final int key) {
      final int valIndex = dataIndex(bitpos);

      final int nodeMap = (int) (this.nodeMap());
      final int dataMap = (int) (this.dataMap() | bitpos);

      switch (valIndex) {
        case 0:
          return nodeOf(mutator, nodeMap, dataMap, key, node1, node2, node3, node4, node5, node6,
              node7);
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    CompactSetNode copyAndRemoveValue(AtomicReference<Thread> mutator, final int bitpos) {
      throw new IllegalStateException("Index out of range.");
    }

    @Override
    CompactSetNode copyAndSetNode(AtomicReference<Thread> mutator, final int bitpos,
        CompactSetNode node) {
      final int index = nodeIndex(bitpos);

      final int nodeMap = this.nodeMap();
      final int dataMap = this.dataMap();

      switch (index) {
        case 0:
          return nodeOf(mutator, nodeMap, dataMap, node, node2, node3, node4, node5, node6, node7);
        case 1:
          return nodeOf(mutator, nodeMap, dataMap, node1, node, node3, node4, node5, node6, node7);
        case 2:
          return nodeOf(mutator, nodeMap, dataMap, node1, node2, node, node4, node5, node6, node7);
        case 3:
          return nodeOf(mutator, nodeMap, dataMap, node1, node2, node3, node, node5, node6, node7);
        case 4:
          return nodeOf(mutator, nodeMap, dataMap, node1, node2, node3, node4, node, node6, node7);
        case 5:
          return nodeOf(mutator, nodeMap, dataMap, node1, node2, node3, node4, node5, node, node7);
        case 6:
          return nodeOf(mutator, nodeMap, dataMap, node1, node2, node3, node4, node5, node6, node);
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    CompactSetNode copyAndMigrateFromInlineToNode(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetNode node) {
      throw new IllegalStateException("Index out of range.");
    }

    @Override
    CompactSetNode copyAndMigrateFromNodeToInline(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetNode node) {
      final int bitIndex = nodeIndex(bitpos);
      final int valIndex = dataIndex(bitpos);

      final int nodeMap = (int) (this.nodeMap() ^ bitpos);
      final int dataMap = (int) (this.dataMap() | bitpos);

      final int key = node.getKey(0);

      switch (bitIndex) {
        case 0:
          switch (valIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key, node2, node3, node4, node5, node6,
                  node7);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        case 1:
          switch (valIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key, node1, node3, node4, node5, node6,
                  node7);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        case 2:
          switch (valIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key, node1, node2, node4, node5, node6,
                  node7);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        case 3:
          switch (valIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key, node1, node2, node3, node5, node6,
                  node7);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        case 4:
          switch (valIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key, node1, node2, node3, node4, node6,
                  node7);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        case 5:
          switch (valIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key, node1, node2, node3, node4, node5,
                  node7);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        case 6:
          switch (valIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key, node1, node2, node3, node4, node5,
                  node6);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;

      result = prime * result + ((int) nodeMap());
      result = prime * result + ((int) dataMap());

      result = prime * result + node1.hashCode();
      result = prime * result + node2.hashCode();
      result = prime * result + node3.hashCode();
      result = prime * result + node4.hashCode();
      result = prime * result + node5.hashCode();
      result = prime * result + node6.hashCode();
      result = prime * result + node7.hashCode();

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
      Set0To7Node_5Bits_Spec0To8_IntKey that = (Set0To7Node_5Bits_Spec0To8_IntKey) other;

      if (nodeMap() != that.nodeMap()) {
        return false;
      }
      if (dataMap() != that.dataMap()) {
        return false;
      }

      if (!(node1.equals(that.node1))) {
        return false;
      }
      if (!(node2.equals(that.node2))) {
        return false;
      }
      if (!(node3.equals(that.node3))) {
        return false;
      }
      if (!(node4.equals(that.node4))) {
        return false;
      }
      if (!(node5.equals(that.node5))) {
        return false;
      }
      if (!(node6.equals(that.node6))) {
        return false;
      }
      if (!(node7.equals(that.node7))) {
        return false;
      }

      return true;
    }

  }

  private static final class Set0To8Node_5Bits_Spec0To8_IntKey extends CompactNodesOnlySetNode {

    private final CompactSetNode node1;
    private final CompactSetNode node2;
    private final CompactSetNode node3;
    private final CompactSetNode node4;
    private final CompactSetNode node5;
    private final CompactSetNode node6;
    private final CompactSetNode node7;
    private final CompactSetNode node8;

    Set0To8Node_5Bits_Spec0To8_IntKey(final AtomicReference<Thread> mutator, final int nodeMap,
        final int dataMap, final CompactSetNode node1, final CompactSetNode node2,
        final CompactSetNode node3, final CompactSetNode node4, final CompactSetNode node5,
        final CompactSetNode node6, final CompactSetNode node7, final CompactSetNode node8) {
      super(mutator, nodeMap, dataMap);
      this.node1 = node1;
      this.node2 = node2;
      this.node3 = node3;
      this.node4 = node4;
      this.node5 = node5;
      this.node6 = node6;
      this.node7 = node7;
      this.node8 = node8;

      assert nodeInvariant();
    }

    @Override
    boolean hasSlots() {
      return true;
    }

    @Override
    int slotArity() {
      return 8;
    }

    @Override
    Object getSlot(final int index) {
      final int boundary = payloadArity();

      if (index < boundary) {
        return getKey(index);
      } else {
        return getNode(index - boundary);
      }
    }

    @Override
    CompactSetNode getNode(int index) {
      switch (index) {
        case 0:
          return node1;
        case 1:
          return node2;
        case 2:
          return node3;
        case 3:
          return node4;
        case 4:
          return node5;
        case 5:
          return node6;
        case 6:
          return node7;
        case 7:
          return node8;
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    int getKey(int index) {
      throw new IllegalStateException("Index out of range.");
    }

    @Override
    boolean hasNodes() {
      return true;
    }

    @Override
    int nodeArity() {
      return 8;
    }

    @Override
    boolean hasPayload() {
      return false;
    }

    @Override
    int payloadArity() {
      return 0;
    }

    @Override
    byte sizePredicate() {
      return SIZE_MORE_THAN_ONE;
    }

    @Override
    CompactSetNode copyAndInsertValue(AtomicReference<Thread> mutator, final int bitpos,
        final int key) {
      final int valIndex = dataIndex(bitpos);

      final int nodeMap = (int) (this.nodeMap());
      final int dataMap = (int) (this.dataMap() | bitpos);

      switch (valIndex) {
        case 0:
          return nodeOf(mutator, nodeMap, dataMap, key, node1, node2, node3, node4, node5, node6,
              node7, node8);
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    CompactSetNode copyAndRemoveValue(AtomicReference<Thread> mutator, final int bitpos) {
      throw new IllegalStateException("Index out of range.");
    }

    @Override
    CompactSetNode copyAndSetNode(AtomicReference<Thread> mutator, final int bitpos,
        CompactSetNode node) {
      final int index = nodeIndex(bitpos);

      final int nodeMap = this.nodeMap();
      final int dataMap = this.dataMap();

      switch (index) {
        case 0:
          return nodeOf(mutator, nodeMap, dataMap, node, node2, node3, node4, node5, node6, node7,
              node8);
        case 1:
          return nodeOf(mutator, nodeMap, dataMap, node1, node, node3, node4, node5, node6, node7,
              node8);
        case 2:
          return nodeOf(mutator, nodeMap, dataMap, node1, node2, node, node4, node5, node6, node7,
              node8);
        case 3:
          return nodeOf(mutator, nodeMap, dataMap, node1, node2, node3, node, node5, node6, node7,
              node8);
        case 4:
          return nodeOf(mutator, nodeMap, dataMap, node1, node2, node3, node4, node, node6, node7,
              node8);
        case 5:
          return nodeOf(mutator, nodeMap, dataMap, node1, node2, node3, node4, node5, node, node7,
              node8);
        case 6:
          return nodeOf(mutator, nodeMap, dataMap, node1, node2, node3, node4, node5, node6, node,
              node8);
        case 7:
          return nodeOf(mutator, nodeMap, dataMap, node1, node2, node3, node4, node5, node6, node7,
              node);
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    CompactSetNode copyAndMigrateFromInlineToNode(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetNode node) {
      throw new IllegalStateException("Index out of range.");
    }

    @Override
    CompactSetNode copyAndMigrateFromNodeToInline(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetNode node) {
      final int bitIndex = nodeIndex(bitpos);
      final int valIndex = dataIndex(bitpos);

      final int nodeMap = (int) (this.nodeMap() ^ bitpos);
      final int dataMap = (int) (this.dataMap() | bitpos);

      final int key = node.getKey(0);

      switch (bitIndex) {
        case 0:
          switch (valIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key, node2, node3, node4, node5, node6,
                  node7, node8);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        case 1:
          switch (valIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key, node1, node3, node4, node5, node6,
                  node7, node8);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        case 2:
          switch (valIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key, node1, node2, node4, node5, node6,
                  node7, node8);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        case 3:
          switch (valIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key, node1, node2, node3, node5, node6,
                  node7, node8);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        case 4:
          switch (valIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key, node1, node2, node3, node4, node6,
                  node7, node8);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        case 5:
          switch (valIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key, node1, node2, node3, node4, node5,
                  node7, node8);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        case 6:
          switch (valIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key, node1, node2, node3, node4, node5,
                  node6, node8);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        case 7:
          switch (valIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key, node1, node2, node3, node4, node5,
                  node6, node7);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;

      result = prime * result + ((int) nodeMap());
      result = prime * result + ((int) dataMap());

      result = prime * result + node1.hashCode();
      result = prime * result + node2.hashCode();
      result = prime * result + node3.hashCode();
      result = prime * result + node4.hashCode();
      result = prime * result + node5.hashCode();
      result = prime * result + node6.hashCode();
      result = prime * result + node7.hashCode();
      result = prime * result + node8.hashCode();

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
      Set0To8Node_5Bits_Spec0To8_IntKey that = (Set0To8Node_5Bits_Spec0To8_IntKey) other;

      if (nodeMap() != that.nodeMap()) {
        return false;
      }
      if (dataMap() != that.dataMap()) {
        return false;
      }

      if (!(node1.equals(that.node1))) {
        return false;
      }
      if (!(node2.equals(that.node2))) {
        return false;
      }
      if (!(node3.equals(that.node3))) {
        return false;
      }
      if (!(node4.equals(that.node4))) {
        return false;
      }
      if (!(node5.equals(that.node5))) {
        return false;
      }
      if (!(node6.equals(that.node6))) {
        return false;
      }
      if (!(node7.equals(that.node7))) {
        return false;
      }
      if (!(node8.equals(that.node8))) {
        return false;
      }

      return true;
    }

  }

  private static final class Set1To0Node_5Bits_Spec0To8_IntKey extends CompactValuesOnlySetNode {

    private final int key1;

    Set1To0Node_5Bits_Spec0To8_IntKey(final AtomicReference<Thread> mutator, final int nodeMap,
        final int dataMap, final int key1) {
      super(mutator, nodeMap, dataMap);
      this.key1 = key1;

      assert nodeInvariant();
    }

    @Override
    boolean hasSlots() {
      return true;
    }

    @Override
    int slotArity() {
      return 1;
    }

    @Override
    Object getSlot(final int index) {
      final int boundary = payloadArity();

      if (index < boundary) {
        return getKey(index);
      } else {
        return getNode(index - boundary);
      }
    }

    @Override
    CompactSetNode getNode(int index) {
      throw new IllegalStateException("Index out of range.");
    }

    @Override
    int getKey(int index) {
      switch (index) {
        case 0:
          return key1;
        default:
          throw new IllegalStateException("Index out of range.");
      }
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
    boolean hasPayload() {
      return true;
    }

    @Override
    int payloadArity() {
      return 1;
    }

    @Override
    byte sizePredicate() {
      return SIZE_ONE;
    }

    @Override
    CompactSetNode copyAndInsertValue(AtomicReference<Thread> mutator, final int bitpos,
        final int key) {
      final int valIndex = dataIndex(bitpos);

      final int nodeMap = (int) (this.nodeMap());
      final int dataMap = (int) (this.dataMap() | bitpos);

      switch (valIndex) {
        case 0:
          return nodeOf(mutator, nodeMap, dataMap, key, key1);
        case 1:
          return nodeOf(mutator, nodeMap, dataMap, key1, key);
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    CompactSetNode copyAndRemoveValue(AtomicReference<Thread> mutator, final int bitpos) {
      final int valIndex = dataIndex(bitpos);

      final int nodeMap = (int) (this.nodeMap());
      final int dataMap = (int) (this.dataMap() ^ bitpos);

      switch (valIndex) {
        case 0:
          return nodeOf(mutator, nodeMap, dataMap);
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    CompactSetNode copyAndSetNode(AtomicReference<Thread> mutator, final int bitpos,
        CompactSetNode node) {
      throw new IllegalStateException("Index out of range.");
    }

    @Override
    CompactSetNode copyAndMigrateFromInlineToNode(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetNode node) {
      final int bitIndex = nodeIndex(bitpos);
      final int valIndex = dataIndex(bitpos);

      final int nodeMap = (int) (this.nodeMap() | bitpos);
      final int dataMap = (int) (this.dataMap() ^ bitpos);

      switch (valIndex) {
        case 0:
          switch (bitIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, node);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    CompactSetNode copyAndMigrateFromNodeToInline(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetNode node) {
      throw new IllegalStateException("Index out of range.");
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;

      result = prime * result + ((int) nodeMap());
      result = prime * result + ((int) dataMap());

      result = prime * result + (int) key1;

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
      Set1To0Node_5Bits_Spec0To8_IntKey that = (Set1To0Node_5Bits_Spec0To8_IntKey) other;

      if (nodeMap() != that.nodeMap()) {
        return false;
      }
      if (dataMap() != that.dataMap()) {
        return false;
      }

      if (!(key1 == that.key1)) {
        return false;
      }

      return true;
    }

  }

  private static final class Set1To1Node_5Bits_Spec0To8_IntKey extends CompactMixedSetNode {

    private final int key1;
    private final CompactSetNode node1;

    Set1To1Node_5Bits_Spec0To8_IntKey(final AtomicReference<Thread> mutator, final int nodeMap,
        final int dataMap, final int key1, final CompactSetNode node1) {
      super(mutator, nodeMap, dataMap);
      this.key1 = key1;
      this.node1 = node1;

      assert nodeInvariant();
    }

    @Override
    boolean hasSlots() {
      return true;
    }

    @Override
    int slotArity() {
      return 2;
    }

    @Override
    Object getSlot(final int index) {
      final int boundary = payloadArity();

      if (index < boundary) {
        return getKey(index);
      } else {
        return getNode(index - boundary);
      }
    }

    @Override
    CompactSetNode getNode(int index) {
      switch (index) {
        case 0:
          return node1;
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    int getKey(int index) {
      switch (index) {
        case 0:
          return key1;
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    boolean hasNodes() {
      return true;
    }

    @Override
    int nodeArity() {
      return 1;
    }

    @Override
    boolean hasPayload() {
      return true;
    }

    @Override
    int payloadArity() {
      return 1;
    }

    @Override
    byte sizePredicate() {
      return SIZE_MORE_THAN_ONE;
    }

    @Override
    CompactSetNode copyAndInsertValue(AtomicReference<Thread> mutator, final int bitpos,
        final int key) {
      final int valIndex = dataIndex(bitpos);

      final int nodeMap = (int) (this.nodeMap());
      final int dataMap = (int) (this.dataMap() | bitpos);

      switch (valIndex) {
        case 0:
          return nodeOf(mutator, nodeMap, dataMap, key, key1, node1);
        case 1:
          return nodeOf(mutator, nodeMap, dataMap, key1, key, node1);
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    CompactSetNode copyAndRemoveValue(AtomicReference<Thread> mutator, final int bitpos) {
      final int valIndex = dataIndex(bitpos);

      final int nodeMap = (int) (this.nodeMap());
      final int dataMap = (int) (this.dataMap() ^ bitpos);

      switch (valIndex) {
        case 0:
          return nodeOf(mutator, nodeMap, dataMap, node1);
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    CompactSetNode copyAndSetNode(AtomicReference<Thread> mutator, final int bitpos,
        CompactSetNode node) {
      final int index = nodeIndex(bitpos);

      final int nodeMap = this.nodeMap();
      final int dataMap = this.dataMap();

      switch (index) {
        case 0:
          return nodeOf(mutator, nodeMap, dataMap, key1, node);
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    CompactSetNode copyAndMigrateFromInlineToNode(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetNode node) {
      final int bitIndex = nodeIndex(bitpos);
      final int valIndex = dataIndex(bitpos);

      final int nodeMap = (int) (this.nodeMap() | bitpos);
      final int dataMap = (int) (this.dataMap() ^ bitpos);

      switch (valIndex) {
        case 0:
          switch (bitIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, node, node1);
            case 1:
              return nodeOf(mutator, nodeMap, dataMap, node1, node);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    CompactSetNode copyAndMigrateFromNodeToInline(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetNode node) {
      final int bitIndex = nodeIndex(bitpos);
      final int valIndex = dataIndex(bitpos);

      final int nodeMap = (int) (this.nodeMap() ^ bitpos);
      final int dataMap = (int) (this.dataMap() | bitpos);

      final int key = node.getKey(0);

      switch (bitIndex) {
        case 0:
          switch (valIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key, key1);
            case 1:
              return nodeOf(mutator, nodeMap, dataMap, key1, key);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;

      result = prime * result + ((int) nodeMap());
      result = prime * result + ((int) dataMap());

      result = prime * result + (int) key1;

      result = prime * result + node1.hashCode();

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
      Set1To1Node_5Bits_Spec0To8_IntKey that = (Set1To1Node_5Bits_Spec0To8_IntKey) other;

      if (nodeMap() != that.nodeMap()) {
        return false;
      }
      if (dataMap() != that.dataMap()) {
        return false;
      }

      if (!(key1 == that.key1)) {
        return false;
      }
      if (!(node1.equals(that.node1))) {
        return false;
      }

      return true;
    }

  }

  private static final class Set1To2Node_5Bits_Spec0To8_IntKey extends CompactMixedSetNode {

    private final int key1;
    private final CompactSetNode node1;
    private final CompactSetNode node2;

    Set1To2Node_5Bits_Spec0To8_IntKey(final AtomicReference<Thread> mutator, final int nodeMap,
        final int dataMap, final int key1, final CompactSetNode node1, final CompactSetNode node2) {
      super(mutator, nodeMap, dataMap);
      this.key1 = key1;
      this.node1 = node1;
      this.node2 = node2;

      assert nodeInvariant();
    }

    @Override
    boolean hasSlots() {
      return true;
    }

    @Override
    int slotArity() {
      return 3;
    }

    @Override
    Object getSlot(final int index) {
      final int boundary = payloadArity();

      if (index < boundary) {
        return getKey(index);
      } else {
        return getNode(index - boundary);
      }
    }

    @Override
    CompactSetNode getNode(int index) {
      switch (index) {
        case 0:
          return node1;
        case 1:
          return node2;
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    int getKey(int index) {
      switch (index) {
        case 0:
          return key1;
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    boolean hasNodes() {
      return true;
    }

    @Override
    int nodeArity() {
      return 2;
    }

    @Override
    boolean hasPayload() {
      return true;
    }

    @Override
    int payloadArity() {
      return 1;
    }

    @Override
    byte sizePredicate() {
      return SIZE_MORE_THAN_ONE;
    }

    @Override
    CompactSetNode copyAndInsertValue(AtomicReference<Thread> mutator, final int bitpos,
        final int key) {
      final int valIndex = dataIndex(bitpos);

      final int nodeMap = (int) (this.nodeMap());
      final int dataMap = (int) (this.dataMap() | bitpos);

      switch (valIndex) {
        case 0:
          return nodeOf(mutator, nodeMap, dataMap, key, key1, node1, node2);
        case 1:
          return nodeOf(mutator, nodeMap, dataMap, key1, key, node1, node2);
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    CompactSetNode copyAndRemoveValue(AtomicReference<Thread> mutator, final int bitpos) {
      final int valIndex = dataIndex(bitpos);

      final int nodeMap = (int) (this.nodeMap());
      final int dataMap = (int) (this.dataMap() ^ bitpos);

      switch (valIndex) {
        case 0:
          return nodeOf(mutator, nodeMap, dataMap, node1, node2);
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    CompactSetNode copyAndSetNode(AtomicReference<Thread> mutator, final int bitpos,
        CompactSetNode node) {
      final int index = nodeIndex(bitpos);

      final int nodeMap = this.nodeMap();
      final int dataMap = this.dataMap();

      switch (index) {
        case 0:
          return nodeOf(mutator, nodeMap, dataMap, key1, node, node2);
        case 1:
          return nodeOf(mutator, nodeMap, dataMap, key1, node1, node);
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    CompactSetNode copyAndMigrateFromInlineToNode(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetNode node) {
      final int bitIndex = nodeIndex(bitpos);
      final int valIndex = dataIndex(bitpos);

      final int nodeMap = (int) (this.nodeMap() | bitpos);
      final int dataMap = (int) (this.dataMap() ^ bitpos);

      switch (valIndex) {
        case 0:
          switch (bitIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, node, node1, node2);
            case 1:
              return nodeOf(mutator, nodeMap, dataMap, node1, node, node2);
            case 2:
              return nodeOf(mutator, nodeMap, dataMap, node1, node2, node);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    CompactSetNode copyAndMigrateFromNodeToInline(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetNode node) {
      final int bitIndex = nodeIndex(bitpos);
      final int valIndex = dataIndex(bitpos);

      final int nodeMap = (int) (this.nodeMap() ^ bitpos);
      final int dataMap = (int) (this.dataMap() | bitpos);

      final int key = node.getKey(0);

      switch (bitIndex) {
        case 0:
          switch (valIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key, key1, node2);
            case 1:
              return nodeOf(mutator, nodeMap, dataMap, key1, key, node2);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        case 1:
          switch (valIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key, key1, node1);
            case 1:
              return nodeOf(mutator, nodeMap, dataMap, key1, key, node1);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;

      result = prime * result + ((int) nodeMap());
      result = prime * result + ((int) dataMap());

      result = prime * result + (int) key1;

      result = prime * result + node1.hashCode();
      result = prime * result + node2.hashCode();

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
      Set1To2Node_5Bits_Spec0To8_IntKey that = (Set1To2Node_5Bits_Spec0To8_IntKey) other;

      if (nodeMap() != that.nodeMap()) {
        return false;
      }
      if (dataMap() != that.dataMap()) {
        return false;
      }

      if (!(key1 == that.key1)) {
        return false;
      }
      if (!(node1.equals(that.node1))) {
        return false;
      }
      if (!(node2.equals(that.node2))) {
        return false;
      }

      return true;
    }

  }

  private static final class Set1To3Node_5Bits_Spec0To8_IntKey extends CompactMixedSetNode {

    private final int key1;
    private final CompactSetNode node1;
    private final CompactSetNode node2;
    private final CompactSetNode node3;

    Set1To3Node_5Bits_Spec0To8_IntKey(final AtomicReference<Thread> mutator, final int nodeMap,
        final int dataMap, final int key1, final CompactSetNode node1, final CompactSetNode node2,
        final CompactSetNode node3) {
      super(mutator, nodeMap, dataMap);
      this.key1 = key1;
      this.node1 = node1;
      this.node2 = node2;
      this.node3 = node3;

      assert nodeInvariant();
    }

    @Override
    boolean hasSlots() {
      return true;
    }

    @Override
    int slotArity() {
      return 4;
    }

    @Override
    Object getSlot(final int index) {
      final int boundary = payloadArity();

      if (index < boundary) {
        return getKey(index);
      } else {
        return getNode(index - boundary);
      }
    }

    @Override
    CompactSetNode getNode(int index) {
      switch (index) {
        case 0:
          return node1;
        case 1:
          return node2;
        case 2:
          return node3;
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    int getKey(int index) {
      switch (index) {
        case 0:
          return key1;
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    boolean hasNodes() {
      return true;
    }

    @Override
    int nodeArity() {
      return 3;
    }

    @Override
    boolean hasPayload() {
      return true;
    }

    @Override
    int payloadArity() {
      return 1;
    }

    @Override
    byte sizePredicate() {
      return SIZE_MORE_THAN_ONE;
    }

    @Override
    CompactSetNode copyAndInsertValue(AtomicReference<Thread> mutator, final int bitpos,
        final int key) {
      final int valIndex = dataIndex(bitpos);

      final int nodeMap = (int) (this.nodeMap());
      final int dataMap = (int) (this.dataMap() | bitpos);

      switch (valIndex) {
        case 0:
          return nodeOf(mutator, nodeMap, dataMap, key, key1, node1, node2, node3);
        case 1:
          return nodeOf(mutator, nodeMap, dataMap, key1, key, node1, node2, node3);
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    CompactSetNode copyAndRemoveValue(AtomicReference<Thread> mutator, final int bitpos) {
      final int valIndex = dataIndex(bitpos);

      final int nodeMap = (int) (this.nodeMap());
      final int dataMap = (int) (this.dataMap() ^ bitpos);

      switch (valIndex) {
        case 0:
          return nodeOf(mutator, nodeMap, dataMap, node1, node2, node3);
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    CompactSetNode copyAndSetNode(AtomicReference<Thread> mutator, final int bitpos,
        CompactSetNode node) {
      final int index = nodeIndex(bitpos);

      final int nodeMap = this.nodeMap();
      final int dataMap = this.dataMap();

      switch (index) {
        case 0:
          return nodeOf(mutator, nodeMap, dataMap, key1, node, node2, node3);
        case 1:
          return nodeOf(mutator, nodeMap, dataMap, key1, node1, node, node3);
        case 2:
          return nodeOf(mutator, nodeMap, dataMap, key1, node1, node2, node);
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    CompactSetNode copyAndMigrateFromInlineToNode(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetNode node) {
      final int bitIndex = nodeIndex(bitpos);
      final int valIndex = dataIndex(bitpos);

      final int nodeMap = (int) (this.nodeMap() | bitpos);
      final int dataMap = (int) (this.dataMap() ^ bitpos);

      switch (valIndex) {
        case 0:
          switch (bitIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, node, node1, node2, node3);
            case 1:
              return nodeOf(mutator, nodeMap, dataMap, node1, node, node2, node3);
            case 2:
              return nodeOf(mutator, nodeMap, dataMap, node1, node2, node, node3);
            case 3:
              return nodeOf(mutator, nodeMap, dataMap, node1, node2, node3, node);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    CompactSetNode copyAndMigrateFromNodeToInline(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetNode node) {
      final int bitIndex = nodeIndex(bitpos);
      final int valIndex = dataIndex(bitpos);

      final int nodeMap = (int) (this.nodeMap() ^ bitpos);
      final int dataMap = (int) (this.dataMap() | bitpos);

      final int key = node.getKey(0);

      switch (bitIndex) {
        case 0:
          switch (valIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key, key1, node2, node3);
            case 1:
              return nodeOf(mutator, nodeMap, dataMap, key1, key, node2, node3);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        case 1:
          switch (valIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key, key1, node1, node3);
            case 1:
              return nodeOf(mutator, nodeMap, dataMap, key1, key, node1, node3);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        case 2:
          switch (valIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key, key1, node1, node2);
            case 1:
              return nodeOf(mutator, nodeMap, dataMap, key1, key, node1, node2);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;

      result = prime * result + ((int) nodeMap());
      result = prime * result + ((int) dataMap());

      result = prime * result + (int) key1;

      result = prime * result + node1.hashCode();
      result = prime * result + node2.hashCode();
      result = prime * result + node3.hashCode();

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
      Set1To3Node_5Bits_Spec0To8_IntKey that = (Set1To3Node_5Bits_Spec0To8_IntKey) other;

      if (nodeMap() != that.nodeMap()) {
        return false;
      }
      if (dataMap() != that.dataMap()) {
        return false;
      }

      if (!(key1 == that.key1)) {
        return false;
      }
      if (!(node1.equals(that.node1))) {
        return false;
      }
      if (!(node2.equals(that.node2))) {
        return false;
      }
      if (!(node3.equals(that.node3))) {
        return false;
      }

      return true;
    }

  }

  private static final class Set1To4Node_5Bits_Spec0To8_IntKey extends CompactMixedSetNode {

    private final int key1;
    private final CompactSetNode node1;
    private final CompactSetNode node2;
    private final CompactSetNode node3;
    private final CompactSetNode node4;

    Set1To4Node_5Bits_Spec0To8_IntKey(final AtomicReference<Thread> mutator, final int nodeMap,
        final int dataMap, final int key1, final CompactSetNode node1, final CompactSetNode node2,
        final CompactSetNode node3, final CompactSetNode node4) {
      super(mutator, nodeMap, dataMap);
      this.key1 = key1;
      this.node1 = node1;
      this.node2 = node2;
      this.node3 = node3;
      this.node4 = node4;

      assert nodeInvariant();
    }

    @Override
    boolean hasSlots() {
      return true;
    }

    @Override
    int slotArity() {
      return 5;
    }

    @Override
    Object getSlot(final int index) {
      final int boundary = payloadArity();

      if (index < boundary) {
        return getKey(index);
      } else {
        return getNode(index - boundary);
      }
    }

    @Override
    CompactSetNode getNode(int index) {
      switch (index) {
        case 0:
          return node1;
        case 1:
          return node2;
        case 2:
          return node3;
        case 3:
          return node4;
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    int getKey(int index) {
      switch (index) {
        case 0:
          return key1;
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    boolean hasNodes() {
      return true;
    }

    @Override
    int nodeArity() {
      return 4;
    }

    @Override
    boolean hasPayload() {
      return true;
    }

    @Override
    int payloadArity() {
      return 1;
    }

    @Override
    byte sizePredicate() {
      return SIZE_MORE_THAN_ONE;
    }

    @Override
    CompactSetNode copyAndInsertValue(AtomicReference<Thread> mutator, final int bitpos,
        final int key) {
      final int valIndex = dataIndex(bitpos);

      final int nodeMap = (int) (this.nodeMap());
      final int dataMap = (int) (this.dataMap() | bitpos);

      switch (valIndex) {
        case 0:
          return nodeOf(mutator, nodeMap, dataMap, key, key1, node1, node2, node3, node4);
        case 1:
          return nodeOf(mutator, nodeMap, dataMap, key1, key, node1, node2, node3, node4);
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    CompactSetNode copyAndRemoveValue(AtomicReference<Thread> mutator, final int bitpos) {
      final int valIndex = dataIndex(bitpos);

      final int nodeMap = (int) (this.nodeMap());
      final int dataMap = (int) (this.dataMap() ^ bitpos);

      switch (valIndex) {
        case 0:
          return nodeOf(mutator, nodeMap, dataMap, node1, node2, node3, node4);
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    CompactSetNode copyAndSetNode(AtomicReference<Thread> mutator, final int bitpos,
        CompactSetNode node) {
      final int index = nodeIndex(bitpos);

      final int nodeMap = this.nodeMap();
      final int dataMap = this.dataMap();

      switch (index) {
        case 0:
          return nodeOf(mutator, nodeMap, dataMap, key1, node, node2, node3, node4);
        case 1:
          return nodeOf(mutator, nodeMap, dataMap, key1, node1, node, node3, node4);
        case 2:
          return nodeOf(mutator, nodeMap, dataMap, key1, node1, node2, node, node4);
        case 3:
          return nodeOf(mutator, nodeMap, dataMap, key1, node1, node2, node3, node);
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    CompactSetNode copyAndMigrateFromInlineToNode(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetNode node) {
      final int bitIndex = nodeIndex(bitpos);
      final int valIndex = dataIndex(bitpos);

      final int nodeMap = (int) (this.nodeMap() | bitpos);
      final int dataMap = (int) (this.dataMap() ^ bitpos);

      switch (valIndex) {
        case 0:
          switch (bitIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, node, node1, node2, node3, node4);
            case 1:
              return nodeOf(mutator, nodeMap, dataMap, node1, node, node2, node3, node4);
            case 2:
              return nodeOf(mutator, nodeMap, dataMap, node1, node2, node, node3, node4);
            case 3:
              return nodeOf(mutator, nodeMap, dataMap, node1, node2, node3, node, node4);
            case 4:
              return nodeOf(mutator, nodeMap, dataMap, node1, node2, node3, node4, node);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    CompactSetNode copyAndMigrateFromNodeToInline(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetNode node) {
      final int bitIndex = nodeIndex(bitpos);
      final int valIndex = dataIndex(bitpos);

      final int nodeMap = (int) (this.nodeMap() ^ bitpos);
      final int dataMap = (int) (this.dataMap() | bitpos);

      final int key = node.getKey(0);

      switch (bitIndex) {
        case 0:
          switch (valIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key, key1, node2, node3, node4);
            case 1:
              return nodeOf(mutator, nodeMap, dataMap, key1, key, node2, node3, node4);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        case 1:
          switch (valIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key, key1, node1, node3, node4);
            case 1:
              return nodeOf(mutator, nodeMap, dataMap, key1, key, node1, node3, node4);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        case 2:
          switch (valIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key, key1, node1, node2, node4);
            case 1:
              return nodeOf(mutator, nodeMap, dataMap, key1, key, node1, node2, node4);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        case 3:
          switch (valIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key, key1, node1, node2, node3);
            case 1:
              return nodeOf(mutator, nodeMap, dataMap, key1, key, node1, node2, node3);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;

      result = prime * result + ((int) nodeMap());
      result = prime * result + ((int) dataMap());

      result = prime * result + (int) key1;

      result = prime * result + node1.hashCode();
      result = prime * result + node2.hashCode();
      result = prime * result + node3.hashCode();
      result = prime * result + node4.hashCode();

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
      Set1To4Node_5Bits_Spec0To8_IntKey that = (Set1To4Node_5Bits_Spec0To8_IntKey) other;

      if (nodeMap() != that.nodeMap()) {
        return false;
      }
      if (dataMap() != that.dataMap()) {
        return false;
      }

      if (!(key1 == that.key1)) {
        return false;
      }
      if (!(node1.equals(that.node1))) {
        return false;
      }
      if (!(node2.equals(that.node2))) {
        return false;
      }
      if (!(node3.equals(that.node3))) {
        return false;
      }
      if (!(node4.equals(that.node4))) {
        return false;
      }

      return true;
    }

  }

  private static final class Set1To5Node_5Bits_Spec0To8_IntKey extends CompactMixedSetNode {

    private final int key1;
    private final CompactSetNode node1;
    private final CompactSetNode node2;
    private final CompactSetNode node3;
    private final CompactSetNode node4;
    private final CompactSetNode node5;

    Set1To5Node_5Bits_Spec0To8_IntKey(final AtomicReference<Thread> mutator, final int nodeMap,
        final int dataMap, final int key1, final CompactSetNode node1, final CompactSetNode node2,
        final CompactSetNode node3, final CompactSetNode node4, final CompactSetNode node5) {
      super(mutator, nodeMap, dataMap);
      this.key1 = key1;
      this.node1 = node1;
      this.node2 = node2;
      this.node3 = node3;
      this.node4 = node4;
      this.node5 = node5;

      assert nodeInvariant();
    }

    @Override
    boolean hasSlots() {
      return true;
    }

    @Override
    int slotArity() {
      return 6;
    }

    @Override
    Object getSlot(final int index) {
      final int boundary = payloadArity();

      if (index < boundary) {
        return getKey(index);
      } else {
        return getNode(index - boundary);
      }
    }

    @Override
    CompactSetNode getNode(int index) {
      switch (index) {
        case 0:
          return node1;
        case 1:
          return node2;
        case 2:
          return node3;
        case 3:
          return node4;
        case 4:
          return node5;
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    int getKey(int index) {
      switch (index) {
        case 0:
          return key1;
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    boolean hasNodes() {
      return true;
    }

    @Override
    int nodeArity() {
      return 5;
    }

    @Override
    boolean hasPayload() {
      return true;
    }

    @Override
    int payloadArity() {
      return 1;
    }

    @Override
    byte sizePredicate() {
      return SIZE_MORE_THAN_ONE;
    }

    @Override
    CompactSetNode copyAndInsertValue(AtomicReference<Thread> mutator, final int bitpos,
        final int key) {
      final int valIndex = dataIndex(bitpos);

      final int nodeMap = (int) (this.nodeMap());
      final int dataMap = (int) (this.dataMap() | bitpos);

      switch (valIndex) {
        case 0:
          return nodeOf(mutator, nodeMap, dataMap, key, key1, node1, node2, node3, node4, node5);
        case 1:
          return nodeOf(mutator, nodeMap, dataMap, key1, key, node1, node2, node3, node4, node5);
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    CompactSetNode copyAndRemoveValue(AtomicReference<Thread> mutator, final int bitpos) {
      final int valIndex = dataIndex(bitpos);

      final int nodeMap = (int) (this.nodeMap());
      final int dataMap = (int) (this.dataMap() ^ bitpos);

      switch (valIndex) {
        case 0:
          return nodeOf(mutator, nodeMap, dataMap, node1, node2, node3, node4, node5);
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    CompactSetNode copyAndSetNode(AtomicReference<Thread> mutator, final int bitpos,
        CompactSetNode node) {
      final int index = nodeIndex(bitpos);

      final int nodeMap = this.nodeMap();
      final int dataMap = this.dataMap();

      switch (index) {
        case 0:
          return nodeOf(mutator, nodeMap, dataMap, key1, node, node2, node3, node4, node5);
        case 1:
          return nodeOf(mutator, nodeMap, dataMap, key1, node1, node, node3, node4, node5);
        case 2:
          return nodeOf(mutator, nodeMap, dataMap, key1, node1, node2, node, node4, node5);
        case 3:
          return nodeOf(mutator, nodeMap, dataMap, key1, node1, node2, node3, node, node5);
        case 4:
          return nodeOf(mutator, nodeMap, dataMap, key1, node1, node2, node3, node4, node);
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    CompactSetNode copyAndMigrateFromInlineToNode(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetNode node) {
      final int bitIndex = nodeIndex(bitpos);
      final int valIndex = dataIndex(bitpos);

      final int nodeMap = (int) (this.nodeMap() | bitpos);
      final int dataMap = (int) (this.dataMap() ^ bitpos);

      switch (valIndex) {
        case 0:
          switch (bitIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, node, node1, node2, node3, node4, node5);
            case 1:
              return nodeOf(mutator, nodeMap, dataMap, node1, node, node2, node3, node4, node5);
            case 2:
              return nodeOf(mutator, nodeMap, dataMap, node1, node2, node, node3, node4, node5);
            case 3:
              return nodeOf(mutator, nodeMap, dataMap, node1, node2, node3, node, node4, node5);
            case 4:
              return nodeOf(mutator, nodeMap, dataMap, node1, node2, node3, node4, node, node5);
            case 5:
              return nodeOf(mutator, nodeMap, dataMap, node1, node2, node3, node4, node5, node);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    CompactSetNode copyAndMigrateFromNodeToInline(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetNode node) {
      final int bitIndex = nodeIndex(bitpos);
      final int valIndex = dataIndex(bitpos);

      final int nodeMap = (int) (this.nodeMap() ^ bitpos);
      final int dataMap = (int) (this.dataMap() | bitpos);

      final int key = node.getKey(0);

      switch (bitIndex) {
        case 0:
          switch (valIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key, key1, node2, node3, node4, node5);
            case 1:
              return nodeOf(mutator, nodeMap, dataMap, key1, key, node2, node3, node4, node5);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        case 1:
          switch (valIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key, key1, node1, node3, node4, node5);
            case 1:
              return nodeOf(mutator, nodeMap, dataMap, key1, key, node1, node3, node4, node5);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        case 2:
          switch (valIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key, key1, node1, node2, node4, node5);
            case 1:
              return nodeOf(mutator, nodeMap, dataMap, key1, key, node1, node2, node4, node5);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        case 3:
          switch (valIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key, key1, node1, node2, node3, node5);
            case 1:
              return nodeOf(mutator, nodeMap, dataMap, key1, key, node1, node2, node3, node5);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        case 4:
          switch (valIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key, key1, node1, node2, node3, node4);
            case 1:
              return nodeOf(mutator, nodeMap, dataMap, key1, key, node1, node2, node3, node4);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;

      result = prime * result + ((int) nodeMap());
      result = prime * result + ((int) dataMap());

      result = prime * result + (int) key1;

      result = prime * result + node1.hashCode();
      result = prime * result + node2.hashCode();
      result = prime * result + node3.hashCode();
      result = prime * result + node4.hashCode();
      result = prime * result + node5.hashCode();

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
      Set1To5Node_5Bits_Spec0To8_IntKey that = (Set1To5Node_5Bits_Spec0To8_IntKey) other;

      if (nodeMap() != that.nodeMap()) {
        return false;
      }
      if (dataMap() != that.dataMap()) {
        return false;
      }

      if (!(key1 == that.key1)) {
        return false;
      }
      if (!(node1.equals(that.node1))) {
        return false;
      }
      if (!(node2.equals(that.node2))) {
        return false;
      }
      if (!(node3.equals(that.node3))) {
        return false;
      }
      if (!(node4.equals(that.node4))) {
        return false;
      }
      if (!(node5.equals(that.node5))) {
        return false;
      }

      return true;
    }

  }

  private static final class Set1To6Node_5Bits_Spec0To8_IntKey extends CompactMixedSetNode {

    private final int key1;
    private final CompactSetNode node1;
    private final CompactSetNode node2;
    private final CompactSetNode node3;
    private final CompactSetNode node4;
    private final CompactSetNode node5;
    private final CompactSetNode node6;

    Set1To6Node_5Bits_Spec0To8_IntKey(final AtomicReference<Thread> mutator, final int nodeMap,
        final int dataMap, final int key1, final CompactSetNode node1, final CompactSetNode node2,
        final CompactSetNode node3, final CompactSetNode node4, final CompactSetNode node5,
        final CompactSetNode node6) {
      super(mutator, nodeMap, dataMap);
      this.key1 = key1;
      this.node1 = node1;
      this.node2 = node2;
      this.node3 = node3;
      this.node4 = node4;
      this.node5 = node5;
      this.node6 = node6;

      assert nodeInvariant();
    }

    @Override
    boolean hasSlots() {
      return true;
    }

    @Override
    int slotArity() {
      return 7;
    }

    @Override
    Object getSlot(final int index) {
      final int boundary = payloadArity();

      if (index < boundary) {
        return getKey(index);
      } else {
        return getNode(index - boundary);
      }
    }

    @Override
    CompactSetNode getNode(int index) {
      switch (index) {
        case 0:
          return node1;
        case 1:
          return node2;
        case 2:
          return node3;
        case 3:
          return node4;
        case 4:
          return node5;
        case 5:
          return node6;
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    int getKey(int index) {
      switch (index) {
        case 0:
          return key1;
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    boolean hasNodes() {
      return true;
    }

    @Override
    int nodeArity() {
      return 6;
    }

    @Override
    boolean hasPayload() {
      return true;
    }

    @Override
    int payloadArity() {
      return 1;
    }

    @Override
    byte sizePredicate() {
      return SIZE_MORE_THAN_ONE;
    }

    @Override
    CompactSetNode copyAndInsertValue(AtomicReference<Thread> mutator, final int bitpos,
        final int key) {
      final int valIndex = dataIndex(bitpos);

      final int nodeMap = (int) (this.nodeMap());
      final int dataMap = (int) (this.dataMap() | bitpos);

      switch (valIndex) {
        case 0:
          return nodeOf(mutator, nodeMap, dataMap, key, key1, node1, node2, node3, node4, node5,
              node6);
        case 1:
          return nodeOf(mutator, nodeMap, dataMap, key1, key, node1, node2, node3, node4, node5,
              node6);
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    CompactSetNode copyAndRemoveValue(AtomicReference<Thread> mutator, final int bitpos) {
      final int valIndex = dataIndex(bitpos);

      final int nodeMap = (int) (this.nodeMap());
      final int dataMap = (int) (this.dataMap() ^ bitpos);

      switch (valIndex) {
        case 0:
          return nodeOf(mutator, nodeMap, dataMap, node1, node2, node3, node4, node5, node6);
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    CompactSetNode copyAndSetNode(AtomicReference<Thread> mutator, final int bitpos,
        CompactSetNode node) {
      final int index = nodeIndex(bitpos);

      final int nodeMap = this.nodeMap();
      final int dataMap = this.dataMap();

      switch (index) {
        case 0:
          return nodeOf(mutator, nodeMap, dataMap, key1, node, node2, node3, node4, node5, node6);
        case 1:
          return nodeOf(mutator, nodeMap, dataMap, key1, node1, node, node3, node4, node5, node6);
        case 2:
          return nodeOf(mutator, nodeMap, dataMap, key1, node1, node2, node, node4, node5, node6);
        case 3:
          return nodeOf(mutator, nodeMap, dataMap, key1, node1, node2, node3, node, node5, node6);
        case 4:
          return nodeOf(mutator, nodeMap, dataMap, key1, node1, node2, node3, node4, node, node6);
        case 5:
          return nodeOf(mutator, nodeMap, dataMap, key1, node1, node2, node3, node4, node5, node);
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    CompactSetNode copyAndMigrateFromInlineToNode(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetNode node) {
      final int bitIndex = nodeIndex(bitpos);
      final int valIndex = dataIndex(bitpos);

      final int nodeMap = (int) (this.nodeMap() | bitpos);
      final int dataMap = (int) (this.dataMap() ^ bitpos);

      switch (valIndex) {
        case 0:
          switch (bitIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, node, node1, node2, node3, node4, node5,
                  node6);
            case 1:
              return nodeOf(mutator, nodeMap, dataMap, node1, node, node2, node3, node4, node5,
                  node6);
            case 2:
              return nodeOf(mutator, nodeMap, dataMap, node1, node2, node, node3, node4, node5,
                  node6);
            case 3:
              return nodeOf(mutator, nodeMap, dataMap, node1, node2, node3, node, node4, node5,
                  node6);
            case 4:
              return nodeOf(mutator, nodeMap, dataMap, node1, node2, node3, node4, node, node5,
                  node6);
            case 5:
              return nodeOf(mutator, nodeMap, dataMap, node1, node2, node3, node4, node5, node,
                  node6);
            case 6:
              return nodeOf(mutator, nodeMap, dataMap, node1, node2, node3, node4, node5, node6,
                  node);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    CompactSetNode copyAndMigrateFromNodeToInline(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetNode node) {
      final int bitIndex = nodeIndex(bitpos);
      final int valIndex = dataIndex(bitpos);

      final int nodeMap = (int) (this.nodeMap() ^ bitpos);
      final int dataMap = (int) (this.dataMap() | bitpos);

      final int key = node.getKey(0);

      switch (bitIndex) {
        case 0:
          switch (valIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key, key1, node2, node3, node4, node5,
                  node6);
            case 1:
              return nodeOf(mutator, nodeMap, dataMap, key1, key, node2, node3, node4, node5,
                  node6);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        case 1:
          switch (valIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key, key1, node1, node3, node4, node5,
                  node6);
            case 1:
              return nodeOf(mutator, nodeMap, dataMap, key1, key, node1, node3, node4, node5,
                  node6);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        case 2:
          switch (valIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key, key1, node1, node2, node4, node5,
                  node6);
            case 1:
              return nodeOf(mutator, nodeMap, dataMap, key1, key, node1, node2, node4, node5,
                  node6);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        case 3:
          switch (valIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key, key1, node1, node2, node3, node5,
                  node6);
            case 1:
              return nodeOf(mutator, nodeMap, dataMap, key1, key, node1, node2, node3, node5,
                  node6);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        case 4:
          switch (valIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key, key1, node1, node2, node3, node4,
                  node6);
            case 1:
              return nodeOf(mutator, nodeMap, dataMap, key1, key, node1, node2, node3, node4,
                  node6);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        case 5:
          switch (valIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key, key1, node1, node2, node3, node4,
                  node5);
            case 1:
              return nodeOf(mutator, nodeMap, dataMap, key1, key, node1, node2, node3, node4,
                  node5);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;

      result = prime * result + ((int) nodeMap());
      result = prime * result + ((int) dataMap());

      result = prime * result + (int) key1;

      result = prime * result + node1.hashCode();
      result = prime * result + node2.hashCode();
      result = prime * result + node3.hashCode();
      result = prime * result + node4.hashCode();
      result = prime * result + node5.hashCode();
      result = prime * result + node6.hashCode();

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
      Set1To6Node_5Bits_Spec0To8_IntKey that = (Set1To6Node_5Bits_Spec0To8_IntKey) other;

      if (nodeMap() != that.nodeMap()) {
        return false;
      }
      if (dataMap() != that.dataMap()) {
        return false;
      }

      if (!(key1 == that.key1)) {
        return false;
      }
      if (!(node1.equals(that.node1))) {
        return false;
      }
      if (!(node2.equals(that.node2))) {
        return false;
      }
      if (!(node3.equals(that.node3))) {
        return false;
      }
      if (!(node4.equals(that.node4))) {
        return false;
      }
      if (!(node5.equals(that.node5))) {
        return false;
      }
      if (!(node6.equals(that.node6))) {
        return false;
      }

      return true;
    }

  }

  private static final class Set1To7Node_5Bits_Spec0To8_IntKey extends CompactMixedSetNode {

    private final int key1;
    private final CompactSetNode node1;
    private final CompactSetNode node2;
    private final CompactSetNode node3;
    private final CompactSetNode node4;
    private final CompactSetNode node5;
    private final CompactSetNode node6;
    private final CompactSetNode node7;

    Set1To7Node_5Bits_Spec0To8_IntKey(final AtomicReference<Thread> mutator, final int nodeMap,
        final int dataMap, final int key1, final CompactSetNode node1, final CompactSetNode node2,
        final CompactSetNode node3, final CompactSetNode node4, final CompactSetNode node5,
        final CompactSetNode node6, final CompactSetNode node7) {
      super(mutator, nodeMap, dataMap);
      this.key1 = key1;
      this.node1 = node1;
      this.node2 = node2;
      this.node3 = node3;
      this.node4 = node4;
      this.node5 = node5;
      this.node6 = node6;
      this.node7 = node7;

      assert nodeInvariant();
    }

    @Override
    boolean hasSlots() {
      return true;
    }

    @Override
    int slotArity() {
      return 8;
    }

    @Override
    Object getSlot(final int index) {
      final int boundary = payloadArity();

      if (index < boundary) {
        return getKey(index);
      } else {
        return getNode(index - boundary);
      }
    }

    @Override
    CompactSetNode getNode(int index) {
      switch (index) {
        case 0:
          return node1;
        case 1:
          return node2;
        case 2:
          return node3;
        case 3:
          return node4;
        case 4:
          return node5;
        case 5:
          return node6;
        case 6:
          return node7;
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    int getKey(int index) {
      switch (index) {
        case 0:
          return key1;
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    boolean hasNodes() {
      return true;
    }

    @Override
    int nodeArity() {
      return 7;
    }

    @Override
    boolean hasPayload() {
      return true;
    }

    @Override
    int payloadArity() {
      return 1;
    }

    @Override
    byte sizePredicate() {
      return SIZE_MORE_THAN_ONE;
    }

    @Override
    CompactSetNode copyAndInsertValue(AtomicReference<Thread> mutator, final int bitpos,
        final int key) {
      final int valIndex = dataIndex(bitpos);

      final int nodeMap = (int) (this.nodeMap());
      final int dataMap = (int) (this.dataMap() | bitpos);

      switch (valIndex) {
        case 0:
          return nodeOf(mutator, nodeMap, dataMap, key, key1, node1, node2, node3, node4, node5,
              node6, node7);
        case 1:
          return nodeOf(mutator, nodeMap, dataMap, key1, key, node1, node2, node3, node4, node5,
              node6, node7);
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    CompactSetNode copyAndRemoveValue(AtomicReference<Thread> mutator, final int bitpos) {
      final int valIndex = dataIndex(bitpos);

      final int nodeMap = (int) (this.nodeMap());
      final int dataMap = (int) (this.dataMap() ^ bitpos);

      switch (valIndex) {
        case 0:
          return nodeOf(mutator, nodeMap, dataMap, node1, node2, node3, node4, node5, node6, node7);
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    CompactSetNode copyAndSetNode(AtomicReference<Thread> mutator, final int bitpos,
        CompactSetNode node) {
      final int index = nodeIndex(bitpos);

      final int nodeMap = this.nodeMap();
      final int dataMap = this.dataMap();

      switch (index) {
        case 0:
          return nodeOf(mutator, nodeMap, dataMap, key1, node, node2, node3, node4, node5, node6,
              node7);
        case 1:
          return nodeOf(mutator, nodeMap, dataMap, key1, node1, node, node3, node4, node5, node6,
              node7);
        case 2:
          return nodeOf(mutator, nodeMap, dataMap, key1, node1, node2, node, node4, node5, node6,
              node7);
        case 3:
          return nodeOf(mutator, nodeMap, dataMap, key1, node1, node2, node3, node, node5, node6,
              node7);
        case 4:
          return nodeOf(mutator, nodeMap, dataMap, key1, node1, node2, node3, node4, node, node6,
              node7);
        case 5:
          return nodeOf(mutator, nodeMap, dataMap, key1, node1, node2, node3, node4, node5, node,
              node7);
        case 6:
          return nodeOf(mutator, nodeMap, dataMap, key1, node1, node2, node3, node4, node5, node6,
              node);
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    CompactSetNode copyAndMigrateFromInlineToNode(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetNode node) {
      final int bitIndex = nodeIndex(bitpos);
      final int valIndex = dataIndex(bitpos);

      final int nodeMap = (int) (this.nodeMap() | bitpos);
      final int dataMap = (int) (this.dataMap() ^ bitpos);

      switch (valIndex) {
        case 0:
          switch (bitIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, node, node1, node2, node3, node4, node5,
                  node6, node7);
            case 1:
              return nodeOf(mutator, nodeMap, dataMap, node1, node, node2, node3, node4, node5,
                  node6, node7);
            case 2:
              return nodeOf(mutator, nodeMap, dataMap, node1, node2, node, node3, node4, node5,
                  node6, node7);
            case 3:
              return nodeOf(mutator, nodeMap, dataMap, node1, node2, node3, node, node4, node5,
                  node6, node7);
            case 4:
              return nodeOf(mutator, nodeMap, dataMap, node1, node2, node3, node4, node, node5,
                  node6, node7);
            case 5:
              return nodeOf(mutator, nodeMap, dataMap, node1, node2, node3, node4, node5, node,
                  node6, node7);
            case 6:
              return nodeOf(mutator, nodeMap, dataMap, node1, node2, node3, node4, node5, node6,
                  node, node7);
            case 7:
              return nodeOf(mutator, nodeMap, dataMap, node1, node2, node3, node4, node5, node6,
                  node7, node);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    CompactSetNode copyAndMigrateFromNodeToInline(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetNode node) {
      final int bitIndex = nodeIndex(bitpos);
      final int valIndex = dataIndex(bitpos);

      final int nodeMap = (int) (this.nodeMap() ^ bitpos);
      final int dataMap = (int) (this.dataMap() | bitpos);

      final int key = node.getKey(0);

      switch (bitIndex) {
        case 0:
          switch (valIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key, key1, node2, node3, node4, node5, node6,
                  node7);
            case 1:
              return nodeOf(mutator, nodeMap, dataMap, key1, key, node2, node3, node4, node5, node6,
                  node7);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        case 1:
          switch (valIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key, key1, node1, node3, node4, node5, node6,
                  node7);
            case 1:
              return nodeOf(mutator, nodeMap, dataMap, key1, key, node1, node3, node4, node5, node6,
                  node7);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        case 2:
          switch (valIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key, key1, node1, node2, node4, node5, node6,
                  node7);
            case 1:
              return nodeOf(mutator, nodeMap, dataMap, key1, key, node1, node2, node4, node5, node6,
                  node7);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        case 3:
          switch (valIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key, key1, node1, node2, node3, node5, node6,
                  node7);
            case 1:
              return nodeOf(mutator, nodeMap, dataMap, key1, key, node1, node2, node3, node5, node6,
                  node7);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        case 4:
          switch (valIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key, key1, node1, node2, node3, node4, node6,
                  node7);
            case 1:
              return nodeOf(mutator, nodeMap, dataMap, key1, key, node1, node2, node3, node4, node6,
                  node7);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        case 5:
          switch (valIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key, key1, node1, node2, node3, node4, node5,
                  node7);
            case 1:
              return nodeOf(mutator, nodeMap, dataMap, key1, key, node1, node2, node3, node4, node5,
                  node7);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        case 6:
          switch (valIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key, key1, node1, node2, node3, node4, node5,
                  node6);
            case 1:
              return nodeOf(mutator, nodeMap, dataMap, key1, key, node1, node2, node3, node4, node5,
                  node6);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;

      result = prime * result + ((int) nodeMap());
      result = prime * result + ((int) dataMap());

      result = prime * result + (int) key1;

      result = prime * result + node1.hashCode();
      result = prime * result + node2.hashCode();
      result = prime * result + node3.hashCode();
      result = prime * result + node4.hashCode();
      result = prime * result + node5.hashCode();
      result = prime * result + node6.hashCode();
      result = prime * result + node7.hashCode();

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
      Set1To7Node_5Bits_Spec0To8_IntKey that = (Set1To7Node_5Bits_Spec0To8_IntKey) other;

      if (nodeMap() != that.nodeMap()) {
        return false;
      }
      if (dataMap() != that.dataMap()) {
        return false;
      }

      if (!(key1 == that.key1)) {
        return false;
      }
      if (!(node1.equals(that.node1))) {
        return false;
      }
      if (!(node2.equals(that.node2))) {
        return false;
      }
      if (!(node3.equals(that.node3))) {
        return false;
      }
      if (!(node4.equals(that.node4))) {
        return false;
      }
      if (!(node5.equals(that.node5))) {
        return false;
      }
      if (!(node6.equals(that.node6))) {
        return false;
      }
      if (!(node7.equals(that.node7))) {
        return false;
      }

      return true;
    }

  }

  private static final class Set2To0Node_5Bits_Spec0To8_IntKey extends CompactValuesOnlySetNode {

    private final int key1;
    private final int key2;

    Set2To0Node_5Bits_Spec0To8_IntKey(final AtomicReference<Thread> mutator, final int nodeMap,
        final int dataMap, final int key1, final int key2) {
      super(mutator, nodeMap, dataMap);
      this.key1 = key1;
      this.key2 = key2;

      assert nodeInvariant();
    }

    @Override
    boolean hasSlots() {
      return true;
    }

    @Override
    int slotArity() {
      return 2;
    }

    @Override
    Object getSlot(final int index) {
      final int boundary = payloadArity();

      if (index < boundary) {
        return getKey(index);
      } else {
        return getNode(index - boundary);
      }
    }

    @Override
    CompactSetNode getNode(int index) {
      throw new IllegalStateException("Index out of range.");
    }

    @Override
    int getKey(int index) {
      switch (index) {
        case 0:
          return key1;
        case 1:
          return key2;
        default:
          throw new IllegalStateException("Index out of range.");
      }
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
    boolean hasPayload() {
      return true;
    }

    @Override
    int payloadArity() {
      return 2;
    }

    @Override
    byte sizePredicate() {
      return SIZE_MORE_THAN_ONE;
    }

    @Override
    CompactSetNode copyAndInsertValue(AtomicReference<Thread> mutator, final int bitpos,
        final int key) {
      final int valIndex = dataIndex(bitpos);

      final int nodeMap = (int) (this.nodeMap());
      final int dataMap = (int) (this.dataMap() | bitpos);

      switch (valIndex) {
        case 0:
          return nodeOf(mutator, nodeMap, dataMap, key, key1, key2);
        case 1:
          return nodeOf(mutator, nodeMap, dataMap, key1, key, key2);
        case 2:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2, key);
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    CompactSetNode copyAndRemoveValue(AtomicReference<Thread> mutator, final int bitpos) {
      final int valIndex = dataIndex(bitpos);

      final int nodeMap = (int) (this.nodeMap());
      final int dataMap = (int) (this.dataMap() ^ bitpos);

      switch (valIndex) {
        case 0:
          return nodeOf(mutator, nodeMap, dataMap, key2);
        case 1:
          return nodeOf(mutator, nodeMap, dataMap, key1);
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    CompactSetNode copyAndSetNode(AtomicReference<Thread> mutator, final int bitpos,
        CompactSetNode node) {
      throw new IllegalStateException("Index out of range.");
    }

    @Override
    CompactSetNode copyAndMigrateFromInlineToNode(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetNode node) {
      final int bitIndex = nodeIndex(bitpos);
      final int valIndex = dataIndex(bitpos);

      final int nodeMap = (int) (this.nodeMap() | bitpos);
      final int dataMap = (int) (this.dataMap() ^ bitpos);

      switch (valIndex) {
        case 0:
          switch (bitIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key2, node);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        case 1:
          switch (bitIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key1, node);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    CompactSetNode copyAndMigrateFromNodeToInline(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetNode node) {
      throw new IllegalStateException("Index out of range.");
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;

      result = prime * result + ((int) nodeMap());
      result = prime * result + ((int) dataMap());

      result = prime * result + (int) key1;
      result = prime * result + (int) key2;

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
      Set2To0Node_5Bits_Spec0To8_IntKey that = (Set2To0Node_5Bits_Spec0To8_IntKey) other;

      if (nodeMap() != that.nodeMap()) {
        return false;
      }
      if (dataMap() != that.dataMap()) {
        return false;
      }

      if (!(key1 == that.key1)) {
        return false;
      }
      if (!(key2 == that.key2)) {
        return false;
      }

      return true;
    }

  }

  private static final class Set2To1Node_5Bits_Spec0To8_IntKey extends CompactMixedSetNode {

    private final int key1;
    private final int key2;
    private final CompactSetNode node1;

    Set2To1Node_5Bits_Spec0To8_IntKey(final AtomicReference<Thread> mutator, final int nodeMap,
        final int dataMap, final int key1, final int key2, final CompactSetNode node1) {
      super(mutator, nodeMap, dataMap);
      this.key1 = key1;
      this.key2 = key2;
      this.node1 = node1;

      assert nodeInvariant();
    }

    @Override
    boolean hasSlots() {
      return true;
    }

    @Override
    int slotArity() {
      return 3;
    }

    @Override
    Object getSlot(final int index) {
      final int boundary = payloadArity();

      if (index < boundary) {
        return getKey(index);
      } else {
        return getNode(index - boundary);
      }
    }

    @Override
    CompactSetNode getNode(int index) {
      switch (index) {
        case 0:
          return node1;
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    int getKey(int index) {
      switch (index) {
        case 0:
          return key1;
        case 1:
          return key2;
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    boolean hasNodes() {
      return true;
    }

    @Override
    int nodeArity() {
      return 1;
    }

    @Override
    boolean hasPayload() {
      return true;
    }

    @Override
    int payloadArity() {
      return 2;
    }

    @Override
    byte sizePredicate() {
      return SIZE_MORE_THAN_ONE;
    }

    @Override
    CompactSetNode copyAndInsertValue(AtomicReference<Thread> mutator, final int bitpos,
        final int key) {
      final int valIndex = dataIndex(bitpos);

      final int nodeMap = (int) (this.nodeMap());
      final int dataMap = (int) (this.dataMap() | bitpos);

      switch (valIndex) {
        case 0:
          return nodeOf(mutator, nodeMap, dataMap, key, key1, key2, node1);
        case 1:
          return nodeOf(mutator, nodeMap, dataMap, key1, key, key2, node1);
        case 2:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2, key, node1);
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    CompactSetNode copyAndRemoveValue(AtomicReference<Thread> mutator, final int bitpos) {
      final int valIndex = dataIndex(bitpos);

      final int nodeMap = (int) (this.nodeMap());
      final int dataMap = (int) (this.dataMap() ^ bitpos);

      switch (valIndex) {
        case 0:
          return nodeOf(mutator, nodeMap, dataMap, key2, node1);
        case 1:
          return nodeOf(mutator, nodeMap, dataMap, key1, node1);
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    CompactSetNode copyAndSetNode(AtomicReference<Thread> mutator, final int bitpos,
        CompactSetNode node) {
      final int index = nodeIndex(bitpos);

      final int nodeMap = this.nodeMap();
      final int dataMap = this.dataMap();

      switch (index) {
        case 0:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2, node);
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    CompactSetNode copyAndMigrateFromInlineToNode(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetNode node) {
      final int bitIndex = nodeIndex(bitpos);
      final int valIndex = dataIndex(bitpos);

      final int nodeMap = (int) (this.nodeMap() | bitpos);
      final int dataMap = (int) (this.dataMap() ^ bitpos);

      switch (valIndex) {
        case 0:
          switch (bitIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key2, node, node1);
            case 1:
              return nodeOf(mutator, nodeMap, dataMap, key2, node1, node);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        case 1:
          switch (bitIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key1, node, node1);
            case 1:
              return nodeOf(mutator, nodeMap, dataMap, key1, node1, node);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    CompactSetNode copyAndMigrateFromNodeToInline(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetNode node) {
      final int bitIndex = nodeIndex(bitpos);
      final int valIndex = dataIndex(bitpos);

      final int nodeMap = (int) (this.nodeMap() ^ bitpos);
      final int dataMap = (int) (this.dataMap() | bitpos);

      final int key = node.getKey(0);

      switch (bitIndex) {
        case 0:
          switch (valIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key, key1, key2);
            case 1:
              return nodeOf(mutator, nodeMap, dataMap, key1, key, key2);
            case 2:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;

      result = prime * result + ((int) nodeMap());
      result = prime * result + ((int) dataMap());

      result = prime * result + (int) key1;
      result = prime * result + (int) key2;

      result = prime * result + node1.hashCode();

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
      Set2To1Node_5Bits_Spec0To8_IntKey that = (Set2To1Node_5Bits_Spec0To8_IntKey) other;

      if (nodeMap() != that.nodeMap()) {
        return false;
      }
      if (dataMap() != that.dataMap()) {
        return false;
      }

      if (!(key1 == that.key1)) {
        return false;
      }
      if (!(key2 == that.key2)) {
        return false;
      }
      if (!(node1.equals(that.node1))) {
        return false;
      }

      return true;
    }

  }

  private static final class Set2To2Node_5Bits_Spec0To8_IntKey extends CompactMixedSetNode {

    private final int key1;
    private final int key2;
    private final CompactSetNode node1;
    private final CompactSetNode node2;

    Set2To2Node_5Bits_Spec0To8_IntKey(final AtomicReference<Thread> mutator, final int nodeMap,
        final int dataMap, final int key1, final int key2, final CompactSetNode node1,
        final CompactSetNode node2) {
      super(mutator, nodeMap, dataMap);
      this.key1 = key1;
      this.key2 = key2;
      this.node1 = node1;
      this.node2 = node2;

      assert nodeInvariant();
    }

    @Override
    boolean hasSlots() {
      return true;
    }

    @Override
    int slotArity() {
      return 4;
    }

    @Override
    Object getSlot(final int index) {
      final int boundary = payloadArity();

      if (index < boundary) {
        return getKey(index);
      } else {
        return getNode(index - boundary);
      }
    }

    @Override
    CompactSetNode getNode(int index) {
      switch (index) {
        case 0:
          return node1;
        case 1:
          return node2;
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    int getKey(int index) {
      switch (index) {
        case 0:
          return key1;
        case 1:
          return key2;
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    boolean hasNodes() {
      return true;
    }

    @Override
    int nodeArity() {
      return 2;
    }

    @Override
    boolean hasPayload() {
      return true;
    }

    @Override
    int payloadArity() {
      return 2;
    }

    @Override
    byte sizePredicate() {
      return SIZE_MORE_THAN_ONE;
    }

    @Override
    CompactSetNode copyAndInsertValue(AtomicReference<Thread> mutator, final int bitpos,
        final int key) {
      final int valIndex = dataIndex(bitpos);

      final int nodeMap = (int) (this.nodeMap());
      final int dataMap = (int) (this.dataMap() | bitpos);

      switch (valIndex) {
        case 0:
          return nodeOf(mutator, nodeMap, dataMap, key, key1, key2, node1, node2);
        case 1:
          return nodeOf(mutator, nodeMap, dataMap, key1, key, key2, node1, node2);
        case 2:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2, key, node1, node2);
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    CompactSetNode copyAndRemoveValue(AtomicReference<Thread> mutator, final int bitpos) {
      final int valIndex = dataIndex(bitpos);

      final int nodeMap = (int) (this.nodeMap());
      final int dataMap = (int) (this.dataMap() ^ bitpos);

      switch (valIndex) {
        case 0:
          return nodeOf(mutator, nodeMap, dataMap, key2, node1, node2);
        case 1:
          return nodeOf(mutator, nodeMap, dataMap, key1, node1, node2);
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    CompactSetNode copyAndSetNode(AtomicReference<Thread> mutator, final int bitpos,
        CompactSetNode node) {
      final int index = nodeIndex(bitpos);

      final int nodeMap = this.nodeMap();
      final int dataMap = this.dataMap();

      switch (index) {
        case 0:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2, node, node2);
        case 1:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2, node1, node);
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    CompactSetNode copyAndMigrateFromInlineToNode(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetNode node) {
      final int bitIndex = nodeIndex(bitpos);
      final int valIndex = dataIndex(bitpos);

      final int nodeMap = (int) (this.nodeMap() | bitpos);
      final int dataMap = (int) (this.dataMap() ^ bitpos);

      switch (valIndex) {
        case 0:
          switch (bitIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key2, node, node1, node2);
            case 1:
              return nodeOf(mutator, nodeMap, dataMap, key2, node1, node, node2);
            case 2:
              return nodeOf(mutator, nodeMap, dataMap, key2, node1, node2, node);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        case 1:
          switch (bitIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key1, node, node1, node2);
            case 1:
              return nodeOf(mutator, nodeMap, dataMap, key1, node1, node, node2);
            case 2:
              return nodeOf(mutator, nodeMap, dataMap, key1, node1, node2, node);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    CompactSetNode copyAndMigrateFromNodeToInline(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetNode node) {
      final int bitIndex = nodeIndex(bitpos);
      final int valIndex = dataIndex(bitpos);

      final int nodeMap = (int) (this.nodeMap() ^ bitpos);
      final int dataMap = (int) (this.dataMap() | bitpos);

      final int key = node.getKey(0);

      switch (bitIndex) {
        case 0:
          switch (valIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key, key1, key2, node2);
            case 1:
              return nodeOf(mutator, nodeMap, dataMap, key1, key, key2, node2);
            case 2:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key, node2);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        case 1:
          switch (valIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key, key1, key2, node1);
            case 1:
              return nodeOf(mutator, nodeMap, dataMap, key1, key, key2, node1);
            case 2:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key, node1);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;

      result = prime * result + ((int) nodeMap());
      result = prime * result + ((int) dataMap());

      result = prime * result + (int) key1;
      result = prime * result + (int) key2;

      result = prime * result + node1.hashCode();
      result = prime * result + node2.hashCode();

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
      Set2To2Node_5Bits_Spec0To8_IntKey that = (Set2To2Node_5Bits_Spec0To8_IntKey) other;

      if (nodeMap() != that.nodeMap()) {
        return false;
      }
      if (dataMap() != that.dataMap()) {
        return false;
      }

      if (!(key1 == that.key1)) {
        return false;
      }
      if (!(key2 == that.key2)) {
        return false;
      }
      if (!(node1.equals(that.node1))) {
        return false;
      }
      if (!(node2.equals(that.node2))) {
        return false;
      }

      return true;
    }

  }

  private static final class Set2To3Node_5Bits_Spec0To8_IntKey extends CompactMixedSetNode {

    private final int key1;
    private final int key2;
    private final CompactSetNode node1;
    private final CompactSetNode node2;
    private final CompactSetNode node3;

    Set2To3Node_5Bits_Spec0To8_IntKey(final AtomicReference<Thread> mutator, final int nodeMap,
        final int dataMap, final int key1, final int key2, final CompactSetNode node1,
        final CompactSetNode node2, final CompactSetNode node3) {
      super(mutator, nodeMap, dataMap);
      this.key1 = key1;
      this.key2 = key2;
      this.node1 = node1;
      this.node2 = node2;
      this.node3 = node3;

      assert nodeInvariant();
    }

    @Override
    boolean hasSlots() {
      return true;
    }

    @Override
    int slotArity() {
      return 5;
    }

    @Override
    Object getSlot(final int index) {
      final int boundary = payloadArity();

      if (index < boundary) {
        return getKey(index);
      } else {
        return getNode(index - boundary);
      }
    }

    @Override
    CompactSetNode getNode(int index) {
      switch (index) {
        case 0:
          return node1;
        case 1:
          return node2;
        case 2:
          return node3;
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    int getKey(int index) {
      switch (index) {
        case 0:
          return key1;
        case 1:
          return key2;
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    boolean hasNodes() {
      return true;
    }

    @Override
    int nodeArity() {
      return 3;
    }

    @Override
    boolean hasPayload() {
      return true;
    }

    @Override
    int payloadArity() {
      return 2;
    }

    @Override
    byte sizePredicate() {
      return SIZE_MORE_THAN_ONE;
    }

    @Override
    CompactSetNode copyAndInsertValue(AtomicReference<Thread> mutator, final int bitpos,
        final int key) {
      final int valIndex = dataIndex(bitpos);

      final int nodeMap = (int) (this.nodeMap());
      final int dataMap = (int) (this.dataMap() | bitpos);

      switch (valIndex) {
        case 0:
          return nodeOf(mutator, nodeMap, dataMap, key, key1, key2, node1, node2, node3);
        case 1:
          return nodeOf(mutator, nodeMap, dataMap, key1, key, key2, node1, node2, node3);
        case 2:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2, key, node1, node2, node3);
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    CompactSetNode copyAndRemoveValue(AtomicReference<Thread> mutator, final int bitpos) {
      final int valIndex = dataIndex(bitpos);

      final int nodeMap = (int) (this.nodeMap());
      final int dataMap = (int) (this.dataMap() ^ bitpos);

      switch (valIndex) {
        case 0:
          return nodeOf(mutator, nodeMap, dataMap, key2, node1, node2, node3);
        case 1:
          return nodeOf(mutator, nodeMap, dataMap, key1, node1, node2, node3);
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    CompactSetNode copyAndSetNode(AtomicReference<Thread> mutator, final int bitpos,
        CompactSetNode node) {
      final int index = nodeIndex(bitpos);

      final int nodeMap = this.nodeMap();
      final int dataMap = this.dataMap();

      switch (index) {
        case 0:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2, node, node2, node3);
        case 1:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2, node1, node, node3);
        case 2:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2, node1, node2, node);
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    CompactSetNode copyAndMigrateFromInlineToNode(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetNode node) {
      final int bitIndex = nodeIndex(bitpos);
      final int valIndex = dataIndex(bitpos);

      final int nodeMap = (int) (this.nodeMap() | bitpos);
      final int dataMap = (int) (this.dataMap() ^ bitpos);

      switch (valIndex) {
        case 0:
          switch (bitIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key2, node, node1, node2, node3);
            case 1:
              return nodeOf(mutator, nodeMap, dataMap, key2, node1, node, node2, node3);
            case 2:
              return nodeOf(mutator, nodeMap, dataMap, key2, node1, node2, node, node3);
            case 3:
              return nodeOf(mutator, nodeMap, dataMap, key2, node1, node2, node3, node);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        case 1:
          switch (bitIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key1, node, node1, node2, node3);
            case 1:
              return nodeOf(mutator, nodeMap, dataMap, key1, node1, node, node2, node3);
            case 2:
              return nodeOf(mutator, nodeMap, dataMap, key1, node1, node2, node, node3);
            case 3:
              return nodeOf(mutator, nodeMap, dataMap, key1, node1, node2, node3, node);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    CompactSetNode copyAndMigrateFromNodeToInline(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetNode node) {
      final int bitIndex = nodeIndex(bitpos);
      final int valIndex = dataIndex(bitpos);

      final int nodeMap = (int) (this.nodeMap() ^ bitpos);
      final int dataMap = (int) (this.dataMap() | bitpos);

      final int key = node.getKey(0);

      switch (bitIndex) {
        case 0:
          switch (valIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key, key1, key2, node2, node3);
            case 1:
              return nodeOf(mutator, nodeMap, dataMap, key1, key, key2, node2, node3);
            case 2:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key, node2, node3);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        case 1:
          switch (valIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key, key1, key2, node1, node3);
            case 1:
              return nodeOf(mutator, nodeMap, dataMap, key1, key, key2, node1, node3);
            case 2:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key, node1, node3);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        case 2:
          switch (valIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key, key1, key2, node1, node2);
            case 1:
              return nodeOf(mutator, nodeMap, dataMap, key1, key, key2, node1, node2);
            case 2:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key, node1, node2);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;

      result = prime * result + ((int) nodeMap());
      result = prime * result + ((int) dataMap());

      result = prime * result + (int) key1;
      result = prime * result + (int) key2;

      result = prime * result + node1.hashCode();
      result = prime * result + node2.hashCode();
      result = prime * result + node3.hashCode();

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
      Set2To3Node_5Bits_Spec0To8_IntKey that = (Set2To3Node_5Bits_Spec0To8_IntKey) other;

      if (nodeMap() != that.nodeMap()) {
        return false;
      }
      if (dataMap() != that.dataMap()) {
        return false;
      }

      if (!(key1 == that.key1)) {
        return false;
      }
      if (!(key2 == that.key2)) {
        return false;
      }
      if (!(node1.equals(that.node1))) {
        return false;
      }
      if (!(node2.equals(that.node2))) {
        return false;
      }
      if (!(node3.equals(that.node3))) {
        return false;
      }

      return true;
    }

  }

  private static final class Set2To4Node_5Bits_Spec0To8_IntKey extends CompactMixedSetNode {

    private final int key1;
    private final int key2;
    private final CompactSetNode node1;
    private final CompactSetNode node2;
    private final CompactSetNode node3;
    private final CompactSetNode node4;

    Set2To4Node_5Bits_Spec0To8_IntKey(final AtomicReference<Thread> mutator, final int nodeMap,
        final int dataMap, final int key1, final int key2, final CompactSetNode node1,
        final CompactSetNode node2, final CompactSetNode node3, final CompactSetNode node4) {
      super(mutator, nodeMap, dataMap);
      this.key1 = key1;
      this.key2 = key2;
      this.node1 = node1;
      this.node2 = node2;
      this.node3 = node3;
      this.node4 = node4;

      assert nodeInvariant();
    }

    @Override
    boolean hasSlots() {
      return true;
    }

    @Override
    int slotArity() {
      return 6;
    }

    @Override
    Object getSlot(final int index) {
      final int boundary = payloadArity();

      if (index < boundary) {
        return getKey(index);
      } else {
        return getNode(index - boundary);
      }
    }

    @Override
    CompactSetNode getNode(int index) {
      switch (index) {
        case 0:
          return node1;
        case 1:
          return node2;
        case 2:
          return node3;
        case 3:
          return node4;
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    int getKey(int index) {
      switch (index) {
        case 0:
          return key1;
        case 1:
          return key2;
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    boolean hasNodes() {
      return true;
    }

    @Override
    int nodeArity() {
      return 4;
    }

    @Override
    boolean hasPayload() {
      return true;
    }

    @Override
    int payloadArity() {
      return 2;
    }

    @Override
    byte sizePredicate() {
      return SIZE_MORE_THAN_ONE;
    }

    @Override
    CompactSetNode copyAndInsertValue(AtomicReference<Thread> mutator, final int bitpos,
        final int key) {
      final int valIndex = dataIndex(bitpos);

      final int nodeMap = (int) (this.nodeMap());
      final int dataMap = (int) (this.dataMap() | bitpos);

      switch (valIndex) {
        case 0:
          return nodeOf(mutator, nodeMap, dataMap, key, key1, key2, node1, node2, node3, node4);
        case 1:
          return nodeOf(mutator, nodeMap, dataMap, key1, key, key2, node1, node2, node3, node4);
        case 2:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2, key, node1, node2, node3, node4);
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    CompactSetNode copyAndRemoveValue(AtomicReference<Thread> mutator, final int bitpos) {
      final int valIndex = dataIndex(bitpos);

      final int nodeMap = (int) (this.nodeMap());
      final int dataMap = (int) (this.dataMap() ^ bitpos);

      switch (valIndex) {
        case 0:
          return nodeOf(mutator, nodeMap, dataMap, key2, node1, node2, node3, node4);
        case 1:
          return nodeOf(mutator, nodeMap, dataMap, key1, node1, node2, node3, node4);
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    CompactSetNode copyAndSetNode(AtomicReference<Thread> mutator, final int bitpos,
        CompactSetNode node) {
      final int index = nodeIndex(bitpos);

      final int nodeMap = this.nodeMap();
      final int dataMap = this.dataMap();

      switch (index) {
        case 0:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2, node, node2, node3, node4);
        case 1:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2, node1, node, node3, node4);
        case 2:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2, node1, node2, node, node4);
        case 3:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2, node1, node2, node3, node);
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    CompactSetNode copyAndMigrateFromInlineToNode(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetNode node) {
      final int bitIndex = nodeIndex(bitpos);
      final int valIndex = dataIndex(bitpos);

      final int nodeMap = (int) (this.nodeMap() | bitpos);
      final int dataMap = (int) (this.dataMap() ^ bitpos);

      switch (valIndex) {
        case 0:
          switch (bitIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key2, node, node1, node2, node3, node4);
            case 1:
              return nodeOf(mutator, nodeMap, dataMap, key2, node1, node, node2, node3, node4);
            case 2:
              return nodeOf(mutator, nodeMap, dataMap, key2, node1, node2, node, node3, node4);
            case 3:
              return nodeOf(mutator, nodeMap, dataMap, key2, node1, node2, node3, node, node4);
            case 4:
              return nodeOf(mutator, nodeMap, dataMap, key2, node1, node2, node3, node4, node);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        case 1:
          switch (bitIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key1, node, node1, node2, node3, node4);
            case 1:
              return nodeOf(mutator, nodeMap, dataMap, key1, node1, node, node2, node3, node4);
            case 2:
              return nodeOf(mutator, nodeMap, dataMap, key1, node1, node2, node, node3, node4);
            case 3:
              return nodeOf(mutator, nodeMap, dataMap, key1, node1, node2, node3, node, node4);
            case 4:
              return nodeOf(mutator, nodeMap, dataMap, key1, node1, node2, node3, node4, node);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    CompactSetNode copyAndMigrateFromNodeToInline(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetNode node) {
      final int bitIndex = nodeIndex(bitpos);
      final int valIndex = dataIndex(bitpos);

      final int nodeMap = (int) (this.nodeMap() ^ bitpos);
      final int dataMap = (int) (this.dataMap() | bitpos);

      final int key = node.getKey(0);

      switch (bitIndex) {
        case 0:
          switch (valIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key, key1, key2, node2, node3, node4);
            case 1:
              return nodeOf(mutator, nodeMap, dataMap, key1, key, key2, node2, node3, node4);
            case 2:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key, node2, node3, node4);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        case 1:
          switch (valIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key, key1, key2, node1, node3, node4);
            case 1:
              return nodeOf(mutator, nodeMap, dataMap, key1, key, key2, node1, node3, node4);
            case 2:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key, node1, node3, node4);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        case 2:
          switch (valIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key, key1, key2, node1, node2, node4);
            case 1:
              return nodeOf(mutator, nodeMap, dataMap, key1, key, key2, node1, node2, node4);
            case 2:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key, node1, node2, node4);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        case 3:
          switch (valIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key, key1, key2, node1, node2, node3);
            case 1:
              return nodeOf(mutator, nodeMap, dataMap, key1, key, key2, node1, node2, node3);
            case 2:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key, node1, node2, node3);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;

      result = prime * result + ((int) nodeMap());
      result = prime * result + ((int) dataMap());

      result = prime * result + (int) key1;
      result = prime * result + (int) key2;

      result = prime * result + node1.hashCode();
      result = prime * result + node2.hashCode();
      result = prime * result + node3.hashCode();
      result = prime * result + node4.hashCode();

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
      Set2To4Node_5Bits_Spec0To8_IntKey that = (Set2To4Node_5Bits_Spec0To8_IntKey) other;

      if (nodeMap() != that.nodeMap()) {
        return false;
      }
      if (dataMap() != that.dataMap()) {
        return false;
      }

      if (!(key1 == that.key1)) {
        return false;
      }
      if (!(key2 == that.key2)) {
        return false;
      }
      if (!(node1.equals(that.node1))) {
        return false;
      }
      if (!(node2.equals(that.node2))) {
        return false;
      }
      if (!(node3.equals(that.node3))) {
        return false;
      }
      if (!(node4.equals(that.node4))) {
        return false;
      }

      return true;
    }

  }

  private static final class Set2To5Node_5Bits_Spec0To8_IntKey extends CompactMixedSetNode {

    private final int key1;
    private final int key2;
    private final CompactSetNode node1;
    private final CompactSetNode node2;
    private final CompactSetNode node3;
    private final CompactSetNode node4;
    private final CompactSetNode node5;

    Set2To5Node_5Bits_Spec0To8_IntKey(final AtomicReference<Thread> mutator, final int nodeMap,
        final int dataMap, final int key1, final int key2, final CompactSetNode node1,
        final CompactSetNode node2, final CompactSetNode node3, final CompactSetNode node4,
        final CompactSetNode node5) {
      super(mutator, nodeMap, dataMap);
      this.key1 = key1;
      this.key2 = key2;
      this.node1 = node1;
      this.node2 = node2;
      this.node3 = node3;
      this.node4 = node4;
      this.node5 = node5;

      assert nodeInvariant();
    }

    @Override
    boolean hasSlots() {
      return true;
    }

    @Override
    int slotArity() {
      return 7;
    }

    @Override
    Object getSlot(final int index) {
      final int boundary = payloadArity();

      if (index < boundary) {
        return getKey(index);
      } else {
        return getNode(index - boundary);
      }
    }

    @Override
    CompactSetNode getNode(int index) {
      switch (index) {
        case 0:
          return node1;
        case 1:
          return node2;
        case 2:
          return node3;
        case 3:
          return node4;
        case 4:
          return node5;
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    int getKey(int index) {
      switch (index) {
        case 0:
          return key1;
        case 1:
          return key2;
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    boolean hasNodes() {
      return true;
    }

    @Override
    int nodeArity() {
      return 5;
    }

    @Override
    boolean hasPayload() {
      return true;
    }

    @Override
    int payloadArity() {
      return 2;
    }

    @Override
    byte sizePredicate() {
      return SIZE_MORE_THAN_ONE;
    }

    @Override
    CompactSetNode copyAndInsertValue(AtomicReference<Thread> mutator, final int bitpos,
        final int key) {
      final int valIndex = dataIndex(bitpos);

      final int nodeMap = (int) (this.nodeMap());
      final int dataMap = (int) (this.dataMap() | bitpos);

      switch (valIndex) {
        case 0:
          return nodeOf(mutator, nodeMap, dataMap, key, key1, key2, node1, node2, node3, node4,
              node5);
        case 1:
          return nodeOf(mutator, nodeMap, dataMap, key1, key, key2, node1, node2, node3, node4,
              node5);
        case 2:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2, key, node1, node2, node3, node4,
              node5);
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    CompactSetNode copyAndRemoveValue(AtomicReference<Thread> mutator, final int bitpos) {
      final int valIndex = dataIndex(bitpos);

      final int nodeMap = (int) (this.nodeMap());
      final int dataMap = (int) (this.dataMap() ^ bitpos);

      switch (valIndex) {
        case 0:
          return nodeOf(mutator, nodeMap, dataMap, key2, node1, node2, node3, node4, node5);
        case 1:
          return nodeOf(mutator, nodeMap, dataMap, key1, node1, node2, node3, node4, node5);
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    CompactSetNode copyAndSetNode(AtomicReference<Thread> mutator, final int bitpos,
        CompactSetNode node) {
      final int index = nodeIndex(bitpos);

      final int nodeMap = this.nodeMap();
      final int dataMap = this.dataMap();

      switch (index) {
        case 0:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2, node, node2, node3, node4, node5);
        case 1:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2, node1, node, node3, node4, node5);
        case 2:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2, node1, node2, node, node4, node5);
        case 3:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2, node1, node2, node3, node, node5);
        case 4:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2, node1, node2, node3, node4, node);
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    CompactSetNode copyAndMigrateFromInlineToNode(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetNode node) {
      final int bitIndex = nodeIndex(bitpos);
      final int valIndex = dataIndex(bitpos);

      final int nodeMap = (int) (this.nodeMap() | bitpos);
      final int dataMap = (int) (this.dataMap() ^ bitpos);

      switch (valIndex) {
        case 0:
          switch (bitIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key2, node, node1, node2, node3, node4,
                  node5);
            case 1:
              return nodeOf(mutator, nodeMap, dataMap, key2, node1, node, node2, node3, node4,
                  node5);
            case 2:
              return nodeOf(mutator, nodeMap, dataMap, key2, node1, node2, node, node3, node4,
                  node5);
            case 3:
              return nodeOf(mutator, nodeMap, dataMap, key2, node1, node2, node3, node, node4,
                  node5);
            case 4:
              return nodeOf(mutator, nodeMap, dataMap, key2, node1, node2, node3, node4, node,
                  node5);
            case 5:
              return nodeOf(mutator, nodeMap, dataMap, key2, node1, node2, node3, node4, node5,
                  node);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        case 1:
          switch (bitIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key1, node, node1, node2, node3, node4,
                  node5);
            case 1:
              return nodeOf(mutator, nodeMap, dataMap, key1, node1, node, node2, node3, node4,
                  node5);
            case 2:
              return nodeOf(mutator, nodeMap, dataMap, key1, node1, node2, node, node3, node4,
                  node5);
            case 3:
              return nodeOf(mutator, nodeMap, dataMap, key1, node1, node2, node3, node, node4,
                  node5);
            case 4:
              return nodeOf(mutator, nodeMap, dataMap, key1, node1, node2, node3, node4, node,
                  node5);
            case 5:
              return nodeOf(mutator, nodeMap, dataMap, key1, node1, node2, node3, node4, node5,
                  node);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    CompactSetNode copyAndMigrateFromNodeToInline(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetNode node) {
      final int bitIndex = nodeIndex(bitpos);
      final int valIndex = dataIndex(bitpos);

      final int nodeMap = (int) (this.nodeMap() ^ bitpos);
      final int dataMap = (int) (this.dataMap() | bitpos);

      final int key = node.getKey(0);

      switch (bitIndex) {
        case 0:
          switch (valIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key, key1, key2, node2, node3, node4, node5);
            case 1:
              return nodeOf(mutator, nodeMap, dataMap, key1, key, key2, node2, node3, node4, node5);
            case 2:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key, node2, node3, node4, node5);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        case 1:
          switch (valIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key, key1, key2, node1, node3, node4, node5);
            case 1:
              return nodeOf(mutator, nodeMap, dataMap, key1, key, key2, node1, node3, node4, node5);
            case 2:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key, node1, node3, node4, node5);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        case 2:
          switch (valIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key, key1, key2, node1, node2, node4, node5);
            case 1:
              return nodeOf(mutator, nodeMap, dataMap, key1, key, key2, node1, node2, node4, node5);
            case 2:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key, node1, node2, node4, node5);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        case 3:
          switch (valIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key, key1, key2, node1, node2, node3, node5);
            case 1:
              return nodeOf(mutator, nodeMap, dataMap, key1, key, key2, node1, node2, node3, node5);
            case 2:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key, node1, node2, node3, node5);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        case 4:
          switch (valIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key, key1, key2, node1, node2, node3, node4);
            case 1:
              return nodeOf(mutator, nodeMap, dataMap, key1, key, key2, node1, node2, node3, node4);
            case 2:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key, node1, node2, node3, node4);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;

      result = prime * result + ((int) nodeMap());
      result = prime * result + ((int) dataMap());

      result = prime * result + (int) key1;
      result = prime * result + (int) key2;

      result = prime * result + node1.hashCode();
      result = prime * result + node2.hashCode();
      result = prime * result + node3.hashCode();
      result = prime * result + node4.hashCode();
      result = prime * result + node5.hashCode();

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
      Set2To5Node_5Bits_Spec0To8_IntKey that = (Set2To5Node_5Bits_Spec0To8_IntKey) other;

      if (nodeMap() != that.nodeMap()) {
        return false;
      }
      if (dataMap() != that.dataMap()) {
        return false;
      }

      if (!(key1 == that.key1)) {
        return false;
      }
      if (!(key2 == that.key2)) {
        return false;
      }
      if (!(node1.equals(that.node1))) {
        return false;
      }
      if (!(node2.equals(that.node2))) {
        return false;
      }
      if (!(node3.equals(that.node3))) {
        return false;
      }
      if (!(node4.equals(that.node4))) {
        return false;
      }
      if (!(node5.equals(that.node5))) {
        return false;
      }

      return true;
    }

  }

  private static final class Set2To6Node_5Bits_Spec0To8_IntKey extends CompactMixedSetNode {

    private final int key1;
    private final int key2;
    private final CompactSetNode node1;
    private final CompactSetNode node2;
    private final CompactSetNode node3;
    private final CompactSetNode node4;
    private final CompactSetNode node5;
    private final CompactSetNode node6;

    Set2To6Node_5Bits_Spec0To8_IntKey(final AtomicReference<Thread> mutator, final int nodeMap,
        final int dataMap, final int key1, final int key2, final CompactSetNode node1,
        final CompactSetNode node2, final CompactSetNode node3, final CompactSetNode node4,
        final CompactSetNode node5, final CompactSetNode node6) {
      super(mutator, nodeMap, dataMap);
      this.key1 = key1;
      this.key2 = key2;
      this.node1 = node1;
      this.node2 = node2;
      this.node3 = node3;
      this.node4 = node4;
      this.node5 = node5;
      this.node6 = node6;

      assert nodeInvariant();
    }

    @Override
    boolean hasSlots() {
      return true;
    }

    @Override
    int slotArity() {
      return 8;
    }

    @Override
    Object getSlot(final int index) {
      final int boundary = payloadArity();

      if (index < boundary) {
        return getKey(index);
      } else {
        return getNode(index - boundary);
      }
    }

    @Override
    CompactSetNode getNode(int index) {
      switch (index) {
        case 0:
          return node1;
        case 1:
          return node2;
        case 2:
          return node3;
        case 3:
          return node4;
        case 4:
          return node5;
        case 5:
          return node6;
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    int getKey(int index) {
      switch (index) {
        case 0:
          return key1;
        case 1:
          return key2;
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    boolean hasNodes() {
      return true;
    }

    @Override
    int nodeArity() {
      return 6;
    }

    @Override
    boolean hasPayload() {
      return true;
    }

    @Override
    int payloadArity() {
      return 2;
    }

    @Override
    byte sizePredicate() {
      return SIZE_MORE_THAN_ONE;
    }

    @Override
    CompactSetNode copyAndInsertValue(AtomicReference<Thread> mutator, final int bitpos,
        final int key) {
      final int valIndex = dataIndex(bitpos);

      final int nodeMap = (int) (this.nodeMap());
      final int dataMap = (int) (this.dataMap() | bitpos);

      switch (valIndex) {
        case 0:
          return nodeOf(mutator, nodeMap, dataMap, key, key1, key2, node1, node2, node3, node4,
              node5, node6);
        case 1:
          return nodeOf(mutator, nodeMap, dataMap, key1, key, key2, node1, node2, node3, node4,
              node5, node6);
        case 2:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2, key, node1, node2, node3, node4,
              node5, node6);
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    CompactSetNode copyAndRemoveValue(AtomicReference<Thread> mutator, final int bitpos) {
      final int valIndex = dataIndex(bitpos);

      final int nodeMap = (int) (this.nodeMap());
      final int dataMap = (int) (this.dataMap() ^ bitpos);

      switch (valIndex) {
        case 0:
          return nodeOf(mutator, nodeMap, dataMap, key2, node1, node2, node3, node4, node5, node6);
        case 1:
          return nodeOf(mutator, nodeMap, dataMap, key1, node1, node2, node3, node4, node5, node6);
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    CompactSetNode copyAndSetNode(AtomicReference<Thread> mutator, final int bitpos,
        CompactSetNode node) {
      final int index = nodeIndex(bitpos);

      final int nodeMap = this.nodeMap();
      final int dataMap = this.dataMap();

      switch (index) {
        case 0:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2, node, node2, node3, node4, node5,
              node6);
        case 1:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2, node1, node, node3, node4, node5,
              node6);
        case 2:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2, node1, node2, node, node4, node5,
              node6);
        case 3:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2, node1, node2, node3, node, node5,
              node6);
        case 4:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2, node1, node2, node3, node4, node,
              node6);
        case 5:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2, node1, node2, node3, node4, node5,
              node);
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    CompactSetNode copyAndMigrateFromInlineToNode(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetNode node) {
      final int bitIndex = nodeIndex(bitpos);
      final int valIndex = dataIndex(bitpos);

      final int nodeMap = (int) (this.nodeMap() | bitpos);
      final int dataMap = (int) (this.dataMap() ^ bitpos);

      switch (valIndex) {
        case 0:
          switch (bitIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key2, node, node1, node2, node3, node4,
                  node5, node6);
            case 1:
              return nodeOf(mutator, nodeMap, dataMap, key2, node1, node, node2, node3, node4,
                  node5, node6);
            case 2:
              return nodeOf(mutator, nodeMap, dataMap, key2, node1, node2, node, node3, node4,
                  node5, node6);
            case 3:
              return nodeOf(mutator, nodeMap, dataMap, key2, node1, node2, node3, node, node4,
                  node5, node6);
            case 4:
              return nodeOf(mutator, nodeMap, dataMap, key2, node1, node2, node3, node4, node,
                  node5, node6);
            case 5:
              return nodeOf(mutator, nodeMap, dataMap, key2, node1, node2, node3, node4, node5,
                  node, node6);
            case 6:
              return nodeOf(mutator, nodeMap, dataMap, key2, node1, node2, node3, node4, node5,
                  node6, node);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        case 1:
          switch (bitIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key1, node, node1, node2, node3, node4,
                  node5, node6);
            case 1:
              return nodeOf(mutator, nodeMap, dataMap, key1, node1, node, node2, node3, node4,
                  node5, node6);
            case 2:
              return nodeOf(mutator, nodeMap, dataMap, key1, node1, node2, node, node3, node4,
                  node5, node6);
            case 3:
              return nodeOf(mutator, nodeMap, dataMap, key1, node1, node2, node3, node, node4,
                  node5, node6);
            case 4:
              return nodeOf(mutator, nodeMap, dataMap, key1, node1, node2, node3, node4, node,
                  node5, node6);
            case 5:
              return nodeOf(mutator, nodeMap, dataMap, key1, node1, node2, node3, node4, node5,
                  node, node6);
            case 6:
              return nodeOf(mutator, nodeMap, dataMap, key1, node1, node2, node3, node4, node5,
                  node6, node);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    CompactSetNode copyAndMigrateFromNodeToInline(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetNode node) {
      final int bitIndex = nodeIndex(bitpos);
      final int valIndex = dataIndex(bitpos);

      final int nodeMap = (int) (this.nodeMap() ^ bitpos);
      final int dataMap = (int) (this.dataMap() | bitpos);

      final int key = node.getKey(0);

      switch (bitIndex) {
        case 0:
          switch (valIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key, key1, key2, node2, node3, node4, node5,
                  node6);
            case 1:
              return nodeOf(mutator, nodeMap, dataMap, key1, key, key2, node2, node3, node4, node5,
                  node6);
            case 2:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key, node2, node3, node4, node5,
                  node6);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        case 1:
          switch (valIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key, key1, key2, node1, node3, node4, node5,
                  node6);
            case 1:
              return nodeOf(mutator, nodeMap, dataMap, key1, key, key2, node1, node3, node4, node5,
                  node6);
            case 2:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key, node1, node3, node4, node5,
                  node6);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        case 2:
          switch (valIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key, key1, key2, node1, node2, node4, node5,
                  node6);
            case 1:
              return nodeOf(mutator, nodeMap, dataMap, key1, key, key2, node1, node2, node4, node5,
                  node6);
            case 2:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key, node1, node2, node4, node5,
                  node6);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        case 3:
          switch (valIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key, key1, key2, node1, node2, node3, node5,
                  node6);
            case 1:
              return nodeOf(mutator, nodeMap, dataMap, key1, key, key2, node1, node2, node3, node5,
                  node6);
            case 2:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key, node1, node2, node3, node5,
                  node6);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        case 4:
          switch (valIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key, key1, key2, node1, node2, node3, node4,
                  node6);
            case 1:
              return nodeOf(mutator, nodeMap, dataMap, key1, key, key2, node1, node2, node3, node4,
                  node6);
            case 2:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key, node1, node2, node3, node4,
                  node6);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        case 5:
          switch (valIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key, key1, key2, node1, node2, node3, node4,
                  node5);
            case 1:
              return nodeOf(mutator, nodeMap, dataMap, key1, key, key2, node1, node2, node3, node4,
                  node5);
            case 2:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key, node1, node2, node3, node4,
                  node5);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;

      result = prime * result + ((int) nodeMap());
      result = prime * result + ((int) dataMap());

      result = prime * result + (int) key1;
      result = prime * result + (int) key2;

      result = prime * result + node1.hashCode();
      result = prime * result + node2.hashCode();
      result = prime * result + node3.hashCode();
      result = prime * result + node4.hashCode();
      result = prime * result + node5.hashCode();
      result = prime * result + node6.hashCode();

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
      Set2To6Node_5Bits_Spec0To8_IntKey that = (Set2To6Node_5Bits_Spec0To8_IntKey) other;

      if (nodeMap() != that.nodeMap()) {
        return false;
      }
      if (dataMap() != that.dataMap()) {
        return false;
      }

      if (!(key1 == that.key1)) {
        return false;
      }
      if (!(key2 == that.key2)) {
        return false;
      }
      if (!(node1.equals(that.node1))) {
        return false;
      }
      if (!(node2.equals(that.node2))) {
        return false;
      }
      if (!(node3.equals(that.node3))) {
        return false;
      }
      if (!(node4.equals(that.node4))) {
        return false;
      }
      if (!(node5.equals(that.node5))) {
        return false;
      }
      if (!(node6.equals(that.node6))) {
        return false;
      }

      return true;
    }

  }

  private static final class Set3To0Node_5Bits_Spec0To8_IntKey extends CompactValuesOnlySetNode {

    private final int key1;
    private final int key2;
    private final int key3;

    Set3To0Node_5Bits_Spec0To8_IntKey(final AtomicReference<Thread> mutator, final int nodeMap,
        final int dataMap, final int key1, final int key2, final int key3) {
      super(mutator, nodeMap, dataMap);
      this.key1 = key1;
      this.key2 = key2;
      this.key3 = key3;

      assert nodeInvariant();
    }

    @Override
    boolean hasSlots() {
      return true;
    }

    @Override
    int slotArity() {
      return 3;
    }

    @Override
    Object getSlot(final int index) {
      final int boundary = payloadArity();

      if (index < boundary) {
        return getKey(index);
      } else {
        return getNode(index - boundary);
      }
    }

    @Override
    CompactSetNode getNode(int index) {
      throw new IllegalStateException("Index out of range.");
    }

    @Override
    int getKey(int index) {
      switch (index) {
        case 0:
          return key1;
        case 1:
          return key2;
        case 2:
          return key3;
        default:
          throw new IllegalStateException("Index out of range.");
      }
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
    boolean hasPayload() {
      return true;
    }

    @Override
    int payloadArity() {
      return 3;
    }

    @Override
    byte sizePredicate() {
      return SIZE_MORE_THAN_ONE;
    }

    @Override
    CompactSetNode copyAndInsertValue(AtomicReference<Thread> mutator, final int bitpos,
        final int key) {
      final int valIndex = dataIndex(bitpos);

      final int nodeMap = (int) (this.nodeMap());
      final int dataMap = (int) (this.dataMap() | bitpos);

      switch (valIndex) {
        case 0:
          return nodeOf(mutator, nodeMap, dataMap, key, key1, key2, key3);
        case 1:
          return nodeOf(mutator, nodeMap, dataMap, key1, key, key2, key3);
        case 2:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2, key, key3);
        case 3:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key);
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    CompactSetNode copyAndRemoveValue(AtomicReference<Thread> mutator, final int bitpos) {
      final int valIndex = dataIndex(bitpos);

      final int nodeMap = (int) (this.nodeMap());
      final int dataMap = (int) (this.dataMap() ^ bitpos);

      switch (valIndex) {
        case 0:
          return nodeOf(mutator, nodeMap, dataMap, key2, key3);
        case 1:
          return nodeOf(mutator, nodeMap, dataMap, key1, key3);
        case 2:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2);
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    CompactSetNode copyAndSetNode(AtomicReference<Thread> mutator, final int bitpos,
        CompactSetNode node) {
      throw new IllegalStateException("Index out of range.");
    }

    @Override
    CompactSetNode copyAndMigrateFromInlineToNode(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetNode node) {
      final int bitIndex = nodeIndex(bitpos);
      final int valIndex = dataIndex(bitpos);

      final int nodeMap = (int) (this.nodeMap() | bitpos);
      final int dataMap = (int) (this.dataMap() ^ bitpos);

      switch (valIndex) {
        case 0:
          switch (bitIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key2, key3, node);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        case 1:
          switch (bitIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key1, key3, node);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        case 2:
          switch (bitIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, node);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    CompactSetNode copyAndMigrateFromNodeToInline(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetNode node) {
      throw new IllegalStateException("Index out of range.");
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;

      result = prime * result + ((int) nodeMap());
      result = prime * result + ((int) dataMap());

      result = prime * result + (int) key1;
      result = prime * result + (int) key2;
      result = prime * result + (int) key3;

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
      Set3To0Node_5Bits_Spec0To8_IntKey that = (Set3To0Node_5Bits_Spec0To8_IntKey) other;

      if (nodeMap() != that.nodeMap()) {
        return false;
      }
      if (dataMap() != that.dataMap()) {
        return false;
      }

      if (!(key1 == that.key1)) {
        return false;
      }
      if (!(key2 == that.key2)) {
        return false;
      }
      if (!(key3 == that.key3)) {
        return false;
      }

      return true;
    }

  }

  private static final class Set3To1Node_5Bits_Spec0To8_IntKey extends CompactMixedSetNode {

    private final int key1;
    private final int key2;
    private final int key3;
    private final CompactSetNode node1;

    Set3To1Node_5Bits_Spec0To8_IntKey(final AtomicReference<Thread> mutator, final int nodeMap,
        final int dataMap, final int key1, final int key2, final int key3,
        final CompactSetNode node1) {
      super(mutator, nodeMap, dataMap);
      this.key1 = key1;
      this.key2 = key2;
      this.key3 = key3;
      this.node1 = node1;

      assert nodeInvariant();
    }

    @Override
    boolean hasSlots() {
      return true;
    }

    @Override
    int slotArity() {
      return 4;
    }

    @Override
    Object getSlot(final int index) {
      final int boundary = payloadArity();

      if (index < boundary) {
        return getKey(index);
      } else {
        return getNode(index - boundary);
      }
    }

    @Override
    CompactSetNode getNode(int index) {
      switch (index) {
        case 0:
          return node1;
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    int getKey(int index) {
      switch (index) {
        case 0:
          return key1;
        case 1:
          return key2;
        case 2:
          return key3;
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    boolean hasNodes() {
      return true;
    }

    @Override
    int nodeArity() {
      return 1;
    }

    @Override
    boolean hasPayload() {
      return true;
    }

    @Override
    int payloadArity() {
      return 3;
    }

    @Override
    byte sizePredicate() {
      return SIZE_MORE_THAN_ONE;
    }

    @Override
    CompactSetNode copyAndInsertValue(AtomicReference<Thread> mutator, final int bitpos,
        final int key) {
      final int valIndex = dataIndex(bitpos);

      final int nodeMap = (int) (this.nodeMap());
      final int dataMap = (int) (this.dataMap() | bitpos);

      switch (valIndex) {
        case 0:
          return nodeOf(mutator, nodeMap, dataMap, key, key1, key2, key3, node1);
        case 1:
          return nodeOf(mutator, nodeMap, dataMap, key1, key, key2, key3, node1);
        case 2:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2, key, key3, node1);
        case 3:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key, node1);
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    CompactSetNode copyAndRemoveValue(AtomicReference<Thread> mutator, final int bitpos) {
      final int valIndex = dataIndex(bitpos);

      final int nodeMap = (int) (this.nodeMap());
      final int dataMap = (int) (this.dataMap() ^ bitpos);

      switch (valIndex) {
        case 0:
          return nodeOf(mutator, nodeMap, dataMap, key2, key3, node1);
        case 1:
          return nodeOf(mutator, nodeMap, dataMap, key1, key3, node1);
        case 2:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2, node1);
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    CompactSetNode copyAndSetNode(AtomicReference<Thread> mutator, final int bitpos,
        CompactSetNode node) {
      final int index = nodeIndex(bitpos);

      final int nodeMap = this.nodeMap();
      final int dataMap = this.dataMap();

      switch (index) {
        case 0:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, node);
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    CompactSetNode copyAndMigrateFromInlineToNode(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetNode node) {
      final int bitIndex = nodeIndex(bitpos);
      final int valIndex = dataIndex(bitpos);

      final int nodeMap = (int) (this.nodeMap() | bitpos);
      final int dataMap = (int) (this.dataMap() ^ bitpos);

      switch (valIndex) {
        case 0:
          switch (bitIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key2, key3, node, node1);
            case 1:
              return nodeOf(mutator, nodeMap, dataMap, key2, key3, node1, node);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        case 1:
          switch (bitIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key1, key3, node, node1);
            case 1:
              return nodeOf(mutator, nodeMap, dataMap, key1, key3, node1, node);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        case 2:
          switch (bitIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, node, node1);
            case 1:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, node1, node);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    CompactSetNode copyAndMigrateFromNodeToInline(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetNode node) {
      final int bitIndex = nodeIndex(bitpos);
      final int valIndex = dataIndex(bitpos);

      final int nodeMap = (int) (this.nodeMap() ^ bitpos);
      final int dataMap = (int) (this.dataMap() | bitpos);

      final int key = node.getKey(0);

      switch (bitIndex) {
        case 0:
          switch (valIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key, key1, key2, key3);
            case 1:
              return nodeOf(mutator, nodeMap, dataMap, key1, key, key2, key3);
            case 2:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key, key3);
            case 3:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;

      result = prime * result + ((int) nodeMap());
      result = prime * result + ((int) dataMap());

      result = prime * result + (int) key1;
      result = prime * result + (int) key2;
      result = prime * result + (int) key3;

      result = prime * result + node1.hashCode();

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
      Set3To1Node_5Bits_Spec0To8_IntKey that = (Set3To1Node_5Bits_Spec0To8_IntKey) other;

      if (nodeMap() != that.nodeMap()) {
        return false;
      }
      if (dataMap() != that.dataMap()) {
        return false;
      }

      if (!(key1 == that.key1)) {
        return false;
      }
      if (!(key2 == that.key2)) {
        return false;
      }
      if (!(key3 == that.key3)) {
        return false;
      }
      if (!(node1.equals(that.node1))) {
        return false;
      }

      return true;
    }

  }

  private static final class Set3To2Node_5Bits_Spec0To8_IntKey extends CompactMixedSetNode {

    private final int key1;
    private final int key2;
    private final int key3;
    private final CompactSetNode node1;
    private final CompactSetNode node2;

    Set3To2Node_5Bits_Spec0To8_IntKey(final AtomicReference<Thread> mutator, final int nodeMap,
        final int dataMap, final int key1, final int key2, final int key3,
        final CompactSetNode node1, final CompactSetNode node2) {
      super(mutator, nodeMap, dataMap);
      this.key1 = key1;
      this.key2 = key2;
      this.key3 = key3;
      this.node1 = node1;
      this.node2 = node2;

      assert nodeInvariant();
    }

    @Override
    boolean hasSlots() {
      return true;
    }

    @Override
    int slotArity() {
      return 5;
    }

    @Override
    Object getSlot(final int index) {
      final int boundary = payloadArity();

      if (index < boundary) {
        return getKey(index);
      } else {
        return getNode(index - boundary);
      }
    }

    @Override
    CompactSetNode getNode(int index) {
      switch (index) {
        case 0:
          return node1;
        case 1:
          return node2;
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    int getKey(int index) {
      switch (index) {
        case 0:
          return key1;
        case 1:
          return key2;
        case 2:
          return key3;
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    boolean hasNodes() {
      return true;
    }

    @Override
    int nodeArity() {
      return 2;
    }

    @Override
    boolean hasPayload() {
      return true;
    }

    @Override
    int payloadArity() {
      return 3;
    }

    @Override
    byte sizePredicate() {
      return SIZE_MORE_THAN_ONE;
    }

    @Override
    CompactSetNode copyAndInsertValue(AtomicReference<Thread> mutator, final int bitpos,
        final int key) {
      final int valIndex = dataIndex(bitpos);

      final int nodeMap = (int) (this.nodeMap());
      final int dataMap = (int) (this.dataMap() | bitpos);

      switch (valIndex) {
        case 0:
          return nodeOf(mutator, nodeMap, dataMap, key, key1, key2, key3, node1, node2);
        case 1:
          return nodeOf(mutator, nodeMap, dataMap, key1, key, key2, key3, node1, node2);
        case 2:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2, key, key3, node1, node2);
        case 3:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key, node1, node2);
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    CompactSetNode copyAndRemoveValue(AtomicReference<Thread> mutator, final int bitpos) {
      final int valIndex = dataIndex(bitpos);

      final int nodeMap = (int) (this.nodeMap());
      final int dataMap = (int) (this.dataMap() ^ bitpos);

      switch (valIndex) {
        case 0:
          return nodeOf(mutator, nodeMap, dataMap, key2, key3, node1, node2);
        case 1:
          return nodeOf(mutator, nodeMap, dataMap, key1, key3, node1, node2);
        case 2:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2, node1, node2);
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    CompactSetNode copyAndSetNode(AtomicReference<Thread> mutator, final int bitpos,
        CompactSetNode node) {
      final int index = nodeIndex(bitpos);

      final int nodeMap = this.nodeMap();
      final int dataMap = this.dataMap();

      switch (index) {
        case 0:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, node, node2);
        case 1:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, node1, node);
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    CompactSetNode copyAndMigrateFromInlineToNode(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetNode node) {
      final int bitIndex = nodeIndex(bitpos);
      final int valIndex = dataIndex(bitpos);

      final int nodeMap = (int) (this.nodeMap() | bitpos);
      final int dataMap = (int) (this.dataMap() ^ bitpos);

      switch (valIndex) {
        case 0:
          switch (bitIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key2, key3, node, node1, node2);
            case 1:
              return nodeOf(mutator, nodeMap, dataMap, key2, key3, node1, node, node2);
            case 2:
              return nodeOf(mutator, nodeMap, dataMap, key2, key3, node1, node2, node);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        case 1:
          switch (bitIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key1, key3, node, node1, node2);
            case 1:
              return nodeOf(mutator, nodeMap, dataMap, key1, key3, node1, node, node2);
            case 2:
              return nodeOf(mutator, nodeMap, dataMap, key1, key3, node1, node2, node);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        case 2:
          switch (bitIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, node, node1, node2);
            case 1:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, node1, node, node2);
            case 2:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, node1, node2, node);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    CompactSetNode copyAndMigrateFromNodeToInline(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetNode node) {
      final int bitIndex = nodeIndex(bitpos);
      final int valIndex = dataIndex(bitpos);

      final int nodeMap = (int) (this.nodeMap() ^ bitpos);
      final int dataMap = (int) (this.dataMap() | bitpos);

      final int key = node.getKey(0);

      switch (bitIndex) {
        case 0:
          switch (valIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key, key1, key2, key3, node2);
            case 1:
              return nodeOf(mutator, nodeMap, dataMap, key1, key, key2, key3, node2);
            case 2:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key, key3, node2);
            case 3:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key, node2);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        case 1:
          switch (valIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key, key1, key2, key3, node1);
            case 1:
              return nodeOf(mutator, nodeMap, dataMap, key1, key, key2, key3, node1);
            case 2:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key, key3, node1);
            case 3:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key, node1);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;

      result = prime * result + ((int) nodeMap());
      result = prime * result + ((int) dataMap());

      result = prime * result + (int) key1;
      result = prime * result + (int) key2;
      result = prime * result + (int) key3;

      result = prime * result + node1.hashCode();
      result = prime * result + node2.hashCode();

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
      Set3To2Node_5Bits_Spec0To8_IntKey that = (Set3To2Node_5Bits_Spec0To8_IntKey) other;

      if (nodeMap() != that.nodeMap()) {
        return false;
      }
      if (dataMap() != that.dataMap()) {
        return false;
      }

      if (!(key1 == that.key1)) {
        return false;
      }
      if (!(key2 == that.key2)) {
        return false;
      }
      if (!(key3 == that.key3)) {
        return false;
      }
      if (!(node1.equals(that.node1))) {
        return false;
      }
      if (!(node2.equals(that.node2))) {
        return false;
      }

      return true;
    }

  }

  private static final class Set3To3Node_5Bits_Spec0To8_IntKey extends CompactMixedSetNode {

    private final int key1;
    private final int key2;
    private final int key3;
    private final CompactSetNode node1;
    private final CompactSetNode node2;
    private final CompactSetNode node3;

    Set3To3Node_5Bits_Spec0To8_IntKey(final AtomicReference<Thread> mutator, final int nodeMap,
        final int dataMap, final int key1, final int key2, final int key3,
        final CompactSetNode node1, final CompactSetNode node2, final CompactSetNode node3) {
      super(mutator, nodeMap, dataMap);
      this.key1 = key1;
      this.key2 = key2;
      this.key3 = key3;
      this.node1 = node1;
      this.node2 = node2;
      this.node3 = node3;

      assert nodeInvariant();
    }

    @Override
    boolean hasSlots() {
      return true;
    }

    @Override
    int slotArity() {
      return 6;
    }

    @Override
    Object getSlot(final int index) {
      final int boundary = payloadArity();

      if (index < boundary) {
        return getKey(index);
      } else {
        return getNode(index - boundary);
      }
    }

    @Override
    CompactSetNode getNode(int index) {
      switch (index) {
        case 0:
          return node1;
        case 1:
          return node2;
        case 2:
          return node3;
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    int getKey(int index) {
      switch (index) {
        case 0:
          return key1;
        case 1:
          return key2;
        case 2:
          return key3;
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    boolean hasNodes() {
      return true;
    }

    @Override
    int nodeArity() {
      return 3;
    }

    @Override
    boolean hasPayload() {
      return true;
    }

    @Override
    int payloadArity() {
      return 3;
    }

    @Override
    byte sizePredicate() {
      return SIZE_MORE_THAN_ONE;
    }

    @Override
    CompactSetNode copyAndInsertValue(AtomicReference<Thread> mutator, final int bitpos,
        final int key) {
      final int valIndex = dataIndex(bitpos);

      final int nodeMap = (int) (this.nodeMap());
      final int dataMap = (int) (this.dataMap() | bitpos);

      switch (valIndex) {
        case 0:
          return nodeOf(mutator, nodeMap, dataMap, key, key1, key2, key3, node1, node2, node3);
        case 1:
          return nodeOf(mutator, nodeMap, dataMap, key1, key, key2, key3, node1, node2, node3);
        case 2:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2, key, key3, node1, node2, node3);
        case 3:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key, node1, node2, node3);
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    CompactSetNode copyAndRemoveValue(AtomicReference<Thread> mutator, final int bitpos) {
      final int valIndex = dataIndex(bitpos);

      final int nodeMap = (int) (this.nodeMap());
      final int dataMap = (int) (this.dataMap() ^ bitpos);

      switch (valIndex) {
        case 0:
          return nodeOf(mutator, nodeMap, dataMap, key2, key3, node1, node2, node3);
        case 1:
          return nodeOf(mutator, nodeMap, dataMap, key1, key3, node1, node2, node3);
        case 2:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2, node1, node2, node3);
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    CompactSetNode copyAndSetNode(AtomicReference<Thread> mutator, final int bitpos,
        CompactSetNode node) {
      final int index = nodeIndex(bitpos);

      final int nodeMap = this.nodeMap();
      final int dataMap = this.dataMap();

      switch (index) {
        case 0:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, node, node2, node3);
        case 1:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, node1, node, node3);
        case 2:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, node1, node2, node);
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    CompactSetNode copyAndMigrateFromInlineToNode(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetNode node) {
      final int bitIndex = nodeIndex(bitpos);
      final int valIndex = dataIndex(bitpos);

      final int nodeMap = (int) (this.nodeMap() | bitpos);
      final int dataMap = (int) (this.dataMap() ^ bitpos);

      switch (valIndex) {
        case 0:
          switch (bitIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key2, key3, node, node1, node2, node3);
            case 1:
              return nodeOf(mutator, nodeMap, dataMap, key2, key3, node1, node, node2, node3);
            case 2:
              return nodeOf(mutator, nodeMap, dataMap, key2, key3, node1, node2, node, node3);
            case 3:
              return nodeOf(mutator, nodeMap, dataMap, key2, key3, node1, node2, node3, node);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        case 1:
          switch (bitIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key1, key3, node, node1, node2, node3);
            case 1:
              return nodeOf(mutator, nodeMap, dataMap, key1, key3, node1, node, node2, node3);
            case 2:
              return nodeOf(mutator, nodeMap, dataMap, key1, key3, node1, node2, node, node3);
            case 3:
              return nodeOf(mutator, nodeMap, dataMap, key1, key3, node1, node2, node3, node);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        case 2:
          switch (bitIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, node, node1, node2, node3);
            case 1:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, node1, node, node2, node3);
            case 2:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, node1, node2, node, node3);
            case 3:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, node1, node2, node3, node);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    CompactSetNode copyAndMigrateFromNodeToInline(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetNode node) {
      final int bitIndex = nodeIndex(bitpos);
      final int valIndex = dataIndex(bitpos);

      final int nodeMap = (int) (this.nodeMap() ^ bitpos);
      final int dataMap = (int) (this.dataMap() | bitpos);

      final int key = node.getKey(0);

      switch (bitIndex) {
        case 0:
          switch (valIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key, key1, key2, key3, node2, node3);
            case 1:
              return nodeOf(mutator, nodeMap, dataMap, key1, key, key2, key3, node2, node3);
            case 2:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key, key3, node2, node3);
            case 3:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key, node2, node3);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        case 1:
          switch (valIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key, key1, key2, key3, node1, node3);
            case 1:
              return nodeOf(mutator, nodeMap, dataMap, key1, key, key2, key3, node1, node3);
            case 2:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key, key3, node1, node3);
            case 3:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key, node1, node3);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        case 2:
          switch (valIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key, key1, key2, key3, node1, node2);
            case 1:
              return nodeOf(mutator, nodeMap, dataMap, key1, key, key2, key3, node1, node2);
            case 2:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key, key3, node1, node2);
            case 3:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key, node1, node2);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;

      result = prime * result + ((int) nodeMap());
      result = prime * result + ((int) dataMap());

      result = prime * result + (int) key1;
      result = prime * result + (int) key2;
      result = prime * result + (int) key3;

      result = prime * result + node1.hashCode();
      result = prime * result + node2.hashCode();
      result = prime * result + node3.hashCode();

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
      Set3To3Node_5Bits_Spec0To8_IntKey that = (Set3To3Node_5Bits_Spec0To8_IntKey) other;

      if (nodeMap() != that.nodeMap()) {
        return false;
      }
      if (dataMap() != that.dataMap()) {
        return false;
      }

      if (!(key1 == that.key1)) {
        return false;
      }
      if (!(key2 == that.key2)) {
        return false;
      }
      if (!(key3 == that.key3)) {
        return false;
      }
      if (!(node1.equals(that.node1))) {
        return false;
      }
      if (!(node2.equals(that.node2))) {
        return false;
      }
      if (!(node3.equals(that.node3))) {
        return false;
      }

      return true;
    }

  }

  private static final class Set3To4Node_5Bits_Spec0To8_IntKey extends CompactMixedSetNode {

    private final int key1;
    private final int key2;
    private final int key3;
    private final CompactSetNode node1;
    private final CompactSetNode node2;
    private final CompactSetNode node3;
    private final CompactSetNode node4;

    Set3To4Node_5Bits_Spec0To8_IntKey(final AtomicReference<Thread> mutator, final int nodeMap,
        final int dataMap, final int key1, final int key2, final int key3,
        final CompactSetNode node1, final CompactSetNode node2, final CompactSetNode node3,
        final CompactSetNode node4) {
      super(mutator, nodeMap, dataMap);
      this.key1 = key1;
      this.key2 = key2;
      this.key3 = key3;
      this.node1 = node1;
      this.node2 = node2;
      this.node3 = node3;
      this.node4 = node4;

      assert nodeInvariant();
    }

    @Override
    boolean hasSlots() {
      return true;
    }

    @Override
    int slotArity() {
      return 7;
    }

    @Override
    Object getSlot(final int index) {
      final int boundary = payloadArity();

      if (index < boundary) {
        return getKey(index);
      } else {
        return getNode(index - boundary);
      }
    }

    @Override
    CompactSetNode getNode(int index) {
      switch (index) {
        case 0:
          return node1;
        case 1:
          return node2;
        case 2:
          return node3;
        case 3:
          return node4;
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    int getKey(int index) {
      switch (index) {
        case 0:
          return key1;
        case 1:
          return key2;
        case 2:
          return key3;
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    boolean hasNodes() {
      return true;
    }

    @Override
    int nodeArity() {
      return 4;
    }

    @Override
    boolean hasPayload() {
      return true;
    }

    @Override
    int payloadArity() {
      return 3;
    }

    @Override
    byte sizePredicate() {
      return SIZE_MORE_THAN_ONE;
    }

    @Override
    CompactSetNode copyAndInsertValue(AtomicReference<Thread> mutator, final int bitpos,
        final int key) {
      final int valIndex = dataIndex(bitpos);

      final int nodeMap = (int) (this.nodeMap());
      final int dataMap = (int) (this.dataMap() | bitpos);

      switch (valIndex) {
        case 0:
          return nodeOf(mutator, nodeMap, dataMap, key, key1, key2, key3, node1, node2, node3,
              node4);
        case 1:
          return nodeOf(mutator, nodeMap, dataMap, key1, key, key2, key3, node1, node2, node3,
              node4);
        case 2:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2, key, key3, node1, node2, node3,
              node4);
        case 3:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key, node1, node2, node3,
              node4);
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    CompactSetNode copyAndRemoveValue(AtomicReference<Thread> mutator, final int bitpos) {
      final int valIndex = dataIndex(bitpos);

      final int nodeMap = (int) (this.nodeMap());
      final int dataMap = (int) (this.dataMap() ^ bitpos);

      switch (valIndex) {
        case 0:
          return nodeOf(mutator, nodeMap, dataMap, key2, key3, node1, node2, node3, node4);
        case 1:
          return nodeOf(mutator, nodeMap, dataMap, key1, key3, node1, node2, node3, node4);
        case 2:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2, node1, node2, node3, node4);
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    CompactSetNode copyAndSetNode(AtomicReference<Thread> mutator, final int bitpos,
        CompactSetNode node) {
      final int index = nodeIndex(bitpos);

      final int nodeMap = this.nodeMap();
      final int dataMap = this.dataMap();

      switch (index) {
        case 0:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, node, node2, node3, node4);
        case 1:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, node1, node, node3, node4);
        case 2:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, node1, node2, node, node4);
        case 3:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, node1, node2, node3, node);
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    CompactSetNode copyAndMigrateFromInlineToNode(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetNode node) {
      final int bitIndex = nodeIndex(bitpos);
      final int valIndex = dataIndex(bitpos);

      final int nodeMap = (int) (this.nodeMap() | bitpos);
      final int dataMap = (int) (this.dataMap() ^ bitpos);

      switch (valIndex) {
        case 0:
          switch (bitIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key2, key3, node, node1, node2, node3,
                  node4);
            case 1:
              return nodeOf(mutator, nodeMap, dataMap, key2, key3, node1, node, node2, node3,
                  node4);
            case 2:
              return nodeOf(mutator, nodeMap, dataMap, key2, key3, node1, node2, node, node3,
                  node4);
            case 3:
              return nodeOf(mutator, nodeMap, dataMap, key2, key3, node1, node2, node3, node,
                  node4);
            case 4:
              return nodeOf(mutator, nodeMap, dataMap, key2, key3, node1, node2, node3, node4,
                  node);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        case 1:
          switch (bitIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key1, key3, node, node1, node2, node3,
                  node4);
            case 1:
              return nodeOf(mutator, nodeMap, dataMap, key1, key3, node1, node, node2, node3,
                  node4);
            case 2:
              return nodeOf(mutator, nodeMap, dataMap, key1, key3, node1, node2, node, node3,
                  node4);
            case 3:
              return nodeOf(mutator, nodeMap, dataMap, key1, key3, node1, node2, node3, node,
                  node4);
            case 4:
              return nodeOf(mutator, nodeMap, dataMap, key1, key3, node1, node2, node3, node4,
                  node);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        case 2:
          switch (bitIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, node, node1, node2, node3,
                  node4);
            case 1:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, node1, node, node2, node3,
                  node4);
            case 2:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, node1, node2, node, node3,
                  node4);
            case 3:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, node1, node2, node3, node,
                  node4);
            case 4:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, node1, node2, node3, node4,
                  node);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    CompactSetNode copyAndMigrateFromNodeToInline(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetNode node) {
      final int bitIndex = nodeIndex(bitpos);
      final int valIndex = dataIndex(bitpos);

      final int nodeMap = (int) (this.nodeMap() ^ bitpos);
      final int dataMap = (int) (this.dataMap() | bitpos);

      final int key = node.getKey(0);

      switch (bitIndex) {
        case 0:
          switch (valIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key, key1, key2, key3, node2, node3, node4);
            case 1:
              return nodeOf(mutator, nodeMap, dataMap, key1, key, key2, key3, node2, node3, node4);
            case 2:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key, key3, node2, node3, node4);
            case 3:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key, node2, node3, node4);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        case 1:
          switch (valIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key, key1, key2, key3, node1, node3, node4);
            case 1:
              return nodeOf(mutator, nodeMap, dataMap, key1, key, key2, key3, node1, node3, node4);
            case 2:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key, key3, node1, node3, node4);
            case 3:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key, node1, node3, node4);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        case 2:
          switch (valIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key, key1, key2, key3, node1, node2, node4);
            case 1:
              return nodeOf(mutator, nodeMap, dataMap, key1, key, key2, key3, node1, node2, node4);
            case 2:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key, key3, node1, node2, node4);
            case 3:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key, node1, node2, node4);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        case 3:
          switch (valIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key, key1, key2, key3, node1, node2, node3);
            case 1:
              return nodeOf(mutator, nodeMap, dataMap, key1, key, key2, key3, node1, node2, node3);
            case 2:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key, key3, node1, node2, node3);
            case 3:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key, node1, node2, node3);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;

      result = prime * result + ((int) nodeMap());
      result = prime * result + ((int) dataMap());

      result = prime * result + (int) key1;
      result = prime * result + (int) key2;
      result = prime * result + (int) key3;

      result = prime * result + node1.hashCode();
      result = prime * result + node2.hashCode();
      result = prime * result + node3.hashCode();
      result = prime * result + node4.hashCode();

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
      Set3To4Node_5Bits_Spec0To8_IntKey that = (Set3To4Node_5Bits_Spec0To8_IntKey) other;

      if (nodeMap() != that.nodeMap()) {
        return false;
      }
      if (dataMap() != that.dataMap()) {
        return false;
      }

      if (!(key1 == that.key1)) {
        return false;
      }
      if (!(key2 == that.key2)) {
        return false;
      }
      if (!(key3 == that.key3)) {
        return false;
      }
      if (!(node1.equals(that.node1))) {
        return false;
      }
      if (!(node2.equals(that.node2))) {
        return false;
      }
      if (!(node3.equals(that.node3))) {
        return false;
      }
      if (!(node4.equals(that.node4))) {
        return false;
      }

      return true;
    }

  }

  private static final class Set3To5Node_5Bits_Spec0To8_IntKey extends CompactMixedSetNode {

    private final int key1;
    private final int key2;
    private final int key3;
    private final CompactSetNode node1;
    private final CompactSetNode node2;
    private final CompactSetNode node3;
    private final CompactSetNode node4;
    private final CompactSetNode node5;

    Set3To5Node_5Bits_Spec0To8_IntKey(final AtomicReference<Thread> mutator, final int nodeMap,
        final int dataMap, final int key1, final int key2, final int key3,
        final CompactSetNode node1, final CompactSetNode node2, final CompactSetNode node3,
        final CompactSetNode node4, final CompactSetNode node5) {
      super(mutator, nodeMap, dataMap);
      this.key1 = key1;
      this.key2 = key2;
      this.key3 = key3;
      this.node1 = node1;
      this.node2 = node2;
      this.node3 = node3;
      this.node4 = node4;
      this.node5 = node5;

      assert nodeInvariant();
    }

    @Override
    boolean hasSlots() {
      return true;
    }

    @Override
    int slotArity() {
      return 8;
    }

    @Override
    Object getSlot(final int index) {
      final int boundary = payloadArity();

      if (index < boundary) {
        return getKey(index);
      } else {
        return getNode(index - boundary);
      }
    }

    @Override
    CompactSetNode getNode(int index) {
      switch (index) {
        case 0:
          return node1;
        case 1:
          return node2;
        case 2:
          return node3;
        case 3:
          return node4;
        case 4:
          return node5;
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    int getKey(int index) {
      switch (index) {
        case 0:
          return key1;
        case 1:
          return key2;
        case 2:
          return key3;
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    boolean hasNodes() {
      return true;
    }

    @Override
    int nodeArity() {
      return 5;
    }

    @Override
    boolean hasPayload() {
      return true;
    }

    @Override
    int payloadArity() {
      return 3;
    }

    @Override
    byte sizePredicate() {
      return SIZE_MORE_THAN_ONE;
    }

    @Override
    CompactSetNode copyAndInsertValue(AtomicReference<Thread> mutator, final int bitpos,
        final int key) {
      final int valIndex = dataIndex(bitpos);

      final int nodeMap = (int) (this.nodeMap());
      final int dataMap = (int) (this.dataMap() | bitpos);

      switch (valIndex) {
        case 0:
          return nodeOf(mutator, nodeMap, dataMap, key, key1, key2, key3, node1, node2, node3,
              node4, node5);
        case 1:
          return nodeOf(mutator, nodeMap, dataMap, key1, key, key2, key3, node1, node2, node3,
              node4, node5);
        case 2:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2, key, key3, node1, node2, node3,
              node4, node5);
        case 3:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key, node1, node2, node3,
              node4, node5);
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    CompactSetNode copyAndRemoveValue(AtomicReference<Thread> mutator, final int bitpos) {
      final int valIndex = dataIndex(bitpos);

      final int nodeMap = (int) (this.nodeMap());
      final int dataMap = (int) (this.dataMap() ^ bitpos);

      switch (valIndex) {
        case 0:
          return nodeOf(mutator, nodeMap, dataMap, key2, key3, node1, node2, node3, node4, node5);
        case 1:
          return nodeOf(mutator, nodeMap, dataMap, key1, key3, node1, node2, node3, node4, node5);
        case 2:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2, node1, node2, node3, node4, node5);
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    CompactSetNode copyAndSetNode(AtomicReference<Thread> mutator, final int bitpos,
        CompactSetNode node) {
      final int index = nodeIndex(bitpos);

      final int nodeMap = this.nodeMap();
      final int dataMap = this.dataMap();

      switch (index) {
        case 0:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, node, node2, node3, node4,
              node5);
        case 1:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, node1, node, node3, node4,
              node5);
        case 2:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, node1, node2, node, node4,
              node5);
        case 3:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, node1, node2, node3, node,
              node5);
        case 4:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, node1, node2, node3, node4,
              node);
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    CompactSetNode copyAndMigrateFromInlineToNode(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetNode node) {
      final int bitIndex = nodeIndex(bitpos);
      final int valIndex = dataIndex(bitpos);

      final int nodeMap = (int) (this.nodeMap() | bitpos);
      final int dataMap = (int) (this.dataMap() ^ bitpos);

      switch (valIndex) {
        case 0:
          switch (bitIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key2, key3, node, node1, node2, node3, node4,
                  node5);
            case 1:
              return nodeOf(mutator, nodeMap, dataMap, key2, key3, node1, node, node2, node3, node4,
                  node5);
            case 2:
              return nodeOf(mutator, nodeMap, dataMap, key2, key3, node1, node2, node, node3, node4,
                  node5);
            case 3:
              return nodeOf(mutator, nodeMap, dataMap, key2, key3, node1, node2, node3, node, node4,
                  node5);
            case 4:
              return nodeOf(mutator, nodeMap, dataMap, key2, key3, node1, node2, node3, node4, node,
                  node5);
            case 5:
              return nodeOf(mutator, nodeMap, dataMap, key2, key3, node1, node2, node3, node4,
                  node5, node);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        case 1:
          switch (bitIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key1, key3, node, node1, node2, node3, node4,
                  node5);
            case 1:
              return nodeOf(mutator, nodeMap, dataMap, key1, key3, node1, node, node2, node3, node4,
                  node5);
            case 2:
              return nodeOf(mutator, nodeMap, dataMap, key1, key3, node1, node2, node, node3, node4,
                  node5);
            case 3:
              return nodeOf(mutator, nodeMap, dataMap, key1, key3, node1, node2, node3, node, node4,
                  node5);
            case 4:
              return nodeOf(mutator, nodeMap, dataMap, key1, key3, node1, node2, node3, node4, node,
                  node5);
            case 5:
              return nodeOf(mutator, nodeMap, dataMap, key1, key3, node1, node2, node3, node4,
                  node5, node);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        case 2:
          switch (bitIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, node, node1, node2, node3, node4,
                  node5);
            case 1:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, node1, node, node2, node3, node4,
                  node5);
            case 2:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, node1, node2, node, node3, node4,
                  node5);
            case 3:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, node1, node2, node3, node, node4,
                  node5);
            case 4:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, node1, node2, node3, node4, node,
                  node5);
            case 5:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, node1, node2, node3, node4,
                  node5, node);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    CompactSetNode copyAndMigrateFromNodeToInline(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetNode node) {
      final int bitIndex = nodeIndex(bitpos);
      final int valIndex = dataIndex(bitpos);

      final int nodeMap = (int) (this.nodeMap() ^ bitpos);
      final int dataMap = (int) (this.dataMap() | bitpos);

      final int key = node.getKey(0);

      switch (bitIndex) {
        case 0:
          switch (valIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key, key1, key2, key3, node2, node3, node4,
                  node5);
            case 1:
              return nodeOf(mutator, nodeMap, dataMap, key1, key, key2, key3, node2, node3, node4,
                  node5);
            case 2:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key, key3, node2, node3, node4,
                  node5);
            case 3:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key, node2, node3, node4,
                  node5);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        case 1:
          switch (valIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key, key1, key2, key3, node1, node3, node4,
                  node5);
            case 1:
              return nodeOf(mutator, nodeMap, dataMap, key1, key, key2, key3, node1, node3, node4,
                  node5);
            case 2:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key, key3, node1, node3, node4,
                  node5);
            case 3:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key, node1, node3, node4,
                  node5);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        case 2:
          switch (valIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key, key1, key2, key3, node1, node2, node4,
                  node5);
            case 1:
              return nodeOf(mutator, nodeMap, dataMap, key1, key, key2, key3, node1, node2, node4,
                  node5);
            case 2:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key, key3, node1, node2, node4,
                  node5);
            case 3:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key, node1, node2, node4,
                  node5);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        case 3:
          switch (valIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key, key1, key2, key3, node1, node2, node3,
                  node5);
            case 1:
              return nodeOf(mutator, nodeMap, dataMap, key1, key, key2, key3, node1, node2, node3,
                  node5);
            case 2:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key, key3, node1, node2, node3,
                  node5);
            case 3:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key, node1, node2, node3,
                  node5);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        case 4:
          switch (valIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key, key1, key2, key3, node1, node2, node3,
                  node4);
            case 1:
              return nodeOf(mutator, nodeMap, dataMap, key1, key, key2, key3, node1, node2, node3,
                  node4);
            case 2:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key, key3, node1, node2, node3,
                  node4);
            case 3:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key, node1, node2, node3,
                  node4);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;

      result = prime * result + ((int) nodeMap());
      result = prime * result + ((int) dataMap());

      result = prime * result + (int) key1;
      result = prime * result + (int) key2;
      result = prime * result + (int) key3;

      result = prime * result + node1.hashCode();
      result = prime * result + node2.hashCode();
      result = prime * result + node3.hashCode();
      result = prime * result + node4.hashCode();
      result = prime * result + node5.hashCode();

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
      Set3To5Node_5Bits_Spec0To8_IntKey that = (Set3To5Node_5Bits_Spec0To8_IntKey) other;

      if (nodeMap() != that.nodeMap()) {
        return false;
      }
      if (dataMap() != that.dataMap()) {
        return false;
      }

      if (!(key1 == that.key1)) {
        return false;
      }
      if (!(key2 == that.key2)) {
        return false;
      }
      if (!(key3 == that.key3)) {
        return false;
      }
      if (!(node1.equals(that.node1))) {
        return false;
      }
      if (!(node2.equals(that.node2))) {
        return false;
      }
      if (!(node3.equals(that.node3))) {
        return false;
      }
      if (!(node4.equals(that.node4))) {
        return false;
      }
      if (!(node5.equals(that.node5))) {
        return false;
      }

      return true;
    }

  }

  private static final class Set4To0Node_5Bits_Spec0To8_IntKey extends CompactValuesOnlySetNode {

    private final int key1;
    private final int key2;
    private final int key3;
    private final int key4;

    Set4To0Node_5Bits_Spec0To8_IntKey(final AtomicReference<Thread> mutator, final int nodeMap,
        final int dataMap, final int key1, final int key2, final int key3, final int key4) {
      super(mutator, nodeMap, dataMap);
      this.key1 = key1;
      this.key2 = key2;
      this.key3 = key3;
      this.key4 = key4;

      assert nodeInvariant();
    }

    @Override
    boolean hasSlots() {
      return true;
    }

    @Override
    int slotArity() {
      return 4;
    }

    @Override
    Object getSlot(final int index) {
      final int boundary = payloadArity();

      if (index < boundary) {
        return getKey(index);
      } else {
        return getNode(index - boundary);
      }
    }

    @Override
    CompactSetNode getNode(int index) {
      throw new IllegalStateException("Index out of range.");
    }

    @Override
    int getKey(int index) {
      switch (index) {
        case 0:
          return key1;
        case 1:
          return key2;
        case 2:
          return key3;
        case 3:
          return key4;
        default:
          throw new IllegalStateException("Index out of range.");
      }
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
    boolean hasPayload() {
      return true;
    }

    @Override
    int payloadArity() {
      return 4;
    }

    @Override
    byte sizePredicate() {
      return SIZE_MORE_THAN_ONE;
    }

    @Override
    CompactSetNode copyAndInsertValue(AtomicReference<Thread> mutator, final int bitpos,
        final int key) {
      final int valIndex = dataIndex(bitpos);

      final int nodeMap = (int) (this.nodeMap());
      final int dataMap = (int) (this.dataMap() | bitpos);

      switch (valIndex) {
        case 0:
          return nodeOf(mutator, nodeMap, dataMap, key, key1, key2, key3, key4);
        case 1:
          return nodeOf(mutator, nodeMap, dataMap, key1, key, key2, key3, key4);
        case 2:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2, key, key3, key4);
        case 3:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key, key4);
        case 4:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key4, key);
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    CompactSetNode copyAndRemoveValue(AtomicReference<Thread> mutator, final int bitpos) {
      final int valIndex = dataIndex(bitpos);

      final int nodeMap = (int) (this.nodeMap());
      final int dataMap = (int) (this.dataMap() ^ bitpos);

      switch (valIndex) {
        case 0:
          return nodeOf(mutator, nodeMap, dataMap, key2, key3, key4);
        case 1:
          return nodeOf(mutator, nodeMap, dataMap, key1, key3, key4);
        case 2:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2, key4);
        case 3:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3);
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    CompactSetNode copyAndSetNode(AtomicReference<Thread> mutator, final int bitpos,
        CompactSetNode node) {
      throw new IllegalStateException("Index out of range.");
    }

    @Override
    CompactSetNode copyAndMigrateFromInlineToNode(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetNode node) {
      final int bitIndex = nodeIndex(bitpos);
      final int valIndex = dataIndex(bitpos);

      final int nodeMap = (int) (this.nodeMap() | bitpos);
      final int dataMap = (int) (this.dataMap() ^ bitpos);

      switch (valIndex) {
        case 0:
          switch (bitIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key2, key3, key4, node);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        case 1:
          switch (bitIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key1, key3, key4, node);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        case 2:
          switch (bitIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key4, node);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        case 3:
          switch (bitIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, node);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    CompactSetNode copyAndMigrateFromNodeToInline(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetNode node) {
      throw new IllegalStateException("Index out of range.");
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;

      result = prime * result + ((int) nodeMap());
      result = prime * result + ((int) dataMap());

      result = prime * result + (int) key1;
      result = prime * result + (int) key2;
      result = prime * result + (int) key3;
      result = prime * result + (int) key4;

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
      Set4To0Node_5Bits_Spec0To8_IntKey that = (Set4To0Node_5Bits_Spec0To8_IntKey) other;

      if (nodeMap() != that.nodeMap()) {
        return false;
      }
      if (dataMap() != that.dataMap()) {
        return false;
      }

      if (!(key1 == that.key1)) {
        return false;
      }
      if (!(key2 == that.key2)) {
        return false;
      }
      if (!(key3 == that.key3)) {
        return false;
      }
      if (!(key4 == that.key4)) {
        return false;
      }

      return true;
    }

  }

  private static final class Set4To1Node_5Bits_Spec0To8_IntKey extends CompactMixedSetNode {

    private final int key1;
    private final int key2;
    private final int key3;
    private final int key4;
    private final CompactSetNode node1;

    Set4To1Node_5Bits_Spec0To8_IntKey(final AtomicReference<Thread> mutator, final int nodeMap,
        final int dataMap, final int key1, final int key2, final int key3, final int key4,
        final CompactSetNode node1) {
      super(mutator, nodeMap, dataMap);
      this.key1 = key1;
      this.key2 = key2;
      this.key3 = key3;
      this.key4 = key4;
      this.node1 = node1;

      assert nodeInvariant();
    }

    @Override
    boolean hasSlots() {
      return true;
    }

    @Override
    int slotArity() {
      return 5;
    }

    @Override
    Object getSlot(final int index) {
      final int boundary = payloadArity();

      if (index < boundary) {
        return getKey(index);
      } else {
        return getNode(index - boundary);
      }
    }

    @Override
    CompactSetNode getNode(int index) {
      switch (index) {
        case 0:
          return node1;
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    int getKey(int index) {
      switch (index) {
        case 0:
          return key1;
        case 1:
          return key2;
        case 2:
          return key3;
        case 3:
          return key4;
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    boolean hasNodes() {
      return true;
    }

    @Override
    int nodeArity() {
      return 1;
    }

    @Override
    boolean hasPayload() {
      return true;
    }

    @Override
    int payloadArity() {
      return 4;
    }

    @Override
    byte sizePredicate() {
      return SIZE_MORE_THAN_ONE;
    }

    @Override
    CompactSetNode copyAndInsertValue(AtomicReference<Thread> mutator, final int bitpos,
        final int key) {
      final int valIndex = dataIndex(bitpos);

      final int nodeMap = (int) (this.nodeMap());
      final int dataMap = (int) (this.dataMap() | bitpos);

      switch (valIndex) {
        case 0:
          return nodeOf(mutator, nodeMap, dataMap, key, key1, key2, key3, key4, node1);
        case 1:
          return nodeOf(mutator, nodeMap, dataMap, key1, key, key2, key3, key4, node1);
        case 2:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2, key, key3, key4, node1);
        case 3:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key, key4, node1);
        case 4:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key4, key, node1);
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    CompactSetNode copyAndRemoveValue(AtomicReference<Thread> mutator, final int bitpos) {
      final int valIndex = dataIndex(bitpos);

      final int nodeMap = (int) (this.nodeMap());
      final int dataMap = (int) (this.dataMap() ^ bitpos);

      switch (valIndex) {
        case 0:
          return nodeOf(mutator, nodeMap, dataMap, key2, key3, key4, node1);
        case 1:
          return nodeOf(mutator, nodeMap, dataMap, key1, key3, key4, node1);
        case 2:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2, key4, node1);
        case 3:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, node1);
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    CompactSetNode copyAndSetNode(AtomicReference<Thread> mutator, final int bitpos,
        CompactSetNode node) {
      final int index = nodeIndex(bitpos);

      final int nodeMap = this.nodeMap();
      final int dataMap = this.dataMap();

      switch (index) {
        case 0:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key4, node);
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    CompactSetNode copyAndMigrateFromInlineToNode(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetNode node) {
      final int bitIndex = nodeIndex(bitpos);
      final int valIndex = dataIndex(bitpos);

      final int nodeMap = (int) (this.nodeMap() | bitpos);
      final int dataMap = (int) (this.dataMap() ^ bitpos);

      switch (valIndex) {
        case 0:
          switch (bitIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key2, key3, key4, node, node1);
            case 1:
              return nodeOf(mutator, nodeMap, dataMap, key2, key3, key4, node1, node);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        case 1:
          switch (bitIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key1, key3, key4, node, node1);
            case 1:
              return nodeOf(mutator, nodeMap, dataMap, key1, key3, key4, node1, node);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        case 2:
          switch (bitIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key4, node, node1);
            case 1:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key4, node1, node);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        case 3:
          switch (bitIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, node, node1);
            case 1:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, node1, node);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    CompactSetNode copyAndMigrateFromNodeToInline(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetNode node) {
      final int bitIndex = nodeIndex(bitpos);
      final int valIndex = dataIndex(bitpos);

      final int nodeMap = (int) (this.nodeMap() ^ bitpos);
      final int dataMap = (int) (this.dataMap() | bitpos);

      final int key = node.getKey(0);

      switch (bitIndex) {
        case 0:
          switch (valIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key, key1, key2, key3, key4);
            case 1:
              return nodeOf(mutator, nodeMap, dataMap, key1, key, key2, key3, key4);
            case 2:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key, key3, key4);
            case 3:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key, key4);
            case 4:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key4, key);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;

      result = prime * result + ((int) nodeMap());
      result = prime * result + ((int) dataMap());

      result = prime * result + (int) key1;
      result = prime * result + (int) key2;
      result = prime * result + (int) key3;
      result = prime * result + (int) key4;

      result = prime * result + node1.hashCode();

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
      Set4To1Node_5Bits_Spec0To8_IntKey that = (Set4To1Node_5Bits_Spec0To8_IntKey) other;

      if (nodeMap() != that.nodeMap()) {
        return false;
      }
      if (dataMap() != that.dataMap()) {
        return false;
      }

      if (!(key1 == that.key1)) {
        return false;
      }
      if (!(key2 == that.key2)) {
        return false;
      }
      if (!(key3 == that.key3)) {
        return false;
      }
      if (!(key4 == that.key4)) {
        return false;
      }
      if (!(node1.equals(that.node1))) {
        return false;
      }

      return true;
    }

  }

  private static final class Set4To2Node_5Bits_Spec0To8_IntKey extends CompactMixedSetNode {

    private final int key1;
    private final int key2;
    private final int key3;
    private final int key4;
    private final CompactSetNode node1;
    private final CompactSetNode node2;

    Set4To2Node_5Bits_Spec0To8_IntKey(final AtomicReference<Thread> mutator, final int nodeMap,
        final int dataMap, final int key1, final int key2, final int key3, final int key4,
        final CompactSetNode node1, final CompactSetNode node2) {
      super(mutator, nodeMap, dataMap);
      this.key1 = key1;
      this.key2 = key2;
      this.key3 = key3;
      this.key4 = key4;
      this.node1 = node1;
      this.node2 = node2;

      assert nodeInvariant();
    }

    @Override
    boolean hasSlots() {
      return true;
    }

    @Override
    int slotArity() {
      return 6;
    }

    @Override
    Object getSlot(final int index) {
      final int boundary = payloadArity();

      if (index < boundary) {
        return getKey(index);
      } else {
        return getNode(index - boundary);
      }
    }

    @Override
    CompactSetNode getNode(int index) {
      switch (index) {
        case 0:
          return node1;
        case 1:
          return node2;
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    int getKey(int index) {
      switch (index) {
        case 0:
          return key1;
        case 1:
          return key2;
        case 2:
          return key3;
        case 3:
          return key4;
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    boolean hasNodes() {
      return true;
    }

    @Override
    int nodeArity() {
      return 2;
    }

    @Override
    boolean hasPayload() {
      return true;
    }

    @Override
    int payloadArity() {
      return 4;
    }

    @Override
    byte sizePredicate() {
      return SIZE_MORE_THAN_ONE;
    }

    @Override
    CompactSetNode copyAndInsertValue(AtomicReference<Thread> mutator, final int bitpos,
        final int key) {
      final int valIndex = dataIndex(bitpos);

      final int nodeMap = (int) (this.nodeMap());
      final int dataMap = (int) (this.dataMap() | bitpos);

      switch (valIndex) {
        case 0:
          return nodeOf(mutator, nodeMap, dataMap, key, key1, key2, key3, key4, node1, node2);
        case 1:
          return nodeOf(mutator, nodeMap, dataMap, key1, key, key2, key3, key4, node1, node2);
        case 2:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2, key, key3, key4, node1, node2);
        case 3:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key, key4, node1, node2);
        case 4:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key4, key, node1, node2);
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    CompactSetNode copyAndRemoveValue(AtomicReference<Thread> mutator, final int bitpos) {
      final int valIndex = dataIndex(bitpos);

      final int nodeMap = (int) (this.nodeMap());
      final int dataMap = (int) (this.dataMap() ^ bitpos);

      switch (valIndex) {
        case 0:
          return nodeOf(mutator, nodeMap, dataMap, key2, key3, key4, node1, node2);
        case 1:
          return nodeOf(mutator, nodeMap, dataMap, key1, key3, key4, node1, node2);
        case 2:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2, key4, node1, node2);
        case 3:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, node1, node2);
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    CompactSetNode copyAndSetNode(AtomicReference<Thread> mutator, final int bitpos,
        CompactSetNode node) {
      final int index = nodeIndex(bitpos);

      final int nodeMap = this.nodeMap();
      final int dataMap = this.dataMap();

      switch (index) {
        case 0:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key4, node, node2);
        case 1:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key4, node1, node);
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    CompactSetNode copyAndMigrateFromInlineToNode(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetNode node) {
      final int bitIndex = nodeIndex(bitpos);
      final int valIndex = dataIndex(bitpos);

      final int nodeMap = (int) (this.nodeMap() | bitpos);
      final int dataMap = (int) (this.dataMap() ^ bitpos);

      switch (valIndex) {
        case 0:
          switch (bitIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key2, key3, key4, node, node1, node2);
            case 1:
              return nodeOf(mutator, nodeMap, dataMap, key2, key3, key4, node1, node, node2);
            case 2:
              return nodeOf(mutator, nodeMap, dataMap, key2, key3, key4, node1, node2, node);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        case 1:
          switch (bitIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key1, key3, key4, node, node1, node2);
            case 1:
              return nodeOf(mutator, nodeMap, dataMap, key1, key3, key4, node1, node, node2);
            case 2:
              return nodeOf(mutator, nodeMap, dataMap, key1, key3, key4, node1, node2, node);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        case 2:
          switch (bitIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key4, node, node1, node2);
            case 1:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key4, node1, node, node2);
            case 2:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key4, node1, node2, node);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        case 3:
          switch (bitIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, node, node1, node2);
            case 1:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, node1, node, node2);
            case 2:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, node1, node2, node);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    CompactSetNode copyAndMigrateFromNodeToInline(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetNode node) {
      final int bitIndex = nodeIndex(bitpos);
      final int valIndex = dataIndex(bitpos);

      final int nodeMap = (int) (this.nodeMap() ^ bitpos);
      final int dataMap = (int) (this.dataMap() | bitpos);

      final int key = node.getKey(0);

      switch (bitIndex) {
        case 0:
          switch (valIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key, key1, key2, key3, key4, node2);
            case 1:
              return nodeOf(mutator, nodeMap, dataMap, key1, key, key2, key3, key4, node2);
            case 2:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key, key3, key4, node2);
            case 3:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key, key4, node2);
            case 4:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key4, key, node2);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        case 1:
          switch (valIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key, key1, key2, key3, key4, node1);
            case 1:
              return nodeOf(mutator, nodeMap, dataMap, key1, key, key2, key3, key4, node1);
            case 2:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key, key3, key4, node1);
            case 3:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key, key4, node1);
            case 4:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key4, key, node1);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;

      result = prime * result + ((int) nodeMap());
      result = prime * result + ((int) dataMap());

      result = prime * result + (int) key1;
      result = prime * result + (int) key2;
      result = prime * result + (int) key3;
      result = prime * result + (int) key4;

      result = prime * result + node1.hashCode();
      result = prime * result + node2.hashCode();

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
      Set4To2Node_5Bits_Spec0To8_IntKey that = (Set4To2Node_5Bits_Spec0To8_IntKey) other;

      if (nodeMap() != that.nodeMap()) {
        return false;
      }
      if (dataMap() != that.dataMap()) {
        return false;
      }

      if (!(key1 == that.key1)) {
        return false;
      }
      if (!(key2 == that.key2)) {
        return false;
      }
      if (!(key3 == that.key3)) {
        return false;
      }
      if (!(key4 == that.key4)) {
        return false;
      }
      if (!(node1.equals(that.node1))) {
        return false;
      }
      if (!(node2.equals(that.node2))) {
        return false;
      }

      return true;
    }

  }

  private static final class Set4To3Node_5Bits_Spec0To8_IntKey extends CompactMixedSetNode {

    private final int key1;
    private final int key2;
    private final int key3;
    private final int key4;
    private final CompactSetNode node1;
    private final CompactSetNode node2;
    private final CompactSetNode node3;

    Set4To3Node_5Bits_Spec0To8_IntKey(final AtomicReference<Thread> mutator, final int nodeMap,
        final int dataMap, final int key1, final int key2, final int key3, final int key4,
        final CompactSetNode node1, final CompactSetNode node2, final CompactSetNode node3) {
      super(mutator, nodeMap, dataMap);
      this.key1 = key1;
      this.key2 = key2;
      this.key3 = key3;
      this.key4 = key4;
      this.node1 = node1;
      this.node2 = node2;
      this.node3 = node3;

      assert nodeInvariant();
    }

    @Override
    boolean hasSlots() {
      return true;
    }

    @Override
    int slotArity() {
      return 7;
    }

    @Override
    Object getSlot(final int index) {
      final int boundary = payloadArity();

      if (index < boundary) {
        return getKey(index);
      } else {
        return getNode(index - boundary);
      }
    }

    @Override
    CompactSetNode getNode(int index) {
      switch (index) {
        case 0:
          return node1;
        case 1:
          return node2;
        case 2:
          return node3;
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    int getKey(int index) {
      switch (index) {
        case 0:
          return key1;
        case 1:
          return key2;
        case 2:
          return key3;
        case 3:
          return key4;
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    boolean hasNodes() {
      return true;
    }

    @Override
    int nodeArity() {
      return 3;
    }

    @Override
    boolean hasPayload() {
      return true;
    }

    @Override
    int payloadArity() {
      return 4;
    }

    @Override
    byte sizePredicate() {
      return SIZE_MORE_THAN_ONE;
    }

    @Override
    CompactSetNode copyAndInsertValue(AtomicReference<Thread> mutator, final int bitpos,
        final int key) {
      final int valIndex = dataIndex(bitpos);

      final int nodeMap = (int) (this.nodeMap());
      final int dataMap = (int) (this.dataMap() | bitpos);

      switch (valIndex) {
        case 0:
          return nodeOf(mutator, nodeMap, dataMap, key, key1, key2, key3, key4, node1, node2,
              node3);
        case 1:
          return nodeOf(mutator, nodeMap, dataMap, key1, key, key2, key3, key4, node1, node2,
              node3);
        case 2:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2, key, key3, key4, node1, node2,
              node3);
        case 3:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key, key4, node1, node2,
              node3);
        case 4:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key4, key, node1, node2,
              node3);
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    CompactSetNode copyAndRemoveValue(AtomicReference<Thread> mutator, final int bitpos) {
      final int valIndex = dataIndex(bitpos);

      final int nodeMap = (int) (this.nodeMap());
      final int dataMap = (int) (this.dataMap() ^ bitpos);

      switch (valIndex) {
        case 0:
          return nodeOf(mutator, nodeMap, dataMap, key2, key3, key4, node1, node2, node3);
        case 1:
          return nodeOf(mutator, nodeMap, dataMap, key1, key3, key4, node1, node2, node3);
        case 2:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2, key4, node1, node2, node3);
        case 3:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, node1, node2, node3);
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    CompactSetNode copyAndSetNode(AtomicReference<Thread> mutator, final int bitpos,
        CompactSetNode node) {
      final int index = nodeIndex(bitpos);

      final int nodeMap = this.nodeMap();
      final int dataMap = this.dataMap();

      switch (index) {
        case 0:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key4, node, node2, node3);
        case 1:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key4, node1, node, node3);
        case 2:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key4, node1, node2, node);
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    CompactSetNode copyAndMigrateFromInlineToNode(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetNode node) {
      final int bitIndex = nodeIndex(bitpos);
      final int valIndex = dataIndex(bitpos);

      final int nodeMap = (int) (this.nodeMap() | bitpos);
      final int dataMap = (int) (this.dataMap() ^ bitpos);

      switch (valIndex) {
        case 0:
          switch (bitIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key2, key3, key4, node, node1, node2, node3);
            case 1:
              return nodeOf(mutator, nodeMap, dataMap, key2, key3, key4, node1, node, node2, node3);
            case 2:
              return nodeOf(mutator, nodeMap, dataMap, key2, key3, key4, node1, node2, node, node3);
            case 3:
              return nodeOf(mutator, nodeMap, dataMap, key2, key3, key4, node1, node2, node3, node);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        case 1:
          switch (bitIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key1, key3, key4, node, node1, node2, node3);
            case 1:
              return nodeOf(mutator, nodeMap, dataMap, key1, key3, key4, node1, node, node2, node3);
            case 2:
              return nodeOf(mutator, nodeMap, dataMap, key1, key3, key4, node1, node2, node, node3);
            case 3:
              return nodeOf(mutator, nodeMap, dataMap, key1, key3, key4, node1, node2, node3, node);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        case 2:
          switch (bitIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key4, node, node1, node2, node3);
            case 1:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key4, node1, node, node2, node3);
            case 2:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key4, node1, node2, node, node3);
            case 3:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key4, node1, node2, node3, node);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        case 3:
          switch (bitIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, node, node1, node2, node3);
            case 1:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, node1, node, node2, node3);
            case 2:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, node1, node2, node, node3);
            case 3:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, node1, node2, node3, node);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    CompactSetNode copyAndMigrateFromNodeToInline(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetNode node) {
      final int bitIndex = nodeIndex(bitpos);
      final int valIndex = dataIndex(bitpos);

      final int nodeMap = (int) (this.nodeMap() ^ bitpos);
      final int dataMap = (int) (this.dataMap() | bitpos);

      final int key = node.getKey(0);

      switch (bitIndex) {
        case 0:
          switch (valIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key, key1, key2, key3, key4, node2, node3);
            case 1:
              return nodeOf(mutator, nodeMap, dataMap, key1, key, key2, key3, key4, node2, node3);
            case 2:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key, key3, key4, node2, node3);
            case 3:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key, key4, node2, node3);
            case 4:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key4, key, node2, node3);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        case 1:
          switch (valIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key, key1, key2, key3, key4, node1, node3);
            case 1:
              return nodeOf(mutator, nodeMap, dataMap, key1, key, key2, key3, key4, node1, node3);
            case 2:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key, key3, key4, node1, node3);
            case 3:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key, key4, node1, node3);
            case 4:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key4, key, node1, node3);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        case 2:
          switch (valIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key, key1, key2, key3, key4, node1, node2);
            case 1:
              return nodeOf(mutator, nodeMap, dataMap, key1, key, key2, key3, key4, node1, node2);
            case 2:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key, key3, key4, node1, node2);
            case 3:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key, key4, node1, node2);
            case 4:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key4, key, node1, node2);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;

      result = prime * result + ((int) nodeMap());
      result = prime * result + ((int) dataMap());

      result = prime * result + (int) key1;
      result = prime * result + (int) key2;
      result = prime * result + (int) key3;
      result = prime * result + (int) key4;

      result = prime * result + node1.hashCode();
      result = prime * result + node2.hashCode();
      result = prime * result + node3.hashCode();

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
      Set4To3Node_5Bits_Spec0To8_IntKey that = (Set4To3Node_5Bits_Spec0To8_IntKey) other;

      if (nodeMap() != that.nodeMap()) {
        return false;
      }
      if (dataMap() != that.dataMap()) {
        return false;
      }

      if (!(key1 == that.key1)) {
        return false;
      }
      if (!(key2 == that.key2)) {
        return false;
      }
      if (!(key3 == that.key3)) {
        return false;
      }
      if (!(key4 == that.key4)) {
        return false;
      }
      if (!(node1.equals(that.node1))) {
        return false;
      }
      if (!(node2.equals(that.node2))) {
        return false;
      }
      if (!(node3.equals(that.node3))) {
        return false;
      }

      return true;
    }

  }

  private static final class Set4To4Node_5Bits_Spec0To8_IntKey extends CompactMixedSetNode {

    private final int key1;
    private final int key2;
    private final int key3;
    private final int key4;
    private final CompactSetNode node1;
    private final CompactSetNode node2;
    private final CompactSetNode node3;
    private final CompactSetNode node4;

    Set4To4Node_5Bits_Spec0To8_IntKey(final AtomicReference<Thread> mutator, final int nodeMap,
        final int dataMap, final int key1, final int key2, final int key3, final int key4,
        final CompactSetNode node1, final CompactSetNode node2, final CompactSetNode node3,
        final CompactSetNode node4) {
      super(mutator, nodeMap, dataMap);
      this.key1 = key1;
      this.key2 = key2;
      this.key3 = key3;
      this.key4 = key4;
      this.node1 = node1;
      this.node2 = node2;
      this.node3 = node3;
      this.node4 = node4;

      assert nodeInvariant();
    }

    @Override
    boolean hasSlots() {
      return true;
    }

    @Override
    int slotArity() {
      return 8;
    }

    @Override
    Object getSlot(final int index) {
      final int boundary = payloadArity();

      if (index < boundary) {
        return getKey(index);
      } else {
        return getNode(index - boundary);
      }
    }

    @Override
    CompactSetNode getNode(int index) {
      switch (index) {
        case 0:
          return node1;
        case 1:
          return node2;
        case 2:
          return node3;
        case 3:
          return node4;
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    int getKey(int index) {
      switch (index) {
        case 0:
          return key1;
        case 1:
          return key2;
        case 2:
          return key3;
        case 3:
          return key4;
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    boolean hasNodes() {
      return true;
    }

    @Override
    int nodeArity() {
      return 4;
    }

    @Override
    boolean hasPayload() {
      return true;
    }

    @Override
    int payloadArity() {
      return 4;
    }

    @Override
    byte sizePredicate() {
      return SIZE_MORE_THAN_ONE;
    }

    @Override
    CompactSetNode copyAndInsertValue(AtomicReference<Thread> mutator, final int bitpos,
        final int key) {
      final int valIndex = dataIndex(bitpos);

      final int nodeMap = (int) (this.nodeMap());
      final int dataMap = (int) (this.dataMap() | bitpos);

      switch (valIndex) {
        case 0:
          return nodeOf(mutator, nodeMap, dataMap, key, key1, key2, key3, key4, node1, node2, node3,
              node4);
        case 1:
          return nodeOf(mutator, nodeMap, dataMap, key1, key, key2, key3, key4, node1, node2, node3,
              node4);
        case 2:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2, key, key3, key4, node1, node2, node3,
              node4);
        case 3:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key, key4, node1, node2, node3,
              node4);
        case 4:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key4, key, node1, node2, node3,
              node4);
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    CompactSetNode copyAndRemoveValue(AtomicReference<Thread> mutator, final int bitpos) {
      final int valIndex = dataIndex(bitpos);

      final int nodeMap = (int) (this.nodeMap());
      final int dataMap = (int) (this.dataMap() ^ bitpos);

      switch (valIndex) {
        case 0:
          return nodeOf(mutator, nodeMap, dataMap, key2, key3, key4, node1, node2, node3, node4);
        case 1:
          return nodeOf(mutator, nodeMap, dataMap, key1, key3, key4, node1, node2, node3, node4);
        case 2:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2, key4, node1, node2, node3, node4);
        case 3:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, node1, node2, node3, node4);
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    CompactSetNode copyAndSetNode(AtomicReference<Thread> mutator, final int bitpos,
        CompactSetNode node) {
      final int index = nodeIndex(bitpos);

      final int nodeMap = this.nodeMap();
      final int dataMap = this.dataMap();

      switch (index) {
        case 0:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key4, node, node2, node3,
              node4);
        case 1:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key4, node1, node, node3,
              node4);
        case 2:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key4, node1, node2, node,
              node4);
        case 3:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key4, node1, node2, node3,
              node);
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    CompactSetNode copyAndMigrateFromInlineToNode(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetNode node) {
      final int bitIndex = nodeIndex(bitpos);
      final int valIndex = dataIndex(bitpos);

      final int nodeMap = (int) (this.nodeMap() | bitpos);
      final int dataMap = (int) (this.dataMap() ^ bitpos);

      switch (valIndex) {
        case 0:
          switch (bitIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key2, key3, key4, node, node1, node2, node3,
                  node4);
            case 1:
              return nodeOf(mutator, nodeMap, dataMap, key2, key3, key4, node1, node, node2, node3,
                  node4);
            case 2:
              return nodeOf(mutator, nodeMap, dataMap, key2, key3, key4, node1, node2, node, node3,
                  node4);
            case 3:
              return nodeOf(mutator, nodeMap, dataMap, key2, key3, key4, node1, node2, node3, node,
                  node4);
            case 4:
              return nodeOf(mutator, nodeMap, dataMap, key2, key3, key4, node1, node2, node3, node4,
                  node);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        case 1:
          switch (bitIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key1, key3, key4, node, node1, node2, node3,
                  node4);
            case 1:
              return nodeOf(mutator, nodeMap, dataMap, key1, key3, key4, node1, node, node2, node3,
                  node4);
            case 2:
              return nodeOf(mutator, nodeMap, dataMap, key1, key3, key4, node1, node2, node, node3,
                  node4);
            case 3:
              return nodeOf(mutator, nodeMap, dataMap, key1, key3, key4, node1, node2, node3, node,
                  node4);
            case 4:
              return nodeOf(mutator, nodeMap, dataMap, key1, key3, key4, node1, node2, node3, node4,
                  node);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        case 2:
          switch (bitIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key4, node, node1, node2, node3,
                  node4);
            case 1:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key4, node1, node, node2, node3,
                  node4);
            case 2:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key4, node1, node2, node, node3,
                  node4);
            case 3:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key4, node1, node2, node3, node,
                  node4);
            case 4:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key4, node1, node2, node3, node4,
                  node);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        case 3:
          switch (bitIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, node, node1, node2, node3,
                  node4);
            case 1:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, node1, node, node2, node3,
                  node4);
            case 2:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, node1, node2, node, node3,
                  node4);
            case 3:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, node1, node2, node3, node,
                  node4);
            case 4:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, node1, node2, node3, node4,
                  node);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    CompactSetNode copyAndMigrateFromNodeToInline(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetNode node) {
      final int bitIndex = nodeIndex(bitpos);
      final int valIndex = dataIndex(bitpos);

      final int nodeMap = (int) (this.nodeMap() ^ bitpos);
      final int dataMap = (int) (this.dataMap() | bitpos);

      final int key = node.getKey(0);

      switch (bitIndex) {
        case 0:
          switch (valIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key, key1, key2, key3, key4, node2, node3,
                  node4);
            case 1:
              return nodeOf(mutator, nodeMap, dataMap, key1, key, key2, key3, key4, node2, node3,
                  node4);
            case 2:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key, key3, key4, node2, node3,
                  node4);
            case 3:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key, key4, node2, node3,
                  node4);
            case 4:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key4, key, node2, node3,
                  node4);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        case 1:
          switch (valIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key, key1, key2, key3, key4, node1, node3,
                  node4);
            case 1:
              return nodeOf(mutator, nodeMap, dataMap, key1, key, key2, key3, key4, node1, node3,
                  node4);
            case 2:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key, key3, key4, node1, node3,
                  node4);
            case 3:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key, key4, node1, node3,
                  node4);
            case 4:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key4, key, node1, node3,
                  node4);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        case 2:
          switch (valIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key, key1, key2, key3, key4, node1, node2,
                  node4);
            case 1:
              return nodeOf(mutator, nodeMap, dataMap, key1, key, key2, key3, key4, node1, node2,
                  node4);
            case 2:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key, key3, key4, node1, node2,
                  node4);
            case 3:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key, key4, node1, node2,
                  node4);
            case 4:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key4, key, node1, node2,
                  node4);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        case 3:
          switch (valIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key, key1, key2, key3, key4, node1, node2,
                  node3);
            case 1:
              return nodeOf(mutator, nodeMap, dataMap, key1, key, key2, key3, key4, node1, node2,
                  node3);
            case 2:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key, key3, key4, node1, node2,
                  node3);
            case 3:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key, key4, node1, node2,
                  node3);
            case 4:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key4, key, node1, node2,
                  node3);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;

      result = prime * result + ((int) nodeMap());
      result = prime * result + ((int) dataMap());

      result = prime * result + (int) key1;
      result = prime * result + (int) key2;
      result = prime * result + (int) key3;
      result = prime * result + (int) key4;

      result = prime * result + node1.hashCode();
      result = prime * result + node2.hashCode();
      result = prime * result + node3.hashCode();
      result = prime * result + node4.hashCode();

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
      Set4To4Node_5Bits_Spec0To8_IntKey that = (Set4To4Node_5Bits_Spec0To8_IntKey) other;

      if (nodeMap() != that.nodeMap()) {
        return false;
      }
      if (dataMap() != that.dataMap()) {
        return false;
      }

      if (!(key1 == that.key1)) {
        return false;
      }
      if (!(key2 == that.key2)) {
        return false;
      }
      if (!(key3 == that.key3)) {
        return false;
      }
      if (!(key4 == that.key4)) {
        return false;
      }
      if (!(node1.equals(that.node1))) {
        return false;
      }
      if (!(node2.equals(that.node2))) {
        return false;
      }
      if (!(node3.equals(that.node3))) {
        return false;
      }
      if (!(node4.equals(that.node4))) {
        return false;
      }

      return true;
    }

  }

  private static final class Set5To0Node_5Bits_Spec0To8_IntKey extends CompactValuesOnlySetNode {

    private final int key1;
    private final int key2;
    private final int key3;
    private final int key4;
    private final int key5;

    Set5To0Node_5Bits_Spec0To8_IntKey(final AtomicReference<Thread> mutator, final int nodeMap,
        final int dataMap, final int key1, final int key2, final int key3, final int key4,
        final int key5) {
      super(mutator, nodeMap, dataMap);
      this.key1 = key1;
      this.key2 = key2;
      this.key3 = key3;
      this.key4 = key4;
      this.key5 = key5;

      assert nodeInvariant();
    }

    @Override
    boolean hasSlots() {
      return true;
    }

    @Override
    int slotArity() {
      return 5;
    }

    @Override
    Object getSlot(final int index) {
      final int boundary = payloadArity();

      if (index < boundary) {
        return getKey(index);
      } else {
        return getNode(index - boundary);
      }
    }

    @Override
    CompactSetNode getNode(int index) {
      throw new IllegalStateException("Index out of range.");
    }

    @Override
    int getKey(int index) {
      switch (index) {
        case 0:
          return key1;
        case 1:
          return key2;
        case 2:
          return key3;
        case 3:
          return key4;
        case 4:
          return key5;
        default:
          throw new IllegalStateException("Index out of range.");
      }
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
    boolean hasPayload() {
      return true;
    }

    @Override
    int payloadArity() {
      return 5;
    }

    @Override
    byte sizePredicate() {
      return SIZE_MORE_THAN_ONE;
    }

    @Override
    CompactSetNode copyAndInsertValue(AtomicReference<Thread> mutator, final int bitpos,
        final int key) {
      final int valIndex = dataIndex(bitpos);

      final int nodeMap = (int) (this.nodeMap());
      final int dataMap = (int) (this.dataMap() | bitpos);

      switch (valIndex) {
        case 0:
          return nodeOf(mutator, nodeMap, dataMap, key, key1, key2, key3, key4, key5);
        case 1:
          return nodeOf(mutator, nodeMap, dataMap, key1, key, key2, key3, key4, key5);
        case 2:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2, key, key3, key4, key5);
        case 3:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key, key4, key5);
        case 4:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key4, key, key5);
        case 5:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key4, key5, key);
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    CompactSetNode copyAndRemoveValue(AtomicReference<Thread> mutator, final int bitpos) {
      final int valIndex = dataIndex(bitpos);

      final int nodeMap = (int) (this.nodeMap());
      final int dataMap = (int) (this.dataMap() ^ bitpos);

      switch (valIndex) {
        case 0:
          return nodeOf(mutator, nodeMap, dataMap, key2, key3, key4, key5);
        case 1:
          return nodeOf(mutator, nodeMap, dataMap, key1, key3, key4, key5);
        case 2:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2, key4, key5);
        case 3:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key5);
        case 4:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key4);
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    CompactSetNode copyAndSetNode(AtomicReference<Thread> mutator, final int bitpos,
        CompactSetNode node) {
      throw new IllegalStateException("Index out of range.");
    }

    @Override
    CompactSetNode copyAndMigrateFromInlineToNode(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetNode node) {
      final int bitIndex = nodeIndex(bitpos);
      final int valIndex = dataIndex(bitpos);

      final int nodeMap = (int) (this.nodeMap() | bitpos);
      final int dataMap = (int) (this.dataMap() ^ bitpos);

      switch (valIndex) {
        case 0:
          switch (bitIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key2, key3, key4, key5, node);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        case 1:
          switch (bitIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key1, key3, key4, key5, node);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        case 2:
          switch (bitIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key4, key5, node);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        case 3:
          switch (bitIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key5, node);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        case 4:
          switch (bitIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key4, node);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    CompactSetNode copyAndMigrateFromNodeToInline(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetNode node) {
      throw new IllegalStateException("Index out of range.");
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;

      result = prime * result + ((int) nodeMap());
      result = prime * result + ((int) dataMap());

      result = prime * result + (int) key1;
      result = prime * result + (int) key2;
      result = prime * result + (int) key3;
      result = prime * result + (int) key4;
      result = prime * result + (int) key5;

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
      Set5To0Node_5Bits_Spec0To8_IntKey that = (Set5To0Node_5Bits_Spec0To8_IntKey) other;

      if (nodeMap() != that.nodeMap()) {
        return false;
      }
      if (dataMap() != that.dataMap()) {
        return false;
      }

      if (!(key1 == that.key1)) {
        return false;
      }
      if (!(key2 == that.key2)) {
        return false;
      }
      if (!(key3 == that.key3)) {
        return false;
      }
      if (!(key4 == that.key4)) {
        return false;
      }
      if (!(key5 == that.key5)) {
        return false;
      }

      return true;
    }

  }

  private static final class Set5To1Node_5Bits_Spec0To8_IntKey extends CompactMixedSetNode {

    private final int key1;
    private final int key2;
    private final int key3;
    private final int key4;
    private final int key5;
    private final CompactSetNode node1;

    Set5To1Node_5Bits_Spec0To8_IntKey(final AtomicReference<Thread> mutator, final int nodeMap,
        final int dataMap, final int key1, final int key2, final int key3, final int key4,
        final int key5, final CompactSetNode node1) {
      super(mutator, nodeMap, dataMap);
      this.key1 = key1;
      this.key2 = key2;
      this.key3 = key3;
      this.key4 = key4;
      this.key5 = key5;
      this.node1 = node1;

      assert nodeInvariant();
    }

    @Override
    boolean hasSlots() {
      return true;
    }

    @Override
    int slotArity() {
      return 6;
    }

    @Override
    Object getSlot(final int index) {
      final int boundary = payloadArity();

      if (index < boundary) {
        return getKey(index);
      } else {
        return getNode(index - boundary);
      }
    }

    @Override
    CompactSetNode getNode(int index) {
      switch (index) {
        case 0:
          return node1;
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    int getKey(int index) {
      switch (index) {
        case 0:
          return key1;
        case 1:
          return key2;
        case 2:
          return key3;
        case 3:
          return key4;
        case 4:
          return key5;
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    boolean hasNodes() {
      return true;
    }

    @Override
    int nodeArity() {
      return 1;
    }

    @Override
    boolean hasPayload() {
      return true;
    }

    @Override
    int payloadArity() {
      return 5;
    }

    @Override
    byte sizePredicate() {
      return SIZE_MORE_THAN_ONE;
    }

    @Override
    CompactSetNode copyAndInsertValue(AtomicReference<Thread> mutator, final int bitpos,
        final int key) {
      final int valIndex = dataIndex(bitpos);

      final int nodeMap = (int) (this.nodeMap());
      final int dataMap = (int) (this.dataMap() | bitpos);

      switch (valIndex) {
        case 0:
          return nodeOf(mutator, nodeMap, dataMap, key, key1, key2, key3, key4, key5, node1);
        case 1:
          return nodeOf(mutator, nodeMap, dataMap, key1, key, key2, key3, key4, key5, node1);
        case 2:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2, key, key3, key4, key5, node1);
        case 3:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key, key4, key5, node1);
        case 4:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key4, key, key5, node1);
        case 5:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key4, key5, key, node1);
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    CompactSetNode copyAndRemoveValue(AtomicReference<Thread> mutator, final int bitpos) {
      final int valIndex = dataIndex(bitpos);

      final int nodeMap = (int) (this.nodeMap());
      final int dataMap = (int) (this.dataMap() ^ bitpos);

      switch (valIndex) {
        case 0:
          return nodeOf(mutator, nodeMap, dataMap, key2, key3, key4, key5, node1);
        case 1:
          return nodeOf(mutator, nodeMap, dataMap, key1, key3, key4, key5, node1);
        case 2:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2, key4, key5, node1);
        case 3:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key5, node1);
        case 4:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key4, node1);
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    CompactSetNode copyAndSetNode(AtomicReference<Thread> mutator, final int bitpos,
        CompactSetNode node) {
      final int index = nodeIndex(bitpos);

      final int nodeMap = this.nodeMap();
      final int dataMap = this.dataMap();

      switch (index) {
        case 0:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key4, key5, node);
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    CompactSetNode copyAndMigrateFromInlineToNode(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetNode node) {
      final int bitIndex = nodeIndex(bitpos);
      final int valIndex = dataIndex(bitpos);

      final int nodeMap = (int) (this.nodeMap() | bitpos);
      final int dataMap = (int) (this.dataMap() ^ bitpos);

      switch (valIndex) {
        case 0:
          switch (bitIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key2, key3, key4, key5, node, node1);
            case 1:
              return nodeOf(mutator, nodeMap, dataMap, key2, key3, key4, key5, node1, node);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        case 1:
          switch (bitIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key1, key3, key4, key5, node, node1);
            case 1:
              return nodeOf(mutator, nodeMap, dataMap, key1, key3, key4, key5, node1, node);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        case 2:
          switch (bitIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key4, key5, node, node1);
            case 1:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key4, key5, node1, node);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        case 3:
          switch (bitIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key5, node, node1);
            case 1:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key5, node1, node);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        case 4:
          switch (bitIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key4, node, node1);
            case 1:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key4, node1, node);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    CompactSetNode copyAndMigrateFromNodeToInline(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetNode node) {
      final int bitIndex = nodeIndex(bitpos);
      final int valIndex = dataIndex(bitpos);

      final int nodeMap = (int) (this.nodeMap() ^ bitpos);
      final int dataMap = (int) (this.dataMap() | bitpos);

      final int key = node.getKey(0);

      switch (bitIndex) {
        case 0:
          switch (valIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key, key1, key2, key3, key4, key5);
            case 1:
              return nodeOf(mutator, nodeMap, dataMap, key1, key, key2, key3, key4, key5);
            case 2:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key, key3, key4, key5);
            case 3:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key, key4, key5);
            case 4:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key4, key, key5);
            case 5:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key4, key5, key);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;

      result = prime * result + ((int) nodeMap());
      result = prime * result + ((int) dataMap());

      result = prime * result + (int) key1;
      result = prime * result + (int) key2;
      result = prime * result + (int) key3;
      result = prime * result + (int) key4;
      result = prime * result + (int) key5;

      result = prime * result + node1.hashCode();

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
      Set5To1Node_5Bits_Spec0To8_IntKey that = (Set5To1Node_5Bits_Spec0To8_IntKey) other;

      if (nodeMap() != that.nodeMap()) {
        return false;
      }
      if (dataMap() != that.dataMap()) {
        return false;
      }

      if (!(key1 == that.key1)) {
        return false;
      }
      if (!(key2 == that.key2)) {
        return false;
      }
      if (!(key3 == that.key3)) {
        return false;
      }
      if (!(key4 == that.key4)) {
        return false;
      }
      if (!(key5 == that.key5)) {
        return false;
      }
      if (!(node1.equals(that.node1))) {
        return false;
      }

      return true;
    }

  }

  private static final class Set5To2Node_5Bits_Spec0To8_IntKey extends CompactMixedSetNode {

    private final int key1;
    private final int key2;
    private final int key3;
    private final int key4;
    private final int key5;
    private final CompactSetNode node1;
    private final CompactSetNode node2;

    Set5To2Node_5Bits_Spec0To8_IntKey(final AtomicReference<Thread> mutator, final int nodeMap,
        final int dataMap, final int key1, final int key2, final int key3, final int key4,
        final int key5, final CompactSetNode node1, final CompactSetNode node2) {
      super(mutator, nodeMap, dataMap);
      this.key1 = key1;
      this.key2 = key2;
      this.key3 = key3;
      this.key4 = key4;
      this.key5 = key5;
      this.node1 = node1;
      this.node2 = node2;

      assert nodeInvariant();
    }

    @Override
    boolean hasSlots() {
      return true;
    }

    @Override
    int slotArity() {
      return 7;
    }

    @Override
    Object getSlot(final int index) {
      final int boundary = payloadArity();

      if (index < boundary) {
        return getKey(index);
      } else {
        return getNode(index - boundary);
      }
    }

    @Override
    CompactSetNode getNode(int index) {
      switch (index) {
        case 0:
          return node1;
        case 1:
          return node2;
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    int getKey(int index) {
      switch (index) {
        case 0:
          return key1;
        case 1:
          return key2;
        case 2:
          return key3;
        case 3:
          return key4;
        case 4:
          return key5;
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    boolean hasNodes() {
      return true;
    }

    @Override
    int nodeArity() {
      return 2;
    }

    @Override
    boolean hasPayload() {
      return true;
    }

    @Override
    int payloadArity() {
      return 5;
    }

    @Override
    byte sizePredicate() {
      return SIZE_MORE_THAN_ONE;
    }

    @Override
    CompactSetNode copyAndInsertValue(AtomicReference<Thread> mutator, final int bitpos,
        final int key) {
      final int valIndex = dataIndex(bitpos);

      final int nodeMap = (int) (this.nodeMap());
      final int dataMap = (int) (this.dataMap() | bitpos);

      switch (valIndex) {
        case 0:
          return nodeOf(mutator, nodeMap, dataMap, key, key1, key2, key3, key4, key5, node1, node2);
        case 1:
          return nodeOf(mutator, nodeMap, dataMap, key1, key, key2, key3, key4, key5, node1, node2);
        case 2:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2, key, key3, key4, key5, node1, node2);
        case 3:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key, key4, key5, node1, node2);
        case 4:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key4, key, key5, node1, node2);
        case 5:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key4, key5, key, node1, node2);
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    CompactSetNode copyAndRemoveValue(AtomicReference<Thread> mutator, final int bitpos) {
      final int valIndex = dataIndex(bitpos);

      final int nodeMap = (int) (this.nodeMap());
      final int dataMap = (int) (this.dataMap() ^ bitpos);

      switch (valIndex) {
        case 0:
          return nodeOf(mutator, nodeMap, dataMap, key2, key3, key4, key5, node1, node2);
        case 1:
          return nodeOf(mutator, nodeMap, dataMap, key1, key3, key4, key5, node1, node2);
        case 2:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2, key4, key5, node1, node2);
        case 3:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key5, node1, node2);
        case 4:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key4, node1, node2);
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    CompactSetNode copyAndSetNode(AtomicReference<Thread> mutator, final int bitpos,
        CompactSetNode node) {
      final int index = nodeIndex(bitpos);

      final int nodeMap = this.nodeMap();
      final int dataMap = this.dataMap();

      switch (index) {
        case 0:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key4, key5, node, node2);
        case 1:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key4, key5, node1, node);
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    CompactSetNode copyAndMigrateFromInlineToNode(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetNode node) {
      final int bitIndex = nodeIndex(bitpos);
      final int valIndex = dataIndex(bitpos);

      final int nodeMap = (int) (this.nodeMap() | bitpos);
      final int dataMap = (int) (this.dataMap() ^ bitpos);

      switch (valIndex) {
        case 0:
          switch (bitIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key2, key3, key4, key5, node, node1, node2);
            case 1:
              return nodeOf(mutator, nodeMap, dataMap, key2, key3, key4, key5, node1, node, node2);
            case 2:
              return nodeOf(mutator, nodeMap, dataMap, key2, key3, key4, key5, node1, node2, node);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        case 1:
          switch (bitIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key1, key3, key4, key5, node, node1, node2);
            case 1:
              return nodeOf(mutator, nodeMap, dataMap, key1, key3, key4, key5, node1, node, node2);
            case 2:
              return nodeOf(mutator, nodeMap, dataMap, key1, key3, key4, key5, node1, node2, node);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        case 2:
          switch (bitIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key4, key5, node, node1, node2);
            case 1:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key4, key5, node1, node, node2);
            case 2:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key4, key5, node1, node2, node);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        case 3:
          switch (bitIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key5, node, node1, node2);
            case 1:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key5, node1, node, node2);
            case 2:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key5, node1, node2, node);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        case 4:
          switch (bitIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key4, node, node1, node2);
            case 1:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key4, node1, node, node2);
            case 2:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key4, node1, node2, node);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    CompactSetNode copyAndMigrateFromNodeToInline(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetNode node) {
      final int bitIndex = nodeIndex(bitpos);
      final int valIndex = dataIndex(bitpos);

      final int nodeMap = (int) (this.nodeMap() ^ bitpos);
      final int dataMap = (int) (this.dataMap() | bitpos);

      final int key = node.getKey(0);

      switch (bitIndex) {
        case 0:
          switch (valIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key, key1, key2, key3, key4, key5, node2);
            case 1:
              return nodeOf(mutator, nodeMap, dataMap, key1, key, key2, key3, key4, key5, node2);
            case 2:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key, key3, key4, key5, node2);
            case 3:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key, key4, key5, node2);
            case 4:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key4, key, key5, node2);
            case 5:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key4, key5, key, node2);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        case 1:
          switch (valIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key, key1, key2, key3, key4, key5, node1);
            case 1:
              return nodeOf(mutator, nodeMap, dataMap, key1, key, key2, key3, key4, key5, node1);
            case 2:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key, key3, key4, key5, node1);
            case 3:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key, key4, key5, node1);
            case 4:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key4, key, key5, node1);
            case 5:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key4, key5, key, node1);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;

      result = prime * result + ((int) nodeMap());
      result = prime * result + ((int) dataMap());

      result = prime * result + (int) key1;
      result = prime * result + (int) key2;
      result = prime * result + (int) key3;
      result = prime * result + (int) key4;
      result = prime * result + (int) key5;

      result = prime * result + node1.hashCode();
      result = prime * result + node2.hashCode();

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
      Set5To2Node_5Bits_Spec0To8_IntKey that = (Set5To2Node_5Bits_Spec0To8_IntKey) other;

      if (nodeMap() != that.nodeMap()) {
        return false;
      }
      if (dataMap() != that.dataMap()) {
        return false;
      }

      if (!(key1 == that.key1)) {
        return false;
      }
      if (!(key2 == that.key2)) {
        return false;
      }
      if (!(key3 == that.key3)) {
        return false;
      }
      if (!(key4 == that.key4)) {
        return false;
      }
      if (!(key5 == that.key5)) {
        return false;
      }
      if (!(node1.equals(that.node1))) {
        return false;
      }
      if (!(node2.equals(that.node2))) {
        return false;
      }

      return true;
    }

  }

  private static final class Set5To3Node_5Bits_Spec0To8_IntKey extends CompactMixedSetNode {

    private final int key1;
    private final int key2;
    private final int key3;
    private final int key4;
    private final int key5;
    private final CompactSetNode node1;
    private final CompactSetNode node2;
    private final CompactSetNode node3;

    Set5To3Node_5Bits_Spec0To8_IntKey(final AtomicReference<Thread> mutator, final int nodeMap,
        final int dataMap, final int key1, final int key2, final int key3, final int key4,
        final int key5, final CompactSetNode node1, final CompactSetNode node2,
        final CompactSetNode node3) {
      super(mutator, nodeMap, dataMap);
      this.key1 = key1;
      this.key2 = key2;
      this.key3 = key3;
      this.key4 = key4;
      this.key5 = key5;
      this.node1 = node1;
      this.node2 = node2;
      this.node3 = node3;

      assert nodeInvariant();
    }

    @Override
    boolean hasSlots() {
      return true;
    }

    @Override
    int slotArity() {
      return 8;
    }

    @Override
    Object getSlot(final int index) {
      final int boundary = payloadArity();

      if (index < boundary) {
        return getKey(index);
      } else {
        return getNode(index - boundary);
      }
    }

    @Override
    CompactSetNode getNode(int index) {
      switch (index) {
        case 0:
          return node1;
        case 1:
          return node2;
        case 2:
          return node3;
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    int getKey(int index) {
      switch (index) {
        case 0:
          return key1;
        case 1:
          return key2;
        case 2:
          return key3;
        case 3:
          return key4;
        case 4:
          return key5;
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    boolean hasNodes() {
      return true;
    }

    @Override
    int nodeArity() {
      return 3;
    }

    @Override
    boolean hasPayload() {
      return true;
    }

    @Override
    int payloadArity() {
      return 5;
    }

    @Override
    byte sizePredicate() {
      return SIZE_MORE_THAN_ONE;
    }

    @Override
    CompactSetNode copyAndInsertValue(AtomicReference<Thread> mutator, final int bitpos,
        final int key) {
      final int valIndex = dataIndex(bitpos);

      final int nodeMap = (int) (this.nodeMap());
      final int dataMap = (int) (this.dataMap() | bitpos);

      switch (valIndex) {
        case 0:
          return nodeOf(mutator, nodeMap, dataMap, key, key1, key2, key3, key4, key5, node1, node2,
              node3);
        case 1:
          return nodeOf(mutator, nodeMap, dataMap, key1, key, key2, key3, key4, key5, node1, node2,
              node3);
        case 2:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2, key, key3, key4, key5, node1, node2,
              node3);
        case 3:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key, key4, key5, node1, node2,
              node3);
        case 4:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key4, key, key5, node1, node2,
              node3);
        case 5:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key4, key5, key, node1, node2,
              node3);
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    CompactSetNode copyAndRemoveValue(AtomicReference<Thread> mutator, final int bitpos) {
      final int valIndex = dataIndex(bitpos);

      final int nodeMap = (int) (this.nodeMap());
      final int dataMap = (int) (this.dataMap() ^ bitpos);

      switch (valIndex) {
        case 0:
          return nodeOf(mutator, nodeMap, dataMap, key2, key3, key4, key5, node1, node2, node3);
        case 1:
          return nodeOf(mutator, nodeMap, dataMap, key1, key3, key4, key5, node1, node2, node3);
        case 2:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2, key4, key5, node1, node2, node3);
        case 3:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key5, node1, node2, node3);
        case 4:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key4, node1, node2, node3);
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    CompactSetNode copyAndSetNode(AtomicReference<Thread> mutator, final int bitpos,
        CompactSetNode node) {
      final int index = nodeIndex(bitpos);

      final int nodeMap = this.nodeMap();
      final int dataMap = this.dataMap();

      switch (index) {
        case 0:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key4, key5, node, node2,
              node3);
        case 1:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key4, key5, node1, node,
              node3);
        case 2:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key4, key5, node1, node2,
              node);
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    CompactSetNode copyAndMigrateFromInlineToNode(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetNode node) {
      final int bitIndex = nodeIndex(bitpos);
      final int valIndex = dataIndex(bitpos);

      final int nodeMap = (int) (this.nodeMap() | bitpos);
      final int dataMap = (int) (this.dataMap() ^ bitpos);

      switch (valIndex) {
        case 0:
          switch (bitIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key2, key3, key4, key5, node, node1, node2,
                  node3);
            case 1:
              return nodeOf(mutator, nodeMap, dataMap, key2, key3, key4, key5, node1, node, node2,
                  node3);
            case 2:
              return nodeOf(mutator, nodeMap, dataMap, key2, key3, key4, key5, node1, node2, node,
                  node3);
            case 3:
              return nodeOf(mutator, nodeMap, dataMap, key2, key3, key4, key5, node1, node2, node3,
                  node);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        case 1:
          switch (bitIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key1, key3, key4, key5, node, node1, node2,
                  node3);
            case 1:
              return nodeOf(mutator, nodeMap, dataMap, key1, key3, key4, key5, node1, node, node2,
                  node3);
            case 2:
              return nodeOf(mutator, nodeMap, dataMap, key1, key3, key4, key5, node1, node2, node,
                  node3);
            case 3:
              return nodeOf(mutator, nodeMap, dataMap, key1, key3, key4, key5, node1, node2, node3,
                  node);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        case 2:
          switch (bitIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key4, key5, node, node1, node2,
                  node3);
            case 1:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key4, key5, node1, node, node2,
                  node3);
            case 2:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key4, key5, node1, node2, node,
                  node3);
            case 3:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key4, key5, node1, node2, node3,
                  node);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        case 3:
          switch (bitIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key5, node, node1, node2,
                  node3);
            case 1:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key5, node1, node, node2,
                  node3);
            case 2:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key5, node1, node2, node,
                  node3);
            case 3:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key5, node1, node2, node3,
                  node);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        case 4:
          switch (bitIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key4, node, node1, node2,
                  node3);
            case 1:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key4, node1, node, node2,
                  node3);
            case 2:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key4, node1, node2, node,
                  node3);
            case 3:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key4, node1, node2, node3,
                  node);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    CompactSetNode copyAndMigrateFromNodeToInline(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetNode node) {
      final int bitIndex = nodeIndex(bitpos);
      final int valIndex = dataIndex(bitpos);

      final int nodeMap = (int) (this.nodeMap() ^ bitpos);
      final int dataMap = (int) (this.dataMap() | bitpos);

      final int key = node.getKey(0);

      switch (bitIndex) {
        case 0:
          switch (valIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key, key1, key2, key3, key4, key5, node2,
                  node3);
            case 1:
              return nodeOf(mutator, nodeMap, dataMap, key1, key, key2, key3, key4, key5, node2,
                  node3);
            case 2:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key, key3, key4, key5, node2,
                  node3);
            case 3:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key, key4, key5, node2,
                  node3);
            case 4:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key4, key, key5, node2,
                  node3);
            case 5:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key4, key5, key, node2,
                  node3);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        case 1:
          switch (valIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key, key1, key2, key3, key4, key5, node1,
                  node3);
            case 1:
              return nodeOf(mutator, nodeMap, dataMap, key1, key, key2, key3, key4, key5, node1,
                  node3);
            case 2:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key, key3, key4, key5, node1,
                  node3);
            case 3:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key, key4, key5, node1,
                  node3);
            case 4:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key4, key, key5, node1,
                  node3);
            case 5:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key4, key5, key, node1,
                  node3);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        case 2:
          switch (valIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key, key1, key2, key3, key4, key5, node1,
                  node2);
            case 1:
              return nodeOf(mutator, nodeMap, dataMap, key1, key, key2, key3, key4, key5, node1,
                  node2);
            case 2:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key, key3, key4, key5, node1,
                  node2);
            case 3:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key, key4, key5, node1,
                  node2);
            case 4:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key4, key, key5, node1,
                  node2);
            case 5:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key4, key5, key, node1,
                  node2);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;

      result = prime * result + ((int) nodeMap());
      result = prime * result + ((int) dataMap());

      result = prime * result + (int) key1;
      result = prime * result + (int) key2;
      result = prime * result + (int) key3;
      result = prime * result + (int) key4;
      result = prime * result + (int) key5;

      result = prime * result + node1.hashCode();
      result = prime * result + node2.hashCode();
      result = prime * result + node3.hashCode();

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
      Set5To3Node_5Bits_Spec0To8_IntKey that = (Set5To3Node_5Bits_Spec0To8_IntKey) other;

      if (nodeMap() != that.nodeMap()) {
        return false;
      }
      if (dataMap() != that.dataMap()) {
        return false;
      }

      if (!(key1 == that.key1)) {
        return false;
      }
      if (!(key2 == that.key2)) {
        return false;
      }
      if (!(key3 == that.key3)) {
        return false;
      }
      if (!(key4 == that.key4)) {
        return false;
      }
      if (!(key5 == that.key5)) {
        return false;
      }
      if (!(node1.equals(that.node1))) {
        return false;
      }
      if (!(node2.equals(that.node2))) {
        return false;
      }
      if (!(node3.equals(that.node3))) {
        return false;
      }

      return true;
    }

  }

  private static final class Set6To0Node_5Bits_Spec0To8_IntKey extends CompactValuesOnlySetNode {

    private final int key1;
    private final int key2;
    private final int key3;
    private final int key4;
    private final int key5;
    private final int key6;

    Set6To0Node_5Bits_Spec0To8_IntKey(final AtomicReference<Thread> mutator, final int nodeMap,
        final int dataMap, final int key1, final int key2, final int key3, final int key4,
        final int key5, final int key6) {
      super(mutator, nodeMap, dataMap);
      this.key1 = key1;
      this.key2 = key2;
      this.key3 = key3;
      this.key4 = key4;
      this.key5 = key5;
      this.key6 = key6;

      assert nodeInvariant();
    }

    @Override
    boolean hasSlots() {
      return true;
    }

    @Override
    int slotArity() {
      return 6;
    }

    @Override
    Object getSlot(final int index) {
      final int boundary = payloadArity();

      if (index < boundary) {
        return getKey(index);
      } else {
        return getNode(index - boundary);
      }
    }

    @Override
    CompactSetNode getNode(int index) {
      throw new IllegalStateException("Index out of range.");
    }

    @Override
    int getKey(int index) {
      switch (index) {
        case 0:
          return key1;
        case 1:
          return key2;
        case 2:
          return key3;
        case 3:
          return key4;
        case 4:
          return key5;
        case 5:
          return key6;
        default:
          throw new IllegalStateException("Index out of range.");
      }
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
    boolean hasPayload() {
      return true;
    }

    @Override
    int payloadArity() {
      return 6;
    }

    @Override
    byte sizePredicate() {
      return SIZE_MORE_THAN_ONE;
    }

    @Override
    CompactSetNode copyAndInsertValue(AtomicReference<Thread> mutator, final int bitpos,
        final int key) {
      final int valIndex = dataIndex(bitpos);

      final int nodeMap = (int) (this.nodeMap());
      final int dataMap = (int) (this.dataMap() | bitpos);

      switch (valIndex) {
        case 0:
          return nodeOf(mutator, nodeMap, dataMap, key, key1, key2, key3, key4, key5, key6);
        case 1:
          return nodeOf(mutator, nodeMap, dataMap, key1, key, key2, key3, key4, key5, key6);
        case 2:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2, key, key3, key4, key5, key6);
        case 3:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key, key4, key5, key6);
        case 4:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key4, key, key5, key6);
        case 5:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key4, key5, key, key6);
        case 6:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key4, key5, key6, key);
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    CompactSetNode copyAndRemoveValue(AtomicReference<Thread> mutator, final int bitpos) {
      final int valIndex = dataIndex(bitpos);

      final int nodeMap = (int) (this.nodeMap());
      final int dataMap = (int) (this.dataMap() ^ bitpos);

      switch (valIndex) {
        case 0:
          return nodeOf(mutator, nodeMap, dataMap, key2, key3, key4, key5, key6);
        case 1:
          return nodeOf(mutator, nodeMap, dataMap, key1, key3, key4, key5, key6);
        case 2:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2, key4, key5, key6);
        case 3:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key5, key6);
        case 4:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key4, key6);
        case 5:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key4, key5);
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    CompactSetNode copyAndSetNode(AtomicReference<Thread> mutator, final int bitpos,
        CompactSetNode node) {
      throw new IllegalStateException("Index out of range.");
    }

    @Override
    CompactSetNode copyAndMigrateFromInlineToNode(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetNode node) {
      final int bitIndex = nodeIndex(bitpos);
      final int valIndex = dataIndex(bitpos);

      final int nodeMap = (int) (this.nodeMap() | bitpos);
      final int dataMap = (int) (this.dataMap() ^ bitpos);

      switch (valIndex) {
        case 0:
          switch (bitIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key2, key3, key4, key5, key6, node);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        case 1:
          switch (bitIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key1, key3, key4, key5, key6, node);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        case 2:
          switch (bitIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key4, key5, key6, node);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        case 3:
          switch (bitIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key5, key6, node);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        case 4:
          switch (bitIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key4, key6, node);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        case 5:
          switch (bitIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key4, key5, node);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    CompactSetNode copyAndMigrateFromNodeToInline(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetNode node) {
      throw new IllegalStateException("Index out of range.");
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;

      result = prime * result + ((int) nodeMap());
      result = prime * result + ((int) dataMap());

      result = prime * result + (int) key1;
      result = prime * result + (int) key2;
      result = prime * result + (int) key3;
      result = prime * result + (int) key4;
      result = prime * result + (int) key5;
      result = prime * result + (int) key6;

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
      Set6To0Node_5Bits_Spec0To8_IntKey that = (Set6To0Node_5Bits_Spec0To8_IntKey) other;

      if (nodeMap() != that.nodeMap()) {
        return false;
      }
      if (dataMap() != that.dataMap()) {
        return false;
      }

      if (!(key1 == that.key1)) {
        return false;
      }
      if (!(key2 == that.key2)) {
        return false;
      }
      if (!(key3 == that.key3)) {
        return false;
      }
      if (!(key4 == that.key4)) {
        return false;
      }
      if (!(key5 == that.key5)) {
        return false;
      }
      if (!(key6 == that.key6)) {
        return false;
      }

      return true;
    }

  }

  private static final class Set6To1Node_5Bits_Spec0To8_IntKey extends CompactMixedSetNode {

    private final int key1;
    private final int key2;
    private final int key3;
    private final int key4;
    private final int key5;
    private final int key6;
    private final CompactSetNode node1;

    Set6To1Node_5Bits_Spec0To8_IntKey(final AtomicReference<Thread> mutator, final int nodeMap,
        final int dataMap, final int key1, final int key2, final int key3, final int key4,
        final int key5, final int key6, final CompactSetNode node1) {
      super(mutator, nodeMap, dataMap);
      this.key1 = key1;
      this.key2 = key2;
      this.key3 = key3;
      this.key4 = key4;
      this.key5 = key5;
      this.key6 = key6;
      this.node1 = node1;

      assert nodeInvariant();
    }

    @Override
    boolean hasSlots() {
      return true;
    }

    @Override
    int slotArity() {
      return 7;
    }

    @Override
    Object getSlot(final int index) {
      final int boundary = payloadArity();

      if (index < boundary) {
        return getKey(index);
      } else {
        return getNode(index - boundary);
      }
    }

    @Override
    CompactSetNode getNode(int index) {
      switch (index) {
        case 0:
          return node1;
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    int getKey(int index) {
      switch (index) {
        case 0:
          return key1;
        case 1:
          return key2;
        case 2:
          return key3;
        case 3:
          return key4;
        case 4:
          return key5;
        case 5:
          return key6;
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    boolean hasNodes() {
      return true;
    }

    @Override
    int nodeArity() {
      return 1;
    }

    @Override
    boolean hasPayload() {
      return true;
    }

    @Override
    int payloadArity() {
      return 6;
    }

    @Override
    byte sizePredicate() {
      return SIZE_MORE_THAN_ONE;
    }

    @Override
    CompactSetNode copyAndInsertValue(AtomicReference<Thread> mutator, final int bitpos,
        final int key) {
      final int valIndex = dataIndex(bitpos);

      final int nodeMap = (int) (this.nodeMap());
      final int dataMap = (int) (this.dataMap() | bitpos);

      switch (valIndex) {
        case 0:
          return nodeOf(mutator, nodeMap, dataMap, key, key1, key2, key3, key4, key5, key6, node1);
        case 1:
          return nodeOf(mutator, nodeMap, dataMap, key1, key, key2, key3, key4, key5, key6, node1);
        case 2:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2, key, key3, key4, key5, key6, node1);
        case 3:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key, key4, key5, key6, node1);
        case 4:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key4, key, key5, key6, node1);
        case 5:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key4, key5, key, key6, node1);
        case 6:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key4, key5, key6, key, node1);
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    CompactSetNode copyAndRemoveValue(AtomicReference<Thread> mutator, final int bitpos) {
      final int valIndex = dataIndex(bitpos);

      final int nodeMap = (int) (this.nodeMap());
      final int dataMap = (int) (this.dataMap() ^ bitpos);

      switch (valIndex) {
        case 0:
          return nodeOf(mutator, nodeMap, dataMap, key2, key3, key4, key5, key6, node1);
        case 1:
          return nodeOf(mutator, nodeMap, dataMap, key1, key3, key4, key5, key6, node1);
        case 2:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2, key4, key5, key6, node1);
        case 3:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key5, key6, node1);
        case 4:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key4, key6, node1);
        case 5:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key4, key5, node1);
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    CompactSetNode copyAndSetNode(AtomicReference<Thread> mutator, final int bitpos,
        CompactSetNode node) {
      final int index = nodeIndex(bitpos);

      final int nodeMap = this.nodeMap();
      final int dataMap = this.dataMap();

      switch (index) {
        case 0:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key4, key5, key6, node);
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    CompactSetNode copyAndMigrateFromInlineToNode(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetNode node) {
      final int bitIndex = nodeIndex(bitpos);
      final int valIndex = dataIndex(bitpos);

      final int nodeMap = (int) (this.nodeMap() | bitpos);
      final int dataMap = (int) (this.dataMap() ^ bitpos);

      switch (valIndex) {
        case 0:
          switch (bitIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key2, key3, key4, key5, key6, node, node1);
            case 1:
              return nodeOf(mutator, nodeMap, dataMap, key2, key3, key4, key5, key6, node1, node);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        case 1:
          switch (bitIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key1, key3, key4, key5, key6, node, node1);
            case 1:
              return nodeOf(mutator, nodeMap, dataMap, key1, key3, key4, key5, key6, node1, node);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        case 2:
          switch (bitIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key4, key5, key6, node, node1);
            case 1:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key4, key5, key6, node1, node);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        case 3:
          switch (bitIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key5, key6, node, node1);
            case 1:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key5, key6, node1, node);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        case 4:
          switch (bitIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key4, key6, node, node1);
            case 1:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key4, key6, node1, node);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        case 5:
          switch (bitIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key4, key5, node, node1);
            case 1:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key4, key5, node1, node);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    CompactSetNode copyAndMigrateFromNodeToInline(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetNode node) {
      final int bitIndex = nodeIndex(bitpos);
      final int valIndex = dataIndex(bitpos);

      final int nodeMap = (int) (this.nodeMap() ^ bitpos);
      final int dataMap = (int) (this.dataMap() | bitpos);

      final int key = node.getKey(0);

      switch (bitIndex) {
        case 0:
          switch (valIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key, key1, key2, key3, key4, key5, key6);
            case 1:
              return nodeOf(mutator, nodeMap, dataMap, key1, key, key2, key3, key4, key5, key6);
            case 2:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key, key3, key4, key5, key6);
            case 3:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key, key4, key5, key6);
            case 4:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key4, key, key5, key6);
            case 5:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key4, key5, key, key6);
            case 6:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key4, key5, key6, key);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;

      result = prime * result + ((int) nodeMap());
      result = prime * result + ((int) dataMap());

      result = prime * result + (int) key1;
      result = prime * result + (int) key2;
      result = prime * result + (int) key3;
      result = prime * result + (int) key4;
      result = prime * result + (int) key5;
      result = prime * result + (int) key6;

      result = prime * result + node1.hashCode();

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
      Set6To1Node_5Bits_Spec0To8_IntKey that = (Set6To1Node_5Bits_Spec0To8_IntKey) other;

      if (nodeMap() != that.nodeMap()) {
        return false;
      }
      if (dataMap() != that.dataMap()) {
        return false;
      }

      if (!(key1 == that.key1)) {
        return false;
      }
      if (!(key2 == that.key2)) {
        return false;
      }
      if (!(key3 == that.key3)) {
        return false;
      }
      if (!(key4 == that.key4)) {
        return false;
      }
      if (!(key5 == that.key5)) {
        return false;
      }
      if (!(key6 == that.key6)) {
        return false;
      }
      if (!(node1.equals(that.node1))) {
        return false;
      }

      return true;
    }

  }

  private static final class Set6To2Node_5Bits_Spec0To8_IntKey extends CompactMixedSetNode {

    private final int key1;
    private final int key2;
    private final int key3;
    private final int key4;
    private final int key5;
    private final int key6;
    private final CompactSetNode node1;
    private final CompactSetNode node2;

    Set6To2Node_5Bits_Spec0To8_IntKey(final AtomicReference<Thread> mutator, final int nodeMap,
        final int dataMap, final int key1, final int key2, final int key3, final int key4,
        final int key5, final int key6, final CompactSetNode node1, final CompactSetNode node2) {
      super(mutator, nodeMap, dataMap);
      this.key1 = key1;
      this.key2 = key2;
      this.key3 = key3;
      this.key4 = key4;
      this.key5 = key5;
      this.key6 = key6;
      this.node1 = node1;
      this.node2 = node2;

      assert nodeInvariant();
    }

    @Override
    boolean hasSlots() {
      return true;
    }

    @Override
    int slotArity() {
      return 8;
    }

    @Override
    Object getSlot(final int index) {
      final int boundary = payloadArity();

      if (index < boundary) {
        return getKey(index);
      } else {
        return getNode(index - boundary);
      }
    }

    @Override
    CompactSetNode getNode(int index) {
      switch (index) {
        case 0:
          return node1;
        case 1:
          return node2;
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    int getKey(int index) {
      switch (index) {
        case 0:
          return key1;
        case 1:
          return key2;
        case 2:
          return key3;
        case 3:
          return key4;
        case 4:
          return key5;
        case 5:
          return key6;
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    boolean hasNodes() {
      return true;
    }

    @Override
    int nodeArity() {
      return 2;
    }

    @Override
    boolean hasPayload() {
      return true;
    }

    @Override
    int payloadArity() {
      return 6;
    }

    @Override
    byte sizePredicate() {
      return SIZE_MORE_THAN_ONE;
    }

    @Override
    CompactSetNode copyAndInsertValue(AtomicReference<Thread> mutator, final int bitpos,
        final int key) {
      final int valIndex = dataIndex(bitpos);

      final int nodeMap = (int) (this.nodeMap());
      final int dataMap = (int) (this.dataMap() | bitpos);

      switch (valIndex) {
        case 0:
          return nodeOf(mutator, nodeMap, dataMap, key, key1, key2, key3, key4, key5, key6, node1,
              node2);
        case 1:
          return nodeOf(mutator, nodeMap, dataMap, key1, key, key2, key3, key4, key5, key6, node1,
              node2);
        case 2:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2, key, key3, key4, key5, key6, node1,
              node2);
        case 3:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key, key4, key5, key6, node1,
              node2);
        case 4:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key4, key, key5, key6, node1,
              node2);
        case 5:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key4, key5, key, key6, node1,
              node2);
        case 6:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key4, key5, key6, key, node1,
              node2);
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    CompactSetNode copyAndRemoveValue(AtomicReference<Thread> mutator, final int bitpos) {
      final int valIndex = dataIndex(bitpos);

      final int nodeMap = (int) (this.nodeMap());
      final int dataMap = (int) (this.dataMap() ^ bitpos);

      switch (valIndex) {
        case 0:
          return nodeOf(mutator, nodeMap, dataMap, key2, key3, key4, key5, key6, node1, node2);
        case 1:
          return nodeOf(mutator, nodeMap, dataMap, key1, key3, key4, key5, key6, node1, node2);
        case 2:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2, key4, key5, key6, node1, node2);
        case 3:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key5, key6, node1, node2);
        case 4:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key4, key6, node1, node2);
        case 5:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key4, key5, node1, node2);
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    CompactSetNode copyAndSetNode(AtomicReference<Thread> mutator, final int bitpos,
        CompactSetNode node) {
      final int index = nodeIndex(bitpos);

      final int nodeMap = this.nodeMap();
      final int dataMap = this.dataMap();

      switch (index) {
        case 0:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key4, key5, key6, node, node2);
        case 1:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key4, key5, key6, node1, node);
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    CompactSetNode copyAndMigrateFromInlineToNode(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetNode node) {
      final int bitIndex = nodeIndex(bitpos);
      final int valIndex = dataIndex(bitpos);

      final int nodeMap = (int) (this.nodeMap() | bitpos);
      final int dataMap = (int) (this.dataMap() ^ bitpos);

      switch (valIndex) {
        case 0:
          switch (bitIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key2, key3, key4, key5, key6, node, node1,
                  node2);
            case 1:
              return nodeOf(mutator, nodeMap, dataMap, key2, key3, key4, key5, key6, node1, node,
                  node2);
            case 2:
              return nodeOf(mutator, nodeMap, dataMap, key2, key3, key4, key5, key6, node1, node2,
                  node);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        case 1:
          switch (bitIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key1, key3, key4, key5, key6, node, node1,
                  node2);
            case 1:
              return nodeOf(mutator, nodeMap, dataMap, key1, key3, key4, key5, key6, node1, node,
                  node2);
            case 2:
              return nodeOf(mutator, nodeMap, dataMap, key1, key3, key4, key5, key6, node1, node2,
                  node);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        case 2:
          switch (bitIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key4, key5, key6, node, node1,
                  node2);
            case 1:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key4, key5, key6, node1, node,
                  node2);
            case 2:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key4, key5, key6, node1, node2,
                  node);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        case 3:
          switch (bitIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key5, key6, node, node1,
                  node2);
            case 1:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key5, key6, node1, node,
                  node2);
            case 2:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key5, key6, node1, node2,
                  node);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        case 4:
          switch (bitIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key4, key6, node, node1,
                  node2);
            case 1:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key4, key6, node1, node,
                  node2);
            case 2:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key4, key6, node1, node2,
                  node);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        case 5:
          switch (bitIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key4, key5, node, node1,
                  node2);
            case 1:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key4, key5, node1, node,
                  node2);
            case 2:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key4, key5, node1, node2,
                  node);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    CompactSetNode copyAndMigrateFromNodeToInline(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetNode node) {
      final int bitIndex = nodeIndex(bitpos);
      final int valIndex = dataIndex(bitpos);

      final int nodeMap = (int) (this.nodeMap() ^ bitpos);
      final int dataMap = (int) (this.dataMap() | bitpos);

      final int key = node.getKey(0);

      switch (bitIndex) {
        case 0:
          switch (valIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key, key1, key2, key3, key4, key5, key6,
                  node2);
            case 1:
              return nodeOf(mutator, nodeMap, dataMap, key1, key, key2, key3, key4, key5, key6,
                  node2);
            case 2:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key, key3, key4, key5, key6,
                  node2);
            case 3:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key, key4, key5, key6,
                  node2);
            case 4:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key4, key, key5, key6,
                  node2);
            case 5:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key4, key5, key, key6,
                  node2);
            case 6:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key4, key5, key6, key,
                  node2);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        case 1:
          switch (valIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key, key1, key2, key3, key4, key5, key6,
                  node1);
            case 1:
              return nodeOf(mutator, nodeMap, dataMap, key1, key, key2, key3, key4, key5, key6,
                  node1);
            case 2:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key, key3, key4, key5, key6,
                  node1);
            case 3:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key, key4, key5, key6,
                  node1);
            case 4:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key4, key, key5, key6,
                  node1);
            case 5:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key4, key5, key, key6,
                  node1);
            case 6:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key4, key5, key6, key,
                  node1);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;

      result = prime * result + ((int) nodeMap());
      result = prime * result + ((int) dataMap());

      result = prime * result + (int) key1;
      result = prime * result + (int) key2;
      result = prime * result + (int) key3;
      result = prime * result + (int) key4;
      result = prime * result + (int) key5;
      result = prime * result + (int) key6;

      result = prime * result + node1.hashCode();
      result = prime * result + node2.hashCode();

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
      Set6To2Node_5Bits_Spec0To8_IntKey that = (Set6To2Node_5Bits_Spec0To8_IntKey) other;

      if (nodeMap() != that.nodeMap()) {
        return false;
      }
      if (dataMap() != that.dataMap()) {
        return false;
      }

      if (!(key1 == that.key1)) {
        return false;
      }
      if (!(key2 == that.key2)) {
        return false;
      }
      if (!(key3 == that.key3)) {
        return false;
      }
      if (!(key4 == that.key4)) {
        return false;
      }
      if (!(key5 == that.key5)) {
        return false;
      }
      if (!(key6 == that.key6)) {
        return false;
      }
      if (!(node1.equals(that.node1))) {
        return false;
      }
      if (!(node2.equals(that.node2))) {
        return false;
      }

      return true;
    }

  }

  private static final class Set7To0Node_5Bits_Spec0To8_IntKey extends CompactValuesOnlySetNode {

    private final int key1;
    private final int key2;
    private final int key3;
    private final int key4;
    private final int key5;
    private final int key6;
    private final int key7;

    Set7To0Node_5Bits_Spec0To8_IntKey(final AtomicReference<Thread> mutator, final int nodeMap,
        final int dataMap, final int key1, final int key2, final int key3, final int key4,
        final int key5, final int key6, final int key7) {
      super(mutator, nodeMap, dataMap);
      this.key1 = key1;
      this.key2 = key2;
      this.key3 = key3;
      this.key4 = key4;
      this.key5 = key5;
      this.key6 = key6;
      this.key7 = key7;

      assert nodeInvariant();
    }

    @Override
    boolean hasSlots() {
      return true;
    }

    @Override
    int slotArity() {
      return 7;
    }

    @Override
    Object getSlot(final int index) {
      final int boundary = payloadArity();

      if (index < boundary) {
        return getKey(index);
      } else {
        return getNode(index - boundary);
      }
    }

    @Override
    CompactSetNode getNode(int index) {
      throw new IllegalStateException("Index out of range.");
    }

    @Override
    int getKey(int index) {
      switch (index) {
        case 0:
          return key1;
        case 1:
          return key2;
        case 2:
          return key3;
        case 3:
          return key4;
        case 4:
          return key5;
        case 5:
          return key6;
        case 6:
          return key7;
        default:
          throw new IllegalStateException("Index out of range.");
      }
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
    boolean hasPayload() {
      return true;
    }

    @Override
    int payloadArity() {
      return 7;
    }

    @Override
    byte sizePredicate() {
      return SIZE_MORE_THAN_ONE;
    }

    @Override
    CompactSetNode copyAndInsertValue(AtomicReference<Thread> mutator, final int bitpos,
        final int key) {
      final int valIndex = dataIndex(bitpos);

      final int nodeMap = (int) (this.nodeMap());
      final int dataMap = (int) (this.dataMap() | bitpos);

      switch (valIndex) {
        case 0:
          return nodeOf(mutator, nodeMap, dataMap, key, key1, key2, key3, key4, key5, key6, key7);
        case 1:
          return nodeOf(mutator, nodeMap, dataMap, key1, key, key2, key3, key4, key5, key6, key7);
        case 2:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2, key, key3, key4, key5, key6, key7);
        case 3:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key, key4, key5, key6, key7);
        case 4:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key4, key, key5, key6, key7);
        case 5:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key4, key5, key, key6, key7);
        case 6:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key4, key5, key6, key, key7);
        case 7:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key4, key5, key6, key7, key);
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    CompactSetNode copyAndRemoveValue(AtomicReference<Thread> mutator, final int bitpos) {
      final int valIndex = dataIndex(bitpos);

      final int nodeMap = (int) (this.nodeMap());
      final int dataMap = (int) (this.dataMap() ^ bitpos);

      switch (valIndex) {
        case 0:
          return nodeOf(mutator, nodeMap, dataMap, key2, key3, key4, key5, key6, key7);
        case 1:
          return nodeOf(mutator, nodeMap, dataMap, key1, key3, key4, key5, key6, key7);
        case 2:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2, key4, key5, key6, key7);
        case 3:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key5, key6, key7);
        case 4:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key4, key6, key7);
        case 5:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key4, key5, key7);
        case 6:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key4, key5, key6);
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    CompactSetNode copyAndSetNode(AtomicReference<Thread> mutator, final int bitpos,
        CompactSetNode node) {
      throw new IllegalStateException("Index out of range.");
    }

    @Override
    CompactSetNode copyAndMigrateFromInlineToNode(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetNode node) {
      final int bitIndex = nodeIndex(bitpos);
      final int valIndex = dataIndex(bitpos);

      final int nodeMap = (int) (this.nodeMap() | bitpos);
      final int dataMap = (int) (this.dataMap() ^ bitpos);

      switch (valIndex) {
        case 0:
          switch (bitIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key2, key3, key4, key5, key6, key7, node);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        case 1:
          switch (bitIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key1, key3, key4, key5, key6, key7, node);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        case 2:
          switch (bitIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key4, key5, key6, key7, node);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        case 3:
          switch (bitIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key5, key6, key7, node);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        case 4:
          switch (bitIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key4, key6, key7, node);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        case 5:
          switch (bitIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key4, key5, key7, node);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        case 6:
          switch (bitIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key4, key5, key6, node);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    CompactSetNode copyAndMigrateFromNodeToInline(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetNode node) {
      throw new IllegalStateException("Index out of range.");
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;

      result = prime * result + ((int) nodeMap());
      result = prime * result + ((int) dataMap());

      result = prime * result + (int) key1;
      result = prime * result + (int) key2;
      result = prime * result + (int) key3;
      result = prime * result + (int) key4;
      result = prime * result + (int) key5;
      result = prime * result + (int) key6;
      result = prime * result + (int) key7;

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
      Set7To0Node_5Bits_Spec0To8_IntKey that = (Set7To0Node_5Bits_Spec0To8_IntKey) other;

      if (nodeMap() != that.nodeMap()) {
        return false;
      }
      if (dataMap() != that.dataMap()) {
        return false;
      }

      if (!(key1 == that.key1)) {
        return false;
      }
      if (!(key2 == that.key2)) {
        return false;
      }
      if (!(key3 == that.key3)) {
        return false;
      }
      if (!(key4 == that.key4)) {
        return false;
      }
      if (!(key5 == that.key5)) {
        return false;
      }
      if (!(key6 == that.key6)) {
        return false;
      }
      if (!(key7 == that.key7)) {
        return false;
      }

      return true;
    }

  }

  private static final class Set7To1Node_5Bits_Spec0To8_IntKey extends CompactMixedSetNode {

    private final int key1;
    private final int key2;
    private final int key3;
    private final int key4;
    private final int key5;
    private final int key6;
    private final int key7;
    private final CompactSetNode node1;

    Set7To1Node_5Bits_Spec0To8_IntKey(final AtomicReference<Thread> mutator, final int nodeMap,
        final int dataMap, final int key1, final int key2, final int key3, final int key4,
        final int key5, final int key6, final int key7, final CompactSetNode node1) {
      super(mutator, nodeMap, dataMap);
      this.key1 = key1;
      this.key2 = key2;
      this.key3 = key3;
      this.key4 = key4;
      this.key5 = key5;
      this.key6 = key6;
      this.key7 = key7;
      this.node1 = node1;

      assert nodeInvariant();
    }

    @Override
    boolean hasSlots() {
      return true;
    }

    @Override
    int slotArity() {
      return 8;
    }

    @Override
    Object getSlot(final int index) {
      final int boundary = payloadArity();

      if (index < boundary) {
        return getKey(index);
      } else {
        return getNode(index - boundary);
      }
    }

    @Override
    CompactSetNode getNode(int index) {
      switch (index) {
        case 0:
          return node1;
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    int getKey(int index) {
      switch (index) {
        case 0:
          return key1;
        case 1:
          return key2;
        case 2:
          return key3;
        case 3:
          return key4;
        case 4:
          return key5;
        case 5:
          return key6;
        case 6:
          return key7;
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    boolean hasNodes() {
      return true;
    }

    @Override
    int nodeArity() {
      return 1;
    }

    @Override
    boolean hasPayload() {
      return true;
    }

    @Override
    int payloadArity() {
      return 7;
    }

    @Override
    byte sizePredicate() {
      return SIZE_MORE_THAN_ONE;
    }

    @Override
    CompactSetNode copyAndInsertValue(AtomicReference<Thread> mutator, final int bitpos,
        final int key) {
      final int valIndex = dataIndex(bitpos);

      final int nodeMap = (int) (this.nodeMap());
      final int dataMap = (int) (this.dataMap() | bitpos);

      switch (valIndex) {
        case 0:
          return nodeOf(mutator, nodeMap, dataMap, key, key1, key2, key3, key4, key5, key6, key7,
              node1);
        case 1:
          return nodeOf(mutator, nodeMap, dataMap, key1, key, key2, key3, key4, key5, key6, key7,
              node1);
        case 2:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2, key, key3, key4, key5, key6, key7,
              node1);
        case 3:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key, key4, key5, key6, key7,
              node1);
        case 4:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key4, key, key5, key6, key7,
              node1);
        case 5:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key4, key5, key, key6, key7,
              node1);
        case 6:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key4, key5, key6, key, key7,
              node1);
        case 7:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key4, key5, key6, key7, key,
              node1);
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    CompactSetNode copyAndRemoveValue(AtomicReference<Thread> mutator, final int bitpos) {
      final int valIndex = dataIndex(bitpos);

      final int nodeMap = (int) (this.nodeMap());
      final int dataMap = (int) (this.dataMap() ^ bitpos);

      switch (valIndex) {
        case 0:
          return nodeOf(mutator, nodeMap, dataMap, key2, key3, key4, key5, key6, key7, node1);
        case 1:
          return nodeOf(mutator, nodeMap, dataMap, key1, key3, key4, key5, key6, key7, node1);
        case 2:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2, key4, key5, key6, key7, node1);
        case 3:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key5, key6, key7, node1);
        case 4:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key4, key6, key7, node1);
        case 5:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key4, key5, key7, node1);
        case 6:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key4, key5, key6, node1);
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    CompactSetNode copyAndSetNode(AtomicReference<Thread> mutator, final int bitpos,
        CompactSetNode node) {
      final int index = nodeIndex(bitpos);

      final int nodeMap = this.nodeMap();
      final int dataMap = this.dataMap();

      switch (index) {
        case 0:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key4, key5, key6, key7, node);
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    CompactSetNode copyAndMigrateFromInlineToNode(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetNode node) {
      final int bitIndex = nodeIndex(bitpos);
      final int valIndex = dataIndex(bitpos);

      final int nodeMap = (int) (this.nodeMap() | bitpos);
      final int dataMap = (int) (this.dataMap() ^ bitpos);

      switch (valIndex) {
        case 0:
          switch (bitIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key2, key3, key4, key5, key6, key7, node,
                  node1);
            case 1:
              return nodeOf(mutator, nodeMap, dataMap, key2, key3, key4, key5, key6, key7, node1,
                  node);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        case 1:
          switch (bitIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key1, key3, key4, key5, key6, key7, node,
                  node1);
            case 1:
              return nodeOf(mutator, nodeMap, dataMap, key1, key3, key4, key5, key6, key7, node1,
                  node);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        case 2:
          switch (bitIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key4, key5, key6, key7, node,
                  node1);
            case 1:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key4, key5, key6, key7, node1,
                  node);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        case 3:
          switch (bitIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key5, key6, key7, node,
                  node1);
            case 1:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key5, key6, key7, node1,
                  node);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        case 4:
          switch (bitIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key4, key6, key7, node,
                  node1);
            case 1:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key4, key6, key7, node1,
                  node);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        case 5:
          switch (bitIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key4, key5, key7, node,
                  node1);
            case 1:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key4, key5, key7, node1,
                  node);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        case 6:
          switch (bitIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key4, key5, key6, node,
                  node1);
            case 1:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key4, key5, key6, node1,
                  node);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    CompactSetNode copyAndMigrateFromNodeToInline(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetNode node) {
      final int bitIndex = nodeIndex(bitpos);
      final int valIndex = dataIndex(bitpos);

      final int nodeMap = (int) (this.nodeMap() ^ bitpos);
      final int dataMap = (int) (this.dataMap() | bitpos);

      final int key = node.getKey(0);

      switch (bitIndex) {
        case 0:
          switch (valIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key, key1, key2, key3, key4, key5, key6,
                  key7);
            case 1:
              return nodeOf(mutator, nodeMap, dataMap, key1, key, key2, key3, key4, key5, key6,
                  key7);
            case 2:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key, key3, key4, key5, key6,
                  key7);
            case 3:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key, key4, key5, key6,
                  key7);
            case 4:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key4, key, key5, key6,
                  key7);
            case 5:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key4, key5, key, key6,
                  key7);
            case 6:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key4, key5, key6, key,
                  key7);
            case 7:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key4, key5, key6, key7,
                  key);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;

      result = prime * result + ((int) nodeMap());
      result = prime * result + ((int) dataMap());

      result = prime * result + (int) key1;
      result = prime * result + (int) key2;
      result = prime * result + (int) key3;
      result = prime * result + (int) key4;
      result = prime * result + (int) key5;
      result = prime * result + (int) key6;
      result = prime * result + (int) key7;

      result = prime * result + node1.hashCode();

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
      Set7To1Node_5Bits_Spec0To8_IntKey that = (Set7To1Node_5Bits_Spec0To8_IntKey) other;

      if (nodeMap() != that.nodeMap()) {
        return false;
      }
      if (dataMap() != that.dataMap()) {
        return false;
      }

      if (!(key1 == that.key1)) {
        return false;
      }
      if (!(key2 == that.key2)) {
        return false;
      }
      if (!(key3 == that.key3)) {
        return false;
      }
      if (!(key4 == that.key4)) {
        return false;
      }
      if (!(key5 == that.key5)) {
        return false;
      }
      if (!(key6 == that.key6)) {
        return false;
      }
      if (!(key7 == that.key7)) {
        return false;
      }
      if (!(node1.equals(that.node1))) {
        return false;
      }

      return true;
    }

  }

  private static final class Set8To0Node_5Bits_Spec0To8_IntKey extends CompactValuesOnlySetNode {

    private final int key1;
    private final int key2;
    private final int key3;
    private final int key4;
    private final int key5;
    private final int key6;
    private final int key7;
    private final int key8;

    Set8To0Node_5Bits_Spec0To8_IntKey(final AtomicReference<Thread> mutator, final int nodeMap,
        final int dataMap, final int key1, final int key2, final int key3, final int key4,
        final int key5, final int key6, final int key7, final int key8) {
      super(mutator, nodeMap, dataMap);
      this.key1 = key1;
      this.key2 = key2;
      this.key3 = key3;
      this.key4 = key4;
      this.key5 = key5;
      this.key6 = key6;
      this.key7 = key7;
      this.key8 = key8;

      assert nodeInvariant();
    }

    @Override
    boolean hasSlots() {
      return true;
    }

    @Override
    int slotArity() {
      return 8;
    }

    @Override
    Object getSlot(final int index) {
      final int boundary = payloadArity();

      if (index < boundary) {
        return getKey(index);
      } else {
        return getNode(index - boundary);
      }
    }

    @Override
    CompactSetNode getNode(int index) {
      throw new IllegalStateException("Index out of range.");
    }

    @Override
    int getKey(int index) {
      switch (index) {
        case 0:
          return key1;
        case 1:
          return key2;
        case 2:
          return key3;
        case 3:
          return key4;
        case 4:
          return key5;
        case 5:
          return key6;
        case 6:
          return key7;
        case 7:
          return key8;
        default:
          throw new IllegalStateException("Index out of range.");
      }
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
    boolean hasPayload() {
      return true;
    }

    @Override
    int payloadArity() {
      return 8;
    }

    @Override
    byte sizePredicate() {
      return SIZE_MORE_THAN_ONE;
    }

    @Override
    CompactSetNode copyAndInsertValue(AtomicReference<Thread> mutator, final int bitpos,
        final int key) {
      final int valIndex = dataIndex(bitpos);

      final int nodeMap = (int) (this.nodeMap());
      final int dataMap = (int) (this.dataMap() | bitpos);

      switch (valIndex) {
        case 0:
          return nodeOf(mutator, nodeMap, dataMap, key, key1, key2, key3, key4, key5, key6, key7,
              key8);
        case 1:
          return nodeOf(mutator, nodeMap, dataMap, key1, key, key2, key3, key4, key5, key6, key7,
              key8);
        case 2:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2, key, key3, key4, key5, key6, key7,
              key8);
        case 3:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key, key4, key5, key6, key7,
              key8);
        case 4:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key4, key, key5, key6, key7,
              key8);
        case 5:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key4, key5, key, key6, key7,
              key8);
        case 6:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key4, key5, key6, key, key7,
              key8);
        case 7:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key4, key5, key6, key7, key,
              key8);
        case 8:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key4, key5, key6, key7, key8,
              key);
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    CompactSetNode copyAndRemoveValue(AtomicReference<Thread> mutator, final int bitpos) {
      final int valIndex = dataIndex(bitpos);

      final int nodeMap = (int) (this.nodeMap());
      final int dataMap = (int) (this.dataMap() ^ bitpos);

      switch (valIndex) {
        case 0:
          return nodeOf(mutator, nodeMap, dataMap, key2, key3, key4, key5, key6, key7, key8);
        case 1:
          return nodeOf(mutator, nodeMap, dataMap, key1, key3, key4, key5, key6, key7, key8);
        case 2:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2, key4, key5, key6, key7, key8);
        case 3:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key5, key6, key7, key8);
        case 4:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key4, key6, key7, key8);
        case 5:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key4, key5, key7, key8);
        case 6:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key4, key5, key6, key8);
        case 7:
          return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key4, key5, key6, key7);
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    CompactSetNode copyAndSetNode(AtomicReference<Thread> mutator, final int bitpos,
        CompactSetNode node) {
      throw new IllegalStateException("Index out of range.");
    }

    @Override
    CompactSetNode copyAndMigrateFromInlineToNode(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetNode node) {
      final int bitIndex = nodeIndex(bitpos);
      final int valIndex = dataIndex(bitpos);

      final int nodeMap = (int) (this.nodeMap() | bitpos);
      final int dataMap = (int) (this.dataMap() ^ bitpos);

      switch (valIndex) {
        case 0:
          switch (bitIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key2, key3, key4, key5, key6, key7, key8,
                  node);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        case 1:
          switch (bitIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key1, key3, key4, key5, key6, key7, key8,
                  node);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        case 2:
          switch (bitIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key4, key5, key6, key7, key8,
                  node);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        case 3:
          switch (bitIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key5, key6, key7, key8,
                  node);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        case 4:
          switch (bitIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key4, key6, key7, key8,
                  node);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        case 5:
          switch (bitIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key4, key5, key7, key8,
                  node);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        case 6:
          switch (bitIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key4, key5, key6, key8,
                  node);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        case 7:
          switch (bitIndex) {
            case 0:
              return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key4, key5, key6, key7,
                  node);
            default:
              throw new IllegalStateException("Index out of range.");
          }
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    CompactSetNode copyAndMigrateFromNodeToInline(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetNode node) {
      throw new IllegalStateException("Index out of range.");
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;

      result = prime * result + ((int) nodeMap());
      result = prime * result + ((int) dataMap());

      result = prime * result + (int) key1;
      result = prime * result + (int) key2;
      result = prime * result + (int) key3;
      result = prime * result + (int) key4;
      result = prime * result + (int) key5;
      result = prime * result + (int) key6;
      result = prime * result + (int) key7;
      result = prime * result + (int) key8;

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
      Set8To0Node_5Bits_Spec0To8_IntKey that = (Set8To0Node_5Bits_Spec0To8_IntKey) other;

      if (nodeMap() != that.nodeMap()) {
        return false;
      }
      if (dataMap() != that.dataMap()) {
        return false;
      }

      if (!(key1 == that.key1)) {
        return false;
      }
      if (!(key2 == that.key2)) {
        return false;
      }
      if (!(key3 == that.key3)) {
        return false;
      }
      if (!(key4 == that.key4)) {
        return false;
      }
      if (!(key5 == that.key5)) {
        return false;
      }
      if (!(key6 == that.key6)) {
        return false;
      }
      if (!(key7 == that.key7)) {
        return false;
      }
      if (!(key8 == that.key8)) {
        return false;
      }

      return true;
    }

  }

}
