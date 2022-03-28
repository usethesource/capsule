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
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import io.usethesource.capsule.Set;
import io.usethesource.capsule.SetMultimap;
import io.usethesource.capsule.core.trie.ArrayView;
import io.usethesource.capsule.core.trie.MultimapNode;
import io.usethesource.capsule.core.trie.Node;
import io.usethesource.capsule.util.collection.AbstractSpecialisedImmutableMap;

import static io.usethesource.capsule.util.collection.AbstractSpecialisedImmutableMap.entryOf;

/*
 * TODO: remove public modifier
 */
public abstract class AbstractTrieSetMultimap<K, V, C extends Iterable<V>, R extends MultimapNode<K, V, C, R>>
    implements SetMultimap<K, V>, java.io.Serializable {

  private static final long serialVersionUID = 42L;

  public AbstractTrieSetMultimap() {
  }

  abstract R getRootNode();

  abstract int getCachedSize();

  abstract int getCachedKeySetHashCode();

  abstract int getCachedKeySetSize();

  protected abstract Set.Immutable<V> valueToTemporaryBox(V value);

  protected abstract C collectionToInternalFormat(Set.Immutable<V> valueCollection);

  protected abstract Set.Immutable<V> internalFormatToCollection(C values);

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

  public static final int transformHashCode(final int hash) {
    return hash;
  }

  protected static <K, V, C extends Iterable<V>, R extends MultimapNode<K, V, C, R>> int hashCode(
      R rootNode) {
    int hash = 0;

    final Iterator<Entry<K, V>> it = new SetMultimapTupleIterator<>(rootNode,
        AbstractSpecialisedImmutableMap::entryOf);

    while (it.hasNext()) {
      final java.util.Map.Entry<K, V> entry = it.next();
      final K key = entry.getKey();
      final V val = entry.getValue();

      hash += key.hashCode() ^ val.hashCode();
    }

    return hash;
  }

  protected static <K, V, C extends Iterable<V>, R extends MultimapNode<K, V, C, R>> int size(
      R rootNode) {
    int size = 0;

    final Iterator<Entry<K, V>> it = new SetMultimapTupleIterator<>(rootNode,
        AbstractSpecialisedImmutableMap::entryOf);

    while (it.hasNext()) {
      it.next();
      size += 1;
    }

    return size;
  }

  protected static <K, V, C extends Iterable<V>, R extends MultimapNode<K, V, C, R>> int keySetHashCode(
      R rootNode) {
    int hash = 0;

    final Iterator<K> it = new SetMultimapKeyIterator<>(rootNode);

    while (it.hasNext()) {
      final K key = it.next();
      hash += key.hashCode();
    }

    return hash;
  }

  protected static <K, V, C extends Iterable<V>, R extends MultimapNode<K, V, C, R>> int keySetSize(
      R rootNode) {
    int size = 0;

    final Iterator<K> it = new SetMultimapKeyIterator<>(rootNode);

    while (it.hasNext()) {
      it.next();
      size += 1;
    }

    return size;
  }

  private static final <K, V> boolean checkHashCodeAndSize(final int targetHash,
      final int targetSize, final Iterator<java.util.Map.Entry<K, V>> iterator) {
    int hash = 0;
    int size = 0;

    while (iterator.hasNext()) {
      final java.util.Map.Entry<K, V> entry = iterator.next();
      final K key = entry.getKey();
      final V val = entry.getValue();

      hash += key.hashCode() ^ val.hashCode();
      size += 1;
    }

    return hash == targetHash && size == targetSize;
  }

  private static final <K> boolean checkKeySetHashCodeAndSize(final int targetHash,
      final int targetSize, final Iterator<K> iterator) {
    int hash = 0;
    int size = 0;

    while (iterator.hasNext()) {
      final K key = iterator.next();

      hash += key.hashCode();
      size += 1;
    }

    return hash == targetHash && size == targetSize;
  }

  /*
   * For analysis purposes only.
   */
  protected Iterator<R> nodeIterator() {
    return new TrieSetMultimap_BleedingEdgeNodeIterator<>(getRootNode());
  }

  /*
   * For analysis purposes only.
   */

  protected int getNodeCount() {
    final Iterator<R> it = nodeIterator();
    int sumNodes = 0;

    for (; it.hasNext(); it.next()) {
      sumNodes += 1;
    }

    return sumNodes;
  }


  @Override
  public final boolean containsKey(final Object o) {
    try {
      final K key = (K) o;
      return getRootNode().containsKey(key, transformHashCode(key.hashCode()), 0);
    } catch (ClassCastException unused) {
      return false;
    }
  }

  @Override
  public final boolean containsValue(final Object o) {
    for (Iterator<V> iterator = valueIterator(); iterator.hasNext(); ) {
      if (Objects.equals(iterator.next(), o)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public final boolean containsEntry(final Object o0, final Object o1) {
    try {
      final K key = (K) o0;
      final V val = (V) o1;
      return getRootNode().containsTuple(key, val, transformHashCode(key.hashCode()), 0);
    } catch (ClassCastException unused) {
      return false;
    }
  }

  @Override
  public final Set.Immutable<V> get(final Object o) {
    try {
      final K key = (K) o;
      final Optional<C> values =
          getRootNode().findByKey(key, transformHashCode(key.hashCode()), 0);

      if (values.isPresent()) {
        return internalFormatToCollection(values.get());
      } else {
        return Set.Immutable.of();
      }
    } catch (ClassCastException unused) {
      return Set.Immutable.of();
    }
  }

  @Override
  public Iterator<K> keyIterator() {
    return new SetMultimapKeyIterator<>(getRootNode());
  }

  @Override
  public abstract Iterator<V> valueIterator();

  protected Iterator<V> valueIterator(final Function<V, C> converter) {
    return valueCollectionsStream(converter)
        .flatMap(values -> StreamSupport.stream(values.spliterator(), false)).iterator();
  }

  @Override
  public Iterator<java.util.Map.Entry<K, V>> entryIterator() {
    return new SetMultimapTupleIterator<>(getRootNode(), AbstractSpecialisedImmutableMap::entryOf);
  }

  @Override
  public Iterator<java.util.Map.Entry<K, Object>> nativeEntryIterator() {
    return new SetMultimapNativeTupleIterator<>(getRootNode());
  }

  @Override
  public <T> Iterator<T> tupleIterator(final BiFunction<K, V, T> tupleOf) {
    return new SetMultimapTupleIterator<>(getRootNode(), tupleOf);
  }

  private Spliterator<C> valueCollectionsSpliterator(
      final Function<V, C> converter) {
    /*
     * TODO: specialize between mutable / SetMultimap.Immutable<K, V> ({@see Spliterator.IMMUTABLE})
     */
    int characteristics = Spliterator.NONNULL | Spliterator.SIZED | Spliterator.SUBSIZED;
    return Spliterators.spliterator(new SetMultimapValueIterator<>(getRootNode(),
        converter), size(), characteristics);
  }

  private Stream<C> valueCollectionsStream(final Function<V, C> converter) {
    boolean isParallel = false;
    return StreamSupport.stream(valueCollectionsSpliterator(converter), isParallel);
  }

  @Override
  public final java.util.Set<K> keySet() {
    java.util.Set<K> keySet = null;

    if (keySet == null) {
      keySet = new AbstractSet<K>() {
        @Override
        public Iterator<K> iterator() {
          return AbstractTrieSetMultimap.this.keyIterator();
        }

        @Override
        public int size() {
          return AbstractTrieSetMultimap.this.sizeDistinct();
        }

        @Override
        public boolean isEmpty() {
          return AbstractTrieSetMultimap.this.isEmpty();
        }

        @Override
        public void clear() {
          throw new UnsupportedOperationException();
        }

        @Override
        public boolean contains(Object k) {
          return AbstractTrieSetMultimap.this.containsKey(k);
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
  public final Collection<V> values() {
    Collection<V> values = null;

    if (values == null) {
      values = new AbstractCollection<V>() {
        @Override
        public Iterator<V> iterator() {
          return AbstractTrieSetMultimap.this.valueIterator();
        }

        @Override
        public int size() {
          return AbstractTrieSetMultimap.this.size();
        }

        @Override
        public boolean isEmpty() {
          return AbstractTrieSetMultimap.this.isEmpty();
        }

        @Override
        public void clear() {
          throw new UnsupportedOperationException();
        }

        @Override
        public boolean contains(Object v) {
          return AbstractTrieSetMultimap.this.containsValue(v);
        }
      };
    }

    return values;
  }

  @Override
  public final java.util.Set<java.util.Map.Entry<K, V>> entrySet() {
    java.util.Set<java.util.Map.Entry<K, V>> entrySet = null;

    if (entrySet == null) {
      entrySet = new AbstractSet<java.util.Map.Entry<K, V>>() {
        @Override
        public Iterator<java.util.Map.Entry<K, V>> iterator() {
          return new Iterator<java.util.Map.Entry<K, V>>() {
            private final Iterator<java.util.Map.Entry<K, V>> i = entryIterator();

            @Override
            public boolean hasNext() {
              return i.hasNext();
            }

            @Override
            public java.util.Map.Entry<K, V> next() {
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
          return AbstractTrieSetMultimap.this.size();
        }

        @Override
        public boolean isEmpty() {
          return AbstractTrieSetMultimap.this.isEmpty();
        }

        @Override
        public void clear() {
          throw new UnsupportedOperationException();
        }

        @Override
        public boolean contains(Object k) {
          return AbstractTrieSetMultimap.this.containsKey(k);
        }
      };
    }

    return entrySet;
  }

  @Override
  public int hashCode() {
    return hashCode(getRootNode());
  }

  @Override
  public boolean equals(final Object other) {
    if (other == this) {
      return true;
    }
    if (other == null) {
      return false;
    }

    if (this.getClass() == other.getClass()) {
      AbstractTrieSetMultimap<?, ?, ?, ?> that = (AbstractTrieSetMultimap<?, ?, ?, ?>) other;

      if (this.getCachedSize() != that.getCachedSize()) {
        return false;
      }

      if (this.getCachedKeySetSize() != that.getCachedKeySetSize()) {
        return false;
      }

      if (this.getCachedKeySetHashCode() != that.getCachedKeySetHashCode()) {
        return false;
      }

      return Objects.equals(this.getRootNode(), that.getRootNode());
    } else if (other instanceof SetMultimap) {
      SetMultimap that = (SetMultimap) other;

      if (this.size() != that.size()) {
        return false;
      }

      for (Iterator<java.util.Map.Entry> it = that.entrySet().iterator(); it.hasNext(); ) {
        final java.util.Map.Entry entry = it.next();

        try {
          final K key = (K) entry.getKey();
          final V value = (V) entry.getValue();

          boolean containsTuple =
              getRootNode().containsTuple(key, value, transformHashCode(key.hashCode()), 0);

          if (!containsTuple) {
            return false;
          }
        } catch (ClassCastException unused) {
          return false;
        }
      }

      return true;
    }

    return false;
  }

  /**
   * Iterator skeleton that uses a fixed stack in depth.
   */
  private static abstract class AbstractSetMultimapIterator<K, V, C, R extends MultimapNode<K, V, C, R>> {

    private static final int MAX_DEPTH = 7;

    protected int currentValueSingletonCursor;
    protected int currentValueSingletonLength;
    protected int currentValueCollectionCursor;
    protected int currentValueCollectionLength;
    protected Node currentValueNode;

    private int currentStackLevel = -1;
    private final int[] nodeCursorsAndLengths = new int[MAX_DEPTH * 2];

    Node[] nodes = new Node[MAX_DEPTH];

    AbstractSetMultimapIterator(R rootNode) {
      ArrayView<? extends Node> subNodes = rootNode.nodeArray();

      if (!subNodes.isEmpty()) {
        currentStackLevel = 0;

        nodes[0] = rootNode;
        nodeCursorsAndLengths[0] = 0;
        nodeCursorsAndLengths[1] = subNodes.size();
      }

      // TODO: introduce dataArray(category) without specifying component
      ArrayView<?> singletonPayload = rootNode.dataArray(0, 0);
      ArrayView<?> collectionPayload = rootNode.dataArray(1, 0);

      if (!singletonPayload.isEmpty() || !collectionPayload.isEmpty()) {
        currentValueNode = rootNode;
        currentValueSingletonCursor = 0;
        currentValueSingletonLength = singletonPayload.size();
        currentValueCollectionCursor = 0;
        currentValueCollectionLength = collectionPayload.size();
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
          final Node nextNode = nodes[currentStackLevel].nodeArray().get(nodeCursor);
          nodeCursorsAndLengths[currentCursorIndex]++;

          ArrayView<? extends Node> subNodes = nextNode.nodeArray();

          if (!subNodes.isEmpty()) {
            /*
             * put node on next stack level for depth-first traversal
             */
            final int nextStackLevel = ++currentStackLevel;
            final int nextCursorIndex = nextStackLevel * 2;
            final int nextLengthIndex = nextCursorIndex + 1;

            nodes[nextStackLevel] = nextNode;
            nodeCursorsAndLengths[nextCursorIndex] = 0;
            nodeCursorsAndLengths[nextLengthIndex] = subNodes.size();
          }

          // TODO: introduce dataArray(category) without specifying component
          ArrayView<?> singletonPayload = nextNode.dataArray(0, 0);
          ArrayView<?> collectionPayload = nextNode.dataArray(1, 0);

          if (!singletonPayload.isEmpty() || !collectionPayload.isEmpty()) {
            /*
             * found next node that contains values
             */
            currentValueNode = nextNode;
            currentValueSingletonCursor = 0;
            currentValueSingletonLength = singletonPayload.size();
            currentValueCollectionCursor = 0;
            currentValueCollectionLength = collectionPayload.size();
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

  protected static class SetMultimapKeyIterator<K, V, C, R extends MultimapNode<K, V, C, R>>
      extends AbstractSetMultimapIterator<K, V, C, R> implements Iterator<K> {

    SetMultimapKeyIterator(R rootNode) {
      super(rootNode);
    }

    @Override
    public K next() {
      if (!hasNext()) {
        throw new NoSuchElementException();
      } else {
        // TODO: check case distinction
        if (currentValueSingletonCursor < currentValueSingletonLength) {
          // return currentValueNode.getSingletonKey(currentValueSingletonCursor++);
          return (K) currentValueNode.dataArray(0, 0).get(currentValueSingletonCursor++);
        } else {
          // return currentValueNode.getCollectionKey(currentValueCollectionCursor++);
          return (K) currentValueNode.dataArray(1, 0).get(currentValueCollectionCursor++);
        }
      }
    }

  }

  protected static class SetMultimapValueIterator<K, V, C, R extends MultimapNode<K, V, C, R>> extends
      AbstractSetMultimapIterator<K, V, C, R>
      implements Iterator<C> {

    final Function<V, C> converter;

    public SetMultimapValueIterator(final R rootNode, final Function<V, C> converter) {
      super(rootNode);
      this.converter = converter;
    }

    @Override
    public C next() {
      if (!hasNext()) {
        throw new NoSuchElementException();
      } else {
        // TODO: check case distinction
        if (currentValueSingletonCursor < currentValueSingletonLength) {
//          return io.usethesource.capsule.Set.Immutable
//              .of(currentValueNode.getSingletonValue(currentValueSingletonCursor++));
          final V value = (V) currentValueNode.dataArray(0, 1).get(currentValueSingletonCursor++);
          return converter.apply(value);
        } else {
          return (C) currentValueNode.dataArray(1, 1).get(currentValueCollectionCursor++);
        }
      }
    }

  }

  protected static class SetMultimapNativeTupleIterator<K, V, C, R extends MultimapNode<K, V, C, R>>
      extends AbstractSetMultimapIterator<K, V, C, R>
      implements Iterator<java.util.Map.Entry<K, Object>> {

    protected SetMultimapNativeTupleIterator(R rootNode) {
      super(rootNode);
    }

    @Override
    public java.util.Map.Entry<K, Object> next() {
      if (!hasNext()) {
        throw new NoSuchElementException();
      } else {
        // TODO: check case distinction
        if (currentValueSingletonCursor < currentValueSingletonLength) {
//          final K currentKey = currentValueNode.getSingletonKey(currentValueSingletonCursor);
//          final Object currentValue =
//              currentValueNode.getSingletonValue(currentValueSingletonCursor);

          final K currentKey = (K) currentValueNode.dataArray(0, 0)
              .get(currentValueSingletonCursor);
          final Object currentValue = currentValueNode.dataArray(0, 1)
              .get(currentValueSingletonCursor);

          currentValueSingletonCursor++;

          return entryOf(currentKey, currentValue);
        } else {
//          final K currentKey = currentValueNode.getCollectionKey(currentValueCollectionCursor);
//          final Object currentValue =
//              currentValueNode.getCollectionValue(currentValueCollectionCursor);

          final K currentKey = (K) currentValueNode.dataArray(1, 0)
              .get(currentValueCollectionCursor);
          final Object currentValue = currentValueNode.dataArray(1, 1)
              .get(currentValueCollectionCursor);

          currentValueCollectionCursor++;

          return entryOf(currentKey, currentValue);
        }
      }
    }

  }

  protected static class SetMultimapTupleIterator<K, V, C extends Iterable<V>, R extends MultimapNode<K, V, C, R>, T>
      extends AbstractSetMultimapIterator<K, V, C, R> implements Iterator<T> {

    final BiFunction<K, V, T> tupleOf;

    K currentKey = null;
    V currentValue = null;
    Iterator<V> currentSetIterator = Collections.emptyIterator();

    protected SetMultimapTupleIterator(R rootNode, final BiFunction<K, V, T> tupleOf) {
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
//            currentKey = currentValueNode.getSingletonKey(currentValueSingletonCursor);
//            currentSetIterator = Collections
//                .singleton(currentValueNode.getSingletonValue(currentValueSingletonCursor))
//                .iterator();

            currentKey = (K) currentValueNode.dataArray(0, 0).get(currentValueSingletonCursor);

            currentSetIterator = Collections
                .singleton((V) currentValueNode.dataArray(0, 1).get(currentValueSingletonCursor))
                .iterator();

            currentValueSingletonCursor++;
          } else {
//            currentKey = currentValueNode.getCollectionKey(currentValueCollectionCursor);
//            currentSetIterator =
//                currentValueNode.getCollectionValue(currentValueCollectionCursor).iterator();

            currentKey = (K) currentValueNode.dataArray(1, 0).get(currentValueCollectionCursor);

            currentSetIterator = ((C) currentValueNode.dataArray(1, 1)
                .get(currentValueCollectionCursor)).iterator();

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
  private static class TrieSetMultimap_BleedingEdgeNodeIterator<K, V, C, R extends MultimapNode<K, V, C, R>>
      implements Iterator<R> {

    final Deque<Iterator<? extends R>> nodeIteratorStack;

    TrieSetMultimap_BleedingEdgeNodeIterator(R rootNode) {
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
    public R next() {
      if (!hasNext()) {
        throw new NoSuchElementException();
      }

      R innerNode = nodeIteratorStack.peek().next();

      ArrayView<? extends Node> subNodes = innerNode.nodeArray();

      if (!subNodes.isEmpty()) {
        nodeIteratorStack.push((Iterator<? extends R>) subNodes.iterator());
      }

      return innerNode;
    }

    @Override
    public void remove() {
      throw new UnsupportedOperationException();
    }
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

}
