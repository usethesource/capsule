/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.criterion;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import io.usethesource.criterion.BenchmarkUtils.DataType;
import io.usethesource.criterion.BenchmarkUtils.SampleDataSelection;
import io.usethesource.criterion.BenchmarkUtils.ValueFactoryFactory;
import io.usethesource.criterion.api.JmhSet;
import io.usethesource.criterion.api.JmhSetMultimap;
import io.usethesource.criterion.api.JmhValue;
import io.usethesource.criterion.api.JmhValueFactory;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OperationsPerInvocation;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Thread)
public class JmhSetMultimapBenchmarks {

  private static boolean USE_PRIMITIVE_DATA = false;

  @Param // ({ "SET_MULTIMAP" })
  public DataType dataType;

  @Param({"MATCH"})
  public SampleDataSelection sampleDataSelection;

  @Param
  public ValueFactoryFactory valueFactoryFactory;

  /*
   * (for (i <- 0 to 23) yield s"'${Math.pow(2, i).toInt}'").mkString(", " ).replace("'", "\"")
   */
  // @Param({ "1", "2", "4", "8", "16", "32", "64", "128", "256", "512",
  // "1024", "2048", "4096",
  // "8192", "16384", "32768", "65536", "131072", "262144", "524288",
  // "1048576",
  // "2097152", "4194304", "8388608" })

  /*
   * val l = (for (i <- 0 to 23) yield s"'${Math.pow(2, i).toInt}'") val r = (for (i <- 0 to 23)
   * yield s"'${Math.pow(2, i-1).toInt + Math.pow(2, i).toInt}'") val zipped = l zip r flatMap {
   * case (x,y) => List(x,y) }
   *
   * val all = zipped.drop(1).take(zipped.size - 2) all.mkString(", ").replace("'", "\"")
   */
  @Param({"1", "2", "3", "4", "6", "8", "12", "16", "24", "32", "48", "64", "96", "128", "192",
      "256", "384", "512", "768", "1024", "1536", "2048", "3072", "4096", "6144", "8192", "12288",
      "16384", "24576", "32768", "49152", "65536", "98304", "131072", "196608", "262144", "393216",
      "524288", "786432", "1048576", "1572864", "2097152", "3145728", "4194304", "6291456",
      "8388608"})
  protected int size;

  @Param({"2"})
  protected int multimapValueSize;

  @Param({"2"})
  protected int stepSizeOneToOneSelector;

  @Param({"0"}) // "1", "2", "3", "4", "5", "6", "7", "8", "9"
  protected int run;

  @Param
  public ElementProducer producer;

  public JmhValueFactory valueFactory;

  public JmhSetMultimap testMap;
  private JmhSetMultimap testMapRealDuplicate;
  private JmhSetMultimap testMapDeltaDuplicate;

  private java.util.Set<JmhValue> cachedKeySet;
  private java.util.Set<JmhValue> canonicalKeySet;

  public JmhSetMultimap testMapInt;

  private JmhSetMultimap testMapRealDuplicateSameSizeButDifferent;

  public JmhValue VALUE_EXISTING;
  public JmhValue VALUE_NOT_EXISTING;

  public int VALUE_EXISTING_INT;
  public int VALUE_NOT_EXISTING_INT;

  public static final int CACHED_NUMBERS_SIZE = 8;
  public JmhValue[] cachedNumbers = new JmhValue[CACHED_NUMBERS_SIZE];
  public JmhValue[] cachedNumbersNotContained = new JmhValue[CACHED_NUMBERS_SIZE];

  public int[] cachedNumbersInt = new int[CACHED_NUMBERS_SIZE];
  public int[] cachedNumbersIntNotContained = new int[CACHED_NUMBERS_SIZE];

  private JmhSetMultimap singletonMapWithExistingValue;
  private JmhSetMultimap singletonMapWithNotExistingValue;

