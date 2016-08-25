/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.capsule;

import static io.usethesource.capsule.DataLayoutHelper.addressSize;
import static io.usethesource.capsule.DataLayoutHelper.isCopyMemorySupported;
import static io.usethesource.capsule.DataLayoutHelper.unsafe;

@SuppressWarnings({"restriction"})
public final class RangecopyUtils {

  @SuppressWarnings("unchecked")
  public static final <T> T allocateHeapRegion(final Class<? extends T> clazz) {
    try {
      final Object newInstance = unsafe.allocateInstance(clazz);
      return (T) newInstance;
    } catch (ClassCastException | InstantiationException e) {
      throw new RuntimeException(e);
    }
  }

  public static final <T> T allocateHeapRegion(final Class<? extends T>[][] lookupTable,
      final int dim1, final int dim2) {
    final Class<? extends T> clazz = lookupTable[dim1][dim2];
    return allocateHeapRegion(clazz);
  }

  public static final boolean _do_rangecompareObjectRegion(Object src, Object dst, long offset,
      int length) {
    long strideSizeInBytes = addressSize;

    for (int i = 0; i < length; i++) {
      Object srcObject = unsafe.getObject(src, offset);
      Object dstObject = unsafe.getObject(dst, offset);
      offset += strideSizeInBytes;

      // assumes that both srcObject != null
      if (!((srcObject == dstObject) || (srcObject.equals(dstObject)))) {
        return false;
      }
    }

    return true;
  }

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
  public final static void __setInIntRegion(Object dst, long dstRegionOffset, int dstPos,
      int value) {
    unsafe.putInt(dst, dstRegionOffset + dstPos * addressSize, value);
  }

  public final static long setInIntRegionVarArgs(Object dst, long dstOffset, int value0,
      int value1) {
    long strideSizeInBytes = 4;

    unsafe.putInt(dst, dstOffset, value0);
    unsafe.putInt(dst, dstOffset + strideSizeInBytes, value1);

    return 2 * strideSizeInBytes;
  }

  @Deprecated
  public final static long setInObjectRegionVarArgs(Object dst, long dstRegionOffset, int dstPos,
      Object value0, Object value1) {
    long dstOffset = dstRegionOffset + dstPos * addressSize;
    return setInObjectRegionVarArgs(dst, dstOffset, value0, value1);
  }

  public final static long setInObjectRegionVarArgs(Object dst, long dstOffset, Object value0) {
    unsafe.putObject(dst, dstOffset, value0);
    return addressSize;
  }

  public final static long setInObjectRegionVarArgs(Object dst, long dstOffset, Object value0,
      Object value1) {
    // System.out.println(org.openjdk.jol.util.VMSupport.vmDetails());
    // System.out.println(org.openjdk.jol.info.ClassLayout.parseClass(dst.getClass()).toPrintable());

    long strideSizeInBytes = addressSize;

    unsafe.putObject(dst, dstOffset, value0);
    unsafe.putObject(dst, dstOffset + strideSizeInBytes, value1);

    return 2 * strideSizeInBytes;
  }

  public final static long setInObjectRegionVarArgs(Object dst, long dstRegionOffset, int dstPos,
      Object... values) {
    long dstOffset = dstRegionOffset + dstPos * addressSize;
    return setInObjectRegionVarArgs(dst, dstOffset, values);
  }

  public final static long setInObjectRegionVarArgs(Object dst, long dstOffset, Object... values) {
    long offset = dstOffset;

    for (Object value : values) {
      unsafe.putObject(dst, offset, value);
      offset += addressSize;
    }

    return offset - dstOffset; // bytes copied
  }

  public final static void setInIntRegion(Object dst, long dstRegionOffset, int dstPos, int value) {
    unsafe.putObject(dst, dstRegionOffset + dstPos * 4, value);
  }

  public final static void setInObjectRegion(Object dst, long dstRegionOffset, int dstPos,
      Object value) {
    unsafe.putObject(dst, dstRegionOffset + dstPos * addressSize, value);
  }

  public final static Object getFromObjectRegion(Object dst, long dstOffset) {
    return unsafe.getObject(dst, dstOffset);
  }

