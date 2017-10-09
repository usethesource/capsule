/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.capsule.core;

import static io.usethesource.capsule.core.PersistentTrieVector.VectorNode.BIT_COUNT_OF_INDEX;
import static io.usethesource.capsule.core.PersistentTrieVector.VectorNode.BIT_PARTITION_MASK;
import static io.usethesource.capsule.core.PersistentTrieVector.VectorNode.BIT_PARTITION_SIZE;
import static io.usethesource.capsule.util.ArrayUtils.copyAndDrop;
import static io.usethesource.capsule.util.ArrayUtils.copyAndInsert;
import static io.usethesource.capsule.util.ArrayUtils.copyAndRemove;
import static io.usethesource.capsule.util.ArrayUtils.copyAndSet;
import static io.usethesource.capsule.util.ArrayUtils.copyAndTake;
import static io.usethesource.capsule.util.ArrayUtils.copyAndUpdate;
import static io.usethesource.capsule.util.ArrayUtils.merge;
import static io.usethesource.capsule.util.BitmapUtils.bitpos;
import static io.usethesource.capsule.util.BitmapUtils.index;
import static io.usethesource.capsule.util.BitmapUtils.mask;
import static io.usethesource.capsule.util.FunctionUtils.asInstanceOf;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BooleanSupplier;
import java.util.function.IntPredicate;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collector.Characteristics;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import io.usethesource.capsule.Vector;
import io.usethesource.capsule.core.PersistentTrieVector.PathVisitor.Arguments;
import io.usethesource.capsule.util.stream.DefaultCollector;

public class PersistentTrieVector<K> implements Vector.Immutable<K> {

  private static final VectorNode EMPTY_FRINGED_NODE = VectorNode.of(0, 0, new VectorNode[]{}, 0);
  private static final VectorNode EMPTY_NODE = VectorNode.of(0, new Object[]{});

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