  @Setup(Level.Trial)
  public void setUp() throws Exception {
    // TODO: look for right place where to put this
    SleepingInteger.IS_SLEEP_ENABLED_IN_HASHCODE = false;
    SleepingInteger.IS_SLEEP_ENABLED_IN_EQUALS = false;

    setUpTestMapWithRandomContent(size, run);

    valueFactory = valueFactoryFactory.getInstance();

    testMap = generateSetMultimap(valueFactory, producer, false, size, multimapValueSize,
        stepSizeOneToOneSelector, run);
    testMapRealDuplicate = generateSetMultimap(valueFactory, producer, false, size,
        multimapValueSize, stepSizeOneToOneSelector, run);

    VALUE_EXISTING =
        (JmhValue) generateExistingAndNonExistingValue(valueFactory, producer, false, size, run)[0];
    VALUE_NOT_EXISTING =
        (JmhValue) generateExistingAndNonExistingValue(valueFactory, producer, false, size, run)[1];

    if (USE_PRIMITIVE_DATA) {
      testMapInt = generateSetMultimap(valueFactory, producer, true, size, multimapValueSize,
          stepSizeOneToOneSelector, run);
      // TODO: testMapRealDuplicateInt = ...

      VALUE_EXISTING_INT =
          (int) generateExistingAndNonExistingValue(valueFactory, producer, true, size, run)[0];
      VALUE_NOT_EXISTING_INT =
          (int) generateExistingAndNonExistingValue(valueFactory, producer, true, size, run)[1];
    }

    switch (sampleDataSelection) {

      /*
       * random integers might or might not be in the dataset
       */
      case RANDOM: {
        // random data generator with fixed seed
        /* seed == Mersenne Prime #8 */
        Random randForOperations = new Random(2147483647L);

        for (int i = 0; i < CACHED_NUMBERS_SIZE; i++) {
          cachedNumbers[i] = producer.createFromInt(randForOperations.nextInt());
        }
      }

      /*
       * random integers are in the dataset
       */
      case MATCH: {
        // random data generator with fixed seed
        int seedForThisTrial = BenchmarkUtils.seedFromSizeAndRun(size, run);
        Random rand = new Random(seedForThisTrial);

        for (int i = 0; i < CACHED_NUMBERS_SIZE; i++) {
          if (i >= size) {
            cachedNumbers[i] = cachedNumbers[i % size];
            cachedNumbersInt[i] = cachedNumbersInt[i % size];
          } else {
            int nextInt = rand.nextInt();
            cachedNumbers[i] = producer.createFromInt(nextInt);
            cachedNumbersInt[i] = nextInt;
          }
        }

        // random data generator with fixed seed
        /* seed == Mersenne Prime #8 */
        Random anotherRand = new Random(2147483647L);

        for (int i = 0; i < CACHED_NUMBERS_SIZE; i++) {
          /*
           * generate random values until a value not part of the data strucure is found
           */
          boolean found = false;
          while (!found) {
            final int nextInt = anotherRand.nextInt();

            final JmhValue candidate = producer.createFromInt(nextInt);
            final int candidateInt = nextInt;

            if (testMap.containsKey(candidate)) { // || testMap.containsKey(candidateInt)
              continue;
            } else {
              cachedNumbersNotContained[i] = candidate;
              cachedNumbersIntNotContained[i] = candidateInt;
              found = true;
            }
          }
        }

        if (false) { // USE_PRIMITIVE_DATA
          // // assert (contained)
          // for (int sample : cachedNumbersInt) {
          // if (!testMapInt.containsKey(sample)) {
          // throw new IllegalStateException();
          // }
          // }
          //
          // // assert (not contained)
          // for (int sample : cachedNumbersIntNotContained) {
          // if (testMapInt.containsKey(sample)) {
          // throw new IllegalStateException();
          // }
          // }
        } else {
          // assert (contained)
          for (JmhValue sample : cachedNumbers) {
            if (!testMap.containsKey(sample)) {
              throw new IllegalStateException();
            }
          }

          // assert (not contained)
          for (JmhValue sample : cachedNumbersNotContained) {
            if (testMap.containsKey(sample)) {
              throw new IllegalStateException();
            }
          }
        }
      }
    }

    final JmhSetMultimap.Builder mapWriter1 = valueFactory.setMultimapBuilder();
    mapWriter1.insert(VALUE_EXISTING, VALUE_EXISTING);
    singletonMapWithExistingValue = mapWriter1.done();

    final JmhSetMultimap.Builder mapWriter2 = valueFactory.setMultimapBuilder();
    mapWriter2.insert(VALUE_NOT_EXISTING, VALUE_NOT_EXISTING);
    singletonMapWithNotExistingValue = mapWriter2.done();

    // System.out.println(String.format("\n\ncachedNumbers = %s",
    // Arrays.toString(cachedNumbers)));
    // System.out.println(String.format("cachedNumbersNotContained =
    // %s\n\n",
    // Arrays.toString(cachedNumbersNotContained)));

    // TODO: look for right place where to put this
    SleepingInteger.IS_SLEEP_ENABLED_IN_HASHCODE = false;
    SleepingInteger.IS_SLEEP_ENABLED_IN_EQUALS = false;

    // OverseerUtils.setup(JmhSetMultimapBenchmarks.class, this);

    cachedKeySet = testMap.keySet();

    final JmhSet.Builder keySetWriter = valueFactory.setBuilder();
    for (JmhValue item : testMap.keySet()) {
      keySetWriter.insert(item);
    }
    canonicalKeySet = keySetWriter.done().asJavaSet();
  }

