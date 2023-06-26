/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
// package io.usethesource.criterion;
/// *******************************************************************************
// * Copyright (c) 2014 CWI
// * All rights reserved. This program and the accompanying materials
// * are made available under the terms of the Eclipse Public License v1.0
// * which accompanies this distribution, and is available at
// * http://www.eclipse.org/legal/epl-v10.html
// *
// * Contributors:
// *
// * * Michael Steindorfer - Michael.Steindorfer@cwi.nl - CWI
// *******************************************************************************/
// package io.usethesource.criterion;
//
// import static io.usethesource.criterion.BenchmarkUtils.ValueFactoryFactory.VF_CLOJURE;
// import static
// io.usethesource.criterion.BenchmarkUtils.ValueFactoryFactory.VF_PDB_PERSISTENT_CURRENT;
// import static
// io.usethesource.criterion.BenchmarkUtils.ValueFactoryFactory.VF_PDB_PERSISTENT_MEMOIZED_LAZY;
// import static io.usethesource.criterion.BenchmarkUtils.ValueFactoryFactory.VF_SCALA;
//
// import java.lang.reflect.InvocationTargetException;
// import java.lang.reflect.Method;
// import java.math.BigInteger;
// import java.nio.charset.StandardCharsets;
// import java.nio.file.Files;
// import java.nio.file.Path;
// import java.nio.file.Paths;
// import java.nio.file.StandardOpenOption;
// import java.util.ArrayList;
// import java.util.Collections;
// import java.util.HashMap;
// import java.util.HashSet;
// import java.util.List;
// import java.util.Map;
// import java.util.Objects;
// import java.util.Optional;
// import java.util.Random;
// import java.util.Set;
//
// import objectexplorer.ObjectGraphMeasurer.Footprint;
//
// import io.usethesource.vallang.ISet;
// import io.usethesource.vallang.ISetWriter;
// import io.usethesource.vallang.IValue;
// import io.usethesource.vallang.IValueFactory;
// import io.usethesource.capsule.ImmutableMap;
// import io.usethesource.capsule.ImmutableSet;
// import io.usethesource.capsule.PersistentTrieMap;
// import io.usethesource.capsule.TrieMap_5Bits_Memoized_LazyHashCode;
// import io.usethesource.capsule.PersistentTrieSet;
// import io.usethesource.capsule.TrieSet_5Bits_Memoized_LazyHashCode;
//
// import scala.Tuple2;
// import clojure.lang.IPersistentMap;
// import clojure.lang.IPersistentSet;
// import clojure.lang.ITransientMap;
// import clojure.lang.ITransientSet;
// import clojure.lang.PersistentHashMap;
// import clojure.lang.PersistentHashSet;
//
// import com.google.common.base.Predicate;
// import com.google.common.base.Predicates;
//
//
// public final class CalculateFootprintsMessy {
//
// public enum Archetype {
// MUTABLE,
// IMMUTABLE,
// PERSISTENT
// }
//
// public enum DataType {
// MULTIMAP,
// MAP,
// SET
// }
//
// private static final IValueFactory valueFactory = ValueFactory.getInstance();
//
// private static boolean reportSet = true;
// private static boolean reportMap = true;
//
// private static int multimapValueCount = 6;
//
// private final static String csvHeader =
// "elementCount,run,className,dataType,archetype,supportsStagedMutability,footprintInBytes,footprintInObjects,footprintInReferences";
// // ,footprintInPrimitives
//
// // public static ISet setUpTestSetWithRandomContent(int size) throws Exception {
////// TrieMapVsOthersFootprint.size = size;
////
//// ISetWriter setWriter = valueFactory.setWriter();
////
//// // random data generator with fixed seed
//// Random rand = new Random(2305843009213693951L); // seed == Mersenne Prime #9
////
//// for (int i = size; i > 0; i--) {
//// final int j = rand.nextInt();
//// final IValue current = valueFactory.integer(j);
////
//// setWriter.insert(current);
//// }
////
//// return setWriter.done();
//// }
//
//
// public static ISet setUpTestRelationWithRandomContent(int size, int run) throws Exception {
// // TrieMapVsOthersFootprint.size = size;
//
// ISetWriter setWriter = valueFactory.setWriter();
//
// int seedForThisTrial = BenchmarkUtils.seedFromSizeAndRun(size, run);
// Random rand = new Random(seedForThisTrial);
//
// // System.out.println(String.format("Seed for this trial: %d.", seedForThisTrial));
//
// for (int i = size; i > 0; i--) {
// final int j = rand.nextInt();
// final IValue current = valueFactory.integer(j);
//
// setWriter.insert(valueFactory.tuple(current, current));
// }
//
// return setWriter.done();
// }
//
// public static ISet setUpTestSetWithRandomContent(int size, int run) throws Exception {
// // TrieMapVsOthersFootprint.size = size;
//
// ISetWriter setWriter = valueFactory.setWriter();
//
// int seedForThisTrial = BenchmarkUtils.seedFromSizeAndRun(size, run);
// Random rand = new Random(seedForThisTrial);
//
// // System.out.println(String.format("Seed for this trial: %d.", seedForThisTrial));
//
// for (int i = size; i > 0; i--) {
// final int j = rand.nextInt();
// final IValue current = valueFactory.integer(j);
//
// setWriter.insert(current);
// }
//
// return setWriter.done();
// }
//
// public static java.util.Set<java.lang.Integer> setUpTestSetWithRandomContentInt(int size, int
// run) throws Exception {
//// TrieMapVsOthersFootprint.size = size;
//
// java.util.Set<java.lang.Integer> setWriter = new HashSet<>();
//
// // random data generator with fixed seed
// Random rand = new Random(size + 13 * run);
//
// for (int i = size; i > 0; i--) {
// final int j = rand.nextInt();
//// final IValue current = valueFactory.integer(j);
//
// setWriter.add(j);
// }
//
// return Collections.unmodifiableSet(setWriter);
// }
//
// public static Object invokeFactoryMethodAndYieldEmptyInstance(final Class<?> target) {
// final Method factoryMethodOfEmpty;
//
// try {
// factoryMethodOfEmpty = target.getMethod("of");
// } catch (NoSuchMethodException | SecurityException e) {
// throw new RuntimeException(e);
// }
//
// try {
// return factoryMethodOfEmpty.invoke(null);
// } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
// throw new RuntimeException(e);
// }
// }
//
// public static void timeTrieSet(final ISet testSet, int elementCount, int run,
// Optional<String> shortName, final Class<?> setClass, final Class<?> mapClass) {
//
// @SuppressWarnings("unchecked")
// ImmutableSet<IValue> xs = (ImmutableSet<IValue>)
// invokeFactoryMethodAndYieldEmptyInstance(setClass);
//
// @SuppressWarnings("unchecked")
// ImmutableMap<IValue, IValue> ys = (ImmutableMap<IValue, IValue>)
// invokeFactoryMethodAndYieldEmptyInstance(mapClass);
//
// for (IValue v : testSet) {
// if (reportSet) xs = xs.__insert(v);
// if (reportMap) ys = ys.__put(v, v);
// }
//
// if (reportSet) {
// measureAndReport(
// xs,
// shortName.isPresent() ? shortName.get() : Objects.toString(setClass
// .getCanonicalName()), DataType.SET,
// Archetype.PERSISTENT, false, elementCount, run);
// }
// if (reportMap) {
// measureAndReport(
// ys,
// shortName.isPresent() ? shortName.get() : Objects.toString(mapClass
// .getCanonicalName()), DataType.MAP,
// Archetype.PERSISTENT, false, elementCount, run);
// }
// }
//
//// public static void timeTrieMultimap(final ISet testSet, int elementCount, int run) {
////// ImmutableSet<IValue> xs = PersistentTrieSet.<IValue>of();
//// ImmutableSetMultimap<IValue, IValue> ys = TrieSetMultimap_BleedingEdge.<IValue, IValue>of();
////
//// for (IValue v : testSet) {
////// if (reportSet) xs = xs.__insert(v);
////
//// for(int i = 0; i < multimapValueCount; i++) {
//// if (reportMap) ys = ys.__put(v, valueFactory.integer(i));
//// }
//// }
////
////// // statistics should exactly match, thus only printing them once
////// ((PersistentTrieSet) xs).printStatistics();
////
////// if (reportSet) measureAndReport(xs, "io.usethesource.capsule.PersistentTrieSet", DataType.SET,
// Archetype.PERSISTENT, false, elementCount, run);
//// if (reportMap) measureAndReport(ys, "io.usethesource.capsule.TrieSetMultimap_BleedingEdge",
// DataType.MULTIMAP, Archetype.PERSISTENT, false, elementCount, run);
//// }
////
//// public static void timeImmutableSetMultimapAsImmutableSetView(final ISet testSet, int
// elementCount, int run) {
//// final ImmutableSetMultimap<IValue, IValue> multimap = TrieSetMultimap_BleedingEdge.<IValue,
// IValue>of();
////
//// final BiFunction<IValue, IValue, ITuple> tupleOf = (first, second) ->
// io.usethesource.vallang.impl.fast.Tuple
//// .newTuple(first, second);
////
//// final BiFunction<ITuple, Integer, Object> tupleElementAt = (tuple, position) -> {
//// switch (position) {
//// case 0:
//// return tuple.get(0);
//// case 1:
//// return tuple.get(1);
//// default:
//// throw new IllegalStateException();
//// }
//// };
////
//// ImmutableSet<ITuple> xs = new ImmutableSetMultimapAsImmutableSetView<IValue, IValue,
// ITuple>(multimap, tupleOf,
//// tupleElementAt);
////// ImmutableMap<IValue, IValue> ys = PersistentTrieMap.<IValue, IValue>of();
////
//// for (IValue v : testSet) {
//// if (reportSet) xs = xs.__insert((ITuple) v);
////// if (reportMap) ys = ys.__put(v, v);
//// }
////
////// // statistics should exactly match, thus only printing them once
////// ((PersistentTrieSet) xs).printStatistics();
////
//// if (reportSet) measureAndReport(xs,
// "io.usethesource.capsule.ImmutableSetMultimapAsImmutableSetView", DataType.SET,
// Archetype.PERSISTENT, false, elementCount, run);
////// if (reportMap) measureAndReport(ys,
// "io.usethesource.capsule.ImmutableSetMultimapAsImmutableMapView", DataType.MAP,
// Archetype.PERSISTENT, false, elementCount, run);
//// }
//
// public void timeTrieSet0To4(final ISet testSet, int elementCount, int run) {
//// TransientSet<IValue> transientSet = TrieSet0To4.<IValue>of();
//// TransientMap<IValue, IValue> transientMap = TrieMap0To4.<IValue, IValue>of();
////
//// for (IValue v : testSet) {
//// if (reportSet) transientSet.__insert(v);
//// if (reportMap) transientMap.__put(v, v);
//// }
////
//// ImmutableSet<IValue> xs = transientSet.freeze();
//// ImmutableMap<IValue, IValue> ys = transientMap.freeze();
////
//// if (reportSet) measureAndReport(xs, "io.usethesource.capsule.TrieSet0To4", DataType.SET,
// Archetype.PERSISTENT, false, elementCount, run);
//// if (reportMap) measureAndReport(ys, "io.usethesource.capsule.TrieMap0To4", DataType.MAP,
// Archetype.PERSISTENT, false, elementCount, run);
// }
//
// public void timeTrieSet0To8(final ISet testSet, int elementCount, int run) {
//// TransientSet<IValue> transientSet = TrieSet0To8.<IValue>of();
//// TransientMap<IValue, IValue> transientMap = TrieMap0To8.<IValue, IValue>of();
////
//// for (IValue v : testSet) {
//// if (reportSet) transientSet.__insert(v);
//// if (reportMap) transientMap.__put(v, v);
//// }
////
//// ImmutableSet<IValue> xs = transientSet.freeze();
//// ImmutableMap<IValue, IValue> ys = transientMap.freeze();
////
//// if (reportSet) measureAndReport(xs, "io.usethesource.capsule.TrieSet0To8", DataType.SET,
// Archetype.PERSISTENT, false, elementCount, run);
//// if (reportMap) measureAndReport(ys, "io.usethesource.capsule.TrieMap0To8", DataType.MAP,
// Archetype.PERSISTENT, false, elementCount, run);
// }
//
// public void timeTrieSet0To12(final ISet testSet, int elementCount, int run) {
//// TransientSet<IValue> transientSet = TrieSet0To12.<IValue>of();
//// TransientMap<IValue, IValue> transientMap = TrieMap0To12.<IValue, IValue>of();
////
//// for (IValue v : testSet) {
//// if (reportSet) transientSet.__insert(v);
//// if (reportMap) transientMap.__put(v, v);
//// }
////
//// ImmutableSet<IValue> xs = transientSet.freeze();
//// ImmutableMap<IValue, IValue> ys = transientMap.freeze();
////
//// if (reportSet) measureAndReport(xs, "io.usethesource.capsule.TrieSet0To12", DataType.SET,
// Archetype.PERSISTENT, false, elementCount, run);
//// if (reportMap) measureAndReport(ys, "io.usethesource.capsule.TrieMap0To12", DataType.MAP,
// Archetype.PERSISTENT, false, elementCount, run);
// }
//
// public void timeTrieSetSpecializationWithUntypedVariables(final ISet testSet, int elementCount,
// int run) {
//// TransientSet<IValue> transientSet = TrieSet_5Bits_Untyped_Spec0To8.<IValue>of();
//// TransientMap<IValue, IValue> transientMap = TrieMap_5Bits_Untyped_Spec0To8.<IValue,
// IValue>of();
////
//// for (IValue v : testSet) {
//// if (reportSet) transientSet.__insert(v);
//// if (reportMap) transientMap.__put(v, v);
//// }
////
//// ImmutableSet<IValue> xs = transientSet.freeze();
//// ImmutableMap<IValue, IValue> ys = transientMap.freeze();
////
////// ((TrieSetSpecializationWithUntypedVariables) xs).printStatistics();
////// ((TrieMapSpecializationWithUntypedVariables) ys).printStatistics();
////
//// if (reportSet) measureAndReport(xs,
// "io.usethesource.capsule.TrieSetSpecializationWithUntypedVariables", DataType.SET,
// Archetype.PERSISTENT, false, elementCount, run);
//// if (reportMap) measureAndReport(ys,
// "io.usethesource.capsule.TrieMapSpecializationWithUntypedVariables", DataType.MAP,
// Archetype.PERSISTENT, false, elementCount, run);
// }
//
// public static void timeTrieSet_BleedingEdge(final ISet testSet, int elementCount, int run) {
//// TransientSet<IValue> transientSet = TrieSet_BleedingEdge.<IValue>of();
//// TransientMap<IValue, IValue> transientMap = TrieMap_BleedingEdge.<IValue, IValue>of();
////
//// for (IValue v : testSet) {
//// if (reportSet) transientSet.__insert(v);
//// if (reportMap) transientMap.__put(v, v);
//// }
////
//// ImmutableSet<IValue> xs = transientSet.freeze();
//// ImmutableMap<IValue, IValue> ys = transientMap.freeze();
////
//// if (reportSet) measureAndReport(xs, "io.usethesource.capsule.TrieSet_BleedingEdge",
// DataType.SET, Archetype.PERSISTENT, false, elementCount, run);
//// if (reportMap) measureAndReport(ys, "io.usethesource.capsule.TrieMap_BleedingEdge",
// DataType.MAP, Archetype.PERSISTENT, false, elementCount, run);
// }
//
// // TODO
// public void timeTrieSetSpecializationInt(final java.util.Set<java.lang.Integer> testSet, int
// elementCount, int run) {
//// TransientSet<java.lang.Integer> transientSet = TrieSet_IntKey_IntValue.of();
//// TransientMap<java.lang.Integer, java.lang.Integer> transientMap =
// TrieMap_IntKey_IntValue.<IValue, IValue>of();
////
//// for (int v : testSet) {
//// if (reportSet) transientSet.__insert(v);
//// if (reportMap) transientMap.__put(v, v);
//// }
////
//// ImmutableSet<java.lang.Integer> xs = transientSet.freeze();
//// ImmutableMap<java.lang.Integer, java.lang.Integer> ys = transientMap.freeze();
////
////// ((TrieSetSpecializationInt) xs).printStatistics();
////// ((TrieMapSpecializationWithUntypedVariables) ys).printStatistics();
////
//// if (reportSet) measureAndReport(xs, "io.usethesource.capsule.TrieSetSpecializationInt",
// DataType.SET, Archetype.PERSISTENT, false, elementCount, run);
//// if (reportMap) measureAndReport(ys, "io.usethesource.capsule.TrieMapSpecializationInt",
// DataType.MAP, Archetype.PERSISTENT, false, elementCount, run);
// }
//
// public void timeJavaMutable(final ISet testSet, int elementCount, int run) {
// Set<IValue> xs = new HashSet<>();
// Map<IValue, IValue> ys = new HashMap<>();
//
// for (IValue v : testSet) {
// if (reportSet) xs.add(v);
// if (reportMap) ys.put(v, v);
// }
//
// if (reportSet) measureAndReport(xs, "java.util.HashSet", DataType.SET, Archetype.MUTABLE, true,
// elementCount, run);
// if (reportMap) measureAndReport(ys, "java.util.HashMap", DataType.MAP, Archetype.MUTABLE, true,
// elementCount, run);
// }
//
// public void timeGSMutableUnifiedSet(final ISet testSet, int elementCount, int run) {
// Set<IValue> xs = new com.gs.collections.impl.set.mutable.UnifiedSet<>();
// Map<IValue, IValue> ys = new com.gs.collections.impl.map.mutable.UnifiedMap<>();
//
// for (IValue v : testSet) {
// if (reportSet) xs.add(v);
// if (reportMap) ys.put(v, v);
// }
//
// if (reportSet) measureAndReport(xs, "com.gs.collections.impl.set.mutable.UnifiedSet",
// DataType.SET, Archetype.MUTABLE, true, elementCount, run);
// if (reportMap) measureAndReport(ys, "com.gs.collections.impl.map.mutable.UnifiedMap",
// DataType.MAP, Archetype.MUTABLE, true, elementCount, run);
// }
//
// public void timeGuavaImmutable(final ISet testSet, int elementCount, int run) {
// com.google.common.collect.ImmutableSet.Builder<IValue> xsBldr =
// com.google.common.collect.ImmutableSet.builder();
// com.google.common.collect.ImmutableMap.Builder<IValue, IValue> ysBldr =
// com.google.common.collect.ImmutableMap.builder();
//
// for (IValue v : testSet) {
// if (reportSet) xsBldr.add(v);
// if (reportMap) ysBldr.put(v, v);
// }
//
// com.google.common.collect.ImmutableSet<IValue> xs = xsBldr.build();
// com.google.common.collect.ImmutableMap<IValue, IValue> ys = ysBldr.build();
//
// if (reportSet) measureAndReport(xs, "com.google.common.collect.ImmutableSet", DataType.SET,
// Archetype.IMMUTABLE, false, elementCount, run);
// if (reportMap) measureAndReport(ys, "com.google.common.collect.ImmutableMap", DataType.MAP,
// Archetype.IMMUTABLE, false, elementCount, run);
// }
//
// public static void timeGuavaImmutableSetMultimap(final ISet testSet, int elementCount, int run) {
////// com.google.common.collect.ImmutableSet.Builder<IValue> xsBldr =
// com.google.common.collect.ImmutableSet.builder();
//// com.google.common.collect.ImmutableSetMultimap.Builder<IValue, IValue> ysBldr =
// com.google.common.collect.ImmutableSetMultimap.builder();
////
//// for (IValue v : testSet) {
////// if (reportSet) xsBldr.add(v);
//// for(int i = 0; i < multimapValueCount; i++) {
//// if (reportMap) ysBldr.put(v, valueFactory.integer(i));
//// }
//// }
////
////// com.google.common.collect.ImmutableSet<IValue> xs = xsBldr.build();
//// com.google.common.collect.ImmutableMultimap<IValue, IValue> ys = ysBldr.build();
////
////// if (reportSet) measureAndReport(xs, "com.google.common.collect.ImmutableSet", DataType.SET,
// Archetype.IMMUTABLE, false, elementCount, run);
//// if (reportMap) measureAndReport(ys, "com.google.common.collect.ImmutableSetMultimap",
// DataType.MULTIMAP, Archetype.IMMUTABLE, false, elementCount, run);
// }
//
// /* Note: immutable creation that uses newWith(...) is tremendously slow. */
// public static void timeGSImmutableSetMultimap(final ISet testSet, int elementCount, int run) {
//// com.gs.collections.api.multimap.set.MutableSetMultimap<IValue, IValue> mutableYs =
// com.gs.collections.impl.factory.Multimaps.mutable.set.with();
////
//// for (IValue v : testSet) {
//// for(int i = 0; i < multimapValueCount; i++) {
//// if (reportMap) mutableYs.put(v, valueFactory.integer(i));
//// }
//// }
////
//// com.gs.collections.api.multimap.set.ImmutableSetMultimap<IValue, IValue> ys =
// mutableYs.toImmutable();
////
//// if (reportMap) measureAndReport(ys, "com.gs.collections.api.multimap.set.ImmutableSetMultimap",
// DataType.MULTIMAP, Archetype.IMMUTABLE, false, elementCount, run);
// }
//
//
//// @Test
//// public void timeSetWriter() {
//// ISetWriter writer = valueFactory.setWriter();
//// for (IValue v : testSet) {
//// writer.insert(v);
//// }
//// writer.done();
//// }
//
// public void timeClojureTransient(final ISet testSet, int elementCount, int run) {
// ITransientSet xs = (ITransientSet) PersistentHashSet.EMPTY.asTransient();
// ITransientMap ys = (ITransientMap) PersistentHashMap.EMPTY.asTransient();
//
// for (IValue v : testSet) {
// if (reportSet) xs = (ITransientSet) xs.conj(v);
// if (reportMap) ys = (ITransientMap) ys.assoc(v, v);
// }
//
// if (reportSet) measureAndReport(xs, "clojure.lang.TransientHashSet", DataType.SET,
// Archetype.PERSISTENT, true, elementCount, run);
// if (reportMap) measureAndReport(ys, "clojure.lang.TransientHashMap", DataType.MAP,
// Archetype.PERSISTENT, true, elementCount, run);
// }
//
// public static void timeClojurePersistent(final ISet testSet, int elementCount, int run,
// Optional<String> shortName) {
// final Class<?> setClass = clojure.lang.PersistentHashSet.class;
// final Class<?> mapClass = clojure.lang.PersistentHashMap.class;
//
// IPersistentSet xs = (IPersistentSet) PersistentHashSet.EMPTY;
// IPersistentMap ys = (IPersistentMap) PersistentHashMap.EMPTY;
//
// for (IValue v : testSet) {
// if (reportSet)
// xs = (IPersistentSet) xs.cons(v);
// if (reportMap)
// ys = (IPersistentMap) ys.assoc(v, v);
// }
//
// if (reportSet) {
// measureAndReport(
// xs,
// shortName.isPresent() ? shortName.get() : Objects.toString(setClass
// .getCanonicalName()), DataType.SET,
// Archetype.PERSISTENT, true, elementCount, run);
// }
// if (reportMap) {
// measureAndReport(
// ys,
// shortName.isPresent() ? shortName.get() : Objects.toString(mapClass
// .getCanonicalName()), DataType.MAP,
// Archetype.PERSISTENT, true, elementCount, run);
// }
// }
//
// public static void timeScalaPersistent(final ISet testSet, int elementCount, int run,
// Optional<String> shortName) {
// final Class<?> setClass = scala.collection.immutable.HashSet.class;
// final Class<?> mapClass = scala.collection.immutable.HashMap.class;
//
// scala.collection.immutable.HashSet<IValue> xs = new scala.collection.immutable.HashSet<>();
// scala.collection.immutable.HashMap<IValue, IValue> ys = new
// scala.collection.immutable.HashMap<>();
//
// for (IValue v : testSet) {
// if (reportSet)
// xs = xs.$plus(v);
// if (reportMap)
// ys = ys.$plus(new Tuple2<>(v, v));
// }
//
// if (reportSet) {
// measureAndReport(
// xs,
// shortName.isPresent() ? shortName.get() : Objects.toString(setClass
// .getCanonicalName()), DataType.SET,
// Archetype.PERSISTENT, false, elementCount, run);
// }
// if (reportMap) {
// measureAndReport(
// ys,
// shortName.isPresent() ? shortName.get() : Objects.toString(mapClass
// .getCanonicalName()), DataType.MAP,
// Archetype.PERSISTENT, false, elementCount, run);
// }
// }
//
// public void timeScalaMutable(final ISet testSet, int elementCount, int run) {
// scala.collection.mutable.HashSet<IValue> xs = new scala.collection.mutable.HashSet<>();
// scala.collection.mutable.HashMap<IValue, IValue> ys = new scala.collection.mutable.HashMap<>();
//
// for (IValue v : testSet) {
// if (reportSet) xs = xs.$plus$eq(v);
// if (reportMap) ys = ys.$plus$eq(new Tuple2<>(v, v));
// }
//
// if (reportSet) measureAndReport(xs, "scala.collection.mutable.HashSet", DataType.SET,
// Archetype.MUTABLE, true, elementCount, run);
// if (reportMap) measureAndReport(ys, "scala.collection.mutable.HashMap", DataType.MAP,
// Archetype.MUTABLE, true, elementCount, run);
// }
//
//// @SuppressWarnings({ "rawtypes", "unchecked" })
//// public void timeScalaSetBuilderPersistent(int reps) {
//// for (int i = 0; i < reps; i++) {
//// scala.collection.Set<IValue> empty = scala.collection.immutable.Set$.MODULE$.empty();
//// scala.collection.mutable.SetBuilder builder = new SetBuilder(empty);
//// for (IValue v : testSet) {
//// builder = builder.$plus$eq(v);
//// }
//// builder.result();
//// }
//// }
////
//// @SuppressWarnings({ "rawtypes", "unchecked" })
//// public void timeScalaSetBuilderMutable(int reps) {
//// for (int i = 0; i < reps; i++) {
//// scala.collection.Set<IValue> empty = scala.collection.mutable.Set$.MODULE$.empty();
//// scala.collection.mutable.SetBuilder builder = new SetBuilder(empty);
//// for (IValue v : testSet) {
//// builder = builder.$plus$eq(v);
//// }
//// builder.result();
//// }
//// }
//
// enum MemoryFootprintPreset {
// RETAINED_SIZE,
// DATA_STRUCTURE_OVERHEAD
// }
//
// @SuppressWarnings("unchecked")
// private static void measureAndReport(final Object objectToMeasure, final String className,
// DataType dataType, Archetype archetype, boolean supportsStagedMutability, int size, int run,
// MemoryFootprintPreset preset) {
// final Predicate<Object> predicate;
//
// switch (preset) {
// case DATA_STRUCTURE_OVERHEAD:
// predicate = Predicates.not(Predicates.or(Predicates.instanceOf(Integer.class),
// Predicates.instanceOf(BigInteger.class), Predicates.instanceOf(IValue.class),
// Predicates.instanceOf(NonUnboxableInteger.class)));
// break;
// case RETAINED_SIZE:
// predicate = Predicates.alwaysTrue();
// break;
// default:
// throw new IllegalStateException();
// }
//
// measureAndReport(objectToMeasure, className, dataType, archetype, supportsStagedMutability, size,
// run, predicate);
// }
//
// private static void measureAndReport(final Object objectToMeasure, final String className,
// DataType dataType, Archetype archetype, boolean supportsStagedMutability, int size, int run) {
// measureAndReport(objectToMeasure, className, dataType, archetype, supportsStagedMutability, size,
// run, MemoryFootprintPreset.DATA_STRUCTURE_OVERHEAD);
// }
//
// private static void measureAndReport(final Object objectToMeasure, final String className,
// DataType dataType, Archetype archetype, boolean supportsStagedMutability, int size, int run,
// Predicate<Object> predicate) {
//// System.out.println(GraphLayout.parseInstance(objectToMeasure).totalSize());
//
// long memoryInBytes = objectexplorer.MemoryMeasurer.measureBytes(objectToMeasure,
// predicate);
// Footprint memoryFootprint = objectexplorer.ObjectGraphMeasurer.measure(objectToMeasure,
// predicate);
//
// final String statString = String.format("%d\t %60s\t[%s]\t %s", memoryInBytes, className,
// dataType,
// memoryFootprint);
// System.out.println(statString);
//
//// final String statLatexString = String.format("%s & %s & %s & %b & %d & %d & %d & \"%s\" \\\\",
// className, dataType, archetype, supportsStagedMutability, memoryInBytes,
// memoryFootprint.getObjects(), memoryFootprint.getReferences(), memoryFootprint.getPrimitives());
//// System.out.println(statLatexString);
//
//
// final String statFileString = String.format("%d,%d,%s,%s,%s,%b,%d,%d,%d", size, run, className,
// dataType, archetype, supportsStagedMutability, memoryInBytes, memoryFootprint.getObjects(),
// memoryFootprint.getReferences());
//
// writeToFile(statFileString);
// }
//
// private static void writeToFile(String line) {
// writeToFile(true, line);
// }
//
// private static void writeToFile(boolean isAppendingToFile, String line) {
// final List<String> lines = new ArrayList<>();
// lines.add(line);
//
// writeToFile(isAppendingToFile, lines);
// }
//
// private static void writeToFile(boolean isAppendingToFile, List<String> lines) {
// final Path file = Paths.get(String.format("map-sizes-and-statistics.csv"));
//
// // write stats to file
// try {
// if (isAppendingToFile) {
// Files.write(file, lines, StandardCharsets.UTF_8, StandardOpenOption.APPEND);
// } else {
// Files.write(file, lines, StandardCharsets.UTF_8);
// }
// } catch (Exception e) {
// e.printStackTrace();
// }
// }
//
// public static void footprintMap__Random_Persistent() {
//
//// for (int exp = 13; exp <= 13; exp += 1) {
//// final int count = (int) Math.pow(2, exp);
////
//// for (int run = 0; run < 1; run++) {
//
// for (int exp = 0; exp <= 23; exp += 1) {
// final int count = (int) Math.pow(2, exp);
//
// for (int run = 0; run < 5; run++) {
//
// ISet tmpSet = null;
// java.util.Set<java.lang.Integer> tmpSetInt = null;
//
// try {
// tmpSet = setUpTestSetWithRandomContent(count, run);
// tmpSetInt = setUpTestSetWithRandomContentInt(count, run);
// } catch (Exception e) {
// e.printStackTrace();
// }
//
// final ISet testSet = tmpSet;
// final java.util.Set<java.lang.Integer> testSetInt = tmpSetInt;
// final int currentRun = run;
//
// timeTrieSet(testSet, count, currentRun,
// Optional.of(VF_PDB_PERSISTENT_CURRENT.toString()),
// PersistentTrieSet.class, PersistentTrieMap.class);
//
//// timeTrieSet(testSet, count, currentRun,
//// Optional.of(VF_PDB_PERSISTENT_LAZY.toString()),
//// TrieSet_5Bits_LazyHashCode.class, TrieMap_5Bits_LazyHashCode.class);
////
//// timeTrieSet(testSet, count, currentRun,
//// Optional.of(VF_PDB_PERSISTENT_MEMOIZED.toString()),
//// TrieSet_5Bits_Memoized.class, TrieMap_5Bits_Memoized.class);
//
// timeTrieSet(testSet, count, currentRun,
// Optional.of(VF_PDB_PERSISTENT_MEMOIZED_LAZY.toString()),
// TrieSet_5Bits_Memoized_LazyHashCode.class,
// TrieMap_5Bits_Memoized_LazyHashCode.class);
//
// timeClojurePersistent(testSet, count, currentRun,
// Optional.of(VF_CLOJURE.toString()));
//
// timeScalaPersistent(testSet, count, currentRun, Optional.of(VF_SCALA.toString()));
//
//// timeTrieSet0To4(testSet, count, currentRun);
//// timeTrieSet0To8(testSet, count, currentRun);
//// timeTrieSet0To12(testSet, count, currentRun);
//// timeTrieSetSpecializationWithUntypedVariables(testSet, count, currentRun);
//// timeTrieSetSpecializationInt(testSetInt, count, currentRun);
//// timeTrieSet_BleedingEdge(testSet, count, currentRun);
//
// // timeJavaMutable(testSet, count, currentRun);
// // timeGSMutableUnifiedSet(testSet, count, currentRun);
// // timeGuavaImmutable(testSet, count, currentRun);
// // timeClojureTransient(testSet, count, currentRun);
// // timeScalaMutable(testSet, count, currentRun);
//
// }
// }
// }
//
// public static void footprintBinaryRelation__Random_Persistent() {
//
// for (int exp = 13; exp <= 13; exp += 1) { // int exp = 0; exp <= 23; exp += 1
// final int count = (int) Math.pow(2, exp);
//
// for (int run = 0; run < 1; run++) {
//
// ISet tmpSet = null;
// ISet tmpRelation = null;
//
// try {
// tmpSet = setUpTestSetWithRandomContent(count, run);
// tmpRelation = setUpTestRelationWithRandomContent(count, run);
// } catch (Exception e) {
// e.printStackTrace();
// }
//
// final ISet testSet = tmpSet;
// final ISet testRelaton = tmpRelation;
// final int currentRun = run;
//
//// timeTrieSet(testSet, count, currentRun);
//// timeTrieSet(testRelaton, count, currentRun);
//// timeImmutableSetMultimapAsImmutableSetView(testRelaton, count, currentRun);
//// timeTrieMultimap(testSet, count, currentRun);
// timeGuavaImmutableSetMultimap(testSet, count, currentRun);
// timeGSImmutableSetMultimap(testSet, count, currentRun);
// }
// }
// }
//
// public static void main(String[] args) throws Exception {
// writeToFile(false, csvHeader);
// footprintMap__Random_Persistent();
//// footprintBinaryRelation__Random_Persistent();
//
//// measureAndReport(new NestedBigInteger(), "NestedBigInteger", DataType.MAP,
// Archetype.PERSISTENT, true, 0, 0, MemoryFootprintPreset.DATA_STRUCTURE_OVERHEAD);
//// measureAndReport(new NestedPrimitiveInteger(), "NestedPrimitiveInteger", DataType.MAP,
// Archetype.PERSISTENT, true, 0, 0, MemoryFootprintPreset.DATA_STRUCTURE_OVERHEAD);
// }
//
// }
//
// class NestedBigInteger {
// private BigInteger bigInt0 = BigInteger.valueOf(10);
// private BigInteger bigInt1 = BigInteger.valueOf(11);
// }
//
// class NestedPrimitiveInteger {
// private int smallInt0 = 10;
// private int smallInt1 = 11;
// }
//
// class NonUnboxableInteger {
// final int value;
//
// NonUnboxableInteger(int value) {
// this.value = value;
// }
//
// }
