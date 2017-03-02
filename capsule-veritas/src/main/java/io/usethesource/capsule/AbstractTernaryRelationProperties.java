/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.capsule;

import io.usethesource.capsule.api.TernaryRelation;
import io.usethesource.capsule.api.Triple;

public abstract class AbstractTernaryRelationProperties<T, U, V> extends
    AbstractSetProperties<Triple<T, U, V>, TernaryRelation.Immutable<T, U, V, Triple<T, U, V>>> {

  public AbstractTernaryRelationProperties(Class<?> type) {
    super(type);
  }

}
