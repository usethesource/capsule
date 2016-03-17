package io.usethesource.capsule;

import static io.usethesource.capsule.DataLayoutHelper.fieldOffset;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

public class DataLayoutHelper {

  public static final long[] arrayOffsets(final Class clazz, final String[] fieldNames) {
    try {
      long[] arrayOffsets = new long[fieldNames.length];

      for (int i = 0; i < fieldNames.length; i++) {
        arrayOffsets[i] = unsafe.objectFieldOffset(clazz.getDeclaredField(fieldNames[i]));
      }

      return arrayOffsets;
    } catch (NoSuchFieldException | SecurityException e) {
      throw new RuntimeException(e);
    }
  }
  
  public static final long fieldOffset(final Class clazz, final String fieldName) {
    try {
      List<Class> bottomUpHierarchy = new LinkedList<>();

      Class currentClass = clazz;
      while (currentClass != null) {
        bottomUpHierarchy.add(currentClass);
        currentClass = currentClass.getSuperclass();
      }

      final java.util.Optional<Field> fieldNameField = bottomUpHierarchy.stream()
          .flatMap(hierarchyClass -> Stream.of(hierarchyClass.getDeclaredFields()))
          .filter(f -> f.getName().equals(fieldName)).findFirst();

      if (fieldNameField.isPresent()) {

        if (java.lang.reflect.Modifier.isStatic(fieldNameField.get().getModifiers())) {
          return unsafe.staticFieldOffset(fieldNameField.get());
        } else {
          return unsafe.objectFieldOffset(fieldNameField.get());
        }
      } else {
        return sun.misc.Unsafe.INVALID_FIELD_OFFSET;
      }
    } catch (SecurityException e) {
      throw new RuntimeException(e);
    }
  }
  
  protected static final sun.misc.Unsafe initializeUnsafe() {
    try {
      Field field = sun.misc.Unsafe.class.getDeclaredField("theUnsafe");
      field.setAccessible(true);
      return (sun.misc.Unsafe) field.get(null);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  protected static final sun.misc.Unsafe unsafe = initializeUnsafe();
  
  static final long initializeArrayBase() {
    try {
      // assuems that both are of type Object and next to each other in memory
      return DataLayoutHelperChild.arrayOffsets[0];
    } catch (SecurityException e) {
      throw new RuntimeException(e);
    }
  }

  static final long arrayBase = initializeArrayBase();

  static final long initializeAddressSize() {
    try {
      // assuems that both are of type Object and next to each other in memory
      return DataLayoutHelperChild.arrayOffsets[1] - DataLayoutHelperChild.arrayOffsets[0];
    } catch (SecurityException e) {
      throw new RuntimeException(e);
    }
  }

  static final long addressSize = initializeAddressSize();
  
  private abstract static class DataLayoutHelperBase {
    
    private final int rawMap1;
    private final int rawMap2;
    
    public DataLayoutHelperBase(Object unused, final int rawMap1, final int rawMap2) {
     this.rawMap1 = rawMap1;
     this.rawMap2 = rawMap2;
    }
    
  }
  
  private abstract static class DataLayoutHelperChild extends DataLayoutHelperBase {

    private static final long[] arrayOffsets =
        arrayOffsets(DataLayoutHelperChild.class, new String[] {"slot0", "slot1"});

    static final int nodeArity = 0;

    static final int payloadArity = 0;

    static final int slotArity = 2;

    static final int untypedSlotArity = 2;

    static final long arrayOffsetLast = /* arrayBase + 1 * addressSize */ -1;    
    
    public final Object slot0 = null;

    public final Object slot1 = null;

    private DataLayoutHelperChild() {
      super(null, (byte) 0, (byte) 0);
    }

  }

  static final long globalRawMap1Offset = fieldOffset(DataLayoutHelperChild.class, "rawMap1");

  static final long globalRawMap2Offset = fieldOffset(DataLayoutHelperChild.class, "rawMap2");

  static final long globalArrayOffsetsOffset =
      fieldOffset(DataLayoutHelperChild.class, "arrayOffsets");

  static final long globalNodeArityOffset =
      fieldOffset(DataLayoutHelperChild.class, "nodeArity");

  static long globalPayloadArityOffset =
      fieldOffset(DataLayoutHelperChild.class, "payloadArity");
  
  static final long globalSlotArityOffset =
      fieldOffset(DataLayoutHelperChild.class, "slotArity");

  static long globalUntypedSlotArityOffset =
      fieldOffset(DataLayoutHelperChild.class, "untypedSlotArity");

  static long globalArrayOffsetLastOffset =
      fieldOffset(DataLayoutHelperChild.class, "arrayOffsetLast");
  
}
