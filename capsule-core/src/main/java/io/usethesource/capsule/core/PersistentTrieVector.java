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
import static io.usethesource.capsule.util.ArrayUtils.copyAndDrop;
import static io.usethesource.capsule.util.ArrayUtils.copyAndInsert;
import static io.usethesource.capsule.util.ArrayUtils.copyAndTake;
import static io.usethesource.capsule.util.ArrayUtils.copyAndUpdate;
import static io.usethesource.capsule.util.BitmapUtils.mask;

import java.util.Optional;
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

    assert root.size() == length;
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
    return root.get(index, index, shift);
  }

  private static final int minimumShift(final int index) {
    int bitWidth = BIT_COUNT_OF_INDEX - Integer.numberOfLeadingZeros(index);

    if (bitWidth % BIT_PARTITION_SIZE == 0) {
      return Math.max(0, (bitWidth / BIT_PARTITION_SIZE) - 1) * BIT_PARTITION_SIZE;
    } else {
      return (bitWidth / BIT_PARTITION_SIZE) * BIT_PARTITION_SIZE;
    }
  }

  // TODO: move to a proper place
  private static final boolean implies(boolean a, boolean b) {
    return !a || b;
  }

  /*
   * NOTE: the 'left shadow' is always explicit (newRelaxedPath), because by default
   * vectors are left-aligned and right-ragged.
   */
  @Override
  public Vector.Immutable<K> pushFront(K item) {
    final int newShift = root.hasFullFront() ? shift + BIT_PARTITION_SIZE : shift;
    final int newLength = length + 1;

    assert implies(newShift > shift, root.hasFullFront());
    assert implies(newShift == shift, !root.hasFullFront());

    if (newShift > shift) {
      final VectorNode<K> newRootNode = VectorNode.of(newShift, 1, new VectorNode[]{
          newLeftFringedPath(item, shift),
          root
      }, length);

      return new PersistentTrieVector<>(newRootNode, newShift, newLength);
    }

    final VectorNode<K> newRootNode = root.pushFront(shift, item);
    return new PersistentTrieVector<>(newRootNode, shift, newLength);
  }

  /*
   * NOTE: here you can control if the 'right shadow' is implicit (newRegularPath)
   * or explicit at a higher cost (newRelaxedPath).
   */
  @Override
  public Vector.Immutable<K> pushBack(K item) {
    final int newShift = root.hasFullBack() ? shift + BIT_PARTITION_SIZE : shift;
    final int newLength = length + 1;

    assert implies(newShift > shift, root.hasFullBack());
    assert implies(newShift == shift, !root.hasFullBack());

    if (newShift > shift) {
      final VectorNode<K> newRootNode = VectorNode.of(newShift, length, new VectorNode[]{
          root,
          newRightFringedPath(item, shift)
      }, 1);

      return new PersistentTrieVector<>(newRootNode, newShift, newLength);
    }

    final VectorNode<K> newRootNode = root.pushBack(shift, item);
    return new PersistentTrieVector<>(newRootNode, shift, newLength);
  }

  @Override
  public Vector.Immutable<K> take(int count) {
    if (count <= 0) {
      return EMPTY_VECTOR;
    } else if (count >= size()) {
      return this;
    } else {
      // TODO: shift may incorrect
      final VectorNode<K> newRootNode = root.take(count - 1, count - 1, shift);
      return new PersistentTrieVector<>(newRootNode, shift, count);
    }
  }

  @Override
  public Vector.Immutable<K> drop(int count) {
    if (count <= 0) {
      return this;
    } else if (count >= size()) {
      return EMPTY_VECTOR;
    } else {
      // TODO: shift may incorrect
      final VectorNode<K> newRootNode = root.drop(count, count, shift);
      return new PersistentTrieVector<>(newRootNode, shift, length - count);
    }
  }

  // TODO: simplify
  private static final <K> VectorNode<K> newLeftFringedPath(K item, int shift) {
    if (shift == 0) {
      return new ContentVectorNode<>(new Object[]{item});
    } else {
      final VectorNode[] dst = new VectorNode[]{
          newLeftFringedPath(item, shift - BIT_PARTITION_SIZE)
      };
      return VectorNode.of(shift, 1, dst, 0);
    }
  }

  // TODO: simplify
  private static final <K> VectorNode<K> newRightFringedPath(K item, int shift) {
    if (shift == 0) {
      return new ContentVectorNode<>(new Object[]{item});
    } else {
      final VectorNode[] dst = new VectorNode[]{
          newRightFringedPath(item, shift - BIT_PARTITION_SIZE)
      };
      return VectorNode.of(shift, 0, dst, 1);
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

    Optional<K> get(int index, int remainder, int shift);

    boolean hasRegularFront();

    boolean hasRegularBack();

    boolean hasFullFront();

    boolean hasFullBack();

    VectorNode<K> pushFront(int shift, K item);

    VectorNode<K> pushBack(int shift, K item);

    // TODO: next up: dropFront() and dropFront(int count)
    // TODO: next up: dropBack () and dropBack (int count)

    // TODO: next up: takeFront(int count)
    // TODO: next up: takeBack (int count)

    /**
     *
     * @param index of the last element we consume
     * @param remainder
     * @param shift
     * @return
     */
    VectorNode<K> take(int index, int remainder, int shift);

    /**
     *
     * @param index of the first element that remains
     * @param remainder
     * @param shift
     * @return
     */
    VectorNode<K> drop(int index, int remainder, int shift);

    static <K> VectorNode<K> of(int shiftWitness, int sizeFringeL, VectorNode[] dst, int sizeFringeR) {
      final int normalizedFringeL = (sizeFringeL == 1 << shiftWitness) ? 0 : sizeFringeL;
      final int normalizedFringeR = (sizeFringeR == 1 << shiftWitness) ? 0 : sizeFringeR;

      return new FringedVectorNode<>(normalizedFringeL, dst, normalizedFringeR);
    }

  }

  private static final class FringedVectorNode<K> implements VectorNode<K> {

    private final int sizeFringeL;
    private final VectorNode[] content;
    private final int sizeFringeR;

    private FringedVectorNode(int sizeFringeL, VectorNode[] content, int sizeFringeR) {
      this.sizeFringeL = sizeFringeL;
      this.content = content;
      this.sizeFringeR = sizeFringeR;

      // TODO implement assertions
      // assert content.length >= 2; // TODO: lazy expansion / path compression
      // assert content.length == 2 && sizeFringeL < sizeFringeR;
      assert implies(sizeFringeL != 0, content[0].size() == sizeFringeL);
      assert implies(sizeFringeR != 0, content[content.length - 1 ].size() == sizeFringeR);
    }

    @Override
    public int size() {
      return Stream.of(content).mapToInt(VectorNode::size).sum();
    }

    @Override
    public Optional<K> get(int index, int remainder, int shift) {
      final int blockRelativeIndex;
      final int newRemainder;

      if (sizeFringeL == 0 || remainder < sizeFringeL) {
        // regular
        blockRelativeIndex = mask(remainder, shift, BIT_PARTITION_MASK);
        newRemainder = remainder & ~(BIT_PARTITION_MASK << shift);
      } else {
        // semi-regular
        blockRelativeIndex = 1 + ((remainder - sizeFringeL) >>> shift);
        newRemainder = (remainder - sizeFringeL) & ~(BIT_PARTITION_MASK << shift);
      }

      return content[blockRelativeIndex].get(index, newRemainder, shift - BIT_PARTITION_SIZE);
    }

    @Override
    public boolean hasRegularFront() {
      return sizeFringeL == 0;
    }

    @Override
    public boolean hasRegularBack() {
      return sizeFringeR == 0;
    }

    public boolean hasFullFront() {
      return hasRegularFront() && content.length == BIT_COUNT_OF_INDEX;
    }

    public boolean hasFullBack() {
      return hasRegularBack() && content.length == BIT_COUNT_OF_INDEX;
    }

    @Override
    public VectorNode<K> pushFront(int shift, K item) {
      boolean isSubTreeBranchFull = sizeFringeL == 0;
      boolean isCurrentBranchFull = isSubTreeBranchFull && content.length == BIT_COUNT_OF_INDEX;

      if (!isSubTreeBranchFull) {
        final VectorNode[] dst = copyAndUpdate(VectorNode[]::new, content, 0,
            node -> node.pushFront(shift - BIT_PARTITION_SIZE, item));

        return VectorNode.of(shift, sizeFringeL + 1, dst, sizeFringeR);
      }

      if (!isCurrentBranchFull) {
        final VectorNode[] dst = copyAndInsert(VectorNode[]::new, content, 0,
            newLeftFringedPath(item, shift - BIT_PARTITION_SIZE));

        return VectorNode.of(shift, 1, dst, sizeFringeR);
      }

      throw new IllegalStateException("Prepending not fully implemented.");
    }

    @Override
    public VectorNode<K> pushBack(int shift, K item) {
      boolean isSubTreeBranchFull = sizeFringeR == 0;
      boolean isCurrentBranchFull = isSubTreeBranchFull && content.length == BIT_COUNT_OF_INDEX;

      if (!isSubTreeBranchFull) {
        final VectorNode[] dst = copyAndUpdate(VectorNode[]::new, content, content.length - 1,
            node -> node.pushBack(shift - BIT_PARTITION_SIZE, item));

        return VectorNode.of(shift, sizeFringeL, dst, sizeFringeR + 1);
      }

      if (!isCurrentBranchFull) {
        final VectorNode[] dst = copyAndInsert(VectorNode[]::new, content, content.length,
            newRightFringedPath(item, shift - BIT_PARTITION_SIZE));

        return VectorNode.of(shift, sizeFringeL, dst, 1);
      }

      throw new IllegalStateException("Appending not fully implemented.");
    }

    @Override
    public VectorNode<K> take(int index, int remainder, int shift) {
      final int blockRelativeIndex;
      final int newRemainder;

      final int newSizeFringeL;
      final int newSizeFringeR;

      if (sizeFringeL == 0 || remainder < sizeFringeL) {
        // regular
        blockRelativeIndex = mask(remainder, shift, BIT_PARTITION_MASK);
        newRemainder = remainder & ~(BIT_PARTITION_MASK << shift);
      } else {
        // semi-regular
        blockRelativeIndex = 1 + ((remainder - sizeFringeL) >>> shift);
        newRemainder = (remainder - sizeFringeL) & ~(BIT_PARTITION_MASK << shift);
      }

      final VectorNode[] dst = copyAndTake(VectorNode[]::new, content, blockRelativeIndex,
          node -> node.take(index, newRemainder, shift - BIT_PARTITION_SIZE));

      if (blockRelativeIndex == 0) {
        // first
        // TODO: how to align first blocks to left or right?
        newSizeFringeL = newRemainder + 1;
        newSizeFringeR = 0;
      } else if (blockRelativeIndex < content.length - 1) {
        // middle
        newSizeFringeL = sizeFringeL;
        newSizeFringeR = newRemainder + 1;
      } else {
        // last
        newSizeFringeL = sizeFringeL;
        newSizeFringeR = newRemainder + 1;
      }

      return VectorNode.of(shift, newSizeFringeL, dst, newSizeFringeR);

    }

    @Override
    public VectorNode<K> drop(int index, int remainder, int shift) {
      final int blockRelativeIndex;
      final int newRemainder;

      final int newSizeFringeL;
      final int newSizeFringeR;

      if (sizeFringeL == 0 || remainder < sizeFringeL) {
        // regular
        blockRelativeIndex = mask(remainder, shift, BIT_PARTITION_MASK);
        newRemainder = remainder & ~(BIT_PARTITION_MASK << shift);
      } else {
        // semi-regular
        blockRelativeIndex = 1 + ((remainder - sizeFringeL) >>> shift);
        newRemainder = (remainder - sizeFringeL) & ~(BIT_PARTITION_MASK << shift);
      }

      final VectorNode[] dst = copyAndDrop(VectorNode[]::new, content, blockRelativeIndex,
          node -> node.drop(index, newRemainder, shift - BIT_PARTITION_SIZE));

      if (blockRelativeIndex == 0) {
        // first
        // TODO: how to align first blocks to left or right?
        newSizeFringeL = (sizeFringeL == 0)
            ? (1 << shift) - newRemainder
            : sizeFringeL - newRemainder;
        newSizeFringeR = sizeFringeR;
      } else if (blockRelativeIndex < content.length - 1) {
        // middle
        newSizeFringeL = (1 << shift) - newRemainder;
        newSizeFringeR = sizeFringeR;
      } else {
        // last
        newSizeFringeL = 0;
        newSizeFringeR = (sizeFringeR == 0)
            ? (1 << shift) - newRemainder
            : sizeFringeR - newRemainder;
      }

      return VectorNode.of(shift, newSizeFringeL, dst, newSizeFringeR);
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
    public Optional<K> get(int index, int remainder, int shift) {
      assert shift == 0;
      assert remainder < content.length;

      int blockRelativeIndex = remainder;

      if (blockRelativeIndex >= content.length) {
        return Optional.empty();
      } else {
        return Optional.of((K) content[blockRelativeIndex]);
      }
    }

    public boolean hasRegularFront() {
      return size() == BIT_COUNT_OF_INDEX;
    }

    public boolean hasRegularBack() {
      return size() == BIT_COUNT_OF_INDEX;
    }

    public boolean hasFullFront() {
      return size() == BIT_COUNT_OF_INDEX;
    }

    public boolean hasFullBack() {
      return size() == BIT_COUNT_OF_INDEX;
    }

    @Override
    public VectorNode<K> pushFront(int shift, K item) {
      assert shift == 0;

      final Object[] src = this.content;
      final Object[] dst = copyAndInsert(Object[]::new, src, 0, item);

      return new ContentVectorNode<>(dst);
    }

    @Override
    public VectorNode<K> pushBack(int shift, K item) {
      assert shift == 0;

      final Object[] src = this.content;
      final Object[] dst = copyAndInsert(Object[]::new, src, src.length, item);

      return new ContentVectorNode<>(dst);
    }

    @Override
    public VectorNode<K> take(int index, int remainder, int shift) {
      assert shift == 0;

      final Object[] src = this.content;
      final Object[] dst = copyAndTake(Object[]::new, src, remainder, item -> item);

      return new ContentVectorNode<>(dst);
    }

    @Override
    public VectorNode<K> drop(int index, int remainder, int shift) {
      assert shift == 0;

      final Object[] src = this.content;
      final Object[] dst = copyAndDrop(Object[]::new, src, remainder, item -> item);

      return new ContentVectorNode<>(dst);
    }

  }

}
