/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.capsule.core.trie;

import java.util.Comparator;
import java.util.concurrent.atomic.AtomicReference;

public interface SetNode<K, R extends SetNode<K, R>> extends Node {

  boolean TRACK_DELTA_OF_META_DATA_PER_NODE = true;
  boolean TRACK_DELTA_OF_META_DATA_PER_COLLECTION = true;

  boolean TRACK_DELTA_OF_META_DATA =
      TRACK_DELTA_OF_META_DATA_PER_NODE || TRACK_DELTA_OF_META_DATA_PER_COLLECTION;

  // NOTE: was true
  boolean TRUST_NODE_SIZE_AND_HASHCODE = true;

  boolean MEMOIZE_HASH_CODE_OF_ELEMENT = false;
  boolean MEMOIZE_HASH_CODE_OF_COLLECTION = true;

  boolean hasPayload();

  int payloadArity();

  K getKey(final int index);

  int getKeyHash(final int index);

  default ImmutablePayloadTuple<K> getPayload(final int index) {
    return ImmutablePayloadTuple.of(getKeyHash(index), getKey(index));
  }

  int size();

  int recursivePayloadHashCode();

  R union(final AtomicReference<Thread> mutator, R that,
      final int shift, final IntersectionResult details, final Comparator<Object> cmp,
      Preference directionPreference);

  R intersect(final AtomicReference<Thread> mutator, R that,
      final int shift, final IntersectionResult details, final Comparator<Object> cmp,
      Preference directionPreference);

  R subtract(final AtomicReference<Thread> mutator, R that,
      final int shift, final IntersectionResult details, final Comparator<Object> cmp,
      Preference directionPreference);

  enum Preference {
    INDIFFERENT,
    LEFT,
    RIGHT;
  }

  final class IntersectionResult {

    private int accumulatedHashCode = 0;
    private int accumulatedSize = 0;

    public final void addHashCode(final int hashCode) {
      accumulatedHashCode = accumulatedHashCode + hashCode;
    }

    public final void addSize(final int size) {
      accumulatedSize = accumulatedSize + size;
    }

    public final int getAccumulatedHashCode() {
      return accumulatedHashCode;
    }

    public final int getAccumulatedSize() {
      return accumulatedSize;
    }

  }

  final class Prototype<K, R extends SetNode<K, R>> {

    final boolean trackSize;

    private int dataMap = 0;
    private int nodeMap = 0;

    private int cachedSize = 0;

    private final Object[] buffer = new Object[32];
    private int dataIndex = 0;
    private int nodeIndex = buffer.length - 1;

    private final int[] hashes = new int[32];
    private int hashIndex = 0;

    public Prototype(boolean trackSize) {
      this.trackSize = trackSize;
    }

    public final int dataMap() {
      return dataMap;
    }

    public final int hashMap() {
      return dataMap;
    }

    public final int nodeMap() {
      return nodeMap;
    }

    public final boolean isEmpty() {
      return dataMap == 0 && nodeMap == 0;
    }

    public final void add(int bitpos, ImmutablePayloadTuple<K> key) {
      // add element
      buffer[dataIndex] = key;
      dataMap |= bitpos;

      // advance cursor
      dataIndex = dataIndex + 1;
    }

    public final void add(int bitpos, K key, int hash) {
      add(bitpos, key);
      addHash(hash);
    }

    public final void add(int bitpos, K key) {
      // add element
      buffer[dataIndex] = key;
      dataMap |= bitpos;

      // advance cursor
      dataIndex = dataIndex + 1;

      // increase cached properties
      if (trackSize) {
        cachedSize += 1;
      }
    }

    public final void addHash(int hash) {
      // add hash
      hashes[hashIndex] = hash;

      // advance cursor
      hashIndex = hashIndex + 1;
    }

    public final void add(int bitpos, R node) {
      // add node
      buffer[nodeIndex] = node;
      nodeMap |= bitpos;

      // advance cursor
      nodeIndex = nodeIndex - 1;

      // increase cached properties
      if (trackSize) {
        cachedSize += node.size();
      }
    }

    public final Object[] compactBuffer() {
      final Object[] compactedBuffer = new Object[dataIndex + 31 - nodeIndex];
      System.arraycopy(buffer, 0, compactedBuffer, 0, dataIndex);
      System.arraycopy(buffer, nodeIndex + 1, compactedBuffer, dataIndex, 31 - nodeIndex);
      return compactedBuffer;
    }

    public final int[] compactHashes() {
      final int[] compactedHashes = new int[hashIndex];
      System.arraycopy(hashes, 0, compactedHashes, 0, hashIndex);
      return compactedHashes;
    }

    public final int getCachedSize() {
      return cachedSize;
    }
  }

}