  @SuppressWarnings("unchecked")
  public final static <T> T getFromObjectRegionAndCast(Object dst, long dstOffset) {
    return (T) unsafe.getObject(dst, dstOffset);
  }

  public final static Object getFromObjectRegion(Object dst, long dstRegionOffset, int dstPos) {
    return unsafe.getObject(dst, dstRegionOffset + dstPos * addressSize);
  }

  @SuppressWarnings("unchecked")
  public final static <T> T getFromObjectRegionAndCast(Object dst, long dstRegionOffset,
      int dstPos) {
    return (T) unsafe.getObject(dst, dstRegionOffset + dstPos * addressSize);
  }

  @SuppressWarnings("unchecked")
  public final static <T> T uncheckedCast(Object o) {
    return (T) o;
  }

  public static final boolean USE_NEXT_CLASS_ARRAY = false;

  private static final boolean IS_COPY_MEMORY_SUPPORTED;

  private static final boolean USE_COPY_MEMORY;

  static {
    IS_COPY_MEMORY_SUPPORTED = isCopyMemorySupported();

    if (IS_COPY_MEMORY_SUPPORTED) {
      System.err.println(String.format("%s.%s=%s", RangecopyUtils.class.getName(),
          "isSunMiscUnsafeCopyMemorySupported", "true"));
    } else {
      System.err.println(String.format("%s.%s=%s", RangecopyUtils.class.getName(),
          "isSunMiscUnsafeCopyMemorySupported", "false"));
    }

    USE_COPY_MEMORY = IS_COPY_MEMORY_SUPPORTED && !Boolean.getBoolean(
        String.format("%s.%s", RangecopyUtils.class.getName(), "dontUseSunMiscUnsafeCopyMemory"));

    if (USE_COPY_MEMORY) {
      System.err.println(String.format("%s.%s=%s", RangecopyUtils.class.getName(),
          "useSunMiscUnsafeCopyMemory", "true"));
    } else {
      System.err.println(String.format("%s.%s=%s", RangecopyUtils.class.getName(),
          "useSunMiscUnsafeCopyMemory", "false"));
    }
  }

