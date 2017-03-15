/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.capsule.core;

import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import io.usethesource.capsule.SetMultimap;
import io.usethesource.capsule.core.trie.ArrayView;
import io.usethesource.capsule.core.trie.EitherSingletonOrCollection;
import io.usethesource.capsule.core.trie.EitherSingletonOrCollection.Type;
import io.usethesource.capsule.core.trie.MultimapNode;
import io.usethesource.capsule.core.trie.MultimapResult;
import io.usethesource.capsule.util.ArrayUtils;
import io.usethesource.capsule.util.EqualityComparator;
import io.usethesource.capsule.util.collection.AbstractSpecialisedImmutableMap;
import io.usethesource.capsule.util.collection.AbstractSpecialisedImmutableSet;

import static io.usethesource.capsule.core.trie.EitherSingletonOrCollection.Type.COLLECTION;
import static io.usethesource.capsule.core.trie.EitherSingletonOrCollection.Type.SINGLETON;
import static io.usethesource.capsule.core.trie.MultimapResult.Modification.INSERTED_KEY;
import static io.usethesource.capsule.core.trie.MultimapResult.Modification.INSERTED_PAYLOAD;
import static io.usethesource.capsule.core.trie.MultimapResult.Modification.INSERTED_VALUE;
import static io.usethesource.capsule.core.trie.MultimapResult.Modification.INSERTED_VALUE_COLLECTION;
import static io.usethesource.capsule.core.trie.MultimapResult.Modification.NOTHING;
import static io.usethesource.capsule.core.trie.MultimapResult.Modification.REMOVED_KEY;
import static io.usethesource.capsule.core.trie.MultimapResult.Modification.REMOVED_PAYLOAD;
import static io.usethesource.capsule.core.trie.MultimapResult.Modification.REMOVED_VALUE;
import static io.usethesource.capsule.core.trie.MultimapResult.Modification.REMOVED_VALUE_COLLECTION;
import static io.usethesource.capsule.core.trie.MultimapResult.Modification.REPLACED_PAYLOAD;
import static io.usethesource.capsule.core.trie.MultimapResult.Modification.REPLACED_VALUE;
import static io.usethesource.capsule.core.trie.MultimapResult.Modification.REPLACED_VALUE_COLLECTION;
import static io.usethesource.capsule.util.BitmapUtils.isBitInBitmap;
import static io.usethesource.capsule.util.collection.AbstractSpecialisedImmutableMap.entryOf;

/**
 * Persistent trie-based set multi-map implementing the HCHAMP encoding.
 */
public class PersistentTrieSetMultimap<K, V> implements SetMultimap.Immutable<K, V> {

  private final EqualityComparator<Object> cmp;

  private static final PersistentTrieSetMultimap EMPTY_SETMULTIMAP = new PersistentTrieSetMultimap(
      EqualityComparator.EQUALS, CompactSetMultimapNode.EMPTY_NODE, 0, 0, 0);

  private static final boolean DEBUG = false;

  private final AbstractSetMultimapNode<K, V> rootNode;
  //  private final int cachedHashCode;
  private final int cachedSize;

  private final int cachedKeySetSize;
  private final int cachedKeySetHashCode;

  PersistentTrieSetMultimap(EqualityComparator<Object> cmp,
      AbstractSetMultimapNode<K, V> rootNode) {
    this.cmp = cmp;
    this.rootNode = rootNode;

    // // this.cachedHashCode = hashCode(rootNode);
    this.cachedSize = size(rootNode);

    this.cachedKeySetHashCode = keySetHashCode(rootNode);
    this.cachedKeySetSize = keySetSize(rootNode);

    if (DEBUG) {
      // assert checkHashCodeAndSize(cachedHashCode, cachedSize, entryIterator());
      assert checkKeySetHashCodeAndSize(cachedKeySetHashCode, cachedKeySetSize, keyIterator());
    }
  }

  PersistentTrieSetMultimap(EqualityComparator<Object> cmp, AbstractSetMultimapNode<K, V> rootNode,
      int cachedSize, int keySetHashCode, int keySetSize) {
    this.cmp = cmp;
    this.rootNode = rootNode;

//    // this.cachedHashCode = cachedHashCode;
    this.cachedSize = cachedSize;

    this.cachedKeySetHashCode = keySetHashCode;
    this.cachedKeySetSize = keySetSize;

    if (DEBUG) {
//      assert checkHashCodeAndSize(cachedHashCode, cachedSize, entryIterator());
      assert checkKeySetHashCodeAndSize(cachedKeySetHashCode, cachedKeySetSize, keyIterator());
    }
  }

  public static final <K, V> SetMultimap.Immutable<K, V> of() {
    return PersistentTrieSetMultimap.EMPTY_SETMULTIMAP;
  }

  public static final <K, V> SetMultimap.Immutable<K, V> of(EqualityComparator<Object> cmp) {
    // TODO: unify with `of()`
    return new PersistentTrieSetMultimap(cmp, CompactSetMultimapNode.EMPTY_NODE, 0, 0, 0);
  }

  public static final <K, V> SetMultimap.Immutable<K, V> of(K key, V... values) {
    SetMultimap.Immutable<K, V> result = PersistentTrieSetMultimap.EMPTY_SETMULTIMAP;

    for (V value : values) {
      result = result.__insert(key, value);
    }

    return result;
  }

  public static final <K, V> SetMultimap.Immutable<K, V> of(K key0, V value0, K key1, V value1) {
    SetMultimap.Transient<K, V> result = PersistentTrieSetMultimap.EMPTY_SETMULTIMAP.asTransient();
    result.__insert(key0, value0);
    result.__insert(key1, value1);
    return result.freeze();
  }

  public static final <K, V> SetMultimap.Transient<K, V> transientOf() {
    return PersistentTrieSetMultimap.EMPTY_SETMULTIMAP.asTransient();
  }

  public static final <K, V> SetMultimap.Transient<K, V> transientOf(
      EqualityComparator<Object> cmp) {
    // TODO: unify with `of()`
    return (SetMultimap.Transient<K, V>) of(cmp).asTransient();
  }

  public static final <K, V> SetMultimap.Transient<K, V> transientOf(K key, V... values) {
    final SetMultimap.Transient<K, V> result =
        PersistentTrieSetMultimap.EMPTY_SETMULTIMAP.asTransient();

    for (V value : values) {
      result.__insert(key, value);
    }

    return result;
  }

  private static <K, V> int hashCode(AbstractSetMultimapNode<K, V> rootNode) {
    int hash = 0;

    final Iterator<Entry<K, V>> it = new SetMultimapTupleIterator<>(rootNode,
        AbstractSpecialisedImmutableMap::entryOf);

    while (it.hasNext()) {
      final Map.Entry<K, V> entry = it.next();
      final K key = entry.getKey();
      final V val = entry.getValue();

      hash += key.hashCode() ^ val.hashCode();
    }

    return hash;
  }

  private static <K, V> int size(AbstractSetMultimapNode<K, V> rootNode) {
    int size = 0;

    final Iterator<Entry<K, V>> it = new SetMultimapTupleIterator<>(rootNode,
        AbstractSpecialisedImmutableMap::entryOf);

    while (it.hasNext()) {
      it.next();
      size += 1;
    }

    return size;
  }

  private static <K, V> int keySetHashCode(AbstractSetMultimapNode<K, V> rootNode) {
    int hash = 0;

    final Iterator<K> it = new SetMultimapKeyIterator<>(rootNode);

    while (it.hasNext()) {
      final K key = it.next();
      hash += key.hashCode();
    }

    return hash;
  }

  private static <K, V> int keySetSize(AbstractSetMultimapNode<K, V> rootNode) {
    int size = 0;

    final Iterator<K> it = new SetMultimapKeyIterator<>(rootNode);

    while (it.hasNext()) {
      it.next();
      size += 1;
    }

    return size;
  }

  static <K, V> boolean checkHashCodeAndSize(final int targetHash, final int targetSize,
      final Iterator<Map.Entry<K, V>> iterator) {
    int hash = 0;
    int size = 0;

    while (iterator.hasNext()) {
      final Map.Entry<K, V> entry = iterator.next();
      final K key = entry.getKey();
      final V val = entry.getValue();

      hash += key.hashCode() ^ val.hashCode();
      size += 1;
    }

    return hash == targetHash && size == targetSize;
  }

  static <K> boolean checkKeySetHashCodeAndSize(final int targetHash, final int targetSize,
      final Iterator<K> iterator) {
    int hash = 0;
    int size = 0;

    while (iterator.hasNext()) {
      final K key = iterator.next();

      hash += key.hashCode();
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
      return rootNode.containsKey(key, transformHashCode(key.hashCode()), 0, cmp);
    } catch (ClassCastException unused) {
      return false;
    }
  }

