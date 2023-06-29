/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.capsule.jmh.impl.persistent.paguro;

import java.util.Iterator;
import java.util.Map.Entry;

import org.organicdesign.fp.collections.PersistentHashMap;

import io.usethesource.capsule.jmh.api.JmhMap;
import io.usethesource.capsule.jmh.api.JmhValue;

public final class PaguroMap implements JmhMap {

  private final PersistentHashMap<JmhValue, JmhValue> content;

  protected PaguroMap(PersistentHashMap<JmhValue, JmhValue> content) {
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
    return new PaguroMap(content.assoc(key, value));
  }

  @Override
  public JmhMap removeKey(JmhValue key) {
    return new PaguroMap(content.without(key));
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

    if (other instanceof PaguroMap) {
      PaguroMap that = (PaguroMap) other;

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
    return content.keySet().iterator();
  }

  @SuppressWarnings("deprecation")
  @Override
  public Iterator<JmhValue> valueIterator() {
    // safe to call iterator in values view
    return content.values().iterator();
  }

  @Override
  public Iterator<Entry<JmhValue, JmhValue>> entryIterator() {
    return content.entrySet().iterator();
  }

}
