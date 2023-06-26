/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.criterion;

import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Thread)
public class JmhBitmaskingBenchmarks {

//  @CompilerControl(CompilerControl.Mode.DONT_INLINE)
//  public static final int bitCountLong(long bitmap) {
//    return Long.bitCount(bitmap);
//  }
//
//  @CompilerControl(CompilerControl.Mode.DONT_INLINE)
//  public static final int bitCountInt(int bitmap) {
//    return Integer.bitCount(bitmap);
//  }
//
//  @CompilerControl(CompilerControl.Mode.DONT_INLINE)
//  public static final long pextLong(long bitmap, int start) {
//    return Long.bitsExtract(bitmap, 0b11L << start);
//  }
//
//  @CompilerControl(CompilerControl.Mode.DONT_INLINE)
//  public static final long bextrLong(long bitmap, int start, int length) {
//    return Long.bextr(bitmap, start, length);
//  }
//
//  @CompilerControl(CompilerControl.Mode.DONT_INLINE)
//  public static final long bextrBitmapUtils(long bitmap, int start, int length) {
//    return BitmapUtils.bextr(bitmap, start, length);
//  }
//
//  @CompilerControl(CompilerControl.Mode.DONT_INLINE)
//  public static final long bextrTwoBitSpecialized(long bitmap, int start) {
//    return (bitmap >>> start) & 0b11L;
//  }
//
//  @CompilerControl(CompilerControl.Mode.DONT_INLINE)
//  public static final boolean bextrCompare(long bitmap, int start, int length) {
//    long result0 = BitmapUtils.bextr(bitmap, start, length);
//    long result1 = Long.bextr(bitmap, start, length);
//
//    return result0 == result1;
//  }
//
//  private static final Random RAND = new Random();
//
//  long bitmap;
//  int start;
//  final int length = 2;
//
//  @Setup(Level.Invocation)
//  public void bextrSetup() {
//    bitmap = RAND.nextLong();
//    start = RAND.nextInt(32) * 2;
//  }
//
//  @Benchmark
//  public void timeTwoBitParallelBitsExtract(Blackhole bh) {
//    // bh.consume(Long.bitsExtract(bitmap, 0b11L << start));
//    bh.consume(pextLong(bitmap, start));
//  }
//
//  @Benchmark
//  public void timeTwoBitExtractGeneric(Blackhole bh) {
//    // bh.consume((bitmap >>> start) & ((1 << length) - 1));
//    bh.consume(bextrBitmapUtils(bitmap, start, length));
//  }
//
//  @Benchmark
//  public void timeTwoBitExtractSpecialized(Blackhole bh) {
//    bh.consume((bitmap >>> start) & 0b11L);
////    bh.consume(bextrTwoBitSpecialized(bitmap, start));
//  }
//
//  @Benchmark
//  public void timeBitsDepositViaBitPatternSet(Blackhole bh) {
////    long mask = 0b11L << start;
//    int pattern = 0b10;
//
//    bh.consume(BitmapUtils.setBitPattern(bitmap, 1L << start, pattern));
//  }
//
//  @Benchmark
//  public void timeBitsDepositSimple(Blackhole bh) {
//    long pattern = 0b10L;
//
//    long mask = (0b11L << start);
//    long result = ((pattern << start) & mask) ^ (bitmap & ~mask);
//
//    bh.consume(result);
//  }
//
//  @Benchmark
//  public void timeBitsDepositRotate(Blackhole bh) {
//    long pattern = 0b10L;
//
//    long mask = Long.rotateLeft(0xFFFFFFFFFFFFFFFCL, start);
//    long result = (pattern << start) ^ (bitmap & mask);
//
//    bh.consume(result);
//  }
//
//
//  @Benchmark
//  public void timeBitsDepositSoftware(Blackhole bh) {
//    long mask = 0b11L << start;
//    long pattern = 0b10;
//
//    bh.consume(BitmapUtils.bitsDeposit(pattern, mask) ^ (bitmap & ~mask));
//  }
//
//  @Benchmark
//  public void timeBitsDepositIntrinsic(Blackhole bh) {
//    long mask = 0b11L << start;
//    long pattern = 0b10;
//
//    bh.consume(Long.bitsDeposit(pattern, mask) ^ (bitmap & ~mask));
//  }
//
//  @Benchmark
//  public void timeBitsExtractSoftware(Blackhole bh) {
//    bh.consume(BitmapUtils.bitsExtract(bitmap, 0b11L << start));
//  }
//
//  @Benchmark
//  public void timeBitsExtractIntrinsic(Blackhole bh) {
//    bh.consume(Long.bitsExtract(bitmap, 0b11L << start));
//  }
//
//  @Benchmark
//  public void timeCompareBitsDeposit(Blackhole bh) {
//    long mask = 0b11L << start;
//    long pattern = 0b10;
//
//    bh.consume(Long.bitsDeposit(pattern, mask) ^ (bitmap & ~mask));
//
////    long resultSoftware = bitsDeposit(pattern, mask) ^ (bitmap & ~mask);
////    long resultIntrinsic = Long.bitsDeposit(pattern, mask) ^ (bitmap & ~mask);
////
//////    System.out.println(String
//////        .format("%b ... %d (Long) versus %d (BitmapUtils) @ start=%2d bitmap=%64s mask=%64s",
//////            resultSoftware == resultIntrinsic,
//////            resultIntrinsic, resultSoftware, start, Long.toBinaryString(bitmap),
//////            0b11L << start));
////
////    if (resultSoftware != resultIntrinsic) {
////      System.out.println(String
////          .format("%b\n%64s\n%64s\n", resultSoftware == resultIntrinsic,
////              Long.toBinaryString(bitmap), Long.toBinaryString(resultSoftware)));
////    }
////
////    bh.consume(resultSoftware == resultIntrinsic);
//  }
//
//  @Benchmark
//  public void timeCompareBitsExtract(Blackhole bh) {
//    bh.consume(Long.bitsExtract(bitmap, 0b11L << start));
//
////    long resultSoftware = BitmapUtils.bitsExtract(bitmap, 0b11L << start);
////    long resultIntrinsic = Long.bitsExtract(bitmap, 0b11L << start);
////
//////    if (resultIntrinsic != -9L) {
//////      if (resultSoftware != resultIntrinsic) {
//////        System.out.println(String
//////            .format("%b ... %d (Long) versus %d (BitmapUtils) @ start=%2d bitmap=%64s mask=%64s",
//////                resultSoftware == resultIntrinsic,
//////                resultIntrinsic, resultSoftware, start, Long.toBinaryString(bitmap),
//////                0b11L << start));
//////      }
//////    }
////
////    if (resultSoftware != resultIntrinsic) {
////      System.out.println(String
////          .format("%b ... %d (Long) versus %d (BitmapUtils) @ start=%2d bitmap=%64s mask=%64s",
////              resultSoftware == resultIntrinsic,
////              resultIntrinsic, resultSoftware, start, Long.toBinaryString(bitmap),
////              0b11L << start));
////
////      bh.consume(resultSoftware == resultIntrinsic);
////    }
//  }
//
//  @Benchmark
//  public void timeBextr(Blackhole bh) {
////    long result = Long.bextr(bitmap, start, length);
////    long result = Long.bextr(bitmap, (length << 8) | start);
////     long result = BitmapUtils.bextr(bitmap, start, length);
//
//    bh.consume(bitCountLong(bitmap));
//    bh.consume(bitCountInt(start));
//    bh.consume(bitCountInt(length));
//
////    if (!bextrCompare(bitmap, start, length)) {
////      throw new IllegalStateException();
////    }
//
//    long resultBextr = bextrBitmapUtils(bitmap, start, length);
//    // long result1 = bextrLong(bitmap, start, length);
//    long resultPext = pextLong(bitmap, start);
//
//    if (resultPext != -9L) { // resultBextr != resultPext
//      if (resultBextr != resultPext) {
//        System.out.println(String
//            .format("%b ... %d (Long) versus %d (BitmapUtils) @ start=%2d bitmap=%64s mask=%64s",
//                resultBextr == resultPext,
//                resultPext, resultBextr, start, Long.toBinaryString(bitmap), 0b11L << start));
//      }
//    }
//
////    if (result0 != result1) {
////      throw new IllegalStateException();
////    }
//
//    bh.consume(resultBextr == resultPext);
//  }

