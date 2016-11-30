/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.capsule.generators;

import io.usethesource.capsule.experimental.lazy.TrieSet_5Bits_LazyHashCode;

@SuppressWarnings({"rawtypes"})
public class SetGeneratorLazyHashCode<K> extends AbstractSetGenerator<TrieSet_5Bits_LazyHashCode> {

  public SetGeneratorLazyHashCode() {
    super(TrieSet_5Bits_LazyHashCode.class);
  }

}
