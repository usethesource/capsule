/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.criterion.impl.immutable.guava;

import java.util.Iterator;
import java.util.Map.Entry;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.SetMultimap;
import io.usethesource.criterion.api.JmhSetMultimap;
import io.usethesource.criterion.api.JmhValue;

public final class ImmutableGuavaSetMultimap implements JmhSetMultimap {

  private final ImmutableSetMultimap<JmhValue, JmhValue> content;

  protected ImmutableGuavaSetMultimap(ImmutableSetMultimap<JmhValue, JmhValue> content) {
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
    final SetMultimap<JmhValue, JmhValue> tmpContent = HashMultimap.create(content);
    tmpContent.put(key, value);

    final ImmutableSetMultimap<JmhValue, JmhValue> newContent = ImmutableSetMultimap
        .copyOf(tmpContent);

    return new ImmutableGuavaSetMultimap(newContent);

//    final ImmutableSetMultimap<JmhValue, JmhValue> newContent =
//        ImmutableSetMultimap.<JmhValue, JmhValue>builder().putAll(content).put(key, value).build();
//
//    return new ImmutableGuavaSetMultimap(newContent);
  }

  @Override
  public JmhSetMultimap remove(JmhValue key, JmhValue value) {
    final SetMultimap<JmhValue, JmhValue> tmpContent = HashMultimap.create(content);
    tmpContent.remove(key, value);

    final ImmutableSetMultimap<JmhValue, JmhValue> newContent = ImmutableSetMultimap
        .copyOf(tmpContent);

    return new ImmutableGuavaSetMultimap(newContent);
  }

  @Override
  public JmhSetMultimap put(JmhValue key, JmhValue value) {
    final SetMultimap<JmhValue, JmhValue> tmpContent = HashMultimap.create(content);
    tmpContent.removeAll(key);
    tmpContent.put(key, value);

    final ImmutableSetMultimap<JmhValue, JmhValue> newContent = ImmutableSetMultimap
        .copyOf(tmpContent);

    return new ImmutableGuavaSetMultimap(newContent);
  }

  @Override
  public JmhSetMultimap remove(JmhValue key) {
    final SetMultimap<JmhValue, JmhValue> tmpContent = HashMultimap.create(content);
    tmpContent.removeAll(key);

    final ImmutableSetMultimap<JmhValue, JmhValue> newContent = ImmutableSetMultimap
        .copyOf(tmpContent);

    return new ImmutableGuavaSetMultimap(newContent);
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

    if (other instanceof ImmutableGuavaSetMultimap) {
      ImmutableGuavaSetMultimap that = (ImmutableGuavaSetMultimap) other;

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

  // @Override
  // public Iterator<JmhValue> valueIterator() {
  // return content.valueIterator();
  // }

  @Override
  public Iterator<Entry<JmhValue, JmhValue>> entryIterator() {
    return content.entries().iterator();
  }

  @Override
  public Iterator<Entry<JmhValue, Object>> nativeEntryIterator() {
    throw new UnsupportedOperationException("Not yet implemented.");
  }

}
