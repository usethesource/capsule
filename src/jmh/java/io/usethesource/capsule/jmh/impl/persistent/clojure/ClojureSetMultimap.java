/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.capsule.jmh.impl.persistent.clojure;

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import clojure.lang.APersistentMap;
import clojure.lang.IPersistentMap;
import clojure.lang.IPersistentSet;
import clojure.lang.PersistentHashSet;
import io.usethesource.capsule.jmh.api.JmhSetMultimap;
import io.usethesource.capsule.jmh.api.JmhValue;

import static io.usethesource.capsule.util.collection.AbstractSpecialisedImmutableMap.entryOf;

public class ClojureSetMultimap implements JmhSetMultimap {

  protected final IPersistentMap xs;

  protected ClojureSetMultimap(IPersistentMap xs) {
    this.xs = xs;
  }

  @Override
  public boolean isEmpty() {
    return size() == 0;
  }

  @Override
  public int size() {
    return xs.count(); // TODO: is unique keySet size instead of entrySet size
  }

  @Override
  public JmhSetMultimap insert(JmhValue key, JmhValue value) {
    Object singletonOrSet = xs.valAt(key);

    if (singletonOrSet == null) {
      return new ClojureSetMultimap(xs.assoc(key, value));
    } else if (singletonOrSet instanceof IPersistentSet) {
      IPersistentSet set = (IPersistentSet) singletonOrSet;
      return new ClojureSetMultimap(xs.assoc(key, set.cons(value)));
    } else if (singletonOrSet.equals(value)) {
      return this;
    } else {
      IPersistentSet set = PersistentHashSet.create(singletonOrSet, value);
      return new ClojureSetMultimap(xs.assoc(key, set));
    }
  }

  @Override
  public JmhSetMultimap put(JmhValue key, JmhValue value) {
    return new ClojureSetMultimap(xs.assoc(key, value));
  }

  @Override
  public JmhSetMultimap remove(JmhValue key) {
    return new ClojureSetMultimap(xs.without(key));
  }

  @Override
  public JmhSetMultimap remove(JmhValue key, JmhValue value) {
    Object singletonOrSet = xs.valAt(key);

    if (singletonOrSet == null) {
      return this;
    } else if (singletonOrSet instanceof IPersistentSet) {
      IPersistentSet oldSet = (IPersistentSet) singletonOrSet;
      IPersistentSet newSet = oldSet.disjoin(value);

      switch (newSet.count()) {
        case 0:
          return new ClojureSetMultimap(xs.without(key));
        case 1:
          return new ClojureSetMultimap(xs.assoc(key, newSet.seq().first()));
        default:
          return new ClojureSetMultimap(xs.assoc(key, newSet));
      }
    } else {
      if (singletonOrSet.equals(value)) {
        return new ClojureSetMultimap(xs.without(key));
      } else {
        return this;
      }
    }
  }

  // @Override
  // public JmhMap removeKey(JmhValue key) {
  // return new ClojureSetMultimap((IPersistentMap) xs.without(key));
  // }

  // @Override
  // public JmhValue get(JmhValue key) {
  // return (JmhValue) xs.valAt(key);
  // }

  @Override
  public boolean containsKey(JmhValue key) {
    return xs.containsKey(key);
  }

  @Override
  public boolean containsValue(JmhValue value) {
    return ((APersistentMap) xs).values().contains(value);
  }

  @Override
  public boolean contains(JmhValue key, JmhValue value) {
    Object singletonOrSet = xs.valAt(key);

    if (singletonOrSet == null) {
      return false;
    } else if (singletonOrSet instanceof IPersistentSet) {
      IPersistentSet set = (IPersistentSet) singletonOrSet;
      return set.contains(value);
    } else {
      return singletonOrSet.equals(value);
    }
  }

  // @Override
  // public boolean containsValue(JmhValue value) {
  // return ((APersistentMap) xs).containsValue(value);
  // }

  @Override
  public java.util.Set keySet() {
    return ((APersistentMap) xs).keySet();
  }

  @Override
  public int hashCode() {
    return xs.hashCode();
  }

  @Override
  public boolean equals(Object other) {
    if (other == this) {
      return true;
    }
    if (other == null) {
      return false;
    }

    if (other instanceof ClojureSetMultimap) {
      ClojureSetMultimap that = (ClojureSetMultimap) other;

      if (this.size() != that.size()) {
        return false;
      }

      return xs.equals(that.xs);
    }

    return false;
  }

  @Override
  public Object unwrap() {
    return xs;
  }

  @SuppressWarnings("unchecked")
  @Override
  public Iterator<JmhValue> iterator() {
    return ((APersistentMap) xs).keySet().iterator();
  }

