/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.criterion;

import java.lang.annotation.Annotation;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.math.BigInteger;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.gs.collections.impl.map.mutable.primitive.IntIntHashMap;
import com.pholser.junit.quickcheck.generator.ComponentizedGenerator;
import com.pholser.junit.quickcheck.generator.GenerationStatus;
import com.pholser.junit.quickcheck.generator.Generator;
import com.pholser.junit.quickcheck.generator.Size;
import com.pholser.junit.quickcheck.generator.java.lang.IntegerGenerator;
import com.pholser.junit.quickcheck.internal.GeometricDistribution;
import com.pholser.junit.quickcheck.internal.generator.SimpleGenerationStatus;
import com.pholser.junit.quickcheck.random.SourceOfRandomness;
import gnu.trove.map.hash.TIntIntHashMap;
import io.usethesource.capsule.SetMultimap;
import io.usethesource.capsule.api.TernaryRelation;
import io.usethesource.capsule.api.Triple;
import io.usethesource.capsule.core.PersistentTrieMap;
import io.usethesource.capsule.experimental.heterogeneous.TrieMap_5Bits_Heterogeneous_BleedingEdge;
import io.usethesource.capsule.experimental.specialized.TrieSet_5Bits_Spec0To8_IntKey;
import io.usethesource.capsule.generators.TripleGenerator;
import io.usethesource.capsule.generators.multimap.AbstractSetMultimapGenerator;
import io.usethesource.capsule.generators.multimap.SetMultimapGenerator_HCHAMP;
import io.usethesource.capsule.generators.relation.AbstractTernaryRelationGenerator;
import io.usethesource.capsule.generators.relation.BidirectionalTrieSetMultimapGenerator;
import io.usethesource.capsule.generators.relation.TernaryTrieSetMultimapGenerator;
import io.usethesource.criterion.BenchmarkUtils.Archetype;
import io.usethesource.criterion.BenchmarkUtils.DataType;
import io.usethesource.criterion.BenchmarkUtils.ValueFactoryFactory;
import io.usethesource.criterion.FootprintUtils.MemoryFootprintPreset;
import io.usethesource.criterion.api.JmhValue;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import objectexplorer.ObjectGraphMeasurer.Footprint;
import org.apache.mahout.math.map.OpenIntIntHashMap;

import static io.usethesource.capsule.util.collection.AbstractSpecialisedImmutableMap.entryOf;
import static io.usethesource.criterion.BenchmarkUtils.ValueFactoryFactory.VF_CHAMP_MULTIMAP_HCHAMP;
import static io.usethesource.criterion.BenchmarkUtils.ValueFactoryFactory.VF_CHAMP_MULTIMAP_HHAMT;
import static io.usethesource.criterion.BenchmarkUtils.ValueFactoryFactory.VF_CHAMP_MULTIMAP_HHAMT_SPECIALIZED;
import static io.usethesource.criterion.BenchmarkUtils.ValueFactoryFactory.VF_CLOJURE;
import static io.usethesource.criterion.BenchmarkUtils.ValueFactoryFactory.VF_DEXX;
import static io.usethesource.criterion.BenchmarkUtils.ValueFactoryFactory.VF_VAVR;
import static io.usethesource.criterion.BenchmarkUtils.ValueFactoryFactory.VF_PCOLLECTIONS;
import static io.usethesource.criterion.BenchmarkUtils.ValueFactoryFactory.VF_SCALA;
import static io.usethesource.criterion.BenchmarkUtils.ValueFactoryFactory.VF_PAGURO;
import static io.usethesource.criterion.BenchmarkUtils.seedFromSizeAndRun;
import static io.usethesource.criterion.FootprintUtils.MemoryFootprintPreset.DATA_STRUCTURE_OVERHEAD;
import static io.usethesource.criterion.FootprintUtils.MemoryFootprintPreset.RETAINED_SIZE_WITH_BOXED_INTEGER_FILTER;
import static io.usethesource.criterion.FootprintUtils.createExponentialRange;
import static io.usethesource.criterion.FootprintUtils.rangeExclusive;
import static io.usethesource.criterion.FootprintUtils.rangeInclusive;
import static io.usethesource.criterion.FootprintUtils.writeToFile;

public final class CalculateFootprintsHeterogeneous {

  static final String memoryArchitecture;

  static {
    /*
     * http://stackoverflow.com/questions/1518213/read-java-jvm-startup-parameters-eg-xmx
     */
    RuntimeMXBean bean = ManagementFactory.getRuntimeMXBean();
    List<String> args = bean.getInputArguments();

    if (args.contains("-XX:-UseCompressedOops")) {
      memoryArchitecture = "64bit";
    } else {
      memoryArchitecture = "32bit";
    }
  }

  private static int multimapValueSize = 2;

