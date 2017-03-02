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
import io.usethesource.capsule.core.PersistentTrieSetMultimap;
import io.usethesource.capsule.util.EqualityComparator;

public class DefaultTrieSetMultimap {

  private static Class<PersistentTrieSetMultimap> target = PersistentTrieSetMultimap.class;

  public static Class<PersistentTrieSetMultimap> getTargetClass() {
    return target;
  }

  private static Method persistentSetMultimapOfEmpty;
  private static Method persistentSetMultimapOfEmptyEq;

  private static Method transientSetMultimapOfEmpty;
  private static Method transientSetMultimapOfEmptyEq;

  static {
    try {
      persistentSetMultimapOfEmpty = target.getMethod("of");
      persistentSetMultimapOfEmptyEq = target.getMethod("of", EqualityComparator.class);

      transientSetMultimapOfEmpty = target.getMethod("transientOf");
      transientSetMultimapOfEmptyEq = target.getMethod("transientOf", EqualityComparator.class);
    } catch (NoSuchMethodException | SecurityException e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    }
  }

  public static final <K, V> SetMultimap.Immutable<K, V> of() {
    try {
      return (SetMultimap.Immutable<K, V>) persistentSetMultimapOfEmpty.invoke(null);
    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
      throw new RuntimeException(e);
    }
  }

  public static final <K, V> SetMultimap.Immutable<K, V> of(EqualityComparator<Object> cmp) {
    try {
      return (SetMultimap.Immutable<K, V>) persistentSetMultimapOfEmptyEq.invoke(null, cmp);
    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
      throw new RuntimeException(e);
    }
  }

  public static final <K, V> SetMultimap.Transient<K, V> transientOf() {
    try {
      return (SetMultimap.Transient<K, V>) transientSetMultimapOfEmpty.invoke(null);
    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
      throw new RuntimeException(e);
    }
  }

  public static final <K, V> SetMultimap.Transient<K, V> transientOf(
      EqualityComparator<Object> cmp) {
    try {
      return (SetMultimap.Transient<K, V>) transientSetMultimapOfEmptyEq.invoke(null, cmp);
    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
      throw new RuntimeException(e);
    }
  }

}
