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

import io.usethesource.capsule.api.Map;
import io.usethesource.capsule.core.PersistentTrieMap;

public class DefaultTrieMap {

  private static Class<PersistentTrieMap> target = PersistentTrieMap.class;

  private static Method persistentMapOfEmpty;
  private static Method persistentMapOfKeyValuePairs;

  private static Method transientMapOfEmpty;
  private static Method transientMapOfKeyValuePairs;

  public static Class<PersistentTrieMap> getTargetClass() {
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

  public static final <K, V> Map.Immutable<K, V> of() {
    try {
      return (Map.Immutable<K, V>) persistentMapOfEmpty.invoke(null);
    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
      throw new RuntimeException(e);
    }
  }

  public static final <K, V> Map.Immutable<K, V> of(Object... keyValuePairs) {
    try {
      return (Map.Immutable<K, V>) persistentMapOfKeyValuePairs
          .invoke(null, (Object) keyValuePairs);
    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
      throw new RuntimeException(e);
    }
  }

  public static final <K, V, M extends Map.Transient<K, V>> M transientOf() {
    try {
      return (M) transientMapOfEmpty.invoke(null);
    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
      throw new RuntimeException(e);
    }
  }

  public static final <K, V> Map.Transient<K, V> transientOf(Object... keyValuePairs) {
    try {
      return (Map.Transient<K, V>) transientMapOfKeyValuePairs.invoke(null, (Object) keyValuePairs);
    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    }
  }

}
