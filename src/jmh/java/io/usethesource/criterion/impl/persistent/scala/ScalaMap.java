/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.criterion.impl.persistent.scala;

import io.usethesource.criterion.api.JmhMap;
import io.usethesource.criterion.api.JmhValue;
import scala.Tuple2;
import scala.collection.immutable.HashMap;

import java.util.Iterator;
import java.util.Map.Entry;

import static scala.collection.JavaConverters.asJavaIterator;
import static scala.collection.JavaConverters.mapAsJavaMap;

public class ScalaMap implements JmhMap {

  protected final HashMap<JmhValue, JmhValue> xs;

  protected ScalaMap(HashMap xs) {
    this.xs = xs;
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
  public JmhMap put(JmhValue key, JmhValue value) {
    return new ScalaMap((HashMap) xs.$plus(new Tuple2<>(key, value)));
  }

  @Override
  public JmhMap removeKey(JmhValue key) {
    return new ScalaMap((HashMap) xs.$minus(key));
  }

  @Override
  public JmhValue get(JmhValue key) {
    return xs.getOrElse(key, () -> null);
  }

  @Override
  public boolean containsKey(JmhValue key) {
    return xs.contains(key);
  }

  @Override
  public boolean containsValue(JmhValue value) {
    return xs.exists((tuple) -> tuple._2.equals(value));
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

    if (other instanceof ScalaMap) {
      ScalaMap that = (ScalaMap) other;

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
    return asJavaIterator(xs.keysIterator());
  }

  @SuppressWarnings("unchecked")
  @Override
  public Iterator<JmhValue> valueIterator() {
    return asJavaIterator(xs.valuesIterator());
  }

  @SuppressWarnings("unchecked")
  @Override
  public Iterator<Entry<JmhValue, JmhValue>> entryIterator() {
    return mapAsJavaMap(xs).entrySet().iterator();
  }

}
