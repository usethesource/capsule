/*******************************************************************************
 * Copyright (c) 2015-2016 CWI All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *
 * * Michael Steindorfer - Michael.Steindorfer@cwi.nl - CWI
 *******************************************************************************/
package io.usethesource.capsule;

import static io.usethesource.capsule.TrieMap_Heterogeneous_BleedingEdge.AbstractMapNode.unsafe;
import static io.usethesource.capsule.TrieMap_Heterogeneous_BleedingEdge.CompactMapNode.addressSize;

public final class RangecopyUtils {

  /*
   * final Object[] src = this.nodes; final Object[] dst = (Object[]) new Object[src.length];
   * 
   * // copy 'src' and set 1 element(s) at position 'idx' System.arraycopy(src, 0, dst, 0,
   * src.length); dst[idx + 0] = node;
   * 
   * rangecopyObjectRegion(src, rareBase, 0, dst, rareBase, 0, untypedSlotArity);
   * setInObjectRegion(src, rareBase, idx, node);
   * 
   * rangecopyObjectRegion(src, rareBase, dst, rareBase, nodeBase - rareBase);
   * setInObjectRegion(src, rareBase + idx * addressSize, node);
   */

  @Deprecated
  final static void __setInIntRegion(Object dst, long dstRegionOffset, int dstPos, int value) {
    unsafe.putInt(dst, dstRegionOffset + dstPos * addressSize, value);
  }

  final static long setInIntRegionVarArgs(Object dst, long dstOffset, int value0, int value1) {
    long strideSizeInBytes = 4;

    unsafe.putInt(dst, dstOffset, value0);
    unsafe.putInt(dst, dstOffset + strideSizeInBytes, value1);

    return 2 * strideSizeInBytes;
  }

  @Deprecated
  final static long setInObjectRegionVarArgs(Object dst, long dstRegionOffset, int dstPos,
      Object value0, Object value1) {
    long dstOffset = dstRegionOffset + dstPos * addressSize;
    return setInObjectRegionVarArgs(dst, dstOffset, value0, value1);
  }

  final static long setInObjectRegionVarArgs(Object dst, long dstOffset, Object value0) {
    unsafe.putObject(dst, dstOffset, value0);
    return addressSize;
  }

  final static long setInObjectRegionVarArgs(Object dst, long dstOffset, Object value0,
      Object value1) {
    long strideSizeInBytes = addressSize;

    unsafe.putObject(dst, dstOffset, value0);
    unsafe.putObject(dst, dstOffset + strideSizeInBytes, value1);

    return 2 * strideSizeInBytes;
  }

  final static long setInObjectRegionVarArgs(Object dst, long dstRegionOffset, int dstPos,
      Object... values) {
    long dstOffset = dstRegionOffset + dstPos * addressSize;
    return setInObjectRegionVarArgs(dst, dstOffset, values);
  }

  final static long setInObjectRegionVarArgs(Object dst, long dstOffset, Object... values) {
    long offset = dstOffset;

    for (Object value : values) {
      unsafe.putObject(dst, offset, value);
      offset += addressSize;
    }

    return offset - dstOffset; // bytes copied
  }

  final static void setInIntRegion(Object dst, long dstRegionOffset, int dstPos, int value) {
    unsafe.putObject(dst, dstRegionOffset + dstPos * 4, value);
  }

  final static void setInObjectRegion(Object dst, long dstRegionOffset, int dstPos, Object value) {
    unsafe.putObject(dst, dstRegionOffset + dstPos * addressSize, value);
  }

  final static Object getFromObjectRegion(Object dst, long dstRegionOffset, int dstPos) {
    return unsafe.getObject(dst, dstRegionOffset + dstPos * addressSize);
  }

  static final boolean USE_NEXT_CLASS_ARRAY = false;

  private static final boolean USE_COPY_MEMORY = true;

  static final long rangecopyPrimitiveRegion(Object src, long srcOffset, Object dst, long dstOffset,
      long sizeInBytes) {
    if (sizeInBytes != 0) {
      if (USE_COPY_MEMORY) {
        unsafe.copyMemory(src, srcOffset, dst, dstOffset, sizeInBytes);
      } else {
        final int size = 4;
        final int length = (int) (sizeInBytes / size);

        for (int i = 0; i < length; i++) {
          unsafe.putInt(dst, dstOffset, unsafe.getInt(src, srcOffset));
          srcOffset += addressSize;
          dstOffset += addressSize;
        }
      }
    }

    return sizeInBytes;
  }

