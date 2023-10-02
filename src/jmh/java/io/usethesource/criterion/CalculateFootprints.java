/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.criterion;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

import clojure.lang.IPersistentMap;
import clojure.lang.IPersistentSet;
import clojure.lang.PersistentHashMap;
import clojure.lang.PersistentHashSet;
import io.usethesource.capsule.Map;
import io.usethesource.capsule.core.PersistentTrieMap;
import io.usethesource.capsule.core.PersistentTrieSet;
import io.usethesource.capsule.experimental.memoized.TrieMap_5Bits_Memoized_LazyHashCode;
import io.usethesource.capsule.experimental.memoized.TrieSet_5Bits_Memoized_LazyHashCode;
import io.usethesource.criterion.BenchmarkUtils.Archetype;
import io.usethesource.criterion.BenchmarkUtils.DataType;
import io.usethesource.criterion.BenchmarkUtils.ValueFactoryFactory;
import io.usethesource.criterion.FootprintUtils.MemoryFootprintPreset;
import io.usethesource.criterion.api.JmhValue;
import scala.Tuple2;

public final class CalculateFootprints {

  private static boolean reportSet = true;
  private static boolean reportMap = true;

  public static java.util.Set<JmhValue> setUpTestSetWithRandomContent(int size, int run) {
    java.util.Set<JmhValue> setWriter = new HashSet<>();

    int seedForThisTrial = BenchmarkUtils.seedFromSizeAndRun(size, run);
    Random rand = new Random(seedForThisTrial);

    for (int i = size; i > 0; i--) {
      final int j = rand.nextInt();
      final JmhValue current = PureIntegerWithCustomHashCode.valueOf(j);

      setWriter.add(current);
    }

    return Collections.unmodifiableSet(setWriter);
  }

  public static Object invokeFactoryMethodAndYieldEmptyInstance(final Class<?> target) {
    final Method factoryMethodOfEmpty;

    try {
      factoryMethodOfEmpty = target.getMethod("of");
    } catch (NoSuchMethodException | SecurityException e) {
      throw new RuntimeException(e);
    }

    try {
      return factoryMethodOfEmpty.invoke(null);
    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
      throw new RuntimeException(e);
    }
  }

  public static final String classToName(Class<?> clazz) {
    return Objects.toString(clazz.getCanonicalName());
  }

  public static String measureFootprintOfPersistentChampSet(final Set<JmhValue> testSet,
      int elementCount, int run, Optional<String> shortName, final Class<?> clazz) {

    io.usethesource.capsule.Set.Immutable<JmhValue> set =
        (io.usethesource.capsule.Set.Immutable<JmhValue>) invokeFactoryMethodAndYieldEmptyInstance(
            clazz);

    for (JmhValue v : testSet) {
      set = set.__insert(v);
    }

    return FootprintUtils.measureAndReport(set, shortName.orElse(classToName(clazz)), DataType.SET,
        Archetype.PERSISTENT, true, elementCount, run,
        MemoryFootprintPreset.DATA_STRUCTURE_OVERHEAD);
  }

  public static String measureFootprintOfPersistentChampMap(final Set<JmhValue> testSet,
      int elementCount, int run, Optional<String> shortName, final Class<?> clazz) {

    Map.Immutable<JmhValue, JmhValue> map =
        (Map.Immutable<JmhValue, JmhValue>) invokeFactoryMethodAndYieldEmptyInstance(clazz);

    for (JmhValue v : testSet) {
      map = map.__put(v, v);
    }

    return FootprintUtils.measureAndReport(map, shortName.orElse(classToName(clazz)), DataType.MAP,
        Archetype.PERSISTENT, true, elementCount, run,
        MemoryFootprintPreset.DATA_STRUCTURE_OVERHEAD);
  }

  public static String measureFootprintOfPersistentClojureSet(final Set<JmhValue> testSet,
      int elementCount, int run, Optional<String> shortName) {
    final Class<?> clazz = clojure.lang.PersistentHashSet.class;

    IPersistentSet set = PersistentHashSet.EMPTY;

    for (JmhValue v : testSet) {
      set = (IPersistentSet) set.cons(v);
    }

    return FootprintUtils.measureAndReport(set, shortName.orElse(classToName(clazz)), DataType.SET,
        Archetype.PERSISTENT, true, elementCount, run,
        MemoryFootprintPreset.DATA_STRUCTURE_OVERHEAD);
  }

