package io.usethesource.capsule.api.deprecated;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;

public interface Set<K> extends java.util.Set<K> {
  interface Immutable<K> extends Set<K> {

    @Override
    boolean containsAll(final Collection<?> c);

    @Deprecated
    default boolean containsAllEquivalent(final Collection<?> c, final Comparator<Object> cmp) {
      throw new UnsupportedOperationException("Not yet implemented @ Set.Immutable.");
    }

    K get(final Object o);

    @Deprecated
    default K getEquivalent(final Object o, final Comparator<Object> cmp) {
      throw new UnsupportedOperationException("Not yet implemented @ Set.Immutable.");
    }

    @Override
    boolean contains(final Object o);

    @Deprecated
    default boolean containsEquivalent(final Object o, final Comparator<Object> cmp) {
      throw new UnsupportedOperationException("Not yet implemented @ Set.Immutable.");
    }

    Set.Immutable<K> __insert(final K key);

    @Deprecated
    default Set.Immutable<K> __insertEquivalent(final K key, final Comparator<Object> cmp) {
      throw new UnsupportedOperationException("Not yet implemented @ Set.Immutable.");
    }

    Set.Immutable<K> __insertAll(final java.util.Set<? extends K> set);

    @Deprecated
    default Set.Immutable<K> __insertAllEquivalent(final java.util.Set<? extends K> set,
                                               final Comparator<Object> cmp) {
      throw new UnsupportedOperationException("Not yet implemented @ Set.Immutable.");
    }

    Set.Immutable<K> __remove(final K key);

    @Deprecated
    default Set.Immutable<K> __removeEquivalent(final K key, final Comparator<Object> cmp) {
      throw new UnsupportedOperationException("Not yet implemented @ Set.Immutable.");
    }

    Set.Immutable<K> __removeAll(final java.util.Set<? extends K> set);

    @Deprecated
    default Set.Immutable<K> __removeAllEquivalent(final java.util.Set<? extends K> set,
                                               final Comparator<Object> cmp) {
      throw new UnsupportedOperationException("Not yet implemented @ Set.Immutable.");
    }

    Set.Immutable<K> __retainAll(final java.util.Set<? extends K> set);

    @Deprecated
    default Set.Immutable<K> __retainAllEquivalent(final Set.Transient<? extends K> transientSet,
                                               final Comparator<Object> cmp) {
      throw new UnsupportedOperationException("Not yet implemented @ Set.Immutable.");
    }

    Iterator<K> keyIterator();

    boolean isTransientSupported();

    Set.Transient<K> asTransient();

  }

  interface Transient<K> extends Set<K> {

    @Override
    boolean containsAll(final Collection<?> c);

    @Deprecated
    default boolean containsAllEquivalent(final Collection<?> c, final Comparator<Object> cmp) {
      throw new UnsupportedOperationException("Not yet implemented @ Set.Transient.");
    }

    K get(final Object o);

    @Deprecated
    default K getEquivalent(final Object o, final Comparator<Object> cmp) {
      throw new UnsupportedOperationException("Not yet implemented @ Set.Transient.");
    }

    @Override
    boolean contains(final Object o);

    @Deprecated
    default boolean containsEquivalent(final Object o, final Comparator<Object> cmp) {
      throw new UnsupportedOperationException("Not yet implemented @ Set.Transient.");
    }

    boolean __insert(final K key);

    @Deprecated
    default boolean __insertEquivalent(final K key, final Comparator<Object> cmp) {
      throw new UnsupportedOperationException("Not yet implemented @ Set.Transient.");
    }

    boolean __insertAll(final java.util.Set<? extends K> set);

    @Deprecated
    default boolean __insertAllEquivalent(final java.util.Set<? extends K> set, final Comparator<Object> cmp) {
      throw new UnsupportedOperationException("Not yet implemented @ Set.Transient.");
    }

    boolean __remove(final K key);

    @Deprecated
    default boolean __removeEquivalent(final K key, final Comparator<Object> cmp) {
      throw new UnsupportedOperationException("Not yet implemented @ Set.Transient.");
    }

    boolean __removeAll(final java.util.Set<? extends K> set);

    @Deprecated
    default boolean __removeAllEquivalent(final java.util.Set<? extends K> set, final Comparator<Object> cmp) {
      throw new UnsupportedOperationException("Not yet implemented @ Set.Transient.");
    }

    boolean __retainAll(final java.util.Set<? extends K> set);

    @Deprecated
    default boolean __retainAllEquivalent(final Set.Transient<? extends K> transientSet,
                                          final Comparator<Object> cmp) {
      throw new UnsupportedOperationException("Not yet implemented @ Set.Transient.");
    }

    Iterator<K> keyIterator();

    Set.Immutable<K> freeze();

  }
}
