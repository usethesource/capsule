/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.capsule.jmh;

import io.usethesource.capsule.jmh.api.JmhSet;
import io.usethesource.capsule.jmh.api.JmhValue;
import io.usethesource.capsule.jmh.api.JmhValueFactory;
import io.usethesource.capsule.jmh.profiler.MemoryFootprintProfiler;

import java.util.Iterator;
import java.util.Random;
import java.util.concurrent.TimeUnit;

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
public class JmhSetBenchmarks {

  @Param({"SET"})
  public BenchmarkUtils.DataType dataType;

  @Param({"MATCH"})
  public BenchmarkUtils.SampleDataSelection sampleDataSelection;

  @Param
  public BenchmarkUtils.ValueFactoryFactory valueFactoryFactory;

  /*
   * (for (i <- 0 to 23) yield s"'${Math.pow(2, i).toInt}'").mkString(", ").replace("'", "\"")
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

  public JmhSet testSet;
  private JmhSet testSetRealDuplicate;
  private JmhSet testSetDeltaDuplicate;

  private JmhSet testSetRealDuplicateSameSizeButDifferent;

  private JmhSet testSetDifferent;

  public JmhValue VALUE_EXISTING;
  public JmhValue VALUE_NOT_EXISTING;

  public static final int CACHED_NUMBERS_SIZE = 8;
  public JmhValue[] cachedNumbers = new JmhValue[CACHED_NUMBERS_SIZE];
  public JmhValue[] cachedNumbersNotContained = new JmhValue[CACHED_NUMBERS_SIZE];

  @Setup(Level.Trial)
  public void setUp() throws Exception {
    setUpTestSetWithRandomContent(size, run);
    setUpTestSetWithRandomContentDifferent(size, run); // TODO unify

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
          } else {
            cachedNumbers[i] = producer.createFromInt(rand.nextInt());
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
            final JmhValue candidate = producer.createFromInt(anotherRand.nextInt());

            if (testSet.contains(candidate)) {
              continue;
            } else {
              cachedNumbersNotContained[i] = candidate;
              found = true;
            }
          }
        }

        // assert (contained)
        for (JmhValue sample : cachedNumbers) {
          if (!testSet.contains(sample)) {
            throw new IllegalStateException();
          }
        }

        // assert (not contained)
        for (JmhValue sample : cachedNumbersNotContained) {
          if (testSet.contains(sample)) {
            throw new IllegalStateException();
          }
        }
      }
    }

    // System.out.println(String.format("\n\ncachedNumbers = %s",
    // Arrays.toString(cachedNumbers)));
    // System.out.println(String.format("cachedNumbersNotContained = %s\n\n",
    // Arrays.toString(cachedNumbersNotContained)));

    // OverseerUtils.setup(JmhSetBenchmarks.class, this);
  }

  protected void setUpTestSetWithRandomContent(int size, int run) throws Exception {
    valueFactory = valueFactoryFactory.getInstance();

    JmhSet.Builder writer1 = valueFactory.setBuilder();
    JmhSet.Builder writer2 = valueFactory.setBuilder();

    int seedForThisTrial = BenchmarkUtils.seedFromSizeAndRun(size, run);
    Random rand = new Random(seedForThisTrial + 13);
    int existingValueIndex = rand.nextInt(size);

    int[] data = BenchmarkUtils.generateTestData(size, run);

    for (int i = size - 1; i >= 0; i--) {
      // final IValue current = producer.createFromInt(data[i]);

      writer1.insert(producer.createFromInt(data[i]));
      writer2.insert(producer.createFromInt(data[i]));

      if (i == existingValueIndex) {
        VALUE_EXISTING = producer.createFromInt(data[i]);
      }
    }

    testSet = writer1.done();
    testSetRealDuplicate = writer2.done();

    /*
     * generate random values until a value not part of the data strucure is found
     */
    while (VALUE_NOT_EXISTING == null) {
      final JmhValue candidate = producer.createFromInt(rand.nextInt());

      if (!testSet.contains(candidate)) {
        VALUE_NOT_EXISTING = candidate;
      }
    }

    testSetDeltaDuplicate = testSet.insert(VALUE_NOT_EXISTING).delete(VALUE_NOT_EXISTING);

