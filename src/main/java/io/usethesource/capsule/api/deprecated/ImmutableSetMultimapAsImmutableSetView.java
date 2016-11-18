/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.capsule.api.deprecated;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;

/*
 * (K, V) -> T
 *
 * Wrapping kev-value pair to tuple
 */
public class ImmutableSetMultimapAsImmutableSetView<K, V, T> implements ImmutableSet<T> {

  final ImmutableSetMultimap<K, V> multimap;

  final BiFunction<K, V, T> tupleOf;

  final BiFunction<T, Integer, Object> tupleElementAt;

  /*
   * Verifies the arity of a tuple (in our case arity should be 2).
   */
  final Function<T, Boolean> tupleChecker;

  public ImmutableSetMultimapAsImmutableSetView(ImmutableSetMultimap<K, V> multimap,
      BiFunction<K, V, T> tupleOf, BiFunction<T, Integer, Object> tupleElementAt,
      Function<T, Boolean> tupleChecker) {
    this.multimap = multimap;
    this.tupleOf = tupleOf;
    this.tupleElementAt = tupleElementAt;
    this.tupleChecker = tupleChecker;
  }

  @Override
  public int size() {
    return multimap.size();
  }

  @Override
  public boolean isEmpty() {
    return multimap.isEmpty();
  }

  @Override
  public Iterator<T> iterator() {
    return multimap.tupleIterator(tupleOf);
  }

  @Override
  public Object[] toArray() {
    throw new UnsupportedOperationException("Auto-generated method stub; not implemented yet.");
  }

  @Override
  public <T> T[] toArray(T[] a) {
    throw new UnsupportedOperationException("Auto-generated method stub; not implemented yet.");
  }

