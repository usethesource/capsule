/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.capsule.core.trie;

import java.util.Objects;

public final class ImmutablePayloadTuple<T> implements
    Comparable<ImmutablePayloadTuple<T>>, java.io.Serializable {

  private static final long serialVersionUID = 42L;

  private final int hash;
  private final T payload;

  public ImmutablePayloadTuple(final int hash, final T payload) {
    this.hash = hash;
    this.payload = payload;
  }

  public static final <K> ImmutablePayloadTuple<K> of(final int hash, final K payload) {
    return new ImmutablePayloadTuple<>(hash, payload);
  }

  public T get() {
    return payload;
  }

  public int keyHash() {
    return hash;
  }

  @Override
  public int compareTo(ImmutablePayloadTuple<T> other) {
    return hash - other.hash;
  }

  @Override
  public int hashCode() {
    return hash;
  }

  @Override
  public boolean equals(Object other) {
    if (null == other) {
      return false;
    }
    if (this == other) {
      return true;
    }
    if (getClass() != other.getClass()) {
      return false;
    }

    ImmutablePayloadTuple that = (ImmutablePayloadTuple) other;

    return hash == that.hash && Objects.equals(payload, that.payload);
  }

  @Override
  public String toString() {
    return String.format("%d", hash);
  }

}