  public static final long rangecopyObjectRegion(Object src, long srcOffset, Object dst, long dstOffset,
      int length) {
    // if (length == 0) {
    // return 0;
    // }
    
    if (USE_COPY_MEMORY) {
      long sizeInBytes = length * addressSize;
      if (sizeInBytes != 0)
        unsafe.copyMemory(src, srcOffset, dst, dstOffset, sizeInBytes);
      return sizeInBytes;
    } else {
      long strideSizeInBytes = addressSize;
      long sizeInBytes = length * strideSizeInBytes;

      for (int i = 0; i < length; i++) {
        unsafe.putObject(dst, dstOffset, unsafe.getObject(src, srcOffset));
        srcOffset += strideSizeInBytes;
        dstOffset += strideSizeInBytes;
      }

      return sizeInBytes;
    }
  }

  static final long __rangecopyObjectRegion(Object src, long srcOffset, Object dst, long dstOffset,
      long sizeInBytes) {
    if (sizeInBytes != 0) {
      if (USE_COPY_MEMORY) {
        unsafe.copyMemory(src, srcOffset, dst, dstOffset, sizeInBytes);
      } else {
        final int length = (int) (sizeInBytes / addressSize);

        for (int i = 0; i < length; i++) {
          unsafe.putObject(dst, dstOffset, unsafe.getObject(src, srcOffset));
          srcOffset += addressSize;
          dstOffset += addressSize;
        }
      }
    }
    return sizeInBytes;
  }

  static final void __rangecopyObjectRegion(Object src, Object dst, long offset, long sizeInBytes) {
    if (USE_COPY_MEMORY) {
      unsafe.copyMemory(src, offset, dst, offset, sizeInBytes);
    } else {
      // final int length = (int) (sizeInBytes / addressSize);
      //
      // for (int i = 0; i < length; i++) {
      // unsafe.putObject(dst, offset, unsafe.getObject(src, offset));
      // offset += addressSize;
      // }
    }
  }

  @Deprecated
  static final void rangecopyIntRegion(Object src, long srcRegionOffset, int srcPos, Object dst,
      long dstRegionOffset, int dstPos, int length) {
    if (length != 0) {
      int strideSizeInBytes = 4;

      if (USE_COPY_MEMORY) {
        long sizeInBytes = length * strideSizeInBytes;
        unsafe.copyMemory(src, srcRegionOffset + srcPos * strideSizeInBytes, dst,
            dstRegionOffset + dstPos * strideSizeInBytes, sizeInBytes);
      } else {
        long srcOffset = srcRegionOffset + srcPos * strideSizeInBytes;
        long dstOffset = dstRegionOffset + dstPos * strideSizeInBytes;

        for (int i = 0; i < length; i++) {
          unsafe.putInt(dst, dstOffset, unsafe.getInt(src, srcOffset));
          srcOffset += strideSizeInBytes;
          dstOffset += strideSizeInBytes;
        }
      }
    }
  }

  public static final long rangecopyIntRegion(Object src, long srcOffset, Object dst, long dstOffset,
      int length) {
    // if (length == 0) {
    // return 0;
    // }

    if (USE_COPY_MEMORY) {
      long sizeInBytes = length * 4L;
      if (sizeInBytes != 0)
        unsafe.copyMemory(src, srcOffset, dst, dstOffset, sizeInBytes);
      return sizeInBytes;
    } else {
      long strideSizeInBytes = 4;
      long offset = srcOffset;

      for (int i = 0; i < length; i++) {
        unsafe.putInt(dst, dstOffset, unsafe.getInt(src, srcOffset));
        srcOffset += strideSizeInBytes;
        dstOffset += strideSizeInBytes;
      }

      return offset - srcOffset;
    }
  }

  static final long sizeOfObject() {
    return addressSize;
  }

  static final void rangecopyObjectRegion(Object src, long srcRegionOffset, int srcPos, Object dst,
      long dstRegionOffset, int dstPos, int length) {
    if (length != 0) {
      long strideSizeInBytes = addressSize;

      if (USE_COPY_MEMORY) {
        unsafe.copyMemory(src, srcRegionOffset + srcPos * strideSizeInBytes, dst,
            dstRegionOffset + dstPos * strideSizeInBytes, length * strideSizeInBytes);
      } else {
        long srcOffset = srcRegionOffset + srcPos * strideSizeInBytes;
        long dstOffset = dstRegionOffset + dstPos * strideSizeInBytes;

        for (int i = 0; i < length; i++) {
          unsafe.putObject(dst, dstOffset, unsafe.getObject(src, srcOffset));
          srcOffset += strideSizeInBytes;
          dstOffset += strideSizeInBytes;
        }
      }
    }
  }

  // static final void __rangecopyObjectRegion(long regionOffset, Object src, int srcPos, Object
  // dst,
  // int dstPos, int length) {
  // long strideSizeInBytes = addressSize;
  //
  // unsafe.copyMemory(src, regionOffset + srcPos * strideSizeInBytes, dst,
  // regionOffset + dstPos * strideSizeInBytes, length * strideSizeInBytes);
  // }

