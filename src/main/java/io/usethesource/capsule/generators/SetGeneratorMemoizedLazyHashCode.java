/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.capsule.generators;

import io.usethesource.capsule.experimental.memoized.TrieSet_5Bits_Memoized_LazyHashCode;

@SuppressWarnings({"rawtypes"})
public class SetGeneratorMemoizedLazyHashCode<K>
    extends AbstractSetGenerator<TrieSet_5Bits_Memoized_LazyHashCode> {

  public SetGeneratorMemoizedLazyHashCode() {
    super(TrieSet_5Bits_Memoized_LazyHashCode.class);
  }

}
