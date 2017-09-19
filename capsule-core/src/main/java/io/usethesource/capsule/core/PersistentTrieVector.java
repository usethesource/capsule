/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.capsule.core;

import static io.usethesource.capsule.core.PersistentTrieVector.VectorNode.BIT_COUNT_OF_INDEX;
import static io.usethesource.capsule.core.PersistentTrieVector.VectorNode.BIT_PARTITION_SIZE;

import java.util.Optional;

import io.usethesource.capsule.Vector;

public class PersistentTrieVector<K> implements Vector.Immutable<K> {

  private static final VectorNode EMPTY_NODE = new ContentVectorNode<>(new Object[]{});

  private static final PersistentTrieVector EMPTY_VECTOR =
      new PersistentTrieVector(EMPTY_NODE, 0, 0);

  private final VectorNode<K> root;
  private final int shift;
  private final int size;
  // private final Object[] head;
  // private final Object[] tail;

  public PersistentTrieVector(VectorNode<K> root, int shift, int size) {
    this.root = root;
    this.shift = shift;
    this.size = size;
  }

  public static final <K> Vector.Immutable<K> of() {
    return EMPTY_VECTOR;
  }

  @Override
  public int size() {
    return size;
  }

  @Override
  public Optional<K> get(int index) {
    return root.get(index, shift);
  }

  private static final int blockOffset(final int index) {
    if (index < BIT_COUNT_OF_INDEX) {
      return 0;
    } else {
      return ((index - 1) >>> BIT_PARTITION_SIZE) << BIT_PARTITION_SIZE;
    }
  }

  private static final int blockRelativeIndex(final int index) {
    return index - blockOffset(index);
  }

  private static final int minimumShift(final int index) {
    int bitWidth = BIT_COUNT_OF_INDEX - Integer.numberOfLeadingZeros(index);

    if (bitWidth % BIT_PARTITION_SIZE == 0) {
      return Math.max(0, (bitWidth / BIT_PARTITION_SIZE) - 1) * BIT_PARTITION_SIZE;
    } else {
      return (bitWidth / BIT_PARTITION_SIZE) * BIT_PARTITION_SIZE;
    }
  }

  @Override
  public Vector.Immutable<K> pushFront(K item) {
    throw new UnsupportedOperationException("Not yet implemented.");
  }

  @Override
  public Vector.Immutable<K> pushBack(K item) {
    final int newSize = size + 1;
    final int newShift = minimumShift(size); // TODO size or newSize

    if (newShift > shift) {
      final VectorNode<K> newLeafNode = new ContentVectorNode<>(new Object[]{item});
      final VectorNode<K> newRootNode = new RegularVectorNode<>(new VectorNode[]{
          root,
          newPath(newLeafNode, shift)
      });

      return new PersistentTrieVector<>(newRootNode, newShift, newSize);
    }

    final VectorNode<K> newRootNode = root.pushBack(size, item, shift);
    return new PersistentTrieVector<>(newRootNode, shift, newSize);
  }

  private static final <K> VectorNode<K> newPath(VectorNode<K> node, int level) {
    if (level == 0) {
      return node;
    } else {
      final VectorNode[] content = new VectorNode[]{
          newPath(node, level - BIT_PARTITION_SIZE)
      };
      return new RegularVectorNode<>(content);
    }
  }

  interface VectorNode<K> {

    int BIT_COUNT_OF_INDEX = 32;
    int BIT_PARTITION_SIZE = 5;
    int BIT_PARTITION_MASK = 0b11111;

    Optional<K> get(int index, int shift);

    VectorNode<K> pushBack(int index, K item, int shift);

  }

  private static final class RegularVectorNode<K> implements VectorNode<K> {

    private final VectorNode[] content;

    private RegularVectorNode(VectorNode[] content) {
      this.content = content;
    }

    @Override
    public Optional<K> get(int index, int shift) {
      int blockRelativeIndex = (index >>> shift) & 0b11111;
      return content[blockRelativeIndex].get(index, shift - BIT_PARTITION_SIZE);
    }

    @Override
    public VectorNode<K> pushBack(int index, K item, int shift) {
      int blockRelativeIndex = (index >>> shift) & 0b11111;

      // assert blockRelativeIndex < content.length;
      assert content.length <= BIT_COUNT_OF_INDEX;

      if (blockRelativeIndex == content.length) {
        // copy and insert node
        final VectorNode[] src = this.content;
        final VectorNode[] dst = new VectorNode[src.length + 1];

        final int idx = blockRelativeIndex;
        final VectorNode<K> newLeafNode = new ContentVectorNode<>(new Object[]{item});
        final VectorNode<K> newNode = newPath(newLeafNode, shift - BIT_PARTITION_SIZE);

        // copy 'src' and insert 1 element(s) at position 'idx'
        System.arraycopy(src, 0, dst, 0, idx);
        dst[idx] = newNode;
        System.arraycopy(src, idx, dst, idx + 1, src.length - idx);

        return new RegularVectorNode<>(dst);
      } else {
        // copy and set node
        final VectorNode[] src = this.content;
        final VectorNode[] dst = new VectorNode[src.length];

        final int idx = blockRelativeIndex;
        final VectorNode<K> newNode = src[idx].pushBack(index, item, shift - BIT_PARTITION_SIZE);

        // copy 'src' and set 1 element(s) at position 'idx'
        System.arraycopy(src, 0, dst, 0, src.length);
        dst[idx] = newNode;

        return new RegularVectorNode<>(dst);
      }
    }

  }

  private static final class RelaxedVectorNode<K> implements VectorNode<K> {

    @Override
    public Optional<K> get(int index, int shift) {
      throw new UnsupportedOperationException("Not yet implemented.");
    }

    @Override
    public VectorNode<K> pushBack(int index, K item, int shift) {
      throw new UnsupportedOperationException("Not yet implemented.");
    }

  }

  private static final class ContentVectorNode<K> implements VectorNode<K> {

    private final Object[] content;

    private ContentVectorNode(Object[] content) {
      this.content = content;
    }

    @Override
    public Optional<K> get(int index, int shift) {
      assert shift == 0;
      assert ((index >>> shift) & 0b11111) <= content.length;

      int blockRelativeIndex = (index >>> shift) & 0b11111;

      if (blockRelativeIndex >= content.length) {
        return Optional.empty();
      } else {
        return Optional.of((K) content[blockRelativeIndex]);
      }
    }

    @Override
    public VectorNode<K> pushBack(int index, K item, int shift) {
      assert shift == 0;
      assert content.length < BIT_COUNT_OF_INDEX;

      final Object[] src = this.content;
      final Object[] dst = new Object[src.length + 1];

      final int idx = src.length;

      // copy 'src' and insert 1 element(s) at position 'idx'
      System.arraycopy(src, 0, dst, 0, idx);
      dst[idx] = item;
      System.arraycopy(src, idx, dst, idx + 1, src.length - idx);

      return new ContentVectorNode<>(dst);
    }

  }

}
