/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.capsule.jmh;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import io.usethesource.capsule.jmh.api.JmhValue;
import io.usethesource.capsule.jmh.BenchmarkUtils.Archetype;
import io.usethesource.capsule.jmh.BenchmarkUtils.DataType;
import objectexplorer.ObjectGraphMeasurer.Footprint;

public final class FootprintUtils {

  final static String CSV_HEADER =
      "elementCount,run,className,dataType,archetype,supportsStagedMutability,footprintInBytes,footprintInObjects,footprintInReferences"; // ,footprintInPrimitives

  /*
   * NOTE: with extensions for heterogeneous data
   */
  public enum MemoryFootprintPreset {
    RETAINED_SIZE, DATA_STRUCTURE_OVERHEAD, RETAINED_SIZE_WITH_BOXED_INTEGER_FILTER
  }

  public static String measureAndReport(final Object objectToMeasure, final String className,
      DataType dataType, Archetype archetype, boolean supportsStagedMutability, int size, int run,
      MemoryFootprintPreset preset) {
    final Predicate<Object> predicate;

    switch (preset) {
      case DATA_STRUCTURE_OVERHEAD:
        predicate = Predicates.not(Predicates.instanceOf(JmhValue.class));
        break;
      case RETAINED_SIZE:
        predicate = Predicates.alwaysTrue();
        break;
      default:
        throw new IllegalStateException();
    }

    // System.out.println(GraphLayout.parseInstance(objectToMeasure).totalSize());

    long memoryInBytes = objectexplorer.MemoryMeasurer.measureBytes(objectToMeasure, predicate);

    Footprint memoryFootprint =
        objectexplorer.ObjectGraphMeasurer.measure(objectToMeasure, predicate);

    final String statString =
        String.format("%d\t %60s\t[%s]\t %s", memoryInBytes, className, dataType, memoryFootprint);
    System.out.println(statString);

    final String statFileString = String.format("%d,%d,%s,%s,%s,%b,%d,%d,%d", size, run, className,
        dataType, archetype, supportsStagedMutability, memoryInBytes, memoryFootprint.getObjects(),
        memoryFootprint.getReferences());

    return statFileString;
  }

  static List<Integer> rangeInclusive(int first, int last) {
    return createLinearRange(first, last + 1, 1);
  }

  static List<Integer> rangeExclusive(int first, int afterLast) {
    return createLinearRange(first, afterLast, 1);
  }

  static List<Integer> rangeInclusive(int first, int last, int stride) {
    return createLinearRange(first, last + stride, stride);
  }

  static List<Integer> createLinearRange(int start, int end, int stride) {
    int count = (end - start) / stride;
    ArrayList<Integer> samples = new ArrayList<>(count);

    for (int i = 0; i < count; i++) {
      samples.add(start);
      start += stride;
    }

    return samples;
  }

  static List<Integer> createExponentialRange(int start, int end) {
    ArrayList<Integer> samples = new ArrayList<>(end - start);

    for (int exp = start; exp < end; exp++) {
      samples.add((int) Math.pow(2, exp));
    }

    return samples;
  }

  static void writeToFile(Path file, boolean isAppendingToFile, List<String> lines) {
    // write stats to file
    try {
      if (isAppendingToFile) {
        Files.write(file, lines, StandardCharsets.UTF_8, StandardOpenOption.APPEND);
      } else {
        Files.write(file, Arrays.asList(CSV_HEADER), StandardCharsets.UTF_8);
        Files.write(file, lines, StandardCharsets.UTF_8, StandardOpenOption.APPEND);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

}
