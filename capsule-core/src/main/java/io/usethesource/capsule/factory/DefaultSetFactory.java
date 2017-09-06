/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.capsule.factory;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

import io.usethesource.capsule.Set;

public final class DefaultSetFactory {

  static final String DEFAULT_CLASS_NAME = "io.usethesource.capsule.core.PersistentTrieSet";

  /*
   * Example usage:
   *   -Dio.usethesource.capsule.Set.targetClass=io.usethesource.capsule.core.PersistentTrieSet
   */
  static final String TARGET_CLASS_NAME = System
      .getProperty(String.format("%s.%s", Set.class.getName(), "targetClass"), DEFAULT_CLASS_NAME);

  private final String persistentFactoryMethodName = "of";

  public static final DefaultSetFactory FACTORY = new DefaultSetFactory();

  private final Method of0;
  private final Method of1;
  private final Method of2;
  private final Method ofN;

  private final Method transientOf0;
  private final Method transientOfN;

  private DefaultSetFactory() {
    try {
      final Class<?> targetClass = Class.forName(TARGET_CLASS_NAME);

      of0 = targetClass.getMethod(persistentFactoryMethodName);
      of1 = targetClass.getMethod(persistentFactoryMethodName, Object.class);
      of2 = targetClass.getMethod(persistentFactoryMethodName, Object.class, Object.class);
      ofN = targetClass.getMethod(persistentFactoryMethodName, Object[].class);

      transientOf0 = targetClass.getMethod("transientOf");
      transientOfN = targetClass.getMethod("transientOf", Object[].class);
    } catch (ClassNotFoundException | NoSuchMethodException | SecurityException e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    }
  }

  private static final <T> T unchecked(Callable<?> factoryMethod) {
    try {
      return (T) factoryMethod.call();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public final <K> Set.Immutable<K> of() {
    return unchecked(() -> of0.invoke(null));
  }

  public final <K> Set.Immutable<K> of(K key0) {
    return unchecked(() -> of1.invoke(null, key0));
  }

  public final <K> Set.Immutable<K> of(K key0, K key1) {
    return unchecked(() -> of2.invoke(null, key0, key1));
  }

  public final <K> Set.Immutable<K> of(K... keys) {
    return unchecked(() -> ofN.invoke(null, (Object) keys));
  }

  public final <K> Set.Transient<K> transientOf() {
    return unchecked(() -> transientOf0.invoke(null));
  }

  public final <K> Set.Transient<K> transientOf(K... keys) {
    return unchecked(() -> transientOfN.invoke(null, (Object) keys));
  }

}
