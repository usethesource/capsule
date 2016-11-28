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
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import io.usethesource.capsule.api.deprecated.ImmutableSet;
import io.usethesource.capsule.api.deprecated.TransientSet;

@SuppressWarnings("rawtypes")
public class TrieSet_5Bits_Spec0To8<K> implements ImmutableSet<K> {

  @SuppressWarnings("unchecked")
  private static final TrieSet_5Bits_Spec0To8 EMPTY_SET =
      new TrieSet_5Bits_Spec0To8(CompactSetNode.EMPTY_NODE, 0, 0);

  private static final boolean DEBUG = false;

  private final AbstractSetNode<K> rootNode;
  private final int hashCode;
  private final int cachedSize;

  /*
   * TODO: visibility is currently public to allow set-multimap experiments. Must be set back to
   * `protected` when experiments are finished.
   */
  public /* protected */ TrieSet_5Bits_Spec0To8(AbstractSetNode<K> rootNode) {
    this.rootNode = rootNode;
    this.hashCode = hashCode(rootNode);
    this.cachedSize = size(rootNode);
    if (DEBUG) {
      assert checkHashCodeAndSize(hashCode, cachedSize);
    }
  }

  TrieSet_5Bits_Spec0To8(AbstractSetNode<K> rootNode, int hashCode, int cachedSize) {
    this.rootNode = rootNode;
    this.hashCode = hashCode;
    this.cachedSize = cachedSize;
    if (DEBUG) {
      assert checkHashCodeAndSize(hashCode, cachedSize);
    }
  }

  @SuppressWarnings("unchecked")
  public static final <K> ImmutableSet<K> of() {
    return TrieSet_5Bits_Spec0To8.EMPTY_SET;
  }

  @SuppressWarnings("unchecked")
  public static final <K> ImmutableSet<K> of(K... keys) {
    ImmutableSet<K> result = TrieSet_5Bits_Spec0To8.EMPTY_SET;

    for (final K key : keys) {
      result = result.__insert(key);
    }

    return result;
  }

  @SuppressWarnings("unchecked")
  public static final <K> TransientSet<K> transientOf() {
    return TrieSet_5Bits_Spec0To8.EMPTY_SET.asTransient();
  }

  @SuppressWarnings("unchecked")
  public static final <K> TransientSet<K> transientOf(K... keys) {
    final TransientSet<K> result = TrieSet_5Bits_Spec0To8.EMPTY_SET.asTransient();

    for (final K key : keys) {
      result.__insert(key);
    }

    return result;
  }

  private boolean checkHashCodeAndSize(final int targetHash, final int targetSize) {
    int hash = 0;
    int size = 0;

    for (Iterator<K> it = keyIterator(); it.hasNext();) {
      final K key = it.next();

      hash += key.hashCode();
      size += 1;
    }

    return hash == targetHash && size == targetSize;
  }

