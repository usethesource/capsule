/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.capsule.jmh.impl.persistent.capsule;

import java.util.Iterator;

import io.usethesource.capsule.Set;
import io.usethesource.capsule.jmh.api.JmhSet;
import io.usethesource.capsule.jmh.api.JmhValue;

public final class CapsuleSet implements JmhSet {

  private final Set.Immutable<JmhValue> content;

  public CapsuleSet(Set.Immutable<JmhValue> content) {
    this.content = content;
  }

  @Override
  public boolean isEmpty() {
    return content.isEmpty();
  }

  @Override
  public JmhSet insert(JmhValue value) {
    return new CapsuleSet(content.__insert(value));
  }

  @Override
  public JmhSet delete(JmhValue value) {
    return new CapsuleSet(content.__remove(value));
  }

  @Override
  public JmhSet union(JmhSet other) {
    final CapsuleSet that = (CapsuleSet) other;
    return new CapsuleSet(content.union(that.content));
  }

  @Override
  public JmhSet subtract(JmhSet other) {
    final CapsuleSet that = (CapsuleSet) other;
    return new CapsuleSet(content.subtract(that.content));
  }

  @Override
  public JmhSet intersect(JmhSet other) {
    final CapsuleSet that = (CapsuleSet) other;
    return new CapsuleSet(content.intersect(that.content));
  }

//  @Override
//  public JmhSet fromIterable(Iterable<JmhValue> iterable) {
//    // NOTE: remove; makes use of static factory!
//    final Set.Transient<JmhValue> builder = Set.Transient.of();
//    iterable.forEach(builder::__insert);
//    return new CapsuleSet(builder.freeze());
//  }

  @Override
  public int size() {
    return content.size();
  }

  @Override
  public boolean contains(JmhValue value) {
    return content.contains(value);
  }

  @Override
  public Iterator<JmhValue> iterator() {
    return content.iterator();
  }

  @Override
  public java.util.Set<JmhValue> asJavaSet() {
    return content;
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

    if (other instanceof CapsuleSet) {
      CapsuleSet that = (CapsuleSet) other;

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

}
