/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.capsule.core.trie;

import java.util.ListIterator;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiFunction;

/**
 * Mapper that traverses a trie and converts each node (of {@code SN}) to a node of type {@code DN}.
 */
public class BottomUpTransientNodeTransformer<SN extends Node, DN extends Node> {

  private static final int MAX_DEPTH = 7;

  private final BiFunction<SN, AtomicReference<Thread>, DN> nodeMapper;
  private final AtomicReference<Thread> mutator;
  private final DN dstRootNode;

  private int stackIndex = -1;
  private final ListIterator<SN>[] srcIteratorStack = new ListIterator[MAX_DEPTH];
  private final ListIterator<DN>[] dstIteratorStack = new ListIterator[MAX_DEPTH];

  public BottomUpTransientNodeTransformer(final SN srcRootNode,
      final BiFunction<SN, AtomicReference<Thread>, DN> nodeMapper) {
    this.nodeMapper = nodeMapper;
    this.mutator = new AtomicReference<>(Thread.currentThread());
    this.dstRootNode = nodeMapper.apply(srcRootNode, mutator);

    final ListIterator<SN> srcIterator = (ListIterator<SN>) srcRootNode.nodeArray().iterator();

    if (srcIterator.hasNext()) {
      final ListIterator<DN> dstIterator = (ListIterator<DN>) dstRootNode.nodeArray().iterator();

      pushOnStack(srcIterator, dstIterator);
    }
  }

  public final DN apply() {
    if (!isStackEmpty()) {
      processStack();
    }

    mutator.set(null);
    return dstRootNode;
  }

  private final boolean isStackEmpty() {
    return stackIndex == -1;
  }

  private final void pushOnStack(ListIterator<SN> srcNode, ListIterator<DN> dstNode) {
    // push on stack
    final int nextIndex = ++stackIndex;
    srcIteratorStack[nextIndex] = srcNode;
    dstIteratorStack[nextIndex] = dstNode;
  }

  private final void dropFromStack() {
    // pop from stack
    final int previousIndex = stackIndex--;
    srcIteratorStack[previousIndex] = null;
    dstIteratorStack[previousIndex] = null;
  }

  /*
   * Traverse trie and convert nodes at first encounter. Sub-trie references are updated
   * incrementally throughout iteration.
   */
  private final void processStack() {
    while (!isStackEmpty()) {
      final ListIterator<SN> srcIterator = srcIteratorStack[stackIndex];
      final ListIterator<DN> dstIterator = dstIteratorStack[stackIndex];

      boolean stackModified = false;
      while (!stackModified) {
        if (srcIterator.hasNext()) {
          final SN src = srcIterator.next();
          final DN dst = nodeMapper.apply(src, mutator);

          dstIterator.next();
          dstIterator.set(dst);

          final ListIterator<SN> nextSrcIterator = (ListIterator<SN>) src.nodeArray().iterator();

          if (nextSrcIterator.hasNext()) {
            final ListIterator<DN> nextDstIterator = (ListIterator<DN>) dst.nodeArray().iterator();

            pushOnStack(nextSrcIterator, nextDstIterator);
            stackModified = true;
          }
        } else {
          dropFromStack();
          stackModified = true;
        }
      }
    }
  }

  // /*
  // * search for next node that can be mapped
  // */
  // private final Optional<SN> applyNodeTranformation(boolean yieldIntermediate) {
  // SN result = null;
  //
  // while (stackLevel >= 0 && result == null) {
  // final ListIterator<MN> srcSubNodeIterator = srcIteratorStack[stackLevel];
  // final ListIterator<SN> dstSubNodeIterator = dstIteratorStack[stackLevel];
  //
  // if (srcSubNodeIterator.hasNext()) {
  // final MN nextMapNode = srcSubNodeIterator.next();
  // final SN nextSetNode = nodeMapper.apply(nextMapNode, mutator);
  //
  // dstSubNodeIterator.next();
  // dstSubNodeIterator.set(nextSetNode);
  //
  // final ListIterator<MN> subNodeIterator =
  // (ListIterator<MN>) nextMapNode.nodeArray().iterator();
  //
  // if (subNodeIterator.hasNext()) {
  // // next node == (to process) intermediate node
  // // put node on next stack level for depth-first traversal
  //// final SN nextSetNode = nodeMapper.apply(nextMapNode, mutator);
  //
  // final int nextStackLevel = ++stackLevel;
  // srcIteratorStack[nextStackLevel] = subNodeIterator;
  // dstIteratorStack[nextStackLevel] =
  // (ListIterator<SN>) nextSetNode.nodeArray().iterator();
  // } else if (yieldIntermediate) {
  // // nextNode == (finished) leaf node
  // result = nextSetNode;
  // }
  // } else {
  // if (yieldIntermediate) {
  // // nextNode == (finished) intermidate node
  // // result = setNodes[stackLevel]; // ???
  // throw new IllegalStateException("TODO: figure out how to return previous element.");
  // } else if (stackLevel == 0) {
  // result = setRootNode;
  // }
  //
  // // pop from stack
  // srcIteratorStack[stackLevel] = null;
  // dstIteratorStack[stackLevel] = null;
  // stackLevel--;
  // }
  // }
  //
  // return Optional.ofNullable(result);
  // }

  // @Override
  // public boolean hasNext() {
  // if (next.get().isPresent()) {
  // return true;
  // } else {
  // final Optional<SN> result = applyNodeTranformation(true);
  // next.set(result);
  // return result.isPresent();
  // }
  // }
  //
  // /**
  // * Returns transformed --either internal or leaf-- node.
  // *
  // * @return mapped node
  // */
  // @Override
  // public SN next() {
  // if (!hasNext()) {
  // throw new NoSuchElementException();
  // } else {
  // return next.getAndSet(Optional.empty()).get();
  // }
  // }
  //
  // @Override
  // public void remove() {
  // throw new UnsupportedOperationException();
  // }

}
