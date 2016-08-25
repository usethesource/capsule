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

public class SetFactory {

  // private final Class<? extends ImmutableSet<?>> targetClass;

  private final Method persistentSetOfEmpty;
  private final Method persistentSetOfKeyValuePairs;

  private final Method transientSetOfEmpty;
  private final Method transientSetOfKeyValuePairs;

  public SetFactory(Class<?> targetClass) {
    // this.targetClass = targetClass;

    try {
      persistentSetOfEmpty = targetClass.getMethod("of");
      persistentSetOfKeyValuePairs = targetClass.getMethod("of", Object[].class);

      transientSetOfEmpty = targetClass.getMethod("transientOf");
      transientSetOfKeyValuePairs = targetClass.getMethod("transientOf", Object[].class);
    } catch (NoSuchMethodException | SecurityException e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    }
  }

  // public Class<? extends ImmutableSet<?>> getTargetClass() {
  // return targetClass;
  // }

  @SuppressWarnings("unchecked")
  public final <K> ImmutableSet<K> of() {
    try {
      return (ImmutableSet<K>) persistentSetOfEmpty.invoke(null);
    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
      throw new RuntimeException(e);
    }
  }

  @SuppressWarnings("unchecked")
  public final <K> ImmutableSet<K> of(K... keys) {
    try {
      return (ImmutableSet<K>) persistentSetOfKeyValuePairs.invoke(null, (Object) keys);
    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
      throw new RuntimeException(e);
    }
  }

  @SuppressWarnings("unchecked")
  public final <K> TransientSet<K> transientOf() {
    try {
      return (TransientSet<K>) transientSetOfEmpty.invoke(null);
    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
      throw new RuntimeException(e);
    }
  }

  @SuppressWarnings("unchecked")
  public final <K> TransientSet<K> transientOf(K... keys) {
    try {
      return (TransientSet<K>) transientSetOfKeyValuePairs.invoke(null, (Object) keys);
    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
      throw new RuntimeException(e);
    }
  }

}
