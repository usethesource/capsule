/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.capsule.jmh.impl.persistent.champ;

import java.util.Iterator;
import java.util.Map.Entry;

import io.usethesource.capsule.SetMultimap;
import io.usethesource.capsule.jmh.api.JmhSetMultimap;
import io.usethesource.capsule.jmh.api.JmhValue;

public final class ChampSetMultimap implements JmhSetMultimap {

  private final SetMultimap.Immutable<JmhValue, JmhValue> content;

  protected ChampSetMultimap(SetMultimap.Immutable<JmhValue, JmhValue> content) {
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
  public JmhSetMultimap insert(JmhValue key, JmhValue value) {
    return new ChampSetMultimap(content.__insert(key, value));
  }

  @Override
  public JmhSetMultimap remove(JmhValue key, JmhValue value) {
    return new ChampSetMultimap(content.__remove(key, value));
  }

  @Override
  public JmhSetMultimap put(JmhValue key, JmhValue value) {
    return new ChampSetMultimap(content.__put(key, value));
  }

  @Override
  public JmhSetMultimap remove(JmhValue key) {
    return new ChampSetMultimap(content.__remove(key));
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
  public boolean contains(JmhValue key, JmhValue value) {
    return content.containsEntry(key, value);
  }

  // @Override
  // public JmhValue get(JmhValue key) {
  // return content.get(key);
  // }

  @Override
  public java.util.Set<JmhValue> keySet() {
    return content.keySet();
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

    if (other instanceof ChampSetMultimap) {
      ChampSetMultimap that = (ChampSetMultimap) other;

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

  // @Override
  // public Iterator<JmhValue> valueIterator() {
  // return content.valueIterator();
  // }

  @Override
  public Iterator<Entry<JmhValue, JmhValue>> entryIterator() {
    return content.entryIterator();
  }

  @Override
  public Iterator<Entry<JmhValue, Object>> nativeEntryIterator() {
    return content.nativeEntryIterator();
  }

}
