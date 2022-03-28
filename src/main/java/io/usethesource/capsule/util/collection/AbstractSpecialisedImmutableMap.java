/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.capsule.util.collection;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;

import io.usethesource.capsule.util.iterator.EmptySupplierIterator;
import io.usethesource.capsule.util.iterator.SupplierIterator;

public abstract class AbstractSpecialisedImmutableMap<K, V>
    implements io.usethesource.capsule.Map.Immutable<K, V>, java.lang.Cloneable, java.io.Serializable {

  private static io.usethesource.capsule.Map.Immutable EMPTY_MAP = new Map0();

  // add method required for property-based test suite
  private static <K, V> io.usethesource.capsule.Map.Immutable<K, V> of() {
    return EMPTY_MAP;
  }

  public static <K, V> io.usethesource.capsule.Map.Immutable<K, V> mapOf() {
    return EMPTY_MAP;
  }

  public static final <K, V> Map.Entry<K, V> entryOf(final K key, final V val) {
    return new MapEntry<K, V>(key, val);
  }

  public static <K, V> io.usethesource.capsule.Map.Immutable<K, V> mapOf(K key1, V val1) {
    return new Map1<K, V>(key1, val1);
  }

  public static <K, V> io.usethesource.capsule.Map.Immutable<K, V> mapOf(K key1, V val1, K key2,
      V val2) {
    return new Map2<K, V>(key1, val1, key2, val2);
  }

  public static <K, V> io.usethesource.capsule.Map.Immutable<K, V> mapOf(K key1, V val1, K key2,
      V val2, K key3, V val3) {
    return new Map3<K, V>(key1, val1, key2, val2, key3, val3);
  }

  public static <K, V> io.usethesource.capsule.Map.Immutable<K, V> mapOf(K key1, V val1, K key2,
      V val2, K key3, V val3,
      K key4, V val4) {
    return new Map4<K, V>(key1, val1, key2, val2, key3, val3, key4, val4);
  }

  public static <K, V> io.usethesource.capsule.Map.Immutable<K, V> mapOf(K key1, V val1, K key2,
      V val2, K key3, V val3,
      K key4, V val4, K key5, V val5) {
    return new Map5<K, V>(key1, val1, key2, val2, key3, val3, key4, val4, key5, val5);
  }

  public static <K, V> io.usethesource.capsule.Map.Immutable<K, V> mapOf(K key1, V val1, K key2,
      V val2, K key3, V val3,
      K key4, V val4, K key5, V val5, K key6, V val6) {
    final io.usethesource.capsule.Map.Transient<K, V> tmp = io.usethesource.capsule.Map.
        Transient.of(key1, val1, key2, val2, key3, val3,
        key4, val4, key5, val5, key6, val6);
    return tmp.freeze();
  }

  public static <K, V> io.usethesource.capsule.Map.Immutable<K, V> mapOf(Map<K, V> map) {
    if (map instanceof io.usethesource.capsule.Map.Immutable) {
      return (io.usethesource.capsule.Map.Immutable<K, V>) map;
    } else {
      final io.usethesource.capsule.Map.Transient<K, V> tmp = io.usethesource.capsule.Map.
          Transient.of();
      tmp.__putAll(map);
      return tmp.freeze();
    }
  }

  @Override
  public V remove(Object key) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void clear() {
    throw new UnsupportedOperationException();
  }

  @Override
  public V put(K key, V value) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void putAll(Map<? extends K, ? extends V> m) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean isEmpty() {
    return size() == 0;
  }

  @Override
  public boolean equals(final Object other) {
    if (other == this) {
      return true;
    }
    if (other == null) {
      return false;
    }

    if (other instanceof Map) {
      try {
        Map<K, V> that = (Map<K, V>) other;

        if (this.size() == that.size()) {
          for (Entry<K, V> e : that.entrySet()) {
            if (!this.containsKey(e.getKey())) {
              return false;
            }
            if (!Objects.equals(e.getValue(), this.get(e.getKey()))) {
              return false;
            }
          }
          return true;
        }
      } catch (ClassCastException unused) {
        return false;
      }
    }

    return false;
  }

  @Override
  public Iterator<V> valueIterator() {
    return values().iterator();
  }

  @Override
  public Iterator<java.util.Map.Entry<K, V>> entryIterator() {
    return entrySet().iterator();
  }

  @Override
  public io.usethesource.capsule.Map.Immutable<K, V> __putAll(
      Map<? extends K, ? extends V> map) {
    io.usethesource.capsule.Map.Transient<K, V> tmp = asTransient();
    if (tmp.__putAll(map)) {
      return tmp.freeze();
    } else {
      return this;
    }
  }

  @Override
  public Object clone() throws CloneNotSupportedException {
    return super.clone();
  }

  @Override
  public boolean isTransientSupported() {
    return true;
  }
}


