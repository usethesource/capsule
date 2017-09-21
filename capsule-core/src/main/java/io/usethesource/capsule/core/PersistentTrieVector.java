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
import static io.usethesource.capsule.util.ArrayUtils.copyAndInsert;
import static io.usethesource.capsule.util.ArrayUtils.copyAndSet;
import static io.usethesource.capsule.util.ArrayUtilsInt.arraycopyAndInsertInt;
import static io.usethesource.capsule.util.BitmapUtils.mask;

import java.util.Optional;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import io.usethesource.capsule.Vector;

public class PersistentTrieVector<K> implements Vector.Immutable<K> {

  private static final VectorNode EMPTY_NODE = new ContentVectorNode<>(new Object[]{});

  private static final PersistentTrieVector EMPTY_VECTOR =
      new PersistentTrieVector(EMPTY_NODE, 0, 0);

  private final VectorNode<K> root;
  private final int shift;
  private final int length;
  // private final Object[] head;
  // private final Object[] tail;

  public PersistentTrieVector(VectorNode<K> root, int shift, int length) {
    this.root = root;
    this.shift = shift;
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
    return root.get(index, 0, shift);
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
    // final int index = 0;

    final int newShift = minimumShift(length); // TODO size or newSize
    final int newLength = length + 1;

    if (newShift > shift) {
      // TODO: get rid of RelaxedVectorNode::new calls below
      final VectorNode<K> newRootNode = VectorNode.of(newShift, new VectorNode[]{
          /*
           * NOTE: the 'left shadow' is always explicit (newRelaxedPath), because by default
           * vectors are left-aligned and right-ragged.
           */
          newRelaxedPath(item, shift),
          root
      }, new int[]{
          1,
          length + 1
      });

      return new PersistentTrieVector<>(newRootNode, newShift, newLength);
    }

    final VectorNode<K> newRootNode = root.pushFront(item, shift);
    return new PersistentTrieVector<>(newRootNode, shift, newLength);
  }

  @Override
  public Vector.Immutable<K> pushBack(K item) {
    final int index = length;

    final int newShift = minimumShift(length); // TODO size or newSize
    final int newLength = length + 1;

    if (newShift > shift) {
      final VectorNode<K> newRootNode = new RegularVectorNode<>(new VectorNode[]{
          root,
          /*
           * NOTE: here you can control if the 'right shadow' is implicit (newRegularPath)
           * or explicit at a higher cost (newRelaxedPath).
           */
          newRegularPath(item, shift)
          // newRelaxedPath(item, shift)
      });

      return new PersistentTrieVector<>(newRootNode, newShift, newLength);
    }

    final VectorNode<K> newRootNode = root.pushBack(item, shift);
    return new PersistentTrieVector<>(newRootNode, shift, newLength);
  }

  private static final <K> VectorNode<K> newRegularPath(K item, int shift) {
    if (shift == 0) {
      return new ContentVectorNode<>(new Object[]{item});
    } else {
      final VectorNode[] dst = new VectorNode[]{
          newRegularPath(item, shift - BIT_PARTITION_SIZE)
      };
      return VectorNode.of(shift, dst);
    }
  }

  private static final <K> VectorNode<K> newRelaxedPath(K item, int shift) {
    if (shift == 0) {
      return new ContentVectorNode<>(new Object[]{item});
    } else {
      final VectorNode[] dst = new VectorNode[]{
          newRelaxedPath(item, shift - BIT_PARTITION_SIZE)
      };
      int[] dstSizes = new int[]{1};

      return VectorNode.of(shift, dst, dstSizes);
    }
  }

  interface VectorNode<K> {

    int BIT_COUNT_OF_INDEX = 32;
    int BIT_PARTITION_SIZE = 5;
    int BIT_PARTITION_MASK = 0b11111;

    /*
     * NOTE: pretty bad performance
     */
    @Deprecated
    int size();

    Optional<K> get(int index, int delta, int shift);

    VectorNode<K> pushFront(K item, int shift);

    VectorNode<K> pushBack(K item, int shift);

    // TODO: next up: dropFront() and dropFront(int count)
    // TODO: next up: dropBack () and dropBack (int count)

    // TODO: next up: takeFront(int count)
    // TODO: next up: takeBack (int count)

    static <K> VectorNode<K> of(int shiftWitness, VectorNode[] dst) {
      return new RegularVectorNode<>(dst);

//      if (dst.length == BIT_COUNT_OF_INDEX) {
//        return new RegularVectorNode<>(dst);
//      } else {
//        final int[] dstSizes = Stream.of(dst).mapToInt(VectorNode::size).toArray();
//
//        for (int i = 0, j = 1; j < dstSizes.length; i++, j++) {
//          dstSizes[j] = dstSizes[i] + dstSizes[j];
//        }
//
//        return new RelaxedVectorNode<>(dst, dstSizes);
//      }
    }

    static <K> VectorNode<K> of(int shiftWitness, VectorNode[] dst, int[] dstSizes) {
      if (isRegular(dstSizes, shiftWitness)) {
        return new RegularVectorNode<>(dst);
      } else {
        return new RelaxedVectorNode<>(dst, dstSizes);
      }
    }

  }

