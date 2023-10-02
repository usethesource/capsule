/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.capsule.jmh.impl.persistent.paguro;

import io.usethesource.capsule.jmh.api.JmhValue;
import io.usethesource.capsule.jmh.impl.AbstractSetBuilder;
import org.organicdesign.fp.collections.PersistentHashSet;

final class PaguroSetBuilder extends AbstractSetBuilder<JmhValue, PersistentHashSet<JmhValue>> {

  PaguroSetBuilder() {
    super(PersistentHashSet.empty(), set -> set::put, PaguroSet::new);
  }

}
