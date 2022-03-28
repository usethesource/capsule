/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.capsule.util.collection;

import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;

import io.usethesource.capsule.util.iterator.EmptySupplierIterator;
import io.usethesource.capsule.util.iterator.SupplierIterator;

public abstract class AbstractSpecialisedImmutableSet<K> extends AbstractImmutableSet<K>
    implements io.usethesource.capsule.Set.Immutable<K>, java.lang.Cloneable, java.io.Serializable {

  private static io.usethesource.capsule.Set.Immutable EMPTY_SET = new Set0();

  // add method required for property-based test suite
  private static <K> io.usethesource.capsule.Set.Immutable<K> of() {
    return EMPTY_SET;
  }

  public static <K> io.usethesource.capsule.Set.Immutable<K> setOf() {
    return EMPTY_SET;
  }

  public static <K> io.usethesource.capsule.Set.Immutable<K> setOf(K key1) {
    return new Set1<K>(key1);
  }

  public static <K> io.usethesource.capsule.Set.Immutable<K> setOf(K key1, K key2) {
    return new Set2<K>(key1, key2);
  }

  public static <K> io.usethesource.capsule.Set.Immutable<K> setOf(K key1, K key2, K key3) {
    return new Set3<K>(key1, key2, key3);
  }

  public static <K> io.usethesource.capsule.Set.Immutable<K> setOf(K key1, K key2, K key3,
      K key4) {
    return new Set4<K>(key1, key2, key3, key4);
  }

  public static <K> io.usethesource.capsule.Set.Immutable<K> setOf(K key1, K key2, K key3,
      K key4, K key5) {
    return new Set5<K>(key1, key2, key3, key4, key5);
  }

  public static <K> io.usethesource.capsule.Set.Immutable<K> setOf(K key1, K key2, K key3,
      K key4, K key5, K key6) {
    final io.usethesource.capsule.Set.Transient<K> tmp = io.usethesource.capsule.Set.Transient
        .of(key1, key2, key3, key4, key5, key6);
    return tmp.freeze();
  }

  public static <K> io.usethesource.capsule.Set.Immutable<K> setOf(Set<K> set) {
    if (set instanceof AbstractSpecialisedImmutableSet) {
      return (io.usethesource.capsule.Set.Immutable<K>) set;
    } else {
      final io.usethesource.capsule.Set.Transient<K> tmp = io.usethesource.capsule.Set.Transient
          .of();
      // TODO check interface definition of Immutable.union()
      for (K item : set) {
        tmp.__insert(item);
      }
      return tmp.freeze();
    }
  }

  @Override
  public boolean add(K k) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean remove(Object o) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean addAll(Collection<? extends K> c) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean removeAll(Collection<?> c) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean retainAll(Collection<?> c) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void clear() {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean isEmpty() {
    return size() == 0;
  }

  @Override
  public Iterator<K> iterator() {
    return keyIterator();
  }

  @Override
  public boolean equals(final Object other) {
    if (other == this) {
      return true;
    }
    if (other == null) {
      return false;
    }

    if (other instanceof Set) {
      try {
        Set<K> that = (Set<K>) other;

        if (this.size() == that.size()) {
          for (K e : that) {
            if (!this.contains(e)) {
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
  public Object clone() throws CloneNotSupportedException {
    return super.clone();
  }

  @Override
  public boolean isTransientSupported() {
    return true;
  }

  @Override
  public io.usethesource.capsule.Set.Immutable<K> __insertAll(Set<? extends K> set) {
    io.usethesource.capsule.Set.Transient<K> tmp = asTransient();
    if (tmp.__insertAll(set)) {
      return tmp.freeze();
    } else {
      return this;
    }
  }

  @Override
  public io.usethesource.capsule.Set.Immutable<K> __retainAll(Set<? extends K> set) {
    io.usethesource.capsule.Set.Transient<K> tmp = asTransient();
    if (tmp.__retainAll(set)) {
      return tmp.freeze();
    } else {
      return this;
    }
  }

  @Override
  public io.usethesource.capsule.Set.Immutable<K> __removeAll(Set<? extends K> set) {
    io.usethesource.capsule.Set.Transient<K> tmp = asTransient();
    if (tmp.__removeAll(set)) {
      return tmp.freeze();
    } else {
      return this;
    }
  }
}


class Set0<K> extends AbstractSpecialisedImmutableSet<K> {

  Set0() {

  }

  @Override
  public boolean contains(Object key) {
    return false;
  }

  @Override
  public K get(Object key) {
    return null;
  }

  @Override
  public int size() {
    return 0;
  }

  @Override
  public SupplierIterator<K, K> keyIterator() {
    return EmptySupplierIterator.emptyIterator();
  }

  @Override
  public io.usethesource.capsule.Set.Immutable<K> __insert(K key) {
    return setOf(key);
  }

  @Override
  public io.usethesource.capsule.Set.Immutable<K> __remove(K key) {
    return this;
  }

  @Override
  public io.usethesource.capsule.Set.Transient<K> asTransient() {
    return io.usethesource.capsule.Set.Transient.of();
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


class Set1<K> extends AbstractSpecialisedImmutableSet<K> {

  private final K key1;

  Set1(final K key1) {
    this.key1 = key1;
  }

  @Override
  public boolean contains(Object key) {
    if (key.equals(key1)) {
      return true;
    } else {
      return false;
    }
  }

  @Override
  public K get(Object key) {
    if (key.equals(key1)) {
      return key1;
    } else {
      return null;
    }
  }

  @Override
  public int size() {
    return 1;
  }

  @Override
  public SupplierIterator<K, K> keyIterator() {
    return new SupplierIterator<K, K>() {
      int cursor = 1;
      boolean hasGet;

      @Override
      public boolean hasNext() {
        return cursor <= Set1.this.size();
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
      public K get() {
        if (hasGet) {
          hasGet = false;

          switch (cursor) {
            case 1:
              return key1;
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
  public io.usethesource.capsule.Set.Immutable<K> __insert(K key) {
    if (key.equals(key1)) {
      return setOf(key);
    } else {
      return setOf(key1, key);
    }
  }

  @Override
  public io.usethesource.capsule.Set.Immutable<K> __remove(K key) {
    if (key.equals(key1)) {
      return setOf();
    } else {
      return this;
    }
  }

  @Override
  public io.usethesource.capsule.Set.Transient<K> asTransient() {
    return io.usethesource.capsule.Set.Transient.of(key1);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(key1);
  }

  @Override
  public String toString() {
    return String.format("{%s}", key1);
  }

}


class Set2<K> extends AbstractSpecialisedImmutableSet<K> {

  private final K key1;
  private final K key2;

  Set2(final K key1, final K key2) {
    if (key1.equals(key2)) {
      throw new IllegalArgumentException("Duplicate elements are not allowed in specialised set.");
    }

    this.key1 = key1;

    this.key2 = key2;
  }

  @Override
  public boolean contains(Object key) {
    if (key.equals(key1)) {
      return true;
    } else if (key.equals(key2)) {
      return true;
    } else {
      return false;
    }
  }

  @Override
  public K get(Object key) {
    if (key.equals(key1)) {
      return key1;
    } else if (key.equals(key2)) {
      return key2;
    } else {
      return null;
    }
  }

  @Override
  public int size() {
    return 2;
  }

  @Override
  public SupplierIterator<K, K> keyIterator() {
    return new SupplierIterator<K, K>() {
      int cursor = 1;
      boolean hasGet;

      @Override
      public boolean hasNext() {
        return cursor <= Set2.this.size();
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
      public K get() {
        if (hasGet) {
          hasGet = false;

          switch (cursor) {
            case 1:
              return key1;
            case 2:
              return key2;
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
  public io.usethesource.capsule.Set.Immutable<K> __insert(K key) {
    if (key.equals(key1)) {
      return setOf(key, key2);
    } else if (key.equals(key2)) {
      return setOf(key1, key);
    } else {
      return setOf(key1, key2, key);
    }
  }

  @Override
  public io.usethesource.capsule.Set.Immutable<K> __remove(K key) {
    if (key.equals(key1)) {
      return setOf(key2);
    } else if (key.equals(key2)) {
      return setOf(key1);
    } else {
      return this;
    }
  }

  @Override
  public io.usethesource.capsule.Set.Transient<K> asTransient() {
    return io.usethesource.capsule.Set.Transient.of(key1, key2);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(key1) + Objects.hashCode(key2);
  }

  @Override
  public String toString() {
    return String.format("{%s, %s}", key1, key2);
  }

}


class Set3<K> extends AbstractSpecialisedImmutableSet<K> {

  private final K key1;
  private final K key2;
  private final K key3;

  Set3(final K key1, final K key2, final K key3) {
    if (key1.equals(key2) || key1.equals(key3) || key2.equals(key3)) {
      throw new IllegalArgumentException("Duplicate elements are not allowed in specialised set.");
    }

    this.key1 = key1;

    this.key2 = key2;

    this.key3 = key3;
  }

  @Override
  public boolean contains(Object key) {
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
  public K get(Object key) {
    if (key.equals(key1)) {
      return key1;
    } else if (key.equals(key2)) {
      return key2;
    } else if (key.equals(key3)) {
      return key3;
    } else {
      return null;
    }
  }

  @Override
  public int size() {
    return 3;
  }

  @Override
  public SupplierIterator<K, K> keyIterator() {
    return new SupplierIterator<K, K>() {
      int cursor = 1;
      boolean hasGet;

      @Override
      public boolean hasNext() {
        return cursor <= Set3.this.size();
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
      public K get() {
        if (hasGet) {
          hasGet = false;

          switch (cursor) {
            case 1:
              return key1;
            case 2:
              return key2;
            case 3:
              return key3;
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
  public io.usethesource.capsule.Set.Immutable<K> __insert(K key) {
    if (key.equals(key1)) {
      return setOf(key, key2, key3);
    } else if (key.equals(key2)) {
      return setOf(key1, key, key3);
    } else if (key.equals(key3)) {
      return setOf(key1, key2, key);
    } else {
      return setOf(key1, key2, key3, key);
    }
  }

  @Override
  public io.usethesource.capsule.Set.Immutable<K> __remove(K key) {
    if (key.equals(key1)) {
      return setOf(key2, key3);
    } else if (key.equals(key2)) {
      return setOf(key1, key3);
    } else if (key.equals(key3)) {
      return setOf(key1, key2);
    } else {
      return this;
    }
  }

  @Override
  public io.usethesource.capsule.Set.Transient<K> asTransient() {
    return io.usethesource.capsule.Set.Transient.of(key1, key2, key3);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(key1) + Objects.hashCode(key2) + Objects.hashCode(key3);
  }

  @Override
  public String toString() {
    return String.format("{%s, %s, %s}", key1, key2, key3);
  }

}


class Set4<K> extends AbstractSpecialisedImmutableSet<K> {

  private final K key1;
  private final K key2;
  private final K key3;
  private final K key4;

  Set4(final K key1, final K key2, final K key3, final K key4) {
    if (key1.equals(key2) || key1.equals(key3) || key1.equals(key4) || key2.equals(key3)
        || key2.equals(key4) || key3.equals(key4)) {
      throw new IllegalArgumentException("Duplicate elements are not allowed in specialised set.");
    }

    this.key1 = key1;

    this.key2 = key2;

    this.key3 = key3;

    this.key4 = key4;
  }

  @Override
  public boolean contains(Object key) {
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
  public K get(Object key) {
    if (key.equals(key1)) {
      return key1;
    } else if (key.equals(key2)) {
      return key2;
    } else if (key.equals(key3)) {
      return key3;
    } else if (key.equals(key4)) {
      return key4;
    } else {
      return null;
    }
  }

  @Override
  public int size() {
    return 4;
  }

  @Override
  public SupplierIterator<K, K> keyIterator() {
    return new SupplierIterator<K, K>() {
      int cursor = 1;
      boolean hasGet;

      @Override
      public boolean hasNext() {
        return cursor <= Set4.this.size();
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
      public K get() {
        if (hasGet) {
          hasGet = false;

          switch (cursor) {
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
  public io.usethesource.capsule.Set.Immutable<K> __insert(K key) {
    if (key.equals(key1)) {
      return setOf(key, key2, key3, key4);
    } else if (key.equals(key2)) {
      return setOf(key1, key, key3, key4);
    } else if (key.equals(key3)) {
      return setOf(key1, key2, key, key4);
    } else if (key.equals(key4)) {
      return setOf(key1, key2, key3, key);
    } else {
      return setOf(key1, key2, key3, key4, key);
    }
  }

  @Override
  public io.usethesource.capsule.Set.Immutable<K> __remove(K key) {
    if (key.equals(key1)) {
      return setOf(key2, key3, key4);
    } else if (key.equals(key2)) {
      return setOf(key1, key3, key4);
    } else if (key.equals(key3)) {
      return setOf(key1, key2, key4);
    } else if (key.equals(key4)) {
      return setOf(key1, key2, key3);
    } else {
      return this;
    }
  }

  @Override
  public io.usethesource.capsule.Set.Transient<K> asTransient() {
    return io.usethesource.capsule.Set.Transient.of(key1, key2, key3, key4);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(key1) + Objects.hashCode(key2) + Objects.hashCode(key3)
        + Objects.hashCode(key4);
  }

  @Override
  public String toString() {
    return String.format("{%s, %s, %s, %s}", key1, key2, key3, key4);
  }

}


class Set5<K> extends AbstractSpecialisedImmutableSet<K> {

  private final K key1;
  private final K key2;
  private final K key3;
  private final K key4;
  private final K key5;

  Set5(final K key1, final K key2, final K key3, final K key4, final K key5) {
    if (key1.equals(key2) || key1.equals(key3) || key1.equals(key4) || key1.equals(key5)
        || key2.equals(key3) || key2.equals(key4) || key2.equals(key5) || key3.equals(key4)
        || key3.equals(key5) || key4.equals(key5)) {
      throw new IllegalArgumentException("Duplicate elements are not allowed in specialised set.");
    }

    this.key1 = key1;

    this.key2 = key2;

    this.key3 = key3;

    this.key4 = key4;

    this.key5 = key5;
  }

  @Override
  public boolean contains(Object key) {
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
  public K get(Object key) {
    if (key.equals(key1)) {
      return key1;
    } else if (key.equals(key2)) {
      return key2;
    } else if (key.equals(key3)) {
      return key3;
    } else if (key.equals(key4)) {
      return key4;
    } else if (key.equals(key5)) {
      return key5;
    } else {
      return null;
    }
  }

  @Override
  public int size() {
    return 5;
  }

  @Override
  public SupplierIterator<K, K> keyIterator() {
    return new SupplierIterator<K, K>() {
      int cursor = 1;
      boolean hasGet;

      @Override
      public boolean hasNext() {
        return cursor <= Set5.this.size();
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
      public K get() {
        if (hasGet) {
          hasGet = false;

          switch (cursor) {
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
  public io.usethesource.capsule.Set.Immutable<K> __insert(K key) {
    if (key.equals(key1)) {
      return setOf(key, key2, key3, key4, key5);
    } else if (key.equals(key2)) {
      return setOf(key1, key, key3, key4, key5);
    } else if (key.equals(key3)) {
      return setOf(key1, key2, key, key4, key5);
    } else if (key.equals(key4)) {
      return setOf(key1, key2, key3, key, key5);
    } else if (key.equals(key5)) {
      return setOf(key1, key2, key3, key4, key);
    } else {
      return setOf(key1, key2, key3, key4, key5, key);
    }
  }

  @Override
  public io.usethesource.capsule.Set.Immutable<K> __remove(K key) {
    if (key.equals(key1)) {
      return setOf(key2, key3, key4, key5);
    } else if (key.equals(key2)) {
      return setOf(key1, key3, key4, key5);
    } else if (key.equals(key3)) {
      return setOf(key1, key2, key4, key5);
    } else if (key.equals(key4)) {
      return setOf(key1, key2, key3, key5);
    } else if (key.equals(key5)) {
      return setOf(key1, key2, key3, key4);
    } else {
      return this;
    }
  }

  @Override
  public io.usethesource.capsule.Set.Transient<K> asTransient() {
    return io.usethesource.capsule.Set.Transient.of(key1, key2, key3, key4, key5);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(key1) + Objects.hashCode(key2) + Objects.hashCode(key3)
        + Objects.hashCode(key4) + Objects.hashCode(key5);
  }

  @Override
  public String toString() {
    return String.format("{%s, %s, %s, %s, %s}", key1, key2, key3, key4, key5);
  }

}
