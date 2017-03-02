/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.capsule.api;

import java.util.Objects;

public interface Triple<T, U, V> {

  static <T, U, V> Triple<T, U, V> of(final T fst, final U snd, final V trd) {
    return new ImmutableTriple(
        Objects.requireNonNull(fst),
        Objects.requireNonNull(snd),
        Objects.requireNonNull(trd));
  }

  T _0();

  U _1();

  V _2();

  <T> T get(int columnIndex);

}


class ImmutableTriple<T, U, V> implements Triple<T, U, V> {

  final T fst;
  final U snd;
  final V trd;

  public ImmutableTriple(final T fst, final U snd, final V trd) {
    this.fst = fst;
    this.snd = snd;
    this.trd = trd;
  }

  @Override
  public T _0() {
    return fst;
  }

  @Override
  public U _1() {
    return snd;
  }

  @Override
  public V _2() {
    return trd;
  }

  @Override
  public <T1> T1 get(int columnIndex) {
    switch (columnIndex) {
      case 0:
        return (T1) fst;
      case 1:
        return (T1) snd;
      case 2:
        return (T1) trd;
      default:
        throw new IndexOutOfBoundsException();
    }

  }

  @Override
  public int hashCode() {
    return Objects.hash(fst, snd, trd);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    ImmutableTriple<?, ?, ?> that = (ImmutableTriple<?, ?, ?>) o;

    return Objects.equals(fst, that.fst) && Objects.equals(snd, that.snd)
        && Objects.equals(trd, that.trd);
  }

  @Override
  public String toString() {
    return String.format("<%s, %s, %s>", fst, snd, trd);
  }

}
