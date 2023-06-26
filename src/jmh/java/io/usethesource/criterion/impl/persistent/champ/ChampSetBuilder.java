/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.criterion.impl.persistent.champ;

import io.usethesource.capsule.Set;
import io.usethesource.capsule.Set.Transient;
import io.usethesource.capsule.SetFactory;
import io.usethesource.criterion.api.JmhSet;
import io.usethesource.criterion.api.JmhValue;
import io.usethesource.criterion.impl.AbstractSetBuilder;

final class ChampSetBuilder extends
    AbstractSetBuilder<JmhValue, Set.Immutable<JmhValue>> {

  ChampSetBuilder(SetFactory setFactory) {
    super(setFactory.of(), set -> set::__insert, ChampSet::new);
  }

}

//final class ChampSetBuilder extends
//    AbstractSetBuilder<JmhValue, Set.Transient<JmhValue>> {
//
//  ChampSetBuilder(SetFactory setFactory) {
//    super(
//        setFactory.transientOf(),
//        set -> (item) -> { set.__insert(item); return set; },
//        set -> new ChampSet(set.freeze()));
//  }
//
//}