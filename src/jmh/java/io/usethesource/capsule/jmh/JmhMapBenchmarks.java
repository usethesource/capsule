/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.capsule.jmh;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import io.usethesource.capsule.jmh.api.JmhMap;
import io.usethesource.capsule.jmh.api.JmhValue;
import io.usethesource.capsule.jmh.api.JmhValueFactory;
import io.usethesource.capsule.jmh.BenchmarkUtils.DataType;
import io.usethesource.capsule.jmh.BenchmarkUtils.SampleDataSelection;
import io.usethesource.capsule.jmh.BenchmarkUtils.ValueFactoryFactory;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OperationsPerInvocation;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Thread)
public class JmhMapBenchmarks {

  private static boolean USE_PRIMITIVE_DATA = false;

  @Param({"MAP"})
  public DataType dataType;

  @Param({"MATCH"})
  public SampleDataSelection sampleDataSelection;

  @Param
  public ValueFactoryFactory valueFactoryFactory;

  /*
   * (for (i <- 0 to 23) yield s"'${Math.pow(2, i).toInt}'").mkString(", " ).replace("'", "\"")
   */
  @Param({"1", "2", "4", "8", "16", "32", "64", "128", "256", "512", "1024", "2048", "4096", "8192",
      "16384", "32768", "65536", "131072", "262144", "524288", "1048576", "2097152", "4194304",
      "8388608"})
  protected int size;

  @Param({"0"}) // "1", "2", "3", "4", "5", "6", "7", "8", "9"
  protected int run;

  @Param
  public ElementProducer producer;

  public JmhValueFactory valueFactory;

  public JmhMap testMap;
  private JmhMap testMapRealDuplicate;
  private JmhMap testMapDeltaDuplicate;

  public JmhMap testMapInt;

  private JmhMap testMapRealDuplicateSameSizeButDifferent;

  public JmhValue VALUE_EXISTING;
  public JmhValue VALUE_NOT_EXISTING;

  public int VALUE_EXISTING_INT;
  public int VALUE_NOT_EXISTING_INT;

  public static final int CACHED_NUMBERS_SIZE = 8;
  public JmhValue[] cachedNumbers = new JmhValue[CACHED_NUMBERS_SIZE];
  public JmhValue[] cachedNumbersNotContained = new JmhValue[CACHED_NUMBERS_SIZE];

  public int[] cachedNumbersInt = new int[CACHED_NUMBERS_SIZE];
  public int[] cachedNumbersIntNotContained = new int[CACHED_NUMBERS_SIZE];

  private JmhMap singletonMapWithExistingValue;
  private JmhMap singletonMapWithNotExistingValue;

