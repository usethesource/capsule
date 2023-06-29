/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.capsule.jmh.impl.persistent.bifurcan;

import io.lacuna.bifurcan.Set;
import io.usethesource.capsule.jmh.api.JmhSet;
import io.usethesource.capsule.jmh.api.JmhValue;

import java.util.Iterator;

public final class BifurcanSet implements JmhSet {

  private final Set<JmhValue> content;

  public BifurcanSet(Set<JmhValue> content) {
    this.content = content;
  }

  @Override
  public boolean isEmpty() {
    return content.size() == 0;
  }

  @Override
  public JmhSet insert(JmhValue value) {
    return new BifurcanSet(content.add(value));
  }

  @Override
  public JmhSet delete(JmhValue value) {
    return new BifurcanSet(content.remove(value));
  }

  @Override
  public int size() {
    return Math.toIntExact(content.size());
  }

  @Override
  public boolean contains(JmhValue value) {
    return content.contains(value);
  }

  @Override
  public Iterator<JmhValue> iterator() {
    return content.iterator();
  }

  @Override
  public java.util.Set<JmhValue> asJavaSet() {
    return content.toSet();
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

    if (other instanceof BifurcanSet) {
      BifurcanSet that = (BifurcanSet) other;

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

}
