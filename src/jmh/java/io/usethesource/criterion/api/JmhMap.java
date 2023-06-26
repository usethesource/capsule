/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.criterion.api;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public interface JmhMap extends Iterable<JmhValue>, JmhValue {

  public boolean isEmpty();

  public int size();

  public JmhMap put(JmhValue key, JmhValue value);

  public JmhMap removeKey(JmhValue key);

  /**
   * @return the value that is mapped to this key, or null if no such value exists
   */
  public JmhValue get(JmhValue key);

  public boolean containsKey(JmhValue key);

  public boolean containsValue(JmhValue value);

  /**
   * @return an iterator over the keys of the map
   */
  @Override
  public Iterator<JmhValue> iterator();

  /**
   * @return an iterator over the values of the map
   */
  public Iterator<JmhValue> valueIterator();

  /**
   * @return an iterator over the keys-value pairs of the map
   */
  public Iterator<Entry<JmhValue, JmhValue>> entryIterator();

  public static interface Builder extends JmhBuilder {

    void put(JmhValue key, JmhValue value);

    void putAll(JmhMap map);

    void putAll(Map<JmhValue, JmhValue> map);

    @Override
    JmhMap done();

  }

}
