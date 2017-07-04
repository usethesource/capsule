/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.capsule.generators.set;

import io.usethesource.capsule.Set;
import io.usethesource.capsule.core.PersistentTrieSetExtended;

public class SetGeneratorDefaultExtended<T extends Set.Immutable> extends AbstractSetGenerator<T> {

  public SetGeneratorDefaultExtended() {
    super((Class<T>) PersistentTrieSetExtended.class);
  }

}
