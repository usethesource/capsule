/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.capsule.jmh.impl.immutable.guava;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.google.common.collect.ImmutableSet;
import io.usethesource.capsule.jmh.api.JmhSet;
import io.usethesource.capsule.jmh.api.JmhValue;

public final class ImmutableGuavaSet implements JmhSet {

  private final ImmutableSet<JmhValue> content;

  ImmutableGuavaSet(ImmutableSet<JmhValue> content) {
    this.content = content;
  }

  @Override
  public boolean isEmpty() {
    return content.isEmpty();
  }

  @Override
  public int size() {
    return content.size();
  }

  @Override
  public JmhSet insert(JmhValue key) {
    final HashSet<JmhValue> tmpContent = new HashSet<>(content);
    tmpContent.add(key);

    final ImmutableSet<JmhValue> newContent = ImmutableSet.copyOf(tmpContent);

    return new ImmutableGuavaSet(newContent);

//    final ImmutableSet<JmhValue> newContent =
//        ImmutableSet.<JmhValue>builder().addAll(content).add(key).build();
//
//    return new ImmutableGuavaSet(newContent);
  }

  @Override
  public JmhSet delete(JmhValue key) {
    final HashSet<JmhValue> tmpContent = new HashSet<>(content);
    tmpContent.remove(key);

    final ImmutableSet<JmhValue> newContent = ImmutableSet.copyOf(tmpContent);

    return new ImmutableGuavaSet(newContent);
  }

  @Override
  public boolean contains(JmhValue key) {
    return content.contains(key);
  }

  @Override
  public int hashCode() {
    return content.hashCode();
  }

  @Override
  public boolean equals(Object other) {
    if (other == this) {
      return true;
    }
    if (other == null) {
      return false;
    }

    if (other instanceof ImmutableGuavaSet) {
      ImmutableGuavaSet that = (ImmutableGuavaSet) other;

      if (this.size() != that.size()) {
        return false;
      }

      return content.equals(that.content);
    }

    return false;
  }

  @Override
  public Object unwrap() {
    return content;
  }

  @Override
  public Iterator<JmhValue> iterator() {
    return content.iterator();
  }

  @Override
  public Set<JmhValue> asJavaSet() {
    return new HashSet<>(content);
  }

}
