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
      new PersistentTrieVector(EMPTY_NODE, 0, 0, 0);

  private final VectorNode<K> root;
  private final int shift;
  private final int offset;
  private final int length;
  // private final Object[] head;
  // private final Object[] tail;

  public PersistentTrieVector(VectorNode<K> root, int shift, int offset, int length) {
    this.root = root;
    this.shift = shift;
    this.offset = offset;
    this.length = length;
  }

  public static final <K> Vector.Immutable<K> of() {
    return EMPTY_VECTOR;
  }

  @Override
  public int size() {
    return length;
  }

  @Override
  public Optional<K> get(int index) {
    return root.get(index, -offset, shift);
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
    final int index = offset;

    final int newShift = minimumShift(length); // TODO size or newSize
    final int newOffset = offset;
    final int newLength = length + 1;

    if (newShift > shift) {
      final VectorNode<K> newLeafNode = new ContentVectorNode<>(new Object[]{item});
      final VectorNode<K> newRootNode = new RelaxedVectorNode<>(new VectorNode[]{
          newPath(newLeafNode, shift),
          root
      }, new int[]{
          1,
          length + 1
      });

      return new PersistentTrieVector<>(newRootNode, newShift, newOffset, newLength);
    }

    final VectorNode<K> newRootNode = root.pushFront(index, offset + 1, -offset + 1, item, shift);
    return new PersistentTrieVector<>(newRootNode, shift, newOffset, newLength);
  }

  @Override
  public Vector.Immutable<K> pushBack(K item) {
    final int index = offset + length;

    final int newShift = minimumShift(length); // TODO size or newSize
    final int newOffset = offset;
    final int newLength = length + 1;

    if (newShift > shift) {
      final VectorNode<K> newLeafNode = new ContentVectorNode<>(new Object[]{item});
      final VectorNode<K> newRootNode = new RegularVectorNode<>(new VectorNode[]{
          root,
          newPath(newLeafNode, shift)
      });

      return new PersistentTrieVector<>(newRootNode, newShift, newOffset, newLength);
    }

    final VectorNode<K> newRootNode = root.pushBack(index, -offset, item, shift);
    return new PersistentTrieVector<>(newRootNode, shift, newOffset, newLength);
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

    Optional<K> get(int index, int delta, int shift);

    VectorNode<K> pushFront(int index, int offset, int delta, K item, int shift);

    VectorNode<K> pushBack(int index, int delta, K item, int shift);

  }

  private static final class RegularVectorNode<K> implements VectorNode<K> {

    private final VectorNode[] content;

    private RegularVectorNode(VectorNode[] content) {
      this.content = content;
    }

    @Override
    public Optional<K> get(int index, int delta, int shift) {
      int blockRelativeIndex = ((index + delta) >>> shift) & 0b11111;
      return content[blockRelativeIndex].get(index, delta, shift - BIT_PARTITION_SIZE);
    }

    @Override
    public VectorNode<K> pushFront(int index, int offset, int delta, K item, int shift) {
      int blockRelativeIndex = ((index + delta) >>> shift) & 0b11111;

      // assert blockRelativeIndex < content.length;
      assert content.length <= BIT_COUNT_OF_INDEX;

      if (index + 1 == offset) {
        // copy and insert node
        final VectorNode[] src = this.content;
        final VectorNode[] dst = new VectorNode[src.length + 1];

        final int idx = 0;
        final VectorNode<K> newLeafNode = new ContentVectorNode<>(new Object[]{item});
        final VectorNode<K> newNode = newPath(newLeafNode, shift - BIT_PARTITION_SIZE);

        // copy 'src' and insert 1 element(s) at position 'idx'
        System.arraycopy(src, 0, dst, 0, idx);
        dst[idx] = newNode;
        System.arraycopy(src, idx, dst, idx + 1, src.length - idx);

        assert dst.length <= BIT_COUNT_OF_INDEX;
        return new RegularVectorNode<>(dst);
      } else {
        // copy and set node
        final VectorNode[] src = this.content;
        final VectorNode[] dst = new VectorNode[src.length];

        final int idx = blockRelativeIndex;
        final VectorNode<K> newNode = src[idx]
            .pushFront(index, offset, delta, item, shift - BIT_PARTITION_SIZE);

        // copy 'src' and set 1 element(s) at position 'idx'
        System.arraycopy(src, 0, dst, 0, src.length);
        dst[idx] = newNode;

        assert dst.length <= BIT_COUNT_OF_INDEX;
        return new RegularVectorNode<>(dst);
      }
    }

    @Override
    public VectorNode<K> pushBack(int index, int delta, K item, int shift) {
      int blockRelativeIndex = ((index + delta) >>> shift) & 0b11111;

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
        final VectorNode<K> newNode = src[idx]
            .pushBack(index, delta, item, shift - BIT_PARTITION_SIZE);

        // copy 'src' and set 1 element(s) at position 'idx'
        System.arraycopy(src, 0, dst, 0, src.length);
        dst[idx] = newNode;

        return new RegularVectorNode<>(dst);
      }
    }

  }

  private static final class RelaxedVectorNode<K> implements VectorNode<K> {

    private final VectorNode[] content;
    private final int[] cumulativeSizes;

    private RelaxedVectorNode(VectorNode[] content, int[] cumulativeSizes) {
      this.content = content;
      this.cumulativeSizes = cumulativeSizes;
    }

    private final static int offset(int[] cumulativeSizes, int index) {
//      int blockRelativeIndex = 0;
//
      for (int i = 0; i < cumulativeSizes.length; i++) {
        if (cumulativeSizes[i] > index) {
          return i;
        }
      }

      throw new IndexOutOfBoundsException("Index larger than subtree.");

      // Arrays.stream(cumulativeSizes).filter()
    }

    @Override
    public Optional<K> get(int index, int delta, int shift) {
      // int blockRelativeIndex = ((index + delta) >>> shift) & 0b11111;
      int blockRelativeIndex = offset(cumulativeSizes, index + delta);

      final int newDelta;
      if (blockRelativeIndex == 0) {
        newDelta = delta;
      } else {
        newDelta = delta - cumulativeSizes[blockRelativeIndex - 1];
      }

      return content[blockRelativeIndex].get(index, newDelta, shift - BIT_PARTITION_SIZE);
    }

    @Override
    public VectorNode<K> pushFront(int index, int offset, int delta, K item, int shift) {
      // throw new UnsupportedOperationException("Not yet implemented.");

      // int blockRelativeIndex = ((index + delta) >>> shift) & 0b11111;
      int blockRelativeIndex = offset(cumulativeSizes, index);
      // int newDelta = delta + cumulativeSizes[blockRelativeIndex];

      boolean isCurrentBranchFull = content.length == BIT_COUNT_OF_INDEX;
      boolean isSubTreeBranchFull = cumulativeSizes[blockRelativeIndex] == BIT_COUNT_OF_INDEX; // wrong!!!

      // assert blockRelativeIndex < content.length;
      assert content.length <= BIT_COUNT_OF_INDEX;

      if (isSubTreeBranchFull && !isCurrentBranchFull) {
        // copy and insert node
        final VectorNode[] src = this.content;
        final VectorNode[] dst = new VectorNode[src.length + 1];

        final int idx = 0;
        final VectorNode<K> newLeafNode = new ContentVectorNode<>(new Object[]{item});
        final VectorNode<K> newNode = newPath(newLeafNode, shift - BIT_PARTITION_SIZE);

        // copy 'src' and insert 1 element(s) at position 'idx'
        System.arraycopy(src, 0, dst, 0, idx);
        dst[idx] = newNode;
        System.arraycopy(src, idx, dst, idx + 1, src.length - idx);

        final int[] srcSizes = this.cumulativeSizes;
        final int[] dstSizes = new int[srcSizes.length + 1];

        dstSizes[0] = 1;
        for (int i = 0; i < srcSizes.length; i++) {
          dstSizes[i + 1] = srcSizes[i] + 1;
        }

        assert dst.length <= BIT_COUNT_OF_INDEX;
        if (dstSizes.length == BIT_COUNT_OF_INDEX && dstSizes[BIT_COUNT_OF_INDEX - 1] == 1 << (shift << 1)) {
          return new RegularVectorNode<>(dst);
        } else {
          return new RelaxedVectorNode<>(dst, dstSizes);
        }
      } else {
        // copy and set node
        final VectorNode[] src = this.content;
        final VectorNode[] dst = new VectorNode[src.length];

        final int idx = blockRelativeIndex;
        final VectorNode<K> newNode = src[idx]
            .pushFront(index, offset, delta, item, shift - BIT_PARTITION_SIZE);

        // copy 'src' and set 1 element(s) at position 'idx'
        System.arraycopy(src, 0, dst, 0, src.length);
        dst[idx] = newNode;

        final int[] srcSizes = this.cumulativeSizes;
        final int[] dstSizes = new int[srcSizes.length];

        for (int i = 0; i < srcSizes.length; i++) {
          if (i < blockRelativeIndex) {
            dstSizes[i] = srcSizes[i];
          } else {
            dstSizes[i] = srcSizes[i] + 1;
          }
        }

        assert dst.length <= BIT_COUNT_OF_INDEX;
        if (dstSizes.length == BIT_COUNT_OF_INDEX && dstSizes[BIT_COUNT_OF_INDEX - 1] == 1 << (shift << 1)) {
          return new RegularVectorNode<>(dst);
        } else {
          return new RelaxedVectorNode<>(dst, dstSizes);
        }
      }
    }

    @Override
    public VectorNode<K> pushBack(int index, int delta, K item, int shift) {
      throw new UnsupportedOperationException("Not yet implemented.");
    }

  }

  private static final class ContentVectorNode<K> implements VectorNode<K> {

    private final Object[] content;

    private ContentVectorNode(Object[] content) {
      this.content = content;
    }

    @Override
    public Optional<K> get(int index, int delta, int shift) {
      assert shift == 0;
      assert (((index + delta) >>> shift) & 0b11111) <= content.length;

      int blockRelativeIndex = ((index + delta) >>> shift) & 0b11111;

      if (blockRelativeIndex >= content.length) {
        return Optional.empty();
      } else {
        return Optional.of((K) content[blockRelativeIndex]);
      }
    }

    /*
     * TODO currently ignores index and delta
     */
    @Override
    public VectorNode<K> pushFront(int index, int offset, int delta, K item, int shift) {
      assert shift == 0;
      assert content.length < BIT_COUNT_OF_INDEX;

      final Object[] src = this.content;
      final Object[] dst = new Object[src.length + 1];

      final int idx = 0;

      // copy 'src' and insert 1 element(s) at position 'idx'
      System.arraycopy(src, 0, dst, 0, idx);
      dst[idx] = item;
      System.arraycopy(src, idx, dst, idx + 1, src.length - idx);

      return new ContentVectorNode<>(dst);
    }

    /*
     * TODO currently ignores index and delta
     */
    @Override
    public VectorNode<K> pushBack(int index, int delta, K item, int shift) {
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
