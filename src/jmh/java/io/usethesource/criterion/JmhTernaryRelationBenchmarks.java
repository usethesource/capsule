/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.criterion;

import static io.usethesource.criterion.BenchmarkUtils.seedFromSizeAndRun;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import com.pholser.junit.quickcheck.generator.GenerationStatus;
import com.pholser.junit.quickcheck.generator.Generator;
import com.pholser.junit.quickcheck.internal.GeometricDistribution;
import com.pholser.junit.quickcheck.internal.generator.SimpleGenerationStatus;
import com.pholser.junit.quickcheck.random.SourceOfRandomness;
import io.usethesource.capsule.Set;
import io.usethesource.capsule.api.TernaryRelation;
import io.usethesource.capsule.api.Triple;
import io.usethesource.capsule.generators.SingletonToTripleGenerator;
import io.usethesource.capsule.generators.relation.AbstractTernaryRelationGenerator;
import io.usethesource.capsule.generators.relation.TernaryTrieSetMultimapGenerator;
import io.usethesource.capsule.util.stream.CapsuleCollectors;
import io.usethesource.criterion.api.JmhValue;
import io.usethesource.criterion.generators.JmhIntegerGenerator;
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
public class JmhTernaryRelationBenchmarks {

  public static final Class<? extends AbstractTernaryRelationGenerator<? extends TernaryRelation.Immutable>> generatorClass = TernaryTrieSetMultimapGenerator.class;

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

  private TernaryRelation.Immutable<JmhValue, JmhValue, JmhValue, Triple<JmhValue, JmhValue, JmhValue>> testMap;

  public JmhValue VALUE_EXISTING;
  public JmhValue VALUE_NOT_EXISTING;

  public static final int CACHED_NUMBERS_SIZE = 8;
  public JmhValue[] cachedNumbers = new JmhValue[CACHED_NUMBERS_SIZE];
  public JmhValue[] cachedNumbersNotContained = new JmhValue[CACHED_NUMBERS_SIZE];

  public static final Class<JmhValue> payloadToken = JmhValue.class;

  @Setup(Level.Trial)

  public void setUp() throws Exception {
    final Generator<JmhValue> itemGenerator;
    final Generator<Triple> tripleGenerator;
    final AbstractTernaryRelationGenerator<? extends TernaryRelation.Immutable> collectionGenerator;

    final SourceOfRandomness random = freshRandom.get();
    final GenerationStatus status = freshGenerationStatus.apply(random);

    try {
      itemGenerator = new JmhIntegerGenerator();

      tripleGenerator = new SingletonToTripleGenerator<>();
      tripleGenerator.addComponentGenerators(Arrays.asList(itemGenerator));

      collectionGenerator = generatorClass.newInstance();
      collectionGenerator.configure(CalculateFootprintsHeterogeneous.size(size, size));
      collectionGenerator.addComponentGenerators(Arrays.asList(null, null, null, tripleGenerator));

      testMap = (TernaryRelation.Immutable) collectionGenerator.generate(random, status);
    } catch (InstantiationException | IllegalAccessException e) {
      throw new RuntimeException(e);
    }

    final Set<JmhValue> elements = testMap.stream().map(Triple::_0)
        .collect(CapsuleCollectors.toSet());

    /*
     * select random integers that are contained in the data set
     */
    for (int i = 0; i < CACHED_NUMBERS_SIZE; i++) {
      if (i >= size) {
        cachedNumbers[i] = cachedNumbers[i % size];
      } else {
        cachedNumbers[i] = random.choose(elements);
      }
    }

    // assert (contained)
    assert Stream.of(cachedNumbers)
        .allMatch(sample -> testMap.contains(Triple.of(sample, sample, sample)));

    /*
     * generate random integers that are not yet contained in the data set
     */
    for (int i = 0; i < CACHED_NUMBERS_SIZE; i++) {
      boolean found = false;
      while (!found) {
        final JmhValue candidate = itemGenerator.generate(random, status);

        if (!elements.contains(candidate)) {
          cachedNumbersNotContained[i] = candidate;
          found = true;
        }
      }
    }

    // assert (contained)
    assert Stream.of(cachedNumbersNotContained)
        .noneMatch(sample -> testMap.contains(Triple.of(sample, sample, sample)));

    VALUE_EXISTING = cachedNumbers[0];
    VALUE_NOT_EXISTING = cachedNumbersNotContained[0];
  }

  private final Supplier<SourceOfRandomness> freshRandom = () -> new SourceOfRandomness(
      new Random(seedFromSizeAndRun(size, run)));

  private final Function<SourceOfRandomness, GenerationStatus> freshGenerationStatus = (random) -> new SimpleGenerationStatus(
      new GeometricDistribution(), random, 1);

  @Benchmark
  @OperationsPerInvocation(2 * CACHED_NUMBERS_SIZE)
  public void timeContainsTriple(Blackhole bh) {
    // partial match
    for (int i = 0; i < CACHED_NUMBERS_SIZE; i++) {
      bh.consume(testMap
          .contains(Triple.of(cachedNumbers[i], cachedNumbers[i], cachedNumbersNotContained[i])));
    }
    // full match
    for (int i = 0; i < CACHED_NUMBERS_SIZE; i++) {
      bh.consume(testMap.contains(Triple.of(cachedNumbers[i], cachedNumbers[i], cachedNumbers[i])));
    }
  }

  @Benchmark
  @OperationsPerInvocation(CACHED_NUMBERS_SIZE)
  public void timeContainsTripleNotContained(Blackhole bh) {
    for (int i = 0; i < CACHED_NUMBERS_SIZE; i++) {
      bh.consume(testMap.contains(Triple
          .of(cachedNumbersNotContained[i], cachedNumbersNotContained[i],
              cachedNumbersNotContained[i])));
    }
  }

  @Benchmark
  @OperationsPerInvocation(3 * CACHED_NUMBERS_SIZE)
  public void timeInsertTriple(Blackhole bh) {
    // full match
    for (int i = 0; i < CACHED_NUMBERS_SIZE; i++) {
      bh.consume(testMap.__insert(Triple.of(cachedNumbers[i], cachedNumbers[i], cachedNumbers[i])));
    }
    // partial match
    for (int i = 0; i < CACHED_NUMBERS_SIZE; i++) {
      bh.consume(testMap
          .__insert(Triple.of(cachedNumbers[i], cachedNumbers[i], cachedNumbersNotContained[i])));
    }
    // no match
    for (int i = 0; i < CACHED_NUMBERS_SIZE; i++) {
      bh.consume(testMap
          .__insert(Triple.of(cachedNumbersNotContained[i], cachedNumbers[i], cachedNumbers[i])));
    }
  }

  public static void main(String[] args) throws RunnerException {
    System.out.println(JmhTernaryRelationBenchmarks.class.getSimpleName());

    // @formatter:off
    Options opt = new OptionsBuilder()
        .include(".*" + JmhTernaryRelationBenchmarks.class.getSimpleName()
            + ".*")
        .timeUnit(TimeUnit.NANOSECONDS)
        .mode(Mode.AverageTime)
        .warmupIterations(10)
        .warmupTime(TimeValue.seconds(1))
        .measurementIterations(10)
        .forks(1)
        .shouldDoGC(true)
        .param("run", "0")
//        .param("run", "1")
//        .param("run", "2")
//        .param("run", "3")
//        .param("run", "4")
//        .param("size", "16")
        .param("size", "2048")
//        .param("size", "1048576")
//        .param("size", "8388608")
        .build();
    // @formatter:on

    new Runner(opt).run();
  }

}
