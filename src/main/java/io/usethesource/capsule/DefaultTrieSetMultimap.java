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

import io.usethesource.capsule.api.deprecated.ImmutableSetMultimap;
import io.usethesource.capsule.experimental.multimap.TrieSetMultimap_HHAMT_Interlinked;
import io.usethesource.capsule.util.EqualityComparator;

public class DefaultTrieSetMultimap {

  @SuppressWarnings("rawtypes")
  private static Class<TrieSetMultimap_HHAMT_Interlinked> target = TrieSetMultimap_HHAMT_Interlinked.class;

  @SuppressWarnings("rawtypes")
  public static Class<TrieSetMultimap_HHAMT_Interlinked> getTargetClass() {
    return target;
  }

  private static Method persistentSetMultimapOfEmpty;
  private static Method persistentSetMultimapOfEmptyEq;
  private static Method persistentSetMultimapOfKeyValuePairs;

  private static Method transientSetMultimapOfEmpty;
  private static Method transientSetMultimapOfKeyValuePairs;

  static {
    try {
      persistentSetMultimapOfEmpty = target.getMethod("of");
      persistentSetMultimapOfEmptyEq = target.getMethod("of", EqualityComparator.class);

      // persistentSetMultimapOfKeyValuePairs = target.getMethod("of", Object[].class);
      //
      // transientSetMultimapOfEmpty = target.getMethod("transientOf");
      // transientSetMultimapOfKeyValuePairs = target.getMethod("transientOf", Object[].class);
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

  // @SuppressWarnings("unchecked")
  // public static final <K, V> ImmutableSetMultimap<K, V> of(Object... keyValuePairs) {
  // try {
  // return (ImmutableSetMultimap<K, V>) persistentSetMultimapOfKeyValuePairs.invoke(null,
  // (Object) keyValuePairs);
  // } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
  // throw new RuntimeException(e);
  // }
  // }
  //
  // @SuppressWarnings("unchecked")
  // public static final <K, V> TransientSetMultimap<K, V> transientOf() {
  // try {
  // return (TransientSetMultimap<K, V>) transientSetMultimapOfEmpty.invoke(null);
  // } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
  // throw new RuntimeException(e);
  // }
  // }
  //
  // @SuppressWarnings("unchecked")
  // public static final <K, V> TransientSetMultimap<K, V> transientOf(Object... keyValuePairs) {
  // try {
  // return (TransientSetMultimap<K, V>) transientSetMultimapOfKeyValuePairs.invoke(null,
  // (Object) keyValuePairs);
  // } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
  // e.printStackTrace();
  // throw new RuntimeException(e);
  // }
  // }

}