  public static final <K> Vector.Immutable<K> of(K item) {
    final VectorNode<K> newRootNode = VectorNode.of(0, new Object[]{item});
    return new PersistentTrieVector<>(newRootNode, 0, 1);
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

  private static final boolean implies(boolean a, BooleanSupplier b) {
    return !a || b.getAsBoolean();
  }

  /*
   * NOTE: the 'left shadow' is always explicit (newRelaxedPath), because by default
   * vectors are left-aligned and right-ragged.
   */
  @Override
  public Vector.Immutable<K> pushFront(K item) {
//    return PersistentTrieVector.of(item).concatenate(this);

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
//    return this.concatenate(PersistentTrieVector.of(item));

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
  public Immutable<K> insertAt(int index, K item) {
    if (index < 0 || index > length) {
      throw new IndexOutOfBoundsException(
          String.format("Index %d out of interval [0,%d]", index, length));
    }

    if (index == 0) {
      return pushFront(item);
    }

    if (index == length) {
      return pushBack(item);
    }

    final Vector.Immutable<K> lhs = take(index);
    final Vector.Immutable<K> rhs = drop(index);

    final Vector.Immutable<K> tmp = lhs.pushBack(item);
    final Vector.Immutable<K> res = tmp.concatenate(rhs);

    return res;
  }

  @Override
  public Vector.Immutable<K> update(int index, K item) {
    if (index < 0 || index >= length) {
      throw new IndexOutOfBoundsException(
          String.format("Index %d out of interval [0,%d)", index, length));
    }

    final VectorNode<K> newRootNode = root.update(index, index, shift, item);
    return new PersistentTrieVector<>(newRootNode, shift, length);
  }

  @Override
  public Vector.Immutable<K> take(int count) {
    if (count <= 0) {
      return EMPTY_VECTOR;
    } else if (count >= size()) {
      return this;
    } else {
      int newShift = shift;
      VectorNode<K> newRootNode = root.take(count - 1, count - 1, shift);

      while (newRootNode.canReduceShift()) {
        newShift -= BIT_PARTITION_SIZE;
        newRootNode = newRootNode.reduceShift();
      }

      return new PersistentTrieVector<>(newRootNode, newShift, count);
    }
  }

  @Override
  public Vector.Immutable<K> drop(int count) {
    if (count <= 0) {
      return this;
    } else if (count >= size()) {
      return EMPTY_VECTOR;
    } else {
      int newShift = shift;
      VectorNode<K> newRootNode = root.drop(count, count, shift);

      while (newRootNode.canReduceShift()) {
        newShift -= BIT_PARTITION_SIZE;
        newRootNode = newRootNode.reduceShift();
      }

      return new PersistentTrieVector<>(newRootNode, newShift, length - count);
    }
  }

  @Override
  public Vector.Immutable<K> concatenate(Vector.Immutable<K> other) {
    if (other instanceof PersistentTrieVector) {
      final PersistentTrieVector<K> that = (PersistentTrieVector<K>) other;

      final Path pathL = this.root.accept(
          new PathVisitor(() -> new Path(this.shift)),
          Arguments.of(this.length - 1, this.length - 1, this.shift));

      final Path pathR = that.root.accept(
          new PathVisitor(() -> new Path(that.shift)),
          Arguments.of(0, 0, that.shift));

      int maxShift = Math.max(this.shift, that.shift);

      final VectorNode<K> nodeL = pathL.nodeAtShift(maxShift)
          .orElse(newLeftProlongedPath(maxShift, pathL.top(), Math.min(pathL.shift, maxShift)));

      final VectorNode<K> nodeR = pathR.nodeAtShift(maxShift)
          .orElse(newRightProlongedPath(maxShift, pathR.top(), Math.min(pathR.shift, maxShift)));

      VectorNode<K> newRootNode = mergeTrees(maxShift, nodeL, nodeR);

      int newShift = Math.max(this.shift, that.shift) + BIT_PARTITION_SIZE;
      int newLength = this.length + that.length;

      while (newRootNode.canReduceShift()) {
        newShift -= BIT_PARTITION_SIZE;
        newRootNode = newRootNode.reduceShift();
      }

      return new PersistentTrieVector<>(newRootNode, newShift, newLength);
    } else {
      throw new UnsupportedOperationException("Not yet implemented.");
    }
  }

  private VectorNode<K> mergeTrees(int shift, VectorNode<K> nodeL, VectorNode<K> nodeR) {
    if (shift == 0) {
      return mergeLeaves(nodeL, nodeR);
    } else {
      final VectorNode<K> nodeM;

      if (shift == 5) {
//        merged = mergeLeaves(nodeL.last(), nodeR.first());
//
//        return mergeAndRebalanceLastTwoLevels(nodeL.init(shift), merged, nodeR.tail(shift));

        nodeM = mergeLeaves(nodeL.last(), nodeR.first());

        return mergeAndRebalanceLastTwoLevels(nodeL, EMPTY_FRINGED_NODE, nodeR);
//      } else if (shift == 10) {
////        merged = mergeTrees(shift - BIT_PARTITION_SIZE, nodeL.last(), nodeR.first());
////
////        return mergeAndRebalanceMiddleLevels(nodeL.init(shift), merged, nodeR.tail(shift));
//
////        final VectorNode<K> merged1 =
////            mergeTrees(shift - BIT_PARTITION_SIZE, nodeL.last(), nodeR.first());
//
//        final VectorNode<K> mergedLeaves = mergeTrees(shift - BIT_PARTITION_SIZE, nodeL.last(),
//            nodeR.first());
//
//        nodeM = mergeAndRebalanceMiddleLevels(EMPTY_FRINGED_NODE, mergedLeaves, nodeR.tail(shift)).reduceShift();
//
//        return mergeAndRebalanceUpperLevels(shift - BIT_PARTITION_SIZE, nodeL.init(shift), nodeM, EMPTY_FRINGED_NODE);
      } else {
        nodeM = mergeTrees(shift - BIT_PARTITION_SIZE, nodeL.last(), nodeR.first());

        // assert merged.size() == nodeL.last().size() + nodeR.last().size();

        return mergeAndRebalanceUpperLevels(shift - BIT_PARTITION_SIZE, nodeL.init(shift), nodeM, nodeR.tail(shift));
      }

    }
  }

  private VectorNode<K> mergeLeaves(VectorNode<K> nodeL, VectorNode<K> nodeR) {
    final ContentVectorNode<K> leafL = (ContentVectorNode<K>) nodeL;
    final ContentVectorNode<K> leafR = (ContentVectorNode<K>) nodeR;

    if (leafL.content.length == 32) {
      return VectorNode
          .of(BIT_PARTITION_SIZE, 0, new VectorNode[]{leafL, leafR}, leafR.content.length);
    } else {
      int totalSize = leafL.content.length + leafR.content.length;

      if (totalSize > BIT_COUNT_OF_INDEX) {
        final int newSizeL = BIT_COUNT_OF_INDEX;
        final int newSizeR = totalSize - newSizeL;

        final Object[] contentL = new Object[newSizeL];
        System.arraycopy(leafL.content, 0, contentL, 0, leafL.content.length);
        System.arraycopy(leafR.content, 0, contentL, leafL.content.length,
            BIT_COUNT_OF_INDEX - leafL.content.length);

        final Object[] contentR = new Object[newSizeR];
        System.arraycopy(leafR.content, leafR.content.length - newSizeR, contentR, 0, newSizeR);

        final VectorNode<K> newLeafL = VectorNode.of(0, contentL);
        final VectorNode<K> newLeafR = VectorNode.of(0, contentR);

        return VectorNode
            .of(BIT_PARTITION_SIZE, newSizeL, new VectorNode[]{newLeafL, newLeafR}, newSizeR);
      } else {
        final int newSizeR = totalSize;

        final Object[] contentR = new Object[newSizeR];
        System.arraycopy(leafL.content, 0, contentR, 0, leafL.content.length);
        System.arraycopy(leafR.content, 0, contentR, leafL.content.length,
            newSizeR - leafL.content.length);

        final VectorNode<K> newLeafR = VectorNode.of(0, contentR);

        return VectorNode
            .of(BIT_PARTITION_SIZE, 0, new VectorNode[]{newLeafR}, newSizeR);
      }
    }
  }

  private VectorNode<K> mergeAndRebalanceLastTwoLevels(
      VectorNode<K> nodeL, VectorNode<K> nodeM, VectorNode<K> nodeR) {

    return mergeAndRebalanceLastTwoLevels(
        (FringedVectorNode<K>) nodeL,
        (FringedVectorNode<K>) nodeM,
        (FringedVectorNode<K>) nodeR);
  }

  private VectorNode<K> mergeAndRebalanceLastTwoLevels(
      FringedVectorNode<K> nodeL, FringedVectorNode<K> nodeM, FringedVectorNode<K> nodeR) {

    VectorNode[] merged = merge(VectorNode[]::new, nodeL.content, nodeM.content, nodeR.content);

    VectorNode[] mergedMR = merge(VectorNode[]::new, nodeM.content, nodeR.content);

    int segmentCountLevel1 = segmentCount(merged.length);

    Object[] items =
        Stream.of(merged)
            .map(asInstanceOf(ContentVectorNode.class))
            .flatMap(leafNode -> Stream.of(leafNode.content))
            .toArray();

    int segmentCountLevel0 = segmentCount(items.length);

    VectorNode<?> possibleResult = Stream.of(items).collect(toVectorNodeLeaf());

    return (VectorNode<K>) possibleResult;
  }

  @Deprecated
  private VectorNode<K> mergeAndRebalanceMiddleLevels(
      VectorNode<K> nodeL, VectorNode<K> nodeM, VectorNode<K> nodeR) {

    return mergeAndRebalanceMiddleLevels(
        (FringedVectorNode<K>) nodeL,
        (FringedVectorNode<K>) nodeM,
        (FringedVectorNode<K>) nodeR);
  }

  @Deprecated
  private VectorNode<K> mergeAndRebalanceMiddleLevels(
      FringedVectorNode<K> nodeL, FringedVectorNode<K> nodeM, FringedVectorNode<K> nodeR) {

    VectorNode[] merged = merge(VectorNode[]::new, nodeL.content, nodeM.content, nodeR.content);

    int segmentCountLevel1 = segmentCount(merged.length);

    Object[] items =
        Stream.of(merged)
            .map(asInstanceOf(FringedVectorNode.class))
            .flatMap(treeNode -> Stream.of(treeNode.content))
            .map(asInstanceOf(ContentVectorNode.class))
            .flatMap(leafNode -> Stream.of(leafNode.content))
            .toArray();

    int segmentCountLevel0 = segmentCount(items.length);

    VectorNode<?> possibleResult = Stream.of(items).collect(toVectorNodeLeaf());

    // return VectorNode.of(15, new VectorNode<K>[] {(VectorNode<K>) possibleResult});

    return newLeftProlongedPath(15, (VectorNode<K>) possibleResult, 10);

    // return (VectorNode<K>) possibleResult;
  }

  static class LeafPrototype<K> {

    private ArrayList<VectorNode<K>> rootBuffer;
    private ArrayList<VectorNode<K>> treeBuffer;
    private ArrayList<K> leafBuffer;

    LeafPrototype() {
      rootBuffer = new ArrayList<>(BIT_COUNT_OF_INDEX);
      treeBuffer = new ArrayList<>(BIT_COUNT_OF_INDEX);
      leafBuffer = new ArrayList<>(BIT_COUNT_OF_INDEX);
    }

    public void add(K item) {

      // overflow level 0?
      if (leafBuffer.size() == BIT_COUNT_OF_INDEX) {
        VectorNode<K> nextLeaf = VectorNode.of(0, leafBuffer.toArray());

        treeBuffer.add(nextLeaf);
        leafBuffer.clear();
      }

      // overflow level 1?
      if (treeBuffer.size() == BIT_COUNT_OF_INDEX) {
        final VectorNode[] treeContent = treeBuffer.toArray(new VectorNode[treeBuffer.size()]);
        VectorNode<K> nextTree = VectorNode.of(BIT_PARTITION_SIZE, 0, treeContent, 0);

        rootBuffer.add(nextTree);
        treeBuffer.clear();
      }

      // add to level 0
      leafBuffer.add(item);
    }

    public VectorNode<K> result() {
      int sizeFringeR = 0;

      if (leafBuffer.size() > 0) {
        final VectorNode<K> lastLeaf = VectorNode.of(0, leafBuffer.toArray());
        treeBuffer.add(lastLeaf);

        sizeFringeR = leafBuffer.size();
      }

      if (treeBuffer.size() > 0) {
        final VectorNode[] treeContent = treeBuffer.toArray(new VectorNode[treeBuffer.size()]);
        final VectorNode<K> lastTree = VectorNode
            .of(5, 0, treeContent, treeContent[treeContent.length - 1].size());
        rootBuffer.add(lastTree);
      }

      final VectorNode[] rootContent = rootBuffer.toArray(new VectorNode[rootBuffer.size()]);
      final VectorNode<K> rootNode = VectorNode
          .of(10, 0, rootContent, rootContent[rootContent.length - 1].size());

      rootBuffer.clear();
      treeBuffer.clear();
      leafBuffer.clear();

      return rootNode;
    }

  }

  static class TreePrototype<K> {

    private final int shiftBaseline;

    private ArrayList<VectorNode<K>> rootBuffer;
    private ArrayList<VectorNode<K>> treeBuffer;
    private ArrayList<VectorNode<K>> leafBuffer;

    private int leafFringeL;
    private int leafFringeR;

    TreePrototype(int shiftBaseline) {
      this.shiftBaseline = shiftBaseline;

      this.rootBuffer = new ArrayList<>(BIT_COUNT_OF_INDEX);
      this.treeBuffer = new ArrayList<>(BIT_COUNT_OF_INDEX);
      this.leafBuffer = new ArrayList<>(BIT_COUNT_OF_INDEX);
    }

    private VectorNode<K> previousLeaf() {
      return leafBuffer.get(leafBuffer.size() - 1);
    }

    public void add(VectorNode<K> item) {
      // overflow level 0?

      switch (leafBuffer.size()) {
        case 00: {
          leafBuffer.add(item);
          leafFringeL += item.sizeFringeL();
          break;
        }
        case 01: {
          /* NOTE: does not work b/c of ContentNode not committing to left or right balancing. */

//          // assert previousLeaf().hasRegularBack();
//          assert item.hasRegularFront();

          leafBuffer.add(item);
          leafFringeL += item.sizeFringeL();
          break;
        }
        case 31: {
//          assert item.hasRegularFront();
//          // assert item.hasRegularBack();

          leafBuffer.add(item);
          leafFringeR += item.sizeFringeR();
          break;
        }
        case 32: {
          final VectorNode[] leafContent = leafBuffer.toArray(new VectorNode[leafBuffer.size()]);

          // TODO: what is sizeFringeL?
          // TODO: what is sizeFringeR?
          // VectorNode<K> nextLeaf = VectorNode.of(shiftBaseline + 0 * BIT_PARTITION_SIZE, 0, leafContent, 0);
          VectorNode<K> nextLeaf = calculateSizes(shiftBaseline + 0 * BIT_PARTITION_SIZE, leafContent);

          treeBuffer.add(nextLeaf);
          leafBuffer.clear();
          leafFringeL = 0;
          leafFringeR = 0;

          // add to level 0
          leafBuffer.add(item);
          leafFringeL += item.sizeFringeL();

          // overflow level 1?
          if (treeBuffer.size() == BIT_COUNT_OF_INDEX) {
            final VectorNode[] treeContent = treeBuffer.toArray(new VectorNode[treeBuffer.size()]);

            // TODO: what is sizeFringeL?
            // TODO: what is sizeFringeR?
            // VectorNode<K> nextTree = VectorNode.of(shiftBaseline + 1 * BIT_PARTITION_SIZE, 0, treeContent, 0);
            VectorNode<K> nextTree = calculateSizes(shiftBaseline + 1 * BIT_PARTITION_SIZE, treeContent);

            rootBuffer.add(nextTree);
            treeBuffer.clear();

          }
          break;
        }
        default: {
//          assert previousLeaf().hasRegularBack();
//          assert item.hasRegularFront();

          // add to level 0
          leafBuffer.add(item);
        }
      }

//      if (leafBuffer.size() == BIT_COUNT_OF_INDEX) {
//        final VectorNode[] leafContent = leafBuffer.toArray(new VectorNode[leafBuffer.size()]);
//
//        // TODO: what is sizeFringeL?
//        // TODO: what is sizeFringeR?
//        VectorNode<K> nextLeaf = VectorNode.of(shiftBaseline + 0 * BIT_PARTITION_SIZE, 0, leafContent, 0);
//
//        treeBuffer.add(nextLeaf);
//        leafBuffer.clear();
//        leafFringeL = 0;
//        leafFringeR = 0;
//
//        // add to level 0
//        leafBuffer.add(item);
//        leafFringeL += item.sizeFringeL();
//
//        // overflow level 1?
//        if (treeBuffer.size() == BIT_COUNT_OF_INDEX) {
//          final VectorNode[] treeContent = treeBuffer.toArray(new VectorNode[treeBuffer.size()]);
//
//          // TODO: what is sizeFringeL?
//          // TODO: what is sizeFringeR?
//          VectorNode<K> nextTree = VectorNode.of(shiftBaseline + 1 * BIT_PARTITION_SIZE, 0, treeContent, 0);
//
//          rootBuffer.add(nextTree);
//          treeBuffer.clear();
//
//        }
//      } else {
//        assert item.hasRegularFront();
//        assert item.hasRegularBack();
//
//        // add to level 0
//        leafBuffer.add(item);
//      }
    }

    public VectorNode<K> result() {
      if (leafBuffer.size() > 0) {
//        final VectorNode<K> lastLeaf = VectorNode.of(leafBuffer.toArray());
//        treeBuffer.add(lastLeaf);

        final VectorNode[] leafContent = leafBuffer.toArray(new VectorNode[leafBuffer.size()]);

        // TODO: what is sizeFringeL?
        // TODO: what is sizeFringeR?
        // final VectorNode<K> lastLeaf = VectorNode.of(shiftBaseline + 0 * BIT_PARTITION_SIZE, 0, leafContent, 0); // leafContent[leafContent.length - 1
        final VectorNode<K> lastLeaf = calculateSizes(shiftBaseline + 0 * BIT_PARTITION_SIZE, leafContent);

        treeBuffer.add(lastLeaf);
      }

      if (treeBuffer.size() > 0) {
        final VectorNode[] treeContent = treeBuffer.toArray(new VectorNode[treeBuffer.size()]);

        // TODO: what is sizeFringeL?
        // TODO: what is sizeFringeR?
//        final VectorNode<K> lastTree = VectorNode
//            .of(shiftBaseline + 1 * BIT_PARTITION_SIZE, 0, treeContent, treeContent[treeContent.length - 1].size());
        final VectorNode<K> lastTree = calculateSizes(shiftBaseline + 1 * BIT_PARTITION_SIZE, treeContent);

        rootBuffer.add(lastTree);
      }

      final VectorNode[] rootContent = rootBuffer.toArray(new VectorNode[rootBuffer.size()]);

      // TODO: what is sizeFringeL?
      // TODO: what is sizeFringeR?
//      final VectorNode<K> rootNode = VectorNode
//          .of(shiftBaseline + 2 * BIT_PARTITION_SIZE, 0, rootContent, rootContent[rootContent.length - 1].size());
      final VectorNode<K> rootNode = calculateSizes(shiftBaseline + 2 * BIT_PARTITION_SIZE, rootContent);

      rootBuffer.clear();
      treeBuffer.clear();
      leafBuffer.clear();

      return rootNode;
    }

  }

  private static <T, A> Collector<T, ?, VectorNode<T>> toVectorNodeLeaf() {

    final BiConsumer<LeafPrototype<T>, T> accumulator = (prototype, item) -> prototype.add(item);

    return new DefaultCollector<>(
        () -> new LeafPrototype<T>(),
        accumulator,
        (left, right) -> {
          throw new UnsupportedOperationException("Not yet implemented.");
        },
        LeafPrototype::result,
        EnumSet.noneOf(Characteristics.class));
  }

  private static <T, A> Collector<VectorNode<T>, ?, VectorNode<T>> toVectorNodeInner(int shiftBaseline) {

    final BiConsumer<TreePrototype<T>, VectorNode<T>> accumulator =
        (prototype, item) -> prototype.add(item);

    return new DefaultCollector<>(
        () -> new TreePrototype<T>(shiftBaseline),
        accumulator,
        (left, right) -> {
          throw new UnsupportedOperationException("Not yet implemented.");
        },
        TreePrototype::result,
        EnumSet.noneOf(Characteristics.class));
  }

  private VectorNode<K> mergeAndRebalanceUpperLevels(int shiftBaseline,
      VectorNode<K> nodeL, VectorNode<K> nodeM, VectorNode<K> nodeR) {

    return mergeAndRebalanceUpperLevels(shiftBaseline,
        (FringedVectorNode<K>) nodeL,
        (FringedVectorNode<K>) nodeM,
        (FringedVectorNode<K>) nodeR);
  }

  private VectorNode<K> mergeAndRebalanceUpperLevels(int shiftBaseline,
      FringedVectorNode<K> nodeL, FringedVectorNode<K> nodeM, FringedVectorNode<K> nodeR) {

//    assert nodeL.sizeFringeR == 0 || nodeL.content.length == 1;
//    assert nodeM.sizeFringeL == 0 || nodeM.content.length == 1;

    final VectorNode[] merged = merge(VectorNode[]::new, nodeL.content, nodeM.content, nodeR.content);

//    final VectorNode[] merged;
//
//    if (nodeM.sizeFringeR == 0) {
//      merged = merge(VectorNode[]::new, nodeL.content, nodeM.content, nodeR.content);
//    } else {
//      assert shiftBaseline == 5;
//
//      FringedVectorNode<K> nodeMR = (FringedVectorNode<K>)
//          mergeAndRebalanceUpperLevels(shiftBaseline, EMPTY_FRINGED_NODE, nodeM, nodeR).reduceShift();
//
//      merged = merge(VectorNode[]::new, nodeL.content, nodeMR.content);
//    }

    // VectorNode[] merged = merge(VectorNode[]::new, nodeL.content, nodeM.content, nodeR.content);

    // VectorNode[] mergedMR = merge(VectorNode[]::new, nodeM.content, nodeR.content);

    int segmentCountLevel1 = segmentCount(merged.length);

    VectorNode<K>[] items =
        Stream.of(merged)
            .map(asInstanceOf(FringedVectorNode.class))
            .flatMap(treeNode -> Stream.of(treeNode.content))
            .toArray(VectorNode[]::new);

    int segmentCountLevel0 = segmentCount(items.length);

    VectorNode<K> possibleResult = Stream.of(items).collect(toVectorNodeInner(shiftBaseline));

    return possibleResult;
  }

  private int segmentCount(int length) {
    int fullSegmentCount = length >>> BIT_PARTITION_SIZE;
    int halfSegmentCount = (length & BIT_PARTITION_MASK) != 0 ? 1 : 0;

    int segmentCount = fullSegmentCount + halfSegmentCount;
    return segmentCount;
  }

  static class Path {

    private final int shift;
    private final VectorNode[] nodes;

    Path(int shift) {
      this(shift, new VectorNode[(shift / BIT_PARTITION_SIZE) + 1]);
    }

    Path(int shift, VectorNode[] path) {
      this.shift = shift;
      this.nodes = path;
    }

    Optional<VectorNode> nodeAtShift(int shift) {
      int index = shift / BIT_PARTITION_SIZE;

      if (0 <= index && index < nodes.length) {
        return Optional.of(nodes[index]);
      } else {
        return Optional.empty();
      }
    }

    void put(int shift, VectorNode item) {
      int index = shift / BIT_PARTITION_SIZE;
      nodes[index] = item;
    }

    VectorNode top() {
      return nodes[nodes.length - 1];
    }

//    void pushFront(VectorNode node) {
//
//    }
//
//    void pushBack(VectorNode node) {
//
//    }

  }

  // TODO: simplify
  private static final <K> VectorNode<K> newLeftProlongedPath(int shift, VectorNode<K> node, int shiftAtNode) {
    assert shift >= 0;
    assert shift >= shiftAtNode;

    if (shift == shiftAtNode) {
      return node;
    } else {
      final VectorNode[] dst = new VectorNode[]{
          newLeftProlongedPath(shift - BIT_PARTITION_SIZE, node, shiftAtNode)
      };
      final VectorNode<K> newNode = VectorNode.of(shift, 0, dst, node.size());

      return newNode;
    }
  }

  // TODO: simplify
  private static final <K> VectorNode<K> newRightProlongedPath(int shift, VectorNode<K> node, int shiftAtNode) {
    assert shift >= 0;
    assert shift >= shiftAtNode;

    if (shift == shiftAtNode) {
      return node;
    } else {
      final VectorNode[] dst = new VectorNode[]{
          newRightProlongedPath(shift - BIT_PARTITION_SIZE, node, shiftAtNode)
      };
      final VectorNode<K> newNode = VectorNode.of(shift, node.size(), dst, 0);

      return newNode;
    }
  }

  // TODO: simplify
  private static final <K> VectorNode<K> newLeftFringedPath(K item, int shift) {
    if (shift == 0) {
      return VectorNode.of(0, new Object[]{item});
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
      return VectorNode.of(0, new Object[]{item});
    } else {
      final VectorNode[] dst = new VectorNode[]{
          newRightFringedPath(item, shift - BIT_PARTITION_SIZE)
      };
      return VectorNode.of(shift, 0, dst, 1);
    }
  }

  interface NodeVisitor<R, A, E extends Throwable> {

    R visitFringedNode(FringedVectorNode node, A args); // throws E

    R visitLeafNode(ContentVectorNode node, A args); // throws E

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

    int sizeFringeL();

    int sizeFringeR();

    boolean canReduceShift();

    VectorNode<K> reduceShift();

    VectorNode<K> pushFront(int shift, K item);

    VectorNode<K> pushBack(int shift, K item);

    VectorNode<K> update(int index, int remainder, int shift, K item);

    // TODO: next up: dropFront() and dropFront(int count)
    // TODO: next up: dropBack () and dropBack (int count)

    // TODO: next up: takeFront(int count)
    // TODO: next up: takeBack (int count)

    /**
     * @param index of the last element we consume
     */
    VectorNode<K> take(int index, int remainder, int shift);

    /**
     * @param index of the first element that remains
     */
    VectorNode<K> drop(int index, int remainder, int shift);

    default VectorNode<K> first() {
      return null;
    }

    default VectorNode<K> last() {
      return null;
    }

    default VectorNode<K> init(int shift) {
      return null;
    }

    default VectorNode<K> tail(int shift) {
      return null;
    }

    <T, A, E extends Throwable> T accept(NodeVisitor<T, A, E> visitor, A args); // throws E

    static <K> VectorNode<K> of(int shiftWitness, Object[] dst) {
      return new ContentVectorNode<>(dst);
    }

    static <K> VectorNode<K> of(int shiftWitness, int sizeFringeL, VectorNode[] dst,
        int sizeFringeR) {

      assert sizeFringeL <= 1 << shiftWitness;
      assert sizeFringeR <= 1 << shiftWitness;

      final int normalizedFringeL = (sizeFringeL == 1 << shiftWitness) ? 0 : sizeFringeL;
      final int normalizedFringeR = (sizeFringeR == 1 << shiftWitness) ? 0 : sizeFringeR;

//      // TODO: do not support empty nodes mid-tree
//      if (dst.length == 0) {
//        // assert shiftWitness == 0;
//        assert sizeFringeL == 0;
//        assert sizeFringeR == 0;
//
//        return EMPTY_NODE;
//      }

      // return new FringedVectorNode<>(normalizedFringeL, dst, normalizedFringeR);
      return calculateSizes(shiftWitness, dst);
    }

  }

  private final static int[] copyAndSum(int[] src) {
    final int[] dst = new int[src.length];

    int cumulativeSum = 0;
    for (int i = 0; i < src.length; i++) {
      dst[i] = (cumulativeSum += src[i]);
    }
    return dst;
  }

  private final static <K> FringedVectorNode<K> calculateSizes(int shift, VectorNode[] content) {

    final int[] contentSizesSingle =
        IntStream.range(0, content.length).map(i -> content[i].size()).toArray();

    final int[] contentSizesSummed = copyAndSum(contentSizesSingle);

    final IntPredicate isFull = (i) -> contentSizesSingle[i] == 1 << shift;

//    final int[] indices = IntStream.range(0, content.length).filter(isFull.negate()).toArray();
//
//    final int[] compactedSizesSingle =
//        IntStream.of(indices).map(i -> contentSizesSingle[i]).toArray();
//
//    final int[] compactedSizesSummed =
//        IntStream.of(indices).map(i -> contentSizesSummed[i]).toArray();
//
//    final int compactedSizemap =
//        IntStream.of(indices).map(i -> 1 << i).reduce(0, (x, y) -> x | y);

    final int compactedSizemap =
        IntStream.range(0, content.length)
            .filter(isFull.negate())
            .map(i -> 1 << i)
            .reduce(0, (x, y) -> x | y);

    final int l;
    final int r;

    if (content.length == 0) {
      l = 0;
      r = 0;
    } else {
      // NOTE: nodes of size 1 have {@code l == r}; TODO: make invariant?
      l = contentSizesSingle[0];
      r = contentSizesSingle[content.length - 1];

//      l = content[0].sizeFringeL();
//      r = content[content.length - 1].sizeFringeR();
    }

    final int sizeFringeL = (l == 1 << shift) ? 0 : l;
    final int sizeFringeR = (r == 1 << shift) ? 0 : r;

//    if (content.length != 0) {
////      assert (1 << shift) - (1 << shift - BIT_COUNT_OF_INDEX) + sizeFringeL
////          == contentSizesSingle[0] % (1 << shift);
//////          == content[0].sizeFringeL();
////
////      assert (1 << shift) - (1 << shift - BIT_COUNT_OF_INDEX) + sizeFringeR
////          == contentSizesSingle[content.length - 1] % (1 << shift);
//////          == content[content.length - 1].sizeFringeR();
//
//      assert sizeFringeL == content[0].sizeFringeL();
//      assert sizeFringeR == content[content.length - 1].sizeFringeR();
//    }

    return new FringedVectorNode<K>(sizeFringeL, compactedSizemap, sizeFringeR, contentSizesSingle, contentSizesSummed, content);
  }

  private static final class FringedVectorNode<K> implements VectorNode<K> {

    private static final int[] EMPTY_SIZES = new int[0];

    private final int sizeFringeL;
    private final VectorNode[] content;
    private final int sizeFringeR;

    private int sizemap = 0;
    private int[] sizesSingle = EMPTY_SIZES;
    private int[] sizesSummed = EMPTY_SIZES;

    private FringedVectorNode(int sizeFringeL, VectorNode[] content, int sizeFringeR) {
      assert implies(sizeFringeL == 32, () -> !(content[0] instanceof ContentVectorNode && content[0].size() == 32));

      this.sizeFringeL = sizeFringeL;
      this.content = content;
      this.sizeFringeR = sizeFringeR;

      // TODO implement assertions
      // assert content.length >= 2; // TODO: lazy expansion / path compression
      // assert content.length == 2 && sizeFringeL < sizeFringeR;
      assert implies(content.length == 0, sizeFringeL == 0 && sizeFringeR == 0);
      assert implies(sizeFringeL != 0, content.length > 0 && content[0].size() == sizeFringeL);
      assert implies(sizeFringeR != 0, content.length > 0 && content[content.length - 1].size() == sizeFringeR);
//      assert implies(sizeFringeL != 0, content.length > 0 && content[0].sizeFringeL() == sizeFringeL);
//      assert implies(sizeFringeR != 0, content.length > 0 && content[content.length - 1].sizeFringeR() == sizeFringeR);
    }

    private FringedVectorNode(int l, int b, int r, int[] sizesSingle, int[] sizesSummed, VectorNode[] content) {
      this(l, content, r);

      this.sizemap = b;
      this.sizesSingle = sizesSingle;
      this.sizesSummed = sizesSummed;

      // TODO: define invariants with asserts
    }

    /*
     * TODO: improve performance (binary search, etc)
     */
    private final static int offset(int[] cumulativeSizes, int index) {
      for (int i = 0; i < cumulativeSizes.length; i++) {
        if (cumulativeSizes[i] > index) {
          return i;
        }
      }
      throw new IndexOutOfBoundsException("Index larger than subtree.");
    }

    private int lazySize = 0;

    @Override
    public int size() {
      if (lazySize == 0) {
//        final int size;
//
//        if (sizemap == 0) {
//          size = ???;
//        } else {
//          size = Stream.of(content).mapToInt(VectorNode::size).sum();
//        }
//
//        lazySize = size;

        if (content.length == 0) {
          lazySize = 0;
        } else {
          lazySize = sizesSummed[content.length - 1];
        }

//        lazySize = Stream.of(content).mapToInt(VectorNode::size).sum();
      }

      return lazySize;
    }

    /*
     * TODO: unify with {@code #update}, only recursive function call differs.
     */
    @Override
    public Optional<K> get(int index, int remainder, int shift) {
      assert 0 <= index;
      assert 0 <= remainder;

      final boolean isFullRegular;
      final boolean isSemiRegular;

      if (content.length < 2) {
        // first and last node equal
        isFullRegular = 0 == sizemap;
        isSemiRegular = 1 == sizemap;
      } else {
        // ignore size of last node
        isFullRegular = 0 == (sizemap & ~((1 << (content.length - 1))));
        isSemiRegular = 1 == (sizemap & ~((1 << (content.length - 1))));
      }

      assert implies(isFullRegular, sizeFringeL == 0);
      assert implies(isSemiRegular, sizeFringeL != 0);

      final int blockRelativeIndex;
      final int newRemainder;

      final int __mask = mask(remainder, shift, BIT_PARTITION_MASK);
      final int __index = index(sizemap, __mask, bitpos(__mask));
      final boolean isEffectivelyRegular =
          (sizemap & bitpos(__mask)) == 0 && __index == 0;

      if (isFullRegular || isEffectivelyRegular || remainder < sizeFringeL) {
        // regular (or in first sub-tree)
        blockRelativeIndex = mask(remainder, shift, BIT_PARTITION_MASK);
        newRemainder = remainder & ~(BIT_PARTITION_MASK << shift);

        return content[blockRelativeIndex].get(index, newRemainder, shift - BIT_PARTITION_SIZE);
      } else if (isSemiRegular) {
        // semi-regular
        // TODO: support {@code isEffectivelyRegular} in semi-regular
        blockRelativeIndex = 1 + ((remainder - sizeFringeL) >>> shift);
        newRemainder = (remainder - sizeFringeL) & ~(BIT_PARTITION_MASK << shift);

        return content[blockRelativeIndex].get(index, newRemainder, shift - BIT_PARTITION_SIZE);
      } else {
        // irregular
        blockRelativeIndex = offset(sizesSummed, remainder);
        newRemainder = (blockRelativeIndex == 0)
            ? remainder
            : remainder - sizesSummed[blockRelativeIndex - 1];

        return content[blockRelativeIndex].get(index, newRemainder, shift - BIT_PARTITION_SIZE);
      }
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
    public int sizeFringeL() {
      return sizeFringeL;
    }

    @Override
    public int sizeFringeR() {
      return sizeFringeR;
    }

    @Override
    public boolean canReduceShift() {
      return content.length == 1;
    }

    @Override
    public VectorNode<K> reduceShift() {
      if (canReduceShift()) {
        return content[0];
      } else {
        return this; // no-op
      }
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

    /*
     * TODO: unify with {@code #get}, only recursive function call differs.
     */
    @Override
    public VectorNode<K> update(int index, int remainder, int shift, K item) {
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

      VectorNode<K>[] newContent = copyAndUpdate(VectorNode[]::new, content, blockRelativeIndex,
          node -> node.update(index, newRemainder, shift - BIT_PARTITION_SIZE, item));

      return VectorNode.of(shift, sizeFringeL, newContent, sizeFringeR);
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

    @Override
    public VectorNode<K> first() {
      return content[0];
    }

    @Override
    public VectorNode<K> last() {
      return content[content.length - 1];
    }

    @Override
    public VectorNode<K> init(int shift) {
      final VectorNode[] dst = copyAndRemove(VectorNode[]::new, content, content.length - 1);

      return VectorNode.of(shift, sizeFringeL, dst, 0);
    }

    @Override
    public VectorNode<K> tail(int shift) {
      final VectorNode[] dst = copyAndRemove(VectorNode[]::new, content, 0);

      return VectorNode.of(shift, 0, dst, sizeFringeR);
    }

    @Override
    public <T, A, E extends Throwable> T accept(NodeVisitor<T, A, E> visitor, A args) { // throws E
      return visitor.visitFringedNode(this, args);
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
    public int sizeFringeL() {
      return size() % BIT_COUNT_OF_INDEX;
    }

    @Override
    public int sizeFringeR() {
      return size() % BIT_COUNT_OF_INDEX;
    }

    @Override
    public boolean canReduceShift() {
      return false;
    }

    @Override
    public VectorNode<K> reduceShift() {
      return this; // no-op
    }

    @Override
    public VectorNode<K> pushFront(int shift, K item) {
      assert shift == 0;

      final Object[] src = this.content;
      final Object[] dst = copyAndInsert(Object[]::new, src, 0, item);

      // TODO: correct?
      return VectorNode.of(0, dst);
    }

    @Override
    public VectorNode<K> pushBack(int shift, K item) {
      assert shift == 0;

      final Object[] src = this.content;
      final Object[] dst = copyAndInsert(Object[]::new, src, src.length, item);

      // TODO: correct?
      return VectorNode.of(0, dst);
    }

    @Override
    public VectorNode<K> update(int index, int remainder, int shift, K item) {
      assert shift == 0;

      final Object[] src = this.content;
      final Object[] dst = copyAndSet(Object[]::new, src, remainder, item);

      return VectorNode.of(0, dst);
    }

    @Override
    public VectorNode<K> take(int index, int remainder, int shift) {
      assert shift == 0;

      final Object[] src = this.content;
      final Object[] dst = copyAndTake(Object[]::new, src, remainder, item -> item);

      return VectorNode.of(0, dst);
    }

    @Override
    public VectorNode<K> drop(int index, int remainder, int shift) {
      assert shift == 0;

      final Object[] src = this.content;
      final Object[] dst = copyAndDrop(Object[]::new, src, remainder, item -> item);

      return VectorNode.of(0, dst);
    }

    @Override
    public <T, A, E extends Throwable> T accept(NodeVisitor<T, A, E> visitor, A args) { // throws E
      return visitor.visitLeafNode(this, args);
    }

  }

  static class PathVisitor implements NodeVisitor<Path, PathVisitor.Arguments, Throwable> {

    private final Path path;

    PathVisitor(Supplier<Path> path) {
      this.path = path.get();
    }

    static class Arguments {

      final int index;
      final int remainder;
      final int shift;

      Arguments(int index, int remainder, int shift) {
        this.index = index;
        this.remainder = remainder;
        this.shift = shift;
      }

      static Arguments of(int index, int remainder, int shift) {
        return new Arguments(index, remainder, shift);
      }

    }

    @Override
    public Path visitFringedNode(FringedVectorNode node, PathVisitor.Arguments args) {
      path.put(args.shift, node); // mutable update

      final int blockRelativeIndex;
      final int newRemainder;

      if (node.sizeFringeL == 0 || args.remainder < node.sizeFringeL) {
        // regular
        blockRelativeIndex = mask(args.remainder, args.shift, BIT_PARTITION_MASK);
        newRemainder = args.remainder & ~(BIT_PARTITION_MASK << args.shift);
      } else {
        // semi-regular
        blockRelativeIndex = 1 + ((args.remainder - node.sizeFringeL) >>> args.shift);
        newRemainder = (args.remainder - node.sizeFringeL) & ~(BIT_PARTITION_MASK << args.shift);
      }

      final VectorNode subNode = node.content[blockRelativeIndex];

      final Arguments subArgs = Arguments
          .of(args.index, newRemainder, args.shift - BIT_PARTITION_SIZE);

      return (Path) subNode.accept(this, subArgs);
    }

    @Override
    public Path visitLeafNode(ContentVectorNode node, PathVisitor.Arguments args) {
      path.put(args.shift, node); // mutable update

      return path;
    }

  }

}