class MapEntry<K, V> implements java.util.Map.Entry<K, V>, java.lang.Cloneable, java.io.Serializable {

  private final K key1;
  private final V val1;

  MapEntry(final K key1, final V val1) {
    this.key1 = key1;
    this.val1 = val1;
  }

  @Override
  public K getKey() {
    return key1;
  }

  @Override
  public V getValue() {
    return val1;
  }

  @Override
  public V setValue(V value) {
    throw new UnsupportedOperationException();
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(key1) ^ Objects.hashCode(val1);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    MapEntry<?, ?> mapEntry = (MapEntry<?, ?>) o;
    return Objects.equals(key1, mapEntry.key1) && Objects.equals(val1, mapEntry.val1);
  }

  @Override
  public String toString() {
    return String.format("<%s, %s>", key1, val1);
  }

  @Override
  public Object clone() throws CloneNotSupportedException {
    return super.clone();
  }

}


class Map0<K, V> extends AbstractSpecialisedImmutableMap<K, V> {

  Map0() {

  }

  @Override
  public boolean containsKey(Object key) {
    return false;
  }

  @Override
  public boolean containsValue(Object val) {
    return false;
  }

  @Override
  public V get(Object key) {
    return null;
  }

  @Override
  public int size() {
    return 0;
  }

  @Override
  public Set<Entry<K, V>> entrySet() {
    return Collections.emptySet();
  }

  @Override
  public Set<K> keySet() {
    return Collections.emptySet();
  }

  @Override
  public Collection<V> values() {
    return Collections.emptySet();
  }

  @Override
  public SupplierIterator<K, V> keyIterator() {
    return EmptySupplierIterator.emptyIterator();
  }

  @Override
  public io.usethesource.capsule.Map.Immutable<K, V> __put(K key, V val) {
    return mapOf(key, val);
  }

  @Override
  public io.usethesource.capsule.Map.Immutable<K, V> __remove(K key) {
    return this;
  }

  @Override
  public io.usethesource.capsule.Map.Transient<K, V> asTransient() {
    return io.usethesource.capsule.Map.Transient.of();
  }

  @Override
  public int hashCode() {
    return 0;
  }

  @Override
  public String toString() {
    return "{}";
  }

}


class Map1<K, V> extends AbstractSpecialisedImmutableMap<K, V> {

  private final K key1;
  private final V val1;

  Map1(final K key1, final V val1) {
    this.key1 = key1;
    this.val1 = val1;
  }

  @Override
  public boolean containsKey(Object key) {
    if (key.equals(key1)) {
      return true;
    } else {
      return false;
    }
  }

  @Override
  public boolean containsValue(Object val) {
    if (val.equals(val1)) {
      return true;
    } else {
      return false;
    }
  }

  @Override
  public V get(Object key) {
    if (key.equals(key1)) {
      return val1;
    } else {
      return null;
    }
  }

  @Override
  public int size() {
    return 1;
  }

  @Override
  public Set<Entry<K, V>> entrySet() {
    return Collections.singleton(entryOf(key1, val1));
  }

  @Override
  public Set<K> keySet() {
    return Collections.singleton(key1);
  }

  @Override
  public Collection<V> values() {
    return Collections.singleton(val1);
  }

  @Override
  public SupplierIterator<K, V> keyIterator() {
    return new SupplierIterator<K, V>() {
      int cursor = 1;
      boolean hasGet;

      @Override
      public boolean hasNext() {
        return cursor <= Map1.this.size();
      }

      @Override
      public K next() {
        switch (cursor++) {
          case 1:
            return key1;
          default:
            throw new IllegalStateException();
        }
      }

      @Override
      public V get() {
        if (hasGet) {
          hasGet = false;

          switch (cursor) {
            case 1:
              return val1;
            default:
              throw new IllegalStateException();
          }
        } else {
          throw new NoSuchElementException();
        }
      }

      @Override
      public void remove() {
        throw new UnsupportedOperationException();
      }
    };

  }

