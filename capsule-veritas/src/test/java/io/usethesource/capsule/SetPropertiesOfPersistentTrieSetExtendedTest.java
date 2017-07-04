/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.capsule;

import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;
import io.usethesource.capsule.core.PersistentTrieSetExtended;
import org.junit.runner.RunWith;

@RunWith(JUnitQuickcheck.class)
public class SetPropertiesOfPersistentTrieSetExtendedTest
    extends AbstractSetProperties<String, PersistentTrieSetExtended<String>> {

  public SetPropertiesOfPersistentTrieSetExtendedTest() {
    super(PersistentTrieSetExtended.class);
  }
}