  private static int stepSizeOneToOneSelector = 2;

  public static void main(String[] args) throws InstantiationException, IllegalAccessException {
//    final String userHome = System.getProperty("user.home");
//    final String userHomeRelativePath = "Research/datastructures-for-metaprogramming/hamt-heterogeneous/data";
//    final Path directoryPath = Paths.get(userHome, userHomeRelativePath);

    final Path directoryPath = Paths.get(".", "target").toAbsolutePath().normalize();

    final boolean appendToFile = false;

    final int numberOfRuns = 1;

    final List<Integer> sizes = Arrays
        .asList(16, 2048, 1048576, 8388608); // createExponentialRangeWithIntermediatePoints();
    final List<Integer> runs = rangeExclusive(0, numberOfRuns);

//    final EnumSet<MemoryFootprintPreset> presets = EnumSet
//        .of(DATA_STRUCTURE_OVERHEAD); // also consider measuring RETAINED_SIZE

    final Function<Map.Entry<Integer, Integer>, Stream<String>> measureAllMultimaps =
        (sizeRunTuple) -> extractAndApply(sizeRunTuple,
            (size, run) -> measurePersistentMultimaps(size, run, DATA_STRUCTURE_OVERHEAD).stream());

    writeToFile(
        directoryPath.resolve("map_sizes_heterogeneous_tiny.csv"),
        appendToFile,
        product(rangeInclusive(0, 100, 1), runs).stream()
            .flatMap(measureAllMultimaps)
            .collect(Collectors.toList()));

//    writeToFile(
//        directoryPath.resolve("map_sizes_heterogeneous_small.csv"),
//        appendToFile,
//        product(rangeInclusive(100, 10_000, 100), runs).stream()
//            .flatMap(measureAllMultimaps)
//            .collect(Collectors.toList()));
//
//    writeToFile(
//        directoryPath.resolve("map_sizes_heterogeneous_medium.csv"),
//        appendToFile,
//        product(rangeInclusive(10_000, 100_000, 1_000), runs).stream()
//            .flatMap(measureAllMultimaps)
//            .collect(Collectors.toList()));
//
//    writeToFile(
//        directoryPath.resolve("map_sizes_heterogeneous_large.csv"),
//        appendToFile,
//        product(rangeInclusive(100_000, 8_000_000, 100_000), runs).stream()
//            .flatMap(measureAllMultimaps)
//            .collect(Collectors.toList()));

    writeToFile(
        directoryPath
            .resolve("map_sizes_heterogeneous_exponential_" + memoryArchitecture + "_latest.csv"),
        appendToFile,
        product(sizes, runs).stream()
            .flatMap(measureAllMultimaps)
            .collect(Collectors.toList()));

//    /*
//     * PRIMITIVE DATA
//     */
//
//    final Function<Map.Entry<Integer, Integer>, Stream<String>> measureAllPrimitiveMultimaps =
//        (sizeRunTuple) -> extractAndApply(sizeRunTuple,
//            (size, run) -> measureMutablePrimitiveMaps(size, run,
//                RETAINED_SIZE_WITH_BOXED_INTEGER_FILTER).stream());
//
//    writeToFile(
//        directoryPath.resolve("map_sizes_heterogeneous_exponential_"
//            + memoryArchitecture + "_primitive_latest.csv"),
//        appendToFile,
//        product(sizes, runs).stream()
//            .flatMap(measureAllPrimitiveMultimaps)
//            .collect(Collectors.toList()));
  }

  public static List<String> measurePersistentMultimaps(int size, int run,
      MemoryFootprintPreset preset) {

    final Function<ValueFactoryFactory, String> executeExperiment =
        (factory) -> createAndMeasureTrieSetMultimap(factory, size, multimapValueSize,
            stepSizeOneToOneSelector, run, preset);

    final EnumSet<ValueFactoryFactory> factories = EnumSet
        .of(VF_CHAMP_MULTIMAP_HCHAMP,
            VF_CHAMP_MULTIMAP_HHAMT,
            VF_CHAMP_MULTIMAP_HHAMT_SPECIALIZED,
            VF_SCALA,
            VF_CLOJURE);

    return factories.stream()
        .map(executeExperiment)
        .collect(Collectors.toList());
  }

  /**
   * Map<K, V> 3rd party libraries containing persistent data structures.
   */
  public static List<String> measurePersistentJavaLibraries(int size, int run,
      MemoryFootprintPreset preset) {

    final Function<ValueFactoryFactory, String> executeExperiment =
        (factory) -> createAndMeasurePersistentMap(factory, size, run, preset);

    final EnumSet<ValueFactoryFactory> factories = EnumSet
        .of(VF_PAGURO,
            VF_DEXX,
            VF_VAVR,
            VF_PCOLLECTIONS);

    return factories.stream()
        .map(executeExperiment)
        .collect(Collectors.toList());
  }

