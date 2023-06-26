/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.criterion.api;

import java.util.Iterator;
import java.util.Map.Entry;

public interface JmhSetMultimap extends JmhValue { // Iterable<JmhValue>

  boolean isEmpty();

  int size();

  JmhSetMultimap insert(JmhValue key, JmhValue value);

  JmhSetMultimap remove(JmhValue key, JmhValue value);

  JmhSetMultimap put(JmhValue key, JmhValue value);

  JmhSetMultimap remove(JmhValue key);

  boolean containsKey(JmhValue key);

  boolean containsValue(JmhValue value);

  boolean contains(JmhValue key, JmhValue value);

  // JmhValue get(JmhValue key);
  //
  // boolean containsValue(JmhValue value);

  Iterator<JmhValue> iterator();

  // Iterator<JmhValue> valueIterator();

  Iterator<Entry<JmhValue, JmhValue>> entryIterator();

  Iterator<Entry<JmhValue, Object>> nativeEntryIterator();

  java.util.Set<JmhValue> keySet();

  public static interface Builder extends JmhBuilder {

    void insert(JmhValue key, JmhValue value);

    // void putAll(JmhMap map);
    //
    // void putAll(Map<JmhValue, JmhValue> map);

    @Override
    JmhSetMultimap done();

  }

}
