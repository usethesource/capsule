/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.capsule.experimental.multimap;

import java.text.DecimalFormat;
import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiFunction;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import io.usethesource.capsule.api.deprecated.ImmutableSet;
import io.usethesource.capsule.api.deprecated.ImmutableSetMultimap;
import io.usethesource.capsule.api.deprecated.SetMultimap;
import io.usethesource.capsule.api.deprecated.TransientSetMultimap;
import io.usethesource.capsule.util.collection.AbstractSpecialisedImmutableMap;
import io.usethesource.capsule.util.collection.AbstractSpecialisedImmutableSet;

@SuppressWarnings("rawtypes")
public class TrieSetMultimap_ChampBasedPrototype<K, V> implements ImmutableSetMultimap<K, V> {

  @SuppressWarnings("unchecked")
  private static final TrieSetMultimap_ChampBasedPrototype EMPTY_SETMULTIMAP =
      new TrieSetMultimap_ChampBasedPrototype(CompactSetMultimapNode.EMPTY_NODE, 0, 0);

  private static final boolean DEBUG = false;

  private final AbstractSetMultimapNode<K, V> rootNode;
  private final int hashCode;
  private final int cachedSize;

  TrieSetMultimap_ChampBasedPrototype(AbstractSetMultimapNode<K, V> rootNode, int hashCode,
      int cachedSize) {
    this.rootNode = rootNode;
    this.hashCode = hashCode;
    this.cachedSize = cachedSize;
    if (DEBUG) {
      assert checkHashCodeAndSize(hashCode, cachedSize);
    }
  }

  @SuppressWarnings("unchecked")
  public static final <K, V> ImmutableSetMultimap<K, V> of() {
    return TrieSetMultimap_ChampBasedPrototype.EMPTY_SETMULTIMAP;
  }

  @SuppressWarnings("unchecked")
  public static final <K, V> ImmutableSetMultimap<K, V> of(K key, V... values) {
    ImmutableSetMultimap<K, V> result = TrieSetMultimap_ChampBasedPrototype.EMPTY_SETMULTIMAP;

    for (V value : values) {
      result = result.__insert(key, value);
    }

    return result;
  }

  @SuppressWarnings("unchecked")
  public static final <K, V> TransientSetMultimap<K, V> transientOf() {
    return TrieSetMultimap_ChampBasedPrototype.EMPTY_SETMULTIMAP.asTransient();
  }

  @SuppressWarnings("unchecked")
  public static final <K, V> TransientSetMultimap<K, V> transientOf(K key, V... values) {
    final TransientSetMultimap<K, V> result =
        TrieSetMultimap_ChampBasedPrototype.EMPTY_SETMULTIMAP.asTransient();

    for (V value : values) {
      result.__insert(key, value);
    }

    return result;
  }

