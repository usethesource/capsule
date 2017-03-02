/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
// package io.usethesource.capsule.core.trie;
//
// import java.util.Iterator;
// import java.util.NoSuchElementException;
// import java.util.Stack;
// import java.util.function.BiFunction;
//
// import io.usethesource.capsule.core.deprecated.PersistentTrieSet;
// import io.usethesource.capsule.experimental.multimap.TrieSetMultimap_HCHAMP;
//
/// **
// * Basic support for both PUSH- and PULL-based data processing. Still needs tweaking of the
// * interface and implementation, but the the functionality is there.
// */
// public class BottomUpImmutableNodeTransformer<K, V, MN extends
/// TrieSetMultimap_HCHAMP.AbstractSetMultimapNode<K, V>, SN extends
/// PersistentTrieSet.AbstractSetNode<K>>
// implements Iterator<SN> {
//
// static final <K, V, MN extends TrieSetMultimap_HCHAMP.AbstractSetMultimapNode<K, V>, SN extends
/// PersistentTrieSet.AbstractSetNode<K>> SN applyNodeTransformation(
// final MN rootNode, final BiFunction<MN, SN[], SN> nodeMapper) {
//
// BottomUpImmutableNodeTransformer<K, V, MN, SN> transformer =
// new BottomUpImmutableNodeTransformer<>(rootNode, nodeMapper);
// transformer.applyNodeTranformation(TrieSetMultimap_HCHAMP.StreamType.PUSH);
// return transformer.mappedNodesStack.peek();
// }
//
// private static final int MAX_DEPTH = 7;
//
// private final BiFunction<MN, SN[], SN> nodeMapper;
//
// boolean isNextAvailable;
// private int currentStackLevel;
// private final int[] nodeCursorsAndLengths = new int[MAX_DEPTH * 2];
//
// final MN[] nodes = (MN[]) new TrieSetMultimap_HCHAMP.AbstractSetMultimapNode[MAX_DEPTH];
//
// final Stack<SN> mappedNodesStack = new Stack<SN>();
//
// private final static PersistentTrieSet.AbstractSetNode[] EMPTY_SN_ARRAY =
// new PersistentTrieSet.AbstractSetNode[] {};
//
// BottomUpImmutableNodeTransformer(final MN rootNode, final BiFunction<MN, SN[], SN> nodeMapper) {
// mappedNodesStack.ensureCapacity(128);
//
// this.nodeMapper = nodeMapper;
//
// if (rootNode.hasNodes()) {
// // rootNode == non-leaf node
// currentStackLevel = 0;
// isNextAvailable = false;
//
// nodes[0] = rootNode;
// nodeCursorsAndLengths[0] = 0;
// nodeCursorsAndLengths[1] = rootNode.nodeArity();
// } else {
// currentStackLevel = -1;
// isNextAvailable = true;
//
// mappedNodesStack.push(nodeMapper.apply(rootNode, (SN[]) EMPTY_SN_ARRAY));
// // mappedNodesStack.push((SN) rootNode.toSetNode(EMPTY_SN_ARRAY));
// // System.err.println(String.format("mappedNodesStack.size == %d",
// // mappedNodesStack.size()));
// // MAX_STACK_SIZE = Math.max(MAX_STACK_SIZE, mappedNodesStack.size());
// }
// }
//
// /*
// * search for next node that can be mapped
// */
// private boolean applyNodeTranformation(final TrieSetMultimap_HCHAMP.StreamType streamType) {
// while (currentStackLevel >= 0) {
// final int currentCursorIndex = currentStackLevel * 2;
// final int currentLengthIndex = currentCursorIndex + 1;
//
// final int childNodeCursor = nodeCursorsAndLengths[currentCursorIndex];
// final int childNodeLength = nodeCursorsAndLengths[currentLengthIndex];
//
// if (childNodeCursor < childNodeLength) {
// final MN nextNode = (MN) nodes[currentStackLevel].getNode(childNodeCursor);
// nodeCursorsAndLengths[currentCursorIndex]++;
//
// if (nextNode.hasNodes()) {
// // root node == non-leaf node
// // put node on next stack level for depth-first traversal
// final int nextStackLevel = ++currentStackLevel;
// final int nextCursorIndex = nextStackLevel * 2;
// final int nextLengthIndex = nextCursorIndex + 1;
//
// nodes[nextStackLevel] = nextNode;
// nodeCursorsAndLengths[nextCursorIndex] = 0;
// nodeCursorsAndLengths[nextLengthIndex] = nextNode.nodeArity();
// } else {
// // nextNode == leaf node
// mappedNodesStack.push(nodeMapper.apply(nextNode, (SN[]) EMPTY_SN_ARRAY));
// // mappedNodesStack.push((SN) nextNode.toSetNode(EMPTY_SN_ARRAY));
// // System.err.println(String.format("mappedNodesStack.size == %d",
// // mappedNodesStack.size()));
// // MAX_STACK_SIZE = Math.max(MAX_STACK_SIZE, mappedNodesStack.size());
//
// if (streamType == TrieSetMultimap_HCHAMP.StreamType.PULL) {
// return true;
// }
// }
// } else {
// // pop all children
// assert childNodeLength != 0;
// SN[] newChildren = (SN[]) new PersistentTrieSet.AbstractSetNode[childNodeLength];
// for (int i = childNodeLength - 1; i >= 0; i--) {
// newChildren[i] = mappedNodesStack.pop();
// }
//
// // apply mapper, push mapped node
// mappedNodesStack.push(nodeMapper.apply(nodes[currentStackLevel], newChildren));
// // mappedNodesStack.push((SN) nodes[currentStackLevel].toSetNode(newChildren));
// // System.err.println(String.format("mappedNodesStack.size == %d",
// // mappedNodesStack.size()));
// // MAX_STACK_SIZE = Math.max(MAX_STACK_SIZE, mappedNodesStack.size());
// currentStackLevel--;
//
// if (streamType == TrieSetMultimap_HCHAMP.StreamType.PULL) {
// return true;
// }
// }
// }
//
// // TODO: this is mind tangling; rework
// if (streamType == TrieSetMultimap_HCHAMP.StreamType.PUSH) {
// return true;
// } else {
// return false;
// }
// }
//
// @Override
// public boolean hasNext() {
// if (currentStackLevel >= -1 && isNextAvailable) {
// return true;
// } else {
// return isNextAvailable = applyNodeTranformation(TrieSetMultimap_HCHAMP.StreamType.PULL);
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
// isNextAvailable = false;
// return mappedNodesStack.peek();
// }
// }
//
// @Override
// public void remove() {
// throw new UnsupportedOperationException();
// }
//
// public SN result() {
// assert !hasNext();
// return mappedNodesStack.peek();
// }
//
// }
//
//
// enum StreamType {
// PULL, PUSH
// }
//
//// private static final int MAX_DEPTH = 7;
//// private final static Class<?> MAP_CLASS = AbstractSetMultimapNode.class;
//// private final static Class<?> SET_CLASS = PersistentTrieSet.AbstractSetNode.class;
////
//// public static final <K, V, MN extends AbstractSetMultimapNode<K, V>, SN extends
//// PersistentTrieSet.AbstractSetNode<K>> Optional<SN> applyNodeTransformation(
//// final MN mapRootNode, final BiFunction<MN, AtomicReference<Thread>, SN> nodeMapper) {
////
//// final AtomicReference<Thread> mutator = new AtomicReference<>(Thread.currentThread());
////
//// final int[] nodeCursorsAndLengths = new int[MAX_DEPTH * 2];
//// final MN[] mapNodes = (MN[]) Array.newInstance(MAP_CLASS, MAX_DEPTH);
//// final SN[] setNodes = (SN[]) Array.newInstance(SET_CLASS, MAX_DEPTH);
////
//// int currentStackLevel;
////
//// /************/
//// /*** INIT ***/
//// /************/
////
//// final SN setRootNode = nodeMapper.apply(mapRootNode, mutator);
////
//// if (mapRootNode.hasNodes()) {
//// // rootNode == non-leaf node
//// currentStackLevel = 0;
////
//// mapNodes[0] = mapRootNode;
//// setNodes[0] = setRootNode;
////
//// nodeCursorsAndLengths[0] = 0;
//// nodeCursorsAndLengths[1] = mapRootNode.nodeArity();
//// } else {
//// // nextNode == leaf node
//// currentStackLevel = -1;
//// return Optional.of(setRootNode);
//// }
////
//// /************/
//// /*** BODY ***/
//// /************/
////
//// while (currentStackLevel >= 0) {
//// final int currentCursorIndex = currentStackLevel * 2;
//// final int currentLengthIndex = currentCursorIndex + 1;
////
//// final int childNodeCursor = nodeCursorsAndLengths[currentCursorIndex];
//// final int childNodeLength = nodeCursorsAndLengths[currentLengthIndex];
////
//// if (childNodeCursor < childNodeLength) {
//// final MN nextMapNode = (MN) mapNodes[currentStackLevel].getNode(childNodeCursor);
//// nodeCursorsAndLengths[currentCursorIndex]++;
////
//// final SN nextSetNode = nodeMapper.apply(nextMapNode, mutator);
//// setNodes[currentStackLevel].setNode(mutator, childNodeCursor, nextSetNode);
////
//// if (nextMapNode.hasNodes()) {
//// // root node == non-leaf node
//// // put node on next stack level for depth-first traversal
//// final int nextStackLevel = ++currentStackLevel;
//// final int nextCursorIndex = nextStackLevel * 2;
//// final int nextLengthIndex = nextCursorIndex + 1;
////
//// mapNodes[nextStackLevel] = nextMapNode;
//// setNodes[nextStackLevel] = nextSetNode;
//// nodeCursorsAndLengths[nextCursorIndex] = 0;
//// nodeCursorsAndLengths[nextLengthIndex] = nextMapNode.nodeArity();
//// } else {
//// // nextNode == (finished) leaf node
//// }
//// } else {
//// // nextNode == (finished) intermediate node
////
//// // pop from stack
//// currentStackLevel--;
//// }
//// }
////
//// // yield set's rootNode
//// return Optional.of(setNodes[0]);
//// }
