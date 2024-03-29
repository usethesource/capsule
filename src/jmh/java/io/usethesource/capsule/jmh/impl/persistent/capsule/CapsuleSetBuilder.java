/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.capsule.jmh.impl.persistent.capsule;

import io.usethesource.capsule.Set;
import io.usethesource.capsule.jmh.api.JmhValue;
import io.usethesource.capsule.jmh.impl.AbstractSetBuilder;

final class CapsuleSetBuilder extends
    AbstractSetBuilder<JmhValue, Set.Immutable<JmhValue>> {

  CapsuleSetBuilder() {
    super(Set.Immutable.of(), set -> set::__insert, CapsuleSet::new);
  }

}

//final class CapsuleSetBuilder extends
//    AbstractSetBuilder<JmhValue, Set.Transient<JmhValue>> {
//
//  CapsuleSetBuilder(SetFactory setFactory) {
//    super(
//        setFactory.transientOf(),
//        set -> (item) -> { set.__insert(item); return set; },
//        set -> new CapsuleSet(set.freeze()));
//  }
//
//}
