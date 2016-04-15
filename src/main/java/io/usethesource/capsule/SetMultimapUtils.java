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
  
  @Deprecated
  static final <T> ImmutableSet<T> setFromNode(io.usethesource.capsule.TrieSet_5Bits.AbstractSetNode<T> rootNode) {
    return new TrieSet_5Bits<>(rootNode);
  }
  
  @Deprecated
  static final <T> ImmutableSet<T> setFromNode(io.usethesource.capsule.TrieSet_5Bits_Spec0To8.AbstractSetNode<T> rootNode) {
    return new TrieSet_5Bits_Spec0To8<>(rootNode);
  }

  @Deprecated
  static final <T> io.usethesource.capsule.TrieSet_5Bits.AbstractSetNode<T> setNodeOf(T key1) {
    return ((TrieSet_5Bits) TrieSet_5Bits.of(key1)).getRootNode();
  }

  @Deprecated
  static final <T> io.usethesource.capsule.TrieSet_5Bits_Spec0To8.AbstractSetNode<T> specSetNodeOf(T key1) {
    return ((TrieSet_5Bits_Spec0To8) TrieSet_5Bits_Spec0To8.of(key1)).getRootNode();
  }

  @Deprecated
  static final <T> io.usethesource.capsule.TrieSet_5Bits_Spec0To8.AbstractSetNode<T> specSetNodeOf(T key1, T key2) {
    return ((TrieSet_5Bits_Spec0To8) TrieSet_5Bits_Spec0To8.of(key1, key2)).getRootNode();
  }
  
  @Deprecated
  static final <T> io.usethesource.capsule.TrieSet_5Bits.AbstractSetNode<T> setToNode(io.usethesource.capsule.ImmutableSet<T> set) {
    return ((TrieSet_5Bits) set).getRootNode();
  }
  
  @Deprecated
  static final <T> io.usethesource.capsule.TrieSet_5Bits.AbstractSetNode<T> setNodeOf(T key1, T key2) {
    return ((TrieSet_5Bits) TrieSet_5Bits.of(key1, key2)).getRootNode();
  }
  
  @Deprecated
  static final <T> ImmutableSet<T> setOf(T key1) {
    return TrieSet_5Bits.of(key1);
  }

  @Deprecated
  static final <T> ImmutableSet<T> setOf(T key1, T key2) {
    return TrieSet_5Bits.of(key1, key2);
  }  

  static final <T> Set.Immutable<T> setOfNew() {
    return TrieSet.of();
  }
  
  static final <T> Set.Immutable<T> setOfNew(T key1) {
    return TrieSet.of(key1);
  }

  static final <T> Set.Immutable<T> setOfNew(T key1, T key2) {
    return TrieSet.of(key1, key2);
  }    
  
}
