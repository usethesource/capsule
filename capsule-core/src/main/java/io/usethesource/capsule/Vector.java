/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.capsule;

import java.util.Optional;

public interface Vector<K> {

  int size();

  Optional<K> get(int index);

  interface Immutable<K> extends Vector<K> {

    Vector.Immutable<K> pushFront(K item);

    Vector.Immutable<K> pushBack(K item);

    Vector.Immutable<K> take(int count);

    Vector.Immutable<K> drop(int count);

  }

  interface Transient<K> extends Vector<K> {

  }

}
