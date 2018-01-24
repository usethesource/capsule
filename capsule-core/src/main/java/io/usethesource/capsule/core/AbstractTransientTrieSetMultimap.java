/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.capsule.core;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiFunction;
import java.util.function.Function;

import io.usethesource.capsule.Set;
import io.usethesource.capsule.SetMultimap;
import io.usethesource.capsule.core.trie.MultimapNode;
import io.usethesource.capsule.core.trie.MultimapResult;
import io.usethesource.capsule.util.EqualityComparator;

import static io.usethesource.capsule.core.trie.MultimapResult.Modification.INSERTED_KEY;
import static io.usethesource.capsule.core.trie.MultimapResult.Modification.REMOVED_KEY;

/*
 * TODO: remove public modifier
 */
public abstract class AbstractTransientTrieSetMultimap<K, V, C extends Iterable<V>, R extends MultimapNode<K, V, C, R>>
    extends AbstractTrieSetMultimap<K, V, C, R> implements SetMultimap.Transient<K, V> {

  protected static final boolean DEBUG = false;

  protected final AtomicReference<Thread> mutator;

  protected R rootNode;
  // private int cachedHashCode;
  protected int cachedSize;

  protected int cachedKeySetHashCode;
  protected int cachedKeySetSize;


  protected AbstractTransientTrieSetMultimap(
      AbstractPersistentTrieSetMultimap<K, V, C, R> trieSetMultimap) {
    this.mutator = new AtomicReference<Thread>(Thread.currentThread());
    this.rootNode = trieSetMultimap.rootNode;
    // // this.cachedHashCode = trieSetMultimap.cachedHashCode;
    this.cachedSize = trieSetMultimap.cachedSize;
    this.cachedKeySetHashCode = trieSetMultimap.cachedKeySetHashCode;
    this.cachedKeySetSize = trieSetMultimap.cachedKeySetSize;

    assertPropertiesCorrectness();
  }

  private void assertPropertiesCorrectness() {
    if (DEBUG) {
      assert cachedSize == size(rootNode);
      assert cachedKeySetHashCode == keySetHashCode(rootNode);
      assert cachedKeySetSize == keySetSize(rootNode);
    }
  }

  @Override
  final R getRootNode() {
    return rootNode;
  }

  @Override
  final int getCachedSize() {
    return cachedSize;
  }

  @Override
  final int getCachedKeySetHashCode() {
    return cachedKeySetHashCode;
  }

  @Override
  final int getCachedKeySetSize() {
    return cachedKeySetSize;
  }

  @Override
  public final boolean __insert(final K key, final V value) {
    return __insertEquivalent(key, valueToTemporaryBox(value), Object::equals);
  }

  @Override
  public final boolean __insertEquivalent(final K key, final V value,
      final EqualityComparator<Object> cmp) {
    return __insertEquivalent(key, valueToTemporaryBox(value), cmp);
  }

  @Override
  public final boolean __insertEquivalent(K key,
      io.usethesource.capsule.Set.Immutable<V> valueCollection,
      final EqualityComparator<Object> cmp) {
    if (mutator.get() == null) {
      throw new IllegalStateException("Transient already frozen.");
    }

    if (valueCollection.isEmpty()) {
      return false;
    }

    final int keyHash = key.hashCode();
    final MultimapResult<K, V, C> details = MultimapResult.unchanged();

    final C values = collectionToInternalFormat(valueCollection);
    final R newRootNode = rootNode
        .inserted(mutator, key, values, transformHashCode(keyHash), 0, details, cmp);

    switch (details.getModificationEffect()) {
      case NOTHING: {
        return false;
      }

      case INSERTED_PAYLOAD: {
        // int hashCodeDeltaNew = tupleHash(keyHash, values);
        // this.cachedHashCode = cachedHashCode + hashCodeDeltaNew;

        this.cachedSize += details.sizeDelta().get();
        this.cachedKeySetHashCode = cachedKeySetHashCode;
        this.cachedKeySetSize = cachedKeySetSize;

        if (details.getModificationDetails().contains(INSERTED_KEY)) {
          this.cachedKeySetHashCode += keyHash;
          this.cachedKeySetSize += 1;
        }

        this.rootNode = newRootNode;
        assertPropertiesCorrectness();
        return true;
      }

      default: {
        throw new IllegalStateException("Unhandled modification effect.");
      }
    }
  }

  @Override
  public final boolean __put(K key, V value) {
    return __put(key, valueToTemporaryBox(value));
  }

  @Override
  public final boolean __putEquivalent(K key, V value, final EqualityComparator<Object> cmp) {
    return __putEquivalent(key, valueToTemporaryBox(value), cmp);
  }

  @Override
  public final boolean __putEquivalent(K key, io.usethesource.capsule.Set.Immutable<V> valueCollection, final EqualityComparator<Object> cmp) {
    if (mutator.get() == null) {
      throw new IllegalStateException("Transient already frozen.");
    }

    if (valueCollection.isEmpty()) {
      return __remove(key);
    }

    final int keyHash = key.hashCode();
    final MultimapResult<K, V, C> details = MultimapResult.unchanged();

    final C values = collectionToInternalFormat(valueCollection);
    final R newRootNode =
        rootNode.updated(mutator, key, values, transformHashCode(keyHash), 0, details, cmp);

    switch (details.getModificationEffect()) {
      case NOTHING: {
        return false;
      }

      case REPLACED_PAYLOAD: {
        // int hashCodeDeltaOld = tupleHash(keyHash, details.getEvictedPayload());
        // int hashCodeDeltaNew = tupleHash(keyHash, values);
        // this.cachedHashCode = cachedHashCode + hashCodeDeltaNew - hashCodeDeltaOld;

        Set.Immutable<V> evictedValueCollection =
            internalFormatToCollection(details.getEvictedPayload().get());

        this.cachedSize = cachedSize - evictedValueCollection.size() + valueCollection.size();
        this.cachedKeySetHashCode = cachedKeySetHashCode;
        this.cachedKeySetSize = cachedKeySetSize;

        this.rootNode = newRootNode;
        assertPropertiesCorrectness();
        return true;
      }

      case INSERTED_PAYLOAD: {
        assert details.getModificationDetails().contains(INSERTED_KEY);

        // int hashCodeDeltaNew = tupleHash(keyHash, values);
        // this.cachedHashCode = cachedHashCode + hashCodeDeltaNew;

        this.cachedSize = cachedSize + valueCollection.size();
        this.cachedKeySetHashCode = cachedKeySetHashCode + keyHash;
        this.cachedKeySetSize = cachedKeySetSize + 1;

        this.rootNode = newRootNode;
        assertPropertiesCorrectness();
        return true;
      }

      default: {
        throw new IllegalStateException("Unhandled modification effect.");
      }
    }
  }

  @Override
  public final boolean __remove(final K key, final V value) {
    return __removeEquivalent(key, value, Object::equals);
  }

  @Override
  public final boolean __removeEquivalent(final K key, final V value, final EqualityComparator<Object> cmp) {
    if (mutator.get() == null) {
      throw new IllegalStateException("Transient already frozen.");
    }

    final int keyHash = key.hashCode();
    final MultimapResult<K, V, C> details = MultimapResult.unchanged();

    final R newRootNode =
        rootNode.removed(mutator, key, value, transformHashCode(keyHash), 0, details, cmp);

    switch (details.getModificationEffect()) {
      case NOTHING: {
        return false;
      }

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
        assertPropertiesCorrectness();
        return true;
      }

      default: {
        throw new IllegalStateException("Unhandled modification effect.");
      }
    }
  }

  @Override
  public final boolean __removeEquivalent(K key, final EqualityComparator<Object> cmp) {
    if (mutator.get() == null) {
      throw new IllegalStateException("Transient already frozen.");
    }

    final int keyHash = key.hashCode();
    final MultimapResult<K, V, C> details = MultimapResult.unchanged();

    final R newRootNode =
        rootNode.removed(mutator, key, transformHashCode(keyHash), 0, details, cmp);

    switch (details.getModificationEffect()) {
      case NOTHING: {
        return false;
      }

      case REMOVED_PAYLOAD: {
        assert details.getModificationDetails().contains(REMOVED_KEY);

        // int hashCodeDeltaOld = tupleHash(keyHash, details.getEvictedPayload());
        // this.cachedHashCode = cachedHashCode - hashCodeDeltaOld;

        Set.Immutable<V> evictedValueCollection =
            internalFormatToCollection(details.getEvictedPayload().get());

        this.cachedSize = cachedSize - evictedValueCollection.size();
        this.cachedKeySetHashCode = cachedKeySetHashCode - keyHash;
        this.cachedKeySetSize = cachedKeySetSize - 1;

        this.rootNode = newRootNode;
        assertPropertiesCorrectness();
        return true;
      }

      default: {
        throw new IllegalStateException("Unhandled modification effect.");
      }
    }
  }

  public static class TransientSetMultimapKeyIterator<K, V, C extends Iterable<V>, R extends MultimapNode<K, V, C, R>> extends
      SetMultimapKeyIterator<K, V, C, R> {

    final AbstractTransientTrieSetMultimap<K, V, C, R> collection;
    K lastKey;

    public TransientSetMultimapKeyIterator(
        final AbstractTransientTrieSetMultimap<K, V, C, R> collection) {
      super((R) collection.rootNode);
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

  public static class TransientSetMultimapValueIterator<K, V, C extends Iterable<V>, R extends MultimapNode<K, V, C, R>>
      extends SetMultimapValueIterator<K, V, C, R> {

    final AbstractTransientTrieSetMultimap<K, V, C, R> collection;

    public TransientSetMultimapValueIterator(
        final AbstractTransientTrieSetMultimap<K, V, C, R> collection,
        final Function<V, C> converter) {
      super((R) collection.rootNode, converter);
      this.collection = collection;
    }

    @Override
    public C next() {
      return super.next();
    }

    @Override
    public void remove() {
      throw new UnsupportedOperationException();
    }
  }

  public static class TransientSetMultimapTupleIterator<K, V, C extends Iterable<V>, R extends MultimapNode<K, V, C, R>, T>
      extends SetMultimapTupleIterator<K, V, C, R, T> {

    final AbstractTransientTrieSetMultimap<K, V, C, R> collection;

    public TransientSetMultimapTupleIterator(
        final AbstractTransientTrieSetMultimap<K, V, C, R> collection,
        final BiFunction<K, V, T> tupleOf) {
      super((R) collection.rootNode, tupleOf);
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

}
