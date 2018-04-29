/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.capsule.generators.map;

import io.usethesource.capsule.Map;
import io.usethesource.capsule.util.collection.AbstractSpecialisedImmutableMap;

public class SpecializedMapGenerator<T extends Map.Immutable> extends AbstractMapGenerator<T> {

  public SpecializedMapGenerator() {
    super((Class<T>) AbstractSpecialisedImmutableMap.class);
  }

}