  private final static boolean isRegular(int[] cumulativeSizes, int shift) {
    assert isSizeDifferenceValid(cumulativeSizes, shift);

    return cumulativeSizes[cumulativeSizes.length - 1] == (1 << shift) * cumulativeSizes.length;
  }

  private final static boolean isSizeDifferenceValid(int[] cumulativeSizes, int shift) {
    int[] cumulativeDiffs = new int[cumulativeSizes.length];
    for (int i = cumulativeSizes.length - 1; i > 0; i--) {
      cumulativeDiffs[i] = cumulativeSizes[i] - cumulativeSizes[i - 1];
    }
    cumulativeDiffs[0] = cumulativeSizes[0];

    int maxSize = (1 << shift);

    // NOTE: just use to figure out irregularities with balancing
    // assert IntStream.of(cumulativeDiffs).filter(size -> size == 1).count() > 1;

    return IntStream.of(cumulativeDiffs).allMatch(size -> size <= maxSize);
  }

  private static final class RegularVectorNode<K> implements VectorNode<K> {

    private final VectorNode[] content;

    private RegularVectorNode(VectorNode[] content) {
      this.content = content;
    }

    @Override
    public int size() {
      return Stream.of(content).mapToInt(VectorNode::size).sum();
    }

    @Override
    public Optional<K> get(int index, int delta, int shift) {
      final int blockRelativeIndex = mask(index + delta, shift, BIT_PARTITION_MASK);

      final int newDelta = delta - (1 << shift) * blockRelativeIndex;

      return content[blockRelativeIndex].get(index, newDelta, shift - BIT_PARTITION_SIZE);
    }

    @Override
    public VectorNode<K> pushFront(K item, int shift) {
      final int blockRelativeIndex = 0;
      final int idx = blockRelativeIndex;

      // assert blockRelativeIndex < content.length;
      assert content.length <= BIT_COUNT_OF_INDEX;

      boolean isCurrentBranchFull = content.length == BIT_COUNT_OF_INDEX;
      assert !isCurrentBranchFull; // assumes that addPath is called on higher level;

      // copy and insert node
      final VectorNode<K> newNode = newRelaxedPath(item, shift - BIT_PARTITION_SIZE);

      final VectorNode[] src = this.content;
      final VectorNode[] dst = copyAndInsert(VectorNode[]::new, src, idx, newNode);

      int newPathSize = 1;
      int sizeIncrement = (1 << shift);

      final int[] dstSizes = new int[dst.length];
      dstSizes[0] = newPathSize;
      for (int i = 1; i < dstSizes.length; i++) {
        dstSizes[i] = newPathSize + i * sizeIncrement;
      }

      assert dst.length <= BIT_COUNT_OF_INDEX;
      return VectorNode.of(shift, dst, dstSizes);
    }

    @Override
    public VectorNode<K> pushBack(K item, int shift) {
      boolean isRightMostSubtreeFull = content[content.length - 1].size() == (1 << shift);

      final int blockRelativeIndex = isRightMostSubtreeFull ? content.length : content.length - 1;
      final int idx = blockRelativeIndex;

      // assert blockRelativeIndex < content.length;
      assert content.length <= BIT_COUNT_OF_INDEX;

      if (blockRelativeIndex == content.length) {
        // copy and insert node
        final VectorNode<K> newNode = newRegularPath(item, shift - BIT_PARTITION_SIZE);

        final VectorNode[] src = this.content;
        final VectorNode[] dst = copyAndInsert(VectorNode[]::new, src, idx, newNode);

        return VectorNode.of(shift, dst);
      } else {
        // copy and set node
        final VectorNode[] src = this.content;

        final VectorNode<K> newNode = src[idx]
            .pushBack(item, shift - BIT_PARTITION_SIZE);

        final VectorNode[] dst = copyAndSet(VectorNode[]::new, src, idx, newNode);

        return VectorNode.of(shift, dst);
      }
    }

  }

  private static final class RelaxedVectorNode<K> implements VectorNode<K> {

    private final VectorNode[] content;
    private final int[] cumulativeSizes;

    private RelaxedVectorNode(VectorNode[] content, int[] cumulativeSizes) {
      this.content = content;
      this.cumulativeSizes = cumulativeSizes;

      assert content.length == cumulativeSizes.length;
    }

    private final static int offset(int[] cumulativeSizes, int index) {
      for (int i = 0; i < cumulativeSizes.length; i++) {
        if (cumulativeSizes[i] > index) {
          return i;
        }
      }
      throw new IndexOutOfBoundsException("Index larger than subtree.");
    }

    @Override
    public int size() {
      return cumulativeSizes[content.length - 1];
    }