  @Setup(Level.Trial)
  public void setUp() throws Exception {
    // TODO: look for right place where to put this
    SleepingInteger.IS_SLEEP_ENABLED_IN_HASHCODE = false;
    SleepingInteger.IS_SLEEP_ENABLED_IN_EQUALS = false;

    setUpTestMapWithRandomContent(size, run);

    valueFactory = valueFactoryFactory.getInstance();

    testMap = generateMap(valueFactory, producer, false, size, run);
    testMapRealDuplicate = generateMap(valueFactory, producer, false, size, run);

    VALUE_EXISTING =
        (JmhValue) generateExistingAndNonExistingValue(valueFactory, producer, false, size, run)[0];
    VALUE_NOT_EXISTING =
        (JmhValue) generateExistingAndNonExistingValue(valueFactory, producer, false, size, run)[1];

    if (USE_PRIMITIVE_DATA) {
      testMapInt = generateMap(valueFactory, producer, true, size, run);
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

    final JmhMap.Builder mapWriter1 = valueFactory.mapBuilder();
    mapWriter1.put(VALUE_EXISTING, VALUE_EXISTING);
    singletonMapWithExistingValue = mapWriter1.done();

    final JmhMap.Builder mapWriter2 = valueFactory.mapBuilder();
    mapWriter2.put(VALUE_NOT_EXISTING, VALUE_NOT_EXISTING);
    singletonMapWithNotExistingValue = mapWriter2.done();

    // System.out.println(String.format("\n\ncachedNumbers = %s",
    // Arrays.toString(cachedNumbers)));
    // System.out.println(String.format("cachedNumbersNotContained =
    // %s\n\n",
    // Arrays.toString(cachedNumbersNotContained)));

    // TODO: look for right place where to put this
    SleepingInteger.IS_SLEEP_ENABLED_IN_HASHCODE = false;
    SleepingInteger.IS_SLEEP_ENABLED_IN_EQUALS = false;

    // OverseerUtils.setup(JmhMapBenchmarks.class, this);
  }

  protected void setUpTestMapWithRandomContent(int size, int run) throws Exception {

    valueFactory = valueFactoryFactory.getInstance();

    JmhMap.Builder writer1 = valueFactory.mapBuilder();
    JmhMap.Builder writer2 = valueFactory.mapBuilder();

    int seedForThisTrial = BenchmarkUtils.seedFromSizeAndRun(size, run);
    Random rand = new Random(seedForThisTrial + 13);
    int existingValueIndex = rand.nextInt(size);

    int[] data = BenchmarkUtils.generateTestData(size, run);

    for (int i = size - 1; i >= 0; i--) {
      // final IValue current = producer.createFromInt(data[i]);

      if (false && USE_PRIMITIVE_DATA) {
        // writer1.put(data[i], data[i]);
        // writer2.put(data[i], data[i]);
      } else {
        writer1.put(producer.createFromInt(data[i]), producer.createFromInt(data[i]));
        writer2.put(producer.createFromInt(data[i]), producer.createFromInt(data[i]));
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

    testMapDeltaDuplicate =
        testMap.put(VALUE_EXISTING, VALUE_NOT_EXISTING).put(VALUE_EXISTING, VALUE_EXISTING);

    testMapRealDuplicateSameSizeButDifferent =
        testMapRealDuplicate.removeKey(VALUE_EXISTING).put(VALUE_NOT_EXISTING, VALUE_NOT_EXISTING);
  }

  protected static JmhMap generateMap(JmhValueFactory valueFactory, ElementProducer producer,
      boolean usePrimitiveData, int size, int run) throws Exception {

    final int[] data = BenchmarkUtils.generateTestData(size, run);
    final JmhMap.Builder writer = valueFactory.mapBuilder();

    for (int i = size - 1; i >= 0; i--) {
      if (false && usePrimitiveData) {
        // writer.put(data[i], data[i]);
      } else {
        writer.put(producer.createFromInt(data[i]), producer.createFromInt(data[i]));
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
  // OverseerUtils.setup(JmhMapBenchmarks.class, this);
  // OverseerUtils.doRecord(true);
  // }
  //
  // @TearDown(Level.Invocation)
  // public void tearDownInvocation() {
  // OverseerUtils.doRecord(false);
  // }

  // @Benchmark
  // public void timeContainsKeySingle(Blackhole bh) {
  // bh.consume(testMap.containsKey(VALUE_EXISTING));
  // }

  @Benchmark
  @OperationsPerInvocation(CACHED_NUMBERS_SIZE)
  public void timeContainsKey(Blackhole bh) {
    for (int i = 0; i < CACHED_NUMBERS_SIZE; i++) {
      bh.consume(testMap.containsKey(cachedNumbers[i]));
    }
  }

  // @Benchmark
  // @OperationsPerInvocation(CACHED_NUMBERS_SIZE)
  // public void timeContainsKeyInt(Blackhole bh) {
  // for (int i = 0; i < CACHED_NUMBERS_SIZE; i++) {
  // bh.consume(testMapInt.containsKey(cachedNumbersInt[i]));
  // }
  // }

  @Benchmark
  @OperationsPerInvocation(CACHED_NUMBERS_SIZE)
  public void timeContainsKeyNotContained(Blackhole bh) {
    for (int i = 0; i < CACHED_NUMBERS_SIZE; i++) {
      bh.consume(testMap.containsKey(cachedNumbersNotContained[i]));
    }
  }

  @Benchmark
  public void timeIteration(Blackhole bh) {
    for (Iterator<JmhValue> iterator = testMap.iterator(); iterator.hasNext(); ) {
      bh.consume(iterator.next());
    }
  }

  @Benchmark
  public void timeIterationEntry(Blackhole bh) {
    for (Iterator<java.util.Map.Entry<JmhValue, JmhValue>> iterator =
        testMap.entryIterator(); iterator.hasNext(); ) {
      bh.consume(iterator.next());
    }
  }

  // @Benchmark
  // public void timeInsertSingle(Blackhole bh) {
  // bh.consume(testMap.put(VALUE_NOT_EXISTING, VALUE_NOT_EXISTING));
  // }

  @Benchmark
  @OperationsPerInvocation(CACHED_NUMBERS_SIZE)
  public void timeInsert(Blackhole bh) {
    for (int i = 0; i < CACHED_NUMBERS_SIZE; i++) {
      bh.consume(testMap.put(cachedNumbersNotContained[i], VALUE_NOT_EXISTING));
    }
  }

  // @Benchmark
  // @OperationsPerInvocation(CACHED_NUMBERS_SIZE)
  // public void timeInsertInt(Blackhole bh) {
  // for (int i = 0; i < CACHED_NUMBERS_SIZE; i++) {
  // bh.consume(testMapInt.put(cachedNumbersIntNotContained[i], VALUE_NOT_EXISTING_INT));
  // }
  // }

  @Benchmark
  @OperationsPerInvocation(CACHED_NUMBERS_SIZE)
  public void timeInsertContained(Blackhole bh) {
    for (int i = 0; i < CACHED_NUMBERS_SIZE; i++) {
      bh.consume(testMap.put(cachedNumbers[i], cachedNumbers[i]));
    }
  }

  @Benchmark
  @OperationsPerInvocation(CACHED_NUMBERS_SIZE)
  public void timeRemoveKeyNotContained(Blackhole bh) {
    for (int i = 0; i < CACHED_NUMBERS_SIZE; i++) {
      bh.consume(testMap.removeKey(cachedNumbersNotContained[i]));
    }
  }

  @Benchmark
  @OperationsPerInvocation(CACHED_NUMBERS_SIZE)
  public void timeRemoveKey(Blackhole bh) {
    for (int i = 0; i < CACHED_NUMBERS_SIZE; i++) {
      bh.consume(testMap.removeKey(cachedNumbers[i]));
    }
  }

  @Benchmark
  public void timeEqualsRealDuplicate(Blackhole bh) {
    bh.consume(testMap.equals(testMapRealDuplicate));
  }

  @Benchmark
  public void timeEqualsRealDuplicateModified(Blackhole bh) {
    bh.consume(testMap.equals(testMapRealDuplicateSameSizeButDifferent));
  }

  @Benchmark
  public void timeEqualsDeltaDuplicate(Blackhole bh) {
    bh.consume(testMap.equals(testMapDeltaDuplicate));
  }

  @Benchmark
  @BenchmarkMode(Mode.SingleShotTime)
  @Warmup(iterations = 0)
  @Measurement(iterations = 1)
  public void timeHashCodeOnce(Blackhole bh) {
    bh.consume(testMap.hashCode());
  }

  @Benchmark
  public void timeHashCode(Blackhole bh) {
    bh.consume(testMap.hashCode());
  }

  public static void main(String[] args) throws RunnerException {
    System.out.println(JmhMapBenchmarks.class.getSimpleName());

    // @formatter:off
    Options opt = new OptionsBuilder()
        .include(".*" + JmhMapBenchmarks.class.getSimpleName() + ".(.*)")
        .timeUnit(TimeUnit.NANOSECONDS)
        .mode(Mode.AverageTime)
        .warmupIterations(10)
        .warmupTime(TimeValue.seconds(1))
        .measurementIterations(10)
        .forks(1)
        .param("dataType", "MAP")
        .param("run", "0")
//        .param("run", "1")
//        .param("run", "2")
//        .param("run", "3")
//        .param("run", "4")
        .param("producer", "PURE_INTEGER")
        .param("sampleDataSelection", "MATCH")
        .param("size", "16")
        .param("size", "2048")
        .param("size", "1048576")
        .param("valueFactoryFactory", "VF_CHAMP")
//        .param("valueFactoryFactory", "VF_SCALA")
//        .param("valueFactoryFactory", "VF_CLOJURE")
//        .param("valueFactoryFactory", "VF_JAVASLANG")
//        .param("valueFactoryFactory", "VF_PAGURO")
//        .param("valueFactoryFactory", "VF_DEXX")
//        .param("valueFactoryFactory", "VF_PCOLLECTIONS")
//        .param("valueFactoryFactory", "VF_GUAVA_IMMUTABLE")
        .build();
    // @formatter:on

    new Runner(opt).run();
  }

}
