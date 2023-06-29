/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.capsule.jmh.impl.persistent.clojure;

import clojure.lang.APersistentSet;
import clojure.lang.IPersistentSet;
import clojure.lang.PersistentHashSet;
import io.usethesource.capsule.jmh.api.JmhSet;
import io.usethesource.capsule.jmh.api.JmhValue;

import java.util.Iterator;
import java.util.Set;

class ClojureSet implements JmhSet {

  protected final IPersistentSet xs;

  protected ClojureSet(IPersistentSet xs) {
    this.xs = xs;
  }

  protected ClojureSet() {
    this(PersistentHashSet.EMPTY);
  }

  protected ClojureSet(JmhValue... values) {
    this(PersistentHashSet.create((Object[]) values));
  }

  @SuppressWarnings("unchecked")
  @Override
  public Iterator<JmhValue> iterator() {
    return ((Iterable<JmhValue>) xs).iterator();
  }

  @Override
  public boolean isEmpty() {
    return size() == 0;
  }

  @Override
  public int size() {
    return xs.count();
  }

  @Override
  public boolean contains(JmhValue x) {
    return xs.contains(x);
  }

  @Override
  public JmhSet insert(JmhValue x) {
    return new ClojureSet((IPersistentSet) xs.cons(x));
  }

  @Override
  public JmhSet delete(JmhValue x) {
    return new ClojureSet(xs.disjoin(x));
  }

  @Override
  public Set<JmhValue> asJavaSet() {
    return ((APersistentSet) xs);
  }

  @Override
  public int hashCode() {
    return xs.hashCode();
  }

  @Override
  public boolean equals(Object other) {
    if (other == this) {
      return true;
    }
    if (other == null) {
      return false;
    }

    if (other instanceof ClojureSet) {
      ClojureSet that = (ClojureSet) other;

      return xs.equals(that.xs);
    }

    return false;
  }

  @Override
  public Object unwrap() {
    return xs;
  }

}
