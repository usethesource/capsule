/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.capsule.util;

public final class BitmapUtils {

  private static final boolean USE_SELF_WRITTEN_POPULATION_COUNT = false;
  private static final boolean USE_SELF_WRITTEN_POPULATION_COUNT_CHECK =
      !USE_SELF_WRITTEN_POPULATION_COUNT && false;

  public static final long filter00(long bitmap) {
    return ((bitmap & 0x5555555555555555L) ^ 0x5555555555555555L)
        & (((bitmap >> 1) & 0x5555555555555555L) ^ 0x5555555555555555L);
  }

  public static final long filter01(long bitmap) {
    return (bitmap & 0x5555555555555555L)
        & (((bitmap >> 1) & 0x5555555555555555L) ^ 0x5555555555555555L);
  }

  public static final long filter10(long bitmap) {
    return ((bitmap & 0x5555555555555555L) ^ 0x5555555555555555L)
        & ((bitmap >> 1) & 0x5555555555555555L);
  }

  public static final long filter11(long bitmap) {
    return (bitmap & 0x5555555555555555L) & ((bitmap >> 1) & 0x5555555555555555L);
  }

  public static final long filter(long bitmap, int pattern) {
    switch (pattern) {
      case 0b00:
        return filter00(bitmap);
      case 0b01:
        return filter01(bitmap);
      case 0b10:
        return filter10(bitmap);
      case 0b11:
        return filter11(bitmap);
      default:
        throw new IllegalArgumentException();
    }
  }

  public static final int index(long bitmap, int pattern, long bitpos) {
    return java.lang.Long.bitCount(filter(bitmap, pattern) & (bitpos - 1));
  }

  public static final int index01(final long bitmap, final long bitpos) {
    if (USE_SELF_WRITTEN_POPULATION_COUNT) {
      return (int) populationCountPattern01(bitmap & (bitpos - 1));
    } else {
      // final long filteredBitmap = (bitmap & 0x5555555555555555L)
      // & (((bitmap >> 1) & 0x5555555555555555L) ^ 0x5555555555555555L);
      final long filteredBitmap = filter01(bitmap);
      final int index = java.lang.Long.bitCount(filteredBitmap & (bitpos - 1));

      if (USE_SELF_WRITTEN_POPULATION_COUNT_CHECK) {
        final int otherIndex = (int) populationCountPattern01(bitmap & (bitpos - 1));
        if (index != otherIndex) {
          throw new IllegalStateException(index + "!=" + otherIndex);
        }
      }

      return index;
    }
  }

  public static final int index10(final long bitmap, final long bitpos) {
    if (USE_SELF_WRITTEN_POPULATION_COUNT) {
      return (int) populationCountPattern10(bitmap & (bitpos - 1));
    } else {
      // final long filteredBitmap = ((bitmap & 0x5555555555555555L) ^ 0x5555555555555555L)
      // & ((bitmap >> 1) & 0x5555555555555555L);
      final long filteredBitmap = filter10(bitmap);
      final int index = java.lang.Long.bitCount(filteredBitmap & (bitpos - 1));

      if (USE_SELF_WRITTEN_POPULATION_COUNT_CHECK) {
        final int otherIndex = (int) populationCountPattern10(bitmap & (bitpos - 1));
        if (index != otherIndex) {
          throw new IllegalStateException(index + "!=" + otherIndex);
        }
      }

      return index;
    }
  }

  public static final int index11(final long bitmap, final long bitpos) {
    if (USE_SELF_WRITTEN_POPULATION_COUNT) {
      return (int) populationCountPattern11(bitmap & (bitpos - 1));
    } else {
      // final long filteredBitmap =
      // (bitmap & 0x5555555555555555L) & ((bitmap >> 1) & 0x5555555555555555L);
      final long filteredBitmap = filter11(bitmap);
      final int index = java.lang.Long.bitCount(filteredBitmap & (bitpos - 1));

      if (USE_SELF_WRITTEN_POPULATION_COUNT_CHECK) {
        final int otherIndex = (int) populationCountPattern11(bitmap & (bitpos - 1));
        if (index != otherIndex) {
          throw new IllegalStateException(index + "!=" + otherIndex);
        }
      }

      return index;
    }
  }

  public static final long populationCountPattern00(long v) {
    long c = ((v & 0x5555555555555555L) ^ 0x5555555555555555L)
        & (((v >> 1) & 0x5555555555555555L) ^ 0x5555555555555555L);
    c = (c & 0x3333333333333333L) + ((c >> 2) & 0x3333333333333333L);
    c = (c & 0x0F0F0F0F0F0F0F0FL) + ((c >> 4) & 0x0F0F0F0F0F0F0F0FL);
    c = (c & 0x00FF00FF00FF00FFL) + ((c >> 8) & 0x00FF00FF00FF00FFL);
    c = (c & 0x0000FFFF0000FFFFL) + ((c >> 16) & 0x0000FFFF0000FFFFL);
    return c;
  }

  public static final long populationCountPattern01(long v) {
    long c = (v & 0x5555555555555555L) & (((v >> 1) & 0x5555555555555555L) ^ 0x5555555555555555L);
    c = (c & 0x3333333333333333L) + ((c >> 2) & 0x3333333333333333L);
    c = (c & 0x0F0F0F0F0F0F0F0FL) + ((c >> 4) & 0x0F0F0F0F0F0F0F0FL);
    c = (c & 0x00FF00FF00FF00FFL) + ((c >> 8) & 0x00FF00FF00FF00FFL);
    c = (c & 0x0000FFFF0000FFFFL) + ((c >> 16) & 0x0000FFFF0000FFFFL);
    return c;
  }

  public static final long populationCountPattern10(long v) {
    long c = ((v & 0x5555555555555555L) ^ 0x5555555555555555L) & ((v >> 1) & 0x5555555555555555L);
    c = (c & 0x3333333333333333L) + ((c >> 2) & 0x3333333333333333L);
    c = (c & 0x0F0F0F0F0F0F0F0FL) + ((c >> 4) & 0x0F0F0F0F0F0F0F0FL);
    c = (c & 0x00FF00FF00FF00FFL) + ((c >> 8) & 0x00FF00FF00FF00FFL);
    c = (c & 0x0000FFFF0000FFFFL) + ((c >> 16) & 0x0000FFFF0000FFFFL);
    return c;
  }

  public static final long populationCountPattern11(long v) {
    long c = (v & 0x5555555555555555L) & ((v >> 1) & 0x5555555555555555L);
    c = (c & 0x3333333333333333L) + ((c >> 2) & 0x3333333333333333L);
    c = (c & 0x0F0F0F0F0F0F0F0FL) + ((c >> 4) & 0x0F0F0F0F0F0F0F0FL);
    c = (c & 0x00FF00FF00FF00FFL) + ((c >> 8) & 0x00FF00FF00FF00FFL);
    c = (c & 0x0000FFFF0000FFFFL) + ((c >> 16) & 0x0000FFFF0000FFFFL);
    return c;
  }

  public static boolean isBitInBitmap(byte bitmap, byte bitpos) {
    return (bitmap != 0 && (bitmap == -1 || (bitmap & bitpos) != 0));
    // return (bitmap & bitpos) != 0;
  }

  public static boolean isBitInBitmap(int bitmap, int bitpos) {
    return (bitmap != 0 && (bitmap == -1 || (bitmap & bitpos) != 0));
    // return (bitmap & bitpos) != 0;
  }
}
