package io.usethesource.capsule;

public class SetMultimapUtils {

  final static int PATTERN_EMPTY = 0b00;
  final static int PATTERN_NODE = 0b01;
  final static int PATTERN_DATA_SINGLETON = 0b10;
  final static int PATTERN_DATA_COLLECTION = 0b11;  
  
  static final long setBitPattern00(final long bitmap, final long doubledBitpos) {
    // generally: from xx to 00
    // here: set both bits individually
    long updatedBitmap = bitmap;
    updatedBitmap |= doubledBitpos;
    updatedBitmap ^= doubledBitpos;
    updatedBitmap |= (doubledBitpos << 1);
    updatedBitmap ^= (doubledBitpos << 1);
    return updatedBitmap;
  }
  
  static final long setBitPattern01(final long bitmap, final long doubledBitpos) {
    // generally: from xx to 01
    // here: set both bits individually
    long updatedBitmap = bitmap;
    updatedBitmap |= doubledBitpos;
    updatedBitmap |= (doubledBitpos << 1);
    updatedBitmap ^= (doubledBitpos << 1);
    return updatedBitmap;
  }
  
  static final long setBitPattern10(final long bitmap, final long doubledBitpos) {
    // generally: from xx to 10
    // here: set both bits individually
    long updatedBitmap = bitmap;
    updatedBitmap |= doubledBitpos;
    updatedBitmap ^= doubledBitpos;
    updatedBitmap |= (doubledBitpos << 1);
    return updatedBitmap;
  }
  
  static final long setBitPattern11(final long bitmap, final long doubledBitpos) {
    // generally: from xx to 11
    // here: set both bits individually
    long updatedBitmap = bitmap;
    updatedBitmap |= (doubledBitpos);
    updatedBitmap |= (doubledBitpos << 1);
    return updatedBitmap;    
  }
  
  static final long setBitPattern(final long bitmap, final long doubledBitpos,
      final int pattern) {
    switch (pattern) {
      case PATTERN_NODE:
        return setBitPattern01(bitmap, doubledBitpos);
      case PATTERN_DATA_SINGLETON:
        return setBitPattern10(bitmap, doubledBitpos);
      case PATTERN_DATA_COLLECTION:
        return setBitPattern11(bitmap, doubledBitpos);
      default:
        return setBitPattern00(bitmap, doubledBitpos);
    }
  }

  static final long setBitPattern00(final long doubledBitpos) {
    // generally: from 00 to 00
    // here: set both bits individually
    long updatedBitmap = 0L;
    return updatedBitmap;
  }
  
  static final long setBitPattern01(final long doubledBitpos) {
    // generally: from 00 to 01
    // here: set both bits individually
    long updatedBitmap = 0L;
    updatedBitmap |= doubledBitpos;
    return updatedBitmap;
  }
  
  static final long setBitPattern10(final long doubledBitpos) {
    // generally: from 00 to 10
    // here: set both bits individually
    long updatedBitmap = 0L;
    updatedBitmap |= (doubledBitpos << 1);
    return updatedBitmap;
  }
  
  static final long setBitPattern11(final long doubledBitpos) {
    // generally: from 00 to 11
    // here: set both bits individually
    long updatedBitmap = 0L;
    updatedBitmap |= (doubledBitpos);
    updatedBitmap |= (doubledBitpos << 1);
    return updatedBitmap;  
  }
  
  static final long setBitPattern(final long doubledBitpos, final int pattern) {
    switch (pattern) {
      case PATTERN_NODE:
        return setBitPattern01(doubledBitpos);
      case PATTERN_DATA_SINGLETON:
        return setBitPattern10(doubledBitpos);
      case PATTERN_DATA_COLLECTION:
        return setBitPattern11(doubledBitpos);
      default:
        return setBitPattern00(doubledBitpos);
    }
  }  
  
}
