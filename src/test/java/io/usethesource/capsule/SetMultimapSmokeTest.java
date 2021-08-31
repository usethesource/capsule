/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.capsule;

import java.util.Collection;
import java.util.Iterator;

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
      @Size(max = 0) final SetMultimap.Immutable<Integer, String> emptyCollection) {

    SetMultimap.Immutable<Integer, String> map = emptyCollection
        .__insert(1, "x")
        .__insert(1, "y");

    assertEquals(2, map.size());
    assertTrue(map.containsKey(1));
  }

  @Property
  public void testInsertTwoTuplesWithOneRemoveThatShareSameKeyX(
      @Size(max = 0) final SetMultimap.Immutable<Integer, String> emptyCollection) {

    SetMultimap.Immutable<Integer, String> map = emptyCollection
        .__insert(1, "x")
        .__insert(1, "y")
        .__remove(1, "x");

    assertEquals(1, map.size());
    assertTrue(map.containsKey(1));
  }

  @Property
  public void testInsertTwoTuplesWithOneRemoveThatShareSameKeyY(
      @Size(max = 0) final SetMultimap.Immutable<Integer, String> emptyCollection) {

    SetMultimap.Immutable<Integer, String> map = emptyCollection
        .__insert(1, "x")
        .__insert(1, "y")
        .__remove(1, "y");

    assertEquals(1, map.size());
    assertTrue(map.containsKey(1));
  }

  @Property
  public void testInsertTwoTuplesWithOneRemoveThatShareSameKeyXY(
      @Size(max = 0) final SetMultimap.Immutable<Integer, String> emptyCollection) {

    SetMultimap.Immutable<Integer, String> map = emptyCollection
        .__insert(1, "x")
        .__insert(1, "y")
        .__remove(1, "x").__remove(1, "y");

    assertEquals(0, map.size());
    assertFalse(map.containsKey(1));
  }

  @Property
  public void testInsertTwoTuplesThatShareSameKey_Iterate(
      @Size(max = 0) final SetMultimap.Immutable<Integer, String> emptyCollection) {

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
      @Size(max = 0) final SetMultimap.Immutable<Object, String> emptyCollection) {

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

  private int size(Iterator<?> iterator) {
    int size = 0;

    while (iterator.hasNext()) {
      size++;
      iterator.next();
    }

    return size;
  }

  @Property
  public void testHashCollisionPut(
      @Size(max = 0) final io.usethesource.capsule.core.PersistentTrieSetMultimap<Object, Integer> emptyCollection) { // TODO generic type `SetMultimap.Immutable` not applicable, since `PersistentBidirectionalTrieSetMultimap` doesn't support `__put(K key, Set.Immutable<V> values)`

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

    final SetMultimap.Immutable<Object, Integer> mapWithBucketsOfSize3 = emptyCollection
        .__insert(a, 1).__insert(a, 2).__insert(a, 3)
        .__insert(b, 4).__insert(b, 5).__insert(b, 6)
        .__insert(c, 7).__insert(c, 8).__insert(c, 9);

    assertEquals(3, mapWithBucketsOfSize3.sizeDistinct());
    assertEquals(9, mapWithBucketsOfSize3.size());
    assertEquals(3, size(mapWithBucketsOfSize3.nativeEntryIterator()));
    assertEquals(9, size(mapWithBucketsOfSize3.entryIterator()));

    final SetMultimap.Immutable<Object, Integer> mapWithBucketsOfSize1 = mapWithBucketsOfSize3
        .__put(a, 0)
        .__put(b, 0)
        .__put(c, 0);

    assertEquals(3, mapWithBucketsOfSize1.sizeDistinct());
    assertEquals(3, mapWithBucketsOfSize1.size());
    assertEquals(3, size(mapWithBucketsOfSize1.nativeEntryIterator()));
    assertEquals(3, size(mapWithBucketsOfSize1.entryIterator()));

    final SetMultimap.Immutable<Object, Integer> mapWithBucketsOfSize2 = mapWithBucketsOfSize3
        .__put(a, Set.Immutable.of(1, 2))
        .__put(b, Set.Immutable.of(4, 5))
        .__put(c, Set.Immutable.of(7, 8));

    assertEquals(3, mapWithBucketsOfSize2.sizeDistinct());
    assertEquals(6, mapWithBucketsOfSize2.size());
    assertEquals(3, size(mapWithBucketsOfSize2.nativeEntryIterator()));
    assertEquals(6, size(mapWithBucketsOfSize2.entryIterator()));
  }

  @Property
  public void testHashCollisionInsert(
      @Size(max = 0) final SetMultimap.Immutable<Object, Integer> emptyCollection) {

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

    final SetMultimap.Immutable<Object, Integer> mapWithBucketsOfSize1 = emptyCollection
        .__insert(a, 1)
        .__insert(b, 4)
        .__insert(c, 7);

    assertEquals(3, mapWithBucketsOfSize1.sizeDistinct());
    assertEquals(3, mapWithBucketsOfSize1.size());
    assertEquals(3, size(mapWithBucketsOfSize1.nativeEntryIterator()));
    assertEquals(3, size(mapWithBucketsOfSize1.entryIterator()));

    final SetMultimap.Immutable<Object, Integer> mapWithBucketsOfSize2 = mapWithBucketsOfSize1
        .__insert(a, Set.Immutable.of(2))
        .__insert(b, Set.Immutable.of(5))
        .__insert(c, Set.Immutable.of(8));

    assertEquals(3, mapWithBucketsOfSize2.sizeDistinct());
    assertEquals(6, mapWithBucketsOfSize2.size());
    assertEquals(3, size(mapWithBucketsOfSize2.nativeEntryIterator()));
    assertEquals(6, size(mapWithBucketsOfSize2.entryIterator()));

    final SetMultimap.Immutable<Object, Integer> mapWithBucketsOfSize3 = mapWithBucketsOfSize1
        .__insert(a, Set.Immutable.of(2, 3))
        .__insert(b, Set.Immutable.of(5, 6))
        .__insert(c, Set.Immutable.of(8, 9));

    assertEquals(3, mapWithBucketsOfSize3.sizeDistinct());
    assertEquals(9, mapWithBucketsOfSize3.size());
    assertEquals(3, size(mapWithBucketsOfSize3.nativeEntryIterator()));
    assertEquals(9, size(mapWithBucketsOfSize3.entryIterator()));
  }
}