  static abstract class EitherIntOrObject {
    public enum Type {
      INT, OBJECT
    }

    public static final EitherIntOrObject ofInt(int value) {
      return new EitherAsInt(value);
    }

    public static final EitherIntOrObject ofObject(Object value) {
      return new EitherAsObject(value);
    }

    abstract boolean isType(Type type);

    abstract int getInt();

    abstract Object getObject();
  }

  static final class EitherAsInt extends EitherIntOrObject {
    private final int value;

    private EitherAsInt(int value) {
      this.value = value;
    }

    @Override
    boolean isType(Type type) {
      return type == Type.INT;
    }

    @Override
    int getInt() {
      return value;
    }

    @Override
    Object getObject() {
      throw new UnsupportedOperationException(
          String.format("Requested type %s but actually found %s.", Type.OBJECT, Type.INT));
    }
  }

  static final class EitherAsObject extends EitherIntOrObject {
    private final Object value;

    private EitherAsObject(Object value) {
      this.value = value;
    }

    @Override
    boolean isType(Type type) {
      return type == Type.OBJECT;
    }

    @Override
    int getInt() {
      throw new UnsupportedOperationException(
          String.format("Requested type %s but actually found %s.", Type.INT, Type.OBJECT));
    }

    @Override
    Object getObject() {
      return value;
    }
  }

  /*
   * byte bitmaps
   */

  public static byte nodeMap(byte rawMap1, byte rawMap2) {
    return (byte) (Byte.toUnsignedInt(rawMap1) ^ Byte.toUnsignedInt(rareMap(rawMap1, rawMap2)));
  }

  public static byte nodeMap(byte rawMap1, byte rawMap2, byte rareMap) {
    return (byte) (Byte.toUnsignedInt(rawMap1) ^ Byte.toUnsignedInt(rareMap));
  }
  
  public static byte dataMap(byte rawMap1, byte rawMap2) {
    return (byte) (Byte.toUnsignedInt(rawMap2) ^ Byte.toUnsignedInt(rareMap(rawMap1, rawMap2)));
  }

  public static byte dataMap(byte rawMap1, byte rawMap2, byte rareMap) {
    return (byte) (Byte.toUnsignedInt(rawMap2) ^ Byte.toUnsignedInt(rareMap));
  }
  
  public static byte rareMap(byte rawMap1, byte rawMap2) {
    return (byte) (Byte.toUnsignedInt(rawMap1) & Byte.toUnsignedInt(rawMap2));
  }

  public static boolean isBitInBitmap(byte bitmap, byte bitpos) {
    return (bitmap != 0 && (bitmap == -1 || (bitmap & bitpos) != 0));
    // return (bitmap & bitpos) != 0;
  }

  public static int toState(byte rawMap1, byte rawMap2, byte bitpos) {
    int bit1 = isBitInBitmap(rawMap1, bitpos) ? 1 : 0;
    int bit2 = isBitInBitmap(rawMap2, bitpos) ? 2 : 0;
    int bit = bit1 | bit2;
    return bit;
  }

  /*
   * int bitmaps
   */

  public static int nodeMap(int rawMap1, int rawMap2) {
    return rawMap1 ^ rareMap(rawMap1, rawMap2);
  }

  public static int dataMap(int rawMap1, int rawMap2) {
    return rawMap2 ^ rareMap(rawMap1, rawMap2);
  }

  public static int rareMap(int rawMap1, int rawMap2) {
    return rawMap1 & rawMap2;
  }

  public static boolean isBitInBitmap(int bitmap, int bitpos) {
    return (bitmap != 0 && (bitmap == -1 || (bitmap & bitpos) != 0));
    // return (bitmap & bitpos) != 0;
  }

  public static int toState(int rawMap1, int rawMap2, int bitpos) {
    int bit1 = isBitInBitmap(rawMap1, bitpos) ? 1 : 0;
    int bit2 = isBitInBitmap(rawMap2, bitpos) ? 2 : 0;
    int bit = bit1 | bit2;
    return bit;
  }

  static class Companion {

    final int nodeArity;

    final int payloadArity;

    final int slotArity;

    final int untypedSlotArity;

    final long rareBase;

    final long arrayOffsetLast;

    final long nodeBase;
    
    Companion(int nodeArity, int payloadArity, int slotArity, int untypedSlotArity, long rareBase, long arrayOffsetLast, long nodeBase) {
      this.nodeArity = nodeArity;
      this.payloadArity = payloadArity;
      this.slotArity = slotArity;
      this.untypedSlotArity = untypedSlotArity;
      this.rareBase = rareBase;
      this.arrayOffsetLast = arrayOffsetLast;
      this.nodeBase = nodeBase;          
    }   
    
  }
  
}
