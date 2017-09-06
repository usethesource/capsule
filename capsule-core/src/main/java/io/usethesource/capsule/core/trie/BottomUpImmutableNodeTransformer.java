/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.capsule.core.trie;

import java.util.ListIterator;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiFunction;
import java.util.function.IntFunction;

/**
 * Bottom Up Trie Transformer, e.g., for combined mapping plus canonicalization of the tree.
 *
 * TODO: finish implementation (WIP converting from transient to immutable transformer)
 */
public class BottomUpImmutableNodeTransformer<SN extends Node, DN extends Node> {

  private static final int MAX_DEPTH = 7;

  //  private final BiFunction<SN, AtomicReference<Thread>, DN> nodeMapper;
  private final BiFunction<SN, DN[], DN> nodeMapper;
  private final AtomicReference<Thread> mutator;
  private final DN dstRootNode;

  private final IntFunction<DN[]> arrayConstructor;

  static final <SN extends Node, DN extends Node> DN applyNodeTransformation(final SN rootNode,
      final BiFunction<SN, DN[], DN> nodeMapper, final IntFunction<DN[]> arrayConstructor) {

    BottomUpImmutableNodeTransformer<SN, DN> transformer =
        new BottomUpImmutableNodeTransformer<>(rootNode, nodeMapper, arrayConstructor);
    transformer.processStack();
    return transformer.mappedNodesStack.peek();
  }

  private int stackIndex = -1;
  private final SN[] srcNodeStack = (SN[]) new Object[MAX_DEPTH];

  private final int[] srcNodeCursorsAndLengths = new int[MAX_DEPTH * 2];
  private final Stack<DN> mappedNodesStack = new Stack<DN>();

  private final DN[] EMPTY_DN_ARRAY = (DN[]) new Object[]{};

  public BottomUpImmutableNodeTransformer(final SN srcRootNode,
      final BiFunction<SN, DN[], DN> nodeMapper, final IntFunction<DN[]> arrayConstructor) {
    mappedNodesStack.ensureCapacity(128);
    this.nodeMapper = nodeMapper;
    this.arrayConstructor = arrayConstructor;
    this.mutator = new AtomicReference<>(Thread.currentThread());
    this.dstRootNode = null; // nodeMapper.apply(srcRootNode, mutator);

    final ListIterator<SN> srcIterator = (ListIterator<SN>) srcRootNode.nodeArray().iterator();

    if (srcIterator.hasNext()) {
//      final ListIterator<DN> dstIterator = (ListIterator<DN>) dstRootNode.nodeArray().iterator();
//
//      pushOnStack(srcIterator, dstIterator);

      pushOnStack(srcRootNode);
    } else {
      // TODO: Transform Leaf Node
      mappedNodesStack.push(nodeMapper.apply(srcRootNode, (DN[]) EMPTY_DN_ARRAY));
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

  private final void pushOnStack(SN srcNode) {
    // push on stack
    final int nextIndex = ++stackIndex;

    srcNodeStack[nextIndex] = srcNode;
    srcNodeCursorsAndLengths[2 * nextIndex + 0] = 0;
    srcNodeCursorsAndLengths[2 * nextIndex + 1] = srcNode.nodeArray().size();
  }

  private final void dropFromStack() {
    // pop from stack
    final int previousIndex = stackIndex--;
    srcNodeStack[previousIndex] = null;
    srcNodeCursorsAndLengths[2 * previousIndex + 0] = 0;
    srcNodeCursorsAndLengths[2 * previousIndex + 1] = 0;
  }

  private final void processStack() {
    while (!isStackEmpty()) {
      final int currentCursorIndex = stackIndex * 2;
      final int currentLengthIndex = currentCursorIndex + 1;

      final int childNodeCursor = srcNodeCursorsAndLengths[currentCursorIndex];
      final int childNodeLength = srcNodeCursorsAndLengths[currentLengthIndex];

      boolean stackModified = false;
      while (!stackModified) {
        if (childNodeCursor < childNodeLength) {
          final SN src = (SN) srcNodeStack[stackIndex].nodeArray().get(childNodeCursor);
          srcNodeCursorsAndLengths[currentCursorIndex]++;

//          final SN src = srcIterator.next();
//          final DN dst = nodeMapper.apply(src, mutator);
//
//          dstIterator.next();
//          dstIterator.set(dst);

          final ListIterator<SN> nextSrcIterator = (ListIterator<SN>) src.nodeArray().iterator();

          if (nextSrcIterator.hasNext()) {
            // root node == non-leaf node
            // put node on next stack level for depth-first traversal
            pushOnStack(src);
            stackModified = true;
          } else {
            // nextNode == leaf node
            // TODO: Transform Leaf Node
            mappedNodesStack.push(nodeMapper.apply(src, (DN[]) EMPTY_DN_ARRAY));
          }
        } else {
          // pop all children
          assert childNodeLength != 0;
          DN[] newChildren = arrayConstructor.apply(childNodeLength);
          for (int i = childNodeLength - 1; i >= 0; i--) {
            newChildren[i] = mappedNodesStack.pop();
          }

          // TODO: Transform Non-Leaf Node
          // apply mapper, push mapped node
          mappedNodesStack.push(nodeMapper.apply(srcNodeStack[stackIndex], newChildren));

          dropFromStack();
          stackModified = true;
        }
      }
    }
  }

}
