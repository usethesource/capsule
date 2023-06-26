/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.criterion.impl.persistent.vavr;

import java.util.Iterator;
import java.util.Map.Entry;

import io.usethesource.criterion.api.JmhMap;
import io.usethesource.criterion.api.JmhValue;
import io.vavr.collection.HashMap;

public final class VavrMap implements JmhMap {

  private final HashMap<JmhValue, JmhValue> content;

  protected VavrMap(HashMap<JmhValue, JmhValue> content) {
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
    return new VavrMap(content.put(key, value));
  }

  @Override
  public JmhMap removeKey(JmhValue key) {
    return new VavrMap(content.remove(key));
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
    return content.get(key).get();
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

    if (other instanceof VavrMap) {
      VavrMap that = (VavrMap) other;

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

  @Override
  public Iterator<JmhValue> valueIterator() {
    return content.values().iterator();
  }

  @Override
  public Iterator<Entry<JmhValue, JmhValue>> entryIterator() {
    return content.toJavaMap().entrySet().iterator();
  }

}