  // @SuppressWarnings("unchecked")
  // @Override
  // public Iterator<JmhValue> valueIterator() {
  // return ((APersistentMap) xs).values().iterator();
  // }

  @SuppressWarnings("unchecked")
  @Override
  public Iterator<Entry<JmhValue, Object>> nativeEntryIterator() {
    return ((APersistentMap) xs).entrySet().iterator();
  }

  @SuppressWarnings("unchecked")
  @Override
  public Iterator<Entry<JmhValue, JmhValue>> entryIterator() {
    Iterator<Entry<JmhValue, Object>> it = ((APersistentMap) xs).entrySet().iterator();
    return new FlatteningIterator(it);
  }

  private static class FlatteningIterator implements Iterator<Map.Entry<JmhValue, JmhValue>> {

    final Iterator<Entry<JmhValue, Object>> entryIterator;

    JmhValue lastKey = null;
    Iterator<JmhValue> lastIterator = Collections.emptyIterator();

    public FlatteningIterator(Iterator<Entry<JmhValue, Object>> entryIterator) {
      this.entryIterator = entryIterator;
    }

    @Override
    public boolean hasNext() {
      if (lastIterator.hasNext()) {
        return true;
      } else {
        return entryIterator.hasNext();
      }
    }

    @SuppressWarnings("unchecked")
    @Override
    public Entry<JmhValue, JmhValue> next() {
      assert hasNext();

      if (lastIterator.hasNext()) {
        return entryOf(lastKey, lastIterator.next());
      } else {
        lastKey = null;

        Entry<JmhValue, Object> nextEntry = entryIterator.next();

        Object singletonOrSet = nextEntry.getValue();

        if (singletonOrSet instanceof IPersistentSet) {
          IPersistentSet set = (IPersistentSet) singletonOrSet;

          lastKey = nextEntry.getKey();
          lastIterator = ((Iterable<JmhValue>) set).iterator();

          return entryOf(lastKey, lastIterator.next());
        } else {
          return (Map.Entry<JmhValue, JmhValue>) (Object) nextEntry;
        }
      }
    }

  }

  // @Override
  // public Iterator<Entry<JmhValue, JmhValue>> entryIterator() {
  // return untypedEntryStream().flatMap(ClojureSetMultimap::dispatchOnTypeAndFlatten)
  // .iterator();
  // }
  //
  // @SuppressWarnings("unchecked")
  // private Stream<Entry<JmhValue, Object>> untypedEntryStream() {
  // int size = xs.count();
  // Iterator<Entry<JmhValue, Object>> it = ((APersistentMap) xs).entrySet().iterator();
  //
  // Spliterator<Entry<JmhValue, Object>> split = Spliterators.spliterator(it, size,
  // Spliterator.NONNULL | Spliterator.SIZED | Spliterator.SUBSIZED);
  //
  // return StreamSupport.stream(split, false);
  // }
  //
  // @SuppressWarnings("unchecked")
  // private static Stream<Entry<JmhValue, JmhValue>> dispatchOnTypeAndFlatten(
  // Entry<JmhValue, Object> tuple) {
  // Object singletonOrSet = tuple.getValue();
  //
  // if (singletonOrSet instanceof IPersistentSet) {
  // IPersistentSet set = (IPersistentSet) singletonOrSet;
  //
  // Iterator<Entry<JmhValue, JmhValue>> it = new MultimapEntryToMapEntriesIterator(
  // tuple.getKey(), ((Iterable<JmhValue>) set).iterator());
  //
  // Spliterator<Entry<JmhValue, JmhValue>> split = Spliterators.spliterator(it, set.count(),
  // Spliterator.NONNULL | Spliterator.SIZED | Spliterator.SUBSIZED);
  //
  // return StreamSupport.stream(split, false);
  // } else {
  // return Stream.of((Map.Entry<JmhValue, JmhValue>) (Object) tuple);
  // }
  // }
  //
  // private static class MultimapEntryToMapEntriesIterator
  // implements Iterator<Map.Entry<JmhValue, JmhValue>> {
  //
  // final JmhValue key;
  // final Iterator<JmhValue> valueIterator;
  //
  // public MultimapEntryToMapEntriesIterator(JmhValue key, Iterator<JmhValue> valueIterator) {
  // this.key = key;
  // this.valueIterator = valueIterator;
  // }
  //
  // @Override
  // public boolean hasNext() {
  // return valueIterator.hasNext();
  // }
  //
  // @Override
  // public Entry<JmhValue, JmhValue> next() {
  // return entryOf(key, valueIterator.next());
  // }
  //
  // }

}