  @Override
  public io.usethesource.capsule.Map.Immutable<K, V> __put(K key, V val) {
    if (key.equals(key1)) {
      return mapOf(key, val);
    } else {
      return mapOf(key1, val1, key, val);
    }
  }

  @Override
  public io.usethesource.capsule.Map.Immutable<K, V> __remove(K key) {
    if (key.equals(key1)) {
      return mapOf();
    } else {
      return this;
    }
  }

  @Override
  public io.usethesource.capsule.Map.Transient<K, V> asTransient() {
    return io.usethesource.capsule.Map.Transient.of(key1, val1);
  }

  @Override
  public int hashCode() {
    return ((Objects.hashCode(key1) ^ Objects.hashCode(val1)));
  }

  @Override
  public String toString() {
    return String.format("{%s=%s}", key1, val1);
  }

}


class Map2<K, V> extends AbstractSpecialisedImmutableMap<K, V> {

  private final K key1;
  private final V val1;

  private final K key2;
  private final V val2;

  Map2(final K key1, final V val1, final K key2, final V val2) {
    if (key1.equals(key2)) {
      throw new IllegalArgumentException("Duplicate keys are not allowed in specialised map.");
    }

    this.key1 = key1;
    this.val1 = val1;

    this.key2 = key2;
    this.val2 = val2;
  }

  @Override
  public boolean containsKey(Object key) {
    if (key.equals(key1)) {
      return true;
    } else if (key.equals(key2)) {
      return true;
    } else {
      return false;
    }
  }

  @Override
  public boolean containsValue(Object val) {
    if (val.equals(val1)) {
      return true;
    } else if (val.equals(val2)) {
      return true;
    } else {
      return false;
    }
  }

  @Override
  public V get(Object key) {
    if (key.equals(key1)) {
      return val1;
    } else if (key.equals(key2)) {
      return val2;
    } else {
      return null;
    }
  }

  @Override
  public int size() {
    return 2;
  }

  @Override
  public Set<Entry<K, V>> entrySet() {
    return AbstractSpecialisedImmutableSet.<Map.Entry<K, V>>setOf(entryOf(key1, val1),
        entryOf(key2, val2));
  }

  @Override
  public Set<K> keySet() {
    return AbstractSpecialisedImmutableSet.setOf(key1, key2);
  }

  @Override
  public Collection<V> values() {
    // TODO: return immutable or persistent --not only unmodifiable-- listOf(...)
    return Collections.unmodifiableList(Arrays.asList(val1, val2));
  }

  @Override
  public SupplierIterator<K, V> keyIterator() {
    return new SupplierIterator<K, V>() {
      int cursor = 1;
      boolean hasGet;

      @Override
      public boolean hasNext() {
        return cursor <= Map2.this.size();
      }

      @Override
      public K next() {
        switch (cursor++) {
          case 1:
            return key1;
          case 2:
            return key2;
          default:
            throw new IllegalStateException();
        }
      }

      @Override
      public V get() {
        if (hasGet) {
          hasGet = false;

          switch (cursor) {
            case 1:
              return val1;
            case 2:
              return val2;
            default:
              throw new IllegalStateException();
          }
        } else {
          throw new NoSuchElementException();
        }
      }

      @Override
      public void remove() {
        throw new UnsupportedOperationException();
      }
    };

  }

  @Override
  public io.usethesource.capsule.Map.Immutable<K, V> __put(K key, V val) {
    if (key.equals(key1)) {
      return mapOf(key, val, key2, val2);
    } else if (key.equals(key2)) {
      return mapOf(key1, val1, key, val);
    } else {
      return mapOf(key1, val1, key2, val2, key, val);
    }
  }

  @Override
  public io.usethesource.capsule.Map.Immutable<K, V> __remove(K key) {
    if (key.equals(key1)) {
      return mapOf(key2, val2);
    } else if (key.equals(key2)) {
      return mapOf(key1, val1);
    } else {
      return this;
    }
  }

