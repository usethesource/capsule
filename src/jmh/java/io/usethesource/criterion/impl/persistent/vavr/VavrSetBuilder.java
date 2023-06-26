/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.criterion.impl.persistent.vavr;

import io.usethesource.criterion.api.JmhValue;
import io.usethesource.criterion.impl.AbstractSetBuilder;
import io.vavr.collection.HashSet;

final class VavrSetBuilder extends AbstractSetBuilder<JmhValue, HashSet<JmhValue>> {

  VavrSetBuilder() {
    super(HashSet.empty(), set -> set::add, VavrSet::new);
  }

}
