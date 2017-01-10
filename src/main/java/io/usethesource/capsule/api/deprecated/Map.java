package io.usethesource.capsule.api.deprecated;

import java.util.Comparator;
import java.util.Iterator;

public interface Map<K, V> extends java.util.Map<K, V> {
  interface Immutable<K, V> extends Map<K, V> {

    @Override
    V get(final Object o);

    V getEquivalent(final Object o, final Comparator<Object> cmp);

    @Override
    boolean containsKey(final Object o);

    boolean containsKeyEquivalent(final Object o, final Comparator<Object> cmp);

    @Override
    boolean containsValue(final Object o);

    boolean containsValueEquivalent(final Object o, final Comparator<Object> cmp);

    Map.Immutable<K, V> __put(final K key, final V val);

    Map.Immutable<K, V> __putEquivalent(final K key, final V val, final Comparator<Object> cmp);

    Map.Immutable<K, V> __putAll(final java.util.Map<? extends K, ? extends V> map);

    Map.Immutable<K, V> __putAllEquivalent(final java.util.Map<? extends K, ? extends V> map,
                                       final Comparator<Object> cmp);

    Map.Immutable<K, V> __remove(final K key);

    Map.Immutable<K, V> __removeEquivalent(final K key, final Comparator<Object> cmp);

    Iterator<K> keyIterator();

    Iterator<V> valueIterator();

    Iterator<Entry<K, V>> entryIterator();

    boolean isTransientSupported();

    Map.Transient<K, V> asTransient();

  }

  interface Transient<K, V> extends Map<K, V> {

    @Override
    V get(final Object o);

    V getEquivalent(final Object o, final Comparator<Object> cmp);

    @Override
    boolean containsKey(final Object o);

    boolean containsKeyEquivalent(final Object o, final Comparator<Object> cmp);

    @Override
    boolean containsValue(final Object o);

    boolean containsValueEquivalent(final Object o, final Comparator<Object> cmp);

    V __put(final K key, final V val);

    V __putEquivalent(final K key, final V val, final Comparator<Object> cmp);

    boolean __putAll(final java.util.Map<? extends K, ? extends V> map);

    boolean __putAllEquivalent(final java.util.Map<? extends K, ? extends V> map, final Comparator<Object> cmp);

    V __remove(final K key);

    V __removeEquivalent(final K key, final Comparator<Object> cmp);

    Iterator<K> keyIterator();

    Iterator<V> valueIterator();

    Iterator<Entry<K, V>> entryIterator();

    Map.Immutable<K, V> freeze();

  }
}