  /**
   * Map[int, int].
   */
  public static List<String> measureMutablePrimitiveMaps(int size, int run,
      MemoryFootprintPreset preset) {

    final Number[] data = createNumericData(size, run, 1.00);

    final Set<Supplier<String>> experiments = new HashSet<>();

    experiments.add(() -> createAndMeasureGuavaImmutableMap(data, size, run, preset)); // Reference
    experiments.add(() -> createAndMeasureTrieMapHeterogeneous_asMap(data, size, run, preset));

    experiments.add(() -> createAndMeasureFastUtilInt2IntOpenHashMap(data, size, run, preset));
    experiments.add(() -> createAndMeasureMahoutMutableIntIntHashMap(data, size, run, preset));
    experiments.add(() -> createAndMeasureTrove4jTIntIntHashMap(data, size, run, preset));
    experiments.add(() -> createAndMeasureGsImmutableIntIntMap(data, size, run, preset));

//    // generic maps
//    experiments.add(() -> createAndMeasureJavaUtilHashMap(data, size, run, preset));
//    experiments.add(() -> createAndMeasureTrieMapHomogeneous(data, size, run, preset));
//    experiments.add(() -> createAndMeasureTrieMapHeterogeneous(data, size, run, preset, true));
//    experiments.add(() -> createAndMeasureTrieMapHeterogeneous(data, size, run, preset, false));

    return experiments.stream()
        .map(Supplier::get)
        .collect(Collectors.toList());
  }

  /**
   * SetMultimap.
   */
  public static List<String> measureXxxxMultiMaps(int size, int run, MemoryFootprintPreset preset) {

    final Number[] data = createNumericData(size, run, 1.00);

    final Set<Supplier<String>> experiments = new HashSet<>();
    experiments.add(() -> createAndMeasureGsImmutableSetMultimap(data, size, run, preset));
    experiments.add(() -> createAndMeasureGuavaImmutableSetMultimap(data, size, run, preset));

    return experiments.stream()
        .map(Supplier::get)
        .collect(Collectors.toList());
  }

  /**
   * Map<K, V> vs Multimap<K, V>.
   */
  public static List<String> measurePersistentMapVsMultimap(int size, int run,
      MemoryFootprintPreset preset) {

    final Function<ValueFactoryFactory, String> executeExperiment =
        (factory) -> createAndMeasureTrieMap(factory, size, run, preset);

    final EnumSet<ValueFactoryFactory> factories = EnumSet
        .of(VF_CHAMP_MULTIMAP_HCHAMP,
            VF_CHAMP_MULTIMAP_HHAMT,
            VF_CHAMP_MULTIMAP_HHAMT_SPECIALIZED);

    return factories.stream()
        .map(executeExperiment)
        .collect(Collectors.toList());
  }

  private static void measureBinaryAndTernaryMultimaps() {
    int elementCount = 1048576;

    final Class<? extends AbstractSetMultimapGenerator<? extends SetMultimap.Immutable>>[] generatorClasses =
        new Class[]{BidirectionalTrieSetMultimapGenerator.class,
            SetMultimapGenerator_HCHAMP.class};

    System.out.println("\n\n\n\n");

    {
      final String result = createAndMeasureXXX_2(TernaryTrieSetMultimapGenerator.class,
          elementCount, 2, 2, 0, RETAINED_SIZE_WITH_BOXED_INTEGER_FILTER);

      System.out.println("\n" + result + "\n\n\n\n");
    }

    Arrays.asList(generatorClasses).forEach(clazz -> {
      final String result = createAndMeasureXXX(clazz, elementCount, 2, 2, 0,
          RETAINED_SIZE_WITH_BOXED_INTEGER_FILTER);

      System.out.println("\n" + result + "\n\n\n\n");
    });
  }

  // public static void testPrintStatsRandomSmallAndBigIntegers() {
  // int measurements = 4;
  //
  // for (int exp = 0; exp <= 23; exp += 1) {
  // final int thisExpSize = (int) Math.pow(2, exp);
  // final int prevExpSize = (int) Math.pow(2, exp-1);
  //
  // int stride = (thisExpSize - prevExpSize) / measurements;
  //
  // if (stride == 0) {
  // measurements = 1;
  // }
  //
  // for (int m = measurements - 1; m >= 0; m--) {
  // int size = thisExpSize - m * stride;
  // }

  public static final <X, Y, Z> Z extractAndApply(Map.Entry<X, Y> tuple,
      BiFunction<X, Y, Z> mapper) {
    return mapper.apply(tuple.getKey(), tuple.getValue());
  }

