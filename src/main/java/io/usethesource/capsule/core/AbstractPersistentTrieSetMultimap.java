/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.capsule.core;

import io.usethesource.capsule.Set;
import io.usethesource.capsule.SetMultimap;
import io.usethesource.capsule.core.trie.MultimapNode;
import io.usethesource.capsule.core.trie.MultimapResult;

import static io.usethesource.capsule.core.trie.MultimapResult.Modification.INSERTED_KEY;
import static io.usethesource.capsule.core.trie.MultimapResult.Modification.REMOVED_KEY;

/*
 * TODO: remove public modifier
 */
public abstract class AbstractPersistentTrieSetMultimap<K, V, C extends Iterable<V>, R extends MultimapNode<K, V, C, R>>
    extends AbstractTrieSetMultimap<K, V, C, R>
    implements SetMultimap.Immutable<K, V>, java.io.Serializable {

  private static final long serialVersionUID = 42L;

  protected static final boolean DEBUG = false;

  protected final R rootNode;
  //  protected final int cachedHashCode;
  protected final int cachedSize;

  protected final int cachedKeySetSize;
  protected final int cachedKeySetHashCode;

  protected AbstractPersistentTrieSetMultimap(final R rootNode, int cachedSize, int keySetHashCode, int keySetSize) {
    this.rootNode = rootNode;

    // this.cachedHashCode = cachedHashCode;
    this.cachedSize = cachedSize;

    this.cachedKeySetHashCode = keySetHashCode;
    this.cachedKeySetSize = keySetSize;

    if (DEBUG) {
      assert cachedSize == size(rootNode);
      assert keySetHashCode == keySetHashCode(rootNode);
      assert keySetSize == keySetSize(rootNode);
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

  protected abstract SetMultimap.Immutable<K, V> wrap(R rootNode, int cachedSize, int cachedKeySetHashCode, int cachedKeySetSize);

  @Override
  public final SetMultimap.Immutable<K, V> __insert(final K key, final V value) {
    return __insert(key, valueToTemporaryBox(value));
  }

  @Override
  public final SetMultimap.Immutable<K, V> __insert(K key, Set.Immutable<V> valueCollection) {
    if (valueCollection.isEmpty()) {
      return this;
    }

    final int keyHash = key.hashCode();
    final MultimapResult<K, V, C> details = MultimapResult.unchanged();

    final C values = collectionToInternalFormat(valueCollection);
    final R newRootNode =
        rootNode.inserted(null, key, values, transformHashCode(keyHash), 0, details);

    switch (details.getModificationEffect()) {
      case NOTHING: {
        return this;
      }

      case INSERTED_PAYLOAD: {
        // int hashCodeDeltaNew = tupleHash(keyHash, values);
        // int propertyHashCode = cachedHashCode + hashCodeDeltaNew;

        int propertySize = cachedSize + details.sizeDelta().get();
        int propertyKeySetHashCode = cachedKeySetHashCode;
        int propertyKeySetSize = cachedKeySetSize;

        if (details.containsModification(INSERTED_KEY)) {
          propertyKeySetHashCode += keyHash;
          propertyKeySetSize += 1;
        }

        return wrap(newRootNode, propertySize, propertyKeySetHashCode, propertyKeySetSize);
      }

      default: {
        throw new IllegalStateException("Unhandled modification effect.");
      }
    }
  }

  @Override
  public final SetMultimap.Immutable<K, V> __put(K key, V value) {
    return __put(key, valueToTemporaryBox(value));
  }

  @Override
  public final SetMultimap.Immutable<K, V> __put(K key, Set.Immutable<V> valueCollection) {
    if (valueCollection.isEmpty()) {
      return __remove(key);
    }

    final int keyHash = key.hashCode();
    final MultimapResult<K, V, C> details = MultimapResult.unchanged();

    final C values = collectionToInternalFormat(valueCollection);
    final R newRootNode =
        rootNode.updated(null, key, values, transformHashCode(keyHash), 0, details);

    switch (details.getModificationEffect()) {
      case NOTHING: {
        return this;
      }

      case REPLACED_PAYLOAD: {
        // int hashCodeDeltaOld = tupleHash(keyHash, details.getEvictedPayload());
        // int hashCodeDeltaNew = tupleHash(keyHash, values);
        // int propertyHashCode = cachedHashCode + hashCodeDeltaNew - hashCodeDeltaOld;

        Set.Immutable<V> evictedValueCollection =
            internalFormatToCollection(details.getEvictedPayload().get());

        int propertySize = cachedSize - evictedValueCollection.size() + valueCollection.size();
        int propertyKeySetHashCode = cachedKeySetHashCode;
        int propertyKeySetSize = cachedKeySetSize;

        return wrap(newRootNode, propertySize, propertyKeySetHashCode, propertyKeySetSize);
      }

      case INSERTED_PAYLOAD: {
        assert details.containsModification(INSERTED_KEY);

        // int hashCodeDeltaNew = tupleHash(keyHash, values);
        // int propertyHashCode = cachedHashCode + hashCodeDeltaNew;

        int propertySize = cachedSize + valueCollection.size();
        int propertyKeySetHashCode = cachedKeySetHashCode + keyHash;
        int propertyKeySetSize = cachedKeySetSize + 1;

        return wrap(newRootNode, propertySize, propertyKeySetHashCode, propertyKeySetSize);
      }

      default: {
        throw new IllegalStateException("Unhandled modification effect.");
      }
    }
  }

  @Override
  public final SetMultimap.Immutable<K, V> __remove(final K key, final V value) {
    final int keyHash = key.hashCode();
    final MultimapResult<K, V, C> details = MultimapResult.unchanged();

    final R newRootNode =
        rootNode.removed(null, key, value, transformHashCode(keyHash), 0, details);

    switch (details.getModificationEffect()) {
      case NOTHING: {
        return this;
      }

      case REMOVED_PAYLOAD: {
        // int hashCodeDeltaOld = tupleHash(keyHash, value); // TODO: support collection
        // int propertyHashCode = cachedHashCode - hashCodeDeltaOld;

        int propertySize = cachedSize - 1; // TODO: support collection
        int propertyKeySetHashCode = cachedKeySetHashCode;
        int propertyKeySetSize = cachedKeySetSize;

        if (details.containsModification(REMOVED_KEY)) {
          propertyKeySetHashCode -= keyHash;
          propertyKeySetSize -= 1;
        }

        return wrap(newRootNode, propertySize, propertyKeySetHashCode, propertyKeySetSize);
      }

      default: {
        throw new IllegalStateException("Unhandled modification effect.");
      }
    }
  }

  @Override
  public final SetMultimap.Immutable<K, V> __remove(K key) {
    final int keyHash = key.hashCode();
    final MultimapResult<K, V, C> details = MultimapResult.unchanged();

    final R newRootNode =
        rootNode.removed(null, key, transformHashCode(keyHash), 0, details);

    switch (details.getModificationEffect()) {
      case NOTHING: {
        return this;
      }

      case REMOVED_PAYLOAD: {
        assert details.containsModification(REMOVED_KEY);

        // int hashCodeDeltaOld = tupleHash(keyHash, details.getEvictedPayload());
        // int propertyHashCode = cachedHashCode - hashCodeDeltaOld;

        Set.Immutable<V> evictedValueCollection =
            internalFormatToCollection(details.getEvictedPayload().get());

        int propertySize = cachedSize - evictedValueCollection.size();
        int propertyKeySetHashCode = cachedKeySetHashCode - keyHash;
        int propertyKeySetSize = cachedKeySetSize - 1;

        return wrap(newRootNode, propertySize, propertyKeySetHashCode, propertyKeySetSize);
      }

      default: {
        throw new IllegalStateException("Unhandled modification effect.");
      }
    }
  }

}
