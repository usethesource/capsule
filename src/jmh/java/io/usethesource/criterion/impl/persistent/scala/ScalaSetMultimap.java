/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.criterion.impl.persistent.scala;

import io.usethesource.criterion.api.JmhSetMultimap;
import io.usethesource.criterion.api.JmhValue;
import scala.Tuple2;
import scala.collection.immutable.MultiDict;

import java.util.Iterator;
import java.util.Map.Entry;

import static scala.collection.JavaConverters.asJavaIterator;
import static scala.collection.JavaConverters.setAsJavaSet;

public class ScalaSetMultimap implements JmhSetMultimap {

  protected final MultiDict<JmhValue, JmhValue> xs;

  protected ScalaSetMultimap(MultiDict<JmhValue, JmhValue> xs) {
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
  public JmhSetMultimap insert(JmhValue key, JmhValue value) {
    return new ScalaSetMultimap(xs.$plus(new Tuple2<>(key, value)));
  }

  @Override
  public JmhSetMultimap put(JmhValue key, JmhValue value) {
    return new ScalaSetMultimap(xs.$minus$times(key).$plus(new Tuple2<>(key, value)));
  }

  @Override
  public JmhSetMultimap remove(JmhValue key) {
    return new ScalaSetMultimap(xs.$minus$times(key));
  }

  @Override
  public JmhSetMultimap remove(JmhValue key, JmhValue value) {
    return new ScalaSetMultimap(xs.$minus(new Tuple2<>(key, value)));
  }

  // @Override
  // public JmhMap removeKey(JmhValue key) {
  // return new ClojureSetMultimap((IPersistentMap) xs.without(key));
  // }

  // @Override
  // public JmhValue get(JmhValue key) {
  // return (JmhValue) xs.valAt(key);
  // }

  @Override
  public boolean containsKey(JmhValue key) {
    return xs.containsKey(key);
  }

  @Override
  public boolean containsValue(JmhValue value) {
    return xs.containsValue(value);
  }

  @Override
  public boolean contains(JmhValue key, JmhValue value) {
    return xs.containsEntry(new Tuple2<>(key, value));
  }

  // @Override
  // public boolean containsValue(JmhValue value) {
  // return ((APersistentMap) xs).containsValue(value);
  // }

  @Override
  public java.util.Set keySet() {
    return setAsJavaSet(xs.keySet());
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

    if (other instanceof ScalaSetMultimap) {
      ScalaSetMultimap that = (ScalaSetMultimap) other;

      if (this.size() != that.size()) {
        return false;
      }

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
    return asJavaIterator(xs.keySet().iterator());
  }

  // @SuppressWarnings("unchecked")
  // @Override
  // public Iterator<JmhValue> valueIterator() {
  // return ((APersistentMap) xs).values().iterator();
  // }

  @SuppressWarnings("unchecked")
  @Override
  public Iterator<Entry<JmhValue, Object>> nativeEntryIterator() { // TODO needs another interface to support native iteration
    return asJavaIterator(xs.iterator().map(tuple -> java.util.Map.entry(tuple._1, tuple._2)));
  }

  @SuppressWarnings("unchecked")
  @Override
  public Iterator<Entry<JmhValue, JmhValue>> entryIterator() {
    return asJavaIterator(xs.iterator().map(tuple -> java.util.Map.entry(tuple._1, tuple._2)));
  }

}