  @Override
  public io.usethesource.capsule.Map.Transient<K, V> asTransient() {
    return io.usethesource.capsule.Map.Transient.of(key1, val1, key2, val2);
  }

  @Override
  public int hashCode() {
    return ((Objects.hashCode(key1) ^ Objects.hashCode(val1))
        + (Objects.hashCode(key2) ^ Objects.hashCode(val2)));
  }

  @Override
  public String toString() {
    return String.format("{%s=%s, %s=%s}", key1, val1, key2, val2);
  }

}


class Map3<K, V> extends AbstractSpecialisedImmutableMap<K, V> {

  private final K key1;
  private final V val1;

  private final K key2;
  private final V val2;

  private final K key3;
  private final V val3;

  Map3(final K key1, final V val1, final K key2, final V val2, final K key3, final V val3) {
    if (key1.equals(key2) || key1.equals(key3) || key2.equals(key3)) {
      throw new IllegalArgumentException("Duplicate keys are not allowed in specialised map.");
    }

    this.key1 = key1;
    this.val1 = val1;

    this.key2 = key2;
    this.val2 = val2;

    this.key3 = key3;
    this.val3 = val3;
  }

  @Override
  public boolean containsKey(Object key) {
    if (key.equals(key1)) {
      return true;
    } else if (key.equals(key2)) {
      return true;
    } else if (key.equals(key3)) {
      return true;
    } else {
      return false;
    }
  }

  @Override
  public boolean containsValue(Object val) {
    if (val.equals(val1)) {
      return true;
    } else if (val.equals(val2)) {
      return true;
    } else if (val.equals(val3)) {
      return true;
    } else {
      return false;
    }
  }

  @Override
  public V get(Object key) {
    if (key.equals(key1)) {
      return val1;
    } else if (key.equals(key2)) {
      return val2;
    } else if (key.equals(key3)) {
      return val3;
    } else {
      return null;
    }
  }

  @Override
  public int size() {
    return 3;
  }

  @Override
  public Set<Entry<K, V>> entrySet() {
    return AbstractSpecialisedImmutableSet.<Map.Entry<K, V>>setOf(entryOf(key1, val1),
        entryOf(key2, val2), entryOf(key3, val3));
  }

  @Override
  public Set<K> keySet() {
    return AbstractSpecialisedImmutableSet.setOf(key1, key2, key3);
  }

  @Override
  public Collection<V> values() {
    // TODO: return immutable or persistent --not only unmodifiable-- listOf(...)
    return Collections.unmodifiableList(Arrays.asList(val1, val2, val3));
  }

  @Override
  public SupplierIterator<K, V> keyIterator() {
    return new SupplierIterator<K, V>() {
      int cursor = 1;
      boolean hasGet;

      @Override
      public boolean hasNext() {
        return cursor <= Map3.this.size();
      }

      @Override
      public K next() {
        switch (cursor++) {
          case 1:
            return key1;
          case 2:
            return key2;
          case 3:
            return key3;
          default:
            throw new IllegalStateException();
        }
      }

      @Override
      public V get() {
        if (hasGet) {
          hasGet = false;

          switch (cursor) {
            case 1:
              return val1;
            case 2:
              return val2;
            case 3:
              return val3;
            default:
              throw new IllegalStateException();
          }
        } else {
          throw new NoSuchElementException();
        }
      }

      @Override
      public void remove() {
        throw new UnsupportedOperationException();
      }
    };

  }

  @Override
  public io.usethesource.capsule.Map.Immutable<K, V> __put(K key, V val) {
    if (key.equals(key1)) {
      return mapOf(key, val, key2, val2, key3, val3);
    } else if (key.equals(key2)) {
      return mapOf(key1, val1, key, val, key3, val3);
    } else if (key.equals(key3)) {
      return mapOf(key1, val1, key2, val2, key, val);
    } else {
      return mapOf(key1, val1, key2, val2, key3, val3, key, val);
    }
  }

