/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.capsule.jmh.impl.persistent.scala;

import io.usethesource.capsule.jmh.api.JmhSetMultimap;
import io.usethesource.capsule.jmh.api.JmhValue;
import scala.Tuple2;
import scala.collection.immutable.MultiDict;
import scala.collection.immutable.MultiDict$;
import scala.collection.mutable.Builder;

class ScalaSetMultimapWriter implements JmhSetMultimap.Builder {

  protected final Builder<Tuple2<JmhValue, JmhValue>, MultiDict<JmhValue, JmhValue>> builder = MultiDict$.MODULE$.newBuilder();

  protected ScalaSetMultimapWriter() {
    super();
  }

  @Override
  public void insert(JmhValue key, JmhValue value) {
    builder.addOne(new Tuple2<>(key, value));
  }

  @Override
  public JmhSetMultimap done() {
    return new ScalaSetMultimap(builder.result());
  }

}
