/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.criterion.impl.persistent.clojure;

import clojure.lang.IPersistentSet;
import clojure.lang.ITransientSet;
import clojure.lang.PersistentHashSet;
import io.usethesource.criterion.api.JmhValue;
import io.usethesource.criterion.impl.AbstractSetBuilder;

final class ClojureSetWriter extends
    AbstractSetBuilder<JmhValue, ITransientSet> {

  /*
   * TODO: improve readability of code by wrapping casts
   */
  ClojureSetWriter() {
    super((ITransientSet) PersistentHashSet.EMPTY.asTransient(),
        set -> (item) -> (ITransientSet) set.conj(item),
        set -> new ClojureSet((IPersistentSet) set.persistent()));
  }

}
