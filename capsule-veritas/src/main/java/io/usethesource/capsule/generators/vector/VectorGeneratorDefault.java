/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.capsule.generators.vector;

import io.usethesource.capsule.Vector;
import io.usethesource.capsule.core.PersistentTrieVector;

public class VectorGeneratorDefault<T extends Vector.Immutable> extends AbstractVectorGenerator<T> {

  public VectorGeneratorDefault() {
    super((Class<T>) PersistentTrieVector.class);
  }

}
