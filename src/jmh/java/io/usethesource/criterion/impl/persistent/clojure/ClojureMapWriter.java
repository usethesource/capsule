/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.criterion.impl.persistent.clojure;

import clojure.lang.ITransientMap;
import clojure.lang.PersistentHashMap;
import io.usethesource.criterion.api.JmhValue;
import io.usethesource.criterion.impl.AbstractMapBuilder;

final class ClojureMapWriter extends
    AbstractMapBuilder<JmhValue, ITransientMap> {

  ClojureMapWriter() {
    super(PersistentHashMap.EMPTY.asTransient(), set -> set::assoc,
        set -> new ClojureMap(set.persistent()));
  }

}