  @Override
  public io.usethesource.capsule.Map.Immutable<K, V> __remove(K key) {
    if (key.equals(key1)) {
      return mapOf(key2, val2, key3, val3);
    } else if (key.equals(key2)) {
      return mapOf(key1, val1, key3, val3);
    } else if (key.equals(key3)) {
      return mapOf(key1, val1, key2, val2);
    } else {
      return this;
    }
  }

  @Override
  public io.usethesource.capsule.Map.Transient<K, V> asTransient() {
    return io.usethesource.capsule.Map.Transient.of(key1, val1, key2, val2, key3, val3);
  }

  @Override
  public int hashCode() {
    return ((Objects.hashCode(key1) ^ Objects.hashCode(val1))
        + (Objects.hashCode(key2) ^ Objects.hashCode(val2))
        + (Objects.hashCode(key3) ^ Objects.hashCode(val3)));
  }

  @Override
  public String toString() {
    return String.format("{%s=%s, %s=%s, %s=%s}", key1, val1, key2, val2, key3, val3);
  }

}


class Map4<K, V> extends AbstractSpecialisedImmutableMap<K, V> {

  private final K key1;
  private final V val1;

  private final K key2;
  private final V val2;

  private final K key3;
  private final V val3;

  private final K key4;
  private final V val4;

  Map4(final K key1, final V val1, final K key2, final V val2, final K key3, final V val3,
      final K key4, final V val4) {
    if (key1.equals(key2) || key1.equals(key3) || key1.equals(key4) || key2.equals(key3)
        || key2.equals(key4) || key3.equals(key4)) {
      throw new IllegalArgumentException("Duplicate keys are not allowed in specialised map.");
    }

    this.key1 = key1;
    this.val1 = val1;

    this.key2 = key2;
    this.val2 = val2;

    this.key3 = key3;
    this.val3 = val3;

    this.key4 = key4;
    this.val4 = val4;
  }

  @Override
  public boolean containsKey(Object key) {
    if (key.equals(key1)) {
      return true;
    } else if (key.equals(key2)) {
      return true;
    } else if (key.equals(key3)) {
      return true;
    } else if (key.equals(key4)) {
      return true;
    } else {
      return false;
    }
  }

  @Override
  public boolean containsValue(Object val) {
    if (val.equals(val1)) {
      return true;
    } else if (val.equals(val2)) {
      return true;
    } else if (val.equals(val3)) {
      return true;
    } else if (val.equals(val4)) {
      return true;
    } else {
      return false;
    }
  }

  @Override
  public V get(Object key) {
    if (key.equals(key1)) {
      return val1;
    } else if (key.equals(key2)) {
      return val2;
    } else if (key.equals(key3)) {
      return val3;
    } else if (key.equals(key4)) {
      return val4;
    } else {
      return null;
    }
  }

  @Override
  public int size() {
    return 4;
  }

  @Override
  public Set<Entry<K, V>> entrySet() {
    return AbstractSpecialisedImmutableSet.<Map.Entry<K, V>>setOf(entryOf(key1, val1),
        entryOf(key2, val2), entryOf(key3, val3), entryOf(key4, val4));
  }

  @Override
  public Set<K> keySet() {
    return AbstractSpecialisedImmutableSet.setOf(key1, key2, key3, key4);
  }

  @Override
  public Collection<V> values() {
    // TODO: return immutable or persistent --not only unmodifiable-- listOf(...)
    return Collections.unmodifiableList(Arrays.asList(val1, val2, val3, val4));
  }

  @Override
  public SupplierIterator<K, V> keyIterator() {
    return new SupplierIterator<K, V>() {
      int cursor = 1;
      boolean hasGet;

      @Override
      public boolean hasNext() {
        return cursor <= Map4.this.size();
      }

      @Override
      public K next() {
        switch (cursor++) {
          case 1:
            return key1;
          case 2:
            return key2;
          case 3:
            return key3;
          case 4:
            return key4;
          default:
            throw new IllegalStateException();
        }
      }

      @Override
      public V get() {
        if (hasGet) {
          hasGet = false;

          switch (cursor) {
            case 1:
              return val1;
            case 2:
              return val2;
            case 3:
              return val3;
            case 4:
              return val4;
            default:
              throw new IllegalStateException();
          }
        } else {
          throw new NoSuchElementException();
        }
      }

      @Override
      public void remove() {
        throw new UnsupportedOperationException();
      }
    };

  }

