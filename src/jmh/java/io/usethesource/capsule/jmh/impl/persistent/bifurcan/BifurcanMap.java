/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.capsule.jmh.impl.persistent.bifurcan;

import io.lacuna.bifurcan.Map;
import io.usethesource.capsule.jmh.api.JmhMap;
import io.usethesource.capsule.jmh.api.JmhValue;

import java.util.Iterator;
import java.util.Map.Entry;

public final class BifurcanMap implements JmhMap {

  private final Map<JmhValue, JmhValue> content;

  BifurcanMap(Map<JmhValue, JmhValue> content) {
    this.content = content;
  }

  @Override
  public boolean isEmpty() {
    return content.size() == 0;
  }

  @Override
  public int size() {
    return Math.toIntExact(content.size());
  }

  @Override
  public JmhMap put(JmhValue key, JmhValue value) {
    return new BifurcanMap((Map<JmhValue, JmhValue>) content.put(key, value));
  }

  @Override
  public JmhMap removeKey(JmhValue key) {
    return new BifurcanMap(content.remove(key));
  }

  @Override
  public boolean containsKey(JmhValue key) {
    return content.contains(key);
  }

  @Override
  public boolean containsValue(JmhValue value) {
    return content.stream().filter(entry -> entry.value().equals(value)).findAny().isEmpty();
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

    if (other instanceof BifurcanMap) {
      BifurcanMap that = (BifurcanMap) other;

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
    return content.keys().iterator();
  }

  @Override
  public Iterator<JmhValue> valueIterator() {
    return content.values().iterator();
  }

  @Override
  public Iterator<Entry<JmhValue, JmhValue>> entryIterator() {
    return content.stream().map(entry -> java.util.Map.entry(entry.key(), entry.value())).iterator();
  }

}