  public static final <X, Y> List<Map.Entry<X, Y>> product(List<X> xs, List<Y> ys) {
    List<Map.Entry<X, Y>> xys = new ArrayList<>(xs.size() * ys.size());

    for (X x : xs) {
      for (Y y : ys) {
        xys.add(entryOf(x, y));
      }
    }

    return xys;
  }

  public static List<Integer> createExponentialRangeWithIntermediatePoints() {
    List<Integer> tmpExponentialRange1 = createExponentialRange(0, 24);
    List<Integer> tmpExponentialRange2 = createExponentialRange(-1, 23);

    List<Integer> tmpExponentialRange = new ArrayList<>(2 * tmpExponentialRange1.size());
    for (int i = 0; i < tmpExponentialRange1.size(); i++) {
      tmpExponentialRange.add(tmpExponentialRange1.get(i));
      tmpExponentialRange.add(tmpExponentialRange1.get(i) + tmpExponentialRange2.get(i));
    }

    List<Integer> exponentialRange = tmpExponentialRange.stream().skip(1)
        .limit(2 * tmpExponentialRange1.size() - 2).collect(Collectors.toList());

    return exponentialRange;
  }

  public static Number[] createNumericData(int size, int run, double percentageOfPrimitives) {
    Number[] data = new Number[size];

    int countForPrimitives = (int) ((percentageOfPrimitives) * size);
    int smallCount = 0;
    int bigCount = 0;

    Random rand = new Random(13);
    for (int i = 0; i < size; i++) {
      final int j = rand.nextInt();
      final BigInteger bigJ = BigInteger.valueOf(j).multiply(BigInteger.valueOf(j));

      if (i < countForPrimitives) {
        // System.out.println("SMALL");
        smallCount++;
        data[i] = j;
      } else {
        // System.out.println("BIG");
        bigCount++;
        data[i] = bigJ;
      }
    }

    System.out.println();
    System.out.println(String.format("PRIMITIVE:   %10d (%.2f percent)", smallCount,
        100. * smallCount / (smallCount + bigCount)));
    System.out.println(String.format("BIG_INTEGER: %10d (%.2f percent)", bigCount,
        100. * bigCount / (smallCount + bigCount)));
    // System.out.println(String.format("UNIQUE: %10d (%.2f percent)",
    // map.size(), 100. * map.size() / (smallCount + bigCount)));
    System.out.println();

    return data;
  }

  // public static String createAndMeasureMultiChamp(final Object[] data, int elementCount, int run,
  // MemoryFootprintPreset preset) {
  // ImmutableSetMultimap<Integer, Integer> ys = TrieSetMultimap_BleedingEdge.of();
  //
  // for (Object o : data) {
  // for (int i = 0; i < multimapValueCount; i++) {
  // ys = ys.__put((Integer) o, (Integer) i);
  // }
  // }
  //
  // return measureAndReport(ys, "io.usethesource.capsule.TrieSetMultimap_BleedingEdge",
  // DataType.MULTIMAP,
  // Archetype.PERSISTENT, false, elementCount, run, preset);
  // }

  public static String createAndMeasureGsImmutableSetMultimap(final Object[] data, int elementCount,
      int run, MemoryFootprintPreset preset) {
    com.gs.collections.api.multimap.set.MutableSetMultimap<Integer, Integer> mutableYs =
        com.gs.collections.impl.factory.Multimaps.mutable.set.with();

    for (Object o : data) {
      for (int i = 0; i < multimapValueSize; i++) {
        mutableYs.put((Integer) o, (Integer) i);
      }
    }

    /* Note: direct creation of immutable that uses newWith(...) is tremendously slow. */
    com.gs.collections.api.multimap.set.ImmutableSetMultimap<Integer, Integer> ys =
        mutableYs.toImmutable();

    return measureAndReport(ys, "com.gs.collections.api.multimap.set.ImmutableSetMultimap",
        DataType.SET_MULTIMAP, Archetype.IMMUTABLE, false, elementCount, run, preset);
  }

  public static String createAndMeasureFastUtilInt2IntOpenHashMap(final Object[] data,
      int elementCount, int run, MemoryFootprintPreset preset) {
    it.unimi.dsi.fastutil.ints.AbstractInt2IntMap mutableYs = new Int2IntOpenHashMap();

    for (Object o : data) {
      for (int i = 0; i < multimapValueSize; i++) {
        mutableYs.put((Integer) o, (Integer) i);
      }
    }

    return measureAndReport(mutableYs, "it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap",
        DataType.MAP, Archetype.MUTABLE, false, elementCount, run, preset);
  }

