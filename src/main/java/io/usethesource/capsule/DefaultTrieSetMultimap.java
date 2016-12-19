/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.capsule;

import io.usethesource.capsule.api.deprecated.ImmutableSetMultimap;
import io.usethesource.capsule.api.deprecated.TransientSetMultimap;
import io.usethesource.capsule.experimental.multimap.TrieSetMultimap_HCHAMP;
import io.usethesource.capsule.util.EqualityComparator;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class DefaultTrieSetMultimap {

  @SuppressWarnings("rawtypes")
  private static Class<TrieSetMultimap_HCHAMP> target = TrieSetMultimap_HCHAMP.class;

  @SuppressWarnings("rawtypes")
  public static Class<TrieSetMultimap_HCHAMP> getTargetClass() {
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

  @SuppressWarnings("unchecked")
  public static final <K, V> ImmutableSetMultimap<K, V> of() {
    try {
      return (ImmutableSetMultimap<K, V>) persistentSetMultimapOfEmpty.invoke(null);
    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
      throw new RuntimeException(e);
    }
  }

  @SuppressWarnings("unchecked")
  public static final <K, V> ImmutableSetMultimap<K, V> of(EqualityComparator<Object> cmp) {
    try {
      return (ImmutableSetMultimap<K, V>) persistentSetMultimapOfEmptyEq.invoke(null, cmp);
    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
      throw new RuntimeException(e);
    }
  }

  @SuppressWarnings("unchecked")
  public static final <K, V> TransientSetMultimap<K, V> transientOf() {
    try {
      return (TransientSetMultimap<K, V>) transientSetMultimapOfEmpty.invoke(null);
    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
      throw new RuntimeException(e);
    }
  }

  @SuppressWarnings("unchecked")
  public static final <K, V> TransientSetMultimap<K, V> transientOf(
      EqualityComparator<Object> cmp) {
    try {
      return (TransientSetMultimap<K, V>) transientSetMultimapOfEmptyEq.invoke(null, cmp);
    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
      throw new RuntimeException(e);
    }
  }

}