    testSetRealDuplicateSameSizeButDifferent =
        testSetRealDuplicate.insert(VALUE_NOT_EXISTING).delete(VALUE_EXISTING);
  }

  protected void setUpTestSetWithRandomContentDifferent(int size, int run) throws Exception {
    JmhSet.Builder writer1 = valueFactory.setBuilder();

    final Random rand = new Random(BenchmarkUtils.seedFromSizeAndRun(size, run) + 43);

    int[] data = BenchmarkUtils.generateTestData(size, rand);

    for (int i = size - 1; i >= 0; i--) {
      writer1.insert(producer.createFromInt(data[i]));
    }

    testSetDifferent = writer1.done();
  }

  public static final JmhSet createTestObject(final JmhValueFactory valueFactory,
      final ElementProducer producer, final int size, final int run) {

    final JmhSet.Builder builder = valueFactory.setBuilder();
    final Random rand = new Random(BenchmarkUtils.seedFromSizeAndRun(size, run) + 43);

    final int[] data = BenchmarkUtils.generateTestData(size, rand);

    for (int i = size - 1; i >= 0; i--) {
      builder.insert(producer.createFromInt(data[i]));
    }

    return builder.done();
  }

  // @TearDown(Level.Trial)
  // public void tearDown() {
  // OverseerUtils.tearDown();
  // }
  //
  //// @Setup(Level.Iteration)
  //// public void setupIteration() {
  //// OverseerUtils.doRecord(true);
  //// }
  ////
  //// @TearDown(Level.Iteration)
  //// public void tearDownIteration() {
  //// OverseerUtils.doRecord(false);
  //// }
  //
  // @Setup(Level.Invocation)
  // public void setupInvocation() {
  // OverseerUtils.setup(JmhSetBenchmarks.class, this);
  // OverseerUtils.doRecord(true);
  // }
  //
  // @TearDown(Level.Invocation)
  // public void tearDownInvocation() {
  // OverseerUtils.doRecord(false);
  // }

  @Benchmark
  @OperationsPerInvocation(2 * CACHED_NUMBERS_SIZE)
  public void timeLookup(Blackhole bh) {
    // full match
    for (int i = 0; i < CACHED_NUMBERS_SIZE; i++) {
      bh.consume(testSet.contains(cachedNumbers[i]));
    }
    // no match
    for (int i = 0; i < CACHED_NUMBERS_SIZE; i++) {
      bh.consume(testSet.contains(cachedNumbersNotContained[i]));
    }
  }

  @Benchmark
  @OperationsPerInvocation(CACHED_NUMBERS_SIZE)
  public void timeLookupKeyContained(Blackhole bh) {
    for (int i = 0; i < CACHED_NUMBERS_SIZE; i++) {
      bh.consume(testSet.contains(cachedNumbers[i]));
    }
  }

  @Benchmark
  @OperationsPerInvocation(CACHED_NUMBERS_SIZE)
  public void timeLookupKeyNotContained(Blackhole bh) {
    for (int i = 0; i < CACHED_NUMBERS_SIZE; i++) {
      bh.consume(testSet.contains(cachedNumbersNotContained[i]));
    }
  }

  @Benchmark
  public void timeIteration(Blackhole bh) {
    for (Iterator<JmhValue> iterator = testSet.iterator(); iterator.hasNext(); ) {
      bh.consume(iterator.next());
    }
  }

  @Benchmark
  @OperationsPerInvocation(2 * CACHED_NUMBERS_SIZE)
  public void timeInsert(Blackhole bh) {
    // full match
    for (int i = 0; i < CACHED_NUMBERS_SIZE; i++) {
      bh.consume(testSet.insert(cachedNumbers[i]));
    }
    // no match
    for (int i = 0; i < CACHED_NUMBERS_SIZE; i++) {
      bh.consume(testSet.insert(cachedNumbersNotContained[i]));
    }
  }

  @Benchmark
  @OperationsPerInvocation(CACHED_NUMBERS_SIZE)
  public void timeInsertNotContained(Blackhole bh) {
    for (int i = 0; i < CACHED_NUMBERS_SIZE; i++) {
      bh.consume(testSet.insert(cachedNumbersNotContained[i]));

    }
  }

  @Benchmark
  @OperationsPerInvocation(CACHED_NUMBERS_SIZE)
  public void timeInsertContained(Blackhole bh) {
    for (int i = 0; i < CACHED_NUMBERS_SIZE; i++) {
      bh.consume(testSet.insert(cachedNumbers[i]));

    }
  }

  @Benchmark
  @OperationsPerInvocation(2 * CACHED_NUMBERS_SIZE)
  public void timeRemove(Blackhole bh) {
    // full match
    for (int i = 0; i < CACHED_NUMBERS_SIZE; i++) {
      bh.consume(testSet.delete(cachedNumbers[i]));
    }
    // no match
    for (int i = 0; i < CACHED_NUMBERS_SIZE; i++) {
      bh.consume(testSet.delete(cachedNumbersNotContained[i]));
    }
  }

  @Benchmark
  @OperationsPerInvocation(CACHED_NUMBERS_SIZE)
  public void timeRemoveKeyContained(Blackhole bh) {
    for (int i = 0; i < CACHED_NUMBERS_SIZE; i++) {
      bh.consume(testSet.delete(cachedNumbers[i]));
    }
  }

  @Benchmark
  @OperationsPerInvocation(CACHED_NUMBERS_SIZE)
  public void timeRemoveKeyNotContained(Blackhole bh) {
    for (int i = 0; i < CACHED_NUMBERS_SIZE; i++) {
      bh.consume(testSet.delete(cachedNumbersNotContained[i]));
    }
  }

  @Benchmark
  public void timeEqualsWorstCase(Blackhole bh) {
    bh.consume(testSet.equals(testSetRealDuplicate));
  }

  @Benchmark
  public void timeEqualsGoodCase(Blackhole bh) {
    bh.consume(testSet.equals(testSetRealDuplicateSameSizeButDifferent));
  }

  @Benchmark
  public void timeSubsetOf(Blackhole bh) {
    bh.consume(testSet.subsetOf(testSetRealDuplicate));
  }

  @Benchmark
  public void timeUnionRealDuplicate(Blackhole bh) {
    bh.consume(testSet.union(testSetDeltaDuplicate));
  }

  @Benchmark
  public void timeUnionDeltaDuplicate(Blackhole bh) {
    bh.consume(testSet.union(testSetDeltaDuplicate));
  }

  @Benchmark
  public void timeUnionDifferent(Blackhole bh) {
    bh.consume(testSet.union(testSetDifferent));
  }

  @Benchmark
  public void timeSubtractRealDuplicate(Blackhole bh) {
    bh.consume(testSet.subtract(testSetDeltaDuplicate));
  }

  @Benchmark
  public void timeSubtractDeltaDuplicate(Blackhole bh) {
    bh.consume(testSet.subtract(testSetDeltaDuplicate));
  }

  @Benchmark
  public void timeSubtractDifferent(Blackhole bh) {
    bh.consume(testSet.subtract(testSetDifferent));
  }

  @Benchmark
  public void timeIntersectRealDuplicate(Blackhole bh) {
    bh.consume(testSet.intersect(testSetDeltaDuplicate));
  }

  @Benchmark
  public void timeIntersectDeltaDuplicate(Blackhole bh) {
    bh.consume(testSet.intersect(testSetDeltaDuplicate));
  }

  @Benchmark
  public void timeIntersectDifferent(Blackhole bh) {
    bh.consume(testSet.intersect(testSetDifferent));
  }

  @Benchmark
  public void timeSize(Blackhole bh) {
    bh.consume(testSet.size());
  }

  @Benchmark
  public void timeHashCode(Blackhole bh) {
    bh.consume(testSet.hashCode());
  }

  @Benchmark
  public void footprint(Blackhole bh) {
    bh.consume(testSet); // no-op returning identity
  }

  public static void main(String[] args) throws RunnerException {
    System.out.println(JmhSetBenchmarks.class.getSimpleName());

    Options opt = new OptionsBuilder()
        .include(".*" + JmhSetBenchmarks.class.getSimpleName()
            + ".footprint(.*)") // Union|Subtract|Intersect
        .timeUnit(TimeUnit.NANOSECONDS)
        .mode(Mode.SingleShotTime)
        .warmupIterations(0)
        .warmupTime(TimeValue.seconds(1))
        .measurementIterations(1)
        .forks(0)
        .param("dataType", "SET")
        .param("run", "0")
//        .param("run", "1")
//        .param("run", "2")
//        .param("run", "3")
//        .param("run", "4")
        .param("producer", "PURE_INTEGER") // PURE_INTEGER, SLEEPING_INTEGER
        .addProfiler(MemoryFootprintProfiler.class)
        .param("sampleDataSelection", "MATCH")
//        .param("size", "16")
//        .param("size", "2048")
        .param("size", "1048576")
        .param("valueFactoryFactory", "VF_SCALA_STRAWMAN")
        .param("valueFactoryFactory", "VF_SCALA")
//        .param("valueFactoryFactory", "VF_AXIOM")
        .param("valueFactoryFactory", "VF_CHAMP")
////        .param("valueFactoryFactory", "VF_CHAMP_EXTENDED")
        .build();
    // @formatter:off
    // @formatter:on

    new Runner(opt).run();
  }

}