  public static String createAndMeasureMahoutMutableIntIntHashMap(final Object[] data,
      int elementCount, int run, MemoryFootprintPreset preset) {
    org.apache.mahout.math.map.AbstractIntIntMap mutableYs = new OpenIntIntHashMap();

    for (Object o : data) {
      for (int i = 0; i < multimapValueSize; i++) {
        mutableYs.put((Integer) o, (Integer) i);
      }
    }

    return measureAndReport(mutableYs, "org.apache.mahout.math.map.OpenIntIntHashMap", DataType.MAP,
        Archetype.MUTABLE, false, elementCount, run, preset);
  }

  public static String createAndMeasureGsImmutableIntIntMap(final Object[] data, int elementCount,
      int run, MemoryFootprintPreset preset) {
    com.gs.collections.api.map.primitive.MutableIntIntMap mutableYs = new IntIntHashMap();

    for (Object o : data) {
      for (int i = 0; i < multimapValueSize; i++) {
        mutableYs.put((Integer) o, (Integer) i);
      }
    }

    com.gs.collections.api.map.primitive.ImmutableIntIntMap ys = mutableYs.toImmutable();

    return measureAndReport(ys, "com.gs.collections.api.map.primitive.ImmutableIntIntMap",
        DataType.MAP, Archetype.IMMUTABLE, false, elementCount, run, preset);
  }

  private final static JmhValue box(int i) {
    return new PureIntegerWithCustomHashCode(i);
  }

  public static String createAndMeasureGuavaImmutableMap(final Object[] data, int elementCount,
      int run, MemoryFootprintPreset preset) {
    com.google.common.collect.ImmutableMap.Builder<JmhValue, JmhValue> ysBldr =
        com.google.common.collect.ImmutableMap.builder();

    // filters duplicates (because builder can't handle them)
    Set<Object> seenKeys = new HashSet<>(data.length);

    for (Object o : data) {
      if (!seenKeys.contains(o)) {
        seenKeys.add(o);
        ysBldr.put(box((Integer) o), box((Integer) o));
      }
    }

    com.google.common.collect.ImmutableMap<JmhValue, JmhValue> ys = ysBldr.build();

    return measureAndReport(ys, "com.google.common.collect.ImmutableMap", DataType.MAP,
        Archetype.IMMUTABLE, false, elementCount, run, preset);
  }

  public static String createAndMeasureGuavaImmutableSetMultimap(final Object[] data,
      int elementCount, int run, MemoryFootprintPreset preset) {
    com.google.common.collect.ImmutableSetMultimap.Builder<JmhValue, JmhValue> ysBldr =
        com.google.common.collect.ImmutableSetMultimap.builder();

    for (int keyIdx = 0; keyIdx < data.length; keyIdx++) {
      Object o = data[keyIdx];

      if (keyIdx % stepSizeOneToOneSelector == 0) {
        ysBldr.put(box((Integer) o), box((Integer) o));
      } else {
        for (int i = 0; i < multimapValueSize; i++) {
          ysBldr.put(box((Integer) o), box(i));
        }
      }
    }

    com.google.common.collect.ImmutableMultimap<JmhValue, JmhValue> ys = ysBldr.build();

    return measureAndReport(ys, "com.google.common.collect.ImmutableSetMultimap",
        DataType.SET_MULTIMAP, Archetype.IMMUTABLE, false, elementCount, run, preset);
  }

  public static String createAndMeasureTrieMapHeterogeneous_asMap(final Object[] data,
      int elementCount, int run, MemoryFootprintPreset preset) {
    TrieMap_5Bits_Heterogeneous_BleedingEdge ys =
        (TrieMap_5Bits_Heterogeneous_BleedingEdge) TrieMap_5Bits_Heterogeneous_BleedingEdge.of();

    // simulating a multimap with a specialized nested integer set
    for (int keyIdx = 0; keyIdx < data.length; keyIdx++) {
      Object o = data[keyIdx];
      int intValue = ((Integer) o).intValue();

      ys = (TrieMap_5Bits_Heterogeneous_BleedingEdge) ys.__put(intValue, intValue);
    }

    final String shortName = "HHAMT_AS_MAP<int, int>";

    return measureAndReport(ys, shortName, DataType.MAP, Archetype.PERSISTENT, false, elementCount,
        run, preset);
  }

  public static String createAndMeasureTrieMapHeterogeneous_asMultimap(final Object[] data,
      int elementCount, int run, MemoryFootprintPreset preset) {
    TrieMap_5Bits_Heterogeneous_BleedingEdge ys =
        (TrieMap_5Bits_Heterogeneous_BleedingEdge) TrieMap_5Bits_Heterogeneous_BleedingEdge.of();

    // simulating a multimap with a specialized nested integer set
    for (int keyIdx = 0; keyIdx < data.length; keyIdx++) {
      Object o = data[keyIdx];
      int intValue = ((Integer) o).intValue();

      if (keyIdx % stepSizeOneToOneSelector == 0) {
        ys = (TrieMap_5Bits_Heterogeneous_BleedingEdge) ys.__put(intValue, intValue);
      } else {
        io.usethesource.capsule.Set.Immutable<Integer> nestedSet =
            TrieSet_5Bits_Spec0To8_IntKey.of();

        for (int i = 0; i < multimapValueSize; i++) {
          nestedSet = nestedSet.__insert(i);
        }

        ys = (TrieMap_5Bits_Heterogeneous_BleedingEdge) ys.__put(o, nestedSet);
      }
    }

    final String shortName = "HHAMT_AS_SET_MULTIMAP<int, int>";

    return measureAndReport(ys, shortName, DataType.SET_MULTIMAP, Archetype.PERSISTENT, false,
        elementCount, run, preset);
  }

