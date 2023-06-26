/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.criterion.api;

import java.util.Iterator;

public interface JmhSet extends JmhValue, Iterable<JmhValue> {

  boolean isEmpty();

  int size();

  boolean contains(JmhValue element);

  JmhSet insert(JmhValue element);

  JmhSet delete(JmhValue elem);

  default boolean subsetOf(JmhSet other) {
     throw new UnsupportedOperationException();
  }

  default JmhSet union(JmhSet other) {
    throw new UnsupportedOperationException();
  }

  default JmhSet subtract(JmhSet other) {
    throw new UnsupportedOperationException();
  }

  default JmhSet intersect(JmhSet other) {
    throw new UnsupportedOperationException();
  }

  @Override
  Iterator<JmhValue> iterator();

  @Deprecated
  java.util.Set<JmhValue> asJavaSet();

  interface Builder extends JmhBuilder {

    @Deprecated
    default void insert(JmhValue value) {
      insert(new JmhValue[]{value});
    }

    void insert(JmhValue... v);

    void insertAll(Iterable<? extends JmhValue> collection);

    @Override
    JmhSet done();

  }

}
