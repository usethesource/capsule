/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.capsule;

import java.util.Collection;

import io.usethesource.capsule.experimental.multimap.TrieSetMultimap_HHAMT_Specialized_Path_Interlinked;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class SetMultimapSmokeTest {

  final static int size = 64;

  @Test
  public void testInsertTwoTuplesThatShareSameKey() {
    SetMultimap.Immutable<Integer, String> map =
        TrieSetMultimap_HHAMT_Specialized_Path_Interlinked.<Integer, String>of().__insert(1, "x")
            .__insert(1, "y");

    assertEquals(2, map.size());
    assertTrue(map.containsKey(1));
  }

  @Test
  public void testInsertTwoTuplesWithOneRemoveThatShareSameKeyX() {
    SetMultimap.Immutable<Integer, String> map = TrieSetMultimap_HHAMT_Specialized_Path_Interlinked
        .<Integer, String>of().__insert(1, "x").__insert(1, "y").__remove(1, "x");

    assertEquals(1, map.size());
    assertTrue(map.containsKey(1));
  }

  @Test
  public void testInsertTwoTuplesWithOneRemoveThatShareSameKeyY() {
    SetMultimap.Immutable<Integer, String> map = TrieSetMultimap_HHAMT_Specialized_Path_Interlinked
        .<Integer, String>of().__insert(1, "x").__insert(1, "y").__remove(1, "y");

    assertEquals(1, map.size());
    assertTrue(map.containsKey(1));
  }

  @Test
  public void testInsertTwoTuplesWithOneRemoveThatShareSameKeyXY() {
    SetMultimap.Immutable<Integer, String> map =
        TrieSetMultimap_HHAMT_Specialized_Path_Interlinked.<Integer, String>of().__insert(1, "x")
            .__insert(1, "y")
            .__remove(1, "x").__remove(1, "y");

    assertEquals(0, map.size());
    assertFalse(map.containsKey(1));
  }

  @Test
  public void testInsertTwoTuplesThatShareSameKey_Iterate() {
    SetMultimap.Immutable<Integer, String> map =
        TrieSetMultimap_HHAMT_Specialized_Path_Interlinked.<Integer, String>of().__insert(1, "x")
            .__insert(1, "y");

    Collection<String> values = map.values();

    assertEquals(2, values.size());
    assertTrue(values.contains("x"));
    assertTrue(values.contains("y"));
  }

}