  public static String createAndMeasurePersistentMap(ValueFactoryFactory valueFactoryFactory,
      int elementCount, int run, MemoryFootprintPreset preset) {
    try {
      final Object mapInstance = JmhMapBenchmarks.generateMap(valueFactoryFactory.getInstance(),
          ElementProducer.PDB_INTEGER, false, elementCount, run);

      return measureAndReport(mapInstance, valueFactoryFactory.name(), DataType.MAP,
          Archetype.PERSISTENT, false, elementCount, run, preset);
    } catch (Exception e) {
      e.printStackTrace();
      return "ERROR";
    }
  }

  /*
   * TODO: check where this is used; misnomer.
   */
  @Deprecated
  public static String createAndMeasureTrieMap(ValueFactoryFactory valueFactoryFactory,
      int elementCount, int run, MemoryFootprintPreset preset) {
    try {
      final int fixedMultimapValueSize = 1;
      final int fixedStepSizeOneToOneSelector = 1;

      final Object setMultimapInstance = JmhSetMultimapBenchmarks.generateSetMultimap(
          valueFactoryFactory.getInstance(), ElementProducer.PDB_INTEGER, false, elementCount,
          fixedMultimapValueSize, fixedStepSizeOneToOneSelector, run);

      return measureAndReport(setMultimapInstance, valueFactoryFactory.name(), DataType.MAP,
          Archetype.PERSISTENT, false, elementCount, run, preset);
    } catch (Exception e) {
      e.printStackTrace();
    }

    return "ERROR";
  }

  public final static Size size(int min, int max) {
    return new Size() {
      @Override
      public int min() {
        return min;
      }

      @Override
      public int max() {
        return max;
      }

      @Override
      public Class<? extends Annotation> annotationType() {
        return Size.class;
      }
    };
  }

  public static String createAndMeasureXXX(
      Class<? extends AbstractSetMultimapGenerator<? extends SetMultimap.Immutable>> generatorClass,
      int elementCount, int multimapValueSize, int stepSizeOneToOneSelector, int run,
      MemoryFootprintPreset preset) {

    try {
      final AbstractSetMultimapGenerator<? extends SetMultimap.Immutable> gen =
          generatorClass.newInstance();

      gen.configure(size(elementCount, elementCount));

      gen.addComponentGenerators(Arrays.asList(new IntegerGenerator(), new IntegerGenerator()));

      final SourceOfRandomness random =
          new SourceOfRandomness(new Random(seedFromSizeAndRun(elementCount, run)));

      final GenerationStatus status =
          new SimpleGenerationStatus(new GeometricDistribution(), random, 1);

      final Object setMultimapInstance = gen.generate(random, status);

      // System.out.println(setMultimapInstance);

      return measureAndReport(setMultimapInstance, generatorClass.getName(), DataType.SET_MULTIMAP,
          Archetype.PERSISTENT, false, elementCount, run, preset);
    } catch (InstantiationException e) {
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    }

    return "ERROR";
  }

  public static final <T> Class<T> classCast(Class clazz) {
    return (Class<T>) clazz;
  }