  @Override
  public io.usethesource.capsule.Map.Immutable<K, V> __put(K key, V val) {
    if (key.equals(key1)) {
      return mapOf(key, val, key2, val2, key3, val3, key4, val4);
    } else if (key.equals(key2)) {
      return mapOf(key1, val1, key, val, key3, val3, key4, val4);
    } else if (key.equals(key3)) {
      return mapOf(key1, val1, key2, val2, key, val, key4, val4);
    } else if (key.equals(key4)) {
      return mapOf(key1, val1, key2, val2, key3, val3, key, val);
    } else {
      return mapOf(key1, val1, key2, val2, key3, val3, key4, val4, key, val);
    }
  }

  @Override
  public io.usethesource.capsule.Map.Immutable<K, V> __remove(K key) {
    if (key.equals(key1)) {
      return mapOf(key2, val2, key3, val3, key4, val4);
    } else if (key.equals(key2)) {
      return mapOf(key1, val1, key3, val3, key4, val4);
    } else if (key.equals(key3)) {
      return mapOf(key1, val1, key2, val2, key4, val4);
    } else if (key.equals(key4)) {
      return mapOf(key1, val1, key2, val2, key3, val3);
    } else {
      return this;
    }
  }

  @Override
  public io.usethesource.capsule.Map.Transient<K, V> asTransient() {
    return io.usethesource.capsule.Map.
        Transient.of(key1, val1, key2, val2, key3, val3, key4, val4);
  }

  @Override
  public int hashCode() {
    return ((Objects.hashCode(key1) ^ Objects.hashCode(val1))
        + (Objects.hashCode(key2) ^ Objects.hashCode(val2))
        + (Objects.hashCode(key3) ^ Objects.hashCode(val3))
        + (Objects.hashCode(key4) ^ Objects.hashCode(val4)));
  }

  @Override
  public String toString() {
    return String.format("{%s=%s, %s=%s, %s=%s, %s=%s}", key1, val1, key2, val2, key3, val3, key4,
        val4);
  }

}


class Map5<K, V> extends AbstractSpecialisedImmutableMap<K, V> {

  private final K key1;
  private final V val1;

  private final K key2;
  private final V val2;

  private final K key3;
  private final V val3;

  private final K key4;
  private final V val4;

  private final K key5;
  private final V val5;

  Map5(final K key1, final V val1, final K key2, final V val2, final K key3, final V val3,
      final K key4, final V val4, final K key5, final V val5) {
    if (key1.equals(key2) || key1.equals(key3) || key1.equals(key4) || key1.equals(key5)
        || key2.equals(key3) || key2.equals(key4) || key2.equals(key5) || key3.equals(key4)
        || key3.equals(key5) || key4.equals(key5)) {
      throw new IllegalArgumentException("Duplicate keys are not allowed in specialised map.");
    }

    this.key1 = key1;
    this.val1 = val1;

    this.key2 = key2;
    this.val2 = val2;

    this.key3 = key3;
    this.val3 = val3;

    this.key4 = key4;
    this.val4 = val4;

    this.key5 = key5;
    this.val5 = val5;
  }

  @Override
  public boolean containsKey(Object key) {
    if (key.equals(key1)) {
      return true;
    } else if (key.equals(key2)) {
      return true;
    } else if (key.equals(key3)) {
      return true;
    } else if (key.equals(key4)) {
      return true;
    } else if (key.equals(key5)) {
      return true;
    } else {
      return false;
    }
  }

  @Override
  public boolean containsValue(Object val) {
    if (val.equals(val1)) {
      return true;
    } else if (val.equals(val2)) {
      return true;
    } else if (val.equals(val3)) {
      return true;
    } else if (val.equals(val4)) {
      return true;
    } else if (val.equals(val5)) {
      return true;
    } else {
      return false;
    }
  }

  @Override
  public V get(Object key) {
    if (key.equals(key1)) {
      return val1;
    } else if (key.equals(key2)) {
      return val2;
    } else if (key.equals(key3)) {
      return val3;
    } else if (key.equals(key4)) {
      return val4;
    } else if (key.equals(key5)) {
      return val5;
    } else {
      return null;
    }
  }

  @Override
  public int size() {
    return 5;
  }

