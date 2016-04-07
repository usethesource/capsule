/*******************************************************************************
 * Copyright (c) 2013-2015 CWI All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *
 * * Michael Steindorfer - Michael.Steindorfer@cwi.nl - CWI
 *******************************************************************************/
package io.usethesource.capsule;

import static io.usethesource.capsule.BitmapUtils.filter;
import static io.usethesource.capsule.BitmapUtils.index01;
import static io.usethesource.capsule.BitmapUtils.index10;
import static io.usethesource.capsule.BitmapUtils.index11;
import static io.usethesource.capsule.SetMultimapUtils.PATTERN_DATA_COLLECTION;
import static io.usethesource.capsule.SetMultimapUtils.PATTERN_DATA_SINGLETON;
import static io.usethesource.capsule.SetMultimapUtils.PATTERN_EMPTY;
import static io.usethesource.capsule.SetMultimapUtils.PATTERN_NODE;
import static io.usethesource.capsule.SetMultimapUtils.setBitPattern;
import static io.usethesource.capsule.SetMultimapUtils.setOf;
import static io.usethesource.capsule.TrieSetMultimap_HHAMT.EitherSingletonOrCollection.Type.COLLECTION;
import static io.usethesource.capsule.TrieSetMultimap_HHAMT.EitherSingletonOrCollection.Type.SINGLETON;

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
import java.util.Optional;
import java.util.Set;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiFunction;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import io.usethesource.capsule.TrieSetMultimap_HHAMT.EitherSingletonOrCollection.Type;

@SuppressWarnings("rawtypes")
public class TrieSetMultimap_HHAMT<K, V> implements ImmutableSetMultimap<K, V> {

  @SuppressWarnings("unchecked")
  private static final TrieSetMultimap_HHAMT EMPTY_SETMULTIMAP =
      new TrieSetMultimap_HHAMT(CompactSetMultimapNode.EMPTY_NODE, 0, 0);

  private static final boolean DEBUG = false;

  private final AbstractSetMultimapNode<K, V> rootNode;
  private final int hashCode;
  private final int cachedSize;

  TrieSetMultimap_HHAMT(AbstractSetMultimapNode<K, V> rootNode, int hashCode, int cachedSize) {
    this.rootNode = rootNode;
    this.hashCode = hashCode;
    this.cachedSize = cachedSize;
    if (DEBUG) {
      assert checkHashCodeAndSize(hashCode, cachedSize);
    }
  }

  @SuppressWarnings("unchecked")
  public static final <K, V> ImmutableSetMultimap<K, V> of() {
    return TrieSetMultimap_HHAMT.EMPTY_SETMULTIMAP;
  }

  @SuppressWarnings("unchecked")
  public static final <K, V> ImmutableSetMultimap<K, V> of(K key, V... values) {
    ImmutableSetMultimap<K, V> result = TrieSetMultimap_HHAMT.EMPTY_SETMULTIMAP;

    for (V value : values) {
      result = result.__insert(key, value);
    }

    return result;
  }

  @SuppressWarnings("unchecked")
  public static final <K, V> TransientSetMultimap<K, V> transientOf() {
    return TrieSetMultimap_HHAMT.EMPTY_SETMULTIMAP.asTransient();
  }

