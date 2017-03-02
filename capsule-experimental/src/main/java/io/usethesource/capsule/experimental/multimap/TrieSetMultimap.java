/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.capsule.experimental.multimap;

import static io.usethesource.capsule.experimental.multimap.SetMultimapUtils.*;
import static io.usethesource.capsule.experimental.multimap.TrieSetMultimap.EitherSingletonOrCollection.Type.COLLECTION;
import static io.usethesource.capsule.experimental.multimap.TrieSetMultimap.EitherSingletonOrCollection.Type.SINGLETON;
import static io.usethesource.capsule.util.BitmapUtils.filter;
import static io.usethesource.capsule.util.BitmapUtils.index;
import static io.usethesource.capsule.util.collection.AbstractSpecialisedImmutableMap.entryOf;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.usethesource.capsule.api.experimental.Set;
import io.usethesource.capsule.api.experimental.SetMultimap;
import io.usethesource.capsule.experimental.multimap.TrieSetMultimap.EitherSingletonOrCollection.Type;
import io.usethesource.capsule.util.EqualityComparator;
import io.usethesource.capsule.util.collection.AbstractSpecialisedImmutableMap;

@SuppressWarnings("rawtypes")
public class TrieSetMultimap<K, V> implements SetMultimap.Immutable<K, V> {

  private final EqualityComparator<Object> cmp;

  @SuppressWarnings("unchecked")
  private static final TrieSetMultimap EMPTY_SETMULTIMAP =
      new TrieSetMultimap(EqualityComparator.EQUALS, CompactSetMultimapNode.EMPTY_NODE, 0, 0);

  private static final boolean DEBUG = false;

  private final AbstractSetMultimapNode<K, V> rootNode;
  private final int cachedHashCode;
  private final int cachedSize;

  TrieSetMultimap(EqualityComparator<Object> cmp, AbstractSetMultimapNode<K, V> rootNode,
      int hashCode, long cachedSize) {
    this.cmp = cmp;
    this.rootNode = rootNode;
    this.cachedHashCode = hashCode;
    this.cachedSize = Math.toIntExact(cachedSize); // does not support long yet
    if (DEBUG) {
      assert checkHashCodeAndSize(hashCode, cachedSize);
    }
  }

  @SuppressWarnings("unchecked")
  public static final <K, V> SetMultimap.Immutable<K, V> of() {
    return TrieSetMultimap.EMPTY_SETMULTIMAP;
  }

  @SuppressWarnings("unchecked")
  public static final <K, V> SetMultimap.Immutable<K, V> of(K key, V... values) {
    SetMultimap.Immutable<K, V> result = TrieSetMultimap.EMPTY_SETMULTIMAP;

    for (V value : values) {
      result = result.insert(key, value);
    }

    return result;
  }

  @SuppressWarnings("unchecked")
  public static final <K, V> SetMultimap.Transient<K, V> transientOf() {
    return TrieSetMultimap.EMPTY_SETMULTIMAP.asTransient();
  }

  @SuppressWarnings("unchecked")
  public static final <K, V> SetMultimap.Transient<K, V> transientOf(K key, V... values) {
    final SetMultimap.Transient<K, V> result = TrieSetMultimap.EMPTY_SETMULTIMAP.asTransient();

    for (V value : values) {
      result.insert(key, value);
    }

    return result;
  }

  private boolean checkHashCodeAndSize(final int targetHash, final long targetSize) {
    int hash = 0;
    long size = 0;

    for (Map.Entry<K, V> entry : this) {
      final K key = entry.getKey();
      final V val = entry.getValue();

      hash ^= key.hashCode();
      hash ^= val.hashCode();
      size += 1;
    }

    return hash == targetHash && size == targetSize;
  }

  public static final int transformHashCode(final int hash) {
    return hash;
  }

  @Override
  public boolean contains(final K key) {
    return rootNode.containsKey(key, transformHashCode(key.hashCode()), 0, cmp);
  }

  @Override
  public boolean contains(final K key, final V val) {
    return rootNode.containsTuple(key, val, transformHashCode(key.hashCode()), 0, cmp);
  }

  // @Override
  // public boolean containsValue(final Object o) {
  // for (Iterator<V> iterator = valueIterator(); iterator.hasNext();) {
  // if (iterator.next().equals(o)) {
  // return true;
  // }
  // }
  // return false;
  // }

  // @Override
  // public boolean containsValueEquivalent(final Object o, final Comparator<Object> cmp) {
  // for (Iterator<V> iterator = valueIterator(); iterator.hasNext();) {
  // if (cmp.compare(iterator.next(), o) == 0) {
  // return true;
  // }
  // }
  // return false;
  // }

  @Override
  public Optional<Set.Immutable<V>> apply(K key) {
    return rootNode.findByKey(key, transformHashCode(key.hashCode()), 0, cmp);
  }

  // @Override
  // public Set.Immutable<V> get(final Object o) {
  // try {
  // @SuppressWarnings("unchecked")
  // final K key = (K) o;
  // final Optional<Set.Immutable<V>> result =
  // rootNode.findByKey(key, transformHashCode(key.hashCode()), 0);
  //
  // if (result.isPresent()) {
  // return result.get();
  // } else {
  // return null;
  // }
  // } catch (ClassCastException unused) {
  // return null;
  // }
  // }
  //
  // @Override
  // public Set.Immutable<V> getEquivalent(final Object o, final Comparator<Object> cmp) {
  // throw new UnsupportedOperationException("Not yet implemented.");
  // }

  @Override
  public SetMultimap.Immutable<K, V> put(K key, V val) {
    final int keyHash = key.hashCode();
    final SetMultimapResult<K, V> details = SetMultimapResult.unchanged();

    final CompactSetMultimapNode<K, V> newRootNode =
        rootNode.updated(null, key, val, transformHashCode(keyHash), 0, details, cmp);

    if (details.isModified()) {
      if (details.hasReplacedValue()) {
        if (details.getType() == EitherSingletonOrCollection.Type.SINGLETON) {
          final int valHashOld = details.getReplacedValue().hashCode();
          final int valHashNew = val.hashCode();

          return new TrieSetMultimap<K, V>(cmp, newRootNode,
              cachedHashCode ^ ((keyHash ^ valHashNew)) ^ ((keyHash ^ valHashOld)), cachedSize);
        } else {
          final int sumOfReplacedHashes;

          if ((1 + details.getReplacedCollection().size()) % 2 == 0) {
            sumOfReplacedHashes = details.getReplacedCollection().hashCode();
          } else {
            sumOfReplacedHashes = details.getReplacedCollection().hashCode() ^ keyHash;
          }

          final int valHashNew = val.hashCode();

          return new TrieSetMultimap<K, V>(cmp, newRootNode,
              cachedHashCode ^ ((keyHash ^ valHashNew)) ^ sumOfReplacedHashes,
              cachedSize - details.getReplacedCollection().size() + 1);
        }
      }

      final int valHash = val.hashCode();
      return new TrieSetMultimap<K, V>(cmp, newRootNode, cachedHashCode ^ ((keyHash ^ valHash)),
          cachedSize + 1);
    }

    return this;
  }

  @Override
  public SetMultimap.Immutable<K, V> put(final K key, final Set<V> values) {
    throw new UnsupportedOperationException("Not yet implemented.");
  }

  @Override
  public SetMultimap.Immutable<K, V> insert(final K key, final V val) {
    final int keyHash = key.hashCode();
    final SetMultimapResult<K, V> details = SetMultimapResult.unchanged();

    final CompactSetMultimapNode<K, V> newRootNode =
        rootNode.inserted(null, key, val, transformHashCode(keyHash), 0, details, cmp);

    if (details.isModified()) {
      final int valHash = val.hashCode();
      return new TrieSetMultimap<K, V>(cmp, newRootNode, cachedHashCode ^ ((keyHash ^ valHash)),
          cachedSize + 1);
    }

    return this;
  }

  @Override
  public SetMultimap.Immutable<K, V> insert(final K key, final Set<V> values) {
    throw new UnsupportedOperationException("Not yet implemented.");
  }

  @Override
  public SetMultimap.Immutable<K, V> remove(final K key, final V val) {
    final int keyHash = key.hashCode();
    final SetMultimapResult<K, V> details = SetMultimapResult.unchanged();

    final CompactSetMultimapNode<K, V> newRootNode =
        rootNode.removed(null, key, val, transformHashCode(keyHash), 0, details, cmp);

    if (details.isModified()) {
      assert details.hasReplacedValue();
      final int valHash = details.getReplacedValue().hashCode();
      return new TrieSetMultimap<K, V>(cmp, newRootNode, cachedHashCode ^ ((keyHash ^ valHash)),
          cachedSize - 1);
    }

    return this;
  }

  @Override
  public SetMultimap.Immutable<K, V> remove(K key) {
    final int keyHash = key.hashCode();
    final SetMultimapResult<K, V> details = SetMultimapResult.unchanged();

    final CompactSetMultimapNode<K, V> newRootNode =
        rootNode.removedAll(null, key, transformHashCode(keyHash), 0, details, cmp);

    if (details.isModified()) {
      assert details.hasReplacedValue();

      if (details.getType() == EitherSingletonOrCollection.Type.SINGLETON) {
        final int valHash = details.getReplacedValue().hashCode();
        return new TrieSetMultimap<K, V>(cmp, newRootNode, cachedHashCode ^ ((keyHash ^ valHash)),
            cachedSize - 1);
      } else {
        final int sumOfReplacedHashes;

        if ((details.getReplacedCollection().size()) % 2 == 0) {
          sumOfReplacedHashes = details.getReplacedCollection().hashCode();
        } else {
          sumOfReplacedHashes = details.getReplacedCollection().hashCode() ^ keyHash;
        }

        return new TrieSetMultimap<K, V>(cmp, newRootNode, cachedHashCode ^ sumOfReplacedHashes,
            cachedSize - details.getReplacedCollection().size());
      }
    }

    return this;
  }

