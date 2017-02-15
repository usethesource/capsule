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

import io.usethesource.capsule.api.SetMultimap;

public class SetMultimapFactory {

  // private final Class<? extends Immutable<?, ?>> targetClass;

  private final Method persistentMapOfEmpty;
  // private final Method persistentMapOfKeyValuePairs;

  private final Method transientMapOfEmpty;
  // private final Method transientMapOfKeyValuePairs;

  public SetMultimapFactory(Class<?> targetClass) {
    // this.targetClass = targetClass;

    try {
      persistentMapOfEmpty = targetClass.getMethod("of");
      // persistentMapOfKeyValuePairs = targetClass.getMethod("of", Object.class, Object[].class);

      transientMapOfEmpty = targetClass.getMethod("transientOf");
      // transientMapOfKeyValuePairs = targetClass.getMethod("transientOf", Object.class,
      // Object[].class);
    } catch (NoSuchMethodException | SecurityException e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    }
  }

  // public Class<? extends Immutable<?, ?>> getTargetClass() {
  // return targetClass;
  // }

  @SuppressWarnings("unchecked")
  public final <K, V> SetMultimap.Immutable<K, V> of() {
    try {
      return (SetMultimap.Immutable<K, V>) persistentMapOfEmpty.invoke(null);
    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
      throw new RuntimeException(e);
    }
  }

  // @SuppressWarnings("unchecked")
  // public final <K, V> SetMultimap.Immutable<K, V> of(K key, V... values) {
  // try {
  // return (SetMultimap.Immutable<K, V>) persistentMapOfKeyValuePairs.invoke(null, key, values);
  // } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
  // throw new RuntimeException(e);
  // }
  // }

  @SuppressWarnings("unchecked")
  public final <K, V> SetMultimap.Transient<K, V> transientOf() {
    try {
      return (SetMultimap.Transient<K, V>) transientMapOfEmpty.invoke(null);
    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
      throw new RuntimeException(e);
    }
  }

  // @SuppressWarnings("unchecked")
  // public final <K, V> SetMultimap.Transient<K, V> transientOf(K key, V... values) {
  // try {
  // return (SetMultimap.Transient<K, V>) transientMapOfKeyValuePairs.invoke(null, key, values);
  // } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
  // e.printStackTrace();
  // throw new RuntimeException(e);
  // }
  // }

}