  @SuppressWarnings("unchecked")
  public static final <K, V> TransientSetMultimap<K, V> transientOf(K key, V... values) {
    final TransientSetMultimap<K, V> result = TrieSetMultimap_HHAMT.EMPTY_SETMULTIMAP.asTransient();

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

  @Override
  public boolean containsKey(final Object o) {
    try {
      @SuppressWarnings("unchecked")
      final K key = (K) o;
      return rootNode.containsKey(key, transformHashCode(key.hashCode()), 0);
    } catch (ClassCastException unused) {
      return false;
    }
  }

  @Override
  public boolean containsKeyEquivalent(final Object o, final Comparator<Object> cmp) {
    throw new UnsupportedOperationException("Not yet implemented.");
  }

  @Override
  public boolean containsValue(final Object o) {
    for (Iterator<V> iterator = valueIterator(); iterator.hasNext();) {
      if (iterator.next().equals(o)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public boolean containsValueEquivalent(final Object o, final Comparator<Object> cmp) {
    for (Iterator<V> iterator = valueIterator(); iterator.hasNext();) {
      if (cmp.compare(iterator.next(), o) == 0) {
        return true;
      }
    }
    return false;
  }

  @Override
  public boolean containsEntry(final Object o0, final Object o1) {
    try {
      @SuppressWarnings("unchecked")
      final K key = (K) o0;
      @SuppressWarnings("unchecked")
      final V val = (V) o1;
      return rootNode.containsTuple(key, val, transformHashCode(key.hashCode()), 0);
    } catch (ClassCastException unused) {
      return false;
    }
  }

  @Override
  public boolean containsEntryEquivalent(final Object o0, final Object o1,
      final Comparator<Object> cmp) {
    throw new UnsupportedOperationException("Not yet implemented.");
  }

  @Override
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

  @Override
  public ImmutableSet<V> getEquivalent(final Object o, final Comparator<Object> cmp) {
    throw new UnsupportedOperationException("Not yet implemented.");
  }

  @Override
  public ImmutableSetMultimap<K, V> __put(K key, V val) {
    final int keyHash = key.hashCode();
    final SetMultimapResult<K, V> details = SetMultimapResult.unchanged();

    final CompactSetMultimapNode<K, V> newRootNode =
        rootNode.updated(null, key, val, transformHashCode(keyHash), 0, details);

    if (details.isModified()) {
      if (details.hasReplacedValue()) {
        if (details.getType() == EitherSingletonOrCollection.Type.SINGLETON) {
          final int valHashOld = details.getReplacedValue().hashCode();
          final int valHashNew = val.hashCode();

          return new TrieSetMultimap_HHAMT<K, V>(newRootNode,
              hashCode + ((keyHash ^ valHashNew)) - ((keyHash ^ valHashOld)), cachedSize);
        } else {
          int sumOfReplacedHashes = 0;

          for (V replaceValue : details.getReplacedCollection()) {
            sumOfReplacedHashes += (keyHash ^ replaceValue.hashCode());
          }

          final int valHashNew = val.hashCode();

          return new TrieSetMultimap_HHAMT<K, V>(newRootNode,
              hashCode + ((keyHash ^ valHashNew)) - sumOfReplacedHashes,
              cachedSize - details.getReplacedCollection().size() + 1);
        }
      }

      final int valHash = val.hashCode();
      return new TrieSetMultimap_HHAMT<K, V>(newRootNode, hashCode + ((keyHash ^ valHash)),
          cachedSize + 1);
    }

    return this;
  }

  @Override
  public ImmutableSetMultimap<K, V> __insert(final K key, final V val) {
    final int keyHash = key.hashCode();
    final SetMultimapResult<K, V> details = SetMultimapResult.unchanged();

    final CompactSetMultimapNode<K, V> newRootNode =
        rootNode.inserted(null, key, val, transformHashCode(keyHash), 0, details);

    if (details.isModified()) {
      final int valHash = val.hashCode();
      return new TrieSetMultimap_HHAMT<K, V>(newRootNode, hashCode + ((keyHash ^ valHash)),
          cachedSize + 1);
    }

    return this;
  }

  @Override
  public ImmutableSetMultimap<K, V> __insertEquivalent(final K key, final V val,
      final Comparator<Object> cmp) {
    throw new UnsupportedOperationException("Not yet implemented.");
  }

  @Override
  public ImmutableSetMultimap<K, V> __insertAll(
      final SetMultimap_Legacy<? extends K, ? extends V> setMultimap) {
    final TransientSetMultimap<K, V> tmpTransient = this.asTransient();
    tmpTransient.__insertAll(setMultimap);
    return tmpTransient.freeze();
  }

  @Override
  public ImmutableSetMultimap<K, V> __insertAllEquivalent(
      final SetMultimap_Legacy<? extends K, ? extends V> setMultimap, final Comparator<Object> cmp) {
    final TransientSetMultimap<K, V> tmpTransient = this.asTransient();
    tmpTransient.__insertAllEquivalent(setMultimap, cmp);
    return tmpTransient.freeze();
  }

  @Override
  public ImmutableSetMultimap<K, V> __removeEntry(final K key, final V val) {
    final int keyHash = key.hashCode();
    final SetMultimapResult<K, V> details = SetMultimapResult.unchanged();

    final CompactSetMultimapNode<K, V> newRootNode =
        rootNode.removed(null, key, val, transformHashCode(keyHash), 0, details);

    if (details.isModified()) {
      assert details.hasReplacedValue();
      final int valHash = details.getReplacedValue().hashCode();
      return new TrieSetMultimap_HHAMT<K, V>(newRootNode, hashCode - ((keyHash ^ valHash)),
          cachedSize - 1);
    }

    return this;
  }

  @Override
  public ImmutableSetMultimap<K, V> __removeEntryEquivalent(final K key, final V val,
      final Comparator<Object> cmp) {
    throw new UnsupportedOperationException("Not yet implemented.");
  }

  @Override
  public ImmutableSetMultimap<K, V> __remove(K key) {
    final int keyHash = key.hashCode();
    final SetMultimapResult<K, V> details = SetMultimapResult.unchanged();

    final CompactSetMultimapNode<K, V> newRootNode =
        rootNode.removedAll(null, key, transformHashCode(keyHash), 0, details);

    if (details.isModified()) {
      assert details.hasReplacedValue();

      if (details.getType() == EitherSingletonOrCollection.Type.SINGLETON) {
        final int valHash = details.getReplacedValue().hashCode();
        return new TrieSetMultimap_HHAMT<K, V>(newRootNode, hashCode - ((keyHash ^ valHash)),
            cachedSize - 1);
      } else {
        int sumOfReplacedHashes = 0;

        for (V replaceValue : details.getReplacedCollection()) {
          sumOfReplacedHashes += (keyHash ^ replaceValue.hashCode());
        }

        return new TrieSetMultimap_HHAMT<K, V>(newRootNode, hashCode - sumOfReplacedHashes,
            cachedSize - details.getReplacedCollection().size());
      }
    }

    return this;
  }

  @Override
  public ImmutableSetMultimap<K, V> __removeEquivalent(K key, Comparator<Object> cmp) {
    throw new UnsupportedOperationException("Not yet implemented.");
  }

  @Override
  public V put(final K key, final V val) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void putAll(final SetMultimap_Legacy<? extends K, ? extends V> m) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void clear() {
    throw new UnsupportedOperationException();
  }

  @Override
  public V remove(final Object key, final Object val) {
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
    return new SetMultimapKeyIterator<>(rootNode);
  }

  @Override
  public Iterator<V> valueIterator() {
    return valueCollectionsStream().flatMap(Set::stream).iterator();
  }

  @Override
  public Iterator<Map.Entry<K, V>> entryIterator() {
    return new SetMultimapTupleIterator<>(rootNode, AbstractSpecialisedImmutableMap::entryOf);
  }

  @Override
  public Iterator<Map.Entry<K, Object>> nativeEntryIterator() {
    return new SetMultimapNativeTupleIterator<>(rootNode);
  }

  @Override
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
          return TrieSetMultimap_HHAMT.this.keyIterator();
        }

        @Override
        public int size() {
          return TrieSetMultimap_HHAMT.this.sizeDistinct();
        }

        @Override
        public boolean isEmpty() {
          return TrieSetMultimap_HHAMT.this.isEmpty();
        }

        @Override
        public void clear() {
          TrieSetMultimap_HHAMT.this.clear();
        }

        @Override
        public boolean contains(Object k) {
          return TrieSetMultimap_HHAMT.this.containsKey(k);
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
          return TrieSetMultimap_HHAMT.this.valueIterator();
        }

        @Override
        public int size() {
          return TrieSetMultimap_HHAMT.this.size();
        }

        @Override
        public boolean isEmpty() {
          return TrieSetMultimap_HHAMT.this.isEmpty();
        }

        @Override
        public void clear() {
          TrieSetMultimap_HHAMT.this.clear();
        }

        @Override
        public boolean contains(Object v) {
          return TrieSetMultimap_HHAMT.this.containsValue(v);
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
          return TrieSetMultimap_HHAMT.this.size();
        }

        @Override
        public boolean isEmpty() {
          return TrieSetMultimap_HHAMT.this.isEmpty();
        }

        @Override
        public void clear() {
          TrieSetMultimap_HHAMT.this.clear();
        }

        @Override
        public boolean contains(Object k) {
          return TrieSetMultimap_HHAMT.this.containsKey(k);
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

    if (other instanceof TrieSetMultimap_HHAMT) {
      TrieSetMultimap_HHAMT<?, ?> that = (TrieSetMultimap_HHAMT<?, ?>) other;

      if (this.cachedSize != that.cachedSize) {
        return false;
      }

      /*
       * TODO: Fix __put(K key, ImmutableSet<V> valColl) for batch insertion and re-enabled
       * fast-fail check.
       */
//      if (this.hashCode != that.hashCode) {
//        return false;
//      }

      return rootNode.equals(that.rootNode);
    } else if (other instanceof SetMultimap_Legacy) {
      SetMultimap_Legacy that = (SetMultimap_Legacy) other;

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

  // /*
  // * For analysis purposes only. Payload X Node
  // */
  // protected int[][] arityCombinationsHistogram() {
  // final Iterator<AbstractSetMultimapNode<K, V>> it = nodeIterator();
  // final int[][] sumArityCombinations = new int[33][33];
  //
  // while (it.hasNext()) {
  // final AbstractSetMultimapNode<K, V> node = it.next();
  // sumArityCombinations[node.payloadArity()][node.nodeArity()] += 1;
  // }
  //
  // return sumArityCombinations;
  // }
  //
  // /*
  // * For analysis purposes only.
  // */
  // protected int[] arityHistogram() {
  // final int[][] sumArityCombinations = arityCombinationsHistogram();
  // final int[] sumArity = new int[33];
  //
  // final int maxArity = 32; // TODO: factor out constant
  //
  // for (int j = 0; j <= maxArity; j++) {
  // for (int maxRestArity = maxArity - j, k = 0; k <= maxRestArity - j; k++) {
  // sumArity[j + k] += sumArityCombinations[j][k];
  // }
  // }
  //
  // return sumArity;
  // }
  //
  // /*
  // * For analysis purposes only.
  // */
  // public void printStatistics() {
  // final int[][] sumArityCombinations = arityCombinationsHistogram();
  // final int[] sumArity = arityHistogram();
  // final int sumNodes = getNodeCount();
  //
  // final int[] cumsumArity = new int[33];
  // for (int cumsum = 0, i = 0; i < 33; i++) {
  // cumsum += sumArity[i];
  // cumsumArity[i] = cumsum;
  // }
  //
  // final float threshhold = 0.01f; // for printing results
  // for (int i = 0; i < 33; i++) {
  // float arityPercentage = (float) (sumArity[i]) / sumNodes;
  // float cumsumArityPercentage = (float) (cumsumArity[i]) / sumNodes;
  //
  // if (arityPercentage != 0 && arityPercentage >= threshhold) {
  // // details per level
  // StringBuilder bldr = new StringBuilder();
  // int max = i;
  // for (int j = 0; j <= max; j++) {
  // for (int k = max - j; k <= max - j; k++) {
  // float arityCombinationsPercentage = (float) (sumArityCombinations[j][k]) / sumNodes;
  //
  // if (arityCombinationsPercentage != 0 && arityCombinationsPercentage >= threshhold) {
  // bldr.append(String.format("%d/%d: %s, ", j, k,
  // new DecimalFormat("0.00%").format(arityCombinationsPercentage)));
  // }
  // }
  // }
  // final String detailPercentages = bldr.toString();
  //
  // // overview
  // System.out.println(String.format("%2d: %s\t[cumsum = %s]\t%s", i,
  // new DecimalFormat("0.00%").format(arityPercentage),
  // new DecimalFormat("0.00%").format(cumsumArityPercentage), detailPercentages));
  // }
  // }
  // }

  static abstract class EitherSingletonOrCollection<T> {
    public enum Type {
      SINGLETON, COLLECTION
    }

    public static final <T> EitherSingletonOrCollection<T> of(T value) {
      return new SomeSingleton<>(value);
    }

    public static final <T> EitherSingletonOrCollection of(ImmutableSet<T> value) {
      return new SomeCollection<>(value);
    }

    abstract boolean isType(Type type);

    abstract T getSingleton();

    abstract ImmutableSet<T> getCollection();
  }

  static final class SomeSingleton<T> extends EitherSingletonOrCollection<T> {
    private final T value;

    private SomeSingleton(T value) {
      this.value = value;
    }

    @Override
    boolean isType(Type type) {
      return type == Type.SINGLETON;
    }

    @Override
    T getSingleton() {
      return value;
    }

    @Override
    ImmutableSet<T> getCollection() {
      throw new UnsupportedOperationException(String
          .format("Requested type %s but actually found %s.", Type.COLLECTION, Type.SINGLETON));
    }
  }

  static final class SomeCollection<T> extends EitherSingletonOrCollection<T> {
    private final ImmutableSet<T> value;

    private SomeCollection(ImmutableSet<T> value) {
      this.value = value;
    }

    @Override
    boolean isType(Type type) {
      return type == Type.COLLECTION;
    }

    @Override
    T getSingleton() {
      throw new UnsupportedOperationException(String
          .format("Requested type %s but actually found %s.", Type.SINGLETON, Type.COLLECTION));
    }

    @Override
    ImmutableSet<T> getCollection() {
      return value;
    }
  }

  static final class SetMultimapResult<K, V> {
    private V replacedValue;
    private ImmutableSet<V> replacedValueCollection;
    private EitherSingletonOrCollection.Type replacedType;

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
      this.replacedType = SINGLETON;
    }

    public void updated(ImmutableSet<V> replacedValueCollection) {
      this.replacedValueCollection = replacedValueCollection;
      this.isModified = true;
      this.isReplaced = true;
      this.replacedType = COLLECTION;
    }

    // update: neither element, nor element count changed
    public static <K, V> SetMultimapResult<K, V> unchanged() {
      return new SetMultimapResult<>();
    }

    private SetMultimapResult() {}

    public boolean isModified() {
      return isModified;
    }

    public EitherSingletonOrCollection.Type getType() {
      return replacedType;
    }

    public boolean hasReplacedValue() {
      return isReplaced;
    }

    public V getReplacedValue() {
      assert getType() == SINGLETON;
      return replacedValue;
    }

    public ImmutableSet<V> getReplacedCollection() {
      assert getType() == COLLECTION;
      return replacedValueCollection;
    }
  }

  protected static interface INode<K, V> {
  }

  protected static abstract class AbstractSetMultimapNode<K, V> implements INode<K, V> {

    static final int TUPLE_LENGTH = 2;

    abstract boolean containsKey(final K key, final int keyHash, final int shift);

    abstract boolean containsTuple(final K key, final V val, final int keyHash, final int shift);    
    
    abstract Optional<ImmutableSet<V>> findByKey(final K key, final int keyHash, final int shift);

    abstract CompactSetMultimapNode<K, V> inserted(final AtomicReference<Thread> mutator,
        final K key, final V val, final int keyHash, final int shift,
        final SetMultimapResult<K, V> details);

    abstract CompactSetMultimapNode<K, V> updated(final AtomicReference<Thread> mutator,
        final K key, final V val, final int keyHash, final int shift,
        final SetMultimapResult<K, V> details);
    
    abstract CompactSetMultimapNode<K, V> updated(final AtomicReference<Thread> mutator,
        final K key, final ImmutableSet<V> val, final int keyHash, final int shift,
        final SetMultimapResult<K, V> details);

    abstract CompactSetMultimapNode<K, V> removed(final AtomicReference<Thread> mutator,
        final K key, final V val, final int keyHash, final int shift,
        final SetMultimapResult<K, V> details);

    abstract CompactSetMultimapNode<K, V> removedAll(final AtomicReference<Thread> mutator,
        final K key, final int keyHash, final int shift, final SetMultimapResult<K, V> details);

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

    abstract int emptyArity();
    
    // @Deprecated // split data / coll arity
    // abstract boolean hasPayload();
    //
    // @Deprecated // split data / coll arity
    // abstract int payloadArity();

    abstract boolean hasPayload(EitherSingletonOrCollection.Type type);

    // abstract int payloadArity();
    
    abstract int payloadArity(EitherSingletonOrCollection.Type type);

    abstract K getSingletonKey(final int index);

    abstract V getSingletonValue(final int index);

    abstract K getCollectionKey(final int index);

    abstract ImmutableSet<V> getCollectionValue(final int index);

    abstract boolean hasSlots();

    abstract int slotArity();

    abstract Object getSlot(final int index);

    /**
     * The arity of this trie node (i.e. number of values and nodes stored on this level).
     * 
     * @return sum of nodes and values stored within
     */
    abstract int arity();

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
      return 1 << mask;
    }

    static final int doubledMask(int keyHash, int shift) {
      final int mask = mask(keyHash, shift);
      return mask << 1;
    }

    static final long doubledBitpos(final int doubledMask) {
      return 1L << doubledMask;
    }

    static final int pattern(long bitmap, int doubledMask) {
      return (int) ((bitmap >>> doubledMask) & 0b11);
    }

    // @Deprecated
    // abstract int dataMap();
    //
    // @Deprecated
    // abstract int collMap();
    //
    // @Deprecated
    // abstract int nodeMap();

    abstract long bitmap();

    @Deprecated
    @Override
    int arity() {
      // TODO: replace with 32 - arity(emptyMap)
      // return arity(bitmap(), PATTERN_DATA_SINGLETON) + arity(bitmap(), PATTERN_DATA_COLLECTION) +
      // arity(bitmap(), PATTERN_NODE);

      int[] arities = arities(bitmap());
      return Arrays.stream(arities).skip(1).sum();
    }

    static final int arity(long bitmap, int pattern) {
      if (bitmap == 0) {
        if (pattern == PATTERN_EMPTY) {
          return 32;
        } else {
          return 0;
        }
      } else {
        return Long.bitCount(filter(bitmap, pattern));
      }
    }

    // TODO: Implement arity histogram over bitmap (with single for loop) that calculates offsets
    static final int[] arities(final long bitmap) {
      int[] arities = new int[4];

      long shiftedBitmap = bitmap;
      for (int i = 0; i < 32; i++) {
        arities[(int) shiftedBitmap & 0b11]++;
        shiftedBitmap = shiftedBitmap >>> 2;
      }

      return arities;
    }

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
      return true;
      // boolean inv1 = (size() - payloadArity() >= 2 * (arity() - payloadArity()));
      // boolean inv2 = (this.arity() == 0) ? sizePredicate() == SIZE_EMPTY : true;
      // boolean inv3 =
      // (this.arity() == 1 && payloadArity() == 1) ? sizePredicate() == SIZE_ONE : true;
      // boolean inv4 = (this.arity() >= 2) ? sizePredicate() == SIZE_MORE_THAN_ONE : true;
      //
      // boolean inv5 = (this.nodeArity() >= 0) && (this.payloadArity() >= 0)
      // && ((this.payloadArity() + this.nodeArity()) == this.arity());
      //
      // return inv1 && inv2 && inv3 && inv4 && inv5;
    }

    abstract CompactSetMultimapNode<K, V> copyAndUpdateBitmaps(AtomicReference<Thread> mutator,
        final long bitmap);

    abstract CompactSetMultimapNode<K, V> copyAndSetSingletonValue(
        final AtomicReference<Thread> mutator, final long doubledBitpos, final V val);

    abstract CompactSetMultimapNode<K, V> copyAndSetCollectionValue(
        final AtomicReference<Thread> mutator, final long doubledBitpos,
        final ImmutableSet<V> valColl);

    abstract CompactSetMultimapNode<K, V> copyAndSetNode(final AtomicReference<Thread> mutator,
        final long doubledBitpos, final CompactSetMultimapNode<K, V> node);

    abstract CompactSetMultimapNode<K, V> copyAndInsertSingleton(
        final AtomicReference<Thread> mutator, final long doubledBitpos, final K key, final V val);
    
    abstract CompactSetMultimapNode<K, V> copyAndInsertCollection(
        final AtomicReference<Thread> mutator, final long doubledBitpos, final K key,
        final ImmutableSet<V> valColl);

    abstract CompactSetMultimapNode<K, V> copyAndMigrateFromSingletonToCollection(
        final AtomicReference<Thread> mutator, final long doubledBitpos, final K key,
        final ImmutableSet<V> valColl);

    abstract CompactSetMultimapNode<K, V> copyAndRemoveSingleton(
        final AtomicReference<Thread> mutator, final long doubledBitpos);
    
    abstract CompactSetMultimapNode<K, V> copyAndRemoveSingleton(
        final AtomicReference<Thread> mutator, final long doubledBitpos, long updatedBitmap);

    /*
     * Batch updated, necessary for removedAll.
     */
    abstract CompactSetMultimapNode<K, V> copyAndRemoveCollection(
        final AtomicReference<Thread> mutator, final long doubledBitpos);

    abstract CompactSetMultimapNode<K, V> copyAndMigrateFromSingletonToNode(
        final AtomicReference<Thread> mutator, final long doubledBitpos,
        final CompactSetMultimapNode<K, V> node);

    abstract CompactSetMultimapNode<K, V> copyAndMigrateFromNodeToSingleton(
        final AtomicReference<Thread> mutator, final long doubledBitpos,
        final CompactSetMultimapNode<K, V> node); // node get's unwrapped inside method

    abstract CompactSetMultimapNode<K, V> copyAndMigrateFromCollectionToNode(
        final AtomicReference<Thread> mutator, final long doubledBitpos,
        final CompactSetMultimapNode<K, V> node);

    abstract CompactSetMultimapNode<K, V> copyAndMigrateFromNodeToCollection(
        final AtomicReference<Thread> mutator, final long doubledBitpos,
        final CompactSetMultimapNode<K, V> node); // node get's unwrapped inside method

    abstract CompactSetMultimapNode<K, V> copyAndMigrateFromCollectionToSingleton(
        final AtomicReference<Thread> mutator, final long doubledBitpos, final K key, final V val);

    // TODO: fix hash collision support
    static final <K, V> CompactSetMultimapNode<K, V> mergeTwoSingletonPairs(final K key0,
        final V val0, final int keyHash0, final K key1, final V val1, final int keyHash1,
        final int shift) {
      assert !(key0.equals(key1));

      if (shift >= HASH_CODE_LENGTH) {
        throw new IllegalStateException("Hash collision not yet fixed.");
        // return new HashCollisionSetMultimapNode_BleedingEdge<>(keyHash0,
        // (K[]) new Object[] {key0, key1}, (ImmutableSet<V>[]) new ImmutableSet[] {val0, val1});
      }

      final int mask0 = doubledMask(keyHash0, shift);
      final int mask1 = doubledMask(keyHash1, shift);

      if (mask0 != mask1) {
        // both nodes fit on same level
        long bitmap = 0L;
        bitmap = setBitPattern(bitmap, doubledBitpos(mask0), PATTERN_DATA_SINGLETON);
        bitmap = setBitPattern(bitmap, doubledBitpos(mask1), PATTERN_DATA_SINGLETON);

        if (mask0 < mask1) {
          return nodeOf(null, bitmap, new Object[] {key0, val0, key1, val1});
        } else {
          return nodeOf(null, bitmap, new Object[] {key1, val1, key0, val0});
        }
      } else {
        final CompactSetMultimapNode<K, V> node = mergeTwoSingletonPairs(key0, val0, keyHash0, key1,
            val1, keyHash1, shift + BIT_PARTITION_SIZE);
        // values fit on next level
        final long bitmap = setBitPattern(0L, doubledBitpos(mask0), PATTERN_NODE);

        return nodeOf(null, bitmap, new Object[] {node});
      }
    }

    // TODO: fix hash collision support
    static final <K, V> CompactSetMultimapNode<K, V> mergeTwoCollectionPairs(final K key0,
        final ImmutableSet<V> valColl0, final int keyHash0, final K key1,
        final ImmutableSet<V> valColl1, final int keyHash1,
        final int shift) {
      assert !(key0.equals(key1));

      if (shift >= HASH_CODE_LENGTH) {
        throw new IllegalStateException("Hash collision not yet fixed.");
        // return new HashCollisionSetMultimapNode_BleedingEdge<>(keyHash0,
        // (K[]) new Object[] {key0, key1}, (ImmutableSet<V>[]) new ImmutableSet[] {val0, val1});
      }

      final int mask0 = doubledMask(keyHash0, shift);
      final int mask1 = doubledMask(keyHash1, shift);

      if (mask0 != mask1) {
        // both nodes fit on same level
        long bitmap = 0L;
        bitmap = setBitPattern(bitmap, doubledBitpos(mask0), PATTERN_DATA_COLLECTION);
        bitmap = setBitPattern(bitmap, doubledBitpos(mask1), PATTERN_DATA_COLLECTION);

        if (mask0 < mask1) {
          return nodeOf(null, bitmap, new Object[] {key0, valColl0, key1, valColl1});
        } else {
          return nodeOf(null, bitmap, new Object[] {key1, valColl1, key0, valColl0});
        }
      } else {
        final CompactSetMultimapNode<K, V> node = mergeTwoCollectionPairs(key0, valColl0, keyHash0,
            key1, valColl1, keyHash1, shift + BIT_PARTITION_SIZE);
        // values fit on next level
        final long bitmap = setBitPattern(0L, doubledBitpos(mask0), PATTERN_NODE);

        return nodeOf(null, bitmap, new Object[] {node});
      }
    }    
    
    // TODO: fix hash collision support
    static final <K, V> CompactSetMultimapNode<K, V> mergeCollectionAndSingletonPairs(final K key0,
        final ImmutableSet<V> valColl0, final int keyHash0, final K key1, final V val1,
        final int keyHash1, final int shift) {
      assert !(key0.equals(key1));

      if (shift >= HASH_CODE_LENGTH) {
        throw new IllegalStateException("Hash collision not yet fixed.");
        // return new HashCollisionSetMultimapNode_BleedingEdge<>(keyHash0,
        // (K[]) new Object[] {key0, key1},
        // (ImmutableSet<V>[]) new ImmutableSet[] {valColl0, val1});
      }

      final int mask0 = doubledMask(keyHash0, shift);
      final int mask1 = doubledMask(keyHash1, shift);

      if (mask0 != mask1) {
        // both nodes fit on same level
        long bitmap = 0L;
        bitmap = setBitPattern(bitmap, doubledBitpos(mask0), PATTERN_DATA_COLLECTION);
        bitmap = setBitPattern(bitmap, doubledBitpos(mask1), PATTERN_DATA_SINGLETON);

        // singleton before collection
        return nodeOf(null, bitmap, new Object[] {key1, val1, key0, valColl0});
      } else {
        final CompactSetMultimapNode<K, V> node = mergeCollectionAndSingletonPairs(key0, valColl0,
            keyHash0, key1, val1, keyHash1, shift + BIT_PARTITION_SIZE);
        // values fit on next level
        final long bitmap = setBitPattern(0L, doubledBitpos(mask0), PATTERN_NODE);

        return nodeOf(null, bitmap, new Object[] {node});
      }
    }

    static final CompactSetMultimapNode EMPTY_NODE;

    static {
      EMPTY_NODE = new BitmapIndexedSetMultimapNode<>(null, 0L, new Object[] {});
    };

    static final <K, V> CompactSetMultimapNode<K, V> nodeOf(final AtomicReference<Thread> mutator,
        final long bitmap, final Object[] nodes) {
      return new BitmapIndexedSetMultimapNode<>(mutator, bitmap, nodes);
    }

    @SuppressWarnings("unchecked")
    static final <K, V> CompactSetMultimapNode<K, V> nodeOf(AtomicReference<Thread> mutator) {
      return EMPTY_NODE;
    }

    static final <K, V> CompactSetMultimapNode<K, V> nodeOf(AtomicReference<Thread> mutator,
        final long bitmap, final K key, final ImmutableSet<V> valColl) {
      return nodeOf(mutator, bitmap, new Object[] {key, valColl});
    }

    // static final int index(final int bitmap, final int bitpos) {
    // return java.lang.Integer.bitCount(bitmap & (bitpos - 1));
    // }
    //
    // static final int index(final int bitmap, final int mask, final int bitpos) {
    // return (bitmap == -1) ? mask : index(bitmap, bitpos);
    // }

    @Deprecated
    int dataIndex(final long doubledBitpos) {
      return index10(bitmap(), doubledBitpos);
    }

    @Deprecated
    int collIndex(final long doubledBitpos) {
      return index11(bitmap(), doubledBitpos);
    }

    @Deprecated
    int nodeIndex(final long doubledBitpos) {
      return index01(bitmap(), doubledBitpos);
    }    

    @Override
    boolean containsKey(final K key, final int keyHash, final int shift) {
      long bitmap = this.bitmap();

      final int doubledMask = doubledMask(keyHash, shift);
      final int pattern = pattern(bitmap, doubledMask);

      final long doubledBitpos = doubledBitpos(doubledMask);
      
      switch (pattern) {
        case PATTERN_NODE: {
          int index = index01(bitmap, doubledBitpos);
          return getNode(index).containsKey(key, keyHash, shift + BIT_PARTITION_SIZE);
        }
        case PATTERN_DATA_SINGLETON: {
          int index = index10(bitmap, doubledBitpos);
          return getSingletonKey(index).equals(key);
        }
        case PATTERN_DATA_COLLECTION: {
          int index = index11(bitmap, doubledBitpos);
          return getCollectionKey(index).equals(key);
        }
        default:
          return false;
      }
    }

    boolean containsKey(final K key, final int keyHash, final int shift,
        final Comparator<Object> cmp) {
      throw new UnsupportedOperationException("Not yet implemented.");
    }

    @Override
    boolean containsTuple(final K key, final V val, final int keyHash, final int shift) {
      long bitmap = this.bitmap();

      final int doubledMask = doubledMask(keyHash, shift);
      final int pattern = pattern(bitmap, doubledMask);

      final long doubledBitpos = doubledBitpos(doubledMask);

      switch (pattern) {
        case PATTERN_NODE: {
          int index = index01(bitmap, doubledBitpos);

          final AbstractSetMultimapNode<K, V> subNode = getNode(index);
          return subNode.containsTuple(key, val, keyHash, shift + BIT_PARTITION_SIZE);
        }
        case PATTERN_DATA_SINGLETON: {
          int index = index10(bitmap, doubledBitpos);

          final K currentKey = getSingletonKey(index);
          if (currentKey.equals(key)) {

            final V currentVal = getSingletonValue(index);
            return currentVal.equals(val);
          }

          return false;
        }
        case PATTERN_DATA_COLLECTION: {
          int index = index11(bitmap, doubledBitpos);

          final K currentKey = getCollectionKey(index);
          if (currentKey.equals(key)) {

            final ImmutableSet<V> currentValColl = getCollectionValue(index);
            return currentValColl.contains(val);
          }

          return false;
        }
        default:
          return false;
      }
    }    
        
    @Override
    Optional<ImmutableSet<V>> findByKey(final K key, final int keyHash, final int shift) {
      long bitmap = this.bitmap();

      final int doubledMask = doubledMask(keyHash, shift);
      final int pattern = pattern(bitmap, doubledMask);

      final long doubledBitpos = doubledBitpos(doubledMask);

      switch (pattern) {
        case PATTERN_NODE: {
          int index = index01(bitmap, doubledBitpos);

          final AbstractSetMultimapNode<K, V> subNode = getNode(index);
          return subNode.findByKey(key, keyHash, shift + BIT_PARTITION_SIZE);
        }
        case PATTERN_DATA_SINGLETON: {
          int index = index10(bitmap, doubledBitpos);

          final K currentKey = getSingletonKey(index);
          if (currentKey.equals(key)) {

            final V currentVal = getSingletonValue(index);
            return Optional.of(setOf(currentVal));
          }

          return Optional.empty();
        }
        case PATTERN_DATA_COLLECTION: {
          int index = index11(bitmap, doubledBitpos);

          final K currentKey = getCollectionKey(index);
          if (currentKey.equals(key)) {

            final ImmutableSet<V> currentValColl = getCollectionValue(index);
            return Optional.of(currentValColl);
          }

          return Optional.empty();
        }
        default:
          return Optional.empty();
      }
    }

    Optional<ImmutableSet<V>> findByKey(final K key, final int keyHash, final int shift,
        final Comparator<Object> cmp) {
      throw new UnsupportedOperationException("Not yet implemented.");
    }

    @Override
    CompactSetMultimapNode<K, V> inserted(final AtomicReference<Thread> mutator, final K key,
        final V val, final int keyHash, final int shift, final SetMultimapResult<K, V> details) {
      long bitmap = this.bitmap();

      final int doubledMask = doubledMask(keyHash, shift);
      final int pattern = pattern(bitmap, doubledMask);

      final long doubledBitpos = doubledBitpos(doubledMask);

      switch (pattern) {
        case PATTERN_NODE: {
          int nodeIndex = index01(bitmap, doubledBitpos);
          final CompactSetMultimapNode<K, V> subNode = getNode(nodeIndex);
          final CompactSetMultimapNode<K, V> subNodeNew =
              subNode.inserted(mutator, key, val, keyHash, shift + BIT_PARTITION_SIZE, details);

          if (details.isModified()) {
            return copyAndSetNode(mutator, doubledBitpos, subNodeNew);
          } else {
            return this;
          }
        }
        case PATTERN_DATA_SINGLETON: {
          int dataIndex = index10(bitmap, doubledBitpos);
          final K currentKey = getSingletonKey(dataIndex);

          if (currentKey.equals(key)) {
            final V currentVal = getSingletonValue(dataIndex);

            if (currentVal.equals(val)) {
              return this;
            } else {
              // migrate from singleton to collection
              final ImmutableSet<V> valColl = setOf(currentVal, val);

              details.modified();
              return copyAndMigrateFromSingletonToCollection(mutator, doubledBitpos, currentKey,
                  valColl);
            }
          } else {
            // prefix-collision (case: singleton x singleton)
            final V currentVal = getSingletonValue(dataIndex);

            final CompactSetMultimapNode<K, V> subNodeNew = mergeTwoSingletonPairs(currentKey,
                currentVal, transformHashCode(currentKey.hashCode()), key, val, keyHash,
                shift + BIT_PARTITION_SIZE);

            details.modified();
            return copyAndMigrateFromSingletonToNode(mutator, doubledBitpos, subNodeNew);
          }
        }
        case PATTERN_DATA_COLLECTION: {
          int collIndex = index11(bitmap, doubledBitpos);
          final K currentCollKey = getCollectionKey(collIndex);

          if (currentCollKey.equals(key)) {
            final ImmutableSet<V> currentCollVal = getCollectionValue(collIndex);

            if (currentCollVal.contains(val)) {
              return this;
            } else {
              // add new mapping
              final ImmutableSet<V> newCollVal = currentCollVal.__insert(val);

              details.modified();
              return copyAndSetCollectionValue(mutator, doubledBitpos, newCollVal);
            }
          } else {
            // prefix-collision (case: collection x singleton)
            final ImmutableSet<V> currentValNode = getCollectionValue(collIndex);
            final CompactSetMultimapNode<K, V> subNodeNew = mergeCollectionAndSingletonPairs(
                currentCollKey, currentValNode, transformHashCode(currentCollKey.hashCode()), key,
                val, keyHash, shift + BIT_PARTITION_SIZE);

            details.modified();
            return copyAndMigrateFromCollectionToNode(mutator, doubledBitpos, subNodeNew);
          }
        }
        default: {
          details.modified();
          return copyAndInsertSingleton(mutator, doubledBitpos, key, val);
        }
      }
    }

    @Override
    CompactSetMultimapNode<K, V> updated(final AtomicReference<Thread> mutator, final K key,
        final V val, final int keyHash, final int shift, final SetMultimapResult<K, V> details) {
      long bitmap = this.bitmap();

      final int doubledMask = doubledMask(keyHash, shift);
      final int pattern = pattern(bitmap, doubledMask);

      final long doubledBitpos = doubledBitpos(doubledMask);

      switch (pattern) {
        case PATTERN_NODE: {
          int nodeIndex = index01(bitmap, doubledBitpos);
          final CompactSetMultimapNode<K, V> subNode = getNode(nodeIndex);
          final CompactSetMultimapNode<K, V> subNodeNew =
              subNode.updated(mutator, key, val, keyHash, shift + BIT_PARTITION_SIZE, details);

          if (details.isModified()) {
            return copyAndSetNode(mutator, doubledBitpos, subNodeNew);
          } else {
            return this;
          }
        }
        case PATTERN_DATA_SINGLETON: {
          int dataIndex = index10(bitmap, doubledBitpos);
          final K currentKey = getSingletonKey(dataIndex);

          if (currentKey.equals(key)) {
            final V currentVal = getSingletonValue(dataIndex);

            // update singleton value
            details.updated(currentVal);
            return copyAndSetSingletonValue(mutator, doubledBitpos, val);
          } else {
            // prefix-collision (case: singleton x singleton)
            final V currentVal = getSingletonValue(dataIndex);

            final CompactSetMultimapNode<K, V> subNodeNew = mergeTwoSingletonPairs(currentKey,
                currentVal, transformHashCode(currentKey.hashCode()), key, val, keyHash,
                shift + BIT_PARTITION_SIZE);

            details.modified();
            return copyAndMigrateFromSingletonToNode(mutator, doubledBitpos, subNodeNew);
          }
        }
        case PATTERN_DATA_COLLECTION: {
          int collIndex = index11(bitmap, doubledBitpos);
          final K currentCollKey = getCollectionKey(collIndex);

          if (currentCollKey.equals(key)) {
            final ImmutableSet<V> currentCollVal = getCollectionValue(collIndex);

            // migrate from collection to singleton
            details.updated(currentCollVal);
            return copyAndMigrateFromCollectionToSingleton(mutator, doubledBitpos, currentCollKey,
                val);
          } else {
            // prefix-collision (case: collection x singleton)
            final ImmutableSet<V> currentValNode = getCollectionValue(collIndex);
            final CompactSetMultimapNode<K, V> subNodeNew = mergeCollectionAndSingletonPairs(
                currentCollKey, currentValNode, transformHashCode(currentCollKey.hashCode()), key,
                val, keyHash, shift + BIT_PARTITION_SIZE);

            details.modified();
            return copyAndMigrateFromCollectionToNode(mutator, doubledBitpos, subNodeNew);
          }
        }
        default: {
          details.modified();
          return copyAndInsertSingleton(mutator, doubledBitpos, key, val);
        }
      }
    }
    
    @Override
    CompactSetMultimapNode<K, V> updated(final AtomicReference<Thread> mutator, final K key,
        final ImmutableSet<V> valColl, final int keyHash, final int shift, final SetMultimapResult<K, V> details) {
      long bitmap = this.bitmap();

      final int doubledMask = doubledMask(keyHash, shift);
      final int pattern = pattern(bitmap, doubledMask);

      final long doubledBitpos = doubledBitpos(doubledMask);

      switch (pattern) {
        case PATTERN_NODE: {
          int nodeIndex = index01(bitmap, doubledBitpos);
          final CompactSetMultimapNode<K, V> subNode = getNode(nodeIndex);
          final CompactSetMultimapNode<K, V> subNodeNew =
              subNode.updated(mutator, key, valColl, keyHash, shift + BIT_PARTITION_SIZE, details);

          if (details.isModified()) {
            return copyAndSetNode(mutator, doubledBitpos, subNodeNew);
          } else {
            return this;
          }
        }
        case PATTERN_DATA_SINGLETON: {
          int dataIndex = index10(bitmap, doubledBitpos);
          final K currentKey = getSingletonKey(dataIndex);

          if (currentKey.equals(key)) {
            final V currentVal = getSingletonValue(dataIndex);

            // migrate from singleton to collection
            details.updated(currentVal);
            return copyAndMigrateFromSingletonToCollection(mutator, doubledBitpos, currentKey,
                valColl);
//            
//            
//            // update singleton value
//            details.updated(currentVal);
//            return copyAndSetSingletonValue(mutator, doubledBitpos, valColl);
          } else {
            // prefix-collision (case: collection x singleton)
            final V currentVal = getSingletonValue(dataIndex);

            final CompactSetMultimapNode<K, V> subNodeNew = mergeCollectionAndSingletonPairs(
                key, valColl, keyHash, currentKey,
                currentVal, transformHashCode(currentKey.hashCode()), shift + BIT_PARTITION_SIZE);

            details.modified();
            return copyAndMigrateFromSingletonToNode(mutator, doubledBitpos, subNodeNew);
            
//            // prefix-collision (case: singleton x singleton)
//            final V currentVal = getSingletonValue(dataIndex);
//
//            final CompactSetMultimapNode<K, V> subNodeNew = mergeTwoSingletonPairs(currentKey,
//                currentVal, transformHashCode(currentKey.hashCode()), key, valColl, keyHash,
//                shift + BIT_PARTITION_SIZE);
//
//            details.modified();
//            return copyAndMigrateFromSingletonToNode(mutator, doubledBitpos, subNodeNew);
          }
        }
        case PATTERN_DATA_COLLECTION: {
          int collIndex = index11(bitmap, doubledBitpos);
          final K currentCollKey = getCollectionKey(collIndex);

          if (currentCollKey.equals(key)) {
            final ImmutableSet<V> currentCollVal = getCollectionValue(collIndex);

            // update collection value
            details.updated(currentCollVal);
            return copyAndSetCollectionValue(mutator, doubledBitpos, valColl);            
            
//            // migrate from collection to singleton
//            details.updated(currentCollVal);
//            return copyAndMigrateFromCollectionToSingleton(mutator, doubledBitpos, currentCollKey,
//                valColl);
          } else {
            // prefix-collision (case: collection x collection)
            final ImmutableSet<V> currentValNode = getCollectionValue(collIndex);
            final CompactSetMultimapNode<K, V> subNodeNew = mergeTwoCollectionPairs(currentCollKey,
                currentValNode, transformHashCode(currentCollKey.hashCode()), key, valColl, keyHash,
                shift + BIT_PARTITION_SIZE);
            
            details.modified();
            return copyAndMigrateFromCollectionToNode(mutator, doubledBitpos, subNodeNew);
                        
//            // prefix-collision (case: collection x singleton)
//            final ImmutableSet<V> currentValNode = getCollectionValue(collIndex);
//            final CompactSetMultimapNode<K, V> subNodeNew = mergeCollectionAndSingletonPairs(
//                currentCollKey, currentValNode, transformHashCode(currentCollKey.hashCode()), key,
//                valColl, keyHash, shift + BIT_PARTITION_SIZE);
//
//            details.modified();
//            return copyAndMigrateFromCollectionToNode(mutator, doubledBitpos, subNodeNew);
          }
        }
        default: {
          details.modified();
          return copyAndInsertCollection(mutator, doubledBitpos, key, valColl);
        }
      }
    }
    
    @Override
    CompactSetMultimapNode<K, V> removed(final AtomicReference<Thread> mutator, final K key,
        final V val, final int keyHash, final int shift, final SetMultimapResult<K, V> details) {
      long bitmap = this.bitmap();

      final int doubledMask = doubledMask(keyHash, shift);
      final int pattern = pattern(bitmap, doubledMask);

      final long doubledBitpos = doubledBitpos(doubledMask);

      switch (pattern) {
        case PATTERN_NODE: {
          int nodeIndex = index01(bitmap, doubledBitpos);

          final CompactSetMultimapNode<K, V> subNode = getNode(nodeIndex);
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
              if (slotArity() == 0) {
                if (shift == 0) {
                  // singleton remaining                 
                  final long doubledBitposAtShift0 = doubledBitpos(bitpos(doubledMask(keyHash, 0)));

                  final long updatedBitmapAtShift0 =
                      setBitPattern(doubledBitposAtShift0, subNodeNew.patternOfSingleton());

                  return subNodeNew.copyAndUpdateBitmaps(mutator, updatedBitmapAtShift0);
                } else {
                  // escalate (singleton or empty) result
                  return subNodeNew;
                }
              } else {
                // inline value (move to front)
                EitherSingletonOrCollection.Type type = subNodeNew.typeOfSingleton();

                if (type == EitherSingletonOrCollection.Type.SINGLETON) {
                  return copyAndMigrateFromNodeToSingleton(mutator, doubledBitpos, subNodeNew);
                } else {
                  return copyAndMigrateFromNodeToCollection(mutator, doubledBitpos, subNodeNew);
                }

              }
            }
            default: {
              // modify current node (set replacement node)
              return copyAndSetNode(mutator, doubledBitpos, subNodeNew);
            }
          }
        }
        case PATTERN_DATA_SINGLETON: {
          int dataIndex = index10(bitmap, doubledBitpos);

          final K currentKey = getSingletonKey(dataIndex);
          if (currentKey.equals(key)) {

            final V currentVal = getSingletonValue(dataIndex);
            if (currentVal.equals(val)) {

              // remove mapping
              details.updated(val);
              return copyAndRemoveSingleton(mutator, doubledBitpos);
            } else {
              return this;
            }
          } else {
            return this;
          }
        }
        case PATTERN_DATA_COLLECTION: {
          int collIndex = index11(bitmap, doubledBitpos);

          final K currentKey = getCollectionKey(collIndex);
          if (currentKey.equals(key)) {

            final ImmutableSet<V> currentValColl = getCollectionValue(collIndex);
            if (currentValColl.contains(val)) {

              // remove mapping
              details.updated(val);

              final ImmutableSet<V> newValColl = currentValColl.__remove(val);

              if (newValColl.size() == 1) {
                // TODO: investigate options for unboxing singleton collections
                V remainingVal = newValColl.iterator().next();
                return copyAndMigrateFromCollectionToSingleton(mutator, doubledBitpos, key,
                    remainingVal);
              } else {
                return copyAndSetCollectionValue(mutator, doubledBitpos, newValColl);
              }
            } else {
              return this;
            }
          } else {
            return this;
          }
        }
        default:
          return this;
      }
    }

    final static boolean hasSingleNode(int[] arities) {
      return arities[PATTERN_EMPTY] == 31 && arities[PATTERN_NODE] == 1;
    }

    final static boolean hasTwoPayloads(int[] arities) {
      return arities[PATTERN_EMPTY] == 30 && arities[PATTERN_NODE] == 0;
    }    
    
    enum State {
      EMPTY, NODE, PAYLOAD, PAYLOAD_RARE
    }    
    
    static final State toState(final int pattern) {
//      final State[] states = {State.EMPTY, State.NODE, State.PAYLOAD, State.PAYLOAD_RARE};
//      return states[pattern];
      
      switch (pattern) {
        case PATTERN_EMPTY:
          return State.EMPTY;
        case PATTERN_NODE:
          return State.NODE;
        case PATTERN_DATA_SINGLETON:
          return State.PAYLOAD;
        default:
          return State.PAYLOAD_RARE;
      }
    }
    
    @Override
    CompactSetMultimapNode<K, V> removedAll(final AtomicReference<Thread> mutator, final K key,
        final int keyHash, final int shift, final SetMultimapResult<K, V> details) {
      long bitmap = this.bitmap();

      final int doubledMask = doubledMask(keyHash, shift);
      final int pattern = pattern(bitmap, doubledMask);

      final long doubledBitpos = doubledBitpos(doubledMask);

      switch (pattern) {
        case PATTERN_NODE: {
          int nodeIndex = index01(bitmap, doubledBitpos);

          final CompactSetMultimapNode<K, V> subNode = getNode(nodeIndex);
          final CompactSetMultimapNode<K, V> subNodeNew =
              subNode.removedAll(mutator, key, keyHash, shift + BIT_PARTITION_SIZE, details);

          if (!details.isModified()) {
            return this;
          }

          switch (subNodeNew.sizePredicate()) {
            case 0: {
              throw new IllegalStateException("Sub-node must have at least one element.");
            }
            case 1: {
              if (slotArity() == 1) {
                if (shift == 0) {
                  // singleton remaining                 
                  final long doubledBitposAtShift0 = doubledBitpos(bitpos(doubledMask(keyHash, 0)));

                  final long updatedBitmapAtShift0 =
                      setBitPattern(doubledBitposAtShift0, subNodeNew.patternOfSingleton());

                  return subNodeNew.copyAndUpdateBitmaps(mutator, updatedBitmapAtShift0);
                } else {
                  // escalate (singleton or empty) result
                  return subNodeNew;
                }
              } else {
//                // inline value (move to front)
//                final State subNodeState = subNodeNew.stateOfSingleton();
//
////                switch (subNodeState) {
////                  case EMPTY:
////                  case NODE:
////                  case PAYLOAD:
////                    return copyAndMigrateFromNodeToSingleton(mutator, doubledBitpos, subNodeNew);
////                  case PAYLOAD_RARE:
////                    return copyAndMigrateFromNodeToCollection(mutator, doubledBitpos, subNodeNew);
////                }
//                
//                if (subNodeState == State.PAYLOAD) {
//                  return copyAndMigrateFromNodeToSingleton(mutator, doubledBitpos, subNodeNew);
//                } else {
//                  return copyAndMigrateFromNodeToCollection(mutator, doubledBitpos, subNodeNew);
//                }         
                
                // inline value (move to front)
                EitherSingletonOrCollection.Type type = subNodeNew.typeOfSingleton();

                if (type == EitherSingletonOrCollection.Type.SINGLETON) {
                  return copyAndMigrateFromNodeToSingleton(mutator, doubledBitpos, subNodeNew);
                } else {
                  return copyAndMigrateFromNodeToCollection(mutator, doubledBitpos, subNodeNew);
                }

//                // inline value (move to front)
//                final int subNodePattern = subNodeNew.patternOfSingleton();
//
//                if (subNodePattern == PATTERN_DATA_SINGLETON) {
//                  return copyAndMigrateFromNodeToSingleton(mutator, doubledBitpos, subNodeNew);
//                } else {
//                  return copyAndMigrateFromNodeToCollection(mutator, doubledBitpos, subNodeNew);
//                }
                
//                switch (subNodePattern) {
//                  case PATTERN_DATA_SINGLETON:
//                    return copyAndMigrateFromNodeToSingleton(mutator, doubledBitpos, subNodeNew);
//                  case PATTERN_DATA_COLLECTION:
//                    return copyAndMigrateFromNodeToCollection(mutator, doubledBitpos, subNodeNew);
//                  default:
//                    return null;
//                }
              }
            }
            default: {
              // modify current node (set replacement node)
              return copyAndSetNode(mutator, doubledBitpos, subNodeNew);
            }
          }
        }
        case PATTERN_DATA_SINGLETON: {
          int dataIndex = index10(bitmap, doubledBitpos);

          final K currentKey = getSingletonKey(dataIndex);
          if (currentKey.equals(key)) {

            final V currentVal = getSingletonValue(dataIndex);

            details.updated(currentVal);
            return copyAndRemoveSingleton(mutator, doubledBitpos);
          } else {
            return this;
          }
        }
        case PATTERN_DATA_COLLECTION: {
          int collIndex = index11(bitmap, doubledBitpos);

          final K currentKey = getCollectionKey(collIndex);
          if (currentKey.equals(key)) {

            final ImmutableSet<V> currentValColl = getCollectionValue(collIndex);

            details.updated(currentValColl);
            return copyAndRemoveCollection(mutator, doubledBitpos);
          } else {
            return this;
          }
        }
        default:
          return this;
      }
    }

    abstract int patternOfSingleton();
    
    @Deprecated
    abstract EitherSingletonOrCollection.Type typeOfSingleton();

    @Deprecated
    abstract State stateOfSingleton();

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
      long bitmap = this.bitmap();

      int[] arities = arities(bitmap);

      final StringBuilder bldr = new StringBuilder();
      bldr.append('[');

      for (byte i = 0; i < arities[PATTERN_DATA_SINGLETON]; i++) {
        final byte pos = -1; // TODO: recoverMask(dataMap, (byte) (i + 1));
        bldr.append(String.format("@%d<#%d,#%d>", pos, Objects.hashCode(getSingletonKey(i)),
            Objects.hashCode(getSingletonValue(i))));

        if (!((i + 1) == arities[PATTERN_DATA_SINGLETON])) {
          bldr.append(", ");
        }
      }

      if (arities[PATTERN_DATA_SINGLETON] > 0 && arities[PATTERN_DATA_COLLECTION] > 0) {
        bldr.append(", ");
      }

      for (byte i = 0; i < arities[PATTERN_DATA_COLLECTION]; i++) {
        final byte pos = -1; // TODO: recoverMask(collMap, (byte) (i + 1));
        bldr.append(String.format("@%d<#%d,#%d>", pos, Objects.hashCode(getCollectionKey(i)),
            Objects.hashCode(getCollectionValue(i))));

        if (!((i + 1) == arities[PATTERN_DATA_COLLECTION])) {
          bldr.append(", ");
        }
      }

      if (arities[PATTERN_DATA_COLLECTION] > 0 && arities[PATTERN_NODE] > 0) {
        bldr.append(", ");
      }

      for (byte i = 0; i < arities[PATTERN_NODE]; i++) {
        final byte pos = -1; // TODO: recoverMask(nodeMap, (byte) (i + 1));
        bldr.append(String.format("@%d: %s", pos, getNode(i)));

        if (!((i + 1) == arities[PATTERN_NODE])) {
          bldr.append(", ");
        }
      }

      bldr.append(']');
      return bldr.toString();
    }

  }

  protected static abstract class CompactMixedSetMultimapNode<K, V>
      extends CompactSetMultimapNode<K, V> {

    private final long bitmap;

    CompactMixedSetMultimapNode(final AtomicReference<Thread> mutator, final long bitmap) {
      this.bitmap = bitmap;
    }

    @Override
    public long bitmap() {
      return bitmap;
    }

    // @Override
    // int dataMap() {
    // return rawMap2() ^ collMap();
    // }
    //
    // @Override
    // int collMap() {
    // return rawMap1() & rawMap2();
    // }
    //
    // @Override
    // int nodeMap() {
    // return rawMap1() ^ collMap();
    // }

  }

  private static final class BitmapIndexedSetMultimapNode<K, V>
      extends CompactMixedSetMultimapNode<K, V> {

    final AtomicReference<Thread> mutator;
    final Object[] nodes;

    private BitmapIndexedSetMultimapNode(final AtomicReference<Thread> mutator, final long bitmap,
        final Object[] nodes) {
      super(mutator, bitmap);

      this.mutator = mutator;
      this.nodes = nodes;

      if (DEBUG) {
        int[] arities = arities(bitmap);

        assert (TUPLE_LENGTH * arities[PATTERN_DATA_SINGLETON]
            + TUPLE_LENGTH * arities[PATTERN_DATA_COLLECTION]
            + arities[PATTERN_NODE] == nodes.length);

        for (int i = 0; i < arities[PATTERN_DATA_SINGLETON]; i++) {
          int offset = i * TUPLE_LENGTH;

          assert ((nodes[offset + 0] instanceof ImmutableSet) == false);
          assert ((nodes[offset + 1] instanceof ImmutableSet) == false);

          assert ((nodes[offset + 0] instanceof CompactSetMultimapNode) == false);
          assert ((nodes[offset + 1] instanceof CompactSetMultimapNode) == false);
        }

        for (int i = 0; i < arities[PATTERN_DATA_COLLECTION]; i++) {
          int offset = (i + arities[PATTERN_DATA_SINGLETON]) * TUPLE_LENGTH;

          assert ((nodes[offset + 0] instanceof ImmutableSet) == false);
          assert ((nodes[offset + 1] instanceof ImmutableSet) == true);

          assert ((nodes[offset + 0] instanceof CompactSetMultimapNode) == false);
          assert ((nodes[offset + 1] instanceof CompactSetMultimapNode) == false);
        }

        for (int i = 0; i < arities[PATTERN_NODE]; i++) {
          int offset =
              (arities[PATTERN_DATA_SINGLETON] + arities[PATTERN_DATA_COLLECTION]) * TUPLE_LENGTH;

          assert ((nodes[offset + i] instanceof ImmutableSet) == false);

          assert ((nodes[offset + i] instanceof CompactSetMultimapNode) == true);
        }
      }

      assert nodeInvariant();
    }

    @SuppressWarnings("unchecked")
    @Override
    K getSingletonKey(final int index) {
      return (K) nodes[TUPLE_LENGTH * index];
    }

    @SuppressWarnings("unchecked")
    @Override
    V getSingletonValue(int index) {
      return (V) nodes[TUPLE_LENGTH * index + 1];
    }

    @SuppressWarnings("unchecked")
    @Override
    K getCollectionKey(int index) {
      // TODO: improve on offset calculation (caching it, etc)
      int offset = TUPLE_LENGTH * (arity(bitmap(), PATTERN_DATA_SINGLETON) + index);
      return (K) nodes[offset];
    }

    @SuppressWarnings("unchecked")
    @Override
    ImmutableSet<V> getCollectionValue(final int index) {
      // TODO: improve on offset calculation (caching it, etc)
      int offset = TUPLE_LENGTH * (arity(bitmap(), PATTERN_DATA_SINGLETON) + index) + 1;
      return (ImmutableSet<V>) nodes[offset];
    }

    @SuppressWarnings("unchecked")
    @Override
    CompactSetMultimapNode<K, V> getNode(final int index) {
      return (CompactSetMultimapNode<K, V>) nodes[nodes.length - 1 - index];
    }

    // @Override
    // boolean hasPayload() {
    // return rawMap2() != 0;
    // }
    //
    // @Override
    // int payloadArity() {
    // return java.lang.Integer.bitCount(rawMap2());
    // }

    @Override
    int emptyArity() {
      return arity(bitmap(), PATTERN_EMPTY);
    }
    
    boolean hasPayload(EitherSingletonOrCollection.Type type) {
      return payloadArity(type) != 0;
    }

    @Override
    int payloadArity(EitherSingletonOrCollection.Type type) {
      // int[] arities = arities(bitmap());
      // return arities[PATTERN_NODE];

      if (type == Type.SINGLETON) {
        // return arities[PATTERN_DATA_SINGLETON];
        return arity(bitmap(), PATTERN_DATA_SINGLETON);
      } else {
        // return arities[PATTERN_DATA_COLLECTION];
        return arity(bitmap(), PATTERN_DATA_COLLECTION);
      }
    }

    @Override
    boolean hasNodes() {
      return nodeArity() != 0;
    }

    @Override
    int nodeArity() {
//      int[] arities = arities(bitmap());
//      return arities[PATTERN_NODE];
        return arity(bitmap(), PATTERN_NODE);
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
      result = (int) (prime * result + (bitmap()));
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
      if (bitmap() != that.bitmap()) {
        return false;
      }
      if (!Arrays.equals(nodes, that.nodes)) {
        return false;
      }
      return true;
    }

    @Override
    byte sizePredicate() {

//      switch (slotArity()) {
//        case 0:
//          return SIZE_EMPTY;
//        case 1:
//          return SIZE_MORE_THAN_ONE; // works for maps only: must be subnode; patternOfSingleton();
//        case 2:
//          return arity(bitmap(), PATTERN_NODE) == 0 ? SIZE_ONE : SIZE_MORE_THAN_ONE;
//        default:
//          return SIZE_MORE_THAN_ONE;
//      }
      
//      if (this.nodeArity() == 0) {
//        switch (this.payloadArity()) {
//          case 0:
//            return SIZE_EMPTY;
//          case 1:
//            return SIZE_ONE;
//          default:
//            return SIZE_MORE_THAN_ONE;
//        }
//      } else {
//        return SIZE_MORE_THAN_ONE;
//      }
      
      
//      int[] arities = arities(bitmap());
//      
//      int nodeArity = arities[PATTERN_NODE];
//      int emptyArity = arities[PATTERN_EMPTY];
      
      final long bitmap = this.bitmap();
      
      int nodeArity = arity(bitmap, PATTERN_NODE);
      int emptyArity = arity(bitmap, PATTERN_EMPTY);
      
//      int aritySingleton = arity(bitmap, PATTERN_DATA_SINGLETON);
//      int arityCollection = arity(bitmap, PATTERN_DATA_COLLECTION);
      
      if (nodeArity > 0) {
        return SIZE_MORE_THAN_ONE;
      } else {
        switch (emptyArity) {
          case 32:
            return SIZE_EMPTY;
          case 31:
            return SIZE_ONE;
          default:
            return SIZE_MORE_THAN_ONE;
        }
      }
      
//      if (this.nodeArity() == 0) {
//        switch (arity(bitmap(), PATTERN_DATA_SINGLETON)
//            + arity(bitmap(), PATTERN_DATA_COLLECTION)) {
//          case 0:
//            return SIZE_EMPTY;
//          case 1:
//            return SIZE_ONE;
//          default:
//            return SIZE_MORE_THAN_ONE;
//        }
//      } else {
//        return SIZE_MORE_THAN_ONE;
//      }
    }

    @Override
    CompactSetMultimapNode<K, V> copyAndSetSingletonValue(final AtomicReference<Thread> mutator,
        final long doubledBitpos, final V val) {

      final int idx = TUPLE_LENGTH * dataIndex(doubledBitpos) + 1;
      final CompactSetMultimapNode<K, V> updatedNode = copyAndSetXxxValue(mutator, idx, val);

      return updatedNode;
    }

    @Override
    CompactSetMultimapNode<K, V> copyAndSetCollectionValue(final AtomicReference<Thread> mutator,
        final long doubledBitpos, final ImmutableSet<V> valColl) {

      final int idx =
          TUPLE_LENGTH * (arity(bitmap(), PATTERN_DATA_SINGLETON) + collIndex(doubledBitpos)) + 1;
      final CompactSetMultimapNode<K, V> updatedNode = copyAndSetXxxValue(mutator, idx, valColl);

      return updatedNode;
    }

    private CompactSetMultimapNode<K, V> copyAndSetXxxValue(final AtomicReference<Thread> mutator,
        final int idx, final Object newValue) {
      final CompactSetMultimapNode<K, V> updatedNode;
      if (isAllowedToEdit(this.mutator, mutator)) {
        // no copying if already editable
        this.nodes[idx] = newValue;
        updatedNode = this;
      } else {
        final Object[] src = this.nodes;
        final Object[] dst = new Object[src.length];

        // copy 'src' and set 1 element(s) at position 'idx'
        System.arraycopy(src, 0, dst, 0, src.length);
        dst[idx + 0] = newValue;

        updatedNode = nodeOf(mutator, bitmap(), dst);
      }
      return updatedNode;
    }

    @Override
    CompactSetMultimapNode<K, V> copyAndSetNode(final AtomicReference<Thread> mutator,
        final long doubledBitpos, final CompactSetMultimapNode<K, V> node) {

      final int idx = this.nodes.length - 1 - nodeIndex(doubledBitpos);

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

        return nodeOf(mutator, bitmap(), dst);
      }
    }

    @Override
    CompactSetMultimapNode<K, V> copyAndInsertSingleton(final AtomicReference<Thread> mutator,
        final long doubledBitpos, final K key, final V val) {
      final int idx = TUPLE_LENGTH * dataIndex(doubledBitpos);

      final Object[] src = this.nodes;
      final Object[] dst = new Object[src.length + 2];

      // copy 'src' and insert 2 element(s) at position 'idx'
      System.arraycopy(src, 0, dst, 0, idx);
      dst[idx + 0] = key;
      dst[idx + 1] = val;
      System.arraycopy(src, idx, dst, idx + 2, src.length - idx);

      // generally: from 00 to 10
      // here: set both bits individually
      long updatedBitmap = bitmap();
      updatedBitmap |= doubledBitpos; // idempotent
      updatedBitmap ^= doubledBitpos; // idempotent
      updatedBitmap |= (doubledBitpos << 1);

      return nodeOf(mutator, updatedBitmap, dst);
    }
    
    @Override
    CompactSetMultimapNode<K, V> copyAndInsertCollection(final AtomicReference<Thread> mutator,
        final long doubledBitpos, final K key, final ImmutableSet<V> valColl) {
      final int idx = TUPLE_LENGTH * (arity(bitmap(), PATTERN_DATA_SINGLETON) + collIndex(doubledBitpos));

      final Object[] src = this.nodes;
      final Object[] dst = new Object[src.length + 2];

      // copy 'src' and insert 2 element(s) at position 'idx'
      System.arraycopy(src, 0, dst, 0, idx);
      dst[idx + 0] = key;
      dst[idx + 1] = valColl;
      System.arraycopy(src, idx, dst, idx + 2, src.length - idx);

      // generally: from 00 to 11
      // here: set both bits individually
      long updatedBitmap = bitmap();
      updatedBitmap |= doubledBitpos;
      updatedBitmap |= (doubledBitpos << 1);

      return nodeOf(mutator, updatedBitmap, dst);
    }    

    @Override
    CompactSetMultimapNode<K, V> copyAndMigrateFromSingletonToCollection(
        AtomicReference<Thread> mutator, long doubledBitpos, K key, ImmutableSet<V> valColl) {

      final int idxOld = TUPLE_LENGTH * dataIndex(doubledBitpos);
      final int idxNew =
          TUPLE_LENGTH * (arity(bitmap(), PATTERN_DATA_SINGLETON) - 1 + collIndex(doubledBitpos));

      final Object[] src = this.nodes;
      final Object[] dst = new Object[src.length];

      // copy 'src' and remove 2 element(s) at position 'idxOld' and
      // insert 2 element(s) at position 'idxNew'
      assert idxOld <= idxNew;
      System.arraycopy(src, 0, dst, 0, idxOld);
      System.arraycopy(src, idxOld + 2, dst, idxOld, idxNew - idxOld);
      dst[idxNew + 0] = key;
      dst[idxNew + 1] = valColl;
      System.arraycopy(src, idxNew + 2, dst, idxNew + 2, src.length - idxNew - 2);

//      // generally: from 10 to 11
//      // here: set both bits individually
//      long updatedBitmap = bitmap();
//      updatedBitmap |= (doubledBitpos);
//      updatedBitmap |= (doubledBitpos << 1);
      
      long updatedBitmap = setBitPattern(bitmap(), doubledBitpos, PATTERN_DATA_COLLECTION);
      return nodeOf(mutator, updatedBitmap, dst);
    }

    @Override
    CompactSetMultimapNode<K, V> copyAndMigrateFromCollectionToSingleton(
        AtomicReference<Thread> mutator, long doubledBitpos, K key, V val) {

      // TODO: does not support src == dst yet for shifting

      final int idxOld =
          TUPLE_LENGTH * (arity(bitmap(), PATTERN_DATA_SINGLETON) + collIndex(doubledBitpos));
      final int idxNew = TUPLE_LENGTH * dataIndex(doubledBitpos);

      final Object[] src = this.nodes;
      final Object[] dst = new Object[src.length];

      // copy 'src' and remove 2 element(s) at position 'idxOld' and
      // insert 2 element(s) at position 'idxNew'
      assert idxNew <= idxOld;
      System.arraycopy(src, 0, dst, 0, idxNew);
      dst[idxNew + 0] = key;
      dst[idxNew + 1] = val;
      System.arraycopy(src, idxNew, dst, idxNew + 2, idxOld - idxNew);
      System.arraycopy(src, idxOld + 2, dst, idxOld + 2, src.length - idxOld - 2);

      // generally: from 11 to 10
      // here: set both bits individually
      long updatedBitmap = bitmap();
      updatedBitmap ^= (doubledBitpos);
      updatedBitmap |= (doubledBitpos << 1);

      return nodeOf(mutator, updatedBitmap, dst);
    }

    @Override
    CompactSetMultimapNode<K, V> copyAndRemoveSingleton(final AtomicReference<Thread> mutator,
        final long doubledBitpos) {
      final int idx = TUPLE_LENGTH * dataIndex(doubledBitpos);

      final Object[] src = this.nodes;
      final Object[] dst = new Object[src.length - 2];

      // copy 'src' and remove 2 element(s) at position 'idx'
      System.arraycopy(src, 0, dst, 0, idx);
      System.arraycopy(src, idx + 2, dst, idx, src.length - idx - 2);

      // generally: from 10 to 00
      // here: set both bits individually
      long updatedBitmap = bitmap();
      updatedBitmap |= doubledBitpos; // idempotent
      updatedBitmap ^= doubledBitpos; // idempotent
      updatedBitmap ^= (doubledBitpos << 1);

      return nodeOf(mutator, updatedBitmap, dst);
    }
    
    // TODO: remove code duplication and merge with above
    @Override
    CompactSetMultimapNode<K, V> copyAndRemoveSingleton(final AtomicReference<Thread> mutator,
        final long doubledBitpos, long updatedBitmap) {
      final int idx = TUPLE_LENGTH * dataIndex(doubledBitpos);

      final Object[] src = this.nodes;
      final Object[] dst = new Object[src.length - 2];

      // copy 'src' and remove 2 element(s) at position 'idx'
      System.arraycopy(src, 0, dst, 0, idx);
      System.arraycopy(src, idx + 2, dst, idx, src.length - idx - 2);

      return nodeOf(mutator, updatedBitmap, dst);
    }
    
    @Override
    CompactSetMultimapNode<K, V> copyAndRemoveCollection(final AtomicReference<Thread> mutator,
        final long doubledBitpos) {
      final int idx =
          TUPLE_LENGTH * (arity(bitmap(), PATTERN_DATA_SINGLETON) + collIndex(doubledBitpos));

      final Object[] src = this.nodes;
      final Object[] dst = new Object[src.length - 2];

      // copy 'src' and remove 2 element(s) at position 'idx'
      System.arraycopy(src, 0, dst, 0, idx);
      System.arraycopy(src, idx + 2, dst, idx, src.length - idx - 2);

      // generally: from 11 to 00
      // here: set both bits individually
      long updatedBitmap = bitmap();
      updatedBitmap ^= (doubledBitpos);
      updatedBitmap ^= (doubledBitpos << 1);

      return nodeOf(mutator, updatedBitmap, dst);
    }

    @Override
    CompactSetMultimapNode<K, V> copyAndMigrateFromSingletonToNode(
        final AtomicReference<Thread> mutator, final long doubledBitpos,
        final CompactSetMultimapNode<K, V> node) {

      final int idxOld = TUPLE_LENGTH * dataIndex(doubledBitpos);
      final int idxNew = this.nodes.length - TUPLE_LENGTH - nodeIndex(doubledBitpos);

      final Object[] dst = copyAndMigrateFromXxxToNode(idxOld, idxNew, node);

      // generally: from 10 to 01
      // here: set both bits individually
      long updatedBitmap = bitmap();
      updatedBitmap |= (doubledBitpos);
      updatedBitmap ^= (doubledBitpos << 1);

      return nodeOf(mutator, updatedBitmap, dst);
    }

    @Override
    CompactSetMultimapNode<K, V> copyAndMigrateFromCollectionToNode(AtomicReference<Thread> mutator,
        long doubledBitpos, CompactSetMultimapNode<K, V> node) {

      final int idxOld =
          TUPLE_LENGTH * (arity(bitmap(), PATTERN_DATA_SINGLETON) + collIndex(doubledBitpos));
      final int idxNew = this.nodes.length - TUPLE_LENGTH - nodeIndex(doubledBitpos);

      final Object[] dst = copyAndMigrateFromXxxToNode(idxOld, idxNew, node);

      // generally: from 11 to 01
      // here: set both bits individually
      long updatedBitmap = bitmap();
      updatedBitmap |= (doubledBitpos);
      updatedBitmap ^= (doubledBitpos << 1);

      return nodeOf(mutator, updatedBitmap, dst);
    }

    private Object[] copyAndMigrateFromXxxToNode(final int idxOld, final int idxNew,
        final CompactSetMultimapNode<K, V> node) {

      final Object[] src = this.nodes;
      final Object[] dst = new Object[src.length - 2 + 1];

      // copy 'src' and remove 2 element(s) at position 'idxOld' and
      // insert 1 element(s) at position 'idxNew'
      assert idxOld <= idxNew;
      System.arraycopy(src, 0, dst, 0, idxOld);
      System.arraycopy(src, idxOld + 2, dst, idxOld, idxNew - idxOld);
      dst[idxNew + 0] = node;
      System.arraycopy(src, idxNew + 2, dst, idxNew + 1, src.length - idxNew - 2);

      return dst;
    }

    @Override
    CompactSetMultimapNode<K, V> copyAndMigrateFromNodeToSingleton(
        final AtomicReference<Thread> mutator, final long doubledBitpos,
        final CompactSetMultimapNode<K, V> node) {

      final int idxOld = this.nodes.length - 1 - nodeIndex(doubledBitpos);
      final int idxNew = TUPLE_LENGTH * dataIndex(doubledBitpos);

      Object keyToInline = node.getSingletonKey(0);
      Object valToInline = node.getSingletonValue(0);

      final Object[] dst = copyAndMigrateFromNodeToXxx(idxOld, idxNew, keyToInline, valToInline);

      // generally: from 01 to 10
      // here: set both bits individually
      long updatedBitmap = bitmap();
      updatedBitmap ^= (doubledBitpos);
      updatedBitmap |= (doubledBitpos << 1);

      return nodeOf(mutator, updatedBitmap, dst);
    }

    @Override
    CompactSetMultimapNode<K, V> copyAndMigrateFromNodeToCollection(AtomicReference<Thread> mutator,
        long doubledBitpos, CompactSetMultimapNode<K, V> node) {

      final int idxOld = this.nodes.length - 1 - nodeIndex(doubledBitpos);
      final int idxNew =
          TUPLE_LENGTH * (arity(bitmap(), PATTERN_DATA_SINGLETON) + collIndex(doubledBitpos));

      Object keyToInline = node.getCollectionKey(0);
      Object valToInline = node.getCollectionValue(0);

      final Object[] dst = copyAndMigrateFromNodeToXxx(idxOld, idxNew, keyToInline, valToInline);

      // generally: from 01 to 11
      // here: set both bits individually
      long updatedBitmap = bitmap();
      updatedBitmap |= (doubledBitpos);
      updatedBitmap |= (doubledBitpos << 1);

      return nodeOf(mutator, updatedBitmap, dst);
    }

    private Object[] copyAndMigrateFromNodeToXxx(final int idxOld, final int idxNew,
        Object keyToInline, Object valToInline) {

      final Object[] src = this.nodes;
      final Object[] dst = new Object[src.length - 1 + 2];

      // copy 'src' and remove 1 element(s) at position 'idxOld' and
      // insert 2 element(s) at position 'idxNew'
      assert idxOld >= idxNew;
      System.arraycopy(src, 0, dst, 0, idxNew);
      dst[idxNew + 0] = keyToInline;
      dst[idxNew + 1] = valToInline;
      System.arraycopy(src, idxNew, dst, idxNew + 2, idxOld - idxNew);
      System.arraycopy(src, idxOld + 1, dst, idxOld + 2, src.length - idxOld - 1);

      return dst;
    }

    @Override
    CompactSetMultimapNode<K, V> copyAndUpdateBitmaps(AtomicReference<Thread> mutator,
        final long bitmap) {
      return nodeOf(mutator, bitmap, nodes);
    }

    @Override
    int patternOfSingleton() {
      assert this.sizePredicate() == SIZE_ONE;

      long bitmap = this.bitmap();

      final int doubledMask = Long.numberOfTrailingZeros(bitmap) / 2 * 2;
      final int pattern = pattern(bitmap, doubledMask);

      return pattern;
    }
    
    @Override
    State stateOfSingleton() {
      assert this.sizePredicate() == SIZE_ONE;

      long bitmap = this.bitmap();

      final int doubledMask = Long.numberOfTrailingZeros(bitmap) / 2 * 2;
      final int pattern = pattern(bitmap, doubledMask);

      return toState(pattern);
    }    
    
    @Deprecated
    @Override
    EitherSingletonOrCollection.Type typeOfSingleton() {
      final int pattern = patternOfSingleton();
      
      if (pattern == PATTERN_DATA_SINGLETON) {
        return EitherSingletonOrCollection.Type.SINGLETON;
      } else {
        return EitherSingletonOrCollection.Type.COLLECTION;
      }      
    }

  }

  // private static final class HashCollisionSetMultimapNode_BleedingEdge<K, V>
  // extends CompactSetMultimapNode<K, V> {
  // private final K[] keys;
  // private final ImmutableSet<V>[] vals;
  // private final int hash;
  //
  // HashCollisionSetMultimapNode_BleedingEdge(final int hash, final K[] keys,
  // final ImmutableSet<V>[] vals) {
  // this.keys = keys;
  // this.vals = vals;
  // this.hash = hash;
  //
  // // assert payloadArity() >= 2;
  // }
  //
  // @Override
  // boolean containsKey(final K key, final int keyHash, final int shift) {
  // if (this.hash == keyHash) {
  // for (K k : keys) {
  // if (k.equals(key)) {
  // return true;
  // }
  // }
  // }
  // return false;
  // }
  //
  // @Override
  // boolean containsKey(final K key, final int keyHash, final int shift,
  // final Comparator<Object> cmp) {
  // throw new UnsupportedOperationException("Not yet implemented.");
  // }
  //
  // @Override
  // Optional<ImmutableSet<V>> findByKey(final K key, final int keyHash, final int shift) {
  // for (int i = 0; i < keys.length; i++) {
  // final K _key = keys[i];
  // if (key.equals(_key)) {
  // final ImmutableSet<V> valColl = vals[i];
  // return Optional.of(valColl);
  // }
  // }
  // return Optional.empty();
  // }
  //
  // @Override
  // Optional<ImmutableSet<V>> findByKey(final K key, final int keyHash, final int shift,
  // final Comparator<Object> cmp) {
  // throw new UnsupportedOperationException("Not yet implemented.");
  // }
  //
  // @Override
  // CompactSetMultimapNode<K, V> inserted(final AtomicReference<Thread> mutator, final K key,
  // final V val, final int keyHash, final int shift, final SetMultimapResult<K, V> details) {
  // assert this.hash == keyHash;
  //
  // for (int idx = 0; idx < keys.length; idx++) {
  // if (keys[idx].equals(key)) {
  // final ImmutableSet<V> currentValColl = vals[idx];
  //
  // if (currentValColl.contains(val)) {
  // return this;
  // } else {
  // // add new mapping
  // final ImmutableSet<V> valCollNew = currentValColl.__insert(val);
  //
  // final ImmutableSet<V>[] src = this.vals;
  // @SuppressWarnings("unchecked")
  // final ImmutableSet<V>[] dst = new ImmutableSet[src.length];
  //
  // // copy 'src' and set 1 element(s) at position 'idx'
  // System.arraycopy(src, 0, dst, 0, src.length);
  // dst[idx + 0] = valCollNew;
  //
  // final CompactSetMultimapNode<K, V> thisNew =
  // new HashCollisionSetMultimapNode_BleedingEdge<>(this.hash, this.keys, dst);
  //
  // details.modified();
  // return thisNew;
  // }
  // }
  // }
  //
  // // add new tuple
  // final ImmutableSet<V> valCollNew = setOf(val);
  //
  // @SuppressWarnings("unchecked")
  // final K[] keysNew = (K[]) new Object[this.keys.length + 1];
  //
  // // copy 'this.keys' and insert 1 element(s) at position
  // // 'keys.length'
  // System.arraycopy(this.keys, 0, keysNew, 0, keys.length);
  // keysNew[keys.length + 0] = key;
  // System.arraycopy(this.keys, keys.length, keysNew, keys.length + 1,
  // this.keys.length - keys.length);
  //
  // @SuppressWarnings("unchecked")
  // final ImmutableSet<V>[] valsNew = new ImmutableSet[this.vals.length + 1];
  //
  // // copy 'this.vals' and insert 1 element(s) at position
  // // 'vals.length'
  // System.arraycopy(this.vals, 0, valsNew, 0, vals.length);
  // valsNew[vals.length + 0] = valCollNew;
  // System.arraycopy(this.vals, vals.length, valsNew, vals.length + 1,
  // this.vals.length - vals.length);
  //
  // details.modified();
  // return new HashCollisionSetMultimapNode_BleedingEdge<>(keyHash, keysNew, valsNew);
  // }
  //
  // @Override
  // CompactSetMultimapNode<K, V> removed(final AtomicReference<Thread> mutator, final K key,
  // final V val, final int keyHash, final int shift, final SetMultimapResult<K, V> details) {
  // for (int idx = 0; idx < keys.length; idx++) {
  // if (keys[idx].equals(key)) {
  // final ImmutableSet<V> currentValColl = getCollectionValue(idx);
  //
  // if (currentValColl.contains(val)) {
  // details.updated(val);
  //
  // // remove tuple
  // final ImmutableSet<V> valCollNew = currentValColl.__remove(val);
  //
  // if (valCollNew.size() != 0) {
  // // update mapping
  // @SuppressWarnings("unchecked")
  // final ImmutableSet<V>[] valsNew = new ImmutableSet[this.vals.length];
  //
  // // copy 'this.vals' and set 1 element(s) at position
  // // 'idx'
  // System.arraycopy(this.vals, 0, valsNew, 0, this.vals.length);
  // valsNew[idx + 0] = valCollNew;
  //
  // return new HashCollisionSetMultimapNode_BleedingEdge<>(keyHash, keys, valsNew);
  // } else {
  // // drop mapping
  // if (this.arity() == 2) {
  // /*
  // * Create root node with singleton element. This node will be a) either be the new
  // * root returned, or b) unwrapped and inlined.
  // */
  // final K theOtherKey = (idx == 0) ? keys[1] : keys[0];
  // final ImmutableSet<V> theOtherVal = (idx == 0) ? vals[1] : vals[0];
  //
  // final int nodeMap = 0;
  // final int dataMap = doubledBitpos(mask(hash, 0));
  //
  // return CompactSetMultimapNode.<K, V>nodeOf(mutator, bitmap, theOtherKey,
  // theOtherVal);
  // } else {
  // @SuppressWarnings("unchecked")
  // final K[] keysNew = (K[]) new Object[this.keys.length - 1];
  //
  // // copy 'this.keys' and remove 1 element(s) at
  // // position 'idx'
  // System.arraycopy(this.keys, 0, keysNew, 0, idx);
  // System.arraycopy(this.keys, idx + 1, keysNew, idx, this.keys.length - idx - 1);
  //
  // @SuppressWarnings("unchecked")
  // final ImmutableSet<V>[] valsNew = new ImmutableSet[this.vals.length - 1];
  //
  // // copy 'this.vals' and remove 1 element(s) at
  // // position 'idx'
  // System.arraycopy(this.vals, 0, valsNew, 0, idx);
  // System.arraycopy(this.vals, idx + 1, valsNew, idx, this.vals.length - idx - 1);
  //
  // return new HashCollisionSetMultimapNode_BleedingEdge<>(keyHash, keysNew, valsNew);
  // }
  // }
  // } else {
  // return this;
  // }
  // }
  // }
  // return this;
  // }
  //
  // // @Override
  // // boolean hasPayload() {
  // // return true;
  // // }
  // //
  // // @Override
  // // int payloadArity() {
  // // return keys.length;
  // // }
  //
  // @Override
  // boolean hasNodes() {
  // return false;
  // }
  //
  // @Override
  // int nodeArity() {
  // return 0;
  // }
  //
  // // @Override
  // // int arity() {
  // // return payloadArity();
  // // }
  //
  // @Override
  // byte sizePredicate() {
  // return SIZE_MORE_THAN_ONE;
  // }
  //
  // @Override
  // K getSingletonKey(final int index) {
  // return keys[index];
  // }
  //
  // @Override
  // ImmutableSet<V> getCollectionValue(final int index) {
  // return vals[index];
  // }
  //
  // @Override
  // public CompactSetMultimapNode<K, V> getNode(int index) {
  // throw new IllegalStateException("Is leaf node.");
  // }
  //
  // @Override
  // Object getSlot(final int index) {
  // throw new UnsupportedOperationException();
  // }
  //
  // @Override
  // boolean hasSlots() {
  // throw new UnsupportedOperationException();
  // }
  //
  // @Override
  // int slotArity() {
  // throw new UnsupportedOperationException();
  // }
  //
  // @Override
  // public int hashCode() {
  // final int prime = 31;
  // int result = 0;
  // result = prime * result + hash;
  // result = prime * result + Arrays.hashCode(keys);
  // result = prime * result + Arrays.hashCode(vals);
  // return result;
  // }
  //
  // @Override
  // public boolean equals(Object other) {
  // throw new UnsupportedOperationException();
  // // if (null == other) {
  // // return false;
  // // }
  // // if (this == other) {
  // // return true;
  // // }
  // // if (getClass() != other.getClass()) {
  // // return false;
  // // }
  // //
  // // HashCollisionSetMultimapNode_BleedingEdge<?, ?> that =
  // // (HashCollisionSetMultimapNode_BleedingEdge<?, ?>) other;
  // //
  // // if (hash != that.hash) {
  // // return false;
  // // }
  // //
  // // if (arity() != that.arity()) {
  // // return false;
  // // }
  // //
  // // /*
  // // * Linear scan for each key, because of arbitrary element order.
  // // */
  // // outerLoop: for (int i = 0; i < that.payloadArity(); i++) {
  // // final Object otherKey = that.getSingletonKey(i);
  // // final Object otherVal = that.getCollectionValue(i);
  // //
  // // for (int j = 0; j < keys.length; j++) {
  // // final K key = keys[j];
  // // final ImmutableSet<V> valColl = vals[j];
  // //
  // // if (key.equals(otherKey) && valColl.equals(otherVal)) {
  // // continue outerLoop;
  // // }
  // // }
  // // return false;
  // // }
  // //
  // // return true;
  // }
  //
  // @Override
  // CompactSetMultimapNode<K, V> copyAndSetCollectionValue(final AtomicReference<Thread> mutator,
  // final long doubledBitpos, final ImmutableSet<V> valColl) {
  // throw new UnsupportedOperationException();
  // }
  //
  // @Override
  // CompactSetMultimapNode<K, V> copyAndInsertSingleton(final AtomicReference<Thread> mutator,
  // final long doubledBitpos, final K key, final V val) {
  // throw new UnsupportedOperationException();
  // }
  //
  // @Override
  // CompactSetMultimapNode<K, V> copyAndRemoveSingleton(final AtomicReference<Thread> mutator,
  // final long doubledBitpos) {
  // throw new UnsupportedOperationException();
  // }
  //
  // @Override
  // CompactSetMultimapNode<K, V> copyAndSetNode(final AtomicReference<Thread> mutator,
  // final long doubledBitpos, final CompactSetMultimapNode<K, V> node) {
  // throw new UnsupportedOperationException();
  // }
  //
  // @Override
  // CompactSetMultimapNode<K, V> copyAndMigrateFromSingletonToNode(
  // final AtomicReference<Thread> mutator, final long doubledBitpos,
  // final CompactSetMultimapNode<K, V> node) {
  // throw new UnsupportedOperationException();
  // }
  //
  // @Override
  // CompactSetMultimapNode<K, V> copyAndMigrateFromNodeToSingleton(
  // final AtomicReference<Thread> mutator, final long doubledBitpos,
  // final CompactSetMultimapNode<K, V> node) {
  // throw new UnsupportedOperationException();
  // }
  //
  // @Override
  // long bitmap() {
  // throw new UnsupportedOperationException();
  // }
  //
  // @Override
  // int dataMap() {
  // throw new UnsupportedOperationException();
  // }
  //
  // @Override
  // int collMap() {
  // throw new UnsupportedOperationException();
  // }
  //
  // @Override
  // int nodeMap() {
  // throw new UnsupportedOperationException();
  // }
  //
  // @Override
  // CompactSetMultimapNode<K, V> copyAndMigrateFromSingletonToCollection(
  // AtomicReference<Thread> mutator, long doubledBitpos, K key, ImmutableSet<V> valColl) {
  // throw new UnsupportedOperationException();
  // }
  //
  // @Override
  // CompactSetMultimapNode<K, V> copyAndMigrateFromCollectionToNode(AtomicReference<Thread>
  // mutator,
  // long doubledBitpos, CompactSetMultimapNode<K, V> node) {
  // throw new UnsupportedOperationException();
  // }
  //
  // @Override
  // CompactSetMultimapNode<K, V> copyAndMigrateFromNodeToCollection(AtomicReference<Thread>
  // mutator,
  // long doubledBitpos, CompactSetMultimapNode<K, V> node) {
  // throw new UnsupportedOperationException();
  // }
  //
  // @Override
  // V getSingletonValue(int index) {
  // // TODO Auto-generated method stub
  // return null;
  // }
  //
  // @Override
  // K getCollectionKey(int index) {
  // // TODO Auto-generated method stub
  // return null;
  // }
  //
  // @Override
  // CompactSetMultimapNode<K, V> copyAndUpdateBitmaps(AtomicReference<Thread> mutator,
  // long bitmap) {
  // throw new UnsupportedOperationException();
  // }
  //
  // @Override
  // CompactSetMultimapNode<K, V> canonicalize(AtomicReference<Thread> mutator, int keyHash,
  // int shift) {
  // throw new UnsupportedOperationException();
  // }
  //
  // @Override
  // CompactSetMultimapNode<K, V> copyAndMigrateFromCollectionToSingleton(
  // AtomicReference<Thread> mutator, long doubledBitpos, K key, V val) {
  // throw new UnsupportedOperationException();
  // }
  //
  // @Override
  // io.usethesource.capsule.TrieSetMultimap_HHAMT.EitherSingletonOrCollection.Type
  // typeOfSingleton() {
  // // TODO Auto-generated method stub
  // return null;
  // }
  //
  // @Override
  // boolean hasPayload(Type type) {
  // // TODO Auto-generated method stub
  // return false;
  // }
  //
  // @Override
  // int payloadArity(Type type) {
  // // TODO Auto-generated method stub
  // return 0;
  // }
  //
  // @Override
  // CompactSetMultimapNode<K, V> copyAndRemoveCollection(AtomicReference<Thread> mutator,
  // long doubledBitpos) {
  // // TODO Auto-generated method stub
  // return null;
  // }
  //
  // @Override
  // CompactSetMultimapNode<K, V> copyAndSetSingletonValue(AtomicReference<Thread> mutator,
  // long doubledBitpos, V val) {
  // // TODO Auto-generated method stub
  // return null;
  // }
  //
  // }

  /**
   * Iterator skeleton that uses a fixed stack in depth.
   */
  private static abstract class AbstractSetMultimapIterator<K, V> {

    private static final int MAX_DEPTH = 7;

    protected int currentValueSingletonCursor;
    protected int currentValueSingletonLength;
    protected int currentValueCollectionCursor;
    protected int currentValueCollectionLength;
    protected AbstractSetMultimapNode<K, V> currentValueNode;

    private int currentStackLevel = -1;
    private final int[] nodeCursorsAndLengths = new int[MAX_DEPTH * 2];

    @SuppressWarnings("unchecked")
    AbstractSetMultimapNode<K, V>[] nodes = new AbstractSetMultimapNode[MAX_DEPTH];

    AbstractSetMultimapIterator(AbstractSetMultimapNode<K, V> rootNode) {
      int nodeArity = rootNode.nodeArity();       
      if (nodeArity != 0) {
        currentStackLevel = 0;

        nodes[0] = rootNode;
        nodeCursorsAndLengths[0] = 0;
        nodeCursorsAndLengths[1] = nodeArity;
      }

      int emptyArity = rootNode.emptyArity();
      if (emptyArity + nodeArity < 32) {
        currentValueNode = rootNode;
        currentValueSingletonCursor = 0;
        currentValueSingletonLength = rootNode.payloadArity(SINGLETON);
        currentValueCollectionCursor = 0;
        currentValueCollectionLength = rootNode.payloadArity(COLLECTION);
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

          int nodeArity = nextNode.nodeArity();
          if (nodeArity != 0) {
            /*
             * put node on next stack level for depth-first traversal
             */
            final int nextStackLevel = ++currentStackLevel;
            final int nextCursorIndex = nextStackLevel * 2;
            final int nextLengthIndex = nextCursorIndex + 1;

            nodes[nextStackLevel] = nextNode;
            nodeCursorsAndLengths[nextCursorIndex] = 0;
            nodeCursorsAndLengths[nextLengthIndex] = nodeArity;
          }

          
//          int emptyArity = nextNode.emptyArity();
//          if (emptyArity + nodeArity < 32) {
          if (nextNode.hasPayload(SINGLETON) || nextNode.hasPayload(COLLECTION)) {
//          if (payloadAritySingleton != 0 || payloadArityCollection != 0) {
            /*
             * found next node that contains values
             */
            currentValueNode = nextNode;
            currentValueSingletonCursor = 0;
            currentValueSingletonLength = nextNode.payloadArity(SINGLETON);
            currentValueCollectionCursor = 0;
            currentValueCollectionLength = nextNode.payloadArity(Type.COLLECTION);
            return true;
          }
        } else {
          currentStackLevel--;
        }
      }

      return false;
    }

    public boolean hasNext() {
      if (currentValueSingletonCursor < currentValueSingletonLength
          || currentValueCollectionCursor < currentValueCollectionLength) {
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
        // TODO: check case distinction
        if (currentValueSingletonCursor < currentValueSingletonLength) {
          return currentValueNode.getSingletonKey(currentValueSingletonCursor++);
        } else {
          return currentValueNode.getCollectionKey(currentValueCollectionCursor++);
        }
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
        // TODO: check case distinction
        if (currentValueSingletonCursor < currentValueSingletonLength) {
          return setOf(currentValueNode.getSingletonValue(currentValueSingletonCursor++));
        } else {
          return currentValueNode.getCollectionValue(currentValueCollectionCursor++);
        }
      }
    }

  }

  protected static class SetMultimapNativeTupleIterator<K, V>
      extends AbstractSetMultimapIterator<K, V> implements Iterator<Map.Entry<K, Object>> {

    SetMultimapNativeTupleIterator(AbstractSetMultimapNode<K, V> rootNode) {
      super(rootNode);
    }

    @Override
    public Map.Entry<K, Object> next() {
      if (!hasNext()) {
        throw new NoSuchElementException();
      } else {
        // TODO: check case distinction
        if (currentValueSingletonCursor < currentValueSingletonLength) {
          final K currentKey = currentValueNode.getSingletonKey(currentValueSingletonCursor);
          final Object currentValue =
              currentValueNode.getSingletonValue(currentValueSingletonCursor);
          currentValueSingletonCursor++;

          return AbstractSpecialisedImmutableMap.entryOf(currentKey, currentValue);
        } else {
          final K currentKey = currentValueNode.getCollectionKey(currentValueCollectionCursor);
          final Object currentValue =
              currentValueNode.getCollectionValue(currentValueCollectionCursor);
          currentValueCollectionCursor++;

          return AbstractSpecialisedImmutableMap.entryOf(currentKey, currentValue);
        }
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

    @Override
    public boolean hasNext() {
      if (currentSetIterator.hasNext()) {
        return true;
      } else {
        if (super.hasNext()) {
          // TODO: check case distinction
          if (currentValueSingletonCursor < currentValueSingletonLength) {
            currentKey = currentValueNode.getSingletonKey(currentValueSingletonCursor);
            currentSetIterator = Collections
                .singleton(currentValueNode.getSingletonValue(currentValueSingletonCursor))
                .iterator();
            currentValueSingletonCursor++;
          } else {
            currentKey = currentValueNode.getCollectionKey(currentValueCollectionCursor);
            currentSetIterator =
                currentValueNode.getCollectionValue(currentValueCollectionCursor).iterator();
            currentValueCollectionCursor++;
          }

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
        TrieSetMultimap_HHAMT<K, V> trieSetMultimap_BleedingEdge) {
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

    @Override
    public V put(final K key, final V val) {
      throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(final SetMultimap_Legacy<? extends K, ? extends V> m) {
      throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
      throw new UnsupportedOperationException();
    }

    @Override
    public V remove(final Object key, final Object val) {
      throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsKey(final Object o) {
      try {
        @SuppressWarnings("unchecked")
        final K key = (K) o;
        return rootNode.containsKey(key, transformHashCode(key.hashCode()), 0);
      } catch (ClassCastException unused) {
        return false;
      }
    }

    @Override
    public boolean containsKeyEquivalent(final Object o, final Comparator<Object> cmp) {
      throw new UnsupportedOperationException("Not yet implemented.");
    }

    @Override
    public boolean containsValue(final Object o) {
      for (Iterator<V> iterator = valueIterator(); iterator.hasNext();) {
        if (iterator.next().equals(o)) {
          return true;
        }
      }
      return false;
    }

    @Override
    public boolean containsValueEquivalent(final Object o, final Comparator<Object> cmp) {
      throw new UnsupportedOperationException("Not yet implemented.");
    }

    @Override
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

    @Override
    public boolean containsEntryEquivalent(final Object o0, final Object o1,
        final Comparator<Object> cmp) {
      throw new UnsupportedOperationException("Not yet implemented.");
    }

    @Override
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

    @Override
    public ImmutableSet<V> getEquivalent(final Object o, final Comparator<Object> cmp) {
      throw new UnsupportedOperationException("Not yet implemented.");
    }

    @Override
    public boolean __put(K key, ImmutableSet<V> valColl) {
      if (mutator.get() == null) {
        throw new IllegalStateException("Transient already frozen.");
      }
      
//      if (valColl.size() == 1) {
//        throw new IllegalStateException();
//      }
     
      final int keyHash = key.hashCode();
      final SetMultimapResult<K, V> details = SetMultimapResult.unchanged();

      final CompactSetMultimapNode<K, V> newRootNode =
          rootNode.updated(null, key, valColl, transformHashCode(keyHash), 0, details);

      if (details.isModified()) {
        if (details.hasReplacedValue()) {
          if (details.getType() == EitherSingletonOrCollection.Type.SINGLETON) {
            final int valHashOld = details.getReplacedValue().hashCode();
            final int valHashNew = valColl.hashCode();

            rootNode = newRootNode;
//            hashCode += 0;
//            cachedSize += 0;
            
//            return new TrieSetMultimap_HHAMT<K, V>(newRootNode,
//                hashCode + ((keyHash ^ valHashNew)) - ((keyHash ^ valHashOld)), cachedSize);

            throw new IllegalStateException();
//            
//            return true;
          } else {
            int sumOfReplacedHashes = 0;

            for (V replaceValue : details.getReplacedCollection()) {
              sumOfReplacedHashes += (keyHash ^ replaceValue.hashCode());
            }

            final int valHashNew = valColl.hashCode();

            rootNode = newRootNode;
//          hashCode += 0;
//          cachedSize += 0;
            
//            return new TrieSetMultimap_HHAMT<K, V>(newRootNode,
//                hashCode + ((keyHash ^ valHashNew)) - sumOfReplacedHashes,
//                cachedSize - details.getReplacedCollection().size() + 1);

            throw new IllegalStateException();            
//            
//            return true;         
          }
        }

        int sumOfNewHashes = 0;
        
//        for (V newValue : valColl) {
//          sumOfNewHashes += (keyHash ^ newValue.hashCode());
//        }

        rootNode = newRootNode;
        hashCode += sumOfNewHashes;
        cachedSize += valColl.size();
        
//        final int valHash = valColl.hashCode();
//        return new TrieSetMultimap_HHAMT<K, V>(newRootNode, hashCode + ((keyHash ^ valHash)),
//            cachedSize + 1);
        
        return true;
      }

      return false;
    }
        
    @Override
    public boolean __insert(final K key, final V val) {
      if (mutator.get() == null) {
        throw new IllegalStateException("Transient already frozen.");
      }

      final int keyHash = key.hashCode();
      final SetMultimapResult<K, V> details = SetMultimapResult.unchanged();

      final CompactSetMultimapNode<K, V> newRootNode =
          rootNode.inserted(mutator, key, val, transformHashCode(keyHash), 0, details);

      if (details.isModified()) {
        /*
         * TODO: Fix __put(K key, ImmutableSet<V> valColl) for batch insertion and re-enabled
         * fast-fail check.
         */
//        final int valHashNew = val.hashCode();
        rootNode = newRootNode;
//        hashCode += (keyHash ^ valHashNew);
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

    @Override
    public boolean __insertEquivalent(final K key, final V val, final Comparator<Object> cmp) {
      throw new UnsupportedOperationException("Not yet implemented.");
    }

    @Override
    public boolean __insertAll(final SetMultimap_Legacy<? extends K, ? extends V> setMultimap) {
      boolean modified = false;

      for (Map.Entry<? extends K, ? extends V> entry : setMultimap.entrySet()) {
        modified |= this.__insert(entry.getKey(), entry.getValue());
      }

      return modified;
    }

    @Override
    public boolean __insertAllEquivalent(final SetMultimap_Legacy<? extends K, ? extends V> setMultimap,
        final Comparator<Object> cmp) {
      throw new UnsupportedOperationException("Not yet implemented.");
    }

    @Override
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

    @Override
    public boolean __removeTupleEquivalent(final K key, final V val, final Comparator<Object> cmp) {
      throw new UnsupportedOperationException("Not yet implemented.");
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
      return new TransientSetMultimapKeyIterator<>(this);
    }

    @Override
    public Iterator<V> valueIterator() {
      return valueCollectionsStream().flatMap(Set::stream).iterator();
    }

    @Override
    public Iterator<Map.Entry<K, V>> entryIterator() {
      return new TransientSetMultimapTupleIterator<>(this,
          AbstractSpecialisedImmutableMap::entryOf);
    }

    @Override
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

      @Override
      public K next() {
        return lastKey = super.next();
      }

      @Override
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

      @Override
      public ImmutableSet<V> next() {
        return super.next();
      }

      @Override
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

      @Override
      public T next() {
        return super.next();
      }

      @Override
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

        /*
         * TODO: Fix __put(K key, ImmutableSet<V> valColl) for batch insertion and re-enabled
         * fast-fail check.
         */
//        if (this.hashCode != that.hashCode) {
//          return false;
//        }

        return rootNode.equals(that.rootNode);
      } else if (other instanceof SetMultimap_Legacy) {
        SetMultimap_Legacy that = (SetMultimap_Legacy) other;

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
      return new TrieSetMultimap_HHAMT<K, V>(rootNode, hashCode, cachedSize);
    }
  }

}
