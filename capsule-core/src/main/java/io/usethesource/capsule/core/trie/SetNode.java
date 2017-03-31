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

  boolean hasPayload();

  int payloadArity();

  K getKey(final int index);

  int getKeyHash(final int index);

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

  final class Buffer<K, R extends SetNode<K, R>> {

    private final Object[] buffer = new Object[32];
    private final int[] hashes = new int[32];

    private int dataIndex = 0;
    private int nodeIndex = buffer.length - 1;

    private int sumOfPayload = 0;
    private int sumOfHashes = 0;

    public final boolean isEmpty() {
      return dataIndex == 0 && nodeIndex == buffer.length - 1;
    }

    @Deprecated
    public final void addPayloadSize(int size) {
      sumOfPayload += size;
    }

    @Deprecated
    public final void addPayloadHash(int hash) {
      sumOfHashes += hash;
    }

    public final int getPayloadSize() {
      return sumOfPayload;
    }

    public final int getPayloadHash() {
      return sumOfHashes;
    }

    private void add0(K key, int keyHash) {
      buffer[dataIndex] = key;
      hashes[dataIndex] = keyHash;
      dataIndex = dataIndex + 1;
    }

    /**
     * Add key / keyHash tuple to buffer and increment meta data counters.
     */
    public final void add(K key, int keyHash) {
      add0(key, keyHash);

      // update meta data
      sumOfPayload = sumOfPayload + 1;
      sumOfHashes = sumOfHashes + keyHash;
    }

    /**
     * Add node to buffer without updating meta data counters
     * (already done in sub-node's buffer).
     */
    public final void add(R node) {
      buffer[nodeIndex] = node;
      nodeIndex = nodeIndex - 1;
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
      final int[] compactedHashes = new int[dataIndex];
      System.arraycopy(hashes, 0, compactedHashes, 0, dataIndex);
      return compactedHashes;
    }

  }

}