  public static String measureFootprintOfPersistentClojureMap(final Set<JmhValue> testSet,
      int elementCount, int run, Optional<String> shortName) {
    final Class<?> clazz = clojure.lang.PersistentHashMap.class;

    IPersistentMap map = PersistentHashMap.EMPTY;

    for (JmhValue v : testSet) {

      map = map.assoc(v, v);
    }

    return FootprintUtils.measureAndReport(map, shortName.orElse(classToName(clazz)), DataType.MAP,
        Archetype.PERSISTENT, true, elementCount, run,
        MemoryFootprintPreset.DATA_STRUCTURE_OVERHEAD);
  }

  public static String measureFootprintOfPersistentScalaSet(final Set<JmhValue> testSet,
      int elementCount, int run, Optional<String> shortName) {
    final Class<?> clazz = scala.collection.immutable.HashSet.class;

    scala.collection.immutable.HashSet<JmhValue> set = new scala.collection.immutable.HashSet<>();

    for (JmhValue v : testSet) {
      set = set.$plus(v);
    }

    return FootprintUtils.measureAndReport(set, shortName.orElse(classToName(clazz)), DataType.SET,
        Archetype.PERSISTENT, false, elementCount, run,
        MemoryFootprintPreset.DATA_STRUCTURE_OVERHEAD);
  }

  public static String measureFootprintOfPersistentScalaMap(final Set<JmhValue> testSet,
      int elementCount, int run, Optional<String> shortName) {
    final Class<?> clazz = scala.collection.immutable.HashMap.class;

    scala.collection.immutable.HashMap<JmhValue, JmhValue> map =
        new scala.collection.immutable.HashMap<>();

    for (JmhValue v : testSet) {
      map = map.$plus(new Tuple2<>(v, v));
    }

    return FootprintUtils.measureAndReport(map, shortName.orElse(classToName(clazz)), DataType.MAP,
        Archetype.PERSISTENT, false, elementCount, run,
        MemoryFootprintPreset.DATA_STRUCTURE_OVERHEAD);
  }

  public static void main(String[] args) {
    final List<String> results = new LinkedList<>();

    for (int exp = 0; exp <= 23; exp += 1) {
      final int count = (int) Math.pow(2, exp);

      for (int run = 0; run < 5; run++) {
        final Set<JmhValue> testSet = setUpTestSetWithRandomContent(count, run);

        if (reportSet) {
          results.add(measureFootprintOfPersistentChampSet(testSet, count, run,
              Optional.of(ValueFactoryFactory.VF_CHAMP.toString()), PersistentTrieSet.class));

          results.add(measureFootprintOfPersistentChampSet(testSet, count, run,
              Optional.of(ValueFactoryFactory.VF_CHAMP_MEMOIZED.toString()),
              TrieSet_5Bits_Memoized_LazyHashCode.class));

//          results.add(measureFootprintOfPersistentChampSet(testSet, count, run,
//              Optional.of(ValueFactoryFactory.VF_AXIOM.toString()),
//              AxiomHashTrieSet.class));

//          results.add(measureFootprintOfPersistentChampSet(testSet, count, run,
//              Optional.of(ValueFactoryFactory.VF_CHAMP_EXTENDED.toString()),
//              PersistentTrieSetExtended.class));

          results.add(measureFootprintOfPersistentClojureSet(testSet, count, run,
              Optional.of(ValueFactoryFactory.VF_CLOJURE.toString())));

          results.add(measureFootprintOfPersistentScalaSet(testSet, count, run,
              Optional.of(ValueFactoryFactory.VF_SCALA.toString())));

          System.out.println();
        }

        if (reportMap) {
          results.add(measureFootprintOfPersistentChampMap(testSet, count, run,
              Optional.of(ValueFactoryFactory.VF_CHAMP.toString()), PersistentTrieMap.class));

          results.add(measureFootprintOfPersistentChampMap(testSet, count, run,
              Optional.of(ValueFactoryFactory.VF_CHAMP_MEMOIZED.toString()),
              TrieMap_5Bits_Memoized_LazyHashCode.class));

//          results.add(measureFootprintOfPersistentChampMap(testSet, count, run,
//              Optional.of(ValueFactoryFactory.VF_AXIOM.toString()),
//              AxiomHashTrieMap.class));

//          results.add(measureFootprintOfPersistentChampMap(testSet, count, run,
//                  Optional.of("VF_CHAMP_EXTENDED"),
//                  PersistentTrieMapExtended.class));

          results.add(measureFootprintOfPersistentClojureMap(testSet, count, run,
              Optional.of(ValueFactoryFactory.VF_CLOJURE.toString())));

          results.add(measureFootprintOfPersistentScalaMap(testSet, count, run,
              Optional.of(ValueFactoryFactory.VF_SCALA.toString())));

          System.out.println();
        }
      }
    }

    FootprintUtils.writeToFile(Paths.get("map-sizes-and-statistics.csv"), false, results);
  }

}
