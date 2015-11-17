/*******************************************************************************
 * Copyright (c) 2013-2015 CWI All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *
 * * Michael Steindorfer - Michael.Steindorfer@cwi.nl - CWI
 *******************************************************************************/
package io.usethesource.capsule;

import static io.usethesource.capsule.AbstractSpecialisedImmutableMap.entryOf;

import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

@SuppressWarnings("rawtypes")
public class TrieMap_Heterogeneous_BleedingEdge
    implements ImmutableMap<java.lang.Integer, java.lang.Integer> {

  protected static final AbstractMapNode EMPTY_NODE =
      new Map0To0Node_Heterogeneous_BleedingEdge(null, (byte) 0, (byte) 0);

  @SuppressWarnings("unchecked")
  private static final TrieMap_Heterogeneous_BleedingEdge EMPTY_MAP =
      new TrieMap_Heterogeneous_BleedingEdge(EMPTY_NODE, 0, 0);

  private static final boolean DEBUG = false;

  private final AbstractMapNode rootNode;
  private final int hashCode;
  private final int cachedSize;

  TrieMap_Heterogeneous_BleedingEdge(AbstractMapNode rootNode, int hashCode, int cachedSize) {
    this.rootNode = rootNode;
    this.hashCode = hashCode;
    this.cachedSize = cachedSize;
    if (DEBUG) {
      assert checkHashCodeAndSize(hashCode, cachedSize);
    }
  }

  @SuppressWarnings("unchecked")
  public static final ImmutableMap<java.lang.Integer, java.lang.Integer> of() {
    return TrieMap_Heterogeneous_BleedingEdge.EMPTY_MAP;
  }

  @SuppressWarnings("unchecked")
  public static final ImmutableMap<java.lang.Integer, java.lang.Integer> of(
      Object... keyValuePairs) {
    if (keyValuePairs.length % 2 != 0) {
      throw new IllegalArgumentException("Length of argument list is uneven: no key/value pairs.");
    }

    ImmutableMap<java.lang.Integer, java.lang.Integer> result =
        TrieMap_Heterogeneous_BleedingEdge.EMPTY_MAP;

    for (int i = 0; i < keyValuePairs.length; i += 2) {
      final int key = (int) keyValuePairs[i];
      final int val = (int) keyValuePairs[i + 1];

      result = result.__put(key, val);
    }

    return result;
  }

  @SuppressWarnings("unchecked")
  public static final TransientMap<java.lang.Integer, java.lang.Integer> transientOf() {
    return TrieMap_Heterogeneous_BleedingEdge.EMPTY_MAP.asTransient();
  }

  @SuppressWarnings("unchecked")
  public static final TransientMap<java.lang.Integer, java.lang.Integer> transientOf(
      Object... keyValuePairs) {
    if (keyValuePairs.length % 2 != 0) {
      throw new IllegalArgumentException("Length of argument list is uneven: no key/value pairs.");
    }

    final TransientMap<java.lang.Integer, java.lang.Integer> result =
        TrieMap_Heterogeneous_BleedingEdge.EMPTY_MAP.asTransient();

    for (int i = 0; i < keyValuePairs.length; i += 2) {
      final int key = (int) keyValuePairs[i];
      final int val = (int) keyValuePairs[i + 1];

      result.__put(key, val);
    }

    return result;
  }

  private boolean checkHashCodeAndSize(final int targetHash, final int targetSize) {
    int hash = 0;
    int size = 0;

    for (Iterator<Map.Entry<java.lang.Integer, java.lang.Integer>> it = entryIterator(); it
        .hasNext();) {
      final Map.Entry<java.lang.Integer, java.lang.Integer> entry = it.next();
      final int key = entry.getKey();
      final int val = entry.getValue();

      hash += key ^ val;
      size += 1;
    }

    return hash == targetHash && size == targetSize;
  }

  public static final int transformHashCode(final int hash) {
    return hash;
  }

  @Override
  public boolean containsKey(final Object key) {
    return rootNode.containsKey(key, transformHashCode(key.hashCode()), 0);
  }

  public boolean containsKey(final int key) {
    return rootNode.containsKey(key, transformHashCode(key), 0);
  }

  @Override
  public boolean containsKeyEquivalent(final Object o, final Comparator<Object> cmp) {
    try {
      @SuppressWarnings("unchecked")
      final int key = (int) o;
      return rootNode.containsKey(key, transformHashCode(key), 0, cmp);
    } catch (ClassCastException unused) {
      return false;
    }
  }

  @Override
  public boolean containsValue(final Object o) {
    for (Iterator<java.lang.Integer> iterator = valueIterator(); iterator.hasNext();) {
      if (iterator.next().equals(o)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public boolean containsValueEquivalent(final Object o, final Comparator<Object> cmp) {
    for (Iterator<java.lang.Integer> iterator = valueIterator(); iterator.hasNext();) {
      if (cmp.compare(iterator.next(), o) == 0) {
        return true;
      }
    }
    return false;
  }

  @Override
  public java.lang.Integer get(final Object o) {
    try {
      @SuppressWarnings("unchecked")
      final int key = (int) o;
      final Optional<java.lang.Integer> result = rootNode.findByKey(key, transformHashCode(key), 0);

      if (result.isPresent()) {
        return result.get();
      } else {
        return null;
      }
    } catch (ClassCastException unused) {
      return null;
    }
  }

  @Override
  public java.lang.Integer getEquivalent(final Object o, final Comparator<Object> cmp) {
    try {
      @SuppressWarnings("unchecked")
      final int key = (int) o;
      final Optional<java.lang.Integer> result =
          rootNode.findByKey(key, transformHashCode(key), 0, cmp);

      if (result.isPresent()) {
        return result.get();
      } else {
        return null;
      }
    } catch (ClassCastException unused) {
      return null;
    }
  }

  @Override
  public ImmutableMap<java.lang.Integer, java.lang.Integer> __put(final java.lang.Integer key,
      final java.lang.Integer val) {
    // TODO: remove requirement distinct value groups
    if (containsKey((int) key)) {
      return this;
    }

    final int keyHash = key.hashCode();
    final MapResult details = MapResult.unchanged();

    final AbstractMapNode newRootNode =
        rootNode.updated(null, key, val, transformHashCode(keyHash), 0, details);

    if (details.isModified()) {
      if (details.hasReplacedValue()) {
        final int valHashOld = details.getReplacedValue();
        final int valHashNew = val;

        return new TrieMap_Heterogeneous_BleedingEdge(newRootNode,
            hashCode + ((keyHash ^ valHashNew)) - ((keyHash ^ valHashOld)), cachedSize);
      }

      final int valHash = val;
      return new TrieMap_Heterogeneous_BleedingEdge(newRootNode, hashCode + ((keyHash ^ valHash)),
          cachedSize + 1);
    }

    return this;
  }

  public ImmutableMap<java.lang.Integer, java.lang.Integer> __put(final int key, final int val) {
    // TODO: remove requirement distinct value groups
    if (containsKey(Integer.valueOf(key))) {
      return this;
    }

    final int keyHash = key;
    final MapResult details = MapResult.unchanged();

    final AbstractMapNode newRootNode =
        rootNode.updated(null, key, val, transformHashCode(keyHash), 0, details);

    if (details.isModified()) {
      if (details.hasReplacedValue()) {
        final int valHashOld = details.getReplacedValue();
        final int valHashNew = val;

        return new TrieMap_Heterogeneous_BleedingEdge(newRootNode,
            hashCode + ((keyHash ^ valHashNew)) - ((keyHash ^ valHashOld)), cachedSize);
      }

      final int valHash = val;
      return new TrieMap_Heterogeneous_BleedingEdge(newRootNode, hashCode + ((keyHash ^ valHash)),
          cachedSize + 1);
    }

    return this;
  }

  @Override
  public ImmutableMap<java.lang.Integer, java.lang.Integer> __putEquivalent(
      final java.lang.Integer key, final java.lang.Integer val, final Comparator<Object> cmp) {
    final int keyHash = key.hashCode();
    final MapResult details = MapResult.unchanged();

    final AbstractMapNode newRootNode =
        rootNode.updated(null, key, val, transformHashCode(keyHash), 0, details, cmp);

    if (details.isModified()) {
      if (details.hasReplacedValue()) {
        final int valHashOld = details.getReplacedValue();
        final int valHashNew = val;

        return new TrieMap_Heterogeneous_BleedingEdge(newRootNode,
            hashCode + ((keyHash ^ valHashNew)) - ((keyHash ^ valHashOld)), cachedSize);
      }

      final int valHash = val;
      return new TrieMap_Heterogeneous_BleedingEdge(newRootNode, hashCode + ((keyHash ^ valHash)),
          cachedSize + 1);
    }

    return this;
  }

  @Override
  public ImmutableMap<java.lang.Integer, java.lang.Integer> __putAll(
      final Map<? extends java.lang.Integer, ? extends java.lang.Integer> map) {
    final TransientMap<java.lang.Integer, java.lang.Integer> tmpTransient = this.asTransient();
    tmpTransient.__putAll(map);
    return tmpTransient.freeze();
  }

  @Override
  public ImmutableMap<java.lang.Integer, java.lang.Integer> __putAllEquivalent(
      final Map<? extends java.lang.Integer, ? extends java.lang.Integer> map,
      final Comparator<Object> cmp) {
    final TransientMap<java.lang.Integer, java.lang.Integer> tmpTransient = this.asTransient();
    tmpTransient.__putAllEquivalent(map, cmp);
    return tmpTransient.freeze();
  }

  @Override
  public ImmutableMap<java.lang.Integer, java.lang.Integer> __remove(final java.lang.Integer key) {
    final int keyHash = key.hashCode();
    final MapResult details = MapResult.unchanged();

    final AbstractMapNode newRootNode =
        rootNode.removed(null, key, transformHashCode(keyHash), 0, details);

    if (details.isModified()) {
      assert details.hasReplacedValue();
      final int valHash = details.getReplacedValue();
      return new TrieMap_Heterogeneous_BleedingEdge(newRootNode, hashCode - ((keyHash ^ valHash)),
          cachedSize - 1);
    }

    return this;
  }

  @Override
  public ImmutableMap<java.lang.Integer, java.lang.Integer> __removeEquivalent(
      final java.lang.Integer key, final Comparator<Object> cmp) {
    final int keyHash = key.hashCode();
    final MapResult details = MapResult.unchanged();

    final AbstractMapNode newRootNode =
        rootNode.removed(null, key, transformHashCode(keyHash), 0, details, cmp);

    if (details.isModified()) {
      assert details.hasReplacedValue();
      final int valHash = details.getReplacedValue();
      return new TrieMap_Heterogeneous_BleedingEdge(newRootNode, hashCode - ((keyHash ^ valHash)),
          cachedSize - 1);
    }

    return this;
  }

  @Override
  public java.lang.Integer put(final java.lang.Integer key, final java.lang.Integer val) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void putAll(final Map<? extends java.lang.Integer, ? extends java.lang.Integer> m) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void clear() {
    throw new UnsupportedOperationException();
  }

  @Override
  public java.lang.Integer remove(final Object key) {
    throw new UnsupportedOperationException();
  }

  @Override
  public int size() {
    return cachedSize;
  }

  @Override
  public boolean isEmpty() {
    return cachedSize == 0;
  }

  @Override
  public Iterator<java.lang.Integer> keyIterator() {
    return new MapKeyIterator(rootNode);
  }

  @Override
  public Iterator<java.lang.Integer> valueIterator() {
    return new MapValueIterator(rootNode);
  }

  @Override
  public Iterator<Map.Entry<java.lang.Integer, java.lang.Integer>> entryIterator() {
    return new MapEntryIterator(rootNode);
  }

  @Override
  public Set<java.lang.Integer> keySet() {
    Set<java.lang.Integer> keySet = null;

    if (keySet == null) {
      keySet = new AbstractSet<java.lang.Integer>() {
        @Override
        public Iterator<java.lang.Integer> iterator() {
          return TrieMap_Heterogeneous_BleedingEdge.this.keyIterator();
        }

        @Override
        public int size() {
          return TrieMap_Heterogeneous_BleedingEdge.this.size();
        }

        @Override
        public boolean isEmpty() {
          return TrieMap_Heterogeneous_BleedingEdge.this.isEmpty();
        }

        @Override
        public void clear() {
          TrieMap_Heterogeneous_BleedingEdge.this.clear();
        }

        @Override
        public boolean contains(Object k) {
          return TrieMap_Heterogeneous_BleedingEdge.this.containsKey(k);
        }
      };
    }

    return keySet;
  }

  @Override
  public Collection<java.lang.Integer> values() {
    Collection<java.lang.Integer> values = null;

    if (values == null) {
      values = new AbstractCollection<java.lang.Integer>() {
        @Override
        public Iterator<java.lang.Integer> iterator() {
          return TrieMap_Heterogeneous_BleedingEdge.this.valueIterator();
        }

        @Override
        public int size() {
          return TrieMap_Heterogeneous_BleedingEdge.this.size();
        }

        @Override
        public boolean isEmpty() {
          return TrieMap_Heterogeneous_BleedingEdge.this.isEmpty();
        }

        @Override
        public void clear() {
          TrieMap_Heterogeneous_BleedingEdge.this.clear();
        }

        @Override
        public boolean contains(Object v) {
          return TrieMap_Heterogeneous_BleedingEdge.this.containsValue(v);
        }
      };
    }

    return values;
  }

  @Override
  public Set<java.util.Map.Entry<java.lang.Integer, java.lang.Integer>> entrySet() {
    Set<java.util.Map.Entry<java.lang.Integer, java.lang.Integer>> entrySet = null;

    if (entrySet == null) {
      entrySet = new AbstractSet<java.util.Map.Entry<java.lang.Integer, java.lang.Integer>>() {
        @Override
        public Iterator<java.util.Map.Entry<java.lang.Integer, java.lang.Integer>> iterator() {
          return new Iterator<Map.Entry<java.lang.Integer, java.lang.Integer>>() {
            private final Iterator<Map.Entry<java.lang.Integer, java.lang.Integer>> i =
                entryIterator();

            @Override
            public boolean hasNext() {
              return i.hasNext();
            }

            @Override
            public Map.Entry<java.lang.Integer, java.lang.Integer> next() {
              return i.next();
            }

            @Override
            public void remove() {
              i.remove();
            }
          };
        }

        @Override
        public int size() {
          return TrieMap_Heterogeneous_BleedingEdge.this.size();
        }

        @Override
        public boolean isEmpty() {
          return TrieMap_Heterogeneous_BleedingEdge.this.isEmpty();
        }

        @Override
        public void clear() {
          TrieMap_Heterogeneous_BleedingEdge.this.clear();
        }

        @Override
        public boolean contains(Object k) {
          return TrieMap_Heterogeneous_BleedingEdge.this.containsKey(k);
        }
      };
    }

    return entrySet;
  }

  @Override
  public boolean equals(final Object other) {
    if (other == this) {
      return true;
    }
    if (other == null) {
      return false;
    }

    if (other instanceof TrieMap_Heterogeneous_BleedingEdge) {
      TrieMap_Heterogeneous_BleedingEdge that = (TrieMap_Heterogeneous_BleedingEdge) other;

      if (this.cachedSize != that.cachedSize) {
        return false;
      }

      if (this.hashCode != that.hashCode) {
        return false;
      }

      return rootNode.equals(that.rootNode);
    } else if (other instanceof Map) {
      Map that = (Map) other;

      if (this.size() != that.size())
        return false;

      for (@SuppressWarnings("unchecked")
      Iterator<Map.Entry> it = that.entrySet().iterator(); it.hasNext();) {
        Map.Entry entry = it.next();

        try {
          @SuppressWarnings("unchecked")
          final int key = (java.lang.Integer) entry.getKey();
          final Optional<java.lang.Integer> result =
              rootNode.findByKey(key, transformHashCode(key), 0);

          if (!result.isPresent()) {
            return false;
          } else {
            @SuppressWarnings("unchecked")
            final int val = (java.lang.Integer) entry.getValue();

            if (!result.get().equals(val)) {
              return false;
            }
          }
        } catch (ClassCastException unused) {
          return false;
        }
      }

      return true;
    }

    return false;
  }

  @Override
  public int hashCode() {
    return hashCode;
  }

  @Override
  public boolean isTransientSupported() {
    return true;
  }

  @Override
  public TransientMap<java.lang.Integer, java.lang.Integer> asTransient() {
    return new TransientTrieMap_Heterogeneous_BleedingEdge(this);
  }

  /*
   * For analysis purposes only.
   */
  protected AbstractMapNode getRootNode() {
    return rootNode;
  }

  /*
   * For analysis purposes only.
   */
  protected Iterator<AbstractMapNode> nodeIterator() {
    return new TrieMap_Heterogeneous_BleedingEdgeNodeIterator(rootNode);
  }

  /*
   * For analysis purposes only.
   */
  protected int getNodeCount() {
    final Iterator<AbstractMapNode> it = nodeIterator();
    int sumNodes = 0;

    for (; it.hasNext(); it.next()) {
      sumNodes += 1;
    }

    return sumNodes;
  }

  /*
   * For analysis purposes only.
   */
  protected int[][] arityCombinationsHistogram() {
    final Iterator<AbstractMapNode> it = nodeIterator();
    final int[][] sumArityCombinations = new int[9][9];

    while (it.hasNext()) {
      final AbstractMapNode node = it.next();
      sumArityCombinations[node.payloadArity()][node.nodeArity()] += 1;
    }

    return sumArityCombinations;
  }

  /*
   * For analysis purposes only.
   */
  protected int[] arityHistogram() {
    final int[][] sumArityCombinations = arityCombinationsHistogram();
    final int[] sumArity = new int[9];

    final int maxArity = 8; // TODO: factor out constant

    for (int j = 0; j <= maxArity; j++) {
      for (int maxRestArity = maxArity - j, k = 0; k <= maxRestArity - j; k++) {
        sumArity[j + k] += sumArityCombinations[j][k];
      }
    }

    return sumArity;
  }

  /*
   * For analysis purposes only.
   */
  public void printStatistics() {
    final int[][] sumArityCombinations = arityCombinationsHistogram();
    final int[] sumArity = arityHistogram();
    final int sumNodes = getNodeCount();

    final int[] cumsumArity = new int[9];
    for (int cumsum = 0, i = 0; i < 9; i++) {
      cumsum += sumArity[i];
      cumsumArity[i] = cumsum;
    }

    final float threshhold = 0.01f; // for printing results
    for (int i = 0; i < 9; i++) {
      float arityPercentage = (float) (sumArity[i]) / sumNodes;
      float cumsumArityPercentage = (float) (cumsumArity[i]) / sumNodes;

      if (arityPercentage != 0 && arityPercentage >= threshhold) {
        // details per level
        StringBuilder bldr = new StringBuilder();
        int max = i;
        for (int j = 0; j <= max; j++) {
          for (int k = max - j; k <= max - j; k++) {
            float arityCombinationsPercentage = (float) (sumArityCombinations[j][k]) / sumNodes;

            if (arityCombinationsPercentage != 0 && arityCombinationsPercentage >= threshhold) {
              bldr.append(String.format("%d/%d: %s, ", j, k,
                  new DecimalFormat("0.00%").format(arityCombinationsPercentage)));
            }
          }
        }
        final String detailPercentages = bldr.toString();

        // overview
        System.out.println(String.format("%2d: %s\t[cumsum = %s]\t%s", i,
            new DecimalFormat("0.00%").format(arityPercentage),
            new DecimalFormat("0.00%").format(cumsumArityPercentage), detailPercentages));
      }
    }
  }

  abstract static class Optional<T> {
    private static final Optional EMPTY = new Optional() {
      @Override
      boolean isPresent() {
        return false;
      }

      @Override
      Object get() {
        return null;
      }
    };

    @SuppressWarnings("unchecked")
    static <T> Optional<T> empty() {
      return EMPTY;
    }

    static <T> Optional<T> of(T value) {
      return new Value<T>(value);
    }

    abstract boolean isPresent();

    abstract T get();

    private static final class Value<T> extends Optional<T> {
      private final T value;

      private Value(T value) {
        this.value = value;
      }

      @Override
      boolean isPresent() {
        return true;
      }

      @Override
      T get() {
        return value;
      }
    }
  }
  static final class MapResult {
    private int replacedValue;
    private boolean isModified;
    private boolean isReplaced;

    // update: inserted/removed single element, element count changed
    public void modified() {
      this.isModified = true;
    }

    public void updated(int replacedValue) {
      this.replacedValue = replacedValue;
      this.isModified = true;
      this.isReplaced = true;
    }

    // update: neither element, nor element count changed
    public static MapResult unchanged() {
      return new MapResult();
    }

    private MapResult() {}

    public boolean isModified() {
      return isModified;
    }

    public boolean hasReplacedValue() {
      return isReplaced;
    }

    public int getReplacedValue() {
      return replacedValue;
    }
  }
  protected static interface INode<K, V> {
  }
  private abstract static class AbstractMapNode {

    protected static final sun.misc.Unsafe initializeUnsafe() {
      try {
        Field field = sun.misc.Unsafe.class.getDeclaredField("theUnsafe");
        field.setAccessible(true);
        return (sun.misc.Unsafe) field.get(null);
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }

    protected static final sun.misc.Unsafe unsafe = initializeUnsafe();

    protected static final int TUPLE_LENGTH = 2;

    protected static final boolean isAllowedToEdit(final AtomicReference<Thread> x,
        final AtomicReference<Thread> y) {
      return x != null && y != null && (x == y || x.get() == y.get());
    }

    abstract boolean containsKey(final Object key, final int keyHash, final int shift);

    abstract boolean containsKey(final int key, final int keyHash, final int shift);

    abstract boolean containsKey(final int key, final int keyHash, final int shift,
        final Comparator<Object> cmp);

    abstract Optional<java.lang.Integer> findByKey(final int key, final int keyHash,
        final int shift);

    abstract Optional<java.lang.Integer> findByKey(final int key, final int keyHash,
        final int shift, final Comparator<Object> cmp);

    abstract AbstractMapNode updated(final AtomicReference<Thread> mutator, final int key,
        final int val, final int keyHash, final int shift, final MapResult details);

    abstract AbstractMapNode updated(final AtomicReference<Thread> mutator, final int key,
        final int val, final int keyHash, final int shift, final MapResult details,
        final Comparator<Object> cmp);

    abstract AbstractMapNode updated(final AtomicReference<Thread> mutator, final Object key,
        final Object val, final int keyHash, final int shift, final MapResult details);

    abstract AbstractMapNode updated(final AtomicReference<Thread> mutator, final Object key,
        final Object val, final int keyHash, final int shift, final MapResult details,
        final Comparator<Object> cmp);

    abstract AbstractMapNode removed(final AtomicReference<Thread> mutator, final int key,
        final int keyHash, final int shift, final MapResult details);

    abstract AbstractMapNode removed(final AtomicReference<Thread> mutator, final int key,
        final int keyHash, final int shift, final MapResult details, final Comparator<Object> cmp);

    abstract boolean hasNodes();

    abstract int nodeArity();

    abstract AbstractMapNode getNode(final int index);

    Iterator<? extends AbstractMapNode> nodeIterator() {
      return new Iterator<AbstractMapNode>() {
        int nextIndex = 0;
        final int nodeArity = AbstractMapNode.this.nodeArity();

        @Override
        public void remove() {
          throw new UnsupportedOperationException();
        }

        @Override
        public AbstractMapNode next() {
          if (!hasNext())
            throw new NoSuchElementException();
          return AbstractMapNode.this.getNode(nextIndex++);
        }

        @Override
        public boolean hasNext() {
          return nextIndex < nodeArity;
        }
      };
    }

    abstract boolean hasPayload();

    abstract int payloadArity();

    abstract int rarePayloadArity();

    abstract int getKey(final int index);

    abstract int getVal(final int index);

    abstract Map.Entry<java.lang.Integer, java.lang.Integer> getKeyValueEntry(final int index);

    abstract Object getRareKey(final int index);

    abstract Object getRareVal(final int index);

    abstract boolean hasSlots();

    abstract int slotArity();

    abstract Object getSlot(final int index);

    abstract int untypedSlotArity();

    /**
     * The arity of this trie node (i.e. number of values and nodes stored on this level).
     * 
     * @return sum of nodes and values stored within
     */

    int arity() {
      return payloadArity() + nodeArity();
    }

    int size() {
      final Iterator<java.lang.Integer> it = new MapKeyIterator(this);

      int size = 0;
      while (it.hasNext()) {
        size += 1;
        it.next();
      };
      return size;
    }

    static final byte sizeEmpty() {
      return 0b0;
    }

    static final byte sizeOne() {
      return 0b1;
    }

    static final byte sizeMoreThanOne() {
      return 0b10;
    }

    byte sizePredicate() {
      if (this.nodeArity() == 0) {
        switch (this.payloadArity()) {
          case 0:
            return sizeEmpty();
          case 1:
            return sizeOne();
          default:
            return sizeMoreThanOne();
        }
      } else {
        return sizeMoreThanOne();
      }
    }

    @Override
    abstract public boolean equals(final Object other);

    @Override
    abstract public String toString();

  }
  private abstract static class CompactMapNode extends AbstractMapNode {

    static final long initializeArrayBase() {
      try {
        // assuems that both are of type Object and next to each other in memory
        return DataLayoutHelper.arrayOffsets[0];
      } catch (SecurityException e) {
        throw new RuntimeException(e);
      }
    }

    static final long arrayBase = initializeArrayBase();

    static final long initializeAddressSize() {
      try {
        // assuems that both are of type Object and next to each other in memory
        return DataLayoutHelper.arrayOffsets[1] - DataLayoutHelper.arrayOffsets[0];
      } catch (SecurityException e) {
        throw new RuntimeException(e);
      }
    }

    static final long addressSize = initializeAddressSize();

    static final Class[][] initializeSpecializationsByContentAndNodes() {
      Class[][] next = new Class[9][17];

      try {
        for (int m = 0; m <= 8; m++) {
          for (int n = 0; n <= 16; n++) {
            int mNext = m;
            int nNext = n;

            if (mNext < 0 || mNext > 8 || nNext < 0 || nNext > 16
                || Math.ceil(nNext / 2.0) + mNext > 8) {
              next[m][n] = null;
            } else {
              next[m][n] = Class.forName(String.format(
                  "io.usethesource.capsule.TrieMap_Heterogeneous_BleedingEdge$Map%dTo%dNode_Heterogeneous_BleedingEdge",
                  mNext, nNext));
            }
          }
        }
      } catch (ClassNotFoundException e) {
        throw new RuntimeException(e);
      }

      return next;
    }

    static final Class[][] specializationsByContentAndNodes =
        initializeSpecializationsByContentAndNodes();

    static final long globalNodeMapOffset =
        fieldOffset(Map0To2Node_Heterogeneous_BleedingEdge.class, "rawMap1");

    static final long globalDataMapOffset =
        fieldOffset(Map0To2Node_Heterogeneous_BleedingEdge.class, "rawMap2");

    static final long globalArrayOffsetsOffset =
        fieldOffset(Map0To2Node_Heterogeneous_BleedingEdge.class, "arrayOffsets");

    static final long globalNodeArityOffset =
        fieldOffset(Map0To2Node_Heterogeneous_BleedingEdge.class, "nodeArity");

    static final long globalPayloadArityOffset =
        fieldOffset(Map0To2Node_Heterogeneous_BleedingEdge.class, "payloadArity");

    static final long globalSlotArityOffset =
        fieldOffset(Map0To2Node_Heterogeneous_BleedingEdge.class, "slotArity");

    static final long globalUntypedSlotArityOffset =
        fieldOffset(Map0To2Node_Heterogeneous_BleedingEdge.class, "untypedSlotArity");

    static final long globalArrayOffsetLastOffset =
        fieldOffset(Map0To2Node_Heterogeneous_BleedingEdge.class, "arrayOffsetLast");

    static final int hashCodeLength() {
      return 32;
    }

    static final int bitPartitionSize() {
      return 3;
    }

    static final int bitPartitionMask() {
      return 0b111;
    }

    static final int mask(final int keyHash, final int shift) {
      return (keyHash >>> shift) & bitPartitionMask();
    }

    static final byte bitpos(final int mask) {
      return (byte) (1 << mask);
    }

    byte nodeMap() {
      return (byte) ((rawMap1() ^ rareMap()) & 0xFF);
    }

    byte dataMap() {
      return (byte) ((rawMap2() ^ rareMap()) & 0xFF);
    }

    byte rareMap() {
      return (byte) ((rawMap1() & rawMap2()) & 0xFF);
    }

    abstract byte rawMap1();

    abstract byte rawMap2();

    static final boolean isRare(final Object o) {
      throw new UnsupportedOperationException(); // TODO: to implement
    }

    static final boolean isRare(final Object o0, final Object o1) {
      throw new UnsupportedOperationException(); // TODO: to implement
    }

    static final boolean isRare(final byte bitpos) {
      throw new UnsupportedOperationException(); // TODO: to implement
    }

    static final int getKey(final Class<? extends CompactMapNode> clazz,
        final CompactMapNode instance, final int index) {
      // TODO: remove try / catch and throw SecurityException instead
      try {
        long keyOffset = arrayBase + (TUPLE_LENGTH * addressSize) * index;
        return unsafe.getInt(instance, keyOffset);
      } catch (SecurityException e) {
        throw new RuntimeException(e);
      }
    }

    static final Object getRareKey(final Class<? extends CompactMapNode> clazz,
        final CompactMapNode instance, final int index) {
      // TODO: remove try / catch and throw SecurityException instead
      try {
        long keyOffset = arrayBase + (TUPLE_LENGTH * 4 * instance.payloadArity())
            + (TUPLE_LENGTH * addressSize) * index;
        return unsafe.getObject(instance, keyOffset);
      } catch (SecurityException e) {
        throw new RuntimeException(e);
      }
    }

    static final AbstractMapNode getNode(final Class<? extends CompactMapNode> clazz,
        final CompactMapNode instance, final int index) {
      // TODO: remove try / catch and throw SecurityException instead
      try {
        final long arrayOffsetLast = unsafe.getLong(clazz, globalArrayOffsetLastOffset);
        final long nodeOffset = arrayOffsetLast - addressSize * index;

        return (AbstractMapNode) unsafe.getObject(instance, nodeOffset);
      } catch (SecurityException e) {
        throw new RuntimeException(e);
      }
    }

    enum ContentType {
      KEY, VAL, RARE_KEY, RARE_VAL, NODE, SLOT
    }

    int logicalToPhysicalIndex(final ContentType type, final int index) {
      final int physicalIndex;

      switch (type) {
        case KEY:
          physicalIndex = TUPLE_LENGTH * index;
          break;
        case VAL:
          physicalIndex = TUPLE_LENGTH * index + 1;
          break;
        case RARE_KEY:
          physicalIndex =
              TUPLE_LENGTH * index + TUPLE_LENGTH * java.lang.Integer.bitCount(dataMap() & 0xFF);
          break;
        case RARE_VAL:
          physicalIndex = TUPLE_LENGTH * index
              + TUPLE_LENGTH * java.lang.Integer.bitCount(dataMap() & 0xFF) + 1;
          break;
        case NODE:
          physicalIndex = slotArity() - 1 - index;
          break;
        case SLOT:
          physicalIndex = index;
          break;
        default:
          throw new IllegalStateException("Cases not exhausted?");
      }

      return physicalIndex;
    }

    boolean nodeInvariant() {
      boolean inv1 = (size() - payloadArity() >= 2 * (arity() - payloadArity()));
      boolean inv2 = (this.arity() == 0) ? sizePredicate() == sizeEmpty() : true;
      boolean inv3 =
          (this.arity() == 1 && payloadArity() == 1) ? sizePredicate() == sizeOne() : true;
      boolean inv4 = (this.arity() >= 2) ? sizePredicate() == sizeMoreThanOne() : true;
      boolean inv5 = (this.nodeArity() >= 0) && (this.payloadArity() >= 0)
          && ((this.payloadArity() + this.nodeArity()) == this.arity());

      return inv1 && inv2 && inv3 && inv4 && inv5;
    }

    static final long[] arrayOffsets(final Class clazz, final String[] fieldNames) {
      try {
        long[] arrayOffsets = new long[fieldNames.length];

        for (int i = 0; i < fieldNames.length; i++) {
          arrayOffsets[i] = unsafe.objectFieldOffset(clazz.getDeclaredField(fieldNames[i]));
        }

        return arrayOffsets;
      } catch (NoSuchFieldException | SecurityException e) {
        throw new RuntimeException(e);
      }
    }

    static final long fieldOffset(final Class clazz, final String fieldName) {
      try {
        List<Class> bottomUpHierarchy = new LinkedList<>();

        Class currentClass = clazz;
        while (currentClass != null) {
          bottomUpHierarchy.add(currentClass);
          currentClass = currentClass.getSuperclass();
        }

        final java.util.Optional<Field> fieldNameField = bottomUpHierarchy.stream()
            .flatMap(hierarchyClass -> Stream.of(hierarchyClass.getDeclaredFields()))
            .filter(f -> f.getName().equals(fieldName)).findFirst();

        if (fieldNameField.isPresent()) {

          if (java.lang.reflect.Modifier.isStatic(fieldNameField.get().getModifiers())) {
            return unsafe.staticFieldOffset(fieldNameField.get());
          } else {
            return unsafe.objectFieldOffset(fieldNameField.get());
          }
        } else {
          return sun.misc.Unsafe.INVALID_FIELD_OFFSET;
        }
      } catch (SecurityException e) {
        throw new RuntimeException(e);
      }
    }

    CompactMapNode copyAndSetValue(final AtomicReference<Thread> mutator, final byte bitpos,
        final int val) {
      try {
        final int valIdx = dataIndex(bitpos);

        final Class srcClass = this.getClass();
        final Class dstClass = specializationsByContentAndNodes[payloadArity()][untypedSlotArity()];

        if (dstClass == null) {
          throw new RuntimeException(
              String.format("[%s] No new specialization [payloadArity=%d, nodeArity=%d].",
                  srcClass.getName(), payloadArity(), nodeArity()));
        }

        final CompactMapNode src = this;
        final CompactMapNode dst = (CompactMapNode) (unsafe.allocateInstance(dstClass));

        final long[] srcArrayOffsets =
            (long[]) unsafe.getObject(srcClass, globalArrayOffsetsOffset);

        final long[] dstArrayOffsets =
            (long[]) unsafe.getObject(dstClass, globalArrayOffsetsOffset);

        // copy and update bitmaps
        final byte newNodeMap = unsafe.getByte(src, globalNodeMapOffset);
        unsafe.putByte(dst, globalNodeMapOffset, newNodeMap);

        // copy and update bitmaps
        final byte newDataMap = unsafe.getByte(src, globalDataMapOffset);
        unsafe.putByte(dst, globalDataMapOffset, newDataMap);

        // copy payload range (isRare = true)
        for (int i = 0; i < rarePayloadArity(); i++) {
          unsafe.putObject(dst,
              dstArrayOffsets[dst.logicalToPhysicalIndex(ContentType.RARE_KEY, i)],
              unsafe.getObject(src,
                  srcArrayOffsets[src.logicalToPhysicalIndex(ContentType.RARE_KEY, i)]));
          unsafe.putObject(dst,
              dstArrayOffsets[dst.logicalToPhysicalIndex(ContentType.RARE_VAL, i)],
              unsafe.getObject(src,
                  srcArrayOffsets[src.logicalToPhysicalIndex(ContentType.RARE_VAL, i)]));
        }

        // copy payload range (isRare = false)
        for (int i = 0; i < valIdx; i++) {
          unsafe.putInt(dst, dstArrayOffsets[dst.logicalToPhysicalIndex(ContentType.KEY, i)],
              unsafe.getInt(src, srcArrayOffsets[src.logicalToPhysicalIndex(ContentType.KEY, i)]));
          unsafe.putInt(dst, dstArrayOffsets[dst.logicalToPhysicalIndex(ContentType.VAL, i)],
              unsafe.getInt(src, srcArrayOffsets[src.logicalToPhysicalIndex(ContentType.VAL, i)]));
        }

        unsafe.putInt(dst, dstArrayOffsets[dst.logicalToPhysicalIndex(ContentType.KEY, valIdx)],
            unsafe.getInt(src,
                srcArrayOffsets[src.logicalToPhysicalIndex(ContentType.KEY, valIdx)]));
        unsafe.putInt(dst, dstArrayOffsets[dst.logicalToPhysicalIndex(ContentType.VAL, valIdx)],
            val);

        for (int i = valIdx + 1; i < payloadArity(); i++) {
          unsafe.putInt(dst, dstArrayOffsets[dst.logicalToPhysicalIndex(ContentType.KEY, i)],
              unsafe.getInt(src, srcArrayOffsets[src.logicalToPhysicalIndex(ContentType.KEY, i)]));
          unsafe.putInt(dst, dstArrayOffsets[dst.logicalToPhysicalIndex(ContentType.VAL, i)],
              unsafe.getInt(src, srcArrayOffsets[src.logicalToPhysicalIndex(ContentType.VAL, i)]));
        }

        // copy node range
        for (int i = 0; i < nodeArity(); i++) {
          unsafe.putObject(dst, dstArrayOffsets[dst.logicalToPhysicalIndex(ContentType.NODE, i)],
              unsafe.getObject(src,
                  srcArrayOffsets[src.logicalToPhysicalIndex(ContentType.NODE, i)]));
        }

        return dst;
      } catch (InstantiationException e) {
        throw new RuntimeException(e);
      }
    }

    CompactMapNode copyAndSetValue(final AtomicReference<Thread> mutator, final byte bitpos,
        final Object val) {
      try {
        final int valIdx = dataIndex(bitpos);

        final Class srcClass = this.getClass();
        final Class dstClass = specializationsByContentAndNodes[payloadArity()][untypedSlotArity()];

        if (dstClass == null) {
          throw new RuntimeException(
              String.format("[%s] No new specialization [payloadArity=%d, nodeArity=%d].",
                  srcClass.getName(), payloadArity(), nodeArity()));
        }

        final CompactMapNode src = this;
        final CompactMapNode dst = (CompactMapNode) (unsafe.allocateInstance(dstClass));

        final long[] srcArrayOffsets =
            (long[]) unsafe.getObject(srcClass, globalArrayOffsetsOffset);

        final long[] dstArrayOffsets =
            (long[]) unsafe.getObject(dstClass, globalArrayOffsetsOffset);

        // copy and update bitmaps
        final byte newNodeMap = unsafe.getByte(src, globalNodeMapOffset);
        unsafe.putByte(dst, globalNodeMapOffset, newNodeMap);

        // copy and update bitmaps
        final byte newDataMap = unsafe.getByte(src, globalDataMapOffset);
        unsafe.putByte(dst, globalDataMapOffset, newDataMap);

        // copy payload range (isRare = false)
        for (int i = 0; i < payloadArity(); i++) {
          unsafe.putInt(dst, dstArrayOffsets[dst.logicalToPhysicalIndex(ContentType.KEY, i)],
              unsafe.getInt(src, srcArrayOffsets[src.logicalToPhysicalIndex(ContentType.KEY, i)]));
          unsafe.putInt(dst, dstArrayOffsets[dst.logicalToPhysicalIndex(ContentType.VAL, i)],
              unsafe.getInt(src, srcArrayOffsets[src.logicalToPhysicalIndex(ContentType.VAL, i)]));
        }

        // copy payload range (isRare = true)
        for (int i = 0; i < valIdx; i++) {
          unsafe.putObject(dst,
              dstArrayOffsets[dst.logicalToPhysicalIndex(ContentType.RARE_KEY, i)],
              unsafe.getObject(src,
                  srcArrayOffsets[src.logicalToPhysicalIndex(ContentType.RARE_KEY, i)]));
          unsafe.putObject(dst,
              dstArrayOffsets[dst.logicalToPhysicalIndex(ContentType.RARE_VAL, i)],
              unsafe.getObject(src,
                  srcArrayOffsets[src.logicalToPhysicalIndex(ContentType.RARE_VAL, i)]));
        }

        unsafe.putObject(dst,
            dstArrayOffsets[dst.logicalToPhysicalIndex(ContentType.RARE_KEY, valIdx)],
            unsafe.getObject(src,
                srcArrayOffsets[src.logicalToPhysicalIndex(ContentType.RARE_KEY, valIdx)]));
        unsafe.putObject(dst,
            dstArrayOffsets[dst.logicalToPhysicalIndex(ContentType.RARE_VAL, valIdx)],
            unsafe.getObject(src,
                srcArrayOffsets[src.logicalToPhysicalIndex(ContentType.RARE_VAL, valIdx)]));

        for (int i = valIdx + 1; i < rarePayloadArity(); i++) {
          unsafe.putObject(dst,
              dstArrayOffsets[dst.logicalToPhysicalIndex(ContentType.RARE_KEY, i)],
              unsafe.getObject(src,
                  srcArrayOffsets[src.logicalToPhysicalIndex(ContentType.RARE_KEY, i)]));
          unsafe.putObject(dst,
              dstArrayOffsets[dst.logicalToPhysicalIndex(ContentType.RARE_VAL, i)],
              unsafe.getObject(src,
                  srcArrayOffsets[src.logicalToPhysicalIndex(ContentType.RARE_VAL, i)]));
        }

        // copy node range
        for (int i = 0; i < nodeArity(); i++) {
          unsafe.putObject(dst, dstArrayOffsets[dst.logicalToPhysicalIndex(ContentType.NODE, i)],
              unsafe.getObject(src,
                  srcArrayOffsets[src.logicalToPhysicalIndex(ContentType.NODE, i)]));
        }

        return dst;
      } catch (InstantiationException e) {
        throw new RuntimeException(e);
      }
    }

    CompactMapNode copyAndInsertValue(final AtomicReference<Thread> mutator, final byte bitpos,
        final int key, final int val) {
      try {
        final int valIdx = dataIndex(bitpos);

        final Class srcClass = this.getClass();
        final Class dstClass =
            specializationsByContentAndNodes[payloadArity() + 1][untypedSlotArity()];

        if (dstClass == null) {
          throw new RuntimeException(
              String.format("[%s] No new specialization [payloadArity=%d, nodeArity=%d].",
                  srcClass.getName(), payloadArity(), nodeArity()));
        }

        final CompactMapNode src = this;
        final CompactMapNode dst = (CompactMapNode) (unsafe.allocateInstance(dstClass));

        final long[] srcArrayOffsets =
            (long[]) unsafe.getObject(srcClass, globalArrayOffsetsOffset);

        final long[] dstArrayOffsets =
            (long[]) unsafe.getObject(dstClass, globalArrayOffsetsOffset);

        // copy and update bitmaps
        final byte newNodeMap = unsafe.getByte(src, globalNodeMapOffset);
        unsafe.putByte(dst, globalNodeMapOffset, newNodeMap);

        // copy and update bitmaps
        final byte newDataMap = (byte) (unsafe.getByte(src, globalDataMapOffset) | bitpos);
        unsafe.putByte(dst, globalDataMapOffset, newDataMap);

        // copy payload range (isRare = true)
        for (int i = 0; i < rarePayloadArity(); i++) {
          unsafe.putObject(dst,
              dstArrayOffsets[dst.logicalToPhysicalIndex(ContentType.RARE_KEY, i)],
              unsafe.getObject(src,
                  srcArrayOffsets[src.logicalToPhysicalIndex(ContentType.RARE_KEY, i)]));
          unsafe.putObject(dst,
              dstArrayOffsets[dst.logicalToPhysicalIndex(ContentType.RARE_VAL, i)],
              unsafe.getObject(src,
                  srcArrayOffsets[src.logicalToPhysicalIndex(ContentType.RARE_VAL, i)]));
        }

        // copy payload range (isRare = false)
        for (int i = 0; i < valIdx; i++) {
          unsafe.putInt(dst, dstArrayOffsets[dst.logicalToPhysicalIndex(ContentType.KEY, i)],
              unsafe.getInt(src, srcArrayOffsets[src.logicalToPhysicalIndex(ContentType.KEY, i)]));
          unsafe.putInt(dst, dstArrayOffsets[dst.logicalToPhysicalIndex(ContentType.VAL, i)],
              unsafe.getInt(src, srcArrayOffsets[src.logicalToPhysicalIndex(ContentType.VAL, i)]));
        }

        unsafe.putInt(dst, dstArrayOffsets[dst.logicalToPhysicalIndex(ContentType.KEY, valIdx)],
            key);
        unsafe.putInt(dst, dstArrayOffsets[dst.logicalToPhysicalIndex(ContentType.VAL, valIdx)],
            val);

        for (int i = valIdx; i < payloadArity(); i++) {
          unsafe.putInt(dst, dstArrayOffsets[dst.logicalToPhysicalIndex(ContentType.KEY, i + 1)],
              unsafe.getInt(src, srcArrayOffsets[src.logicalToPhysicalIndex(ContentType.KEY, i)]));
          unsafe.putInt(dst, dstArrayOffsets[dst.logicalToPhysicalIndex(ContentType.VAL, i + 1)],
              unsafe.getInt(src, srcArrayOffsets[src.logicalToPhysicalIndex(ContentType.VAL, i)]));
        }

        // copy node range
        for (int i = 0; i < nodeArity(); i++) {
          unsafe.putObject(dst, dstArrayOffsets[dst.logicalToPhysicalIndex(ContentType.NODE, i)],
              unsafe.getObject(src,
                  srcArrayOffsets[src.logicalToPhysicalIndex(ContentType.NODE, i)]));
        }

        checkSanity(dst);
        return dst;
      } catch (InstantiationException e) {
        throw new RuntimeException(e);
      }
    }

    CompactMapNode copyAndInsertValue(final AtomicReference<Thread> mutator, final byte bitpos,
        final Object key, final Object val) {
      try {
        final int valIdx = rareIndex(bitpos);

        final Class srcClass = this.getClass();
        final Class dstClass =
            specializationsByContentAndNodes[payloadArity()][untypedSlotArity() + TUPLE_LENGTH];

        if (dstClass == null) {
          throw new RuntimeException(
              String.format("[%s] No new specialization [payloadArity=%d, nodeArity=%d].",
                  srcClass.getName(), payloadArity(), nodeArity()));
        }

        final CompactMapNode src = this;
        final CompactMapNode dst = (CompactMapNode) (unsafe.allocateInstance(dstClass));

        final long[] srcArrayOffsets =
            (long[]) unsafe.getObject(srcClass, globalArrayOffsetsOffset);

        final long[] dstArrayOffsets =
            (long[]) unsafe.getObject(dstClass, globalArrayOffsetsOffset);

        // copy and update bitmaps
        final byte newNodeMap = (byte) (unsafe.getByte(src, globalNodeMapOffset) | bitpos);
        unsafe.putByte(dst, globalNodeMapOffset, newNodeMap);

        // copy and update bitmaps
        final byte newDataMap = (byte) (unsafe.getByte(src, globalDataMapOffset) | bitpos);
        unsafe.putByte(dst, globalDataMapOffset, newDataMap);

        // copy payload range (isRare = false)
        for (int i = 0; i < payloadArity(); i++) {
          unsafe.putInt(dst, dstArrayOffsets[dst.logicalToPhysicalIndex(ContentType.KEY, i)],
              unsafe.getInt(src, srcArrayOffsets[src.logicalToPhysicalIndex(ContentType.KEY, i)]));
          unsafe.putInt(dst, dstArrayOffsets[dst.logicalToPhysicalIndex(ContentType.VAL, i)],
              unsafe.getInt(src, srcArrayOffsets[src.logicalToPhysicalIndex(ContentType.VAL, i)]));
        }

        // copy payload range (isRare = true)
        for (int i = 0; i < valIdx; i++) {
          unsafe.putObject(dst,
              dstArrayOffsets[dst.logicalToPhysicalIndex(ContentType.RARE_KEY, i)],
              unsafe.getObject(src,
                  srcArrayOffsets[src.logicalToPhysicalIndex(ContentType.RARE_KEY, i)]));
          unsafe.putObject(dst,
              dstArrayOffsets[dst.logicalToPhysicalIndex(ContentType.RARE_VAL, i)],
              unsafe.getObject(src,
                  srcArrayOffsets[src.logicalToPhysicalIndex(ContentType.RARE_VAL, i)]));
        }

        unsafe.putObject(dst,
            dstArrayOffsets[dst.logicalToPhysicalIndex(ContentType.RARE_KEY, valIdx)], key);
        unsafe.putObject(dst,
            dstArrayOffsets[dst.logicalToPhysicalIndex(ContentType.RARE_VAL, valIdx)], val);

        for (int i = valIdx; i < rarePayloadArity(); i++) {
          unsafe.putObject(dst,
              dstArrayOffsets[dst.logicalToPhysicalIndex(ContentType.RARE_KEY, i + 1)],
              unsafe.getObject(src,
                  srcArrayOffsets[src.logicalToPhysicalIndex(ContentType.RARE_KEY, i)]));
          unsafe.putObject(dst,
              dstArrayOffsets[dst.logicalToPhysicalIndex(ContentType.RARE_VAL, i + 1)],
              unsafe.getObject(src,
                  srcArrayOffsets[src.logicalToPhysicalIndex(ContentType.RARE_VAL, i)]));
        }

        // copy node range
        for (int i = 0; i < nodeArity(); i++) {
          unsafe.putObject(dst, dstArrayOffsets[dst.logicalToPhysicalIndex(ContentType.NODE, i)],
              unsafe.getObject(src,
                  srcArrayOffsets[src.logicalToPhysicalIndex(ContentType.NODE, i)]));
        }

        if ((src.rarePayloadArity() + 1) != dst.rarePayloadArity())
          throw new RuntimeException();
        checkSanity(dst);
        return dst;
      } catch (InstantiationException e) {
        throw new RuntimeException(e);
      }
    }

    void checkSanity(CompactMapNode dst) {
      for (int i = 0; i < dst.rarePayloadArity(); i++) {
        if ((dst.getRareKey(i) instanceof CompactMapNode) == true)
          throw new RuntimeException();
        if ((dst.getRareVal(i) instanceof CompactMapNode) == true)
          throw new RuntimeException();
      }

      for (int i = 0; i < dst.nodeArity(); i++) {
        if ((dst.getNode(i) instanceof CompactMapNode) == false)
          throw new RuntimeException();
      }
    }

    CompactMapNode copyAndRemoveValue(final AtomicReference<Thread> mutator, final byte bitpos) {
      try {
        final int valIdx = dataIndex(bitpos);

        final Class srcClass = this.getClass();
        final Class dstClass = specializationsByContentAndNodes[payloadArity() - 1][nodeArity()];

        if (dstClass == null) {
          throw new RuntimeException(
              String.format("[%s] No new specialization [payloadArity=%d, nodeArity=%d].",
                  srcClass.getName(), payloadArity(), nodeArity()));
        }

        final CompactMapNode src = this;
        final CompactMapNode dst = (CompactMapNode) (unsafe.allocateInstance(dstClass));

        final long[] srcArrayOffsets =
            (long[]) unsafe.getObject(srcClass, globalArrayOffsetsOffset);

        final long[] dstArrayOffsets =
            (long[]) unsafe.getObject(dstClass, globalArrayOffsetsOffset);

        // copy and update bitmaps
        final byte newNodeMap = unsafe.getByte(src, globalNodeMapOffset);
        unsafe.putByte(dst, globalNodeMapOffset, newNodeMap);

        // copy and update bitmaps
        final byte newDataMap = (byte) (unsafe.getByte(src, globalDataMapOffset) ^ bitpos);
        unsafe.putByte(dst, globalDataMapOffset, newDataMap);

        // copy payload range
        for (int i = 0; i < valIdx; i++) {
          unsafe.putInt(dst, dstArrayOffsets[dst.logicalToPhysicalIndex(ContentType.KEY, i)],
              unsafe.getInt(src, srcArrayOffsets[src.logicalToPhysicalIndex(ContentType.KEY, i)]));
          unsafe.putInt(dst, dstArrayOffsets[dst.logicalToPhysicalIndex(ContentType.VAL, i)],
              unsafe.getInt(src, srcArrayOffsets[src.logicalToPhysicalIndex(ContentType.VAL, i)]));
        }
        for (int i = valIdx + 1; i < payloadArity(); i++) {
          unsafe.putInt(dst, dstArrayOffsets[dst.logicalToPhysicalIndex(ContentType.KEY, i - 1)],
              unsafe.getInt(src, srcArrayOffsets[src.logicalToPhysicalIndex(ContentType.KEY, i)]));
          unsafe.putInt(dst, dstArrayOffsets[dst.logicalToPhysicalIndex(ContentType.VAL, i - 1)],
              unsafe.getInt(src, srcArrayOffsets[src.logicalToPhysicalIndex(ContentType.VAL, i)]));
        }

        // copy node range
        for (int i = 0; i < nodeArity(); i++) {
          unsafe.putObject(dst, dstArrayOffsets[dst.logicalToPhysicalIndex(ContentType.NODE, i)],
              unsafe.getObject(src,
                  srcArrayOffsets[src.logicalToPhysicalIndex(ContentType.NODE, i)]));
        }

        return dst;
      } catch (InstantiationException e) {
        throw new RuntimeException(e);
      }
    }

    CompactMapNode copyAndSetNode(final AtomicReference<Thread> mutator, final byte bitpos,
        final AbstractMapNode node) {
      try {
        final int idx = nodeIndex(bitpos);

        final Class srcClass = this.getClass();
        final Class dstClass = specializationsByContentAndNodes[payloadArity()][untypedSlotArity()];

        if (dstClass == null) {
          throw new RuntimeException(
              String.format("[%s] No new specialization [payloadArity=%d, nodeArity=%d].",
                  srcClass.getName(), payloadArity(), nodeArity()));
        }

        final CompactMapNode src = this;
        final CompactMapNode dst = (CompactMapNode) (unsafe.allocateInstance(dstClass));

        final long[] srcArrayOffsets =
            (long[]) unsafe.getObject(srcClass, globalArrayOffsetsOffset);

        final long[] dstArrayOffsets =
            (long[]) unsafe.getObject(dstClass, globalArrayOffsetsOffset);

        // copy and update bitmaps
        final byte newNodeMap = unsafe.getByte(src, globalNodeMapOffset);
        unsafe.putByte(dst, globalNodeMapOffset, newNodeMap);

        // copy and update bitmaps
        final byte newDataMap = unsafe.getByte(src, globalDataMapOffset);
        unsafe.putByte(dst, globalDataMapOffset, newDataMap);

        // copy payload range (isRare = true)
        for (int i = 0; i < rarePayloadArity(); i++) {
          unsafe.putObject(dst,
              dstArrayOffsets[dst.logicalToPhysicalIndex(ContentType.RARE_KEY, i)],
              unsafe.getObject(src,
                  srcArrayOffsets[src.logicalToPhysicalIndex(ContentType.RARE_KEY, i)]));
          unsafe.putObject(dst,
              dstArrayOffsets[dst.logicalToPhysicalIndex(ContentType.RARE_VAL, i)],
              unsafe.getObject(src,
                  srcArrayOffsets[src.logicalToPhysicalIndex(ContentType.RARE_VAL, i)]));
        }

        // copy payload range (isRare = false)
        for (int i = 0; i < payloadArity(); i++) {
          unsafe.putInt(dst, dstArrayOffsets[dst.logicalToPhysicalIndex(ContentType.KEY, i)],
              unsafe.getInt(src, srcArrayOffsets[src.logicalToPhysicalIndex(ContentType.KEY, i)]));
          unsafe.putInt(dst, dstArrayOffsets[dst.logicalToPhysicalIndex(ContentType.VAL, i)],
              unsafe.getInt(src, srcArrayOffsets[src.logicalToPhysicalIndex(ContentType.VAL, i)]));
        }

        // copy node range
        for (int i = 0; i < idx; i++) {
          unsafe.putObject(dst, dstArrayOffsets[dst.logicalToPhysicalIndex(ContentType.NODE, i)],
              unsafe.getObject(src,
                  srcArrayOffsets[src.logicalToPhysicalIndex(ContentType.NODE, i)]));
        }

        unsafe.putObject(dst, dstArrayOffsets[dst.logicalToPhysicalIndex(ContentType.NODE, idx)],
            node);

        for (int i = idx + 1; i < nodeArity(); i++) {
          unsafe.putObject(dst, dstArrayOffsets[dst.logicalToPhysicalIndex(ContentType.NODE, i)],
              unsafe.getObject(src,
                  srcArrayOffsets[src.logicalToPhysicalIndex(ContentType.NODE, i)]));
        }

        return dst;
      } catch (InstantiationException e) {
        throw new RuntimeException(e);
      }
    }

    CompactMapNode copyAndMigrateFromInlineToNode(final AtomicReference<Thread> mutator,
        final byte bitpos, final AbstractMapNode node) {
      try {
        final int idxOld = dataIndex(bitpos);
        final int idxNew = nodeIndex(bitpos);

        final Class srcClass = this.getClass();
        final Class dstClass =
            specializationsByContentAndNodes[payloadArity() - 1][untypedSlotArity() + 1];

        if (dstClass == null) {
          throw new RuntimeException(
              String.format("[%s] No new specialization [payloadArity=%d, nodeArity=%d].",
                  srcClass.getName(), payloadArity(), nodeArity()));
        }

        final CompactMapNode src = this;
        final CompactMapNode dst = (CompactMapNode) (unsafe.allocateInstance(dstClass));

        final long[] srcArrayOffsets =
            (long[]) unsafe.getObject(srcClass, globalArrayOffsetsOffset);

        final long[] dstArrayOffsets =
            (long[]) unsafe.getObject(dstClass, globalArrayOffsetsOffset);

        // idempotent operation; in case of rare bit was already set before

        // copy and update bitmaps
        final byte newNodeMap = (byte) (unsafe.getByte(src, globalNodeMapOffset) | bitpos);
        unsafe.putByte(dst, globalNodeMapOffset, newNodeMap);

        // copy and update bitmaps
        final byte newDataMap = (byte) (unsafe.getByte(src, globalDataMapOffset) ^ bitpos);
        unsafe.putByte(dst, globalDataMapOffset, newDataMap);

        // copy payload range (isRare = true)
        for (int i = 0; i < rarePayloadArity(); i++) {
          unsafe.putObject(dst,
              dstArrayOffsets[dst.logicalToPhysicalIndex(ContentType.RARE_KEY, i)],
              unsafe.getObject(src,
                  srcArrayOffsets[src.logicalToPhysicalIndex(ContentType.RARE_KEY, i)]));
          unsafe.putObject(dst,
              dstArrayOffsets[dst.logicalToPhysicalIndex(ContentType.RARE_VAL, i)],
              unsafe.getObject(src,
                  srcArrayOffsets[src.logicalToPhysicalIndex(ContentType.RARE_VAL, i)]));
        }

        // copy payload range (isRare = false)
        for (int i = 0; i < idxOld; i++) {
          unsafe.putInt(dst, dstArrayOffsets[dst.logicalToPhysicalIndex(ContentType.KEY, i)],
              unsafe.getInt(src, srcArrayOffsets[src.logicalToPhysicalIndex(ContentType.KEY, i)]));
          unsafe.putInt(dst, dstArrayOffsets[dst.logicalToPhysicalIndex(ContentType.VAL, i)],
              unsafe.getInt(src, srcArrayOffsets[src.logicalToPhysicalIndex(ContentType.VAL, i)]));
        }

        for (int i = idxOld + 1; i < payloadArity(); i++) {
          unsafe.putInt(dst, dstArrayOffsets[dst.logicalToPhysicalIndex(ContentType.KEY, i - 1)],
              unsafe.getInt(src, srcArrayOffsets[src.logicalToPhysicalIndex(ContentType.KEY, i)]));
          unsafe.putInt(dst, dstArrayOffsets[dst.logicalToPhysicalIndex(ContentType.VAL, i - 1)],
              unsafe.getInt(src, srcArrayOffsets[src.logicalToPhysicalIndex(ContentType.VAL, i)]));
        }

        // copy node range
        for (int i = 0; i < idxNew; i++) {
          unsafe.putObject(dst, dstArrayOffsets[dst.logicalToPhysicalIndex(ContentType.NODE, i)],
              unsafe.getObject(src,
                  srcArrayOffsets[src.logicalToPhysicalIndex(ContentType.NODE, i)]));
        }

        unsafe.putObject(dst, dstArrayOffsets[dst.logicalToPhysicalIndex(ContentType.NODE, idxNew)],
            node);

        for (int i = idxNew; i < nodeArity(); i++) {
          unsafe.putObject(dst,
              dstArrayOffsets[dst.logicalToPhysicalIndex(ContentType.NODE, i + 1)],
              unsafe.getObject(src,
                  srcArrayOffsets[src.logicalToPhysicalIndex(ContentType.NODE, i)]));
        }

        if (src.rarePayloadArity() != dst.rarePayloadArity())
          throw new RuntimeException();
        checkSanity(dst);
        return dst;
      } catch (InstantiationException e) {
        throw new RuntimeException(e);
      }
    }

    CompactMapNode copyAndMigrateFromRareInlineToNode(final AtomicReference<Thread> mutator,
        final byte bitpos, final AbstractMapNode node) {
      try {
        final int idxOld = rareIndex(bitpos);
        final int idxNew = nodeIndex(bitpos);

        final Class srcClass = this.getClass();
        final Class dstClass =
            specializationsByContentAndNodes[payloadArity()][untypedSlotArity() - TUPLE_LENGTH + 1];

        if (dstClass == null) {
          throw new RuntimeException(
              String.format("[%s] No new specialization [payloadArity=%d, nodeArity=%d].",
                  srcClass.getName(), payloadArity(), nodeArity()));
        }

        final CompactMapNode src = this;
        final CompactMapNode dst = (CompactMapNode) (unsafe.allocateInstance(dstClass));

        final long[] srcArrayOffsets =
            (long[]) unsafe.getObject(srcClass, globalArrayOffsetsOffset);

        final long[] dstArrayOffsets =
            (long[]) unsafe.getObject(dstClass, globalArrayOffsetsOffset);

        // idempotent operation; in case of rare bit was already set before

        // copy and update bitmaps
        final byte newNodeMap = (byte) (unsafe.getByte(src, globalNodeMapOffset) | bitpos);
        unsafe.putByte(dst, globalNodeMapOffset, newNodeMap);

        // copy and update bitmaps
        final byte newDataMap = (byte) (unsafe.getByte(src, globalDataMapOffset) ^ bitpos);
        unsafe.putByte(dst, globalDataMapOffset, newDataMap);

        // copy payload range (isRare = false)
        for (int i = 0; i < payloadArity(); i++) {
          unsafe.putInt(dst, dstArrayOffsets[dst.logicalToPhysicalIndex(ContentType.KEY, i)],
              unsafe.getInt(src, srcArrayOffsets[src.logicalToPhysicalIndex(ContentType.KEY, i)]));
          unsafe.putInt(dst, dstArrayOffsets[dst.logicalToPhysicalIndex(ContentType.VAL, i)],
              unsafe.getInt(src, srcArrayOffsets[src.logicalToPhysicalIndex(ContentType.VAL, i)]));
        }

        // copy payload range (isRare = true)
        for (int i = 0; i < idxOld; i++) {
          unsafe.putObject(dst,
              dstArrayOffsets[dst.logicalToPhysicalIndex(ContentType.RARE_KEY, i)],
              unsafe.getObject(src,
                  srcArrayOffsets[src.logicalToPhysicalIndex(ContentType.RARE_KEY, i)]));
          unsafe.putObject(dst,
              dstArrayOffsets[dst.logicalToPhysicalIndex(ContentType.RARE_VAL, i)],
              unsafe.getObject(src,
                  srcArrayOffsets[src.logicalToPhysicalIndex(ContentType.RARE_VAL, i)]));
        }

        for (int i = idxOld + 1; i < rarePayloadArity(); i++) {
          unsafe.putObject(dst,
              dstArrayOffsets[dst.logicalToPhysicalIndex(ContentType.RARE_KEY, i - 1)],
              unsafe.getObject(src,
                  srcArrayOffsets[src.logicalToPhysicalIndex(ContentType.RARE_KEY, i)]));
          unsafe.putObject(dst,
              dstArrayOffsets[dst.logicalToPhysicalIndex(ContentType.RARE_VAL, i - 1)],
              unsafe.getObject(src,
                  srcArrayOffsets[src.logicalToPhysicalIndex(ContentType.RARE_VAL, i)]));
        }

        // copy node range
        for (int i = 0; i < idxNew; i++) {
          unsafe.putObject(dst, dstArrayOffsets[dst.logicalToPhysicalIndex(ContentType.NODE, i)],
              unsafe.getObject(src,
                  srcArrayOffsets[src.logicalToPhysicalIndex(ContentType.NODE, i)]));
        }

        unsafe.putObject(dst, dstArrayOffsets[dst.logicalToPhysicalIndex(ContentType.NODE, idxNew)],
            node);

        for (int i = idxNew; i < nodeArity(); i++) {
          unsafe.putObject(dst,
              dstArrayOffsets[dst.logicalToPhysicalIndex(ContentType.NODE, i + 1)],
              unsafe.getObject(src,
                  srcArrayOffsets[src.logicalToPhysicalIndex(ContentType.NODE, i)]));
        }

        if ((src.rarePayloadArity() - 1) != dst.rarePayloadArity())
          throw new RuntimeException();
        checkSanity(dst);
        return dst;
      } catch (InstantiationException e) {
        throw new RuntimeException(e);
      }
    }

    CompactMapNode copyAndMigrateFromNodeToInline(final AtomicReference<Thread> mutator,
        final byte bitpos, final AbstractMapNode node) {
      try {
        final int idxOld = nodeIndex(bitpos);
        final int idxNew = dataIndex(bitpos);

        final Class srcClass = this.getClass();
        final Class dstClass =
            specializationsByContentAndNodes[payloadArity() + 1][nodeArity() - 1];

        if (dstClass == null) {
          throw new RuntimeException(
              String.format("[%s] No new specialization [payloadArity=%d, nodeArity=%d].",
                  srcClass.getName(), payloadArity(), nodeArity()));
        }

        final CompactMapNode src = this;
        final CompactMapNode dst = (CompactMapNode) (unsafe.allocateInstance(dstClass));

        final long[] srcArrayOffsets =
            (long[]) unsafe.getObject(srcClass, globalArrayOffsetsOffset);

        final long[] dstArrayOffsets =
            (long[]) unsafe.getObject(dstClass, globalArrayOffsetsOffset);

        // copy and update bitmaps
        final byte newNodeMap = (byte) (unsafe.getByte(src, globalNodeMapOffset) ^ bitpos);
        unsafe.putByte(dst, globalNodeMapOffset, newNodeMap);

        // copy and update bitmaps
        final byte newDataMap = (byte) (unsafe.getByte(src, globalDataMapOffset) | bitpos);
        unsafe.putByte(dst, globalDataMapOffset, newDataMap);

        // copy payload range
        for (int i = 0; i < idxNew; i++) {
          unsafe.putInt(dst, dstArrayOffsets[dst.logicalToPhysicalIndex(ContentType.KEY, i)],
              unsafe.getInt(src, srcArrayOffsets[src.logicalToPhysicalIndex(ContentType.KEY, i)]));
          unsafe.putInt(dst, dstArrayOffsets[dst.logicalToPhysicalIndex(ContentType.VAL, i)],
              unsafe.getInt(src, srcArrayOffsets[src.logicalToPhysicalIndex(ContentType.VAL, i)]));
        }

        unsafe.putInt(dst, dstArrayOffsets[dst.logicalToPhysicalIndex(ContentType.KEY, idxNew)],
            node.getKey(0));
        unsafe.putInt(dst, dstArrayOffsets[dst.logicalToPhysicalIndex(ContentType.VAL, idxNew)],
            node.getVal(0));

        for (int i = idxNew; i < payloadArity(); i++) {
          unsafe.putInt(dst, dstArrayOffsets[dst.logicalToPhysicalIndex(ContentType.KEY, i + 1)],
              unsafe.getInt(src, srcArrayOffsets[src.logicalToPhysicalIndex(ContentType.KEY, i)]));
          unsafe.putInt(dst, dstArrayOffsets[dst.logicalToPhysicalIndex(ContentType.VAL, i + 1)],
              unsafe.getInt(src, srcArrayOffsets[src.logicalToPhysicalIndex(ContentType.VAL, i)]));
        }

        // copy node range
        for (int i = 0; i < idxOld; i++) {
          unsafe.putObject(dst, dstArrayOffsets[dst.logicalToPhysicalIndex(ContentType.NODE, i)],
              unsafe.getObject(src,
                  srcArrayOffsets[src.logicalToPhysicalIndex(ContentType.NODE, i)]));
        }
        for (int i = idxOld + 1; i < nodeArity(); i++) {
          unsafe.putObject(dst,
              dstArrayOffsets[dst.logicalToPhysicalIndex(ContentType.NODE, i - 1)],
              unsafe.getObject(src,
                  srcArrayOffsets[src.logicalToPhysicalIndex(ContentType.NODE, i)]));
        }

        return dst;
      } catch (InstantiationException e) {
        throw new RuntimeException(e);
      }
    }

    static final AbstractMapNode mergeTwoKeyValPairs(final Object key0, final Object val0,
        final int keyHash0, final Object key1, final Object val1, final int keyHash1,
        final int shift) {
      // assert !(key0 == key1);

      // if (shift >= hashCodeLength()) {
      // return new HashCollisionMapNode_Heterogeneous_BleedingEdge(keyHash0, (int[]) new int[] {
      // key0, key1 }
      // , (int[]) new int[] { val0, val1 });
      // }

      final int mask0 = mask(keyHash0, shift);
      final int mask1 = mask(keyHash1, shift);

      if (mask0 != mask1) {
        // both nodes fit on same level
        final byte nodeMap = (byte) (bitpos(mask0) | bitpos(mask1));
        final byte dataMap = (byte) (bitpos(mask0) | bitpos(mask1));

        if (mask0 < mask1) {
          return nodeOf4x0(null, nodeMap, dataMap, key0, val0, key1, val1);
        } else {
          return nodeOf4x0(null, nodeMap, dataMap, key1, val1, key0, val0);
        }
      } else {
        final AbstractMapNode node = mergeTwoKeyValPairs(key0, val0, keyHash0, key1, val1, keyHash1,
            shift + bitPartitionSize());
        // values fit on next level

        final byte nodeMap = bitpos(mask0);
        return nodeOf1x0(null, nodeMap, (byte) 0, node);
      }
    }

    static final AbstractMapNode mergeTwoKeyValPairs(final Object key0, final Object val0,
        final int keyHash0, final int key1, final int val1, final int keyHash1, final int shift) {
      // assert !(key0 == key1);

      // if (shift >= hashCodeLength()) {
      // return new HashCollisionMapNode_Heterogeneous_BleedingEdge(keyHash0, (int[]) new int[] {
      // key0, key1 }
      // , (int[]) new int[] { val0, val1 });
      // }

      final int mask0 = mask(keyHash0, shift);
      final int mask1 = mask(keyHash1, shift);

      if (mask0 != mask1) {
        // both nodes fit on same level
        final byte nodeMap = (bitpos(mask0));
        final byte dataMap = (byte) (bitpos(mask0) | bitpos(mask1));

        // convention: rare after base
        return nodeOf2x1(null, nodeMap, dataMap, key1, val1, key0, val0);
      } else {
        final AbstractMapNode node = mergeTwoKeyValPairs(key0, val0, keyHash0, key1, val1, keyHash1,
            shift + bitPartitionSize());
        // values fit on next level

        final byte nodeMap = bitpos(mask0);
        return nodeOf1x0(null, nodeMap, (byte) 0, node);
      }
    }

    static final AbstractMapNode mergeTwoKeyValPairs(final int key0, final int val0,
        final int keyHash0, final Object key1, final Object val1, final int keyHash1,
        final int shift) {
      // assert !(key0 == key1);

      // if (shift >= hashCodeLength()) {
      // return new HashCollisionMapNode_Heterogeneous_BleedingEdge(keyHash0, (int[]) new int[] {
      // key0, key1 }
      // , (int[]) new int[] { val0, val1 });
      // }

      final int mask0 = mask(keyHash0, shift);
      final int mask1 = mask(keyHash1, shift);

      if (mask0 != mask1) {
        // both nodes fit on same level
        final byte nodeMap = (bitpos(mask1));
        final byte dataMap = (byte) (bitpos(mask0) | bitpos(mask1));

        // convention: rare after base
        return nodeOf2x1(null, nodeMap, dataMap, key0, val0, key1, val1);
      } else {
        final AbstractMapNode node = mergeTwoKeyValPairs(key0, val0, keyHash0, key1, val1, keyHash1,
            shift + bitPartitionSize());
        // values fit on next level

        final byte nodeMap = bitpos(mask0);
        return nodeOf1x0(null, nodeMap, (byte) 0, node);
      }
    }

    static final AbstractMapNode mergeTwoKeyValPairs(final int key0, final int val0,
        final int keyHash0, final int key1, final int val1, final int keyHash1, final int shift) {
      // assert !(key0 == key1);

      // if (shift >= hashCodeLength()) {
      // return new HashCollisionMapNode_Heterogeneous_BleedingEdge(keyHash0, (int[]) new int[] {
      // key0, key1 }
      // , (int[]) new int[] { val0, val1 });
      // }

      final int mask0 = mask(keyHash0, shift);
      final int mask1 = mask(keyHash1, shift);

      if (mask0 != mask1) {
        // both nodes fit on same level
        final byte nodeMap = 0;
        final byte dataMap = (byte) (bitpos(mask0) | bitpos(mask1));

        if (mask0 < mask1) {
          return nodeOf0x2(null, nodeMap, dataMap, key0, val0, key1, val1);
        } else {
          return nodeOf0x2(null, nodeMap, dataMap, key1, val1, key0, val0);
        }
      } else {
        final AbstractMapNode node = mergeTwoKeyValPairs(key0, val0, keyHash0, key1, val1, keyHash1,
            shift + bitPartitionSize());
        // values fit on next level

        final byte nodeMap = bitpos(mask0);
        return nodeOf1x0(null, nodeMap, (byte) 0, node);
      }
    }

    static final int index(final byte bitmap, final byte bitpos) {
      return java.lang.Integer.bitCount(bitmap & 0xFF & (bitpos - 1));
    }

    static final int index(final byte bitmap, final int mask, final byte bitpos) {
      return ((bitmap & 0xFF) == -1) ? mask : index(bitmap, bitpos);
    }

    int dataIndex(final byte bitpos) {
      return java.lang.Integer.bitCount(dataMap() & 0xFF & (bitpos - 1));
    }

    int nodeIndex(final byte bitpos) {
      return java.lang.Integer.bitCount(nodeMap() & 0xFF & (bitpos - 1));
    }

    int rareIndex(final byte bitpos) {
      return java.lang.Integer.bitCount(rareMap() & 0xFF & (bitpos - 1));
    }

    AbstractMapNode nodeAt(final byte bitpos) {
      return getNode(nodeIndex(bitpos));
    }

    private static final boolean equals(final Object o1, final Object o2) {
      if (null == o1 || null == o2) {
        return false;
      }
      if (o1 == o2) {
        return true;
      }
      if (o1.getClass() != o2.getClass()) {
        return false;
      }

      final CompactMapNode src = (CompactMapNode) o1;
      final CompactMapNode dst = (CompactMapNode) o2;

      final Class clazz = o1.getClass();
      final long[] arrayOffsets = (long[]) unsafe.getObject(clazz, globalArrayOffsetsOffset);

      // compare nodeMap
      if (!(unsafe.getByte(src, globalNodeMapOffset) == unsafe.getByte(dst, globalNodeMapOffset))) {
        return false;
      }

      // compare dataMap
      if (!(unsafe.getByte(src, globalDataMapOffset) == unsafe.getByte(dst, globalDataMapOffset))) {
        return false;
      }

      // compare payload range
      for (int i = 0; i < src.payloadArity(); i++) {
        if (!(unsafe.getInt(src,
            arrayOffsets[src.logicalToPhysicalIndex(ContentType.KEY, i)]) == unsafe.getInt(dst,
                arrayOffsets[dst.logicalToPhysicalIndex(ContentType.KEY, i)]))) {
          return false;
        }

        if (!(unsafe.getInt(src,
            arrayOffsets[src.logicalToPhysicalIndex(ContentType.VAL, i)]) == unsafe.getInt(dst,
                arrayOffsets[dst.logicalToPhysicalIndex(ContentType.VAL, i)]))) {
          return false;
        }
      }

      // compare node range
      for (int i = 0; i < src.nodeArity(); i++) {
        if (!(unsafe.getObject(src, arrayOffsets[src.logicalToPhysicalIndex(ContentType.NODE, i)])
            .equals(unsafe.getObject(dst,
                arrayOffsets[dst.logicalToPhysicalIndex(ContentType.NODE, i)])))) {
          return false;
        }
      }

      return true;
    }

    static final byte recoverMask(int map, final byte i_th) {
      assert 1 <= i_th && i_th <= 8;

      byte cnt1 = 0;
      byte mask = 0;

      while (mask < 8) {
        if ((map & 0x01) == 0x01) {
          cnt1 += 1;

          if (cnt1 == i_th) {
            return mask;
          }
        }

        map = (byte) (map >> 1);
        mask += 1;
      }

      assert cnt1 != i_th;
      throw new RuntimeException("Called with invalid arguments.");
    }

    @Override
    public String toString() {
      final StringBuilder bldr = new StringBuilder();
      bldr.append('[');
      for (byte i = 0; i < payloadArity(); i++) {
        final byte pos = recoverMask(dataMap(), (byte) (i + 1));
        bldr.append(String.format("@%d<#%d,#%d>", pos, Objects.hashCode(getKey(i)),
            Objects.hashCode(getVal(i))));
        if (!((i + 1) == payloadArity())) {
          bldr.append(", ");
        }
      }
      if (payloadArity() > 0 && nodeArity() > 0) {
        bldr.append(", ");
      }
      for (byte i = 0; i < nodeArity(); i++) {
        final byte pos = recoverMask(nodeMap(), (byte) (i + 1));
        bldr.append(String.format("@%d: %s", pos, getNode(i)));
        if (!((i + 1) == nodeArity())) {
          bldr.append(", ");
        }
      }
      bldr.append(']');
      return bldr.toString();
    }

    static final AbstractMapNode nodeOf1x0(final AtomicReference<Thread> mutator,
        final byte nodeMap, final byte dataMap, final Object slot0) {
      try {
        final Class<Map0To1Node_Heterogeneous_BleedingEdge> dstClass =
            Map0To1Node_Heterogeneous_BleedingEdge.class;

        final Map0To1Node_Heterogeneous_BleedingEdge dst =
            (Map0To1Node_Heterogeneous_BleedingEdge) (unsafe.allocateInstance(dstClass));

        unsafe.putByte(dst, globalNodeMapOffset, nodeMap);
        unsafe.putByte(dst, globalDataMapOffset, dataMap);

        // works in presence of padding
        long offset = arrayBase;
        unsafe.putObject(dst, offset, slot0);
        offset += addressSize;

        return dst;
      } catch (InstantiationException | SecurityException e) {
        throw new RuntimeException(e);
      }
    }

    static final AbstractMapNode nodeOf0x1(final AtomicReference<Thread> mutator,
        final byte nodeMap, final byte dataMap, final int key1, final int val1) {
      try {
        final Class<Map1To0Node_Heterogeneous_BleedingEdge> dstClass =
            Map1To0Node_Heterogeneous_BleedingEdge.class;

        final Map1To0Node_Heterogeneous_BleedingEdge dst =
            (Map1To0Node_Heterogeneous_BleedingEdge) (unsafe.allocateInstance(dstClass));

        unsafe.putByte(dst, globalNodeMapOffset, nodeMap);
        unsafe.putByte(dst, globalDataMapOffset, dataMap);

        // works in presence of padding
        long offset = arrayBase;
        unsafe.putInt(dst, offset, key1);
        offset += addressSize;
        unsafe.putInt(dst, offset, val1);
        offset += addressSize;

        return dst;
      } catch (InstantiationException | SecurityException e) {
        throw new RuntimeException(e);
      }
    }

    static final AbstractMapNode nodeOf0x2(final AtomicReference<Thread> mutator,
        final byte nodeMap, final byte dataMap, final int key1, final int val1, final int key2,
        final int val2) {
      try {
        final Class<Map2To0Node_Heterogeneous_BleedingEdge> dstClass =
            Map2To0Node_Heterogeneous_BleedingEdge.class;

        final Map2To0Node_Heterogeneous_BleedingEdge dst =
            (Map2To0Node_Heterogeneous_BleedingEdge) (unsafe.allocateInstance(dstClass));

        unsafe.putByte(dst, globalNodeMapOffset, nodeMap);
        unsafe.putByte(dst, globalDataMapOffset, dataMap);

        // works in presence of padding
        long offset = arrayBase;
        unsafe.putInt(dst, offset, key1);
        offset += addressSize;
        unsafe.putInt(dst, offset, val1);
        offset += addressSize;
        unsafe.putInt(dst, offset, key2);
        offset += addressSize;
        unsafe.putInt(dst, offset, val2);
        offset += addressSize;

        return dst;
      } catch (InstantiationException | SecurityException e) {
        throw new RuntimeException(e);
      }
    }

    static final AbstractMapNode nodeOf4x0(final AtomicReference<Thread> mutator,
        final byte nodeMap, final byte dataMap, final Object slot0, final Object slot1,
        final Object slot2, final Object slot3) {
      try {
        final Class<Map0To4Node_Heterogeneous_BleedingEdge> dstClass =
            Map0To4Node_Heterogeneous_BleedingEdge.class;

        final Map0To4Node_Heterogeneous_BleedingEdge dst =
            (Map0To4Node_Heterogeneous_BleedingEdge) (unsafe.allocateInstance(dstClass));

        unsafe.putByte(dst, globalNodeMapOffset, nodeMap);
        unsafe.putByte(dst, globalDataMapOffset, dataMap);

        // works in presence of padding
        long offset = arrayBase;
        unsafe.putObject(dst, offset, slot0);
        offset += addressSize;
        unsafe.putObject(dst, offset, slot1);
        offset += addressSize;
        unsafe.putObject(dst, offset, slot2);
        offset += addressSize;
        unsafe.putObject(dst, offset, slot3);
        offset += addressSize;

        return dst;
      } catch (InstantiationException | SecurityException e) {
        throw new RuntimeException(e);
      }
    }

    static final AbstractMapNode nodeOf2x1(final AtomicReference<Thread> mutator,
        final byte nodeMap, final byte dataMap, final int key1, final int val1, final Object slot0,
        final Object slot1) {
      try {
        final Class<Map1To2Node_Heterogeneous_BleedingEdge> dstClass =
            Map1To2Node_Heterogeneous_BleedingEdge.class;

        final Map1To2Node_Heterogeneous_BleedingEdge dst =
            (Map1To2Node_Heterogeneous_BleedingEdge) (unsafe.allocateInstance(dstClass));

        unsafe.putByte(dst, globalNodeMapOffset, nodeMap);
        unsafe.putByte(dst, globalDataMapOffset, dataMap);

        // works in presence of padding
        long offset = arrayBase;
        unsafe.putInt(dst, offset, key1);
        offset += addressSize;
        unsafe.putInt(dst, offset, val1);
        offset += addressSize;
        unsafe.putObject(dst, offset, slot0);
        offset += addressSize;
        unsafe.putObject(dst, offset, slot1);
        offset += addressSize;

        return dst;
      } catch (InstantiationException | SecurityException e) {
        throw new RuntimeException(e);
      }
    }

    @Override
    AbstractMapNode updated(final AtomicReference<Thread> mutator, final int key, final int val,
        final int keyHash, final int shift, final MapResult details, final Comparator<Object> cmp) {
      final int mask = mask(keyHash, shift);
      final byte bitpos = bitpos(mask);

      // check for inplace value
      final byte dataMap = dataMap();
      if (dataMap != 0 && (dataMap == -1 || (dataMap & bitpos) != 0)) {
        final int dataIndex = index(dataMap, mask, bitpos);
        final int currentKey = getKey(dataIndex);

        if (currentKey == key) {
          final int currentVal = getVal(dataIndex);

          // update mapping
          details.updated(currentVal);
          return copyAndSetValue(mutator, bitpos, val);
        } else {
          final int currentVal = getVal(dataIndex);

          final AbstractMapNode subNodeNew = mergeTwoKeyValPairs(currentKey, currentVal,
              transformHashCode(currentKey), key, val, keyHash, shift + bitPartitionSize());

          details.modified();
          return copyAndMigrateFromInlineToNode(mutator, bitpos, subNodeNew);
        }

      }

      // check for inplace (rare) value
      final byte rareMap = rareMap();
      if (rareMap != 0 && (rareMap == -1 || (rareMap & bitpos) != 0)) {
        final int rareIndex = index(rareMap, mask, bitpos);
        final Object currentKey = getRareKey(rareIndex);

        final Object currentVal = getRareVal(rareIndex);

        final AbstractMapNode subNodeNew =
            mergeTwoKeyValPairs(currentKey, currentVal, transformHashCode(currentKey.hashCode()),
                key, val, keyHash, shift + bitPartitionSize());

        details.modified();
        return copyAndMigrateFromRareInlineToNode(mutator, bitpos, subNodeNew);

      }

      // check for node (not value)
      final byte nodeMap = nodeMap();
      if (nodeMap != 0 && (nodeMap == -1 || (nodeMap & bitpos) != 0)) {
        final int nodeIndex = index(nodeMap, mask, bitpos);
        final AbstractMapNode subNode = getNode(nodeIndex);
        final AbstractMapNode subNodeNew =
            subNode.updated(mutator, key, val, keyHash, shift + bitPartitionSize(), details, cmp);

        if (details.isModified()) {
          return copyAndSetNode(mutator, bitpos, subNodeNew);
        } else {
          return this;
        }
      }

      // no value
      details.modified();
      return copyAndInsertValue(mutator, bitpos, key, val);
    }

    @Override
    AbstractMapNode updated(final AtomicReference<Thread> mutator, final int key, final int val,
        final int keyHash, final int shift, final MapResult details) {
      final int mask = mask(keyHash, shift);
      final byte bitpos = bitpos(mask);

      // check for inplace value
      final byte dataMap = dataMap();
      if (dataMap != 0 && (dataMap == -1 || (dataMap & bitpos) != 0)) {
        final int dataIndex = index(dataMap, mask, bitpos);
        final int currentKey = getKey(dataIndex);

        if (currentKey == key) {
          final int currentVal = getVal(dataIndex);

          // update mapping
          details.updated(currentVal);
          return copyAndSetValue(mutator, bitpos, val);
        } else {
          final int currentVal = getVal(dataIndex);

          final AbstractMapNode subNodeNew = mergeTwoKeyValPairs(currentKey, currentVal,
              transformHashCode(currentKey), key, val, keyHash, shift + bitPartitionSize());

          details.modified();
          return copyAndMigrateFromInlineToNode(mutator, bitpos, subNodeNew);
        }

      }

      // check for inplace (rare) value
      final byte rareMap = rareMap();
      if (rareMap != 0 && (rareMap == -1 || (rareMap & bitpos) != 0)) {
        final int rareIndex = index(rareMap, mask, bitpos);
        final Object currentKey = getRareKey(rareIndex);

        final Object currentVal = getRareVal(rareIndex);

        final AbstractMapNode subNodeNew =
            mergeTwoKeyValPairs(currentKey, currentVal, transformHashCode(currentKey.hashCode()),
                key, val, keyHash, shift + bitPartitionSize());

        details.modified();
        return copyAndMigrateFromRareInlineToNode(mutator, bitpos, subNodeNew);

      }

      // check for node (not value)
      final byte nodeMap = nodeMap();
      if (nodeMap != 0 && (nodeMap == -1 || (nodeMap & bitpos) != 0)) {
        final int nodeIndex = index(nodeMap, mask, bitpos);
        final AbstractMapNode subNode = getNode(nodeIndex);
        final AbstractMapNode subNodeNew =
            subNode.updated(mutator, key, val, keyHash, shift + bitPartitionSize(), details);

        if (details.isModified()) {
          return copyAndSetNode(mutator, bitpos, subNodeNew);
        } else {
          return this;
        }
      }

      // no value
      details.modified();
      return copyAndInsertValue(mutator, bitpos, key, val);
    }

    @Override
    Map.Entry<java.lang.Integer, java.lang.Integer> getKeyValueEntry(final int index) {
      return entryOf(getKey(index), getVal(index));
    }

    @Override
    AbstractMapNode updated(final AtomicReference<Thread> mutator, final Object key,
        final Object val, final int keyHash, final int shift, final MapResult details,
        final Comparator<Object> cmp) {
      final int mask = mask(keyHash, shift);
      final byte bitpos = bitpos(mask);

      // check for inplace value
      final byte dataMap = dataMap();
      if (dataMap != 0 && (dataMap == -1 || (dataMap & bitpos) != 0)) {
        final int dataIndex = index(dataMap, mask, bitpos);
        final int currentKey = getKey(dataIndex);

        final int currentVal = getVal(dataIndex);

        final AbstractMapNode subNodeNew = mergeTwoKeyValPairs(currentKey, currentVal,
            transformHashCode(currentKey), key, val, keyHash, shift + bitPartitionSize());

        details.modified();
        return copyAndMigrateFromInlineToNode(mutator, bitpos, subNodeNew);

      }

      // check for inplace (rare) value
      final byte rareMap = rareMap();
      if (rareMap != 0 && (rareMap == -1 || (rareMap & bitpos) != 0)) {
        final int rareIndex = index(rareMap, mask, bitpos);
        final Object currentKey = getRareKey(rareIndex);

        if (cmp.compare(currentKey, key) == 0) {
          final Object currentVal = getRareVal(rareIndex);

          // update mapping
          details.updated((int) currentVal);
          return copyAndSetValue(mutator, bitpos, val);
        } else {
          final Object currentVal = getRareVal(rareIndex);

          final AbstractMapNode subNodeNew =
              mergeTwoKeyValPairs(currentKey, currentVal, transformHashCode(currentKey.hashCode()),
                  key, val, keyHash, shift + bitPartitionSize());

          details.modified();
          return copyAndMigrateFromRareInlineToNode(mutator, bitpos, subNodeNew);
        }

      }

      // check for node (not value)
      final byte nodeMap = nodeMap();
      if (nodeMap != 0 && (nodeMap == -1 || (nodeMap & bitpos) != 0)) {
        final int nodeIndex = index(nodeMap, mask, bitpos);
        final AbstractMapNode subNode = getNode(nodeIndex);
        final AbstractMapNode subNodeNew =
            subNode.updated(mutator, key, val, keyHash, shift + bitPartitionSize(), details, cmp);

        if (details.isModified()) {
          return copyAndSetNode(mutator, bitpos, subNodeNew);
        } else {
          return this;
        }
      }

      // no value
      details.modified();
      return copyAndInsertValue(mutator, bitpos, key, val);
    }

    @Override
    AbstractMapNode updated(final AtomicReference<Thread> mutator, final Object key,
        final Object val, final int keyHash, final int shift, final MapResult details) {
      final int mask = mask(keyHash, shift);
      final byte bitpos = bitpos(mask);

      // check for inplace value
      final byte dataMap = dataMap();
      if (dataMap != 0 && (dataMap == -1 || (dataMap & bitpos) != 0)) {
        final int dataIndex = index(dataMap, mask, bitpos);
        final int currentKey = getKey(dataIndex);

        final int currentVal = getVal(dataIndex);

        final AbstractMapNode subNodeNew = mergeTwoKeyValPairs(currentKey, currentVal,
            transformHashCode(currentKey), key, val, keyHash, shift + bitPartitionSize());

        details.modified();
        return copyAndMigrateFromInlineToNode(mutator, bitpos, subNodeNew);

      }

      // check for inplace (rare) value
      final byte rareMap = rareMap();
      if (rareMap != 0 && (rareMap == -1 || (rareMap & bitpos) != 0)) {
        final int rareIndex = index(rareMap, mask, bitpos);
        final Object currentKey = getRareKey(rareIndex);

        if (currentKey.equals(key)) {
          final Object currentVal = getRareVal(rareIndex);

          // update mapping
          details.updated((int) currentVal);
          return copyAndSetValue(mutator, bitpos, val);
        } else {
          final Object currentVal = getRareVal(rareIndex);

          final AbstractMapNode subNodeNew =
              mergeTwoKeyValPairs(currentKey, currentVal, transformHashCode(currentKey.hashCode()),
                  key, val, keyHash, shift + bitPartitionSize());

          details.modified();
          return copyAndMigrateFromRareInlineToNode(mutator, bitpos, subNodeNew);
        }

      }

      // check for node (not value)
      final byte nodeMap = nodeMap();
      if (nodeMap != 0 && (nodeMap == -1 || (nodeMap & bitpos) != 0)) {
        final int nodeIndex = index(nodeMap, mask, bitpos);
        final AbstractMapNode subNode = getNode(nodeIndex);
        final AbstractMapNode subNodeNew =
            subNode.updated(mutator, key, val, keyHash, shift + bitPartitionSize(), details);

        if (details.isModified()) {
          return copyAndSetNode(mutator, bitpos, subNodeNew);
        } else {
          return this;
        }
      }

      // no value
      details.modified();
      return copyAndInsertValue(mutator, bitpos, key, val);
    }

    @Override
    int getKey(final int index) {
      try {
        final long[] arrayOffsets =
            (long[]) unsafe.getObject(this.getClass(), globalArrayOffsetsOffset);
        return unsafe.getInt(this, arrayOffsets[logicalToPhysicalIndex(ContentType.KEY, index)]);
      } catch (SecurityException e) {
        throw new RuntimeException(e);
      }
    }

    @Override
    Object getRareKey(final int index) {
      try {
        final long[] arrayOffsets =
            (long[]) unsafe.getObject(this.getClass(), globalArrayOffsetsOffset);
        return unsafe.getObject(this,
            arrayOffsets[logicalToPhysicalIndex(ContentType.RARE_KEY, index)]);
      } catch (SecurityException e) {
        throw new RuntimeException(e);
      }
    }

    @Override
    boolean containsKey(final int key, final int keyHash, final int shift,
        final Comparator<Object> cmp) {
      CompactMapNode instance = this;
      Class<? extends CompactMapNode> clazz = instance.getClass();

      for (int shift0 = shift; true; shift0 += bitPartitionSize()) {
        final int mask = mask(keyHash, shift0);
        final byte bitpos = bitpos(mask);

        final byte nodeMap = instance.nodeMap();
        // final byte nodeMap = unsafe.getByte(instance, globalNodeMapOffset);
        if (nodeMap != 0 && (nodeMap == -1 || (nodeMap & bitpos) != 0)) {
          final int index = index(nodeMap, mask, bitpos);
          final AbstractMapNode nestedInstance = getNode(clazz, instance, index);

          try {
            instance = (CompactMapNode) nestedInstance;
            clazz = instance.getClass();
          } catch (ClassCastException unused) {
            // return ((HashCollisionMapNode_Heterogeneous_BleedingEdge) nestedInstance)
            // .containsKey(key, keyHash, 0);
          }
        } else {
          final byte dataMap = instance.dataMap();
          // final byte dataMap = unsafe.getByte(instance, globalDataMapOffset);
          if (dataMap != 0 && (dataMap == -1 || (dataMap & bitpos) != 0)) {
            final int index = index(dataMap, mask, bitpos);
            return getKey(clazz, instance, index) == key;
          } else {
            return false;
          }
        }
      }
    }

    @Override
    Optional<java.lang.Integer> findByKey(final int key, final int keyHash, final int shift,
        final Comparator<Object> cmp) {
      final int mask = mask(keyHash, shift);
      final byte bitpos = bitpos(mask);

      if ((dataMap() & bitpos) != 0) { // inplace value
        final int index = dataIndex(bitpos);
        if (getKey(index) == key) {
          final java.lang.Integer result = getVal(index);

          return Optional.of(result);
        }

        return Optional.empty();
      }

      if ((nodeMap() & bitpos) != 0) { // node (not value)
        final AbstractMapNode subNode = nodeAt(bitpos);

        return subNode.findByKey(key, keyHash, shift + bitPartitionSize(), cmp);
      }

      return Optional.empty();
    }

    @Override
    Object getSlot(final int index) {
      try {
        final long[] arrayOffsets =
            (long[]) unsafe.getObject(this.getClass(), globalArrayOffsetsOffset);
        return unsafe.getObject(this,
            arrayOffsets[logicalToPhysicalIndex(ContentType.SLOT, index)]);
      } catch (SecurityException e) {
        throw new RuntimeException(e);
      }
    }

    @Override
    int payloadArity() {
      return Integer.bitCount(dataMap() & 0xFF);

      // try {
      // return unsafe.getInt(this.getClass(), globalPayloadArityOffset);
      // } catch (SecurityException e) {
      // throw new RuntimeException(e);
      // }
    }

    @Override
    boolean hasPayload() {
      return payloadArity() != 0;
    }

    @Override
    Optional<java.lang.Integer> findByKey(final int key, final int keyHash, final int shift) {
      final int mask = mask(keyHash, shift);
      final byte bitpos = bitpos(mask);

      if ((dataMap() & bitpos) != 0) { // inplace value
        final int index = dataIndex(bitpos);
        if (getKey(index) == key) {
          final java.lang.Integer result = getVal(index);

          return Optional.of(result);
        }

        return Optional.empty();
      }

      if ((nodeMap() & bitpos) != 0) { // node (not value)
        final AbstractMapNode subNode = nodeAt(bitpos);

        return subNode.findByKey(key, keyHash, shift + bitPartitionSize());
      }

      return Optional.empty();
    }

    @Override
    boolean hasNodes() {
      return nodeArity() != 0;
    }

    @Override
    int rarePayloadArity() {
      return Integer.bitCount(rareMap() & 0xFF);
    }

    @Override
    boolean hasSlots() {
      return slotArity() != 0;
    }

    @Override
    int nodeArity() {
      return Integer.bitCount(nodeMap() & 0xFF);

      // try {
      // return unsafe.getInt(this.getClass(), globalNodeArityOffset);
      // } catch (SecurityException e) {
      // throw new RuntimeException(e);
      // }
    }

    @Override
    AbstractMapNode removed(final AtomicReference<Thread> mutator, final int key, final int keyHash,
        final int shift, final MapResult details, final Comparator<Object> cmp) {
      final int mask = mask(keyHash, shift);
      final byte bitpos = bitpos(mask);

      if ((dataMap() & bitpos) != 0) { // inplace value
        final int dataIndex = dataIndex(bitpos);

        if (getKey(dataIndex) == key) {
          final int currentVal = getVal(dataIndex);
          details.updated(currentVal);

          if (this.payloadArity() == 2 && this.nodeArity() == 0) {
            /*
             * Create new node with remaining pair. The new node will a) either become the new root
             * returned, or b) unwrapped and inlined during returning.
             */
            final byte newDataMap =
                (shift == 0) ? (byte) (dataMap() ^ bitpos) : bitpos(mask(keyHash, 0));

            return nodeOf0x1(mutator, (byte) (0), newDataMap, getKey(1 - dataIndex),
                getVal(1 - dataIndex));
          } else {
            return copyAndRemoveValue(mutator, bitpos);
          }
        } else {
          return this;
        }
      } else if ((nodeMap() & bitpos) != 0) { // node (not value)
        final AbstractMapNode subNode = nodeAt(bitpos);
        final AbstractMapNode subNodeNew =
            subNode.removed(mutator, key, keyHash, shift + bitPartitionSize(), details, cmp);

        if (!details.isModified()) {
          return this;
        }

        switch (subNodeNew.sizePredicate()) {
          case 0: {
            throw new IllegalStateException("Sub-node must have at least one element.");
          }
          case 1: {
            // inline value (move to front)
            details.modified();
            return copyAndMigrateFromNodeToInline(mutator, bitpos, subNodeNew);
          }
          default: {
            // modify current node (set replacement node)
            return copyAndSetNode(mutator, bitpos, subNodeNew);
          }
        }
      }

      return this;
    }

    @Override
    int slotArity() {
      try {
        return unsafe.getInt(this.getClass(), globalSlotArityOffset);
      } catch (SecurityException e) {
        throw new RuntimeException(e);
      }
    }

    @Override
    public boolean equals(final Object other) {
      return equals(this, other);
    }

    @Override
    boolean containsKey(final int key, final int keyHash, final int shift) {
      CompactMapNode instance = this;
      Class<? extends CompactMapNode> clazz = instance.getClass();

      for (int shift0 = shift; true; shift0 += bitPartitionSize()) {
        final int mask = mask(keyHash, shift0);
        final byte bitpos = bitpos(mask);

        final byte nodeMap = instance.nodeMap();
        // final byte nodeMap = unsafe.getByte(instance, globalNodeMapOffset);
        if (nodeMap != 0 && (nodeMap == -1 || (nodeMap & bitpos) != 0)) {
          final int index = index(nodeMap, mask, bitpos);
          final AbstractMapNode nestedInstance = getNode(clazz, instance, index);

          try {
            instance = (CompactMapNode) nestedInstance;
            clazz = instance.getClass();
          } catch (ClassCastException unused) {
            // return ((HashCollisionMapNode_Heterogeneous_BleedingEdge) nestedInstance)
            // .containsKey(key, keyHash, 0);
          }
        } else {
          final byte dataMap = instance.dataMap();
          // final byte dataMap = unsafe.getByte(instance, globalDataMapOffset);
          if (dataMap != 0 && (dataMap == -1 || (dataMap & bitpos) != 0)) {
            final int index = index(dataMap, mask, bitpos);
            return getKey(clazz, instance, index) == key;
          } else {
            return false;
          }
        }
      }
    }

    @Override
    boolean containsKey(final Object key, final int keyHash, final int shift) {
      CompactMapNode instance = this;
      Class<? extends CompactMapNode> clazz = instance.getClass();

      for (int shift0 = shift; true; shift0 += bitPartitionSize()) {
        final int mask = mask(keyHash, shift0);
        final byte bitpos = bitpos(mask);

        final byte nodeMap = instance.nodeMap();
        // final byte nodeMap = unsafe.getByte(instance, globalNodeMapOffset);
        if (nodeMap != 0 && (nodeMap == -1 || (nodeMap & bitpos) != 0)) {
          final int index = index(nodeMap, mask, bitpos);
          final AbstractMapNode nestedInstance = getNode(clazz, instance, index);

          try {
            instance = (CompactMapNode) nestedInstance;
            clazz = instance.getClass();
          } catch (ClassCastException unused) {
            // return ((HashCollisionMapNode_Heterogeneous_BleedingEdge) nestedInstance)
            // .containsKey(key, keyHash, 0);
          }
        } else {
          final byte rareMap = instance.rareMap();
          // final byte rareMap = unsafe.getByte(instance, globalrareMapOffset);
          if (rareMap != 0 && (rareMap == -1 || (rareMap & bitpos) != 0)) {
            final int index = index(rareMap, mask, bitpos);
            return getRareKey(clazz, instance, index).equals(key);
          } else {
            return false;
          }
        }
      }
    }

    @Override
    int getVal(final int index) {
      try {
        final long[] arrayOffsets =
            (long[]) unsafe.getObject(this.getClass(), globalArrayOffsetsOffset);
        return unsafe.getInt(this, arrayOffsets[logicalToPhysicalIndex(ContentType.VAL, index)]);
      } catch (SecurityException e) {
        throw new RuntimeException(e);
      }
    }

    @Override
    AbstractMapNode removed(final AtomicReference<Thread> mutator, final int key, final int keyHash,
        final int shift, final MapResult details) {
      final int mask = mask(keyHash, shift);
      final byte bitpos = bitpos(mask);

      if ((dataMap() & bitpos) != 0) { // inplace value
        final int dataIndex = dataIndex(bitpos);

        if (getKey(dataIndex) == key) {
          final int currentVal = getVal(dataIndex);
          details.updated(currentVal);

          if (this.payloadArity() == 2 && this.nodeArity() == 0) {
            /*
             * Create new node with remaining pair. The new node will a) either become the new root
             * returned, or b) unwrapped and inlined during returning.
             */
            final byte newDataMap =
                (shift == 0) ? (byte) (dataMap() ^ bitpos) : bitpos(mask(keyHash, 0));

            return nodeOf0x1(mutator, (byte) (0), newDataMap, getKey(1 - dataIndex),
                getVal(1 - dataIndex));
          } else {
            return copyAndRemoveValue(mutator, bitpos);
          }
        } else {
          return this;
        }
      } else if ((nodeMap() & bitpos) != 0) { // node (not value)
        final AbstractMapNode subNode = nodeAt(bitpos);
        final AbstractMapNode subNodeNew =
            subNode.removed(mutator, key, keyHash, shift + bitPartitionSize(), details);

        if (!details.isModified()) {
          return this;
        }

        switch (subNodeNew.sizePredicate()) {
          case 0: {
            throw new IllegalStateException("Sub-node must have at least one element.");
          }
          case 1: {
            // inline value (move to front)
            details.modified();
            return copyAndMigrateFromNodeToInline(mutator, bitpos, subNodeNew);
          }
          default: {
            // modify current node (set replacement node)
            return copyAndSetNode(mutator, bitpos, subNodeNew);
          }
        }
      }

      return this;
    }

    @Override
    int untypedSlotArity() {
      try {
        return unsafe.getInt(this.getClass(), globalUntypedSlotArityOffset);
      } catch (SecurityException e) {
        throw new RuntimeException(e);
      }
    }

    @Override
    Object getRareVal(final int index) {
      try {
        final long[] arrayOffsets =
            (long[]) unsafe.getObject(this.getClass(), globalArrayOffsetsOffset);
        return unsafe.getObject(this,
            arrayOffsets[logicalToPhysicalIndex(ContentType.RARE_VAL, index)]);
      } catch (SecurityException e) {
        throw new RuntimeException(e);
      }
    }

    @Override
    AbstractMapNode getNode(final int index) {
      try {
        final long[] arrayOffsets =
            (long[]) unsafe.getObject(this.getClass(), globalArrayOffsetsOffset);
        return (AbstractMapNode) unsafe.getObject(this,
            arrayOffsets[logicalToPhysicalIndex(ContentType.NODE, index)]);
      } catch (SecurityException e) {
        throw new RuntimeException(e);
      }
    }

  }
  private abstract static class CompactEmptyMapNode extends CompactMapNode {

    private CompactEmptyMapNode(final AtomicReference<Thread> mutator) {

    }

    @Override
    byte rawMap1() {
      return 0;
    }

    @Override
    byte rawMap2() {
      return 0;
    }

  }
  private abstract static class CompactNodesOnlyMapNode extends CompactMapNode {

    private CompactNodesOnlyMapNode(final AtomicReference<Thread> mutator, final byte rawMap1) {
      this.rawMap1 = rawMap1;
    }

    @Override
    byte rawMap1() {
      return rawMap1;
    }

    private final byte rawMap1;

    @Override
    byte rawMap2() {
      return 0;
    }

  }
  private abstract static class CompactValuesOnlyMapNode extends CompactMapNode {

    private CompactValuesOnlyMapNode(final AtomicReference<Thread> mutator, final byte rawMap1,
        final byte rawMap2) {
      this.rawMap1 = rawMap1;
      this.rawMap2 = rawMap2;
    }

    @Override
    byte rawMap1() {
      return rawMap1;
    }

    private final byte rawMap1;

    @Override
    byte rawMap2() {
      return rawMap2;
    }

    private final byte rawMap2;

  }
  private abstract static class CompactMixedMapNode extends CompactMapNode {

    private CompactMixedMapNode(final AtomicReference<Thread> mutator, final byte rawMap1,
        final byte rawMap2) {
      this.rawMap1 = rawMap1;
      this.rawMap2 = rawMap2;
    }

    @Override
    byte rawMap1() {
      return rawMap1;
    }

    private final byte rawMap1;

    @Override
    byte rawMap2() {
      return rawMap2;
    }

    private final byte rawMap2;

  }

  /**
   * Iterator skeleton that uses a fixed stack in depth.
   */
  private static abstract class AbstractMapIterator {

    private static final int MAX_DEPTH = 11;

    protected int currentValueCursor;
    protected int currentValueLength;
    protected AbstractMapNode currentValueNode;

    private int currentStackLevel = -1;
    private final int[] nodeCursorsAndLengths = new int[MAX_DEPTH * 2];

    @SuppressWarnings("unchecked")
    AbstractMapNode[] nodes = new AbstractMapNode[MAX_DEPTH];

    AbstractMapIterator(AbstractMapNode rootNode) {
      if (rootNode.hasNodes()) {
        currentStackLevel = 0;

        nodes[0] = rootNode;
        nodeCursorsAndLengths[0] = 0;
        nodeCursorsAndLengths[1] = rootNode.nodeArity();
      }

      if (rootNode.hasPayload()) {
        currentValueNode = rootNode;
        currentValueCursor = 0;
        currentValueLength = rootNode.payloadArity();
      }
    }

    /*
     * search for next node that contains values
     */
    private boolean searchNextValueNode() {
      while (currentStackLevel >= 0) {
        final int currentCursorIndex = currentStackLevel * 2;
        final int currentLengthIndex = currentCursorIndex + 1;

        final int nodeCursor = nodeCursorsAndLengths[currentCursorIndex];
        final int nodeLength = nodeCursorsAndLengths[currentLengthIndex];

        if (nodeCursor < nodeLength) {
          final AbstractMapNode nextNode = nodes[currentStackLevel].getNode(nodeCursor);
          nodeCursorsAndLengths[currentCursorIndex]++;

          if (nextNode.hasNodes()) {
            /*
             * put node on next stack level for depth-first traversal
             */
            final int nextStackLevel = ++currentStackLevel;
            final int nextCursorIndex = nextStackLevel * 2;
            final int nextLengthIndex = nextCursorIndex + 1;

            nodes[nextStackLevel] = nextNode;
            nodeCursorsAndLengths[nextCursorIndex] = 0;
            nodeCursorsAndLengths[nextLengthIndex] = nextNode.nodeArity();
          }

          if (nextNode.hasPayload()) {
            /*
             * found next node that contains values
             */
            currentValueNode = nextNode;
            currentValueCursor = 0;
            currentValueLength = nextNode.payloadArity();
            return true;
          }
        } else {
          currentStackLevel--;
        }
      }

      return false;
    }

    public boolean hasNext() {
      if (currentValueCursor < currentValueLength) {
        return true;
      } else {
        return searchNextValueNode();
      }
    }

    public void remove() {
      throw new UnsupportedOperationException();
    }
  }

  protected static class MapKeyIterator extends AbstractMapIterator
      implements Iterator<java.lang.Integer> {

    MapKeyIterator(AbstractMapNode rootNode) {
      super(rootNode);
    }

    @Override
    public java.lang.Integer next() {
      if (!hasNext()) {
        throw new NoSuchElementException();
      } else {
        return currentValueNode.getKey(currentValueCursor++);
      }
    }

  }

  protected static class MapValueIterator extends AbstractMapIterator
      implements Iterator<java.lang.Integer> {

    MapValueIterator(AbstractMapNode rootNode) {
      super(rootNode);
    }

    @Override
    public java.lang.Integer next() {
      if (!hasNext()) {
        throw new NoSuchElementException();
      } else {
        return currentValueNode.getVal(currentValueCursor++);
      }
    }

  }

  protected static class MapEntryIterator extends AbstractMapIterator
      implements Iterator<Map.Entry<java.lang.Integer, java.lang.Integer>> {

    MapEntryIterator(AbstractMapNode rootNode) {
      super(rootNode);
    }

    @Override
    public Map.Entry<java.lang.Integer, java.lang.Integer> next() {
      if (!hasNext()) {
        throw new NoSuchElementException();
      } else {
        return currentValueNode.getKeyValueEntry(currentValueCursor++);
      }
    }

  }

  /**
   * Iterator that first iterates over inlined-values and then continues depth first recursively.
   */
  private static class TrieMap_Heterogeneous_BleedingEdgeNodeIterator
      implements Iterator<AbstractMapNode> {

    final Deque<Iterator<? extends AbstractMapNode>> nodeIteratorStack;

    TrieMap_Heterogeneous_BleedingEdgeNodeIterator(AbstractMapNode rootNode) {
      nodeIteratorStack = new ArrayDeque<>();
      nodeIteratorStack.push(Collections.singleton(rootNode).iterator());
    }

    @Override
    public boolean hasNext() {
      while (true) {
        if (nodeIteratorStack.isEmpty()) {
          return false;
        } else {
          if (nodeIteratorStack.peek().hasNext()) {
            return true;
          } else {
            nodeIteratorStack.pop();
            continue;
          }
        }
      }
    }

    @Override
    public AbstractMapNode next() {
      if (!hasNext()) {
        throw new NoSuchElementException();
      }

      AbstractMapNode innerNode = nodeIteratorStack.peek().next();

      if (innerNode.hasNodes()) {
        nodeIteratorStack.push(innerNode.nodeIterator());
      }

      return innerNode;
    }

    @Override
    public void remove() {
      throw new UnsupportedOperationException();
    }
  }
  static final class TransientTrieMap_Heterogeneous_BleedingEdge
      implements TransientMap<java.lang.Integer, java.lang.Integer> {
    final private AtomicReference<Thread> mutator;
    private AbstractMapNode rootNode;
    private int hashCode;
    private int cachedSize;

    TransientTrieMap_Heterogeneous_BleedingEdge(
        TrieMap_Heterogeneous_BleedingEdge trieMap_Heterogeneous_BleedingEdge) {
      this.mutator = new AtomicReference<Thread>(Thread.currentThread());
      this.rootNode = trieMap_Heterogeneous_BleedingEdge.rootNode;
      this.hashCode = trieMap_Heterogeneous_BleedingEdge.hashCode;
      this.cachedSize = trieMap_Heterogeneous_BleedingEdge.cachedSize;
      if (DEBUG) {
        assert checkHashCodeAndSize(hashCode, cachedSize);
      }
    }

    private boolean checkHashCodeAndSize(final int targetHash, final int targetSize) {
      int hash = 0;
      int size = 0;

      for (Iterator<Map.Entry<java.lang.Integer, java.lang.Integer>> it = entryIterator(); it
          .hasNext();) {
        final Map.Entry<java.lang.Integer, java.lang.Integer> entry = it.next();
        final int key = entry.getKey();
        final int val = entry.getValue();

        hash += key ^ val;
        size += 1;
      }

      return hash == targetHash && size == targetSize;
    }

    @Override
    public java.lang.Integer put(final java.lang.Integer key, final java.lang.Integer val) {
      throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(final Map<? extends java.lang.Integer, ? extends java.lang.Integer> m) {
      throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
      throw new UnsupportedOperationException();
    }

    @Override
    public java.lang.Integer remove(final Object key) {
      throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsKey(final Object o) {
      try {
        @SuppressWarnings("unchecked")
        final int key = (int) o;
        return rootNode.containsKey(key, transformHashCode(key), 0);
      } catch (ClassCastException unused) {
        return false;
      }
    }

    @Override
    public boolean containsKeyEquivalent(final Object o, final Comparator<Object> cmp) {
      try {
        @SuppressWarnings("unchecked")
        final int key = (int) o;
        return rootNode.containsKey(key, transformHashCode(key), 0, cmp);
      } catch (ClassCastException unused) {
        return false;
      }
    }

    @Override
    public boolean containsValue(final Object o) {
      for (Iterator<java.lang.Integer> iterator = valueIterator(); iterator.hasNext();) {
        if (iterator.next().equals(o)) {
          return true;
        }
      }
      return false;
    }

    @Override
    public boolean containsValueEquivalent(final Object o, final Comparator<Object> cmp) {
      for (Iterator<java.lang.Integer> iterator = valueIterator(); iterator.hasNext();) {
        if (cmp.compare(iterator.next(), o) == 0) {
          return true;
        }
      }
      return false;
    }

    @Override
    public java.lang.Integer get(final Object o) {
      try {
        @SuppressWarnings("unchecked")
        final int key = (int) o;
        final Optional<java.lang.Integer> result =
            rootNode.findByKey(key, transformHashCode(key), 0);

        if (result.isPresent()) {
          return result.get();
        } else {
          return null;
        }
      } catch (ClassCastException unused) {
        return null;
      }
    }

    @Override
    public java.lang.Integer getEquivalent(final Object o, final Comparator<Object> cmp) {
      try {
        @SuppressWarnings("unchecked")
        final int key = (int) o;
        final Optional<java.lang.Integer> result =
            rootNode.findByKey(key, transformHashCode(key), 0, cmp);

        if (result.isPresent()) {
          return result.get();
        } else {
          return null;
        }
      } catch (ClassCastException unused) {
        return null;
      }
    }

    @Override
    public java.lang.Integer __put(final java.lang.Integer key, final java.lang.Integer val) {
      if (mutator.get() == null) {
        throw new IllegalStateException("Transient already frozen.");
      }

      final int keyHash = key.hashCode();
      final MapResult details = MapResult.unchanged();

      final AbstractMapNode newRootNode =
          rootNode.updated(mutator, key, val, transformHashCode(keyHash), 0, details);

      if (details.isModified()) {
        if (details.hasReplacedValue()) {
          final int old = details.getReplacedValue();

          final int valHashOld = old;
          final int valHashNew = val;

          rootNode = newRootNode;
          hashCode = hashCode + (keyHash ^ valHashNew) - (keyHash ^ valHashOld);

          if (DEBUG) {
            assert checkHashCodeAndSize(hashCode, cachedSize);
          }
          return details.getReplacedValue();
        } else {
          final int valHashNew = val;
          rootNode = newRootNode;
          hashCode += (keyHash ^ valHashNew);
          cachedSize += 1;

          if (DEBUG) {
            assert checkHashCodeAndSize(hashCode, cachedSize);
          }
          return null;
        }
      }

      if (DEBUG) {
        assert checkHashCodeAndSize(hashCode, cachedSize);
      }
      return null;
    }

    @Override
    public java.lang.Integer __putEquivalent(final java.lang.Integer key,
        final java.lang.Integer val, final Comparator<Object> cmp) {
      if (mutator.get() == null) {
        throw new IllegalStateException("Transient already frozen.");
      }

      final int keyHash = key.hashCode();
      final MapResult details = MapResult.unchanged();

      final AbstractMapNode newRootNode =
          rootNode.updated(mutator, key, val, transformHashCode(keyHash), 0, details, cmp);

      if (details.isModified()) {
        if (details.hasReplacedValue()) {
          final int old = details.getReplacedValue();

          final int valHashOld = old;
          final int valHashNew = val;

          rootNode = newRootNode;
          hashCode = hashCode + (keyHash ^ valHashNew) - (keyHash ^ valHashOld);

          if (DEBUG) {
            assert checkHashCodeAndSize(hashCode, cachedSize);
          }
          return details.getReplacedValue();
        } else {
          final int valHashNew = val;
          rootNode = newRootNode;
          hashCode += (keyHash ^ valHashNew);
          cachedSize += 1;

          if (DEBUG) {
            assert checkHashCodeAndSize(hashCode, cachedSize);
          }
          return null;
        }
      }

      if (DEBUG) {
        assert checkHashCodeAndSize(hashCode, cachedSize);
      }
      return null;
    }

    @Override
    public boolean __putAll(
        final Map<? extends java.lang.Integer, ? extends java.lang.Integer> map) {
      boolean modified = false;

      for (Map.Entry<? extends java.lang.Integer, ? extends java.lang.Integer> entry : map
          .entrySet()) {
        final boolean isPresent = this.containsKey(entry.getKey());
        final java.lang.Integer replaced = this.__put(entry.getKey(), entry.getValue());

        if (!isPresent || replaced != null) {
          modified = true;
        }
      }

      return modified;
    }

    @Override
    public boolean __putAllEquivalent(
        final Map<? extends java.lang.Integer, ? extends java.lang.Integer> map,
        final Comparator<Object> cmp) {
      boolean modified = false;

      for (Map.Entry<? extends java.lang.Integer, ? extends java.lang.Integer> entry : map
          .entrySet()) {
        final boolean isPresent = this.containsKeyEquivalent(entry.getKey(), cmp);
        final java.lang.Integer replaced =
            this.__putEquivalent(entry.getKey(), entry.getValue(), cmp);

        if (!isPresent || replaced != null) {
          modified = true;
        }
      }

      return modified;
    }

    @Override
    public java.lang.Integer __remove(final java.lang.Integer key) {
      if (mutator.get() == null) {
        throw new IllegalStateException("Transient already frozen.");
      }

      final int keyHash = key.hashCode();
      final MapResult details = MapResult.unchanged();

      final AbstractMapNode newRootNode =
          rootNode.removed(mutator, key, transformHashCode(keyHash), 0, details);

      if (details.isModified()) {
        assert details.hasReplacedValue();
        final int valHash = details.getReplacedValue();

        rootNode = newRootNode;
        hashCode = hashCode - (keyHash ^ valHash);
        cachedSize = cachedSize - 1;

        if (DEBUG) {
          assert checkHashCodeAndSize(hashCode, cachedSize);
        }
        return details.getReplacedValue();
      }

      if (DEBUG) {
        assert checkHashCodeAndSize(hashCode, cachedSize);
      }

      return null;
    }

    @Override
    public java.lang.Integer __removeEquivalent(final java.lang.Integer key,
        final Comparator<Object> cmp) {
      if (mutator.get() == null) {
        throw new IllegalStateException("Transient already frozen.");
      }

      final int keyHash = key.hashCode();
      final MapResult details = MapResult.unchanged();

      final AbstractMapNode newRootNode =
          rootNode.removed(mutator, key, transformHashCode(keyHash), 0, details, cmp);

      if (details.isModified()) {
        assert details.hasReplacedValue();
        final int valHash = details.getReplacedValue();

        rootNode = newRootNode;
        hashCode = hashCode - (keyHash ^ valHash);
        cachedSize = cachedSize - 1;

        if (DEBUG) {
          assert checkHashCodeAndSize(hashCode, cachedSize);
        }
        return details.getReplacedValue();
      }

      if (DEBUG) {
        assert checkHashCodeAndSize(hashCode, cachedSize);
      }

      return null;
    }

    @Override
    public int size() {
      return cachedSize;
    }

    @Override
    public boolean isEmpty() {
      return cachedSize == 0;
    }

    @Override
    public Iterator<java.lang.Integer> keyIterator() {
      return new TransientMapKeyIterator(this);
    }

    @Override
    public Iterator<java.lang.Integer> valueIterator() {
      return new TransientMapValueIterator(this);
    }

    @Override
    public Iterator<Map.Entry<java.lang.Integer, java.lang.Integer>> entryIterator() {
      return new TransientMapEntryIterator(this);
    }

    public static class TransientMapKeyIterator extends MapKeyIterator {
      final TransientTrieMap_Heterogeneous_BleedingEdge collection;
      int lastKey;

      public TransientMapKeyIterator(final TransientTrieMap_Heterogeneous_BleedingEdge collection) {
        super(collection.rootNode);
        this.collection = collection;
      }

      @Override
      public java.lang.Integer next() {
        return lastKey = super.next();
      }

      @Override
      public void remove() {
        // TODO: test removal at iteration rigorously
        collection.__remove(lastKey);
      }
    }

    public static class TransientMapValueIterator extends MapValueIterator {
      final TransientTrieMap_Heterogeneous_BleedingEdge collection;

      public TransientMapValueIterator(
          final TransientTrieMap_Heterogeneous_BleedingEdge collection) {
        super(collection.rootNode);
        this.collection = collection;
      }

      @Override
      public java.lang.Integer next() {
        return super.next();
      }

      @Override
      public void remove() {
        throw new UnsupportedOperationException();
      }
    }

    public static class TransientMapEntryIterator extends MapEntryIterator {
      final TransientTrieMap_Heterogeneous_BleedingEdge collection;

      public TransientMapEntryIterator(
          final TransientTrieMap_Heterogeneous_BleedingEdge collection) {
        super(collection.rootNode);
        this.collection = collection;
      }

      @Override
      public Map.Entry<java.lang.Integer, java.lang.Integer> next() {
        return super.next();
      }

      @Override
      public void remove() {
        throw new UnsupportedOperationException();
      }
    }

    @Override
    public Set<java.lang.Integer> keySet() {
      Set<java.lang.Integer> keySet = null;

      if (keySet == null) {
        keySet = new AbstractSet<java.lang.Integer>() {
          @Override
          public Iterator<java.lang.Integer> iterator() {
            return TransientTrieMap_Heterogeneous_BleedingEdge.this.keyIterator();
          }

          @Override
          public int size() {
            return TransientTrieMap_Heterogeneous_BleedingEdge.this.size();
          }

          @Override
          public boolean isEmpty() {
            return TransientTrieMap_Heterogeneous_BleedingEdge.this.isEmpty();
          }

          @Override
          public void clear() {
            TransientTrieMap_Heterogeneous_BleedingEdge.this.clear();
          }

          @Override
          public boolean contains(Object k) {
            return TransientTrieMap_Heterogeneous_BleedingEdge.this.containsKey(k);
          }
        };
      }

      return keySet;
    }

    @Override
    public Collection<java.lang.Integer> values() {
      Collection<java.lang.Integer> values = null;

      if (values == null) {
        values = new AbstractCollection<java.lang.Integer>() {
          @Override
          public Iterator<java.lang.Integer> iterator() {
            return TransientTrieMap_Heterogeneous_BleedingEdge.this.valueIterator();
          }

          @Override
          public int size() {
            return TransientTrieMap_Heterogeneous_BleedingEdge.this.size();
          }

          @Override
          public boolean isEmpty() {
            return TransientTrieMap_Heterogeneous_BleedingEdge.this.isEmpty();
          }

          @Override
          public void clear() {
            TransientTrieMap_Heterogeneous_BleedingEdge.this.clear();
          }

          @Override
          public boolean contains(Object v) {
            return TransientTrieMap_Heterogeneous_BleedingEdge.this.containsValue(v);
          }
        };
      }

      return values;
    }

    @Override
    public Set<java.util.Map.Entry<java.lang.Integer, java.lang.Integer>> entrySet() {
      Set<java.util.Map.Entry<java.lang.Integer, java.lang.Integer>> entrySet = null;

      if (entrySet == null) {
        entrySet = new AbstractSet<java.util.Map.Entry<java.lang.Integer, java.lang.Integer>>() {
          @Override
          public Iterator<java.util.Map.Entry<java.lang.Integer, java.lang.Integer>> iterator() {
            return new Iterator<Map.Entry<java.lang.Integer, java.lang.Integer>>() {
              private final Iterator<Map.Entry<java.lang.Integer, java.lang.Integer>> i =
                  entryIterator();

              @Override
              public boolean hasNext() {
                return i.hasNext();
              }

              @Override
              public Map.Entry<java.lang.Integer, java.lang.Integer> next() {
                return i.next();
              }

              @Override
              public void remove() {
                i.remove();
              }
            };
          }

          @Override
          public int size() {
            return TransientTrieMap_Heterogeneous_BleedingEdge.this.size();
          }

          @Override
          public boolean isEmpty() {
            return TransientTrieMap_Heterogeneous_BleedingEdge.this.isEmpty();
          }

          @Override
          public void clear() {
            TransientTrieMap_Heterogeneous_BleedingEdge.this.clear();
          }

          @Override
          public boolean contains(Object k) {
            return TransientTrieMap_Heterogeneous_BleedingEdge.this.containsKey(k);
          }
        };
      }

      return entrySet;
    }

    @Override
    public boolean equals(final Object other) {
      if (other == this) {
        return true;
      }
      if (other == null) {
        return false;
      }

      if (other instanceof TransientTrieMap_Heterogeneous_BleedingEdge) {
        TransientTrieMap_Heterogeneous_BleedingEdge that =
            (TransientTrieMap_Heterogeneous_BleedingEdge) other;

        if (this.cachedSize != that.cachedSize) {
          return false;
        }

        if (this.hashCode != that.hashCode) {
          return false;
        }

        return rootNode.equals(that.rootNode);
      } else if (other instanceof Map) {
        Map that = (Map) other;

        if (this.size() != that.size())
          return false;

        for (@SuppressWarnings("unchecked")
        Iterator<Map.Entry> it = that.entrySet().iterator(); it.hasNext();) {
          Map.Entry entry = it.next();

          try {
            @SuppressWarnings("unchecked")
            final int key = (java.lang.Integer) entry.getKey();
            final Optional<java.lang.Integer> result =
                rootNode.findByKey(key, transformHashCode(key), 0);

            if (!result.isPresent()) {
              return false;
            } else {
              @SuppressWarnings("unchecked")
              final int val = (java.lang.Integer) entry.getValue();

              if (!result.get().equals(val)) {
                return false;
              }
            }
          } catch (ClassCastException unused) {
            return false;
          }
        }

        return true;
      }

      return false;
    }

    @Override
    public int hashCode() {
      return hashCode;
    }

    @Override
    public ImmutableMap<java.lang.Integer, java.lang.Integer> freeze() {
      if (mutator.get() == null) {
        throw new IllegalStateException("Transient already frozen.");
      }

      mutator.set(null);
      return new TrieMap_Heterogeneous_BleedingEdge(rootNode, hashCode, cachedSize);
    }
  }
  private static class Map0To0Node_Heterogeneous_BleedingEdge extends CompactMixedMapNode {

    static final long[] arrayOffsets =
        arrayOffsets(Map0To0Node_Heterogeneous_BleedingEdge.class, new String[] {});

    static final int nodeArity = 0;

    static final int payloadArity = 0;

    static final int slotArity = 0;

    static final int untypedSlotArity = 0;

    static final long arrayOffsetLast = arrayBase + -1 * addressSize;

    private Map0To0Node_Heterogeneous_BleedingEdge(final AtomicReference<Thread> mutator,
        final byte nodeMap, final byte dataMap) {
      super(mutator, nodeMap, dataMap);
    }

  }
  private static class Map0To1Node_Heterogeneous_BleedingEdge extends CompactMixedMapNode {

    static final long[] arrayOffsets =
        arrayOffsets(Map0To1Node_Heterogeneous_BleedingEdge.class, new String[] {"slot0"});

    static final int nodeArity = 1;

    static final int payloadArity = 0;

    static final int slotArity = 1;

    static final int untypedSlotArity = 1;

    static final long arrayOffsetLast = arrayBase + 0 * addressSize;

    private final Object slot0;

    private Map0To1Node_Heterogeneous_BleedingEdge(final AtomicReference<Thread> mutator,
        final byte nodeMap, final byte dataMap, final Object slot0) {
      super(mutator, nodeMap, dataMap);
      this.slot0 = slot0;
    }

  }
  private static class Map0To2Node_Heterogeneous_BleedingEdge extends CompactMixedMapNode {

    static final long[] arrayOffsets =
        arrayOffsets(Map0To2Node_Heterogeneous_BleedingEdge.class, new String[] {"slot0", "slot1"});

    static final int nodeArity = 2;

    static final int payloadArity = 0;

    static final int slotArity = 2;

    static final int untypedSlotArity = 2;

    static final long arrayOffsetLast = arrayBase + 1 * addressSize;

    private final Object slot0;

    private final Object slot1;

    private Map0To2Node_Heterogeneous_BleedingEdge(final AtomicReference<Thread> mutator,
        final byte nodeMap, final byte dataMap, final Object slot0, final Object slot1) {
      super(mutator, nodeMap, dataMap);
      this.slot0 = slot0;
      this.slot1 = slot1;
    }

  }
  private static class Map0To3Node_Heterogeneous_BleedingEdge extends CompactMixedMapNode {

    static final long[] arrayOffsets = arrayOffsets(Map0To3Node_Heterogeneous_BleedingEdge.class,
        new String[] {"slot0", "slot1", "slot2"});

    static final int nodeArity = 3;

    static final int payloadArity = 0;

    static final int slotArity = 3;

    static final int untypedSlotArity = 3;

    static final long arrayOffsetLast = arrayBase + 2 * addressSize;

    private final Object slot0;

    private final Object slot1;

    private final Object slot2;

    private Map0To3Node_Heterogeneous_BleedingEdge(final AtomicReference<Thread> mutator,
        final byte nodeMap, final byte dataMap, final Object slot0, final Object slot1,
        final Object slot2) {
      super(mutator, nodeMap, dataMap);
      this.slot0 = slot0;
      this.slot1 = slot1;
      this.slot2 = slot2;
    }

  }
  private static class Map0To4Node_Heterogeneous_BleedingEdge extends CompactMixedMapNode {

    static final long[] arrayOffsets = arrayOffsets(Map0To4Node_Heterogeneous_BleedingEdge.class,
        new String[] {"slot0", "slot1", "slot2", "slot3"});

    static final int nodeArity = 4;

    static final int payloadArity = 0;

    static final int slotArity = 4;

    static final int untypedSlotArity = 4;

    static final long arrayOffsetLast = arrayBase + 3 * addressSize;

    private final Object slot0;

    private final Object slot1;

    private final Object slot2;

    private final Object slot3;

    private Map0To4Node_Heterogeneous_BleedingEdge(final AtomicReference<Thread> mutator,
        final byte nodeMap, final byte dataMap, final Object slot0, final Object slot1,
        final Object slot2, final Object slot3) {
      super(mutator, nodeMap, dataMap);
      this.slot0 = slot0;
      this.slot1 = slot1;
      this.slot2 = slot2;
      this.slot3 = slot3;
    }

  }
  private static class Map0To5Node_Heterogeneous_BleedingEdge extends CompactMixedMapNode {

    static final long[] arrayOffsets = arrayOffsets(Map0To5Node_Heterogeneous_BleedingEdge.class,
        new String[] {"slot0", "slot1", "slot2", "slot3", "slot4"});

    static final int nodeArity = 5;

    static final int payloadArity = 0;

    static final int slotArity = 5;

    static final int untypedSlotArity = 5;

    static final long arrayOffsetLast = arrayBase + 4 * addressSize;

    private final Object slot0;

    private final Object slot1;

    private final Object slot2;

    private final Object slot3;

    private final Object slot4;

    private Map0To5Node_Heterogeneous_BleedingEdge(final AtomicReference<Thread> mutator,
        final byte nodeMap, final byte dataMap, final Object slot0, final Object slot1,
        final Object slot2, final Object slot3, final Object slot4) {
      super(mutator, nodeMap, dataMap);
      this.slot0 = slot0;
      this.slot1 = slot1;
      this.slot2 = slot2;
      this.slot3 = slot3;
      this.slot4 = slot4;
    }

  }
  private static class Map0To6Node_Heterogeneous_BleedingEdge extends CompactMixedMapNode {

    static final long[] arrayOffsets = arrayOffsets(Map0To6Node_Heterogeneous_BleedingEdge.class,
        new String[] {"slot0", "slot1", "slot2", "slot3", "slot4", "slot5"});

    static final int nodeArity = 6;

    static final int payloadArity = 0;

    static final int slotArity = 6;

    static final int untypedSlotArity = 6;

    static final long arrayOffsetLast = arrayBase + 5 * addressSize;

    private final Object slot0;

    private final Object slot1;

    private final Object slot2;

    private final Object slot3;

    private final Object slot4;

    private final Object slot5;

    private Map0To6Node_Heterogeneous_BleedingEdge(final AtomicReference<Thread> mutator,
        final byte nodeMap, final byte dataMap, final Object slot0, final Object slot1,
        final Object slot2, final Object slot3, final Object slot4, final Object slot5) {
      super(mutator, nodeMap, dataMap);
      this.slot0 = slot0;
      this.slot1 = slot1;
      this.slot2 = slot2;
      this.slot3 = slot3;
      this.slot4 = slot4;
      this.slot5 = slot5;
    }

  }
  private static class Map0To7Node_Heterogeneous_BleedingEdge extends CompactMixedMapNode {

    static final long[] arrayOffsets = arrayOffsets(Map0To7Node_Heterogeneous_BleedingEdge.class,
        new String[] {"slot0", "slot1", "slot2", "slot3", "slot4", "slot5", "slot6"});

    static final int nodeArity = 7;

    static final int payloadArity = 0;

    static final int slotArity = 7;

    static final int untypedSlotArity = 7;

    static final long arrayOffsetLast = arrayBase + 6 * addressSize;

    private final Object slot0;

    private final Object slot1;

    private final Object slot2;

    private final Object slot3;

    private final Object slot4;

    private final Object slot5;

    private final Object slot6;

    private Map0To7Node_Heterogeneous_BleedingEdge(final AtomicReference<Thread> mutator,
        final byte nodeMap, final byte dataMap, final Object slot0, final Object slot1,
        final Object slot2, final Object slot3, final Object slot4, final Object slot5,
        final Object slot6) {
      super(mutator, nodeMap, dataMap);
      this.slot0 = slot0;
      this.slot1 = slot1;
      this.slot2 = slot2;
      this.slot3 = slot3;
      this.slot4 = slot4;
      this.slot5 = slot5;
      this.slot6 = slot6;
    }

  }
  private static class Map0To8Node_Heterogeneous_BleedingEdge extends CompactMixedMapNode {

    static final long[] arrayOffsets = arrayOffsets(Map0To8Node_Heterogeneous_BleedingEdge.class,
        new String[] {"slot0", "slot1", "slot2", "slot3", "slot4", "slot5", "slot6", "slot7"});

    static final int nodeArity = 8;

    static final int payloadArity = 0;

    static final int slotArity = 8;

    static final int untypedSlotArity = 8;

    static final long arrayOffsetLast = arrayBase + 7 * addressSize;

    private final Object slot0;

    private final Object slot1;

    private final Object slot2;

    private final Object slot3;

    private final Object slot4;

    private final Object slot5;

    private final Object slot6;

    private final Object slot7;

    private Map0To8Node_Heterogeneous_BleedingEdge(final AtomicReference<Thread> mutator,
        final byte nodeMap, final byte dataMap, final Object slot0, final Object slot1,
        final Object slot2, final Object slot3, final Object slot4, final Object slot5,
        final Object slot6, final Object slot7) {
      super(mutator, nodeMap, dataMap);
      this.slot0 = slot0;
      this.slot1 = slot1;
      this.slot2 = slot2;
      this.slot3 = slot3;
      this.slot4 = slot4;
      this.slot5 = slot5;
      this.slot6 = slot6;
      this.slot7 = slot7;
    }

  }
  private static class Map0To9Node_Heterogeneous_BleedingEdge extends CompactMixedMapNode {

    static final long[] arrayOffsets =
        arrayOffsets(Map0To9Node_Heterogeneous_BleedingEdge.class, new String[] {"slot0", "slot1",
            "slot2", "slot3", "slot4", "slot5", "slot6", "slot7", "slot8"});

    static final int nodeArity = 9;

    static final int payloadArity = 0;

    static final int slotArity = 9;

    static final int untypedSlotArity = 9;

    static final long arrayOffsetLast = arrayBase + 8 * addressSize;

    private final Object slot0;

    private final Object slot1;

    private final Object slot2;

    private final Object slot3;

    private final Object slot4;

    private final Object slot5;

    private final Object slot6;

    private final Object slot7;

    private final Object slot8;

    private Map0To9Node_Heterogeneous_BleedingEdge(final AtomicReference<Thread> mutator,
        final byte nodeMap, final byte dataMap, final Object slot0, final Object slot1,
        final Object slot2, final Object slot3, final Object slot4, final Object slot5,
        final Object slot6, final Object slot7, final Object slot8) {
      super(mutator, nodeMap, dataMap);
      this.slot0 = slot0;
      this.slot1 = slot1;
      this.slot2 = slot2;
      this.slot3 = slot3;
      this.slot4 = slot4;
      this.slot5 = slot5;
      this.slot6 = slot6;
      this.slot7 = slot7;
      this.slot8 = slot8;
    }

  }
  private static class Map0To10Node_Heterogeneous_BleedingEdge extends CompactMixedMapNode {

    static final long[] arrayOffsets =
        arrayOffsets(Map0To10Node_Heterogeneous_BleedingEdge.class, new String[] {"slot0", "slot1",
            "slot2", "slot3", "slot4", "slot5", "slot6", "slot7", "slot8", "slot9"});

    static final int nodeArity = 10;

    static final int payloadArity = 0;

    static final int slotArity = 10;

    static final int untypedSlotArity = 10;

    static final long arrayOffsetLast = arrayBase + 9 * addressSize;

    private final Object slot0;

    private final Object slot1;

    private final Object slot2;

    private final Object slot3;

    private final Object slot4;

    private final Object slot5;

    private final Object slot6;

    private final Object slot7;

    private final Object slot8;

    private final Object slot9;

    private Map0To10Node_Heterogeneous_BleedingEdge(final AtomicReference<Thread> mutator,
        final byte nodeMap, final byte dataMap, final Object slot0, final Object slot1,
        final Object slot2, final Object slot3, final Object slot4, final Object slot5,
        final Object slot6, final Object slot7, final Object slot8, final Object slot9) {
      super(mutator, nodeMap, dataMap);
      this.slot0 = slot0;
      this.slot1 = slot1;
      this.slot2 = slot2;
      this.slot3 = slot3;
      this.slot4 = slot4;
      this.slot5 = slot5;
      this.slot6 = slot6;
      this.slot7 = slot7;
      this.slot8 = slot8;
      this.slot9 = slot9;
    }

  }
  private static class Map0To11Node_Heterogeneous_BleedingEdge extends CompactMixedMapNode {

    static final long[] arrayOffsets =
        arrayOffsets(Map0To11Node_Heterogeneous_BleedingEdge.class, new String[] {"slot0", "slot1",
            "slot2", "slot3", "slot4", "slot5", "slot6", "slot7", "slot8", "slot9", "slot10"});

    static final int nodeArity = 11;

    static final int payloadArity = 0;

    static final int slotArity = 11;

    static final int untypedSlotArity = 11;

    static final long arrayOffsetLast = arrayBase + 10 * addressSize;

    private final Object slot0;

    private final Object slot1;

    private final Object slot2;

    private final Object slot3;

    private final Object slot4;

    private final Object slot5;

    private final Object slot6;

    private final Object slot7;

    private final Object slot8;

    private final Object slot9;

    private final Object slot10;

    private Map0To11Node_Heterogeneous_BleedingEdge(final AtomicReference<Thread> mutator,
        final byte nodeMap, final byte dataMap, final Object slot0, final Object slot1,
        final Object slot2, final Object slot3, final Object slot4, final Object slot5,
        final Object slot6, final Object slot7, final Object slot8, final Object slot9,
        final Object slot10) {
      super(mutator, nodeMap, dataMap);
      this.slot0 = slot0;
      this.slot1 = slot1;
      this.slot2 = slot2;
      this.slot3 = slot3;
      this.slot4 = slot4;
      this.slot5 = slot5;
      this.slot6 = slot6;
      this.slot7 = slot7;
      this.slot8 = slot8;
      this.slot9 = slot9;
      this.slot10 = slot10;
    }

  }
  private static class Map0To12Node_Heterogeneous_BleedingEdge extends CompactMixedMapNode {

    static final long[] arrayOffsets = arrayOffsets(Map0To12Node_Heterogeneous_BleedingEdge.class,
        new String[] {"slot0", "slot1", "slot2", "slot3", "slot4", "slot5", "slot6", "slot7",
            "slot8", "slot9", "slot10", "slot11"});

    static final int nodeArity = 12;

    static final int payloadArity = 0;

    static final int slotArity = 12;

    static final int untypedSlotArity = 12;

    static final long arrayOffsetLast = arrayBase + 11 * addressSize;

    private final Object slot0;

    private final Object slot1;

    private final Object slot2;

    private final Object slot3;

    private final Object slot4;

    private final Object slot5;

    private final Object slot6;

    private final Object slot7;

    private final Object slot8;

    private final Object slot9;

    private final Object slot10;

    private final Object slot11;

    private Map0To12Node_Heterogeneous_BleedingEdge(final AtomicReference<Thread> mutator,
        final byte nodeMap, final byte dataMap, final Object slot0, final Object slot1,
        final Object slot2, final Object slot3, final Object slot4, final Object slot5,
        final Object slot6, final Object slot7, final Object slot8, final Object slot9,
        final Object slot10, final Object slot11) {
      super(mutator, nodeMap, dataMap);
      this.slot0 = slot0;
      this.slot1 = slot1;
      this.slot2 = slot2;
      this.slot3 = slot3;
      this.slot4 = slot4;
      this.slot5 = slot5;
      this.slot6 = slot6;
      this.slot7 = slot7;
      this.slot8 = slot8;
      this.slot9 = slot9;
      this.slot10 = slot10;
      this.slot11 = slot11;
    }

  }
  private static class Map0To13Node_Heterogeneous_BleedingEdge extends CompactMixedMapNode {

    static final long[] arrayOffsets = arrayOffsets(Map0To13Node_Heterogeneous_BleedingEdge.class,
        new String[] {"slot0", "slot1", "slot2", "slot3", "slot4", "slot5", "slot6", "slot7",
            "slot8", "slot9", "slot10", "slot11", "slot12"});

    static final int nodeArity = 13;

    static final int payloadArity = 0;

    static final int slotArity = 13;

    static final int untypedSlotArity = 13;

    static final long arrayOffsetLast = arrayBase + 12 * addressSize;

    private final Object slot0;

    private final Object slot1;

    private final Object slot2;

    private final Object slot3;

    private final Object slot4;

    private final Object slot5;

    private final Object slot6;

    private final Object slot7;

    private final Object slot8;

    private final Object slot9;

    private final Object slot10;

    private final Object slot11;

    private final Object slot12;

    private Map0To13Node_Heterogeneous_BleedingEdge(final AtomicReference<Thread> mutator,
        final byte nodeMap, final byte dataMap, final Object slot0, final Object slot1,
        final Object slot2, final Object slot3, final Object slot4, final Object slot5,
        final Object slot6, final Object slot7, final Object slot8, final Object slot9,
        final Object slot10, final Object slot11, final Object slot12) {
      super(mutator, nodeMap, dataMap);
      this.slot0 = slot0;
      this.slot1 = slot1;
      this.slot2 = slot2;
      this.slot3 = slot3;
      this.slot4 = slot4;
      this.slot5 = slot5;
      this.slot6 = slot6;
      this.slot7 = slot7;
      this.slot8 = slot8;
      this.slot9 = slot9;
      this.slot10 = slot10;
      this.slot11 = slot11;
      this.slot12 = slot12;
    }

  }
  private static class Map0To14Node_Heterogeneous_BleedingEdge extends CompactMixedMapNode {

    static final long[] arrayOffsets = arrayOffsets(Map0To14Node_Heterogeneous_BleedingEdge.class,
        new String[] {"slot0", "slot1", "slot2", "slot3", "slot4", "slot5", "slot6", "slot7",
            "slot8", "slot9", "slot10", "slot11", "slot12", "slot13"});

    static final int nodeArity = 14;

    static final int payloadArity = 0;

    static final int slotArity = 14;

    static final int untypedSlotArity = 14;

    static final long arrayOffsetLast = arrayBase + 13 * addressSize;

    private final Object slot0;

    private final Object slot1;

    private final Object slot2;

    private final Object slot3;

    private final Object slot4;

    private final Object slot5;

    private final Object slot6;

    private final Object slot7;

    private final Object slot8;

    private final Object slot9;

    private final Object slot10;

    private final Object slot11;

    private final Object slot12;

    private final Object slot13;

    private Map0To14Node_Heterogeneous_BleedingEdge(final AtomicReference<Thread> mutator,
        final byte nodeMap, final byte dataMap, final Object slot0, final Object slot1,
        final Object slot2, final Object slot3, final Object slot4, final Object slot5,
        final Object slot6, final Object slot7, final Object slot8, final Object slot9,
        final Object slot10, final Object slot11, final Object slot12, final Object slot13) {
      super(mutator, nodeMap, dataMap);
      this.slot0 = slot0;
      this.slot1 = slot1;
      this.slot2 = slot2;
      this.slot3 = slot3;
      this.slot4 = slot4;
      this.slot5 = slot5;
      this.slot6 = slot6;
      this.slot7 = slot7;
      this.slot8 = slot8;
      this.slot9 = slot9;
      this.slot10 = slot10;
      this.slot11 = slot11;
      this.slot12 = slot12;
      this.slot13 = slot13;
    }

  }
  private static class Map0To15Node_Heterogeneous_BleedingEdge extends CompactMixedMapNode {

    static final long[] arrayOffsets = arrayOffsets(Map0To15Node_Heterogeneous_BleedingEdge.class,
        new String[] {"slot0", "slot1", "slot2", "slot3", "slot4", "slot5", "slot6", "slot7",
            "slot8", "slot9", "slot10", "slot11", "slot12", "slot13", "slot14"});

    static final int nodeArity = 15;

    static final int payloadArity = 0;

    static final int slotArity = 15;

    static final int untypedSlotArity = 15;

    static final long arrayOffsetLast = arrayBase + 14 * addressSize;

    private final Object slot0;

    private final Object slot1;

    private final Object slot2;

    private final Object slot3;

    private final Object slot4;

    private final Object slot5;

    private final Object slot6;

    private final Object slot7;

    private final Object slot8;

    private final Object slot9;

    private final Object slot10;

    private final Object slot11;

    private final Object slot12;

    private final Object slot13;

    private final Object slot14;

    private Map0To15Node_Heterogeneous_BleedingEdge(final AtomicReference<Thread> mutator,
        final byte nodeMap, final byte dataMap, final Object slot0, final Object slot1,
        final Object slot2, final Object slot3, final Object slot4, final Object slot5,
        final Object slot6, final Object slot7, final Object slot8, final Object slot9,
        final Object slot10, final Object slot11, final Object slot12, final Object slot13,
        final Object slot14) {
      super(mutator, nodeMap, dataMap);
      this.slot0 = slot0;
      this.slot1 = slot1;
      this.slot2 = slot2;
      this.slot3 = slot3;
      this.slot4 = slot4;
      this.slot5 = slot5;
      this.slot6 = slot6;
      this.slot7 = slot7;
      this.slot8 = slot8;
      this.slot9 = slot9;
      this.slot10 = slot10;
      this.slot11 = slot11;
      this.slot12 = slot12;
      this.slot13 = slot13;
      this.slot14 = slot14;
    }

  }
  private static class Map0To16Node_Heterogeneous_BleedingEdge extends CompactMixedMapNode {

    static final long[] arrayOffsets = arrayOffsets(Map0To16Node_Heterogeneous_BleedingEdge.class,
        new String[] {"slot0", "slot1", "slot2", "slot3", "slot4", "slot5", "slot6", "slot7",
            "slot8", "slot9", "slot10", "slot11", "slot12", "slot13", "slot14", "slot15"});

    static final int nodeArity = 16;

    static final int payloadArity = 0;

    static final int slotArity = 16;

    static final int untypedSlotArity = 16;

    static final long arrayOffsetLast = arrayBase + 15 * addressSize;

    private final Object slot0;

    private final Object slot1;

    private final Object slot2;

    private final Object slot3;

    private final Object slot4;

    private final Object slot5;

    private final Object slot6;

    private final Object slot7;

    private final Object slot8;

    private final Object slot9;

    private final Object slot10;

    private final Object slot11;

    private final Object slot12;

    private final Object slot13;

    private final Object slot14;

    private final Object slot15;

    private Map0To16Node_Heterogeneous_BleedingEdge(final AtomicReference<Thread> mutator,
        final byte nodeMap, final byte dataMap, final Object slot0, final Object slot1,
        final Object slot2, final Object slot3, final Object slot4, final Object slot5,
        final Object slot6, final Object slot7, final Object slot8, final Object slot9,
        final Object slot10, final Object slot11, final Object slot12, final Object slot13,
        final Object slot14, final Object slot15) {
      super(mutator, nodeMap, dataMap);
      this.slot0 = slot0;
      this.slot1 = slot1;
      this.slot2 = slot2;
      this.slot3 = slot3;
      this.slot4 = slot4;
      this.slot5 = slot5;
      this.slot6 = slot6;
      this.slot7 = slot7;
      this.slot8 = slot8;
      this.slot9 = slot9;
      this.slot10 = slot10;
      this.slot11 = slot11;
      this.slot12 = slot12;
      this.slot13 = slot13;
      this.slot14 = slot14;
      this.slot15 = slot15;
    }

  }
  private static class Map1To0Node_Heterogeneous_BleedingEdge extends CompactMixedMapNode {

    static final long[] arrayOffsets =
        arrayOffsets(Map1To0Node_Heterogeneous_BleedingEdge.class, new String[] {"key1", "val1"});

    static final int nodeArity = 0;

    static final int payloadArity = 1;

    static final int slotArity = 2;

    static final int untypedSlotArity = 0;

    static final long arrayOffsetLast = arrayBase + 1 * addressSize;

    private final int key1;

    private final int val1;

    private Map1To0Node_Heterogeneous_BleedingEdge(final AtomicReference<Thread> mutator,
        final byte nodeMap, final byte dataMap, final int key1, final int val1) {
      super(mutator, nodeMap, dataMap);
      this.key1 = key1;
      this.val1 = val1;
    }

  }
  private static class Map1To1Node_Heterogeneous_BleedingEdge extends CompactMixedMapNode {

    static final long[] arrayOffsets = arrayOffsets(Map1To1Node_Heterogeneous_BleedingEdge.class,
        new String[] {"key1", "val1", "slot0"});

    static final int nodeArity = 1;

    static final int payloadArity = 1;

    static final int slotArity = 3;

    static final int untypedSlotArity = 1;

    static final long arrayOffsetLast = arrayBase + 2 * addressSize;

    private final int key1;

    private final int val1;

    private final Object slot0;

    private Map1To1Node_Heterogeneous_BleedingEdge(final AtomicReference<Thread> mutator,
        final byte nodeMap, final byte dataMap, final int key1, final int val1,
        final Object slot0) {
      super(mutator, nodeMap, dataMap);
      this.key1 = key1;
      this.val1 = val1;
      this.slot0 = slot0;
    }

  }
  private static class Map1To2Node_Heterogeneous_BleedingEdge extends CompactMixedMapNode {

    static final long[] arrayOffsets = arrayOffsets(Map1To2Node_Heterogeneous_BleedingEdge.class,
        new String[] {"key1", "val1", "slot0", "slot1"});

    static final int nodeArity = 2;

    static final int payloadArity = 1;

    static final int slotArity = 4;

    static final int untypedSlotArity = 2;

    static final long arrayOffsetLast = arrayBase + 3 * addressSize;

    private final int key1;

    private final int val1;

    private final Object slot0;

    private final Object slot1;

    private Map1To2Node_Heterogeneous_BleedingEdge(final AtomicReference<Thread> mutator,
        final byte nodeMap, final byte dataMap, final int key1, final int val1, final Object slot0,
        final Object slot1) {
      super(mutator, nodeMap, dataMap);
      this.key1 = key1;
      this.val1 = val1;
      this.slot0 = slot0;
      this.slot1 = slot1;
    }

  }
  private static class Map1To3Node_Heterogeneous_BleedingEdge extends CompactMixedMapNode {

    static final long[] arrayOffsets = arrayOffsets(Map1To3Node_Heterogeneous_BleedingEdge.class,
        new String[] {"key1", "val1", "slot0", "slot1", "slot2"});

    static final int nodeArity = 3;

    static final int payloadArity = 1;

    static final int slotArity = 5;

    static final int untypedSlotArity = 3;

    static final long arrayOffsetLast = arrayBase + 4 * addressSize;

    private final int key1;

    private final int val1;

    private final Object slot0;

    private final Object slot1;

    private final Object slot2;

    private Map1To3Node_Heterogeneous_BleedingEdge(final AtomicReference<Thread> mutator,
        final byte nodeMap, final byte dataMap, final int key1, final int val1, final Object slot0,
        final Object slot1, final Object slot2) {
      super(mutator, nodeMap, dataMap);
      this.key1 = key1;
      this.val1 = val1;
      this.slot0 = slot0;
      this.slot1 = slot1;
      this.slot2 = slot2;
    }

  }
  private static class Map1To4Node_Heterogeneous_BleedingEdge extends CompactMixedMapNode {

    static final long[] arrayOffsets = arrayOffsets(Map1To4Node_Heterogeneous_BleedingEdge.class,
        new String[] {"key1", "val1", "slot0", "slot1", "slot2", "slot3"});

    static final int nodeArity = 4;

    static final int payloadArity = 1;

    static final int slotArity = 6;

    static final int untypedSlotArity = 4;

    static final long arrayOffsetLast = arrayBase + 5 * addressSize;

    private final int key1;

    private final int val1;

    private final Object slot0;

    private final Object slot1;

    private final Object slot2;

    private final Object slot3;

    private Map1To4Node_Heterogeneous_BleedingEdge(final AtomicReference<Thread> mutator,
        final byte nodeMap, final byte dataMap, final int key1, final int val1, final Object slot0,
        final Object slot1, final Object slot2, final Object slot3) {
      super(mutator, nodeMap, dataMap);
      this.key1 = key1;
      this.val1 = val1;
      this.slot0 = slot0;
      this.slot1 = slot1;
      this.slot2 = slot2;
      this.slot3 = slot3;
    }

  }
  private static class Map1To5Node_Heterogeneous_BleedingEdge extends CompactMixedMapNode {

    static final long[] arrayOffsets = arrayOffsets(Map1To5Node_Heterogeneous_BleedingEdge.class,
        new String[] {"key1", "val1", "slot0", "slot1", "slot2", "slot3", "slot4"});

    static final int nodeArity = 5;

    static final int payloadArity = 1;

    static final int slotArity = 7;

    static final int untypedSlotArity = 5;

    static final long arrayOffsetLast = arrayBase + 6 * addressSize;

    private final int key1;

    private final int val1;

    private final Object slot0;

    private final Object slot1;

    private final Object slot2;

    private final Object slot3;

    private final Object slot4;

    private Map1To5Node_Heterogeneous_BleedingEdge(final AtomicReference<Thread> mutator,
        final byte nodeMap, final byte dataMap, final int key1, final int val1, final Object slot0,
        final Object slot1, final Object slot2, final Object slot3, final Object slot4) {
      super(mutator, nodeMap, dataMap);
      this.key1 = key1;
      this.val1 = val1;
      this.slot0 = slot0;
      this.slot1 = slot1;
      this.slot2 = slot2;
      this.slot3 = slot3;
      this.slot4 = slot4;
    }

  }
  private static class Map1To6Node_Heterogeneous_BleedingEdge extends CompactMixedMapNode {

    static final long[] arrayOffsets = arrayOffsets(Map1To6Node_Heterogeneous_BleedingEdge.class,
        new String[] {"key1", "val1", "slot0", "slot1", "slot2", "slot3", "slot4", "slot5"});

    static final int nodeArity = 6;

    static final int payloadArity = 1;

    static final int slotArity = 8;

    static final int untypedSlotArity = 6;

    static final long arrayOffsetLast = arrayBase + 7 * addressSize;

    private final int key1;

    private final int val1;

    private final Object slot0;

    private final Object slot1;

    private final Object slot2;

    private final Object slot3;

    private final Object slot4;

    private final Object slot5;

    private Map1To6Node_Heterogeneous_BleedingEdge(final AtomicReference<Thread> mutator,
        final byte nodeMap, final byte dataMap, final int key1, final int val1, final Object slot0,
        final Object slot1, final Object slot2, final Object slot3, final Object slot4,
        final Object slot5) {
      super(mutator, nodeMap, dataMap);
      this.key1 = key1;
      this.val1 = val1;
      this.slot0 = slot0;
      this.slot1 = slot1;
      this.slot2 = slot2;
      this.slot3 = slot3;
      this.slot4 = slot4;
      this.slot5 = slot5;
    }

  }
  private static class Map1To7Node_Heterogeneous_BleedingEdge extends CompactMixedMapNode {

    static final long[] arrayOffsets =
        arrayOffsets(Map1To7Node_Heterogeneous_BleedingEdge.class, new String[] {"key1", "val1",
            "slot0", "slot1", "slot2", "slot3", "slot4", "slot5", "slot6"});

    static final int nodeArity = 7;

    static final int payloadArity = 1;

    static final int slotArity = 9;

    static final int untypedSlotArity = 7;

    static final long arrayOffsetLast = arrayBase + 8 * addressSize;

    private final int key1;

    private final int val1;

    private final Object slot0;

    private final Object slot1;

    private final Object slot2;

    private final Object slot3;

    private final Object slot4;

    private final Object slot5;

    private final Object slot6;

    private Map1To7Node_Heterogeneous_BleedingEdge(final AtomicReference<Thread> mutator,
        final byte nodeMap, final byte dataMap, final int key1, final int val1, final Object slot0,
        final Object slot1, final Object slot2, final Object slot3, final Object slot4,
        final Object slot5, final Object slot6) {
      super(mutator, nodeMap, dataMap);
      this.key1 = key1;
      this.val1 = val1;
      this.slot0 = slot0;
      this.slot1 = slot1;
      this.slot2 = slot2;
      this.slot3 = slot3;
      this.slot4 = slot4;
      this.slot5 = slot5;
      this.slot6 = slot6;
    }

  }
  private static class Map1To8Node_Heterogeneous_BleedingEdge extends CompactMixedMapNode {

    static final long[] arrayOffsets =
        arrayOffsets(Map1To8Node_Heterogeneous_BleedingEdge.class, new String[] {"key1", "val1",
            "slot0", "slot1", "slot2", "slot3", "slot4", "slot5", "slot6", "slot7"});

    static final int nodeArity = 8;

    static final int payloadArity = 1;

    static final int slotArity = 10;

    static final int untypedSlotArity = 8;

    static final long arrayOffsetLast = arrayBase + 9 * addressSize;

    private final int key1;

    private final int val1;

    private final Object slot0;

    private final Object slot1;

    private final Object slot2;

    private final Object slot3;

    private final Object slot4;

    private final Object slot5;

    private final Object slot6;

    private final Object slot7;

    private Map1To8Node_Heterogeneous_BleedingEdge(final AtomicReference<Thread> mutator,
        final byte nodeMap, final byte dataMap, final int key1, final int val1, final Object slot0,
        final Object slot1, final Object slot2, final Object slot3, final Object slot4,
        final Object slot5, final Object slot6, final Object slot7) {
      super(mutator, nodeMap, dataMap);
      this.key1 = key1;
      this.val1 = val1;
      this.slot0 = slot0;
      this.slot1 = slot1;
      this.slot2 = slot2;
      this.slot3 = slot3;
      this.slot4 = slot4;
      this.slot5 = slot5;
      this.slot6 = slot6;
      this.slot7 = slot7;
    }

  }
  private static class Map1To9Node_Heterogeneous_BleedingEdge extends CompactMixedMapNode {

    static final long[] arrayOffsets =
        arrayOffsets(Map1To9Node_Heterogeneous_BleedingEdge.class, new String[] {"key1", "val1",
            "slot0", "slot1", "slot2", "slot3", "slot4", "slot5", "slot6", "slot7", "slot8"});

    static final int nodeArity = 9;

    static final int payloadArity = 1;

    static final int slotArity = 11;

    static final int untypedSlotArity = 9;

    static final long arrayOffsetLast = arrayBase + 10 * addressSize;

    private final int key1;

    private final int val1;

    private final Object slot0;

    private final Object slot1;

    private final Object slot2;

    private final Object slot3;

    private final Object slot4;

    private final Object slot5;

    private final Object slot6;

    private final Object slot7;

    private final Object slot8;

    private Map1To9Node_Heterogeneous_BleedingEdge(final AtomicReference<Thread> mutator,
        final byte nodeMap, final byte dataMap, final int key1, final int val1, final Object slot0,
        final Object slot1, final Object slot2, final Object slot3, final Object slot4,
        final Object slot5, final Object slot6, final Object slot7, final Object slot8) {
      super(mutator, nodeMap, dataMap);
      this.key1 = key1;
      this.val1 = val1;
      this.slot0 = slot0;
      this.slot1 = slot1;
      this.slot2 = slot2;
      this.slot3 = slot3;
      this.slot4 = slot4;
      this.slot5 = slot5;
      this.slot6 = slot6;
      this.slot7 = slot7;
      this.slot8 = slot8;
    }

  }
  private static class Map1To10Node_Heterogeneous_BleedingEdge extends CompactMixedMapNode {

    static final long[] arrayOffsets = arrayOffsets(Map1To10Node_Heterogeneous_BleedingEdge.class,
        new String[] {"key1", "val1", "slot0", "slot1", "slot2", "slot3", "slot4", "slot5", "slot6",
            "slot7", "slot8", "slot9"});

    static final int nodeArity = 10;

    static final int payloadArity = 1;

    static final int slotArity = 12;

    static final int untypedSlotArity = 10;

    static final long arrayOffsetLast = arrayBase + 11 * addressSize;

    private final int key1;

    private final int val1;

    private final Object slot0;

    private final Object slot1;

    private final Object slot2;

    private final Object slot3;

    private final Object slot4;

    private final Object slot5;

    private final Object slot6;

    private final Object slot7;

    private final Object slot8;

    private final Object slot9;

    private Map1To10Node_Heterogeneous_BleedingEdge(final AtomicReference<Thread> mutator,
        final byte nodeMap, final byte dataMap, final int key1, final int val1, final Object slot0,
        final Object slot1, final Object slot2, final Object slot3, final Object slot4,
        final Object slot5, final Object slot6, final Object slot7, final Object slot8,
        final Object slot9) {
      super(mutator, nodeMap, dataMap);
      this.key1 = key1;
      this.val1 = val1;
      this.slot0 = slot0;
      this.slot1 = slot1;
      this.slot2 = slot2;
      this.slot3 = slot3;
      this.slot4 = slot4;
      this.slot5 = slot5;
      this.slot6 = slot6;
      this.slot7 = slot7;
      this.slot8 = slot8;
      this.slot9 = slot9;
    }

  }
  private static class Map1To11Node_Heterogeneous_BleedingEdge extends CompactMixedMapNode {

    static final long[] arrayOffsets = arrayOffsets(Map1To11Node_Heterogeneous_BleedingEdge.class,
        new String[] {"key1", "val1", "slot0", "slot1", "slot2", "slot3", "slot4", "slot5", "slot6",
            "slot7", "slot8", "slot9", "slot10"});

    static final int nodeArity = 11;

    static final int payloadArity = 1;

    static final int slotArity = 13;

    static final int untypedSlotArity = 11;

    static final long arrayOffsetLast = arrayBase + 12 * addressSize;

    private final int key1;

    private final int val1;

    private final Object slot0;

    private final Object slot1;

    private final Object slot2;

    private final Object slot3;

    private final Object slot4;

    private final Object slot5;

    private final Object slot6;

    private final Object slot7;

    private final Object slot8;

    private final Object slot9;

    private final Object slot10;

    private Map1To11Node_Heterogeneous_BleedingEdge(final AtomicReference<Thread> mutator,
        final byte nodeMap, final byte dataMap, final int key1, final int val1, final Object slot0,
        final Object slot1, final Object slot2, final Object slot3, final Object slot4,
        final Object slot5, final Object slot6, final Object slot7, final Object slot8,
        final Object slot9, final Object slot10) {
      super(mutator, nodeMap, dataMap);
      this.key1 = key1;
      this.val1 = val1;
      this.slot0 = slot0;
      this.slot1 = slot1;
      this.slot2 = slot2;
      this.slot3 = slot3;
      this.slot4 = slot4;
      this.slot5 = slot5;
      this.slot6 = slot6;
      this.slot7 = slot7;
      this.slot8 = slot8;
      this.slot9 = slot9;
      this.slot10 = slot10;
    }

  }
  private static class Map1To12Node_Heterogeneous_BleedingEdge extends CompactMixedMapNode {

    static final long[] arrayOffsets = arrayOffsets(Map1To12Node_Heterogeneous_BleedingEdge.class,
        new String[] {"key1", "val1", "slot0", "slot1", "slot2", "slot3", "slot4", "slot5", "slot6",
            "slot7", "slot8", "slot9", "slot10", "slot11"});

    static final int nodeArity = 12;

    static final int payloadArity = 1;

    static final int slotArity = 14;

    static final int untypedSlotArity = 12;

    static final long arrayOffsetLast = arrayBase + 13 * addressSize;

    private final int key1;

    private final int val1;

    private final Object slot0;

    private final Object slot1;

    private final Object slot2;

    private final Object slot3;

    private final Object slot4;

    private final Object slot5;

    private final Object slot6;

    private final Object slot7;

    private final Object slot8;

    private final Object slot9;

    private final Object slot10;

    private final Object slot11;

    private Map1To12Node_Heterogeneous_BleedingEdge(final AtomicReference<Thread> mutator,
        final byte nodeMap, final byte dataMap, final int key1, final int val1, final Object slot0,
        final Object slot1, final Object slot2, final Object slot3, final Object slot4,
        final Object slot5, final Object slot6, final Object slot7, final Object slot8,
        final Object slot9, final Object slot10, final Object slot11) {
      super(mutator, nodeMap, dataMap);
      this.key1 = key1;
      this.val1 = val1;
      this.slot0 = slot0;
      this.slot1 = slot1;
      this.slot2 = slot2;
      this.slot3 = slot3;
      this.slot4 = slot4;
      this.slot5 = slot5;
      this.slot6 = slot6;
      this.slot7 = slot7;
      this.slot8 = slot8;
      this.slot9 = slot9;
      this.slot10 = slot10;
      this.slot11 = slot11;
    }

  }
  private static class Map1To13Node_Heterogeneous_BleedingEdge extends CompactMixedMapNode {

    static final long[] arrayOffsets = arrayOffsets(Map1To13Node_Heterogeneous_BleedingEdge.class,
        new String[] {"key1", "val1", "slot0", "slot1", "slot2", "slot3", "slot4", "slot5", "slot6",
            "slot7", "slot8", "slot9", "slot10", "slot11", "slot12"});

    static final int nodeArity = 13;

    static final int payloadArity = 1;

    static final int slotArity = 15;

    static final int untypedSlotArity = 13;

    static final long arrayOffsetLast = arrayBase + 14 * addressSize;

    private final int key1;

    private final int val1;

    private final Object slot0;

    private final Object slot1;

    private final Object slot2;

    private final Object slot3;

    private final Object slot4;

    private final Object slot5;

    private final Object slot6;

    private final Object slot7;

    private final Object slot8;

    private final Object slot9;

    private final Object slot10;

    private final Object slot11;

    private final Object slot12;

    private Map1To13Node_Heterogeneous_BleedingEdge(final AtomicReference<Thread> mutator,
        final byte nodeMap, final byte dataMap, final int key1, final int val1, final Object slot0,
        final Object slot1, final Object slot2, final Object slot3, final Object slot4,
        final Object slot5, final Object slot6, final Object slot7, final Object slot8,
        final Object slot9, final Object slot10, final Object slot11, final Object slot12) {
      super(mutator, nodeMap, dataMap);
      this.key1 = key1;
      this.val1 = val1;
      this.slot0 = slot0;
      this.slot1 = slot1;
      this.slot2 = slot2;
      this.slot3 = slot3;
      this.slot4 = slot4;
      this.slot5 = slot5;
      this.slot6 = slot6;
      this.slot7 = slot7;
      this.slot8 = slot8;
      this.slot9 = slot9;
      this.slot10 = slot10;
      this.slot11 = slot11;
      this.slot12 = slot12;
    }

  }
  private static class Map1To14Node_Heterogeneous_BleedingEdge extends CompactMixedMapNode {

    static final long[] arrayOffsets = arrayOffsets(Map1To14Node_Heterogeneous_BleedingEdge.class,
        new String[] {"key1", "val1", "slot0", "slot1", "slot2", "slot3", "slot4", "slot5", "slot6",
            "slot7", "slot8", "slot9", "slot10", "slot11", "slot12", "slot13"});

    static final int nodeArity = 14;

    static final int payloadArity = 1;

    static final int slotArity = 16;

    static final int untypedSlotArity = 14;

    static final long arrayOffsetLast = arrayBase + 15 * addressSize;

    private final int key1;

    private final int val1;

    private final Object slot0;

    private final Object slot1;

    private final Object slot2;

    private final Object slot3;

    private final Object slot4;

    private final Object slot5;

    private final Object slot6;

    private final Object slot7;

    private final Object slot8;

    private final Object slot9;

    private final Object slot10;

    private final Object slot11;

    private final Object slot12;

    private final Object slot13;

    private Map1To14Node_Heterogeneous_BleedingEdge(final AtomicReference<Thread> mutator,
        final byte nodeMap, final byte dataMap, final int key1, final int val1, final Object slot0,
        final Object slot1, final Object slot2, final Object slot3, final Object slot4,
        final Object slot5, final Object slot6, final Object slot7, final Object slot8,
        final Object slot9, final Object slot10, final Object slot11, final Object slot12,
        final Object slot13) {
      super(mutator, nodeMap, dataMap);
      this.key1 = key1;
      this.val1 = val1;
      this.slot0 = slot0;
      this.slot1 = slot1;
      this.slot2 = slot2;
      this.slot3 = slot3;
      this.slot4 = slot4;
      this.slot5 = slot5;
      this.slot6 = slot6;
      this.slot7 = slot7;
      this.slot8 = slot8;
      this.slot9 = slot9;
      this.slot10 = slot10;
      this.slot11 = slot11;
      this.slot12 = slot12;
      this.slot13 = slot13;
    }

  }
  private static class Map2To0Node_Heterogeneous_BleedingEdge extends CompactMixedMapNode {

    static final long[] arrayOffsets = arrayOffsets(Map2To0Node_Heterogeneous_BleedingEdge.class,
        new String[] {"key1", "val1", "key2", "val2"});

    static final int nodeArity = 0;

    static final int payloadArity = 2;

    static final int slotArity = 4;

    static final int untypedSlotArity = 0;

    static final long arrayOffsetLast = arrayBase + 3 * addressSize;

    private final int key1;

    private final int val1;

    private final int key2;

    private final int val2;

    private Map2To0Node_Heterogeneous_BleedingEdge(final AtomicReference<Thread> mutator,
        final byte nodeMap, final byte dataMap, final int key1, final int val1, final int key2,
        final int val2) {
      super(mutator, nodeMap, dataMap);
      this.key1 = key1;
      this.val1 = val1;
      this.key2 = key2;
      this.val2 = val2;
    }

  }
  private static class Map2To1Node_Heterogeneous_BleedingEdge extends CompactMixedMapNode {

    static final long[] arrayOffsets = arrayOffsets(Map2To1Node_Heterogeneous_BleedingEdge.class,
        new String[] {"key1", "val1", "key2", "val2", "slot0"});

    static final int nodeArity = 1;

    static final int payloadArity = 2;

    static final int slotArity = 5;

    static final int untypedSlotArity = 1;

    static final long arrayOffsetLast = arrayBase + 4 * addressSize;

    private final int key1;

    private final int val1;

    private final int key2;

    private final int val2;

    private final Object slot0;

    private Map2To1Node_Heterogeneous_BleedingEdge(final AtomicReference<Thread> mutator,
        final byte nodeMap, final byte dataMap, final int key1, final int val1, final int key2,
        final int val2, final Object slot0) {
      super(mutator, nodeMap, dataMap);
      this.key1 = key1;
      this.val1 = val1;
      this.key2 = key2;
      this.val2 = val2;
      this.slot0 = slot0;
    }

  }
  private static class Map2To2Node_Heterogeneous_BleedingEdge extends CompactMixedMapNode {

    static final long[] arrayOffsets = arrayOffsets(Map2To2Node_Heterogeneous_BleedingEdge.class,
        new String[] {"key1", "val1", "key2", "val2", "slot0", "slot1"});

    static final int nodeArity = 2;

    static final int payloadArity = 2;

    static final int slotArity = 6;

    static final int untypedSlotArity = 2;

    static final long arrayOffsetLast = arrayBase + 5 * addressSize;

    private final int key1;

    private final int val1;

    private final int key2;

    private final int val2;

    private final Object slot0;

    private final Object slot1;

    private Map2To2Node_Heterogeneous_BleedingEdge(final AtomicReference<Thread> mutator,
        final byte nodeMap, final byte dataMap, final int key1, final int val1, final int key2,
        final int val2, final Object slot0, final Object slot1) {
      super(mutator, nodeMap, dataMap);
      this.key1 = key1;
      this.val1 = val1;
      this.key2 = key2;
      this.val2 = val2;
      this.slot0 = slot0;
      this.slot1 = slot1;
    }

  }
  private static class Map2To3Node_Heterogeneous_BleedingEdge extends CompactMixedMapNode {

    static final long[] arrayOffsets = arrayOffsets(Map2To3Node_Heterogeneous_BleedingEdge.class,
        new String[] {"key1", "val1", "key2", "val2", "slot0", "slot1", "slot2"});

    static final int nodeArity = 3;

    static final int payloadArity = 2;

    static final int slotArity = 7;

    static final int untypedSlotArity = 3;

    static final long arrayOffsetLast = arrayBase + 6 * addressSize;

    private final int key1;

    private final int val1;

    private final int key2;

    private final int val2;

    private final Object slot0;

    private final Object slot1;

    private final Object slot2;

    private Map2To3Node_Heterogeneous_BleedingEdge(final AtomicReference<Thread> mutator,
        final byte nodeMap, final byte dataMap, final int key1, final int val1, final int key2,
        final int val2, final Object slot0, final Object slot1, final Object slot2) {
      super(mutator, nodeMap, dataMap);
      this.key1 = key1;
      this.val1 = val1;
      this.key2 = key2;
      this.val2 = val2;
      this.slot0 = slot0;
      this.slot1 = slot1;
      this.slot2 = slot2;
    }

  }
  private static class Map2To4Node_Heterogeneous_BleedingEdge extends CompactMixedMapNode {

    static final long[] arrayOffsets = arrayOffsets(Map2To4Node_Heterogeneous_BleedingEdge.class,
        new String[] {"key1", "val1", "key2", "val2", "slot0", "slot1", "slot2", "slot3"});

    static final int nodeArity = 4;

    static final int payloadArity = 2;

    static final int slotArity = 8;

    static final int untypedSlotArity = 4;

    static final long arrayOffsetLast = arrayBase + 7 * addressSize;

    private final int key1;

    private final int val1;

    private final int key2;

    private final int val2;

    private final Object slot0;

    private final Object slot1;

    private final Object slot2;

    private final Object slot3;

    private Map2To4Node_Heterogeneous_BleedingEdge(final AtomicReference<Thread> mutator,
        final byte nodeMap, final byte dataMap, final int key1, final int val1, final int key2,
        final int val2, final Object slot0, final Object slot1, final Object slot2,
        final Object slot3) {
      super(mutator, nodeMap, dataMap);
      this.key1 = key1;
      this.val1 = val1;
      this.key2 = key2;
      this.val2 = val2;
      this.slot0 = slot0;
      this.slot1 = slot1;
      this.slot2 = slot2;
      this.slot3 = slot3;
    }

  }
  private static class Map2To5Node_Heterogeneous_BleedingEdge extends CompactMixedMapNode {

    static final long[] arrayOffsets = arrayOffsets(Map2To5Node_Heterogeneous_BleedingEdge.class,
        new String[] {"key1", "val1", "key2", "val2", "slot0", "slot1", "slot2", "slot3", "slot4"});

    static final int nodeArity = 5;

    static final int payloadArity = 2;

    static final int slotArity = 9;

    static final int untypedSlotArity = 5;

    static final long arrayOffsetLast = arrayBase + 8 * addressSize;

    private final int key1;

    private final int val1;

    private final int key2;

    private final int val2;

    private final Object slot0;

    private final Object slot1;

    private final Object slot2;

    private final Object slot3;

    private final Object slot4;

    private Map2To5Node_Heterogeneous_BleedingEdge(final AtomicReference<Thread> mutator,
        final byte nodeMap, final byte dataMap, final int key1, final int val1, final int key2,
        final int val2, final Object slot0, final Object slot1, final Object slot2,
        final Object slot3, final Object slot4) {
      super(mutator, nodeMap, dataMap);
      this.key1 = key1;
      this.val1 = val1;
      this.key2 = key2;
      this.val2 = val2;
      this.slot0 = slot0;
      this.slot1 = slot1;
      this.slot2 = slot2;
      this.slot3 = slot3;
      this.slot4 = slot4;
    }

  }
  private static class Map2To6Node_Heterogeneous_BleedingEdge extends CompactMixedMapNode {

    static final long[] arrayOffsets =
        arrayOffsets(Map2To6Node_Heterogeneous_BleedingEdge.class, new String[] {"key1", "val1",
            "key2", "val2", "slot0", "slot1", "slot2", "slot3", "slot4", "slot5"});

    static final int nodeArity = 6;

    static final int payloadArity = 2;

    static final int slotArity = 10;

    static final int untypedSlotArity = 6;

    static final long arrayOffsetLast = arrayBase + 9 * addressSize;

    private final int key1;

    private final int val1;

    private final int key2;

    private final int val2;

    private final Object slot0;

    private final Object slot1;

    private final Object slot2;

    private final Object slot3;

    private final Object slot4;

    private final Object slot5;

    private Map2To6Node_Heterogeneous_BleedingEdge(final AtomicReference<Thread> mutator,
        final byte nodeMap, final byte dataMap, final int key1, final int val1, final int key2,
        final int val2, final Object slot0, final Object slot1, final Object slot2,
        final Object slot3, final Object slot4, final Object slot5) {
      super(mutator, nodeMap, dataMap);
      this.key1 = key1;
      this.val1 = val1;
      this.key2 = key2;
      this.val2 = val2;
      this.slot0 = slot0;
      this.slot1 = slot1;
      this.slot2 = slot2;
      this.slot3 = slot3;
      this.slot4 = slot4;
      this.slot5 = slot5;
    }

  }
  private static class Map2To7Node_Heterogeneous_BleedingEdge extends CompactMixedMapNode {

    static final long[] arrayOffsets =
        arrayOffsets(Map2To7Node_Heterogeneous_BleedingEdge.class, new String[] {"key1", "val1",
            "key2", "val2", "slot0", "slot1", "slot2", "slot3", "slot4", "slot5", "slot6"});

    static final int nodeArity = 7;

    static final int payloadArity = 2;

    static final int slotArity = 11;

    static final int untypedSlotArity = 7;

    static final long arrayOffsetLast = arrayBase + 10 * addressSize;

    private final int key1;

    private final int val1;

    private final int key2;

    private final int val2;

    private final Object slot0;

    private final Object slot1;

    private final Object slot2;

    private final Object slot3;

    private final Object slot4;

    private final Object slot5;

    private final Object slot6;

    private Map2To7Node_Heterogeneous_BleedingEdge(final AtomicReference<Thread> mutator,
        final byte nodeMap, final byte dataMap, final int key1, final int val1, final int key2,
        final int val2, final Object slot0, final Object slot1, final Object slot2,
        final Object slot3, final Object slot4, final Object slot5, final Object slot6) {
      super(mutator, nodeMap, dataMap);
      this.key1 = key1;
      this.val1 = val1;
      this.key2 = key2;
      this.val2 = val2;
      this.slot0 = slot0;
      this.slot1 = slot1;
      this.slot2 = slot2;
      this.slot3 = slot3;
      this.slot4 = slot4;
      this.slot5 = slot5;
      this.slot6 = slot6;
    }

  }
  private static class Map2To8Node_Heterogeneous_BleedingEdge extends CompactMixedMapNode {

    static final long[] arrayOffsets = arrayOffsets(Map2To8Node_Heterogeneous_BleedingEdge.class,
        new String[] {"key1", "val1", "key2", "val2", "slot0", "slot1", "slot2", "slot3", "slot4",
            "slot5", "slot6", "slot7"});

    static final int nodeArity = 8;

    static final int payloadArity = 2;

    static final int slotArity = 12;

    static final int untypedSlotArity = 8;

    static final long arrayOffsetLast = arrayBase + 11 * addressSize;

    private final int key1;

    private final int val1;

    private final int key2;

    private final int val2;

    private final Object slot0;

    private final Object slot1;

    private final Object slot2;

    private final Object slot3;

    private final Object slot4;

    private final Object slot5;

    private final Object slot6;

    private final Object slot7;

    private Map2To8Node_Heterogeneous_BleedingEdge(final AtomicReference<Thread> mutator,
        final byte nodeMap, final byte dataMap, final int key1, final int val1, final int key2,
        final int val2, final Object slot0, final Object slot1, final Object slot2,
        final Object slot3, final Object slot4, final Object slot5, final Object slot6,
        final Object slot7) {
      super(mutator, nodeMap, dataMap);
      this.key1 = key1;
      this.val1 = val1;
      this.key2 = key2;
      this.val2 = val2;
      this.slot0 = slot0;
      this.slot1 = slot1;
      this.slot2 = slot2;
      this.slot3 = slot3;
      this.slot4 = slot4;
      this.slot5 = slot5;
      this.slot6 = slot6;
      this.slot7 = slot7;
    }

  }
  private static class Map2To9Node_Heterogeneous_BleedingEdge extends CompactMixedMapNode {

    static final long[] arrayOffsets = arrayOffsets(Map2To9Node_Heterogeneous_BleedingEdge.class,
        new String[] {"key1", "val1", "key2", "val2", "slot0", "slot1", "slot2", "slot3", "slot4",
            "slot5", "slot6", "slot7", "slot8"});

    static final int nodeArity = 9;

    static final int payloadArity = 2;

    static final int slotArity = 13;

    static final int untypedSlotArity = 9;

    static final long arrayOffsetLast = arrayBase + 12 * addressSize;

    private final int key1;

    private final int val1;

    private final int key2;

    private final int val2;

    private final Object slot0;

    private final Object slot1;

    private final Object slot2;

    private final Object slot3;

    private final Object slot4;

    private final Object slot5;

    private final Object slot6;

    private final Object slot7;

    private final Object slot8;

    private Map2To9Node_Heterogeneous_BleedingEdge(final AtomicReference<Thread> mutator,
        final byte nodeMap, final byte dataMap, final int key1, final int val1, final int key2,
        final int val2, final Object slot0, final Object slot1, final Object slot2,
        final Object slot3, final Object slot4, final Object slot5, final Object slot6,
        final Object slot7, final Object slot8) {
      super(mutator, nodeMap, dataMap);
      this.key1 = key1;
      this.val1 = val1;
      this.key2 = key2;
      this.val2 = val2;
      this.slot0 = slot0;
      this.slot1 = slot1;
      this.slot2 = slot2;
      this.slot3 = slot3;
      this.slot4 = slot4;
      this.slot5 = slot5;
      this.slot6 = slot6;
      this.slot7 = slot7;
      this.slot8 = slot8;
    }

  }
  private static class Map2To10Node_Heterogeneous_BleedingEdge extends CompactMixedMapNode {

    static final long[] arrayOffsets = arrayOffsets(Map2To10Node_Heterogeneous_BleedingEdge.class,
        new String[] {"key1", "val1", "key2", "val2", "slot0", "slot1", "slot2", "slot3", "slot4",
            "slot5", "slot6", "slot7", "slot8", "slot9"});

    static final int nodeArity = 10;

    static final int payloadArity = 2;

    static final int slotArity = 14;

    static final int untypedSlotArity = 10;

    static final long arrayOffsetLast = arrayBase + 13 * addressSize;

    private final int key1;

    private final int val1;

    private final int key2;

    private final int val2;

    private final Object slot0;

    private final Object slot1;

    private final Object slot2;

    private final Object slot3;

    private final Object slot4;

    private final Object slot5;

    private final Object slot6;

    private final Object slot7;

    private final Object slot8;

    private final Object slot9;

    private Map2To10Node_Heterogeneous_BleedingEdge(final AtomicReference<Thread> mutator,
        final byte nodeMap, final byte dataMap, final int key1, final int val1, final int key2,
        final int val2, final Object slot0, final Object slot1, final Object slot2,
        final Object slot3, final Object slot4, final Object slot5, final Object slot6,
        final Object slot7, final Object slot8, final Object slot9) {
      super(mutator, nodeMap, dataMap);
      this.key1 = key1;
      this.val1 = val1;
      this.key2 = key2;
      this.val2 = val2;
      this.slot0 = slot0;
      this.slot1 = slot1;
      this.slot2 = slot2;
      this.slot3 = slot3;
      this.slot4 = slot4;
      this.slot5 = slot5;
      this.slot6 = slot6;
      this.slot7 = slot7;
      this.slot8 = slot8;
      this.slot9 = slot9;
    }

  }
  private static class Map2To11Node_Heterogeneous_BleedingEdge extends CompactMixedMapNode {

    static final long[] arrayOffsets = arrayOffsets(Map2To11Node_Heterogeneous_BleedingEdge.class,
        new String[] {"key1", "val1", "key2", "val2", "slot0", "slot1", "slot2", "slot3", "slot4",
            "slot5", "slot6", "slot7", "slot8", "slot9", "slot10"});

    static final int nodeArity = 11;

    static final int payloadArity = 2;

    static final int slotArity = 15;

    static final int untypedSlotArity = 11;

    static final long arrayOffsetLast = arrayBase + 14 * addressSize;

    private final int key1;

    private final int val1;

    private final int key2;

    private final int val2;

    private final Object slot0;

    private final Object slot1;

    private final Object slot2;

    private final Object slot3;

    private final Object slot4;

    private final Object slot5;

    private final Object slot6;

    private final Object slot7;

    private final Object slot8;

    private final Object slot9;

    private final Object slot10;

    private Map2To11Node_Heterogeneous_BleedingEdge(final AtomicReference<Thread> mutator,
        final byte nodeMap, final byte dataMap, final int key1, final int val1, final int key2,
        final int val2, final Object slot0, final Object slot1, final Object slot2,
        final Object slot3, final Object slot4, final Object slot5, final Object slot6,
        final Object slot7, final Object slot8, final Object slot9, final Object slot10) {
      super(mutator, nodeMap, dataMap);
      this.key1 = key1;
      this.val1 = val1;
      this.key2 = key2;
      this.val2 = val2;
      this.slot0 = slot0;
      this.slot1 = slot1;
      this.slot2 = slot2;
      this.slot3 = slot3;
      this.slot4 = slot4;
      this.slot5 = slot5;
      this.slot6 = slot6;
      this.slot7 = slot7;
      this.slot8 = slot8;
      this.slot9 = slot9;
      this.slot10 = slot10;
    }

  }
  private static class Map2To12Node_Heterogeneous_BleedingEdge extends CompactMixedMapNode {

    static final long[] arrayOffsets = arrayOffsets(Map2To12Node_Heterogeneous_BleedingEdge.class,
        new String[] {"key1", "val1", "key2", "val2", "slot0", "slot1", "slot2", "slot3", "slot4",
            "slot5", "slot6", "slot7", "slot8", "slot9", "slot10", "slot11"});

    static final int nodeArity = 12;

    static final int payloadArity = 2;

    static final int slotArity = 16;

    static final int untypedSlotArity = 12;

    static final long arrayOffsetLast = arrayBase + 15 * addressSize;

    private final int key1;

    private final int val1;

    private final int key2;

    private final int val2;

    private final Object slot0;

    private final Object slot1;

    private final Object slot2;

    private final Object slot3;

    private final Object slot4;

    private final Object slot5;

    private final Object slot6;

    private final Object slot7;

    private final Object slot8;

    private final Object slot9;

    private final Object slot10;

    private final Object slot11;

    private Map2To12Node_Heterogeneous_BleedingEdge(final AtomicReference<Thread> mutator,
        final byte nodeMap, final byte dataMap, final int key1, final int val1, final int key2,
        final int val2, final Object slot0, final Object slot1, final Object slot2,
        final Object slot3, final Object slot4, final Object slot5, final Object slot6,
        final Object slot7, final Object slot8, final Object slot9, final Object slot10,
        final Object slot11) {
      super(mutator, nodeMap, dataMap);
      this.key1 = key1;
      this.val1 = val1;
      this.key2 = key2;
      this.val2 = val2;
      this.slot0 = slot0;
      this.slot1 = slot1;
      this.slot2 = slot2;
      this.slot3 = slot3;
      this.slot4 = slot4;
      this.slot5 = slot5;
      this.slot6 = slot6;
      this.slot7 = slot7;
      this.slot8 = slot8;
      this.slot9 = slot9;
      this.slot10 = slot10;
      this.slot11 = slot11;
    }

  }
  private static class Map3To0Node_Heterogeneous_BleedingEdge extends CompactMixedMapNode {

    static final long[] arrayOffsets = arrayOffsets(Map3To0Node_Heterogeneous_BleedingEdge.class,
        new String[] {"key1", "val1", "key2", "val2", "key3", "val3"});

    static final int nodeArity = 0;

    static final int payloadArity = 3;

    static final int slotArity = 6;

    static final int untypedSlotArity = 0;

    static final long arrayOffsetLast = arrayBase + 5 * addressSize;

    private final int key1;

    private final int val1;

    private final int key2;

    private final int val2;

    private final int key3;

    private final int val3;

    private Map3To0Node_Heterogeneous_BleedingEdge(final AtomicReference<Thread> mutator,
        final byte nodeMap, final byte dataMap, final int key1, final int val1, final int key2,
        final int val2, final int key3, final int val3) {
      super(mutator, nodeMap, dataMap);
      this.key1 = key1;
      this.val1 = val1;
      this.key2 = key2;
      this.val2 = val2;
      this.key3 = key3;
      this.val3 = val3;
    }

  }
  private static class Map3To1Node_Heterogeneous_BleedingEdge extends CompactMixedMapNode {

    static final long[] arrayOffsets = arrayOffsets(Map3To1Node_Heterogeneous_BleedingEdge.class,
        new String[] {"key1", "val1", "key2", "val2", "key3", "val3", "slot0"});

    static final int nodeArity = 1;

    static final int payloadArity = 3;

    static final int slotArity = 7;

    static final int untypedSlotArity = 1;

    static final long arrayOffsetLast = arrayBase + 6 * addressSize;

    private final int key1;

    private final int val1;

    private final int key2;

    private final int val2;

    private final int key3;

    private final int val3;

    private final Object slot0;

    private Map3To1Node_Heterogeneous_BleedingEdge(final AtomicReference<Thread> mutator,
        final byte nodeMap, final byte dataMap, final int key1, final int val1, final int key2,
        final int val2, final int key3, final int val3, final Object slot0) {
      super(mutator, nodeMap, dataMap);
      this.key1 = key1;
      this.val1 = val1;
      this.key2 = key2;
      this.val2 = val2;
      this.key3 = key3;
      this.val3 = val3;
      this.slot0 = slot0;
    }

  }
  private static class Map3To2Node_Heterogeneous_BleedingEdge extends CompactMixedMapNode {

    static final long[] arrayOffsets = arrayOffsets(Map3To2Node_Heterogeneous_BleedingEdge.class,
        new String[] {"key1", "val1", "key2", "val2", "key3", "val3", "slot0", "slot1"});

    static final int nodeArity = 2;

    static final int payloadArity = 3;

    static final int slotArity = 8;

    static final int untypedSlotArity = 2;

    static final long arrayOffsetLast = arrayBase + 7 * addressSize;

    private final int key1;

    private final int val1;

    private final int key2;

    private final int val2;

    private final int key3;

    private final int val3;

    private final Object slot0;

    private final Object slot1;

    private Map3To2Node_Heterogeneous_BleedingEdge(final AtomicReference<Thread> mutator,
        final byte nodeMap, final byte dataMap, final int key1, final int val1, final int key2,
        final int val2, final int key3, final int val3, final Object slot0, final Object slot1) {
      super(mutator, nodeMap, dataMap);
      this.key1 = key1;
      this.val1 = val1;
      this.key2 = key2;
      this.val2 = val2;
      this.key3 = key3;
      this.val3 = val3;
      this.slot0 = slot0;
      this.slot1 = slot1;
    }

  }
  private static class Map3To3Node_Heterogeneous_BleedingEdge extends CompactMixedMapNode {

    static final long[] arrayOffsets = arrayOffsets(Map3To3Node_Heterogeneous_BleedingEdge.class,
        new String[] {"key1", "val1", "key2", "val2", "key3", "val3", "slot0", "slot1", "slot2"});

    static final int nodeArity = 3;

    static final int payloadArity = 3;

    static final int slotArity = 9;

    static final int untypedSlotArity = 3;

    static final long arrayOffsetLast = arrayBase + 8 * addressSize;

    private final int key1;

    private final int val1;

    private final int key2;

    private final int val2;

    private final int key3;

    private final int val3;

    private final Object slot0;

    private final Object slot1;

    private final Object slot2;

    private Map3To3Node_Heterogeneous_BleedingEdge(final AtomicReference<Thread> mutator,
        final byte nodeMap, final byte dataMap, final int key1, final int val1, final int key2,
        final int val2, final int key3, final int val3, final Object slot0, final Object slot1,
        final Object slot2) {
      super(mutator, nodeMap, dataMap);
      this.key1 = key1;
      this.val1 = val1;
      this.key2 = key2;
      this.val2 = val2;
      this.key3 = key3;
      this.val3 = val3;
      this.slot0 = slot0;
      this.slot1 = slot1;
      this.slot2 = slot2;
    }

  }
  private static class Map3To4Node_Heterogeneous_BleedingEdge extends CompactMixedMapNode {

    static final long[] arrayOffsets =
        arrayOffsets(Map3To4Node_Heterogeneous_BleedingEdge.class, new String[] {"key1", "val1",
            "key2", "val2", "key3", "val3", "slot0", "slot1", "slot2", "slot3"});

    static final int nodeArity = 4;

    static final int payloadArity = 3;

    static final int slotArity = 10;

    static final int untypedSlotArity = 4;

    static final long arrayOffsetLast = arrayBase + 9 * addressSize;

    private final int key1;

    private final int val1;

    private final int key2;

    private final int val2;

    private final int key3;

    private final int val3;

    private final Object slot0;

    private final Object slot1;

    private final Object slot2;

    private final Object slot3;

    private Map3To4Node_Heterogeneous_BleedingEdge(final AtomicReference<Thread> mutator,
        final byte nodeMap, final byte dataMap, final int key1, final int val1, final int key2,
        final int val2, final int key3, final int val3, final Object slot0, final Object slot1,
        final Object slot2, final Object slot3) {
      super(mutator, nodeMap, dataMap);
      this.key1 = key1;
      this.val1 = val1;
      this.key2 = key2;
      this.val2 = val2;
      this.key3 = key3;
      this.val3 = val3;
      this.slot0 = slot0;
      this.slot1 = slot1;
      this.slot2 = slot2;
      this.slot3 = slot3;
    }

  }
  private static class Map3To5Node_Heterogeneous_BleedingEdge extends CompactMixedMapNode {

    static final long[] arrayOffsets =
        arrayOffsets(Map3To5Node_Heterogeneous_BleedingEdge.class, new String[] {"key1", "val1",
            "key2", "val2", "key3", "val3", "slot0", "slot1", "slot2", "slot3", "slot4"});

    static final int nodeArity = 5;

    static final int payloadArity = 3;

    static final int slotArity = 11;

    static final int untypedSlotArity = 5;

    static final long arrayOffsetLast = arrayBase + 10 * addressSize;

    private final int key1;

    private final int val1;

    private final int key2;

    private final int val2;

    private final int key3;

    private final int val3;

    private final Object slot0;

    private final Object slot1;

    private final Object slot2;

    private final Object slot3;

    private final Object slot4;

    private Map3To5Node_Heterogeneous_BleedingEdge(final AtomicReference<Thread> mutator,
        final byte nodeMap, final byte dataMap, final int key1, final int val1, final int key2,
        final int val2, final int key3, final int val3, final Object slot0, final Object slot1,
        final Object slot2, final Object slot3, final Object slot4) {
      super(mutator, nodeMap, dataMap);
      this.key1 = key1;
      this.val1 = val1;
      this.key2 = key2;
      this.val2 = val2;
      this.key3 = key3;
      this.val3 = val3;
      this.slot0 = slot0;
      this.slot1 = slot1;
      this.slot2 = slot2;
      this.slot3 = slot3;
      this.slot4 = slot4;
    }

  }
  private static class Map3To6Node_Heterogeneous_BleedingEdge extends CompactMixedMapNode {

    static final long[] arrayOffsets =
        arrayOffsets(Map3To6Node_Heterogeneous_BleedingEdge.class, new String[] {"key1", "val1",
            "key2", "val2", "key3", "val3", "slot0", "slot1", "slot2", "slot3", "slot4", "slot5"});

    static final int nodeArity = 6;

    static final int payloadArity = 3;

    static final int slotArity = 12;

    static final int untypedSlotArity = 6;

    static final long arrayOffsetLast = arrayBase + 11 * addressSize;

    private final int key1;

    private final int val1;

    private final int key2;

    private final int val2;

    private final int key3;

    private final int val3;

    private final Object slot0;

    private final Object slot1;

    private final Object slot2;

    private final Object slot3;

    private final Object slot4;

    private final Object slot5;

    private Map3To6Node_Heterogeneous_BleedingEdge(final AtomicReference<Thread> mutator,
        final byte nodeMap, final byte dataMap, final int key1, final int val1, final int key2,
        final int val2, final int key3, final int val3, final Object slot0, final Object slot1,
        final Object slot2, final Object slot3, final Object slot4, final Object slot5) {
      super(mutator, nodeMap, dataMap);
      this.key1 = key1;
      this.val1 = val1;
      this.key2 = key2;
      this.val2 = val2;
      this.key3 = key3;
      this.val3 = val3;
      this.slot0 = slot0;
      this.slot1 = slot1;
      this.slot2 = slot2;
      this.slot3 = slot3;
      this.slot4 = slot4;
      this.slot5 = slot5;
    }

  }
  private static class Map3To7Node_Heterogeneous_BleedingEdge extends CompactMixedMapNode {

    static final long[] arrayOffsets = arrayOffsets(Map3To7Node_Heterogeneous_BleedingEdge.class,
        new String[] {"key1", "val1", "key2", "val2", "key3", "val3", "slot0", "slot1", "slot2",
            "slot3", "slot4", "slot5", "slot6"});

    static final int nodeArity = 7;

    static final int payloadArity = 3;

    static final int slotArity = 13;

    static final int untypedSlotArity = 7;

    static final long arrayOffsetLast = arrayBase + 12 * addressSize;

    private final int key1;

    private final int val1;

    private final int key2;

    private final int val2;

    private final int key3;

    private final int val3;

    private final Object slot0;

    private final Object slot1;

    private final Object slot2;

    private final Object slot3;

    private final Object slot4;

    private final Object slot5;

    private final Object slot6;

    private Map3To7Node_Heterogeneous_BleedingEdge(final AtomicReference<Thread> mutator,
        final byte nodeMap, final byte dataMap, final int key1, final int val1, final int key2,
        final int val2, final int key3, final int val3, final Object slot0, final Object slot1,
        final Object slot2, final Object slot3, final Object slot4, final Object slot5,
        final Object slot6) {
      super(mutator, nodeMap, dataMap);
      this.key1 = key1;
      this.val1 = val1;
      this.key2 = key2;
      this.val2 = val2;
      this.key3 = key3;
      this.val3 = val3;
      this.slot0 = slot0;
      this.slot1 = slot1;
      this.slot2 = slot2;
      this.slot3 = slot3;
      this.slot4 = slot4;
      this.slot5 = slot5;
      this.slot6 = slot6;
    }

  }
  private static class Map3To8Node_Heterogeneous_BleedingEdge extends CompactMixedMapNode {

    static final long[] arrayOffsets = arrayOffsets(Map3To8Node_Heterogeneous_BleedingEdge.class,
        new String[] {"key1", "val1", "key2", "val2", "key3", "val3", "slot0", "slot1", "slot2",
            "slot3", "slot4", "slot5", "slot6", "slot7"});

    static final int nodeArity = 8;

    static final int payloadArity = 3;

    static final int slotArity = 14;

    static final int untypedSlotArity = 8;

    static final long arrayOffsetLast = arrayBase + 13 * addressSize;

    private final int key1;

    private final int val1;

    private final int key2;

    private final int val2;

    private final int key3;

    private final int val3;

    private final Object slot0;

    private final Object slot1;

    private final Object slot2;

    private final Object slot3;

    private final Object slot4;

    private final Object slot5;

    private final Object slot6;

    private final Object slot7;

    private Map3To8Node_Heterogeneous_BleedingEdge(final AtomicReference<Thread> mutator,
        final byte nodeMap, final byte dataMap, final int key1, final int val1, final int key2,
        final int val2, final int key3, final int val3, final Object slot0, final Object slot1,
        final Object slot2, final Object slot3, final Object slot4, final Object slot5,
        final Object slot6, final Object slot7) {
      super(mutator, nodeMap, dataMap);
      this.key1 = key1;
      this.val1 = val1;
      this.key2 = key2;
      this.val2 = val2;
      this.key3 = key3;
      this.val3 = val3;
      this.slot0 = slot0;
      this.slot1 = slot1;
      this.slot2 = slot2;
      this.slot3 = slot3;
      this.slot4 = slot4;
      this.slot5 = slot5;
      this.slot6 = slot6;
      this.slot7 = slot7;
    }

  }
  private static class Map3To9Node_Heterogeneous_BleedingEdge extends CompactMixedMapNode {

    static final long[] arrayOffsets = arrayOffsets(Map3To9Node_Heterogeneous_BleedingEdge.class,
        new String[] {"key1", "val1", "key2", "val2", "key3", "val3", "slot0", "slot1", "slot2",
            "slot3", "slot4", "slot5", "slot6", "slot7", "slot8"});

    static final int nodeArity = 9;

    static final int payloadArity = 3;

    static final int slotArity = 15;

    static final int untypedSlotArity = 9;

    static final long arrayOffsetLast = arrayBase + 14 * addressSize;

    private final int key1;

    private final int val1;

    private final int key2;

    private final int val2;

    private final int key3;

    private final int val3;

    private final Object slot0;

    private final Object slot1;

    private final Object slot2;

    private final Object slot3;

    private final Object slot4;

    private final Object slot5;

    private final Object slot6;

    private final Object slot7;

    private final Object slot8;

    private Map3To9Node_Heterogeneous_BleedingEdge(final AtomicReference<Thread> mutator,
        final byte nodeMap, final byte dataMap, final int key1, final int val1, final int key2,
        final int val2, final int key3, final int val3, final Object slot0, final Object slot1,
        final Object slot2, final Object slot3, final Object slot4, final Object slot5,
        final Object slot6, final Object slot7, final Object slot8) {
      super(mutator, nodeMap, dataMap);
      this.key1 = key1;
      this.val1 = val1;
      this.key2 = key2;
      this.val2 = val2;
      this.key3 = key3;
      this.val3 = val3;
      this.slot0 = slot0;
      this.slot1 = slot1;
      this.slot2 = slot2;
      this.slot3 = slot3;
      this.slot4 = slot4;
      this.slot5 = slot5;
      this.slot6 = slot6;
      this.slot7 = slot7;
      this.slot8 = slot8;
    }

  }
  private static class Map3To10Node_Heterogeneous_BleedingEdge extends CompactMixedMapNode {

    static final long[] arrayOffsets = arrayOffsets(Map3To10Node_Heterogeneous_BleedingEdge.class,
        new String[] {"key1", "val1", "key2", "val2", "key3", "val3", "slot0", "slot1", "slot2",
            "slot3", "slot4", "slot5", "slot6", "slot7", "slot8", "slot9"});

    static final int nodeArity = 10;

    static final int payloadArity = 3;

    static final int slotArity = 16;

    static final int untypedSlotArity = 10;

    static final long arrayOffsetLast = arrayBase + 15 * addressSize;

    private final int key1;

    private final int val1;

    private final int key2;

    private final int val2;

    private final int key3;

    private final int val3;

    private final Object slot0;

    private final Object slot1;

    private final Object slot2;

    private final Object slot3;

    private final Object slot4;

    private final Object slot5;

    private final Object slot6;

    private final Object slot7;

    private final Object slot8;

    private final Object slot9;

    private Map3To10Node_Heterogeneous_BleedingEdge(final AtomicReference<Thread> mutator,
        final byte nodeMap, final byte dataMap, final int key1, final int val1, final int key2,
        final int val2, final int key3, final int val3, final Object slot0, final Object slot1,
        final Object slot2, final Object slot3, final Object slot4, final Object slot5,
        final Object slot6, final Object slot7, final Object slot8, final Object slot9) {
      super(mutator, nodeMap, dataMap);
      this.key1 = key1;
      this.val1 = val1;
      this.key2 = key2;
      this.val2 = val2;
      this.key3 = key3;
      this.val3 = val3;
      this.slot0 = slot0;
      this.slot1 = slot1;
      this.slot2 = slot2;
      this.slot3 = slot3;
      this.slot4 = slot4;
      this.slot5 = slot5;
      this.slot6 = slot6;
      this.slot7 = slot7;
      this.slot8 = slot8;
      this.slot9 = slot9;
    }

  }
  private static class Map4To0Node_Heterogeneous_BleedingEdge extends CompactMixedMapNode {

    static final long[] arrayOffsets = arrayOffsets(Map4To0Node_Heterogeneous_BleedingEdge.class,
        new String[] {"key1", "val1", "key2", "val2", "key3", "val3", "key4", "val4"});

    static final int nodeArity = 0;

    static final int payloadArity = 4;

    static final int slotArity = 8;

    static final int untypedSlotArity = 0;

    static final long arrayOffsetLast = arrayBase + 7 * addressSize;

    private final int key1;

    private final int val1;

    private final int key2;

    private final int val2;

    private final int key3;

    private final int val3;

    private final int key4;

    private final int val4;

    private Map4To0Node_Heterogeneous_BleedingEdge(final AtomicReference<Thread> mutator,
        final byte nodeMap, final byte dataMap, final int key1, final int val1, final int key2,
        final int val2, final int key3, final int val3, final int key4, final int val4) {
      super(mutator, nodeMap, dataMap);
      this.key1 = key1;
      this.val1 = val1;
      this.key2 = key2;
      this.val2 = val2;
      this.key3 = key3;
      this.val3 = val3;
      this.key4 = key4;
      this.val4 = val4;
    }

  }
  private static class Map4To1Node_Heterogeneous_BleedingEdge extends CompactMixedMapNode {

    static final long[] arrayOffsets = arrayOffsets(Map4To1Node_Heterogeneous_BleedingEdge.class,
        new String[] {"key1", "val1", "key2", "val2", "key3", "val3", "key4", "val4", "slot0"});

    static final int nodeArity = 1;

    static final int payloadArity = 4;

    static final int slotArity = 9;

    static final int untypedSlotArity = 1;

    static final long arrayOffsetLast = arrayBase + 8 * addressSize;

    private final int key1;

    private final int val1;

    private final int key2;

    private final int val2;

    private final int key3;

    private final int val3;

    private final int key4;

    private final int val4;

    private final Object slot0;

    private Map4To1Node_Heterogeneous_BleedingEdge(final AtomicReference<Thread> mutator,
        final byte nodeMap, final byte dataMap, final int key1, final int val1, final int key2,
        final int val2, final int key3, final int val3, final int key4, final int val4,
        final Object slot0) {
      super(mutator, nodeMap, dataMap);
      this.key1 = key1;
      this.val1 = val1;
      this.key2 = key2;
      this.val2 = val2;
      this.key3 = key3;
      this.val3 = val3;
      this.key4 = key4;
      this.val4 = val4;
      this.slot0 = slot0;
    }

  }
  private static class Map4To2Node_Heterogeneous_BleedingEdge extends CompactMixedMapNode {

    static final long[] arrayOffsets =
        arrayOffsets(Map4To2Node_Heterogeneous_BleedingEdge.class, new String[] {"key1", "val1",
            "key2", "val2", "key3", "val3", "key4", "val4", "slot0", "slot1"});

    static final int nodeArity = 2;

    static final int payloadArity = 4;

    static final int slotArity = 10;

    static final int untypedSlotArity = 2;

    static final long arrayOffsetLast = arrayBase + 9 * addressSize;

    private final int key1;

    private final int val1;

    private final int key2;

    private final int val2;

    private final int key3;

    private final int val3;

    private final int key4;

    private final int val4;

    private final Object slot0;

    private final Object slot1;

    private Map4To2Node_Heterogeneous_BleedingEdge(final AtomicReference<Thread> mutator,
        final byte nodeMap, final byte dataMap, final int key1, final int val1, final int key2,
        final int val2, final int key3, final int val3, final int key4, final int val4,
        final Object slot0, final Object slot1) {
      super(mutator, nodeMap, dataMap);
      this.key1 = key1;
      this.val1 = val1;
      this.key2 = key2;
      this.val2 = val2;
      this.key3 = key3;
      this.val3 = val3;
      this.key4 = key4;
      this.val4 = val4;
      this.slot0 = slot0;
      this.slot1 = slot1;
    }

  }
  private static class Map4To3Node_Heterogeneous_BleedingEdge extends CompactMixedMapNode {

    static final long[] arrayOffsets =
        arrayOffsets(Map4To3Node_Heterogeneous_BleedingEdge.class, new String[] {"key1", "val1",
            "key2", "val2", "key3", "val3", "key4", "val4", "slot0", "slot1", "slot2"});

    static final int nodeArity = 3;

    static final int payloadArity = 4;

    static final int slotArity = 11;

    static final int untypedSlotArity = 3;

    static final long arrayOffsetLast = arrayBase + 10 * addressSize;

    private final int key1;

    private final int val1;

    private final int key2;

    private final int val2;

    private final int key3;

    private final int val3;

    private final int key4;

    private final int val4;

    private final Object slot0;

    private final Object slot1;

    private final Object slot2;

    private Map4To3Node_Heterogeneous_BleedingEdge(final AtomicReference<Thread> mutator,
        final byte nodeMap, final byte dataMap, final int key1, final int val1, final int key2,
        final int val2, final int key3, final int val3, final int key4, final int val4,
        final Object slot0, final Object slot1, final Object slot2) {
      super(mutator, nodeMap, dataMap);
      this.key1 = key1;
      this.val1 = val1;
      this.key2 = key2;
      this.val2 = val2;
      this.key3 = key3;
      this.val3 = val3;
      this.key4 = key4;
      this.val4 = val4;
      this.slot0 = slot0;
      this.slot1 = slot1;
      this.slot2 = slot2;
    }

  }
  private static class Map4To4Node_Heterogeneous_BleedingEdge extends CompactMixedMapNode {

    static final long[] arrayOffsets =
        arrayOffsets(Map4To4Node_Heterogeneous_BleedingEdge.class, new String[] {"key1", "val1",
            "key2", "val2", "key3", "val3", "key4", "val4", "slot0", "slot1", "slot2", "slot3"});

    static final int nodeArity = 4;

    static final int payloadArity = 4;

    static final int slotArity = 12;

    static final int untypedSlotArity = 4;

    static final long arrayOffsetLast = arrayBase + 11 * addressSize;

    private final int key1;

    private final int val1;

    private final int key2;

    private final int val2;

    private final int key3;

    private final int val3;

    private final int key4;

    private final int val4;

    private final Object slot0;

    private final Object slot1;

    private final Object slot2;

    private final Object slot3;

    private Map4To4Node_Heterogeneous_BleedingEdge(final AtomicReference<Thread> mutator,
        final byte nodeMap, final byte dataMap, final int key1, final int val1, final int key2,
        final int val2, final int key3, final int val3, final int key4, final int val4,
        final Object slot0, final Object slot1, final Object slot2, final Object slot3) {
      super(mutator, nodeMap, dataMap);
      this.key1 = key1;
      this.val1 = val1;
      this.key2 = key2;
      this.val2 = val2;
      this.key3 = key3;
      this.val3 = val3;
      this.key4 = key4;
      this.val4 = val4;
      this.slot0 = slot0;
      this.slot1 = slot1;
      this.slot2 = slot2;
      this.slot3 = slot3;
    }

  }
  private static class Map4To5Node_Heterogeneous_BleedingEdge extends CompactMixedMapNode {

    static final long[] arrayOffsets = arrayOffsets(Map4To5Node_Heterogeneous_BleedingEdge.class,
        new String[] {"key1", "val1", "key2", "val2", "key3", "val3", "key4", "val4", "slot0",
            "slot1", "slot2", "slot3", "slot4"});

    static final int nodeArity = 5;

    static final int payloadArity = 4;

    static final int slotArity = 13;

    static final int untypedSlotArity = 5;

    static final long arrayOffsetLast = arrayBase + 12 * addressSize;

    private final int key1;

    private final int val1;

    private final int key2;

    private final int val2;

    private final int key3;

    private final int val3;

    private final int key4;

    private final int val4;

    private final Object slot0;

    private final Object slot1;

    private final Object slot2;

    private final Object slot3;

    private final Object slot4;

    private Map4To5Node_Heterogeneous_BleedingEdge(final AtomicReference<Thread> mutator,
        final byte nodeMap, final byte dataMap, final int key1, final int val1, final int key2,
        final int val2, final int key3, final int val3, final int key4, final int val4,
        final Object slot0, final Object slot1, final Object slot2, final Object slot3,
        final Object slot4) {
      super(mutator, nodeMap, dataMap);
      this.key1 = key1;
      this.val1 = val1;
      this.key2 = key2;
      this.val2 = val2;
      this.key3 = key3;
      this.val3 = val3;
      this.key4 = key4;
      this.val4 = val4;
      this.slot0 = slot0;
      this.slot1 = slot1;
      this.slot2 = slot2;
      this.slot3 = slot3;
      this.slot4 = slot4;
    }

  }
  private static class Map4To6Node_Heterogeneous_BleedingEdge extends CompactMixedMapNode {

    static final long[] arrayOffsets = arrayOffsets(Map4To6Node_Heterogeneous_BleedingEdge.class,
        new String[] {"key1", "val1", "key2", "val2", "key3", "val3", "key4", "val4", "slot0",
            "slot1", "slot2", "slot3", "slot4", "slot5"});

    static final int nodeArity = 6;

    static final int payloadArity = 4;

    static final int slotArity = 14;

    static final int untypedSlotArity = 6;

    static final long arrayOffsetLast = arrayBase + 13 * addressSize;

    private final int key1;

    private final int val1;

    private final int key2;

    private final int val2;

    private final int key3;

    private final int val3;

    private final int key4;

    private final int val4;

    private final Object slot0;

    private final Object slot1;

    private final Object slot2;

    private final Object slot3;

    private final Object slot4;

    private final Object slot5;

    private Map4To6Node_Heterogeneous_BleedingEdge(final AtomicReference<Thread> mutator,
        final byte nodeMap, final byte dataMap, final int key1, final int val1, final int key2,
        final int val2, final int key3, final int val3, final int key4, final int val4,
        final Object slot0, final Object slot1, final Object slot2, final Object slot3,
        final Object slot4, final Object slot5) {
      super(mutator, nodeMap, dataMap);
      this.key1 = key1;
      this.val1 = val1;
      this.key2 = key2;
      this.val2 = val2;
      this.key3 = key3;
      this.val3 = val3;
      this.key4 = key4;
      this.val4 = val4;
      this.slot0 = slot0;
      this.slot1 = slot1;
      this.slot2 = slot2;
      this.slot3 = slot3;
      this.slot4 = slot4;
      this.slot5 = slot5;
    }

  }
  private static class Map4To7Node_Heterogeneous_BleedingEdge extends CompactMixedMapNode {

    static final long[] arrayOffsets = arrayOffsets(Map4To7Node_Heterogeneous_BleedingEdge.class,
        new String[] {"key1", "val1", "key2", "val2", "key3", "val3", "key4", "val4", "slot0",
            "slot1", "slot2", "slot3", "slot4", "slot5", "slot6"});

    static final int nodeArity = 7;

    static final int payloadArity = 4;

    static final int slotArity = 15;

    static final int untypedSlotArity = 7;

    static final long arrayOffsetLast = arrayBase + 14 * addressSize;

    private final int key1;

    private final int val1;

    private final int key2;

    private final int val2;

    private final int key3;

    private final int val3;

    private final int key4;

    private final int val4;

    private final Object slot0;

    private final Object slot1;

    private final Object slot2;

    private final Object slot3;

    private final Object slot4;

    private final Object slot5;

    private final Object slot6;

    private Map4To7Node_Heterogeneous_BleedingEdge(final AtomicReference<Thread> mutator,
        final byte nodeMap, final byte dataMap, final int key1, final int val1, final int key2,
        final int val2, final int key3, final int val3, final int key4, final int val4,
        final Object slot0, final Object slot1, final Object slot2, final Object slot3,
        final Object slot4, final Object slot5, final Object slot6) {
      super(mutator, nodeMap, dataMap);
      this.key1 = key1;
      this.val1 = val1;
      this.key2 = key2;
      this.val2 = val2;
      this.key3 = key3;
      this.val3 = val3;
      this.key4 = key4;
      this.val4 = val4;
      this.slot0 = slot0;
      this.slot1 = slot1;
      this.slot2 = slot2;
      this.slot3 = slot3;
      this.slot4 = slot4;
      this.slot5 = slot5;
      this.slot6 = slot6;
    }

  }
  private static class Map4To8Node_Heterogeneous_BleedingEdge extends CompactMixedMapNode {

    static final long[] arrayOffsets = arrayOffsets(Map4To8Node_Heterogeneous_BleedingEdge.class,
        new String[] {"key1", "val1", "key2", "val2", "key3", "val3", "key4", "val4", "slot0",
            "slot1", "slot2", "slot3", "slot4", "slot5", "slot6", "slot7"});

    static final int nodeArity = 8;

    static final int payloadArity = 4;

    static final int slotArity = 16;

    static final int untypedSlotArity = 8;

    static final long arrayOffsetLast = arrayBase + 15 * addressSize;

    private final int key1;

    private final int val1;

    private final int key2;

    private final int val2;

    private final int key3;

    private final int val3;

    private final int key4;

    private final int val4;

    private final Object slot0;

    private final Object slot1;

    private final Object slot2;

    private final Object slot3;

    private final Object slot4;

    private final Object slot5;

    private final Object slot6;

    private final Object slot7;

    private Map4To8Node_Heterogeneous_BleedingEdge(final AtomicReference<Thread> mutator,
        final byte nodeMap, final byte dataMap, final int key1, final int val1, final int key2,
        final int val2, final int key3, final int val3, final int key4, final int val4,
        final Object slot0, final Object slot1, final Object slot2, final Object slot3,
        final Object slot4, final Object slot5, final Object slot6, final Object slot7) {
      super(mutator, nodeMap, dataMap);
      this.key1 = key1;
      this.val1 = val1;
      this.key2 = key2;
      this.val2 = val2;
      this.key3 = key3;
      this.val3 = val3;
      this.key4 = key4;
      this.val4 = val4;
      this.slot0 = slot0;
      this.slot1 = slot1;
      this.slot2 = slot2;
      this.slot3 = slot3;
      this.slot4 = slot4;
      this.slot5 = slot5;
      this.slot6 = slot6;
      this.slot7 = slot7;
    }

  }
  private static class Map5To0Node_Heterogeneous_BleedingEdge extends CompactMixedMapNode {

    static final long[] arrayOffsets =
        arrayOffsets(Map5To0Node_Heterogeneous_BleedingEdge.class, new String[] {"key1", "val1",
            "key2", "val2", "key3", "val3", "key4", "val4", "key5", "val5"});

    static final int nodeArity = 0;

    static final int payloadArity = 5;

    static final int slotArity = 10;

    static final int untypedSlotArity = 0;

    static final long arrayOffsetLast = arrayBase + 9 * addressSize;

    private final int key1;

    private final int val1;

    private final int key2;

    private final int val2;

    private final int key3;

    private final int val3;

    private final int key4;

    private final int val4;

    private final int key5;

    private final int val5;

    private Map5To0Node_Heterogeneous_BleedingEdge(final AtomicReference<Thread> mutator,
        final byte nodeMap, final byte dataMap, final int key1, final int val1, final int key2,
        final int val2, final int key3, final int val3, final int key4, final int val4,
        final int key5, final int val5) {
      super(mutator, nodeMap, dataMap);
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

  }
  private static class Map5To1Node_Heterogeneous_BleedingEdge extends CompactMixedMapNode {

    static final long[] arrayOffsets =
        arrayOffsets(Map5To1Node_Heterogeneous_BleedingEdge.class, new String[] {"key1", "val1",
            "key2", "val2", "key3", "val3", "key4", "val4", "key5", "val5", "slot0"});

    static final int nodeArity = 1;

    static final int payloadArity = 5;

    static final int slotArity = 11;

    static final int untypedSlotArity = 1;

    static final long arrayOffsetLast = arrayBase + 10 * addressSize;

    private final int key1;

    private final int val1;

    private final int key2;

    private final int val2;

    private final int key3;

    private final int val3;

    private final int key4;

    private final int val4;

    private final int key5;

    private final int val5;

    private final Object slot0;

    private Map5To1Node_Heterogeneous_BleedingEdge(final AtomicReference<Thread> mutator,
        final byte nodeMap, final byte dataMap, final int key1, final int val1, final int key2,
        final int val2, final int key3, final int val3, final int key4, final int val4,
        final int key5, final int val5, final Object slot0) {
      super(mutator, nodeMap, dataMap);
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
      this.slot0 = slot0;
    }

  }
  private static class Map5To2Node_Heterogeneous_BleedingEdge extends CompactMixedMapNode {

    static final long[] arrayOffsets =
        arrayOffsets(Map5To2Node_Heterogeneous_BleedingEdge.class, new String[] {"key1", "val1",
            "key2", "val2", "key3", "val3", "key4", "val4", "key5", "val5", "slot0", "slot1"});

    static final int nodeArity = 2;

    static final int payloadArity = 5;

    static final int slotArity = 12;

    static final int untypedSlotArity = 2;

    static final long arrayOffsetLast = arrayBase + 11 * addressSize;

    private final int key1;

    private final int val1;

    private final int key2;

    private final int val2;

    private final int key3;

    private final int val3;

    private final int key4;

    private final int val4;

    private final int key5;

    private final int val5;

    private final Object slot0;

    private final Object slot1;

    private Map5To2Node_Heterogeneous_BleedingEdge(final AtomicReference<Thread> mutator,
        final byte nodeMap, final byte dataMap, final int key1, final int val1, final int key2,
        final int val2, final int key3, final int val3, final int key4, final int val4,
        final int key5, final int val5, final Object slot0, final Object slot1) {
      super(mutator, nodeMap, dataMap);
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
      this.slot0 = slot0;
      this.slot1 = slot1;
    }

  }
  private static class Map5To3Node_Heterogeneous_BleedingEdge extends CompactMixedMapNode {

    static final long[] arrayOffsets = arrayOffsets(Map5To3Node_Heterogeneous_BleedingEdge.class,
        new String[] {"key1", "val1", "key2", "val2", "key3", "val3", "key4", "val4", "key5",
            "val5", "slot0", "slot1", "slot2"});

    static final int nodeArity = 3;

    static final int payloadArity = 5;

    static final int slotArity = 13;

    static final int untypedSlotArity = 3;

    static final long arrayOffsetLast = arrayBase + 12 * addressSize;

    private final int key1;

    private final int val1;

    private final int key2;

    private final int val2;

    private final int key3;

    private final int val3;

    private final int key4;

    private final int val4;

    private final int key5;

    private final int val5;

    private final Object slot0;

    private final Object slot1;

    private final Object slot2;

    private Map5To3Node_Heterogeneous_BleedingEdge(final AtomicReference<Thread> mutator,
        final byte nodeMap, final byte dataMap, final int key1, final int val1, final int key2,
        final int val2, final int key3, final int val3, final int key4, final int val4,
        final int key5, final int val5, final Object slot0, final Object slot1,
        final Object slot2) {
      super(mutator, nodeMap, dataMap);
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
      this.slot0 = slot0;
      this.slot1 = slot1;
      this.slot2 = slot2;
    }

  }
  private static class Map5To4Node_Heterogeneous_BleedingEdge extends CompactMixedMapNode {

    static final long[] arrayOffsets = arrayOffsets(Map5To4Node_Heterogeneous_BleedingEdge.class,
        new String[] {"key1", "val1", "key2", "val2", "key3", "val3", "key4", "val4", "key5",
            "val5", "slot0", "slot1", "slot2", "slot3"});

    static final int nodeArity = 4;

    static final int payloadArity = 5;

    static final int slotArity = 14;

    static final int untypedSlotArity = 4;

    static final long arrayOffsetLast = arrayBase + 13 * addressSize;

    private final int key1;

    private final int val1;

    private final int key2;

    private final int val2;

    private final int key3;

    private final int val3;

    private final int key4;

    private final int val4;

    private final int key5;

    private final int val5;

    private final Object slot0;

    private final Object slot1;

    private final Object slot2;

    private final Object slot3;

    private Map5To4Node_Heterogeneous_BleedingEdge(final AtomicReference<Thread> mutator,
        final byte nodeMap, final byte dataMap, final int key1, final int val1, final int key2,
        final int val2, final int key3, final int val3, final int key4, final int val4,
        final int key5, final int val5, final Object slot0, final Object slot1, final Object slot2,
        final Object slot3) {
      super(mutator, nodeMap, dataMap);
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
      this.slot0 = slot0;
      this.slot1 = slot1;
      this.slot2 = slot2;
      this.slot3 = slot3;
    }

  }
  private static class Map5To5Node_Heterogeneous_BleedingEdge extends CompactMixedMapNode {

    static final long[] arrayOffsets = arrayOffsets(Map5To5Node_Heterogeneous_BleedingEdge.class,
        new String[] {"key1", "val1", "key2", "val2", "key3", "val3", "key4", "val4", "key5",
            "val5", "slot0", "slot1", "slot2", "slot3", "slot4"});

    static final int nodeArity = 5;

    static final int payloadArity = 5;

    static final int slotArity = 15;

    static final int untypedSlotArity = 5;

    static final long arrayOffsetLast = arrayBase + 14 * addressSize;

    private final int key1;

    private final int val1;

    private final int key2;

    private final int val2;

    private final int key3;

    private final int val3;

    private final int key4;

    private final int val4;

    private final int key5;

    private final int val5;

    private final Object slot0;

    private final Object slot1;

    private final Object slot2;

    private final Object slot3;

    private final Object slot4;

    private Map5To5Node_Heterogeneous_BleedingEdge(final AtomicReference<Thread> mutator,
        final byte nodeMap, final byte dataMap, final int key1, final int val1, final int key2,
        final int val2, final int key3, final int val3, final int key4, final int val4,
        final int key5, final int val5, final Object slot0, final Object slot1, final Object slot2,
        final Object slot3, final Object slot4) {
      super(mutator, nodeMap, dataMap);
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
      this.slot0 = slot0;
      this.slot1 = slot1;
      this.slot2 = slot2;
      this.slot3 = slot3;
      this.slot4 = slot4;
    }

  }
  private static class Map5To6Node_Heterogeneous_BleedingEdge extends CompactMixedMapNode {

    static final long[] arrayOffsets = arrayOffsets(Map5To6Node_Heterogeneous_BleedingEdge.class,
        new String[] {"key1", "val1", "key2", "val2", "key3", "val3", "key4", "val4", "key5",
            "val5", "slot0", "slot1", "slot2", "slot3", "slot4", "slot5"});

    static final int nodeArity = 6;

    static final int payloadArity = 5;

    static final int slotArity = 16;

    static final int untypedSlotArity = 6;

    static final long arrayOffsetLast = arrayBase + 15 * addressSize;

    private final int key1;

    private final int val1;

    private final int key2;

    private final int val2;

    private final int key3;

    private final int val3;

    private final int key4;

    private final int val4;

    private final int key5;

    private final int val5;

    private final Object slot0;

    private final Object slot1;

    private final Object slot2;

    private final Object slot3;

    private final Object slot4;

    private final Object slot5;

    private Map5To6Node_Heterogeneous_BleedingEdge(final AtomicReference<Thread> mutator,
        final byte nodeMap, final byte dataMap, final int key1, final int val1, final int key2,
        final int val2, final int key3, final int val3, final int key4, final int val4,
        final int key5, final int val5, final Object slot0, final Object slot1, final Object slot2,
        final Object slot3, final Object slot4, final Object slot5) {
      super(mutator, nodeMap, dataMap);
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
      this.slot0 = slot0;
      this.slot1 = slot1;
      this.slot2 = slot2;
      this.slot3 = slot3;
      this.slot4 = slot4;
      this.slot5 = slot5;
    }

  }
  private static class Map6To0Node_Heterogeneous_BleedingEdge extends CompactMixedMapNode {

    static final long[] arrayOffsets =
        arrayOffsets(Map6To0Node_Heterogeneous_BleedingEdge.class, new String[] {"key1", "val1",
            "key2", "val2", "key3", "val3", "key4", "val4", "key5", "val5", "key6", "val6"});

    static final int nodeArity = 0;

    static final int payloadArity = 6;

    static final int slotArity = 12;

    static final int untypedSlotArity = 0;

    static final long arrayOffsetLast = arrayBase + 11 * addressSize;

    private final int key1;

    private final int val1;

    private final int key2;

    private final int val2;

    private final int key3;

    private final int val3;

    private final int key4;

    private final int val4;

    private final int key5;

    private final int val5;

    private final int key6;

    private final int val6;

    private Map6To0Node_Heterogeneous_BleedingEdge(final AtomicReference<Thread> mutator,
        final byte nodeMap, final byte dataMap, final int key1, final int val1, final int key2,
        final int val2, final int key3, final int val3, final int key4, final int val4,
        final int key5, final int val5, final int key6, final int val6) {
      super(mutator, nodeMap, dataMap);
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
      this.key6 = key6;
      this.val6 = val6;
    }

  }
  private static class Map6To1Node_Heterogeneous_BleedingEdge extends CompactMixedMapNode {

    static final long[] arrayOffsets = arrayOffsets(Map6To1Node_Heterogeneous_BleedingEdge.class,
        new String[] {"key1", "val1", "key2", "val2", "key3", "val3", "key4", "val4", "key5",
            "val5", "key6", "val6", "slot0"});

    static final int nodeArity = 1;

    static final int payloadArity = 6;

    static final int slotArity = 13;

    static final int untypedSlotArity = 1;

    static final long arrayOffsetLast = arrayBase + 12 * addressSize;

    private final int key1;

    private final int val1;

    private final int key2;

    private final int val2;

    private final int key3;

    private final int val3;

    private final int key4;

    private final int val4;

    private final int key5;

    private final int val5;

    private final int key6;

    private final int val6;

    private final Object slot0;

    private Map6To1Node_Heterogeneous_BleedingEdge(final AtomicReference<Thread> mutator,
        final byte nodeMap, final byte dataMap, final int key1, final int val1, final int key2,
        final int val2, final int key3, final int val3, final int key4, final int val4,
        final int key5, final int val5, final int key6, final int val6, final Object slot0) {
      super(mutator, nodeMap, dataMap);
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
      this.key6 = key6;
      this.val6 = val6;
      this.slot0 = slot0;
    }

  }
  private static class Map6To2Node_Heterogeneous_BleedingEdge extends CompactMixedMapNode {

    static final long[] arrayOffsets = arrayOffsets(Map6To2Node_Heterogeneous_BleedingEdge.class,
        new String[] {"key1", "val1", "key2", "val2", "key3", "val3", "key4", "val4", "key5",
            "val5", "key6", "val6", "slot0", "slot1"});

    static final int nodeArity = 2;

    static final int payloadArity = 6;

    static final int slotArity = 14;

    static final int untypedSlotArity = 2;

    static final long arrayOffsetLast = arrayBase + 13 * addressSize;

    private final int key1;

    private final int val1;

    private final int key2;

    private final int val2;

    private final int key3;

    private final int val3;

    private final int key4;

    private final int val4;

    private final int key5;

    private final int val5;

    private final int key6;

    private final int val6;

    private final Object slot0;

    private final Object slot1;

    private Map6To2Node_Heterogeneous_BleedingEdge(final AtomicReference<Thread> mutator,
        final byte nodeMap, final byte dataMap, final int key1, final int val1, final int key2,
        final int val2, final int key3, final int val3, final int key4, final int val4,
        final int key5, final int val5, final int key6, final int val6, final Object slot0,
        final Object slot1) {
      super(mutator, nodeMap, dataMap);
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
      this.key6 = key6;
      this.val6 = val6;
      this.slot0 = slot0;
      this.slot1 = slot1;
    }

  }
  private static class Map6To3Node_Heterogeneous_BleedingEdge extends CompactMixedMapNode {

    static final long[] arrayOffsets = arrayOffsets(Map6To3Node_Heterogeneous_BleedingEdge.class,
        new String[] {"key1", "val1", "key2", "val2", "key3", "val3", "key4", "val4", "key5",
            "val5", "key6", "val6", "slot0", "slot1", "slot2"});

    static final int nodeArity = 3;

    static final int payloadArity = 6;

    static final int slotArity = 15;

    static final int untypedSlotArity = 3;

    static final long arrayOffsetLast = arrayBase + 14 * addressSize;

    private final int key1;

    private final int val1;

    private final int key2;

    private final int val2;

    private final int key3;

    private final int val3;

    private final int key4;

    private final int val4;

    private final int key5;

    private final int val5;

    private final int key6;

    private final int val6;

    private final Object slot0;

    private final Object slot1;

    private final Object slot2;

    private Map6To3Node_Heterogeneous_BleedingEdge(final AtomicReference<Thread> mutator,
        final byte nodeMap, final byte dataMap, final int key1, final int val1, final int key2,
        final int val2, final int key3, final int val3, final int key4, final int val4,
        final int key5, final int val5, final int key6, final int val6, final Object slot0,
        final Object slot1, final Object slot2) {
      super(mutator, nodeMap, dataMap);
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
      this.key6 = key6;
      this.val6 = val6;
      this.slot0 = slot0;
      this.slot1 = slot1;
      this.slot2 = slot2;
    }

  }
  private static class Map6To4Node_Heterogeneous_BleedingEdge extends CompactMixedMapNode {

    static final long[] arrayOffsets = arrayOffsets(Map6To4Node_Heterogeneous_BleedingEdge.class,
        new String[] {"key1", "val1", "key2", "val2", "key3", "val3", "key4", "val4", "key5",
            "val5", "key6", "val6", "slot0", "slot1", "slot2", "slot3"});

    static final int nodeArity = 4;

    static final int payloadArity = 6;

    static final int slotArity = 16;

    static final int untypedSlotArity = 4;

    static final long arrayOffsetLast = arrayBase + 15 * addressSize;

    private final int key1;

    private final int val1;

    private final int key2;

    private final int val2;

    private final int key3;

    private final int val3;

    private final int key4;

    private final int val4;

    private final int key5;

    private final int val5;

    private final int key6;

    private final int val6;

    private final Object slot0;

    private final Object slot1;

    private final Object slot2;

    private final Object slot3;

    private Map6To4Node_Heterogeneous_BleedingEdge(final AtomicReference<Thread> mutator,
        final byte nodeMap, final byte dataMap, final int key1, final int val1, final int key2,
        final int val2, final int key3, final int val3, final int key4, final int val4,
        final int key5, final int val5, final int key6, final int val6, final Object slot0,
        final Object slot1, final Object slot2, final Object slot3) {
      super(mutator, nodeMap, dataMap);
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
      this.key6 = key6;
      this.val6 = val6;
      this.slot0 = slot0;
      this.slot1 = slot1;
      this.slot2 = slot2;
      this.slot3 = slot3;
    }

  }
  private static class Map7To0Node_Heterogeneous_BleedingEdge extends CompactMixedMapNode {

    static final long[] arrayOffsets = arrayOffsets(Map7To0Node_Heterogeneous_BleedingEdge.class,
        new String[] {"key1", "val1", "key2", "val2", "key3", "val3", "key4", "val4", "key5",
            "val5", "key6", "val6", "key7", "val7"});

    static final int nodeArity = 0;

    static final int payloadArity = 7;

    static final int slotArity = 14;

    static final int untypedSlotArity = 0;

    static final long arrayOffsetLast = arrayBase + 13 * addressSize;

    private final int key1;

    private final int val1;

    private final int key2;

    private final int val2;

    private final int key3;

    private final int val3;

    private final int key4;

    private final int val4;

    private final int key5;

    private final int val5;

    private final int key6;

    private final int val6;

    private final int key7;

    private final int val7;

    private Map7To0Node_Heterogeneous_BleedingEdge(final AtomicReference<Thread> mutator,
        final byte nodeMap, final byte dataMap, final int key1, final int val1, final int key2,
        final int val2, final int key3, final int val3, final int key4, final int val4,
        final int key5, final int val5, final int key6, final int val6, final int key7,
        final int val7) {
      super(mutator, nodeMap, dataMap);
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
      this.key6 = key6;
      this.val6 = val6;
      this.key7 = key7;
      this.val7 = val7;
    }

  }
  private static class Map7To1Node_Heterogeneous_BleedingEdge extends CompactMixedMapNode {

    static final long[] arrayOffsets = arrayOffsets(Map7To1Node_Heterogeneous_BleedingEdge.class,
        new String[] {"key1", "val1", "key2", "val2", "key3", "val3", "key4", "val4", "key5",
            "val5", "key6", "val6", "key7", "val7", "slot0"});

    static final int nodeArity = 1;

    static final int payloadArity = 7;

    static final int slotArity = 15;

    static final int untypedSlotArity = 1;

    static final long arrayOffsetLast = arrayBase + 14 * addressSize;

    private final int key1;

    private final int val1;

    private final int key2;

    private final int val2;

    private final int key3;

    private final int val3;

    private final int key4;

    private final int val4;

    private final int key5;

    private final int val5;

    private final int key6;

    private final int val6;

    private final int key7;

    private final int val7;

    private final Object slot0;

    private Map7To1Node_Heterogeneous_BleedingEdge(final AtomicReference<Thread> mutator,
        final byte nodeMap, final byte dataMap, final int key1, final int val1, final int key2,
        final int val2, final int key3, final int val3, final int key4, final int val4,
        final int key5, final int val5, final int key6, final int val6, final int key7,
        final int val7, final Object slot0) {
      super(mutator, nodeMap, dataMap);
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
      this.key6 = key6;
      this.val6 = val6;
      this.key7 = key7;
      this.val7 = val7;
      this.slot0 = slot0;
    }

  }
  private static class Map7To2Node_Heterogeneous_BleedingEdge extends CompactMixedMapNode {

    static final long[] arrayOffsets = arrayOffsets(Map7To2Node_Heterogeneous_BleedingEdge.class,
        new String[] {"key1", "val1", "key2", "val2", "key3", "val3", "key4", "val4", "key5",
            "val5", "key6", "val6", "key7", "val7", "slot0", "slot1"});

    static final int nodeArity = 2;

    static final int payloadArity = 7;

    static final int slotArity = 16;

    static final int untypedSlotArity = 2;

    static final long arrayOffsetLast = arrayBase + 15 * addressSize;

    private final int key1;

    private final int val1;

    private final int key2;

    private final int val2;

    private final int key3;

    private final int val3;

    private final int key4;

    private final int val4;

    private final int key5;

    private final int val5;

    private final int key6;

    private final int val6;

    private final int key7;

    private final int val7;

    private final Object slot0;

    private final Object slot1;

    private Map7To2Node_Heterogeneous_BleedingEdge(final AtomicReference<Thread> mutator,
        final byte nodeMap, final byte dataMap, final int key1, final int val1, final int key2,
        final int val2, final int key3, final int val3, final int key4, final int val4,
        final int key5, final int val5, final int key6, final int val6, final int key7,
        final int val7, final Object slot0, final Object slot1) {
      super(mutator, nodeMap, dataMap);
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
      this.key6 = key6;
      this.val6 = val6;
      this.key7 = key7;
      this.val7 = val7;
      this.slot0 = slot0;
      this.slot1 = slot1;
    }

  }
  private static class Map8To0Node_Heterogeneous_BleedingEdge extends CompactMixedMapNode {

    static final long[] arrayOffsets = arrayOffsets(Map8To0Node_Heterogeneous_BleedingEdge.class,
        new String[] {"key1", "val1", "key2", "val2", "key3", "val3", "key4", "val4", "key5",
            "val5", "key6", "val6", "key7", "val7", "key8", "val8"});

    static final int nodeArity = 0;

    static final int payloadArity = 8;

    static final int slotArity = 16;

    static final int untypedSlotArity = 0;

    static final long arrayOffsetLast = arrayBase + 15 * addressSize;

    private final int key1;

    private final int val1;

    private final int key2;

    private final int val2;

    private final int key3;

    private final int val3;

    private final int key4;

    private final int val4;

    private final int key5;

    private final int val5;

    private final int key6;

    private final int val6;

    private final int key7;

    private final int val7;

    private final int key8;

    private final int val8;

    private Map8To0Node_Heterogeneous_BleedingEdge(final AtomicReference<Thread> mutator,
        final byte nodeMap, final byte dataMap, final int key1, final int val1, final int key2,
        final int val2, final int key3, final int val3, final int key4, final int val4,
        final int key5, final int val5, final int key6, final int val6, final int key7,
        final int val7, final int key8, final int val8) {
      super(mutator, nodeMap, dataMap);
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
      this.key6 = key6;
      this.val6 = val6;
      this.key7 = key7;
      this.val7 = val7;
      this.key8 = key8;
      this.val8 = val8;
    }

  }

  private abstract static class DataLayoutHelper extends CompactMixedMapNode {

    private static final long[] arrayOffsets =
        arrayOffsets(DataLayoutHelper.class, new String[] {"slot0", "slot1"});

    public final Object slot0 = null;

    public final Object slot1 = null;

    private DataLayoutHelper() {
      super(null, (byte) 0, (byte) 0);
    }

  }

}
