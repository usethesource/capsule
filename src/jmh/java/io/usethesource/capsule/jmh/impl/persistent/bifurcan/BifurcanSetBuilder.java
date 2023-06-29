/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.capsule.jmh.impl.persistent.bifurcan;

import io.lacuna.bifurcan.Set;
import io.usethesource.capsule.jmh.api.JmhValue;
import io.usethesource.capsule.jmh.impl.AbstractSetBuilder;

final class BifurcanSetBuilder extends AbstractSetBuilder<JmhValue, Set<JmhValue>> {

  BifurcanSetBuilder() {
    super(Set.empty(), set -> set::add, BifurcanSet::new);
  }

}
