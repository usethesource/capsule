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

  boolean TRUST_NODE_SIZE_AND_HASHCODE = true;

  boolean MEMOIZE_HASH_CODE_OF_ELEMENT = false;
  boolean MEMOIZE_HASH_CODE_OF_COLLECTION = true;

  boolean hasPayload();

  int payloadArity();

  K getKey(final int index);

  int getKeyHash(final int index);

  int size();

  int recursivePayloadHashCode();

  default R union(final AtomicReference<Thread> mutator, R that,
      final int shift, final IntersectionResult details, final Comparator<Object> cmp,
      Preference directionPreference) {
    throw new UnsupportedOperationException("Not implemented yet.");
  }

  default R intersect(final AtomicReference<Thread> mutator, R that,
      final int shift, final IntersectionResult details, final Comparator<Object> cmp,
      Preference directionPreference) {
    throw new UnsupportedOperationException("Not implemented yet.");
  }

  default R subtract(final AtomicReference<Thread> mutator, R that,
      final int shift, final IntersectionResult details, final Comparator<Object> cmp,
      Preference directionPreference) {
    throw new UnsupportedOperationException("Not implemented yet.");
  }

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

    private int newDataMap = 0;
    private int newNodeMap = 0;

    private final Object[] buffer = new Object[32];
    private int dataIndex = 0;
    private int nodeIndex = buffer.length - 1;

    private final int[] hashes = new int[32];
    private int hashIndex = 0;

    private int sumOfDeltaSize = 0;
    private int sumOfDeltaHashCode = 0;

    private int sumOfAbsoluteSize = 0;
    private int sumOfAbsoluteHashCode = 0;

    public final int dataMap() {
      return newDataMap;
    }

    public final int nodeMap() {
      return newNodeMap;
    }

    public final boolean isEmpty() {
      return dataIndex == 0 && nodeIndex == buffer.length - 1;
    }

    @Deprecated
    public final void addPayloadSize(int size) {
      sumOfDeltaSize += size;
    }

    @Deprecated
    public final void addPayloadHash(int hash) {
      sumOfDeltaHashCode += hash;
    }

    public final int getPayloadSize() {
      return sumOfDeltaSize;
    }

    public final int getPayloadHash() {
      return sumOfDeltaHashCode;
    }

    public final void add(int bitpos, K key) {
      // add element
      buffer[dataIndex] = key;
      newDataMap |= bitpos;
      // advance cursor
      dataIndex = dataIndex + 1;
      // update meta data
      if (TRACK_DELTA_OF_META_DATA) {
        sumOfDeltaSize = sumOfDeltaSize + 1;
      }
    }

    public final void addHash(int hash) {
      // add hash
      hashes[hashIndex] = hash;
      // advance cursor
      hashIndex = hashIndex + 1;
      // update meta data
      if (TRACK_DELTA_OF_META_DATA) {
        sumOfDeltaHashCode = sumOfDeltaHashCode + hash;
      }
    }

    public final void add(int bitpos, R node) {
      // add node
      buffer[nodeIndex] = node;
      newNodeMap |= bitpos;

      // advance cursor
      nodeIndex = nodeIndex - 1;

//      // update meta data
//      sumOfAbsoluteSize = sumOfAbsoluteSize + node.size();
//      sumOfAbsoluteHashCode = sumOfAbsoluteHashCode + node.recursivePayloadHashCode();
    }

    private void add0(K key, int keyHash) {
      buffer[dataIndex] = key;
      hashes[dataIndex] = keyHash;
      dataIndex = dataIndex + 1;

      // update meta data
      sumOfAbsoluteSize = sumOfAbsoluteSize + 1;
      sumOfAbsoluteHashCode = sumOfAbsoluteHashCode + keyHash;
    }

    /**
     * Add key / keyHash tuple to buffer and increment meta data counters.
     */
    @Deprecated
    public final void add(K key, int keyHash) {
      add0(key, keyHash);

      // update meta data
      if (TRACK_DELTA_OF_META_DATA) {
        sumOfDeltaSize = sumOfDeltaSize + 1;
        sumOfDeltaHashCode = sumOfDeltaHashCode + keyHash;
      }
    }

    /**
     * Add node to buffer without updating meta data counters
     * (already done in sub-node's buffer).
     */
    @Deprecated
    public final void add(R node) {
      buffer[nodeIndex] = node;
      nodeIndex = nodeIndex - 1;

      // update meta data
      sumOfAbsoluteSize = sumOfAbsoluteSize + node.size();
      sumOfAbsoluteHashCode = sumOfAbsoluteHashCode + node.recursivePayloadHashCode();
    }

    /**
     * Unbox and inline node into buffer without updating meta data counters
     * (already done in sub-node's buffer).
     */
    public final void inline(R node) {
      add0(node.getKey(0), node.getKeyHash(0));
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

    final public int getNodeHashCode() {
      return sumOfAbsoluteHashCode;
    }

    final public int getNodeSize() {
      return sumOfAbsoluteSize;
    }
  }

}
