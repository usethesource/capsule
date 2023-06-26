/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.criterion.profiler;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import io.usethesource.criterion.BenchmarkUtils.ValueFactoryFactory;
import io.usethesource.criterion.ElementProducer;
import io.usethesource.criterion.api.JmhValue;
import io.usethesource.criterion.api.JmhValueFactory;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import objectexplorer.ObjectGraphMeasurer.Footprint;
import org.openjdk.jmh.infra.BenchmarkParams;
import org.openjdk.jmh.infra.IterationParams;
import org.openjdk.jmh.profile.InternalProfiler;
import org.openjdk.jmh.profile.ProfilerResult;
import org.openjdk.jmh.results.AggregationPolicy;
import org.openjdk.jmh.results.IterationResult;
import org.openjdk.jmh.results.Result;
import org.openjdk.jmh.results.ResultRole;

public class MemoryFootprintProfiler implements InternalProfiler {

  private final static String UNIT_BYTES = "bytes";
  private final static String UNIT_COUNT = "count";
  private final static AggregationPolicy POLICY = AggregationPolicy.MAX;

  @Override
  public String getDescription() {
    return "Approximates the memory usage based object-graph walking and object layout knowledge.";
  }

  @Override
  public void beforeIteration(BenchmarkParams benchmarkParams, IterationParams iterationParams) {
  }

  /**
   * Run this code after a benchmark iteration finished
   *
   * @param benchmarkParams benchmark parameters used for current launch
   * @param iterationParams iteration parameters used for current launch
   * @param result iteration result
   * @return profiler results
   */
  @Override
  public Collection<? extends Result<ProfilerResult>> afterIteration(
      BenchmarkParams benchmarkParams, IterationParams iterationParams, IterationResult result) {

    try {
      final String benchmarkClassName = benchmarkToClassName(benchmarkParams.getBenchmark());
      final Method factoryMethod = factoryMethod(benchmarkClassName);

      final JmhValue wrappedObject =
          (JmhValue) factoryMethod.invoke(null, initializeArguments(benchmarkParams));
      final Object objectToMeasure = wrappedObject.unwrap();

      final Predicate<Object> predicateDataStructureOverhead =
          Predicates.not(Predicates.instanceOf(JmhValue.class));
      final Predicate<Object> predicateRetainedSize = Predicates.alwaysTrue();

      final List<ProfilerResult> profilerResults = new ArrayList<>();

      /**
       * Traverse object graph for measuring memory footprint (in bytes).
       */
      long memoryInBytes = objectexplorer.MemoryMeasurer
          .measureBytes(objectToMeasure, predicateDataStructureOverhead);

      // NOTE: non-standard constructor for passing ResultRole.PRIMARY
      final ProfilerResult memoryResult =
          new ProfilerResult(ResultRole.PRIMARY, "memory", memoryInBytes, UNIT_BYTES, POLICY);

      // hack: substitute results
      result.resetResults();
      result.addResult(memoryResult);

//      /**
//       * Traverse object graph for measuring field statistics.
//       */
//      final Footprint statistic = objectexplorer.ObjectGraphMeasurer
//          .measure(objectToMeasure, predicateDataStructureOverhead);
//
//      final ProfilerResult objectsResult =
//          new ProfilerResult("objects", statistic.getObjects(), UNIT_COUNT, POLICY);
//      profilerResults.add(objectsResult);
//
//      final ProfilerResult referencesResult =
//          new ProfilerResult("references", statistic.getReferences(), UNIT_COUNT, POLICY);
//      profilerResults.add(referencesResult);
//
//      final ProfilerResult primitivesResult =
//          new ProfilerResult("primitives", statistic.getPrimitives().size(), UNIT_COUNT, POLICY);
//      profilerResults.add(primitivesResult);

      return profilerResults;
    } catch (Exception e) {
      e.printStackTrace();
      return Arrays.asList();
    }
  }

  private static final String benchmarkToClassName(String benchmarkName) {
    int lastDelimiterIndex = benchmarkName.lastIndexOf(".");
    return benchmarkName.substring(0, lastDelimiterIndex);
  }

  private static final Method factoryMethod(String className)
      throws ClassNotFoundException, NoSuchMethodException {

    final Method method = Class.forName(className).getMethod("createTestObject",
        JmhValueFactory.class,
        ElementProducer.class,
        int.class,
        int.class);

    return method;
  }

  private static final Object[] initializeArguments(BenchmarkParams benchmarkParams) {
    final Object[] args = new Object[4];

    String valueFactoryString = benchmarkParams.getParam("valueFactoryFactory");
    String elementProducerString = benchmarkParams.getParam("producer");
    String sizeString = benchmarkParams.getParam("size");
    String runString = benchmarkParams.getParam("run");

    args[0] = ValueFactoryFactory.valueOf(valueFactoryString).getInstance();
    args[1] = ElementProducer.valueOf(elementProducerString);
    args[2] = Integer.parseInt(sizeString);
    args[3] = Integer.parseInt(runString);

    return args;
  }

}