  private static <K> int hashCode(AbstractSetNode<K> rootNode) {
    int hash = 0;

    for (Iterator<K> it = new SetKeyIterator<>(rootNode); it.hasNext();) {
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

  public static final int transformHashCode(final int hash) {
    return hash;
  }

  public boolean contains(final Object o) {
    try {
      @SuppressWarnings("unchecked")
      final K key = (K) o;
      return rootNode.contains(key, transformHashCode(key.hashCode()), 0);
    } catch (ClassCastException unused) {
      return false;
    }
  }

  public boolean containsEquivalent(final Object o, final Comparator<Object> cmp) {
    try {
      @SuppressWarnings("unchecked")
      final K key = (K) o;
      return rootNode.contains(key, transformHashCode(key.hashCode()), 0, cmp);
    } catch (ClassCastException unused) {
      return false;
    }
  }

  public K get(final Object o) {
    try {
      @SuppressWarnings("unchecked")
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

  public K getEquivalent(final Object o, final Comparator<Object> cmp) {
    try {
      @SuppressWarnings("unchecked")
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

  public ImmutableSet<K> __insert(final K key) {
    final int keyHash = key.hashCode();
    final SetResult<K> details = SetResult.unchanged();

    final CompactSetNode<K> newRootNode =
        rootNode.updated(null, key, transformHashCode(keyHash), 0, details);

    if (details.isModified()) {
      return new TrieSet_5Bits_Spec0To8<K>(newRootNode, hashCode + keyHash, cachedSize + 1);
    }

    return this;
  }

  public ImmutableSet<K> __insertEquivalent(final K key, final Comparator<Object> cmp) {
    final int keyHash = key.hashCode();
    final SetResult<K> details = SetResult.unchanged();

    final CompactSetNode<K> newRootNode =
        rootNode.updated(null, key, transformHashCode(keyHash), 0, details, cmp);

    if (details.isModified()) {
      return new TrieSet_5Bits_Spec0To8<K>(newRootNode, hashCode + keyHash, cachedSize + 1);
    }

    return this;
  }

  public ImmutableSet<K> __insertAll(final Set<? extends K> set) {
    final TransientSet<K> tmpTransient = this.asTransient();
    tmpTransient.__insertAll(set);
    return tmpTransient.freeze();
  }

  public ImmutableSet<K> __insertAllEquivalent(final Set<? extends K> set,
      final Comparator<Object> cmp) {
    final TransientSet<K> tmpTransient = this.asTransient();
    tmpTransient.__insertAllEquivalent(set, cmp);
    return tmpTransient.freeze();
  }

  public ImmutableSet<K> __remove(final K key) {
    final int keyHash = key.hashCode();
    final SetResult<K> details = SetResult.unchanged();

    final CompactSetNode<K> newRootNode =
        rootNode.removed(null, key, transformHashCode(keyHash), 0, details);

    if (details.isModified()) {
      return new TrieSet_5Bits_Spec0To8<K>(newRootNode, hashCode - keyHash, cachedSize - 1);
    }

    return this;
  }

  public ImmutableSet<K> __removeEquivalent(final K key, final Comparator<Object> cmp) {
    final int keyHash = key.hashCode();
    final SetResult<K> details = SetResult.unchanged();

    final CompactSetNode<K> newRootNode =
        rootNode.removed(null, key, transformHashCode(keyHash), 0, details, cmp);

    if (details.isModified()) {
      return new TrieSet_5Bits_Spec0To8<K>(newRootNode, hashCode - keyHash, cachedSize - 1);
    }

    return this;
  }

  public ImmutableSet<K> __removeAll(final Set<? extends K> set) {
    final TransientSet<K> tmpTransient = this.asTransient();
    tmpTransient.__removeAll(set);
    return tmpTransient.freeze();
  }

  public ImmutableSet<K> __removeAllEquivalent(final Set<? extends K> set,
      final Comparator<Object> cmp) {
    final TransientSet<K> tmpTransient = this.asTransient();
    tmpTransient.__removeAllEquivalent(set, cmp);
    return tmpTransient.freeze();
  }

  public ImmutableSet<K> __retainAll(final Set<? extends K> set) {
    final TransientSet<K> tmpTransient = this.asTransient();
    tmpTransient.__retainAll(set);
    return tmpTransient.freeze();
  }

  public ImmutableSet<K> __retainAllEquivalent(final TransientSet<? extends K> transientSet,
      final Comparator<Object> cmp) {
    final TransientSet<K> tmpTransient = this.asTransient();
    tmpTransient.__retainAllEquivalent(transientSet, cmp);
    return tmpTransient.freeze();
  }

  public boolean add(final K key) {
    throw new UnsupportedOperationException();
  }

  public boolean addAll(final Collection<? extends K> c) {
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

  public Iterator<K> iterator() {
    return keyIterator();
  }

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

    if (other instanceof TrieSet_5Bits_Spec0To8) {
      TrieSet_5Bits_Spec0To8<?> that = (TrieSet_5Bits_Spec0To8<?>) other;

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
  public String toString() {
    String body = stream().map(k -> k.toString()).reduce((o1, o2) -> String.join(", ", o1, o2)).orElse("");
    return String.format("{%s}", body);
  }

  @Override
  public boolean isTransientSupported() {
    return true;
  }

  @Override
  public TransientSet<K> asTransient() {
    return new TransientTrieSet_5Bits_Spec0To8<K>(this);
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
    return new TrieSet_5Bits_Spec0To8NodeIterator<>(rootNode);
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

  /*
   * TODO: visibility is currently public to allow set-multimap experiments. Must be set back to
   * `protected` when experiments are finished.
   */
  public /* protected */ static final class SetResult<K> {
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

    private SetResult() {}

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

  protected static interface INode<K, V> {
  }

  /*
   * TODO: visibility is currently public to allow set-multimap experiments. Must be set back to
   * `protected` when experiments are finished.
   */
  public /* protected */ static abstract class AbstractSetNode<K>
      implements INode<K, java.lang.Void>, Iterable<K> {

    static final int TUPLE_LENGTH = 1;

    /*
     * TODO: visibility is currently public to allow set-multimap experiments. Must be set back to
     * `protected` when experiments are finished.
     */
    public /* protected */ abstract boolean contains(final K key, final int keyHash,
        final int shift);

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

    static final boolean isAllowedToEdit(AtomicReference<Thread> x, AtomicReference<Thread> y) {
      return x != null && y != null && (x == y || x.get() == y.get());
    }

    abstract boolean hasNodes();

    abstract int nodeArity();

    abstract AbstractSetNode<K> getNode(final int index);

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

    abstract K getKey(final int index);

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
    public /* protected */ int size() {
      final Iterator<K> it = new SetKeyIterator<>(this);

      int size = 0;
      while (it.hasNext()) {
        size += 1;
        it.next();
      }

      return size;
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

    CompactSetNode<K> removeInplaceValueAndConvertToSpecializedNode(
        final AtomicReference<Thread> mutator, final int bitpos) {
      throw new UnsupportedOperationException();
    }

    static final <K> CompactSetNode<K> mergeTwoKeyValPairs(final K key0, final int keyHash0,
        final K key1, final int keyHash1, final int shift) {
      assert !(key0.equals(key1));

      if (shift >= HASH_CODE_LENGTH) {
        // throw new
        // IllegalStateException("Hash collision not yet fixed.");
        return new HashCollisionSetNode_5Bits_Spec0To8<>(keyHash0, (K[]) new Object[] {key0, key1});
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
        final CompactSetNode<K> node =
            mergeTwoKeyValPairs(key0, keyHash0, key1, keyHash1, shift + BIT_PARTITION_SIZE);
        // values fit on next level

        final int nodeMap = bitpos(mask0);
        return nodeOf(null, nodeMap, (int) 0, node);
      }
    }

    static final CompactSetNode EMPTY_NODE;

    static {

      EMPTY_NODE = new Set0To0Node_5Bits_Spec0To8<>(null, (int) 0, (int) 0);

    };

    static final <K> CompactSetNode<K> nodeOf(final AtomicReference<Thread> mutator,
        final int nodeMap, final int dataMap, final Object[] nodes) {
      return new BitmapIndexedSetNode<>(mutator, nodeMap, dataMap, nodes);
    }

    @SuppressWarnings("unchecked")
    static final <K> CompactSetNode<K> nodeOf(AtomicReference<Thread> mutator) {
      return EMPTY_NODE;
    }

    static final <K> CompactSetNode<K> nodeOf(final AtomicReference<Thread> mutator,
        final int nodeMap, final int dataMap) {
      return EMPTY_NODE;
    }

    static final <K> CompactSetNode<K> nodeOf(final AtomicReference<Thread> mutator,
        final int nodeMap, final int dataMap, final CompactSetNode<K> node1) {
      return new Set0To1Node_5Bits_Spec0To8<>(mutator, nodeMap, dataMap, node1);
    }

    static final <K> CompactSetNode<K> nodeOf(final AtomicReference<Thread> mutator,
        final int nodeMap, final int dataMap, final CompactSetNode<K> node1,
        final CompactSetNode<K> node2) {
      return new Set0To2Node_5Bits_Spec0To8<>(mutator, nodeMap, dataMap, node1, node2);
    }

    static final <K> CompactSetNode<K> nodeOf(final AtomicReference<Thread> mutator,
        final int nodeMap, final int dataMap, final CompactSetNode<K> node1,
        final CompactSetNode<K> node2, final CompactSetNode<K> node3) {
      return new Set0To3Node_5Bits_Spec0To8<>(mutator, nodeMap, dataMap, node1, node2, node3);
    }

    static final <K> CompactSetNode<K> nodeOf(final AtomicReference<Thread> mutator,
        final int nodeMap, final int dataMap, final CompactSetNode<K> node1,
        final CompactSetNode<K> node2, final CompactSetNode<K> node3,
        final CompactSetNode<K> node4) {
      return new Set0To4Node_5Bits_Spec0To8<>(mutator, nodeMap, dataMap, node1, node2, node3,
          node4);
    }

    static final <K> CompactSetNode<K> nodeOf(final AtomicReference<Thread> mutator,
        final int nodeMap, final int dataMap, final CompactSetNode<K> node1,
        final CompactSetNode<K> node2, final CompactSetNode<K> node3, final CompactSetNode<K> node4,
        final CompactSetNode<K> node5) {
      return new Set0To5Node_5Bits_Spec0To8<>(mutator, nodeMap, dataMap, node1, node2, node3, node4,
          node5);
    }

    static final <K> CompactSetNode<K> nodeOf(final AtomicReference<Thread> mutator,
        final int nodeMap, final int dataMap, final CompactSetNode<K> node1,
        final CompactSetNode<K> node2, final CompactSetNode<K> node3, final CompactSetNode<K> node4,
        final CompactSetNode<K> node5, final CompactSetNode<K> node6) {
      return new Set0To6Node_5Bits_Spec0To8<>(mutator, nodeMap, dataMap, node1, node2, node3, node4,
          node5, node6);
    }

    static final <K> CompactSetNode<K> nodeOf(final AtomicReference<Thread> mutator,
        final int nodeMap, final int dataMap, final CompactSetNode<K> node1,
        final CompactSetNode<K> node2, final CompactSetNode<K> node3, final CompactSetNode<K> node4,
        final CompactSetNode<K> node5, final CompactSetNode<K> node6,
        final CompactSetNode<K> node7) {
      return new Set0To7Node_5Bits_Spec0To8<>(mutator, nodeMap, dataMap, node1, node2, node3, node4,
          node5, node6, node7);
    }

    static final <K> CompactSetNode<K> nodeOf(final AtomicReference<Thread> mutator,
        final int nodeMap, final int dataMap, final CompactSetNode<K> node1,
        final CompactSetNode<K> node2, final CompactSetNode<K> node3, final CompactSetNode<K> node4,
        final CompactSetNode<K> node5, final CompactSetNode<K> node6, final CompactSetNode<K> node7,
        final CompactSetNode<K> node8) {
      return new Set0To8Node_5Bits_Spec0To8<>(mutator, nodeMap, dataMap, node1, node2, node3, node4,
          node5, node6, node7, node8);
    }

    static final <K> CompactSetNode<K> nodeOf(final AtomicReference<Thread> mutator,
        final int nodeMap, final int dataMap, final CompactSetNode<K> node1,
        final CompactSetNode<K> node2, final CompactSetNode<K> node3, final CompactSetNode<K> node4,
        final CompactSetNode<K> node5, final CompactSetNode<K> node6, final CompactSetNode<K> node7,
        final CompactSetNode<K> node8, final CompactSetNode<K> node9) {
      // NOTE: reversed node argument list due to CHAMP encoding
      return nodeOf(mutator, nodeMap, dataMap,
          new Object[] {node9, node8, node7, node6, node5, node4, node3, node2, node1});
    }

    static final <K> CompactSetNode<K> nodeOf(final AtomicReference<Thread> mutator,
        final int nodeMap, final int dataMap, final K key1) {
      return new Set1To0Node_5Bits_Spec0To8<>(mutator, nodeMap, dataMap, key1);
    }

    static final <K> CompactSetNode<K> nodeOf(final AtomicReference<Thread> mutator,
        final int nodeMap, final int dataMap, final K key1, final CompactSetNode<K> node1) {
      return new Set1To1Node_5Bits_Spec0To8<>(mutator, nodeMap, dataMap, key1, node1);
    }

    static final <K> CompactSetNode<K> nodeOf(final AtomicReference<Thread> mutator,
        final int nodeMap, final int dataMap, final K key1, final CompactSetNode<K> node1,
        final CompactSetNode<K> node2) {
      return new Set1To2Node_5Bits_Spec0To8<>(mutator, nodeMap, dataMap, key1, node1, node2);
    }

    static final <K> CompactSetNode<K> nodeOf(final AtomicReference<Thread> mutator,
        final int nodeMap, final int dataMap, final K key1, final CompactSetNode<K> node1,
        final CompactSetNode<K> node2, final CompactSetNode<K> node3) {
      return new Set1To3Node_5Bits_Spec0To8<>(mutator, nodeMap, dataMap, key1, node1, node2, node3);
    }

    static final <K> CompactSetNode<K> nodeOf(final AtomicReference<Thread> mutator,
        final int nodeMap, final int dataMap, final K key1, final CompactSetNode<K> node1,
        final CompactSetNode<K> node2, final CompactSetNode<K> node3,
        final CompactSetNode<K> node4) {
      return new Set1To4Node_5Bits_Spec0To8<>(mutator, nodeMap, dataMap, key1, node1, node2, node3,
          node4);
    }

    static final <K> CompactSetNode<K> nodeOf(final AtomicReference<Thread> mutator,
        final int nodeMap, final int dataMap, final K key1, final CompactSetNode<K> node1,
        final CompactSetNode<K> node2, final CompactSetNode<K> node3, final CompactSetNode<K> node4,
        final CompactSetNode<K> node5) {
      return new Set1To5Node_5Bits_Spec0To8<>(mutator, nodeMap, dataMap, key1, node1, node2, node3,
          node4, node5);
    }

    static final <K> CompactSetNode<K> nodeOf(final AtomicReference<Thread> mutator,
        final int nodeMap, final int dataMap, final K key1, final CompactSetNode<K> node1,
        final CompactSetNode<K> node2, final CompactSetNode<K> node3, final CompactSetNode<K> node4,
        final CompactSetNode<K> node5, final CompactSetNode<K> node6) {
      return new Set1To6Node_5Bits_Spec0To8<>(mutator, nodeMap, dataMap, key1, node1, node2, node3,
          node4, node5, node6);
    }

    static final <K> CompactSetNode<K> nodeOf(final AtomicReference<Thread> mutator,
        final int nodeMap, final int dataMap, final K key1, final CompactSetNode<K> node1,
        final CompactSetNode<K> node2, final CompactSetNode<K> node3, final CompactSetNode<K> node4,
        final CompactSetNode<K> node5, final CompactSetNode<K> node6,
        final CompactSetNode<K> node7) {
      return new Set1To7Node_5Bits_Spec0To8<>(mutator, nodeMap, dataMap, key1, node1, node2, node3,
          node4, node5, node6, node7);
    }

    static final <K> CompactSetNode<K> nodeOf(final AtomicReference<Thread> mutator,
        final int nodeMap, final int dataMap, final K key1, final CompactSetNode<K> node1,
        final CompactSetNode<K> node2, final CompactSetNode<K> node3, final CompactSetNode<K> node4,
        final CompactSetNode<K> node5, final CompactSetNode<K> node6, final CompactSetNode<K> node7,
        final CompactSetNode<K> node8) {
      // NOTE: reversed node argument list due to CHAMP encoding
      return nodeOf(mutator, nodeMap, dataMap,
          new Object[] {key1, node8, node7, node6, node5, node4, node3, node2, node1});
    }

    static final <K> CompactSetNode<K> nodeOf(final AtomicReference<Thread> mutator,
        final int nodeMap, final int dataMap, final K key1, final K key2) {
      return new Set2To0Node_5Bits_Spec0To8<>(mutator, nodeMap, dataMap, key1, key2);
    }

    static final <K> CompactSetNode<K> nodeOf(final AtomicReference<Thread> mutator,
        final int nodeMap, final int dataMap, final K key1, final K key2,
        final CompactSetNode<K> node1) {
      return new Set2To1Node_5Bits_Spec0To8<>(mutator, nodeMap, dataMap, key1, key2, node1);
    }

    static final <K> CompactSetNode<K> nodeOf(final AtomicReference<Thread> mutator,
        final int nodeMap, final int dataMap, final K key1, final K key2,
        final CompactSetNode<K> node1, final CompactSetNode<K> node2) {
      return new Set2To2Node_5Bits_Spec0To8<>(mutator, nodeMap, dataMap, key1, key2, node1, node2);
    }

    static final <K> CompactSetNode<K> nodeOf(final AtomicReference<Thread> mutator,
        final int nodeMap, final int dataMap, final K key1, final K key2,
        final CompactSetNode<K> node1, final CompactSetNode<K> node2,
        final CompactSetNode<K> node3) {
      return new Set2To3Node_5Bits_Spec0To8<>(mutator, nodeMap, dataMap, key1, key2, node1, node2,
          node3);
    }

    static final <K> CompactSetNode<K> nodeOf(final AtomicReference<Thread> mutator,
        final int nodeMap, final int dataMap, final K key1, final K key2,
        final CompactSetNode<K> node1, final CompactSetNode<K> node2, final CompactSetNode<K> node3,
        final CompactSetNode<K> node4) {
      return new Set2To4Node_5Bits_Spec0To8<>(mutator, nodeMap, dataMap, key1, key2, node1, node2,
          node3, node4);
    }

    static final <K> CompactSetNode<K> nodeOf(final AtomicReference<Thread> mutator,
        final int nodeMap, final int dataMap, final K key1, final K key2,
        final CompactSetNode<K> node1, final CompactSetNode<K> node2, final CompactSetNode<K> node3,
        final CompactSetNode<K> node4, final CompactSetNode<K> node5) {
      return new Set2To5Node_5Bits_Spec0To8<>(mutator, nodeMap, dataMap, key1, key2, node1, node2,
          node3, node4, node5);
    }

    static final <K> CompactSetNode<K> nodeOf(final AtomicReference<Thread> mutator,
        final int nodeMap, final int dataMap, final K key1, final K key2,
        final CompactSetNode<K> node1, final CompactSetNode<K> node2, final CompactSetNode<K> node3,
        final CompactSetNode<K> node4, final CompactSetNode<K> node5,
        final CompactSetNode<K> node6) {
      return new Set2To6Node_5Bits_Spec0To8<>(mutator, nodeMap, dataMap, key1, key2, node1, node2,
          node3, node4, node5, node6);
    }

    static final <K> CompactSetNode<K> nodeOf(final AtomicReference<Thread> mutator,
        final int nodeMap, final int dataMap, final K key1, final K key2,
        final CompactSetNode<K> node1, final CompactSetNode<K> node2, final CompactSetNode<K> node3,
        final CompactSetNode<K> node4, final CompactSetNode<K> node5, final CompactSetNode<K> node6,
        final CompactSetNode<K> node7) {
      // NOTE: reversed node argument list due to CHAMP encoding
      return nodeOf(mutator, nodeMap, dataMap,
          new Object[] {key1, key2, node7, node6, node5, node4, node3, node2, node1});
    }

    static final <K> CompactSetNode<K> nodeOf(final AtomicReference<Thread> mutator,
        final int nodeMap, final int dataMap, final K key1, final K key2, final K key3) {
      return new Set3To0Node_5Bits_Spec0To8<>(mutator, nodeMap, dataMap, key1, key2, key3);
    }

    static final <K> CompactSetNode<K> nodeOf(final AtomicReference<Thread> mutator,
        final int nodeMap, final int dataMap, final K key1, final K key2, final K key3,
        final CompactSetNode<K> node1) {
      return new Set3To1Node_5Bits_Spec0To8<>(mutator, nodeMap, dataMap, key1, key2, key3, node1);
    }

    static final <K> CompactSetNode<K> nodeOf(final AtomicReference<Thread> mutator,
        final int nodeMap, final int dataMap, final K key1, final K key2, final K key3,
        final CompactSetNode<K> node1, final CompactSetNode<K> node2) {
      return new Set3To2Node_5Bits_Spec0To8<>(mutator, nodeMap, dataMap, key1, key2, key3, node1,
          node2);
    }

    static final <K> CompactSetNode<K> nodeOf(final AtomicReference<Thread> mutator,
        final int nodeMap, final int dataMap, final K key1, final K key2, final K key3,
        final CompactSetNode<K> node1, final CompactSetNode<K> node2,
        final CompactSetNode<K> node3) {
      return new Set3To3Node_5Bits_Spec0To8<>(mutator, nodeMap, dataMap, key1, key2, key3, node1,
          node2, node3);
    }

    static final <K> CompactSetNode<K> nodeOf(final AtomicReference<Thread> mutator,
        final int nodeMap, final int dataMap, final K key1, final K key2, final K key3,
        final CompactSetNode<K> node1, final CompactSetNode<K> node2, final CompactSetNode<K> node3,
        final CompactSetNode<K> node4) {
      return new Set3To4Node_5Bits_Spec0To8<>(mutator, nodeMap, dataMap, key1, key2, key3, node1,
          node2, node3, node4);
    }

    static final <K> CompactSetNode<K> nodeOf(final AtomicReference<Thread> mutator,
        final int nodeMap, final int dataMap, final K key1, final K key2, final K key3,
        final CompactSetNode<K> node1, final CompactSetNode<K> node2, final CompactSetNode<K> node3,
        final CompactSetNode<K> node4, final CompactSetNode<K> node5) {
      return new Set3To5Node_5Bits_Spec0To8<>(mutator, nodeMap, dataMap, key1, key2, key3, node1,
          node2, node3, node4, node5);
    }

    static final <K> CompactSetNode<K> nodeOf(final AtomicReference<Thread> mutator,
        final int nodeMap, final int dataMap, final K key1, final K key2, final K key3,
        final CompactSetNode<K> node1, final CompactSetNode<K> node2, final CompactSetNode<K> node3,
        final CompactSetNode<K> node4, final CompactSetNode<K> node5,
        final CompactSetNode<K> node6) {
      // NOTE: reversed node argument list due to CHAMP encoding
      return nodeOf(mutator, nodeMap, dataMap,
          new Object[] {key1, key2, key3, node6, node5, node4, node3, node2, node1});
    }

    static final <K> CompactSetNode<K> nodeOf(final AtomicReference<Thread> mutator,
        final int nodeMap, final int dataMap, final K key1, final K key2, final K key3,
        final K key4) {
      return new Set4To0Node_5Bits_Spec0To8<>(mutator, nodeMap, dataMap, key1, key2, key3, key4);
    }

    static final <K> CompactSetNode<K> nodeOf(final AtomicReference<Thread> mutator,
        final int nodeMap, final int dataMap, final K key1, final K key2, final K key3,
        final K key4, final CompactSetNode<K> node1) {
      return new Set4To1Node_5Bits_Spec0To8<>(mutator, nodeMap, dataMap, key1, key2, key3, key4,
          node1);
    }

    static final <K> CompactSetNode<K> nodeOf(final AtomicReference<Thread> mutator,
        final int nodeMap, final int dataMap, final K key1, final K key2, final K key3,
        final K key4, final CompactSetNode<K> node1, final CompactSetNode<K> node2) {
      return new Set4To2Node_5Bits_Spec0To8<>(mutator, nodeMap, dataMap, key1, key2, key3, key4,
          node1, node2);
    }

    static final <K> CompactSetNode<K> nodeOf(final AtomicReference<Thread> mutator,
        final int nodeMap, final int dataMap, final K key1, final K key2, final K key3,
        final K key4, final CompactSetNode<K> node1, final CompactSetNode<K> node2,
        final CompactSetNode<K> node3) {
      return new Set4To3Node_5Bits_Spec0To8<>(mutator, nodeMap, dataMap, key1, key2, key3, key4,
          node1, node2, node3);
    }

    static final <K> CompactSetNode<K> nodeOf(final AtomicReference<Thread> mutator,
        final int nodeMap, final int dataMap, final K key1, final K key2, final K key3,
        final K key4, final CompactSetNode<K> node1, final CompactSetNode<K> node2,
        final CompactSetNode<K> node3, final CompactSetNode<K> node4) {
      return new Set4To4Node_5Bits_Spec0To8<>(mutator, nodeMap, dataMap, key1, key2, key3, key4,
          node1, node2, node3, node4);
    }

    static final <K> CompactSetNode<K> nodeOf(final AtomicReference<Thread> mutator,
        final int nodeMap, final int dataMap, final K key1, final K key2, final K key3,
        final K key4, final CompactSetNode<K> node1, final CompactSetNode<K> node2,
        final CompactSetNode<K> node3, final CompactSetNode<K> node4,
        final CompactSetNode<K> node5) {
      // NOTE: reversed node argument list due to CHAMP encoding
      return nodeOf(mutator, nodeMap, dataMap,
          new Object[] {key1, key2, key3, key4, node5, node4, node3, node2, node1});
    }

    static final <K> CompactSetNode<K> nodeOf(final AtomicReference<Thread> mutator,
        final int nodeMap, final int dataMap, final K key1, final K key2, final K key3,
        final K key4, final K key5) {
      return new Set5To0Node_5Bits_Spec0To8<>(mutator, nodeMap, dataMap, key1, key2, key3, key4,
          key5);
    }

    static final <K> CompactSetNode<K> nodeOf(final AtomicReference<Thread> mutator,
        final int nodeMap, final int dataMap, final K key1, final K key2, final K key3,
        final K key4, final K key5, final CompactSetNode<K> node1) {
      return new Set5To1Node_5Bits_Spec0To8<>(mutator, nodeMap, dataMap, key1, key2, key3, key4,
          key5, node1);
    }

    static final <K> CompactSetNode<K> nodeOf(final AtomicReference<Thread> mutator,
        final int nodeMap, final int dataMap, final K key1, final K key2, final K key3,
        final K key4, final K key5, final CompactSetNode<K> node1, final CompactSetNode<K> node2) {
      return new Set5To2Node_5Bits_Spec0To8<>(mutator, nodeMap, dataMap, key1, key2, key3, key4,
          key5, node1, node2);
    }

    static final <K> CompactSetNode<K> nodeOf(final AtomicReference<Thread> mutator,
        final int nodeMap, final int dataMap, final K key1, final K key2, final K key3,
        final K key4, final K key5, final CompactSetNode<K> node1, final CompactSetNode<K> node2,
        final CompactSetNode<K> node3) {
      return new Set5To3Node_5Bits_Spec0To8<>(mutator, nodeMap, dataMap, key1, key2, key3, key4,
          key5, node1, node2, node3);
    }

    static final <K> CompactSetNode<K> nodeOf(final AtomicReference<Thread> mutator,
        final int nodeMap, final int dataMap, final K key1, final K key2, final K key3,
        final K key4, final K key5, final CompactSetNode<K> node1, final CompactSetNode<K> node2,
        final CompactSetNode<K> node3, final CompactSetNode<K> node4) {
      // NOTE: reversed node argument list due to CHAMP encoding
      return nodeOf(mutator, nodeMap, dataMap,
          new Object[] {key1, key2, key3, key4, key5, node4, node3, node2, node1});
    }

    static final <K> CompactSetNode<K> nodeOf(final AtomicReference<Thread> mutator,
        final int nodeMap, final int dataMap, final K key1, final K key2, final K key3,
        final K key4, final K key5, final K key6) {
      return new Set6To0Node_5Bits_Spec0To8<>(mutator, nodeMap, dataMap, key1, key2, key3, key4,
          key5, key6);
    }

    static final <K> CompactSetNode<K> nodeOf(final AtomicReference<Thread> mutator,
        final int nodeMap, final int dataMap, final K key1, final K key2, final K key3,
        final K key4, final K key5, final K key6, final CompactSetNode<K> node1) {
      return new Set6To1Node_5Bits_Spec0To8<>(mutator, nodeMap, dataMap, key1, key2, key3, key4,
          key5, key6, node1);
    }

    static final <K> CompactSetNode<K> nodeOf(final AtomicReference<Thread> mutator,
        final int nodeMap, final int dataMap, final K key1, final K key2, final K key3,
        final K key4, final K key5, final K key6, final CompactSetNode<K> node1,
        final CompactSetNode<K> node2) {
      return new Set6To2Node_5Bits_Spec0To8<>(mutator, nodeMap, dataMap, key1, key2, key3, key4,
          key5, key6, node1, node2);
    }

    static final <K> CompactSetNode<K> nodeOf(final AtomicReference<Thread> mutator,
        final int nodeMap, final int dataMap, final K key1, final K key2, final K key3,
        final K key4, final K key5, final K key6, final CompactSetNode<K> node1,
        final CompactSetNode<K> node2, final CompactSetNode<K> node3) {
      // NOTE: reversed node argument list due to CHAMP encoding
      return nodeOf(mutator, nodeMap, dataMap,
          new Object[] {key1, key2, key3, key4, key5, key6, node3, node2, node1});
    }

    static final <K> CompactSetNode<K> nodeOf(final AtomicReference<Thread> mutator,
        final int nodeMap, final int dataMap, final K key1, final K key2, final K key3,
        final K key4, final K key5, final K key6, final K key7) {
      return new Set7To0Node_5Bits_Spec0To8<>(mutator, nodeMap, dataMap, key1, key2, key3, key4,
          key5, key6, key7);
    }

    static final <K> CompactSetNode<K> nodeOf(final AtomicReference<Thread> mutator,
        final int nodeMap, final int dataMap, final K key1, final K key2, final K key3,
        final K key4, final K key5, final K key6, final K key7, final CompactSetNode<K> node1) {
      return new Set7To1Node_5Bits_Spec0To8<>(mutator, nodeMap, dataMap, key1, key2, key3, key4,
          key5, key6, key7, node1);
    }

    static final <K> CompactSetNode<K> nodeOf(final AtomicReference<Thread> mutator,
        final int nodeMap, final int dataMap, final K key1, final K key2, final K key3,
        final K key4, final K key5, final K key6, final K key7, final CompactSetNode<K> node1,
        final CompactSetNode<K> node2) {
      // NOTE: reversed node argument list due to CHAMP encoding
      return nodeOf(mutator, nodeMap, dataMap,
          new Object[] {key1, key2, key3, key4, key5, key6, key7, node2, node1});
    }

    static final <K> CompactSetNode<K> nodeOf(final AtomicReference<Thread> mutator,
        final int nodeMap, final int dataMap, final K key1, final K key2, final K key3,
        final K key4, final K key5, final K key6, final K key7, final K key8) {
      return new Set8To0Node_5Bits_Spec0To8<>(mutator, nodeMap, dataMap, key1, key2, key3, key4,
          key5, key6, key7, key8);
    }

    static final <K> CompactSetNode<K> nodeOf(final AtomicReference<Thread> mutator,
        final int nodeMap, final int dataMap, final K key1, final K key2, final K key3,
        final K key4, final K key5, final K key6, final K key7, final K key8,
        final CompactSetNode<K> node1) {
      // NOTE: reversed node argument list due to CHAMP encoding
      return nodeOf(mutator, nodeMap, dataMap,
          new Object[] {key1, key2, key3, key4, key5, key6, key7, key8, node1});
    }

    static final <K> CompactSetNode<K> nodeOf(final AtomicReference<Thread> mutator,
        final int nodeMap, final int dataMap, final K key1, final K key2, final K key3,
        final K key4, final K key5, final K key6, final K key7, final K key8, final K key9) {
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

    CompactSetNode<K> nodeAt(final int bitpos) {
      return getNode(nodeIndex(bitpos));
    }

    /*
     * TODO: visibility is currently public to allow set-multimap experiments. Must be set back to
     * `protected` when experiments are finished.
     */
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
              return CompactSetNode.<K>nodeOf(mutator, (int) 0, newDataMap, getKey(1));
            } else {
              return CompactSetNode.<K>nodeOf(mutator, (int) 0, newDataMap, getKey(0));
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
              return CompactSetNode.<K>nodeOf(mutator, (int) 0, newDataMap, getKey(1));
            } else {
              return CompactSetNode.<K>nodeOf(mutator, (int) 0, newDataMap, getKey(0));
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

  protected static abstract class CompactMixedSetNode<K> extends CompactSetNode<K> {

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

  protected static abstract class CompactNodesOnlySetNode<K> extends CompactSetNode<K> {

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

  protected static abstract class CompactValuesOnlySetNode<K> extends CompactSetNode<K> {

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

  protected static abstract class CompactEmptySetNode<K> extends CompactSetNode<K> {

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
      }

      assert arity() > 8;
      assert nodeInvariant();
    }

    @SuppressWarnings("unchecked")
    @Override
    K getKey(final int index) {
      return (K) nodes[TUPLE_LENGTH * index];
    }

    @SuppressWarnings("unchecked")
    @Override
    CompactSetNode<K> getNode(final int index) {
      return (CompactSetNode<K>) nodes[nodes.length - 1 - index];
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
      BitmapIndexedSetNode<?> that = (BitmapIndexedSetNode<?>) other;
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
    CompactSetNode<K> copyAndSetNode(final AtomicReference<Thread> mutator, final int bitpos,
        final CompactSetNode<K> node) {

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
    CompactSetNode<K> copyAndInsertValue(final AtomicReference<Thread> mutator, final int bitpos,
        final K key) {
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
    CompactSetNode<K> copyAndRemoveValue(final AtomicReference<Thread> mutator, final int bitpos) {
      final int idx = TUPLE_LENGTH * dataIndex(bitpos);

      final Object[] src = this.nodes;
      final Object[] dst = (Object[]) new Object[src.length - 1];

      // copy 'src' and remove 1 element(s) at position 'idx'
      System.arraycopy(src, 0, dst, 0, idx);
      System.arraycopy(src, idx + 1, dst, idx, src.length - idx - 1);

      return nodeOf(mutator, nodeMap(), (int) (dataMap() ^ bitpos), dst);
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

      return nodeOf(mutator, (int) (nodeMap() | bitpos), (int) (dataMap() ^ bitpos), dst);
    }

    @Override
    CompactSetNode<K> copyAndMigrateFromNodeToInline(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetNode<K> node) {

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
    CompactSetNode<K> removeInplaceValueAndConvertToSpecializedNode(
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

          final CompactSetNode<K> node1 = getNode(0);
          final CompactSetNode<K> node2 = getNode(1);
          final CompactSetNode<K> node3 = getNode(2);
          final CompactSetNode<K> node4 = getNode(3);
          final CompactSetNode<K> node5 = getNode(4);
          final CompactSetNode<K> node6 = getNode(5);
          final CompactSetNode<K> node7 = getNode(6);
          final CompactSetNode<K> node8 = getNode(7);

          return nodeOf(mutator, nodeMap, dataMap, node1, node2, node3, node4, node5, node6, node7,
              node8);

        }
        case 2: {
          K key1;

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

          final CompactSetNode<K> node1 = getNode(0);
          final CompactSetNode<K> node2 = getNode(1);
          final CompactSetNode<K> node3 = getNode(2);
          final CompactSetNode<K> node4 = getNode(3);
          final CompactSetNode<K> node5 = getNode(4);
          final CompactSetNode<K> node6 = getNode(5);
          final CompactSetNode<K> node7 = getNode(6);

          return nodeOf(mutator, nodeMap, dataMap, key1, node1, node2, node3, node4, node5, node6,
              node7);

        }
        case 3: {
          K key1;
          K key2;

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

          final CompactSetNode<K> node1 = getNode(0);
          final CompactSetNode<K> node2 = getNode(1);
          final CompactSetNode<K> node3 = getNode(2);
          final CompactSetNode<K> node4 = getNode(3);
          final CompactSetNode<K> node5 = getNode(4);
          final CompactSetNode<K> node6 = getNode(5);

          return nodeOf(mutator, nodeMap, dataMap, key1, key2, node1, node2, node3, node4, node5,
              node6);

        }
        case 4: {
          K key1;
          K key2;
          K key3;

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

          final CompactSetNode<K> node1 = getNode(0);
          final CompactSetNode<K> node2 = getNode(1);
          final CompactSetNode<K> node3 = getNode(2);
          final CompactSetNode<K> node4 = getNode(3);
          final CompactSetNode<K> node5 = getNode(4);

          return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, node1, node2, node3, node4,
              node5);

        }
        case 5: {
          K key1;
          K key2;
          K key3;
          K key4;

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

          final CompactSetNode<K> node1 = getNode(0);
          final CompactSetNode<K> node2 = getNode(1);
          final CompactSetNode<K> node3 = getNode(2);
          final CompactSetNode<K> node4 = getNode(3);

          return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key4, node1, node2, node3,
              node4);

        }
        case 6: {
          K key1;
          K key2;
          K key3;
          K key4;
          K key5;

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

          final CompactSetNode<K> node1 = getNode(0);
          final CompactSetNode<K> node2 = getNode(1);
          final CompactSetNode<K> node3 = getNode(2);

          return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key4, key5, node1, node2,
              node3);

        }
        case 7: {
          K key1;
          K key2;
          K key3;
          K key4;
          K key5;
          K key6;

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

          final CompactSetNode<K> node1 = getNode(0);
          final CompactSetNode<K> node2 = getNode(1);

          return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key4, key5, key6, node1,
              node2);

        }
        case 8: {
          K key1;
          K key2;
          K key3;
          K key4;
          K key5;
          K key6;
          K key7;

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

          final CompactSetNode<K> node1 = getNode(0);

          return nodeOf(mutator, nodeMap, dataMap, key1, key2, key3, key4, key5, key6, key7, node1);

        }
        case 9: {
          K key1;
          K key2;
          K key3;
          K key4;
          K key5;
          K key6;
          K key7;
          K key8;

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

  private static final class HashCollisionSetNode_5Bits_Spec0To8<K> extends CompactSetNode<K> {
    private final K[] keys;

    private final int hash;

    HashCollisionSetNode_5Bits_Spec0To8(final int hash, final K[] keys) {
      this.keys = keys;

      this.hash = hash;

      assert payloadArity() >= 2;
    }

    /*
     * TODO: visibility is currently public to allow set-multimap experiments. Must be set back to
     * `protected` when experiments are finished.
     */
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

    Optional<K> findByKey(final K key, final int keyHash, final int shift) {
      for (int i = 0; i < keys.length; i++) {
        final K _key = keys[i];
        if (key.equals(_key)) {
          return Optional.of(_key);
        }
      }
      return Optional.empty();
    }

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
    public /* protected */ CompactSetNode<K> updated(final AtomicReference<Thread> mutator,
        final K key, final int keyHash, final int shift, final SetResult<K> details) {
      assert this.hash == keyHash;

      for (int idx = 0; idx < keys.length; idx++) {
        if (keys[idx].equals(key)) {
          return this;
        }
      }

      @SuppressWarnings("unchecked")
      final K[] keysNew = (K[]) new Object[this.keys.length + 1];

      // copy 'this.keys' and insert 1 element(s) at position
      // 'keys.length'
      System.arraycopy(this.keys, 0, keysNew, 0, keys.length);
      keysNew[keys.length + 0] = key;
      System.arraycopy(this.keys, keys.length, keysNew, keys.length + 1,
          this.keys.length - keys.length);

      details.modified();
      return new HashCollisionSetNode_5Bits_Spec0To8<>(keyHash, keysNew);
    }

    CompactSetNode<K> updated(final AtomicReference<Thread> mutator, final K key, final int keyHash,
        final int shift, final SetResult<K> details, final Comparator<Object> cmp) {
      assert this.hash == keyHash;

      for (int idx = 0; idx < keys.length; idx++) {
        if (cmp.compare(keys[idx], key) == 0) {
          return this;
        }
      }

      @SuppressWarnings("unchecked")
      final K[] keysNew = (K[]) new Object[this.keys.length + 1];

      // copy 'this.keys' and insert 1 element(s) at position
      // 'keys.length'
      System.arraycopy(this.keys, 0, keysNew, 0, keys.length);
      keysNew[keys.length + 0] = key;
      System.arraycopy(this.keys, keys.length, keysNew, keys.length + 1,
          this.keys.length - keys.length);

      details.modified();
      return new HashCollisionSetNode_5Bits_Spec0To8<>(keyHash, keysNew);
    }

    /*
     * TODO: visibility is currently public to allow set-multimap experiments. Must be set back to
     * `protected` when experiments are finished.
     */
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
            @SuppressWarnings("unchecked")
            final K[] keysNew = (K[]) new Object[this.keys.length - 1];

            // copy 'this.keys' and remove 1 element(s) at position
            // 'idx'
            System.arraycopy(this.keys, 0, keysNew, 0, idx);
            System.arraycopy(this.keys, idx + 1, keysNew, idx, this.keys.length - idx - 1);

            return new HashCollisionSetNode_5Bits_Spec0To8<>(keyHash, keysNew);
          }
        }
      }
      return this;
    }

    CompactSetNode<K> removed(final AtomicReference<Thread> mutator, final K key, final int keyHash,
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
            @SuppressWarnings("unchecked")
            final K[] keysNew = (K[]) new Object[this.keys.length - 1];

            // copy 'this.keys' and remove 1 element(s) at position
            // 'idx'
            System.arraycopy(this.keys, 0, keysNew, 0, idx);
            System.arraycopy(this.keys, idx + 1, keysNew, idx, this.keys.length - idx - 1);

            return new HashCollisionSetNode_5Bits_Spec0To8<>(keyHash, keysNew);
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
    K getKey(final int index) {
      return keys[index];
    }

    @Override
    public CompactSetNode<K> getNode(int index) {
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

      HashCollisionSetNode_5Bits_Spec0To8<?> that = (HashCollisionSetNode_5Bits_Spec0To8<?>) other;

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
    CompactSetNode<K> copyAndRemoveValue(final AtomicReference<Thread> mutator, final int bitpos) {
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
    CompactSetNode<K> removeInplaceValueAndConvertToSpecializedNode(
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
  private static abstract class AbstractSetIterator<K> {

    private static final int MAX_DEPTH = 7;

    protected int currentValueCursor;
    protected int currentValueLength;
    protected AbstractSetNode<K> currentValueNode;

    private int currentStackLevel = -1;
    private final int[] nodeCursorsAndLengths = new int[MAX_DEPTH * 2];

    @SuppressWarnings("unchecked")
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
  private static class TrieSet_5Bits_Spec0To8NodeIterator<K>
      implements Iterator<AbstractSetNode<K>> {

    final Deque<Iterator<? extends AbstractSetNode<K>>> nodeIteratorStack;

    TrieSet_5Bits_Spec0To8NodeIterator(AbstractSetNode<K> rootNode) {
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

  static final class TransientTrieSet_5Bits_Spec0To8<K> implements TransientSet<K> {
    final private AtomicReference<Thread> mutator;
    private AbstractSetNode<K> rootNode;
    private int hashCode;
    private int cachedSize;

    TransientTrieSet_5Bits_Spec0To8(TrieSet_5Bits_Spec0To8<K> trieSet_5Bits_Spec0To8) {
      this.mutator = new AtomicReference<Thread>(Thread.currentThread());
      this.rootNode = trieSet_5Bits_Spec0To8.rootNode;
      this.hashCode = trieSet_5Bits_Spec0To8.hashCode;
      this.cachedSize = trieSet_5Bits_Spec0To8.cachedSize;
      if (DEBUG) {
        assert checkHashCodeAndSize(hashCode, cachedSize);
      }
    }

    private boolean checkHashCodeAndSize(final int targetHash, final int targetSize) {
      int hash = 0;
      int size = 0;

      for (Iterator<K> it = keyIterator(); it.hasNext();) {
        final K key = it.next();

        hash += key.hashCode();
        size += 1;
      }

      return hash == targetHash && size == targetSize;
    }

    public boolean add(final K key) {
      throw new UnsupportedOperationException();
    }

    public boolean addAll(final Collection<? extends K> c) {
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
        final K key = (K) o;
        return rootNode.contains(key, transformHashCode(key.hashCode()), 0);
      } catch (ClassCastException unused) {
        return false;
      }
    }

    public boolean containsEquivalent(final Object o, final Comparator<Object> cmp) {
      try {
        @SuppressWarnings("unchecked")
        final K key = (K) o;
        return rootNode.contains(key, transformHashCode(key.hashCode()), 0, cmp);
      } catch (ClassCastException unused) {
        return false;
      }
    }

    public K get(final Object o) {
      try {
        @SuppressWarnings("unchecked")
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

    public K getEquivalent(final Object o, final Comparator<Object> cmp) {
      try {
        @SuppressWarnings("unchecked")
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

    public boolean __insert(final K key) {
      if (mutator.get() == null) {
        throw new IllegalStateException("Transient already frozen.");
      }

      final int keyHash = key.hashCode();
      final SetResult<K> details = SetResult.unchanged();

      final CompactSetNode<K> newRootNode =
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

    public boolean __insertEquivalent(final K key, final Comparator<Object> cmp) {
      if (mutator.get() == null) {
        throw new IllegalStateException("Transient already frozen.");
      }

      final int keyHash = key.hashCode();
      final SetResult<K> details = SetResult.unchanged();

      final CompactSetNode<K> newRootNode =
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

    public boolean __insertAll(final Set<? extends K> set) {
      boolean modified = false;

      for (final K key : set) {
        modified |= this.__insert(key);
      }

      return modified;
    }

    public boolean __insertAllEquivalent(final Set<? extends K> set, final Comparator<Object> cmp) {
      boolean modified = false;

      for (final K key : set) {
        modified |= this.__insertEquivalent(key, cmp);
      }

      return modified;
    }

    public boolean __remove(final K key) {
      if (mutator.get() == null) {
        throw new IllegalStateException("Transient already frozen.");
      }

      final int keyHash = key.hashCode();
      final SetResult<K> details = SetResult.unchanged();

      final CompactSetNode<K> newRootNode =
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

    public boolean __removeEquivalent(final K key, final Comparator<Object> cmp) {
      if (mutator.get() == null) {
        throw new IllegalStateException("Transient already frozen.");
      }

      final int keyHash = key.hashCode();
      final SetResult<K> details = SetResult.unchanged();

      final CompactSetNode<K> newRootNode =
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

    public boolean __removeAll(final Set<? extends K> set) {
      boolean modified = false;

      for (final K key : set) {
        modified |= this.__remove(key);
      }

      return modified;
    }

    public boolean __removeAllEquivalent(final Set<? extends K> set, final Comparator<Object> cmp) {
      boolean modified = false;

      for (final K key : set) {
        modified |= this.__removeEquivalent(key, cmp);
      }

      return modified;
    }

    public boolean __retainAll(final Set<? extends K> set) {
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

    public boolean __retainAllEquivalent(final TransientSet<? extends K> transientSet,
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

    public int size() {
      return cachedSize;
    }

    public boolean isEmpty() {
      return cachedSize == 0;
    }

    public Iterator<K> iterator() {
      return keyIterator();
    }

    public Iterator<K> keyIterator() {
      return new TransientSetKeyIterator<>(this);
    }

    public static class TransientSetKeyIterator<K> extends SetKeyIterator<K> {
      final TransientTrieSet_5Bits_Spec0To8<K> collection;
      K lastKey;

      public TransientSetKeyIterator(final TransientTrieSet_5Bits_Spec0To8<K> collection) {
        super(collection.rootNode);
        this.collection = collection;
      }

      public K next() {
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

      if (other instanceof TransientTrieSet_5Bits_Spec0To8) {
        TransientTrieSet_5Bits_Spec0To8<?> that = (TransientTrieSet_5Bits_Spec0To8<?>) other;

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
    public ImmutableSet<K> freeze() {
      if (mutator.get() == null) {
        throw new IllegalStateException("Transient already frozen.");
      }

      mutator.set(null);
      return new TrieSet_5Bits_Spec0To8<K>(rootNode, hashCode, cachedSize);
    }
  }

  private static final class Set0To0Node_5Bits_Spec0To8<K> extends CompactEmptySetNode<K> {

    Set0To0Node_5Bits_Spec0To8(final AtomicReference<Thread> mutator, final int nodeMap,
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
    CompactSetNode<K> getNode(int index) {
      throw new IllegalStateException("Index out of range.");
    }

    @Override
    K getKey(int index) {
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
    CompactSetNode<K> copyAndInsertValue(AtomicReference<Thread> mutator, final int bitpos,
        final K key) {
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
    CompactSetNode<K> copyAndRemoveValue(AtomicReference<Thread> mutator, final int bitpos) {
      throw new IllegalStateException("Index out of range.");
    }

    @Override
    CompactSetNode<K> copyAndSetNode(AtomicReference<Thread> mutator, final int bitpos,
        CompactSetNode<K> node) {
      throw new IllegalStateException("Index out of range.");
    }

    @Override
    CompactSetNode<K> copyAndMigrateFromInlineToNode(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetNode<K> node) {
      throw new IllegalStateException("Index out of range.");
    }

    @Override
    CompactSetNode<K> copyAndMigrateFromNodeToInline(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetNode<K> node) {
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

  private static final class Set0To1Node_5Bits_Spec0To8<K> extends CompactNodesOnlySetNode<K> {

    private final CompactSetNode<K> node1;

    Set0To1Node_5Bits_Spec0To8(final AtomicReference<Thread> mutator, final int nodeMap,
        final int dataMap, final CompactSetNode<K> node1) {
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
    CompactSetNode<K> getNode(int index) {
      switch (index) {
        case 0:
          return node1;
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    K getKey(int index) {
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
    CompactSetNode<K> copyAndInsertValue(AtomicReference<Thread> mutator, final int bitpos,
        final K key) {
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
    CompactSetNode<K> copyAndRemoveValue(AtomicReference<Thread> mutator, final int bitpos) {
      throw new IllegalStateException("Index out of range.");
    }

    @Override
    CompactSetNode<K> copyAndSetNode(AtomicReference<Thread> mutator, final int bitpos,
        CompactSetNode<K> node) {
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
    CompactSetNode<K> copyAndMigrateFromInlineToNode(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetNode<K> node) {
      throw new IllegalStateException("Index out of range.");
    }

    @Override
    CompactSetNode<K> copyAndMigrateFromNodeToInline(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetNode<K> node) {
      final int bitIndex = nodeIndex(bitpos);
      final int valIndex = dataIndex(bitpos);

      final int nodeMap = (int) (this.nodeMap() ^ bitpos);
      final int dataMap = (int) (this.dataMap() | bitpos);

      final K key = node.getKey(0);

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
      Set0To1Node_5Bits_Spec0To8<?> that = (Set0To1Node_5Bits_Spec0To8<?>) other;

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

  private static final class Set0To2Node_5Bits_Spec0To8<K> extends CompactNodesOnlySetNode<K> {

    private final CompactSetNode<K> node1;
    private final CompactSetNode<K> node2;

    Set0To2Node_5Bits_Spec0To8(final AtomicReference<Thread> mutator, final int nodeMap,
        final int dataMap, final CompactSetNode<K> node1, final CompactSetNode<K> node2) {
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
    CompactSetNode<K> getNode(int index) {
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
    K getKey(int index) {
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
    CompactSetNode<K> copyAndInsertValue(AtomicReference<Thread> mutator, final int bitpos,
        final K key) {
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
    CompactSetNode<K> copyAndRemoveValue(AtomicReference<Thread> mutator, final int bitpos) {
      throw new IllegalStateException("Index out of range.");
    }

    @Override
    CompactSetNode<K> copyAndSetNode(AtomicReference<Thread> mutator, final int bitpos,
        CompactSetNode<K> node) {
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
    CompactSetNode<K> copyAndMigrateFromInlineToNode(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetNode<K> node) {
      throw new IllegalStateException("Index out of range.");
    }

    @Override
    CompactSetNode<K> copyAndMigrateFromNodeToInline(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetNode<K> node) {
      final int bitIndex = nodeIndex(bitpos);
      final int valIndex = dataIndex(bitpos);

      final int nodeMap = (int) (this.nodeMap() ^ bitpos);
      final int dataMap = (int) (this.dataMap() | bitpos);

      final K key = node.getKey(0);

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
      Set0To2Node_5Bits_Spec0To8<?> that = (Set0To2Node_5Bits_Spec0To8<?>) other;

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

  private static final class Set0To3Node_5Bits_Spec0To8<K> extends CompactNodesOnlySetNode<K> {

    private final CompactSetNode<K> node1;
    private final CompactSetNode<K> node2;
    private final CompactSetNode<K> node3;

    Set0To3Node_5Bits_Spec0To8(final AtomicReference<Thread> mutator, final int nodeMap,
        final int dataMap, final CompactSetNode<K> node1, final CompactSetNode<K> node2,
        final CompactSetNode<K> node3) {
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
    CompactSetNode<K> getNode(int index) {
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
    K getKey(int index) {
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
    CompactSetNode<K> copyAndInsertValue(AtomicReference<Thread> mutator, final int bitpos,
        final K key) {
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
    CompactSetNode<K> copyAndRemoveValue(AtomicReference<Thread> mutator, final int bitpos) {
      throw new IllegalStateException("Index out of range.");
    }

    @Override
    CompactSetNode<K> copyAndSetNode(AtomicReference<Thread> mutator, final int bitpos,
        CompactSetNode<K> node) {
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
    CompactSetNode<K> copyAndMigrateFromInlineToNode(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetNode<K> node) {
      throw new IllegalStateException("Index out of range.");
    }

    @Override
    CompactSetNode<K> copyAndMigrateFromNodeToInline(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetNode<K> node) {
      final int bitIndex = nodeIndex(bitpos);
      final int valIndex = dataIndex(bitpos);

      final int nodeMap = (int) (this.nodeMap() ^ bitpos);
      final int dataMap = (int) (this.dataMap() | bitpos);

      final K key = node.getKey(0);

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
      Set0To3Node_5Bits_Spec0To8<?> that = (Set0To3Node_5Bits_Spec0To8<?>) other;

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

  private static final class Set0To4Node_5Bits_Spec0To8<K> extends CompactNodesOnlySetNode<K> {

    private final CompactSetNode<K> node1;
    private final CompactSetNode<K> node2;
    private final CompactSetNode<K> node3;
    private final CompactSetNode<K> node4;

    Set0To4Node_5Bits_Spec0To8(final AtomicReference<Thread> mutator, final int nodeMap,
        final int dataMap, final CompactSetNode<K> node1, final CompactSetNode<K> node2,
        final CompactSetNode<K> node3, final CompactSetNode<K> node4) {
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
    CompactSetNode<K> getNode(int index) {
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
    K getKey(int index) {
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
    CompactSetNode<K> copyAndInsertValue(AtomicReference<Thread> mutator, final int bitpos,
        final K key) {
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
    CompactSetNode<K> copyAndRemoveValue(AtomicReference<Thread> mutator, final int bitpos) {
      throw new IllegalStateException("Index out of range.");
    }

    @Override
    CompactSetNode<K> copyAndSetNode(AtomicReference<Thread> mutator, final int bitpos,
        CompactSetNode<K> node) {
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
    CompactSetNode<K> copyAndMigrateFromInlineToNode(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetNode<K> node) {
      throw new IllegalStateException("Index out of range.");
    }

    @Override
    CompactSetNode<K> copyAndMigrateFromNodeToInline(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetNode<K> node) {
      final int bitIndex = nodeIndex(bitpos);
      final int valIndex = dataIndex(bitpos);

      final int nodeMap = (int) (this.nodeMap() ^ bitpos);
      final int dataMap = (int) (this.dataMap() | bitpos);

      final K key = node.getKey(0);

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
      Set0To4Node_5Bits_Spec0To8<?> that = (Set0To4Node_5Bits_Spec0To8<?>) other;

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

  private static final class Set0To5Node_5Bits_Spec0To8<K> extends CompactNodesOnlySetNode<K> {

    private final CompactSetNode<K> node1;
    private final CompactSetNode<K> node2;
    private final CompactSetNode<K> node3;
    private final CompactSetNode<K> node4;
    private final CompactSetNode<K> node5;

    Set0To5Node_5Bits_Spec0To8(final AtomicReference<Thread> mutator, final int nodeMap,
        final int dataMap, final CompactSetNode<K> node1, final CompactSetNode<K> node2,
        final CompactSetNode<K> node3, final CompactSetNode<K> node4,
        final CompactSetNode<K> node5) {
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
    CompactSetNode<K> getNode(int index) {
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
    K getKey(int index) {
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
    CompactSetNode<K> copyAndInsertValue(AtomicReference<Thread> mutator, final int bitpos,
        final K key) {
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
    CompactSetNode<K> copyAndRemoveValue(AtomicReference<Thread> mutator, final int bitpos) {
      throw new IllegalStateException("Index out of range.");
    }

    @Override
    CompactSetNode<K> copyAndSetNode(AtomicReference<Thread> mutator, final int bitpos,
        CompactSetNode<K> node) {
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
    CompactSetNode<K> copyAndMigrateFromInlineToNode(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetNode<K> node) {
      throw new IllegalStateException("Index out of range.");
    }

    @Override
    CompactSetNode<K> copyAndMigrateFromNodeToInline(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetNode<K> node) {
      final int bitIndex = nodeIndex(bitpos);
      final int valIndex = dataIndex(bitpos);

      final int nodeMap = (int) (this.nodeMap() ^ bitpos);
      final int dataMap = (int) (this.dataMap() | bitpos);

      final K key = node.getKey(0);

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
      Set0To5Node_5Bits_Spec0To8<?> that = (Set0To5Node_5Bits_Spec0To8<?>) other;

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

  private static final class Set0To6Node_5Bits_Spec0To8<K> extends CompactNodesOnlySetNode<K> {

    private final CompactSetNode<K> node1;
    private final CompactSetNode<K> node2;
    private final CompactSetNode<K> node3;
    private final CompactSetNode<K> node4;
    private final CompactSetNode<K> node5;
    private final CompactSetNode<K> node6;

    Set0To6Node_5Bits_Spec0To8(final AtomicReference<Thread> mutator, final int nodeMap,
        final int dataMap, final CompactSetNode<K> node1, final CompactSetNode<K> node2,
        final CompactSetNode<K> node3, final CompactSetNode<K> node4, final CompactSetNode<K> node5,
        final CompactSetNode<K> node6) {
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
    CompactSetNode<K> getNode(int index) {
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
    K getKey(int index) {
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
    CompactSetNode<K> copyAndInsertValue(AtomicReference<Thread> mutator, final int bitpos,
        final K key) {
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
    CompactSetNode<K> copyAndRemoveValue(AtomicReference<Thread> mutator, final int bitpos) {
      throw new IllegalStateException("Index out of range.");
    }

    @Override
    CompactSetNode<K> copyAndSetNode(AtomicReference<Thread> mutator, final int bitpos,
        CompactSetNode<K> node) {
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
    CompactSetNode<K> copyAndMigrateFromInlineToNode(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetNode<K> node) {
      throw new IllegalStateException("Index out of range.");
    }

    @Override
    CompactSetNode<K> copyAndMigrateFromNodeToInline(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetNode<K> node) {
      final int bitIndex = nodeIndex(bitpos);
      final int valIndex = dataIndex(bitpos);

      final int nodeMap = (int) (this.nodeMap() ^ bitpos);
      final int dataMap = (int) (this.dataMap() | bitpos);

      final K key = node.getKey(0);

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
      Set0To6Node_5Bits_Spec0To8<?> that = (Set0To6Node_5Bits_Spec0To8<?>) other;

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

  private static final class Set0To7Node_5Bits_Spec0To8<K> extends CompactNodesOnlySetNode<K> {

    private final CompactSetNode<K> node1;
    private final CompactSetNode<K> node2;
    private final CompactSetNode<K> node3;
    private final CompactSetNode<K> node4;
    private final CompactSetNode<K> node5;
    private final CompactSetNode<K> node6;
    private final CompactSetNode<K> node7;

    Set0To7Node_5Bits_Spec0To8(final AtomicReference<Thread> mutator, final int nodeMap,
        final int dataMap, final CompactSetNode<K> node1, final CompactSetNode<K> node2,
        final CompactSetNode<K> node3, final CompactSetNode<K> node4, final CompactSetNode<K> node5,
        final CompactSetNode<K> node6, final CompactSetNode<K> node7) {
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
    CompactSetNode<K> getNode(int index) {
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
    K getKey(int index) {
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
    CompactSetNode<K> copyAndInsertValue(AtomicReference<Thread> mutator, final int bitpos,
        final K key) {
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
    CompactSetNode<K> copyAndRemoveValue(AtomicReference<Thread> mutator, final int bitpos) {
      throw new IllegalStateException("Index out of range.");
    }

    @Override
    CompactSetNode<K> copyAndSetNode(AtomicReference<Thread> mutator, final int bitpos,
        CompactSetNode<K> node) {
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
    CompactSetNode<K> copyAndMigrateFromInlineToNode(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetNode<K> node) {
      throw new IllegalStateException("Index out of range.");
    }

    @Override
    CompactSetNode<K> copyAndMigrateFromNodeToInline(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetNode<K> node) {
      final int bitIndex = nodeIndex(bitpos);
      final int valIndex = dataIndex(bitpos);

      final int nodeMap = (int) (this.nodeMap() ^ bitpos);
      final int dataMap = (int) (this.dataMap() | bitpos);

      final K key = node.getKey(0);

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
      Set0To7Node_5Bits_Spec0To8<?> that = (Set0To7Node_5Bits_Spec0To8<?>) other;

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

  private static final class Set0To8Node_5Bits_Spec0To8<K> extends CompactNodesOnlySetNode<K> {

    private final CompactSetNode<K> node1;
    private final CompactSetNode<K> node2;
    private final CompactSetNode<K> node3;
    private final CompactSetNode<K> node4;
    private final CompactSetNode<K> node5;
    private final CompactSetNode<K> node6;
    private final CompactSetNode<K> node7;
    private final CompactSetNode<K> node8;

    Set0To8Node_5Bits_Spec0To8(final AtomicReference<Thread> mutator, final int nodeMap,
        final int dataMap, final CompactSetNode<K> node1, final CompactSetNode<K> node2,
        final CompactSetNode<K> node3, final CompactSetNode<K> node4, final CompactSetNode<K> node5,
        final CompactSetNode<K> node6, final CompactSetNode<K> node7,
        final CompactSetNode<K> node8) {
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
    CompactSetNode<K> getNode(int index) {
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
    K getKey(int index) {
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
    CompactSetNode<K> copyAndInsertValue(AtomicReference<Thread> mutator, final int bitpos,
        final K key) {
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
    CompactSetNode<K> copyAndRemoveValue(AtomicReference<Thread> mutator, final int bitpos) {
      throw new IllegalStateException("Index out of range.");
    }

    @Override
    CompactSetNode<K> copyAndSetNode(AtomicReference<Thread> mutator, final int bitpos,
        CompactSetNode<K> node) {
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
    CompactSetNode<K> copyAndMigrateFromInlineToNode(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetNode<K> node) {
      throw new IllegalStateException("Index out of range.");
    }

    @Override
    CompactSetNode<K> copyAndMigrateFromNodeToInline(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetNode<K> node) {
      final int bitIndex = nodeIndex(bitpos);
      final int valIndex = dataIndex(bitpos);

      final int nodeMap = (int) (this.nodeMap() ^ bitpos);
      final int dataMap = (int) (this.dataMap() | bitpos);

      final K key = node.getKey(0);

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
      Set0To8Node_5Bits_Spec0To8<?> that = (Set0To8Node_5Bits_Spec0To8<?>) other;

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

  private static final class Set1To0Node_5Bits_Spec0To8<K> extends CompactValuesOnlySetNode<K> {

    private final K key1;

    Set1To0Node_5Bits_Spec0To8(final AtomicReference<Thread> mutator, final int nodeMap,
        final int dataMap, final K key1) {
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
    CompactSetNode<K> getNode(int index) {
      throw new IllegalStateException("Index out of range.");
    }

    @Override
    K getKey(int index) {
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
    CompactSetNode<K> copyAndInsertValue(AtomicReference<Thread> mutator, final int bitpos,
        final K key) {
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
    CompactSetNode<K> copyAndRemoveValue(AtomicReference<Thread> mutator, final int bitpos) {
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
    CompactSetNode<K> copyAndSetNode(AtomicReference<Thread> mutator, final int bitpos,
        CompactSetNode<K> node) {
      throw new IllegalStateException("Index out of range.");
    }

    @Override
    CompactSetNode<K> copyAndMigrateFromInlineToNode(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetNode<K> node) {
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
    CompactSetNode<K> copyAndMigrateFromNodeToInline(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetNode<K> node) {
      throw new IllegalStateException("Index out of range.");
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;

      result = prime * result + ((int) nodeMap());
      result = prime * result + ((int) dataMap());

      result = prime * result + key1.hashCode();

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
      Set1To0Node_5Bits_Spec0To8<?> that = (Set1To0Node_5Bits_Spec0To8<?>) other;

      if (nodeMap() != that.nodeMap()) {
        return false;
      }
      if (dataMap() != that.dataMap()) {
        return false;
      }

      if (!(key1.equals(that.key1))) {
        return false;
      }

      return true;
    }

  }

  private static final class Set1To1Node_5Bits_Spec0To8<K> extends CompactMixedSetNode<K> {

    private final K key1;
    private final CompactSetNode<K> node1;

    Set1To1Node_5Bits_Spec0To8(final AtomicReference<Thread> mutator, final int nodeMap,
        final int dataMap, final K key1, final CompactSetNode<K> node1) {
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
    CompactSetNode<K> getNode(int index) {
      switch (index) {
        case 0:
          return node1;
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    K getKey(int index) {
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
    CompactSetNode<K> copyAndInsertValue(AtomicReference<Thread> mutator, final int bitpos,
        final K key) {
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
    CompactSetNode<K> copyAndRemoveValue(AtomicReference<Thread> mutator, final int bitpos) {
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
    CompactSetNode<K> copyAndSetNode(AtomicReference<Thread> mutator, final int bitpos,
        CompactSetNode<K> node) {
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
    CompactSetNode<K> copyAndMigrateFromInlineToNode(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetNode<K> node) {
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
    CompactSetNode<K> copyAndMigrateFromNodeToInline(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetNode<K> node) {
      final int bitIndex = nodeIndex(bitpos);
      final int valIndex = dataIndex(bitpos);

      final int nodeMap = (int) (this.nodeMap() ^ bitpos);
      final int dataMap = (int) (this.dataMap() | bitpos);

      final K key = node.getKey(0);

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

      result = prime * result + key1.hashCode();

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
      Set1To1Node_5Bits_Spec0To8<?> that = (Set1To1Node_5Bits_Spec0To8<?>) other;

      if (nodeMap() != that.nodeMap()) {
        return false;
      }
      if (dataMap() != that.dataMap()) {
        return false;
      }

      if (!(key1.equals(that.key1))) {
        return false;
      }
      if (!(node1.equals(that.node1))) {
        return false;
      }

      return true;
    }

  }

  private static final class Set1To2Node_5Bits_Spec0To8<K> extends CompactMixedSetNode<K> {

    private final K key1;
    private final CompactSetNode<K> node1;
    private final CompactSetNode<K> node2;

    Set1To2Node_5Bits_Spec0To8(final AtomicReference<Thread> mutator, final int nodeMap,
        final int dataMap, final K key1, final CompactSetNode<K> node1,
        final CompactSetNode<K> node2) {
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
    CompactSetNode<K> getNode(int index) {
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
    K getKey(int index) {
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
    CompactSetNode<K> copyAndInsertValue(AtomicReference<Thread> mutator, final int bitpos,
        final K key) {
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
    CompactSetNode<K> copyAndRemoveValue(AtomicReference<Thread> mutator, final int bitpos) {
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
    CompactSetNode<K> copyAndSetNode(AtomicReference<Thread> mutator, final int bitpos,
        CompactSetNode<K> node) {
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
    CompactSetNode<K> copyAndMigrateFromInlineToNode(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetNode<K> node) {
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
    CompactSetNode<K> copyAndMigrateFromNodeToInline(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetNode<K> node) {
      final int bitIndex = nodeIndex(bitpos);
      final int valIndex = dataIndex(bitpos);

      final int nodeMap = (int) (this.nodeMap() ^ bitpos);
      final int dataMap = (int) (this.dataMap() | bitpos);

      final K key = node.getKey(0);

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

      result = prime * result + key1.hashCode();

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
      Set1To2Node_5Bits_Spec0To8<?> that = (Set1To2Node_5Bits_Spec0To8<?>) other;

      if (nodeMap() != that.nodeMap()) {
        return false;
      }
      if (dataMap() != that.dataMap()) {
        return false;
      }

      if (!(key1.equals(that.key1))) {
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

  private static final class Set1To3Node_5Bits_Spec0To8<K> extends CompactMixedSetNode<K> {

    private final K key1;
    private final CompactSetNode<K> node1;
    private final CompactSetNode<K> node2;
    private final CompactSetNode<K> node3;

    Set1To3Node_5Bits_Spec0To8(final AtomicReference<Thread> mutator, final int nodeMap,
        final int dataMap, final K key1, final CompactSetNode<K> node1,
        final CompactSetNode<K> node2, final CompactSetNode<K> node3) {
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
    CompactSetNode<K> getNode(int index) {
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
    K getKey(int index) {
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
    CompactSetNode<K> copyAndInsertValue(AtomicReference<Thread> mutator, final int bitpos,
        final K key) {
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
    CompactSetNode<K> copyAndRemoveValue(AtomicReference<Thread> mutator, final int bitpos) {
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
    CompactSetNode<K> copyAndSetNode(AtomicReference<Thread> mutator, final int bitpos,
        CompactSetNode<K> node) {
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
    CompactSetNode<K> copyAndMigrateFromInlineToNode(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetNode<K> node) {
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
    CompactSetNode<K> copyAndMigrateFromNodeToInline(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetNode<K> node) {
      final int bitIndex = nodeIndex(bitpos);
      final int valIndex = dataIndex(bitpos);

      final int nodeMap = (int) (this.nodeMap() ^ bitpos);
      final int dataMap = (int) (this.dataMap() | bitpos);

      final K key = node.getKey(0);

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

      result = prime * result + key1.hashCode();

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
      Set1To3Node_5Bits_Spec0To8<?> that = (Set1To3Node_5Bits_Spec0To8<?>) other;

      if (nodeMap() != that.nodeMap()) {
        return false;
      }
      if (dataMap() != that.dataMap()) {
        return false;
      }

      if (!(key1.equals(that.key1))) {
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

  private static final class Set1To4Node_5Bits_Spec0To8<K> extends CompactMixedSetNode<K> {

    private final K key1;
    private final CompactSetNode<K> node1;
    private final CompactSetNode<K> node2;
    private final CompactSetNode<K> node3;
    private final CompactSetNode<K> node4;

    Set1To4Node_5Bits_Spec0To8(final AtomicReference<Thread> mutator, final int nodeMap,
        final int dataMap, final K key1, final CompactSetNode<K> node1,
        final CompactSetNode<K> node2, final CompactSetNode<K> node3,
        final CompactSetNode<K> node4) {
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
    CompactSetNode<K> getNode(int index) {
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
    K getKey(int index) {
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
    CompactSetNode<K> copyAndInsertValue(AtomicReference<Thread> mutator, final int bitpos,
        final K key) {
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
    CompactSetNode<K> copyAndRemoveValue(AtomicReference<Thread> mutator, final int bitpos) {
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
    CompactSetNode<K> copyAndSetNode(AtomicReference<Thread> mutator, final int bitpos,
        CompactSetNode<K> node) {
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
    CompactSetNode<K> copyAndMigrateFromInlineToNode(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetNode<K> node) {
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
    CompactSetNode<K> copyAndMigrateFromNodeToInline(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetNode<K> node) {
      final int bitIndex = nodeIndex(bitpos);
      final int valIndex = dataIndex(bitpos);

      final int nodeMap = (int) (this.nodeMap() ^ bitpos);
      final int dataMap = (int) (this.dataMap() | bitpos);

      final K key = node.getKey(0);

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

      result = prime * result + key1.hashCode();

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
      Set1To4Node_5Bits_Spec0To8<?> that = (Set1To4Node_5Bits_Spec0To8<?>) other;

      if (nodeMap() != that.nodeMap()) {
        return false;
      }
      if (dataMap() != that.dataMap()) {
        return false;
      }

      if (!(key1.equals(that.key1))) {
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

  private static final class Set1To5Node_5Bits_Spec0To8<K> extends CompactMixedSetNode<K> {

    private final K key1;
    private final CompactSetNode<K> node1;
    private final CompactSetNode<K> node2;
    private final CompactSetNode<K> node3;
    private final CompactSetNode<K> node4;
    private final CompactSetNode<K> node5;

    Set1To5Node_5Bits_Spec0To8(final AtomicReference<Thread> mutator, final int nodeMap,
        final int dataMap, final K key1, final CompactSetNode<K> node1,
        final CompactSetNode<K> node2, final CompactSetNode<K> node3, final CompactSetNode<K> node4,
        final CompactSetNode<K> node5) {
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
    CompactSetNode<K> getNode(int index) {
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
    K getKey(int index) {
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
    CompactSetNode<K> copyAndInsertValue(AtomicReference<Thread> mutator, final int bitpos,
        final K key) {
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
    CompactSetNode<K> copyAndRemoveValue(AtomicReference<Thread> mutator, final int bitpos) {
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
    CompactSetNode<K> copyAndSetNode(AtomicReference<Thread> mutator, final int bitpos,
        CompactSetNode<K> node) {
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
    CompactSetNode<K> copyAndMigrateFromInlineToNode(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetNode<K> node) {
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
    CompactSetNode<K> copyAndMigrateFromNodeToInline(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetNode<K> node) {
      final int bitIndex = nodeIndex(bitpos);
      final int valIndex = dataIndex(bitpos);

      final int nodeMap = (int) (this.nodeMap() ^ bitpos);
      final int dataMap = (int) (this.dataMap() | bitpos);

      final K key = node.getKey(0);

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

      result = prime * result + key1.hashCode();

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
      Set1To5Node_5Bits_Spec0To8<?> that = (Set1To5Node_5Bits_Spec0To8<?>) other;

      if (nodeMap() != that.nodeMap()) {
        return false;
      }
      if (dataMap() != that.dataMap()) {
        return false;
      }

      if (!(key1.equals(that.key1))) {
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

  private static final class Set1To6Node_5Bits_Spec0To8<K> extends CompactMixedSetNode<K> {

    private final K key1;
    private final CompactSetNode<K> node1;
    private final CompactSetNode<K> node2;
    private final CompactSetNode<K> node3;
    private final CompactSetNode<K> node4;
    private final CompactSetNode<K> node5;
    private final CompactSetNode<K> node6;

    Set1To6Node_5Bits_Spec0To8(final AtomicReference<Thread> mutator, final int nodeMap,
        final int dataMap, final K key1, final CompactSetNode<K> node1,
        final CompactSetNode<K> node2, final CompactSetNode<K> node3, final CompactSetNode<K> node4,
        final CompactSetNode<K> node5, final CompactSetNode<K> node6) {
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
    CompactSetNode<K> getNode(int index) {
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
    K getKey(int index) {
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
    CompactSetNode<K> copyAndInsertValue(AtomicReference<Thread> mutator, final int bitpos,
        final K key) {
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
    CompactSetNode<K> copyAndRemoveValue(AtomicReference<Thread> mutator, final int bitpos) {
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
    CompactSetNode<K> copyAndSetNode(AtomicReference<Thread> mutator, final int bitpos,
        CompactSetNode<K> node) {
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
    CompactSetNode<K> copyAndMigrateFromInlineToNode(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetNode<K> node) {
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
    CompactSetNode<K> copyAndMigrateFromNodeToInline(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetNode<K> node) {
      final int bitIndex = nodeIndex(bitpos);
      final int valIndex = dataIndex(bitpos);

      final int nodeMap = (int) (this.nodeMap() ^ bitpos);
      final int dataMap = (int) (this.dataMap() | bitpos);

      final K key = node.getKey(0);

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

      result = prime * result + key1.hashCode();

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
      Set1To6Node_5Bits_Spec0To8<?> that = (Set1To6Node_5Bits_Spec0To8<?>) other;

      if (nodeMap() != that.nodeMap()) {
        return false;
      }
      if (dataMap() != that.dataMap()) {
        return false;
      }

      if (!(key1.equals(that.key1))) {
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

  private static final class Set1To7Node_5Bits_Spec0To8<K> extends CompactMixedSetNode<K> {

    private final K key1;
    private final CompactSetNode<K> node1;
    private final CompactSetNode<K> node2;
    private final CompactSetNode<K> node3;
    private final CompactSetNode<K> node4;
    private final CompactSetNode<K> node5;
    private final CompactSetNode<K> node6;
    private final CompactSetNode<K> node7;

    Set1To7Node_5Bits_Spec0To8(final AtomicReference<Thread> mutator, final int nodeMap,
        final int dataMap, final K key1, final CompactSetNode<K> node1,
        final CompactSetNode<K> node2, final CompactSetNode<K> node3, final CompactSetNode<K> node4,
        final CompactSetNode<K> node5, final CompactSetNode<K> node6,
        final CompactSetNode<K> node7) {
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
    CompactSetNode<K> getNode(int index) {
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
    K getKey(int index) {
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
    CompactSetNode<K> copyAndInsertValue(AtomicReference<Thread> mutator, final int bitpos,
        final K key) {
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
    CompactSetNode<K> copyAndRemoveValue(AtomicReference<Thread> mutator, final int bitpos) {
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
    CompactSetNode<K> copyAndSetNode(AtomicReference<Thread> mutator, final int bitpos,
        CompactSetNode<K> node) {
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
    CompactSetNode<K> copyAndMigrateFromInlineToNode(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetNode<K> node) {
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
    CompactSetNode<K> copyAndMigrateFromNodeToInline(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetNode<K> node) {
      final int bitIndex = nodeIndex(bitpos);
      final int valIndex = dataIndex(bitpos);

      final int nodeMap = (int) (this.nodeMap() ^ bitpos);
      final int dataMap = (int) (this.dataMap() | bitpos);

      final K key = node.getKey(0);

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

      result = prime * result + key1.hashCode();

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
      Set1To7Node_5Bits_Spec0To8<?> that = (Set1To7Node_5Bits_Spec0To8<?>) other;

      if (nodeMap() != that.nodeMap()) {
        return false;
      }
      if (dataMap() != that.dataMap()) {
        return false;
      }

      if (!(key1.equals(that.key1))) {
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

  private static final class Set2To0Node_5Bits_Spec0To8<K> extends CompactValuesOnlySetNode<K> {

    private final K key1;
    private final K key2;

    Set2To0Node_5Bits_Spec0To8(final AtomicReference<Thread> mutator, final int nodeMap,
        final int dataMap, final K key1, final K key2) {
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
    CompactSetNode<K> getNode(int index) {
      throw new IllegalStateException("Index out of range.");
    }

    @Override
    K getKey(int index) {
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
    CompactSetNode<K> copyAndInsertValue(AtomicReference<Thread> mutator, final int bitpos,
        final K key) {
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
    CompactSetNode<K> copyAndRemoveValue(AtomicReference<Thread> mutator, final int bitpos) {
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
    CompactSetNode<K> copyAndSetNode(AtomicReference<Thread> mutator, final int bitpos,
        CompactSetNode<K> node) {
      throw new IllegalStateException("Index out of range.");
    }

    @Override
    CompactSetNode<K> copyAndMigrateFromInlineToNode(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetNode<K> node) {
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
    CompactSetNode<K> copyAndMigrateFromNodeToInline(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetNode<K> node) {
      throw new IllegalStateException("Index out of range.");
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;

      result = prime * result + ((int) nodeMap());
      result = prime * result + ((int) dataMap());

      result = prime * result + key1.hashCode();
      result = prime * result + key2.hashCode();

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
      Set2To0Node_5Bits_Spec0To8<?> that = (Set2To0Node_5Bits_Spec0To8<?>) other;

      if (nodeMap() != that.nodeMap()) {
        return false;
      }
      if (dataMap() != that.dataMap()) {
        return false;
      }

      if (!(key1.equals(that.key1))) {
        return false;
      }
      if (!(key2.equals(that.key2))) {
        return false;
      }

      return true;
    }

  }

  private static final class Set2To1Node_5Bits_Spec0To8<K> extends CompactMixedSetNode<K> {

    private final K key1;
    private final K key2;
    private final CompactSetNode<K> node1;

    Set2To1Node_5Bits_Spec0To8(final AtomicReference<Thread> mutator, final int nodeMap,
        final int dataMap, final K key1, final K key2, final CompactSetNode<K> node1) {
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
    CompactSetNode<K> getNode(int index) {
      switch (index) {
        case 0:
          return node1;
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    K getKey(int index) {
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
    CompactSetNode<K> copyAndInsertValue(AtomicReference<Thread> mutator, final int bitpos,
        final K key) {
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
    CompactSetNode<K> copyAndRemoveValue(AtomicReference<Thread> mutator, final int bitpos) {
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
    CompactSetNode<K> copyAndSetNode(AtomicReference<Thread> mutator, final int bitpos,
        CompactSetNode<K> node) {
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
    CompactSetNode<K> copyAndMigrateFromInlineToNode(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetNode<K> node) {
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
    CompactSetNode<K> copyAndMigrateFromNodeToInline(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetNode<K> node) {
      final int bitIndex = nodeIndex(bitpos);
      final int valIndex = dataIndex(bitpos);

      final int nodeMap = (int) (this.nodeMap() ^ bitpos);
      final int dataMap = (int) (this.dataMap() | bitpos);

      final K key = node.getKey(0);

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

      result = prime * result + key1.hashCode();
      result = prime * result + key2.hashCode();

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
      Set2To1Node_5Bits_Spec0To8<?> that = (Set2To1Node_5Bits_Spec0To8<?>) other;

      if (nodeMap() != that.nodeMap()) {
        return false;
      }
      if (dataMap() != that.dataMap()) {
        return false;
      }

      if (!(key1.equals(that.key1))) {
        return false;
      }
      if (!(key2.equals(that.key2))) {
        return false;
      }
      if (!(node1.equals(that.node1))) {
        return false;
      }

      return true;
    }

  }

  private static final class Set2To2Node_5Bits_Spec0To8<K> extends CompactMixedSetNode<K> {

    private final K key1;
    private final K key2;
    private final CompactSetNode<K> node1;
    private final CompactSetNode<K> node2;

    Set2To2Node_5Bits_Spec0To8(final AtomicReference<Thread> mutator, final int nodeMap,
        final int dataMap, final K key1, final K key2, final CompactSetNode<K> node1,
        final CompactSetNode<K> node2) {
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
    CompactSetNode<K> getNode(int index) {
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
    K getKey(int index) {
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
    CompactSetNode<K> copyAndInsertValue(AtomicReference<Thread> mutator, final int bitpos,
        final K key) {
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
    CompactSetNode<K> copyAndRemoveValue(AtomicReference<Thread> mutator, final int bitpos) {
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
    CompactSetNode<K> copyAndSetNode(AtomicReference<Thread> mutator, final int bitpos,
        CompactSetNode<K> node) {
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
    CompactSetNode<K> copyAndMigrateFromInlineToNode(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetNode<K> node) {
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
    CompactSetNode<K> copyAndMigrateFromNodeToInline(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetNode<K> node) {
      final int bitIndex = nodeIndex(bitpos);
      final int valIndex = dataIndex(bitpos);

      final int nodeMap = (int) (this.nodeMap() ^ bitpos);
      final int dataMap = (int) (this.dataMap() | bitpos);

      final K key = node.getKey(0);

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

      result = prime * result + key1.hashCode();
      result = prime * result + key2.hashCode();

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
      Set2To2Node_5Bits_Spec0To8<?> that = (Set2To2Node_5Bits_Spec0To8<?>) other;

      if (nodeMap() != that.nodeMap()) {
        return false;
      }
      if (dataMap() != that.dataMap()) {
        return false;
      }

      if (!(key1.equals(that.key1))) {
        return false;
      }
      if (!(key2.equals(that.key2))) {
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

  private static final class Set2To3Node_5Bits_Spec0To8<K> extends CompactMixedSetNode<K> {

    private final K key1;
    private final K key2;
    private final CompactSetNode<K> node1;
    private final CompactSetNode<K> node2;
    private final CompactSetNode<K> node3;

    Set2To3Node_5Bits_Spec0To8(final AtomicReference<Thread> mutator, final int nodeMap,
        final int dataMap, final K key1, final K key2, final CompactSetNode<K> node1,
        final CompactSetNode<K> node2, final CompactSetNode<K> node3) {
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
    CompactSetNode<K> getNode(int index) {
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
    K getKey(int index) {
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
    CompactSetNode<K> copyAndInsertValue(AtomicReference<Thread> mutator, final int bitpos,
        final K key) {
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
    CompactSetNode<K> copyAndRemoveValue(AtomicReference<Thread> mutator, final int bitpos) {
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
    CompactSetNode<K> copyAndSetNode(AtomicReference<Thread> mutator, final int bitpos,
        CompactSetNode<K> node) {
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
    CompactSetNode<K> copyAndMigrateFromInlineToNode(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetNode<K> node) {
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
    CompactSetNode<K> copyAndMigrateFromNodeToInline(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetNode<K> node) {
      final int bitIndex = nodeIndex(bitpos);
      final int valIndex = dataIndex(bitpos);

      final int nodeMap = (int) (this.nodeMap() ^ bitpos);
      final int dataMap = (int) (this.dataMap() | bitpos);

      final K key = node.getKey(0);

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

      result = prime * result + key1.hashCode();
      result = prime * result + key2.hashCode();

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
      Set2To3Node_5Bits_Spec0To8<?> that = (Set2To3Node_5Bits_Spec0To8<?>) other;

      if (nodeMap() != that.nodeMap()) {
        return false;
      }
      if (dataMap() != that.dataMap()) {
        return false;
      }

      if (!(key1.equals(that.key1))) {
        return false;
      }
      if (!(key2.equals(that.key2))) {
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

  private static final class Set2To4Node_5Bits_Spec0To8<K> extends CompactMixedSetNode<K> {

    private final K key1;
    private final K key2;
    private final CompactSetNode<K> node1;
    private final CompactSetNode<K> node2;
    private final CompactSetNode<K> node3;
    private final CompactSetNode<K> node4;

    Set2To4Node_5Bits_Spec0To8(final AtomicReference<Thread> mutator, final int nodeMap,
        final int dataMap, final K key1, final K key2, final CompactSetNode<K> node1,
        final CompactSetNode<K> node2, final CompactSetNode<K> node3,
        final CompactSetNode<K> node4) {
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
    CompactSetNode<K> getNode(int index) {
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
    K getKey(int index) {
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
    CompactSetNode<K> copyAndInsertValue(AtomicReference<Thread> mutator, final int bitpos,
        final K key) {
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
    CompactSetNode<K> copyAndRemoveValue(AtomicReference<Thread> mutator, final int bitpos) {
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
    CompactSetNode<K> copyAndSetNode(AtomicReference<Thread> mutator, final int bitpos,
        CompactSetNode<K> node) {
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
    CompactSetNode<K> copyAndMigrateFromInlineToNode(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetNode<K> node) {
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
    CompactSetNode<K> copyAndMigrateFromNodeToInline(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetNode<K> node) {
      final int bitIndex = nodeIndex(bitpos);
      final int valIndex = dataIndex(bitpos);

      final int nodeMap = (int) (this.nodeMap() ^ bitpos);
      final int dataMap = (int) (this.dataMap() | bitpos);

      final K key = node.getKey(0);

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

      result = prime * result + key1.hashCode();
      result = prime * result + key2.hashCode();

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
      Set2To4Node_5Bits_Spec0To8<?> that = (Set2To4Node_5Bits_Spec0To8<?>) other;

      if (nodeMap() != that.nodeMap()) {
        return false;
      }
      if (dataMap() != that.dataMap()) {
        return false;
      }

      if (!(key1.equals(that.key1))) {
        return false;
      }
      if (!(key2.equals(that.key2))) {
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

  private static final class Set2To5Node_5Bits_Spec0To8<K> extends CompactMixedSetNode<K> {

    private final K key1;
    private final K key2;
    private final CompactSetNode<K> node1;
    private final CompactSetNode<K> node2;
    private final CompactSetNode<K> node3;
    private final CompactSetNode<K> node4;
    private final CompactSetNode<K> node5;

    Set2To5Node_5Bits_Spec0To8(final AtomicReference<Thread> mutator, final int nodeMap,
        final int dataMap, final K key1, final K key2, final CompactSetNode<K> node1,
        final CompactSetNode<K> node2, final CompactSetNode<K> node3, final CompactSetNode<K> node4,
        final CompactSetNode<K> node5) {
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
    CompactSetNode<K> getNode(int index) {
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
    K getKey(int index) {
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
    CompactSetNode<K> copyAndInsertValue(AtomicReference<Thread> mutator, final int bitpos,
        final K key) {
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
    CompactSetNode<K> copyAndRemoveValue(AtomicReference<Thread> mutator, final int bitpos) {
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
    CompactSetNode<K> copyAndSetNode(AtomicReference<Thread> mutator, final int bitpos,
        CompactSetNode<K> node) {
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
    CompactSetNode<K> copyAndMigrateFromInlineToNode(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetNode<K> node) {
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
    CompactSetNode<K> copyAndMigrateFromNodeToInline(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetNode<K> node) {
      final int bitIndex = nodeIndex(bitpos);
      final int valIndex = dataIndex(bitpos);

      final int nodeMap = (int) (this.nodeMap() ^ bitpos);
      final int dataMap = (int) (this.dataMap() | bitpos);

      final K key = node.getKey(0);

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

      result = prime * result + key1.hashCode();
      result = prime * result + key2.hashCode();

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
      Set2To5Node_5Bits_Spec0To8<?> that = (Set2To5Node_5Bits_Spec0To8<?>) other;

      if (nodeMap() != that.nodeMap()) {
        return false;
      }
      if (dataMap() != that.dataMap()) {
        return false;
      }

      if (!(key1.equals(that.key1))) {
        return false;
      }
      if (!(key2.equals(that.key2))) {
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

  private static final class Set2To6Node_5Bits_Spec0To8<K> extends CompactMixedSetNode<K> {

    private final K key1;
    private final K key2;
    private final CompactSetNode<K> node1;
    private final CompactSetNode<K> node2;
    private final CompactSetNode<K> node3;
    private final CompactSetNode<K> node4;
    private final CompactSetNode<K> node5;
    private final CompactSetNode<K> node6;

    Set2To6Node_5Bits_Spec0To8(final AtomicReference<Thread> mutator, final int nodeMap,
        final int dataMap, final K key1, final K key2, final CompactSetNode<K> node1,
        final CompactSetNode<K> node2, final CompactSetNode<K> node3, final CompactSetNode<K> node4,
        final CompactSetNode<K> node5, final CompactSetNode<K> node6) {
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
    CompactSetNode<K> getNode(int index) {
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
    K getKey(int index) {
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
    CompactSetNode<K> copyAndInsertValue(AtomicReference<Thread> mutator, final int bitpos,
        final K key) {
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
    CompactSetNode<K> copyAndRemoveValue(AtomicReference<Thread> mutator, final int bitpos) {
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
    CompactSetNode<K> copyAndSetNode(AtomicReference<Thread> mutator, final int bitpos,
        CompactSetNode<K> node) {
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
    CompactSetNode<K> copyAndMigrateFromInlineToNode(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetNode<K> node) {
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
    CompactSetNode<K> copyAndMigrateFromNodeToInline(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetNode<K> node) {
      final int bitIndex = nodeIndex(bitpos);
      final int valIndex = dataIndex(bitpos);

      final int nodeMap = (int) (this.nodeMap() ^ bitpos);
      final int dataMap = (int) (this.dataMap() | bitpos);

      final K key = node.getKey(0);

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

      result = prime * result + key1.hashCode();
      result = prime * result + key2.hashCode();

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
      Set2To6Node_5Bits_Spec0To8<?> that = (Set2To6Node_5Bits_Spec0To8<?>) other;

      if (nodeMap() != that.nodeMap()) {
        return false;
      }
      if (dataMap() != that.dataMap()) {
        return false;
      }

      if (!(key1.equals(that.key1))) {
        return false;
      }
      if (!(key2.equals(that.key2))) {
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

  private static final class Set3To0Node_5Bits_Spec0To8<K> extends CompactValuesOnlySetNode<K> {

    private final K key1;
    private final K key2;
    private final K key3;

    Set3To0Node_5Bits_Spec0To8(final AtomicReference<Thread> mutator, final int nodeMap,
        final int dataMap, final K key1, final K key2, final K key3) {
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
    CompactSetNode<K> getNode(int index) {
      throw new IllegalStateException("Index out of range.");
    }

    @Override
    K getKey(int index) {
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
    CompactSetNode<K> copyAndInsertValue(AtomicReference<Thread> mutator, final int bitpos,
        final K key) {
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
    CompactSetNode<K> copyAndRemoveValue(AtomicReference<Thread> mutator, final int bitpos) {
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
    CompactSetNode<K> copyAndSetNode(AtomicReference<Thread> mutator, final int bitpos,
        CompactSetNode<K> node) {
      throw new IllegalStateException("Index out of range.");
    }

    @Override
    CompactSetNode<K> copyAndMigrateFromInlineToNode(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetNode<K> node) {
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
    CompactSetNode<K> copyAndMigrateFromNodeToInline(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetNode<K> node) {
      throw new IllegalStateException("Index out of range.");
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;

      result = prime * result + ((int) nodeMap());
      result = prime * result + ((int) dataMap());

      result = prime * result + key1.hashCode();
      result = prime * result + key2.hashCode();
      result = prime * result + key3.hashCode();

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
      Set3To0Node_5Bits_Spec0To8<?> that = (Set3To0Node_5Bits_Spec0To8<?>) other;

      if (nodeMap() != that.nodeMap()) {
        return false;
      }
      if (dataMap() != that.dataMap()) {
        return false;
      }

      if (!(key1.equals(that.key1))) {
        return false;
      }
      if (!(key2.equals(that.key2))) {
        return false;
      }
      if (!(key3.equals(that.key3))) {
        return false;
      }

      return true;
    }

  }

  private static final class Set3To1Node_5Bits_Spec0To8<K> extends CompactMixedSetNode<K> {

    private final K key1;
    private final K key2;
    private final K key3;
    private final CompactSetNode<K> node1;

    Set3To1Node_5Bits_Spec0To8(final AtomicReference<Thread> mutator, final int nodeMap,
        final int dataMap, final K key1, final K key2, final K key3,
        final CompactSetNode<K> node1) {
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
    CompactSetNode<K> getNode(int index) {
      switch (index) {
        case 0:
          return node1;
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    K getKey(int index) {
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
    CompactSetNode<K> copyAndInsertValue(AtomicReference<Thread> mutator, final int bitpos,
        final K key) {
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
    CompactSetNode<K> copyAndRemoveValue(AtomicReference<Thread> mutator, final int bitpos) {
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
    CompactSetNode<K> copyAndSetNode(AtomicReference<Thread> mutator, final int bitpos,
        CompactSetNode<K> node) {
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
    CompactSetNode<K> copyAndMigrateFromInlineToNode(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetNode<K> node) {
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
    CompactSetNode<K> copyAndMigrateFromNodeToInline(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetNode<K> node) {
      final int bitIndex = nodeIndex(bitpos);
      final int valIndex = dataIndex(bitpos);

      final int nodeMap = (int) (this.nodeMap() ^ bitpos);
      final int dataMap = (int) (this.dataMap() | bitpos);

      final K key = node.getKey(0);

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

      result = prime * result + key1.hashCode();
      result = prime * result + key2.hashCode();
      result = prime * result + key3.hashCode();

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
      Set3To1Node_5Bits_Spec0To8<?> that = (Set3To1Node_5Bits_Spec0To8<?>) other;

      if (nodeMap() != that.nodeMap()) {
        return false;
      }
      if (dataMap() != that.dataMap()) {
        return false;
      }

      if (!(key1.equals(that.key1))) {
        return false;
      }
      if (!(key2.equals(that.key2))) {
        return false;
      }
      if (!(key3.equals(that.key3))) {
        return false;
      }
      if (!(node1.equals(that.node1))) {
        return false;
      }

      return true;
    }

  }

  private static final class Set3To2Node_5Bits_Spec0To8<K> extends CompactMixedSetNode<K> {

    private final K key1;
    private final K key2;
    private final K key3;
    private final CompactSetNode<K> node1;
    private final CompactSetNode<K> node2;

    Set3To2Node_5Bits_Spec0To8(final AtomicReference<Thread> mutator, final int nodeMap,
        final int dataMap, final K key1, final K key2, final K key3, final CompactSetNode<K> node1,
        final CompactSetNode<K> node2) {
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
    CompactSetNode<K> getNode(int index) {
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
    K getKey(int index) {
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
    CompactSetNode<K> copyAndInsertValue(AtomicReference<Thread> mutator, final int bitpos,
        final K key) {
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
    CompactSetNode<K> copyAndRemoveValue(AtomicReference<Thread> mutator, final int bitpos) {
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
    CompactSetNode<K> copyAndSetNode(AtomicReference<Thread> mutator, final int bitpos,
        CompactSetNode<K> node) {
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
    CompactSetNode<K> copyAndMigrateFromInlineToNode(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetNode<K> node) {
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
    CompactSetNode<K> copyAndMigrateFromNodeToInline(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetNode<K> node) {
      final int bitIndex = nodeIndex(bitpos);
      final int valIndex = dataIndex(bitpos);

      final int nodeMap = (int) (this.nodeMap() ^ bitpos);
      final int dataMap = (int) (this.dataMap() | bitpos);

      final K key = node.getKey(0);

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

      result = prime * result + key1.hashCode();
      result = prime * result + key2.hashCode();
      result = prime * result + key3.hashCode();

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
      Set3To2Node_5Bits_Spec0To8<?> that = (Set3To2Node_5Bits_Spec0To8<?>) other;

      if (nodeMap() != that.nodeMap()) {
        return false;
      }
      if (dataMap() != that.dataMap()) {
        return false;
      }

      if (!(key1.equals(that.key1))) {
        return false;
      }
      if (!(key2.equals(that.key2))) {
        return false;
      }
      if (!(key3.equals(that.key3))) {
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

  private static final class Set3To3Node_5Bits_Spec0To8<K> extends CompactMixedSetNode<K> {

    private final K key1;
    private final K key2;
    private final K key3;
    private final CompactSetNode<K> node1;
    private final CompactSetNode<K> node2;
    private final CompactSetNode<K> node3;

    Set3To3Node_5Bits_Spec0To8(final AtomicReference<Thread> mutator, final int nodeMap,
        final int dataMap, final K key1, final K key2, final K key3, final CompactSetNode<K> node1,
        final CompactSetNode<K> node2, final CompactSetNode<K> node3) {
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
    CompactSetNode<K> getNode(int index) {
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
    K getKey(int index) {
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
    CompactSetNode<K> copyAndInsertValue(AtomicReference<Thread> mutator, final int bitpos,
        final K key) {
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
    CompactSetNode<K> copyAndRemoveValue(AtomicReference<Thread> mutator, final int bitpos) {
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
    CompactSetNode<K> copyAndSetNode(AtomicReference<Thread> mutator, final int bitpos,
        CompactSetNode<K> node) {
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
    CompactSetNode<K> copyAndMigrateFromInlineToNode(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetNode<K> node) {
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
    CompactSetNode<K> copyAndMigrateFromNodeToInline(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetNode<K> node) {
      final int bitIndex = nodeIndex(bitpos);
      final int valIndex = dataIndex(bitpos);

      final int nodeMap = (int) (this.nodeMap() ^ bitpos);
      final int dataMap = (int) (this.dataMap() | bitpos);

      final K key = node.getKey(0);

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

      result = prime * result + key1.hashCode();
      result = prime * result + key2.hashCode();
      result = prime * result + key3.hashCode();

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
      Set3To3Node_5Bits_Spec0To8<?> that = (Set3To3Node_5Bits_Spec0To8<?>) other;

      if (nodeMap() != that.nodeMap()) {
        return false;
      }
      if (dataMap() != that.dataMap()) {
        return false;
      }

      if (!(key1.equals(that.key1))) {
        return false;
      }
      if (!(key2.equals(that.key2))) {
        return false;
      }
      if (!(key3.equals(that.key3))) {
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

  private static final class Set3To4Node_5Bits_Spec0To8<K> extends CompactMixedSetNode<K> {

    private final K key1;
    private final K key2;
    private final K key3;
    private final CompactSetNode<K> node1;
    private final CompactSetNode<K> node2;
    private final CompactSetNode<K> node3;
    private final CompactSetNode<K> node4;

    Set3To4Node_5Bits_Spec0To8(final AtomicReference<Thread> mutator, final int nodeMap,
        final int dataMap, final K key1, final K key2, final K key3, final CompactSetNode<K> node1,
        final CompactSetNode<K> node2, final CompactSetNode<K> node3,
        final CompactSetNode<K> node4) {
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
    CompactSetNode<K> getNode(int index) {
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
    K getKey(int index) {
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
    CompactSetNode<K> copyAndInsertValue(AtomicReference<Thread> mutator, final int bitpos,
        final K key) {
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
    CompactSetNode<K> copyAndRemoveValue(AtomicReference<Thread> mutator, final int bitpos) {
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
    CompactSetNode<K> copyAndSetNode(AtomicReference<Thread> mutator, final int bitpos,
        CompactSetNode<K> node) {
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
    CompactSetNode<K> copyAndMigrateFromInlineToNode(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetNode<K> node) {
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
    CompactSetNode<K> copyAndMigrateFromNodeToInline(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetNode<K> node) {
      final int bitIndex = nodeIndex(bitpos);
      final int valIndex = dataIndex(bitpos);

      final int nodeMap = (int) (this.nodeMap() ^ bitpos);
      final int dataMap = (int) (this.dataMap() | bitpos);

      final K key = node.getKey(0);

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

      result = prime * result + key1.hashCode();
      result = prime * result + key2.hashCode();
      result = prime * result + key3.hashCode();

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
      Set3To4Node_5Bits_Spec0To8<?> that = (Set3To4Node_5Bits_Spec0To8<?>) other;

      if (nodeMap() != that.nodeMap()) {
        return false;
      }
      if (dataMap() != that.dataMap()) {
        return false;
      }

      if (!(key1.equals(that.key1))) {
        return false;
      }
      if (!(key2.equals(that.key2))) {
        return false;
      }
      if (!(key3.equals(that.key3))) {
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

  private static final class Set3To5Node_5Bits_Spec0To8<K> extends CompactMixedSetNode<K> {

    private final K key1;
    private final K key2;
    private final K key3;
    private final CompactSetNode<K> node1;
    private final CompactSetNode<K> node2;
    private final CompactSetNode<K> node3;
    private final CompactSetNode<K> node4;
    private final CompactSetNode<K> node5;

    Set3To5Node_5Bits_Spec0To8(final AtomicReference<Thread> mutator, final int nodeMap,
        final int dataMap, final K key1, final K key2, final K key3, final CompactSetNode<K> node1,
        final CompactSetNode<K> node2, final CompactSetNode<K> node3, final CompactSetNode<K> node4,
        final CompactSetNode<K> node5) {
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
    CompactSetNode<K> getNode(int index) {
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
    K getKey(int index) {
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
    CompactSetNode<K> copyAndInsertValue(AtomicReference<Thread> mutator, final int bitpos,
        final K key) {
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
    CompactSetNode<K> copyAndRemoveValue(AtomicReference<Thread> mutator, final int bitpos) {
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
    CompactSetNode<K> copyAndSetNode(AtomicReference<Thread> mutator, final int bitpos,
        CompactSetNode<K> node) {
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
    CompactSetNode<K> copyAndMigrateFromInlineToNode(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetNode<K> node) {
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
    CompactSetNode<K> copyAndMigrateFromNodeToInline(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetNode<K> node) {
      final int bitIndex = nodeIndex(bitpos);
      final int valIndex = dataIndex(bitpos);

      final int nodeMap = (int) (this.nodeMap() ^ bitpos);
      final int dataMap = (int) (this.dataMap() | bitpos);

      final K key = node.getKey(0);

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

      result = prime * result + key1.hashCode();
      result = prime * result + key2.hashCode();
      result = prime * result + key3.hashCode();

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
      Set3To5Node_5Bits_Spec0To8<?> that = (Set3To5Node_5Bits_Spec0To8<?>) other;

      if (nodeMap() != that.nodeMap()) {
        return false;
      }
      if (dataMap() != that.dataMap()) {
        return false;
      }

      if (!(key1.equals(that.key1))) {
        return false;
      }
      if (!(key2.equals(that.key2))) {
        return false;
      }
      if (!(key3.equals(that.key3))) {
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

  private static final class Set4To0Node_5Bits_Spec0To8<K> extends CompactValuesOnlySetNode<K> {

    private final K key1;
    private final K key2;
    private final K key3;
    private final K key4;

    Set4To0Node_5Bits_Spec0To8(final AtomicReference<Thread> mutator, final int nodeMap,
        final int dataMap, final K key1, final K key2, final K key3, final K key4) {
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
    CompactSetNode<K> getNode(int index) {
      throw new IllegalStateException("Index out of range.");
    }

    @Override
    K getKey(int index) {
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
    CompactSetNode<K> copyAndInsertValue(AtomicReference<Thread> mutator, final int bitpos,
        final K key) {
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
    CompactSetNode<K> copyAndRemoveValue(AtomicReference<Thread> mutator, final int bitpos) {
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
    CompactSetNode<K> copyAndSetNode(AtomicReference<Thread> mutator, final int bitpos,
        CompactSetNode<K> node) {
      throw new IllegalStateException("Index out of range.");
    }

    @Override
    CompactSetNode<K> copyAndMigrateFromInlineToNode(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetNode<K> node) {
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
    CompactSetNode<K> copyAndMigrateFromNodeToInline(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetNode<K> node) {
      throw new IllegalStateException("Index out of range.");
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;

      result = prime * result + ((int) nodeMap());
      result = prime * result + ((int) dataMap());

      result = prime * result + key1.hashCode();
      result = prime * result + key2.hashCode();
      result = prime * result + key3.hashCode();
      result = prime * result + key4.hashCode();

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
      Set4To0Node_5Bits_Spec0To8<?> that = (Set4To0Node_5Bits_Spec0To8<?>) other;

      if (nodeMap() != that.nodeMap()) {
        return false;
      }
      if (dataMap() != that.dataMap()) {
        return false;
      }

      if (!(key1.equals(that.key1))) {
        return false;
      }
      if (!(key2.equals(that.key2))) {
        return false;
      }
      if (!(key3.equals(that.key3))) {
        return false;
      }
      if (!(key4.equals(that.key4))) {
        return false;
      }

      return true;
    }

  }

  private static final class Set4To1Node_5Bits_Spec0To8<K> extends CompactMixedSetNode<K> {

    private final K key1;
    private final K key2;
    private final K key3;
    private final K key4;
    private final CompactSetNode<K> node1;

    Set4To1Node_5Bits_Spec0To8(final AtomicReference<Thread> mutator, final int nodeMap,
        final int dataMap, final K key1, final K key2, final K key3, final K key4,
        final CompactSetNode<K> node1) {
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
    CompactSetNode<K> getNode(int index) {
      switch (index) {
        case 0:
          return node1;
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    K getKey(int index) {
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
    CompactSetNode<K> copyAndInsertValue(AtomicReference<Thread> mutator, final int bitpos,
        final K key) {
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
    CompactSetNode<K> copyAndRemoveValue(AtomicReference<Thread> mutator, final int bitpos) {
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
    CompactSetNode<K> copyAndSetNode(AtomicReference<Thread> mutator, final int bitpos,
        CompactSetNode<K> node) {
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
    CompactSetNode<K> copyAndMigrateFromInlineToNode(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetNode<K> node) {
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
    CompactSetNode<K> copyAndMigrateFromNodeToInline(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetNode<K> node) {
      final int bitIndex = nodeIndex(bitpos);
      final int valIndex = dataIndex(bitpos);

      final int nodeMap = (int) (this.nodeMap() ^ bitpos);
      final int dataMap = (int) (this.dataMap() | bitpos);

      final K key = node.getKey(0);

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

      result = prime * result + key1.hashCode();
      result = prime * result + key2.hashCode();
      result = prime * result + key3.hashCode();
      result = prime * result + key4.hashCode();

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
      Set4To1Node_5Bits_Spec0To8<?> that = (Set4To1Node_5Bits_Spec0To8<?>) other;

      if (nodeMap() != that.nodeMap()) {
        return false;
      }
      if (dataMap() != that.dataMap()) {
        return false;
      }

      if (!(key1.equals(that.key1))) {
        return false;
      }
      if (!(key2.equals(that.key2))) {
        return false;
      }
      if (!(key3.equals(that.key3))) {
        return false;
      }
      if (!(key4.equals(that.key4))) {
        return false;
      }
      if (!(node1.equals(that.node1))) {
        return false;
      }

      return true;
    }

  }

  private static final class Set4To2Node_5Bits_Spec0To8<K> extends CompactMixedSetNode<K> {

    private final K key1;
    private final K key2;
    private final K key3;
    private final K key4;
    private final CompactSetNode<K> node1;
    private final CompactSetNode<K> node2;

    Set4To2Node_5Bits_Spec0To8(final AtomicReference<Thread> mutator, final int nodeMap,
        final int dataMap, final K key1, final K key2, final K key3, final K key4,
        final CompactSetNode<K> node1, final CompactSetNode<K> node2) {
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
    CompactSetNode<K> getNode(int index) {
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
    K getKey(int index) {
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
    CompactSetNode<K> copyAndInsertValue(AtomicReference<Thread> mutator, final int bitpos,
        final K key) {
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
    CompactSetNode<K> copyAndRemoveValue(AtomicReference<Thread> mutator, final int bitpos) {
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
    CompactSetNode<K> copyAndSetNode(AtomicReference<Thread> mutator, final int bitpos,
        CompactSetNode<K> node) {
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
    CompactSetNode<K> copyAndMigrateFromInlineToNode(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetNode<K> node) {
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
    CompactSetNode<K> copyAndMigrateFromNodeToInline(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetNode<K> node) {
      final int bitIndex = nodeIndex(bitpos);
      final int valIndex = dataIndex(bitpos);

      final int nodeMap = (int) (this.nodeMap() ^ bitpos);
      final int dataMap = (int) (this.dataMap() | bitpos);

      final K key = node.getKey(0);

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

      result = prime * result + key1.hashCode();
      result = prime * result + key2.hashCode();
      result = prime * result + key3.hashCode();
      result = prime * result + key4.hashCode();

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
      Set4To2Node_5Bits_Spec0To8<?> that = (Set4To2Node_5Bits_Spec0To8<?>) other;

      if (nodeMap() != that.nodeMap()) {
        return false;
      }
      if (dataMap() != that.dataMap()) {
        return false;
      }

      if (!(key1.equals(that.key1))) {
        return false;
      }
      if (!(key2.equals(that.key2))) {
        return false;
      }
      if (!(key3.equals(that.key3))) {
        return false;
      }
      if (!(key4.equals(that.key4))) {
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

  private static final class Set4To3Node_5Bits_Spec0To8<K> extends CompactMixedSetNode<K> {

    private final K key1;
    private final K key2;
    private final K key3;
    private final K key4;
    private final CompactSetNode<K> node1;
    private final CompactSetNode<K> node2;
    private final CompactSetNode<K> node3;

    Set4To3Node_5Bits_Spec0To8(final AtomicReference<Thread> mutator, final int nodeMap,
        final int dataMap, final K key1, final K key2, final K key3, final K key4,
        final CompactSetNode<K> node1, final CompactSetNode<K> node2,
        final CompactSetNode<K> node3) {
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
    CompactSetNode<K> getNode(int index) {
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
    K getKey(int index) {
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
    CompactSetNode<K> copyAndInsertValue(AtomicReference<Thread> mutator, final int bitpos,
        final K key) {
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
    CompactSetNode<K> copyAndRemoveValue(AtomicReference<Thread> mutator, final int bitpos) {
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
    CompactSetNode<K> copyAndSetNode(AtomicReference<Thread> mutator, final int bitpos,
        CompactSetNode<K> node) {
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
    CompactSetNode<K> copyAndMigrateFromInlineToNode(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetNode<K> node) {
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
    CompactSetNode<K> copyAndMigrateFromNodeToInline(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetNode<K> node) {
      final int bitIndex = nodeIndex(bitpos);
      final int valIndex = dataIndex(bitpos);

      final int nodeMap = (int) (this.nodeMap() ^ bitpos);
      final int dataMap = (int) (this.dataMap() | bitpos);

      final K key = node.getKey(0);

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

      result = prime * result + key1.hashCode();
      result = prime * result + key2.hashCode();
      result = prime * result + key3.hashCode();
      result = prime * result + key4.hashCode();

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
      Set4To3Node_5Bits_Spec0To8<?> that = (Set4To3Node_5Bits_Spec0To8<?>) other;

      if (nodeMap() != that.nodeMap()) {
        return false;
      }
      if (dataMap() != that.dataMap()) {
        return false;
      }

      if (!(key1.equals(that.key1))) {
        return false;
      }
      if (!(key2.equals(that.key2))) {
        return false;
      }
      if (!(key3.equals(that.key3))) {
        return false;
      }
      if (!(key4.equals(that.key4))) {
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

  private static final class Set4To4Node_5Bits_Spec0To8<K> extends CompactMixedSetNode<K> {

    private final K key1;
    private final K key2;
    private final K key3;
    private final K key4;
    private final CompactSetNode<K> node1;
    private final CompactSetNode<K> node2;
    private final CompactSetNode<K> node3;
    private final CompactSetNode<K> node4;

    Set4To4Node_5Bits_Spec0To8(final AtomicReference<Thread> mutator, final int nodeMap,
        final int dataMap, final K key1, final K key2, final K key3, final K key4,
        final CompactSetNode<K> node1, final CompactSetNode<K> node2, final CompactSetNode<K> node3,
        final CompactSetNode<K> node4) {
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
    CompactSetNode<K> getNode(int index) {
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
    K getKey(int index) {
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
    CompactSetNode<K> copyAndInsertValue(AtomicReference<Thread> mutator, final int bitpos,
        final K key) {
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
    CompactSetNode<K> copyAndRemoveValue(AtomicReference<Thread> mutator, final int bitpos) {
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
    CompactSetNode<K> copyAndSetNode(AtomicReference<Thread> mutator, final int bitpos,
        CompactSetNode<K> node) {
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
    CompactSetNode<K> copyAndMigrateFromInlineToNode(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetNode<K> node) {
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
    CompactSetNode<K> copyAndMigrateFromNodeToInline(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetNode<K> node) {
      final int bitIndex = nodeIndex(bitpos);
      final int valIndex = dataIndex(bitpos);

      final int nodeMap = (int) (this.nodeMap() ^ bitpos);
      final int dataMap = (int) (this.dataMap() | bitpos);

      final K key = node.getKey(0);

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

      result = prime * result + key1.hashCode();
      result = prime * result + key2.hashCode();
      result = prime * result + key3.hashCode();
      result = prime * result + key4.hashCode();

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
      Set4To4Node_5Bits_Spec0To8<?> that = (Set4To4Node_5Bits_Spec0To8<?>) other;

      if (nodeMap() != that.nodeMap()) {
        return false;
      }
      if (dataMap() != that.dataMap()) {
        return false;
      }

      if (!(key1.equals(that.key1))) {
        return false;
      }
      if (!(key2.equals(that.key2))) {
        return false;
      }
      if (!(key3.equals(that.key3))) {
        return false;
      }
      if (!(key4.equals(that.key4))) {
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

  private static final class Set5To0Node_5Bits_Spec0To8<K> extends CompactValuesOnlySetNode<K> {

    private final K key1;
    private final K key2;
    private final K key3;
    private final K key4;
    private final K key5;

    Set5To0Node_5Bits_Spec0To8(final AtomicReference<Thread> mutator, final int nodeMap,
        final int dataMap, final K key1, final K key2, final K key3, final K key4, final K key5) {
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
    CompactSetNode<K> getNode(int index) {
      throw new IllegalStateException("Index out of range.");
    }

    @Override
    K getKey(int index) {
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
    CompactSetNode<K> copyAndInsertValue(AtomicReference<Thread> mutator, final int bitpos,
        final K key) {
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
    CompactSetNode<K> copyAndRemoveValue(AtomicReference<Thread> mutator, final int bitpos) {
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
    CompactSetNode<K> copyAndSetNode(AtomicReference<Thread> mutator, final int bitpos,
        CompactSetNode<K> node) {
      throw new IllegalStateException("Index out of range.");
    }

    @Override
    CompactSetNode<K> copyAndMigrateFromInlineToNode(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetNode<K> node) {
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
    CompactSetNode<K> copyAndMigrateFromNodeToInline(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetNode<K> node) {
      throw new IllegalStateException("Index out of range.");
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;

      result = prime * result + ((int) nodeMap());
      result = prime * result + ((int) dataMap());

      result = prime * result + key1.hashCode();
      result = prime * result + key2.hashCode();
      result = prime * result + key3.hashCode();
      result = prime * result + key4.hashCode();
      result = prime * result + key5.hashCode();

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
      Set5To0Node_5Bits_Spec0To8<?> that = (Set5To0Node_5Bits_Spec0To8<?>) other;

      if (nodeMap() != that.nodeMap()) {
        return false;
      }
      if (dataMap() != that.dataMap()) {
        return false;
      }

      if (!(key1.equals(that.key1))) {
        return false;
      }
      if (!(key2.equals(that.key2))) {
        return false;
      }
      if (!(key3.equals(that.key3))) {
        return false;
      }
      if (!(key4.equals(that.key4))) {
        return false;
      }
      if (!(key5.equals(that.key5))) {
        return false;
      }

      return true;
    }

  }

  private static final class Set5To1Node_5Bits_Spec0To8<K> extends CompactMixedSetNode<K> {

    private final K key1;
    private final K key2;
    private final K key3;
    private final K key4;
    private final K key5;
    private final CompactSetNode<K> node1;

    Set5To1Node_5Bits_Spec0To8(final AtomicReference<Thread> mutator, final int nodeMap,
        final int dataMap, final K key1, final K key2, final K key3, final K key4, final K key5,
        final CompactSetNode<K> node1) {
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
    CompactSetNode<K> getNode(int index) {
      switch (index) {
        case 0:
          return node1;
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    K getKey(int index) {
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
    CompactSetNode<K> copyAndInsertValue(AtomicReference<Thread> mutator, final int bitpos,
        final K key) {
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
    CompactSetNode<K> copyAndRemoveValue(AtomicReference<Thread> mutator, final int bitpos) {
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
    CompactSetNode<K> copyAndSetNode(AtomicReference<Thread> mutator, final int bitpos,
        CompactSetNode<K> node) {
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
    CompactSetNode<K> copyAndMigrateFromInlineToNode(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetNode<K> node) {
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
    CompactSetNode<K> copyAndMigrateFromNodeToInline(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetNode<K> node) {
      final int bitIndex = nodeIndex(bitpos);
      final int valIndex = dataIndex(bitpos);

      final int nodeMap = (int) (this.nodeMap() ^ bitpos);
      final int dataMap = (int) (this.dataMap() | bitpos);

      final K key = node.getKey(0);

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

      result = prime * result + key1.hashCode();
      result = prime * result + key2.hashCode();
      result = prime * result + key3.hashCode();
      result = prime * result + key4.hashCode();
      result = prime * result + key5.hashCode();

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
      Set5To1Node_5Bits_Spec0To8<?> that = (Set5To1Node_5Bits_Spec0To8<?>) other;

      if (nodeMap() != that.nodeMap()) {
        return false;
      }
      if (dataMap() != that.dataMap()) {
        return false;
      }

      if (!(key1.equals(that.key1))) {
        return false;
      }
      if (!(key2.equals(that.key2))) {
        return false;
      }
      if (!(key3.equals(that.key3))) {
        return false;
      }
      if (!(key4.equals(that.key4))) {
        return false;
      }
      if (!(key5.equals(that.key5))) {
        return false;
      }
      if (!(node1.equals(that.node1))) {
        return false;
      }

      return true;
    }

  }

  private static final class Set5To2Node_5Bits_Spec0To8<K> extends CompactMixedSetNode<K> {

    private final K key1;
    private final K key2;
    private final K key3;
    private final K key4;
    private final K key5;
    private final CompactSetNode<K> node1;
    private final CompactSetNode<K> node2;

    Set5To2Node_5Bits_Spec0To8(final AtomicReference<Thread> mutator, final int nodeMap,
        final int dataMap, final K key1, final K key2, final K key3, final K key4, final K key5,
        final CompactSetNode<K> node1, final CompactSetNode<K> node2) {
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
    CompactSetNode<K> getNode(int index) {
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
    K getKey(int index) {
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
    CompactSetNode<K> copyAndInsertValue(AtomicReference<Thread> mutator, final int bitpos,
        final K key) {
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
    CompactSetNode<K> copyAndRemoveValue(AtomicReference<Thread> mutator, final int bitpos) {
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
    CompactSetNode<K> copyAndSetNode(AtomicReference<Thread> mutator, final int bitpos,
        CompactSetNode<K> node) {
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
    CompactSetNode<K> copyAndMigrateFromInlineToNode(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetNode<K> node) {
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
    CompactSetNode<K> copyAndMigrateFromNodeToInline(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetNode<K> node) {
      final int bitIndex = nodeIndex(bitpos);
      final int valIndex = dataIndex(bitpos);

      final int nodeMap = (int) (this.nodeMap() ^ bitpos);
      final int dataMap = (int) (this.dataMap() | bitpos);

      final K key = node.getKey(0);

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

      result = prime * result + key1.hashCode();
      result = prime * result + key2.hashCode();
      result = prime * result + key3.hashCode();
      result = prime * result + key4.hashCode();
      result = prime * result + key5.hashCode();

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
      Set5To2Node_5Bits_Spec0To8<?> that = (Set5To2Node_5Bits_Spec0To8<?>) other;

      if (nodeMap() != that.nodeMap()) {
        return false;
      }
      if (dataMap() != that.dataMap()) {
        return false;
      }

      if (!(key1.equals(that.key1))) {
        return false;
      }
      if (!(key2.equals(that.key2))) {
        return false;
      }
      if (!(key3.equals(that.key3))) {
        return false;
      }
      if (!(key4.equals(that.key4))) {
        return false;
      }
      if (!(key5.equals(that.key5))) {
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

  private static final class Set5To3Node_5Bits_Spec0To8<K> extends CompactMixedSetNode<K> {

    private final K key1;
    private final K key2;
    private final K key3;
    private final K key4;
    private final K key5;
    private final CompactSetNode<K> node1;
    private final CompactSetNode<K> node2;
    private final CompactSetNode<K> node3;

    Set5To3Node_5Bits_Spec0To8(final AtomicReference<Thread> mutator, final int nodeMap,
        final int dataMap, final K key1, final K key2, final K key3, final K key4, final K key5,
        final CompactSetNode<K> node1, final CompactSetNode<K> node2,
        final CompactSetNode<K> node3) {
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
    CompactSetNode<K> getNode(int index) {
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
    K getKey(int index) {
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
    CompactSetNode<K> copyAndInsertValue(AtomicReference<Thread> mutator, final int bitpos,
        final K key) {
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
    CompactSetNode<K> copyAndRemoveValue(AtomicReference<Thread> mutator, final int bitpos) {
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
    CompactSetNode<K> copyAndSetNode(AtomicReference<Thread> mutator, final int bitpos,
        CompactSetNode<K> node) {
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
    CompactSetNode<K> copyAndMigrateFromInlineToNode(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetNode<K> node) {
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
    CompactSetNode<K> copyAndMigrateFromNodeToInline(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetNode<K> node) {
      final int bitIndex = nodeIndex(bitpos);
      final int valIndex = dataIndex(bitpos);

      final int nodeMap = (int) (this.nodeMap() ^ bitpos);
      final int dataMap = (int) (this.dataMap() | bitpos);

      final K key = node.getKey(0);

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

      result = prime * result + key1.hashCode();
      result = prime * result + key2.hashCode();
      result = prime * result + key3.hashCode();
      result = prime * result + key4.hashCode();
      result = prime * result + key5.hashCode();

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
      Set5To3Node_5Bits_Spec0To8<?> that = (Set5To3Node_5Bits_Spec0To8<?>) other;

      if (nodeMap() != that.nodeMap()) {
        return false;
      }
      if (dataMap() != that.dataMap()) {
        return false;
      }

      if (!(key1.equals(that.key1))) {
        return false;
      }
      if (!(key2.equals(that.key2))) {
        return false;
      }
      if (!(key3.equals(that.key3))) {
        return false;
      }
      if (!(key4.equals(that.key4))) {
        return false;
      }
      if (!(key5.equals(that.key5))) {
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

  private static final class Set6To0Node_5Bits_Spec0To8<K> extends CompactValuesOnlySetNode<K> {

    private final K key1;
    private final K key2;
    private final K key3;
    private final K key4;
    private final K key5;
    private final K key6;

    Set6To0Node_5Bits_Spec0To8(final AtomicReference<Thread> mutator, final int nodeMap,
        final int dataMap, final K key1, final K key2, final K key3, final K key4, final K key5,
        final K key6) {
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
    CompactSetNode<K> getNode(int index) {
      throw new IllegalStateException("Index out of range.");
    }

    @Override
    K getKey(int index) {
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
    CompactSetNode<K> copyAndInsertValue(AtomicReference<Thread> mutator, final int bitpos,
        final K key) {
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
    CompactSetNode<K> copyAndRemoveValue(AtomicReference<Thread> mutator, final int bitpos) {
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
    CompactSetNode<K> copyAndSetNode(AtomicReference<Thread> mutator, final int bitpos,
        CompactSetNode<K> node) {
      throw new IllegalStateException("Index out of range.");
    }

    @Override
    CompactSetNode<K> copyAndMigrateFromInlineToNode(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetNode<K> node) {
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
    CompactSetNode<K> copyAndMigrateFromNodeToInline(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetNode<K> node) {
      throw new IllegalStateException("Index out of range.");
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;

      result = prime * result + ((int) nodeMap());
      result = prime * result + ((int) dataMap());

      result = prime * result + key1.hashCode();
      result = prime * result + key2.hashCode();
      result = prime * result + key3.hashCode();
      result = prime * result + key4.hashCode();
      result = prime * result + key5.hashCode();
      result = prime * result + key6.hashCode();

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
      Set6To0Node_5Bits_Spec0To8<?> that = (Set6To0Node_5Bits_Spec0To8<?>) other;

      if (nodeMap() != that.nodeMap()) {
        return false;
      }
      if (dataMap() != that.dataMap()) {
        return false;
      }

      if (!(key1.equals(that.key1))) {
        return false;
      }
      if (!(key2.equals(that.key2))) {
        return false;
      }
      if (!(key3.equals(that.key3))) {
        return false;
      }
      if (!(key4.equals(that.key4))) {
        return false;
      }
      if (!(key5.equals(that.key5))) {
        return false;
      }
      if (!(key6.equals(that.key6))) {
        return false;
      }

      return true;
    }

  }

  private static final class Set6To1Node_5Bits_Spec0To8<K> extends CompactMixedSetNode<K> {

    private final K key1;
    private final K key2;
    private final K key3;
    private final K key4;
    private final K key5;
    private final K key6;
    private final CompactSetNode<K> node1;

    Set6To1Node_5Bits_Spec0To8(final AtomicReference<Thread> mutator, final int nodeMap,
        final int dataMap, final K key1, final K key2, final K key3, final K key4, final K key5,
        final K key6, final CompactSetNode<K> node1) {
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
    CompactSetNode<K> getNode(int index) {
      switch (index) {
        case 0:
          return node1;
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    K getKey(int index) {
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
    CompactSetNode<K> copyAndInsertValue(AtomicReference<Thread> mutator, final int bitpos,
        final K key) {
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
    CompactSetNode<K> copyAndRemoveValue(AtomicReference<Thread> mutator, final int bitpos) {
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
    CompactSetNode<K> copyAndSetNode(AtomicReference<Thread> mutator, final int bitpos,
        CompactSetNode<K> node) {
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
    CompactSetNode<K> copyAndMigrateFromInlineToNode(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetNode<K> node) {
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
    CompactSetNode<K> copyAndMigrateFromNodeToInline(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetNode<K> node) {
      final int bitIndex = nodeIndex(bitpos);
      final int valIndex = dataIndex(bitpos);

      final int nodeMap = (int) (this.nodeMap() ^ bitpos);
      final int dataMap = (int) (this.dataMap() | bitpos);

      final K key = node.getKey(0);

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

      result = prime * result + key1.hashCode();
      result = prime * result + key2.hashCode();
      result = prime * result + key3.hashCode();
      result = prime * result + key4.hashCode();
      result = prime * result + key5.hashCode();
      result = prime * result + key6.hashCode();

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
      Set6To1Node_5Bits_Spec0To8<?> that = (Set6To1Node_5Bits_Spec0To8<?>) other;

      if (nodeMap() != that.nodeMap()) {
        return false;
      }
      if (dataMap() != that.dataMap()) {
        return false;
      }

      if (!(key1.equals(that.key1))) {
        return false;
      }
      if (!(key2.equals(that.key2))) {
        return false;
      }
      if (!(key3.equals(that.key3))) {
        return false;
      }
      if (!(key4.equals(that.key4))) {
        return false;
      }
      if (!(key5.equals(that.key5))) {
        return false;
      }
      if (!(key6.equals(that.key6))) {
        return false;
      }
      if (!(node1.equals(that.node1))) {
        return false;
      }

      return true;
    }

  }

  private static final class Set6To2Node_5Bits_Spec0To8<K> extends CompactMixedSetNode<K> {

    private final K key1;
    private final K key2;
    private final K key3;
    private final K key4;
    private final K key5;
    private final K key6;
    private final CompactSetNode<K> node1;
    private final CompactSetNode<K> node2;

    Set6To2Node_5Bits_Spec0To8(final AtomicReference<Thread> mutator, final int nodeMap,
        final int dataMap, final K key1, final K key2, final K key3, final K key4, final K key5,
        final K key6, final CompactSetNode<K> node1, final CompactSetNode<K> node2) {
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
    CompactSetNode<K> getNode(int index) {
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
    K getKey(int index) {
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
    CompactSetNode<K> copyAndInsertValue(AtomicReference<Thread> mutator, final int bitpos,
        final K key) {
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
    CompactSetNode<K> copyAndRemoveValue(AtomicReference<Thread> mutator, final int bitpos) {
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
    CompactSetNode<K> copyAndSetNode(AtomicReference<Thread> mutator, final int bitpos,
        CompactSetNode<K> node) {
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
    CompactSetNode<K> copyAndMigrateFromInlineToNode(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetNode<K> node) {
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
    CompactSetNode<K> copyAndMigrateFromNodeToInline(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetNode<K> node) {
      final int bitIndex = nodeIndex(bitpos);
      final int valIndex = dataIndex(bitpos);

      final int nodeMap = (int) (this.nodeMap() ^ bitpos);
      final int dataMap = (int) (this.dataMap() | bitpos);

      final K key = node.getKey(0);

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

      result = prime * result + key1.hashCode();
      result = prime * result + key2.hashCode();
      result = prime * result + key3.hashCode();
      result = prime * result + key4.hashCode();
      result = prime * result + key5.hashCode();
      result = prime * result + key6.hashCode();

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
      Set6To2Node_5Bits_Spec0To8<?> that = (Set6To2Node_5Bits_Spec0To8<?>) other;

      if (nodeMap() != that.nodeMap()) {
        return false;
      }
      if (dataMap() != that.dataMap()) {
        return false;
      }

      if (!(key1.equals(that.key1))) {
        return false;
      }
      if (!(key2.equals(that.key2))) {
        return false;
      }
      if (!(key3.equals(that.key3))) {
        return false;
      }
      if (!(key4.equals(that.key4))) {
        return false;
      }
      if (!(key5.equals(that.key5))) {
        return false;
      }
      if (!(key6.equals(that.key6))) {
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

  private static final class Set7To0Node_5Bits_Spec0To8<K> extends CompactValuesOnlySetNode<K> {

    private final K key1;
    private final K key2;
    private final K key3;
    private final K key4;
    private final K key5;
    private final K key6;
    private final K key7;

    Set7To0Node_5Bits_Spec0To8(final AtomicReference<Thread> mutator, final int nodeMap,
        final int dataMap, final K key1, final K key2, final K key3, final K key4, final K key5,
        final K key6, final K key7) {
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
    CompactSetNode<K> getNode(int index) {
      throw new IllegalStateException("Index out of range.");
    }

    @Override
    K getKey(int index) {
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
    CompactSetNode<K> copyAndInsertValue(AtomicReference<Thread> mutator, final int bitpos,
        final K key) {
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
    CompactSetNode<K> copyAndRemoveValue(AtomicReference<Thread> mutator, final int bitpos) {
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
    CompactSetNode<K> copyAndSetNode(AtomicReference<Thread> mutator, final int bitpos,
        CompactSetNode<K> node) {
      throw new IllegalStateException("Index out of range.");
    }

    @Override
    CompactSetNode<K> copyAndMigrateFromInlineToNode(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetNode<K> node) {
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
    CompactSetNode<K> copyAndMigrateFromNodeToInline(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetNode<K> node) {
      throw new IllegalStateException("Index out of range.");
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;

      result = prime * result + ((int) nodeMap());
      result = prime * result + ((int) dataMap());

      result = prime * result + key1.hashCode();
      result = prime * result + key2.hashCode();
      result = prime * result + key3.hashCode();
      result = prime * result + key4.hashCode();
      result = prime * result + key5.hashCode();
      result = prime * result + key6.hashCode();
      result = prime * result + key7.hashCode();

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
      Set7To0Node_5Bits_Spec0To8<?> that = (Set7To0Node_5Bits_Spec0To8<?>) other;

      if (nodeMap() != that.nodeMap()) {
        return false;
      }
      if (dataMap() != that.dataMap()) {
        return false;
      }

      if (!(key1.equals(that.key1))) {
        return false;
      }
      if (!(key2.equals(that.key2))) {
        return false;
      }
      if (!(key3.equals(that.key3))) {
        return false;
      }
      if (!(key4.equals(that.key4))) {
        return false;
      }
      if (!(key5.equals(that.key5))) {
        return false;
      }
      if (!(key6.equals(that.key6))) {
        return false;
      }
      if (!(key7.equals(that.key7))) {
        return false;
      }

      return true;
    }

  }

  private static final class Set7To1Node_5Bits_Spec0To8<K> extends CompactMixedSetNode<K> {

    private final K key1;
    private final K key2;
    private final K key3;
    private final K key4;
    private final K key5;
    private final K key6;
    private final K key7;
    private final CompactSetNode<K> node1;

    Set7To1Node_5Bits_Spec0To8(final AtomicReference<Thread> mutator, final int nodeMap,
        final int dataMap, final K key1, final K key2, final K key3, final K key4, final K key5,
        final K key6, final K key7, final CompactSetNode<K> node1) {
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
    CompactSetNode<K> getNode(int index) {
      switch (index) {
        case 0:
          return node1;
        default:
          throw new IllegalStateException("Index out of range.");
      }
    }

    @Override
    K getKey(int index) {
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
    CompactSetNode<K> copyAndInsertValue(AtomicReference<Thread> mutator, final int bitpos,
        final K key) {
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
    CompactSetNode<K> copyAndRemoveValue(AtomicReference<Thread> mutator, final int bitpos) {
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
    CompactSetNode<K> copyAndSetNode(AtomicReference<Thread> mutator, final int bitpos,
        CompactSetNode<K> node) {
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
    CompactSetNode<K> copyAndMigrateFromInlineToNode(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetNode<K> node) {
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
    CompactSetNode<K> copyAndMigrateFromNodeToInline(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetNode<K> node) {
      final int bitIndex = nodeIndex(bitpos);
      final int valIndex = dataIndex(bitpos);

      final int nodeMap = (int) (this.nodeMap() ^ bitpos);
      final int dataMap = (int) (this.dataMap() | bitpos);

      final K key = node.getKey(0);

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

      result = prime * result + key1.hashCode();
      result = prime * result + key2.hashCode();
      result = prime * result + key3.hashCode();
      result = prime * result + key4.hashCode();
      result = prime * result + key5.hashCode();
      result = prime * result + key6.hashCode();
      result = prime * result + key7.hashCode();

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
      Set7To1Node_5Bits_Spec0To8<?> that = (Set7To1Node_5Bits_Spec0To8<?>) other;

      if (nodeMap() != that.nodeMap()) {
        return false;
      }
      if (dataMap() != that.dataMap()) {
        return false;
      }

      if (!(key1.equals(that.key1))) {
        return false;
      }
      if (!(key2.equals(that.key2))) {
        return false;
      }
      if (!(key3.equals(that.key3))) {
        return false;
      }
      if (!(key4.equals(that.key4))) {
        return false;
      }
      if (!(key5.equals(that.key5))) {
        return false;
      }
      if (!(key6.equals(that.key6))) {
        return false;
      }
      if (!(key7.equals(that.key7))) {
        return false;
      }
      if (!(node1.equals(that.node1))) {
        return false;
      }

      return true;
    }

  }

  private static final class Set8To0Node_5Bits_Spec0To8<K> extends CompactValuesOnlySetNode<K> {

    private final K key1;
    private final K key2;
    private final K key3;
    private final K key4;
    private final K key5;
    private final K key6;
    private final K key7;
    private final K key8;

    Set8To0Node_5Bits_Spec0To8(final AtomicReference<Thread> mutator, final int nodeMap,
        final int dataMap, final K key1, final K key2, final K key3, final K key4, final K key5,
        final K key6, final K key7, final K key8) {
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
    CompactSetNode<K> getNode(int index) {
      throw new IllegalStateException("Index out of range.");
    }

    @Override
    K getKey(int index) {
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
    CompactSetNode<K> copyAndInsertValue(AtomicReference<Thread> mutator, final int bitpos,
        final K key) {
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
    CompactSetNode<K> copyAndRemoveValue(AtomicReference<Thread> mutator, final int bitpos) {
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
    CompactSetNode<K> copyAndSetNode(AtomicReference<Thread> mutator, final int bitpos,
        CompactSetNode<K> node) {
      throw new IllegalStateException("Index out of range.");
    }

    @Override
    CompactSetNode<K> copyAndMigrateFromInlineToNode(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetNode<K> node) {
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
    CompactSetNode<K> copyAndMigrateFromNodeToInline(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetNode<K> node) {
      throw new IllegalStateException("Index out of range.");
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;

      result = prime * result + ((int) nodeMap());
      result = prime * result + ((int) dataMap());

      result = prime * result + key1.hashCode();
      result = prime * result + key2.hashCode();
      result = prime * result + key3.hashCode();
      result = prime * result + key4.hashCode();
      result = prime * result + key5.hashCode();
      result = prime * result + key6.hashCode();
      result = prime * result + key7.hashCode();
      result = prime * result + key8.hashCode();

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
      Set8To0Node_5Bits_Spec0To8<?> that = (Set8To0Node_5Bits_Spec0To8<?>) other;

      if (nodeMap() != that.nodeMap()) {
        return false;
      }
      if (dataMap() != that.dataMap()) {
        return false;
      }

      if (!(key1.equals(that.key1))) {
        return false;
      }
      if (!(key2.equals(that.key2))) {
        return false;
      }
      if (!(key3.equals(that.key3))) {
        return false;
      }
      if (!(key4.equals(that.key4))) {
        return false;
      }
      if (!(key5.equals(that.key5))) {
        return false;
      }
      if (!(key6.equals(that.key6))) {
        return false;
      }
      if (!(key7.equals(that.key7))) {
        return false;
      }
      if (!(key8.equals(that.key8))) {
        return false;
      }

      return true;
    }

  }

}