    @Override
    public Optional<K> get(int index, int delta, int shift) {
      // int blockRelativeIndex = mask(index + delta, shift, BIT_PARTITION_MASK);
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
    public VectorNode<K> pushFront(K item, int shift) {
      final int blockRelativeIndex = 0;
      final int idx = blockRelativeIndex;

      boolean isCurrentBranchFull = content.length == BIT_COUNT_OF_INDEX;
      boolean isSubTreeBranchFull = cumulativeSizes[0] == 1 << shift;

      // assert blockRelativeIndex < content.length;
      assert content.length <= BIT_COUNT_OF_INDEX;

      if (isSubTreeBranchFull && !isCurrentBranchFull) {
        // copy and insert node
        final VectorNode<K> newNode = newRelaxedPath(item, shift - BIT_PARTITION_SIZE);

        final VectorNode[] src = this.content;
        final VectorNode[] dst = copyAndInsert(VectorNode[]::new, src, idx, newNode);

        final int[] srcSizes = this.cumulativeSizes;
        final int[] dstSizes = new int[srcSizes.length + 1];

        int newPathSize = 1;

        dstSizes[0] = newPathSize;
        for (int i = 0; i < srcSizes.length; i++) {
          dstSizes[i + 1] = newPathSize + srcSizes[i];
        }

        assert dst.length <= BIT_COUNT_OF_INDEX;
        return VectorNode.of(shift, dst, dstSizes);
      } else {
        // copy and set node
        final VectorNode[] src = this.content;

        final VectorNode<K> newNode = src[idx]
            .pushFront(item, shift - BIT_PARTITION_SIZE);

        final VectorNode[] dst = copyAndSet(VectorNode[]::new, src, idx, newNode);

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
        return VectorNode.of(shift, dst, dstSizes);
      }
    }

    @Override
    public VectorNode<K> pushBack(K item, int shift) {
      // int blockRelativeIndex = mask(index + delta, shift, BIT_PARTITION_MASK);
      // final int blockRelativeIndex = offset(cumulativeSizes, index + delta);

      boolean isCurrentBranchFull = content.length == BIT_COUNT_OF_INDEX;
      boolean isSubTreeBranchFull = cumulativeSizes[cumulativeSizes.length - 1] == (1 << shift) * (cumulativeSizes.length - 1 + 1);

//      final int newDelta;
//      if (blockRelativeIndex == 0) {
//        newDelta = delta;
//      } else {
//        newDelta = delta - cumulativeSizes[blockRelativeIndex - 1];
//      }

      // final int blockRelativeIndex = blockRelativeOffset; // index + newDelta; // mask(index + newDelta, shift, BIT_PARTITION_MASK);

//      boolean isCurrentBranchFull = content.length == BIT_COUNT_OF_INDEX;
//      boolean isSubTreeBranchFull = cumulativeSizes[blockRelativeIndex] == (1 << shift) * (blockRelativeIndex + 1);

      if (isSubTreeBranchFull && !isCurrentBranchFull) {
        // copy and insert node

        // final int blockRelativeIndex = content.length;

        final VectorNode<K> newNode = newRegularPath(item, shift - BIT_PARTITION_SIZE);

        // final int idx = content.length;

        final VectorNode[] src = this.content;
        // NOTE: add to end (content.length)
        final VectorNode[] dst = copyAndInsert(VectorNode[]::new, src, content.length, newNode);

        final int[] srcSizes = this.cumulativeSizes;
        // NOTE: add to end (content.length)
        final int[] dstSizes = arraycopyAndInsertInt(srcSizes, content.length, srcSizes[srcSizes.length - 1] + 1);

        assert dst.length <= BIT_COUNT_OF_INDEX;
        return VectorNode.of(shift, dst, dstSizes);
      } else {
        // copy and set node

        final int blockRelativeIndex = content.length - 1;

        final VectorNode[] src = this.content;

        final VectorNode<K> newNode = src[blockRelativeIndex].pushBack(item, shift - BIT_PARTITION_SIZE);

        final VectorNode[] dst = copyAndSet(VectorNode[]::new, src, blockRelativeIndex, newNode);

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
        return VectorNode.of(shift, dst, dstSizes);
      }
    }

  }

  private static final class ContentVectorNode<K> implements VectorNode<K> {

    private final Object[] content;

    private ContentVectorNode(Object[] content) {
      this.content = content;

      assert content.length <= BIT_COUNT_OF_INDEX;
    }

    @Override
    public int size() {
      return content.length;
    }

    @Override
    public Optional<K> get(int index, int delta, int shift) {
      assert shift == 0;
      assert mask(index + delta, shift, BIT_PARTITION_MASK) <= content.length;

      int blockRelativeIndex = mask(index + delta, shift, BIT_PARTITION_MASK);

      if (blockRelativeIndex >= content.length) {
        return Optional.empty();
      } else {
        return Optional.of((K) content[blockRelativeIndex]);
      }
    }

    @Override
    public VectorNode<K> pushFront(K item, int shift) {
      assert shift == 0;

      final Object[] src = this.content;
      final Object[] dst = copyAndInsert(Object[]::new, src, 0, item);

      return new ContentVectorNode<>(dst);
    }

    /*
     * TODO currently ignores index and delta
     */
    @Override
    public VectorNode<K> pushBack(K item, int shift) {
      assert shift == 0;

      final Object[] src = this.content;
      final Object[] dst = copyAndInsert(Object[]::new, src, src.length, item);

      return new ContentVectorNode<>(dst);
    }

  }

}
