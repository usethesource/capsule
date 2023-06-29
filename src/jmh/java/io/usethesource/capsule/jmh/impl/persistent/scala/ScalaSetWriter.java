/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.capsule.jmh.impl.persistent.scala;

import io.usethesource.capsule.jmh.api.JmhValue;
import io.usethesource.capsule.jmh.impl.AbstractSetBuilder;
import scala.collection.immutable.HashSetBuilder;

final class ScalaSetWriter extends
        AbstractSetBuilder<JmhValue, HashSetBuilder<JmhValue>> {

  ScalaSetWriter() {
    super(new HashSetBuilder<>(),
        set -> (item) -> (HashSetBuilder) set.$plus$eq(item),
        set -> new ScalaSet(set.result()));
  }

}
