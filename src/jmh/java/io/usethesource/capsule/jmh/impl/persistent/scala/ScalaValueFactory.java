/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.capsule.jmh.impl.persistent.scala;

import io.usethesource.capsule.jmh.api.JmhMap;
import io.usethesource.capsule.jmh.api.JmhSet;
import io.usethesource.capsule.jmh.api.JmhSetMultimap;
import io.usethesource.capsule.jmh.api.JmhValueFactory;

public class ScalaValueFactory implements JmhValueFactory {

  @Override
  public JmhMap.Builder mapBuilder() {
    return new ScalaMapWriter();
  }

  @Override
  public JmhSet.Builder setBuilder() {
    return new ScalaSetWriter();
  }

  @Override
  public JmhSetMultimap.Builder setMultimapBuilder() {
    return new ScalaSetMultimapWriter();
  }

  @Override
  public String toString() {
    return "VF_SCALA";
  }

}