  public static String createAndMeasureXXX_2(
      Class<? extends AbstractTernaryRelationGenerator<? extends TernaryRelation.Immutable>> generatorClass,
      int elementCount, int multimapValueSize, int stepSizeOneToOneSelector, int run,
      MemoryFootprintPreset preset) {

    final Class<TernaryRelation.Immutable<Integer, Integer, Integer, Triple<Integer, Integer, Integer>>> targetClass = classCast(
        TernaryRelation.Immutable.class);

    final ComponentizedGenerator<TernaryRelation.Immutable<Integer, Integer, Integer, Triple<Integer, Integer, Integer>>> generator =
        new ComponentizedGenerator(targetClass) {
          @Override
          public TernaryRelation.Immutable<Integer, Integer, Integer, Triple<Integer, Integer, Integer>> generate(
              SourceOfRandomness sourceOfRandomness, GenerationStatus generationStatus) {
            return null;
          }
        };

    try {
      final AbstractTernaryRelationGenerator<? extends TernaryRelation.Immutable> gen =
          generatorClass.newInstance();

      gen.configure(size(elementCount, elementCount));

      Generator<Triple> tripleGenerator = new TripleGenerator();
      tripleGenerator.addComponentGenerators(
          Arrays.asList(new IntegerGenerator(), new IntegerGenerator(), new IntegerGenerator()));

      gen.addComponentGenerators(Arrays.asList(new IntegerGenerator(), new IntegerGenerator(),
          new IntegerGenerator(), tripleGenerator));

      final SourceOfRandomness random =
          new SourceOfRandomness(new Random(seedFromSizeAndRun(elementCount, run)));

      final GenerationStatus status =
          new SimpleGenerationStatus(new GeometricDistribution(), random, 1);

      final Object setMultimapInstance = gen.generate(random, status);

      // System.out.println(setMultimapInstance);

      return measureAndReport(setMultimapInstance, generatorClass.getName(), DataType.SET_MULTIMAP,
          Archetype.PERSISTENT, false, elementCount, run, preset);
    } catch (InstantiationException e) {
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    }

    return "ERROR";
  }

  public static String createAndMeasureTrieSetMultimap(ValueFactoryFactory valueFactoryFactory,
      int elementCount, int multimapValueSize, int stepSizeOneToOneSelector, int run,
      MemoryFootprintPreset preset) {
    try {
      final Object setMultimapInstance = JmhSetMultimapBenchmarks.generateSetMultimap(
          valueFactoryFactory.getInstance(), ElementProducer.PDB_INTEGER, false, elementCount,
          multimapValueSize, stepSizeOneToOneSelector, run);

      return measureAndReport(setMultimapInstance, valueFactoryFactory.name(),
          DataType.SET_MULTIMAP, Archetype.PERSISTENT, false, elementCount, run, preset);
    } catch (Exception e) {
      e.printStackTrace();
    }

    return "ERROR";
  }

  public static String createAndMeasureTrieMapHomogeneous(final Object[] data, int elementCount,
      int run, MemoryFootprintPreset preset) {
    io.usethesource.capsule.Map.Immutable<Integer, Integer> ys = PersistentTrieMap.of();

    // for (Object v : data) {
    // ys = ys.__put(v, v);
    // assert ys.containsKey(v);
    // }

    int[] convertedData = new int[elementCount];

    for (int i = 0; i < elementCount; i++) {
      final Object v = data[i];
      final int convertedValue;

      if (v instanceof Integer) {
        convertedValue = (Integer) v;
      } else if (v instanceof BigInteger) {
        convertedValue = ((BigInteger) v).intValue();
      } else {
        throw new IllegalStateException("Expecting input data of type Integer or BigInteger.");
      }

      convertedData[i] = convertedValue;
    }

    for (int value : convertedData) {
      ys = ys.__put(value, value);
      assert ys.containsKey(value);
    }

    String shortName = "TrieMap [Boxed]";

    // String longName = String.format(
    // "io.usethesource.capsule.TrieMap_5Bits_Spec0To8", isSpecialized);

    return measureAndReport(ys, shortName, DataType.MAP, Archetype.PERSISTENT, false, elementCount,
        run, preset);
  }

  public static String createAndMeasureTrieMapHeterogeneous(final Object[] data, int elementCount,
      int run, MemoryFootprintPreset preset, boolean storePrimivesBoxed) {
    TrieMap_5Bits_Heterogeneous_BleedingEdge ys =
        (TrieMap_5Bits_Heterogeneous_BleedingEdge) TrieMap_5Bits_Heterogeneous_BleedingEdge.of();

    for (Object v : data) {
      if (v instanceof Integer && storePrimivesBoxed) {
        // PureInteger boxedValue = new PureInteger(((Integer) v).intValue());
        Integer boxedValue = (Integer) v;

        ys = (TrieMap_5Bits_Heterogeneous_BleedingEdge) ys.__put(boxedValue, boxedValue);
        assert ys.containsKey(boxedValue);
      } else if (v instanceof Integer && !storePrimivesBoxed) {
        int unboxedValue = ((Integer) v).intValue();

        ys = (TrieMap_5Bits_Heterogeneous_BleedingEdge) ys.__put(unboxedValue, unboxedValue);
        assert ys.containsKey(unboxedValue);
        // } else {
        // ys = (TrieMap_5Bits_Heterogeneous_BleedingEdge) ys.__put(v, v);
        // assert ys.containsKey(v);
      }
    }

    final String shortName = storePrimivesBoxed ? "HTrieMap [Boxed]" : "HTrieMap [Primitive]";

    // String shortName = String.format("TrieMap[%13s, storePrimivesBoxed =
    // %5s]",
    // "heterogeneous", storePrimivesBoxed);
    //
    // String longName = String.format(
    // "io.usethesource.capsule.TrieMap_Heterogeneous[storePrimivesBoxed =
    // %5s]",
    // storePrimivesBoxed);

    return measureAndReport(ys, shortName, DataType.MAP, Archetype.PERSISTENT, false, elementCount,
        run, preset);
  }