  @Override
  public SetMultimap.Immutable<K, V> union(
      final SetMultimap<? extends K, ? extends V> setMultimap) {
    final SetMultimap.Transient<K, V> tmpTransient = this.asTransient();
    tmpTransient.union(setMultimap);
    return tmpTransient.asImmutable();
  }

  @Override
  public SetMultimap.Immutable<K, V> intersect(
      final SetMultimap<? extends K, ? extends V> setMultimap) {
    final SetMultimap.Transient<K, V> tmpTransient = this.asTransient();
    tmpTransient.intersect(setMultimap);
    return tmpTransient.asImmutable();
  }

  @Override
  public SetMultimap.Immutable<K, V> complement(
      final SetMultimap<? extends K, ? extends V> setMultimap) {
    final SetMultimap.Transient<K, V> tmpTransient = this.asTransient();
    tmpTransient.complement(setMultimap);
    return tmpTransient.asImmutable();
  }

  // @Override
  // public V put(final K key, final V val) {
  // throw new UnsupportedOperationException();
  // }
  //
  // @Override
  // public void putAll(final SetMultimap<? extends K, ? extends V> m) {
  // throw new UnsupportedOperationException();
  // }
  //
  // @Override
  // public void clear() {
  // throw new UnsupportedOperationException();
  // }
  //
  // @Override
  // public V remove(final Object key, final Object val) {
  // throw new UnsupportedOperationException();
  // }

  @Override
  public long size() {
    return cachedSize;
  }

  @Override
  public boolean isEmpty() {
    return cachedSize == 0;
  }

  @Override
  public Iterator<Map.Entry<K, V>> iterator() {
    return new SetMultimapTupleIterator<>(rootNode, AbstractSpecialisedImmutableMap::entryOf);
  }

  // @Override
  // public Iterator<K> keyIterator() {
  // return new SetMultimapKeyIterator<>(rootNode);
  // }
  //
  // @Override
  // public Iterator<V> valueIterator() {
  // return valueCollectionsStream().flatMap(Set::stream).iterator();
  // }
  //
  // @Override
  // public Iterator<Map.Entry<K, V>> entryIterator() {
  // return new SetMultimapTupleIterator<>(rootNode, AbstractSpecialisedImmutableMap::entryOf);
  // }

  @Override
  public Iterator<Map.Entry<K, Object>> nativeEntryIterator() {
    return new SetMultimapNativeTupleIterator<>(rootNode);
  }

  // @Override
  // public <T> Iterator<T> tupleIterator(final BiFunction<K, V, T> tupleOf) {
  // return new SetMultimapTupleIterator<>(rootNode, tupleOf);
  // }
  //
  // private Spliterator<Set.Immutable<V>> valueCollectionsSpliterator() {
  // /*
  // * TODO: specialize between mutable / immutable ({@see Spliterator.IMMUTABLE})
  // */
  // int characteristics = Spliterator.NONNULL | Spliterator.SIZED | Spliterator.SUBSIZED;
  // return Spliterators.spliterator(new SetMultimapValueIterator<>(rootNode), size(),
  // characteristics);
  // }
  //
  // private Stream<Set.Immutable<V>> valueCollectionsStream() {
  // boolean isParallel = false;
  // return StreamSupport.stream(valueCollectionsSpliterator(), isParallel);
  // }

  // @Override
  // public Set<K> keySet() {
  // Set<K> keySet = null;
  //
  // if (keySet == null) {
  // keySet = new AbstractSet<K>() {
  // @Override
  // public Iterator<K> iterator() {
  // return TrieSetMultimap.this.keyIterator();
  // }
  //
  // @Override
  // public int size() {
  // return TrieSetMultimap.this.sizeDistinct();
  // }
  //
  // @Override
  // public boolean isEmpty() {
  // return TrieSetMultimap.this.isEmpty();
  // }
  //
  // @Override
  // public void clear() {
  // TrieSetMultimap.this.clear();
  // }
  //
  // @Override
  // public boolean contains(Object k) {
  // return TrieSetMultimap.this.containsKey(k);
  // }
  // };
  // }
  //
  // return keySet;
  // }
  //
  // @Override
  // public Collection<V> values() {
  // Collection<V> values = null;
  //
  // if (values == null) {
  // values = new AbstractCollection<V>() {
  // @Override
  // public Iterator<V> iterator() {
  // return TrieSetMultimap.this.valueIterator();
  // }
  //
  // @Override
  // public int size() {
  // return TrieSetMultimap.this.size();
  // }
  //
  // @Override
  // public boolean isEmpty() {
  // return TrieSetMultimap.this.isEmpty();
  // }
  //
  // @Override
  // public void clear() {
  // TrieSetMultimap.this.clear();
  // }
  //
  // @Override
  // public boolean contains(Object v) {
  // return TrieSetMultimap.this.containsValue(v);
  // }
  // };
  // }
  //
  // return values;
  // }
  //
  // @Override
  // public Set<java.util.Map.Entry<K, V>> entrySet() {
  // Set<java.util.Map.Entry<K, V>> entrySet = null;
  //
  // if (entrySet == null) {
  // entrySet = new AbstractSet<java.util.Map.Entry<K, V>>() {
  // @Override
  // public Iterator<java.util.Map.Entry<K, V>> iterator() {
  // return new Iterator<Map.Entry<K, V>>() {
  // private final Iterator<Map.Entry<K, V>> i = entryIterator();
  //
  // @Override
  // public boolean hasNext() {
  // return i.hasNext();
  // }
  //
  // @Override
  // public Map.Entry<K, V> next() {
  // return i.next();
  // }
  //
  // @Override
  // public void remove() {
  // i.remove();
  // }
  // };
  // }
  //
  // @Override
  // public int size() {
  // return TrieSetMultimap.this.size();
  // }
  //
  // @Override
  // public boolean isEmpty() {
  // return TrieSetMultimap.this.isEmpty();
  // }
  //
  // @Override
  // public void clear() {
  // TrieSetMultimap.this.clear();
  // }
  //
  // @Override
  // public boolean contains(Object k) {
  // return TrieSetMultimap.this.containsKey(k);
  // }
  // };
  // }
  //
  // return entrySet;
  // }

