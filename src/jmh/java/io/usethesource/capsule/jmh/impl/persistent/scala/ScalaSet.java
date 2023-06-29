/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.capsule.jmh.impl.persistent.scala;

import io.usethesource.capsule.jmh.api.JmhSet;
import io.usethesource.capsule.jmh.api.JmhValue;
import scala.collection.immutable.HashSet;

import java.util.Iterator;
import java.util.Set;

import static scala.collection.JavaConverters.asJavaIterator;
import static scala.collection.JavaConverters.setAsJavaSet;

class ScalaSet implements JmhSet {

  protected final HashSet<JmhValue> xs;

  protected ScalaSet(HashSet<JmhValue> xs) {
    this.xs = xs;
  }

  @SuppressWarnings("unchecked")
  @Override
  public Iterator<JmhValue> iterator() {
    return asJavaIterator(xs.iterator());
  }

  @Override
  public boolean isEmpty() {
    return size() == 0;
  }

  @Override
  public int size() {
    return xs.size();
  }

  @Override
  public boolean contains(JmhValue x) {
    return xs.contains(x);
  }

  @Override
  public JmhSet insert(JmhValue x) {
    return new ScalaSet((HashSet<JmhValue>) xs.$plus(x));
  }

  @Override
  public JmhSet delete(JmhValue x) {
    return new ScalaSet((HashSet<JmhValue>) xs.$minus(x));
  }

  @Override
  public Set<JmhValue> asJavaSet() {
    return setAsJavaSet(xs);
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

    if (other instanceof ScalaSet) {
      ScalaSet that = (ScalaSet) other;

      return xs.equals(that.xs);
    }

    return false;
  }

  @Override
  public Object unwrap() {
    return xs;
  }

}
