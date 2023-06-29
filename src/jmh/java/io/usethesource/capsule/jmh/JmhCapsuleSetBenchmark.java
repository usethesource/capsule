/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.capsule.jmh;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Supplier;

import com.pholser.junit.quickcheck.generator.GenerationStatus;
import com.pholser.junit.quickcheck.generator.Generator;
import com.pholser.junit.quickcheck.internal.GeometricDistribution;
import com.pholser.junit.quickcheck.internal.generator.SimpleGenerationStatus;
import com.pholser.junit.quickcheck.random.SourceOfRandomness;
import io.usethesource.capsule.Set;
import io.usethesource.capsule.generators.set.AbstractSetGenerator;
import io.usethesource.capsule.generators.set.SetGeneratorDefault;
import io.usethesource.capsule.jmh.api.JmhValue;
import io.usethesource.capsule.jmh.generators.JmhSleepingIntegerGenerator;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Mode;
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

import static io.usethesource.capsule.jmh.BenchmarkUtils.seedFromSizeAndRun;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Thread)
public class JmhCapsuleSetBenchmark {

  public enum SetGeneratorClassEnum {
    CHAMP {
      @Override
      public Class<? extends AbstractSetGenerator> generatorClass() {
        return SetGeneratorDefault.class;
      }
    };

    public abstract Class<? extends AbstractSetGenerator> generatorClass();

  }

  @Param({"CHAMP"})
  protected SetGeneratorClassEnum generatorDescriptor;

//  static final Class<? extends Set.Immutable> classUnderTest =
//      TrieSet_5Bits_Memoized_LazyHashCode.class;
//
//  @Param({SetGeneratorMemoizedLazyHashCode.class})
//
//  static final Class<?> generatorClass = SetGeneratorMemoizedLazyHashCode.class;

  // RuntimeCodeGenerationTestSuite.generatorClass(classUnderTest, JmhValue.class);

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

  @Param({"0"}) // "1", "2", "3", "4", "5", "6", "7", "8", "9"
  protected int run;

  private Set.Immutable<JmhValue> testSet1;
  private Set.Immutable<JmhValue> testSet2;
  private Set.Immutable<JmhValue> testSetCommon;

  private Set.Immutable<JmhValue> testSet1Duplicate;
  private Set.Immutable<JmhValue> testSet2Duplicate;

  public JmhValue VALUE_EXISTING;
  public JmhValue VALUE_NOT_EXISTING;

  public static final int CACHED_NUMBERS_SIZE = 8;
  public JmhValue[] cachedNumbers = new JmhValue[CACHED_NUMBERS_SIZE];
  public JmhValue[] cachedNumbersNotContained = new JmhValue[CACHED_NUMBERS_SIZE];

  public static final Class<JmhValue> payloadToken = JmhValue.class;

  @Setup(Level.Trial)

