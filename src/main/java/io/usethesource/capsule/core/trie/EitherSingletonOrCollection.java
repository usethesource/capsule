/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.capsule.core.trie;

public abstract class EitherSingletonOrCollection<T> {

  public enum Type {
    SINGLETON, COLLECTION
  }

  public static final <T> EitherSingletonOrCollection<T> of(T value) {
    return new SomeSingleton<>(value);
  }

  public static final <T> EitherSingletonOrCollection of(
      io.usethesource.capsule.Set.Immutable<T> value) {
    return new SomeCollection<>(value);
  }

  abstract boolean isType(Type type);

  abstract T getSingleton();

  abstract io.usethesource.capsule.Set.Immutable<T> getCollection();

  private static final class SomeSingleton<T> extends EitherSingletonOrCollection<T> {

    private final T value;

    private SomeSingleton(T value) {
      this.value = value;
    }

    @Override
    boolean isType(Type type) {
      return type == Type.SINGLETON;
    }

    @Override
    T getSingleton() {
      return value;
    }

    @Override
    io.usethesource.capsule.Set.Immutable<T> getCollection() {
      throw new UnsupportedOperationException(String
          .format("Requested type %s but actually found %s.", Type.COLLECTION, Type.SINGLETON));
    }
  }

  private static final class SomeCollection<T> extends EitherSingletonOrCollection<T> {

    private final io.usethesource.capsule.Set.Immutable<T> value;

    private SomeCollection(io.usethesource.capsule.Set.Immutable<T> value) {
      this.value = value;
    }

    @Override
    boolean isType(Type type) {
      return type == Type.COLLECTION;
    }

    @Override
    T getSingleton() {
      throw new UnsupportedOperationException(String
          .format("Requested type %s but actually found %s.", Type.SINGLETON, Type.COLLECTION));
    }

    @Override
    io.usethesource.capsule.Set.Immutable<T> getCollection() {
      return value;
    }
  }

}
