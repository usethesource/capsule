/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.capsule.jmh.impl.persistent.clojure;

import java.util.Iterator;
import java.util.Map.Entry;

import clojure.lang.APersistentMap;
import clojure.lang.IPersistentMap;
import io.usethesource.capsule.jmh.api.JmhMap;
import io.usethesource.capsule.jmh.api.JmhValue;

public class ClojureMap implements JmhMap {

  protected final IPersistentMap xs;

  protected ClojureMap(IPersistentMap xs) {
    this.xs = xs;
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
  public JmhMap put(JmhValue key, JmhValue value) {
    return new ClojureMap(xs.assoc(key, value));
  }

  @Override
  public JmhMap removeKey(JmhValue key) {
    return new ClojureMap(xs.without(key));
  }

  @Override
  public JmhValue get(JmhValue key) {
    return (JmhValue) xs.valAt(key);
  }

  @Override
  public boolean containsKey(JmhValue key) {
    return xs.containsKey(key);
  }

  @Override
  public boolean containsValue(JmhValue value) {
    return ((APersistentMap) xs).containsValue(value);
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

    if (other instanceof ClojureMap) {
      ClojureMap that = (ClojureMap) other;

      return xs.equals(that.xs);
    }

    return false;
  }

  @Override
  public Object unwrap() {
    return xs;
  }

  @SuppressWarnings("unchecked")
  @Override
  public Iterator<JmhValue> iterator() {
    return ((APersistentMap) xs).keySet().iterator();
  }

  @SuppressWarnings("unchecked")
  @Override
  public Iterator<JmhValue> valueIterator() {
    return ((APersistentMap) xs).values().iterator();
  }

  @SuppressWarnings("unchecked")
  @Override
  public Iterator<Entry<JmhValue, JmhValue>> entryIterator() {
    return ((APersistentMap) xs).entrySet().iterator();
  }

}