  protected void setUpTestMapWithRandomContent(int size, int run) throws Exception {

    valueFactory = valueFactoryFactory.getInstance();

    JmhSetMultimap.Builder writer1 = valueFactory.setMultimapBuilder();
    JmhSetMultimap.Builder writer2 = valueFactory.setMultimapBuilder();

    int seedForThisTrial = BenchmarkUtils.seedFromSizeAndRun(size, run);
    Random rand = new Random(seedForThisTrial + 13);
    int existingValueIndex = rand.nextInt(size);

    int[] data = BenchmarkUtils.generateTestData(size, run);

    for (int i = size - 1; i >= 0; i--) {
      // final IValue current = producer.createFromInt(data[i]);

      if (false) { // USE_PRIMITIVE_DATA
        // writer1.insert(data[i], data[i]);
        // writer2.insert(data[i], data[i]);
      } else {
        writer1.insert(producer.createFromInt(data[i]), producer.createFromInt(data[i]));
        writer2.insert(producer.createFromInt(data[i]), producer.createFromInt(data[i]));
      }

      if (i == existingValueIndex) {
        VALUE_EXISTING = producer.createFromInt(data[i]);
      }
    }

    testMap = writer1.done();
    testMapRealDuplicate = writer2.done();

    /*
     * generate random values until a value not part of the data strucure is found
     */
    while (VALUE_NOT_EXISTING == null) {
      final int candidateInt = rand.nextInt();
      final JmhValue candidate = producer.createFromInt(candidateInt);

      if (!testMap.containsKey(candidate)) { // !testMap.containsKey(candidateInt) &&
        VALUE_NOT_EXISTING_INT = candidateInt;
        VALUE_NOT_EXISTING = candidate;
      }
    }

    // TODO: put is compatible with regular map backends that don't
    // implement insert, but won't work for real multimaps. fix it.
    testMapDeltaDuplicate =
        testMap.put(VALUE_EXISTING, VALUE_NOT_EXISTING).put(VALUE_EXISTING, VALUE_EXISTING);

    if (testMap.size() != testMapDeltaDuplicate.size()) {
      throw new IllegalStateException();
    }

    // TODO: put is compatible with regular map backends that don't
    // implement insert, but won't work for real multimaps. fix it.
    testMapRealDuplicateSameSizeButDifferent =
        testMapRealDuplicate.remove(VALUE_EXISTING, VALUE_EXISTING)
            .put(VALUE_NOT_EXISTING, VALUE_NOT_EXISTING);

    if (testMap.size() != testMapRealDuplicateSameSizeButDifferent.size()) {
      throw new IllegalStateException();
    }
  }

