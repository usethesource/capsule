package io.usethesource.capsule.api.deprecated;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;

public interface Set {
  interface ImmutableSet<K> extends java.util.Set<K> {

    @Override
    boolean containsAll(final Collection<?> c);

    @Deprecated
    default boolean containsAllEquivalent(final Collection<?> c, final Comparator<Object> cmp) {
      throw new UnsupportedOperationException("Not yet implemented @ ImmutableSet.");
    }

    K get(final Object o);

    @Deprecated
    default K getEquivalent(final Object o, final Comparator<Object> cmp) {
      throw new UnsupportedOperationException("Not yet implemented @ ImmutableSet.");
    }

    @Override
    boolean contains(final Object o);

    @Deprecated
    default boolean containsEquivalent(final Object o, final Comparator<Object> cmp) {
      throw new UnsupportedOperationException("Not yet implemented @ ImmutableSet.");
    }

    ImmutableSet<K> __insert(final K key);

    @Deprecated
    default ImmutableSet<K> __insertEquivalent(final K key, final Comparator<Object> cmp) {
      throw new UnsupportedOperationException("Not yet implemented @ ImmutableSet.");
    }

    ImmutableSet<K> __insertAll(final java.util.Set<? extends K> set);

    @Deprecated
    default ImmutableSet<K> __insertAllEquivalent(final java.util.Set<? extends K> set,
                                                  final Comparator<Object> cmp) {
      throw new UnsupportedOperationException("Not yet implemented @ ImmutableSet.");
    }

    ImmutableSet<K> __remove(final K key);

    @Deprecated
    default ImmutableSet<K> __removeEquivalent(final K key, final Comparator<Object> cmp) {
      throw new UnsupportedOperationException("Not yet implemented @ ImmutableSet.");
    }

    ImmutableSet<K> __removeAll(final java.util.Set<? extends K> set);

    @Deprecated
    default ImmutableSet<K> __removeAllEquivalent(final java.util.Set<? extends K> set,
                                                  final Comparator<Object> cmp) {
      throw new UnsupportedOperationException("Not yet implemented @ ImmutableSet.");
    }

    ImmutableSet<K> __retainAll(final java.util.Set<? extends K> set);

    @Deprecated
    default ImmutableSet<K> __retainAllEquivalent(final TransientSet<? extends K> transientSet,
                                                  final Comparator<Object> cmp) {
      throw new UnsupportedOperationException("Not yet implemented @ ImmutableSet.");
    }

    Iterator<K> keyIterator();

    boolean isTransientSupported();

    TransientSet<K> asTransient();

  }

  interface TransientSet<K> extends java.util.Set<K> {

    @Override
    boolean containsAll(final Collection<?> c);

    @Deprecated
    default boolean containsAllEquivalent(final Collection<?> c, final Comparator<Object> cmp) {
      throw new UnsupportedOperationException("Not yet implemented @ TransientSet.");
    }

    K get(final Object o);

    @Deprecated
    default K getEquivalent(final Object o, final Comparator<Object> cmp) {
      throw new UnsupportedOperationException("Not yet implemented @ TransientSet.");
    }

    @Override
    boolean contains(final Object o);

    @Deprecated
    default boolean containsEquivalent(final Object o, final Comparator<Object> cmp) {
      throw new UnsupportedOperationException("Not yet implemented @ TransientSet.");
    }

    boolean __insert(final K key);

    @Deprecated
    default boolean __insertEquivalent(final K key, final Comparator<Object> cmp) {
      throw new UnsupportedOperationException("Not yet implemented @ TransientSet.");
    }

    boolean __insertAll(final java.util.Set<? extends K> set);

    @Deprecated
    default boolean __insertAllEquivalent(final java.util.Set<? extends K> set, final Comparator<Object> cmp) {
      throw new UnsupportedOperationException("Not yet implemented @ TransientSet.");
    }

    boolean __remove(final K key);

    @Deprecated
    default boolean __removeEquivalent(final K key, final Comparator<Object> cmp) {
      throw new UnsupportedOperationException("Not yet implemented @ TransientSet.");
    }

    boolean __removeAll(final java.util.Set<? extends K> set);

    @Deprecated
    default boolean __removeAllEquivalent(final java.util.Set<? extends K> set, final Comparator<Object> cmp) {
      throw new UnsupportedOperationException("Not yet implemented @ TransientSet.");
    }

    boolean __retainAll(final java.util.Set<? extends K> set);

    @Deprecated
    default boolean __retainAllEquivalent(final TransientSet<? extends K> transientSet,
                                          final Comparator<Object> cmp) {
      throw new UnsupportedOperationException("Not yet implemented @ TransientSet.");
    }

    Iterator<K> keyIterator();

    ImmutableSet<K> freeze();

  }
}