  @Override
  public boolean add(T tuple) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean remove(Object o) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean addAll(Collection<? extends T> c) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean retainAll(Collection<?> c) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean removeAll(Collection<?> c) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void clear() {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean contains(Object o) {
    try {
      T tuple = (T) o;

      if (!tupleChecker.apply(tuple))
        throw new ClassCastException("Type validation failed.");

      @SuppressWarnings("unchecked")
      final K key = (K) tupleElementAt.apply(tuple, 0);
      @SuppressWarnings("unchecked")
      final V val = (V) tupleElementAt.apply(tuple, 1);

      return multimap.containsEntry(key, val);
    } catch (ClassCastException | ArrayIndexOutOfBoundsException e) {
      // not a tuple or not at least two elements
      return false;
    }
  }

  @Override
  public boolean containsEquivalent(Object o, Comparator<Object> cmp) {
    try {
      T tuple = (T) o;

      if (!tupleChecker.apply(tuple))
        throw new ClassCastException("Type validation failed.");

      @SuppressWarnings("unchecked")
      final K key = (K) tupleElementAt.apply(tuple, 0);
      @SuppressWarnings("unchecked")
      final V val = (V) tupleElementAt.apply(tuple, 1);

      return multimap.containsEntryEquivalent(key, val, cmp);
    } catch (ClassCastException | ArrayIndexOutOfBoundsException e) {
      // not a tuple or not at least two elements
      return false;
    }
  }

  @Override
  public boolean containsAll(Collection<?> c) {
    throw new UnsupportedOperationException("Auto-generated method stub; not implemented yet.");
  }

  @Override
  public boolean containsAllEquivalent(Collection<?> c, Comparator<Object> cmp) {
    throw new UnsupportedOperationException("Auto-generated method stub; not implemented yet.");
  }

  @Override
  public T get(Object o) {
    throw new UnsupportedOperationException("Auto-generated method stub; not implemented yet.");
  }

  @Override
  public T getEquivalent(Object o, Comparator<Object> cmp) {
    throw new UnsupportedOperationException("Auto-generated method stub; not implemented yet.");
  }

  @Override
  public ImmutableSet<T> __insert(T tuple) {
    if (!tupleChecker.apply(tuple))
      throw new ClassCastException("Type validation failed.");

    @SuppressWarnings("unchecked")
    final K key = (K) tupleElementAt.apply(tuple, 0);
    @SuppressWarnings("unchecked")
    final V val = (V) tupleElementAt.apply(tuple, 1);

    final ImmutableSetMultimap<K, V> multimapNew = multimap.__insert(key, val);

    if (multimapNew == multimap) {
      return this;
    } else {
      return new ImmutableSetMultimapAsImmutableSetView<>(multimapNew, tupleOf, tupleElementAt,
          tupleChecker);
    }
  }

  @Override
  public ImmutableSet<T> __insertEquivalent(T tuple, Comparator<Object> cmp) {
    if (!tupleChecker.apply(tuple))
      throw new ClassCastException("Type validation failed.");

    @SuppressWarnings("unchecked")
    final K key = (K) tupleElementAt.apply(tuple, 0);
    @SuppressWarnings("unchecked")
    final V val = (V) tupleElementAt.apply(tuple, 1);

    final ImmutableSetMultimap<K, V> multimapNew = multimap.__insertEquivalent(key, val, cmp);

    if (multimapNew == multimap) {
      return this;
    } else {
      return new ImmutableSetMultimapAsImmutableSetView<>(multimapNew, tupleOf, tupleElementAt,
          tupleChecker);
    }
  }

  @Override
  public ImmutableSet<T> __insertAll(Set<? extends T> set) {
    throw new UnsupportedOperationException("Auto-generated method stub; not implemented yet.");
  }

  @Override
  public ImmutableSet<T> __insertAllEquivalent(Set<? extends T> set, Comparator<Object> cmp) {
    throw new UnsupportedOperationException("Auto-generated method stub; not implemented yet.");
  }

  @Override
  public ImmutableSet<T> __remove(T tuple) {
    @SuppressWarnings("unchecked")
    final K key = (K) tupleElementAt.apply(tuple, 0);
    @SuppressWarnings("unchecked")
    final V val = (V) tupleElementAt.apply(tuple, 1);

    final ImmutableSetMultimap<K, V> multimapNew = multimap.__removeEntry(key, val);

    return new ImmutableSetMultimapAsImmutableSetView<>(multimapNew, tupleOf, tupleElementAt,
        tupleChecker);
  }

  @Override
  public ImmutableSet<T> __removeEquivalent(T tuple, Comparator<Object> cmp) {
    @SuppressWarnings("unchecked")
    final K key = (K) tupleElementAt.apply(tuple, 0);
    @SuppressWarnings("unchecked")
    final V val = (V) tupleElementAt.apply(tuple, 1);

    final ImmutableSetMultimap<K, V> multimapNew = multimap.__removeEntryEquivalent(key, val, cmp);

    return new ImmutableSetMultimapAsImmutableSetView<>(multimapNew, tupleOf, tupleElementAt,
        tupleChecker);
  }

  @Override
  public ImmutableSet<T> __removeAll(Set<? extends T> set) {
    throw new UnsupportedOperationException("Auto-generated method stub; not implemented yet.");
  }

  @Override
  public ImmutableSet<T> __removeAllEquivalent(Set<? extends T> set, Comparator<Object> cmp) {
    throw new UnsupportedOperationException("Auto-generated method stub; not implemented yet.");
  }

  @Override
  public ImmutableSet<T> __retainAll(Set<? extends T> set) {
    throw new UnsupportedOperationException("Auto-generated method stub; not implemented yet.");
  }

  @Override
  public ImmutableSet<T> __retainAllEquivalent(TransientSet<? extends T> set,
      Comparator<Object> cmp) {
    throw new UnsupportedOperationException("Auto-generated method stub; not implemented yet.");
  }

  @Override
  public Iterator<T> keyIterator() {
    return multimap.tupleIterator(tupleOf);
  }

  @Override
  public boolean isTransientSupported() {
    return true;
  }

  @Override
  public TransientSet<T> asTransient() {
    return new TransientSetMultimapAsTransientSetView<>(multimap.asTransient(), tupleOf,
        tupleElementAt, tupleChecker);
  }

  static final class TransientSetMultimapAsTransientSetView<K, V, T> implements TransientSet<T> {

    final TransientSetMultimap<K, V> multimap;

    final BiFunction<K, V, T> tupleOf;

    final BiFunction<T, Integer, Object> tupleElementAt;

    /*
     * Verifies the arity of a tuple (in our case arity should be 2).
     */
    final Function<T, Boolean> tupleChecker;

    public TransientSetMultimapAsTransientSetView(TransientSetMultimap<K, V> multimap,
        BiFunction<K, V, T> tupleOf, BiFunction<T, Integer, Object> tupleElementAt,
        Function<T, Boolean> tupleChecker) {
      this.multimap = multimap;
      this.tupleOf = tupleOf;
      this.tupleElementAt = tupleElementAt;
      this.tupleChecker = tupleChecker;
    }

    @Override
    public int size() {
      return multimap.size();
    }

    @Override
    public boolean isEmpty() {
      return multimap.isEmpty();
    }

    @Override
    public Iterator<T> iterator() {
      return multimap.tupleIterator(tupleOf);
    }

    @Override
    public Object[] toArray() {
      throw new UnsupportedOperationException("Auto-generated method stub; not implemented yet.");
    }

    @Override
    public <T> T[] toArray(T[] a) {
      throw new UnsupportedOperationException("Auto-generated method stub; not implemented yet.");
    }

    @Override
    public boolean add(T e) {
      throw new UnsupportedOperationException("Auto-generated method stub; not implemented yet.");
    }

    @Override
    public boolean remove(Object o) {
      throw new UnsupportedOperationException("Auto-generated method stub; not implemented yet.");
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
      throw new UnsupportedOperationException("Auto-generated method stub; not implemented yet.");
    }

    @Override
    public boolean retainAll(Collection<?> c) {
      throw new UnsupportedOperationException("Auto-generated method stub; not implemented yet.");
    }

    @Override
    public boolean removeAll(Collection<?> c) {
      throw new UnsupportedOperationException("Auto-generated method stub; not implemented yet.");
    }

    @Override
    public void clear() {
      throw new UnsupportedOperationException("Auto-generated method stub; not implemented yet.");
    }

    @Override
    public boolean contains(Object o) {
      try {
        T tuple = (T) o;

        if (!tupleChecker.apply(tuple))
          throw new ClassCastException("Type validation failed.");

        @SuppressWarnings("unchecked")
        final K key = (K) tupleElementAt.apply(tuple, 0);
        @SuppressWarnings("unchecked")
        final V val = (V) tupleElementAt.apply(tuple, 1);

        return multimap.containsEntry(key, val);
      } catch (ClassCastException | ArrayIndexOutOfBoundsException e) {
        // not a tuple or not at least two elements
        return false;
      }
    }

    @Override
    public boolean containsEquivalent(Object o, Comparator<Object> cmp) {
      try {
        T tuple = (T) o;

        if (!tupleChecker.apply(tuple))
          throw new ClassCastException("Type validation failed.");

        @SuppressWarnings("unchecked")
        final K key = (K) tupleElementAt.apply(tuple, 0);
        @SuppressWarnings("unchecked")
        final V val = (V) tupleElementAt.apply(tuple, 1);

        return multimap.containsEntryEquivalent(key, val, cmp);
      } catch (ClassCastException | ArrayIndexOutOfBoundsException e) {
        // not a tuple or not at least two elements
        return false;
      }
    }

    @Override
    public boolean containsAll(Collection<?> c) {
      throw new UnsupportedOperationException("Auto-generated method stub; not implemented yet.");
    }

    @Override
    public boolean containsAllEquivalent(Collection<?> c, Comparator<Object> cmp) {
      throw new UnsupportedOperationException("Auto-generated method stub; not implemented yet.");
    }

    @Override
    public T get(Object o) {
      throw new UnsupportedOperationException("Auto-generated method stub; not implemented yet.");
    }

    @Override
    public T getEquivalent(Object o, Comparator<Object> cmp) {
      throw new UnsupportedOperationException("Auto-generated method stub; not implemented yet.");
    }

    @Override
    public boolean __insert(T tuple) {
      if (!tupleChecker.apply(tuple))
        throw new ClassCastException("Type validation failed.");

      @SuppressWarnings("unchecked")
      final K key = (K) tupleElementAt.apply(tuple, 0);
      @SuppressWarnings("unchecked")
      final V val = (V) tupleElementAt.apply(tuple, 1);

      return multimap.__insert(key, val);
    }

    @Override
    public boolean __insertEquivalent(T tuple, Comparator<Object> cmp) {
      if (!tupleChecker.apply(tuple))
        throw new ClassCastException("Type validation failed.");

      @SuppressWarnings("unchecked")
      final K key = (K) tupleElementAt.apply(tuple, 0);
      @SuppressWarnings("unchecked")
      final V val = (V) tupleElementAt.apply(tuple, 1);

      return multimap.__insertEquivalent(key, val, cmp);
    }

    @Override
    public boolean __insertAll(Set<? extends T> set) {
      throw new UnsupportedOperationException("Auto-generated method stub; not implemented yet.");
    }

    @Override
    public boolean __insertAllEquivalent(Set<? extends T> set, Comparator<Object> cmp) {
      throw new UnsupportedOperationException("Auto-generated method stub; not implemented yet.");
    }

    @Override
    public boolean __remove(T tuple) {
      @SuppressWarnings("unchecked")
      final K key = (K) tupleElementAt.apply(tuple, 0);
      @SuppressWarnings("unchecked")
      final V val = (V) tupleElementAt.apply(tuple, 1);

      return multimap.__removeTuple(key, val);
    }

    @Override
    public boolean __removeEquivalent(T tuple, Comparator<Object> cmp) {
      @SuppressWarnings("unchecked")
      final K key = (K) tupleElementAt.apply(tuple, 0);
      @SuppressWarnings("unchecked")
      final V val = (V) tupleElementAt.apply(tuple, 1);

      return multimap.__removeTupleEquivalent(key, val, cmp);
    }

    @Override
    public boolean __removeAll(Set<? extends T> set) {
      throw new UnsupportedOperationException("Auto-generated method stub; not implemented yet.");
    }

    @Override
    public boolean __removeAllEquivalent(Set<? extends T> set, Comparator<Object> cmp) {
      throw new UnsupportedOperationException("Auto-generated method stub; not implemented yet.");
    }

    @Override
    public boolean __retainAll(Set<? extends T> set) {
      throw new UnsupportedOperationException("Auto-generated method stub; not implemented yet.");
    }

    @Override
    public boolean __retainAllEquivalent(TransientSet<? extends T> set, Comparator<Object> cmp) {
      throw new UnsupportedOperationException("Auto-generated method stub; not implemented yet.");
    }

    @Override
    public Iterator<T> keyIterator() {
      return multimap.tupleIterator(tupleOf);
    }

    @Override
    public int hashCode() {
      int hash = 0;

      for (Iterator<T> it = iterator(); it.hasNext();) {
        final T tuple = it.next();
        hash += tuple.hashCode();
      }

      return hash;
    }

    @Override
    public boolean equals(Object other) {
      if (other == this) {
        return true;
      }
      if (other == null) {
        return false;
      }

      if (other instanceof TransientSetMultimapAsTransientSetView) {
        TransientSetMultimapAsTransientSetView<?, ?, ?> that =
            (TransientSetMultimapAsTransientSetView<?, ?, ?>) other;

        return multimap.equals(that.multimap);
      } else if (other instanceof Set) {
        Set<?> that = (Set<?>) other;

        if (this.size() != that.size())
          return false;

        return containsAll(that);
      }

      return false;
    }

    @Override
    public ImmutableSet<T> freeze() {
      return new ImmutableSetMultimapAsImmutableSetView<>(multimap.freeze(), tupleOf,
          tupleElementAt, tupleChecker);
    }

  }

  @Override
  public int hashCode() {
    int hash = 0;

    for (Iterator<T> it = iterator(); it.hasNext();) {
      final T tuple = it.next();
      hash += tuple.hashCode();
    }

    return hash;
  }

  @Override
  public boolean equals(Object other) {
    if (other == this) {
      return true;
    }
    if (other == null) {
      return false;
    }

    if (other instanceof ImmutableSetMultimapAsImmutableSetView) {
      ImmutableSetMultimapAsImmutableSetView<?, ?, ?> that =
          (ImmutableSetMultimapAsImmutableSetView<?, ?, ?>) other;

      return multimap.equals(that.multimap);
    } else if (other instanceof Set) {
      Set<?> that = (Set<?>) other;

      if (this.size() != that.size())
        return false;

      return containsAll(that);
    }

    return false;
  }

}