  protected static JmhSetMultimap generateSetMultimap(JmhValueFactory valueFactory,
      ElementProducer producer, boolean usePrimitiveData, int size, int multimapValueSize,
      int stepSizeOneToOneSelector, int run) throws Exception {

    final int[] data = BenchmarkUtils.generateTestData(size, run);
    final JmhSetMultimap.Builder writer = valueFactory.setMultimapBuilder();

    /*
     * TODO: update algorithm for selection data contained/not-contained to use idx %
     * multimapValueSize == 0 for checking contained keys, and idx % multimapValueSize != 0 for
     * checking contained values.
     */

    for (int i = size - 1; i >= 0; i--) {
      int keyIdx = i;

      if (keyIdx % stepSizeOneToOneSelector == 0) {
        writer.insert(producer.createFromInt(data[keyIdx]), producer.createFromInt(data[keyIdx]));
      } else {
        for (int j = multimapValueSize - 1; j >= 0 && i >= 0; j--) {
          int valIdx = (i + j) % size;

          if (false && usePrimitiveData) {
            // writer.insert(data[keyIdx], data[valIdx]);
          } else {
            writer.insert(producer.createFromInt(data[keyIdx]),
                producer.createFromInt(data[valIdx]));
          }
        }
      }
    }

    return writer.done();
  }

  protected static Object[] generateExistingAndNonExistingValue(JmhValueFactory valueFactory,
      ElementProducer producer, boolean usePrimitiveData, int size, int run) throws Exception {

    int[] data = BenchmarkUtils.generateTestData(size, run);

    int[] sortedData = data.clone();
    Arrays.sort(sortedData);

    int seedForThisTrial = BenchmarkUtils.seedFromSizeAndRun(size, run);
    Random rand = new Random(seedForThisTrial + 13);
    int existingValueIndex = rand.nextInt(size);

    final Object VALUE_EXISTING;

    if (usePrimitiveData) {
      VALUE_EXISTING = data[existingValueIndex];
    } else {
      VALUE_EXISTING = producer.createFromInt(data[existingValueIndex]);
    }

    final Object VALUE_NOT_EXISTING;
    /*
     * generate random values until a value not part of the data strucure is found
     */
    while (true) {
      final int candidateInt = rand.nextInt();

      if (Arrays.binarySearch(sortedData, candidateInt) == -1) {
        if (usePrimitiveData) {
          VALUE_NOT_EXISTING = candidateInt;
        } else {
          VALUE_NOT_EXISTING = producer.createFromInt(candidateInt);
        }
        break;
      }
    }

    return new Object[]{VALUE_EXISTING, VALUE_NOT_EXISTING};
  }

  // @TearDown(Level.Trial)
  // public void tearDown() {
  // OverseerUtils.tearDown();
  // }
  //
  // // @Setup(Level.Iteration)
  // // public void setupIteration() {
  // // OverseerUtils.doRecord(true);
  // // }
  // //
  // // @TearDown(Level.Iteration)
  // // public void tearDownIteration() {
  // // OverseerUtils.doRecord(false);
  // // }
  //
  // @Setup(Level.Invocation)
  // public void setupInvocation() {
  // OverseerUtils.setup(JmhSetMultimapBenchmarks.class, this);
  // OverseerUtils.doRecord(true);
  // }
  //
  // @TearDown(Level.Invocation)
  // public void tearDownInvocation() {
  // OverseerUtils.doRecord(false);
  // }

  // @Benchmark
  // public void timeMapLikeContainsKeySingle(Blackhole bh) {
  // bh.consume(testMap.containsKey(VALUE_EXISTING));
  // }

  @Benchmark
  @OperationsPerInvocation(CACHED_NUMBERS_SIZE)
  public void timeMapLikeContainsKey(Blackhole bh) {
    for (int i = 0; i < CACHED_NUMBERS_SIZE; i++) {
      bh.consume(testMap.containsKey(cachedNumbers[i]));
    }
  }

