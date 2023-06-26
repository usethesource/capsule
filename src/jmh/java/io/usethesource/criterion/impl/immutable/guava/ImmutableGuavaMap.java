/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.criterion.impl.immutable.guava;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import com.google.common.collect.ImmutableMap;
import io.usethesource.criterion.api.JmhMap;
import io.usethesource.criterion.api.JmhValue;

public final class ImmutableGuavaMap implements JmhMap {

  private final ImmutableMap<JmhValue, JmhValue> content;

  protected ImmutableGuavaMap(ImmutableMap<JmhValue, JmhValue> content) {
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
    final HashMap<JmhValue, JmhValue> tmpContent = new HashMap<>(content);
    tmpContent.put(key, value);

    final ImmutableMap<JmhValue, JmhValue> newContent = ImmutableMap.copyOf(tmpContent);

    return new ImmutableGuavaMap(newContent);

//    try {
//      final ImmutableMap<JmhValue, JmhValue> newContent =
//          ImmutableMap.<JmhValue, JmhValue>builder().putAll(content).put(key, value).build();
//
//      return new ImmutableGuavaMap(newContent);
//    } catch (IllegalArgumentException e) {
//      return this;
//    }
  }

  @Override
  public JmhMap removeKey(JmhValue key) {
    final HashMap<JmhValue, JmhValue> tmpContent = new HashMap<>(content);
    tmpContent.remove(key);

    final ImmutableMap<JmhValue, JmhValue> newContent = ImmutableMap.copyOf(tmpContent);

    return new ImmutableGuavaMap(newContent);
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

    if (other instanceof ImmutableGuavaMap) {
      ImmutableGuavaMap that = (ImmutableGuavaMap) other;

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
    return content.entrySet().iterator();
  }

}
