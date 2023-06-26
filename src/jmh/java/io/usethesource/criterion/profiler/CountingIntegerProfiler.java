/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.criterion.profiler;

import java.util.Arrays;
import java.util.Collection;

import org.openjdk.jmh.infra.BenchmarkParams;
import org.openjdk.jmh.infra.IterationParams;
import org.openjdk.jmh.profile.InternalProfiler;
import org.openjdk.jmh.profile.ProfilerResult;
import org.openjdk.jmh.results.AggregationPolicy;
import org.openjdk.jmh.results.IterationResult;
import org.openjdk.jmh.results.Result;

import io.usethesource.criterion.CountingInteger;

public class CountingIntegerProfiler implements InternalProfiler {

  @Override
  public String getDescription() {
    return "Counts the number of hashCode() and equals() invocations on class 'CountingInteger'";
  }

  @Override
  public void beforeIteration(BenchmarkParams benchmarkParams, IterationParams iterationParams) {
    CountingInteger.resetCounters();
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
    String unit = "invocations";
    AggregationPolicy policy = AggregationPolicy.AVG;

    final ProfilerResult hashCodeResult =
        new ProfilerResult("hashCode", CountingInteger.getHashcodeCounter(), unit, policy);

    final ProfilerResult equalsResult =
        new ProfilerResult("equals", CountingInteger.getEqualsCounter(), unit, policy);

    return Arrays.asList(hashCodeResult, equalsResult);
  }

}
