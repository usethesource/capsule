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

import io.usethesource.capsule.api.deprecated.ImmutableMap;
import io.usethesource.capsule.api.deprecated.TransientMap;

public class MapFactory {

  // private final Class<? extends ImmutableMap<?, ?>> targetClass;

  private final Method persistentMapOfEmpty;
  private final Method persistentMapOfKeyValuePairs;

  private final Method transientMapOfEmpty;
  private final Method transientMapOfKeyValuePairs;

  public MapFactory(Class<?> targetClass) {
    // this.targetClass = targetClass;

    try {
      persistentMapOfEmpty = targetClass.getMethod("of");
      persistentMapOfKeyValuePairs = targetClass.getMethod("of", Object[].class);

      transientMapOfEmpty = targetClass.getMethod("transientOf");
      transientMapOfKeyValuePairs = targetClass.getMethod("transientOf", Object[].class);
    } catch (NoSuchMethodException | SecurityException e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    }
  }

  // public Class<? extends ImmutableMap<?, ?>> getTargetClass() {
  // return targetClass;
  // }

  @SuppressWarnings("unchecked")
  public final <K, V> ImmutableMap<K, V> of() {
    try {
      return (ImmutableMap<K, V>) persistentMapOfEmpty.invoke(null);
    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
      throw new RuntimeException(e);
    }
  }

  @SuppressWarnings("unchecked")
  public final <K, V> ImmutableMap<K, V> of(Object... keyValuePairs) {
    try {
      return (ImmutableMap<K, V>) persistentMapOfKeyValuePairs.invoke(null, (Object) keyValuePairs);
    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
      throw new RuntimeException(e);
    }
  }

  @SuppressWarnings("unchecked")
  public final <K, V> TransientMap<K, V> transientOf() {
    try {
      return (TransientMap<K, V>) transientMapOfEmpty.invoke(null);
    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
      throw new RuntimeException(e);
    }
  }

  @SuppressWarnings("unchecked")
  public final <K, V> TransientMap<K, V> transientOf(Object... keyValuePairs) {
    try {
      return (TransientMap<K, V>) transientMapOfKeyValuePairs.invoke(null, (Object) keyValuePairs);
    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    }
  }

}
