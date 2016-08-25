/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.capsule;

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

  public static final sun.misc.Unsafe unsafe = initializeUnsafe();
  
  static final long initializeArrayBase() {
    try {
      // assuems that both are of type Object and next to each other in memory
      return DataLayoutHelperChild.arrayOffsets[0];
    } catch (SecurityException e) {
      throw new RuntimeException(e);
    }
  }

  public static final long arrayBase = initializeArrayBase();

  static final long initializeAddressSize() {
    try {
      // assuems that both are of type Object and next to each other in memory
      return DataLayoutHelperChild.arrayOffsets[1] - DataLayoutHelperChild.arrayOffsets[0];
    } catch (SecurityException e) {
      throw new RuntimeException(e);
    }
  }

  public static final long addressSize = initializeAddressSize();
    
  @SuppressWarnings("restriction")
  static final boolean isCopyMemorySupported() {
    DataLayoutHelperChild src = new DataLayoutHelperChild(new Object(), new Object());
    DataLayoutHelperChild dst = new DataLayoutHelperChild();

    try {
      unsafe.copyMemory(src, DataLayoutHelperChild.arrayOffsets[0], dst,
          DataLayoutHelperChild.arrayOffsets[0], 2 * addressSize);
      return src.slot0 == dst.slot0 && src.slot1 == dst.slot1;
    } catch (IllegalArgumentException e) {
      return false;
    }
  }  
  
  abstract static class DataLayoutHelperBase {
    
    final int rawMap1;
    final int rawMap2;
    
    public DataLayoutHelperBase(Object unused, final int rawMap1, final int rawMap2) {
     this.rawMap1 = rawMap1;
     this.rawMap2 = rawMap2;
    }
    
  }
  
  static class DataLayoutHelperChild extends DataLayoutHelperBase {

    static final long[] arrayOffsets =
        arrayOffsets(DataLayoutHelperChild.class, new String[] {"slot0", "slot1"});

    static final int nodeArity = 0;

    static final int payloadArity = 0;

    static final int slotArity = 2;

    static final int untypedSlotArity = 2;

    static final long arrayOffsetLast = /* arrayBase + 1 * addressSize */ -1;    
    
    public final Object slot0;

    public final Object slot1;

    DataLayoutHelperChild() {
      super(null, (byte) 0, (byte) 0);
      this.slot0 = null;
      this.slot1 = null;      
    }
    
    DataLayoutHelperChild(final Object slot0, final Object slot1) {
      super(null, (byte) 0, (byte) 0);
      this.slot0 = slot0;
      this.slot1 = slot1;
    }

  }

//  static final long globalRawMap1Offset = fieldOffset(DataLayoutHelperChild.class, "rawMap1");
//
//  static final long globalRawMap2Offset = fieldOffset(DataLayoutHelperChild.class, "rawMap2");
//
//  static final long globalArrayOffsetsOffset =
//      fieldOffset(DataLayoutHelperChild.class, "arrayOffsets");
//
//  static final long globalNodeArityOffset =
//      fieldOffset(DataLayoutHelperChild.class, "nodeArity");
//
//  static long globalPayloadArityOffset =
//      fieldOffset(DataLayoutHelperChild.class, "payloadArity");
//  
//  static final long globalSlotArityOffset =
//      fieldOffset(DataLayoutHelperChild.class, "slotArity");
//
//  static long globalUntypedSlotArityOffset =
//      fieldOffset(DataLayoutHelperChild.class, "untypedSlotArity");
//
//  static long globalArrayOffsetLastOffset =
//      fieldOffset(DataLayoutHelperChild.class, "arrayOffsetLast");
  
}
