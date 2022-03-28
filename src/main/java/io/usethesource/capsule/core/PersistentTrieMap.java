/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.capsule.core;

import java.text.DecimalFormat;
import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiFunction;

import io.usethesource.capsule.core.trie.ArrayView;
import io.usethesource.capsule.core.trie.MapNode;
import io.usethesource.capsule.core.trie.MapNodeResult;

import static io.usethesource.capsule.util.collection.AbstractSpecialisedImmutableMap.entryOf;

public class PersistentTrieMap<K, V> implements io.usethesource.capsule.Map.Immutable<K, V>,
    java.io.Serializable {

  private static final long serialVersionUID = 42L;

  private static final CompactMapNode EMPTY_NODE = new BitmapIndexedMapNode<>(null, (0), (0),
      new Object[]{});

  private static final PersistentTrieMap EMPTY_MAP = new PersistentTrieMap(EMPTY_NODE, 0, 0);

  private static final boolean DEBUG = false;

  private final AbstractMapNode<K, V> rootNode;
  private final int cachedHashCode;
  private final int cachedSize;

  PersistentTrieMap(AbstractMapNode<K, V> rootNode, int cachedHashCode, int cachedSize) {
    this.rootNode = rootNode;
    this.cachedHashCode = cachedHashCode;
    this.cachedSize = cachedSize;
    if (DEBUG) {
      assert checkHashCodeAndSize(cachedHashCode, cachedSize);
    }
  }

  public static final <K, V> io.usethesource.capsule.Map.Immutable<K, V> of() {
    return PersistentTrieMap.EMPTY_MAP;
  }

  public static final <K, V> io.usethesource.capsule.Map.Immutable<K, V> of(
      Object... keyValuePairs) {
    if (keyValuePairs.length % 2 != 0) {
      throw new IllegalArgumentException("Length of argument list is uneven: no key/value pairs.");
    }

    io.usethesource.capsule.Map.Immutable<K, V> result = PersistentTrieMap.EMPTY_MAP;

    for (int i = 0; i < keyValuePairs.length; i += 2) {
      final K key = (K) keyValuePairs[i];
      final V val = (V) keyValuePairs[i + 1];

      result = result.__put(key, val);
    }

    return result;
  }

  public static final <K, V> io.usethesource.capsule.Map.Transient<K, V> transientOf() {
    return PersistentTrieMap.EMPTY_MAP.asTransient();
  }

  public static final <K, V> io.usethesource.capsule.Map.Transient<K, V> transientOf(
      Object... keyValuePairs) {
    if (keyValuePairs.length % 2 != 0) {
      throw new IllegalArgumentException("Length of argument list is uneven: no key/value pairs.");
    }

    final io.usethesource.capsule.Map.Transient<K, V> result = PersistentTrieMap.EMPTY_MAP
        .asTransient();

    for (int i = 0; i < keyValuePairs.length; i += 2) {
      final K key = (K) keyValuePairs[i];
      final V val = (V) keyValuePairs[i + 1];

      result.__put(key, val);
    }

    return result;
  }

  private boolean checkHashCodeAndSize(final int targetHash, final int targetSize) {
    int hash = 0;
    int size = 0;

    for (Iterator<Map.Entry<K, V>> it = entryIterator(); it.hasNext(); ) {
      final Map.Entry<K, V> entry = it.next();
      final K key = entry.getKey();
      final V val = entry.getValue();

      hash += key.hashCode() ^ val.hashCode();
      size += 1;
    }

    return hash == targetHash && size == targetSize;
  }

  public static final int transformHashCode(final int hash) {
    return hash;
  }

  @Override
  public boolean containsKey(final Object o) {
    try {
      final K key = (K) o;
      return rootNode.containsKey(key, transformHashCode(key.hashCode()), 0);
    } catch (ClassCastException unused) {
      return false;
    }
  }

  @Override
  public boolean containsValue(final Object o) {
    for (Iterator<V> iterator = valueIterator(); iterator.hasNext(); ) {
      if (Objects.equals(iterator.next(), o)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public V get(final Object o) {
    try {
      final K key = (K) o;
      final Optional<V> result = rootNode.findByKey(key, transformHashCode(key.hashCode()), 0);

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
  public io.usethesource.capsule.Map.Immutable<K, V> __put(final K key, final V val) {
    final int keyHash = key.hashCode();
    final MapNodeResult<K, V> details = MapNodeResult.unchanged();

    final AbstractMapNode<K, V> newRootNode = rootNode.updated(null, key, val,
        transformHashCode(keyHash), 0, details);

    if (details.isModified()) {
      if (details.hasReplacedValue()) {
        final int valHashOld = details.getReplacedValue().hashCode();
        final int valHashNew = val.hashCode();

        return new PersistentTrieMap<K, V>(newRootNode,
            cachedHashCode + ((keyHash ^ valHashNew)) - ((keyHash ^ valHashOld)), cachedSize);
      }

      final int valHash = val.hashCode();
      return new PersistentTrieMap<K, V>(newRootNode, cachedHashCode + ((keyHash ^ valHash)),
          cachedSize + 1);
    }

    return this;
  }

  @Override
  public io.usethesource.capsule.Map.Immutable<K, V> __putAll(
      final Map<? extends K, ? extends V> map) {
    final io.usethesource.capsule.Map.Transient<K, V> tmpTransient = this.asTransient();
    tmpTransient.__putAll(map);
    return tmpTransient.freeze();
  }

  @Override
  public io.usethesource.capsule.Map.Immutable<K, V> __remove(final K key) {
    final int keyHash = key.hashCode();
    final MapNodeResult<K, V> details = MapNodeResult.unchanged();

    final AbstractMapNode<K, V> newRootNode = rootNode.removed(null, key,
        transformHashCode(keyHash), 0, details);

    if (details.isModified()) {
      assert details.hasReplacedValue();
      final int valHash = details.getReplacedValue().hashCode();
      return new PersistentTrieMap<K, V>(newRootNode, cachedHashCode - ((keyHash ^ valHash)),
          cachedSize - 1);
    }

    return this;
  }

  @Override
  public V put(final K key, final V val) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void putAll(final Map<? extends K, ? extends V> m) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void clear() {
    throw new UnsupportedOperationException();
  }

  @Override
  public V remove(final Object key) {
    throw new UnsupportedOperationException();
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
  public Iterator<K> keyIterator() {
    return new MapKeyIterator<>(rootNode);
  }

  @Override
  public Iterator<V> valueIterator() {
    return new MapValueIterator<>(rootNode);
  }

  @Override
  public Iterator<Map.Entry<K, V>> entryIterator() {
    return new MapEntryIterator<>(rootNode);
  }

  @Override
  public Set<K> keySet() {
    Set<K> keySet = null;

    if (keySet == null) {
      keySet = new AbstractSet<K>() {
        @Override
        public Iterator<K> iterator() {
          return PersistentTrieMap.this.keyIterator();
        }

        @Override
        public int size() {
          return PersistentTrieMap.this.size();
        }

        @Override
        public boolean isEmpty() {
          return PersistentTrieMap.this.isEmpty();
        }

        @Override
        public void clear() {
          PersistentTrieMap.this.clear();
        }

        @Override
        public boolean contains(Object k) {
          return PersistentTrieMap.this.containsKey(k);
        }
      };
    }

    return keySet;
  }

  @Override
  public Collection<V> values() {
    Collection<V> values = null;

    if (values == null) {
      values = new AbstractCollection<V>() {
        @Override
        public Iterator<V> iterator() {
          return PersistentTrieMap.this.valueIterator();
        }

        @Override
        public int size() {
          return PersistentTrieMap.this.size();
        }

        @Override
        public boolean isEmpty() {
          return PersistentTrieMap.this.isEmpty();
        }

        @Override
        public void clear() {
          PersistentTrieMap.this.clear();
        }

        @Override
        public boolean contains(Object v) {
          return PersistentTrieMap.this.containsValue(v);
        }
      };
    }

    return values;
  }

  @Override
  public Set<java.util.Map.Entry<K, V>> entrySet() {
    Set<java.util.Map.Entry<K, V>> entrySet = null;

    if (entrySet == null) {
      entrySet = new AbstractSet<java.util.Map.Entry<K, V>>() {
        @Override
        public Iterator<java.util.Map.Entry<K, V>> iterator() {
          return new Iterator<Map.Entry<K, V>>() {
            private final Iterator<Map.Entry<K, V>> i = entryIterator();

            @Override
            public boolean hasNext() {
              return i.hasNext();
            }

            @Override
            public Map.Entry<K, V> next() {
              return i.next();
            }

            @Override
            public void remove() {
              i.remove();
            }
          };
        }

        @Override
        public int size() {
          return PersistentTrieMap.this.size();
        }

        @Override
        public boolean isEmpty() {
          return PersistentTrieMap.this.isEmpty();
        }

        @Override
        public void clear() {
          PersistentTrieMap.this.clear();
        }

        @Override
        public boolean contains(Object k) {
          return PersistentTrieMap.this.containsKey(k);
        }
      };
    }

    return entrySet;
  }

  @Override
  public boolean equals(final Object other) {
    if (other == this) {
      return true;
    }
    if (other == null) {
      return false;
    }

    if (other instanceof PersistentTrieMap) {
      PersistentTrieMap<?, ?> that = (PersistentTrieMap<?, ?>) other;

      if (this.cachedSize != that.cachedSize) {
        return false;
      }

      if (this.cachedHashCode != that.cachedHashCode) {
        return false;
      }

      return rootNode.equals(that.rootNode);
    } else if (other instanceof Map) {
      Map that = (Map) other;

      if (this.size() != that.size()) {
        return false;
      }

      for (
          Iterator<Map.Entry> it = that.entrySet().iterator(); it.hasNext(); ) {
        Map.Entry entry = it.next();

        try {
          final K key = (K) entry.getKey();
          final Optional<V> result = rootNode
              .findByKey(key, transformHashCode(key.hashCode()), 0);

          if (!result.isPresent()) {
            return false;
          } else {
            final V val = (V) entry.getValue();

            if (!Objects.equals(result.get(), val)) {
              return false;
            }
          }
        } catch (ClassCastException unused) {
          return false;
        }
      }

      return true;
    }

    return false;
  }

  @Override
  public int hashCode() {
    return cachedHashCode;
  }

  @Override
  public String toString() {
    String body = entrySet().stream()
        .map(entry -> String.format("%s: %s", entry.getKey(), entry.getValue()))
        .reduce((left, right) -> String.join(", ", left, right))
        .orElse("");
    return String.format("{%s}", body);
  }

  @Override
  public boolean isTransientSupported() {
    return true;
  }

  @Override
  public io.usethesource.capsule.Map.Transient<K, V> asTransient() {
    return new TransientTrieMap<K, V>(this);
  }

  /*
   * For analysis purposes only.
   */
  protected AbstractMapNode<K, V> getRootNode() {
    return rootNode;
  }

  /*
   * For analysis purposes only.
   */
  protected Iterator<AbstractMapNode<K, V>> nodeIterator() {
    return new TrieMapNodeIterator<>(rootNode);
  }

  /*
   * For analysis purposes only.
   */
  protected int getNodeCount() {
    final Iterator<AbstractMapNode<K, V>> it = nodeIterator();
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
    final Iterator<AbstractMapNode<K, V>> it = nodeIterator();
    final int[][] sumArityCombinations = new int[33][33];

    while (it.hasNext()) {
      final AbstractMapNode<K, V> node = it.next();
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

  // TODO: support {@code Iterable} interface like AbstractSetNode
  protected static abstract class AbstractMapNode<K, V> implements
      MapNode<K, V, AbstractMapNode<K, V>>,
      java.io.Serializable {

    private static final long serialVersionUID = 42L;

    static final int TUPLE_LENGTH = 2;

    static final <T> boolean isAllowedToEdit(AtomicReference<?> x, AtomicReference<?> y) {
      return x != null && y != null && (x == y || x.get() == y.get());
    }

    @Override
    public <T> ArrayView<T> dataArray(final int category, final int component) {
      if (category == 0) {
        switch (component) {
          case 0:
            return categoryArrayView0();
          case 1:
            return categoryArrayView1();
        }
      }
      throw new IllegalArgumentException("Category %i component %i is not supported.");
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

    private <T> ArrayView<T> categoryArrayView1() {
      return new ArrayView<T>() {
        @Override
        public int size() {
          return payloadArity();
        }

        @Override
        public T get(int index) {
          return (T) getValue(index);
        }
      };
    }

    @Override
    public abstract ArrayView<AbstractMapNode<K, V>> nodeArray();

    abstract boolean hasNodes();

    abstract int nodeArity();

    abstract AbstractMapNode<K, V> getNode(final int index);

    @Deprecated
    Iterator<? extends AbstractMapNode<K, V>> nodeIterator() {
      return new Iterator<AbstractMapNode<K, V>>() {

        int nextIndex = 0;
        final int nodeArity = AbstractMapNode.this.nodeArity();

        @Override
        public void remove() {
          throw new UnsupportedOperationException();
        }

        @Override
        public AbstractMapNode<K, V> next() {
          if (!hasNext()) {
            throw new NoSuchElementException();
          }
          return AbstractMapNode.this.getNode(nextIndex++);
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

    abstract V getValue(final int index);

    abstract Map.Entry<K, V> getKeyValueEntry(final int index);

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
      final Iterator<K> it = new MapKeyIterator<>(this);

      int size = 0;
      while (it.hasNext()) {
        size += 1;
        it.next();
      }

      return size;
    }
  }

  protected static abstract class CompactMapNode<K, V> extends AbstractMapNode<K, V> {

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
    abstract CompactMapNode<K, V> getNode(final int index);

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

    abstract CompactMapNode<K, V> copyAndSetValue(final AtomicReference<Thread> mutator,
        final int bitpos, final V val);

    abstract CompactMapNode<K, V> copyAndInsertValue(final AtomicReference<Thread> mutator,
        final int bitpos, final K key, final V val);

    abstract CompactMapNode<K, V> copyAndRemoveValue(final AtomicReference<Thread> mutator,
        final int bitpos);

    abstract CompactMapNode<K, V> copyAndSetNode(final AtomicReference<Thread> mutator,
        final int bitpos, final AbstractMapNode<K, V> node);

    abstract CompactMapNode<K, V> copyAndMigrateFromInlineToNode(
        final AtomicReference<Thread> mutator, final int bitpos, final AbstractMapNode<K, V> node);

    abstract CompactMapNode<K, V> copyAndMigrateFromNodeToInline(
        final AtomicReference<Thread> mutator, final int bitpos, final AbstractMapNode<K, V> node);

    static final <K, V> CompactMapNode<K, V> mergeTwoKeyValPairs(final K key0, final V val0,
        final int keyHash0, final K key1, final V val1, final int keyHash1, final int shift) {
      assert !(key0.equals(key1));

      if (shift >= HASH_CODE_LENGTH) {
        // throw new
        // IllegalStateException("Hash collision not yet fixed.");
        return new HashCollisionMapNode<>(keyHash0, (K[]) new Object[]{key0, key1},
            (V[]) new Object[]{val0, val1});
      }

      final int mask0 = mask(keyHash0, shift);
      final int mask1 = mask(keyHash1, shift);

      if (mask0 != mask1) {
        // both nodes fit on same level
        final int dataMap = bitpos(mask0) | bitpos(mask1);

        if (mask0 < mask1) {
          return nodeOf(null, (0), dataMap, new Object[]{key0, val0, key1, val1});
        } else {
          return nodeOf(null, (0), dataMap, new Object[]{key1, val1, key0, val0});
        }
      } else {
        final CompactMapNode<K, V> node = mergeTwoKeyValPairs(key0, val0, keyHash0, key1, val1,
            keyHash1, shift + BIT_PARTITION_SIZE);
        // values fit on next level

        final int nodeMap = bitpos(mask0);
        return nodeOf(null, nodeMap, (0), new Object[]{node});
      }
    }

    static final <K, V> CompactMapNode<K, V> nodeOf(final AtomicReference<Thread> mutator,
        final int nodeMap, final int dataMap, final Object[] nodes) {
      return new BitmapIndexedMapNode<>(mutator, nodeMap, dataMap, nodes);
    }

    static final <K, V> CompactMapNode<K, V> nodeOf(AtomicReference<Thread> mutator) {
      return EMPTY_NODE;
    }

    static final <K, V> CompactMapNode<K, V> nodeOf(AtomicReference<Thread> mutator,
        final int nodeMap, final int dataMap, final K key, final V val) {
      assert nodeMap == 0;
      return nodeOf(mutator, (0), dataMap, new Object[]{key, val});
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

    CompactMapNode<K, V> nodeAt(final int bitpos) {
      return getNode(nodeIndex(bitpos));
    }

    @Override
    public boolean containsKey(final K key, final int keyHash, final int shift) {
      final int mask = mask(keyHash, shift);
      final int bitpos = bitpos(mask);

      final int dataMap = dataMap();
      if ((dataMap & bitpos) != 0) {
        final int index = index(dataMap, mask, bitpos);
        return Objects.equals(getKey(index), key);
      }

      final int nodeMap = nodeMap();
      if ((nodeMap & bitpos) != 0) {
        final int index = index(nodeMap, mask, bitpos);
        return getNode(index).containsKey(key, keyHash, shift + BIT_PARTITION_SIZE);
      }

      return false;
    }

    @Override
    public Optional<V> findByKey(final K key, final int keyHash, final int shift) {
      final int mask = mask(keyHash, shift);
      final int bitpos = bitpos(mask);

      if ((dataMap() & bitpos) != 0) { // inplace value
        final int index = dataIndex(bitpos);
        if (Objects.equals(getKey(index), key)) {
          final V result = getValue(index);

          return Optional.of(result);
        }

        return Optional.empty();
      }

      if ((nodeMap() & bitpos) != 0) { // node (not value)
        final AbstractMapNode<K, V> subNode = nodeAt(bitpos);

        return subNode.findByKey(key, keyHash, shift + BIT_PARTITION_SIZE);
      }

      return Optional.empty();
    }

    @Override
    public AbstractMapNode<K, V> updated(final AtomicReference<Thread> mutator, final K key, final V val,
                                         final int keyHash, final int shift, final MapNodeResult<K, V> details) {
      final int mask = mask(keyHash, shift);
      final int bitpos = bitpos(mask);

      if ((dataMap() & bitpos) != 0) { // inplace value
        final int dataIndex = dataIndex(bitpos);
        final K currentKey = getKey(dataIndex);

        if (Objects.equals(currentKey, key)) {
          final V currentVal = getValue(dataIndex);

          // update mapping
          details.updated(currentVal);
          return copyAndSetValue(mutator, bitpos, val);
        } else {
          final V currentVal = getValue(dataIndex);
          final AbstractMapNode<K, V> subNodeNew =
              mergeTwoKeyValPairs(currentKey, currentVal, transformHashCode(currentKey.hashCode()),
                  key, val, keyHash, shift + BIT_PARTITION_SIZE);

          details.modified();
          return copyAndMigrateFromInlineToNode(mutator, bitpos, subNodeNew);
        }
      } else if ((nodeMap() & bitpos) != 0) { // node (not value)
        final AbstractMapNode<K, V> subNode = nodeAt(bitpos);
        final AbstractMapNode<K, V> subNodeNew =
            subNode.updated(mutator, key, val, keyHash, shift + BIT_PARTITION_SIZE, details);

        if (details.isModified()) {
          return copyAndSetNode(mutator, bitpos, subNodeNew);
        } else {
          return this;
        }
      } else {
        // no value
        details.modified();
        return copyAndInsertValue(mutator, bitpos, key, val);
      }
    }

    @Override
    public AbstractMapNode<K, V> removed(final AtomicReference<Thread> mutator, final K key,
                                         final int keyHash, final int shift, final MapNodeResult<K, V> details) {
      final int mask = mask(keyHash, shift);
      final int bitpos = bitpos(mask);

      if ((dataMap() & bitpos) != 0) { // inplace value
        final int dataIndex = dataIndex(bitpos);

        if (Objects.equals(getKey(dataIndex), key)) {
          final V currentVal = getValue(dataIndex);
          details.updated(currentVal);

          if (this.payloadArity() == 2 && this.nodeArity() == 0) {
            /*
             * Create new node with remaining pair. The new node will a) either become the new root
             * returned, or b) unwrapped and inlined during returning.
             */
            final int newDataMap =
                (shift == 0) ? (int) (dataMap() ^ bitpos) : bitpos(mask(keyHash, 0));

            if (dataIndex == 0) {
              return CompactMapNode.<K, V>nodeOf(mutator, 0, newDataMap, getKey(1), getValue(1));
            } else {
              return CompactMapNode.<K, V>nodeOf(mutator, 0, newDataMap, getKey(0), getValue(0));
            }
          } else {
            return copyAndRemoveValue(mutator, bitpos);
          }
        } else {
          return this;
        }
      } else if ((nodeMap() & bitpos) != 0) { // node (not value)
        final AbstractMapNode<K, V> subNode = nodeAt(bitpos);
        final AbstractMapNode<K, V> subNodeNew =
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
        bldr.append(String.format("@%d<#%d,#%d>", pos, Objects.hashCode(getKey(i)),
            Objects.hashCode(getValue(i))));

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

  protected static abstract class CompactMixedMapNode<K, V> extends CompactMapNode<K, V> {

    private final int nodeMap;
    private final int dataMap;

    CompactMixedMapNode(final AtomicReference<Thread> mutator, final int nodeMap,
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

  private static final class BitmapIndexedMapNode<K, V> extends CompactMixedMapNode<K, V> {

    transient final AtomicReference<Thread> mutator;
    final Object[] nodes;

    private BitmapIndexedMapNode(final AtomicReference<Thread> mutator, final int nodeMap,
        final int dataMap, final Object[] nodes) {
      super(mutator, nodeMap, dataMap);

      this.mutator = mutator;
      this.nodes = nodes;

      if (DEBUG) {
        assert (TUPLE_LENGTH * java.lang.Integer.bitCount(dataMap)
            + java.lang.Integer.bitCount(nodeMap) == nodes.length);

        for (int i = 0; i < TUPLE_LENGTH * payloadArity(); i++) {
          assert ((nodes[i] instanceof CompactMapNode) == false);
        }
        for (int i = TUPLE_LENGTH * payloadArity(); i < nodes.length; i++) {
          assert ((nodes[i] instanceof CompactMapNode) == true);
        }

        assert nodeInvariant();
      }
    }

    @Override
    public ArrayView<AbstractMapNode<K, V>> nodeArray() {
      return new ArrayView<AbstractMapNode<K, V>>() {
        @Override
        public int size() {
          return BitmapIndexedMapNode.this.nodeArity();
        }

        @Override
        public AbstractMapNode<K, V> get(int index) {
          return BitmapIndexedMapNode.this.getNode(index);
        }

        /**
         * TODO: replace with {{@link #set(int, AbstractMapNode, AtomicReference)}}
         */
        @Override
        public void set(int index, AbstractMapNode<K, V> item) {
          // if (!isAllowedToEdit(BitmapIndexedSetNode.this.mutator, writeCapabilityToken)) {
          // throw new IllegalStateException();
          // }

          nodes[nodes.length - 1 - index] = item;
        }

        @Override
        public void set(int index, AbstractMapNode<K, V> item,
            AtomicReference<?> writeCapabilityToken) {
          if (!isAllowedToEdit(BitmapIndexedMapNode.this.mutator, writeCapabilityToken)) {
            throw new IllegalStateException();
          }

          nodes[nodes.length - 1 - index] = item;
        }
      };
    }

    @Override
    K getKey(final int index) {
      return (K) nodes[TUPLE_LENGTH * index];
    }

    @Override
    V getValue(final int index) {
      return (V) nodes[TUPLE_LENGTH * index + 1];
    }

    @Override
    Map.Entry<K, V> getKeyValueEntry(final int index) {
      return entryOf((K) nodes[TUPLE_LENGTH * index], (V) nodes[TUPLE_LENGTH * index + 1]);
    }

    @Override
    CompactMapNode<K, V> getNode(final int index) {
      return (CompactMapNode<K, V>) nodes[nodes.length - 1 - index];
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
      BitmapIndexedMapNode<?,?> that = (BitmapIndexedMapNode<?,?>) other;
      if (nodeMap() != that.nodeMap()) {
        return false;
      }
      if (dataMap() != that.dataMap()) {
        return false;
      }
      if (!deepContentEquality(nodes, that.nodes, 2 * payloadArity(), slotArity())) {
        return false;
      }
      return true;
    }

    private final boolean deepContentEquality(
        /* @NotNull */ Object[] a1, /* @NotNull */ Object[] a2, int splitAt, int length) {

//      assert a1 != null && a2 != null;
//      assert a1.length == a2.length;

      if (a1 == a2) {
        return true;
      }

      // compare local payload
      for (int i = 0; i < splitAt; i++) {
        Object o1 = a1[i];
        Object o2 = a2[i];

        if (!Objects.equals(o1, o2)) {
          return false;
        }
      }

      // recursively compare nested nodes
      for (int i = splitAt; i < length; i++) {
        AbstractMapNode o1 = (AbstractMapNode) a1[i];
        AbstractMapNode o2 = (AbstractMapNode) a2[i];

        if (!Objects.equals(o1, o2)) {
          return false;
        }
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
    CompactMapNode<K, V> copyAndSetValue(final AtomicReference<Thread> mutator, final int bitpos,
        final V val) {
      final int idx = TUPLE_LENGTH * dataIndex(bitpos) + 1;

      if (isAllowedToEdit(this.mutator, mutator)) {
        // no copying if already editable
        this.nodes[idx] = val;
        return this;
      } else {
        final Object[] src = this.nodes;
        final Object[] dst = new Object[src.length];

        // copy 'src' and set 1 element(s) at position 'idx'
        System.arraycopy(src, 0, dst, 0, src.length);
        dst[idx + 0] = val;

        return nodeOf(mutator, nodeMap(), dataMap(), dst);
      }
    }

    @Override
    CompactMapNode<K, V> copyAndSetNode(final AtomicReference<Thread> mutator, final int bitpos,
        final AbstractMapNode<K, V> node) {

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
    CompactMapNode<K, V> copyAndInsertValue(final AtomicReference<Thread> mutator, final int bitpos,
        final K key, final V val) {
      final int idx = TUPLE_LENGTH * dataIndex(bitpos);

      final Object[] src = this.nodes;
      final Object[] dst = new Object[src.length + 2];

      // copy 'src' and insert 2 element(s) at position 'idx'
      System.arraycopy(src, 0, dst, 0, idx);
      dst[idx + 0] = key;
      dst[idx + 1] = val;
      System.arraycopy(src, idx, dst, idx + 2, src.length - idx);

      return nodeOf(mutator, nodeMap(), dataMap() | bitpos, dst);
    }

    @Override
    CompactMapNode<K, V> copyAndRemoveValue(final AtomicReference<Thread> mutator,
        final int bitpos) {
      final int idx = TUPLE_LENGTH * dataIndex(bitpos);

      final Object[] src = this.nodes;
      final Object[] dst = new Object[src.length - 2];

      // copy 'src' and remove 2 element(s) at position 'idx'
      System.arraycopy(src, 0, dst, 0, idx);
      System.arraycopy(src, idx + 2, dst, idx, src.length - idx - 2);

      return nodeOf(mutator, nodeMap(), dataMap() ^ bitpos, dst);
    }

    @Override
    CompactMapNode<K, V> copyAndMigrateFromInlineToNode(final AtomicReference<Thread> mutator,
        final int bitpos, final AbstractMapNode<K, V> node) {

      final int idxOld = TUPLE_LENGTH * dataIndex(bitpos);
      final int idxNew = this.nodes.length - TUPLE_LENGTH - nodeIndex(bitpos);

      final Object[] src = this.nodes;
      final Object[] dst = new Object[src.length - 2 + 1];

      // copy 'src' and remove 2 element(s) at position 'idxOld' and
      // insert 1 element(s) at position 'idxNew' (TODO: carefully test)
      assert idxOld <= idxNew;
      System.arraycopy(src, 0, dst, 0, idxOld);
      System.arraycopy(src, idxOld + 2, dst, idxOld, idxNew - idxOld);
      dst[idxNew + 0] = node;
      System.arraycopy(src, idxNew + 2, dst, idxNew + 1, src.length - idxNew - 2);

      return nodeOf(mutator, nodeMap() | bitpos, dataMap() ^ bitpos, dst);
    }

    @Override
    CompactMapNode<K, V> copyAndMigrateFromNodeToInline(final AtomicReference<Thread> mutator,
        final int bitpos, final AbstractMapNode<K, V> node) {

      final int idxOld = this.nodes.length - 1 - nodeIndex(bitpos);
      final int idxNew = TUPLE_LENGTH * dataIndex(bitpos);

      final Object[] src = this.nodes;
      final Object[] dst = new Object[src.length - 1 + 2];

      // copy 'src' and remove 1 element(s) at position 'idxOld' and
      // insert 2 element(s) at position 'idxNew' (TODO: carefully test)
      assert idxOld >= idxNew;
      System.arraycopy(src, 0, dst, 0, idxNew);
      dst[idxNew + 0] = node.getKey(0);
      dst[idxNew + 1] = node.getValue(0);
      System.arraycopy(src, idxNew, dst, idxNew + 2, idxOld - idxNew);
      System.arraycopy(src, idxOld + 1, dst, idxOld + 2, src.length - idxOld - 1);

      return nodeOf(mutator, nodeMap() ^ bitpos, dataMap() | bitpos, dst);
    }

  }

  private static final class HashCollisionMapNode<K, V> extends CompactMapNode<K, V> {

    private final K[] keys;
    private final V[] vals;
    private final int hash;

    HashCollisionMapNode(final int hash, final K[] keys, final V[] vals) {
      this.keys = keys;
      this.vals = vals;
      this.hash = hash;

      assert payloadArity() >= 2;
    }

    @Override
    public ArrayView<AbstractMapNode<K, V>> nodeArray() {
      return ArrayView.empty();
    }

    @Override
    public boolean containsKey(final K key, final int keyHash, final int shift) {
      if (this.hash == keyHash) {
        for (K k : keys) {
          if (Objects.equals(k, key)) {
            return true;
          }
        }
      }
      return false;
    }

    @Override
    public Optional<V> findByKey(final K key, final int keyHash, final int shift) {
      for (int i = 0; i < keys.length; i++) {
        final K _key = keys[i];
        if (Objects.equals(key, _key)) {
          final V val = vals[i];
          return Optional.of(val);
        }
      }
      return Optional.empty();
    }

    @Override
    public AbstractMapNode<K, V> updated(final AtomicReference<Thread> mutator, final K key, final V val,
                                         final int keyHash, final int shift, final MapNodeResult<K, V> details) {
      assert this.hash == keyHash;

      for (int idx = 0; idx < keys.length; idx++) {
        if (Objects.equals(keys[idx], key)) {
          final V currentVal = vals[idx];

          if (Objects.equals(currentVal, val)) {
            return this;
          } else {
            // add new mapping
            final V[] src = this.vals;
            final V[] dst = (V[]) new Object[src.length];

            // copy 'src' and set 1 element(s) at position 'idx'
            System.arraycopy(src, 0, dst, 0, src.length);
            dst[idx + 0] = val;

            final CompactMapNode<K, V> thisNew =
                new HashCollisionMapNode<>(this.hash, this.keys, dst);

            details.updated(currentVal);
            return thisNew;
          }
        }
      }

      final K[] keysNew = (K[]) new Object[this.keys.length + 1];

      // copy 'this.keys' and insert 1 element(s) at position
      // 'keys.length'
      System.arraycopy(this.keys, 0, keysNew, 0, keys.length);
      keysNew[keys.length + 0] = key;
      System.arraycopy(this.keys, keys.length, keysNew, keys.length + 1,
          this.keys.length - keys.length);

      final V[] valsNew = (V[]) new Object[this.vals.length + 1];

      // copy 'this.vals' and insert 1 element(s) at position
      // 'vals.length'
      System.arraycopy(this.vals, 0, valsNew, 0, vals.length);
      valsNew[vals.length + 0] = val;
      System.arraycopy(this.vals, vals.length, valsNew, vals.length + 1,
          this.vals.length - vals.length);

      details.modified();
      return new HashCollisionMapNode<>(keyHash, keysNew, valsNew);
    }

    @Override
    public AbstractMapNode<K, V> removed(final AtomicReference<Thread> mutator, final K key,
                                         final int keyHash, final int shift, final MapNodeResult<K, V> details) {
      for (int idx = 0; idx < keys.length; idx++) {
        if (Objects.equals(keys[idx], key)) {
          final V currentVal = vals[idx];
          details.updated(currentVal);

          if (this.arity() == 1) {
            return nodeOf(mutator);
          } else if (this.arity() == 2) {
            /*
             * Create root node with singleton element. This node will be a) either be the new root
             * returned, or b) unwrapped and inlined.
             */
            final K theOtherKey = (idx == 0) ? keys[1] : keys[0];
            final V theOtherVal = (idx == 0) ? vals[1] : vals[0];
            return CompactMapNode.<K, V>nodeOf(mutator).updated(mutator, theOtherKey, theOtherVal,
                keyHash, 0, details);
          } else {
            final K[] keysNew = (K[]) new Object[this.keys.length - 1];

            // copy 'this.keys' and remove 1 element(s) at position
            // 'idx'
            System.arraycopy(this.keys, 0, keysNew, 0, idx);
            System.arraycopy(this.keys, idx + 1, keysNew, idx, this.keys.length - idx - 1);

            final V[] valsNew = (V[]) new Object[this.vals.length - 1];

            // copy 'this.vals' and remove 1 element(s) at position
            // 'idx'
            System.arraycopy(this.vals, 0, valsNew, 0, idx);
            System.arraycopy(this.vals, idx + 1, valsNew, idx, this.vals.length - idx - 1);

            return new HashCollisionMapNode<>(keyHash, keysNew, valsNew);
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
    public byte sizePredicate() {
      return SIZE_MORE_THAN_ONE;
    }

    @Override
    K getKey(final int index) {
      return keys[index];
    }

    @Override
    V getValue(final int index) {
      return vals[index];
    }

    @Override
    Map.Entry<K, V> getKeyValueEntry(final int index) {
      return entryOf(keys[index], vals[index]);
    }

    @Override
    public CompactMapNode<K, V> getNode(int index) {
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
      result = prime * result + Arrays.hashCode(vals);
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

      HashCollisionMapNode<?, ?> that = (HashCollisionMapNode<?, ?>) other;

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
        final Object otherVal = that.getValue(i);

        for (int j = 0; j < keys.length; j++) {
          final K key = keys[j];
          final V val = vals[j];

          if (Objects.equals(key, otherKey) && Objects.equals(val, otherVal)) {
            continue outerLoop;
          }
        }
        return false;
      }

      return true;
    }

    @Override
    CompactMapNode<K, V> copyAndSetValue(final AtomicReference<Thread> mutator, final int bitpos,
        final V val) {
      throw new UnsupportedOperationException();
    }

    @Override
    CompactMapNode<K, V> copyAndInsertValue(final AtomicReference<Thread> mutator, final int bitpos,
        final K key, final V val) {
      throw new UnsupportedOperationException();
    }

    @Override
    CompactMapNode<K, V> copyAndRemoveValue(final AtomicReference<Thread> mutator,
        final int bitpos) {
      throw new UnsupportedOperationException();
    }

    @Override
    CompactMapNode<K, V> copyAndSetNode(final AtomicReference<Thread> mutator, final int bitpos,
        final AbstractMapNode<K, V> node) {
      throw new UnsupportedOperationException();
    }

    @Override
    CompactMapNode<K, V> copyAndMigrateFromInlineToNode(final AtomicReference<Thread> mutator,
        final int bitpos, final AbstractMapNode<K, V> node) {
      throw new UnsupportedOperationException();
    }

    @Override
    CompactMapNode<K, V> copyAndMigrateFromNodeToInline(final AtomicReference<Thread> mutator,
        final int bitpos, final AbstractMapNode<K, V> node) {
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
  private static abstract class AbstractMapIterator<K, V> {

    private static final int MAX_DEPTH = 7;

    protected int currentValueCursor;
    protected int currentValueLength;
    protected AbstractMapNode<K, V> currentValueNode;

    private int currentStackLevel = -1;
    private final int[] nodeCursorsAndLengths = new int[MAX_DEPTH * 2];

    AbstractMapNode<K, V>[] nodes = new AbstractMapNode[MAX_DEPTH];

    AbstractMapIterator(AbstractMapNode<K, V> rootNode) {
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
          final AbstractMapNode<K, V> nextNode = nodes[currentStackLevel].getNode(nodeCursor);
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

  protected static class MapKeyIterator<K, V> extends AbstractMapIterator<K, V>
      implements Iterator<K> {

    MapKeyIterator(AbstractMapNode<K, V> rootNode) {
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

  protected static class MapValueIterator<K, V> extends AbstractMapIterator<K, V>
      implements Iterator<V> {

    MapValueIterator(AbstractMapNode<K, V> rootNode) {
      super(rootNode);
    }

    @Override
    public V next() {
      if (!hasNext()) {
        throw new NoSuchElementException();
      } else {
        return currentValueNode.getValue(currentValueCursor++);
      }
    }

  }

  protected static class MapEntryIterator<K, V> extends AbstractMapIterator<K, V>
      implements Iterator<Map.Entry<K, V>> {

    MapEntryIterator(AbstractMapNode<K, V> rootNode) {
      super(rootNode);
    }

    @Override
    public Map.Entry<K, V> next() {
      if (!hasNext()) {
        throw new NoSuchElementException();
      } else {
        return currentValueNode.getKeyValueEntry(currentValueCursor++);
      }
    }

  }

  /**
   * Iterator that first iterates over inlined-values and then continues depth first recursively.
   */
  private static class TrieMapNodeIterator<K, V> implements Iterator<AbstractMapNode<K, V>> {

    final Deque<Iterator<? extends AbstractMapNode<K, V>>> nodeIteratorStack;

    TrieMapNodeIterator(AbstractMapNode<K, V> rootNode) {
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
    public AbstractMapNode<K, V> next() {
      if (!hasNext()) {
        throw new NoSuchElementException();
      }

      AbstractMapNode<K, V> innerNode = nodeIteratorStack.peek().next();

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

  static final class TransientTrieMap<K, V> implements
      io.usethesource.capsule.Map.Transient<K, V> {

    final private AtomicReference<Thread> mutator;
    private AbstractMapNode<K, V> rootNode;
    private int cachedHashCode;
    private int cachedSize;

    TransientTrieMap(PersistentTrieMap<K, V> trieMap) {
      this.mutator = new AtomicReference<Thread>(Thread.currentThread());
      this.rootNode = trieMap.rootNode;
      this.cachedHashCode = trieMap.cachedHashCode;
      this.cachedSize = trieMap.cachedSize;
      if (DEBUG) {
        assert checkHashCodeAndSize(cachedHashCode, cachedSize);
      }
    }

    private boolean checkHashCodeAndSize(final int targetHash, final int targetSize) {
      int hash = 0;
      int size = 0;

      for (Iterator<Map.Entry<K, V>> it = entryIterator(); it.hasNext(); ) {
        final Map.Entry<K, V> entry = it.next();
        final K key = entry.getKey();
        final V val = entry.getValue();

        hash += key.hashCode() ^ val.hashCode();
        size += 1;
      }

      return hash == targetHash && size == targetSize;
    }

    @Override
    public V put(final K key, final V val) {
      /**
       * Delegation added to support {@link Map#compute(Object, BiFunction)}.
       */
      return __put(key, val);

      // throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(final Map<? extends K, ? extends V> m) {
      throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
      throw new UnsupportedOperationException();
    }

    @Override
    public V remove(final Object key) {
      throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsKey(final Object o) {
      try {
        final K key = (K) o;
        return rootNode.containsKey(key, transformHashCode(key.hashCode()), 0);
      } catch (ClassCastException unused) {
        return false;
      }
    }

    @Override
    public boolean containsValue(final Object o) {
      for (Iterator<V> iterator = valueIterator(); iterator.hasNext(); ) {
        if (Objects.equals(iterator.next(), o)) {
          return true;
        }
      }
      return false;
    }

    @Override
    public V get(final Object o) {
      try {
        final K key = (K) o;
        final Optional<V> result =
            rootNode.findByKey(key, transformHashCode(key.hashCode()), 0);

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
    public V __put(final K key, final V val) {
      if (mutator.get() == null) {
        throw new IllegalStateException("Transient already frozen.");
      }

      final int keyHash = key.hashCode();
      final MapNodeResult<K, V> details = MapNodeResult.unchanged();

      final AbstractMapNode<K, V> newRootNode =
          rootNode.updated(mutator, key, val, transformHashCode(keyHash), 0, details);

      if (details.isModified()) {
        if (details.hasReplacedValue()) {
          final V old = details.getReplacedValue();

          final int valHashOld = old.hashCode();
          final int valHashNew = val.hashCode();

          rootNode = newRootNode;
          cachedHashCode = cachedHashCode + (keyHash ^ valHashNew) - (keyHash ^ valHashOld);

          if (DEBUG) {
            assert checkHashCodeAndSize(cachedHashCode, cachedSize);
          }
          return details.getReplacedValue();
        } else {
          final int valHashNew = val.hashCode();
          rootNode = newRootNode;
          cachedHashCode += (keyHash ^ valHashNew);
          cachedSize += 1;

          if (DEBUG) {
            assert checkHashCodeAndSize(cachedHashCode, cachedSize);
          }
          return null;
        }
      }

      if (DEBUG) {
        assert checkHashCodeAndSize(cachedHashCode, cachedSize);
      }
      return null;
    }

    @Override
    public boolean __putAll(final Map<? extends K, ? extends V> map) {
      boolean modified = false;

      for (Map.Entry<? extends K, ? extends V> entry : map.entrySet()) {
        final boolean isPresent = this.containsKey(entry.getKey());
        final V replaced = this.__put(entry.getKey(), entry.getValue());

        if (!isPresent || replaced != null) {
          modified = true;
        }
      }

      return modified;
    }

    @Override
    public V __remove(final K key) {
      if (mutator.get() == null) {
        throw new IllegalStateException("Transient already frozen.");
      }

      final int keyHash = key.hashCode();
      final MapNodeResult<K, V> details = MapNodeResult.unchanged();

      final AbstractMapNode<K, V> newRootNode = rootNode.removed(mutator, key,
          transformHashCode(keyHash), 0, details);

      if (details.isModified()) {
        assert details.hasReplacedValue();
        final int valHash = details.getReplacedValue().hashCode();

        rootNode = newRootNode;
        cachedHashCode = cachedHashCode - (keyHash ^ valHash);
        cachedSize = cachedSize - 1;

        if (DEBUG) {
          assert checkHashCodeAndSize(cachedHashCode, cachedSize);
        }
        return details.getReplacedValue();
      }

      if (DEBUG) {
        assert checkHashCodeAndSize(cachedHashCode, cachedSize);
      }

      return null;
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
    public Iterator<K> keyIterator() {
      return new TransientMapKeyIterator<>(this);
    }

    @Override
    public Iterator<V> valueIterator() {
      return new TransientMapValueIterator<>(this);
    }

    @Override
    public Iterator<Map.Entry<K, V>> entryIterator() {
      return new TransientMapEntryIterator<>(this);
    }

    public static class TransientMapKeyIterator<K, V> extends MapKeyIterator<K, V> {

      final TransientTrieMap<K, V> collection;
      K lastKey;

      public TransientMapKeyIterator(final TransientTrieMap<K, V> collection) {
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

    public static class TransientMapValueIterator<K, V> extends MapValueIterator<K, V> {

      final TransientTrieMap<K, V> collection;

      public TransientMapValueIterator(final TransientTrieMap<K, V> collection) {
        super(collection.rootNode);
        this.collection = collection;
      }

      @Override
      public V next() {
        return super.next();
      }

      @Override
      public void remove() {
        throw new UnsupportedOperationException();
      }
    }

    public static class TransientMapEntryIterator<K, V> extends MapEntryIterator<K, V> {

      final TransientTrieMap<K, V> collection;

      public TransientMapEntryIterator(final TransientTrieMap<K, V> collection) {
        super(collection.rootNode);
        this.collection = collection;
      }

      @Override
      public Map.Entry<K, V> next() {
        return super.next();
      }

      @Override
      public void remove() {
        throw new UnsupportedOperationException();
      }
    }

    @Override
    public Set<K> keySet() {
      Set<K> keySet = null;

      if (keySet == null) {
        keySet = new AbstractSet<K>() {
          @Override
          public Iterator<K> iterator() {
            return TransientTrieMap.this.keyIterator();
          }

          @Override
          public int size() {
            return TransientTrieMap.this.size();
          }

          @Override
          public boolean isEmpty() {
            return TransientTrieMap.this.isEmpty();
          }

          @Override
          public void clear() {
            TransientTrieMap.this.clear();
          }

          @Override
          public boolean contains(Object k) {
            return TransientTrieMap.this.containsKey(k);
          }
        };
      }

      return keySet;
    }

    @Override
    public Collection<V> values() {
      Collection<V> values = null;

      if (values == null) {
        values = new AbstractCollection<V>() {
          @Override
          public Iterator<V> iterator() {
            return TransientTrieMap.this.valueIterator();
          }

          @Override
          public int size() {
            return TransientTrieMap.this.size();
          }

          @Override
          public boolean isEmpty() {
            return TransientTrieMap.this.isEmpty();
          }

          @Override
          public void clear() {
            TransientTrieMap.this.clear();
          }

          @Override
          public boolean contains(Object v) {
            return TransientTrieMap.this.containsValue(v);
          }
        };
      }

      return values;
    }

    @Override
    public Set<java.util.Map.Entry<K, V>> entrySet() {
      Set<java.util.Map.Entry<K, V>> entrySet = null;

      if (entrySet == null) {
        entrySet = new AbstractSet<java.util.Map.Entry<K, V>>() {
          @Override
          public Iterator<java.util.Map.Entry<K, V>> iterator() {
            return new Iterator<Map.Entry<K, V>>() {
              private final Iterator<Map.Entry<K, V>> i = entryIterator();

              @Override
              public boolean hasNext() {
                return i.hasNext();
              }

              @Override
              public Map.Entry<K, V> next() {
                return i.next();
              }

              @Override
              public void remove() {
                i.remove();
              }
            };
          }

          @Override
          public int size() {
            return TransientTrieMap.this.size();
          }

          @Override
          public boolean isEmpty() {
            return TransientTrieMap.this.isEmpty();
          }

          @Override
          public void clear() {
            TransientTrieMap.this.clear();
          }

          @Override
          public boolean contains(Object k) {
            return TransientTrieMap.this.containsKey(k);
          }
        };
      }

      return entrySet;
    }

    public boolean equals(final Object other) {
      if (other == this) {
        return true;
      }
      if (other == null) {
        return false;
      }

      if (other instanceof PersistentTrieMap.TransientTrieMap) {
        TransientTrieMap<?, ?> that = (TransientTrieMap<?, ?>) other;

        if (this.cachedSize != that.cachedSize) {
          return false;
        }

        if (this.cachedHashCode != that.cachedHashCode) {
          return false;
        }

        return rootNode.equals(that.rootNode);
      } else if (other instanceof Map) {
        Map that = (Map) other;

        if (this.size() != that.size()) {
          return false;
        }

        for (
            Iterator<Map.Entry> it = that.entrySet().iterator(); it.hasNext(); ) {
          Map.Entry entry = it.next();

          try {
            final K key = (K) entry.getKey();
            final Optional<V> result =
                rootNode.findByKey(key, transformHashCode(key.hashCode()), 0);

            if (!result.isPresent()) {
              return false;
            } else {
              final V val = (V) entry.getValue();

              if (!Objects.equals(result.get(), val)) {
                return false;
              }
            }
          } catch (ClassCastException unused) {
            return false;
          }
        }

        return true;
      }

      return false;
    }

    @Override
    public int hashCode() {
      return cachedHashCode;
    }

    @Override
    public String toString() {
      String body = entrySet().stream()
          .map(entry -> String.format("%s: %s", entry.getKey(), entry.getValue()))
          .reduce((left, right) -> String.join(", ", left, right))
          .orElse("");
      return String.format("{%s}", body);
    }

    @Override
    public io.usethesource.capsule.Map.Immutable<K, V> freeze() {
      if (mutator.get() == null) {
        throw new IllegalStateException("Transient already frozen.");
      }

      mutator.set(null);
      return new PersistentTrieMap<K, V>(rootNode, cachedHashCode, cachedSize);
    }
  }

}
