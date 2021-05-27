/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.capsule;

import java.util.Collection;

import com.pholser.junit.quickcheck.Property;
import com.pholser.junit.quickcheck.generator.Size;
import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

@RunWith(JUnitQuickcheck.class)
public class SetMultimapSmokeTest<K, V, CT extends SetMultimap.Immutable<K, V>> {

  @Property
  public void testInsertTwoTuplesThatShareSameKey(
      @Size(min = 0, max = 0) final SetMultimap.Immutable<Integer, String> emptyCollection) {

    SetMultimap.Immutable<Integer, String> map = emptyCollection
        .__insert(1, "x")
        .__insert(1, "y");

    assertEquals(2, map.size());
    assertTrue(map.containsKey(1));
  }

  @Property
  public void testInsertTwoTuplesWithOneRemoveThatShareSameKeyX(
      @Size(min = 0, max = 0) final SetMultimap.Immutable<Integer, String> emptyCollection) {

    SetMultimap.Immutable<Integer, String> map = emptyCollection
        .__insert(1, "x")
        .__insert(1, "y")
        .__remove(1, "x");

    assertEquals(1, map.size());
    assertTrue(map.containsKey(1));
  }

  @Property
  public void testInsertTwoTuplesWithOneRemoveThatShareSameKeyY(
      @Size(min = 0, max = 0) final SetMultimap.Immutable<Integer, String> emptyCollection) {

    SetMultimap.Immutable<Integer, String> map = emptyCollection
        .__insert(1, "x")
        .__insert(1, "y")
        .__remove(1, "y");

    assertEquals(1, map.size());
    assertTrue(map.containsKey(1));
  }

  @Property
  public void testInsertTwoTuplesWithOneRemoveThatShareSameKeyXY(
      @Size(min = 0, max = 0) final SetMultimap.Immutable<Integer, String> emptyCollection) {

    SetMultimap.Immutable<Integer, String> map = emptyCollection
        .__insert(1, "x")
        .__insert(1, "y")
        .__remove(1, "x").__remove(1, "y");

    assertEquals(0, map.size());
    assertFalse(map.containsKey(1));
  }

  @Property
  public void testInsertTwoTuplesThatShareSameKey_Iterate(
      @Size(min = 0, max = 0) final SetMultimap.Immutable<Integer, String> emptyCollection) {

    SetMultimap.Immutable<Integer, String> map = emptyCollection
        .__insert(1, "x")
        .__insert(1, "y");

    Collection<String> values = map.values();

    assertEquals(2, values.size());
    assertTrue(values.contains("x"));
    assertTrue(values.contains("y"));
  }

  @Property
  public void testHashCollisionReproduction(
      @Size(min = 0, max = 0) final SetMultimap.Immutable<Object, String> emptyCollection) {

    Object a = new Object() {
      public int hashCode() {
        return 0;
      }
    };

    Object b = new Object() {
      public int hashCode() {
        return 0;
      }
    };

    Object c = new Object() {
      public int hashCode() {
        return 0;
      }
    };

    final SetMultimap.Immutable<Object, String> map =
        emptyCollection.__insert(a, "x").__insert(b, "y");

    final SetMultimap.Immutable<Object, String> mapDuplicate =
        emptyCollection.__insert(a, "x").__insert(b, "y");

    final SetMultimap.Immutable<Object, String> mapDuplicateWithDifferentOrder =
        emptyCollection.__insert(b, "y").__insert(a, "x");

    final SetMultimap.Immutable<Object, String> mapDifferent =
        emptyCollection.__insert(a, "x").__insert(c, "z");

    assertEquals(map, mapDuplicate);
    assertEquals(map, mapDuplicateWithDifferentOrder);

    assertEquals(mapDuplicate, map);
    assertEquals(mapDuplicateWithDifferentOrder, map);

    assertNotEquals(map, mapDifferent);
    assertNotEquals(mapDifferent, map);
  }
}
