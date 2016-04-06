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
import static io.usethesource.capsule.RangecopyUtils.arrayviewcopy;
import static io.usethesource.capsule.RangecopyUtils.arrayviewcopyInt;
import static io.usethesource.capsule.RangecopyUtils.arrayviewcopyObject;
import static io.usethesource.capsule.RangecopyUtils.getFromObjectRegion;
import static io.usethesource.capsule.RangecopyUtils.isBitInBitmap;
import static io.usethesource.capsule.RangecopyUtils.rangecopyIntRegion;
import static io.usethesource.capsule.RangecopyUtils.rangecopyObjectRegion;
import static io.usethesource.capsule.RangecopyUtils.setInIntRegion;
import static io.usethesource.capsule.RangecopyUtils.setInIntRegionVarArgs;
import static io.usethesource.capsule.RangecopyUtils.setInObjectRegion;
import static io.usethesource.capsule.RangecopyUtils.setInObjectRegionVarArgs;
import static io.usethesource.capsule.RangecopyUtils.sizeOfObject;

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

import io.usethesource.capsule.RangecopyUtils.ArrayView;
import io.usethesource.capsule.RangecopyUtils.Companion;
import io.usethesource.capsule.RangecopyUtils.EitherIntOrObject;
import io.usethesource.capsule.RangecopyUtils.IntArrayView;
import io.usethesource.capsule.RangecopyUtils.ObjectArrayView;
import io.usethesource.capsule.RangecopyUtils.StreamingCopy;
import io.usethesource.capsule.TrieMap_5Bits.CompactMapNode;
import io.usethesource.capsule.TrieMap_5Bits_Heterogeneous_BleedingEdge_IntIntSpecializations.Map0To0Node_5Bits_Heterogeneous_BleedingEdge;
import io.usethesource.capsule.TrieMap_5Bits_Heterogeneous_BleedingEdge_IntIntSpecializations.Map0To1Node_5Bits_Heterogeneous_BleedingEdge;
import io.usethesource.capsule.TrieMap_5Bits_Heterogeneous_BleedingEdge_IntIntSpecializations.Map0To2Node_5Bits_Heterogeneous_BleedingEdge;
import io.usethesource.capsule.TrieMap_5Bits_Heterogeneous_BleedingEdge_IntIntSpecializations.Map0To4Node_5Bits_Heterogeneous_BleedingEdge;
import io.usethesource.capsule.TrieMap_5Bits_Heterogeneous_BleedingEdge_IntIntSpecializations.Map1To0Node_5Bits_Heterogeneous_BleedingEdge;
import io.usethesource.capsule.TrieMap_5Bits_Heterogeneous_BleedingEdge_IntIntSpecializations.Map1To2Node_5Bits_Heterogeneous_BleedingEdge;
import io.usethesource.capsule.TrieMap_5Bits_Heterogeneous_BleedingEdge_IntIntSpecializations.Map2To0Node_5Bits_Heterogeneous_BleedingEdge;

@SuppressWarnings({"rawtypes", "restriction"})
public class TrieMap_5Bits_Heterogeneous_BleedingEdge implements ImmutableMap<Object, Object> {

  protected static final AbstractMapNode EMPTY_NODE =
      new Map0To0Node_5Bits_Heterogeneous_BleedingEdge(null, (int) 0, (int) 0);

  @SuppressWarnings("unchecked")
  private static final TrieMap_5Bits_Heterogeneous_BleedingEdge EMPTY_MAP =
      new TrieMap_5Bits_Heterogeneous_BleedingEdge(EMPTY_NODE, 0, 0);

  private static final boolean DEBUG = false;

  private final AbstractMapNode rootNode;
  private final int hashCode;
  private final int cachedSize;

  TrieMap_5Bits_Heterogeneous_BleedingEdge(AbstractMapNode rootNode, int hashCode, int cachedSize) {
    this.rootNode = rootNode;
    this.hashCode = hashCode;
    this.cachedSize = cachedSize;
    if (DEBUG) {
      assert checkHashCodeAndSize(hashCode, cachedSize);
    }
  }

  @SuppressWarnings("unchecked")
  public static final ImmutableMap<Object, Object> of() {
    return TrieMap_5Bits_Heterogeneous_BleedingEdge.EMPTY_MAP;
  }

  @SuppressWarnings("unchecked")
  public static final ImmutableMap<Object, Object> of(Object... keyValuePairs) {
    if (keyValuePairs.length % 2 != 0) {
      throw new IllegalArgumentException("Length of argument list is uneven: no key/value pairs.");
    }

    ImmutableMap<Object, Object> result = TrieMap_5Bits_Heterogeneous_BleedingEdge.EMPTY_MAP;

    for (int i = 0; i < keyValuePairs.length; i += 2) {
      final int key = (int) keyValuePairs[i];
      final int val = (int) keyValuePairs[i + 1];

      result = result.__put(key, val);
    }

    return result;
  }

  @SuppressWarnings("unchecked")
  public static final TransientMap<Object, Object> transientOf() {
    return TrieMap_5Bits_Heterogeneous_BleedingEdge.EMPTY_MAP.asTransient();
  }

  @SuppressWarnings("unchecked")
  public static final TransientMap<Object, Object> transientOf(Object... keyValuePairs) {
    if (keyValuePairs.length % 2 != 0) {
      throw new IllegalArgumentException("Length of argument list is uneven: no key/value pairs.");
    }

    final TransientMap<Object, Object> result =
        TrieMap_5Bits_Heterogeneous_BleedingEdge.EMPTY_MAP.asTransient();

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

    for (Iterator<Map.Entry<Object, Object>> it = entryIterator(); it.hasNext();) {
      final Map.Entry<Object, Object> entry = it.next();
      final Object key = entry.getKey();
      final Object val = entry.getValue();

      hash += key.hashCode() ^ val.hashCode();
      size += 1;
    }

    return hash == targetHash && size == targetSize;
  }

  public static final int transformHashCode(final int hash) {
    return hash;
  }

  public boolean containsKey(final int key) {
    return rootNode.containsKey(key, transformHashCode(key), 0);
  }

  public boolean containsKeyEquivalent(final int key, final Comparator<Object> cmp) {
    return rootNode.containsKeyEquivalent(key, transformHashCode(key), 0, cmp);
  }

  public boolean containsKey(final Object o) {
    try {
      @SuppressWarnings("unchecked")
      final Object key = (Object) o;
      return rootNode.containsKey(key, transformHashCode(key.hashCode()), 0);
    } catch (ClassCastException unused) {
      return false;
    }
  }

  public boolean containsKeyEquivalent(final Object o, final Comparator<Object> cmp) {
    try {
      @SuppressWarnings("unchecked")
      final Object key = (Object) o;
      return rootNode.containsKeyEquivalent(key, transformHashCode(key.hashCode()), 0, cmp);
    } catch (ClassCastException unused) {
      return false;
    }
  }

  public boolean containsValue(final Object o) {
    for (Iterator<Object> iterator = valueIterator(); iterator.hasNext();) {
      if (iterator.next().equals(o)) {
        return true;
      }
    }
    return false;
  }

  public boolean containsValueEquivalent(final Object o, final Comparator<Object> cmp) {
    for (Iterator<Object> iterator = valueIterator(); iterator.hasNext();) {
      if (cmp.compare(iterator.next(), o) == 0) {
        return true;
      }
    }
    return false;
  }

  public Object get(final Object o) {
    try {
      @SuppressWarnings("unchecked")
      final int key = (int) o;
      final Optional<Object> result = rootNode.findByKey(key, transformHashCode(key), 0);

      if (result.isPresent()) {
        return result.get();
      } else {
        return null;
      }
    } catch (ClassCastException unused) {
      return null;
    }
  }

  public Object getEquivalent(final Object o, final Comparator<Object> cmp) {
    try {
      @SuppressWarnings("unchecked")
      final int key = (int) o;
      final Optional<Object> result = rootNode.findByKey(key, transformHashCode(key), 0, cmp);

      if (result.isPresent()) {
        return result.get();
      } else {
        return null;
      }
    } catch (ClassCastException unused) {
      return null;
    }
  }

  public ImmutableMap<Object, Object> __put(final int key, final int val) {
    final int keyHash = (int) key;
    final MapResult details = MapResult.unchanged();

    final AbstractMapNode newRootNode =
        rootNode.updated(null, key, val, transformHashCode(keyHash), 0, details);

    if (details.isModified()) {
      if (details.hasReplacedValue()) {
        // final int valHashOld = details.getReplacedValue().getInt();
        // TODO: current workaround, fix & remove
        final int valHashOld = details.getReplacedValue().getAsObject().hashCode();
        
        final int valHashNew = val;

        return new TrieMap_5Bits_Heterogeneous_BleedingEdge(newRootNode,
            hashCode + ((keyHash ^ valHashNew)) - ((keyHash ^ valHashOld)), cachedSize);
      }

      final int valHash = val;

      return new TrieMap_5Bits_Heterogeneous_BleedingEdge(newRootNode,
          hashCode + ((keyHash ^ valHash)), cachedSize + 1);
    }

    return this;
  }

  public ImmutableMap<Object, Object> __putEquivalent(final int key, final int val,
      final Comparator<Object> cmp) {
    final int keyHash = (int) key;
    final MapResult details = MapResult.unchanged();

    final AbstractMapNode newRootNode =
        rootNode.updated(null, key, val, transformHashCode(keyHash), 0, details, cmp);

    if (details.isModified()) {
      if (details.hasReplacedValue()) {
        final int valHashOld = details.getReplacedValue().getInt();
        final int valHashNew = val;

        return new TrieMap_5Bits_Heterogeneous_BleedingEdge(newRootNode,
            hashCode + ((keyHash ^ valHashNew)) - ((keyHash ^ valHashOld)), cachedSize);
      }

      final int valHash = val;

      return new TrieMap_5Bits_Heterogeneous_BleedingEdge(newRootNode,
          hashCode + ((keyHash ^ valHash)), cachedSize + 1);
    }

    return this;
  }

  public ImmutableMap<Object, Object> __put(final Object key, final Object val) {
    final int keyHash = key.hashCode();
    final MapResult details = MapResult.unchanged();

    final AbstractMapNode newRootNode =
        rootNode.updated(null, key, val, transformHashCode(keyHash), 0, details);

    if (details.isModified()) {
      if (details.hasReplacedValue()) {
        final int valHashOld = details.getReplacedValue().getObject().hashCode();
        final int valHashNew = val.hashCode();

        return new TrieMap_5Bits_Heterogeneous_BleedingEdge(newRootNode,
            hashCode + ((keyHash ^ valHashNew)) - ((keyHash ^ valHashOld)), cachedSize);
      }

      final int valHash = val.hashCode();

      return new TrieMap_5Bits_Heterogeneous_BleedingEdge(newRootNode,
          hashCode + ((keyHash ^ valHash)), cachedSize + 1);
    }

    return this;
  }

  public ImmutableMap<Object, Object> __putEquivalent(final Object key, final Object val,
      final Comparator<Object> cmp) {
    final int keyHash = key.hashCode();
    final MapResult details = MapResult.unchanged();

    final AbstractMapNode newRootNode =
        rootNode.updated(null, key, val, transformHashCode(keyHash), 0, details, cmp);

    if (details.isModified()) {
      if (details.hasReplacedValue()) {
        final int valHashOld = details.getReplacedValue().getObject().hashCode();
        final int valHashNew = val.hashCode();

        return new TrieMap_5Bits_Heterogeneous_BleedingEdge(newRootNode,
            hashCode + ((keyHash ^ valHashNew)) - ((keyHash ^ valHashOld)), cachedSize);
      }

      final int valHash = val.hashCode();

      return new TrieMap_5Bits_Heterogeneous_BleedingEdge(newRootNode,
          hashCode + ((keyHash ^ valHash)), cachedSize + 1);
    }

    return this;
  }

  public ImmutableMap<Object, Object> __putAll(final Map<? extends Object, ? extends Object> map) {
    final TransientMap<Object, Object> tmpTransient = this.asTransient();
    tmpTransient.__putAll(map);
    return tmpTransient.freeze();
  }

  public ImmutableMap<Object, Object> __putAllEquivalent(
      final Map<? extends Object, ? extends Object> map, final Comparator<Object> cmp) {
    final TransientMap<Object, Object> tmpTransient = this.asTransient();
    tmpTransient.__putAllEquivalent(map, cmp);
    return tmpTransient.freeze();
  }

  public ImmutableMap<Object, Object> __remove(final int key) {
    final int keyHash = (int) key;
    final MapResult details = MapResult.unchanged();

    final AbstractMapNode newRootNode =
        rootNode.removed(null, key, transformHashCode(keyHash), 0, details);

    if (details.isModified()) {
      assert details.hasReplacedValue();
      final int valHash = details.getReplacedValue().getInt();
      return new TrieMap_5Bits_Heterogeneous_BleedingEdge(newRootNode,
          hashCode - ((keyHash ^ valHash)), cachedSize - 1);
    }

    return this;
  }

  public ImmutableMap<Object, Object> __removeEquivalent(final int key,
      final Comparator<Object> cmp) {
    final int keyHash = (int) key;
    final MapResult details = MapResult.unchanged();

    final AbstractMapNode newRootNode =
        rootNode.removed(null, key, transformHashCode(keyHash), 0, details, cmp);

    if (details.isModified()) {
      assert details.hasReplacedValue();
      final int valHash = details.getReplacedValue().getInt();
      return new TrieMap_5Bits_Heterogeneous_BleedingEdge(newRootNode,
          hashCode - ((keyHash ^ valHash)), cachedSize - 1);
    }

    return this;
  }

  public ImmutableMap<Object, Object> __remove(final Object key) {
    final int keyHash = key.hashCode();
    final MapResult details = MapResult.unchanged();

    final AbstractMapNode newRootNode =
        rootNode.removed(null, key, transformHashCode(keyHash), 0, details);

    if (details.isModified()) {
      assert details.hasReplacedValue();
      final int valHash = details.getReplacedValue().getObject().hashCode();
      return new TrieMap_5Bits_Heterogeneous_BleedingEdge(newRootNode,
          hashCode - ((keyHash ^ valHash)), cachedSize - 1);
    }

    return this;
  }

  public ImmutableMap<Object, Object> __removeEquivalent(final Object key,
      final Comparator<Object> cmp) {
    final int keyHash = key.hashCode();
    final MapResult details = MapResult.unchanged();

    final AbstractMapNode newRootNode =
        rootNode.removed(null, key, transformHashCode(keyHash), 0, details, cmp);

    if (details.isModified()) {
      assert details.hasReplacedValue();
      final int valHash = details.getReplacedValue().getObject().hashCode();
      return new TrieMap_5Bits_Heterogeneous_BleedingEdge(newRootNode,
          hashCode - ((keyHash ^ valHash)), cachedSize - 1);
    }

    return this;
  }

  public Object put(final Object key, final Object val) {
    throw new UnsupportedOperationException();
  }

  public void putAll(final Map<? extends Object, ? extends Object> m) {
    throw new UnsupportedOperationException();
  }

  public void clear() {
    throw new UnsupportedOperationException();
  }

  public Object remove(final Object key) {
    throw new UnsupportedOperationException();
  }

  public int size() {
    return cachedSize;
  }

  public boolean isEmpty() {
    return cachedSize == 0;
  }

  public Iterator<Object> keyIterator() {
    return new MapKeyIterator(rootNode);
  }

  public Iterator<Object> valueIterator() {
    return new MapValueIterator(rootNode);
  }

  public Iterator<Map.Entry<Object, Object>> entryIterator() {
    return new MapEntryIterator(rootNode);
  }

  @Override
  public Set<Object> keySet() {
    Set<Object> keySet = null;

    if (keySet == null) {
      keySet = new AbstractSet<Object>() {
        @Override
        public Iterator<Object> iterator() {
          return TrieMap_5Bits_Heterogeneous_BleedingEdge.this.keyIterator();
        }

        @Override
        public int size() {
          return TrieMap_5Bits_Heterogeneous_BleedingEdge.this.size();
        }

        @Override
        public boolean isEmpty() {
          return TrieMap_5Bits_Heterogeneous_BleedingEdge.this.isEmpty();
        }

        @Override
        public void clear() {
          TrieMap_5Bits_Heterogeneous_BleedingEdge.this.clear();
        }

        @Override
        public boolean contains(Object k) {
          return TrieMap_5Bits_Heterogeneous_BleedingEdge.this.containsKey(k);
        }
      };
    }

    return keySet;
  }