  @Benchmark
  @OperationsPerInvocation(CACHED_NUMBERS_SIZE)
  public void timeMapLikeContainsValue(Blackhole bh) {
    for (int i = 0; i < CACHED_NUMBERS_SIZE; i++) {
      bh.consume(testMap.containsValue(cachedNumbers[i]));
    }
  }

  // // @Benchmark /* Type=Int */
  // @OperationsPerInvocation(CACHED_NUMBERS_SIZE)
  // public void timeMapLikeContainsKeyInt(Blackhole bh) {
  // for (int i = 0; i < CACHED_NUMBERS_SIZE; i++) {
  // bh.consume(testMapInt.containsKey(cachedNumbersInt[i]));
  // }
  // }

  @Benchmark
  @OperationsPerInvocation(CACHED_NUMBERS_SIZE)
  public void timeMapLikeContainsKeyNotContained(Blackhole bh) {
    for (int i = 0; i < CACHED_NUMBERS_SIZE; i++) {
      bh.consume(testMap.containsKey(cachedNumbersNotContained[i]));
    }
  }

  @Benchmark
  @OperationsPerInvocation(2 * CACHED_NUMBERS_SIZE)
  public void timeMultimapLikeContainsTuple(Blackhole bh) {
    // partial match
    for (int i = 0; i < CACHED_NUMBERS_SIZE; i++) {
      bh.consume(testMap.contains(cachedNumbers[i], cachedNumbersNotContained[i]));
    }
    // full match
    for (int i = 0; i < CACHED_NUMBERS_SIZE; i++) {
      bh.consume(testMap.contains(cachedNumbers[i], cachedNumbers[i]));
    }
  }

  @Benchmark
  @OperationsPerInvocation(CACHED_NUMBERS_SIZE)
  public void timeMultimapLikeContainsTupleNotContained(Blackhole bh) {
    for (int i = 0; i < CACHED_NUMBERS_SIZE; i++) {
      bh.consume(testMap.contains(cachedNumbersNotContained[i], cachedNumbersNotContained[i]));
    }
  }

  @Benchmark
  public void timeMapLikeIterationKey(Blackhole bh) {
    for (Iterator<JmhValue> iterator = testMap.iterator(); iterator.hasNext(); ) {
      bh.consume(iterator.next());
    }
  }

  @Benchmark
  public void timeMapLikeIterationNativeEntry(Blackhole bh) {
    for (Iterator<java.util.Map.Entry<JmhValue, Object>> iterator =
        testMap.nativeEntryIterator(); iterator.hasNext(); ) {
      bh.consume(iterator.next());
    }
  }

  // @Benchmark
  // public void timeMultimapLikeIterationKey(Blackhole bh) {
  // for (Iterator<JmhValue> iterator = testMap.iterator(); iterator.hasNext();) {
  // bh.consume(iterator.next());
  // }
  // }

  @Benchmark
  public void timeMultimapLikeIterationFlattenedEntry(Blackhole bh) {
    for (Iterator<java.util.Map.Entry<JmhValue, JmhValue>> iterator =
        testMap.entryIterator(); iterator.hasNext(); ) {
      bh.consume(iterator.next());
    }
  }

  // @Benchmark
  // @OperationsPerInvocation(CACHED_NUMBERS_SIZE)
  // public void timeMapLikePut(Blackhole bh) {
  // for (int i = 0; i < CACHED_NUMBERS_SIZE; i++) {
  // bh.consume(testMap.put(cachedNumbersNotContained[i], VALUE_NOT_EXISTING));
  // }
  // }
  //
  // @Benchmark
  // @OperationsPerInvocation(CACHED_NUMBERS_SIZE)
  // public void timeMapLikePutContained(Blackhole bh) {
  // for (int i = 0; i < CACHED_NUMBERS_SIZE; i++) {
  // bh.consume(testMap.put(cachedNumbers[i], cachedNumbers[i]));
  // }
  // }