  @Override
  public boolean containsValue(final Object o) {
    for (Iterator<V> iterator = valueIterator(); iterator.hasNext(); ) {
      if (cmp.equals(iterator.next(), o)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public boolean containsEntry(final Object o0, final Object o1) {
    try {
      final K key = (K) o0;
      final V val = (V) o1;
      return rootNode.containsTuple(key, val, transformHashCode(key.hashCode()), 0, cmp);
    } catch (ClassCastException unused) {
      return false;
    }
  }

  @Override
  public io.usethesource.capsule.Set.Immutable<V> get(final Object o) {
    try {
      final K key = (K) o;
      final Optional<io.usethesource.capsule.Set.Immutable<V>> result =
          rootNode.findByKey(key, transformHashCode(key.hashCode()), 0, cmp);

      return result.orElse(io.usethesource.capsule.Set.Immutable.of());
    } catch (ClassCastException unused) {
      return io.usethesource.capsule.Set.Immutable.of();
    }
  }

  private static final <K, V> int tupleHash(final int keyHash, final int valueHash) {
    return keyHash ^ valueHash;
  }

  private static final <K, V> int tupleHash(final int keyHash, final V value) {
    return tupleHash(keyHash, Objects.hashCode(value));
  }

  private static final <K, V> int tupleHash(final K key, final V value) {
    return tupleHash(Objects.hashCode(key), Objects.hashCode(value));
  }

  private static final <K, V, C extends java.util.Collection<V>> int tupleHash(final int keyHash,
      final C values) {
    return values.stream().mapToInt(Objects::hashCode)
        .map(valueHash -> tupleHash(keyHash, valueHash)).sum();
  }

  private static final <K, V, C extends java.util.Collection<V>> int tupleHash(final K key,
      final C values) {
    return tupleHash(Objects.hashCode(key), values);
  }

  @Override
  public final SetMultimap.Immutable<K, V> __put(K key, V value) {
    return __put(key, AbstractSpecialisedImmutableSet.setOf(value));
  }

  @Override
  public final SetMultimap.Immutable<K, V> __put(K key,
      io.usethesource.capsule.Set.Immutable<V> values) {
    if (values.isEmpty()) {
      return __remove(key);
    }

    final int keyHash = key.hashCode();
    final SetMultimapResult<K, V> details = SetMultimapResult.unchanged();

//    final AbstractSetMultimapNode<K, V> newRootNode;
//
//    if (values.size() == 1) {
//      final V value = values.findFirst().get();
//      newRootNode = rootNode
//          .updatedSingle(null, key, value, transformHashCode(keyHash), 0, details, cmp);
//    } else {
//      newRootNode = rootNode
//          .updatedMultiple(null, key, values, transformHashCode(keyHash), 0, details, cmp);
//    }

    final AbstractSetMultimapNode<K, V> newRootNode =
        rootNode.updated(null, key, values, transformHashCode(keyHash), 0, details, cmp);

    switch (details.getModificationEffect()) {
      case REPLACED_PAYLOAD: {
        // int hashCodeDeltaOld = tupleHash(keyHash, details.getEvictedPayload());
        // int hashCodeDeltaNew = tupleHash(keyHash, values);

        // int propertyHashCode = cachedHashCode + hashCodeDeltaNew - hashCodeDeltaOld;
        int propertySize = cachedSize - details.getEvictedPayload().size() + values.size();
        int propertyKeySetHashCode = cachedKeySetHashCode;
        int propertyKeySetSize = cachedKeySetSize;

        return new PersistentTrieSetMultimap<K, V>(cmp, newRootNode,
            propertySize, propertyKeySetHashCode, propertyKeySetSize);
      }

      case INSERTED_PAYLOAD: {
        assert details.getModificationDetails().contains(INSERTED_KEY);

        // int hashCodeDeltaNew = tupleHash(keyHash, values);

        // int propertyHashCode = cachedHashCode + hashCodeDeltaNew;
        int propertySize = cachedSize + values.size();
        int propertyKeySetHashCode = cachedKeySetHashCode + keyHash;
        int propertyKeySetSize = cachedKeySetSize + 1;

        return new PersistentTrieSetMultimap<K, V>(cmp, newRootNode,
            propertySize, propertyKeySetHashCode, propertyKeySetSize);
      }

      default: {
        return this;
      }
    }
  }

  private static final BinaryOperator<Boolean> BOOLEAN_OR = (left, right) -> left | right;
  private static final BinaryOperator<Boolean> BOOLEAN_XOR = (left, right) -> left ^ right;

  @Override
  public final SetMultimap.Immutable<K, V> __insert(final K key, final V value) {
    return __insert(key, AbstractSpecialisedImmutableSet.setOf(value));
  }

  /*
   * TODO: erroneous -> does not communicate back how many values were inserted (necessary for updating size count)
   */
  @Override
  public final SetMultimap.Immutable<K, V> __insert(K key,
      io.usethesource.capsule.Set.Immutable<V> values) {
    if (values.isEmpty()) {
      return this;
    }

    final int keyHash = key.hashCode();
    final SetMultimapResult<K, V> details = SetMultimapResult.unchanged();

//    final AbstractSetMultimapNode<K, V> newRootNode;
//
//    if (values.size() == 1) {
//      final V value = values.findFirst().get();
//      newRootNode = rootNode
//          .insertedSingle(null, key, value, transformHashCode(keyHash), 0, details, cmp);
//    } else {
//      newRootNode = rootNode
//          .insertedMultiple(null, key, values, transformHashCode(keyHash), 0, details, cmp);
//    }

    final AbstractSetMultimapNode<K, V> newRootNode = rootNode
        .inserted(null, key, values, transformHashCode(keyHash), 0, details, cmp);

    switch (details.getModificationEffect()) {
      case INSERTED_PAYLOAD: {
        // int hashCodeDeltaNew = tupleHash(keyHash, values);

        // int propertyHashCode = cachedHashCode + hashCodeDeltaNew;
        int propertySize = cachedSize + details.sizeDelta().get();
        int propertyKeySetHashCode = cachedKeySetHashCode;
        int propertyKeySetSize = cachedKeySetSize;

        if (details.getModificationDetails().contains(INSERTED_KEY)) {
          propertyKeySetHashCode += keyHash;
          propertyKeySetSize += 1;
        }

        return new PersistentTrieSetMultimap<K, V>(cmp, newRootNode,
            propertySize, propertyKeySetHashCode, propertyKeySetSize);
      }

      default: {
        return this;
      }
    }
  }

  @Override
  public SetMultimap.Immutable<K, V> union(
      final SetMultimap<? extends K, ? extends V> setMultimap) {
    final SetMultimap.Transient<K, V> tmpTransient = this.asTransient();
    tmpTransient.union(setMultimap);
    return tmpTransient.freeze();
  }

  @Override
  public final SetMultimap.Immutable<K, V> __remove(final K key, final V value) {
    final int keyHash = key.hashCode();
    final SetMultimapResult<K, V> details = SetMultimapResult.unchanged();

    final AbstractSetMultimapNode<K, V> newRootNode =
        rootNode.removed(null, key, value, transformHashCode(keyHash), 0, details, cmp);

    switch (details.getModificationEffect()) {
      case REMOVED_PAYLOAD: {
        // int hashCodeDeltaOld = tupleHash(keyHash, value); // TODO: support collection

        // int propertyHashCode = cachedHashCode - hashCodeDeltaOld;
        int propertySize = cachedSize - 1; // TODO: support collection
        int propertyKeySetHashCode = cachedKeySetHashCode;
        int propertyKeySetSize = cachedKeySetSize;

        if (details.getModificationDetails().contains(REMOVED_KEY)) {
          propertyKeySetHashCode -= keyHash;
          propertyKeySetSize -= 1;
        }

        return new PersistentTrieSetMultimap<K, V>(cmp, newRootNode,
            propertySize, propertyKeySetHashCode, propertyKeySetSize);
      }

      default: {
        return this;
      }
    }
  }

  @Override
  public final SetMultimap.Immutable<K, V> __remove(K key) {
    final int keyHash = key.hashCode();
    final SetMultimapResult<K, V> details = SetMultimapResult.unchanged();

    final AbstractSetMultimapNode<K, V> newRootNode =
        rootNode.removed(null, key, transformHashCode(keyHash), 0, details, cmp);

    switch (details.getModificationEffect()) {
      case REMOVED_PAYLOAD: {
        assert details.getModificationDetails().contains(REMOVED_KEY);

        // int hashCodeDeltaOld = tupleHash(keyHash, details.getEvictedPayload());

        // int propertyHashCode = cachedHashCode - hashCodeDeltaOld;
        int propertySize = cachedSize - details.getEvictedPayload().size();
        int propertyKeySetHashCode = cachedKeySetHashCode - keyHash;
        int propertyKeySetSize = cachedKeySetSize - 1;

        return new PersistentTrieSetMultimap<K, V>(cmp, newRootNode,
            propertySize, propertyKeySetHashCode, propertyKeySetSize);
      }

      default: {
        return this;
      }
    }
  }

  @Override
  public SetMultimap.Immutable<V, K> inverseMap() {
    final SetMultimap.Transient<V, K> builder = PersistentTrieSetMultimap.transientOf();

    entryIterator().forEachRemaining(tuple -> builder.__insert(tuple.getValue(), tuple.getKey()));

    return builder.freeze();
  }

  @Override
  public int size() {
    return cachedSize;
  }

  @Override
  public int sizeDistinct() {
    return cachedKeySetSize;
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

  private Spliterator<io.usethesource.capsule.Set.Immutable<V>> valueCollectionsSpliterator() {
    /*
     * TODO: specialize between mutable / SetMultimap.Immutable<K, V> ({@see Spliterator.IMMUTABLE})
     */
    int characteristics = Spliterator.NONNULL | Spliterator.SIZED | Spliterator.SUBSIZED;
    return Spliterators.spliterator(new SetMultimapValueIterator<>(rootNode), size(),
        characteristics);
  }

  private Stream<io.usethesource.capsule.Set.Immutable<V>> valueCollectionsStream() {
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
          return PersistentTrieSetMultimap.this.keyIterator();
        }

        @Override
        public int size() {
          return PersistentTrieSetMultimap.this.sizeDistinct();
        }

        @Override
        public boolean isEmpty() {
          return PersistentTrieSetMultimap.this.isEmpty();
        }

        @Override
        public void clear() {
          throw new UnsupportedOperationException();
        }

        @Override
        public boolean contains(Object k) {
          return PersistentTrieSetMultimap.this.containsKey(k);
        }
      };
    }

    return keySet;
  }

//  /**
//   * Eagerly calcualated set of keys (instead of returning a set view on a map).
//   *
//   * @return canonical SetMultimap.Immutable<K, V> set of keys
//   */
//  @Override
//  public Set<K> keySet() {
//    final BottomUpTransientNodeTransformer<AbstractSetMultimapNode<K, V>, PersistentTrieSet.AbstractSetNode<K>> transformer =
//        new BottomUpTransientNodeTransformer<>(rootNode,
//            (node, mutator) -> node.toSetNode(mutator));
//
//    final PersistentTrieSet.AbstractSetNode<K> newRootNode = transformer.apply();
//
//    return new PersistentTrieSet<>(newRootNode, cachedKeySetHashCode, cachedKeySetSize);
//  }

  @Override
  public Collection<V> values() {
    Collection<V> values = null;

    if (values == null) {
      values = new AbstractCollection<V>() {
        @Override
        public Iterator<V> iterator() {
          return PersistentTrieSetMultimap.this.valueIterator();
        }

        @Override
        public int size() {
          return PersistentTrieSetMultimap.this.size();
        }

        @Override
        public boolean isEmpty() {
          return PersistentTrieSetMultimap.this.isEmpty();
        }

        @Override
        public void clear() {
          throw new UnsupportedOperationException();
        }

        @Override
        public boolean contains(Object v) {
          return PersistentTrieSetMultimap.this.containsValue(v);
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
          return PersistentTrieSetMultimap.this.size();
        }

        @Override
        public boolean isEmpty() {
          return PersistentTrieSetMultimap.this.isEmpty();
        }

        @Override
        public void clear() {
          throw new UnsupportedOperationException();
        }

        @Override
        public boolean contains(Object k) {
          return PersistentTrieSetMultimap.this.containsKey(k);
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

    if (other instanceof PersistentTrieSetMultimap) {
      PersistentTrieSetMultimap<?, ?> that = (PersistentTrieSetMultimap<?, ?>) other;

      if (this.cachedSize != that.cachedSize) {
        return false;
      }

//      if (this.cachedHashCode != that.cachedHashCode) {
//        return false;
//      }

      if (this.cachedKeySetSize != that.cachedKeySetSize) {
        return false;
      }

      if (this.cachedKeySetHashCode != that.cachedKeySetHashCode) {
        return false;
      }

      return rootNode.equals(that.rootNode);
    } else if (other instanceof SetMultimap) {
      SetMultimap that = (SetMultimap) other;

      if (this.size() != that.size()) {
        return false;
      }

      for (
          Iterator<Map.Entry> it = that.entrySet().iterator(); it.hasNext(); ) {
        Map.Entry entry = it.next();

        try {
          final K key = (K) entry.getKey();
          final Optional<io.usethesource.capsule.Set.Immutable<V>> result =
              rootNode.findByKey(key, transformHashCode(key.hashCode()), 0, cmp);

          if (!result.isPresent()) {
            return false;
          } else {
            final io.usethesource.capsule.Set.Immutable<V> valColl =
                (io.usethesource.capsule.Set.Immutable<V>) entry.getValue();

            if (!cmp.equals(result.get(), valColl)) {
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
//    return cachedHashCode;
    return hashCode(rootNode);
  }

  @Override
  public String toString() {
    final int TO_STRING_MAX_COUNT = 10;

    String body =
        entrySet().stream().limit(TO_STRING_MAX_COUNT)
            .map(entry -> String.format("%s: %s", entry.getKey(), entry.getValue()))
            .reduce((o1, o2) -> String.join(", ", o1, o2)).orElse("");

    if (size() > TO_STRING_MAX_COUNT && !body.isEmpty()) {
      return String.format("{%s, ...}", body);
    } else {
      return String.format("{%s}", body);
    }
  }

  @Override
  public boolean isTransientSupported() {
    return true;
  }

  @Override
  public SetMultimap.Transient<K, V> asTransient() {
    return new TransientTrieSetMultimap<K, V>(this);
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

  static final class SetMultimapResult<K, V> implements
      MultimapResult<K, V, io.usethesource.capsule.Set.Immutable<V>> {

    private Modification modificationEffect = NOTHING;
    private EnumSet<Modification> modificationDetails = EnumSet.noneOf(Modification.class);
    private Optional<Integer> sizeDelta = Optional.empty();

    private io.usethesource.capsule.Set.Immutable<V> evictedPayload =
        io.usethesource.capsule.Set.Immutable.of();

    @Override
    public Modification getModificationEffect() {
      return modificationEffect;
    }

    @Override
    public EnumSet<Modification> getModificationDetails() {
      return modificationDetails;
    }

    public Optional<Integer> sizeDelta() {
      return sizeDelta;
    }

    @Override
    public void modified(Modification modificationEffect,
        EnumSet<Modification> modificationDetails) {
      this.modificationEffect = modificationEffect;
      this.modificationDetails = modificationDetails;
    }

    @Override
    public void modified(Modification modificationEffect,
        EnumSet<Modification> modificationDetails, int sizeDelta) {
      this.modificationEffect = modificationEffect;
      this.modificationDetails = modificationDetails;
      this.sizeDelta = Optional.of(sizeDelta);
    }

    @Override
    public void modified(Modification modificationEffect, EnumSet<Modification> modificationDetails,
        io.usethesource.capsule.Set.Immutable<V> evictedPayload) {
      this.modificationEffect = modificationEffect;
      this.modificationDetails = modificationDetails;
      this.evictedPayload = evictedPayload;
    }

    public io.usethesource.capsule.Set.Immutable<V> getEvictedPayload() {
      return evictedPayload;
    }

    public static <K, V> SetMultimapResult<K, V> unchanged() {
      return new SetMultimapResult<>();
    }

    private SetMultimapResult() {
    }
  }

  protected static abstract class AbstractSetMultimapNode<K, V> implements
      MultimapNode<K, V, io.usethesource.capsule.Set.Immutable<V>, AbstractSetMultimapNode<K, V>> {

    static final int TUPLE_LENGTH = 2;

//    abstract CompactSetMultimapNode<K, V> inserted(final AtomicReference<Thread> mutator,
//        final K key, final V val, final int keyHash, final int shift,
//        final MultimapResult<K, V, io.usethesource.capsule.Set.Immutable<V>> details, EqualityComparator<Object> cmp);
//
//    abstract CompactSetMultimapNode<K, V> updated(final AtomicReference<Thread> mutator,
//        final K key, final V val, final int keyHash, final int shift,
//        final MultimapResult<K, V, io.usethesource.capsule.Set.Immutable<V>> details, EqualityComparator<Object> cmp);
//
//    abstract CompactSetMultimapNode<K, V> removed(final AtomicReference<Thread> mutator,
//        final K key, final V val, final int keyHash, final int shift,
//        final MultimapResult<K, V, io.usethesource.capsule.Set.Immutable<V>> details, EqualityComparator<Object> cmp);
//
//    abstract CompactSetMultimapNode<K, V> removedAll(final AtomicReference<Thread> mutator,
//        final K key, final int keyHash, final int shift, final MultimapResult<K, V, io.usethesource.capsule.Set.Immutable<V>> details,
//        EqualityComparator<Object> cmp);

    // TODO: move to interface
    abstract AbstractSetMultimapNode<K, V> inserted(final AtomicReference<Thread> mutator,
        final K key, final io.usethesource.capsule.Set.Immutable<V> values, final int keyHash,
        final int shift,
        final MultimapResult<K, V, io.usethesource.capsule.Set.Immutable<V>> details,
        EqualityComparator<Object> cmp);

    // TODO: move to interface
    abstract AbstractSetMultimapNode<K, V> insertedMultiple(final AtomicReference<Thread> mutator,
        final K key, final io.usethesource.capsule.Set.Immutable<V> values, final int keyHash,
        final int shift,
        final MultimapResult<K, V, io.usethesource.capsule.Set.Immutable<V>> details,
        EqualityComparator<Object> cmp);

    // TODO: move to interface
    abstract AbstractSetMultimapNode<K, V> updated(AtomicReference<Thread> mutator,
        K key,
        io.usethesource.capsule.Set.Immutable<V> values, int keyHash, int shift,
        MultimapResult<K, V, io.usethesource.capsule.Set.Immutable<V>> details,
        EqualityComparator<Object> cmp);

    static final boolean isAllowedToEdit(AtomicReference<?> x, AtomicReference<?> y) {
      return x != null && y != null && (x == y || x.get() == y.get());
    }

    @Override
    public <T> ArrayView<T> dataArray(final int category, final int component) {
      switch (category) {
        case 0:
          return categoryArrayView0(component);
        case 1:
          return categoryArrayView1(component);
        default:
          throw new IllegalArgumentException("Category %i is not supported.");
      }
    }

    private <T> ArrayView<T> categoryArrayView0(final int component) {
      return new ArrayView<T>() {
        @Override
        public int size() {
          return payloadArity(SINGLETON);
        }

        @Override
        public T get(int index) {
          switch (component) {
            case 0:
              return (T) getSingletonKey(index);
            case 1:
              return (T) getSingletonValue(index);
          }
          throw new IllegalStateException();
        }
      };
    }

    private <T> ArrayView<T> categoryArrayView1(final int component) {
      return new ArrayView<T>() {
        @Override
        public int size() {
          return payloadArity(COLLECTION);
        }

        @Override
        public T get(int index) {
          switch (component) {
            case 0:
              return (T) getCollectionKey(index);
            case 1:
              return (T) getCollectionValue(index);
          }
          throw new IllegalStateException();
        }
      };
    }

//    @Override
//    public <T> ArrayView<T> dataArray(final int category) {
//      switch (category) {
//        case 0:
//          return categoryArrayView0();
//        case 1:
//          return categoryArrayView1();
//        default:
//          throw new IllegalArgumentException("Category %i is not supported.");
//      }
//    }
//
//    private <T> ArrayView<T> categoryArrayView0() {
//      return new ArrayView<T>() {
//        @Override
//        public int size() {
//          return arity(dataMap());
//        }
//
//        @Override
//        public T get(int index) {
//          return (T) entryOf(getSingletonKey(index), getSingletonValue(index));
//        }
//      };
//    }
//
//    private <T> ArrayView<T> categoryArrayView1() {
//      return new ArrayView<T>() {
//        @Override
//        public int size() {
//          return arity(collMap());
//        }
//
//        @Override
//        public T get(int index) {
//          return (T) entryOf(getCollectionKey(index), getCollectionValue(index));
//        }
//      };
//    }

    @Override
    public abstract ArrayView<AbstractSetMultimapNode<K, V>> nodeArray();

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
          if (!hasNext()) {
            throw new NoSuchElementException();
          }
          return AbstractSetMultimapNode.this.getNode(nextIndex++);
        }

        @Override
        public boolean hasNext() {
          return nextIndex < nodeArity;
        }
      };
    }

    abstract boolean hasPayload(EitherSingletonOrCollection.Type type);

    abstract int payloadArity(EitherSingletonOrCollection.Type type);

    abstract K getSingletonKey(final int index);

    abstract V getSingletonValue(final int index);

    abstract K getCollectionKey(final int index);

    abstract io.usethesource.capsule.Set.Immutable<V> getCollectionValue(
        final int index);

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

    /***** CONVERISONS *****/

    abstract PersistentTrieSet.AbstractSetNode<K> toSetNode(AtomicReference<Thread> mutator);

// abstract PersistentTrieSet.AbstractSetNode<K> toSetNode(
    // PersistentTrieSet.AbstractSetNode<K>... newChildren);

    // PersistentTrieSet.AbstractSetNode<K> toSetNode() {
    // throw new UnsupportedOperationException(
    // "Not yet implemented ~ structural conversion of multimap to set.");
    // }
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

    abstract int bitmap(int category);

    @Deprecated
    abstract int dataMap();

    @Deprecated
    abstract int collMap();

    @Deprecated
    abstract int nodeMap();

    abstract int rawMap1();

    abstract int rawMap2();

    @Deprecated
    @Override
    int arity() {
      return arity(dataMap()) + arity(collMap()) + arity(nodeMap());
    }

    static final int arity(int bitmap) {
      if (bitmap == 0) {
        return 0;
      } else {
        return Integer.bitCount(bitmap);
      }
    }

    static final byte SIZE_EMPTY = 0b00;
    static final byte SIZE_ONE = 0b01;
    static final byte SIZE_MORE_THAN_ONE = 0b10;

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

    abstract CompactSetMultimapNode<K, V> copyAndSetSingletonValue(
        final AtomicReference<Thread> mutator, final int bitpos, final V val);

    abstract CompactSetMultimapNode<K, V> copyAndSetCollectionValue(
        final AtomicReference<Thread> mutator, final int bitpos,
        final io.usethesource.capsule.Set.Immutable<V> valColl);

    abstract CompactSetMultimapNode<K, V> copyAndSetNode(final AtomicReference<Thread> mutator,
        final int bitpos, final AbstractSetMultimapNode<K, V> node);

    abstract CompactSetMultimapNode<K, V> copyAndInsertSingleton(
        final AtomicReference<Thread> mutator, final int bitpos, final K key, final V val);

    abstract CompactSetMultimapNode<K, V> copyAndInsertCollection(AtomicReference<Thread> mutator,
        int bitpos,
        K key, io.usethesource.capsule.Set.Immutable<V> values);

    abstract CompactSetMultimapNode<K, V> copyAndMigrateFromSingletonToCollection(
        final AtomicReference<Thread> mutator, final int bitpos, final K key,
        final io.usethesource.capsule.Set.Immutable<V> valColl);

    abstract CompactSetMultimapNode<K, V> copyAndRemoveSingleton(
        final AtomicReference<Thread> mutator, final int bitpos);

    /*
     * Batch updated, necessary for removedAll.
     */
    abstract CompactSetMultimapNode<K, V> copyAndRemoveCollection(
        final AtomicReference<Thread> mutator, final int bitpos);

    abstract CompactSetMultimapNode<K, V> copyAndMigrateFromSingletonToNode(
        final AtomicReference<Thread> mutator, final int bitpos,
        final AbstractSetMultimapNode<K, V> node);

    abstract CompactSetMultimapNode<K, V> copyAndMigrateFromNodeToSingleton(
        final AtomicReference<Thread> mutator, final int bitpos,
        final AbstractSetMultimapNode<K, V> node); // node get's unwrapped inside method

    abstract CompactSetMultimapNode<K, V> copyAndMigrateFromCollectionToNode(
        final AtomicReference<Thread> mutator, final int bitpos,
        final AbstractSetMultimapNode<K, V> node);

    abstract CompactSetMultimapNode<K, V> copyAndMigrateFromNodeToCollection(
        final AtomicReference<Thread> mutator, final int bitpos,
        final AbstractSetMultimapNode<K, V> node); // node get's unwrapped inside method

    abstract CompactSetMultimapNode<K, V> copyAndMigrateFromCollectionToSingleton(
        final AtomicReference<Thread> mutator, final int bitpos, final K key, final V val);

    static final <K, V> CompactSetMultimapNode<K, V> mergeTwoSingletonPairs(final K key0,
        final V val0, final int keyHash0, final K key1, final V val1, final int keyHash1,
        final int shift, EqualityComparator<Object> cmp) {
      // assert !(cmp.equals(key0, key1));

      if (shift >= HASH_CODE_LENGTH) {
        return AbstractHashCollisionNode
            .of(keyHash0, key0, io.usethesource.capsule.Set.Immutable.of(val0), key1,
                io.usethesource.capsule.Set.Immutable.of(val1));
      }

      final int mask0 = mask(keyHash0, shift);
      final int mask1 = mask(keyHash1, shift);

      if (mask0 != mask1) {
        // both nodes fit on same level
        final int rawMap1 = 0;
        final int rawMap2 = bitpos(mask0) | bitpos(mask1);

        if (mask0 < mask1) {
          return nodeOf(null, rawMap1, rawMap2, new Object[]{key0, val0, key1, val1});
        } else {
          return nodeOf(null, rawMap1, rawMap2, new Object[]{key1, val1, key0, val0});
        }
      } else {
        final CompactSetMultimapNode<K, V> node = mergeTwoSingletonPairs(key0, val0, keyHash0,
            key1,
            val1, keyHash1, shift + BIT_PARTITION_SIZE, cmp);
        // values fit on next level

        final int rawMap1 = bitpos(mask0);
        final int rawMap2 = 0;
        return nodeOf(null, rawMap1, rawMap2, new Object[]{node});
      }
    }

    static final <K, V> CompactSetMultimapNode<K, V> mergeCollectionAndSingletonPairs(
        final K key0,
        final io.usethesource.capsule.Set.Immutable<V> valColl0, final int keyHash0,
        final K key1, final V val1, final int keyHash1, final int shift,
        EqualityComparator<Object> cmp) {
      // assert !(cmp.equals(key0, key1));

      if (shift >= HASH_CODE_LENGTH) {
        return AbstractHashCollisionNode
            .of(keyHash0, key1, io.usethesource.capsule.Set.Immutable.of(val1), key0, valColl0);
      }

      final int mask0 = mask(keyHash0, shift);
      final int mask1 = mask(keyHash1, shift);

      if (mask0 != mask1) {
        // both nodes fit on same level
        final int rawMap1 = bitpos(mask0);
        final int rawMap2 = bitpos(mask0) | bitpos(mask1);

        // singleton before collection
        return nodeOf(null, rawMap1, rawMap2, new Object[]{key1, val1, key0, valColl0});
      } else {
        final CompactSetMultimapNode<K, V> node = mergeCollectionAndSingletonPairs(key0, valColl0,
            keyHash0, key1, val1, keyHash1, shift + BIT_PARTITION_SIZE, cmp);
        // values fit on next level

        final int rawMap1 = bitpos(mask0);
        final int rawMap2 = 0;
        return nodeOf(null, rawMap1, rawMap2, new Object[]{node});
      }
    }

    static final <K, V, C extends io.usethesource.capsule.Set.Immutable<V>> AbstractSetMultimapNode<K, V> mergeTwoCollectionPairs(
        final K key0,
        final C valColl0, final int keyHash0, final K key1,
        final C valColl1, final int keyHash1, final int shift,
        EqualityComparator<Object> cmp) {
      assert !(cmp.equals(key0, key1));

      if (shift >= HASH_CODE_LENGTH) {
        return AbstractHashCollisionNode.of(keyHash0, key1, valColl1, key0, valColl1);
      }

      final int mask0 = mask(keyHash0, shift);
      final int mask1 = mask(keyHash1, shift);

      if (mask0 != mask1) {
        // both nodes fit on same level
        int bitmap0 = 1 << mask0 | 1 << mask1;
        int bitmap1 = bitmap0;

        if (mask0 < mask1) {
          return nodeOf(null, bitmap0, bitmap1, new Object[]{key0, valColl0, key1, valColl1});
        } else {
          return nodeOf(null, bitmap0, bitmap1, new Object[]{key1, valColl1, key0, valColl0});
        }
      } else {
        final AbstractSetMultimapNode<K, V> node = mergeTwoCollectionPairs(key0, valColl0, keyHash0,
            key1, valColl1, keyHash1, shift + BIT_PARTITION_SIZE, cmp);
        // values fit on next level
        int bitmap0 = 1 << mask0;
        int bitmap1 = 0;

        return nodeOf(null, bitmap0, bitmap1, new Object[]{node});
      }
    }

    static final CompactSetMultimapNode EMPTY_NODE;

    static {
      EMPTY_NODE = new BitmapIndexedSetMultimapNode<>(null, (0), (0), new Object[]{});
    }

    static final <K, V> CompactSetMultimapNode<K, V> nodeOf(final AtomicReference<Thread> mutator,
        final int nodeMap, final int dataMap, final Object[] nodes) {
      return new BitmapIndexedSetMultimapNode<>(mutator, nodeMap, dataMap, nodes);
    }

    static final <K, V> CompactSetMultimapNode<K, V> nodeOf(AtomicReference<Thread> mutator) {
      return EMPTY_NODE;
    }

    static final <K, V> CompactSetMultimapNode<K, V> nodeOf(AtomicReference<Thread> mutator,
        final int nodeMap, final int dataMap, final K key,
        final io.usethesource.capsule.Set.Immutable<V> valColl) {
      assert nodeMap == 0;
      return nodeOf(mutator, (0), dataMap, new Object[]{key, valColl});
    }

    static final int index(final int bitmap, final int bitpos) {
      return java.lang.Integer.bitCount(bitmap & (bitpos - 1));
    }

    static final int index(final int bitmap, final int mask, final int bitpos) {
      return (bitmap == -1) ? mask : index(bitmap, bitpos);
    }

    @Deprecated
    final int dataIndex(final int bitpos) {
      return java.lang.Integer.bitCount(dataMap() & (bitpos - 1));
    }

    @Deprecated
    final int collIndex(final int bitpos) {
      return java.lang.Integer.bitCount(collMap() & (bitpos - 1));
    }

    @Deprecated
    final int nodeIndex(final int bitpos) {
      return java.lang.Integer.bitCount(nodeMap() & (bitpos - 1));
    }

    @Override // TODO: final
    public boolean containsKey(final K key, final int keyHash, final int shift,
        EqualityComparator<Object> cmp) {
      final int mask = mask(keyHash, shift);
      final int bitpos = bitpos(mask);

      int rawMap1 = this.rawMap1();
      int rawMap2 = this.rawMap2();

      final int collMap = rawMap1 & rawMap2;
      final int dataMap = rawMap2 ^ collMap;
      final int nodeMap = rawMap1 ^ collMap;

      if (isBitInBitmap(dataMap, bitpos)) {
        final int index = index(dataMap, mask, bitpos);
        return cmp.equals(getSingletonKey(index), key);
      }

      if (isBitInBitmap(collMap, bitpos)) {
        final int index = index(collMap, mask, bitpos);
        return cmp.equals(getCollectionKey(index), key);
      }

      if (isBitInBitmap(nodeMap, bitpos)) {
        final int index = index(nodeMap, mask, bitpos);
        return getNode(index).containsKey(key, keyHash, shift + BIT_PARTITION_SIZE, cmp);
      }

      return false;
    }

    @Override // TODO: final
    public boolean containsTuple(final K key, final V value, final int keyHash, final int shift,
        EqualityComparator<Object> cmp) {
      final int mask = mask(keyHash, shift);
      final int bitpos = bitpos(mask);

      int rawMap1 = this.rawMap1();
      int rawMap2 = this.rawMap2();

      final int collMap = rawMap1 & rawMap2;
      final int dataMap = rawMap2 ^ collMap;
      final int nodeMap = rawMap1 ^ collMap;

      if (isBitInBitmap(dataMap, bitpos)) {
        final int index = index(dataMap, mask, bitpos);
        return cmp.equals(getSingletonKey(index), key) && cmp
            .equals(getSingletonValue(index), value);
      }

      if (isBitInBitmap(collMap, bitpos)) {
        final int index = index(collMap, mask, bitpos);
        return cmp.equals(getCollectionKey(index), key)
            && getCollectionValue(index).containsEquivalent(value, cmp.toComparator());
      }

      if (isBitInBitmap(nodeMap, bitpos)) {
        final int index = index(nodeMap, mask, bitpos);
        return getNode(index).containsTuple(key, value, keyHash, shift + BIT_PARTITION_SIZE, cmp);
      }

      return false;
    }

    @Override // TODO: final
    public Optional<io.usethesource.capsule.Set.Immutable<V>> findByKey(final K key,
        final int keyHash, final int shift, EqualityComparator<Object> cmp) {
      final int mask = mask(keyHash, shift);
      final int bitpos = bitpos(mask);

      int rawMap1 = this.rawMap1();
      int rawMap2 = this.rawMap2();

      final int collMap = rawMap1 & rawMap2;
      final int dataMap = rawMap2 ^ collMap;
      final int nodeMap = rawMap1 ^ collMap;

      if (isBitInBitmap(dataMap, bitpos)) {
        final int index = index(dataMap, mask, bitpos);

        final K currentKey = getSingletonKey(index);
        if (cmp.equals(currentKey, key)) {

          final V currentVal = getSingletonValue(index);
          return Optional.of(io.usethesource.capsule.Set.Immutable.of(currentVal));
        }

        return Optional.empty();
      }

      if (isBitInBitmap(collMap, bitpos)) {
        final int index = index(collMap, mask, bitpos);

        final K currentKey = getCollectionKey(index);
        if (cmp.equals(currentKey, key)) {

          final io.usethesource.capsule.Set.Immutable<V> currentValColl =
              getCollectionValue(index);
          return Optional.of(currentValColl);
        }

        return Optional.empty();
      }

      if (isBitInBitmap(nodeMap, bitpos)) {
        final int index = index(nodeMap, mask, bitpos);

        final AbstractSetMultimapNode<K, V> subNode = getNode(index);
        return subNode.findByKey(key, keyHash, shift + BIT_PARTITION_SIZE, cmp);
      }

      // default
      return Optional.empty();
    }

    @Override
    public AbstractSetMultimapNode<K, V> inserted(final AtomicReference<Thread> mutator,
        final K key, final io.usethesource.capsule.Set.Immutable<V> values, final int keyHash,
        final int shift,
        final MultimapResult<K, V, io.usethesource.capsule.Set.Immutable<V>> details,
        EqualityComparator<Object> cmp) {
      if (values.size() == 1) {
        final V value = values.findFirst().get();
        return insertedSingle(mutator, key, value, keyHash, shift, details, cmp);
      } else {
        return insertedMultiple(mutator, key, values, keyHash, shift, details, cmp);
      }
    }

    @Override // TODO: final
    public AbstractSetMultimapNode<K, V> insertedSingle(final AtomicReference<Thread> mutator,
        final K key,
        final V value, final int keyHash, final int shift,
        final MultimapResult<K, V, io.usethesource.capsule.Set.Immutable<V>> details,
        EqualityComparator<Object> cmp) {
      final int mask = mask(keyHash, shift);
      final int bitpos = bitpos(mask);

      int rawMap1 = this.rawMap1();
      int rawMap2 = this.rawMap2();

      final int collMap = rawMap1 & rawMap2;
      final int dataMap = rawMap2 ^ collMap;
      final int nodeMap = rawMap1 ^ collMap;

      if (isBitInBitmap(dataMap, bitpos)) {
        final int dataIndex = index(dataMap, mask, bitpos);
        final K currentKey = getSingletonKey(dataIndex);

        if (cmp.equals(currentKey, key)) {
          final V currentVal = getSingletonValue(dataIndex);

          if (cmp.equals(currentVal, value)) {
            return this;
          } else {
            // migrate from singleton to collection
            final io.usethesource.capsule.Set.Immutable<V> valColl =
                io.usethesource.capsule.Set.Immutable.of(currentVal, value);

            details.modified(INSERTED_PAYLOAD, EnumSet.of(INSERTED_VALUE), 1);
            return copyAndMigrateFromSingletonToCollection(mutator, bitpos, currentKey, valColl);
          }
        } else {
          // prefix-collision (case: singleton x singleton)
          final V currentVal = getSingletonValue(dataIndex);

          final AbstractSetMultimapNode<K, V> subNodeNew = mergeTwoSingletonPairs(currentKey,
              currentVal, transformHashCode(currentKey.hashCode()), key, value, keyHash,
              shift + BIT_PARTITION_SIZE, cmp);

          details.modified(INSERTED_PAYLOAD, EnumSet.of(INSERTED_KEY, INSERTED_VALUE), 1);
          return copyAndMigrateFromSingletonToNode(mutator, bitpos, subNodeNew);
        }
      }

      if (isBitInBitmap(collMap, bitpos)) {
        final int collIndex = index(collMap, mask, bitpos);
        final K currentCollKey = getCollectionKey(collIndex);

        if (cmp.equals(currentCollKey, key)) {
          final io.usethesource.capsule.Set.Immutable<V> currentCollVal =
              getCollectionValue(collIndex);

          if (currentCollVal.contains(value)) {
            return this;
          } else {
            // add new mapping
            final io.usethesource.capsule.Set.Immutable<V> newCollVal =
                currentCollVal.__insert(value);

            details.modified(INSERTED_PAYLOAD, EnumSet.of(INSERTED_VALUE), 1);
            return copyAndSetCollectionValue(mutator, bitpos, newCollVal);
          }
        } else {
          // prefix-collision (case: collection x singleton)
          final io.usethesource.capsule.Set.Immutable<V> currentValues =
              getCollectionValue(collIndex);
          final AbstractSetMultimapNode<K, V> subNodeNew = mergeCollectionAndSingletonPairs(
              currentCollKey, currentValues, transformHashCode(currentCollKey.hashCode()), key,
              value, keyHash, shift + BIT_PARTITION_SIZE, cmp);

          details.modified(INSERTED_PAYLOAD, EnumSet.of(INSERTED_KEY, INSERTED_VALUE), 1);
          return copyAndMigrateFromCollectionToNode(mutator, bitpos, subNodeNew);
        }
      }

      if (isBitInBitmap(nodeMap, bitpos)) {
        final AbstractSetMultimapNode<K, V> subNode = getNode(nodeIndex(bitpos));
        final AbstractSetMultimapNode<K, V> subNodeNew =
            subNode
                .insertedSingle(mutator, key, value, keyHash, shift + BIT_PARTITION_SIZE, details,
                    cmp);

        if (details.getModificationEffect() != NOTHING) {
          return copyAndSetNode(mutator, bitpos, subNodeNew);
        } else {
          return this;
        }
      }

      // default
      details.modified(INSERTED_PAYLOAD, EnumSet.of(INSERTED_KEY, INSERTED_VALUE), 1);
      return copyAndInsertSingleton(mutator, bitpos, key, value);
    }

    // TODO: final
    @Override
    public AbstractSetMultimapNode<K, V> insertedMultiple(final AtomicReference<Thread> mutator,
        final K key, final io.usethesource.capsule.Set.Immutable<V> values, final int keyHash,
        final int shift,
        final MultimapResult<K, V, io.usethesource.capsule.Set.Immutable<V>> details,
        EqualityComparator<Object> cmp) {
      final int mask = mask(keyHash, shift);
      final int bitpos = bitpos(mask);

      int rawMap1 = this.rawMap1();
      int rawMap2 = this.rawMap2();

      final int collMap = rawMap1 & rawMap2;
      final int dataMap = rawMap2 ^ collMap;
      final int nodeMap = rawMap1 ^ collMap;

      if (isBitInBitmap(dataMap, bitpos)) {
        final int dataIndex = index(dataMap, mask, bitpos);
        final K currentKey = getSingletonKey(dataIndex);

        if (cmp.equals(currentKey, key)) {
          final V currentVal = getSingletonValue(dataIndex);

          // migrate from singleton to collection
          final io.usethesource.capsule.Set.Immutable<V> mergedValues = values.__insert(currentVal);
          final int sizeDelta = 2 * values.size() - mergedValues.size();

          details.modified(INSERTED_PAYLOAD, EnumSet.of(INSERTED_VALUE_COLLECTION), sizeDelta);
          return copyAndMigrateFromSingletonToCollection(mutator, bitpos, currentKey, mergedValues);
        } else {
          // prefix-collision (case: singleton x collection)
          final V currentVal = getSingletonValue(dataIndex);

          final AbstractSetMultimapNode<K, V> subNodeNew = mergeCollectionAndSingletonPairs(key,
              values, keyHash, currentKey, currentVal, transformHashCode(currentKey.hashCode()),
              shift + BIT_PARTITION_SIZE, cmp);
          final int sizeDelta = values.size();

          details.modified(INSERTED_PAYLOAD, EnumSet.of(INSERTED_KEY, INSERTED_VALUE_COLLECTION),
              sizeDelta);
          return copyAndMigrateFromSingletonToNode(mutator, bitpos, subNodeNew);
        }
      }

      if (isBitInBitmap(collMap, bitpos)) {
        final int collIndex = index(collMap, mask, bitpos);
        final K currentCollKey = getCollectionKey(collIndex);

        if (cmp.equals(currentCollKey, key)) {
          final io.usethesource.capsule.Set.Immutable<V> currentCollVal =
              getCollectionValue(collIndex);

          // TODO: replace by more efficient union that switches between smaller and bigger
          final io.usethesource.capsule.Set.Immutable<V> mergedValues = currentCollVal
              .__insertAll(values);
          final int sizeDelta = mergedValues.size() - currentCollVal.size();

          if (sizeDelta == 0) {
            return this;
          } else {
            details.modified(INSERTED_PAYLOAD, EnumSet.of(INSERTED_VALUE_COLLECTION), sizeDelta);
            return copyAndSetCollectionValue(mutator, bitpos, mergedValues);
          }
        } else {
          // prefix-collision (case: collection x collection)
          final io.usethesource.capsule.Set.Immutable<V> currentValues =
              getCollectionValue(collIndex);
          final AbstractSetMultimapNode<K, V> subNodeNew = mergeTwoCollectionPairs(
              currentCollKey, currentValues, transformHashCode(currentCollKey.hashCode()), key,
              values, keyHash, shift + BIT_PARTITION_SIZE, cmp);
          final int sizeDelta = values.size();

          details.modified(INSERTED_PAYLOAD, EnumSet.of(INSERTED_KEY, INSERTED_VALUE_COLLECTION),
              sizeDelta);
          return copyAndMigrateFromCollectionToNode(mutator, bitpos, subNodeNew);
        }
      }

      if (isBitInBitmap(nodeMap, bitpos)) {
        final AbstractSetMultimapNode<K, V> subNode = getNode(nodeIndex(bitpos));
        final AbstractSetMultimapNode<K, V> subNodeNew =
            subNode
                .insertedMultiple(mutator, key, values, keyHash, shift + BIT_PARTITION_SIZE,
                    details, cmp);

        if (details.getModificationEffect() != NOTHING) {
          return copyAndSetNode(mutator, bitpos, subNodeNew);
        } else {
          return this;
        }
      }

      // default
      details.modified(INSERTED_PAYLOAD, EnumSet.of(INSERTED_KEY, INSERTED_VALUE_COLLECTION),
          values.size());
      return copyAndInsertCollection(mutator, bitpos, key, values);
    }

    @Override
    public final AbstractSetMultimapNode<K, V> updated(AtomicReference<Thread> mutator,
        K key,
        io.usethesource.capsule.Set.Immutable<V> values, int keyHash, int shift,
        MultimapResult<K, V, io.usethesource.capsule.Set.Immutable<V>> details,
        EqualityComparator<Object> cmp) {
      if (values.size() == 1) {
        final V value = values.findFirst().get();
        return updatedSingle(mutator, key, value, keyHash, shift, details, cmp);
      } else {
        return updatedMultiple(mutator, key, values, keyHash, shift, details, cmp);
      }
    }

    @Override // TODO: final
    public AbstractSetMultimapNode<K, V> updatedSingle(final AtomicReference<Thread> mutator,
        final K key,
        final V value, final int keyHash, final int shift,
        final MultimapResult<K, V, io.usethesource.capsule.Set.Immutable<V>> details,
        EqualityComparator<Object> cmp) {
      final int mask = mask(keyHash, shift);
      final int bitpos = bitpos(mask);

      int rawMap1 = this.rawMap1();
      int rawMap2 = this.rawMap2();

      final int collMap = rawMap1 & rawMap2;
      final int dataMap = rawMap2 ^ collMap;
      final int nodeMap = rawMap1 ^ collMap;

      if (isBitInBitmap(dataMap, bitpos)) {
        final int dataIndex = index(dataMap, mask, bitpos);
        final K currentKey = getSingletonKey(dataIndex);

        if (cmp.equals(currentKey, key)) {
          final V currentVal = getSingletonValue(dataIndex);

          // update singleton value
          details.modified(REPLACED_PAYLOAD, EnumSet.of(REPLACED_VALUE),
              io.usethesource.capsule.Set.Immutable.of(currentVal));
          return copyAndSetSingletonValue(mutator, bitpos, value);
        } else {
          // prefix-collision (case: singleton x singleton)
          final V currentVal = getSingletonValue(dataIndex);

          final AbstractSetMultimapNode<K, V> subNodeNew = mergeTwoSingletonPairs(currentKey,
              currentVal, transformHashCode(currentKey.hashCode()), key, value, keyHash,
              shift + BIT_PARTITION_SIZE, cmp);

          details.modified(INSERTED_PAYLOAD, EnumSet.of(INSERTED_KEY, INSERTED_VALUE));
          return copyAndMigrateFromSingletonToNode(mutator, bitpos, subNodeNew);
        }
      }

      if (isBitInBitmap(collMap, bitpos)) {
        final int collIndex = index(collMap, mask, bitpos);
        final K currentCollKey = getCollectionKey(collIndex);

        if (cmp.equals(currentCollKey, key)) {
          final io.usethesource.capsule.Set.Immutable<V> currentCollVal =
              getCollectionValue(collIndex);

          // migrate from collection to singleton
          details.modified(REPLACED_PAYLOAD, EnumSet.of(REPLACED_VALUE_COLLECTION), currentCollVal);
          return copyAndMigrateFromCollectionToSingleton(mutator, bitpos, currentCollKey, value);
        } else {
          // prefix-collision (case: collection x singleton)
          final io.usethesource.capsule.Set.Immutable<V> currentValues =
              getCollectionValue(collIndex);
          final AbstractSetMultimapNode<K, V> subNodeNew = mergeCollectionAndSingletonPairs(
              currentCollKey, currentValues, transformHashCode(currentCollKey.hashCode()), key,
              value, keyHash, shift + BIT_PARTITION_SIZE, cmp);

          details.modified(INSERTED_PAYLOAD, EnumSet.of(INSERTED_KEY, INSERTED_VALUE));
          return copyAndMigrateFromCollectionToNode(mutator, bitpos, subNodeNew);
        }
      }

      if (isBitInBitmap(nodeMap, bitpos)) {
        final AbstractSetMultimapNode<K, V> subNode = getNode(nodeIndex(bitpos));
        final AbstractSetMultimapNode<K, V> subNodeNew =
            subNode.updatedSingle(mutator, key, value, keyHash, shift + BIT_PARTITION_SIZE, details,
                cmp);

        if (details.getModificationEffect() != NOTHING) {
          return copyAndSetNode(mutator, bitpos, subNodeNew);
        } else {
          return this;
        }
      }

      // default
      details.modified(INSERTED_PAYLOAD, EnumSet.of(INSERTED_KEY, INSERTED_VALUE));
      return copyAndInsertSingleton(mutator, bitpos, key, value);
    }

    @Override // TODO: final
    public AbstractSetMultimapNode<K, V> updatedMultiple(AtomicReference<Thread> mutator,
        K key,
        io.usethesource.capsule.Set.Immutable<V> values, int keyHash, int shift,
        MultimapResult<K, V, io.usethesource.capsule.Set.Immutable<V>> details,
        EqualityComparator<Object> cmp) {
      final int mask = mask(keyHash, shift);
      final int bitpos = bitpos(mask);

      int rawMap1 = this.rawMap1();
      int rawMap2 = this.rawMap2();

      final int collMap = rawMap1 & rawMap2;
      final int dataMap = rawMap2 ^ collMap;
      final int nodeMap = rawMap1 ^ collMap;

      if (isBitInBitmap(dataMap, bitpos)) {
        final int dataIndex = index(dataMap, mask, bitpos);
        final K currentKey = getSingletonKey(dataIndex);

        if (cmp.equals(currentKey, key)) {
          final V currentVal = getSingletonValue(dataIndex);

          // update singleton value
          details.modified(REPLACED_PAYLOAD, EnumSet.of(REPLACED_VALUE));
          return copyAndMigrateFromSingletonToCollection(mutator, bitpos, key, values);
        } else {
          // prefix-collision (case: singleton x collection)
          final V currentVal = getSingletonValue(dataIndex);

          final AbstractSetMultimapNode<K, V> subNodeNew = mergeCollectionAndSingletonPairs(key,
              values, keyHash, currentKey, currentVal,
              transformHashCode(currentKey.hashCode()), shift + BIT_PARTITION_SIZE, cmp);

          details.modified(INSERTED_PAYLOAD, EnumSet.of(INSERTED_KEY, INSERTED_VALUE_COLLECTION));
          return copyAndMigrateFromSingletonToNode(mutator, bitpos, subNodeNew);
        }
      }

      if (isBitInBitmap(collMap, bitpos)) {
        final int collIndex = index(collMap, mask, bitpos);
        final K currentCollKey = getCollectionKey(collIndex);

        if (cmp.equals(currentCollKey, key)) {
          final io.usethesource.capsule.Set.Immutable<V> currentCollVal =
              getCollectionValue(collIndex);

          // migrate from collection to singleton
          details.modified(REPLACED_PAYLOAD, EnumSet.of(REPLACED_VALUE_COLLECTION));
          return copyAndSetCollectionValue(mutator, bitpos, values);
        } else {
          // prefix-collision (case: collection x collection)
          final io.usethesource.capsule.Set.Immutable<V> currentValues = getCollectionValue(
              collIndex);
          final AbstractSetMultimapNode<K, V> subNodeNew = mergeTwoCollectionPairs(
              currentCollKey, currentValues, transformHashCode(currentCollKey.hashCode()), key,
              values, keyHash, shift + BIT_PARTITION_SIZE, cmp);

          details.modified(INSERTED_PAYLOAD, EnumSet.of(INSERTED_KEY, INSERTED_VALUE_COLLECTION));
          return copyAndMigrateFromCollectionToNode(mutator, bitpos, subNodeNew);
        }
      }

      if (isBitInBitmap(nodeMap, bitpos)) {
        final AbstractSetMultimapNode<K, V> subNode = getNode(nodeIndex(bitpos));
        final AbstractSetMultimapNode<K, V> subNodeNew =
            subNode
                .updatedMultiple(mutator, key, values, keyHash, shift + BIT_PARTITION_SIZE, details,
                    cmp);

        if (details.getModificationEffect() != NOTHING) {
          return copyAndSetNode(mutator, bitpos, subNodeNew);
        } else {
          return this;
        }
      }

      // default
      details.modified(INSERTED_PAYLOAD, EnumSet.of(INSERTED_KEY, INSERTED_VALUE_COLLECTION));
      return copyAndInsertCollection(mutator, bitpos, key, values);
    }

    @Override // TODO: final
    public AbstractSetMultimapNode<K, V> removed(final AtomicReference<Thread> mutator,
        final K key,
        final V value, final int keyHash, final int shift,
        final MultimapResult<K, V, io.usethesource.capsule.Set.Immutable<V>> details,
        EqualityComparator<Object> cmp) {
      final int mask = mask(keyHash, shift);
      final int bitpos = bitpos(mask);

      int rawMap1 = this.rawMap1();
      int rawMap2 = this.rawMap2();

      final int collMap = rawMap1 & rawMap2;
      final int dataMap = rawMap2 ^ collMap;
      final int nodeMap = rawMap1 ^ collMap;

      if (isBitInBitmap(dataMap, bitpos)) {
        final int dataIndex = index(dataMap, mask, bitpos);

        final K currentKey = getSingletonKey(dataIndex);
        if (cmp.equals(currentKey, key)) {

          final V currentVal = getSingletonValue(dataIndex);
          if (cmp.equals(currentVal, value)) {

            // remove mapping
            details.modified(REMOVED_PAYLOAD, EnumSet.of(REMOVED_KEY, REMOVED_VALUE),
                io.usethesource.capsule.Set.Immutable.of(currentVal));
            return copyAndRemoveSingleton(mutator, bitpos).canonicalize(mutator, keyHash, shift);

          } else {
            return this;
          }
        } else {
          return this;
        }
      }

      if (isBitInBitmap(collMap, bitpos)) {
        final int collIndex = index(collMap, mask, bitpos);

        final K currentKey = getCollectionKey(collIndex);
        if (cmp.equals(currentKey, key)) {

          final io.usethesource.capsule.Set.Immutable<V> currentValColl =
              getCollectionValue(collIndex);
          if (currentValColl.contains(value)) {

            // remove mapping
            details.modified(REMOVED_PAYLOAD, EnumSet.of(REMOVED_VALUE),
                io.usethesource.capsule.Set.Immutable.of(value));

            final io.usethesource.capsule.Set.Immutable<V> newValColl =
                currentValColl.__remove(value);

            if (newValColl.size() == 1) {
              // TODO: investigate options for unboxing singleton collections
              V remainingVal = newValColl.iterator().next();
              return copyAndMigrateFromCollectionToSingleton(mutator, bitpos, key, remainingVal);
            } else {
              return copyAndSetCollectionValue(mutator, bitpos, newValColl);
            }
          } else {
            return this;
          }
        } else {
          return this;
        }
      }

      if (isBitInBitmap(nodeMap, bitpos)) {
        final AbstractSetMultimapNode<K, V> subNode = getNode(index(nodeMap, mask, bitpos));
        final AbstractSetMultimapNode<K, V> subNodeNew =
            subNode.removed(mutator, key, value, keyHash, shift + BIT_PARTITION_SIZE, details, cmp);

        if (details.getModificationEffect() == NOTHING) {
          return this;
        }

        switch (subNodeNew.sizePredicate()) {
          case 0: {
            throw new IllegalStateException("Sub-node must have at least one element.");
          }
          case 1: {
            if (arity(nodeMap) == 1 && arity(dataMap) == 0 && arity(collMap) == 0) {
              // escalate (singleton or empty) result
              return subNodeNew;
            } else {
              // inline value (move to front)
              EitherSingletonOrCollection.Type type = subNodeNew.typeOfSingleton();

              if (type == EitherSingletonOrCollection.Type.SINGLETON) {
                return copyAndMigrateFromNodeToSingleton(mutator, bitpos, subNodeNew);
              } else {
                return copyAndMigrateFromNodeToCollection(mutator, bitpos, subNodeNew);
              }

            }
          }
          default: {
            // modify current node (set replacement node)
            return copyAndSetNode(mutator, bitpos, subNodeNew);
          }
        }
      }

      // default
      return this;
    }

    @Override // TODO: final
    public AbstractSetMultimapNode<K, V> removed(final AtomicReference<Thread> mutator,
        final K key,
        final int keyHash, final int shift,
        final MultimapResult<K, V, io.usethesource.capsule.Set.Immutable<V>> details,
        EqualityComparator<Object> cmp) {
      final int mask = mask(keyHash, shift);
      final int bitpos = bitpos(mask);

      int rawMap1 = this.rawMap1();
      int rawMap2 = this.rawMap2();

      final int collMap = rawMap1 & rawMap2;
      final int dataMap = rawMap2 ^ collMap;
      final int nodeMap = rawMap1 ^ collMap;

      if (isBitInBitmap(dataMap, bitpos)) {
        final int dataIndex = index(dataMap, mask, bitpos);

        final K currentKey = getSingletonKey(dataIndex);
        if (cmp.equals(currentKey, key)) {

          final V currentVal = getSingletonValue(dataIndex);
          // if (cmp.equals(currentVal, val)) {
          //
          // // remove mapping
          // details.updated(val);
          // return copyAndRemoveSingleton(mutator, bitpos).canonicalize(mutator, keyHash, shift);
          //
          // } else {
          // return this;
          // }

          details.modified(REMOVED_PAYLOAD, EnumSet.of(REMOVED_KEY, REMOVED_VALUE),
              io.usethesource.capsule.Set.Immutable.of(currentVal));
          return copyAndRemoveSingleton(mutator, bitpos).canonicalize(mutator, keyHash, shift);
        } else {
          return this;
        }
      }

      if (isBitInBitmap(collMap, bitpos)) {
        final int collIndex = index(collMap, mask, bitpos);

        final K currentKey = getCollectionKey(collIndex);
        if (cmp.equals(currentKey, key)) {

          final io.usethesource.capsule.Set.Immutable<V> currentValColl =
              getCollectionValue(collIndex);
          // if (currentValColl.contains(val)) {
          //
          // // remove mapping
          // details.updated(val);
          //
          // final Immutable<V> newValColl = currentValColl.__remove(val);
          //
          // if (newValColl.size() == 1) {
          // // TODO: investigate options for unboxing singleton collections
          // V remainingVal = newValColl.iterator().next();
          // return copyAndMigrateFromCollectionToSingleton(mutator, bitpos, key, remainingVal);
          // } else {
          // return copyAndSetCollectionValue(mutator, bitpos, newValColl);
          // }
          // } else {
          // return this;
          // }

          details.modified(REMOVED_PAYLOAD, EnumSet.of(REMOVED_KEY, REMOVED_VALUE_COLLECTION),
              currentValColl);
          return copyAndRemoveCollection(mutator, bitpos).canonicalize(mutator, keyHash, shift);
        } else {
          return this;
        }
      }

      if (isBitInBitmap(nodeMap, bitpos)) {
        final AbstractSetMultimapNode<K, V> subNode = getNode(index(nodeMap, mask, bitpos));
        final AbstractSetMultimapNode<K, V> subNodeNew =
            subNode.removed(mutator, key, keyHash, shift + BIT_PARTITION_SIZE, details, cmp);

        if (details.getModificationEffect() == NOTHING) {
          return this;
        }

        switch (subNodeNew.sizePredicate()) {
          case 0: {
            throw new IllegalStateException("Sub-node must have at least one element.");
          }
          case 1: {
            if (arity(nodeMap) == 1 && arity(dataMap) == 0 && arity(collMap) == 0) {
              // escalate (singleton or empty) result
              return subNodeNew;
            } else {
              // inline value (move to front)
              EitherSingletonOrCollection.Type type = subNodeNew.typeOfSingleton();

              if (type == EitherSingletonOrCollection.Type.SINGLETON) {
                return copyAndMigrateFromNodeToSingleton(mutator, bitpos, subNodeNew);
              } else {
                return copyAndMigrateFromNodeToCollection(mutator, bitpos, subNodeNew);
              }

            }
          }
          default: {
            // modify current node (set replacement node)
            return copyAndSetNode(mutator, bitpos, subNodeNew);
          }
        }
      }

      // default
      return this;
    }

    abstract CompactSetMultimapNode<K, V> canonicalize(AtomicReference<Thread> mutator,
        final int keyHash, final int shift);

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
      int rawMap1 = this.rawMap1();
      int rawMap2 = this.rawMap2();

      final int collMap = rawMap1 & rawMap2;
      final int dataMap = rawMap2 ^ collMap;
      final int nodeMap = rawMap1 ^ collMap;

      final StringBuilder bldr = new StringBuilder();
      bldr.append('[');

      for (byte i = 0; i < arity(dataMap); i++) {
        final byte pos = recoverMask(dataMap, (byte) (i + 1));
//        bldr.append(String.format("@%d<#%d,#%d>", pos, Objects.hashCode(getSingletonKey(i)),
//            Objects.hashCode(getSingletonValue(i))));

        bldr.append(String.format("@%d", pos));

        if (!((i + 1) == arity(dataMap))) {
          bldr.append(", ");
        }
      }

      if (arity(dataMap) > 0 && arity(collMap) > 0) {
        bldr.append(", ");
      }

      for (byte i = 0; i < arity(collMap); i++) {
        final byte pos = recoverMask(collMap, (byte) (i + 1));
//        bldr.append(String.format("@%d<#%d,#%d>", pos, Objects.hashCode(getCollectionKey(i)),
//            Objects.hashCode(getCollectionValue(i))));

        bldr.append(String.format("@%d", pos));

        if (!((i + 1) == arity(collMap))) {
          bldr.append(", ");
        }
      }

      if (arity(collMap) > 0 && arity(nodeMap) > 0) {
        bldr.append(", ");
      }

      for (byte i = 0; i < arity(nodeMap); i++) {
        final byte pos = recoverMask(nodeMap, (byte) (i + 1));
        bldr.append(String.format("@%d: %s", pos, getNode(i)));

        if (!((i + 1) == arity(nodeMap))) {
          bldr.append(", ");
        }
      }

      bldr.append(']');
      return bldr.toString();
    }

  }

  protected static abstract class CompactMixedSetMultimapNode<K, V>
      extends CompactSetMultimapNode<K, V> {

    private final int rawMap1; // former nodeMap
    private final int rawMap2; // former dataMap

    CompactMixedSetMultimapNode(final AtomicReference<Thread> mutator, final int nodeMap,
        final int dataMap) {
      this.rawMap1 = nodeMap;
      this.rawMap2 = dataMap;
    }

    @Override
    public final int rawMap1() { // former nodeMap
      return rawMap1;
    }

    @Override
    public final int rawMap2() { // former dataMap
      return rawMap2;
    }

    @Override
    final int bitmap(int category) {
      switch (category) {
        case 0:
          return dataMap();
        case 1:
          return collMap();
        default:
          return 0;
      }
    }

    @Override
    final int dataMap() {
      return rawMap2() ^ collMap();
    }

    @Override
    final int collMap() {
      return rawMap1() & rawMap2();
    }

    @Override
    final int nodeMap() {
      return rawMap1() ^ collMap();
    }

  }

  private static final class BitmapIndexedSetMultimapNode<K, V>
      extends CompactMixedSetMultimapNode<K, V> {

    final AtomicReference<Thread> mutator;
    final Object[] nodes;

    private BitmapIndexedSetMultimapNode(final AtomicReference<Thread> mutator, final int rawMap1,
        final int rawMap2, final Object[] nodes) {
      super(mutator, rawMap1, rawMap2);

      this.mutator = mutator;
      this.nodes = nodes;

      if (DEBUG) {
        final int collMap = rawMap1 & rawMap2;
        final int dataMap = rawMap2 ^ collMap;
        final int nodeMap = rawMap1 ^ collMap;

        assert (TUPLE_LENGTH * java.lang.Integer.bitCount(dataMap)
            + TUPLE_LENGTH * java.lang.Integer.bitCount(collMap)
            + java.lang.Integer.bitCount(nodeMap) == nodes.length);

        for (int i = 0; i < arity(dataMap); i++) {
          int offset = i * TUPLE_LENGTH;

          assert ((nodes[offset
              + 0] instanceof io.usethesource.capsule.Set.Immutable) == false);
          assert ((nodes[offset
              + 1] instanceof io.usethesource.capsule.Set.Immutable) == false);

          assert ((nodes[offset + 0] instanceof CompactSetMultimapNode) == false);
          assert ((nodes[offset + 1] instanceof CompactSetMultimapNode) == false);
        }

        for (int i = 0; i < arity(collMap); i++) {
          int offset = (i + arity(dataMap)) * TUPLE_LENGTH;

          assert ((nodes[offset
              + 0] instanceof io.usethesource.capsule.Set.Immutable) == false);
          assert ((nodes[offset
              + 1] instanceof io.usethesource.capsule.Set.Immutable) == true);

          assert ((nodes[offset + 0] instanceof CompactSetMultimapNode) == false);
          assert ((nodes[offset + 1] instanceof CompactSetMultimapNode) == false);
        }

        for (int i = 0; i < arity(nodeMap); i++) {
          int offset = (arity(dataMap) + arity(collMap)) * TUPLE_LENGTH;

          assert ((nodes[offset
              + i] instanceof io.usethesource.capsule.Set.Immutable) == false);

          assert ((nodes[offset + i] instanceof CompactSetMultimapNode) == true);
        }
      }

      assert nodeInvariant();
    }

    @Override
    public ArrayView<AbstractSetMultimapNode<K, V>> nodeArray() {
      return new ArrayView<AbstractSetMultimapNode<K, V>>() {
        @Override
        public int size() {
          return BitmapIndexedSetMultimapNode.this.nodeArity();
        }

        @Override
        public AbstractSetMultimapNode<K, V> get(int index) {
          return (AbstractSetMultimapNode<K, V>) BitmapIndexedSetMultimapNode.this.getNode(index);
        }

        /**
         * TODO: replace with {{@link #set(int, AbstractSetMultimapNode, AtomicReference)}}
         */
        @Override
        public void set(int index, AbstractSetMultimapNode<K, V> item) {
//          if (!isAllowedToEdit(BitmapIndexedSetMultimapNode.this.mutator, writeCapabilityToken)) {
//            throw new IllegalStateException();
//          }

          nodes[nodes.length - 1 - index] = item;
        }

        @Override
        public void set(int index, AbstractSetMultimapNode<K, V> item,
            AtomicReference<?> writeCapabilityToken) {
          if (!isAllowedToEdit(BitmapIndexedSetMultimapNode.this.mutator, writeCapabilityToken)) {
            throw new IllegalStateException();
          }

          nodes[nodes.length - 1 - index] = item;
        }
      };
    }

    @Override
    K getSingletonKey(final int index) {
      return (K) nodes[TUPLE_LENGTH * index];
    }

    @Override
    V getSingletonValue(int index) {
      return (V) nodes[TUPLE_LENGTH * index + 1];
    }

    @Override
    K getCollectionKey(int index) {
      // TODO: improve on offset calculation (caching it, etc)
      int offset = TUPLE_LENGTH * (arity(dataMap()) + index);
      return (K) nodes[offset];
    }

    @Override
    io.usethesource.capsule.Set.Immutable<V> getCollectionValue(final int index) {
      // TODO: improve on offset calculation (caching it, etc)
      int offset = TUPLE_LENGTH * (arity(dataMap()) + index) + 1;
      return (io.usethesource.capsule.Set.Immutable<V>) nodes[offset];
    }

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
    boolean hasPayload(EitherSingletonOrCollection.Type type) {
      return payloadArity(type) != 0;
    }

    @Override
    int payloadArity(EitherSingletonOrCollection.Type type) {
      if (type == Type.SINGLETON) {
        return java.lang.Integer.bitCount(dataMap());
      } else {
        return java.lang.Integer.bitCount(collMap());
      }
    }

    @Override
    boolean hasNodes() {
      return nodeArity() != 0;
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
    PersistentTrieSet.AbstractSetNode<K> toSetNode(final AtomicReference<Thread> mutator) {
      final int mergedPayloadMap = rawMap2();

      final ArrayView<K> dataArray0 = dataArray(0, 0);
      final ArrayView<K> dataArray1 = dataArray(1, 0);

      final Iterator<K> iterator = ziperator(arity(mergedPayloadMap), bitmap(0),
          dataArray0.iterator(), bitmap(1), dataArray1.iterator());

      // allocate an array that can hold the keys + empty placeholder slots for sub-nodes
      final Object[] content = new Object[arity(mergedPayloadMap) + arity(nodeMap())];

      for (int i = 0; iterator.hasNext(); i++) {
        content[i] = iterator.next();
      }

      return PersistentTrieSet.AbstractSetNode.newBitmapIndexedNode(mutator, nodeMap(),
          mergedPayloadMap, content);
    }

    private <T> Iterator<T> ziperator(final int expectedSize, final int bitmap0,
        final Iterator<T> dataIterator0, final int bitmap1, final Iterator<T> dataIterator1) {
      return new Iterator<T>() {

        private int encounteredSize = 0;
        private int bitsToSkip = 0;

        @Override
        public boolean hasNext() {
          return encounteredSize < expectedSize;
        }

        @Override
        public T next() {
          // assume that operation succeeds
          encounteredSize += 1;

          int trailingZeroCount0 = Integer.numberOfTrailingZeros(bitmap0 >> bitsToSkip);
          int trailingZeroCount1 = Integer.numberOfTrailingZeros(bitmap1 >> bitsToSkip);

          bitsToSkip = bitsToSkip + 1 + Math.min(trailingZeroCount0, trailingZeroCount1);

          if (trailingZeroCount0 < trailingZeroCount1) {
            return dataIterator0.next();
          } else {
            return dataIterator1.next();
          }
        }
      };
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
      result = prime * result + (rawMap1());
      result = prime * result + (rawMap2());
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
      if (rawMap1() != that.rawMap1()) {
        return false;
      }
      if (rawMap2() != that.rawMap2()) {
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
        switch (arity(dataMap()) + arity(collMap())) {
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
    CompactSetMultimapNode<K, V> copyAndSetSingletonValue(final AtomicReference<Thread> mutator,
        final int bitpos, final V val) {

      final int idx = TUPLE_LENGTH * dataIndex(bitpos) + 1;
      final CompactSetMultimapNode<K, V> updatedNode = copyAndSetXxxValue(mutator, idx, val);

      return updatedNode;
    }

    @Override
    CompactSetMultimapNode<K, V> copyAndSetCollectionValue(final AtomicReference<Thread> mutator,
        final int bitpos, final io.usethesource.capsule.Set.Immutable<V> valColl) {

      final int idx = TUPLE_LENGTH * (arity(dataMap()) + collIndex(bitpos)) + 1;
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

        updatedNode = nodeOf(mutator, rawMap1(), rawMap2(), dst);
      }
      return updatedNode;
    }

    @Override
    CompactSetMultimapNode<K, V> copyAndSetNode(final AtomicReference<Thread> mutator,
        final int bitpos, final AbstractSetMultimapNode<K, V> node) {

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

        return nodeOf(mutator, rawMap1(), rawMap2(), dst);
      }
    }

    @Override
    CompactSetMultimapNode<K, V> copyAndInsertSingleton(final AtomicReference<Thread> mutator,
        final int bitpos, final K key, final V val) {
      final int idx = TUPLE_LENGTH * dataIndex(bitpos);

      final Object[] src = this.nodes;
      final Object[] dst = new Object[src.length + 2];

      // copy 'src' and insert 2 element(s) at position 'idx'
      System.arraycopy(src, 0, dst, 0, idx);
      dst[idx + 0] = key;
      dst[idx + 1] = val;
      System.arraycopy(src, idx, dst, idx + 2, src.length - idx);

      return nodeOf(mutator, rawMap1(), rawMap2() | bitpos, dst);
    }

    @Override
    CompactSetMultimapNode<K, V> copyAndInsertCollection(final AtomicReference<Thread> mutator,
        final int bitpos, final K key, final io.usethesource.capsule.Set.Immutable<V> valColl) {
      final int idx = TUPLE_LENGTH * (arity(dataMap()) + collIndex(bitpos));

      final Object[] src = this.nodes;
      final Object[] dst = new Object[src.length + 2];

      // copy 'src' and insert 2 element(s) at position 'idx'
      System.arraycopy(src, 0, dst, 0, idx);
      dst[idx + 0] = key;
      dst[idx + 1] = valColl;
      System.arraycopy(src, idx, dst, idx + 2, src.length - idx);

      return nodeOf(mutator, rawMap1() | bitpos, rawMap2() | bitpos, dst);
    }

    @Override
    CompactSetMultimapNode<K, V> copyAndMigrateFromSingletonToCollection(
        AtomicReference<Thread> mutator, int bitpos, K key,
        io.usethesource.capsule.Set.Immutable<V> valColl) {

      final int idxOld = TUPLE_LENGTH * index(dataMap(), bitpos);
      final int idxNew = TUPLE_LENGTH * (arity(dataMap()) - 1 + index(collMap(), bitpos));

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

      return nodeOf(mutator, rawMap1() | bitpos, rawMap2() | bitpos, dst);
    }

    @Override
    CompactSetMultimapNode<K, V> copyAndMigrateFromCollectionToSingleton(
        AtomicReference<Thread> mutator, int bitpos, K key, V val) {

      // TODO: does not support src == dst yet for shifting

      final int idxOld = TUPLE_LENGTH * (arity(dataMap()) + index(collMap(), bitpos));
      final int idxNew = TUPLE_LENGTH * index(dataMap(), bitpos);

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

      return nodeOf(mutator, rawMap1() ^ bitpos, rawMap2() | bitpos, dst);
    }

    @Override
    CompactSetMultimapNode<K, V> copyAndRemoveSingleton(final AtomicReference<Thread> mutator,
        final int bitpos) {
      final int idx = TUPLE_LENGTH * dataIndex(bitpos);

      final Object[] src = this.nodes;
      final Object[] dst = new Object[src.length - 2];

      // copy 'src' and remove 2 element(s) at position 'idx'
      System.arraycopy(src, 0, dst, 0, idx);
      System.arraycopy(src, idx + 2, dst, idx, src.length - idx - 2);

      return nodeOf(mutator, rawMap1(), rawMap2() ^ bitpos, dst);
    }

    @Override
    CompactSetMultimapNode<K, V> copyAndRemoveCollection(final AtomicReference<Thread> mutator,
        final int bitpos) {
      final int idx = TUPLE_LENGTH * (arity(dataMap()) + collIndex(bitpos));

      final Object[] src = this.nodes;
      final Object[] dst = new Object[src.length - 2];

      // copy 'src' and remove 2 element(s) at position 'idx'
      System.arraycopy(src, 0, dst, 0, idx);
      System.arraycopy(src, idx + 2, dst, idx, src.length - idx - 2);

      return nodeOf(mutator, rawMap1() ^ bitpos, rawMap2() ^ bitpos, dst);
    }

    @Override
    CompactSetMultimapNode<K, V> copyAndMigrateFromSingletonToNode(
        final AtomicReference<Thread> mutator, final int bitpos,
        final AbstractSetMultimapNode<K, V> node) {

      final int idxOld = TUPLE_LENGTH * dataIndex(bitpos);
      final int idxNew = this.nodes.length - TUPLE_LENGTH - nodeIndex(bitpos);

      final Object[] dst = copyAndMigrateFromXxxToNode(idxOld, idxNew, node);

      return nodeOf(mutator, rawMap1() | bitpos, rawMap2() ^ bitpos, dst);
    }

    @Override
    CompactSetMultimapNode<K, V> copyAndMigrateFromCollectionToNode(
        AtomicReference<Thread> mutator,
        int bitpos, AbstractSetMultimapNode<K, V> node) {

      final int idxOld = TUPLE_LENGTH * (arity(dataMap()) + index(collMap(), bitpos));
      final int idxNew = this.nodes.length - TUPLE_LENGTH - nodeIndex(bitpos);

      final Object[] dst = copyAndMigrateFromXxxToNode(idxOld, idxNew, node);

      return nodeOf(mutator, rawMap1() | bitpos, rawMap2() ^ bitpos, dst);
    }

    private Object[] copyAndMigrateFromXxxToNode(final int idxOld, final int idxNew,
        final AbstractSetMultimapNode<K, V> node) {

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
        final AtomicReference<Thread> mutator, final int bitpos,
        final AbstractSetMultimapNode<K, V> node) {

      final int idxOld = this.nodes.length - 1 - nodeIndex(bitpos);
      final int idxNew = TUPLE_LENGTH * dataIndex(bitpos);

      Object keyToInline = node.getSingletonKey(0);
      Object valToInline = node.getSingletonValue(0);

      final Object[] dst = copyAndMigrateFromNodeToXxx(idxOld, idxNew, keyToInline, valToInline);

      return nodeOf(mutator, rawMap1() ^ bitpos, rawMap2() | bitpos, dst);
    }

    @Override
    CompactSetMultimapNode<K, V> copyAndMigrateFromNodeToCollection(
        AtomicReference<Thread> mutator,
        int bitpos, AbstractSetMultimapNode<K, V> node) {

      final int idxOld = this.nodes.length - 1 - nodeIndex(bitpos);
      final int idxNew = TUPLE_LENGTH * (arity(dataMap()) + index(collMap(), bitpos));

      Object keyToInline = node.getCollectionKey(0);
      Object valToInline = node.getCollectionValue(0);

      final Object[] dst = copyAndMigrateFromNodeToXxx(idxOld, idxNew, keyToInline, valToInline);

      return nodeOf(mutator, rawMap1() | bitpos, rawMap2() | bitpos, dst);

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

    public CompactSetMultimapNode<K, V> copyAndUpdateBitmaps(AtomicReference<Thread> mutator,
        final int rawMap1, final int rawMap2) {
      return nodeOf(mutator, rawMap1, rawMap2, nodes);
    }

    @Override
    public EitherSingletonOrCollection.Type typeOfSingleton() {
      assert this.sizePredicate() == SIZE_ONE;

      int rawMap1 = rawMap1();
      int rawMap2 = rawMap2();

      if (rawMap1 != rawMap2) {
        return EitherSingletonOrCollection.Type.SINGLETON;
      } else {
        return EitherSingletonOrCollection.Type.COLLECTION;
      }
    }

    @Override
    CompactSetMultimapNode<K, V> canonicalize(AtomicReference<Thread> mutator, final int keyHash,
        final int shift) {
      if (shift > 0) {
        int rawMap1 = rawMap1();
        int rawMap2 = rawMap2();

        boolean slotCountEqualsTupleLength = nodes.length == TUPLE_LENGTH;
        boolean containsPaylaod = rawMap2 != 0;

        if (slotCountEqualsTupleLength && containsPaylaod) {
          int newBitmap = bitpos(mask(keyHash, 0));

          if (rawMap1 != rawMap2) {
            // is data payload
            return copyAndUpdateBitmaps(mutator, 0, newBitmap);
          } else {
            // is coll payload
            return copyAndUpdateBitmaps(mutator, newBitmap, newBitmap);
          }
        }
      }

      // default
      return this;
    }

  }

  private static abstract class AbstractHashCollisionNode<K, V>
      extends CompactSetMultimapNode<K, V> {

    static final <K, V, VS extends io.usethesource.capsule.Set.Immutable<V>> AbstractHashCollisionNode<K, V> of(
        final int hash, final K key0, final VS valColl0, final K key1, final VS valColl1) {
      return new HashCollisionNode<>(hash, key0, valColl0, key1, valColl1);
    }

    private static final RuntimeException UOE_BOILERPLATE = new UnsupportedOperationException(
        "TODO: CompactSetMultimapNode -> AbstractSetMultimapNode");

    private static final Supplier<RuntimeException> UOE_FACTORY =
        () -> new UnsupportedOperationException(
            "TODO: CompactSetMultimapNode -> AbstractSetMultimapNode");

    @Override
    int bitmap(int category) {
      throw UOE_FACTORY.get();
    }

    @Override
    int dataMap() {
      throw UOE_FACTORY.get();
    }

    @Override
    int collMap() {
      throw UOE_FACTORY.get();
    }

    @Override
    int nodeMap() {
      throw UOE_FACTORY.get();
    }

    @Override
    int rawMap1() {
      throw UOE_FACTORY.get();
    }

    @Override
    int rawMap2() {
      throw UOE_FACTORY.get();
    }

    public CompactSetMultimapNode<K, V> copyAndUpdateBitmaps(AtomicReference<Thread> mutator,
        int rawMap1,
        int rawMap2) {
      throw UOE_FACTORY.get();
    }

    @Override
    CompactSetMultimapNode<K, V> copyAndSetSingletonValue(AtomicReference<Thread> mutator,
        int bitpos, V val) {
      throw UOE_FACTORY.get();
    }

    @Override
    CompactSetMultimapNode<K, V> copyAndSetCollectionValue(AtomicReference<Thread> mutator,
        int bitpos, io.usethesource.capsule.Set.Immutable<V> valColl) {
      throw UOE_FACTORY.get();
    }

    @Override
    CompactSetMultimapNode<K, V> copyAndSetNode(AtomicReference<Thread> mutator, int bitpos,
        AbstractSetMultimapNode<K, V> node) {
      throw UOE_FACTORY.get();
    }

    @Override
    CompactSetMultimapNode<K, V> copyAndInsertSingleton(AtomicReference<Thread> mutator,
        int bitpos,
        K key, V val) {
      throw UOE_FACTORY.get();
    }

    @Override
    CompactSetMultimapNode<K, V> copyAndInsertCollection(AtomicReference<Thread> mutator,
        int bitpos,
        K key, io.usethesource.capsule.Set.Immutable<V> values) {
      throw UOE_FACTORY.get();
    }


    @Override
    CompactSetMultimapNode<K, V> copyAndMigrateFromSingletonToCollection(
        AtomicReference<Thread> mutator, int bitpos, K key,
        io.usethesource.capsule.Set.Immutable<V> valColl) {
      throw UOE_FACTORY.get();
    }

    @Override
    CompactSetMultimapNode<K, V> copyAndRemoveSingleton(AtomicReference<Thread> mutator,
        int bitpos) {
      throw UOE_FACTORY.get();
    }

    @Override
    CompactSetMultimapNode<K, V> copyAndRemoveCollection(AtomicReference<Thread> mutator,
        int bitpos) {
      throw UOE_FACTORY.get();
    }

    @Override
    CompactSetMultimapNode<K, V> copyAndMigrateFromSingletonToNode(
        AtomicReference<Thread> mutator,
        int bitpos, AbstractSetMultimapNode<K, V> node) {
      throw UOE_FACTORY.get();
    }

    @Override
    CompactSetMultimapNode<K, V> copyAndMigrateFromNodeToSingleton(
        AtomicReference<Thread> mutator,
        int bitpos, AbstractSetMultimapNode<K, V> node) {
      throw UOE_FACTORY.get();
    }

    @Override
    CompactSetMultimapNode<K, V> copyAndMigrateFromCollectionToNode(
        AtomicReference<Thread> mutator,
        int bitpos, AbstractSetMultimapNode<K, V> node) {
      throw UOE_FACTORY.get();
    }

    @Override
    CompactSetMultimapNode<K, V> copyAndMigrateFromNodeToCollection(
        AtomicReference<Thread> mutator,
        int bitpos, AbstractSetMultimapNode<K, V> node) {
      throw UOE_FACTORY.get();
    }

    @Override
    CompactSetMultimapNode<K, V> copyAndMigrateFromCollectionToSingleton(
        AtomicReference<Thread> mutator, int bitpos, K key, V val) {
      throw UOE_FACTORY.get();
    }

    @Override
    public Type typeOfSingleton() {
      throw UOE_FACTORY.get();
    }

    @Override
    CompactSetMultimapNode<K, V> canonicalize(AtomicReference<Thread> mutator, int keyHash,
        int shift) {
      throw UOE_FACTORY.get();
    }
  }

  private static final class HashCollisionNode<K, V> extends AbstractHashCollisionNode<K, V> {

    private final int hash;
    private final List<Map.Entry<K, io.usethesource.capsule.Set.Immutable<V>>> collisionContent;

    HashCollisionNode(final int hash, final K key0,
        final io.usethesource.capsule.Set.Immutable<V> valColl0, final K key1,
        final io.usethesource.capsule.Set.Immutable<V> valColl1) {
      this(hash, Arrays.asList(entryOf(key0, valColl0), entryOf(key1, valColl1)));
    }

    HashCollisionNode(final int hash,
        final List<Map.Entry<K, io.usethesource.capsule.Set.Immutable<V>>> collisionContent) {
      this.hash = hash;
      this.collisionContent = collisionContent;
    }

    @Override
    public ArrayView<AbstractSetMultimapNode<K, V>> nodeArray() {
      return ArrayView.empty();
    }

    private static final RuntimeException UOE = new UnsupportedOperationException();

    private static final Supplier<RuntimeException> UOE_NOT_YET_IMPLEMENTED_FACTORY =
        () -> new UnsupportedOperationException("Not yet implemented @ HashCollisionNode.");

    @Override
    PersistentTrieSet.AbstractSetNode<K> toSetNode(AtomicReference<Thread> mutator) {
      // is leaf; ignore mutator
      return PersistentTrieSet.AbstractSetNode.newHashCollisonNode(hash,
          (K[]) collisionContent.stream().map(Map.Entry::getKey).toArray());
    }

    // @Override
    // PersistentTrieSet.AbstractSetNode<K> toSetNode(PersistentTrieSet.AbstractSetNode<K>[] newChildren) {
    // // is leaf; ignore newChildren
    // return PersistentTrieSet.AbstractSetNode.newHashCollisonNode(hash,
    // (K[]) collisionContent.stream().map(Map.Entry::getKey).toArray());
    // }

    @Override
    public byte sizePredicate() {
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
    boolean hasPayload(Type type) {
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
    int payloadArity(Type type) {
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
    io.usethesource.capsule.Set.Immutable<V> getCollectionValue(int index) {
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
    public boolean containsKey(K key, int keyHash, int shift,
        EqualityComparator<Object> cmp) {
      return collisionContent.stream().filter(entry -> cmp.equals(key, entry.getKey())).findAny()
          .isPresent();
    }

    @Override
    public boolean containsTuple(K key, V value, int keyHash, int shift,
        EqualityComparator<Object> cmp) {
      return collisionContent.stream()
          .filter(entry -> cmp.equals(key, entry.getKey())
              && entry.getValue().containsEquivalent(value, cmp.toComparator()))
          .findAny().isPresent();
    }

    @Override
    public final Optional<io.usethesource.capsule.Set.Immutable<V>> findByKey(K key, int keyHash,
        int shift, EqualityComparator<Object> cmp) {
      return collisionContent.stream().filter(entry -> cmp.equals(key, entry.getKey())).findAny()
          .map(Map.Entry::getValue);
    }

    @Override
    public AbstractSetMultimapNode<K, V> insertedSingle(AtomicReference<Thread> mutator, K key,
        V value,
        int keyHash, int shift,
        MultimapResult<K, V, io.usethesource.capsule.Set.Immutable<V>> details,
        EqualityComparator<Object> cmp) {
      Optional<Map.Entry<K, io.usethesource.capsule.Set.Immutable<V>>> optionalTuple =
          collisionContent.stream().filter(entry -> cmp.equals(key, entry.getKey())).findAny();

      if (optionalTuple.isPresent()) {
        // contains key

        io.usethesource.capsule.Set.Immutable<V> values =
            optionalTuple.get().getValue();

        if (values.containsEquivalent(value, cmp.toComparator())) {
          // contains key and value
          // // details.unchanged();
          return this;

        } else {
          // contains key but not value

          Function<Map.Entry<K, io.usethesource.capsule.Set.Immutable<V>>, Map.Entry<K, io.usethesource.capsule.Set.Immutable<V>>> substitutionMapper =
              (kImmutableSetEntry) -> {
                if (kImmutableSetEntry == optionalTuple.get()) {
                  io.usethesource.capsule.Set.Immutable<V> updatedValues =
                      values.__insertEquivalent(value, cmp.toComparator());
                  return entryOf(key, updatedValues);
                } else {
                  return kImmutableSetEntry;
                }
              };

          List<Map.Entry<K, io.usethesource.capsule.Set.Immutable<V>>> updatedCollisionContent =
              collisionContent.stream().map(substitutionMapper).collect(Collectors.toList());

          // TODO not all API uses EqualityComparator
          // TODO does not check that remainder is unmodified
          assert updatedCollisionContent.size() == collisionContent.size();
          assert updatedCollisionContent.contains(optionalTuple.get()) == false;
          // assert updatedCollisionContent.contains(entryOf(key, values.__insertEquivalent(val,
          // cmp.toComparator())));
          assert updatedCollisionContent.stream()
              .filter(entry -> cmp.equals(key, entry.getKey())
                  && entry.getValue().containsEquivalent(value, cmp.toComparator()))
              .findAny().isPresent();

          details.modified(INSERTED_PAYLOAD, EnumSet.of(INSERTED_VALUE));
          return new HashCollisionNode<K, V>(hash, updatedCollisionContent);
        }
      } else {
        // does not contain key

        Stream.Builder<Map.Entry<K, io.usethesource.capsule.Set.Immutable<V>>> builder =
            Stream.<Map.Entry<K, io.usethesource.capsule.Set.Immutable<V>>>builder()
                .add(entryOf(key, io.usethesource.capsule.Set.Immutable.of(value)));

        collisionContent.forEach(builder::accept);

        List<Map.Entry<K, io.usethesource.capsule.Set.Immutable<V>>> updatedCollisionContent =
            builder.build().collect(Collectors.toList());

        // TODO not all API uses EqualityComparator
        assert updatedCollisionContent.size() == collisionContent.size() + 1;
        assert updatedCollisionContent.containsAll(collisionContent);
        // assert updatedCollisionContent.contains(entryOf(key, setOf(val)));
        assert updatedCollisionContent.stream().filter(entry -> cmp.equals(key, entry.getKey())
            && Objects.equals(io.usethesource.capsule.Set.Immutable.of(value), entry.getValue()))
            .findAny()
            .isPresent();

        details.modified(INSERTED_PAYLOAD, EnumSet.of(INSERTED_KEY, INSERTED_VALUE));
        return new HashCollisionNode<K, V>(hash, updatedCollisionContent);
      }
    }

    @Override
    public AbstractSetMultimapNode<K, V> updatedSingle(AtomicReference<Thread> mutator, K key,
        V value,
        int keyHash,
        int shift, MultimapResult<K, V, io.usethesource.capsule.Set.Immutable<V>> details,
        EqualityComparator<Object> cmp) {
      Optional<Map.Entry<K, io.usethesource.capsule.Set.Immutable<V>>> optionalTuple =
          collisionContent.stream().filter(entry -> cmp.equals(key, entry.getKey())).findAny();

      if (optionalTuple.isPresent()) {
        // contains key -> replace val anyways

        io.usethesource.capsule.Set.Immutable<V> values =
            optionalTuple.get().getValue();

        Function<Map.Entry<K, io.usethesource.capsule.Set.Immutable<V>>, Map.Entry<K, io.usethesource.capsule.Set.Immutable<V>>> substitutionMapper =
            (kImmutableSetEntry) -> {
              if (kImmutableSetEntry == optionalTuple.get()) {
                io.usethesource.capsule.Set.Immutable<V> updatedValues =
                    values.__insertEquivalent(value, cmp.toComparator());
                return entryOf(key, updatedValues);
              } else {
                return kImmutableSetEntry;
              }
            };

        List<Map.Entry<K, io.usethesource.capsule.Set.Immutable<V>>> updatedCollisionContent =
            collisionContent.stream().map(substitutionMapper).collect(Collectors.toList());

        if (values.size() == 1) {
          details.modified(REPLACED_PAYLOAD, EnumSet.of(REPLACED_VALUE), values);
        } else {
          details.modified(REPLACED_PAYLOAD, EnumSet.of(REPLACED_VALUE_COLLECTION), values);
        }

        return new HashCollisionNode<K, V>(hash, updatedCollisionContent);
      } else {
        // does not contain key

        Stream.Builder<Map.Entry<K, io.usethesource.capsule.Set.Immutable<V>>> builder =
            Stream.<Map.Entry<K, io.usethesource.capsule.Set.Immutable<V>>>builder()
                .add(entryOf(key, io.usethesource.capsule.Set.Immutable.of(value)));

        collisionContent.forEach(builder::accept);

        List<Map.Entry<K, io.usethesource.capsule.Set.Immutable<V>>> updatedCollisionContent =
            builder.build().collect(Collectors.toList());

        details.modified(INSERTED_PAYLOAD, EnumSet.of(INSERTED_KEY, INSERTED_VALUE));
        return new HashCollisionNode<K, V>(hash, updatedCollisionContent);
      }
    }

    @Override
    public AbstractSetMultimapNode<K, V> removed(AtomicReference<Thread> mutator, K key, V value,
        int keyHash,
        int shift, MultimapResult<K, V, io.usethesource.capsule.Set.Immutable<V>> details,
        EqualityComparator<Object> cmp) {
      Optional<Map.Entry<K, io.usethesource.capsule.Set.Immutable<V>>> optionalTuple =
          collisionContent.stream().filter(entry -> cmp.equals(key, entry.getKey())).findAny();

      if (optionalTuple.isPresent()) {
        // contains key

        io.usethesource.capsule.Set.Immutable<V> values =
            optionalTuple.get().getValue();

        if (values.containsEquivalent(value, cmp.toComparator())) {
          // contains key and value -> remove mapping

          final List<Map.Entry<K, io.usethesource.capsule.Set.Immutable<V>>> updatedCollisionContent;

          if (values.size() == 1) {
            updatedCollisionContent = collisionContent.stream()
                .filter(kImmutableSetEntry -> kImmutableSetEntry != optionalTuple.get())
                .collect(Collectors.toList());

            details.modified(REMOVED_PAYLOAD, EnumSet.of(REMOVED_KEY, REMOVED_VALUE));
            return new HashCollisionNode<K, V>(hash, updatedCollisionContent);
          } else {
            Function<Map.Entry<K, io.usethesource.capsule.Set.Immutable<V>>, Map.Entry<K, io.usethesource.capsule.Set.Immutable<V>>> substitutionMapper =
                (kImmutableSetEntry) -> {
                  if (kImmutableSetEntry == optionalTuple.get()) {
                    io.usethesource.capsule.Set.Immutable<V> updatedValues =
                        values.__removeEquivalent(value, cmp.toComparator());
                    return entryOf(key, updatedValues);
                  } else {
                    return kImmutableSetEntry;
                  }
                };

            updatedCollisionContent =
                collisionContent.stream().map(substitutionMapper).collect(Collectors.toList());

            details.modified(REMOVED_PAYLOAD, EnumSet.of(REMOVED_VALUE));
            return new HashCollisionNode<K, V>(hash, updatedCollisionContent);
          }
        }
      }

      // details.unchanged();
      return this;
    }

    public AbstractSetMultimapNode<K, V> removed(AtomicReference<Thread> mutator, K key,
        int keyHash,
        int shift, SetMultimapResult<K, V> details, EqualityComparator<Object> cmp) {
      final Optional<Map.Entry<K, io.usethesource.capsule.Set.Immutable<V>>> optionalTuple =
          collisionContent.stream().filter(entry -> cmp.equals(key, entry.getKey())).findAny();

      if (optionalTuple.isPresent()) {
        // contains key

        io.usethesource.capsule.Set.Immutable<V> values =
            optionalTuple.get().getValue();

        final List<Map.Entry<K, io.usethesource.capsule.Set.Immutable<V>>> updatedCollisionContent;

        updatedCollisionContent = collisionContent.stream()
            .filter(kImmutableSetEntry -> kImmutableSetEntry != optionalTuple.get())
            .collect(Collectors.toList());

        if (values.size() == 1) {
          details.modified(REMOVED_PAYLOAD, EnumSet.of(REMOVED_KEY, REMOVED_VALUE), values);
          return new HashCollisionNode<K, V>(hash, updatedCollisionContent);
        } else {
          details
              .modified(REMOVED_PAYLOAD, EnumSet.of(REMOVED_KEY, REMOVED_VALUE_COLLECTION), values);
          return new HashCollisionNode<K, V>(hash, updatedCollisionContent);
        }
      }

      // details.unchanged();
      return this;
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

    AbstractSetMultimapNode<K, V>[] nodes = new AbstractSetMultimapNode[MAX_DEPTH];

    AbstractSetMultimapIterator(AbstractSetMultimapNode<K, V> rootNode) {
      if (rootNode.hasNodes()) {
        currentStackLevel = 0;

        nodes[0] = rootNode;
        nodeCursorsAndLengths[0] = 0;
        nodeCursorsAndLengths[1] = rootNode.nodeArity();
      }

      if (rootNode.hasPayload(SINGLETON) || rootNode.hasPayload(COLLECTION)) {
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

          if (nextNode.hasPayload(SINGLETON) || nextNode.hasPayload(COLLECTION)) {
            /*
             * found next node that contains values
             */
            currentValueNode = nextNode;
            currentValueSingletonCursor = 0;
            currentValueSingletonLength = nextNode.payloadArity(SINGLETON);
            currentValueCollectionCursor = 0;
            currentValueCollectionLength = nextNode.payloadArity(COLLECTION);
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
      implements Iterator<io.usethesource.capsule.Set.Immutable<V>> {

    SetMultimapValueIterator(AbstractSetMultimapNode<K, V> rootNode) {
      super(rootNode);
    }

    @Override
    public io.usethesource.capsule.Set.Immutable<V> next() {
      if (!hasNext()) {
        throw new NoSuchElementException();
      } else {
        // TODO: check case distinction
        if (currentValueSingletonCursor < currentValueSingletonLength) {
          return io.usethesource.capsule.Set.Immutable
              .of(currentValueNode.getSingletonValue(currentValueSingletonCursor++));
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

          return entryOf(currentKey, currentValue);
        } else {
          final K currentKey = currentValueNode.getCollectionKey(currentValueCollectionCursor);
          final Object currentValue =
              currentValueNode.getCollectionValue(currentValueCollectionCursor);
          currentValueCollectionCursor++;

          return entryOf(currentKey, currentValue);
        }
      }
    }

  }

  protected static class SetMultimapTupleIterator<K, V, T> extends
      AbstractSetMultimapIterator<K, V>
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

  static final class TransientTrieSetMultimap<K, V>
      implements SetMultimap.Transient<K, V> {

    private final EqualityComparator<Object> cmp;
    private final AtomicReference<Thread> mutator;

    private AbstractSetMultimapNode<K, V> rootNode;
    // private int cachedHashCode;
    private int cachedSize;

    private int cachedKeySetHashCode;
    private int cachedKeySetSize;

    TransientTrieSetMultimap(
        PersistentTrieSetMultimap<K, V> trieSetMultimap) {
      this.cmp = trieSetMultimap.cmp;
      this.mutator = new AtomicReference<Thread>(Thread.currentThread());
      this.rootNode = trieSetMultimap.rootNode;
      // // this.cachedHashCode = trieSetMultimap.cachedHashCode;
      this.cachedSize = trieSetMultimap.cachedSize;
      this.cachedKeySetHashCode = trieSetMultimap.cachedKeySetHashCode;
      this.cachedKeySetSize = trieSetMultimap.cachedKeySetSize;
      if (DEBUG) {
        // assert checkHashCodeAndSize(cachedHashCode, cachedSize, entryIterator());
        assert checkKeySetHashCodeAndSize(cachedKeySetHashCode, cachedKeySetSize, keyIterator());
      }
    }

    @Override
    public boolean containsKey(final Object o) {
      try {
        final K key = (K) o;
        return rootNode.containsKey(key, transformHashCode(key.hashCode()), 0, cmp);
      } catch (ClassCastException unused) {
        return false;
      }
    }

    @Override
    public boolean containsValue(final Object o) {
      for (Iterator<V> iterator = valueIterator(); iterator.hasNext(); ) {
        if (cmp.equals(iterator.next(), o)) {
          return true;
        }
      }
      return false;
    }

    @Override
    public boolean containsEntry(final Object o0, final Object o1) {
      try {
        final K key = (K) o0;
        final V val = (V) o1;
        final Optional<io.usethesource.capsule.Set.Immutable<V>> result =
            rootNode.findByKey(key, transformHashCode(key.hashCode()), 0, cmp);

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
    public io.usethesource.capsule.Set.Immutable<V> get(final Object o) {
      try {
        final K key = (K) o;
        final Optional<io.usethesource.capsule.Set.Immutable<V>> result =
            rootNode.findByKey(key, transformHashCode(key.hashCode()), 0, cmp);

        return result.orElse(io.usethesource.capsule.Set.Immutable.of());
      } catch (ClassCastException unused) {
        return io.usethesource.capsule.Set.Immutable.of();
      }
    }

    @Override
    public final boolean __put(K key, V value) {
      return __put(key, AbstractSpecialisedImmutableSet.setOf(value));
    }

    @Override
    public final boolean __put(K key, io.usethesource.capsule.Set.Immutable<V> values) {
      if (mutator.get() == null) {
        throw new IllegalStateException("Transient already frozen.");
      }

      if (values.isEmpty()) {
        return __remove(key);
      }

      final int keyHash = key.hashCode();
      final SetMultimapResult<K, V> details = SetMultimapResult.unchanged();

//      final AbstractSetMultimapNode<K, V> newRootNode;
//
//      if (values.size() == 1) {
//        final V value = values.findFirst().get();
//        newRootNode = rootNode
//            .updatedSingle(mutator, key, value, transformHashCode(keyHash), 0, details, cmp);
//      } else {
//        newRootNode = rootNode
//            .updatedMultiple(mutator, key, values, transformHashCode(keyHash), 0, details, cmp);
//      }

      final AbstractSetMultimapNode<K, V> newRootNode =
          rootNode.updated(mutator, key, values, transformHashCode(keyHash), 0, details, cmp);

      switch (details.getModificationEffect()) {
        case REPLACED_PAYLOAD: {
          // int hashCodeDeltaOld = tupleHash(keyHash, details.getEvictedPayload());
          // int hashCodeDeltaNew = tupleHash(keyHash, values);

          // this.cachedHashCode = cachedHashCode + hashCodeDeltaNew - hashCodeDeltaOld;
          this.cachedSize = cachedSize - details.getEvictedPayload().size() + values.size();
          this.cachedKeySetHashCode = cachedKeySetHashCode;
          this.cachedKeySetSize = cachedKeySetSize;

          this.rootNode = newRootNode;
          return true;
        }

        case INSERTED_PAYLOAD: {
          assert details.getModificationDetails().contains(INSERTED_KEY);

          // int hashCodeDeltaNew = tupleHash(keyHash, values);

          // this.cachedHashCode = cachedHashCode + hashCodeDeltaNew;
          this.cachedSize = cachedSize + values.size();
          this.cachedKeySetHashCode = cachedKeySetHashCode + keyHash;
          this.cachedKeySetSize = cachedKeySetSize + 1;

          this.rootNode = newRootNode;
          return true;
        }

        default: {
          return false;
        }
      }
    }

    @Override
    public final boolean __insert(final K key, final V value) {
      return __insert(key, AbstractSpecialisedImmutableSet.setOf(value));
    }

    /*
     * TODO: erroneous -> does not communicate back how many values were inserted (necessary for updating size count)
     */
    @Override
    public final boolean __insert(K key, io.usethesource.capsule.Set.Immutable<V> values) {
      if (mutator.get() == null) {
        throw new IllegalStateException("Transient already frozen.");
      }

      if (values.isEmpty()) {
        return false;
      }

      final int keyHash = key.hashCode();
      final SetMultimapResult<K, V> details = SetMultimapResult.unchanged();

//      final AbstractSetMultimapNode<K, V> newRootNode;
//
//      if (values.size() == 1) {
//        final V value = values.findFirst().get();
//        newRootNode = rootNode
//            .insertedSingle(mutator, key, value, transformHashCode(keyHash), 0, details, cmp);
//      } else {
//        newRootNode = rootNode
//            .insertedMultiple(mutator, key, values, transformHashCode(keyHash), 0, details, cmp);
//      }

      final AbstractSetMultimapNode<K, V> newRootNode = rootNode
          .inserted(mutator, key, values, transformHashCode(keyHash), 0, details, cmp);

      switch (details.getModificationEffect()) {
        case INSERTED_PAYLOAD: {
          // int hashCodeDeltaNew = tupleHash(keyHash, values);

          // this.cachedHashCode = cachedHashCode + hashCodeDeltaNew;
          this.cachedSize = cachedSize + details.sizeDelta().get();
          this.cachedKeySetHashCode = cachedKeySetHashCode;
          this.cachedKeySetSize = cachedKeySetSize;

          if (details.getModificationDetails().contains(INSERTED_KEY)) {
            this.cachedKeySetHashCode += keyHash;
            this.cachedKeySetSize += 1;
          }

          this.rootNode = newRootNode;
          return true;
        }

        default: {
          return false;
        }
      }
    }

    @Override
    public boolean union(final SetMultimap<? extends K, ? extends V> setMultimap) {
      boolean modified = false;

      for (Map.Entry<? extends K, ? extends V> entry : setMultimap.entrySet()) {
        modified |= this.__insert(entry.getKey(), entry.getValue());
      }

      return modified;
    }

    @Override
    public final boolean __remove(final K key, final V value) {
      if (mutator.get() == null) {
        throw new IllegalStateException("Transient already frozen.");
      }

      final int keyHash = key.hashCode();
      final SetMultimapResult<K, V> details = SetMultimapResult.unchanged();

      final AbstractSetMultimapNode<K, V> newRootNode =
          rootNode.removed(mutator, key, value, transformHashCode(keyHash), 0, details, cmp);

      switch (details.getModificationEffect()) {
        case REMOVED_PAYLOAD: {
          // int hashCodeDeltaOld = tupleHash(keyHash, value); // TODO: support collection

          // this.cachedHashCode = cachedHashCode - hashCodeDeltaOld;
          this.cachedSize = cachedSize - 1; // TODO: support collection
          this.cachedKeySetHashCode = cachedKeySetHashCode;
          this.cachedKeySetSize = cachedKeySetSize;

          if (details.getModificationDetails().contains(REMOVED_KEY)) {
            this.cachedKeySetHashCode -= keyHash;
            this.cachedKeySetSize -= 1;
          }

          this.rootNode = newRootNode;
          return true;
        }

        default: {
          return false;
        }
      }
    }

    @Override
    public final boolean __remove(K key) {
      if (mutator.get() == null) {
        throw new IllegalStateException("Transient already frozen.");
      }

      final int keyHash = key.hashCode();
      final SetMultimapResult<K, V> details = SetMultimapResult.unchanged();

      final AbstractSetMultimapNode<K, V> newRootNode =
          rootNode.removed(mutator, key, transformHashCode(keyHash), 0, details, cmp);

      switch (details.getModificationEffect()) {
        case REMOVED_PAYLOAD: {
          assert details.getModificationDetails().contains(REMOVED_KEY);

          // int hashCodeDeltaOld = tupleHash(keyHash, details.getEvictedPayload());

          // this.cachedHashCode = cachedHashCode - hashCodeDeltaOld;
          this.cachedSize = cachedSize - details.getEvictedPayload().size();
          this.cachedKeySetHashCode = cachedKeySetHashCode - keyHash;
          this.cachedKeySetSize = cachedKeySetSize - 1;

          this.rootNode = newRootNode;
          return true;
        }

        default: {
          return false;
        }
      }
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

    private Spliterator<io.usethesource.capsule.Set.Immutable<V>> valueCollectionsSpliterator() {
      /*
       * TODO: specialize between mutable / SetMultimap.Immutable<K, V> ({@see
       * Spliterator.IMMUTABLE})
       */
      int characteristics = Spliterator.NONNULL | Spliterator.SIZED | Spliterator.SUBSIZED;
      return Spliterators.spliterator(new SetMultimapValueIterator<>(rootNode), size(),
          characteristics);
    }

    private Stream<io.usethesource.capsule.Set.Immutable<V>> valueCollectionsStream() {
      boolean isParallel = false;
      return StreamSupport.stream(valueCollectionsSpliterator(), isParallel);
    }

    public static class TransientSetMultimapKeyIterator<K, V> extends
        SetMultimapKeyIterator<K, V> {

      final TransientTrieSetMultimap<K, V> collection;
      K lastKey;

      public TransientSetMultimapKeyIterator(
          final TransientTrieSetMultimap<K, V> collection) {
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

      public TransientSetMultimapValueIterator(
          final TransientTrieSetMultimap<K, V> collection) {
        super(collection.rootNode);
        this.collection = collection;
      }

      @Override
      public io.usethesource.capsule.Set.Immutable<V> next() {
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

      public TransientSetMultimapTupleIterator(
          final TransientTrieSetMultimap<K, V> collection,
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
        collection.__remove(currentKey, currentValue);
      }
    }

    @Override
    public Set<K> keySet() {
      Set<K> keySet = null;

      if (keySet == null) {
        keySet = new AbstractSet<K>() {
          @Override
          public Iterator<K> iterator() {
            return TransientTrieSetMultimap.this.keyIterator();
          }

          @Override
          public int size() {
            return TransientTrieSetMultimap.this.sizeDistinct();
          }

          @Override
          public boolean isEmpty() {
            return TransientTrieSetMultimap.this.isEmpty();
          }

          @Override
          public void clear() {
            throw new UnsupportedOperationException();
          }

          @Override
          public boolean contains(Object k) {
            return TransientTrieSetMultimap.this.containsKey(k);
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
            return TransientTrieSetMultimap.this.valueIterator();
          }

          @Override
          public int size() {
            return TransientTrieSetMultimap.this.size();
          }

          @Override
          public boolean isEmpty() {
            return TransientTrieSetMultimap.this.isEmpty();
          }

          @Override
          public void clear() {
            throw new UnsupportedOperationException();
          }

          @Override
          public boolean contains(Object v) {
            return TransientTrieSetMultimap.this.containsValue(v);
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
            return TransientTrieSetMultimap.this.size();
          }

          @Override
          public boolean isEmpty() {
            return TransientTrieSetMultimap.this.isEmpty();
          }

          @Override
          public void clear() {
            throw new UnsupportedOperationException();
          }

          @Override
          public boolean contains(Object k) {
            return TransientTrieSetMultimap.this.containsKey(k);
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

      if (other instanceof PersistentTrieSetMultimap.TransientTrieSetMultimap) {
        TransientTrieSetMultimap<?, ?> that =
            (TransientTrieSetMultimap<?, ?>) other;

        if (this.cachedSize != that.cachedSize) {
          return false;
        }

//        if (this.cachedHashCode != that.cachedHashCode) {
//          return false;
//        }

        if (this.cachedKeySetSize != that.cachedKeySetSize) {
          return false;
        }

        if (this.cachedKeySetHashCode != that.cachedKeySetHashCode) {
          return false;
        }

        return rootNode.equals(that.rootNode);
      } else if (other instanceof SetMultimap) {
        SetMultimap that = (SetMultimap) other;

        if (this.size() != that.size()) {
          return false;
        }

        for (
            Iterator<Map.Entry> it = that.entrySet().iterator(); it.hasNext(); ) {
          Map.Entry entry = it.next();

          try {
            final K key = (K) entry.getKey();
            final Optional<io.usethesource.capsule.Set.Immutable<V>> result =
                rootNode.findByKey(key, transformHashCode(key.hashCode()), 0, cmp);

            if (!result.isPresent()) {
              return false;
            } else {
              final io.usethesource.capsule.Set.Immutable<V> valColl =
                  (io.usethesource.capsule.Set.Immutable<V>) entry.getValue();

              if (!cmp.equals(result.get(), valColl)) {
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
//    return cachedHashCode;
      return PersistentTrieSetMultimap.hashCode(rootNode);
    }

    @Override
    public SetMultimap.Immutable<K, V> freeze() {
      if (mutator.get() == null) {
        throw new IllegalStateException("Transient already frozen.");
      }

      mutator.set(null);
      return new PersistentTrieSetMultimap<K, V>(cmp, rootNode, cachedSize,
          cachedKeySetHashCode, cachedKeySetSize);
    }
  }

}