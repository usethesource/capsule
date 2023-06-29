/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.capsule.jmh.api;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public interface JmhMap extends Iterable<JmhValue>, JmhValue {

  boolean isEmpty();

  int size();

  JmhMap put(JmhValue key, JmhValue value);

  JmhMap removeKey(JmhValue key);

  /**
   * @return the value that is mapped to this key, or null if no such value exists
   */
  JmhValue get(JmhValue key);

  boolean containsKey(JmhValue key);

  boolean containsValue(JmhValue value);

  /**
   * @return an iterator over the keys of the map
   */
  @Override
  Iterator<JmhValue> iterator();

  /**
   * @return an iterator over the values of the map
   */
  Iterator<JmhValue> valueIterator();

  /**
   * @return an iterator over the keys-value pairs of the map
   */
  Iterator<Entry<JmhValue, JmhValue>> entryIterator();

  interface Builder extends JmhBuilder {

    void put(JmhValue key, JmhValue value);

    void putAll(JmhMap map);

    void putAll(Map<JmhValue, JmhValue> map);

    @Override
    JmhMap done();

  }

}