  @Override
  public Collection<Object> values() {
    Collection<Object> values = null;

    if (values == null) {
      values = new AbstractCollection<Object>() {
        @Override
        public Iterator<Object> iterator() {
          return TrieMap_5Bits_Heterogeneous_BleedingEdge.this.valueIterator();
        }

        @Override
        public int size() {
          return TrieMap_5Bits_Heterogeneous_BleedingEdge.this.size();
        }

        @Override
        public boolean isEmpty() {
          return TrieMap_5Bits_Heterogeneous_BleedingEdge.this.isEmpty();
        }

        @Override
        public void clear() {
          TrieMap_5Bits_Heterogeneous_BleedingEdge.this.clear();
        }

        @Override
        public boolean contains(Object v) {
          return TrieMap_5Bits_Heterogeneous_BleedingEdge.this.containsValue(v);
        }
      };
    }

    return values;
  }

  @Override
  public Set<java.util.Map.Entry<Object, Object>> entrySet() {
    Set<java.util.Map.Entry<Object, Object>> entrySet = null;

    if (entrySet == null) {
      entrySet = new AbstractSet<java.util.Map.Entry<Object, Object>>() {
        @Override
        public Iterator<java.util.Map.Entry<Object, Object>> iterator() {
          return new Iterator<Map.Entry<Object, Object>>() {
            private final Iterator<Map.Entry<Object, Object>> i = entryIterator();

            @Override
            public boolean hasNext() {
              return i.hasNext();
            }

            @Override
            public Map.Entry<Object, Object> next() {
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
          return TrieMap_5Bits_Heterogeneous_BleedingEdge.this.size();
        }

        @Override
        public boolean isEmpty() {
          return TrieMap_5Bits_Heterogeneous_BleedingEdge.this.isEmpty();
        }

        @Override
        public void clear() {
          TrieMap_5Bits_Heterogeneous_BleedingEdge.this.clear();
        }

        @Override
        public boolean contains(Object k) {
          return TrieMap_5Bits_Heterogeneous_BleedingEdge.this.containsKey(k);
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

    if (other instanceof TrieMap_5Bits_Heterogeneous_BleedingEdge) {
      TrieMap_5Bits_Heterogeneous_BleedingEdge that =
          (TrieMap_5Bits_Heterogeneous_BleedingEdge) other;

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
          final Object key = (Object) entry.getKey();
          final Optional<Object> result =
              rootNode.findByKey(key, transformHashCode(key.hashCode()), 0);

          if (!result.isPresent()) {
            return false;
          } else {
            @SuppressWarnings("unchecked")
            final Object val = (Object) entry.getValue();

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
  public TransientMap<Object, Object> asTransient() {
    return new TransientTrieMap_5Bits_Heterogeneous_BleedingEdge(this);
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
    return new TrieMap_5Bits_Heterogeneous_BleedingEdgeNodeIterator(rootNode);
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
    final int[][] sumArityCombinations = new int[33][33];

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
    final int[] sumArity = new int[33];

    final int maxArity = 32; // TODO: factor out constant

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

    final int[] cumsumArity = new int[33];
    for (int cumsum = 0, i = 0; i < 33; i++) {
      cumsum += sumArity[i];
      cumsumArity[i] = cumsum;
    }

    final float threshhold = 0.01f; // for printing results
    for (int i = 0; i < 33; i++) {
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
    private EitherIntOrObject replacedValue;
    private boolean isModified;
    private boolean isReplaced;

    // update: inserted/removed single element, element count changed
    public void modified() {
      this.isModified = true;
    }

    public void updated(EitherIntOrObject replacedValue) {
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

    public EitherIntOrObject getReplacedValue() {
      return replacedValue;
    }
  }
  protected static interface INode<K, V> {
  }
  protected abstract static class AbstractMapNode {

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

    abstract boolean containsKey(final int key, final int keyHash, final int shift);

    abstract boolean containsKeyEquivalent(final int key, final int keyHash, final int shift,
        final Comparator<Object> cmp);

    abstract boolean containsKey(final Object key, final int keyHash, final int shift);

    abstract boolean containsKeyEquivalent(final Object key, final int keyHash, final int shift,
        final Comparator<Object> cmp);

    abstract Optional<Object> findByKey(final int key, final int keyHash, final int shift);

    abstract Optional<Object> findByKey(final int key, final int keyHash, final int shift,
        final Comparator<Object> cmp);

    abstract Optional<Object> findByKey(final Object key, final int keyHash, final int shift);

    abstract Optional<Object> findByKey(final Object key, final int keyHash, final int shift,
        final Comparator<Object> cmp);

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

    abstract public AbstractMapNode removed(final AtomicReference<Thread> mutator, final int key,
        final int keyHash, final int shift, final MapResult details);

    abstract public AbstractMapNode removed(final AtomicReference<Thread> mutator, final int key,
        final int keyHash, final int shift, final MapResult details, final Comparator<Object> cmp);

    abstract public AbstractMapNode removed(final AtomicReference<Thread> mutator, final Object key,
        final int keyHash, final int shift, final MapResult details);

    abstract public AbstractMapNode removed(final AtomicReference<Thread> mutator, final Object key,
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

    abstract Map.Entry<Object, Object> getKeyValueEntry(final int index);

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
      final Iterator<Object> it = new MapKeyIterator(this);

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
        switch (this.payloadArity() + this.rarePayloadArity()) {
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

    abstract public boolean equals(final Object other);

    abstract public String toString();
  }
  protected abstract static class CompactMapNode extends AbstractMapNode {

    protected CompactMapNode(final AtomicReference<Thread> mutator, final int rawMap1,
        final int rawMap2) {
      this.rawMap1 = rawMap1;
      this.rawMap2 = rawMap2;
    }

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
      Class[][] next = new Class[33][65];

      try {
        for (int m = 0; m <= 32; m++) {
          for (int n = 0; n <= 64; n++) {
            int mNext = m;
            int nNext = n;

            // TODO: last expression is not properly generated yet and maybe incorrect
            if (mNext < 0 || mNext > 32 || nNext < 0 || nNext > 64
                || Math.ceil(nNext / 2.0) + mNext > 32) {
              next[m][n] = null;
            } else {
              next[m][n] = Class.forName(String.format(
                  "io.usethesource.capsule.TrieMap_5Bits_Heterogeneous_BleedingEdge_IntIntSpecializations$Map%dTo%dNode_5Bits_Heterogeneous_BleedingEdge",
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

    static long globalRawMap1Offset =
        fieldOffset(Map0To2Node_5Bits_Heterogeneous_BleedingEdge.class, "rawMap1");

    static long globalRawMap2Offset =
        fieldOffset(Map0To2Node_5Bits_Heterogeneous_BleedingEdge.class, "rawMap2");

    static long globalArrayOffsetsOffset =
        fieldOffset(Map0To2Node_5Bits_Heterogeneous_BleedingEdge.class, "arrayOffsets");

    static long globalNodeArityOffset =
        fieldOffset(Map0To2Node_5Bits_Heterogeneous_BleedingEdge.class, "nodeArity");

    static long globalPayloadArityOffset =
        fieldOffset(Map0To2Node_5Bits_Heterogeneous_BleedingEdge.class, "payloadArity");

    static long globalSlotArityOffset =
        fieldOffset(Map0To2Node_5Bits_Heterogeneous_BleedingEdge.class, "slotArity");

    static long globalUntypedSlotArityOffset =
        fieldOffset(Map0To2Node_5Bits_Heterogeneous_BleedingEdge.class, "untypedSlotArity");

    static long globalRareBaseOffset =
        fieldOffset(Map0To2Node_5Bits_Heterogeneous_BleedingEdge.class, "rareBase");

    static long globalArrayOffsetLastOffset =
        fieldOffset(Map0To2Node_5Bits_Heterogeneous_BleedingEdge.class, "arrayOffsetLast");

    static long globalNodeBaseOffset =
        fieldOffset(Map0To2Node_5Bits_Heterogeneous_BleedingEdge.class, "nodeBase");

    Companion getCompanion() {

      Class clazz = this.getClass();

      int nodeArity = unsafe.getInt(clazz, globalNodeArityOffset);
      int payloadArity = unsafe.getInt(clazz, globalPayloadArityOffset);
      int slotArity = unsafe.getInt(clazz, globalSlotArityOffset);
      int untypedSlotArity = unsafe.getInt(clazz, globalUntypedSlotArityOffset);
      long rareBase = unsafe.getLong(clazz, globalRareBaseOffset);
      long arrayOffsetLast = unsafe.getLong(clazz, globalArrayOffsetLastOffset);
      long nodeBase = unsafe.getLong(clazz, globalNodeBaseOffset);

      return new Companion(nodeArity, payloadArity, slotArity, untypedSlotArity, rareBase,
          arrayOffsetLast, nodeBase);

    }

    final ArrayView getIntArrayView() {
      final Class<?> clazz = this.getClass();
      final int payloadArity = unsafe.getInt(clazz, globalPayloadArityOffset);
      
      return new IntArrayView(this, arrayBase, payloadArity * TUPLE_LENGTH);      
    }
    
    final ObjectArrayView getObjectArrayView() {
      final Class<?> clazz = this.getClass();
      final int untypedSlotArity = unsafe.getInt(clazz, globalUntypedSlotArityOffset);
      final long rareBase = unsafe.getLong(clazz, globalRareBaseOffset);

      return new ObjectArrayView(this, rareBase, untypedSlotArity);
    }   
    
    static final int hashCodeLength() {
      return 32;
    }

    static final int bitPartitionSize() {
      return 5;
    }

    static final int bitPartitionMask() {
      return 0b11111;
    }

    static final int mask(final int keyHash, final int shift) {
      return (keyHash >>> shift) & bitPartitionMask();
    }

    static final int bitpos(final int mask) {
      return (int) (1 << mask);
    }

    int nodeMap() {
      return (int) (rawMap1() ^ rareMap());
    }

    int dataMap() {
      return (int) (rawMap2() ^ rareMap());
    }

    int rareMap() {
      return (int) (rawMap1() & rawMap2());
    }

    public int rawMap1() {
      return rawMap1;
    }

    private int rawMap1;

    public int rawMap2() {
      return rawMap2;
    }

    private int rawMap2;

    static final boolean isRare(final Object o) {
      throw new UnsupportedOperationException(); // TODO: to implement
    }

    static final boolean isRare(final Object o0, final Object o1) {
      throw new UnsupportedOperationException(); // TODO: to implement
    }

    static final boolean isRare(final int bitpos) {
      throw new UnsupportedOperationException(); // TODO: to implement
    }

    static final int getKey(final Class<? extends CompactMapNode> clazz,
        final CompactMapNode instance, final int index) {
      // TODO: generate global offset for begin of rare payload
      // TODO: remove hard-coded sizeOf(int)

      long keyOffset = arrayBase + (TUPLE_LENGTH * index + 0) * 4;
      return (int) unsafe.getInt(instance, keyOffset);

    }

    static final Object getRareKey(final Class<? extends CompactMapNode> clazz,
        final CompactMapNode instance, final int index) {
      // TODO: generate global offset for begin of rare payload
      // TODO: remove hard-coded sizeOf(int)

      long rareBase = unsafe.getLong(clazz, globalRareBaseOffset);
      long keyOffset = rareBase + (TUPLE_LENGTH * index + 0) * addressSize;

      return (Object) getFromObjectRegion(instance, rareBase, TUPLE_LENGTH * index + 0);

    }

    static final int getVal(final Class<? extends CompactMapNode> clazz,
        final CompactMapNode instance, final int index) {
      // TODO: generate global offset for begin of rare payload
      // TODO: remove hard-coded sizeOf(int)

      long keyOffset = arrayBase + (TUPLE_LENGTH * index + 1) * 4;
      return (int) unsafe.getInt(instance, keyOffset);

    }

    static final Object getRareVal(final Class<? extends CompactMapNode> clazz,
        final CompactMapNode instance, final int index) {
      // TODO: generate global offset for begin of rare payload
      // TODO: remove hard-coded sizeOf(int)

      long rareBase = unsafe.getLong(clazz, globalRareBaseOffset);
      long keyOffset = rareBase + (TUPLE_LENGTH * index + 1) * addressSize;

      return (Object) getFromObjectRegion(instance, rareBase, TUPLE_LENGTH * index + 1);

    }

    static final AbstractMapNode getNode(final Class<? extends CompactMapNode> clazz,
        final CompactMapNode instance, final int index) {
      final int untypedSlotArity = unsafe.getInt(clazz, globalUntypedSlotArityOffset);
      final long rareBase = unsafe.getLong(clazz, globalRareBaseOffset);

      final int pIndex = untypedSlotArity - 1 - index;

      return (AbstractMapNode) getFromObjectRegion(instance, rareBase, pIndex);
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
              TUPLE_LENGTH * index + TUPLE_LENGTH * java.lang.Integer.bitCount(dataMap());
          break;
        case RARE_VAL:
          physicalIndex =
              TUPLE_LENGTH * index + TUPLE_LENGTH * java.lang.Integer.bitCount(dataMap()) + 1;
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

    static final CompactMapNode allocateHeapRegion(final Class<? extends CompactMapNode> clazz) {
      try {
        final Object newInstance = unsafe.allocateInstance(clazz);
        return (CompactMapNode) newInstance;
      } catch (ClassCastException | InstantiationException e) {
        throw new RuntimeException(e);
      }
    }

    static final CompactMapNode allocateHeapRegion(final int dim1, final int dim2) {
      final Class clazz = specializationsByContentAndNodes[dim1][dim2];
      return allocateHeapRegion(clazz);
    }

    CompactMapNode copyAndSetValue(final AtomicReference<Thread> mutator, final int bitpos,
        final int index, final int val) {
      final Class srcClass = this.getClass();

      final int payloadArity = unsafe.getInt(srcClass, globalPayloadArityOffset);
      final int untypedSlotArity = unsafe.getInt(srcClass, globalUntypedSlotArityOffset);

      final CompactMapNode src = this;
      final CompactMapNode dst = allocateHeapRegion(srcClass);

      dst.rawMap1 = rawMap1;

      dst.rawMap2 = rawMap2;

      int pIndex = TUPLE_LENGTH * index + 1;

      long offset = arrayBase;
      offset += rangecopyIntRegion(src, offset, dst, offset, 2 * payloadArity);

      setInIntRegion(dst, arrayBase, pIndex, val);

      long rareBase = offset;
      offset += rangecopyObjectRegion(src, offset, dst, offset, untypedSlotArity);

      /*
       * final int pIndex = TUPLE_LENGTH * index + 1;
       * 
       * rangecopyPrimitiveRegion(src, arrayBase, dst, arrayBase, primitiveRegionSize);
       * setInIntRegion(dst, arrayBase, pIndex, val);
       * 
       * rangecopyObjectRegion(src, rareBase, 0, dst, rareBase, 0, untypedSlotArity);
       */

      return dst;
    }

    CompactMapNode copyAndSetRareValue(final AtomicReference<Thread> mutator, final int bitpos,
        final int index, final Object val) {
      final Class srcClass = this.getClass();

      final int payloadArity = unsafe.getInt(srcClass, globalPayloadArityOffset);
      final int untypedSlotArity = unsafe.getInt(srcClass, globalUntypedSlotArityOffset);

      final CompactMapNode src = this;
      final CompactMapNode dst = allocateHeapRegion(srcClass);

      dst.rawMap1 = rawMap1;

      dst.rawMap2 = rawMap2;

      int pIndex = TUPLE_LENGTH * index + 1;

      long offset = arrayBase;
      offset += rangecopyIntRegion(src, offset, dst, offset, 2 * payloadArity);

      long rareBase = offset;
      offset += rangecopyObjectRegion(src, offset, dst, offset, untypedSlotArity);

      setInObjectRegion(dst, rareBase, pIndex, val);

      /*
       * final int pIndex = TUPLE_LENGTH * index + 1;
       * 
       * rangecopyPrimitiveRegion(src, arrayBase, dst, arrayBase, primitiveRegionSize);
       * 
       * rangecopyObjectRegion(src, rareBase, 0, dst, rareBase, 0, untypedSlotArity);
       * setInObjectRegion(dst, rareBase, pIndex, val);
       */

      return dst;
    }

    CompactMapNode copyAndInsertValue(final AtomicReference<Thread> mutator, final int bitpos,
        final int index, final int key, final int val) {
      final Class srcClass = this.getClass();

      final int payloadArity = unsafe.getInt(srcClass, globalPayloadArityOffset);
      final int untypedSlotArity = unsafe.getInt(srcClass, globalUntypedSlotArityOffset);

      final CompactMapNode src = this;
      final CompactMapNode dst = allocateHeapRegion(payloadArity + 1, untypedSlotArity);

      dst.rawMap1 = rawMap1;
      dst.rawMap2 = (int) (rawMap2 | bitpos);

      final int pIndex = TUPLE_LENGTH * index;

      int typedSlotArity = payloadArity * 2;

      long offset = arrayBase;
      long delta = 0;

      offset += rangecopyIntRegion(src, offset, dst, offset, pIndex);
      delta += setInIntRegionVarArgs(dst, offset, key, val);
      offset += rangecopyIntRegion(src, offset, dst, offset + delta, typedSlotArity - pIndex);
      offset += rangecopyObjectRegion(src, offset, dst, offset + delta, untypedSlotArity);

      return dst;
    }

    CompactMapNode copyAndInsertRareValue(final AtomicReference<Thread> mutator, final int bitpos,
        final int index, final Object key, final Object val) {
      final Class srcClass = this.getClass();

      final int payloadArity = unsafe.getInt(srcClass, globalPayloadArityOffset);
      final int untypedSlotArity = unsafe.getInt(srcClass, globalUntypedSlotArityOffset);

      final CompactMapNode src = this;
      final CompactMapNode dst = allocateHeapRegion(payloadArity, untypedSlotArity + TUPLE_LENGTH);

      dst.rawMap1 = (int) (rawMap1 | bitpos);
      dst.rawMap2 = (int) (rawMap2 | bitpos);

      final int pIndex = TUPLE_LENGTH * index;

      long offset = arrayBase;
      long delta = 0;

      offset += rangecopyIntRegion(src, offset, dst, offset, 2 * payloadArity);
      offset += rangecopyObjectRegion(src, offset, dst, offset, pIndex);
      delta += setInObjectRegionVarArgs(dst, offset, key, val);
      offset += rangecopyObjectRegion(src, offset, dst, offset + delta, untypedSlotArity - pIndex);

      return dst;
    }
    
//    CompactMapNode copyAndInsertRareValue(final AtomicReference<Thread> mutator, final int bitpos,
//        final int index, final Object key, final Object val) {
//
//      ArrayView src1 = getIntArrayView();
//      ArrayView src2 = getObjectArrayView();
//
//      final CompactMapNode dst = allocateHeapRegion(src1.length / 2, src2.length + TUPLE_LENGTH);
//
//      dst.rawMap1 = rawMap1 | bitpos;
//      dst.rawMap2 = rawMap2 | bitpos;
//
//      ArrayView dst1 = dst.getIntArrayView();
//      ArrayView dst2 = dst.getObjectArrayView();
//
//      arrayviewcopy(src1, 0, dst1, 0, src1.length);
//
//      final int idx2 = index * TUPLE_LENGTH;
//
//      arrayviewcopy(src2, 0, dst2, 0, idx2);
//      dst2.set(idx2 + 0, key);
//      dst2.set(idx2 + 1, val);
//      arrayviewcopy(src2, idx2, dst2, idx2 + TUPLE_LENGTH, src2.length - idx2);
//
//      return dst;
//    }
    
    CompactMapNode copyAndRemoveValue(final AtomicReference<Thread> mutator, final int bitpos) {
      final int valIdx = dataIndex(bitpos);

      final Class srcClass = this.getClass();

      final int payloadArity = unsafe.getInt(srcClass, globalPayloadArityOffset);
      final int untypedSlotArity = unsafe.getInt(srcClass, globalUntypedSlotArityOffset);

      final CompactMapNode src = this;
      final CompactMapNode dst = allocateHeapRegion(payloadArity - 1, untypedSlotArity);

      long srcOffset = arrayBase;
      long dstOffset = arrayBase;

      dst.rawMap1 = unsafe.getInt(src, globalRawMap1Offset);

      dst.rawMap2 = (int) (unsafe.getInt(src, globalRawMap2Offset) ^ bitpos);

      for (int i = 0; i < valIdx; i++) {
        unsafe.putInt(dst, dstOffset, unsafe.getInt(src, srcOffset));
        srcOffset += 4;
        dstOffset += 4;
        unsafe.putInt(dst, dstOffset, unsafe.getInt(src, srcOffset));
        srcOffset += 4;
        dstOffset += 4;
      }
      srcOffset += 4;
      srcOffset += 4;
      for (int i = valIdx + 1; i < payloadArity(); i++) {
        unsafe.putInt(dst, dstOffset, unsafe.getInt(src, srcOffset));
        srcOffset += 4;
        dstOffset += 4;
        unsafe.putInt(dst, dstOffset, unsafe.getInt(src, srcOffset));
        srcOffset += 4;
        dstOffset += 4;
      }
      for (int i = 0; i < rarePayloadArity(); i++) {
        unsafe.putObject(dst, dstOffset, unsafe.getObject(src, srcOffset));
        srcOffset += addressSize;
        dstOffset += addressSize;
        unsafe.putObject(dst, dstOffset, unsafe.getObject(src, srcOffset));
        srcOffset += addressSize;
        dstOffset += addressSize;
      }
      for (int i = nodeArity() - 1; i >= 0; i--) {
        unsafe.putObject(dst, dstOffset, unsafe.getObject(src, srcOffset));
        srcOffset += addressSize;
        dstOffset += addressSize;
      }

      return dst;
    }

    CompactMapNode copyAndRemoveRareValue(final AtomicReference<Thread> mutator, final int bitpos) {
      final int valIdx = rareIndex(bitpos);

      final Class srcClass = this.getClass();

      final int payloadArity = unsafe.getInt(srcClass, globalPayloadArityOffset);
      final int untypedSlotArity = unsafe.getInt(srcClass, globalUntypedSlotArityOffset);

      final CompactMapNode src = this;
      final CompactMapNode dst = allocateHeapRegion(payloadArity, untypedSlotArity - TUPLE_LENGTH);

      long srcOffset = arrayBase;
      long dstOffset = arrayBase;

      dst.rawMap1 = (int) (unsafe.getInt(src, globalRawMap1Offset) ^ bitpos);

      dst.rawMap2 = (int) (unsafe.getInt(src, globalRawMap2Offset) ^ bitpos);

      for (int i = 0; i < payloadArity(); i++) {
        unsafe.putInt(dst, dstOffset, unsafe.getInt(src, srcOffset));
        srcOffset += 4;
        dstOffset += 4;
        unsafe.putInt(dst, dstOffset, unsafe.getInt(src, srcOffset));
        srcOffset += 4;
        dstOffset += 4;
      }
      for (int i = 0; i < valIdx; i++) {
        unsafe.putObject(dst, dstOffset, unsafe.getObject(src, srcOffset));
        srcOffset += addressSize;
        dstOffset += addressSize;
        unsafe.putObject(dst, dstOffset, unsafe.getObject(src, srcOffset));
        srcOffset += addressSize;
        dstOffset += addressSize;
      }
      srcOffset += addressSize;
      srcOffset += addressSize;
      for (int i = valIdx + 1; i < rarePayloadArity(); i++) {
        unsafe.putObject(dst, dstOffset, unsafe.getObject(src, srcOffset));
        srcOffset += addressSize;
        dstOffset += addressSize;
        unsafe.putObject(dst, dstOffset, unsafe.getObject(src, srcOffset));
        srcOffset += addressSize;
        dstOffset += addressSize;
      }
      for (int i = nodeArity() - 1; i >= 0; i--) {
        unsafe.putObject(dst, dstOffset, unsafe.getObject(src, srcOffset));
        srcOffset += addressSize;
        dstOffset += addressSize;
      }

      return dst;
    }

    CompactMapNode copyAndSetNode(final AtomicReference<Thread> mutator, final int index,
        final AbstractMapNode node) {
      final Class srcClass = this.getClass();

      final int payloadArity = unsafe.getInt(srcClass, globalPayloadArityOffset);
      final int untypedSlotArity = unsafe.getInt(srcClass, globalUntypedSlotArityOffset);

      final CompactMapNode src = this;
      final CompactMapNode dst = allocateHeapRegion(srcClass);

      // copy and update bitmaps
      dst.rawMap1 = rawMap1;
      dst.rawMap2 = rawMap2;

      /*
       * rangecopyPrimitiveRegion(src, arrayBase, dst, arrayBase, primitiveRegionSize);
       * 
       * rangecopyObjectRegion(src, rareBase, 0, dst, rareBase, 0, untypedSlotArity);
       * setInObjectRegion(dst, rareBase, untypedSlotArity - 1 - index, node);
       */

      int pIndex = untypedSlotArity - 1 - index;

      long offset = arrayBase;
      offset += rangecopyIntRegion(src, offset, dst, offset, 2 * payloadArity);

      long rareBase = offset;
      offset += rangecopyObjectRegion(src, offset, dst, offset, untypedSlotArity);

      setInObjectRegion(dst, rareBase, pIndex, node);

      return dst;
    }
    
//    CompactMapNode copyAndSetNode(final AtomicReference<Thread> mutator, final int index,
//        final AbstractMapNode node) {
//      
//      ArrayView src1 = getIntArrayView();
//      ArrayView src2 = getObjectArrayView();
//
//      final CompactMapNode dst = allocateHeapRegion(getClass());
//
//      dst.rawMap1 = rawMap1;
//      dst.rawMap2 = rawMap2;
//
//      ArrayView dst1 = dst.getIntArrayView();
//      ArrayView dst2 = dst.getObjectArrayView();
//
//      arrayviewcopy(src1, 0, dst1, 0, src1.length);
//
//      final int idx2 = src2.length - 1 - index;
//
//      arrayviewcopy(src2, 0, dst2, 0, src2.length);
//      dst2.set(idx2, node);
//
//      return dst;      
//    }    

    CompactMapNode copyAndMigrateFromInlineToNode(final AtomicReference<Thread> mutator,
        final int bitpos, final int indexOld, final int indexNew, final AbstractMapNode node) {
      final Class srcClass = this.getClass();

      final int payloadArity = unsafe.getInt(srcClass, globalPayloadArityOffset);
      final int untypedSlotArity = unsafe.getInt(srcClass, globalUntypedSlotArityOffset);

      final CompactMapNode src = this;
      final CompactMapNode dst = allocateHeapRegion(payloadArity - 1, untypedSlotArity + 1);

      // idempotent operation; in case of rare bit was already set before
      dst.rawMap1 = (int) (rawMap1 | bitpos);
      dst.rawMap2 = (int) (rawMap2 ^ bitpos);

      final int pIndexOld = TUPLE_LENGTH * indexOld;
      final int pIndexNew = (untypedSlotArity + 1) - 1 - indexNew;

      long offset = arrayBase;
      offset += rangecopyIntRegion(src, offset, dst, offset, pIndexOld);
      long delta = 2 * 4 /* sizeOfInt() */;
      offset += rangecopyIntRegion(src, offset + delta, dst, offset,
          (TUPLE_LENGTH * (payloadArity - 1) - pIndexOld));

      offset += rangecopyObjectRegion(src, offset + delta, dst, offset, pIndexNew);
      long delta2 = setInObjectRegionVarArgs(dst, offset, node);
      delta -= delta2;
      offset += delta2;
      offset +=
          rangecopyObjectRegion(src, offset + delta, dst, offset, untypedSlotArity - pIndexNew);

      return dst;
    }

    CompactMapNode copyAndMigrateFromRareInlineToNode(final AtomicReference<Thread> mutator,
        final int bitpos, final int indexOld, final int indexNew, final AbstractMapNode node) {
      final Class srcClass = this.getClass();

      final int payloadArity = unsafe.getInt(srcClass, globalPayloadArityOffset);
      final int untypedSlotArity = unsafe.getInt(srcClass, globalUntypedSlotArityOffset);

      final CompactMapNode src = this;
      final CompactMapNode dst =
          allocateHeapRegion(payloadArity, untypedSlotArity - TUPLE_LENGTH + 1);

      // idempotent operation; in case of rare bit was already set before
      dst.rawMap1 = (int) (rawMap1 | bitpos);
      dst.rawMap2 = (int) (rawMap2 ^ bitpos);

      final int pIndexOld = TUPLE_LENGTH * indexOld;
      final int pIndexNew = (untypedSlotArity - TUPLE_LENGTH + 1) - 1 - indexNew;

      long offset = arrayBase;
      offset += rangecopyIntRegion(src, arrayBase, dst, arrayBase, 2 * payloadArity);

      offset += rangecopyObjectRegion(src, offset, dst, offset, pIndexOld);
      long delta = 2 * sizeOfObject();
      offset += rangecopyObjectRegion(src, offset + delta, dst, offset, pIndexNew - pIndexOld);
      long delta2 = setInObjectRegionVarArgs(dst, offset, node);
      delta -= delta2;
      offset += delta2;
      offset +=
          rangecopyObjectRegion(src, offset + delta, dst, offset, untypedSlotArity - pIndexNew - 2);

      return dst;
    }

//    // TODO: code ~25ms slower than code above
//    CompactMapNode copyAndMigrateFromRareInlineToNode(final AtomicReference<Thread> mutator,
//        final int bitpos, final int indexOld, final int indexNew, final AbstractMapNode node) {
//
//      ArrayView src1 = getIntArrayView();
//      ArrayView src2 = getObjectArrayView();
//
//      final CompactMapNode dst = allocateHeapRegion(src1.length / 2, src2.length - TUPLE_LENGTH + 1);
//
//      dst.rawMap1 = rawMap1 | bitpos;
//      dst.rawMap2 = rawMap2 ^ bitpos;
//
//      ArrayView dst1 = dst.getIntArrayView();
//      ArrayView dst2 = dst.getObjectArrayView();
//
//      arrayviewcopy(src1, 0, dst1, 0, src1.length);
//      
//      final int idxOld2 = indexOld * TUPLE_LENGTH;
//      final int idxNew2 = dst2.length - 1 - indexNew;
//      
//      arrayviewcopy(src2, 0, dst2, 0, idxOld2);
//      arrayviewcopy(src2, idxOld2 + TUPLE_LENGTH, dst2, idxOld2, idxNew2 - idxOld2);
//      dst2.set(idxNew2, node);
//      arrayviewcopy(src2, idxNew2 + TUPLE_LENGTH, dst2, idxNew2 + 1, src2.length - idxNew2 - TUPLE_LENGTH);
//
//      return dst;
//    }
    
//    // TODO: code ~XXms slower than code above
//    CompactMapNode copyAndMigrateFromRareInlineToNode(final AtomicReference<Thread> mutator,
//        final int bitpos, final int indexOld, final int indexNew, final AbstractMapNode node) {
//
//      ArrayView src1 = getIntArrayView();
//      ObjectArrayView src2 = getObjectArrayView();
//
//      final CompactMapNode dst =
//          allocateHeapRegion(src1.length / 2, src2.length - TUPLE_LENGTH + 1);
//
//      dst.rawMap1 = rawMap1 | bitpos;
//      dst.rawMap2 = rawMap2 ^ bitpos;
//
//      ArrayView dst1 = dst.getIntArrayView();
//      ObjectArrayView dst2 = dst.getObjectArrayView();
//
//      arrayviewcopy(src1, 0, dst1, 0, src1.length);
//
//      final int idxOld2 = indexOld * TUPLE_LENGTH;
//      final int idxNew2 = dst2.length - 1 - indexNew;
//
//      /***************/
//      
////      arrayviewcopy(src2, 0, dst2, 0, idxOld2);
////      arrayviewcopy(src2, idxOld2 + TUPLE_LENGTH, dst2, idxOld2, idxNew2 - idxOld2);
////      dst2.set(idxNew2, node);
////      arrayviewcopy(src2, idxNew2 + TUPLE_LENGTH, dst2, idxNew2 + 1,
////          src2.length - idxNew2 - TUPLE_LENGTH);
//
//      /***************/
//      
////      long offset = src1.offset;
////
////      offset += rangecopyObjectRegion(src2.base, offset, dst2.base, offset, idxOld2);
////      offset += rangecopyObjectRegion(src2.base, offset + sizeOfObject() * TUPLE_LENGTH, dst2.base, offset, idxNew2 - idxOld2);
////      offset += setInObjectRegionVarArgs(dst2.base, offset, node);
////      offset += rangecopyObjectRegion(src2.base, offset + sizeOfObject(), dst2.base, offset, src2.length - idxNew2 - TUPLE_LENGTH);
//
//      /***************/
//      
//      StreamingCopy sc = StreamingCopy.streamingCopyTwoOffsets(src2, dst2);
//      
//      sc.copy(idxOld2);
//      sc.skipAtSrc(TUPLE_LENGTH);
//      sc.copy(idxNew2 - idxOld2);
//      sc.insert(node);
//      sc.copy(src2.length - idxNew2 - TUPLE_LENGTH);
//      
////      sc.copy(idxOld2);
////      sc.copyWithSrcForward(idxNew2 - idxOld2, TUPLE_LENGTH);
////      sc.insert(node);
////      sc.copyWithSrcForward(src2.length - idxNew2 - TUPLE_LENGTH, TUPLE_LENGTH);
//      
////      StreamingCopy sc = StreamingCopy.streamingCopyOneOffset(src2, dst2);
////
////      sc.copy(idxOld2);
////      sc.copyWithSrcForward(idxNew2 - idxOld2, TUPLE_LENGTH);
////      sc.put(node);
////      sc.copyWithSrcDstForward(src2.length - idxNew2 - TUPLE_LENGTH, TUPLE_LENGTH, 1);
//      
//      return dst;
//    }
    
//    CompactMapNode copyAndMigrateFromRareInlineToNode(final AtomicReference<Thread> mutator,
//        final int bitpos, final int indexOld, final int indexNew, final AbstractMapNode node) {
//      final Class srcClass = this.getClass();
//
//      final int payloadArity = unsafe.getInt(srcClass, globalPayloadArityOffset);
//      final int untypedSlotArity = unsafe.getInt(srcClass, globalUntypedSlotArityOffset);
//
//      final CompactMapNode src = this;
//      final CompactMapNode dst =
//          allocateHeapRegion(payloadArity, untypedSlotArity - TUPLE_LENGTH + 1);
//
//      // idempotent operation; in case of rare bit was already set before
//      dst.rawMap1 = (int) (rawMap1 | bitpos);
//      dst.rawMap2 = (int) (rawMap2 ^ bitpos);
//
//      final int pIndexOld = TUPLE_LENGTH * indexOld;
//      final int pIndexNew = (untypedSlotArity - TUPLE_LENGTH + 1) - 1 - indexNew;
//
//      long offset = arrayBase;
//      offset += rangecopyIntRegion(src, arrayBase, dst, arrayBase, 2 * payloadArity);
//
//      offset += rangecopyObjectRegion(src, offset, dst, offset, pIndexOld);
//      long delta = 2 * sizeOfObject();
//      offset += rangecopyObjectRegion(src, offset + delta, dst, offset, pIndexNew - pIndexOld);
//      long delta2 = setInObjectRegionVarArgs(dst, offset, node);
//      delta -= delta2;
//      offset += delta2;
//      offset +=
//          rangecopyObjectRegion(src, offset + delta, dst, offset, untypedSlotArity - pIndexNew - 2);
//
//      return dst;
//    }    
    
    CompactMapNode copyAndMigrateFromNodeToInline(final AtomicReference<Thread> mutator,
        final int bitpos, final AbstractMapNode node) {
      final int idxOld = nodeIndex(bitpos);
      final int idxNew = dataIndex(bitpos);

      final Class srcClass = this.getClass();

      final int payloadArity = unsafe.getInt(srcClass, globalPayloadArityOffset);
      final int untypedSlotArity = unsafe.getInt(srcClass, globalUntypedSlotArityOffset);

      final CompactMapNode src = this;
      final CompactMapNode dst =
          allocateHeapRegion(payloadArity + 1, untypedSlotArity - TUPLE_LENGTH);

      long srcOffset = arrayBase;
      long dstOffset = arrayBase;

      // idempotent operation; in case of rare bit was already set before
      dst.rawMap1 = (int) (unsafe.getInt(src, globalRawMap1Offset) ^ bitpos);

      dst.rawMap2 = (int) (unsafe.getInt(src, globalRawMap2Offset) | bitpos);

      for (int i = 0; i < idxNew; i++) {
        unsafe.putInt(dst, dstOffset, unsafe.getInt(src, srcOffset));
        srcOffset += 4;
        dstOffset += 4;
        unsafe.putInt(dst, dstOffset, unsafe.getInt(src, srcOffset));
        srcOffset += 4;
        dstOffset += 4;
      }
      unsafe.putInt(dst, dstOffset, node.getKey(0));
      dstOffset += 4;
      unsafe.putInt(dst, dstOffset, node.getVal(0));
      dstOffset += 4;
      for (int i = idxNew; i < payloadArity(); i++) {
        unsafe.putInt(dst, dstOffset, unsafe.getInt(src, srcOffset));
        srcOffset += 4;
        dstOffset += 4;
        unsafe.putInt(dst, dstOffset, unsafe.getInt(src, srcOffset));
        srcOffset += 4;
        dstOffset += 4;
      }
      for (int i = 0; i < rarePayloadArity(); i++) {
        unsafe.putObject(dst, dstOffset, unsafe.getObject(src, srcOffset));
        srcOffset += addressSize;
        dstOffset += addressSize;
        unsafe.putObject(dst, dstOffset, unsafe.getObject(src, srcOffset));
        srcOffset += addressSize;
        dstOffset += addressSize;
      }
      for (int i = nodeArity() - 1; i >= idxOld + 1; i--) {
        unsafe.putObject(dst, dstOffset, unsafe.getObject(src, srcOffset));
        srcOffset += addressSize;
        dstOffset += addressSize;
      }
      srcOffset += addressSize;
      for (int i = idxOld - 1; i >= 0; i--) {
        unsafe.putObject(dst, dstOffset, unsafe.getObject(src, srcOffset));
        srcOffset += addressSize;
        dstOffset += addressSize;
      }

      return dst;
    }

    CompactMapNode copyAndMigrateFromNodeToRareInline(final AtomicReference<Thread> mutator,
        final int bitpos, final AbstractMapNode node) {
      final int idxOld = nodeIndex(bitpos);
      final int idxNew = rareIndex(bitpos);

      final Class srcClass = this.getClass();

      final int payloadArity = unsafe.getInt(srcClass, globalPayloadArityOffset);
      final int untypedSlotArity = unsafe.getInt(srcClass, globalUntypedSlotArityOffset);

      final CompactMapNode src = this;
      final CompactMapNode dst =
          allocateHeapRegion(payloadArity, untypedSlotArity + TUPLE_LENGTH - 1);

      long srcOffset = arrayBase;
      long dstOffset = arrayBase;

      // idempotent operation; in case of rare bit was already set before
      dst.rawMap1 = (int) (unsafe.getInt(src, globalRawMap1Offset) | bitpos);

      dst.rawMap2 = (int) (unsafe.getInt(src, globalRawMap2Offset) | bitpos);

      for (int i = 0; i < payloadArity(); i++) {
        unsafe.putInt(dst, dstOffset, unsafe.getInt(src, srcOffset));
        srcOffset += 4;
        dstOffset += 4;
        unsafe.putInt(dst, dstOffset, unsafe.getInt(src, srcOffset));
        srcOffset += 4;
        dstOffset += 4;
      }
      for (int i = 0; i < idxNew; i++) {
        unsafe.putObject(dst, dstOffset, unsafe.getObject(src, srcOffset));
        srcOffset += addressSize;
        dstOffset += addressSize;
        unsafe.putObject(dst, dstOffset, unsafe.getObject(src, srcOffset));
        srcOffset += addressSize;
        dstOffset += addressSize;
      }
      unsafe.putObject(dst, dstOffset, node.getRareKey(0));
      dstOffset += addressSize;
      unsafe.putObject(dst, dstOffset, node.getRareVal(0));
      dstOffset += addressSize;
      for (int i = idxNew; i < rarePayloadArity(); i++) {
        unsafe.putObject(dst, dstOffset, unsafe.getObject(src, srcOffset));
        srcOffset += addressSize;
        dstOffset += addressSize;
        unsafe.putObject(dst, dstOffset, unsafe.getObject(src, srcOffset));
        srcOffset += addressSize;
        dstOffset += addressSize;
      }
      for (int i = nodeArity() - 1; i >= idxOld + 1; i--) {
        unsafe.putObject(dst, dstOffset, unsafe.getObject(src, srcOffset));
        srcOffset += addressSize;
        dstOffset += addressSize;
      }
      srcOffset += addressSize;
      for (int i = idxOld - 1; i >= 0; i--) {
        unsafe.putObject(dst, dstOffset, unsafe.getObject(src, srcOffset));
        srcOffset += addressSize;
        dstOffset += addressSize;
      }

      return dst;
    }

    static final AbstractMapNode mergeTwoKeyValPairs(final Object key0, final Object val0,
        final int keyHash0, final Object key1, final Object val1, final int keyHash1,
        final int shift) {
      // assert !(key0 == key1);

      if (shift >= hashCodeLength()) {
        return new HashCollisionMapNode_5Bits_Heterogeneous_BleedingEdge(keyHash0,
            (Object[]) new Object[] {key0, key1}, (Object[]) new Object[] {val0, val1});
      }

      final int mask0 = mask(keyHash0, shift);
      final int mask1 = mask(keyHash1, shift);

      if (mask0 != mask1) {
        // both nodes fit on same level
        final int nodeMap = (int) (bitpos(mask0) | bitpos(mask1));
        final int dataMap = (int) (bitpos(mask0) | bitpos(mask1));

        if (mask0 < mask1) {
          return nodeOf4x0(null, (int) nodeMap, dataMap, key0, val0, key1, val1);
        } else {
          return nodeOf4x0(null, (int) nodeMap, dataMap, key1, val1, key0, val0);
        }
      } else {
        final AbstractMapNode node = mergeTwoKeyValPairs(key0, val0, keyHash0, key1, val1, keyHash1,
            shift + bitPartitionSize());
        // values fit on next level

        final int nodeMap = bitpos(mask0);
        return nodeOf1x0(null, nodeMap, (int) 0, node);
      }
    }

    static final AbstractMapNode mergeTwoKeyValPairs(final Object key0, final Object val0,
        final int keyHash0, final int key1, final int val1, final int keyHash1, final int shift) {
      // assert !(key0 == key1);

      if (shift >= hashCodeLength()) {
        return new HashCollisionMapNode_5Bits_Heterogeneous_BleedingEdge(keyHash0,
            (Object[]) new Object[] {key0, key1}, (Object[]) new Object[] {val0, val1});
      }

      final int mask0 = mask(keyHash0, shift);
      final int mask1 = mask(keyHash1, shift);

      if (mask0 != mask1) {
        // both nodes fit on same level
        final int nodeMap = (int) (bitpos(mask0));
        final int dataMap = (int) (bitpos(mask0) | bitpos(mask1));

        // convention: rare after base
        return nodeOf2x1(null, (int) nodeMap, dataMap, key1, val1, key0, val0);
      } else {
        final AbstractMapNode node = mergeTwoKeyValPairs(key0, val0, keyHash0, key1, val1, keyHash1,
            shift + bitPartitionSize());
        // values fit on next level

        final int nodeMap = bitpos(mask0);
        return nodeOf1x0(null, nodeMap, (int) 0, node);
      }
    }

    static final AbstractMapNode mergeTwoKeyValPairs(final int key0, final int val0,
        final int keyHash0, final Object key1, final Object val1, final int keyHash1,
        final int shift) {
      // assert !(key0 == key1);

      if (shift >= hashCodeLength()) {
        return new HashCollisionMapNode_5Bits_Heterogeneous_BleedingEdge(keyHash0,
            (Object[]) new Object[] {key0, key1}, (Object[]) new Object[] {val0, val1});
      }

      final int mask0 = mask(keyHash0, shift);
      final int mask1 = mask(keyHash1, shift);

      if (mask0 != mask1) {
        // both nodes fit on same level
        final int nodeMap = (int) (bitpos(mask1));
        final int dataMap = (int) (bitpos(mask0) | bitpos(mask1));

        // convention: rare after base
        return nodeOf2x1(null, (int) nodeMap, dataMap, key0, val0, key1, val1);
      } else {
        final AbstractMapNode node = mergeTwoKeyValPairs(key0, val0, keyHash0, key1, val1, keyHash1,
            shift + bitPartitionSize());
        // values fit on next level

        final int nodeMap = bitpos(mask0);
        return nodeOf1x0(null, nodeMap, (int) 0, node);
      }
    }

    static final AbstractMapNode mergeTwoKeyValPairs(final int key0, final int val0,
        final int keyHash0, final int key1, final int val1, final int keyHash1, final int shift) {
      // assert !(key0 == key1);

      if (shift >= hashCodeLength()) {
        return new HashCollisionMapNode_5Bits_Heterogeneous_BleedingEdge(keyHash0,
            (Object[]) new Object[] {key0, key1}, (Object[]) new Object[] {val0, val1});
      }

      final int mask0 = mask(keyHash0, shift);
      final int mask1 = mask(keyHash1, shift);

      if (mask0 != mask1) {
        // both nodes fit on same level
        final int nodeMap = 0;
        final int dataMap = (int) (bitpos(mask0) | bitpos(mask1));

        if (mask0 < mask1) {
          return nodeOf0x2(null, (int) nodeMap, dataMap, key0, val0, key1, val1);
        } else {
          return nodeOf0x2(null, (int) nodeMap, dataMap, key1, val1, key0, val0);
        }
      } else {
        final AbstractMapNode node = mergeTwoKeyValPairs(key0, val0, keyHash0, key1, val1, keyHash1,
            shift + bitPartitionSize());
        // values fit on next level

        final int nodeMap = bitpos(mask0);
        return nodeOf1x0(null, nodeMap, (int) 0, node);
      }
    }

    static final int index(final int bitmap, final int bitpos) {
      return java.lang.Integer.bitCount(bitmap & (bitpos - 1));
    }

    static final int index(final int bitmap, final int mask, final int bitpos) {
      return (bitmap == -1) ? mask : index(bitmap, bitpos);
    }

    int dataIndex(final int bitpos) {
      return java.lang.Integer.bitCount(dataMap() & (bitpos - 1));
    }

    int nodeIndex(final int bitpos) {
      return java.lang.Integer.bitCount(nodeMap() & (bitpos - 1));
    }

    int rareIndex(final int bitpos) {
      return java.lang.Integer.bitCount(rareMap() & (bitpos - 1));
    }

    AbstractMapNode nodeAt(final int bitpos) {
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

      // compare rawMap1
      if (!(unsafe.getInt(src, globalRawMap1Offset) == unsafe.getInt(dst, globalRawMap1Offset))) {
        return false;
      }

      // compare rawMap2
      if (!(unsafe.getInt(src, globalRawMap2Offset) == unsafe.getInt(dst, globalRawMap2Offset))) {
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
      assert 1 <= i_th && i_th <= 32;

      byte cnt1 = 0;
      byte mask = 0;

      while (mask < 32) {
        if ((map & 0x01) == 0x01) {
          cnt1 += 1;

          if (cnt1 == i_th) {
            return mask;
          }
        }

        map = (int) (map >> 1);
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

    static final AbstractMapNode nodeOf1x0(final AtomicReference<Thread> mutator, final int nodeMap,
        final int dataMap, final Object slot0) {
      return new Map0To1Node_5Bits_Heterogeneous_BleedingEdge(mutator, nodeMap, dataMap, slot0);
    }

    static final AbstractMapNode nodeOf0x1(final AtomicReference<Thread> mutator, final int nodeMap,
        final int dataMap, final int key1, final int val1) {
      return new Map1To0Node_5Bits_Heterogeneous_BleedingEdge(mutator, nodeMap, dataMap, key1,
          val1);
    }

    static final AbstractMapNode nodeOf0x2(final AtomicReference<Thread> mutator, final int nodeMap,
        final int dataMap, final int key1, final int val1, final int key2, final int val2) {
      return new Map2To0Node_5Bits_Heterogeneous_BleedingEdge(mutator, nodeMap, dataMap, key1, val1,
          key2, val2);
    }

    static final AbstractMapNode nodeOf4x0(final AtomicReference<Thread> mutator, final int nodeMap,
        final int dataMap, final Object slot0, final Object slot1, final Object slot2,
        final Object slot3) {
      return new Map0To4Node_5Bits_Heterogeneous_BleedingEdge(mutator, nodeMap, dataMap, slot0,
          slot1, slot2, slot3);
    }

    static final AbstractMapNode nodeOf2x0(final AtomicReference<Thread> mutator, final int nodeMap,
        final int dataMap, final Object slot0, final Object slot1) {
      return new Map0To2Node_5Bits_Heterogeneous_BleedingEdge(mutator, nodeMap, dataMap, slot0,
          slot1);
    }

    static final AbstractMapNode nodeOf2x1(final AtomicReference<Thread> mutator, final int nodeMap,
        final int dataMap, final int key1, final int val1, final Object slot0, final Object slot1) {
      return new Map1To2Node_5Bits_Heterogeneous_BleedingEdge(mutator, nodeMap, dataMap, key1, val1,
          slot0, slot1);
    }

    @Override
    boolean containsKeyEquivalent(final int key, final int keyHash, final int shift,
        final Comparator<Object> cmp) {
      CompactMapNode instance = this;
      Class<? extends CompactMapNode> clazz = instance.getClass();

      for (int shift0 = shift; true; shift0 += bitPartitionSize()) {
        final int mask = mask(keyHash, shift0);
        final int bitpos = bitpos(mask);

        final int nodeMap = instance.nodeMap();
        // final int nodeMap = unsafe.getInt(instance, globalRawMap1Offset);
        if (isBitInBitmap(nodeMap, bitpos)) {
          final int index = index(nodeMap, mask, bitpos);
          final AbstractMapNode nestedInstance = getNode(clazz, instance, index);

          try {
            instance = (CompactMapNode) nestedInstance;
            clazz = instance.getClass();
          } catch (ClassCastException unused) {
            return ((HashCollisionMapNode_5Bits_Heterogeneous_BleedingEdge) nestedInstance)
                .containsKey(key, keyHash, 0);
          }
        } else {
          final int dataMap = instance.dataMap();
          // final int dataMap = unsafe.getInt(instance, globalRawMap2Offset);
          if (isBitInBitmap(dataMap, bitpos)) {
            final int index = index(dataMap, mask, bitpos);
            return getKey(clazz, instance, index) == key;
          } else {
            return false;
          }
        }
      }
    }

    @Override
    AbstractMapNode updated(final AtomicReference<Thread> mutator, final int key, final int val,
        final int keyHash, final int shift, final MapResult details, final Comparator<Object> cmp) {
      final int mask = mask(keyHash, shift);
      final int bitpos = bitpos(mask);

      int rawMap1 = this.rawMap1;
      int rawMap2 = this.rawMap2;

      final int rareMap = (int) (rawMap1 & rawMap2);
      final int dataMap = (int) (rawMap2 ^ rareMap);
      final int nodeMap = (int) (rawMap1 ^ rareMap);

      final int nodeIndex = index(nodeMap, mask, bitpos);

      // check for node (not value)
      if (isBitInBitmap(nodeMap, bitpos)) {
        final AbstractMapNode subNode = getNode(nodeIndex);
        final AbstractMapNode subNodeNew =
            subNode.updated(mutator, key, val, keyHash, shift + bitPartitionSize(), details, cmp);

        if (details.isModified()) {
          return copyAndSetNode(mutator, nodeIndex, subNodeNew);
        } else {
          return this;
        }
      } else
        // check for inplace (rare) value
        if (isBitInBitmap(rareMap, bitpos)) {
        final int rareIndex = index(rareMap, mask, bitpos);
        final Object currentKey = getRareKey(rareIndex);

        final Object currentVal = getRareVal(rareIndex);

        final AbstractMapNode subNodeNew =
            mergeTwoKeyValPairs(currentKey, currentVal, transformHashCode(currentKey.hashCode()),
                key, val, keyHash, shift + bitPartitionSize());

        details.modified();
        return copyAndMigrateFromRareInlineToNode(mutator, bitpos, rareIndex, nodeIndex,
            subNodeNew);

      } else
          // check for inplace value
          if (isBitInBitmap(dataMap, bitpos)) {
        final int dataIndex = index(dataMap, mask, bitpos);
        final int currentKey = getKey(dataIndex);

        if (currentKey == key) {
          final int currentVal = getVal(dataIndex);

          // update mapping
          details.updated(EitherIntOrObject.ofInt(currentVal));
          return copyAndSetValue(mutator, bitpos, dataIndex, val);
        } else {
          final int currentVal = getVal(dataIndex);

          final AbstractMapNode subNodeNew = mergeTwoKeyValPairs(currentKey, currentVal,
              transformHashCode(currentKey), key, val, keyHash, shift + bitPartitionSize());

          details.modified();
          return copyAndMigrateFromInlineToNode(mutator, bitpos, dataIndex, nodeIndex, subNodeNew);
        }

      } else {
        // no value
        details.modified();
        int dataIndex = index(dataMap, mask, bitpos);
        return copyAndInsertValue(mutator, bitpos, dataIndex, key, val);
      }
    }

    @Override
    AbstractMapNode updated(final AtomicReference<Thread> mutator, final int key, final int val,
        final int keyHash, final int shift, final MapResult details) {
      final int mask = mask(keyHash, shift);
      final int bitpos = bitpos(mask);

      int rawMap1 = this.rawMap1;
      int rawMap2 = this.rawMap2;

      final int rareMap = (int) (rawMap1 & rawMap2);
      final int dataMap = (int) (rawMap2 ^ rareMap);
      final int nodeMap = (int) (rawMap1 ^ rareMap);

      final int nodeIndex = index(nodeMap, mask, bitpos);

      // check for node (not value)
      if (isBitInBitmap(nodeMap, bitpos)) {
        final AbstractMapNode subNode = getNode(nodeIndex);
        final AbstractMapNode subNodeNew =
            subNode.updated(mutator, key, val, keyHash, shift + bitPartitionSize(), details);

        if (details.isModified()) {
          return copyAndSetNode(mutator, nodeIndex, subNodeNew);
        } else {
          return this;
        }
      } else
        // check for inplace (rare) value
        if (isBitInBitmap(rareMap, bitpos)) {
        final int rareIndex = index(rareMap, mask, bitpos);
        final Object currentKey = getRareKey(rareIndex);

        final Object currentVal = getRareVal(rareIndex);

        final AbstractMapNode subNodeNew =
            mergeTwoKeyValPairs(currentKey, currentVal, transformHashCode(currentKey.hashCode()),
                key, val, keyHash, shift + bitPartitionSize());

        details.modified();
        return copyAndMigrateFromRareInlineToNode(mutator, bitpos, rareIndex, nodeIndex,
            subNodeNew);

      } else
          // check for inplace value
          if (isBitInBitmap(dataMap, bitpos)) {
        final int dataIndex = index(dataMap, mask, bitpos);
        final int currentKey = getKey(dataIndex);

        if (currentKey == key) {
          final int currentVal = getVal(dataIndex);

          // update mapping
          details.updated(EitherIntOrObject.ofInt(currentVal));
          return copyAndSetValue(mutator, bitpos, dataIndex, val);
        } else {
          final int currentVal = getVal(dataIndex);

          final AbstractMapNode subNodeNew = mergeTwoKeyValPairs(currentKey, currentVal,
              transformHashCode(currentKey), key, val, keyHash, shift + bitPartitionSize());

          details.modified();
          return copyAndMigrateFromInlineToNode(mutator, bitpos, dataIndex, nodeIndex, subNodeNew);
        }

      } else {
        // no value
        details.modified();
        int dataIndex = index(dataMap, mask, bitpos);
        return copyAndInsertValue(mutator, bitpos, dataIndex, key, val);
      }
    }

    @Override
    Map.Entry<Object, Object> getKeyValueEntry(final int index) {
      return entryOf(getKey(index), getVal(index));
    }

    @Override
    AbstractMapNode updated(final AtomicReference<Thread> mutator, final Object key,
        final Object val, final int keyHash, final int shift, final MapResult details,
        final Comparator<Object> cmp) {
      final int mask = mask(keyHash, shift);
      final int bitpos = bitpos(mask);

      int rawMap1 = this.rawMap1;
      int rawMap2 = this.rawMap2;

      final int rareMap = (int) (rawMap1 & rawMap2);
      final int dataMap = (int) (rawMap2 ^ rareMap);
      final int nodeMap = (int) (rawMap1 ^ rareMap);

      final int nodeIndex = index(nodeMap, mask, bitpos);

      // check for node (not value)
      if (isBitInBitmap(nodeMap, bitpos)) {
        final AbstractMapNode subNode = getNode(nodeIndex);
        final AbstractMapNode subNodeNew =
            subNode.updated(mutator, key, val, keyHash, shift + bitPartitionSize(), details, cmp);

        if (details.isModified()) {
          return copyAndSetNode(mutator, nodeIndex, subNodeNew);
        } else {
          return this;
        }
      } else
        // check for inplace (rare) value
        if (isBitInBitmap(rareMap, bitpos)) {
        final int rareIndex = index(rareMap, mask, bitpos);
        final Object currentKey = getRareKey(rareIndex);

        if (cmp.compare(currentKey, key) == 0) {
          final Object currentVal = getRareVal(rareIndex);

          // update mapping
          details.updated(EitherIntOrObject.ofObject(currentVal));
          return copyAndSetRareValue(mutator, bitpos, rareIndex, val);
        } else {
          final Object currentVal = getRareVal(rareIndex);

          final AbstractMapNode subNodeNew =
              mergeTwoKeyValPairs(currentKey, currentVal, transformHashCode(currentKey.hashCode()),
                  key, val, keyHash, shift + bitPartitionSize());

          details.modified();
          return copyAndMigrateFromRareInlineToNode(mutator, bitpos, rareIndex, nodeIndex,
              subNodeNew);
        }

      } else
          // check for inplace value
          if (isBitInBitmap(dataMap, bitpos)) {
        final int dataIndex = index(dataMap, mask, bitpos);
        final int currentKey = getKey(dataIndex);

        final int currentVal = getVal(dataIndex);

        final AbstractMapNode subNodeNew = mergeTwoKeyValPairs(currentKey, currentVal,
            transformHashCode(currentKey), key, val, keyHash, shift + bitPartitionSize());

        details.modified();
        return copyAndMigrateFromInlineToNode(mutator, bitpos, dataIndex, nodeIndex, subNodeNew);

      } else {
        // no value
        details.modified();
        int rareIndex = index(rareMap, mask, bitpos);
        return copyAndInsertRareValue(mutator, bitpos, rareIndex, key, val);
      }
    }

    @Override
    AbstractMapNode updated(final AtomicReference<Thread> mutator, final Object key,
        final Object val, final int keyHash, final int shift, final MapResult details) {
      final int mask = mask(keyHash, shift);
      final int bitpos = bitpos(mask);

      int rawMap1 = this.rawMap1;
      int rawMap2 = this.rawMap2;

      final int rareMap = (int) (rawMap1 & rawMap2);
      final int dataMap = (int) (rawMap2 ^ rareMap);
      final int nodeMap = (int) (rawMap1 ^ rareMap);

      final int nodeIndex = index(nodeMap, mask, bitpos);

      // check for node (not value)
      if (isBitInBitmap(nodeMap, bitpos)) {
        final AbstractMapNode subNode = getNode(nodeIndex);
        final AbstractMapNode subNodeNew =
            subNode.updated(mutator, key, val, keyHash, shift + bitPartitionSize(), details);

        if (details.isModified()) {
          return copyAndSetNode(mutator, nodeIndex, subNodeNew);
        } else {
          return this;
        }
      } else
        // check for inplace (rare) value
        if (isBitInBitmap(rareMap, bitpos)) {
        final int rareIndex = index(rareMap, mask, bitpos);
        final Object currentKey = getRareKey(rareIndex);

        if (currentKey.equals(key)) {
          final Object currentVal = getRareVal(rareIndex);

          // update mapping
          details.updated(EitherIntOrObject.ofObject(currentVal));
          return copyAndSetRareValue(mutator, bitpos, rareIndex, val);
        } else {
          final Object currentVal = getRareVal(rareIndex);

          final AbstractMapNode subNodeNew =
              mergeTwoKeyValPairs(currentKey, currentVal, transformHashCode(currentKey.hashCode()),
                  key, val, keyHash, shift + bitPartitionSize());

          details.modified();
          return copyAndMigrateFromRareInlineToNode(mutator, bitpos, rareIndex, nodeIndex,
              subNodeNew);
        }

      } else
          // check for inplace value
          if (isBitInBitmap(dataMap, bitpos)) {
        final int dataIndex = index(dataMap, mask, bitpos);
        final int currentKey = getKey(dataIndex);

        final int currentVal = getVal(dataIndex);

        final AbstractMapNode subNodeNew = mergeTwoKeyValPairs(currentKey, currentVal,
            transformHashCode(currentKey), key, val, keyHash, shift + bitPartitionSize());

        details.modified();
        return copyAndMigrateFromInlineToNode(mutator, bitpos, dataIndex, nodeIndex, subNodeNew);

      } else {
        // no value
        details.modified();
        int rareIndex = index(rareMap, mask, bitpos);
        return copyAndInsertRareValue(mutator, bitpos, rareIndex, key, val);
      }
    }

    @Override
    Optional<Object> findByKey(final int key, final int keyHash, final int shift,
        final Comparator<Object> cmp) {
      final int mask = mask(keyHash, shift);
      final int bitpos = bitpos(mask);

      final int dataMap = this.dataMap();
      if (isBitInBitmap(dataMap, bitpos)) { // inplace value
        final int index = index(dataMap, mask, bitpos);

        if (getKey(index) == key) {
          final Object result = getVal(index);

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
    int getKey(final int index) {
      return getKey(this.getClass(), this, index);
    }

    @Override
    Object getRareKey(final int index) {
      return getRareKey(this.getClass(), this, index);
    }

    @Override
    boolean containsKeyEquivalent(final Object key, final int keyHash, final int shift,
        final Comparator<Object> cmp) {
      CompactMapNode instance = this;
      Class<? extends CompactMapNode> clazz = instance.getClass();

      for (int shift0 = shift; true; shift0 += bitPartitionSize()) {
        final int mask = mask(keyHash, shift0);
        final int bitpos = bitpos(mask);

        final int nodeMap = instance.nodeMap();
        // final int nodeMap = unsafe.getInt(instance, globalRawMap1Offset);
        if (isBitInBitmap(nodeMap, bitpos)) {
          final int index = index(nodeMap, mask, bitpos);
          final AbstractMapNode nestedInstance = getNode(clazz, instance, index);

          try {
            instance = (CompactMapNode) nestedInstance;
            clazz = instance.getClass();
          } catch (ClassCastException unused) {
            return ((HashCollisionMapNode_5Bits_Heterogeneous_BleedingEdge) nestedInstance)
                .containsKey(key, keyHash, 0);
          }
        } else {
          final int rareMap = instance.rareMap();
          // final int rareMap = unsafe.getInt(instance, globalRawMap2Offset);
          if (isBitInBitmap(rareMap, bitpos)) {
            final int index = index(rareMap, mask, bitpos);
            return cmp.compare(getRareKey(clazz, instance, index), key) == 0;
          } else {
            return false;
          }
        }
      }
    }

    @Override
    boolean containsKey(final int key, final int keyHash, final int shift) {
      CompactMapNode instance = this;
      Class<? extends CompactMapNode> clazz = instance.getClass();

      for (int shift0 = shift; true; shift0 += bitPartitionSize()) {
        final int mask = mask(keyHash, shift0);
        final int bitpos = bitpos(mask);

        final int nodeMap = instance.nodeMap();
        // final int nodeMap = unsafe.getInt(instance, globalRawMap1Offset);
        if (isBitInBitmap(nodeMap, bitpos)) {
          final int index = index(nodeMap, mask, bitpos);
          final AbstractMapNode nestedInstance = getNode(clazz, instance, index);

          try {
            instance = (CompactMapNode) nestedInstance;
            clazz = instance.getClass();
          } catch (ClassCastException unused) {
            return ((HashCollisionMapNode_5Bits_Heterogeneous_BleedingEdge) nestedInstance)
                .containsKey(key, keyHash, 0);
          }
        } else {
          final int dataMap = instance.dataMap();
          // final int dataMap = unsafe.getInt(instance, globalRawMap2Offset);
          if (isBitInBitmap(dataMap, bitpos)) {
            final int index = index(dataMap, mask, bitpos);
            return getKey(clazz, instance, index) == key;
          } else {
            return false;
          }
        }
      }
    }

    @Override
    int untypedSlotArity() {
      return unsafe.getInt(this.getClass(), globalUntypedSlotArityOffset);
    }

    @Override
    Optional<Object> findByKey(final Object key, final int keyHash, final int shift,
        final Comparator<Object> cmp) {
      final int mask = mask(keyHash, shift);
      final int bitpos = bitpos(mask);

      final int rareMap = this.rareMap();
      if (isBitInBitmap(rareMap, bitpos)) { // inplace value
        final int index = index(rareMap, mask, bitpos);

        if (cmp.compare(getRareKey(index), key) == 0) {
          final Object result = getRareVal(index);

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
    Optional<Object> findByKey(final int key, final int keyHash, final int shift) {
      final int mask = mask(keyHash, shift);
      final int bitpos = bitpos(mask);

      final int dataMap = this.dataMap();
      if (isBitInBitmap(dataMap, bitpos)) { // inplace value
        final int index = index(dataMap, mask, bitpos);

        if (getKey(index) == key) {
          final Object result = getVal(index);

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
    boolean containsKey(final Object key, final int keyHash, final int shift) {
      CompactMapNode instance = this;
      Class<? extends CompactMapNode> clazz = instance.getClass();

      for (int shift0 = shift; true; shift0 += bitPartitionSize()) {
        final int mask = mask(keyHash, shift0);
        final int bitpos = bitpos(mask);

        final int nodeMap = instance.nodeMap();
        // final int nodeMap = unsafe.getInt(instance, globalRawMap1Offset);
        if (isBitInBitmap(nodeMap, bitpos)) {
          final int index = index(nodeMap, mask, bitpos);
          final AbstractMapNode nestedInstance = getNode(clazz, instance, index);

          try {
            instance = (CompactMapNode) nestedInstance;
            clazz = instance.getClass();
          } catch (ClassCastException unused) {
            return ((HashCollisionMapNode_5Bits_Heterogeneous_BleedingEdge) nestedInstance)
                .containsKey(key, keyHash, 0);
          }
        } else {
          final int rareMap = instance.rareMap();
          // final int rareMap = unsafe.getInt(instance, globalRawMap2Offset);
          if (isBitInBitmap(rareMap, bitpos)) {
            final int index = index(rareMap, mask, bitpos);
            return getRareKey(clazz, instance, index).equals(key);
          } else {
            return false;
          }
        }
      }
    }

    @Override
    Object getSlot(final int index) {
      try {
        final long[] arrayOffsets =
            (long[]) unsafe.getObject(this.getClass(), globalArrayOffsetsOffset);
        return (Object) unsafe.getObject(this,
            arrayOffsets[logicalToPhysicalIndex(ContentType.SLOT, index)]);
      } catch (SecurityException e) {
        throw new RuntimeException(e);
      }
    }

    @Override
    int payloadArity() {
      return unsafe.getInt(this.getClass(), globalPayloadArityOffset);
    }

    @Override
    boolean hasPayload() {
      return payloadArity() != 0;
    }

    @Override
    public AbstractMapNode removed(final AtomicReference<Thread> mutator, final int key,
        final int keyHash, final int shift, final MapResult details, final Comparator<Object> cmp) {
      final int mask = mask(keyHash, shift);
      final int bitpos = bitpos(mask);

      // TODO: generalize bitmap declaration/if/index assignment in code generator pattern
      // check for inplace value
      final int dataMap = dataMap();
      if (isBitInBitmap(dataMap, bitpos)) {
        final int dataIndex = index(dataMap, mask, bitpos);
        final int currentKey = getKey(dataIndex);

        if (currentKey == key) {
          final int currentVal = getVal(dataIndex);
          details.updated(EitherIntOrObject.ofInt(currentVal));

          /*
           * Case payload == 2: Create new node with remaining pair. The new node will a) either
           * become the new root returned, or b) unwrapped and inlined during returning.
           */
          if (this.payloadArity() == 2 && this.rarePayloadArity() == 0 && this.nodeArity() == 0) {
            final int newRawMap =
                (shift == 0) ? (int) (rawMap2() ^ bitpos) : bitpos(mask(keyHash, 0));

            return nodeOf0x1(mutator, newRawMap, newRawMap, getKey(1 - dataIndex),
                getVal(1 - dataIndex));
          } else
            if (this.payloadArity() == 1 && this.rarePayloadArity() == 1 && this.nodeArity() == 0) {
            final int newRawMap =
                (shift == 0) ? (int) (rawMap2() ^ bitpos) : bitpos(mask(keyHash, 0));

            return nodeOf2x0(mutator, (int) (0), newRawMap, getRareKey(0), getRareVal(0));
          } else {
            return copyAndRemoveValue(mutator, bitpos);
          }
        } else {
          return this;
        }
      }

      // check for node (not value)
      final int nodeMap = nodeMap();
      if (isBitInBitmap(nodeMap, bitpos)) {
        final int nodeIndex = index(nodeMap, mask, bitpos);
        final AbstractMapNode subNode = getNode(nodeIndex);
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
            if (payloadArity() == 1) {
              return copyAndMigrateFromNodeToInline(mutator, bitpos, subNodeNew);
            } else {
              return copyAndMigrateFromNodeToRareInline(mutator, bitpos, subNodeNew);
            }

          }
          default: {
            // modify current node (set replacement node)
            return copyAndSetNode(mutator, nodeIndex, subNodeNew);
          }
        }
      }

      // no value
      return this;
    }

    @Override
    Optional<Object> findByKey(final Object key, final int keyHash, final int shift) {
      final int mask = mask(keyHash, shift);
      final int bitpos = bitpos(mask);

      final int rareMap = this.rareMap();
      if (isBitInBitmap(rareMap, bitpos)) { // inplace value
        final int index = index(rareMap, mask, bitpos);

        if (getRareKey(index).equals(key)) {
          final Object result = getRareVal(index);

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
    Object getRareVal(final int index) {
      return getRareVal(this.getClass(), this, index);
    }

    @Override
    int rarePayloadArity() {
      return Integer.bitCount(rareMap());
    }

    @Override
    public AbstractMapNode removed(final AtomicReference<Thread> mutator, final Object key,
        final int keyHash, final int shift, final MapResult details, final Comparator<Object> cmp) {
      final int mask = mask(keyHash, shift);
      final int bitpos = bitpos(mask);

      // TODO: generalize bitmap declaration/if/index assignment in code generator pattern
      // check for inplace (rare) value
      final int rareMap = rareMap();
      if (isBitInBitmap(rareMap, bitpos)) {
        final int rareIndex = index(rareMap, mask, bitpos);
        final Object currentKey = getRareKey(rareIndex);

        if (cmp.compare(currentKey, key) == 0) {
          final Object currentVal = getRareVal(rareIndex);
          details.updated(EitherIntOrObject.ofObject(currentVal));

          /*
           * Case payload == 2: Create new node with remaining pair. The new node will a) either
           * become the new root returned, or b) unwrapped and inlined during returning.
           */
          if (this.payloadArity() == 0 && this.rarePayloadArity() == 2 && this.nodeArity() == 0) {
            final int newRawMap =
                (shift == 0) ? (int) (rawMap2() ^ bitpos) : bitpos(mask(keyHash, 0));

            return nodeOf2x0(mutator, newRawMap, newRawMap, getRareKey(1 - rareIndex),
                getRareVal(1 - rareIndex));
          } else
            if (this.payloadArity() == 1 && this.rarePayloadArity() == 1 && this.nodeArity() == 0) {
            final int newRawMap =
                (shift == 0) ? (int) (rawMap2() ^ bitpos) : bitpos(mask(keyHash, 0));

            return nodeOf0x1(mutator, (int) (0), newRawMap, getKey(0), getVal(0));
          } else {
            return copyAndRemoveRareValue(mutator, bitpos);
          }
        } else {
          return this;
        }
      }

      // check for node (not value)
      final int nodeMap = nodeMap();
      if (isBitInBitmap(nodeMap, bitpos)) {
        final int nodeIndex = index(nodeMap, mask, bitpos);
        final AbstractMapNode subNode = getNode(nodeIndex);
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
            if (payloadArity() == 1) {
              return copyAndMigrateFromNodeToInline(mutator, bitpos, subNodeNew);
            } else {
              return copyAndMigrateFromNodeToRareInline(mutator, bitpos, subNodeNew);
            }

          }
          default: {
            // modify current node (set replacement node)
            return copyAndSetNode(mutator, nodeIndex, subNodeNew);
          }
        }
      }

      // no value
      return this;
    }

    @Override
    public AbstractMapNode removed(final AtomicReference<Thread> mutator, final int key,
        final int keyHash, final int shift, final MapResult details) {
      final int mask = mask(keyHash, shift);
      final int bitpos = bitpos(mask);

      // TODO: generalize bitmap declaration/if/index assignment in code generator pattern
      // check for inplace value
      final int dataMap = dataMap();
      if (isBitInBitmap(dataMap, bitpos)) {
        final int dataIndex = index(dataMap, mask, bitpos);
        final int currentKey = getKey(dataIndex);

        if (currentKey == key) {
          final int currentVal = getVal(dataIndex);
          details.updated(EitherIntOrObject.ofInt(currentVal));

          /*
           * Case payload == 2: Create new node with remaining pair. The new node will a) either
           * become the new root returned, or b) unwrapped and inlined during returning.
           */
          if (this.payloadArity() == 2 && this.rarePayloadArity() == 0 && this.nodeArity() == 0) {
            final int newRawMap =
                (shift == 0) ? (int) (rawMap2() ^ bitpos) : bitpos(mask(keyHash, 0));

            return nodeOf0x1(mutator, newRawMap, newRawMap, getKey(1 - dataIndex),
                getVal(1 - dataIndex));
          } else
            if (this.payloadArity() == 1 && this.rarePayloadArity() == 1 && this.nodeArity() == 0) {
            final int newRawMap =
                (shift == 0) ? (int) (rawMap2() ^ bitpos) : bitpos(mask(keyHash, 0));

            return nodeOf2x0(mutator, (int) (0), newRawMap, getRareKey(0), getRareVal(0));
          } else {
            return copyAndRemoveValue(mutator, bitpos);
          }
        } else {
          return this;
        }
      }

      // check for node (not value)
      final int nodeMap = nodeMap();
      if (isBitInBitmap(nodeMap, bitpos)) {
        final int nodeIndex = index(nodeMap, mask, bitpos);
        final AbstractMapNode subNode = getNode(nodeIndex);
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
            if (payloadArity() == 1) {
              return copyAndMigrateFromNodeToInline(mutator, bitpos, subNodeNew);
            } else {
              return copyAndMigrateFromNodeToRareInline(mutator, bitpos, subNodeNew);
            }

          }
          default: {
            // modify current node (set replacement node)
            return copyAndSetNode(mutator, nodeIndex, subNodeNew);
          }
        }
      }

      // no value
      return this;
    }

    @Override
    boolean hasSlots() {
      return slotArity() != 0;
    }

    @Override
    int nodeArity() {
      return Integer.bitCount(nodeMap());
    }

    @Override
    public AbstractMapNode removed(final AtomicReference<Thread> mutator, final Object key,
        final int keyHash, final int shift, final MapResult details) {
      final int mask = mask(keyHash, shift);
      final int bitpos = bitpos(mask);

      // TODO: generalize bitmap declaration/if/index assignment in code generator pattern
      // check for inplace (rare) value
      final int rareMap = rareMap();
      if (isBitInBitmap(rareMap, bitpos)) {
        final int rareIndex = index(rareMap, mask, bitpos);
        final Object currentKey = getRareKey(rareIndex);

        if (currentKey.equals(key)) {
          final Object currentVal = getRareVal(rareIndex);
          details.updated(EitherIntOrObject.ofObject(currentVal));

          /*
           * Case payload == 2: Create new node with remaining pair. The new node will a) either
           * become the new root returned, or b) unwrapped and inlined during returning.
           */
          if (this.payloadArity() == 0 && this.rarePayloadArity() == 2 && this.nodeArity() == 0) {
            final int newRawMap =
                (shift == 0) ? (int) (rawMap2() ^ bitpos) : bitpos(mask(keyHash, 0));

            return nodeOf2x0(mutator, newRawMap, newRawMap, getRareKey(1 - rareIndex),
                getRareVal(1 - rareIndex));
          } else
            if (this.payloadArity() == 1 && this.rarePayloadArity() == 1 && this.nodeArity() == 0) {
            final int newRawMap =
                (shift == 0) ? (int) (rawMap2() ^ bitpos) : bitpos(mask(keyHash, 0));

            return nodeOf0x1(mutator, (int) (0), newRawMap, getKey(0), getVal(0));
          } else {
            return copyAndRemoveRareValue(mutator, bitpos);
          }
        } else {
          return this;
        }
      }

      // check for node (not value)
      final int nodeMap = nodeMap();
      if (isBitInBitmap(nodeMap, bitpos)) {
        final int nodeIndex = index(nodeMap, mask, bitpos);
        final AbstractMapNode subNode = getNode(nodeIndex);
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
            if (payloadArity() == 1) {
              return copyAndMigrateFromNodeToInline(mutator, bitpos, subNodeNew);
            } else {
              return copyAndMigrateFromNodeToRareInline(mutator, bitpos, subNodeNew);
            }

          }
          default: {
            // modify current node (set replacement node)
            return copyAndSetNode(mutator, nodeIndex, subNodeNew);
          }
        }
      }

      // no value
      return this;
    }

    @Override
    int slotArity() {
      return unsafe.getInt(this.getClass(), globalSlotArityOffset);
    }

    @Override
    public boolean equals(final Object other) {
      return equals(this, other);
    }

    @Override
    int getVal(final int index) {
      return getVal(this.getClass(), this, index);
    }

    @Override
    AbstractMapNode getNode(final int index) {
      return getNode(this.getClass(), this, index);
    }
  }

  private static class HashCollisionMapNode_5Bits_Heterogeneous_BleedingEdge
      extends AbstractMapNode {

    final int hash;
    final Object[] keys;
    final Object[] vals;

    private HashCollisionMapNode_5Bits_Heterogeneous_BleedingEdge(final int hash,
        final Object[] keys, final Object[] vals) {
      this.hash = hash;
      this.keys = keys;
      this.vals = vals;
    }

    @Override
    AbstractMapNode updated(final AtomicReference<Thread> mutator, final int key, final int val,
        final int keyHash, final int shift, final MapResult details, final Comparator<Object> cmp) {
      assert this.hash == keyHash;

      for (int idx = 0; idx < keys.length; idx++) {
        if (cmp.compare(keys[idx], key) == 0) {
          final Object currentVal = vals[idx];

          if (cmp.compare(currentVal, val) == 0) {
            return this;
          } else {
            // add new mapping
            final Object[] src = this.vals;
            final Object[] dst = (Object[]) new Object[src.length];

            // copy 'src' and set 1 element(s) at position 'idx'
            System.arraycopy(src, 0, dst, 0, src.length);
            dst[idx + 0] = val;

            final AbstractMapNode thisNew =
                new HashCollisionMapNode_5Bits_Heterogeneous_BleedingEdge(this.hash, this.keys,
                    dst);

            details.updated(EitherIntOrObject.ofObject(currentVal));
            return thisNew;
          }
        }
      }

      final Object[] keysNew = (Object[]) new Object[this.keys.length + 1];

      // copy 'this.keys' and insert 1 element(s) at position 'keys.length'
      System.arraycopy(this.keys, 0, keysNew, 0, keys.length);
      keysNew[keys.length + 0] = key;
      System.arraycopy(this.keys, keys.length, keysNew, keys.length + 1,
          this.keys.length - keys.length);

      final Object[] valsNew = (Object[]) new Object[this.vals.length + 1];

      // copy 'this.vals' and insert 1 element(s) at position 'vals.length'
      System.arraycopy(this.vals, 0, valsNew, 0, vals.length);
      valsNew[vals.length + 0] = val;
      System.arraycopy(this.vals, vals.length, valsNew, vals.length + 1,
          this.vals.length - vals.length);

      details.modified();
      return new HashCollisionMapNode_5Bits_Heterogeneous_BleedingEdge(keyHash, keysNew, valsNew);
    }

    @Override
    AbstractMapNode updated(final AtomicReference<Thread> mutator, final int key, final int val,
        final int keyHash, final int shift, final MapResult details) {
      assert this.hash == keyHash;

      for (int idx = 0; idx < keys.length; idx++) {
        if (keys[idx].equals(key)) {
          final Object currentVal = vals[idx];

          if (currentVal.equals(val)) {
            return this;
          } else {
            // add new mapping
            final Object[] src = this.vals;
            final Object[] dst = (Object[]) new Object[src.length];

            // copy 'src' and set 1 element(s) at position 'idx'
            System.arraycopy(src, 0, dst, 0, src.length);
            dst[idx + 0] = val;

            final AbstractMapNode thisNew =
                new HashCollisionMapNode_5Bits_Heterogeneous_BleedingEdge(this.hash, this.keys,
                    dst);

            details.updated(EitherIntOrObject.ofObject(currentVal));
            return thisNew;
          }
        }
      }

      final Object[] keysNew = (Object[]) new Object[this.keys.length + 1];

      // copy 'this.keys' and insert 1 element(s) at position 'keys.length'
      System.arraycopy(this.keys, 0, keysNew, 0, keys.length);
      keysNew[keys.length + 0] = key;
      System.arraycopy(this.keys, keys.length, keysNew, keys.length + 1,
          this.keys.length - keys.length);

      final Object[] valsNew = (Object[]) new Object[this.vals.length + 1];

      // copy 'this.vals' and insert 1 element(s) at position 'vals.length'
      System.arraycopy(this.vals, 0, valsNew, 0, vals.length);
      valsNew[vals.length + 0] = val;
      System.arraycopy(this.vals, vals.length, valsNew, vals.length + 1,
          this.vals.length - vals.length);

      details.modified();
      return new HashCollisionMapNode_5Bits_Heterogeneous_BleedingEdge(keyHash, keysNew, valsNew);
    }

    @Override
    Map.Entry<Object, Object> getKeyValueEntry(final int index) {
      return entryOf(keys[index], vals[index]);
    }

    @Override
    AbstractMapNode updated(final AtomicReference<Thread> mutator, final Object key,
        final Object val, final int keyHash, final int shift, final MapResult details,
        final Comparator<Object> cmp) {
      assert this.hash == keyHash;

      for (int idx = 0; idx < keys.length; idx++) {
        if (cmp.compare(keys[idx], key) == 0) {
          final Object currentVal = vals[idx];

          if (cmp.compare(currentVal, val) == 0) {
            return this;
          } else {
            // add new mapping
            final Object[] src = this.vals;
            final Object[] dst = (Object[]) new Object[src.length];

            // copy 'src' and set 1 element(s) at position 'idx'
            System.arraycopy(src, 0, dst, 0, src.length);
            dst[idx + 0] = val;

            final AbstractMapNode thisNew =
                new HashCollisionMapNode_5Bits_Heterogeneous_BleedingEdge(this.hash, this.keys,
                    dst);

            details.updated(EitherIntOrObject.ofObject(currentVal));
            return thisNew;
          }
        }
      }

      final Object[] keysNew = (Object[]) new Object[this.keys.length + 1];

      // copy 'this.keys' and insert 1 element(s) at position 'keys.length'
      System.arraycopy(this.keys, 0, keysNew, 0, keys.length);
      keysNew[keys.length + 0] = key;
      System.arraycopy(this.keys, keys.length, keysNew, keys.length + 1,
          this.keys.length - keys.length);

      final Object[] valsNew = (Object[]) new Object[this.vals.length + 1];

      // copy 'this.vals' and insert 1 element(s) at position 'vals.length'
      System.arraycopy(this.vals, 0, valsNew, 0, vals.length);
      valsNew[vals.length + 0] = val;
      System.arraycopy(this.vals, vals.length, valsNew, vals.length + 1,
          this.vals.length - vals.length);

      details.modified();
      return new HashCollisionMapNode_5Bits_Heterogeneous_BleedingEdge(keyHash, keysNew, valsNew);
    }

    @Override
    AbstractMapNode updated(final AtomicReference<Thread> mutator, final Object key,
        final Object val, final int keyHash, final int shift, final MapResult details) {
      assert this.hash == keyHash;

      for (int idx = 0; idx < keys.length; idx++) {
        if (keys[idx].equals(key)) {
          final Object currentVal = vals[idx];

          if (currentVal.equals(val)) {
            return this;
          } else {
            // add new mapping
            final Object[] src = this.vals;
            final Object[] dst = (Object[]) new Object[src.length];

            // copy 'src' and set 1 element(s) at position 'idx'
            System.arraycopy(src, 0, dst, 0, src.length);
            dst[idx + 0] = val;

            final AbstractMapNode thisNew =
                new HashCollisionMapNode_5Bits_Heterogeneous_BleedingEdge(this.hash, this.keys,
                    dst);

            details.updated(EitherIntOrObject.ofObject(currentVal));
            return thisNew;
          }
        }
      }

      final Object[] keysNew = (Object[]) new Object[this.keys.length + 1];

      // copy 'this.keys' and insert 1 element(s) at position 'keys.length'
      System.arraycopy(this.keys, 0, keysNew, 0, keys.length);
      keysNew[keys.length + 0] = key;
      System.arraycopy(this.keys, keys.length, keysNew, keys.length + 1,
          this.keys.length - keys.length);

      final Object[] valsNew = (Object[]) new Object[this.vals.length + 1];

      // copy 'this.vals' and insert 1 element(s) at position 'vals.length'
      System.arraycopy(this.vals, 0, valsNew, 0, vals.length);
      valsNew[vals.length + 0] = val;
      System.arraycopy(this.vals, vals.length, valsNew, vals.length + 1,
          this.vals.length - vals.length);

      details.modified();
      return new HashCollisionMapNode_5Bits_Heterogeneous_BleedingEdge(keyHash, keysNew, valsNew);
    }

    @Override
    Optional<Object> findByKey(final int key, final int keyHash, final int shift,
        final Comparator<Object> cmp) {
      for (int i = 0; i < keys.length; i++) {
        final Object _key = keys[i];
        if (cmp.compare(_key, key) == 0) {
          final Object val = vals[i];
          return Optional.of(val);
        }
      }
      return Optional.empty();
    }

    @Override
    int getKey(final int index) {
      throw new IllegalStateException("Converted to `rarePayload`.");
    }

    @Override
    public String toString() {
      throw new UnsupportedOperationException("Not yet implemented.");
    }

    @Override
    Object getRareKey(final int index) {
      return keys[index];
    }

    @Override
    boolean containsKeyEquivalent(final Object key, final int keyHash, final int shift,
        final Comparator<Object> cmp) {
      if (this.hash == keyHash) {
        for (Object k : keys) {
          if (cmp.compare(k, key) == 0) {
            return true;
          }
        }
      }
      return false;
    }

    @Override
    boolean containsKey(final int key, final int keyHash, final int shift) {
      if (this.hash == keyHash) {
        for (Object k : keys) {
          if (k.equals(key)) {
            return true;
          }
        }
      }
      return false;
    }

    @Override
    int untypedSlotArity() {
      throw new UnsupportedOperationException();
    }

    @Override
    Optional<Object> findByKey(final Object key, final int keyHash, final int shift,
        final Comparator<Object> cmp) {
      for (int i = 0; i < keys.length; i++) {
        final Object _key = keys[i];
        if (cmp.compare(_key, key) == 0) {
          final Object val = vals[i];
          return Optional.of(val);
        }
      }
      return Optional.empty();
    }

    @Override
    Optional<Object> findByKey(final int key, final int keyHash, final int shift) {
      for (int i = 0; i < keys.length; i++) {
        final Object _key = keys[i];
        if (_key.equals(key)) {
          final Object val = vals[i];
          return Optional.of(val);
        }
      }
      return Optional.empty();
    }

    @Override
    Object getSlot(final int index) {
      throw new UnsupportedOperationException();
    }

    @Override
    int payloadArity() {
      return 0;
    }

    @Override
    boolean hasPayload() {
      return true;
    }

    @Override
    public AbstractMapNode removed(final AtomicReference<Thread> mutator, final int key,
        final int keyHash, final int shift, final MapResult details, final Comparator<Object> cmp) {
      for (int idx = 0; idx < keys.length; idx++) {
        if (cmp.compare(keys[idx], key) == 0) {
          final Object currentVal = vals[idx];
          details.updated(EitherIntOrObject.ofObject(currentVal));

          if (this.arity() == 1) {
            return EMPTY_NODE;
          } else if (this.arity() == 2) {
            /*
             * Create root node with singleton element. This node will be a) either be the new root
             * returned, or b) unwrapped and inlined.
             */
            final Object theOtherKey = (idx == 0) ? keys[1] : keys[0];
            final Object theOtherVal = (idx == 0) ? vals[1] : vals[0];
            return EMPTY_NODE.updated(mutator, theOtherKey, theOtherVal, keyHash, 0, details, cmp);
          } else {
            final Object[] keysNew = (Object[]) new Object[this.keys.length - 1];

            // copy 'this.keys' and remove 1 element(s) at position 'idx'
            System.arraycopy(this.keys, 0, keysNew, 0, idx);
            System.arraycopy(this.keys, idx + 1, keysNew, idx, this.keys.length - idx - 1);

            final Object[] valsNew = (Object[]) new Object[this.vals.length - 1];

            // copy 'this.vals' and remove 1 element(s) at position 'idx'
            System.arraycopy(this.vals, 0, valsNew, 0, idx);
            System.arraycopy(this.vals, idx + 1, valsNew, idx, this.vals.length - idx - 1);

            return new HashCollisionMapNode_5Bits_Heterogeneous_BleedingEdge(keyHash, keysNew,
                valsNew);
          }
        }
      }
      return this;
    }

    @Override
    Optional<Object> findByKey(final Object key, final int keyHash, final int shift) {
      for (int i = 0; i < keys.length; i++) {
        final Object _key = keys[i];
        if (_key.equals(key)) {
          final Object val = vals[i];
          return Optional.of(val);
        }
      }
      return Optional.empty();
    }

    @Override
    public AbstractMapNode removed(final AtomicReference<Thread> mutator, final Object key,
        final int keyHash, final int shift, final MapResult details, final Comparator<Object> cmp) {
      for (int idx = 0; idx < keys.length; idx++) {
        if (cmp.compare(keys[idx], key) == 0) {
          final Object currentVal = vals[idx];
          details.updated(EitherIntOrObject.ofObject(currentVal));

          if (this.arity() == 1) {
            return EMPTY_NODE;
          } else if (this.arity() == 2) {
            /*
             * Create root node with singleton element. This node will be a) either be the new root
             * returned, or b) unwrapped and inlined.
             */
            final Object theOtherKey = (idx == 0) ? keys[1] : keys[0];
            final Object theOtherVal = (idx == 0) ? vals[1] : vals[0];
            return EMPTY_NODE.updated(mutator, theOtherKey, theOtherVal, keyHash, 0, details, cmp);
          } else {
            final Object[] keysNew = (Object[]) new Object[this.keys.length - 1];

            // copy 'this.keys' and remove 1 element(s) at position 'idx'
            System.arraycopy(this.keys, 0, keysNew, 0, idx);
            System.arraycopy(this.keys, idx + 1, keysNew, idx, this.keys.length - idx - 1);

            final Object[] valsNew = (Object[]) new Object[this.vals.length - 1];

            // copy 'this.vals' and remove 1 element(s) at position 'idx'
            System.arraycopy(this.vals, 0, valsNew, 0, idx);
            System.arraycopy(this.vals, idx + 1, valsNew, idx, this.vals.length - idx - 1);

            return new HashCollisionMapNode_5Bits_Heterogeneous_BleedingEdge(keyHash, keysNew,
                valsNew);
          }
        }
      }
      return this;
    }

    @Override
    public AbstractMapNode removed(final AtomicReference<Thread> mutator, final int key,
        final int keyHash, final int shift, final MapResult details) {
      for (int idx = 0; idx < keys.length; idx++) {
        if (keys[idx].equals(key)) {
          final Object currentVal = vals[idx];
          details.updated(EitherIntOrObject.ofObject(currentVal));

          if (this.arity() == 1) {
            return EMPTY_NODE;
          } else if (this.arity() == 2) {
            /*
             * Create root node with singleton element. This node will be a) either be the new root
             * returned, or b) unwrapped and inlined.
             */
            final Object theOtherKey = (idx == 0) ? keys[1] : keys[0];
            final Object theOtherVal = (idx == 0) ? vals[1] : vals[0];
            return EMPTY_NODE.updated(mutator, theOtherKey, theOtherVal, keyHash, 0, details);
          } else {
            final Object[] keysNew = (Object[]) new Object[this.keys.length - 1];

            // copy 'this.keys' and remove 1 element(s) at position 'idx'
            System.arraycopy(this.keys, 0, keysNew, 0, idx);
            System.arraycopy(this.keys, idx + 1, keysNew, idx, this.keys.length - idx - 1);

            final Object[] valsNew = (Object[]) new Object[this.vals.length - 1];

            // copy 'this.vals' and remove 1 element(s) at position 'idx'
            System.arraycopy(this.vals, 0, valsNew, 0, idx);
            System.arraycopy(this.vals, idx + 1, valsNew, idx, this.vals.length - idx - 1);

            return new HashCollisionMapNode_5Bits_Heterogeneous_BleedingEdge(keyHash, keysNew,
                valsNew);
          }
        }
      }
      return this;
    }

    @Override
    boolean hasSlots() {
      throw new UnsupportedOperationException();
    }

    @Override
    int nodeArity() {
      return 0;
    }

    @Override
    public AbstractMapNode removed(final AtomicReference<Thread> mutator, final Object key,
        final int keyHash, final int shift, final MapResult details) {
      for (int idx = 0; idx < keys.length; idx++) {
        if (keys[idx].equals(key)) {
          final Object currentVal = vals[idx];
          details.updated(EitherIntOrObject.ofObject(currentVal));

          if (this.arity() == 1) {
            return EMPTY_NODE;
          } else if (this.arity() == 2) {
            /*
             * Create root node with singleton element. This node will be a) either be the new root
             * returned, or b) unwrapped and inlined.
             */
            final Object theOtherKey = (idx == 0) ? keys[1] : keys[0];
            final Object theOtherVal = (idx == 0) ? vals[1] : vals[0];
            return EMPTY_NODE.updated(mutator, theOtherKey, theOtherVal, keyHash, 0, details);
          } else {
            final Object[] keysNew = (Object[]) new Object[this.keys.length - 1];

            // copy 'this.keys' and remove 1 element(s) at position 'idx'
            System.arraycopy(this.keys, 0, keysNew, 0, idx);
            System.arraycopy(this.keys, idx + 1, keysNew, idx, this.keys.length - idx - 1);

            final Object[] valsNew = (Object[]) new Object[this.vals.length - 1];

            // copy 'this.vals' and remove 1 element(s) at position 'idx'
            System.arraycopy(this.vals, 0, valsNew, 0, idx);
            System.arraycopy(this.vals, idx + 1, valsNew, idx, this.vals.length - idx - 1);

            return new HashCollisionMapNode_5Bits_Heterogeneous_BleedingEdge(keyHash, keysNew,
                valsNew);
          }
        }
      }
      return this;
    }

    @Override
    int slotArity() {
      throw new UnsupportedOperationException();
    }

    @Override
    public boolean equals(final Object other) {
      if (null == other) {
        return false;
      }
      if (this == other) {
        return true;
      }
      if (getClass() != other.getClass()) {
        return false;
      }

      HashCollisionMapNode_5Bits_Heterogeneous_BleedingEdge that =
          (HashCollisionMapNode_5Bits_Heterogeneous_BleedingEdge) other;

      if (hash != that.hash) {
        return false;
      }

      if (arity() != that.arity()) {
        return false;
      }

      /*
       * Linear scan for each key, because of arbitrary element order.
       */
      outerLoop: for (int i = 0; i < that.payloadArity(); i++) {
        final Object otherKey = that.getKey(i);
        final Object otherVal = that.getVal(i);

        for (int j = 0; j < keys.length; j++) {
          final Object key = keys[j];
          final Object val = vals[j];

          if (key.equals(otherKey) && val.equals(otherVal)) {
            continue outerLoop;
          }
        }
        return false;
      }

      return true;
    }

    @Override
    boolean containsKeyEquivalent(final int key, final int keyHash, final int shift,
        final Comparator<Object> cmp) {
      if (this.hash == keyHash) {
        for (Object k : keys) {
          if (cmp.compare(k, key) == 0) {
            return true;
          }
        }
      }
      return false;
    }

    @Override
    boolean containsKey(final Object key, final int keyHash, final int shift) {
      if (this.hash == keyHash) {
        for (Object k : keys) {
          if (k.equals(key)) {
            return true;
          }
        }
      }
      return false;
    }

    @Override
    byte sizePredicate() {
      return sizeMoreThanOne();
    }

    @Override
    boolean hasNodes() {
      return false;
    }

    @Override
    Object getRareVal(final int index) {
      return vals[index];
    }

    @Override
    int rarePayloadArity() {
      return keys.length;
    }

    @Override
    AbstractMapNode getNode(final int index) {
      throw new IllegalStateException("Is leaf node.");
    }

    @Override
    int getVal(final int index) {
      throw new IllegalStateException("Converted to `rarePayload`.");
    }
  }
  /**
   * Iterator skeleton that uses a fixed stack in depth.
   */
  private static abstract class AbstractMapIterator {

    private static final int MAX_DEPTH = 7;

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

  protected static class MapKeyIterator extends AbstractMapIterator implements Iterator<Object> {

    MapKeyIterator(AbstractMapNode rootNode) {
      super(rootNode);
    }

    @Override
    public Object next() {
      if (!hasNext()) {
        throw new NoSuchElementException();
      } else {
        return currentValueNode.getKey(currentValueCursor++);
      }
    }

  }

  protected static class MapValueIterator extends AbstractMapIterator implements Iterator<Object> {

    MapValueIterator(AbstractMapNode rootNode) {
      super(rootNode);
    }

    @Override
    public Object next() {
      if (!hasNext()) {
        throw new NoSuchElementException();
      } else {
        return currentValueNode.getVal(currentValueCursor++);
      }
    }

  }

  protected static class MapEntryIterator extends AbstractMapIterator
      implements Iterator<Map.Entry<Object, Object>> {

    MapEntryIterator(AbstractMapNode rootNode) {
      super(rootNode);
    }

    @Override
    public Map.Entry<Object, Object> next() {
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
  private static class TrieMap_5Bits_Heterogeneous_BleedingEdgeNodeIterator
      implements Iterator<AbstractMapNode> {

    final Deque<Iterator<? extends AbstractMapNode>> nodeIteratorStack;

    TrieMap_5Bits_Heterogeneous_BleedingEdgeNodeIterator(AbstractMapNode rootNode) {
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
  static final class TransientTrieMap_5Bits_Heterogeneous_BleedingEdge
      implements TransientMap<Object, Object> {
    final private AtomicReference<Thread> mutator;
    private AbstractMapNode rootNode;
    private int hashCode;
    private int cachedSize;

    TransientTrieMap_5Bits_Heterogeneous_BleedingEdge(
        TrieMap_5Bits_Heterogeneous_BleedingEdge trieMap_5Bits_Heterogeneous_BleedingEdge) {
      this.mutator = new AtomicReference<Thread>(Thread.currentThread());
      this.rootNode = trieMap_5Bits_Heterogeneous_BleedingEdge.rootNode;
      this.hashCode = trieMap_5Bits_Heterogeneous_BleedingEdge.hashCode;
      this.cachedSize = trieMap_5Bits_Heterogeneous_BleedingEdge.cachedSize;
      if (DEBUG) {
        assert checkHashCodeAndSize(hashCode, cachedSize);
      }
    }

    private boolean checkHashCodeAndSize(final int targetHash, final int targetSize) {
      int hash = 0;
      int size = 0;

      for (Iterator<Map.Entry<Object, Object>> it = entryIterator(); it.hasNext();) {
        final Map.Entry<Object, Object> entry = it.next();
        final Object key = entry.getKey();
        final Object val = entry.getValue();

        hash += key.hashCode() ^ val.hashCode();
        size += 1;
      }

      return hash == targetHash && size == targetSize;
    }

    public Object put(final Object key, final Object val) {
      throw new UnsupportedOperationException();
    }

    public void putAll(final Map<? extends Object, ? extends Object> m) {
      throw new UnsupportedOperationException();
    }

    public void clear() {
      throw new UnsupportedOperationException();
    }

    public Object remove(final Object key) {
      throw new UnsupportedOperationException();
    }

    public boolean containsKey(final int key) {
      return rootNode.containsKey(key, transformHashCode(key), 0);
    }

    public boolean containsKeyEquivalent(final int key, final Comparator<Object> cmp) {
      return rootNode.containsKeyEquivalent(key, transformHashCode(key), 0, cmp);
    }

    public boolean containsKey(final Object o) {
      try {
        @SuppressWarnings("unchecked")
        final Object key = (Object) o;
        return rootNode.containsKey(key, transformHashCode(key.hashCode()), 0);
      } catch (ClassCastException unused) {
        return false;
      }
    }

    public boolean containsKeyEquivalent(final Object o, final Comparator<Object> cmp) {
      try {
        @SuppressWarnings("unchecked")
        final Object key = (Object) o;
        return rootNode.containsKeyEquivalent(key, transformHashCode(key.hashCode()), 0, cmp);
      } catch (ClassCastException unused) {
        return false;
      }
    }

    public boolean containsValue(final Object o) {
      for (Iterator<Object> iterator = valueIterator(); iterator.hasNext();) {
        if (iterator.next().equals(o)) {
          return true;
        }
      }
      return false;
    }

    public boolean containsValueEquivalent(final Object o, final Comparator<Object> cmp) {
      for (Iterator<Object> iterator = valueIterator(); iterator.hasNext();) {
        if (cmp.compare(iterator.next(), o) == 0) {
          return true;
        }
      }
      return false;
    }

    public Object get(final Object o) {
      try {
        @SuppressWarnings("unchecked")
        final int key = (int) o;
        final Optional<Object> result = rootNode.findByKey(key, transformHashCode(key), 0);

        if (result.isPresent()) {
          return result.get();
        } else {
          return null;
        }
      } catch (ClassCastException unused) {
        return null;
      }
    }

    public Object getEquivalent(final Object o, final Comparator<Object> cmp) {
      try {
        @SuppressWarnings("unchecked")
        final int key = (int) o;
        final Optional<Object> result = rootNode.findByKey(key, transformHashCode(key), 0, cmp);

        if (result.isPresent()) {
          return result.get();
        } else {
          return null;
        }
      } catch (ClassCastException unused) {
        return null;
      }
    }

    public Object __put(final int key, final int val) {
      if (mutator.get() == null) {
        throw new IllegalStateException("Transient already frozen.");
      }

      final int keyHash = (int) key;
      final MapResult details = MapResult.unchanged();

      final AbstractMapNode newRootNode =
          rootNode.updated(mutator, key, val, transformHashCode(keyHash), 0, details);

      if (details.isModified()) {
        if (details.hasReplacedValue()) {
          final int replacedValue = details.getReplacedValue().getInt();

          final int valHashOld = replacedValue;
          final int valHashNew = val;

          rootNode = newRootNode;
          hashCode = hashCode + (keyHash ^ valHashNew) - (keyHash ^ valHashOld);

          if (DEBUG) {
            assert checkHashCodeAndSize(hashCode, cachedSize);
          }
          return replacedValue;
        } else {
          final int valHashNew = (int) val;
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

    public Object __putEquivalent(final int key, final int val, final Comparator<Object> cmp) {
      if (mutator.get() == null) {
        throw new IllegalStateException("Transient already frozen.");
      }

      final int keyHash = (int) key;
      final MapResult details = MapResult.unchanged();

      final AbstractMapNode newRootNode =
          rootNode.updated(mutator, key, val, transformHashCode(keyHash), 0, details, cmp);

      if (details.isModified()) {
        if (details.hasReplacedValue()) {
          final int replacedValue = details.getReplacedValue().getInt();

          final int valHashOld = replacedValue;
          final int valHashNew = val;

          rootNode = newRootNode;
          hashCode = hashCode + (keyHash ^ valHashNew) - (keyHash ^ valHashOld);

          if (DEBUG) {
            assert checkHashCodeAndSize(hashCode, cachedSize);
          }
          return replacedValue;
        } else {
          final int valHashNew = (int) val;
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

    public Object __put(final Object key, final Object val) {
      if (mutator.get() == null) {
        throw new IllegalStateException("Transient already frozen.");
      }

      final int keyHash = key.hashCode();
      final MapResult details = MapResult.unchanged();

      final AbstractMapNode newRootNode =
          rootNode.updated(mutator, key, val, transformHashCode(keyHash), 0, details);

      if (details.isModified()) {
        if (details.hasReplacedValue()) {
          final Object replacedValue = details.getReplacedValue().getObject();

          final int valHashOld = replacedValue.hashCode();
          final int valHashNew = val.hashCode();

          rootNode = newRootNode;
          hashCode = hashCode + (keyHash ^ valHashNew) - (keyHash ^ valHashOld);

          if (DEBUG) {
            assert checkHashCodeAndSize(hashCode, cachedSize);
          }
          return replacedValue;
        } else {
          final int valHashNew = val.hashCode();
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

    public Object __putEquivalent(final Object key, final Object val,
        final Comparator<Object> cmp) {
      if (mutator.get() == null) {
        throw new IllegalStateException("Transient already frozen.");
      }

      final int keyHash = key.hashCode();
      final MapResult details = MapResult.unchanged();

      final AbstractMapNode newRootNode =
          rootNode.updated(mutator, key, val, transformHashCode(keyHash), 0, details, cmp);

      if (details.isModified()) {
        if (details.hasReplacedValue()) {
          final Object replacedValue = details.getReplacedValue().getObject();

          final int valHashOld = replacedValue.hashCode();
          final int valHashNew = val.hashCode();

          rootNode = newRootNode;
          hashCode = hashCode + (keyHash ^ valHashNew) - (keyHash ^ valHashOld);

          if (DEBUG) {
            assert checkHashCodeAndSize(hashCode, cachedSize);
          }
          return replacedValue;
        } else {
          final int valHashNew = val.hashCode();
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

    public boolean __putAll(final Map<? extends Object, ? extends Object> map) {
      boolean modified = false;

      for (Map.Entry<? extends Object, ? extends Object> entry : map.entrySet()) {
        final boolean isPresent = this.containsKey(entry.getKey());
        final Object replaced = this.__put(entry.getKey(), entry.getValue());

        if (!isPresent || replaced != null) {
          modified = true;
        }
      }

      return modified;
    }

    public boolean __putAllEquivalent(final Map<? extends Object, ? extends Object> map,
        final Comparator<Object> cmp) {
      boolean modified = false;

      for (Map.Entry<? extends Object, ? extends Object> entry : map.entrySet()) {
        final boolean isPresent = this.containsKeyEquivalent(entry.getKey(), cmp);
        final Object replaced = this.__putEquivalent(entry.getKey(), entry.getValue(), cmp);

        if (!isPresent || replaced != null) {
          modified = true;
        }
      }

      return modified;
    }

    public Object __remove(final int key) {
      if (mutator.get() == null) {
        throw new IllegalStateException("Transient already frozen.");
      }

      final int keyHash = (int) key;
      final MapResult details = MapResult.unchanged();

      final AbstractMapNode newRootNode =
          rootNode.removed(mutator, key, transformHashCode(keyHash), 0, details);

      if (details.isModified()) {
        assert details.hasReplacedValue();
        final int valHash = details.getReplacedValue().getInt();

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

    public Object __removeEquivalent(final int key, final Comparator<Object> cmp) {
      if (mutator.get() == null) {
        throw new IllegalStateException("Transient already frozen.");
      }

      final int keyHash = (int) key;
      final MapResult details = MapResult.unchanged();

      final AbstractMapNode newRootNode =
          rootNode.removed(mutator, key, transformHashCode(keyHash), 0, details, cmp);

      if (details.isModified()) {
        assert details.hasReplacedValue();
        final int valHash = details.getReplacedValue().getInt();

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

    public Object __remove(final Object key) {
      if (mutator.get() == null) {
        throw new IllegalStateException("Transient already frozen.");
      }

      final int keyHash = key.hashCode();
      final MapResult details = MapResult.unchanged();

      final AbstractMapNode newRootNode =
          rootNode.removed(mutator, key, transformHashCode(keyHash), 0, details);

      if (details.isModified()) {
        assert details.hasReplacedValue();
        final int valHash = details.getReplacedValue().getObject().hashCode();

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

    public Object __removeEquivalent(final Object key, final Comparator<Object> cmp) {
      if (mutator.get() == null) {
        throw new IllegalStateException("Transient already frozen.");
      }

      final int keyHash = key.hashCode();
      final MapResult details = MapResult.unchanged();

      final AbstractMapNode newRootNode =
          rootNode.removed(mutator, key, transformHashCode(keyHash), 0, details, cmp);

      if (details.isModified()) {
        assert details.hasReplacedValue();
        final int valHash = details.getReplacedValue().getObject().hashCode();

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

    public int size() {
      return cachedSize;
    }

    public boolean isEmpty() {
      return cachedSize == 0;
    }

    public Iterator<Object> keyIterator() {
      return new TransientMapKeyIterator(this);
    }

    public Iterator<Object> valueIterator() {
      return new TransientMapValueIterator(this);
    }

    public Iterator<Map.Entry<Object, Object>> entryIterator() {
      return new TransientMapEntryIterator(this);
    }

    public static class TransientMapKeyIterator extends MapKeyIterator {
      final TransientTrieMap_5Bits_Heterogeneous_BleedingEdge collection;
      Object lastKey;

      public TransientMapKeyIterator(
          final TransientTrieMap_5Bits_Heterogeneous_BleedingEdge collection) {
        super(collection.rootNode);
        this.collection = collection;
      }

      public Object next() {
        return lastKey = super.next();
      }

      public void remove() {
        // TODO: test removal at iteration rigorously
        collection.__remove(lastKey);
      }
    }

    public static class TransientMapValueIterator extends MapValueIterator {
      final TransientTrieMap_5Bits_Heterogeneous_BleedingEdge collection;

      public TransientMapValueIterator(
          final TransientTrieMap_5Bits_Heterogeneous_BleedingEdge collection) {
        super(collection.rootNode);
        this.collection = collection;
      }

      public Object next() {
        return super.next();
      }

      public void remove() {
        throw new UnsupportedOperationException();
      }
    }

    public static class TransientMapEntryIterator extends MapEntryIterator {
      final TransientTrieMap_5Bits_Heterogeneous_BleedingEdge collection;

      public TransientMapEntryIterator(
          final TransientTrieMap_5Bits_Heterogeneous_BleedingEdge collection) {
        super(collection.rootNode);
        this.collection = collection;
      }

      public Map.Entry<Object, Object> next() {
        return super.next();
      }

      public void remove() {
        throw new UnsupportedOperationException();
      }
    }

    @Override
    public Set<Object> keySet() {
      Set<Object> keySet = null;

      if (keySet == null) {
        keySet = new AbstractSet<Object>() {
          @Override
          public Iterator<Object> iterator() {
            return TransientTrieMap_5Bits_Heterogeneous_BleedingEdge.this.keyIterator();
          }

          @Override
          public int size() {
            return TransientTrieMap_5Bits_Heterogeneous_BleedingEdge.this.size();
          }

          @Override
          public boolean isEmpty() {
            return TransientTrieMap_5Bits_Heterogeneous_BleedingEdge.this.isEmpty();
          }

          @Override
          public void clear() {
            TransientTrieMap_5Bits_Heterogeneous_BleedingEdge.this.clear();
          }

          @Override
          public boolean contains(Object k) {
            return TransientTrieMap_5Bits_Heterogeneous_BleedingEdge.this.containsKey(k);
          }
        };
      }

      return keySet;
    }

    @Override
    public Collection<Object> values() {
      Collection<Object> values = null;

      if (values == null) {
        values = new AbstractCollection<Object>() {
          @Override
          public Iterator<Object> iterator() {
            return TransientTrieMap_5Bits_Heterogeneous_BleedingEdge.this.valueIterator();
          }

          @Override
          public int size() {
            return TransientTrieMap_5Bits_Heterogeneous_BleedingEdge.this.size();
          }

          @Override
          public boolean isEmpty() {
            return TransientTrieMap_5Bits_Heterogeneous_BleedingEdge.this.isEmpty();
          }

          @Override
          public void clear() {
            TransientTrieMap_5Bits_Heterogeneous_BleedingEdge.this.clear();
          }

          @Override
          public boolean contains(Object v) {
            return TransientTrieMap_5Bits_Heterogeneous_BleedingEdge.this.containsValue(v);
          }
        };
      }

      return values;
    }

    @Override
    public Set<java.util.Map.Entry<Object, Object>> entrySet() {
      Set<java.util.Map.Entry<Object, Object>> entrySet = null;

      if (entrySet == null) {
        entrySet = new AbstractSet<java.util.Map.Entry<Object, Object>>() {
          @Override
          public Iterator<java.util.Map.Entry<Object, Object>> iterator() {
            return new Iterator<Map.Entry<Object, Object>>() {
              private final Iterator<Map.Entry<Object, Object>> i = entryIterator();

              @Override
              public boolean hasNext() {
                return i.hasNext();
              }

              @Override
              public Map.Entry<Object, Object> next() {
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
            return TransientTrieMap_5Bits_Heterogeneous_BleedingEdge.this.size();
          }

          @Override
          public boolean isEmpty() {
            return TransientTrieMap_5Bits_Heterogeneous_BleedingEdge.this.isEmpty();
          }

          @Override
          public void clear() {
            TransientTrieMap_5Bits_Heterogeneous_BleedingEdge.this.clear();
          }

          @Override
          public boolean contains(Object k) {
            return TransientTrieMap_5Bits_Heterogeneous_BleedingEdge.this.containsKey(k);
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

      if (other instanceof TransientTrieMap_5Bits_Heterogeneous_BleedingEdge) {
        TransientTrieMap_5Bits_Heterogeneous_BleedingEdge that =
            (TransientTrieMap_5Bits_Heterogeneous_BleedingEdge) other;

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
            final Object key = (Object) entry.getKey();
            final Optional<Object> result =
                rootNode.findByKey(key, transformHashCode(key.hashCode()), 0);

            if (!result.isPresent()) {
              return false;
            } else {
              @SuppressWarnings("unchecked")
              final Object val = (Object) entry.getValue();

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
    public ImmutableMap<Object, Object> freeze() {
      if (mutator.get() == null) {
        throw new IllegalStateException("Transient already frozen.");
      }

      mutator.set(null);
      return new TrieMap_5Bits_Heterogeneous_BleedingEdge(rootNode, hashCode, cachedSize);
    }
  }

  private abstract static class DataLayoutHelper extends CompactMapNode {

    private static final long[] arrayOffsets =
        arrayOffsets(DataLayoutHelper.class, new String[] {"slot0", "slot1"});

    public final Object slot0 = null;

    public final Object slot1 = null;

    private DataLayoutHelper() {
      super(null, (int) 0, (int) 0);
    }

  }

}
