/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.capsule.jmh;

import java.util.Arrays;
import java.util.Random;

import io.usethesource.capsule.jmh.api.JmhValueFactory;
import io.usethesource.capsule.jmh.impl.immutable.guava.ImmutableGuavaValueFactory;
import io.usethesource.capsule.jmh.impl.persistent.capsule.CapsuleValueFactory;
import io.usethesource.capsule.jmh.impl.persistent.clojure.ClojureValueFactory;
import io.usethesource.capsule.jmh.impl.persistent.dexx.DexxValueFactory;
import io.usethesource.capsule.jmh.impl.persistent.paguro.PaguroValueFactory;
import io.usethesource.capsule.jmh.impl.persistent.pcollections.PcollectionsValueFactory;
import io.usethesource.capsule.jmh.impl.persistent.scala.ScalaValueFactory;
import io.usethesource.capsule.jmh.impl.persistent.vavr.VavrValueFactory;

public class BenchmarkUtils {

  public enum ValueFactoryFactory {
    VF_CLOJURE {
      @Override
      public JmhValueFactory getInstance() {
        return new ClojureValueFactory();
      }
    },
    VF_SCALA {
      @Override
      public JmhValueFactory getInstance() {
        return new ScalaValueFactory();
      }
    },
    VF_CAPSULE {
      @Override
      public JmhValueFactory getInstance() {
        return new CapsuleValueFactory(false);
      }
    },
    VF_BINARY_RELATION {
      @Override
      public JmhValueFactory getInstance() {
        return new CapsuleValueFactory(true);
      }
    },
    VF_VAVR {
      @Override
      public JmhValueFactory getInstance() {
        return new VavrValueFactory();
      }
    },
    VF_PAGURO {
      @Override
      public JmhValueFactory getInstance() {
        return new PaguroValueFactory();
      }
    },
    VF_DEXX {
      @Override
      public JmhValueFactory getInstance() {
        return new DexxValueFactory();
      }
    },
    VF_PCOLLECTIONS {
      @Override
      public JmhValueFactory getInstance() {
        return new PcollectionsValueFactory();
      }
    },
    VF_GUAVA_IMMUTABLE {
      @Override
      public JmhValueFactory getInstance() {
        return new ImmutableGuavaValueFactory();
      }
    };

    public abstract JmhValueFactory getInstance();
  }

  public enum DataType {
    MAP, SET_MULTIMAP, SET
  }

  public enum Archetype {
    MUTABLE, IMMUTABLE, PERSISTENT
  }

  public enum SampleDataSelection {
    MATCH, RANDOM
  }

  public static int seedFromSizeAndRun(int size, int run) {
    return mix(size) ^ mix(run);
  }

  private static int mix(int n) {
    int h = n;

    h *= 0x5bd1e995;
    h ^= h >>> 13;
    h *= 0x5bd1e995;
    h ^= h >>> 15;

    return h;
  }

  public static int[] generateSortedArrayWithRandomData(int size, int run) {

    int[] randomNumbers = new int[size];

    int seedForThisTrial = BenchmarkUtils.seedFromSizeAndRun(size, run);
    Random rand = new Random(seedForThisTrial);

    // System.out.println(String.format("Seed for this trial: %d.",
    // seedForThisTrial));

    for (int i = size - 1; i >= 0; i--) {
      randomNumbers[i] = rand.nextInt();
    }

    Arrays.sort(randomNumbers);

    return randomNumbers;
  }

  static int[] generateTestData(int size, int run) {
    int seedForThisTrial = seedFromSizeAndRun(size, run);
    Random rand = new Random(seedForThisTrial);

    return generateTestData(size, rand);
  }

  static int[] generateTestData(final int size, final Random rand) {
    int[] data = new int[size];

    // System.out.println(String.format("Seed for this trial: %d.", seedForThisTrial));

    for (int i = size - 1; i >= 0; i--) {
      data[i] = rand.nextInt();
    }

    return data;
  }

}