  @Benchmark
  @OperationsPerInvocation(2 * CACHED_NUMBERS_SIZE)
  public void timeMapLikePut(Blackhole bh) {
    // full match
    for (int i = 0; i < CACHED_NUMBERS_SIZE; i++) {
      bh.consume(testMap.put(cachedNumbers[i], cachedNumbers[i]));
    }
    // no match
    for (int i = 0; i < CACHED_NUMBERS_SIZE; i++) {
      bh.consume(testMap.put(cachedNumbersNotContained[i], VALUE_NOT_EXISTING));
    }
  }

  @Benchmark
  @OperationsPerInvocation(3 * CACHED_NUMBERS_SIZE)
  public void timeMultimapLikeInsertTuple(Blackhole bh) {
    // full match
    for (int i = 0; i < CACHED_NUMBERS_SIZE; i++) {
      bh.consume(testMap.insert(cachedNumbers[i], cachedNumbers[i]));
    }
    // partial match
    for (int i = 0; i < CACHED_NUMBERS_SIZE; i++) {
      bh.consume(testMap.insert(cachedNumbers[i], cachedNumbersNotContained[i]));
    }
    // no match
    for (int i = 0; i < CACHED_NUMBERS_SIZE; i++) {
      bh.consume(testMap.insert(cachedNumbersNotContained[i], cachedNumbers[i]));
    }
  }

  // @Benchmark
  // @OperationsPerInvocation(CACHED_NUMBERS_SIZE)
  // public void timeMultimapLikeInsertTupleContained(Blackhole bh) {
  // for (int i = 0; i < CACHED_NUMBERS_SIZE; i++) {
  // bh.consume(testMap.insert(cachedNumbers[i], cachedNumbers[i]));
  // }
  // }

  @Benchmark
  @OperationsPerInvocation(CACHED_NUMBERS_SIZE)
  public void timeMapLikeRemove(Blackhole bh) {
    // full match
    for (int i = 0; i < CACHED_NUMBERS_SIZE; i++) {
      bh.consume(testMap.remove(cachedNumbers[i]));
    }
  }

  // @Benchmark
  // @OperationsPerInvocation(CACHED_NUMBERS_SIZE)
  // public void timeMapLikeRemoveNotContained(Blackhole bh) {
  // // no match
  // for (int i = 0; i < CACHED_NUMBERS_SIZE; i++) {
  // bh.consume(testMap.remove(cachedNumbersNotContained[i]));
  // }
  // }

  @Benchmark
  @OperationsPerInvocation(2 * CACHED_NUMBERS_SIZE)
  public void timeMultimapLikeRemoveTuple(Blackhole bh) {
    // full match
    for (int i = 0; i < CACHED_NUMBERS_SIZE; i++) {
      bh.consume(testMap.remove(cachedNumbers[i], cachedNumbers[i]));
    }
    // partial match
    for (int i = 0; i < CACHED_NUMBERS_SIZE; i++) {
      bh.consume(testMap.remove(cachedNumbers[i], cachedNumbersNotContained[i]));
    }
  }

  // @Benchmark
  // @OperationsPerInvocation(CACHED_NUMBERS_SIZE)
  // public void timeMultimapLikeRemoveTupleNotContained(Blackhole bh) {
  // for (int i = 0; i < CACHED_NUMBERS_SIZE; i++) {
  // bh.consume(testMap.remove(cachedNumbersNotContained[i], cachedNumbersNotContained[i]));
  // }
  // }

  // @Benchmark
  // public void timeMultimapLikeEqualsRealDuplicate(Blackhole bh) {
  // bh.consume(testMap.equals(testMapRealDuplicate));
  // }
  //
  // @Benchmark
  // public void timeMultimapLikeEqualsDeltaDuplicate(Blackhole bh) {
  // bh.consume(testMap.equals(testMapDeltaDuplicate));
  // }

  // @Benchmark
  // public void timeMapLikeEqualsRealDuplicate(Blackhole bh) {
  // bh.consume(testMap.equals(testMapRealDuplicate));
  // }
  //
  //// @Benchmark
  //// public void timeMapLikeEqualsRealDuplicateModified(Blackhole bh) {
  //// bh.consume(testMap.equals(testMapRealDuplicateSameSizeButDifferent));
  //// }
  //
  // @Benchmark
  // public void timeMapLikeEqualsDeltaDuplicate(Blackhole bh) {
  // bh.consume(testMap.equals(testMapDeltaDuplicate));
  // }