  @Override
  public Set<Entry<K, V>> entrySet() {
    return AbstractSpecialisedImmutableSet.<Map.Entry<K, V>>setOf(entryOf(key1, val1),
        entryOf(key2, val2), entryOf(key3, val3), entryOf(key4, val4), entryOf(key5, val5));
  }

  @Override
  public Set<K> keySet() {
    return AbstractSpecialisedImmutableSet.setOf(key1, key2, key3, key4, key5);
  }

  @Override
  public Collection<V> values() {
    // TODO: return immutable or persistent --not only unmodifiable-- listOf(...)
    return Collections.unmodifiableList(Arrays.asList(val1, val2, val3, val4, val5));
  }

  @Override
  public SupplierIterator<K, V> keyIterator() {
    return new SupplierIterator<K, V>() {
      int cursor = 1;
      boolean hasGet;

      @Override
      public boolean hasNext() {
        return cursor <= Map5.this.size();
      }

      @Override
      public K next() {
        switch (cursor++) {
          case 1:
            return key1;
          case 2:
            return key2;
          case 3:
            return key3;
          case 4:
            return key4;
          case 5:
            return key5;
          default:
            throw new IllegalStateException();
        }
      }

      @Override
      public V get() {
        if (hasGet) {
          hasGet = false;

          switch (cursor) {
            case 1:
              return val1;
            case 2:
              return val2;
            case 3:
              return val3;
            case 4:
              return val4;
            case 5:
              return val5;
            default:
              throw new IllegalStateException();
          }
        } else {
          throw new NoSuchElementException();
        }
      }

      @Override
      public void remove() {
        throw new UnsupportedOperationException();
      }
    };

  }

  @Override
  public io.usethesource.capsule.Map.Immutable<K, V> __put(K key, V val) {
    if (key.equals(key1)) {
      return mapOf(key, val, key2, val2, key3, val3, key4, val4, key5, val5);
    } else if (key.equals(key2)) {
      return mapOf(key1, val1, key, val, key3, val3, key4, val4, key5, val5);
    } else if (key.equals(key3)) {
      return mapOf(key1, val1, key2, val2, key, val, key4, val4, key5, val5);
    } else if (key.equals(key4)) {
      return mapOf(key1, val1, key2, val2, key3, val3, key, val, key5, val5);
    } else if (key.equals(key5)) {
      return mapOf(key1, val1, key2, val2, key3, val3, key4, val4, key, val);
    } else {
      return mapOf(key1, val1, key2, val2, key3, val3, key4, val4, key5, val5, key, val);
    }
  }

  @Override
  public io.usethesource.capsule.Map.Immutable<K, V> __remove(K key) {
    if (key.equals(key1)) {
      return mapOf(key2, val2, key3, val3, key4, val4, key5, val5);
    } else if (key.equals(key2)) {
      return mapOf(key1, val1, key3, val3, key4, val4, key5, val5);
    } else if (key.equals(key3)) {
      return mapOf(key1, val1, key2, val2, key4, val4, key5, val5);
    } else if (key.equals(key4)) {
      return mapOf(key1, val1, key2, val2, key3, val3, key5, val5);
    } else if (key.equals(key5)) {
      return mapOf(key1, val1, key2, val2, key3, val3, key4, val4);
    } else {
      return this;
    }
  }

  @Override
  public io.usethesource.capsule.Map.Transient<K, V> asTransient() {
    return io.usethesource.capsule.Map.
        Transient.of(key1, val1, key2, val2, key3, val3, key4, val4, key5, val5);
  }

  @Override
  public int hashCode() {
    return ((Objects.hashCode(key1) ^ Objects.hashCode(val1))
        + (Objects.hashCode(key2) ^ Objects.hashCode(val2))
        + (Objects.hashCode(key3) ^ Objects.hashCode(val3))
        + (Objects.hashCode(key4) ^ Objects.hashCode(val4))
        + (Objects.hashCode(key5) ^ Objects.hashCode(val5)));
  }

  @Override
  public String toString() {
    return String.format("{%s=%s, %s=%s, %s=%s, %s=%s, %s=%s}", key1, val1, key2, val2, key3, val3,
        key4, val4, key5, val5);
  }

}
