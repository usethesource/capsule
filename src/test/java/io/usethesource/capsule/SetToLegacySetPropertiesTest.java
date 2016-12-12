/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.capsule;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;

import io.usethesource.capsule.core.converter.SetToLegacySetConverter;

@RunWith(Suite.class)
@Suite.SuiteClasses({SetToLegacySetPropertiesTest.IntegerSetPropertiesTest.class,
    SetToLegacySetPropertiesTest.StringSetPropertiesTest.class})
public class SetToLegacySetPropertiesTest {

  @RunWith(JUnitQuickcheck.class)
  public static class IntegerSetPropertiesTest
      extends AbstractSetProperties<Integer, SetToLegacySetConverter<Integer>> {

    public IntegerSetPropertiesTest() {
      super(SetToLegacySetConverter.class);
    }
  }

  @RunWith(JUnitQuickcheck.class)
  public static class StringSetPropertiesTest
      extends AbstractSetProperties<String, SetToLegacySetConverter<String>> {

    public StringSetPropertiesTest() {
      super(SetToLegacySetConverter.class);
    }
  }

}