  @Override
  public boolean equals(final Object other) {
    if (other == this) {
      return true;
    }
    if (other == null) {
      return false;
    }

    if (other instanceof TrieSetMultimap) {
      TrieSetMultimap<?, ?> that = (TrieSetMultimap<?, ?>) other;

      if (this.cachedSize != that.cachedSize) {
        return false;
      }

      if (this.cachedHashCode != that.cachedHashCode) {
        return false;
      }

      return rootNode.equals(that.rootNode);
    } else if (other instanceof SetMultimap) {
      try {
        @SuppressWarnings("unchecked")
        SetMultimap<K, V> that = (SetMultimap<K, V>) other;

        if (this.size() != that.size())
          return false;

        for (Map.Entry<K, V> entry : that) {
          final K key = (K) entry.getKey();
          final Optional<Set.Immutable<V>> result =
              rootNode.findByKey(key, transformHashCode(key.hashCode()), 0, cmp);

          if (!result.isPresent()) {
            return false;
          } else {
            @SuppressWarnings("unchecked")
            final Set.Immutable<V> valColl = (Set.Immutable<V>) entry.getValue();
            if (!result.get().equals(valColl)) {
              return false;
            }
          }
        }
      } catch (ClassCastException unused) {
        return false;
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
  public boolean isTransientSupported() {
    return true;
  }

  @Override
  public SetMultimap.Transient<K, V> asTransient() {
    return new TransientTrieSetMultimap<K, V>(this);
  }

  @Override
  public SetMultimap.Immutable<K, V> asImmutable() {
    return this;
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
    return new TrieSetMultimapNodeIterator<>(rootNode);
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

    public static final <T> EitherSingletonOrCollection of(Set.Immutable<T> value) {
      return new SomeCollection<>(value);
    }

    abstract boolean isType(Type type);

    abstract T getSingleton();

    abstract Set.Immutable<T> getCollection();
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
    Set.Immutable<T> getCollection() {
      throw new UnsupportedOperationException(String
          .format("Requested type %s but actually found %s.", Type.COLLECTION, Type.SINGLETON));
    }
  }

  static final class SomeCollection<T> extends EitherSingletonOrCollection<T> {
    private final Set.Immutable<T> value;

    private SomeCollection(Set.Immutable<T> value) {
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
    Set.Immutable<T> getCollection() {
      return value;
    }
  }

  static final class SetMultimapResult<K, V> {
    private V replacedValue;
    private Set.Immutable<V> replacedValueCollection;
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

    public void updated(Set.Immutable<V> replacedValueCollection) {
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

    public Set.Immutable<V> getReplacedCollection() {
      assert getType() == COLLECTION;
      return replacedValueCollection;
    }
  }

  protected static interface INode<K, V> {
  }

  protected static abstract class AbstractSetMultimapNode<K, V> implements INode<K, V> {

    static final int TUPLE_LENGTH = 2;

    abstract boolean containsKey(final K key, final int keyHash, final int shift,
        final EqualityComparator<Object> cmp);

    abstract boolean containsTuple(final K key, final V val, final int keyHash, final int shift,
        final EqualityComparator<Object> cmp);

    abstract Optional<Set.Immutable<V>> findByKey(final K key, final int keyHash, final int shift,
        final EqualityComparator<Object> cmp);

    abstract CompactSetMultimapNode<K, V> inserted(final AtomicReference<Thread> mutator,
        final K key, final V val, final int keyHash, final int shift,
        final SetMultimapResult<K, V> details, final EqualityComparator<Object> cmp);

    abstract CompactSetMultimapNode<K, V> updated(final AtomicReference<Thread> mutator,
        final K key, final V val, final int keyHash, final int shift,
        final SetMultimapResult<K, V> details, final EqualityComparator<Object> cmp);

    abstract CompactSetMultimapNode<K, V> updated(final AtomicReference<Thread> mutator,
        final K key, final Set<V> val, final int keyHash, final int shift,
        final SetMultimapResult<K, V> details, final EqualityComparator<Object> cmp);

    abstract CompactSetMultimapNode<K, V> removed(final AtomicReference<Thread> mutator,
        final K key, final V val, final int keyHash, final int shift,
        final SetMultimapResult<K, V> details, final EqualityComparator<Object> cmp);

    abstract CompactSetMultimapNode<K, V> removedAll(final AtomicReference<Thread> mutator,
        final K key, final int keyHash, final int shift, final SetMultimapResult<K, V> details,
        final EqualityComparator<Object> cmp);

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

    abstract Set.Immutable<V> getCollectionValue(final int index);

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
        final Set.Immutable<V> valColl);

    abstract CompactSetMultimapNode<K, V> copyAndSetNode(final AtomicReference<Thread> mutator,
        final long doubledBitpos, final CompactSetMultimapNode<K, V> node);

    abstract CompactSetMultimapNode<K, V> copyAndInsertSingleton(
        final AtomicReference<Thread> mutator, final long doubledBitpos, final K key, final V val);

    abstract CompactSetMultimapNode<K, V> copyAndInsertCollection(
        final AtomicReference<Thread> mutator, final long doubledBitpos, final K key,
        final Set.Immutable<V> valColl);

    abstract CompactSetMultimapNode<K, V> copyAndMigrateFromSingletonToCollection(
        final AtomicReference<Thread> mutator, final long doubledBitpos, final K key,
        final Set.Immutable<V> valColl);

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
        return AbstractHashCollisionNode.of(keyHash0, key0, setOfNew(val0), key1, setOfNew(val1));
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
        final Set.Immutable<V> valColl0, final int keyHash0, final K key1,
        final Set.Immutable<V> valColl1, final int keyHash1, final int shift) {
      assert !(key0.equals(key1));

      if (shift >= HASH_CODE_LENGTH) {
        return AbstractHashCollisionNode.of(keyHash0, key0, valColl0, key1, valColl1);
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
        final Set.Immutable<V> valColl0, final int keyHash0, final K key1, final V val1,
        final int keyHash1, final int shift) {
      assert !(key0.equals(key1));

      if (shift >= HASH_CODE_LENGTH) {
        return AbstractHashCollisionNode.of(keyHash0, key0, valColl0, key1, setOfNew(val1));
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
        final long bitmap, final K key, final Set.Immutable<V> valColl) {
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
      return index(bitmap(), PATTERN_DATA_SINGLETON, doubledBitpos);
    }

    @Deprecated
    int collIndex(final long doubledBitpos) {
      return index(bitmap(), PATTERN_DATA_COLLECTION, doubledBitpos);
    }

    @Deprecated
    int nodeIndex(final long doubledBitpos) {
      return index(bitmap(), PATTERN_NODE, doubledBitpos);
    }

    @Override
    boolean containsKey(final K key, final int keyHash, final int shift, EqualityComparator<Object> cmp) {
      long bitmap = this.bitmap();

      final int doubledMask = doubledMask(keyHash, shift);
      final int pattern = pattern(bitmap, doubledMask);

      final long doubledBitpos = doubledBitpos(doubledMask);

      switch (pattern) {
        case PATTERN_NODE: {
          int index = index(bitmap, PATTERN_NODE, doubledBitpos);
          return getNode(index).containsKey(key, keyHash, shift + BIT_PARTITION_SIZE, cmp);
        }
        case PATTERN_DATA_SINGLETON: {
          int index = index(bitmap, PATTERN_DATA_SINGLETON, doubledBitpos);
          return getSingletonKey(index).equals(key);
        }
        case PATTERN_DATA_COLLECTION: {
          int index = index(bitmap, PATTERN_DATA_COLLECTION, doubledBitpos);
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
    boolean containsTuple(final K key, final V val, final int keyHash, final int shift, EqualityComparator<Object> cmp) {
      long bitmap = this.bitmap();

      final int doubledMask = doubledMask(keyHash, shift);
      final int pattern = pattern(bitmap, doubledMask);

      final long doubledBitpos = doubledBitpos(doubledMask);

      switch (pattern) {
        case PATTERN_NODE: {
          int index = index(bitmap, PATTERN_NODE, doubledBitpos);

          final AbstractSetMultimapNode<K, V> subNode = getNode(index);
          return subNode.containsTuple(key, val, keyHash, shift + BIT_PARTITION_SIZE, cmp);
        }
        case PATTERN_DATA_SINGLETON: {
          int index = index(bitmap, PATTERN_DATA_SINGLETON, doubledBitpos);

          final K currentKey = getSingletonKey(index);
          if (currentKey.equals(key)) {

            final V currentVal = getSingletonValue(index);
            return currentVal.equals(val);
          }

          return false;
        }
        case PATTERN_DATA_COLLECTION: {
          int index = index(bitmap, PATTERN_DATA_COLLECTION, doubledBitpos);

          final K currentKey = getCollectionKey(index);
          if (currentKey.equals(key)) {

            final Set.Immutable<V> currentValColl = getCollectionValue(index);
            return currentValColl.contains(val);
          }

          return false;
        }
        default:
          return false;
      }
    }

    @Override
    Optional<Set.Immutable<V>> findByKey(final K key, final int keyHash, final int shift, EqualityComparator<Object> cmp) {
      long bitmap = this.bitmap();

      final int doubledMask = doubledMask(keyHash, shift);
      final int pattern = pattern(bitmap, doubledMask);

      final long doubledBitpos = doubledBitpos(doubledMask);

      switch (pattern) {
        case PATTERN_NODE: {
          int index = index(bitmap, PATTERN_NODE, doubledBitpos);

          final AbstractSetMultimapNode<K, V> subNode = getNode(index);
          return subNode.findByKey(key, keyHash, shift + BIT_PARTITION_SIZE, cmp);
        }
        case PATTERN_DATA_SINGLETON: {
          int index = index(bitmap, PATTERN_DATA_SINGLETON, doubledBitpos);

          final K currentKey = getSingletonKey(index);
          if (currentKey.equals(key)) {

            final V currentVal = getSingletonValue(index);
            return Optional.of(setOfNew(currentVal));
          }

          return Optional.empty();
        }
        case PATTERN_DATA_COLLECTION: {
          int index = index(bitmap, PATTERN_DATA_COLLECTION, doubledBitpos);

          final K currentKey = getCollectionKey(index);
          if (currentKey.equals(key)) {

            final Set.Immutable<V> currentValColl = getCollectionValue(index);
            return Optional.of(currentValColl);
          }

          return Optional.empty();
        }
        default:
          return Optional.empty();
      }
    }

    Optional<Set.Immutable<V>> findByKey(final K key, final int keyHash, final int shift,
        final Comparator<Object> cmp) {
      throw new UnsupportedOperationException("Not yet implemented.");
    }

    @Override
    CompactSetMultimapNode<K, V> inserted(final AtomicReference<Thread> mutator, final K key,
                                          final V val, final int keyHash, final int shift, final SetMultimapResult<K, V> details, EqualityComparator<Object> cmp) {
      long bitmap = this.bitmap();

      final int doubledMask = doubledMask(keyHash, shift);
      final int pattern = pattern(bitmap, doubledMask);

      final long doubledBitpos = doubledBitpos(doubledMask);

      switch (pattern) {
        case PATTERN_NODE: {
          int nodeIndex = index(bitmap, PATTERN_NODE, doubledBitpos);
          final CompactSetMultimapNode<K, V> subNode = getNode(nodeIndex);
          final CompactSetMultimapNode<K, V> subNodeNew =
              subNode.inserted(mutator, key, val, keyHash, shift + BIT_PARTITION_SIZE, details, cmp);

          if (details.isModified()) {
            return copyAndSetNode(mutator, doubledBitpos, subNodeNew);
          } else {
            return this;
          }
        }
        case PATTERN_DATA_SINGLETON: {
          int dataIndex = index(bitmap, PATTERN_DATA_SINGLETON, doubledBitpos);
          final K currentKey = getSingletonKey(dataIndex);

          if (currentKey.equals(key)) {
            final V currentVal = getSingletonValue(dataIndex);

            if (currentVal.equals(val)) {
              return this;
            } else {
              // migrate from singleton to collection
              final Set.Immutable<V> valColl = setOfNew(currentVal, val);

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
          int collIndex = index(bitmap, PATTERN_DATA_COLLECTION, doubledBitpos);
          final K currentCollKey = getCollectionKey(collIndex);

          if (currentCollKey.equals(key)) {
            final Set.Immutable<V> currentCollVal = getCollectionValue(collIndex);

            if (currentCollVal.contains(val)) {
              return this;
            } else {
              // add new mapping
              final Set.Immutable<V> newCollVal = currentCollVal.insert(val);

              details.modified();
              return copyAndSetCollectionValue(mutator, doubledBitpos, newCollVal);
            }
          } else {
            // prefix-collision (case: collection x singleton)
            final Set.Immutable<V> currentValNode = getCollectionValue(collIndex);
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
                                         final V val, final int keyHash, final int shift, final SetMultimapResult<K, V> details, EqualityComparator<Object> cmp) {
      long bitmap = this.bitmap();

      final int doubledMask = doubledMask(keyHash, shift);
      final int pattern = pattern(bitmap, doubledMask);

      final long doubledBitpos = doubledBitpos(doubledMask);

      switch (pattern) {
        case PATTERN_NODE: {
          int nodeIndex = index(bitmap, PATTERN_NODE, doubledBitpos);
          final CompactSetMultimapNode<K, V> subNode = getNode(nodeIndex);
          final CompactSetMultimapNode<K, V> subNodeNew =
              subNode.updated(mutator, key, val, keyHash, shift + BIT_PARTITION_SIZE, details, cmp);

          if (details.isModified()) {
            return copyAndSetNode(mutator, doubledBitpos, subNodeNew);
          } else {
            return this;
          }
        }
        case PATTERN_DATA_SINGLETON: {
          int dataIndex = index(bitmap, PATTERN_DATA_SINGLETON, doubledBitpos);
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
          int collIndex = index(bitmap, PATTERN_DATA_COLLECTION, doubledBitpos);
          final K currentCollKey = getCollectionKey(collIndex);

          if (currentCollKey.equals(key)) {
            final Set.Immutable<V> currentCollVal = getCollectionValue(collIndex);

            // migrate from collection to singleton
            details.updated(currentCollVal);
            return copyAndMigrateFromCollectionToSingleton(mutator, doubledBitpos, currentCollKey,
                val);
          } else {
            // prefix-collision (case: collection x singleton)
            final Set.Immutable<V> currentValNode = getCollectionValue(collIndex);
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
                                         final Set<V> valColl, final int keyHash, final int shift,
                                         final SetMultimapResult<K, V> details, EqualityComparator<Object> cmp) {
      long bitmap = this.bitmap();

      final int doubledMask = doubledMask(keyHash, shift);
      final int pattern = pattern(bitmap, doubledMask);

      final long doubledBitpos = doubledBitpos(doubledMask);

      switch (pattern) {
        case PATTERN_NODE: {
          int nodeIndex = index(bitmap, PATTERN_NODE, doubledBitpos);
          final CompactSetMultimapNode<K, V> subNode = getNode(nodeIndex);
          final CompactSetMultimapNode<K, V> subNodeNew =
              subNode.updated(mutator, key, valColl, keyHash, shift + BIT_PARTITION_SIZE, details, cmp);

          if (details.isModified()) {
            return copyAndSetNode(mutator, doubledBitpos, subNodeNew);
          } else {
            return this;
          }
        }
        case PATTERN_DATA_SINGLETON: {
          int dataIndex = index(bitmap, PATTERN_DATA_SINGLETON, doubledBitpos);
          final K currentKey = getSingletonKey(dataIndex);

          if (currentKey.equals(key)) {
            final V currentVal = getSingletonValue(dataIndex);

            // migrate from singleton to collection
            details.updated(currentVal);
            return copyAndMigrateFromSingletonToCollection(mutator, doubledBitpos, currentKey,
                valColl.asImmutable());
            //
            //
            // // update singleton value
            // details.updated(currentVal);
            // return copyAndSetSingletonValue(mutator, doubledBitpos, valColl);
          } else {
            // prefix-collision (case: collection x singleton)
            final V currentVal = getSingletonValue(dataIndex);

            final CompactSetMultimapNode<K, V> subNodeNew = mergeCollectionAndSingletonPairs(key,
                valColl.asImmutable(), keyHash, currentKey, currentVal,
                transformHashCode(currentKey.hashCode()), shift + BIT_PARTITION_SIZE);

            details.modified();
            return copyAndMigrateFromSingletonToNode(mutator, doubledBitpos, subNodeNew);

            // // prefix-collision (case: singleton x singleton)
            // final V currentVal = getSingletonValue(dataIndex);
            //
            // final CompactSetMultimapNode<K, V> subNodeNew = mergeTwoSingletonPairs(currentKey,
            // currentVal, transformHashCode(currentKey.hashCode()), key, valColl, keyHash,
            // shift + BIT_PARTITION_SIZE);
            //
            // details.modified();
            // return copyAndMigrateFromSingletonToNode(mutator, doubledBitpos, subNodeNew);
          }
        }
        case PATTERN_DATA_COLLECTION: {
          int collIndex = index(bitmap, PATTERN_DATA_COLLECTION, doubledBitpos);
          final K currentCollKey = getCollectionKey(collIndex);

          if (currentCollKey.equals(key)) {
            final Set.Immutable<V> currentCollVal = getCollectionValue(collIndex);

            // update collection value
            details.updated(currentCollVal);
            return copyAndSetCollectionValue(mutator, doubledBitpos, valColl.asImmutable());

            // // migrate from collection to singleton
            // details.updated(currentCollVal);
            // return copyAndMigrateFromCollectionToSingleton(mutator, doubledBitpos,
            // currentCollKey,
            // valColl);
          } else {
            // prefix-collision (case: collection x collection)
            final Set.Immutable<V> currentValNode = getCollectionValue(collIndex);
            final CompactSetMultimapNode<K, V> subNodeNew = mergeTwoCollectionPairs(currentCollKey,
                currentValNode.asImmutable(), transformHashCode(currentCollKey.hashCode()), key,
                valColl.asImmutable(), keyHash, shift + BIT_PARTITION_SIZE);

            details.modified();
            return copyAndMigrateFromCollectionToNode(mutator, doubledBitpos, subNodeNew);

            // // prefix-collision (case: collection x singleton)
            // final Set.Immutable<V> currentValNode = getCollectionValue(collIndex);
            // final CompactSetMultimapNode<K, V> subNodeNew = mergeCollectionAndSingletonPairs(
            // currentCollKey, currentValNode, transformHashCode(currentCollKey.hashCode()), key,
            // valColl, keyHash, shift + BIT_PARTITION_SIZE);
            //
            // details.modified();
            // return copyAndMigrateFromCollectionToNode(mutator, doubledBitpos, subNodeNew);
          }
        }
        default: {
          details.modified();
          return copyAndInsertCollection(mutator, doubledBitpos, key, valColl.asImmutable());
        }
      }
    }

    @Override
    CompactSetMultimapNode<K, V> removed(final AtomicReference<Thread> mutator, final K key,
                                         final V val, final int keyHash, final int shift, final SetMultimapResult<K, V> details, EqualityComparator<Object> cmp) {
      long bitmap = this.bitmap();

      final int doubledMask = doubledMask(keyHash, shift);
      final int pattern = pattern(bitmap, doubledMask);

      final long doubledBitpos = doubledBitpos(doubledMask);

      switch (pattern) {
        case PATTERN_NODE: {
          int nodeIndex = index(bitmap, PATTERN_NODE, doubledBitpos);

          final CompactSetMultimapNode<K, V> subNode = getNode(nodeIndex);
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
          int dataIndex = index(bitmap, PATTERN_DATA_SINGLETON, doubledBitpos);

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
          int collIndex = index(bitmap, PATTERN_DATA_COLLECTION, doubledBitpos);

          final K currentKey = getCollectionKey(collIndex);
          if (currentKey.equals(key)) {

            final Set.Immutable<V> currentValColl = getCollectionValue(collIndex);
            if (currentValColl.contains(val)) {

              // remove mapping
              details.updated(val);

              final Set.Immutable<V> newValColl = currentValColl.remove(val);

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
      // final State[] states = {State.EMPTY, State.NODE, State.PAYLOAD, State.PAYLOAD_RARE};
      // return states[pattern];

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
                                            final int keyHash, final int shift, final SetMultimapResult<K, V> details, EqualityComparator<Object> cmp) {
      long bitmap = this.bitmap();

      final int doubledMask = doubledMask(keyHash, shift);
      final int pattern = pattern(bitmap, doubledMask);

      final long doubledBitpos = doubledBitpos(doubledMask);

      switch (pattern) {
        case PATTERN_NODE: {
          int nodeIndex = index(bitmap, PATTERN_NODE, doubledBitpos);

          final CompactSetMultimapNode<K, V> subNode = getNode(nodeIndex);
          final CompactSetMultimapNode<K, V> subNodeNew =
              subNode.removedAll(mutator, key, keyHash, shift + BIT_PARTITION_SIZE, details, cmp);

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
                // // inline value (move to front)
                // final State subNodeState = subNodeNew.stateOfSingleton();
                //
                //// switch (subNodeState) {
                //// case EMPTY:
                //// case NODE:
                //// case PAYLOAD:
                //// return copyAndMigrateFromNodeToSingleton(mutator, doubledBitpos, subNodeNew);
                //// case PAYLOAD_RARE:
                //// return copyAndMigrateFromNodeToCollection(mutator, doubledBitpos, subNodeNew);
                //// }
                //
                // if (subNodeState == State.PAYLOAD) {
                // return copyAndMigrateFromNodeToSingleton(mutator, doubledBitpos, subNodeNew);
                // } else {
                // return copyAndMigrateFromNodeToCollection(mutator, doubledBitpos, subNodeNew);
                // }

                // inline value (move to front)
                EitherSingletonOrCollection.Type type = subNodeNew.typeOfSingleton();

                if (type == EitherSingletonOrCollection.Type.SINGLETON) {
                  return copyAndMigrateFromNodeToSingleton(mutator, doubledBitpos, subNodeNew);
                } else {
                  return copyAndMigrateFromNodeToCollection(mutator, doubledBitpos, subNodeNew);
                }

                // // inline value (move to front)
                // final int subNodePattern = subNodeNew.patternOfSingleton();
                //
                // if (subNodePattern == PATTERN_DATA_SINGLETON) {
                // return copyAndMigrateFromNodeToSingleton(mutator, doubledBitpos, subNodeNew);
                // } else {
                // return copyAndMigrateFromNodeToCollection(mutator, doubledBitpos, subNodeNew);
                // }

                // switch (subNodePattern) {
                // case PATTERN_DATA_SINGLETON:
                // return copyAndMigrateFromNodeToSingleton(mutator, doubledBitpos, subNodeNew);
                // case PATTERN_DATA_COLLECTION:
                // return copyAndMigrateFromNodeToCollection(mutator, doubledBitpos, subNodeNew);
                // default:
                // return null;
                // }
              }
            }
            default: {
              // modify current node (set replacement node)
              return copyAndSetNode(mutator, doubledBitpos, subNodeNew);
            }
          }
        }
        case PATTERN_DATA_SINGLETON: {
          int dataIndex = index(bitmap, PATTERN_DATA_SINGLETON, doubledBitpos);

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
          int collIndex = index(bitmap, PATTERN_DATA_COLLECTION, doubledBitpos);

          final K currentKey = getCollectionKey(collIndex);
          if (currentKey.equals(key)) {

            final Set.Immutable<V> currentValColl = getCollectionValue(collIndex);

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

          assert ((nodes[offset + 0] instanceof Set.Immutable) == false);
          assert ((nodes[offset + 1] instanceof Set.Immutable) == false);

          assert ((nodes[offset + 0] instanceof CompactSetMultimapNode) == false);
          assert ((nodes[offset + 1] instanceof CompactSetMultimapNode) == false);
        }

        for (int i = 0; i < arities[PATTERN_DATA_COLLECTION]; i++) {
          int offset = (i + arities[PATTERN_DATA_SINGLETON]) * TUPLE_LENGTH;

          assert ((nodes[offset + 0] instanceof Set.Immutable) == false);
          assert ((nodes[offset + 1] instanceof Set.Immutable) == true);

          assert ((nodes[offset + 0] instanceof CompactSetMultimapNode) == false);
          assert ((nodes[offset + 1] instanceof CompactSetMultimapNode) == false);
        }

        for (int i = 0; i < arities[PATTERN_NODE]; i++) {
          int offset =
              (arities[PATTERN_DATA_SINGLETON] + arities[PATTERN_DATA_COLLECTION]) * TUPLE_LENGTH;

          assert ((nodes[offset + i] instanceof Set.Immutable) == false);

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
    Set.Immutable<V> getCollectionValue(final int index) {
      // TODO: improve on offset calculation (caching it, etc)
      int offset = TUPLE_LENGTH * (arity(bitmap(), PATTERN_DATA_SINGLETON) + index) + 1;
      return (Set.Immutable<V>) nodes[offset];
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
      // int[] arities = arities(bitmap());
      // return arities[PATTERN_NODE];
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

      // switch (slotArity()) {
      // case 0:
      // return SIZE_EMPTY;
      // case 1:
      // return SIZE_MORE_THAN_ONE; // works for maps only: must be subnode; patternOfSingleton();
      // case 2:
      // return arity(bitmap(), PATTERN_NODE) == 0 ? SIZE_ONE : SIZE_MORE_THAN_ONE;
      // default:
      // return SIZE_MORE_THAN_ONE;
      // }

      // if (this.nodeArity() == 0) {
      // switch (this.payloadArity()) {
      // case 0:
      // return SIZE_EMPTY;
      // case 1:
      // return SIZE_ONE;
      // default:
      // return SIZE_MORE_THAN_ONE;
      // }
      // } else {
      // return SIZE_MORE_THAN_ONE;
      // }

      // int[] arities = arities(bitmap());
      //
      // int nodeArity = arities[PATTERN_NODE];
      // int emptyArity = arities[PATTERN_EMPTY];

      final long bitmap = this.bitmap();

      int nodeArity = arity(bitmap, PATTERN_NODE);
      int emptyArity = arity(bitmap, PATTERN_EMPTY);

      // int aritySingleton = arity(bitmap, PATTERN_DATA_SINGLETON);
      // int arityCollection = arity(bitmap, PATTERN_DATA_COLLECTION);

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

      // if (this.nodeArity() == 0) {
      // switch (arity(bitmap(), PATTERN_DATA_SINGLETON)
      // + arity(bitmap(), PATTERN_DATA_COLLECTION)) {
      // case 0:
      // return SIZE_EMPTY;
      // case 1:
      // return SIZE_ONE;
      // default:
      // return SIZE_MORE_THAN_ONE;
      // }
      // } else {
      // return SIZE_MORE_THAN_ONE;
      // }
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
        final long doubledBitpos, final Set.Immutable<V> valColl) {

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

      long updatedBitmap = setBitPattern(bitmap(), doubledBitpos, PATTERN_DATA_SINGLETON);
      return nodeOf(mutator, updatedBitmap, dst);
    }

    @Override
    CompactSetMultimapNode<K, V> copyAndInsertCollection(final AtomicReference<Thread> mutator,
        final long doubledBitpos, final K key, final Set.Immutable<V> valColl) {
      final int idx =
          TUPLE_LENGTH * (arity(bitmap(), PATTERN_DATA_SINGLETON) + collIndex(doubledBitpos));

      final Object[] src = this.nodes;
      final Object[] dst = new Object[src.length + 2];

      // copy 'src' and insert 2 element(s) at position 'idx'
      System.arraycopy(src, 0, dst, 0, idx);
      dst[idx + 0] = key;
      dst[idx + 1] = valColl;
      System.arraycopy(src, idx, dst, idx + 2, src.length - idx);

      long updatedBitmap = setBitPattern(bitmap(), doubledBitpos, PATTERN_DATA_COLLECTION);
      return nodeOf(mutator, updatedBitmap, dst);
    }

    @Override
    CompactSetMultimapNode<K, V> copyAndMigrateFromSingletonToCollection(
        AtomicReference<Thread> mutator, long doubledBitpos, K key, Set.Immutable<V> valColl) {

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

      long updatedBitmap = setBitPattern(bitmap(), doubledBitpos, PATTERN_DATA_SINGLETON);
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

      long updatedBitmap = setBitPattern(bitmap(), doubledBitpos, PATTERN_EMPTY);
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

      long updatedBitmap = setBitPattern(bitmap(), doubledBitpos, PATTERN_EMPTY);
      return nodeOf(mutator, updatedBitmap, dst);
    }

    @Override
    CompactSetMultimapNode<K, V> copyAndMigrateFromSingletonToNode(
        final AtomicReference<Thread> mutator, final long doubledBitpos,
        final CompactSetMultimapNode<K, V> node) {

      final int idxOld = TUPLE_LENGTH * dataIndex(doubledBitpos);
      final int idxNew = this.nodes.length - TUPLE_LENGTH - nodeIndex(doubledBitpos);

      final Object[] dst = copyAndMigrateFromXxxToNode(idxOld, idxNew, node);

      long updatedBitmap = setBitPattern(bitmap(), doubledBitpos, PATTERN_NODE);
      return nodeOf(mutator, updatedBitmap, dst);
    }

    @Override
    CompactSetMultimapNode<K, V> copyAndMigrateFromCollectionToNode(AtomicReference<Thread> mutator,
        long doubledBitpos, CompactSetMultimapNode<K, V> node) {

      final int idxOld =
          TUPLE_LENGTH * (arity(bitmap(), PATTERN_DATA_SINGLETON) + collIndex(doubledBitpos));
      final int idxNew = this.nodes.length - TUPLE_LENGTH - nodeIndex(doubledBitpos);

      final Object[] dst = copyAndMigrateFromXxxToNode(idxOld, idxNew, node);

      long updatedBitmap = setBitPattern(bitmap(), doubledBitpos, PATTERN_NODE);
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

      long updatedBitmap = setBitPattern(bitmap(), doubledBitpos, PATTERN_DATA_COLLECTION);
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

      long updatedBitmap = setBitPattern(bitmap(), doubledBitpos, PATTERN_DATA_COLLECTION);
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

  private static abstract class AbstractHashCollisionNode<K, V>
      extends CompactSetMultimapNode<K, V> {

    static final <K, V, VS extends Set.Immutable<V>> AbstractHashCollisionNode<K, V> of(
        final int hash, final K key0, final VS valColl0, final K key1, final VS valColl1) {
      return new HashCollisionNode<>(hash, key0, valColl0, key1, valColl1);
    }

    private static final RuntimeException UOE_BOILERPLATE = new UnsupportedOperationException(
        "TODO: CompactSetMultimapNode -> AbstractSetMultimapNode");

    private static final Supplier<RuntimeException> UOE_FACTORY =
        () -> new UnsupportedOperationException(
            "TODO: CompactSetMultimapNode -> AbstractSetMultimapNode");

    @Override
    public long bitmap() {
      throw UOE_FACTORY.get();
    }

    @Override
    CompactSetMultimapNode<K, V> copyAndSetSingletonValue(AtomicReference<Thread> mutator,
        long doubledBitpos, V val) {
      throw UOE_FACTORY.get();
    }

    @Override
    CompactSetMultimapNode<K, V> copyAndSetCollectionValue(AtomicReference<Thread> mutator,
        long doubledBitpos, Set.Immutable<V> valColl) {
      throw UOE_FACTORY.get();
    }

    @Override
    CompactSetMultimapNode<K, V> copyAndSetNode(AtomicReference<Thread> mutator, long doubledBitpos,
        CompactSetMultimapNode<K, V> node) {
      throw UOE_FACTORY.get();
    }

    @Override
    CompactSetMultimapNode<K, V> copyAndInsertSingleton(AtomicReference<Thread> mutator,
        long doubledBitpos, K key, V val) {
      throw UOE_FACTORY.get();
    }

    @Override
    CompactSetMultimapNode<K, V> copyAndMigrateFromSingletonToCollection(
        AtomicReference<Thread> mutator, long doubledBitpos, K key, Set.Immutable<V> valColl) {
      throw UOE_FACTORY.get();
    }

    @Override
    CompactSetMultimapNode<K, V> copyAndRemoveSingleton(AtomicReference<Thread> mutator,
        long doubledBitpos) {
      throw UOE_FACTORY.get();
    }

    @Override
    CompactSetMultimapNode<K, V> copyAndRemoveCollection(AtomicReference<Thread> mutator,
        long doubledBitpos) {
      throw UOE_FACTORY.get();
    }

    @Override
    CompactSetMultimapNode<K, V> copyAndMigrateFromSingletonToNode(AtomicReference<Thread> mutator,
        long doubledBitpos, CompactSetMultimapNode<K, V> node) {
      throw UOE_FACTORY.get();
    }

    @Override
    CompactSetMultimapNode<K, V> copyAndMigrateFromNodeToSingleton(AtomicReference<Thread> mutator,
        long doubledBitpos, CompactSetMultimapNode<K, V> node) {
      throw UOE_FACTORY.get();
    }

    @Override
    CompactSetMultimapNode<K, V> copyAndMigrateFromCollectionToNode(AtomicReference<Thread> mutator,
        long doubledBitpos, CompactSetMultimapNode<K, V> node) {
      throw UOE_FACTORY.get();
    }

    @Override
    CompactSetMultimapNode<K, V> copyAndMigrateFromNodeToCollection(AtomicReference<Thread> mutator,
        long doubledBitpos, CompactSetMultimapNode<K, V> node) {
      throw UOE_FACTORY.get();
    }

    @Override
    CompactSetMultimapNode<K, V> copyAndUpdateBitmaps(AtomicReference<Thread> mutator,
        long bitmap) {
      throw UOE_FACTORY.get();
    }

    @Override
    CompactSetMultimapNode<K, V> copyAndInsertCollection(AtomicReference<Thread> mutator,
        long doubledBitpos, K key, Set.Immutable<V> valColl) {
      throw UOE_FACTORY.get();
    }

    @Override
    CompactSetMultimapNode<K, V> copyAndRemoveSingleton(AtomicReference<Thread> mutator,
        long doubledBitpos, long updatedBitmap) {
      throw UOE_FACTORY.get();
    }

    @Override
    CompactSetMultimapNode<K, V> copyAndMigrateFromCollectionToSingleton(
        AtomicReference<Thread> mutator, long doubledBitpos, K key, V val) {
      throw UOE_FACTORY.get();
    }

    @Override
    int emptyArity() {
      throw UOE_FACTORY.get();
    }

    @Override
    Type typeOfSingleton() {
      throw UOE_FACTORY.get();
    }

    @Override
    int patternOfSingleton() {
      throw UOE_FACTORY.get();
    }
  }

  private static final class HashCollisionNode<K, V> extends AbstractHashCollisionNode<K, V> {

    private final int hash;
    private final List<Map.Entry<K, Set.Immutable<V>>> collisionContent;

    HashCollisionNode(final int hash, final K key0, final Set.Immutable<V> valColl0, final K key1,
        final Set.Immutable<V> valColl1) {
      this(hash, Arrays.asList(entryOf(key0, valColl0), entryOf(key1, valColl1)));
    }

    HashCollisionNode(final int hash, final List<Map.Entry<K, Set.Immutable<V>>> collisionContent) {
      this.hash = hash;
      this.collisionContent = collisionContent;
    }

    private static final RuntimeException UOE = new UnsupportedOperationException();

    private static final Supplier<RuntimeException> UOE_NOT_YET_IMPLEMENTED_FACTORY =
        () -> new UnsupportedOperationException("Not yet implemented @ HashCollisionNode.");

    @Override
    byte sizePredicate() {
      return SIZE_MORE_THAN_ONE;
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
    CompactSetMultimapNode<K, V> getNode(int index) {
      throw UOE;
    }

    @Override
    boolean hasPayload(EitherSingletonOrCollection.Type type) {
      switch (type) {
        case SINGLETON:
          return collisionContent.stream()
              .filter(kImmutableSetEntry -> kImmutableSetEntry.getValue().size() == 1).findAny()
              .isPresent();
        case COLLECTION:
          return collisionContent.stream()
              .filter(kImmutableSetEntry -> kImmutableSetEntry.getValue().size() >= 2).findAny()
              .isPresent();
      }
      throw new RuntimeException();
    }

    @Override
    int payloadArity(EitherSingletonOrCollection.Type type) {
      switch (type) {
        case SINGLETON:
          return (int) collisionContent.stream()
              .filter(kImmutableSetEntry -> kImmutableSetEntry.getValue().size() == 1).count();
        case COLLECTION:
          return (int) collisionContent.stream()
              .filter(kImmutableSetEntry -> kImmutableSetEntry.getValue().size() >= 2).count();
      }
      throw new RuntimeException();
    }

    @Override
    K getSingletonKey(int index) {
      return collisionContent.stream()
          .filter(kImmutableSetEntry -> kImmutableSetEntry.getValue().size() == 1).skip(index)
          .findAny().get().getKey();
    }

    @Override
    V getSingletonValue(int index) {
      return collisionContent.stream()
          .filter(kImmutableSetEntry -> kImmutableSetEntry.getValue().size() == 1).skip(index)
          .findAny().get().getValue().stream().findAny().get();
    }

    @Override
    K getCollectionKey(int index) {
      return collisionContent.stream()
          .filter(kImmutableSetEntry -> kImmutableSetEntry.getValue().size() >= 2).skip(index)
          .findAny().get().getKey();
    }

    @Override
    Set.Immutable<V> getCollectionValue(int index) {
      return collisionContent.stream()
          .filter(kImmutableSetEntry -> kImmutableSetEntry.getValue().size() >= 2).skip(index)
          .findAny().get().getValue();
    }

    @Override
    boolean hasSlots() {
      return true;
    }

    @Override
    int slotArity() {
      return collisionContent.size() * 2;
    }

    @Override
    Object getSlot(int index) {
      if (index % 2 == 0) {
        return collisionContent.get(index / 2).getKey();
      } else {
        return collisionContent.get(index / 2).getValue();
      }
    }

    @Override
    boolean containsKey(K key, int keyHash, int shift, EqualityComparator<Object> cmp) {
      return collisionContent.stream().filter(entry -> cmp.equals(key, entry.getKey())).findAny()
          .isPresent();
    }

    @Override
    boolean containsTuple(K key, V val, int keyHash, int shift, EqualityComparator<Object> cmp) {
      return collisionContent.stream()
          .filter(entry -> cmp.equals(key, entry.getKey()) && entry.getValue().contains(val))
          .findAny().isPresent();
    }

    @Override
    Optional<Set.Immutable<V>> findByKey(K key, int keyHash, int shift,
        EqualityComparator<Object> cmp) {
      throw UOE_NOT_YET_IMPLEMENTED_FACTORY.get();
    }

    @Override
    CompactSetMultimapNode<K, V> inserted(AtomicReference<Thread> mutator, K key, V val,
        int keyHash, int shift, SetMultimapResult<K, V> details, EqualityComparator<Object> cmp) {
      Optional<Map.Entry<K, Set.Immutable<V>>> optionalTuple =
          collisionContent.stream().filter(entry -> cmp.equals(key, entry.getKey())).findAny();

      if (optionalTuple.isPresent()) {
        // contains key

        Set.Immutable<V> values = optionalTuple.get().getValue();

        if (values.contains(val)) {
          // contains key and value
          details.unchanged();
          return this;

        } else {
          // contains key but not value

          Function<Map.Entry<K, Set.Immutable<V>>, Map.Entry<K, Set.Immutable<V>>> substitutionMapper =
              (kImmutableSetEntry) -> {
                if (kImmutableSetEntry == optionalTuple.get()) {
                  Set.Immutable<V> updatedValues = values.insert(val);
                  return entryOf(key, updatedValues);
                } else {
                  return kImmutableSetEntry;
                }
              };

          List<Map.Entry<K, Set.Immutable<V>>> updatedCollisionContent =
              collisionContent.stream().map(substitutionMapper).collect(Collectors.toList());

          // TODO not all API uses EqualityComparator
          // TODO does not check that remainder is unmodified
          assert updatedCollisionContent.size() == collisionContent.size();
          assert updatedCollisionContent.contains(optionalTuple.get()) == false;
          // assert updatedCollisionContent.contains(entryOf(key, values.__insertEquivalent(val,
          // cmp.toComparator())));
          assert updatedCollisionContent.stream()
              .filter(entry -> cmp.equals(key, entry.getKey()) && entry.getValue().contains(val))
              .findAny().isPresent();

          details.modified();
          return new HashCollisionNode<K, V>(hash, updatedCollisionContent);
        }
      } else {
        // does not contain key

        Stream.Builder<Map.Entry<K, Set.Immutable<V>>> builder =
            Stream.<Map.Entry<K, Set.Immutable<V>>>builder().add(entryOf(key, setOfNew(val)));

        collisionContent.forEach(builder::accept);

        List<Map.Entry<K, Set.Immutable<V>>> updatedCollisionContent =
            builder.build().collect(Collectors.toList());

        // TODO not all API uses EqualityComparator
        assert updatedCollisionContent.size() == collisionContent.size() + 1;
        assert updatedCollisionContent.containsAll(collisionContent);
        // assert updatedCollisionContent.contains(entryOf(key, setOf(val)));
        assert updatedCollisionContent.stream().filter(entry -> cmp.equals(key, entry.getKey())
            && Objects.equals(setOf(val), entry.getValue())).findAny().isPresent();

        details.modified();
        return new HashCollisionNode<K, V>(hash, updatedCollisionContent);
      }
    }

    @Override
    CompactSetMultimapNode<K, V> updated(AtomicReference<Thread> mutator, K key, V val, int keyHash,
        int shift, SetMultimapResult<K, V> details, EqualityComparator<Object> cmp) {
      Optional<Map.Entry<K, Set.Immutable<V>>> optionalTuple =
          collisionContent.stream().filter(entry -> cmp.equals(key, entry.getKey())).findAny();

      if (optionalTuple.isPresent()) {
        // contains key -> replace val anyways

        Set.Immutable<V> values = optionalTuple.get().getValue();

        Function<Map.Entry<K, Set.Immutable<V>>, Map.Entry<K, Set.Immutable<V>>> substitutionMapper =
            (kImmutableSetEntry) -> {
              if (kImmutableSetEntry == optionalTuple.get()) {
                Set.Immutable<V> updatedValues = values.insert(val);
                return entryOf(key, updatedValues);
              } else {
                return kImmutableSetEntry;
              }
            };

        List<Map.Entry<K, Set.Immutable<V>>> updatedCollisionContent =
            collisionContent.stream().map(substitutionMapper).collect(Collectors.toList());

        if (values.size() == 1) {
          details.updated(values.stream().findAny().get()); // unbox singleton
        } else {
          details.updated(values);
        }

        return new HashCollisionNode<K, V>(hash, updatedCollisionContent);
      } else {
        // does not contain key

        Stream.Builder<Map.Entry<K, Set.Immutable<V>>> builder =
            Stream.<Map.Entry<K, Set.Immutable<V>>>builder().add(entryOf(key, setOfNew(val)));

        collisionContent.forEach(builder::accept);

        List<Map.Entry<K, Set.Immutable<V>>> updatedCollisionContent =
            builder.build().collect(Collectors.toList());

        details.modified();
        return new HashCollisionNode<K, V>(hash, updatedCollisionContent);
      }
    }

    @Override
    CompactSetMultimapNode<K, V> removed(AtomicReference<Thread> mutator, K key, V val, int keyHash,
        int shift, SetMultimapResult<K, V> details, EqualityComparator<Object> cmp) {
      Optional<Map.Entry<K, Set.Immutable<V>>> optionalTuple =
          collisionContent.stream().filter(entry -> cmp.equals(key, entry.getKey())).findAny();

      if (optionalTuple.isPresent()) {
        // contains key

        Set.Immutable<V> values = optionalTuple.get().getValue();

        if (values.contains(val)) {
          // contains key and value -> remove mapping

          final List<Map.Entry<K, Set.Immutable<V>>> updatedCollisionContent;

          if (values.size() == 1) {
            updatedCollisionContent = collisionContent.stream()
                .filter(kImmutableSetEntry -> kImmutableSetEntry != optionalTuple.get())
                .collect(Collectors.toList());
          } else {
            Function<Map.Entry<K, Set.Immutable<V>>, Map.Entry<K, Set.Immutable<V>>> substitutionMapper =
                (kImmutableSetEntry) -> {
                  if (kImmutableSetEntry == optionalTuple.get()) {
                    Set.Immutable<V> updatedValues = values.remove(val);
                    return entryOf(key, updatedValues);
                  } else {
                    return kImmutableSetEntry;
                  }
                };

            updatedCollisionContent =
                collisionContent.stream().map(substitutionMapper).collect(Collectors.toList());
          }

          details.updated(val);
          return new HashCollisionNode<K, V>(hash, updatedCollisionContent);
        }
      }

      details.unchanged();
      return this;
    }

    @Override
    State stateOfSingleton() {
      return null;
    }
  }

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

          // int emptyArity = nextNode.emptyArity();
          // if (emptyArity + nodeArity < 32) {
          if (nextNode.hasPayload(SINGLETON) || nextNode.hasPayload(COLLECTION)) {
            // if (payloadAritySingleton != 0 || payloadArityCollection != 0) {
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
      implements Iterator<Set.Immutable<V>> {

    SetMultimapValueIterator(AbstractSetMultimapNode<K, V> rootNode) {
      super(rootNode);
    }

    @Override
    public Set.Immutable<V> next() {
      if (!hasNext()) {
        throw new NoSuchElementException();
      } else {
        // TODO: check case distinction
        if (currentValueSingletonCursor < currentValueSingletonLength) {
          return setOfNew(currentValueNode.getSingletonValue(currentValueSingletonCursor++));
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
  private static class TrieSetMultimapNodeIterator<K, V>
      implements Iterator<AbstractSetMultimapNode<K, V>> {

    final Deque<Iterator<? extends AbstractSetMultimapNode<K, V>>> nodeIteratorStack;

    TrieSetMultimapNodeIterator(AbstractSetMultimapNode<K, V> rootNode) {
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

  static final class TransientTrieSetMultimap<K, V> implements SetMultimap.Transient<K, V> {

    private final EqualityComparator<Object> cmp;

    final private AtomicReference<Thread> mutator;
    private AbstractSetMultimapNode<K, V> rootNode;
    private int cachedHashCode;
    private long cachedSize;

    TransientTrieSetMultimap(TrieSetMultimap<K, V> TrieSetMultimap) {
      this.cmp = TrieSetMultimap.cmp;
      this.mutator = new AtomicReference<Thread>(Thread.currentThread());
      this.rootNode = TrieSetMultimap.rootNode;
      this.cachedHashCode = TrieSetMultimap.cachedHashCode;
      this.cachedSize = TrieSetMultimap.cachedSize;
      if (DEBUG) {
        assert checkHashCodeAndSize(cachedHashCode, cachedSize);
      }
    }

    private boolean checkHashCodeAndSize(final int targetHash, final long targetSize) {
      int hash = 0;
      long size = 0;

      for (Map.Entry<K, V> entry : this) {
        final K key = entry.getKey();
        final V val = entry.getValue();

        hash ^= key.hashCode();
        hash ^= val.hashCode();
        size += 1;
      }

      return hash == targetHash && size == targetSize;
    }

    @Override
    public boolean contains(final K key) {
      return rootNode.containsKey(key, transformHashCode(key.hashCode()), 0, cmp);
    }

    @Override
    public boolean contains(final K key, final V val) {
      return rootNode.containsTuple(key, val, transformHashCode(key.hashCode()), 0, cmp);
    }

    // @Override
    // public boolean containsEntryEquivalent(final Object o0, final Object o1,
    // final Comparator<Object> cmp) {
    // throw new UnsupportedOperationException("Not yet implemented.");
    // }

    @Override
    public Optional<Set.Immutable<V>> apply(K key) {
      return rootNode.findByKey(key, transformHashCode(key.hashCode()), 0, cmp);
    }

    // @Override
    // public Set.Immutable<V> get(final Object o) {
    // try {
    // @SuppressWarnings("unchecked")
    // final K key = (K) o;
    // final Optional<Set.Immutable<V>> result =
    // rootNode.findByKey(key, transformHashCode(key.hashCode()), 0);
    //
    // if (result.isPresent()) {
    // return result.get();
    // } else {
    // return null;
    // }
    // } catch (ClassCastException unused) {
    // return null;
    // }
    // }
    //
    // @Override
    // public Set.Immutable<V> getEquivalent(final Object o, final Comparator<Object> cmp) {
    // throw new UnsupportedOperationException("Not yet implemented.");
    // }

    @Override
    public boolean put(K key, V val) {
      throw new UnsupportedOperationException("Not yet implemented.");
    }

    @Override
    public boolean put(K key, Set<V> values) {
      if (mutator.get() == null) {
        throw new IllegalStateException("Transient already frozen.");
      }

      // TODO: canoicalize for singleton
      if (values.size() == 1) {
        throw new IllegalStateException();
      }

      final int keyHash = key.hashCode();
      final SetMultimapResult<K, V> details = SetMultimapResult.unchanged();

      final CompactSetMultimapNode<K, V> newRootNode =
          rootNode.updated(null, key, values, transformHashCode(keyHash), 0, details, cmp);

      if (details.isModified()) {
        if (details.hasReplacedValue()) {
          if (details.getType() == EitherSingletonOrCollection.Type.SINGLETON) {
            final int valHashOld = details.getReplacedValue().hashCode();
            final int valHashNew = values.hashCode();

            rootNode = newRootNode;
            cachedSize = cachedSize - 1 + values.size();

            // int tmp = (values.size() % 2 == 1) ? 0 : keyHash;
            // hashCode = hashCode ^ valHashOld ^ valHashNew ^ tmp;

            if ((1 + values.size()) % 2 == 0) {
              // keyHash count is even
              cachedHashCode = cachedHashCode ^ valHashOld ^ valHashNew;
            } else {
              // keyHash count is odd
              cachedHashCode = cachedHashCode ^ valHashOld ^ valHashNew ^ keyHash;
            }

            // return setOfNew(details.getReplacedValue());

            if (DEBUG) {
              assert checkHashCodeAndSize(cachedHashCode, cachedSize);
            }

            return true;
          } else {
            final int valHashOld = details.getReplacedCollection().hashCode();
            final int valHashNew = values.hashCode();

            rootNode = newRootNode;
            cachedSize = cachedSize - details.getReplacedCollection().size() + values.size();

            // int tmp = (values.size() % 2 == 1) ? 0 : keyHash;
            // hashCode = hashCode ^ valHashOld ^ valHashNew ^ tmp;

            if ((details.getReplacedCollection().size() + values.size()) % 2 == 0) {
              // keyHash count is even
              cachedHashCode = cachedHashCode ^ valHashOld ^ valHashNew;
            } else {
              // keyHash count is odd
              cachedHashCode = cachedHashCode ^ valHashOld ^ valHashNew ^ keyHash;
            }

            // TODO: likely expensive
            // TODO: first calculate difference before swapping
            // return values.asImmutable().removeAll(details.getReplacedCollection());

            if (DEBUG) {
              assert checkHashCodeAndSize(cachedHashCode, cachedSize);
            }

            return true;
          }
        }

        final int valHashOld = 0;
        final int valHashNew = values.hashCode();

        rootNode = newRootNode;
        cachedSize = cachedSize + values.size();

        if ((values.size()) % 2 == 0) {
          // keyHash count is even
          cachedHashCode = cachedHashCode ^ valHashOld ^ valHashNew;
        } else {
          // keyHash count is odd
          cachedHashCode = cachedHashCode ^ valHashOld ^ valHashNew ^ keyHash;
        }

        // return setOfNew();

        if (DEBUG) {
          assert checkHashCodeAndSize(cachedHashCode, cachedSize);
        }

        return true;
      }

      // return setOfNew();
      return false;
    }

    @Override
    public boolean insert(final K key, final V val) {
      if (mutator.get() == null) {
        throw new IllegalStateException("Transient already frozen.");
      }

      final int keyHash = key.hashCode();
      final SetMultimapResult<K, V> details = SetMultimapResult.unchanged();

      final CompactSetMultimapNode<K, V> newRootNode =
          rootNode.inserted(mutator, key, val, transformHashCode(keyHash), 0, details, cmp);

      if (details.isModified()) {
        final int valHashNew = val.hashCode();
        rootNode = newRootNode;
        cachedHashCode ^= (keyHash ^ valHashNew);
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
    public boolean insert(final K key, final Set<V> values) {
      throw new UnsupportedOperationException("Not yet implemented.");
    }

    @Override
    public boolean remove(final K key, final V val) {
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
        cachedHashCode = cachedHashCode ^ (keyHash ^ valHash);
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
    public boolean remove(final K key) {
      throw new UnsupportedOperationException("Not yet implemented.");
    }

    @Override
    public boolean union(final SetMultimap<? extends K, ? extends V> setMultimap) {
      boolean modified = false;

      /* TODO: use more efficient Iterator<K, Set<V>> */
      for (Map.Entry<? extends K, ? extends V> entry : setMultimap) {
        modified |= this.insert(entry.getKey(), entry.getValue());
      }

      return modified;
    }

    @Override
    public boolean intersect(final SetMultimap<? extends K, ? extends V> setMultimap) {
      throw new UnsupportedOperationException("Not yet implemented.");
    }

    @Override
    public boolean complement(final SetMultimap<? extends K, ? extends V> setMultimap) {
      throw new UnsupportedOperationException("Not yet implemented.");
    }

    @Override
    public long size() {
      return cachedSize;
    }

    @Override
    public boolean isEmpty() {
      return cachedSize == 0;
    }

    @Override
    public Iterator<Map.Entry<K, V>> iterator() {
      return new TransientSetMultimapTupleIterator<>(this,
          AbstractSpecialisedImmutableMap::entryOf);
    }

    // TODO: make transient version of this iterator
    @Override
    public Iterator<Map.Entry<K, Object>> nativeEntryIterator() {
      return new SetMultimapNativeTupleIterator<>(rootNode);
    }

    // @Override
    // public Iterator<K> keyIterator() {
    // return new TransientSetMultimapKeyIterator<>(this);
    // }
    //
    // @Override
    // public Iterator<V> valueIterator() {
    // return valueCollectionsStream().flatMap(Set::stream).iterator();
    // }
    //
    // @Override
    // public Iterator<Map.Entry<K, V>> entryIterator() {
    // return new TransientSetMultimapTupleIterator<>(this,
    // AbstractSpecialisedImmutableMap::entryOf);
    // }
    //
    // @Override
    // public <T> Iterator<T> tupleIterator(final BiFunction<K, V, T> tupleOf) {
    // return new TransientSetMultimapTupleIterator<>(this, tupleOf);
    // }
    //
    // private Spliterator<Set.Immutable<V>> valueCollectionsSpliterator() {
    // /*
    // * TODO: specialize between mutable / immutable ({@see Spliterator.IMMUTABLE})
    // */
    // int characteristics = Spliterator.NONNULL | Spliterator.SIZED | Spliterator.SUBSIZED;
    // return Spliterators.spliterator(new SetMultimapValueIterator<>(rootNode), size(),
    // characteristics);
    // }
    //
    // private Stream<Set.Immutable<V>> valueCollectionsStream() {
    // boolean isParallel = false;
    // return StreamSupport.stream(valueCollectionsSpliterator(), isParallel);
    // }

    public static class TransientSetMultimapKeyIterator<K, V> extends SetMultimapKeyIterator<K, V> {
      final TransientTrieSetMultimap<K, V> collection;
      K lastKey;

      public TransientSetMultimapKeyIterator(final TransientTrieSetMultimap<K, V> collection) {
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
      final TransientTrieSetMultimap<K, V> collection;

      public TransientSetMultimapValueIterator(final TransientTrieSetMultimap<K, V> collection) {
        super(collection.rootNode);
        this.collection = collection;
      }

      @Override
      public Set.Immutable<V> next() {
        return super.next();
      }

      @Override
      public void remove() {
        throw new UnsupportedOperationException();
      }
    }

    public static class TransientSetMultimapTupleIterator<K, V, T>
        extends SetMultimapTupleIterator<K, V, T> {
      final TransientTrieSetMultimap<K, V> collection;

      public TransientSetMultimapTupleIterator(final TransientTrieSetMultimap<K, V> collection,
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
        collection.remove(currentKey, currentValue);
      }
    }

    // @Override
    // public Set<K> keySet() {
    // Set<K> keySet = null;
    //
    // if (keySet == null) {
    // keySet = new AbstractSet<K>() {
    // @Override
    // public Iterator<K> iterator() {
    // return TransientTrieSetMultimap.this.keyIterator();
    // }
    //
    // @Override
    // public int size() {
    // return TransientTrieSetMultimap.this.sizeDistinct();
    // }
    //
    // @Override
    // public boolean isEmpty() {
    // return TransientTrieSetMultimap.this.isEmpty();
    // }
    //
    // @Override
    // public void clear() {
    // TransientTrieSetMultimap.this.clear();
    // }
    //
    // @Override
    // public boolean contains(Object k) {
    // return TransientTrieSetMultimap.this.containsKey(k);
    // }
    // };
    // }
    //
    // return keySet;
    // }
    //
    // @Override
    // public Collection<V> values() {
    // Collection<V> values = null;
    //
    // if (values == null) {
    // values = new AbstractCollection<V>() {
    // @Override
    // public Iterator<V> iterator() {
    // return TransientTrieSetMultimap.this.valueIterator();
    // }
    //
    // @Override
    // public int size() {
    // return TransientTrieSetMultimap.this.size();
    // }
    //
    // @Override
    // public boolean isEmpty() {
    // return TransientTrieSetMultimap.this.isEmpty();
    // }
    //
    // @Override
    // public void clear() {
    // TransientTrieSetMultimap.this.clear();
    // }
    //
    // @Override
    // public boolean contains(Object v) {
    // return TransientTrieSetMultimap.this.containsValue(v);
    // }
    // };
    // }
    //
    // return values;
    // }
    //
    // @Override
    // public Set<java.util.Map.Entry<K, V>> entrySet() {
    // Set<java.util.Map.Entry<K, V>> entrySet = null;
    //
    // if (entrySet == null) {
    // entrySet = new AbstractSet<java.util.Map.Entry<K, V>>() {
    // @Override
    // public Iterator<java.util.Map.Entry<K, V>> iterator() {
    // return new Iterator<Map.Entry<K, V>>() {
    // private final Iterator<Map.Entry<K, V>> i = entryIterator();
    //
    // @Override
    // public boolean hasNext() {
    // return i.hasNext();
    // }
    //
    // @Override
    // public Map.Entry<K, V> next() {
    // return i.next();
    // }
    //
    // @Override
    // public void remove() {
    // i.remove();
    // }
    // };
    // }
    //
    // @Override
    // public int size() {
    // return TransientTrieSetMultimap.this.size();
    // }
    //
    // @Override
    // public boolean isEmpty() {
    // return TransientTrieSetMultimap.this.isEmpty();
    // }
    //
    // @Override
    // public void clear() {
    // TransientTrieSetMultimap.this.clear();
    // }
    //
    // @Override
    // public boolean contains(Object k) {
    // return TransientTrieSetMultimap.this.containsKey(k);
    // }
    // };
    // }
    //
    // return entrySet;
    // }

    @Override
    public boolean equals(final Object other) {
      if (other == this) {
        return true;
      }
      if (other == null) {
        return false;
      }

      if (other instanceof TransientTrieSetMultimap) {
        TransientTrieSetMultimap<?, ?> that = (TransientTrieSetMultimap<?, ?>) other;

        if (this.cachedSize != that.cachedSize) {
          return false;
        }

        if (this.cachedHashCode != that.cachedHashCode) {
          return false;
        }

        return rootNode.equals(that.rootNode);
      } else if (other instanceof SetMultimap) {
        try {
          @SuppressWarnings("unchecked")
          SetMultimap<K, V> that = (SetMultimap<K, V>) other;

          if (this.size() != that.size())
            return false;

          for (Map.Entry<K, V> entry : that) {
            final K key = (K) entry.getKey();
            final Optional<Set.Immutable<V>> result =
                rootNode.findByKey(key, transformHashCode(key.hashCode()), 0, cmp);

            if (!result.isPresent()) {
              return false;
            } else {
              @SuppressWarnings("unchecked")
              final Set.Immutable<V> valColl = (Set.Immutable<V>) entry.getValue();

              if (!result.get().equals(valColl)) {
                return false;
              }
            }
          }
        } catch (ClassCastException unused) {
          return false;
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
    public SetMultimap.Immutable<K, V> asImmutable() {
      if (mutator.get() == null) {
        throw new IllegalStateException("Transient already frozen.");
      }

      mutator.set(null);
      return new TrieSetMultimap<K, V>(cmp, rootNode, cachedHashCode, cachedSize);
    }
  }

}