  public void setUp() throws Exception {
    final Generator<JmhValue> itemGenerator;
    final AbstractSetGenerator<? extends Set.Immutable> collectionGenerator;

    final SourceOfRandomness random0 = new SourceOfRandomness(new Random(13));
    final GenerationStatus status0 = freshGenerationStatus.apply(random0);

    try {
      itemGenerator = new JmhSleepingIntegerGenerator();

      collectionGenerator = generatorDescriptor.generatorClass().newInstance();
      collectionGenerator.configure(CalculateFootprintsHeterogeneous.size(size, size));
      collectionGenerator.addComponentGenerators(List.of(itemGenerator));

      testSetCommon = collectionGenerator.generate(random0, status0);

      SourceOfRandomness random = freshRandom.get();
      GenerationStatus status = freshGenerationStatus.apply(random);

      testSet1 = collectionGenerator.generate(random, status).__insertAll(testSetCommon);
      testSet2 = collectionGenerator.generate(random, status).__insertAll(testSetCommon);

      random = freshRandom.get();
      status = freshGenerationStatus.apply(random);

      testSet1Duplicate = collectionGenerator.generate(random, status).__insertAll(testSetCommon);
      testSet2Duplicate = collectionGenerator.generate(random, status).__insertAll(testSetCommon);

//      assert testSet1.size() == size;
//      assert testSet2.size() == size;
    } catch (InstantiationException | IllegalAccessException e) {
      throw new RuntimeException(e);
    }
//
//    final Set<JmhValue> elements = testSet1.stream().map(Triple::_0)
//        .collect(CapsuleCollectors.toSet());
//
//    /*
//     * select random integers that are contained in the data set
//     */
//    for (int i = 0; i < CACHED_NUMBERS_SIZE; i++) {
//      if (i >= size) {
//        cachedNumbers[i] = cachedNumbers[i % size];
//      } else {
//        cachedNumbers[i] = random.choose(elements);
//      }
//    }
//
//    // assert (contained)
//    assert Stream.of(cachedNumbers)
//        .allMatch(sample -> testSet1.contains(Triple.of(sample, sample, sample)));
//
//    /*
//     * generate random integers that are not yet contained in the data set
//     */
//    for (int i = 0; i < CACHED_NUMBERS_SIZE; i++) {
//      boolean found = false;
//      while (!found) {
//        final JmhValue candidate = itemGenerator.generate(random, status);
//
//        if (!elements.contains(candidate)) {
//          cachedNumbersNotContained[i] = candidate;
//          found = true;
//        }
//      }
//    }
//
//    // assert (contained)
//    assert Stream.of(cachedNumbersNotContained)
//        .noneMatch(sample -> testSet1.contains(Triple.of(sample, sample, sample)));
//
//    VALUE_EXISTING = cachedNumbers[0];
//    VALUE_NOT_EXISTING = cachedNumbersNotContained[0];

//    VALUE_EXISTING = cachedNumbers[0];
    VALUE_NOT_EXISTING = itemGenerator.generate(random0, status0);
  }

  private final Supplier<SourceOfRandomness> freshRandom = () -> new SourceOfRandomness(
      new Random(seedFromSizeAndRun(size, run)));

  private final Function<SourceOfRandomness, GenerationStatus> freshGenerationStatus = (random) -> new SimpleGenerationStatus(
      new GeometricDistribution(), random, 1);

  private <R extends Set.Immutable> R generate(final AbstractSetGenerator<R> collectionGenerator) {
    final SourceOfRandomness random = freshRandom.get();
    final GenerationStatus status = freshGenerationStatus.apply(random);

    return collectionGenerator.generate(random, status);
  }

  @Benchmark
  public void timeUnionStructural(Blackhole bh) {
    bh.consume(testSet1.union(testSet2));
  }

  @Benchmark
  public void timeUnionStructuralOneBigger(Blackhole bh) {
    bh.consume(testSet1.union(testSet1.__insert(VALUE_NOT_EXISTING)));
  }

  @Benchmark
  public void timeUnionStructuralOfIdentical(Blackhole bh) {
    bh.consume(testSet1.union(testSet1));
  }

  @Benchmark
  public void timeUnionStructuralOfDuplicate(Blackhole bh) {
    bh.consume(testSet1.union(testSet1Duplicate));
  }

  @Benchmark
  public void timeUnionFunction(Blackhole bh) {
    bh.consume(Set.Immutable.union(testSet1, testSet2));
  }

  @Benchmark
  public void timeUnionFunctionOneBigger(Blackhole bh) {
    bh.consume(Set.Immutable.union(testSet1, testSet1.__insert(VALUE_NOT_EXISTING)));
  }

  @Benchmark
  public void timeUnionFunctionOfIdentical(Blackhole bh) {
    bh.consume(Set.Immutable.union(testSet1, testSet1));
  }

  @Benchmark
  public void timeUnionFunctionOfDuplicate(Blackhole bh) {
    bh.consume(Set.Immutable.union(testSet1, testSet1Duplicate));
  }