  // @Benchmark
  // @BenchmarkMode(Mode.SingleShotTime)
  //// @Warmup(iterations = 0)
  //// @Measurement(iterations = 1)
  // public void timeHashCode(Blackhole bh) {
  // bh.consume(testMap.hashCode());
  // }

  @Benchmark
  public void timeKeySetWithBuilder(Blackhole bh) {
    final java.util.Set<JmhValue> keySetView = testMap.keySet();
    final JmhSet.Builder builder = valueFactory.setBuilder();

    keySetView.forEach(builder::insert);

    bh.consume(builder.done());
  }

  @Benchmark
  public void timeKeySet(Blackhole bh) {
    bh.consume(testMap.keySet());
  }

  @Benchmark
  public void timeKeySetEqualsCanonicalSet(Blackhole bh) {
    bh.consume(testMap.keySet().equals(canonicalKeySet));
  }

  @Benchmark
  public void timeKeySetCachedEqualsCanonicalSet(Blackhole bh) {
    bh.consume(cachedKeySet.equals(canonicalKeySet));
  }

  public static void main(String[] args) throws RunnerException {
    System.out.println(JmhSetMultimapBenchmarks.class.getSimpleName());

    // timeKeySet*EqualsCanonicalSet, timeMultimapLike*
    // (timeMapLikeContainsValue|timeMultimapLikeInsertTuple|timeMultimapLikeRemoveTuple)

    // @formatter:off
    Options opt = new OptionsBuilder()
        .include(".*" + JmhSetMultimapBenchmarks.class.getSimpleName()
            + ".timeMultimapLikeContainsTuple$")
        .timeUnit(TimeUnit.NANOSECONDS)
        .mode(Mode.AverageTime)
        .warmupIterations(10)
        .warmupTime(TimeValue.seconds(1))
        .measurementIterations(10)
        .forks(0)
        .shouldDoGC(true)
        .param("dataType", "SET_MULTIMAP")
        .param("run", "0")
//        .param("run", "1")
//        .param("run", "2")
//        .param("run", "3")
//        .param("run", "4")
        .param("producer", "PURE_INTEGER")
        .param("sampleDataSelection", "MATCH")
//        .param("size", "8388608")
//        .param("size", "1048576")
        .param("size", "2048")
        .param("size", "16")
        .param("multimapValueSize", "2") // 2
        .param("stepSizeOneToOneSelector", "2") // 2
        .param("valueFactoryFactory", "VF_GUAVA_IMMUTABLE")
//        .param("valueFactoryFactory", "VF_BINARY_RELATION")
//        .param("valueFactoryFactory", "VF_CHAMP")
//        .param("valueFactoryFactory", "VF_CHAMP_HETEROGENEOUS")
//        .param("valueFactoryFactory", "VF_CHAMP_MULTIMAP_PROTOTYPE_OLD")
//        .param("valueFactoryFactory", "VF_CHAMP_MAP_AS_MULTIMAP")
//        .param("valueFactoryFactory", "VF_CHAMP_MULTIMAP_HCHAMP")
        .param("valueFactoryFactory", "VF_CHAMP_MULTIMAP_HHAMT")
//        .param("valueFactoryFactory", "VF_CHAMP_MULTIMAP_HHAMT_SPECIALIZED")
        .param("valueFactoryFactory", "VF_CHAMP_MULTIMAP_HHAMT_INTERLINKED")
//        .param("valueFactoryFactory", "VF_CHAMP_MULTIMAP_HHAMT_NEW")
        .param("valueFactoryFactory", "VF_CHAMP_MULTIMAP_HHAMT_SPECIALIZED_PATH_INTERLINKED")
//        .param("valueFactoryFactory", "VF_SCALA")
//        .param("valueFactoryFactory", "VF_CLOJURE")
        .build();
    // @formatter:on

    new Runner(opt).run();
  }

}
