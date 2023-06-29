/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.capsule.jmh.impl.persistent.capsule;

import java.util.Iterator;
import java.util.Map.Entry;

import io.usethesource.capsule.Map;
import io.usethesource.capsule.jmh.api.JmhMap;
import io.usethesource.capsule.jmh.api.JmhValue;

/*
 * Operates: * without types * with equals() instead of isEqual()
 */
public final class CapsuleMap implements JmhMap {

  private final Map.Immutable<JmhValue, JmhValue> content;

  CapsuleMap(Map.Immutable<JmhValue, JmhValue> content) {
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
  public JmhMap put(JmhValue key, JmhValue value) {
    return new CapsuleMap(content.__put(key, value));
  }

  @Override
  public JmhMap removeKey(JmhValue key) {
    return new CapsuleMap(content.__remove(key));
  }

  @Override
  public boolean containsKey(JmhValue key) {
    return content.containsKey(key);
  }

  @Override
  public boolean containsValue(JmhValue value) {
    return content.containsValue(value);
  }

  @Override
  public JmhValue get(JmhValue key) {
    return content.get(key);
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

    if (other instanceof CapsuleMap) {
      CapsuleMap that = (CapsuleMap) other;

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
    return content.keyIterator();
  }

  @Override
  public Iterator<JmhValue> valueIterator() {
    return content.valueIterator();
  }

  @Override
  public Iterator<Entry<JmhValue, JmhValue>> entryIterator() {
    return content.entryIterator();
  }

}
