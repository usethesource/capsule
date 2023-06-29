/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.capsule.jmh.impl.persistent.scala;

import io.usethesource.capsule.jmh.api.JmhValue;
import io.usethesource.capsule.jmh.impl.AbstractMapBuilder;
import scala.Tuple2;
import scala.collection.immutable.HashMapBuilder;

final class ScalaMapWriter extends
        AbstractMapBuilder<JmhValue, HashMapBuilder<JmhValue, JmhValue>> {

  ScalaMapWriter() {
    super(new HashMapBuilder<>(), builder -> (key, value) -> (HashMapBuilder<JmhValue, JmhValue>) builder.$plus$eq(new Tuple2<>(key, value)),
        builder -> new ScalaMap(builder.result()));
  }

}