  public static final long rangecopyPrimitiveRegion(Object src, long srcOffset, Object dst,
      long dstOffset, long sizeInBytes) {
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

  // public static final long rangecopyObjectRegion(Object src, long srcOffset, Object dst, long
  // dstOffset,
  // int length) {
  //// if (length == 0) {
  //// return 0;
  //// }
  //
  // if (USE_COPY_MEMORY) {
  // long sizeInBytes = length * addressSize;
  // if (sizeInBytes != 0)
  // unsafe.copyMemory(src, srcOffset, dst, dstOffset, sizeInBytes);
  // return sizeInBytes;
  // } else {
  // long strideSizeInBytes = addressSize;
  // long sizeInBytes = length * strideSizeInBytes;
  //
  // for (int i = 0; i < length; i++) {
  // unsafe.putObject(dst, dstOffset, unsafe.getObject(src, srcOffset));
  // srcOffset += strideSizeInBytes;
  // dstOffset += strideSizeInBytes;
  // }
  //
  // return sizeInBytes;
  // }
  // }

  public static final long rangecopyObjectRegion(Object src, long srcRegionOffset, int srcPos,
      Object dst, long dstRegionOffset, int dstPos, int length) {
    if (length == 0) {
      return 0L;
    }

    long strideSizeInBytes = addressSize;
    return _do_rangecopyObjectRegion(src, srcRegionOffset + srcPos * strideSizeInBytes, dst,
        dstRegionOffset + dstPos * strideSizeInBytes, length);
  }

  public static final long rangecopyObjectRegion(Object src, Object dst, long offset, int length) {
    if (length == 0) {
      return 0L;
    }

    return _do_rangecopyObjectRegion(src, dst, offset, length);

    // _do_rangecopyObjectRegion(src, srcOffset, dst, dstOffset, length);
    // return length * addressSize;
  }

  public static final long rangecopyObjectRegion(Object src, long srcOffset, Object dst,
      long dstOffset, int length) {
    if (length == 0) {
      return 0L;
    }

    return _do_rangecopyObjectRegion(src, srcOffset, dst, dstOffset, length);

    // _do_rangecopyObjectRegion(src, srcOffset, dst, dstOffset, length);
    // return length * addressSize;
  }

  public static final long _do_rangecopyObjectRegion(Object src, Object dst, long offset,
      int length) {
    if (USE_COPY_MEMORY) {
      long strideSizeInBytes = addressSize;
      long sizeInBytes = length * strideSizeInBytes;
      unsafe.copyMemory(src, offset, dst, offset, sizeInBytes);
      return sizeInBytes;
    } else {
      long strideSizeInBytes = addressSize;
      long sizeInBytes = length * strideSizeInBytes;
      for (int i = 0; i < length; i++) {
        unsafe.putObject(dst, offset, unsafe.getObject(src, offset));
        offset += strideSizeInBytes;
      }
      return sizeInBytes;
    }

    // return sizeInBytes;
  }

  public static final long _do_rangecopyObjectRegion(Object src, long srcOffset, Object dst,
      long dstOffset, int length) {
    if (USE_COPY_MEMORY) {
      long strideSizeInBytes = addressSize;
      long sizeInBytes = length * strideSizeInBytes;
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

    // return sizeInBytes;
  }

  // public static final long rangecopyIntRegion(Object src, long srcOffset, Object dst, long
  // dstOffset,
  // int length) {
  //// if (length == 0) {
  //// return 0;
  //// }
  //
  // if (USE_COPY_MEMORY) {
  // long sizeInBytes = length * 4L;
  // if (sizeInBytes != 0)
  // unsafe.copyMemory(src, srcOffset, dst, dstOffset, sizeInBytes);
  // return sizeInBytes;
  // } else {
  // long strideSizeInBytes = 4;
  // long offset = srcOffset;
  //
  // for (int i = 0; i < length; i++) {
  // unsafe.putInt(dst, dstOffset, unsafe.getInt(src, srcOffset));
  // srcOffset += strideSizeInBytes;
  // dstOffset += strideSizeInBytes;
  // }
  //
  // return offset - srcOffset;
  // }
  // }

  public static final long rangecopyIntRegion(Object src, long srcRegionOffset, int srcPos,
      Object dst, long dstRegionOffset, int dstPos, int length) {
    if (length == 0) {
      return 0L;
    }

    long strideSizeInBytes = 4L;
    return _do_rangecopyIntRegion(src, srcRegionOffset + srcPos * strideSizeInBytes, dst,
        dstRegionOffset + dstPos * strideSizeInBytes, length);
  }

  public static final long rangecopyIntRegion(Object src, long srcOffset, Object dst,
      long dstOffset, int length) {
    if (length == 0) {
      return 0L;
    }

    // return _do_rangecopyIntRegion(src, srcOffset, dst, dstOffset, length);

    _do_rangecopyIntRegion(src, srcOffset, dst, dstOffset, length);
    return length * 4L;
  }

  public static final long _do_rangecopyIntRegion(Object src, long srcOffset, Object dst,
      long dstOffset, int length) {
    long strideSizeInBytes = 4L;
    long sizeInBytes = length * strideSizeInBytes;

    if (USE_COPY_MEMORY) {
      unsafe.copyMemory(src, srcOffset, dst, dstOffset, sizeInBytes);
    } else {
      for (int i = 0; i < length; i++) {
        unsafe.putInt(dst, dstOffset, unsafe.getInt(src, srcOffset));
        srcOffset += strideSizeInBytes;
        dstOffset += strideSizeInBytes;
      }
    }

    return sizeInBytes;
  }

  public static final long __rangecopyObjectRegion(Object src, long srcOffset, Object dst,
      long dstOffset, long sizeInBytes) {
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

  public static final void __rangecopyObjectRegion(Object src, Object dst, long offset,
      long sizeInBytes) {
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
  public static final void __rangecopyIntRegion(Object src, long srcRegionOffset, int srcPos,
      Object dst, long dstRegionOffset, int dstPos, int length) {
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

  public static final long sizeOfObject() {
    return addressSize;
  }

  // public static final void rangecopyObjectRegion(Object src, long srcRegionOffset, int srcPos,
  // Object dst,
  // long dstRegionOffset, int dstPos, int length) {
  // if (length != 0) {
  // long strideSizeInBytes = addressSize;
  //
  // if (USE_COPY_MEMORY) {
  // unsafe.copyMemory(src, srcRegionOffset + srcPos * strideSizeInBytes, dst,
  // dstRegionOffset + dstPos * strideSizeInBytes, length * strideSizeInBytes);
  // } else {
  // long srcOffset = srcRegionOffset + srcPos * strideSizeInBytes;
  // long dstOffset = dstRegionOffset + dstPos * strideSizeInBytes;
  //
  // for (int i = 0; i < length; i++) {
  // unsafe.putObject(dst, dstOffset, unsafe.getObject(src, srcOffset));
  // srcOffset += strideSizeInBytes;
  // dstOffset += strideSizeInBytes;
  // }
  // }
  // }
  // }

  // static final void __rangecopyObjectRegion(long regionOffset, Object src, int srcPos, Object
  // dst,
  // int dstPos, int length) {
  // long strideSizeInBytes = addressSize;
  //
  // unsafe.copyMemory(src, regionOffset + srcPos * strideSizeInBytes, dst,
  // regionOffset + dstPos * strideSizeInBytes, length * strideSizeInBytes);
  // }

  public static abstract class EitherIntOrObject {
    public enum Type {
      INT, OBJECT
    }

    public static final EitherIntOrObject ofInt(int value) {
      return new EitherAsInt(value);
    }

    public static final EitherIntOrObject ofObject(Object value) {
      return new EitherAsObject(value);
    }

    public abstract boolean isType(Type type);

    public abstract int getInt();

    public abstract Object getObject();
  }

  public static final class EitherAsInt extends EitherIntOrObject {
    private final int value;

    private EitherAsInt(int value) {
      this.value = value;
    }

    @Override
    public boolean isType(Type type) {
      return type == Type.INT;
    }

    @Override
    public int getInt() {
      return value;
    }

    @Override
    public Object getObject() {
      throw new UnsupportedOperationException(
          String.format("Requested type %s but actually found %s.", Type.OBJECT, Type.INT));
    }
  }

  public static final class EitherAsObject extends EitherIntOrObject {
    private final Object value;

    private EitherAsObject(Object value) {
      this.value = value;
    }

    @Override
    public boolean isType(Type type) {
      return type == Type.OBJECT;
    }

    @Override
    public int getInt() {
      throw new UnsupportedOperationException(
          String.format("Requested type %s but actually found %s.", Type.INT, Type.OBJECT));
    }

    @Override
    public Object getObject() {
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

  public static class Companion {

    final int nodeArity;

    final int payloadArity;

    final int slotArity;

    final int untypedSlotArity;

    final long rareBase;

    final long arrayOffsetLast;

    final long nodeBase;

    public Companion(int nodeArity, int payloadArity, int slotArity, int untypedSlotArity,
        long rareBase, long arrayOffsetLast, long nodeBase) {
      this.nodeArity = nodeArity;
      this.payloadArity = payloadArity;
      this.slotArity = slotArity;
      this.untypedSlotArity = untypedSlotArity;
      this.rareBase = rareBase;
      this.arrayOffsetLast = arrayOffsetLast;
      this.nodeBase = nodeBase;
    }

  }

  public abstract static class ArrayView {

    public ArrayView(final Object base, final long offset, final int length) {
      this.base = base;
      this.offset = offset;
      this.length = length;
    }

    final Object base;
    final long offset;
    final int length;

    abstract Class<?> getElementType();
    // abstract int getElementTypeLength();

    abstract void set(int idx, Object value);
  }

  public static class ObjectArrayView extends ArrayView {

    public ObjectArrayView(Object base, long offset, int length) {
      super(base, offset, length);
    }

    @Override
    Class<?> getElementType() {
      return Object.class;
    }

    // @Override
    // int getElementTypeLength() {
    // return ...;
    // }

    @Override
    void set(int idx, Object value) {
      setInObjectRegion(base, offset, idx, value);
    }

  }

  public static class IntArrayView extends ArrayView {

    public IntArrayView(Object base, long offset, int length) {
      super(base, offset, length);
    }

    @Override
    Class<?> getElementType() {
      return Object.class;
    }

    // @Override
    // int getElementTypeLength() {
    // return ...;
    // }

    @Override
    void set(int idx, Object value) {
      if (!(value instanceof Integer)) {
        throw new IllegalArgumentException();
      }

      setInIntRegion(base, offset, idx, (Integer) value);
    }

  }

  /*
   * final ArrayView getIntArrayView() { final Class<?> clazz = this.getClass(); final int
   * payloadArity = unsafe.getInt(clazz, globalPayloadArityOffset);
   * 
   * return new IntArrayView(this, arrayBase, payloadArity * TUPLE_LENGTH); }
   * 
   * final ArrayView getObjectArrayView() { final Class<?> clazz = this.getClass(); final int
   * untypedSlotArity = unsafe.getInt(clazz, globalUntypedSlotArityOffset); final long rareBase =
   * unsafe.getLong(clazz, globalRareBaseOffset);
   * 
   * return new ObjectArrayView(this, rareBase, untypedSlotArity); }
   */

  public static final void arrayviewcopyObject(Object src, int srcPos, Object dst, int dstPos,
      int length) {
    if (length != 0) {
      ArrayView srcView = (ArrayView) src;
      ArrayView dstView = (ArrayView) dst;

      _do_rangecopyObjectRegion(srcView.base, srcView.offset + srcPos * addressSize, dstView.base,
          dstView.offset + dstPos * addressSize, length);
    }
  }

  public static final void arrayviewcopyInt(Object src, int srcPos, Object dst, int dstPos,
      int length) {
    if (length != 0) {
      ArrayView srcView = (ArrayView) src;
      ArrayView dstView = (ArrayView) dst;

      _do_rangecopyIntRegion(srcView.base, srcView.offset + srcPos * addressSize, dstView.base,
          dstView.offset + dstPos * addressSize, length);
    }
  }

  public static final void arrayviewcopy(Object src, int srcPos, Object dst, int dstPos,
      int length) {
    if (length != 0) {
      // if (!(src instanceof ArrayView && dst instanceof ArrayView))
      // throw new IllegalArgumentException();

      ArrayView srcView = (ArrayView) src;
      ArrayView dstView = (ArrayView) dst;

      // if (!(srcView.getElementType() == dstView.getElementType()))
      // throw new IllegalArgumentException();

      if (srcView.getElementType() == Object.class) {
        _do_rangecopyObjectRegion(srcView.base, srcView.offset + srcPos * addressSize, dstView.base,
            dstView.offset + dstPos * addressSize, length);
      } else {
        _do_rangecopyIntRegion(srcView.base, srcView.offset + srcPos * addressSize, dstView.base,
            dstView.offset + dstPos * addressSize, length);
      }
    }
  }

  // public static final long _do_rangecopyObjectRegion(Object src, long srcOffset, Object dst,
  // long dstOffset, int length) {
  // if (USE_COPY_MEMORY) {
  // long strideSizeInBytes = addressSize;
  // long sizeInBytes = length * strideSizeInBytes;
  // unsafe.copyMemory(src, srcOffset, dst, dstOffset, sizeInBytes);
  // return sizeInBytes;
  // } else {
  // long strideSizeInBytes = addressSize;
  // long sizeInBytes = length * strideSizeInBytes;
  // for (int i = 0; i < length; i++) {
  // unsafe.putObject(dst, dstOffset, unsafe.getObject(src, srcOffset));
  // srcOffset += strideSizeInBytes;
  // dstOffset += strideSizeInBytes;
  // }
  // return sizeInBytes;
  // }
  //
  //// return sizeInBytes;
  // }

  // long offset = src1.offset;
  //
  // offset += rangecopyObjectRegion(src2.base, offset, dst2.base, offset, idxOld2);
  // offset += rangecopyObjectRegion(src2.base, offset + sizeOfObject() * TUPLE_LENGTH, dst2.base,
  // offset, idxNew2 - idxOld2);
  // offset += setInObjectRegionVarArgs(dst2.base, offset, node);
  // offset += rangecopyObjectRegion(src2.base, offset + sizeOfObject(), dst2.base, offset,
  // src2.length - idxNew2 - TUPLE_LENGTH);

  public static interface StreamingCopy {

    public static StreamingCopy streamingCopyTwoOffsets(ObjectArrayView from, ObjectArrayView to) {
      return new StreamingCopyTwoOffsets(from, to);
    }

    public static StreamingCopy streamingCopyOneOffset(ObjectArrayView from, ObjectArrayView to) {
      return new StreamingCopyOneOffset(from, to);
    }

    public void copy(int count);

    public void copyWithSrcForward(int count, int srcForward);

    public void copyWithDstForward(int count, int dstForward);

    public void copyWithSrcDstForward(int count, int srcForward, int dstForward);

    public void skipAtSrc(int count);

    public void skipAtDst(int count);

    public void insert(Object value);

    public void insertWithDstForward(Object value, int dstForward);

    public void put(Object value);

  }

  public static final class StreamingCopyTwoOffsets implements StreamingCopy {

    private final ObjectArrayView src;
    private final ObjectArrayView dst;

    private long srcOffset;
    private long dstOffset;

    private StreamingCopyTwoOffsets(ObjectArrayView src, ObjectArrayView dst) {
      this.src = src;
      this.dst = dst;

      srcOffset = src.offset;
      dstOffset = dst.offset;
    }

    void advance(long bytes) {
      srcOffset += bytes;
      dstOffset += bytes;
    }

    public final void copy(int count) {
      advance(rangecopyObjectRegion(src.base, srcOffset, dst.base, dstOffset, count));
    }

    public final void copyWithSrcForward(int count, int srcForward) {
      advance(rangecopyObjectRegion(src.base, srcOffset + srcForward * addressSize, dst.base,
          dstOffset, count));
    }

    public final void copyWithDstForward(int count, int dstForward) {
      advance(rangecopyObjectRegion(src.base, srcOffset, dst.base,
          dstOffset + dstForward * addressSize, count));
    }

    public final void copyWithSrcDstForward(int count, int srcForward, int dstForward) {
      advance(rangecopyObjectRegion(src.base, srcOffset + srcForward * addressSize, dst.base,
          dstOffset + dstForward * addressSize, count));
    }

    public final void skipAtSrc(int count) {
      srcOffset += count * addressSize;
    }

    public final void skipAtDst(int count) {
      dstOffset += count * addressSize;
    }

    public final void insert(Object value) {
      dstOffset += setInObjectRegionVarArgs(dst.base, dstOffset, value);
    }

    public final void insertWithDstForward(Object value, int dstForward) {
      dstOffset += setInObjectRegionVarArgs(dst.base, dstOffset + dstForward * addressSize, value);
    }

    public final void put(Object value) {
      setInObjectRegionVarArgs(dst.base, dstOffset, value);
    }

    // remainder();

  }

  public static final class StreamingCopyOneOffset implements StreamingCopy {

    private final ObjectArrayView src;
    private final ObjectArrayView dst;

    private long offset;

    StreamingCopyOneOffset(ObjectArrayView src, ObjectArrayView dst) {
      this.src = src;
      this.dst = dst;

      assert src.offset == dst.offset;
      offset = src.offset;
    }

    void advance(long bytes) {
      offset += bytes;
    }

    public final void copy(int count) {
      advance(rangecopyObjectRegion(src.base, offset, dst.base, offset, count));
    }

    public final void copyWithSrcForward(int count, int srcForward) {
      advance(rangecopyObjectRegion(src.base, offset + srcForward * addressSize, dst.base, offset,
          count));
    }

    public final void copyWithDstForward(int count, int dstForward) {
      advance(rangecopyObjectRegion(src.base, offset, dst.base, offset + dstForward * addressSize,
          count));
    }

    public final void copyWithSrcDstForward(int count, int srcForward, int dstForward) {
      advance(rangecopyObjectRegion(src.base, offset + srcForward * addressSize, dst.base,
          offset + dstForward * addressSize, count));
    }

    public final void skipAtSrc(int count) {
      // throw ...
    }

    public final void skipAtDst(int count) {
      // throw ...
    }

    public final void insert(Object value) {
      // throw ...
    }

    public final void insertWithDstForward(Object value, int dstForward) {
      offset += setInObjectRegionVarArgs(dst.base, offset + dstForward * addressSize, value);
    }

    public final void put(Object value) {
      setInObjectRegionVarArgs(dst.base, offset, value);
    }

    // remainder();

  }

}
