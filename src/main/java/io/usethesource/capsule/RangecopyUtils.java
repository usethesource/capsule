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

  final static void setInObjectRegion(Object dst, long dstRegionOffset, int dstPos, Object o) {
    unsafe.putObject(dst, dstRegionOffset + dstPos * addressSize, o);
  }

  final static Object getFromObjectRegion(Object dst, long dstRegionOffset, int dstPos) {
    return unsafe.getObject(dst, dstRegionOffset + dstPos * addressSize);
  }

  static final boolean USE_NEXT_CLASS_ARRAY = false;

  private static final boolean USE_COPY_MEMORY = false;

  static final void rangecopyPrimitiveRegion(Object src, long srcOffset, Object dst, long dstOffset,
      long sizeInBytes) {
    if (sizeInBytes == 0) {
      return;
    }

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

  static final void rangecopyObjectRegion(Object src, long srcOffset, Object dst, long dstOffset,
      long sizeInBytes) {
    if (sizeInBytes == 0) {
      return;
    }

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

  static final void rangecopyObjectRegion(Object src, long srcRegionOffset, int srcPos, Object dst,
      long dstRegionOffset, int dstPos, int length) {
    if (length == 0) {
      return;
    }

    if (USE_COPY_MEMORY) {
      long sizeInBytes = length * addressSize;
      unsafe.copyMemory(src, srcRegionOffset + srcPos * addressSize, dst,
          dstRegionOffset + dstPos * addressSize, sizeInBytes);
    } else {
      long srcOffset = srcRegionOffset + srcPos * addressSize;
      long dstOffset = dstRegionOffset + dstPos * addressSize;

      for (int i = 0; i < length; i++) {
        unsafe.putObject(dst, dstOffset, unsafe.getObject(src, srcOffset));
        srcOffset += addressSize;
        dstOffset += addressSize;
      }
    }
  }

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
    return (byte) (rawMap1 ^ rareMap(rawMap1, rawMap2) & 0xFF);
  }

  public static byte dataMap(byte rawMap1, byte rawMap2) {
    return (byte) (rawMap2 ^ rareMap(rawMap1, rawMap2) & 0xFF);
  }

  public static byte rareMap(byte rawMap1, byte rawMap2) {
    return (byte) (rawMap1 & rawMap2 & 0xFF);
  }

  public static boolean isInBitmap(byte bitmap, byte bitpos) {
    return (bitmap != 0 && (bitmap == -1 || (bitmap & bitpos) != 0));
  }

  public static int toState(byte rawMap1, byte rawMap2, byte bitpos) {
    int bit1 = isInBitmap(rawMap1, bitpos) ? 1 : 0;
    int bit2 = isInBitmap(rawMap2, bitpos) ? 2 : 0;
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

  public static boolean isInBitmap(int bitmap, int bitpos) {
    return (bitmap != 0 && (bitmap == -1 || (bitmap & bitpos) != 0));
  }

  public static int toState(int rawMap1, int rawMap2, int bitpos) {
    int bit1 = isInBitmap(rawMap1, bitpos) ? 1 : 0;
    int bit2 = isInBitmap(rawMap2, bitpos) ? 2 : 0;
    int bit = bit1 | bit2;
    return bit;
  }

}