  @Benchmark
  public void timeIntersectStructural(Blackhole bh) {
    bh.consume(testSet1.intersect(testSet2));
  }

  @Benchmark
  public void timeIntersectStructuralOneBigger(Blackhole bh) {
    bh.consume(Set.Immutable.intersect(testSet1, testSet1.__insert(VALUE_NOT_EXISTING)));
  }


  @Benchmark
  public void timeIntersectStructuralOfIdentical(Blackhole bh) {
    bh.consume(testSet1.intersect(testSet1));
  }

  @Benchmark
  public void timeIntersectStructuralOfDuplicate(Blackhole bh) {
    bh.consume(testSet1.intersect(testSet1Duplicate));
  }

  @Benchmark
  public void timeIntersectFunction(Blackhole bh) {
    bh.consume(Set.Immutable.intersect(testSet1, testSet2));
  }

  @Benchmark
  public void timeIntersectFunctionOneBigger(Blackhole bh) {
    bh.consume(Set.Immutable.intersect(testSet1, testSet1.__insert(VALUE_NOT_EXISTING)));
  }


  @Benchmark
  public void timeIntersectFunctionOfIdentical(Blackhole bh) {
    bh.consume(Set.Immutable.intersect(testSet1, testSet1));
  }

  @Benchmark
  public void timeIntersectFunctionOfDuplicate(Blackhole bh) {
    bh.consume(Set.Immutable.intersect(testSet1, testSet1Duplicate));
  }

  @Benchmark
  public void timeSubtractStructural(Blackhole bh) {
    bh.consume(testSet1.subtract(testSet2));
  }

  @Benchmark
  public void timeSubtractStructuralOneBigger(Blackhole bh) {
    bh.consume(testSet1.__insert(VALUE_NOT_EXISTING).subtract(testSet1));
  }

  @Benchmark
  public void timeSubtractStructuralOfIdentical(Blackhole bh) {
    bh.consume(testSet1.subtract(testSet1));
  }

  @Benchmark
  public void timeSubtractStructuralOfDuplicate(Blackhole bh) {
    bh.consume(testSet1.subtract(testSet1Duplicate));
  }

  @Benchmark
  public void timeSubtractFunction(Blackhole bh) {
    bh.consume(Set.Immutable.subtract(testSet1, testSet2));
  }

  @Benchmark
  public void timeSubtractFunctionOneBigger(Blackhole bh) {
    bh.consume(Set.Immutable.subtract(testSet1.__insert(VALUE_NOT_EXISTING), testSet1));
  }

  @Benchmark
  public void timeSubtractFunctionOfIdentical(Blackhole bh) {
    bh.consume(Set.Immutable.subtract(testSet1, testSet1));
  }

  @Benchmark
  public void timeSubtractFunctionOfDuplicate(Blackhole bh) {
    bh.consume(Set.Immutable.subtract(testSet1, testSet1Duplicate));
  }

  public static void main(String[] args) throws RunnerException {
    System.out.println(JmhCapsuleSetBenchmark.class.getSimpleName());

    // @formatter:off
    Options opt = new OptionsBuilder()
        .include(".*" + JmhCapsuleSetBenchmark.class.getSimpleName() + ".time(Union|Intersect|Subtract).*") // Union|Intersect|Subtract
        .timeUnit(TimeUnit.NANOSECONDS)
        .mode(Mode.SingleShotTime)
        .warmupIterations(10)
        .warmupTime(TimeValue.seconds(1))
        .measurementIterations(10)
        .forks(0)
        .shouldDoGC(true)
        .param("generatorDescriptor", "CHAMP")
        .param("run", "0")
//        .param("run", "1")
//        .param("run", "2")
//        .param("run", "3")
//        .param("run", "4")
//        .param("size", "16")
//        .param("size", "2048")
        .param("size", "1048576")
//        .param("size", "8388608")
        .build();
    // @formatter:on

    new Runner(opt).run();
  }

}