  public static String createAndMeasureJavaUtilHashMap(final Object[] data, int elementCount,
      int run, MemoryFootprintPreset preset) {
    Map<Object, Object> ys = new HashMap<>();

    for (Object v : data) {
      ys.put(v, v);
      assert ys.containsKey(v);
    }

    String shortName = String.format("HashMap");

    String.format("java.util.HashMap");

    return measureAndReport(ys, shortName, DataType.MAP, Archetype.MUTABLE, false, elementCount,
        run, preset);
  }

  public static String createAndMeasureTrove4jTIntIntHashMap(final Object[] data, int elementCount,
      int run, MemoryFootprintPreset preset) {
    TIntIntHashMap ys = new TIntIntHashMap(elementCount);

    int[] convertedData = new int[elementCount];

    for (int i = 0; i < elementCount; i++) {
      final Object v = data[i];
      final int convertedValue;

      if (v instanceof Integer) {
        convertedValue = (Integer) v;
      } else if (v instanceof BigInteger) {
        convertedValue = ((BigInteger) v).intValue();
      } else {
        throw new IllegalStateException("Expecting input data of type Integer or BigInteger.");
      }

      convertedData[i] = convertedValue;
    }

    for (int value : convertedData) {
      ys.put(value, value);
      assert ys.containsKey(value);
    }

    return measureAndReport(ys, "gnu.trove.map.hash.TIntIntHashMap", DataType.MAP,
        Archetype.MUTABLE, false, elementCount, run, preset);
  }

  private static String measureAndReport(final Object objectToMeasure, final String className,
      DataType dataType, Archetype archetype, boolean supportsStagedMutability, int size, int run,
      MemoryFootprintPreset preset) {
    final Predicate<Object> predicate;

    switch (preset) {
      case DATA_STRUCTURE_OVERHEAD:
        // TODO: create JmhLeaf
        // predicate = Predicates
        // .not(Predicates.or(Predicates.instanceOf(Integer.class),
        // Predicates.instanceOf(BigInteger.class),
        // Predicates.instanceOf(JmhValue.class), Predicates.instanceOf(PureInteger.class)));
        predicate = Predicates.not(Predicates.or(Predicates.instanceOf(PureInteger.class),
            Predicates.instanceOf(PureIntegerWithCustomHashCode.class)));
        break;
      case RETAINED_SIZE:
        predicate = Predicates.alwaysTrue();
        break;
      case RETAINED_SIZE_WITH_BOXED_INTEGER_FILTER:
        predicate = Predicates.not(Predicates.instanceOf(Integer.class));
        break;
      default:
        throw new IllegalStateException();
    }

    return measureAndReport(objectToMeasure, className, dataType, archetype,
        supportsStagedMutability, size, run, predicate);
  }

  private static String measureAndReport(final Object objectToMeasure, final String className,
      DataType dataType, Archetype archetype, boolean supportsStagedMutability, int size, int run) {
    return measureAndReport(objectToMeasure, className, dataType, archetype,
        supportsStagedMutability, size, run, DATA_STRUCTURE_OVERHEAD);
  }

  private static String measureAndReport(final Object objectToMeasure, final String className,
      DataType dataType, Archetype archetype, boolean supportsStagedMutability, int size, int run,
      Predicate<Object> predicate) {
    // System.out.println(GraphLayout.parseInstance(objectToMeasure).totalSize());

    long memoryInBytes = objectexplorer.MemoryMeasurer.measureBytes(objectToMeasure, predicate);
    Footprint memoryFootprint =
        objectexplorer.ObjectGraphMeasurer.measure(objectToMeasure, predicate);

    final String statString = String.format("%d (%d@%d)\t %60s\t\t %s", size, run, memoryInBytes,
        className, memoryFootprint);
    System.out.println(statString);

    // final String statLatexString = String.format("%s & %s & %s & %b & %d
    // & %d & %d & \"%s\" \\\\", className, dataType, archetype,
    // supportsStagedMutability, memoryInBytes,
    // memoryFootprint.getObjects(), memoryFootprint.getReferences(),
    // memoryFootprint.getPrimitives());
    // System.out.println(statLatexString);

    final String statFileString = String.format("%d,%d,%s,%s,%s,%b,%d,%d,%d", size, run, className,
        dataType, archetype, supportsStagedMutability, memoryInBytes, memoryFootprint.getObjects(),
        memoryFootprint.getReferences());

    return statFileString;
    // writeToFile(statFileString);
  }

}