  private boolean checkHashCodeAndSize(final int targetHash, final int targetSize) {
    int hash = 0;
    int size = 0;

    for (Iterator<Map.Entry<K, V>> it = entryIterator(); it.hasNext();) {
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

  public boolean containsKey(final Object o) {
    try {
      @SuppressWarnings("unchecked")
      final K key = (K) o;
      return rootNode.containsKey(key, transformHashCode(key.hashCode()), 0);
    } catch (ClassCastException unused) {
      return false;
    }
  }

  public boolean containsKeyEquivalent(final Object o, final Comparator<Object> cmp) {
    try {
      @SuppressWarnings("unchecked")
      final K key = (K) o;
      return rootNode.containsKey(key, transformHashCode(key.hashCode()), 0, cmp);
    } catch (ClassCastException unused) {
      return false;
    }
  }

  public boolean containsValue(final Object o) {
    for (Iterator<V> iterator = valueIterator(); iterator.hasNext();) {
      if (iterator.next().equals(o)) {
        return true;
      }
    }
    return false;
  }

  public boolean containsValueEquivalent(final Object o, final Comparator<Object> cmp) {
    for (Iterator<V> iterator = valueIterator(); iterator.hasNext();) {
      if (cmp.compare(iterator.next(), o) == 0) {
        return true;
      }
    }
    return false;
  }

  public boolean containsEntry(final Object o0, final Object o1) {
    try {
      @SuppressWarnings("unchecked")
      final K key = (K) o0;
      @SuppressWarnings("unchecked")
      final V val = (V) o1;
      final Optional<ImmutableSet<V>> result =
          rootNode.findByKey(key, transformHashCode(key.hashCode()), 0);

      if (result.isPresent()) {
        return result.get().contains(val);
      } else {
        return false;
      }
    } catch (ClassCastException unused) {
      return false;
    }
  }

  public boolean containsEntryEquivalent(final Object o0, final Object o1,
      final Comparator<Object> cmp) {
    try {
      @SuppressWarnings("unchecked")
      final K key = (K) o0;
      @SuppressWarnings("unchecked")
      final V val = (V) o1;
      final Optional<ImmutableSet<V>> result =
          rootNode.findByKey(key, transformHashCode(key.hashCode()), 0, cmp);

      if (result.isPresent()) {
        return result.get().containsEquivalent(val, cmp);
      } else {
        return false;
      }
    } catch (ClassCastException unused) {
      return false;
    }
  }

  public ImmutableSet<V> get(final Object o) {
    try {
      @SuppressWarnings("unchecked")
      final K key = (K) o;
      final Optional<ImmutableSet<V>> result =
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

  public ImmutableSet<V> getEquivalent(final Object o, final Comparator<Object> cmp) {
    try {
      @SuppressWarnings("unchecked")
      final K key = (K) o;
      final Optional<ImmutableSet<V>> result =
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

  public ImmutableSetMultimap<K, V> __put(final K key, final V val) {
    throw new UnsupportedOperationException();
  }

  public ImmutableSetMultimap<K, V> __insert(final K key, final V val) {
    final int keyHash = key.hashCode();
    final SetMultimapResult<K, V> details = SetMultimapResult.unchanged();

    final CompactSetMultimapNode<K, V> newRootNode =
        rootNode.updated(null, key, val, transformHashCode(keyHash), 0, details);

    if (details.isModified()) {
      final int valHash = val.hashCode();
      return new TrieSetMultimap_ChampBasedPrototype<K, V>(newRootNode,
          hashCode + ((keyHash ^ valHash)), cachedSize + 1);
    }

    return this;
  }

  public ImmutableSetMultimap<K, V> __insertEquivalent(final K key, final V val,
      final Comparator<Object> cmp) {
    final int keyHash = key.hashCode();
    final SetMultimapResult<K, V> details = SetMultimapResult.unchanged();

    final CompactSetMultimapNode<K, V> newRootNode =
        rootNode.updated(null, key, val, transformHashCode(keyHash), 0, details, cmp);

    if (details.isModified()) {
      final int valHash = val.hashCode();
      return new TrieSetMultimap_ChampBasedPrototype<K, V>(newRootNode,
          hashCode + ((keyHash ^ valHash)), cachedSize + 1);
    }

    return this;
  }

  public ImmutableSetMultimap<K, V> __insertAll(
      final SetMultimap<? extends K, ? extends V> setMultimap) {
    final TransientSetMultimap<K, V> tmpTransient = this.asTransient();
    tmpTransient.__insertAll(setMultimap);
    return tmpTransient.freeze();
  }

  public ImmutableSetMultimap<K, V> __insertAllEquivalent(
      final SetMultimap<? extends K, ? extends V> setMultimap,
      final Comparator<Object> cmp) {
    final TransientSetMultimap<K, V> tmpTransient = this.asTransient();
    tmpTransient.__insertAllEquivalent(setMultimap, cmp);
    return tmpTransient.freeze();
  }

  public ImmutableSetMultimap<K, V> __removeEntry(final K key, final V val) {
    final int keyHash = key.hashCode();
    final SetMultimapResult<K, V> details = SetMultimapResult.unchanged();

    final CompactSetMultimapNode<K, V> newRootNode =
        rootNode.removed(null, key, val, transformHashCode(keyHash), 0, details);

    if (details.isModified()) {
      assert details.hasReplacedValue();
      final int valHash = details.getReplacedValue().hashCode();
      return new TrieSetMultimap_ChampBasedPrototype<K, V>(newRootNode,
          hashCode - ((keyHash ^ valHash)), cachedSize - 1);
    }

    return this;
  }

  public ImmutableSetMultimap<K, V> __removeEntryEquivalent(final K key, final V val,
      final Comparator<Object> cmp) {
    final int keyHash = key.hashCode();
    final SetMultimapResult<K, V> details = SetMultimapResult.unchanged();

    final CompactSetMultimapNode<K, V> newRootNode =
        rootNode.removed(null, key, val, transformHashCode(keyHash), 0, details, cmp);

    if (details.isModified()) {
      assert details.hasReplacedValue();
      final int valHash = details.getReplacedValue().hashCode();
      return new TrieSetMultimap_ChampBasedPrototype<K, V>(newRootNode,
          hashCode - ((keyHash ^ valHash)), cachedSize - 1);
    }

    return this;
  }

  public V put(final K key, final V val) {
    throw new UnsupportedOperationException();
  }

  public void putAll(final SetMultimap<? extends K, ? extends V> m) {
    throw new UnsupportedOperationException();
  }

  public void clear() {
    throw new UnsupportedOperationException();
  }

  public V remove(final Object key, final Object val) {
    throw new UnsupportedOperationException();
  }

  public int size() {
    return cachedSize;
  }

  public boolean isEmpty() {
    return cachedSize == 0;
  }

  public Iterator<K> keyIterator() {
    return new SetMultimapKeyIterator<>(rootNode);
  }

  public Iterator<V> valueIterator() {
    return valueCollectionsStream().flatMap(Set::stream).iterator();
  }

  public Iterator<Map.Entry<K, V>> entryIterator() {
    return new SetMultimapTupleIterator<>(rootNode, AbstractSpecialisedImmutableMap::entryOf);
  }

  public Iterator<Map.Entry<K, Object>> nativeEntryIterator() {
    throw new UnsupportedOperationException();
  }

  public <T> Iterator<T> tupleIterator(final BiFunction<K, V, T> tupleOf) {
    return new SetMultimapTupleIterator<>(rootNode, tupleOf);
  }

  private Spliterator<ImmutableSet<V>> valueCollectionsSpliterator() {
    /*
     * TODO: specialize between mutable / immutable ({@see Spliterator.IMMUTABLE})
     */
    int characteristics = Spliterator.NONNULL | Spliterator.SIZED | Spliterator.SUBSIZED;
    return Spliterators.spliterator(new SetMultimapValueIterator<>(rootNode), size(),
        characteristics);
  }

  private Stream<ImmutableSet<V>> valueCollectionsStream() {
    boolean isParallel = false;
    return StreamSupport.stream(valueCollectionsSpliterator(), isParallel);
  }

  @Override
  public Set<K> keySet() {
    Set<K> keySet = null;

    if (keySet == null) {
      keySet = new AbstractSet<K>() {
        @Override
        public Iterator<K> iterator() {
          return TrieSetMultimap_ChampBasedPrototype.this.keyIterator();
        }

        @Override
        public int size() {
          return TrieSetMultimap_ChampBasedPrototype.this.sizeDistinct();
        }

        @Override
        public boolean isEmpty() {
          return TrieSetMultimap_ChampBasedPrototype.this.isEmpty();
        }

        @Override
        public void clear() {
          TrieSetMultimap_ChampBasedPrototype.this.clear();
        }

        @Override
        public boolean contains(Object k) {
          return TrieSetMultimap_ChampBasedPrototype.this.containsKey(k);
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
          return TrieSetMultimap_ChampBasedPrototype.this.valueIterator();
        }

        @Override
        public int size() {
          return TrieSetMultimap_ChampBasedPrototype.this.size();
        }

        @Override
        public boolean isEmpty() {
          return TrieSetMultimap_ChampBasedPrototype.this.isEmpty();
        }

        @Override
        public void clear() {
          TrieSetMultimap_ChampBasedPrototype.this.clear();
        }

        @Override
        public boolean contains(Object v) {
          return TrieSetMultimap_ChampBasedPrototype.this.containsValue(v);
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
          return TrieSetMultimap_ChampBasedPrototype.this.size();
        }

        @Override
        public boolean isEmpty() {
          return TrieSetMultimap_ChampBasedPrototype.this.isEmpty();
        }

        @Override
        public void clear() {
          TrieSetMultimap_ChampBasedPrototype.this.clear();
        }

        @Override
        public boolean contains(Object k) {
          return TrieSetMultimap_ChampBasedPrototype.this.containsKey(k);
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

    if (other instanceof TrieSetMultimap_ChampBasedPrototype) {
      TrieSetMultimap_ChampBasedPrototype<?, ?> that =
          (TrieSetMultimap_ChampBasedPrototype<?, ?>) other;

      if (this.cachedSize != that.cachedSize) {
        return false;
      }

      if (this.hashCode != that.hashCode) {
        return false;
      }

      return rootNode.equals(that.rootNode);
    } else if (other instanceof SetMultimap) {
      SetMultimap that = (SetMultimap) other;

      if (this.size() != that.size())
        return false;

      for (@SuppressWarnings("unchecked")
      Iterator<Map.Entry> it = that.entrySet().iterator(); it.hasNext();) {
        Map.Entry entry = it.next();

        try {
          @SuppressWarnings("unchecked")
          final K key = (K) entry.getKey();
          final Optional<ImmutableSet<V>> result =
              rootNode.findByKey(key, transformHashCode(key.hashCode()), 0);

          if (!result.isPresent()) {
            return false;
          } else {
            @SuppressWarnings("unchecked")
            final ImmutableSet<V> valColl = (ImmutableSet<V>) entry.getValue();

            if (!result.get().equals(valColl)) {
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
    return hashCode;
  }

  @Override
  public boolean isTransientSupported() {
    return true;
  }

  @Override
  public TransientSetMultimap<K, V> asTransient() {
    return new TransientTrieSetMultimap_BleedingEdge<K, V>(this);
  }

  /*
   * For analysis purposes only.
   */
  protected AbstractSetMultimapNode<K, V> getRootNode() {
    return rootNode;
  }

  /*
   * For analysis purposes only.
   */
  protected Iterator<AbstractSetMultimapNode<K, V>> nodeIterator() {
    return new TrieSetMultimap_BleedingEdgeNodeIterator<>(rootNode);
  }

  /*
   * For analysis purposes only.
   */
  protected int getNodeCount() {
    final Iterator<AbstractSetMultimapNode<K, V>> it = nodeIterator();
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
    final Iterator<AbstractSetMultimapNode<K, V>> it = nodeIterator();
    final int[][] sumArityCombinations = new int[33][33];

    while (it.hasNext()) {
      final AbstractSetMultimapNode<K, V> node = it.next();
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

  static final class SetMultimapResult<K, V> {
    private V replacedValue;
    private boolean isModified;
    private boolean isReplaced;

    // update: inserted/removed single element, element count changed
    public void modified() {
      this.isModified = true;
    }

    public void updated(V replacedValue) {
      this.replacedValue = replacedValue;
      this.isModified = true;
      this.isReplaced = true;
    }

    // update: neither element, nor element count changed
    public static <K, V> SetMultimapResult<K, V> unchanged() {
      return new SetMultimapResult<>();
    }

    private SetMultimapResult() {}

    public boolean isModified() {
      return isModified;
    }

    public boolean hasReplacedValue() {
      return isReplaced;
    }

    public V getReplacedValue() {
      return replacedValue;
    }
  }

  protected static interface INode<K, V> {
  }

  protected static abstract class AbstractSetMultimapNode<K, V> implements INode<K, V> {

    static final int TUPLE_LENGTH = 2;

    abstract boolean containsKey(final K key, final int keyHash, final int shift);

    abstract boolean containsKey(final K key, final int keyHash, final int shift,
        final Comparator<Object> cmp);

    abstract Optional<ImmutableSet<V>> findByKey(final K key, final int keyHash, final int shift);

    abstract Optional<ImmutableSet<V>> findByKey(final K key, final int keyHash, final int shift,
        final Comparator<Object> cmp);

    abstract CompactSetMultimapNode<K, V> updated(final AtomicReference<Thread> mutator,
        final K key, final V val, final int keyHash, final int shift,
        final SetMultimapResult<K, V> details);

    abstract CompactSetMultimapNode<K, V> updated(final AtomicReference<Thread> mutator,
        final K key, final V val, final int keyHash, final int shift,
        final SetMultimapResult<K, V> details, final Comparator<Object> cmp);

    abstract CompactSetMultimapNode<K, V> removed(final AtomicReference<Thread> mutator,
        final K key, final V val, final int keyHash, final int shift,
        final SetMultimapResult<K, V> details);

    abstract CompactSetMultimapNode<K, V> removed(final AtomicReference<Thread> mutator,
        final K key, final V val, final int keyHash, final int shift,
        final SetMultimapResult<K, V> details, final Comparator<Object> cmp);

    static final boolean isAllowedToEdit(AtomicReference<Thread> x, AtomicReference<Thread> y) {
      return x != null && y != null && (x == y || x.get() == y.get());
    }

    abstract boolean hasNodes();

    abstract int nodeArity();

    abstract AbstractSetMultimapNode<K, V> getNode(final int index);

    @Deprecated
    Iterator<? extends AbstractSetMultimapNode<K, V>> nodeIterator() {
      return new Iterator<AbstractSetMultimapNode<K, V>>() {

        int nextIndex = 0;
        final int nodeArity = AbstractSetMultimapNode.this.nodeArity();

        @Override
        public void remove() {
          throw new UnsupportedOperationException();
        }

        @Override
        public AbstractSetMultimapNode<K, V> next() {
          if (!hasNext())
            throw new NoSuchElementException();
          return AbstractSetMultimapNode.this.getNode(nextIndex++);
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

    abstract ImmutableSet<V> getValue(final int index);

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
      final Iterator<K> it = new SetMultimapKeyIterator<>(this);

      int size = 0;
      while (it.hasNext()) {
        size += 1;
        it.next();
      }

      return size;
    }
  }

  protected static abstract class CompactSetMultimapNode<K, V>
      extends AbstractSetMultimapNode<K, V> {

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
    abstract CompactSetMultimapNode<K, V> getNode(final int index);

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

    abstract CompactSetMultimapNode<K, V> copyAndSetValue(final AtomicReference<Thread> mutator,
        final int bitpos, final ImmutableSet<V> valColl);

    abstract CompactSetMultimapNode<K, V> copyAndInsertValue(final AtomicReference<Thread> mutator,
        final int bitpos, final K key, final ImmutableSet<V> valColl);

    abstract CompactSetMultimapNode<K, V> copyAndRemoveValue(final AtomicReference<Thread> mutator,
        final int bitpos);

    abstract CompactSetMultimapNode<K, V> copyAndSetNode(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetMultimapNode<K, V> node);

    abstract CompactSetMultimapNode<K, V> copyAndMigrateFromInlineToNode(
        final AtomicReference<Thread> mutator, final int bitpos,
        final CompactSetMultimapNode<K, V> node);

    abstract CompactSetMultimapNode<K, V> copyAndMigrateFromNodeToInline(
        final AtomicReference<Thread> mutator, final int bitpos,
        final CompactSetMultimapNode<K, V> node);

    static final <K, V> CompactSetMultimapNode<K, V> mergeTwoKeyValPairs(final K key0,
        final ImmutableSet<V> valColl0, final int keyHash0, final K key1,
        final ImmutableSet<V> valColl1, final int keyHash1, final int shift) {
      assert !(key0.equals(key1));

      if (shift >= HASH_CODE_LENGTH) {
        // throw new
        // IllegalStateException("Hash collision not yet fixed.");
        return new HashCollisionSetMultimapNode_BleedingEdge<>(keyHash0,
            (K[]) new Object[] {key0, key1},
            (ImmutableSet<V>[]) new ImmutableSet[] {valColl0, valColl1});
      }

      final int mask0 = mask(keyHash0, shift);
      final int mask1 = mask(keyHash1, shift);

      if (mask0 != mask1) {
        // both nodes fit on same level
        final int dataMap = (int) (bitpos(mask0) | bitpos(mask1));

        if (mask0 < mask1) {
          return nodeOf(null, (int) (0), dataMap, new Object[] {key0, valColl0, key1, valColl1});
        } else {
          return nodeOf(null, (int) (0), dataMap, new Object[] {key1, valColl1, key0, valColl0});
        }
      } else {
        final CompactSetMultimapNode<K, V> node = mergeTwoKeyValPairs(key0, valColl0, keyHash0,
            key1, valColl1, keyHash1, shift + BIT_PARTITION_SIZE);
        // values fit on next level

        final int nodeMap = bitpos(mask0);
        return nodeOf(null, nodeMap, (int) (0), new Object[] {node});
      }
    }

    static final CompactSetMultimapNode EMPTY_NODE;

    static {

      EMPTY_NODE = new BitmapIndexedSetMultimapNode<>(null, (int) (0), (int) (0), new Object[] {});

    };

    static final <K, V> CompactSetMultimapNode<K, V> nodeOf(final AtomicReference<Thread> mutator,
        final int nodeMap, final int dataMap, final Object[] nodes) {
      return new BitmapIndexedSetMultimapNode<>(mutator, nodeMap, dataMap, nodes);
    }

    @SuppressWarnings("unchecked")
    static final <K, V> CompactSetMultimapNode<K, V> nodeOf(AtomicReference<Thread> mutator) {
      return EMPTY_NODE;
    }

    static final <K, V> CompactSetMultimapNode<K, V> nodeOf(AtomicReference<Thread> mutator,
        final int nodeMap, final int dataMap, final K key, final ImmutableSet<V> valColl) {
      assert nodeMap == 0;
      return nodeOf(mutator, (int) (0), dataMap, new Object[] {key, valColl});
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

    CompactSetMultimapNode<K, V> nodeAt(final int bitpos) {
      return getNode(nodeIndex(bitpos));
    }

    boolean containsKey(final K key, final int keyHash, final int shift) {
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
        return getNode(index).containsKey(key, keyHash, shift + BIT_PARTITION_SIZE);
      }

      return false;
    }

    boolean containsKey(final K key, final int keyHash, final int shift,
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
        return getNode(index).containsKey(key, keyHash, shift + BIT_PARTITION_SIZE, cmp);
      }

      return false;
    }

    Optional<ImmutableSet<V>> findByKey(final K key, final int keyHash, final int shift) {
      final int mask = mask(keyHash, shift);
      final int bitpos = bitpos(mask);

      if ((dataMap() & bitpos) != 0) { // inplace value
        final int index = dataIndex(bitpos);
        if (getKey(index).equals(key)) {
          final ImmutableSet<V> result = getValue(index);

          return Optional.of(result);
        }

        return Optional.empty();
      }

      if ((nodeMap() & bitpos) != 0) { // node (not value)
        final AbstractSetMultimapNode<K, V> subNode = nodeAt(bitpos);

        return subNode.findByKey(key, keyHash, shift + BIT_PARTITION_SIZE);
      }

      return Optional.empty();
    }

    Optional<ImmutableSet<V>> findByKey(final K key, final int keyHash, final int shift,
        final Comparator<Object> cmp) {
      final int mask = mask(keyHash, shift);
      final int bitpos = bitpos(mask);

      if ((dataMap() & bitpos) != 0) { // inplace value
        final int index = dataIndex(bitpos);
        if (cmp.compare(getKey(index), key) == 0) {
          final ImmutableSet<V> result = getValue(index);

          return Optional.of(result);
        }

        return Optional.empty();
      }

      if ((nodeMap() & bitpos) != 0) { // node (not value)
        final AbstractSetMultimapNode<K, V> subNode = nodeAt(bitpos);

        return subNode.findByKey(key, keyHash, shift + BIT_PARTITION_SIZE, cmp);
      }

      return Optional.empty();
    }

    CompactSetMultimapNode<K, V> updated(final AtomicReference<Thread> mutator, final K key,
        final V val, final int keyHash, final int shift, final SetMultimapResult<K, V> details) {
      final int mask = mask(keyHash, shift);
      final int bitpos = bitpos(mask);

      if ((dataMap() & bitpos) != 0) { // inplace value
        final int dataIndex = dataIndex(bitpos);
        final K currentKey = getKey(dataIndex);

        if (currentKey.equals(key)) {
          final ImmutableSet<V> valColl = getValue(dataIndex);

          final int valHash = val.hashCode();
          // if(valColl.contains(val, transformHashCode(valHash), 0))
          // {
          if (valColl.contains(val)) {
            return this;
          } else {
            // add new mapping
            // final ImmutableSet<V> valCollNew =
            // valColl.updated(null, val,
            // transformHashCode(valHash), 0, details);
            final ImmutableSet<V> valCollNew = valColl.__insert(val);

            details.modified();
            return copyAndSetValue(mutator, bitpos, valCollNew);
          }
        } else {
          final int valHash = val.hashCode();
          // final ImmutableSet<V> valColl =
          // CompactSetNode.EMPTY_NODE.updated(null, val,
          // transformHashCode(valHash), 0, details);
          final ImmutableSet<V> valColl = AbstractSpecialisedImmutableSet.setOf(val);

          final ImmutableSet<V> currentValNode = getValue(dataIndex);
          final CompactSetMultimapNode<K, V> subNodeNew = mergeTwoKeyValPairs(currentKey,
              currentValNode, transformHashCode(currentKey.hashCode()), key, valColl, keyHash,
              shift + BIT_PARTITION_SIZE);

          details.modified();
          return copyAndMigrateFromInlineToNode(mutator, bitpos, subNodeNew);
        }
      } else if ((nodeMap() & bitpos) != 0) { // node (not value)
        final CompactSetMultimapNode<K, V> subNode = nodeAt(bitpos);
        final CompactSetMultimapNode<K, V> subNodeNew =
            subNode.updated(mutator, key, val, keyHash, shift + BIT_PARTITION_SIZE, details);

        if (details.isModified()) {
          return copyAndSetNode(mutator, bitpos, subNodeNew);
        } else {
          return this;
        }
      } else {
        // no value
        final int valHash = val.hashCode();
        // final ImmutableSet<V> valColl =
        // CompactSetNode.EMPTY_NODE.updated(null, val,
        // transformHashCode(valHash), 0, details);
        final ImmutableSet<V> valColl = AbstractSpecialisedImmutableSet.setOf(val);

        details.modified();
        return copyAndInsertValue(mutator, bitpos, key, valColl);
      }
    }

    CompactSetMultimapNode<K, V> updated(final AtomicReference<Thread> mutator, final K key,
        final V val, final int keyHash, final int shift, final SetMultimapResult<K, V> details,
        final Comparator<Object> cmp) {
      final int mask = mask(keyHash, shift);
      final int bitpos = bitpos(mask);

      if ((dataMap() & bitpos) != 0) { // inplace value
        final int dataIndex = dataIndex(bitpos);
        final K currentKey = getKey(dataIndex);

        if (cmp.compare(currentKey, key) == 0) {
          final ImmutableSet<V> valColl = getValue(dataIndex);

          final int valHash = val.hashCode();
          // if(valColl.contains(val, transformHashCode(valHash), 0))
          // {
          if (valColl.contains(val)) {
            return this;
          } else {
            // add new mapping
            // final ImmutableSet<V> valCollNew =
            // valColl.updated(null, val,
            // transformHashCode(valHash), 0, details);
            final ImmutableSet<V> valCollNew = valColl.__insert(val);

            details.modified();
            return copyAndSetValue(mutator, bitpos, valCollNew);
          }
        } else {
          final int valHash = val.hashCode();
          // final ImmutableSet<V> valColl =
          // CompactSetNode.EMPTY_NODE.updated(null, val,
          // transformHashCode(valHash), 0, details);
          final ImmutableSet<V> valColl = AbstractSpecialisedImmutableSet.setOf(val);

          final ImmutableSet<V> currentValNode = getValue(dataIndex);
          final CompactSetMultimapNode<K, V> subNodeNew = mergeTwoKeyValPairs(currentKey,
              currentValNode, transformHashCode(currentKey.hashCode()), key, valColl, keyHash,
              shift + BIT_PARTITION_SIZE);

          details.modified();
          return copyAndMigrateFromInlineToNode(mutator, bitpos, subNodeNew);
        }
      } else if ((nodeMap() & bitpos) != 0) { // node (not value)
        final CompactSetMultimapNode<K, V> subNode = nodeAt(bitpos);
        final CompactSetMultimapNode<K, V> subNodeNew =
            subNode.updated(mutator, key, val, keyHash, shift + BIT_PARTITION_SIZE, details, cmp);

        if (details.isModified()) {
          return copyAndSetNode(mutator, bitpos, subNodeNew);
        } else {
          return this;
        }
      } else {
        // no value
        final int valHash = val.hashCode();
        // final ImmutableSet<V> valColl =
        // CompactSetNode.EMPTY_NODE.updated(null, val,
        // transformHashCode(valHash), 0, details);
        final ImmutableSet<V> valColl = AbstractSpecialisedImmutableSet.setOf(val);

        details.modified();
        return copyAndInsertValue(mutator, bitpos, key, valColl);
      }
    }

    CompactSetMultimapNode<K, V> removed(final AtomicReference<Thread> mutator, final K key,
        final V val, final int keyHash, final int shift, final SetMultimapResult<K, V> details) {
      final int mask = mask(keyHash, shift);
      final int bitpos = bitpos(mask);

      if ((dataMap() & bitpos) != 0) { // inplace value
        final int dataIndex = dataIndex(bitpos);

        if (getKey(dataIndex).equals(key)) {
          final ImmutableSet<V> valColl = getValue(dataIndex);

          final int valHash = val.hashCode();
          // if(valColl.contains(val, transformHashCode(valHash), 0))
          // {
          if (valColl.contains(val)) {
            details.updated(val);

            // remove mapping
            // final ImmutableSet<V> valCollNew =
            // valColl.removed(null, val,
            // transformHashCode(valHash), 0, details);
            final ImmutableSet<V> valCollNew = valColl.__remove(val);

            if (valCollNew.size() == 0) { // earlier: arity() == 0
              if (this.payloadArity() == 2 && this.nodeArity() == 0) {
                /*
                 * Create new node with remaining pair. The new node will a) either become the new
                 * root returned, or b) unwrapped and inlined during returning.
                 */
                final int newDataMap =
                    (shift == 0) ? (int) (dataMap() ^ bitpos) : bitpos(mask(keyHash, 0));

                if (dataIndex == 0) {
                  return CompactSetMultimapNode.<K, V>nodeOf(mutator, (int) 0, newDataMap,
                      getKey(1), getValue(1));
                } else {
                  return CompactSetMultimapNode.<K, V>nodeOf(mutator, (int) 0, newDataMap,
                      getKey(0), getValue(0));
                }
              } else {
                return copyAndRemoveValue(mutator, bitpos);
              }
            } else {
              return copyAndSetValue(mutator, bitpos, valCollNew);
            }
          } else {
            return this;
          }
        } else {
          return this;
        }
      } else if ((nodeMap() & bitpos) != 0) { // node (not value)
        final CompactSetMultimapNode<K, V> subNode = nodeAt(bitpos);
        final CompactSetMultimapNode<K, V> subNodeNew =
            subNode.removed(mutator, key, val, keyHash, shift + BIT_PARTITION_SIZE, details);

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

    CompactSetMultimapNode<K, V> removed(final AtomicReference<Thread> mutator, final K key,
        final V val, final int keyHash, final int shift, final SetMultimapResult<K, V> details,
        final Comparator<Object> cmp) {
      final int mask = mask(keyHash, shift);
      final int bitpos = bitpos(mask);

      if ((dataMap() & bitpos) != 0) { // inplace value
        final int dataIndex = dataIndex(bitpos);

        if (cmp.compare(getKey(dataIndex), key) == 0) {
          final ImmutableSet<V> valColl = getValue(dataIndex);

          final int valHash = val.hashCode();
          // if(valColl.contains(val, transformHashCode(valHash), 0))
          // {
          if (valColl.contains(val)) {
            details.updated(val);

            // remove mapping
            // final ImmutableSet<V> valCollNew =
            // valColl.removed(null, val,
            // transformHashCode(valHash), 0, details);
            final ImmutableSet<V> valCollNew = valColl.__remove(val);

            if (valCollNew.size() == 0) { // earlier: arity() == 0
              if (this.payloadArity() == 2 && this.nodeArity() == 0) {
                /*
                 * Create new node with remaining pair. The new node will a) either become the new
                 * root returned, or b) unwrapped and inlined during returning.
                 */
                final int newDataMap =
                    (shift == 0) ? (int) (dataMap() ^ bitpos) : bitpos(mask(keyHash, 0));

                if (dataIndex == 0) {
                  return CompactSetMultimapNode.<K, V>nodeOf(mutator, (int) 0, newDataMap,
                      getKey(1), getValue(1));
                } else {
                  return CompactSetMultimapNode.<K, V>nodeOf(mutator, (int) 0, newDataMap,
                      getKey(0), getValue(0));
                }
              } else {
                return copyAndRemoveValue(mutator, bitpos);
              }
            } else {
              return copyAndSetValue(mutator, bitpos, valCollNew);
            }
          } else {
            return this;
          }
        } else {
          return this;
        }
      } else if ((nodeMap() & bitpos) != 0) { // node (not value)
        final CompactSetMultimapNode<K, V> subNode = nodeAt(bitpos);
        final CompactSetMultimapNode<K, V> subNodeNew =
            subNode.removed(mutator, key, val, keyHash, shift + BIT_PARTITION_SIZE, details, cmp);

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

  protected static abstract class CompactMixedSetMultimapNode<K, V>
      extends CompactSetMultimapNode<K, V> {

    private final int nodeMap;
    private final int dataMap;

    CompactMixedSetMultimapNode(final AtomicReference<Thread> mutator, final int nodeMap,
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

  private static final class BitmapIndexedSetMultimapNode<K, V>
      extends CompactMixedSetMultimapNode<K, V> {

    final AtomicReference<Thread> mutator;
    final Object[] nodes;

    private BitmapIndexedSetMultimapNode(final AtomicReference<Thread> mutator, final int nodeMap,
        final int dataMap, final Object[] nodes) {
      super(mutator, nodeMap, dataMap);

      this.mutator = mutator;
      this.nodes = nodes;

      if (DEBUG) {

        assert (TUPLE_LENGTH * java.lang.Integer.bitCount(dataMap)
            + java.lang.Integer.bitCount(nodeMap) == nodes.length);

        for (int i = 0; i < TUPLE_LENGTH * payloadArity(); i++) {
          assert ((nodes[i] instanceof CompactSetMultimapNode) == false);
        }
        for (int i = TUPLE_LENGTH * payloadArity(); i < nodes.length; i++) {
          assert ((nodes[i] instanceof CompactSetMultimapNode) == true);
        }

        for (int i = 1; i < TUPLE_LENGTH * payloadArity(); i += 2) {
          assert ((nodes[i] instanceof ImmutableSet) == true);
        }
      }

      assert nodeInvariant();
    }

    @SuppressWarnings("unchecked")
    @Override
    K getKey(final int index) {
      return (K) nodes[TUPLE_LENGTH * index];
    }

    @SuppressWarnings("unchecked")
    @Override
    ImmutableSet<V> getValue(final int index) {
      return (ImmutableSet<V>) nodes[TUPLE_LENGTH * index + 1];
    }

    @SuppressWarnings("unchecked")
    @Override
    CompactSetMultimapNode<K, V> getNode(final int index) {
      return (CompactSetMultimapNode<K, V>) nodes[nodes.length - 1 - index];
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
      BitmapIndexedSetMultimapNode<?, ?> that = (BitmapIndexedSetMultimapNode<?, ?>) other;
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
    CompactSetMultimapNode<K, V> copyAndSetValue(final AtomicReference<Thread> mutator,
        final int bitpos, final ImmutableSet<V> valColl) {
      final int idx = TUPLE_LENGTH * dataIndex(bitpos) + 1;

      if (isAllowedToEdit(this.mutator, mutator)) {
        // no copying if already editable
        this.nodes[idx] = valColl;
        return this;
      } else {
        final Object[] src = this.nodes;
        final Object[] dst = (Object[]) new Object[src.length];

        // copy 'src' and set 1 element(s) at position 'idx'
        System.arraycopy(src, 0, dst, 0, src.length);
        dst[idx + 0] = valColl;

        return nodeOf(mutator, nodeMap(), dataMap(), dst);
      }
    }

    @Override
    CompactSetMultimapNode<K, V> copyAndSetNode(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetMultimapNode<K, V> node) {

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
    CompactSetMultimapNode<K, V> copyAndInsertValue(final AtomicReference<Thread> mutator,
        final int bitpos, final K key, final ImmutableSet<V> valColl) {
      final int idx = TUPLE_LENGTH * dataIndex(bitpos);

      final Object[] src = this.nodes;
      final Object[] dst = (Object[]) new Object[src.length + 2];

      // copy 'src' and insert 2 element(s) at position 'idx'
      System.arraycopy(src, 0, dst, 0, idx);
      dst[idx + 0] = key;
      dst[idx + 1] = valColl;
      System.arraycopy(src, idx, dst, idx + 2, src.length - idx);

      return nodeOf(mutator, nodeMap(), (int) (dataMap() | bitpos), dst);
    }

    @Override
    CompactSetMultimapNode<K, V> copyAndRemoveValue(final AtomicReference<Thread> mutator,
        final int bitpos) {
      final int idx = TUPLE_LENGTH * dataIndex(bitpos);

      final Object[] src = this.nodes;
      final Object[] dst = (Object[]) new Object[src.length - 2];

      // copy 'src' and remove 2 element(s) at position 'idx'
      System.arraycopy(src, 0, dst, 0, idx);
      System.arraycopy(src, idx + 2, dst, idx, src.length - idx - 2);

      return nodeOf(mutator, nodeMap(), (int) (dataMap() ^ bitpos), dst);
    }

    @Override
    CompactSetMultimapNode<K, V> copyAndMigrateFromInlineToNode(
        final AtomicReference<Thread> mutator, final int bitpos,
        final CompactSetMultimapNode<K, V> node) {

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

      return nodeOf(mutator, (int) (nodeMap() | bitpos), (int) (dataMap() ^ bitpos), dst);
    }

    @Override
    CompactSetMultimapNode<K, V> copyAndMigrateFromNodeToInline(
        final AtomicReference<Thread> mutator, final int bitpos,
        final CompactSetMultimapNode<K, V> node) {

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

      return nodeOf(mutator, (int) (nodeMap() ^ bitpos), (int) (dataMap() | bitpos), dst);
    }

  }

  private static final class HashCollisionSetMultimapNode_BleedingEdge<K, V>
      extends CompactSetMultimapNode<K, V> {
    private final K[] keys;
    private final ImmutableSet<V>[] vals;
    private final int hash;

    HashCollisionSetMultimapNode_BleedingEdge(final int hash, final K[] keys,
        final ImmutableSet<V>[] vals) {
      this.keys = keys;
      this.vals = vals;
      this.hash = hash;

      assert payloadArity() >= 2;
    }

    boolean containsKey(final K key, final int keyHash, final int shift) {
      if (this.hash == keyHash) {
        for (K k : keys) {
          if (k.equals(key)) {
            return true;
          }
        }
      }
      return false;
    }

    boolean containsKey(final K key, final int keyHash, final int shift,
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

    Optional<ImmutableSet<V>> findByKey(final K key, final int keyHash, final int shift) {
      for (int i = 0; i < keys.length; i++) {
        final K _key = keys[i];
        if (key.equals(_key)) {
          final ImmutableSet<V> valColl = vals[i];
          return Optional.of(valColl);
        }
      }
      return Optional.empty();
    }

    Optional<ImmutableSet<V>> findByKey(final K key, final int keyHash, final int shift,
        final Comparator<Object> cmp) {
      for (int i = 0; i < keys.length; i++) {
        final K _key = keys[i];
        if (cmp.compare(key, _key) == 0) {
          final ImmutableSet<V> valColl = vals[i];
          return Optional.of(valColl);
        }
      }
      return Optional.empty();
    }

    CompactSetMultimapNode<K, V> updated(final AtomicReference<Thread> mutator, final K key,
        final V val, final int keyHash, final int shift, final SetMultimapResult<K, V> details) {
      assert this.hash == keyHash;

      for (int idx = 0; idx < keys.length; idx++) {
        if (keys[idx].equals(key)) {
          final ImmutableSet<V> currentValColl = vals[idx];

          if (currentValColl.contains(val)) {
            return this;
          } else {
            // add new mapping
            final ImmutableSet<V> valCollNew = currentValColl.__insert(val);

            final ImmutableSet<V>[] src = this.vals;
            @SuppressWarnings("unchecked")
            final ImmutableSet<V>[] dst = (ImmutableSet<V>[]) new ImmutableSet[src.length];

            // copy 'src' and set 1 element(s) at position 'idx'
            System.arraycopy(src, 0, dst, 0, src.length);
            dst[idx + 0] = valCollNew;

            final CompactSetMultimapNode<K, V> thisNew =
                new HashCollisionSetMultimapNode_BleedingEdge<>(this.hash, this.keys, dst);

            details.modified();
            return thisNew;
          }
        }
      }

      // add new tuple
      final ImmutableSet<V> valCollNew = AbstractSpecialisedImmutableSet.setOf(val);

      @SuppressWarnings("unchecked")
      final K[] keysNew = (K[]) new Object[this.keys.length + 1];

      // copy 'this.keys' and insert 1 element(s) at position
      // 'keys.length'
      System.arraycopy(this.keys, 0, keysNew, 0, keys.length);
      keysNew[keys.length + 0] = key;
      System.arraycopy(this.keys, keys.length, keysNew, keys.length + 1,
          this.keys.length - keys.length);

      @SuppressWarnings("unchecked")
      final ImmutableSet<V>[] valsNew = (ImmutableSet<V>[]) new ImmutableSet[this.vals.length + 1];

      // copy 'this.vals' and insert 1 element(s) at position
      // 'vals.length'
      System.arraycopy(this.vals, 0, valsNew, 0, vals.length);
      valsNew[vals.length + 0] = valCollNew;
      System.arraycopy(this.vals, vals.length, valsNew, vals.length + 1,
          this.vals.length - vals.length);

      details.modified();
      return new HashCollisionSetMultimapNode_BleedingEdge<>(keyHash, keysNew, valsNew);
    }

    CompactSetMultimapNode<K, V> updated(final AtomicReference<Thread> mutator, final K key,
        final V val, final int keyHash, final int shift, final SetMultimapResult<K, V> details,
        final Comparator<Object> cmp) {
      assert this.hash == keyHash;

      for (int idx = 0; idx < keys.length; idx++) {
        if (cmp.compare(keys[idx], key) == 0) {
          final ImmutableSet<V> currentValColl = vals[idx];

          if (currentValColl.containsEquivalent(val, cmp)) {
            return this;
          } else {
            // add new mapping
            final ImmutableSet<V> valCollNew = currentValColl.__insert(val);

            final ImmutableSet<V>[] src = this.vals;
            @SuppressWarnings("unchecked")
            final ImmutableSet<V>[] dst = (ImmutableSet<V>[]) new ImmutableSet[src.length];

            // copy 'src' and set 1 element(s) at position 'idx'
            System.arraycopy(src, 0, dst, 0, src.length);
            dst[idx + 0] = valCollNew;

            final CompactSetMultimapNode<K, V> thisNew =
                new HashCollisionSetMultimapNode_BleedingEdge<>(this.hash, this.keys, dst);

            details.modified();
            return thisNew;
          }
        }
      }

      // add new tuple
      final ImmutableSet<V> valCollNew = AbstractSpecialisedImmutableSet.setOf(val);

      @SuppressWarnings("unchecked")
      final K[] keysNew = (K[]) new Object[this.keys.length + 1];

      // copy 'this.keys' and insert 1 element(s) at position
      // 'keys.length'
      System.arraycopy(this.keys, 0, keysNew, 0, keys.length);
      keysNew[keys.length + 0] = key;
      System.arraycopy(this.keys, keys.length, keysNew, keys.length + 1,
          this.keys.length - keys.length);

      @SuppressWarnings("unchecked")
      final ImmutableSet<V>[] valsNew = (ImmutableSet<V>[]) new ImmutableSet[this.vals.length + 1];

      // copy 'this.vals' and insert 1 element(s) at position
      // 'vals.length'
      System.arraycopy(this.vals, 0, valsNew, 0, vals.length);
      valsNew[vals.length + 0] = valCollNew;
      System.arraycopy(this.vals, vals.length, valsNew, vals.length + 1,
          this.vals.length - vals.length);

      details.modified();
      return new HashCollisionSetMultimapNode_BleedingEdge<>(keyHash, keysNew, valsNew);
    }

    CompactSetMultimapNode<K, V> removed(final AtomicReference<Thread> mutator, final K key,
        final V val, final int keyHash, final int shift, final SetMultimapResult<K, V> details) {
      for (int idx = 0; idx < keys.length; idx++) {
        if (keys[idx].equals(key)) {
          final ImmutableSet<V> currentValColl = getValue(idx);

          if (currentValColl.contains(val)) {
            details.updated(val);

            // remove tuple
            final ImmutableSet<V> valCollNew = currentValColl.__remove(val);

            if (valCollNew.size() != 0) {
              // update mapping
              @SuppressWarnings("unchecked")
              final ImmutableSet<V>[] valsNew =
                  (ImmutableSet<V>[]) new ImmutableSet[this.vals.length];

              // copy 'this.vals' and set 1 element(s) at position
              // 'idx'
              System.arraycopy(this.vals, 0, valsNew, 0, this.vals.length);
              valsNew[idx + 0] = valCollNew;

              return new HashCollisionSetMultimapNode_BleedingEdge<>(keyHash, keys, valsNew);
            } else {
              // drop mapping
              if (this.arity() == 2) {
                /*
                 * Create root node with singleton element. This node will be a) either be the new
                 * root returned, or b) unwrapped and inlined.
                 */
                final K theOtherKey = (idx == 0) ? keys[1] : keys[0];
                final ImmutableSet<V> theOtherVal = (idx == 0) ? vals[1] : vals[0];

                final int nodeMap = 0;
                final int dataMap = bitpos(mask(hash, 0));

                return CompactSetMultimapNode.<K, V>nodeOf(mutator, nodeMap, dataMap, theOtherKey,
                    theOtherVal);
              } else {
                @SuppressWarnings("unchecked")
                final K[] keysNew = (K[]) new Object[this.keys.length - 1];

                // copy 'this.keys' and remove 1 element(s) at
                // position 'idx'
                System.arraycopy(this.keys, 0, keysNew, 0, idx);
                System.arraycopy(this.keys, idx + 1, keysNew, idx, this.keys.length - idx - 1);

                @SuppressWarnings("unchecked")
                final ImmutableSet<V>[] valsNew =
                    (ImmutableSet<V>[]) new ImmutableSet[this.vals.length - 1];

                // copy 'this.vals' and remove 1 element(s) at
                // position 'idx'
                System.arraycopy(this.vals, 0, valsNew, 0, idx);
                System.arraycopy(this.vals, idx + 1, valsNew, idx, this.vals.length - idx - 1);

                return new HashCollisionSetMultimapNode_BleedingEdge<>(keyHash, keysNew, valsNew);
              }
            }
          } else {
            return this;
          }
        }
      }
      return this;
    }

    CompactSetMultimapNode<K, V> removed(final AtomicReference<Thread> mutator, final K key,
        final V val, final int keyHash, final int shift, final SetMultimapResult<K, V> details,
        final Comparator<Object> cmp) {
      for (int idx = 0; idx < keys.length; idx++) {
        if (cmp.compare(keys[idx], key) == 0) {
          final ImmutableSet<V> currentValColl = getValue(idx);

          if (currentValColl.contains(val)) {
            details.updated(val);

            // remove tuple
            final ImmutableSet<V> valCollNew = currentValColl.__removeEquivalent(val, cmp);

            if (valCollNew.size() != 0) {
              // update mapping
              @SuppressWarnings("unchecked")
              final ImmutableSet<V>[] valsNew =
                  (ImmutableSet<V>[]) new ImmutableSet[this.vals.length];

              // copy 'this.vals' and set 1 element(s) at position
              // 'idx'
              System.arraycopy(this.vals, 0, valsNew, 0, this.vals.length);
              valsNew[idx + 0] = valCollNew;

              return new HashCollisionSetMultimapNode_BleedingEdge<>(keyHash, keys, valsNew);
            } else {
              // drop mapping
              if (this.arity() == 2) {
                /*
                 * Create root node with singleton element. This node will be a) either be the new
                 * root returned, or b) unwrapped and inlined.
                 */
                final K theOtherKey = (idx == 0) ? keys[1] : keys[0];
                final ImmutableSet<V> theOtherVal = (idx == 0) ? vals[1] : vals[0];

                final int nodeMap = 0;
                final int dataMap = bitpos(mask(hash, 0));

                return CompactSetMultimapNode.<K, V>nodeOf(mutator, nodeMap, dataMap, theOtherKey,
                    theOtherVal);
              } else {
                @SuppressWarnings("unchecked")
                final K[] keysNew = (K[]) new Object[this.keys.length - 1];

                // copy 'this.keys' and remove 1 element(s) at
                // position 'idx'
                System.arraycopy(this.keys, 0, keysNew, 0, idx);
                System.arraycopy(this.keys, idx + 1, keysNew, idx, this.keys.length - idx - 1);

                @SuppressWarnings("unchecked")
                final ImmutableSet<V>[] valsNew =
                    (ImmutableSet<V>[]) new ImmutableSet[this.vals.length - 1];

                // copy 'this.vals' and remove 1 element(s) at
                // position 'idx'
                System.arraycopy(this.vals, 0, valsNew, 0, idx);
                System.arraycopy(this.vals, idx + 1, valsNew, idx, this.vals.length - idx - 1);

                return new HashCollisionSetMultimapNode_BleedingEdge<>(keyHash, keysNew, valsNew);
              }
            }
          } else {
            return this;
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
    ImmutableSet<V> getValue(final int index) {
      return vals[index];
    }

    @Override
    public CompactSetMultimapNode<K, V> getNode(int index) {
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

      HashCollisionSetMultimapNode_BleedingEdge<?, ?> that =
          (HashCollisionSetMultimapNode_BleedingEdge<?, ?>) other;

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
        final Object otherVal = that.getValue(i);

        for (int j = 0; j < keys.length; j++) {
          final K key = keys[j];
          final ImmutableSet<V> valColl = vals[j];

          if (key.equals(otherKey) && valColl.equals(otherVal)) {
            continue outerLoop;
          }
        }
        return false;
      }

      return true;
    }

    @Override
    CompactSetMultimapNode<K, V> copyAndSetValue(final AtomicReference<Thread> mutator,
        final int bitpos, final ImmutableSet<V> valColl) {
      throw new UnsupportedOperationException();
    }

    @Override
    CompactSetMultimapNode<K, V> copyAndInsertValue(final AtomicReference<Thread> mutator,
        final int bitpos, final K key, final ImmutableSet<V> valColl) {
      throw new UnsupportedOperationException();
    }

    @Override
    CompactSetMultimapNode<K, V> copyAndRemoveValue(final AtomicReference<Thread> mutator,
        final int bitpos) {
      throw new UnsupportedOperationException();
    }

    @Override
    CompactSetMultimapNode<K, V> copyAndSetNode(final AtomicReference<Thread> mutator,
        final int bitpos, final CompactSetMultimapNode<K, V> node) {
      throw new UnsupportedOperationException();
    }

    @Override
    CompactSetMultimapNode<K, V> copyAndMigrateFromInlineToNode(
        final AtomicReference<Thread> mutator, final int bitpos,
        final CompactSetMultimapNode<K, V> node) {
      throw new UnsupportedOperationException();
    }

    @Override
    CompactSetMultimapNode<K, V> copyAndMigrateFromNodeToInline(
        final AtomicReference<Thread> mutator, final int bitpos,
        final CompactSetMultimapNode<K, V> node) {
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
  private static abstract class AbstractSetMultimapIterator<K, V> {

    private static final int MAX_DEPTH = 7;

    protected int currentValueCursor;
    protected int currentValueLength;
    protected AbstractSetMultimapNode<K, V> currentValueNode;

    private int currentStackLevel = -1;
    private final int[] nodeCursorsAndLengths = new int[MAX_DEPTH * 2];

    @SuppressWarnings("unchecked")
    AbstractSetMultimapNode<K, V>[] nodes = new AbstractSetMultimapNode[MAX_DEPTH];

    AbstractSetMultimapIterator(AbstractSetMultimapNode<K, V> rootNode) {
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
          final AbstractSetMultimapNode<K, V> nextNode =
              nodes[currentStackLevel].getNode(nodeCursor);
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

  protected static class SetMultimapKeyIterator<K, V> extends AbstractSetMultimapIterator<K, V>
      implements Iterator<K> {

    SetMultimapKeyIterator(AbstractSetMultimapNode<K, V> rootNode) {
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

  protected static class SetMultimapValueIterator<K, V> extends AbstractSetMultimapIterator<K, V>
      implements Iterator<ImmutableSet<V>> {

    SetMultimapValueIterator(AbstractSetMultimapNode<K, V> rootNode) {
      super(rootNode);
    }

    @Override
    public ImmutableSet<V> next() {
      if (!hasNext()) {
        throw new NoSuchElementException();
      } else {
        return currentValueNode.getValue(currentValueCursor++);
      }
    }

  }

  protected static class SetMultimapTupleIterator<K, V, T> extends AbstractSetMultimapIterator<K, V>
      implements Iterator<T> {

    final BiFunction<K, V, T> tupleOf;

    K currentKey = null;
    V currentValue = null;
    Iterator<V> currentSetIterator = Collections.emptyIterator();

    SetMultimapTupleIterator(AbstractSetMultimapNode<K, V> rootNode,
        final BiFunction<K, V, T> tupleOf) {
      super(rootNode);
      this.tupleOf = tupleOf;
    }

    public boolean hasNext() {
      if (currentSetIterator.hasNext()) {
        return true;
      } else {
        if (super.hasNext()) {
          currentKey = currentValueNode.getKey(currentValueCursor);
          currentSetIterator = currentValueNode.getValue(currentValueCursor).iterator();
          currentValueCursor++;

          return true;
        } else {
          return false;
        }
      }
    }

    @Override
    public T next() {
      if (!hasNext()) {
        throw new NoSuchElementException();
      } else {
        currentValue = currentSetIterator.next();
        return tupleOf.apply(currentKey, currentValue);
      }
    }

  }

  /**
   * Iterator that first iterates over inlined-values and then continues depth first recursively.
   */
  private static class TrieSetMultimap_BleedingEdgeNodeIterator<K, V>
      implements Iterator<AbstractSetMultimapNode<K, V>> {

    final Deque<Iterator<? extends AbstractSetMultimapNode<K, V>>> nodeIteratorStack;

    TrieSetMultimap_BleedingEdgeNodeIterator(AbstractSetMultimapNode<K, V> rootNode) {
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
    public AbstractSetMultimapNode<K, V> next() {
      if (!hasNext()) {
        throw new NoSuchElementException();
      }

      AbstractSetMultimapNode<K, V> innerNode = nodeIteratorStack.peek().next();

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

  static final class TransientTrieSetMultimap_BleedingEdge<K, V>
      implements TransientSetMultimap<K, V> {
    final private AtomicReference<Thread> mutator;
    private AbstractSetMultimapNode<K, V> rootNode;
    private int hashCode;
    private int cachedSize;

    TransientTrieSetMultimap_BleedingEdge(
        TrieSetMultimap_ChampBasedPrototype<K, V> trieSetMultimap_BleedingEdge) {
      this.mutator = new AtomicReference<Thread>(Thread.currentThread());
      this.rootNode = trieSetMultimap_BleedingEdge.rootNode;
      this.hashCode = trieSetMultimap_BleedingEdge.hashCode;
      this.cachedSize = trieSetMultimap_BleedingEdge.cachedSize;
      if (DEBUG) {
        assert checkHashCodeAndSize(hashCode, cachedSize);
      }
    }

    private boolean checkHashCodeAndSize(final int targetHash, final int targetSize) {
      int hash = 0;
      int size = 0;

      for (Iterator<Map.Entry<K, V>> it = entryIterator(); it.hasNext();) {
        final Map.Entry<K, V> entry = it.next();
        final K key = entry.getKey();
        final V val = entry.getValue();

        hash += key.hashCode() ^ val.hashCode();
        size += 1;
      }

      return hash == targetHash && size == targetSize;
    }

    public V put(final K key, final V val) {
      throw new UnsupportedOperationException();
    }

    public void putAll(final SetMultimap<? extends K, ? extends V> m) {
      throw new UnsupportedOperationException();
    }

    public void clear() {
      throw new UnsupportedOperationException();
    }

    public V remove(final Object key, final Object val) {
      throw new UnsupportedOperationException();
    }

    public boolean containsKey(final Object o) {
      try {
        @SuppressWarnings("unchecked")
        final K key = (K) o;
        return rootNode.containsKey(key, transformHashCode(key.hashCode()), 0);
      } catch (ClassCastException unused) {
        return false;
      }
    }

    public boolean containsKeyEquivalent(final Object o, final Comparator<Object> cmp) {
      try {
        @SuppressWarnings("unchecked")
        final K key = (K) o;
        return rootNode.containsKey(key, transformHashCode(key.hashCode()), 0, cmp);
      } catch (ClassCastException unused) {
        return false;
      }
    }

    public boolean containsValue(final Object o) {
      for (Iterator<V> iterator = valueIterator(); iterator.hasNext();) {
        if (iterator.next().equals(o)) {
          return true;
        }
      }
      return false;
    }

    public boolean containsValueEquivalent(final Object o, final Comparator<Object> cmp) {
      for (Iterator<V> iterator = valueIterator(); iterator.hasNext();) {
        if (cmp.compare(iterator.next(), o) == 0) {
          return true;
        }
      }
      return false;
    }

    public boolean containsEntry(final Object o0, final Object o1) {
      try {
        @SuppressWarnings("unchecked")
        final K key = (K) o0;
        @SuppressWarnings("unchecked")
        final V val = (V) o1;
        final Optional<ImmutableSet<V>> result =
            rootNode.findByKey(key, transformHashCode(key.hashCode()), 0);

        if (result.isPresent()) {
          return result.get().contains(val);
        } else {
          return false;
        }
      } catch (ClassCastException unused) {
        return false;
      }
    }

    public boolean containsEntryEquivalent(final Object o0, final Object o1,
        final Comparator<Object> cmp) {
      try {
        @SuppressWarnings("unchecked")
        final K key = (K) o0;
        @SuppressWarnings("unchecked")
        final V val = (V) o1;
        final Optional<ImmutableSet<V>> result =
            rootNode.findByKey(key, transformHashCode(key.hashCode()), 0, cmp);

        if (result.isPresent()) {
          return result.get().containsEquivalent(val, cmp);
        } else {
          return false;
        }
      } catch (ClassCastException unused) {
        return false;
      }
    }

    public ImmutableSet<V> get(final Object o) {
      try {
        @SuppressWarnings("unchecked")
        final K key = (K) o;
        final Optional<ImmutableSet<V>> result =
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

    public ImmutableSet<V> getEquivalent(final Object o, final Comparator<Object> cmp) {
      try {
        @SuppressWarnings("unchecked")
        final K key = (K) o;
        final Optional<ImmutableSet<V>> result =
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

    public boolean __insert(final K key, final V val) {
      if (mutator.get() == null) {
        throw new IllegalStateException("Transient already frozen.");
      }

      final int keyHash = key.hashCode();
      final SetMultimapResult<K, V> details = SetMultimapResult.unchanged();

      final CompactSetMultimapNode<K, V> newRootNode =
          rootNode.updated(mutator, key, val, transformHashCode(keyHash), 0, details);

      if (details.isModified()) {

        final int valHashNew = val.hashCode();
        rootNode = newRootNode;
        hashCode += (keyHash ^ valHashNew);
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

    public boolean __insertEquivalent(final K key, final V val, final Comparator<Object> cmp) {
      if (mutator.get() == null) {
        throw new IllegalStateException("Transient already frozen.");
      }

      final int keyHash = key.hashCode();
      final SetMultimapResult<K, V> details = SetMultimapResult.unchanged();

      final CompactSetMultimapNode<K, V> newRootNode =
          rootNode.updated(mutator, key, val, transformHashCode(keyHash), 0, details, cmp);

      if (details.isModified()) {

        final int valHashNew = val.hashCode();
        rootNode = newRootNode;
        hashCode += (keyHash ^ valHashNew);
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

    public boolean __insertAll(final SetMultimap<? extends K, ? extends V> setMultimap) {
      boolean modified = false;

      for (Map.Entry<? extends K, ? extends V> entry : setMultimap.entrySet()) {
        modified |= this.__insert(entry.getKey(), entry.getValue());
      }

      return modified;
    }

    public boolean __insertAllEquivalent(
        final SetMultimap<? extends K, ? extends V> setMultimap,
        final Comparator<Object> cmp) {
      boolean modified = false;

      for (Map.Entry<? extends K, ? extends V> entry : setMultimap.entrySet()) {
        modified |= this.__insertEquivalent(entry.getKey(), entry.getValue(), cmp);
      }

      return modified;
    }

    public boolean __removeTuple(final K key, final V val) {
      if (mutator.get() == null) {
        throw new IllegalStateException("Transient already frozen.");
      }

      final int keyHash = key.hashCode();
      final SetMultimapResult<K, V> details = SetMultimapResult.unchanged();

      final CompactSetMultimapNode<K, V> newRootNode =
          rootNode.removed(mutator, key, val, transformHashCode(keyHash), 0, details);

      if (details.isModified()) {
        assert details.hasReplacedValue();
        final int valHash = details.getReplacedValue().hashCode();

        rootNode = newRootNode;
        hashCode = hashCode - (keyHash ^ valHash);
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

    public boolean __removeTupleEquivalent(final K key, final V val, final Comparator<Object> cmp) {
      if (mutator.get() == null) {
        throw new IllegalStateException("Transient already frozen.");
      }

      final int keyHash = key.hashCode();
      final SetMultimapResult<K, V> details = SetMultimapResult.unchanged();

      final CompactSetMultimapNode<K, V> newRootNode =
          rootNode.removed(mutator, key, val, transformHashCode(keyHash), 0, details, cmp);

      if (details.isModified()) {
        assert details.hasReplacedValue();
        final int valHash = details.getReplacedValue().hashCode();

        rootNode = newRootNode;
        hashCode = hashCode - (keyHash ^ valHash);
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

    public int size() {
      return cachedSize;
    }

    public boolean isEmpty() {
      return cachedSize == 0;
    }

    public Iterator<K> keyIterator() {
      return new TransientSetMultimapKeyIterator<>(this);
    }

    public Iterator<V> valueIterator() {
      return valueCollectionsStream().flatMap(Set::stream).iterator();
    }

    public Iterator<Map.Entry<K, V>> entryIterator() {
      return new TransientSetMultimapTupleIterator<>(this,
          AbstractSpecialisedImmutableMap::entryOf);
    }

    public <T> Iterator<T> tupleIterator(final BiFunction<K, V, T> tupleOf) {
      return new TransientSetMultimapTupleIterator<>(this, tupleOf);
    }

    private Spliterator<ImmutableSet<V>> valueCollectionsSpliterator() {
      /*
       * TODO: specialize between mutable / immutable ({@see Spliterator.IMMUTABLE})
       */
      int characteristics = Spliterator.NONNULL | Spliterator.SIZED | Spliterator.SUBSIZED;
      return Spliterators.spliterator(new SetMultimapValueIterator<>(rootNode), size(),
          characteristics);
    }

    private Stream<ImmutableSet<V>> valueCollectionsStream() {
      boolean isParallel = false;
      return StreamSupport.stream(valueCollectionsSpliterator(), isParallel);
    }

    public static class TransientSetMultimapKeyIterator<K, V> extends SetMultimapKeyIterator<K, V> {
      final TransientTrieSetMultimap_BleedingEdge<K, V> collection;
      K lastKey;

      public TransientSetMultimapKeyIterator(
          final TransientTrieSetMultimap_BleedingEdge<K, V> collection) {
        super(collection.rootNode);
        this.collection = collection;
      }

      public K next() {
        return lastKey = super.next();
      }

      public void remove() {
        throw new UnsupportedOperationException();
      }
    }

    public static class TransientSetMultimapValueIterator<K, V>
        extends SetMultimapValueIterator<K, V> {
      final TransientTrieSetMultimap_BleedingEdge<K, V> collection;

      public TransientSetMultimapValueIterator(
          final TransientTrieSetMultimap_BleedingEdge<K, V> collection) {
        super(collection.rootNode);
        this.collection = collection;
      }

      public ImmutableSet<V> next() {
        return super.next();
      }

      public void remove() {
        throw new UnsupportedOperationException();
      }
    }

    public static class TransientSetMultimapTupleIterator<K, V, T>
        extends SetMultimapTupleIterator<K, V, T> {
      final TransientTrieSetMultimap_BleedingEdge<K, V> collection;

      public TransientSetMultimapTupleIterator(
          final TransientTrieSetMultimap_BleedingEdge<K, V> collection,
          final BiFunction<K, V, T> tupleOf) {
        super(collection.rootNode, tupleOf);
        this.collection = collection;
      }

      public T next() {
        return super.next();
      }

      public void remove() {
        // TODO: test removal at iteration rigorously
        collection.__removeTuple(currentKey, currentValue);
      }
    }

    @Override
    public Set<K> keySet() {
      Set<K> keySet = null;

      if (keySet == null) {
        keySet = new AbstractSet<K>() {
          @Override
          public Iterator<K> iterator() {
            return TransientTrieSetMultimap_BleedingEdge.this.keyIterator();
          }

          @Override
          public int size() {
            return TransientTrieSetMultimap_BleedingEdge.this.sizeDistinct();
          }

          @Override
          public boolean isEmpty() {
            return TransientTrieSetMultimap_BleedingEdge.this.isEmpty();
          }

          @Override
          public void clear() {
            TransientTrieSetMultimap_BleedingEdge.this.clear();
          }

          @Override
          public boolean contains(Object k) {
            return TransientTrieSetMultimap_BleedingEdge.this.containsKey(k);
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
            return TransientTrieSetMultimap_BleedingEdge.this.valueIterator();
          }

          @Override
          public int size() {
            return TransientTrieSetMultimap_BleedingEdge.this.size();
          }

          @Override
          public boolean isEmpty() {
            return TransientTrieSetMultimap_BleedingEdge.this.isEmpty();
          }

          @Override
          public void clear() {
            TransientTrieSetMultimap_BleedingEdge.this.clear();
          }

          @Override
          public boolean contains(Object v) {
            return TransientTrieSetMultimap_BleedingEdge.this.containsValue(v);
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
            return TransientTrieSetMultimap_BleedingEdge.this.size();
          }

          @Override
          public boolean isEmpty() {
            return TransientTrieSetMultimap_BleedingEdge.this.isEmpty();
          }

          @Override
          public void clear() {
            TransientTrieSetMultimap_BleedingEdge.this.clear();
          }

          @Override
          public boolean contains(Object k) {
            return TransientTrieSetMultimap_BleedingEdge.this.containsKey(k);
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

      if (other instanceof TransientTrieSetMultimap_BleedingEdge) {
        TransientTrieSetMultimap_BleedingEdge<?, ?> that =
            (TransientTrieSetMultimap_BleedingEdge<?, ?>) other;

        if (this.cachedSize != that.cachedSize) {
          return false;
        }

        if (this.hashCode != that.hashCode) {
          return false;
        }

        return rootNode.equals(that.rootNode);
      } else if (other instanceof SetMultimap) {
        SetMultimap that = (SetMultimap) other;

        if (this.size() != that.size())
          return false;

        for (@SuppressWarnings("unchecked")
        Iterator<Map.Entry> it = that.entrySet().iterator(); it.hasNext();) {
          Map.Entry entry = it.next();

          try {
            @SuppressWarnings("unchecked")
            final K key = (K) entry.getKey();
            final Optional<ImmutableSet<V>> result =
                rootNode.findByKey(key, transformHashCode(key.hashCode()), 0);

            if (!result.isPresent()) {
              return false;
            } else {
              @SuppressWarnings("unchecked")
              final ImmutableSet<V> valColl = (ImmutableSet<V>) entry.getValue();

              if (!result.get().equals(valColl)) {
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
      return hashCode;
    }

    @Override
    public ImmutableSetMultimap<K, V> freeze() {
      if (mutator.get() == null) {
        throw new IllegalStateException("Transient already frozen.");
      }

      mutator.set(null);
      return new TrieSetMultimap_ChampBasedPrototype<K, V>(rootNode, hashCode, cachedSize);
    }
  }

  @Override
  public ImmutableSetMultimap<K, V> __remove(K key) {
    throw new UnsupportedOperationException();
  }

  @Override
  public ImmutableSetMultimap<K, V> __removeEquivalent(K key, Comparator<Object> cmp) {
    throw new UnsupportedOperationException();
  }

}