  public static void main(String[] args) throws RunnerException {
    System.out.println(JmhBitmaskingBenchmarks.class.getSimpleName());

    // timeKeySet*EqualsCanonicalSet, timeMultimapLike*
    // (timeMapLikeContainsValue|timeMultimapLikeInsertTuple|timeMultimapLikeRemoveTuple)

    // @formatter:off
    Options opt = new OptionsBuilder()
        .include(".*" + JmhBitmaskingBenchmarks.class.getSimpleName()
            + ".*") // |Intrinsic|Software
        // timeTwoBitParallelBitsExtract|timeTwoBitExtractGeneric|timeTwoBitExtractSpecialized
        // timeTwoBitExtractSpecialized|timeCompareBitsExtract|timeCompareBitsDeposit
        .timeUnit(TimeUnit.NANOSECONDS)
        .mode(Mode.AverageTime)
        .warmupIterations(5)
        .warmupTime(TimeValue.seconds(1))
        .measurementIterations(5)
        .forks(1)
        .shouldDoGC(true)
////        .jvm("/Users/Michael/Development/jdk8u/build/macosx-x86_64-normal-server-release/jdk/bin/java")
////        .jvm("/Users/Michael/Development/jdk8u/build/macosx-x86_64-normal-server-release/images/j2sdk-image/bin/java")
//        .jvm("/Users/Michael/Development/jdk9/build/macosx-x86_64-normal-server-release/images/jdk-bundle/jdk-9.jdk/Contents/Home/bin/java")
////        .jvm("/Users/Michael/Development/jdk9/build/macosx-x86_64-normal-server-slowdebug/images/jdk-bundle/jdk-9.jdk/Contents/Home/bin/java")
//        .jvmArgs("-Xmx8g", "-XX:-TieredCompilation", "-XX:+UnlockDiagnosticVMOptions", "-XX:-TraceClassLoading", "-XX:-LogCompilation", "-XX:-PrintCompilation", "-XX:-PrintAssembly", "-XX:PrintAssemblyOptions=hsdis-print-bytes", "-XX:-PrintIntrinsics", "-XX:+CheckIntrinsics", "-XX:-PrintInlining") // "-XX:DisableIntrinsic=_bitFieldExtract_lii,_bitFieldExtract_ll"
        .jvmArgs("-Xmx16g")
// "-XX:PrintIdealGraphLevel=2"
// "-XX:CompileOnly=java.lang.Long"
// "-XX:CompileCommand=print,java.lang.Long::bextr"
// "-XX:DisableIntrinsic=_bitFieldExtract_ll"
        .build();
    // @formatter:on

    new Runner(opt).run();
  }

}
