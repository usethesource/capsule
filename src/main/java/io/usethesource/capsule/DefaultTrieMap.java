/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.capsule;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class DefaultTrieMap {

  @SuppressWarnings("rawtypes")
  private static Class<TrieMap_5Bits> target = TrieMap_5Bits.class;

  private static Method persistentMapOfEmpty;
  private static Method persistentMapOfKeyValuePairs;

  private static Method transientMapOfEmpty;
  private static Method transientMapOfKeyValuePairs;

  @SuppressWarnings("rawtypes")
  public static Class<TrieMap_5Bits> getTargetClass() {
    return target;
  }

  static {
    try {
      persistentMapOfEmpty = target.getMethod("of");
      persistentMapOfKeyValuePairs = target.getMethod("of", Object[].class);

      transientMapOfEmpty = target.getMethod("transientOf");
      transientMapOfKeyValuePairs = target.getMethod("transientOf", Object[].class);
    } catch (NoSuchMethodException | SecurityException e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    }
  }

  @SuppressWarnings("unchecked")
  public static final <K, V> ImmutableMap<K, V> of() {
    try {
      return (ImmutableMap<K, V>) persistentMapOfEmpty.invoke(null);
    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
      throw new RuntimeException(e);
    }
  }

  @SuppressWarnings("unchecked")
  public static final <K, V> ImmutableMap<K, V> of(Object... keyValuePairs) {
    try {
      return (ImmutableMap<K, V>) persistentMapOfKeyValuePairs.invoke(null, (Object) keyValuePairs);
    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
      throw new RuntimeException(e);
    }
  }

  @SuppressWarnings("unchecked")
  public static final <K, V> TransientMap<K, V> transientOf() {
    try {
      return (TransientMap<K, V>) transientMapOfEmpty.invoke(null);
    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
      throw new RuntimeException(e);
    }
  }

  @SuppressWarnings("unchecked")
  public static final <K, V> TransientMap<K, V> transientOf(Object... keyValuePairs) {
    try {
      return (TransientMap<K, V>) transientMapOfKeyValuePairs.invoke(null, (Object) keyValuePairs);
    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    }
  }

}
