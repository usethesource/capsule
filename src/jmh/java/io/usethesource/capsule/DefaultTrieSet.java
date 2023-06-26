/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
//package io.usethesource.capsule;
//
//import java.lang.reflect.InvocationTargetException;
//import java.lang.reflect.Method;
//
//import io.usethesource.capsule.api.Set;
//import io.usethesource.capsule.core.PersistentTrieSet;
//
//@Deprecated
//public class DefaultTrieSet {
//
//  private static Class<PersistentTrieSet> target = PersistentTrieSet.class;
//
//  private static Method persistentSetOfEmpty;
//  private static Method persistentSetOfKeyValuePairs;
//
//  private static Method transientSetOfEmpty;
//  private static Method transientSetOfKeyValuePairs;
//
//  public static Class<PersistentTrieSet> getTargetClass() {
//    return target;
//  }
//
//  static {
//    try {
//      persistentSetOfEmpty = target.getMethod("of");
//      persistentSetOfKeyValuePairs = target.getMethod("of", Object[].class);
//
//      transientSetOfEmpty = target.getMethod("transientOf");
//      transientSetOfKeyValuePairs = target.getMethod("transientOf", Object[].class);
//    } catch (NoSuchMethodException | SecurityException e) {
//      e.printStackTrace();
//      throw new RuntimeException(e);
//    }
//  }
//
//  public static final <K> Set.Immutable<K> of() {
//    try {
//      return (Set.Immutable<K>) persistentSetOfEmpty.invoke(null);
//    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
//      throw new RuntimeException(e);
//    }
//  }
//
//  public static final <K> Set.Immutable<K> of(K... keys) {
//    try {
//      return (Set.Immutable<K>) persistentSetOfKeyValuePairs.invoke(null, (Object) keys);
//    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
//      throw new RuntimeException(e);
//    }
//  }
//
//  public static final <K> Set.Transient<K> transientOf() {
//    try {
//      return (Set.Transient<K>) transientSetOfEmpty.invoke(null);
//    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
//      throw new RuntimeException(e);
//    }
//  }
//
//  public static final <K> Set.Transient<K> transientOf(K... keys) {
//    try {
//      return (Set.Transient<K>) transientSetOfKeyValuePairs.invoke(null, (Object) keys);
//    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
//      throw new RuntimeException(e);
//    }
//  }
//
//}
