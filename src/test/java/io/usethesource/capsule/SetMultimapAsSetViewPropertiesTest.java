/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.capsule;

import java.util.Map;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;

import io.usethesource.capsule.api.deprecated.ImmutableSetMultimapAsImmutableSetView;

@RunWith(Suite.class)
@Suite.SuiteClasses(SetMultimapAsSetViewPropertiesTest.PropertiesTest.class)
public class SetMultimapAsSetViewPropertiesTest {

  @RunWith(JUnitQuickcheck.class)
  public static class PropertiesTest extends
      AbstractSetProperties<Map.Entry<Integer, Integer>, ImmutableSetMultimapAsImmutableSetView<Integer, Integer, Map.Entry<Integer, Integer>>> {

    public PropertiesTest() {
      super(ImmutableSetMultimapAsImmutableSetView.class);
    }
  }

}
