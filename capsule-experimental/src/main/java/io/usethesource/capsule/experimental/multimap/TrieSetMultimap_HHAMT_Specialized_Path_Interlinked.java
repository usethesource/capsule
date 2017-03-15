/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.capsule.experimental.multimap;

import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import io.usethesource.capsule.SetMultimap;
import io.usethesource.capsule.core.trie.EitherSingletonOrCollection;
import io.usethesource.capsule.core.trie.EitherSingletonOrCollection.Type;
import io.usethesource.capsule.experimental.multimap.TrieSetMultimap_HHAMT_Specializations_Path_Interlinked.SetMultimap0To0Node;
import io.usethesource.capsule.experimental.multimap.TrieSetMultimap_HHAMT_Specializations_Path_Interlinked.SetMultimap0To1Node;
import io.usethesource.capsule.experimental.multimap.TrieSetMultimap_HHAMT_Specializations_Path_Interlinked.SetMultimap0To2Node;
import io.usethesource.capsule.experimental.multimap.TrieSetMultimap_HHAMT_Specializations_Path_Interlinked.SetMultimap0To4Node;
import io.usethesource.capsule.experimental.multimap.TrieSetMultimap_HHAMT_Specializations_Path_Interlinked.SetMultimap1To0Node;
import io.usethesource.capsule.experimental.multimap.TrieSetMultimap_HHAMT_Specializations_Path_Interlinked.SetMultimap1To2Node;
import io.usethesource.capsule.experimental.multimap.TrieSetMultimap_HHAMT_Specializations_Path_Interlinked.SetMultimap2To0Node;
import io.usethesource.capsule.experimental.specialized.TrieSet_5Bits_Spec0To8;
import io.usethesource.capsule.experimental.specialized.TrieSet_5Bits_Spec0To8.AbstractSetNode;
import io.usethesource.capsule.experimental.specialized.TrieSet_5Bits_Spec0To8.SetResult;
import io.usethesource.capsule.util.EqualityComparator;
import io.usethesource.capsule.util.RangecopyUtils;
import io.usethesource.capsule.util.collection.AbstractSpecialisedImmutableMap;

import static io.usethesource.capsule.core.trie.EitherSingletonOrCollection.Type.COLLECTION;
import static io.usethesource.capsule.core.trie.EitherSingletonOrCollection.Type.SINGLETON;
import static io.usethesource.capsule.experimental.multimap.SetMultimapUtils.PATTERN_DATA_COLLECTION;
import static io.usethesource.capsule.experimental.multimap.SetMultimapUtils.PATTERN_DATA_SINGLETON;
import static io.usethesource.capsule.experimental.multimap.SetMultimapUtils.PATTERN_EMPTY;
import static io.usethesource.capsule.experimental.multimap.SetMultimapUtils.PATTERN_NODE;
import static io.usethesource.capsule.experimental.multimap.SetMultimapUtils.setBitPattern;
import static io.usethesource.capsule.experimental.multimap.SetMultimapUtils.setFromNode;
import static io.usethesource.capsule.experimental.multimap.SetMultimapUtils.specSetNodeOf;
import static io.usethesource.capsule.experimental.multimap.SetMultimapUtils.specSetOf;
import static io.usethesource.capsule.experimental.multimap.SetMultimapUtils.specSetToNode;
import static io.usethesource.capsule.util.BitmapUtils.filter;
import static io.usethesource.capsule.util.BitmapUtils.index;
import static io.usethesource.capsule.util.DataLayoutHelper.addressSize;
import static io.usethesource.capsule.util.DataLayoutHelper.arrayOffsets;
import static io.usethesource.capsule.util.DataLayoutHelper.fieldOffset;
import static io.usethesource.capsule.util.DataLayoutHelper.unsafe;
import static io.usethesource.capsule.util.RangecopyUtils._do_rangecompareObjectRegion;
import static io.usethesource.capsule.util.RangecopyUtils.getFromObjectRegion;
import static io.usethesource.capsule.util.RangecopyUtils.getFromObjectRegionAndCast;
import static io.usethesource.capsule.util.RangecopyUtils.rangecopyObjectRegion;
import static io.usethesource.capsule.util.RangecopyUtils.setInObjectRegion;
import static io.usethesource.capsule.util.RangecopyUtils.setInObjectRegionVarArgs;
import static io.usethesource.capsule.util.collection.AbstractSpecialisedImmutableMap.entryOf;

/**
 * NOTE: only difference to {@link TrieSetMultimap_HHAMT_Specialized_Interlinked} is the use of
 * {@link SetMultimapUtils#specSetNodeOf} instead of of {@link SetMultimapUtils#setNodeOf}.
 *
 * TODO: unify by injecting factory method.
 */
public class TrieSetMultimap_HHAMT_Specialized_Path_Interlinked<K, V>
    implements SetMultimap.Immutable<K, V> {

  private final EqualityComparator<Object> cmp;

  protected static final CompactSetMultimapNode EMPTY_NODE = new SetMultimap0To0Node<>(null, 0L);

  private static final TrieSetMultimap_HHAMT_Specialized_Path_Interlinked EMPTY_SETMULTIMAP =
      new TrieSetMultimap_HHAMT_Specialized_Path_Interlinked(EqualityComparator.EQUALS, EMPTY_NODE,
          0, 0);

  private static final boolean DEBUG = false;

  private final AbstractSetMultimapNode<K, V> rootNode;
  private final int hashCode;
  private final int cachedSize;

  TrieSetMultimap_HHAMT_Specialized_Path_Interlinked(EqualityComparator<Object> cmp,
      AbstractSetMultimapNode<K, V> rootNode, int hashCode, int cachedSize) {
    this.cmp = cmp;
    this.rootNode = rootNode;
    this.hashCode = hashCode;
    this.cachedSize = cachedSize;
    if (DEBUG) {
      assert checkHashCodeAndSize(hashCode, cachedSize);
    }
  }

  public static final <K, V> SetMultimap.Immutable<K, V> of() {
    return TrieSetMultimap_HHAMT_Specialized_Path_Interlinked.EMPTY_SETMULTIMAP;
  }

  public static final <K, V> SetMultimap.Immutable<K, V> of(EqualityComparator<Object> cmp) {
    // TODO: unify with `of()`
    return new TrieSetMultimap_HHAMT_Specialized_Path_Interlinked(cmp, EMPTY_NODE, 0, 0);
  }

  public static final <K, V> SetMultimap.Immutable<K, V> of(K key, V... values) {
    SetMultimap.Immutable<K, V> result =
        TrieSetMultimap_HHAMT_Specialized_Path_Interlinked.EMPTY_SETMULTIMAP;

    for (V value : values) {
      result = result.__insert(key, value);
    }

    return result;
  }

  public static final <K, V> SetMultimap.Transient<K, V> transientOf() {
    return TrieSetMultimap_HHAMT_Specialized_Path_Interlinked.EMPTY_SETMULTIMAP.asTransient();
  }

  public static final <K, V> SetMultimap.Transient<K, V> transientOf(K key, V... values) {
    final SetMultimap.Transient<K, V> result =
        TrieSetMultimap_HHAMT_Specialized_Path_Interlinked.EMPTY_SETMULTIMAP.asTransient();

    for (V value : values) {
      result.__insert(key, value);
    }

    return result;
  }

  private boolean checkHashCodeAndSize(final int targetHash, final int targetSize) {
    int hash = 0;
    int size = 0;

    for (Iterator<Map.Entry<K, V>> it = entryIterator(); it.hasNext(); ) {
      final Map.Entry<K, V> entry = it.next();
      final K key = entry.getKey();
      final V val = entry.getValue();

      hash += key.hashCode() ^ val.hashCode();
      size += 1;
    }

    return hash == targetHash && size == targetSize;
  }

  public static final int transformHashCode(final int hash) {
    return hash;
  }

  @Override
  public boolean containsKey(final Object o) {
    try {
      final K key = (K) o;
      return rootNode.containsKey(key, transformHashCode(key.hashCode()), 0, cmp);
    } catch (ClassCastException unused) {
      return false;
    }
  }

  @Override
  public boolean containsValue(final Object o) {
    for (Iterator<V> iterator = valueIterator(); iterator.hasNext(); ) {
      if (cmp.equals(iterator.next(), o)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public boolean containsEntry(final Object o0, final Object o1) {
    try {
      final K key = (K) o0;
      final V val = (V) o1;
      return rootNode.containsTuple(key, val, transformHashCode(key.hashCode()), 0, cmp);
    } catch (ClassCastException unused) {
      return false;
    }
  }

  @Override
  public io.usethesource.capsule.Set.Immutable<V> get(final Object o) {
    try {
      final K key = (K) o;
      final Optional<AbstractSetNode<V>> result =
          rootNode.findByKey(key, transformHashCode(key.hashCode()), 0, cmp);

      if (result.isPresent()) {
        return setFromNode(result.get());
      } else {
        return null;
      }
    } catch (ClassCastException unused) {
      return null;
    }
  }

  @Override
  public SetMultimap.Immutable<K, V> __put(K key, V val) {
    final int keyHash = key.hashCode();
    final SetMultimapResult<K, V> details = SetMultimapResult.unchanged();

    final CompactSetMultimapNode<K, V> newRootNode =
        rootNode.updated(null, key, val, transformHashCode(keyHash), 0, details, cmp);

    if (details.isModified()) {
      if (details.hasReplacedValue()) {
        if (details.getType() == SINGLETON) {
          final int valHashOld = details.getReplacedValue().hashCode();
          final int valHashNew = val.hashCode();

          return new TrieSetMultimap_HHAMT_Specialized_Path_Interlinked<K, V>(cmp, newRootNode,
              hashCode + ((keyHash ^ valHashNew)) - ((keyHash ^ valHashOld)), cachedSize);
        } else {
          int sumOfReplacedHashes = 0;

          for (V replaceValue : details.getReplacedCollection()) {
            sumOfReplacedHashes += (keyHash ^ replaceValue.hashCode());
          }

          final int valHashNew = val.hashCode();

          return new TrieSetMultimap_HHAMT_Specialized_Path_Interlinked<K, V>(cmp, newRootNode,
              hashCode + ((keyHash ^ valHashNew)) - sumOfReplacedHashes,
              cachedSize - details.getReplacedCollection().size() + 1);
        }
      }

      final int valHash = val.hashCode();
      return new TrieSetMultimap_HHAMT_Specialized_Path_Interlinked<K, V>(cmp, newRootNode,
          hashCode + ((keyHash ^ valHash)), cachedSize + 1);
    }

    return this;
  }

  @Override
  public SetMultimap.Immutable<K, V> __insert(final K key, final V val) {
    final int keyHash = key.hashCode();
    final SetMultimapResult<K, V> details = SetMultimapResult.unchanged();

    final CompactSetMultimapNode<K, V> newRootNode =
        rootNode.inserted(null, key, val, transformHashCode(keyHash), 0, details, cmp);

    if (details.isModified()) {
      final int valHash = val.hashCode();
      return new TrieSetMultimap_HHAMT_Specialized_Path_Interlinked<K, V>(cmp, newRootNode,
          hashCode + ((keyHash ^ valHash)), cachedSize + 1);
    }

    return this;
  }

  @Override
  public SetMultimap.Immutable<K, V> union(
      final SetMultimap<? extends K, ? extends V> setMultimap) {
    final SetMultimap.Transient<K, V> tmpTransient = this.asTransient();
    tmpTransient.union(setMultimap);
    return tmpTransient.freeze();
  }

  @Override
  public SetMultimap.Immutable<K, V> __remove(final K key, final V val) {
    final int keyHash = key.hashCode();
    final SetMultimapResult<K, V> details = SetMultimapResult.unchanged();

    final CompactSetMultimapNode<K, V> newRootNode =
        rootNode.removed(null, key, val, transformHashCode(keyHash), 0, details, cmp);

    if (details.isModified()) {
      assert details.hasReplacedValue();
      final int valHash = details.getReplacedValue().hashCode();
      return new TrieSetMultimap_HHAMT_Specialized_Path_Interlinked<K, V>(cmp, newRootNode,
          hashCode - ((keyHash ^ valHash)), cachedSize - 1);
    }

    return this;
  }

  @Override
  public SetMultimap.Immutable<K, V> __remove(K key) {
    final int keyHash = key.hashCode();
    final SetMultimapResult<K, V> details = SetMultimapResult.unchanged();

    final CompactSetMultimapNode<K, V> newRootNode =
        rootNode.removedAll(null, key, transformHashCode(keyHash), 0, details, cmp);

    if (details.isModified()) {
      assert details.hasReplacedValue();

      if (details.getType() == SINGLETON) {
        final int valHash = details.getReplacedValue().hashCode();
        return new TrieSetMultimap_HHAMT_Specialized_Path_Interlinked<K, V>(cmp, newRootNode,
            hashCode - ((keyHash ^ valHash)), cachedSize - 1);
      } else {
        int sumOfReplacedHashes = 0;

        for (V replaceValue : details.getReplacedCollection()) {
          sumOfReplacedHashes += (keyHash ^ replaceValue.hashCode());
        }

        return new TrieSetMultimap_HHAMT_Specialized_Path_Interlinked<K, V>(cmp, newRootNode,
            hashCode - sumOfReplacedHashes, cachedSize - details.getReplacedCollection().size());
      }
    }

    return this;
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
  public Iterator<K> keyIterator() {
    return new SetMultimapKeyIteratorLowLevel<>(rootNode);
    // return new SetMultimapKeyIteratorHistogram<>(rootNode);
  }

  @Override
  public Iterator<V> valueIterator() {
    return valueCollectionsStream().flatMap(AbstractSetNode::stream).iterator();
  }

  @Override
  public Iterator<Map.Entry<K, V>> entryIterator() {
    // return new SetMultimapTupleIterator<>(rootNode, AbstractSpecialisedImmutableMap::entryOf);

    return new FlatteningIterator<>(nativeEntryIterator());
  }

  @Override
  public Iterator<Map.Entry<K, Object>> nativeEntryIterator() {
    // return new SetMultimapNativeTupleIterator<>(rootNode);
    return new SetMultimapNativeTupleIteratorLowLevel<>(rootNode);
  }

  @Override
  public <T> Iterator<T> tupleIterator(final BiFunction<K, V, T> tupleOf) {
    return new SetMultimapTupleIterator<>(rootNode, tupleOf);
  }

  private Spliterator<AbstractSetNode<V>> valueCollectionsSpliterator() {
    /*
     * TODO: specialize between mutable / SetMultimap.Immutable<K, V> ({@see Spliterator.IMMUTABLE})
     */
    int characteristics = Spliterator.NONNULL | Spliterator.SIZED | Spliterator.SUBSIZED;
    return Spliterators.spliterator(new SetMultimapValueIterator<>(rootNode), size(),
        characteristics);
  }

  private Stream<AbstractSetNode<V>> valueCollectionsStream() {
    boolean isParallel = false;
    return StreamSupport.stream(valueCollectionsSpliterator(), isParallel);
  }

  @Override
  public Set<K> keySet() {
    Set<K> keySet = null;

    if (keySet == null) {
      keySet = new AbstractSet<K>() {
        @Override
        public Iterator<K> iterator() {
          return TrieSetMultimap_HHAMT_Specialized_Path_Interlinked.this.keyIterator();
        }

        @Override
        public int size() {
          return TrieSetMultimap_HHAMT_Specialized_Path_Interlinked.this.sizeDistinct();
        }

        @Override
        public boolean isEmpty() {
          return TrieSetMultimap_HHAMT_Specialized_Path_Interlinked.this.isEmpty();
        }

        @Override
        public void clear() {
          throw new UnsupportedOperationException();
        }

        @Override
        public boolean contains(Object k) {
          return TrieSetMultimap_HHAMT_Specialized_Path_Interlinked.this.containsKey(k);
        }
      };
    }

    return keySet;
  }

  @Override
  public Collection<V> values() {
    Collection<V> values = null;

    if (values == null) {
      values = new AbstractCollection<V>() {
        @Override
        public Iterator<V> iterator() {
          return TrieSetMultimap_HHAMT_Specialized_Path_Interlinked.this.valueIterator();
        }

        @Override
        public int size() {
          return TrieSetMultimap_HHAMT_Specialized_Path_Interlinked.this.size();
        }

        @Override
        public boolean isEmpty() {
          return TrieSetMultimap_HHAMT_Specialized_Path_Interlinked.this.isEmpty();
        }

        @Override
        public void clear() {
          throw new UnsupportedOperationException();
        }

        @Override
        public boolean contains(Object v) {
          return TrieSetMultimap_HHAMT_Specialized_Path_Interlinked.this.containsValue(v);
        }
      };
    }

    return values;
  }

  @Override
  public Set<java.util.Map.Entry<K, V>> entrySet() {
    Set<java.util.Map.Entry<K, V>> entrySet = null;

    if (entrySet == null) {
      entrySet = new AbstractSet<java.util.Map.Entry<K, V>>() {
        @Override
        public Iterator<java.util.Map.Entry<K, V>> iterator() {
          return new Iterator<Map.Entry<K, V>>() {
            private final Iterator<Map.Entry<K, V>> i = entryIterator();

            @Override
            public boolean hasNext() {
              return i.hasNext();
            }

            @Override
            public Map.Entry<K, V> next() {
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
          return TrieSetMultimap_HHAMT_Specialized_Path_Interlinked.this.size();
        }

        @Override
        public boolean isEmpty() {
          return TrieSetMultimap_HHAMT_Specialized_Path_Interlinked.this.isEmpty();
        }

        @Override
        public void clear() {
          throw new UnsupportedOperationException();
        }

        @Override
        public boolean contains(Object k) {
          return TrieSetMultimap_HHAMT_Specialized_Path_Interlinked.this.containsKey(k);
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

    if (other instanceof TrieSetMultimap_HHAMT_Specialized_Path_Interlinked) {
      TrieSetMultimap_HHAMT_Specialized_Path_Interlinked<?, ?> that =
          (TrieSetMultimap_HHAMT_Specialized_Path_Interlinked<?, ?>) other;

      if (this.cachedSize != that.cachedSize) {
        return false;
      }

      if (this.hashCode != that.hashCode) {
        return false;
      }

      return rootNode.equals(that.rootNode);
    } else if (other instanceof SetMultimap) {
      SetMultimap that = (SetMultimap) other;

      if (this.size() != that.size()) {
        return false;
      }

      for (
          Iterator<Map.Entry> it = that.entrySet().iterator(); it.hasNext(); ) {
        Map.Entry entry = it.next();

        try {
          final K key = (K) entry.getKey();
          final Optional<AbstractSetNode<V>> result =
              rootNode.findByKey(key, transformHashCode(key.hashCode()), 0, cmp);

          if (!result.isPresent()) {
            return false;
          } else {
            final AbstractSetNode<V> valColl = (AbstractSetNode<V>) entry.getValue();

            if (!cmp.equals(result.get(), valColl)) {
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
  public SetMultimap.Transient<K, V> asTransient() {
    return new TransientTrieSetMultimap_BleedingEdge<K, V>(this);
  }

  /*
   * For analysis purposes only.
   */
  protected AbstractSetMultimapNode<K, V> getRootNode() {
    return rootNode;
  }

  /*
   * For analysis purposes only.
   */
  protected Iterator<AbstractSetMultimapNode<K, V>> nodeIterator() {
    return new TrieSetMultimap_BleedingEdgeNodeIterator<>(rootNode);
  }

  /*
   * For analysis purposes only.
   */
  protected int getNodeCount() {
    final Iterator<AbstractSetMultimapNode<K, V>> it = nodeIterator();
    int sumNodes = 0;

    for (; it.hasNext(); it.next()) {
      sumNodes += 1;
    }

    return sumNodes;
  }

  // /*
  // * For analysis purposes only. Payload X Node
  // */
  // protected int[][] arityCombinationsHistogram() {
  // final Iterator<AbstractSetMultimapNode<K, V>> it = nodeIterator();
  // final int[][] sumArityCombinations = new int[33][33];
  //
  // while (it.hasNext()) {
  // final AbstractSetMultimapNode<K, V> node = it.next();
  // sumArityCombinations[node.payloadArity()][node.nodeArity()] += 1;
  // }
  //
  // return sumArityCombinations;
  // }
  //
  // /*
  // * For analysis purposes only.
  // */
  // protected int[] arityHistogram() {
  // final int[][] sumArityCombinations = arityCombinationsHistogram();
  // final int[] sumArity = new int[33];
  //
  // final int maxArity = 32; // TODO: factor out constant
  //
  // for (int j = 0; j <= maxArity; j++) {
  // for (int maxRestArity = maxArity - j, k = 0; k <= maxRestArity - j; k++) {
  // sumArity[j + k] += sumArityCombinations[j][k];
  // }
  // }
  //
  // return sumArity;
  // }
  //
  // /*
  // * For analysis purposes only.
  // */
  // public void printStatistics() {
  // final int[][] sumArityCombinations = arityCombinationsHistogram();
  // final int[] sumArity = arityHistogram();
  // final int sumNodes = getNodeCount();
  //
  // final int[] cumsumArity = new int[33];
  // for (int cumsum = 0, i = 0; i < 33; i++) {
  // cumsum += sumArity[i];
  // cumsumArity[i] = cumsum;
  // }
  //
  // final float threshhold = 0.01f; // for printing results
  // for (int i = 0; i < 33; i++) {
  // float arityPercentage = (float) (sumArity[i]) / sumNodes;
  // float cumsumArityPercentage = (float) (cumsumArity[i]) / sumNodes;
  //
  // if (arityPercentage != 0 && arityPercentage >= threshhold) {
  // // details per level
  // StringBuilder bldr = new StringBuilder();
  // int max = i;
  // for (int j = 0; j <= max; j++) {
  // for (int k = max - j; k <= max - j; k++) {
  // float arityCombinationsPercentage = (float) (sumArityCombinations[j][k]) / sumNodes;
  //
  // if (arityCombinationsPercentage != 0 && arityCombinationsPercentage >= threshhold) {
  // bldr.append(String.format("%d/%d: %s, ", j, k,
  // new DecimalFormat("0.00%").format(arityCombinationsPercentage)));
  // }
  // }
  // }
  // final String detailPercentages = bldr.toString();
  //
  // // overview
  // System.out.println(String.format("%2d: %s\t[cumsum = %s]\t%s", i,
  // new DecimalFormat("0.00%").format(arityPercentage),
  // new DecimalFormat("0.00%").format(cumsumArityPercentage), detailPercentages));
  // }
  // }
  // }

  static final class SetMultimapResult<K, V> {

    private V replacedValue;
    private AbstractSetNode<V> replacedValueCollection;
    private EitherSingletonOrCollection.Type replacedType;

    private boolean isModified;
    private boolean isReplaced;

    // update: inserted/removed single element, element count changed
    public void modified() {
      this.isModified = true;
    }

    public void updated(V replacedValue) {
      this.replacedValue = replacedValue;
      this.isModified = true;
      this.isReplaced = true;
      this.replacedType = SINGLETON;
    }

    public void updated(AbstractSetNode<V> replacedValueCollection) {
      this.replacedValueCollection = replacedValueCollection;
      this.isModified = true;
      this.isReplaced = true;
      this.replacedType = COLLECTION;
    }

    // update: neither element, nor element count changed
    public static <K, V> SetMultimapResult<K, V> unchanged() {
      return new SetMultimapResult<>();
    }

    private SetMultimapResult() {
    }

    public boolean isModified() {
      return isModified;
    }

    public EitherSingletonOrCollection.Type getType() {
      return replacedType;
    }

    public boolean hasReplacedValue() {
      return isReplaced;
    }

    public V getReplacedValue() {
      assert getType() == SINGLETON;
      return replacedValue;
    }

    public AbstractSetNode<V> getReplacedCollection() {
      assert getType() == COLLECTION;
      return replacedValueCollection;
    }
  }

  protected static interface INode<K, V> {

  }

  protected static abstract class AbstractSetMultimapNode<K, V> implements INode<K, V> {

    static final int TUPLE_LENGTH = 2;

    abstract boolean containsKey(final K key, final int keyHash, final int shift,
        EqualityComparator<Object> cmp);

    abstract boolean containsTuple(final K key, final V val, final int keyHash, final int shift,
        EqualityComparator<Object> cmp);

    abstract Optional<AbstractSetNode<V>> findByKey(final K key, final int keyHash, final int shift,
        EqualityComparator<Object> cmp);

    abstract CompactSetMultimapNode<K, V> inserted(final AtomicReference<Thread> mutator,
        final K key, final V val, final int keyHash, final int shift,
        final SetMultimapResult<K, V> details, EqualityComparator<Object> cmp);

    abstract CompactSetMultimapNode<K, V> updated(final AtomicReference<Thread> mutator,
        final K key, final V val, final int keyHash, final int shift,
        final SetMultimapResult<K, V> details, EqualityComparator<Object> cmp);

    abstract CompactSetMultimapNode<K, V> removed(final AtomicReference<Thread> mutator,
        final K key, final V val, final int keyHash, final int shift,
        final SetMultimapResult<K, V> details, EqualityComparator<Object> cmp);

    abstract CompactSetMultimapNode<K, V> removedAll(final AtomicReference<Thread> mutator,
        final K key, final int keyHash, final int shift, final SetMultimapResult<K, V> details,
        EqualityComparator<Object> cmp);

    static final boolean isAllowedToEdit(AtomicReference<Thread> x, AtomicReference<Thread> y) {
      return x != null && y != null && (x == y || x.get() == y.get());
    }

    abstract boolean hasNodes();

    abstract int nodeArity();

    abstract AbstractSetMultimapNode<K, V> getNode(final int index);

    @Deprecated
    Iterator<? extends AbstractSetMultimapNode<K, V>> nodeIterator() {
      return new Iterator<AbstractSetMultimapNode<K, V>>() {

        int nextIndex = 0;
        final int nodeArity = AbstractSetMultimapNode.this.nodeArity();

        @Override
        public void remove() {
          throw new UnsupportedOperationException();
        }

        @Override
        public AbstractSetMultimapNode<K, V> next() {
          if (!hasNext()) {
            throw new NoSuchElementException();
          }
          return AbstractSetMultimapNode.this.getNode(nextIndex++);
        }

        @Override
        public boolean hasNext() {
          return nextIndex < nodeArity;
        }
      };
    }

    abstract int emptyArity();

    // @Deprecated // split data / coll arity
    abstract boolean hasPayload();

    //
    // @Deprecated // split data / coll arity
    abstract int payloadArity();

    abstract boolean hasPayload(EitherSingletonOrCollection.Type type);

    // abstract int payloadArity();

    abstract int payloadArity(EitherSingletonOrCollection.Type type);

    abstract K getSingletonKey(final int index);

    abstract V getSingletonValue(final int index);

    abstract K getCollectionKey(final int index);

    abstract AbstractSetNode<V> getCollectionValue(final int index);

    abstract boolean hasSlots();

    abstract int slotArity();

    abstract Object getSlot(final int index);

    /**
     * The arity of this trie node (i.e. number of values and nodes stored on this level).
     *
     * @return sum of nodes and values stored within
     */
    abstract int arity();

    abstract int[] arities();

    int size() {
      final Iterator<K> it = new SetMultimapKeyIterator<>(this);

      int size = 0;
      while (it.hasNext()) {
        size += 1;
        it.next();
      }

      return size;
    }
  }

  protected static abstract class CompactSetMultimapNode<K, V>
      extends AbstractSetMultimapNode<K, V> {

    private long bitmap;

    private int cachedSlotArity;
    private int cachedNodeArity;
    private int cachedEmptyArity;

    @Deprecated
    final void initializeLazyFields() {
      // NOTE: temporariliy used to test caching of attributes; will be removed soon again

      // cachedSlotArity = (int) staticSlotArity();
      // cachedNodeArity = (int) Long.bitCount(filter(bitmap, PATTERN_NODE));
      // cachedEmptyArity = (int) Long.bitCount(filter(bitmap, PATTERN_EMPTY));
    }

    CompactSetMultimapNode(final AtomicReference<Thread> mutator, final long bitmap) {
      this.bitmap = bitmap;
      initializeLazyFields();
    }

    // TODO: removed `final` due to HashCollisionNode
    long bitmap() {
      return bitmap;
    }

    static final int HASH_CODE_LENGTH = 32;

    static final int BIT_PARTITION_SIZE = 5;
    static final int BIT_PARTITION_MASK = 0b11111;

    static final int mask(final int keyHash, final int shift) {
      return (keyHash >>> shift) & BIT_PARTITION_MASK;
    }

    static final int bitpos(final int mask) {
      return 1 << mask;
    }

    static final int doubledMask(int keyHash, int shift) {
      final int mask = mask(keyHash, shift);
      return mask << 1;
    }

    static final long doubledBitpos(final int doubledMask) {
      return 1L << doubledMask;
    }

    static final int pattern(long bitmap, int doubledMask) {
      return (int) ((bitmap >>> doubledMask) & 0b11);
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

    // static final Class[][] initializeSpecializationsByContentAndNodes() {
    // Class[][] next = new Class[33][65];
    //
    // try {
    // for (int m = 0; m <= 32; m++) {
    // for (int n = 0; n <= 64; n++) {
    // int mNext = m;
    // int nNext = n;
    //
    // // TODO: last expression is not properly generated yet and maybe incorrect
    // if (mNext < 0 || mNext > 32 || nNext < 0 || nNext > 64
    // || Math.ceil(nNext / 2.0) + mNext > 32) {
    // next[m][n] = null;
    // } else {
    // next[m][n] = Class.forName(String.format(
    // "io.usethesource.capsule.TrieSetMultimap_HHAMT_Specializations_Interlinked$SetMultimap%dTo%dNode",
    // mNext, nNext));
    // }
    // }
    // }
    // } catch (ClassNotFoundException e) {
    // throw new RuntimeException(e);
    // }
    //
    // return next;
    // }
    //
    // static final Class<? extends CompactSetMultimapNode>[][] specializationsByContentAndNodes =
    // initializeSpecializationsByContentAndNodes();

    static final Class[] initializeSpecializationsByContentAndNodes() {
      Class[] next = new Class[33 * 65];

      try {
        for (int m = 0; m <= 32; m++) {
          for (int n = 0; n <= 64; n++) {
            int mNext = m;
            int nNext = n;

            // TODO: last expression is not properly generated yet and maybe incorrect
            if (mNext < 0 || mNext > 32 || nNext < 0 || nNext > 64
                || Math.ceil(nNext / 2.0) + mNext > 32) {
              next[65 * m + n] = null;
            } else {
              next[65 * m + n] = Class.forName(String.format(
                  "io.usethesource.capsule.experimental.multimap.TrieSetMultimap_HHAMT_Specializations_Path_Interlinked$SetMultimap%dTo%dNode",
                  mNext, nNext));
            }
          }
        }
      } catch (ClassNotFoundException e) {
        throw new RuntimeException(e);
      }

      return next;
    }

    static final Class<? extends CompactSetMultimapNode>[] specializationsByContentAndNodes =
        initializeSpecializationsByContentAndNodes();

    static final <T> T allocateHeapRegion(final Class<? extends T>[] lookupTable, final int dim1,
        final int dim2) {
      final Class<? extends T> clazz = lookupTable[65 * dim1 + dim2];
      return RangecopyUtils.allocateHeapRegion(clazz);
    }

    // static final byte[] initializeMetadataByContentAndNodes() {
    // byte[] next = new byte[33 * 65 * 4];
    //
    // try {
    // for (int m = 0; m <= 32; m++) {
    // for (int n = 0; n <= 64; n++) {
    // int mNext = m;
    // int nNext = n;
    //
    // // TODO: last expression is not properly generated yet and maybe incorrect
    // if (mNext < 0 || mNext > 32 || nNext < 0 || nNext > 64
    // || Math.ceil(nNext / 2.0) + mNext > 32) {
    //// int section = (65 * m + n) * 4;
    //// next[65 * m + n] = 0;
    // } else {
    // Class clazz = Class.forName(String.format(
    // "io.usethesource.capsule.TrieSetMultimap_HHAMT_Specializations_Interlinked$SetMultimap%dTo%dNode",
    // mNext, nNext));
    //
    // int section = (65 * m + n) * 4;
    // next[section + 0] = (byte) unsafe.getLong(clazz, globalRareBaseOffset);
    // next[section + 1] = (byte) unsafe.getInt(clazz, globalPayloadArityOffset);
    // next[section + 2] = (byte) unsafe.getInt(clazz, globalUntypedSlotArityOffset);
    // next[section + 3] = (byte) unsafe.getInt(clazz, globalSlotArityOffset);
    //
    // if (next[section + 0] < 0)
    // System.out.println(next[section + 0]);
    // }
    // }
    // }
    // } catch (ClassNotFoundException e) {
    // throw new RuntimeException(e);
    // }
    //
    // return next;
    // }
    //
    // static final byte[] metadataByContentAndNodes =
    // initializeMetadataByContentAndNodes();

    static long globalRawMap1Offset = fieldOffset(SetMultimap0To2Node.class, "rawMap1");

    static long globalRawMap2Offset = fieldOffset(SetMultimap0To2Node.class, "rawMap2");

    static long globalArrayOffsetsOffset = fieldOffset(SetMultimap0To2Node.class, "arrayOffsets");

    static long globalNodeArityOffset = fieldOffset(SetMultimap0To2Node.class, "nodeArity");

    static long globalPayloadArityOffset = fieldOffset(SetMultimap0To2Node.class, "payloadArity");

    static long globalSlotArityOffset = fieldOffset(SetMultimap0To2Node.class, "slotArity");

    static long globalUntypedSlotArityOffset =
        fieldOffset(SetMultimap0To2Node.class, "untypedSlotArity");

    static long globalRareBaseOffset = fieldOffset(SetMultimap0To2Node.class, "rareBase");

    static long globalArrayOffsetLastOffset =
        fieldOffset(SetMultimap0To2Node.class, "arrayOffsetLast");

    static long globalNodeBaseOffset = fieldOffset(SetMultimap0To2Node.class, "nodeBase");

    private final long staticRareBase() {
      return unsafe.getLong(this.getClass(), globalRareBaseOffset);
    }

    private final int staticSlotArity() {
      return unsafe.getInt(this.getClass(), globalSlotArityOffset);
    }

    private final int staticUntypedSlotArity() {
      return unsafe.getInt(this.getClass(), globalUntypedSlotArityOffset);
    }

    private final int staticPayloadArity() {
      return unsafe.getInt(this.getClass(), globalPayloadArityOffset);
    }

    // @Deprecated
    // abstract int dataMap();
    //
    // @Deprecated
    // abstract int collMap();
    //
    // @Deprecated
    // abstract int nodeMap();

    // TODO: use final modifer when decoupling collision node from compact node
    @Override
    boolean hasPayload() {
      return payloadArity() != 0;
    }

    // TODO: use final modifer when decoupling collision node from compact node
    @Override
    int payloadArity() {
      return 32 - nodeArity() - emptyArity();
    }

    // TODO: use final modifer when decoupling collision node from compact node
    @Override
    boolean hasPayload(EitherSingletonOrCollection.Type type) {
      return payloadArity(type) != 0;
    }

    // TODO: use final modifer when decoupling collision node from compact node
    @Override
    int payloadArity(EitherSingletonOrCollection.Type type) {
      if (type == SINGLETON) {
        return arity(bitmap(), PATTERN_DATA_SINGLETON);
      } else {
        return arity(bitmap(), PATTERN_DATA_COLLECTION);
      }
    }

    // TODO: use final modifer when decoupling collision node from compact node
    @Override
    K getSingletonKey(final int index) {
      return (K) getFromObjectRegion(this, arrayBase, TUPLE_LENGTH * index);
    }

    // TODO: use final modifer when decoupling collision node from compact node
    @Override
    V getSingletonValue(final int index) {
      return (V) getFromObjectRegion(this, arrayBase, TUPLE_LENGTH * index + 1);
    }

    // TODO: use final modifer when decoupling collision node from compact node
    @Override
    K getCollectionKey(final int index) {
      return (K) getFromObjectRegion(this, staticRareBase(), TUPLE_LENGTH * index);
    }

    // TODO: use final modifer when decoupling collision node from compact node
    @Override
    AbstractSetNode<V> getCollectionValue(final int index) {
      return (AbstractSetNode<V>) getFromObjectRegion(this, staticRareBase(),
          TUPLE_LENGTH * index + 1);
    }

    // TODO: use final modifer when decoupling collision node from compact node
    @Override
    boolean hasSlots() {
      return slotArity() != 0;
    }

    // TODO: use final modifer when decoupling collision node from compact node
    @Override
    int slotArity() {
      return staticSlotArity();

      // return cachedSlotArity;
    }

    // TODO: use final modifer when decoupling collision node from compact node
    @Override
    Object getSlot(final int index) {
      return getFromObjectRegion(this, arrayBase, index);
    }

    // TODO: use final modifer when decoupling collision node from compact node
    @Override
    int emptyArity() {
      return Long.bitCount(filter(bitmap, PATTERN_EMPTY));
      // return arity(bitmap, PATTERN_EMPTY);

      // return cachedEmptyArity;
    }

    @Deprecated
    @Override
    int arity() {
      // TODO: replace with 32 - arity(emptyMap)
      // return arity(bitmap(), PATTERN_DATA_SINGLETON) + arity(bitmap(), PATTERN_DATA_COLLECTION) +
      // arity(bitmap(), PATTERN_NODE);

      int[] arities = arities(bitmap());
      return Arrays.stream(arities).skip(1).sum();
    }

    static final int arity(long bitmap, int pattern) {
      // if (bitmap == 0) {
      // if (pattern == PATTERN_EMPTY) {
      // return 32;
      // } else {
      // return 0;
      // }
      // } else {
      // return Long.bitCount(filter(bitmap, pattern));
      // }

      long filteredBitmap = filter(bitmap, pattern);

      // if (filteredBitmap == 0) {
      // if (pattern == PATTERN_EMPTY) {
      // return 32;
      // } else {
      // return 0;
      // }
      // }

      return Long.bitCount(filteredBitmap);
    }

    @Override
    public int[] arities() {
      return arities(bitmap);
    }

    static final int[] arities(final long bitmap) {
      int[] arities = new int[4];

      arities[0] = Long.bitCount(filter(bitmap, PATTERN_EMPTY));
      arities[1] = Long.bitCount(filter(bitmap, PATTERN_DATA_SINGLETON));
      arities[2] = Long.bitCount(filter(bitmap, PATTERN_DATA_COLLECTION));
      arities[3] = Long.bitCount(filter(bitmap, PATTERN_NODE));

      return arities;
    }

    static final int[] aritiesSingleLoopOverLong(final long bitmap) {
      int[] arities = new int[4];

      long shiftedBitmap = bitmap;
      for (int i = 0; i < 32; i++) {
        arities[(int) shiftedBitmap & 0b11]++;
        shiftedBitmap = shiftedBitmap >>> 2;
      }

      return arities;
    }

    static final int[] aritiesDoubleLoopOverInt(final long bitmap) {
      int[] arities = new int[4];

      int segment0 = (int) (bitmap >>> 32);
      int segment1 = (int) (bitmap);

      for (int i = 0; i < 16; i++) {
        arities[segment0 & 0b11]++;
        segment0 = segment0 >>> 2;
      }

      for (int i = 0; i < 16; i++) {
        arities[segment1 & 0b11]++;
        segment1 = segment1 >>> 2;
      }

      return arities;
    }

    static final byte SIZE_EMPTY = 0b00;
    static final byte SIZE_ONE = 0b01;
    static final byte SIZE_MORE_THAN_ONE = 0b10;

    /**
     * Abstract predicate over a node's size. Value can be either {@value #SIZE_EMPTY},
     * {@value #SIZE_ONE}, or {@value #SIZE_MORE_THAN_ONE}.
     *
     * @return size predicate
     */
    byte sizePredicate() {
      final long bitmap = this.bitmap();

      int nodeArity = arity(bitmap, PATTERN_NODE);
      int emptyArity = arity(bitmap, PATTERN_EMPTY);

      if (nodeArity > 0) {
        return SIZE_MORE_THAN_ONE;
      } else {
        switch (emptyArity) {
          case 32:
            return SIZE_EMPTY;
          case 31:
            return SIZE_ONE;
          default:
            return SIZE_MORE_THAN_ONE;
        }
      }
    }

    @Override
    boolean hasNodes() {
      return nodeArity() != 0;
    }

    // TODO: use final modifer when decoupling collision node from compact node
    @Override
    int nodeArity() {
      return Long.bitCount(filter(bitmap, PATTERN_NODE));
      // return arity(bitmap, PATTERN_NODE);

      // return cachedNodeArity;
    }

    // TODO: use final modifer when decoupling collision node from compact node
    @Override
    CompactSetMultimapNode<K, V> getNode(final int index) {
      final int pIndex = slotArity() - 1 - index;

      return (CompactSetMultimapNode<K, V>) getSlot(pIndex);

      // final long rareBase = staticRareBase();
      //
      // final int untypedSlotArity = staticUntypedSlotArity();
      // final int pIndex = untypedSlotArity - 1 - index;
      //
      // return (CompactSetMultimapNode<K, V>) getFromObjectRegion(this, rareBase, pIndex);
    }

    void assertNodeInvariant() {
      // return true;
      // boolean inv1 = (size() - payloadArity() >= 2 * (arity() - payloadArity()));
      // boolean inv2 = (this.arity() == 0) ? sizePredicate() == SIZE_EMPTY : true;
      // boolean inv3 =
      // (this.arity() == 1 && payloadArity() == 1) ? sizePredicate() == SIZE_ONE : true;
      // boolean inv4 = (this.arity() >= 2) ? sizePredicate() == SIZE_MORE_THAN_ONE : true;
      //
      // boolean inv5 = (this.nodeArity() >= 0) && (this.payloadArity() >= 0)
      // && ((this.payloadArity() + this.nodeArity()) == this.arity());
      //
      // return inv1 && inv2 && inv3 && inv4 && inv5;

      // if (DEBUG) {
      int[] arities = arities(bitmap);

      assert (TUPLE_LENGTH * arities[PATTERN_DATA_SINGLETON]
          + TUPLE_LENGTH * arities[PATTERN_DATA_COLLECTION] + arities[PATTERN_NODE] == slotArity());

      for (int i = 0; i < arities[PATTERN_DATA_SINGLETON]; i++) {
        int offset = i * TUPLE_LENGTH;

        assert ((getSlot(offset + 0) instanceof AbstractSetNode) == false);
        assert ((getSlot(offset + 1) instanceof AbstractSetNode) == false);

        assert ((getSlot(offset + 0) instanceof CompactSetMultimapNode) == false);
        assert ((getSlot(offset + 1) instanceof CompactSetMultimapNode) == false);
      }

      for (int i = 0; i < arities[PATTERN_DATA_COLLECTION]; i++) {
        int offset = (i + arities[PATTERN_DATA_SINGLETON]) * TUPLE_LENGTH;

        assert ((getSlot(offset + 0) instanceof AbstractSetNode) == false);
        assert ((getSlot(offset + 1) instanceof AbstractSetNode) == true);

        assert ((getSlot(offset + 0) instanceof CompactSetMultimapNode) == false);
        assert ((getSlot(offset + 1) instanceof CompactSetMultimapNode) == false);
      }

      for (int i = 0; i < arities[PATTERN_NODE]; i++) {
        int offset =
            (arities[PATTERN_DATA_SINGLETON] + arities[PATTERN_DATA_COLLECTION]) * TUPLE_LENGTH;

        assert ((getSlot(offset + i) instanceof AbstractSetNode) == false);

        assert ((getSlot(offset + i) instanceof CompactSetMultimapNode) == true);
      }
    }
    // }

    CompactSetMultimapNode<K, V> copyAndUpdateBitmaps(AtomicReference<Thread> mutator,
        final long updatedBitmap) {
      final Class<? extends CompactSetMultimapNode> srcClass = this.getClass();

      final CompactSetMultimapNode src = this;
      final CompactSetMultimapNode dst = RangecopyUtils.allocateHeapRegion(srcClass);

      // copy and update bitmaps
      dst.bitmap = updatedBitmap;

      rangecopyObjectRegion(src, dst, arrayBase, staticSlotArity());

      // dst.assertNodeInvariant();
      // dst.initializeLazyFields();
      return dst;
    }

    CompactSetMultimapNode<K, V> copyAndSetSingletonValue(final AtomicReference<Thread> mutator,
        final long doubledBitpos, final V val) {
      final int index = dataIndex(doubledBitpos);

      final Class<? extends CompactSetMultimapNode> srcClass = this.getClass();

      final CompactSetMultimapNode src = this;
      final CompactSetMultimapNode dst = RangecopyUtils.allocateHeapRegion(srcClass);

      // copy and update bitmaps
      dst.bitmap = bitmap;

      final int slotArity = staticSlotArity();
      int pIndex = TUPLE_LENGTH * index + 1;

      rangecopyObjectRegion(src, arrayBase, dst, arrayBase, slotArity);
      setInObjectRegion(dst, arrayBase, pIndex, val);

      // dst.assertNodeInvariant();
      // dst.initializeLazyFields();
      return dst;
    }

    CompactSetMultimapNode<K, V> copyAndSetCollectionValue(final AtomicReference<Thread> mutator,
        final long doubledBitpos, final AbstractSetNode<V> valColl) {
      final int index = collIndex(doubledBitpos);

      final Class<? extends CompactSetMultimapNode> srcClass = this.getClass();

      final CompactSetMultimapNode src = this;
      final CompactSetMultimapNode dst = RangecopyUtils.allocateHeapRegion(srcClass);

      // copy and update bitmaps
      dst.bitmap = bitmap;

      final int slotArity = staticSlotArity();
      int pIndex = TUPLE_LENGTH * index + 1;

      rangecopyObjectRegion(src, arrayBase, dst, arrayBase, slotArity);
      setInObjectRegion(dst, staticRareBase(), pIndex, valColl);

      // dst.assertNodeInvariant();
      // dst.initializeLazyFields();
      return dst;
    }

    static final <K, V> CompactSetMultimapNode<K, V> allocateHeapRegionAndSetBitmap(
        final Class<? extends CompactSetMultimapNode> clazz, final long bitmap) {
      try {
        final CompactSetMultimapNode<K, V> newInstance =
            (CompactSetMultimapNode<K, V>) unsafe.allocateInstance(clazz);
        newInstance.bitmap = bitmap;
        return newInstance;
      } catch (ClassCastException | InstantiationException e) {
        throw new RuntimeException(e);
      }
    }

    CompactSetMultimapNode<K, V> copyAndSetNode(final AtomicReference<Thread> mutator,
        final int index, final CompactSetMultimapNode<K, V> node) {
      final Class<? extends CompactSetMultimapNode> srcClass = this.getClass();

      final CompactSetMultimapNode src = this;
      final CompactSetMultimapNode dst = RangecopyUtils.allocateHeapRegion(srcClass);

      // copy and update bitmaps
      dst.bitmap = bitmap;

      final int slotArity = staticSlotArity();
      final int pIndex = slotArity - 1 - index;

      // single copy spanning over all references
      rangecopyObjectRegion(src, dst, arrayBase, slotArity);
      setInObjectRegion(dst, arrayBase, pIndex, node);

      // dst.assertNodeInvariant();
      // dst.initializeLazyFields();
      return dst;
    }

    CompactSetMultimapNode<K, V> copyAndInsertSingleton(final AtomicReference<Thread> mutator,
        final long doubledBitpos, final K key, final V val) {
      final int index = dataIndex(doubledBitpos);

      final Class<? extends CompactSetMultimapNode> srcClass = this.getClass();

      // TODO: introduce slotArity constant in specializations
      // final int slotArity = unsafe.getInt(srcClass, globalSlotArityOffset);
      final int payloadArity = staticPayloadArity();
      final int untypedSlotArity = staticUntypedSlotArity();

      final int slotArity = TUPLE_LENGTH * payloadArity + untypedSlotArity;

      final CompactSetMultimapNode src = this;
      final CompactSetMultimapNode dst =
          allocateHeapRegion(specializationsByContentAndNodes, payloadArity + 1, untypedSlotArity);

      dst.bitmap = setBitPattern(bitmap, doubledBitpos, PATTERN_DATA_SINGLETON);

      final int pIndex = TUPLE_LENGTH * index;

      long offset = arrayBase;
      long delta = 0;

      offset += rangecopyObjectRegion(src, dst, offset, pIndex);
      delta += setInObjectRegionVarArgs(dst, offset, key, val);
      offset += rangecopyObjectRegion(src, offset, dst, offset + delta, slotArity - pIndex);

      // dst.assertNodeInvariant();
      // dst.initializeLazyFields();
      return dst;
    }

    CompactSetMultimapNode<K, V> copyAndMigrateFromSingletonToCollection(
        final AtomicReference<Thread> mutator, final long doubledBitpos, final int indexOld,
        final K key, final AbstractSetNode<V> valColl) {
      // final int indexOld = dataIndex(doubledBitpos);
      final int indexNew = collIndex(doubledBitpos);

      final Class<? extends CompactSetMultimapNode> srcClass = this.getClass();

      // TODO: introduce slotArity constant in specializations
      // final int slotArity = unsafe.getInt(srcClass, globalSlotArityOffset);
      final int payloadArity = staticPayloadArity();
      final int untypedSlotArity = staticUntypedSlotArity();

      final int slotArity = TUPLE_LENGTH * payloadArity + untypedSlotArity;

      final CompactSetMultimapNode src = this;
      final CompactSetMultimapNode dst = allocateHeapRegion(specializationsByContentAndNodes,
          payloadArity - 1, untypedSlotArity + 2);

      dst.bitmap = setBitPattern(bitmap, doubledBitpos, PATTERN_DATA_COLLECTION);

      final int pIndexOld = TUPLE_LENGTH * indexOld;
      final int pIndexNew = TUPLE_LENGTH * (payloadArity - 1 + indexNew);

      /* TODO: test code below; not sure that length arguments are correct */

      long offset = arrayBase;
      long delta2 = addressSize * 2;

      offset += rangecopyObjectRegion(src, dst, offset, pIndexOld);
      offset += rangecopyObjectRegion(src, offset + delta2, dst, offset, pIndexNew - pIndexOld);
      setInObjectRegionVarArgs(dst, offset, key, valColl);
      offset += rangecopyObjectRegion(src, dst, offset + delta2, slotArity - pIndexNew - 2);

      // dst.assertNodeInvariant();
      // dst.initializeLazyFields();
      return dst;
    }

    CompactSetMultimapNode<K, V> copyAndRemoveSingleton(final AtomicReference<Thread> mutator,
        final long doubledBitpos) {
      final int indexOld = dataIndex(doubledBitpos);

      final Class<? extends CompactSetMultimapNode> srcClass = this.getClass();

      final int payloadArity = staticPayloadArity();
      final int untypedSlotArity = staticUntypedSlotArity();

      if (payloadArity == 1 && untypedSlotArity == 0) {
        // TODO: check if this optimization can be performed in caller
        return EMPTY_NODE;
      } else {
        final CompactSetMultimapNode src = this;
        final CompactSetMultimapNode dst = allocateHeapRegion(specializationsByContentAndNodes,
            payloadArity - 1, untypedSlotArity);

        dst.bitmap = setBitPattern(bitmap, doubledBitpos, PATTERN_EMPTY);

        final int pIndexOld = TUPLE_LENGTH * indexOld;

        long offset = arrayBase;
        offset += rangecopyObjectRegion(src, dst, offset, pIndexOld);
        long delta = 2 * addressSize /* sizeOfInt() */;
        offset += rangecopyObjectRegion(src, offset + delta, dst, offset,
            (TUPLE_LENGTH * (payloadArity - 1) - pIndexOld + untypedSlotArity));

        // dst.assertNodeInvariant();
        // dst.initializeLazyFields();
        return dst;
      }
    }

    /*
     * Batch updated, necessary for removedAll.
     */
    CompactSetMultimapNode<K, V> copyAndRemoveCollection(final AtomicReference<Thread> mutator,
        final long doubledBitpos) {
      final int indexOld = collIndex(doubledBitpos);

      final Class<? extends CompactSetMultimapNode> srcClass = this.getClass();

      final int payloadArity = staticPayloadArity();
      final int untypedSlotArity = staticUntypedSlotArity();

      if (payloadArity == 0 && untypedSlotArity == TUPLE_LENGTH) {
        // TODO: check if this optimization can be performed in caller
        return EMPTY_NODE;
      } else {
        final CompactSetMultimapNode src = this;
        final CompactSetMultimapNode dst = allocateHeapRegion(specializationsByContentAndNodes,
            payloadArity, untypedSlotArity - TUPLE_LENGTH);

        dst.bitmap = setBitPattern(bitmap, doubledBitpos, PATTERN_EMPTY);

        final int pIndexOld = TUPLE_LENGTH * (payloadArity + indexOld);

        long offset = arrayBase;
        offset += rangecopyObjectRegion(src, dst, offset, pIndexOld);
        long delta = 2 * addressSize /* sizeOfInt() */;
        offset += rangecopyObjectRegion(src, offset + delta, dst, offset,
            (TUPLE_LENGTH * (payloadArity - 1) - pIndexOld + untypedSlotArity));

        // dst.assertNodeInvariant();
        // dst.initializeLazyFields();
        return dst;
      }
    }

    CompactSetMultimapNode<K, V> copyAndMigrateFromSingletonToNode(
        final AtomicReference<Thread> mutator, final long doubledBitpos, final int indexOld,
        final CompactSetMultimapNode<K, V> node) {
      // final int indexOld = dataIndex(doubledBitpos);
      final int indexNew = nodeIndex(doubledBitpos);

      final Class<? extends CompactSetMultimapNode> srcClass = this.getClass();

      // TODO: introduce slotArity constant in specializations
      // final int slotArity = unsafe.getInt(srcClass, globalSlotArityOffset);
      final int payloadArity = staticPayloadArity();
      final int untypedSlotArity = staticUntypedSlotArity();

      final int slotArity = TUPLE_LENGTH * payloadArity + untypedSlotArity;

      final CompactSetMultimapNode src = this;
      final CompactSetMultimapNode dst = allocateHeapRegion(specializationsByContentAndNodes,
          payloadArity - 1, untypedSlotArity + 1);

      dst.bitmap = setBitPattern(bitmap, doubledBitpos, PATTERN_NODE);

      final int pIndexOld = TUPLE_LENGTH * indexOld;
      final int pIndexNew = (slotArity - 1) - 1 - indexNew;

      copyAndMigrateFromXxxToNode(src, dst, slotArity, pIndexOld, pIndexNew, node);

      // dst.assertNodeInvariant();
      // dst.initializeLazyFields();
      return dst;
    }

    private void copyAndMigrateFromXxxToNode(final CompactSetMultimapNode src,
        final CompactSetMultimapNode dst, final int slotArity, final int pIndexOld,
        final int pIndexNew, final CompactSetMultimapNode<K, V> node) {
      long offset = arrayBase;
      long delta1 = addressSize;
      long delta2 = 2 * addressSize;

      offset += rangecopyObjectRegion(src, dst, offset, pIndexOld);
      offset += rangecopyObjectRegion(src, offset + delta2, dst, offset, pIndexNew - pIndexOld);

      setInObjectRegionVarArgs(dst, offset, node);

      offset += rangecopyObjectRegion(src, offset + delta2, dst, offset + delta1,
          slotArity - pIndexNew - 2);
    }

    CompactSetMultimapNode<K, V> copyAndMigrateFromCollectionToNode(
        final AtomicReference<Thread> mutator, final long doubledBitpos, final int indexOld,
        final CompactSetMultimapNode<K, V> node) {
      // final int indexOld = collIndex(doubledBitpos);
      final int indexNew = nodeIndex(doubledBitpos);

      final Class<? extends CompactSetMultimapNode> srcClass = this.getClass();

      // TODO: introduce slotArity constant in specializations
      // final int slotArity = unsafe.getInt(srcClass, globalSlotArityOffset);
      final int payloadArity = staticPayloadArity();
      final int untypedSlotArity = staticUntypedSlotArity();

      final int slotArity = TUPLE_LENGTH * payloadArity + untypedSlotArity;

      final CompactSetMultimapNode src = this;
      final CompactSetMultimapNode dst = allocateHeapRegion(specializationsByContentAndNodes,
          payloadArity, untypedSlotArity - TUPLE_LENGTH + 1);

      dst.bitmap = setBitPattern(bitmap, doubledBitpos, PATTERN_NODE);

      final int pIndexOld = TUPLE_LENGTH * (payloadArity + indexOld);
      final int pIndexNew = (slotArity - 1) - 1 - indexNew;

      copyAndMigrateFromXxxToNode(src, dst, slotArity, pIndexOld, pIndexNew, node);

      // dst.assertNodeInvariant();
      // dst.initializeLazyFields();
      return dst;
    }

    CompactSetMultimapNode<K, V> copyAndMigrateFromNodeToSingleton(
        final AtomicReference<Thread> mutator, final long doubledBitpos,
        final CompactSetMultimapNode<K, V> node) { // node get's unwrapped inside method

      final int indexOld = nodeIndex(doubledBitpos);
      final int indexNew = dataIndex(doubledBitpos);

      final Class<? extends CompactSetMultimapNode> srcClass = this.getClass();

      // TODO: introduce slotArity constant in specializations
      // final int slotArity = unsafe.getInt(srcClass, globalSlotArityOffset);
      final int payloadArity = staticPayloadArity();
      final int untypedSlotArity = staticUntypedSlotArity();

      final int slotArity = TUPLE_LENGTH * payloadArity + untypedSlotArity;

      final CompactSetMultimapNode src = this;
      final CompactSetMultimapNode dst = allocateHeapRegion(specializationsByContentAndNodes,
          payloadArity + 1, untypedSlotArity - 1);

      dst.bitmap = setBitPattern(bitmap, doubledBitpos, PATTERN_DATA_SINGLETON);

      final int pIndexOld = slotArity - 1 - indexOld;
      final int pIndexNew = TUPLE_LENGTH * indexNew;

      Object keyToInline = node.getSingletonKey(0);
      Object valToInline = node.getSingletonValue(0);

      copyAndMigrateFromNodeToXxx(src, dst, slotArity, pIndexOld, pIndexNew, keyToInline,
          valToInline);

      // dst.assertNodeInvariant();
      // dst.initializeLazyFields();
      return dst;
    }

    CompactSetMultimapNode<K, V> copyAndMigrateFromNodeToCollection(
        final AtomicReference<Thread> mutator, final long doubledBitpos,
        final CompactSetMultimapNode<K, V> node) { // node get's unwrapped inside method

      final int indexOld = nodeIndex(doubledBitpos);
      final int indexNew = collIndex(doubledBitpos);

      final Class<? extends CompactSetMultimapNode> srcClass = this.getClass();

      // TODO: introduce slotArity constant in specializations
      // final int slotArity = unsafe.getInt(srcClass, globalSlotArityOffset);
      final int payloadArity = staticPayloadArity();
      final int untypedSlotArity = staticUntypedSlotArity();

      final int slotArity = TUPLE_LENGTH * payloadArity + untypedSlotArity;

      final CompactSetMultimapNode src = this;
      final CompactSetMultimapNode dst = allocateHeapRegion(specializationsByContentAndNodes,
          payloadArity, untypedSlotArity - 1 + TUPLE_LENGTH);

      dst.bitmap = setBitPattern(bitmap, doubledBitpos, PATTERN_DATA_COLLECTION);

      final int pIndexOld = slotArity - 1 - indexOld;
      final int pIndexNew = TUPLE_LENGTH * indexNew;

      Object keyToInline = node.getCollectionKey(0);
      Object valToInline = node.getCollectionValue(0);

      copyAndMigrateFromNodeToXxx(src, dst, slotArity, pIndexOld, pIndexNew, keyToInline,
          valToInline);

      // dst.assertNodeInvariant();
      // dst.initializeLazyFields();
      return dst;
    }

    private void copyAndMigrateFromNodeToXxx(final CompactSetMultimapNode src,
        final CompactSetMultimapNode dst, final int slotArity, final int pIndexOld,
        final int pIndexNew, Object keyToInline, Object valToInline) {
      long offset = arrayBase;
      long delta1 = addressSize;
      long delta2 = delta1 * 2;

      offset += rangecopyObjectRegion(src, dst, offset, pIndexNew);
      setInObjectRegionVarArgs(dst, offset, keyToInline, valToInline);
      offset += rangecopyObjectRegion(src, offset, dst, offset + delta2, pIndexOld - pIndexNew);
      offset += rangecopyObjectRegion(src, offset + delta1, dst, offset + delta2,
          slotArity - pIndexOld - 1);
    }

    CompactSetMultimapNode<K, V> copyAndMigrateFromCollectionToSingleton(
        final AtomicReference<Thread> mutator, final long doubledBitpos, final K key, final V val) {

      // TODO: does not support src == dst yet for shifting

      final int indexOld = collIndex(doubledBitpos);
      final int indexNew = dataIndex(doubledBitpos);

      final Class<? extends CompactSetMultimapNode> srcClass = this.getClass();

      // TODO: introduce slotArity constant in specializations
      // final int slotArity = unsafe.getInt(srcClass, globalSlotArityOffset);
      final int payloadArity = staticPayloadArity();
      final int untypedSlotArity = staticUntypedSlotArity();

      final int slotArity = TUPLE_LENGTH * payloadArity + untypedSlotArity;

      final CompactSetMultimapNode src = this;
      final CompactSetMultimapNode dst = allocateHeapRegion(specializationsByContentAndNodes,
          payloadArity + 1, untypedSlotArity - 2);

      dst.bitmap = setBitPattern(bitmap, doubledBitpos, PATTERN_DATA_SINGLETON);

      final int pIndexOld = TUPLE_LENGTH * (payloadArity + indexOld);
      final int pIndexNew = TUPLE_LENGTH * indexNew;

      long offset = arrayBase;
      long delta2 = addressSize * 2;

      offset += rangecopyObjectRegion(src, dst, offset, pIndexNew);
      setInObjectRegionVarArgs(dst, offset, key, val);
      offset += rangecopyObjectRegion(src, offset, dst, offset + delta2, pIndexOld - pIndexNew);
      offset += rangecopyObjectRegion(src, dst, offset + delta2, slotArity - pIndexOld - 2);

      // dst.assertNodeInvariant();
      // dst.initializeLazyFields();
      return dst;
    }

    // TODO: fix hash collision support
    static final <K, V> CompactSetMultimapNode<K, V> mergeTwoSingletonPairs(final K key0,
        final V val0, final int keyHash0, final K key1, final V val1, final int keyHash1,
        final int shift, EqualityComparator<Object> cmp) {
      assert !(cmp.equals(key0, key1));

      if (shift >= HASH_CODE_LENGTH) {
        return AbstractHashCollisionNode.of(keyHash0, key0, specSetOf(val0), key1, specSetOf(val1));
      }

      final int mask0 = doubledMask(keyHash0, shift);
      final int mask1 = doubledMask(keyHash1, shift);

      if (mask0 != mask1) {
        // both nodes fit on same level
        long bitmap = 0L;
        bitmap = setBitPattern(bitmap, doubledBitpos(mask0), PATTERN_DATA_SINGLETON);
        bitmap = setBitPattern(bitmap, doubledBitpos(mask1), PATTERN_DATA_SINGLETON);

        if (mask0 < mask1) {
          return nodeOf0x2(null, bitmap, key0, val0, key1, val1);
        } else {
          return nodeOf0x2(null, bitmap, key1, val1, key0, val0);
        }
      } else {
        final CompactSetMultimapNode<K, V> node = mergeTwoSingletonPairs(key0, val0, keyHash0, key1,
            val1, keyHash1, shift + BIT_PARTITION_SIZE, cmp);
        // values fit on next level
        final long bitmap = setBitPattern(0L, doubledBitpos(mask0), PATTERN_NODE);

        return nodeOf1x0(null, bitmap, node);
      }
    }

    // TODO: fix hash collision support
    static final <K, V> CompactSetMultimapNode<K, V> mergeCollectionAndSingletonPairs(final K key0,
        final AbstractSetNode<V> valColl0, final int keyHash0, final K key1, final V val1,
        final int keyHash1, final int shift, EqualityComparator<Object> cmp) {
      assert !(cmp.equals(key0, key1));

      if (shift >= HASH_CODE_LENGTH) {
        return AbstractHashCollisionNode.of(keyHash0, key0, setFromNode(valColl0), key1,
            specSetOf(val1));
      }

      final int mask0 = doubledMask(keyHash0, shift);
      final int mask1 = doubledMask(keyHash1, shift);

      if (mask0 != mask1) {
        // both nodes fit on same level
        long bitmap = 0L;
        bitmap = setBitPattern(bitmap, doubledBitpos(mask0), PATTERN_DATA_COLLECTION);
        bitmap = setBitPattern(bitmap, doubledBitpos(mask1), PATTERN_DATA_SINGLETON);

        // singleton before collection
        return nodeOf2x1(null, bitmap, key1, val1, key0, valColl0);
      } else {
        final CompactSetMultimapNode<K, V> node = mergeCollectionAndSingletonPairs(key0, valColl0,
            keyHash0, key1, val1, keyHash1, shift + BIT_PARTITION_SIZE, cmp);
        // values fit on next level
        final long bitmap = setBitPattern(0L, doubledBitpos(mask0), PATTERN_NODE);

        return nodeOf1x0(null, bitmap, node);
      }
    }

    // static final int index(final int bitmap, final int bitpos) {
    // return java.lang.Integer.bitCount(bitmap & (bitpos - 1));
    // }
    //
    // static final int index(final int bitmap, final int mask, final int bitpos) {
    // return (bitmap == -1) ? mask : index(bitmap, bitpos);
    // }

    @Deprecated
    int dataIndex(final long doubledBitpos) {
      return index(bitmap, PATTERN_DATA_SINGLETON, doubledBitpos);
    }

    @Deprecated
    int collIndex(final long doubledBitpos) {
      return index(bitmap, PATTERN_DATA_COLLECTION, doubledBitpos);
    }

    @Deprecated
    int nodeIndex(final long doubledBitpos) {
      return index(bitmap, PATTERN_NODE, doubledBitpos);
    }

    @Override
    boolean containsKey(final K key, final int keyHash, final int shift,
        EqualityComparator<Object> cmp) {
      long bitmap = this.bitmap();

      final int doubledMask = doubledMask(keyHash, shift);
      final int pattern = pattern(bitmap, doubledMask);

      final long doubledBitpos = doubledBitpos(doubledMask);

      switch (pattern) {
        case PATTERN_NODE: {
          int index = index(bitmap, PATTERN_NODE, doubledBitpos);
          return getNode(index).containsKey(key, keyHash, shift + BIT_PARTITION_SIZE, cmp);
        }
        case PATTERN_DATA_SINGLETON: {
          int index = index(bitmap, PATTERN_DATA_SINGLETON, doubledBitpos);
          return cmp.equals(getSingletonKey(index), key);
        }
        case PATTERN_DATA_COLLECTION: {
          int index = index(bitmap, PATTERN_DATA_COLLECTION, doubledBitpos);
          return cmp.equals(getCollectionKey(index), key);
        }
        default:
          return false;
      }
    }

    @Override
    boolean containsTuple(final K key, final V val, final int keyHash, final int shift,
        EqualityComparator<Object> cmp) {
      long bitmap = this.bitmap();

      final int doubledMask = doubledMask(keyHash, shift);
      final int pattern = pattern(bitmap, doubledMask);

      final long doubledBitpos = doubledBitpos(doubledMask);

      switch (pattern) {
        case PATTERN_NODE: {
          int index = index(bitmap, PATTERN_NODE, doubledBitpos);

          final AbstractSetMultimapNode<K, V> subNode = getNode(index);
          return subNode.containsTuple(key, val, keyHash, shift + BIT_PARTITION_SIZE, cmp);
        }
        case PATTERN_DATA_SINGLETON: {
          int index = index(bitmap, PATTERN_DATA_SINGLETON, doubledBitpos);

          final K currentKey = getSingletonKey(index);
          if (cmp.equals(currentKey, key)) {

            final V currentVal = getSingletonValue(index);
            return cmp.equals(currentVal, val);
          }

          return false;
        }
        case PATTERN_DATA_COLLECTION: {
          int index = index(bitmap, PATTERN_DATA_COLLECTION, doubledBitpos);

          final K currentKey = getCollectionKey(index);
          if (cmp.equals(currentKey, key)) {

            final AbstractSetNode<V> currentValColl = getCollectionValue(index);
            return currentValColl.contains(val, val.hashCode(), 0);
          }

          return false;
        }
        default:
          return false;
      }
    }

    @Override
    Optional<AbstractSetNode<V>> findByKey(final K key, final int keyHash, final int shift,
        EqualityComparator<Object> cmp) {
      long bitmap = this.bitmap();

      final int doubledMask = doubledMask(keyHash, shift);
      final int pattern = pattern(bitmap, doubledMask);

      final long doubledBitpos = doubledBitpos(doubledMask);

      switch (pattern) {
        case PATTERN_NODE: {
          int index = index(bitmap, PATTERN_NODE, doubledBitpos);

          final AbstractSetMultimapNode<K, V> subNode = getNode(index);
          return subNode.findByKey(key, keyHash, shift + BIT_PARTITION_SIZE, cmp);
        }
        case PATTERN_DATA_SINGLETON: {
          int index = index(bitmap, PATTERN_DATA_SINGLETON, doubledBitpos);

          final K currentKey = getSingletonKey(index);
          if (cmp.equals(currentKey, key)) {

            final V currentVal = getSingletonValue(index);
            return Optional.of(specSetNodeOf(currentVal));
          }

          return Optional.empty();
        }
        case PATTERN_DATA_COLLECTION: {
          int index = index(bitmap, PATTERN_DATA_COLLECTION, doubledBitpos);

          final K currentKey = getCollectionKey(index);
          if (cmp.equals(currentKey, key)) {

            final AbstractSetNode<V> currentValColl = getCollectionValue(index);
            return Optional.of(currentValColl);
          }

          return Optional.empty();
        }
        default:
          return Optional.empty();
      }
    }

    @Override
    CompactSetMultimapNode<K, V> inserted(final AtomicReference<Thread> mutator, final K key,
        final V val, final int keyHash, final int shift, final SetMultimapResult<K, V> details,
        EqualityComparator<Object> cmp) {
      long bitmap = this.bitmap();

      final int doubledMask = doubledMask(keyHash, shift);
      final int pattern = pattern(bitmap, doubledMask);

      final long doubledBitpos = doubledBitpos(doubledMask);

      switch (pattern) {
        case PATTERN_NODE: {
          int nodeIndex = index(bitmap, PATTERN_NODE, doubledBitpos);
          final CompactSetMultimapNode<K, V> subNode = getNode(nodeIndex);
          final CompactSetMultimapNode<K, V> subNodeNew = subNode.inserted(mutator, key, val,
              keyHash, shift + BIT_PARTITION_SIZE, details, cmp);

          if (details.isModified()) {
            return copyAndSetNode(mutator, nodeIndex, subNodeNew);
          } else {
            return this;
          }
        }
        case PATTERN_DATA_SINGLETON: {
          int dataIndex = index(bitmap, PATTERN_DATA_SINGLETON, doubledBitpos);
          final K currentKey = getSingletonKey(dataIndex);

          if (cmp.equals(currentKey, key)) {
            final V currentVal = getSingletonValue(dataIndex);

            if (cmp.equals(currentVal, val)) {
              return this;
            } else {
              // migrate from singleton to collection
              final AbstractSetNode<V> valColl = specSetNodeOf(currentVal, val);

              details.modified();
              return copyAndMigrateFromSingletonToCollection(mutator, doubledBitpos, dataIndex,
                  currentKey, valColl);
            }
          } else {
            // prefix-collision (case: singleton x singleton)
            final V currentVal = getSingletonValue(dataIndex);

            final CompactSetMultimapNode<K, V> subNodeNew = mergeTwoSingletonPairs(currentKey,
                currentVal, transformHashCode(currentKey.hashCode()), key, val, keyHash,
                shift + BIT_PARTITION_SIZE, cmp);

            details.modified();
            return copyAndMigrateFromSingletonToNode(mutator, doubledBitpos, dataIndex, subNodeNew);
          }
        }
        case PATTERN_DATA_COLLECTION: {
          int collIndex = index(bitmap, PATTERN_DATA_COLLECTION, doubledBitpos);
          final K currentCollKey = getCollectionKey(collIndex);

          if (cmp.equals(currentCollKey, key)) {
            final AbstractSetNode<V> currentCollVal = getCollectionValue(collIndex);

            if (currentCollVal.contains(val, val.hashCode(), 0)) {
              return this;
            } else {
              // add new mapping
              final AbstractSetNode<V> newCollVal =
                  currentCollVal.updated(mutator, val, val.hashCode(), 0, SetResult.unchanged());

              details.modified();
              return copyAndSetCollectionValue(mutator, doubledBitpos, newCollVal);
            }
          } else {
            // prefix-collision (case: collection x singleton)
            final AbstractSetNode<V> currentValNode = getCollectionValue(collIndex);
            final CompactSetMultimapNode<K, V> subNodeNew = mergeCollectionAndSingletonPairs(
                currentCollKey, currentValNode, transformHashCode(currentCollKey.hashCode()), key,
                val, keyHash, shift + BIT_PARTITION_SIZE, cmp);

            details.modified();
            return copyAndMigrateFromCollectionToNode(mutator, doubledBitpos, collIndex,
                subNodeNew);
          }
        }
        default: {
          details.modified();
          return copyAndInsertSingleton(mutator, doubledBitpos, key, val);
        }
      }
    }

    @Override
    CompactSetMultimapNode<K, V> updated(final AtomicReference<Thread> mutator, final K key,
        final V val, final int keyHash, final int shift, final SetMultimapResult<K, V> details,
        EqualityComparator<Object> cmp) {
      long bitmap = this.bitmap();

      final int doubledMask = doubledMask(keyHash, shift);
      final int pattern = pattern(bitmap, doubledMask);

      final long doubledBitpos = doubledBitpos(doubledMask);

      switch (pattern) {
        case PATTERN_NODE: {
          int nodeIndex = index(bitmap, PATTERN_NODE, doubledBitpos);
          final CompactSetMultimapNode<K, V> subNode = getNode(nodeIndex);
          final CompactSetMultimapNode<K, V> subNodeNew =
              subNode.updated(mutator, key, val, keyHash, shift + BIT_PARTITION_SIZE, details, cmp);

          if (details.isModified()) {
            return copyAndSetNode(mutator, nodeIndex, subNodeNew);
          } else {
            return this;
          }
        }
        case PATTERN_DATA_SINGLETON: {
          int dataIndex = index(bitmap, PATTERN_DATA_SINGLETON, doubledBitpos);
          final K currentKey = getSingletonKey(dataIndex);

          if (cmp.equals(currentKey, key)) {
            final V currentVal = getSingletonValue(dataIndex);

            // update singleton value
            details.updated(currentVal);
            return copyAndSetSingletonValue(mutator, doubledBitpos, val);
          } else {
            // prefix-collision (case: singleton x singleton)
            final V currentVal = getSingletonValue(dataIndex);

            final CompactSetMultimapNode<K, V> subNodeNew = mergeTwoSingletonPairs(currentKey,
                currentVal, transformHashCode(currentKey.hashCode()), key, val, keyHash,
                shift + BIT_PARTITION_SIZE, cmp);

            details.modified();
            return copyAndMigrateFromSingletonToNode(mutator, doubledBitpos, dataIndex, subNodeNew);
          }
        }
        case PATTERN_DATA_COLLECTION: {
          int collIndex = index(bitmap, PATTERN_DATA_COLLECTION, doubledBitpos);
          final K currentCollKey = getCollectionKey(collIndex);

          if (cmp.equals(currentCollKey, key)) {
            final AbstractSetNode<V> currentCollVal = getCollectionValue(collIndex);

            // migrate from collection to singleton
            details.updated(currentCollVal);
            return copyAndMigrateFromCollectionToSingleton(mutator, doubledBitpos, currentCollKey,
                val);
          } else {
            // prefix-collision (case: collection x singleton)
            final AbstractSetNode<V> currentValNode = getCollectionValue(collIndex);
            final CompactSetMultimapNode<K, V> subNodeNew = mergeCollectionAndSingletonPairs(
                currentCollKey, currentValNode, transformHashCode(currentCollKey.hashCode()), key,
                val, keyHash, shift + BIT_PARTITION_SIZE, cmp);

            details.modified();
            return copyAndMigrateFromCollectionToNode(mutator, doubledBitpos, collIndex,
                subNodeNew);
          }
        }
        default: {
          details.modified();
          return copyAndInsertSingleton(mutator, doubledBitpos, key, val);
        }
      }
    }

    @Override
    CompactSetMultimapNode<K, V> removed(final AtomicReference<Thread> mutator, final K key,
        final V val, final int keyHash, final int shift, final SetMultimapResult<K, V> details,
        EqualityComparator<Object> cmp) {
      long bitmap = this.bitmap();

      final int doubledMask = doubledMask(keyHash, shift);
      final int pattern = pattern(bitmap, doubledMask);

      final long doubledBitpos = doubledBitpos(doubledMask);

      switch (pattern) {
        case PATTERN_NODE: {
          int nodeIndex = index(bitmap, PATTERN_NODE, doubledBitpos);

          final CompactSetMultimapNode<K, V> subNode = getNode(nodeIndex);
          final CompactSetMultimapNode<K, V> subNodeNew =
              subNode.removed(mutator, key, val, keyHash, shift + BIT_PARTITION_SIZE, details, cmp);

          if (!details.isModified()) {
            return this;
          }

          switch (subNodeNew.sizePredicate()) {
            case 0: {
              throw new IllegalStateException("Sub-node must have at least one element.");
            }
            case 1: {
              if (slotArity() == 0) {
                if (shift == 0) {
                  // singleton remaining
                  final long doubledBitposAtShift0 = doubledBitpos(bitpos(doubledMask(keyHash, 0)));

                  final long updatedBitmapAtShift0 =
                      setBitPattern(doubledBitposAtShift0, subNodeNew.patternOfSingleton());

                  return subNodeNew.copyAndUpdateBitmaps(mutator, updatedBitmapAtShift0);
                } else {
                  // escalate (singleton or empty) result
                  return subNodeNew;
                }
              } else {
                // inline value (move to front)
                EitherSingletonOrCollection.Type type = subNodeNew.typeOfSingleton();

                if (type == SINGLETON) {
                  return copyAndMigrateFromNodeToSingleton(mutator, doubledBitpos, subNodeNew);
                } else {
                  return copyAndMigrateFromNodeToCollection(mutator, doubledBitpos, subNodeNew);
                }

              }
            }
            default: {
              // modify current node (set replacement node)
              return copyAndSetNode(mutator, nodeIndex, subNodeNew);
            }
          }
        }
        case PATTERN_DATA_SINGLETON: {
          int dataIndex = index(bitmap, PATTERN_DATA_SINGLETON, doubledBitpos);

          final K currentKey = getSingletonKey(dataIndex);
          if (cmp.equals(currentKey, key)) {

            final V currentVal = getSingletonValue(dataIndex);
            if (cmp.equals(currentVal, val)) {

              // remove mapping
              details.updated(val);
              return copyAndRemoveSingleton(mutator, doubledBitpos);
            } else {
              return this;
            }
          } else {
            return this;
          }
        }
        case PATTERN_DATA_COLLECTION: {
          int collIndex = index(bitmap, PATTERN_DATA_COLLECTION, doubledBitpos);

          final K currentKey = getCollectionKey(collIndex);
          if (cmp.equals(currentKey, key)) {

            final AbstractSetNode<V> currentValColl = getCollectionValue(collIndex);
            if (currentValColl.contains(val, val.hashCode(), 0)) {

              // remove mapping
              details.updated(val);

              final AbstractSetNode<V> newValColl =
                  currentValColl.removed(mutator, val, val.hashCode(), 0, SetResult.unchanged());

              if (newValColl.size() == 1) {
                // TODO: investigate options for unboxing singleton collections
                V remainingVal = newValColl.iterator().next();
                return copyAndMigrateFromCollectionToSingleton(mutator, doubledBitpos, key,
                    remainingVal);
              } else {
                return copyAndSetCollectionValue(mutator, doubledBitpos, newValColl);
              }
            } else {
              return this;
            }
          } else {
            return this;
          }
        }
        default:
          return this;
      }
    }

    final static boolean hasSingleNode(int[] arities) {
      return arities[PATTERN_EMPTY] == 31 && arities[PATTERN_NODE] == 1;
    }

    final static boolean hasTwoPayloads(int[] arities) {
      return arities[PATTERN_EMPTY] == 30 && arities[PATTERN_NODE] == 0;
    }

    enum State {
      EMPTY, NODE, PAYLOAD, PAYLOAD_RARE
    }

    static final State toState(final int pattern) {
      // final State[] states = {State.EMPTY, State.NODE, State.PAYLOAD, State.PAYLOAD_RARE};
      // return states[pattern];

      switch (pattern) {
        case PATTERN_EMPTY:
          return State.EMPTY;
        case PATTERN_NODE:
          return State.NODE;
        case PATTERN_DATA_SINGLETON:
          return State.PAYLOAD;
        default:
          return State.PAYLOAD_RARE;
      }
    }

    @Override
    CompactSetMultimapNode<K, V> removedAll(final AtomicReference<Thread> mutator, final K key,
        final int keyHash, final int shift, final SetMultimapResult<K, V> details,
        EqualityComparator<Object> cmp) {
      long bitmap = this.bitmap();

      final int doubledMask = doubledMask(keyHash, shift);
      final int pattern = pattern(bitmap, doubledMask);

      final long doubledBitpos = doubledBitpos(doubledMask);

      switch (pattern) {
        case PATTERN_NODE: {
          int nodeIndex = index(bitmap, PATTERN_NODE, doubledBitpos);

          final CompactSetMultimapNode<K, V> subNode = getNode(nodeIndex);
          final CompactSetMultimapNode<K, V> subNodeNew =
              subNode.removedAll(mutator, key, keyHash, shift + BIT_PARTITION_SIZE, details, cmp);

          if (!details.isModified()) {
            return this;
          }

          switch (subNodeNew.sizePredicate()) {
            case 0: {
              throw new IllegalStateException("Sub-node must have at least one element.");
            }
            case 1: {
              if (slotArity() == 1) {
                if (shift == 0) {
                  // singleton remaining
                  final long doubledBitposAtShift0 = doubledBitpos(bitpos(doubledMask(keyHash, 0)));

                  final long updatedBitmapAtShift0 =
                      setBitPattern(doubledBitposAtShift0, subNodeNew.patternOfSingleton());

                  return subNodeNew.copyAndUpdateBitmaps(mutator, updatedBitmapAtShift0);
                } else {
                  // escalate (singleton or empty) result
                  return subNodeNew;
                }
              } else {
                // // inline value (move to front)
                // final State subNodeState = subNodeNew.stateOfSingleton();
                //
                //// switch (subNodeState) {
                //// case EMPTY:
                //// case NODE:
                //// case PAYLOAD:
                //// return copyAndMigrateFromNodeToSingleton(mutator, doubledBitpos, subNodeNew);
                //// case PAYLOAD_RARE:
                //// return copyAndMigrateFromNodeToCollection(mutator, doubledBitpos, subNodeNew);
                //// }
                //
                // if (subNodeState == State.PAYLOAD) {
                // return copyAndMigrateFromNodeToSingleton(mutator, doubledBitpos, subNodeNew);
                // } else {
                // return copyAndMigrateFromNodeToCollection(mutator, doubledBitpos, subNodeNew);
                // }

                // inline value (move to front)
                EitherSingletonOrCollection.Type type = subNodeNew.typeOfSingleton();

                if (type == SINGLETON) {
                  return copyAndMigrateFromNodeToSingleton(mutator, doubledBitpos, subNodeNew);
                } else {
                  return copyAndMigrateFromNodeToCollection(mutator, doubledBitpos, subNodeNew);
                }

                // // inline value (move to front)
                // final int subNodePattern = subNodeNew.patternOfSingleton();
                //
                // if (subNodePattern == PATTERN_DATA_SINGLETON) {
                // return copyAndMigrateFromNodeToSingleton(mutator, doubledBitpos, subNodeNew);
                // } else {
                // return copyAndMigrateFromNodeToCollection(mutator, doubledBitpos, subNodeNew);
                // }

                // switch (subNodePattern) {
                // case PATTERN_DATA_SINGLETON:
                // return copyAndMigrateFromNodeToSingleton(mutator, doubledBitpos, subNodeNew);
                // case PATTERN_DATA_COLLECTION:
                // return copyAndMigrateFromNodeToCollection(mutator, doubledBitpos, subNodeNew);
                // default:
                // return null;
                // }
              }
            }
            default: {
              // modify current node (set replacement node)
              return copyAndSetNode(mutator, nodeIndex, subNodeNew);
            }
          }
        }
        case PATTERN_DATA_SINGLETON: {
          int dataIndex = index(bitmap, PATTERN_DATA_SINGLETON, doubledBitpos);

          final K currentKey = getSingletonKey(dataIndex);
          if (cmp.equals(currentKey, key)) {

            final V currentVal = getSingletonValue(dataIndex);

            details.updated(currentVal);
            return copyAndRemoveSingleton(mutator, doubledBitpos);
          } else {
            return this;
          }
        }
        case PATTERN_DATA_COLLECTION: {
          int collIndex = index(bitmap, PATTERN_DATA_COLLECTION, doubledBitpos);

          final K currentKey = getCollectionKey(collIndex);
          if (cmp.equals(currentKey, key)) {

            final AbstractSetNode<V> currentValColl = getCollectionValue(collIndex);

            details.updated(currentValColl);
            return copyAndRemoveCollection(mutator, doubledBitpos);
          } else {
            return this;
          }
        }
        default:
          return this;
      }
    }

    int patternOfSingleton() {
      assert this.sizePredicate() == SIZE_ONE;

      long bitmap = this.bitmap();

      final int doubledMask = Long.numberOfTrailingZeros(bitmap) / 2 * 2;
      final int pattern = pattern(bitmap, doubledMask);

      return pattern;
    }

    @Deprecated
    State stateOfSingleton() {
      assert this.sizePredicate() == SIZE_ONE;

      long bitmap = this.bitmap();

      final int doubledMask = Long.numberOfTrailingZeros(bitmap) / 2 * 2;
      final int pattern = pattern(bitmap, doubledMask);

      return toState(pattern);
    }

    @Deprecated
    EitherSingletonOrCollection.Type typeOfSingleton() {
      final int pattern = patternOfSingleton();

      if (pattern == PATTERN_DATA_SINGLETON) {
        return SINGLETON;
      } else {
        return COLLECTION;
      }
    }

    /**
     * @return 0 <= mask <= 2^BIT_PARTITION_SIZE - 1
     */
    static byte recoverMask(int map, byte i_th) {
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

        map = map >> 1;
        mask += 1;
      }

      assert cnt1 != i_th;
      throw new RuntimeException("Called with invalid arguments.");
    }

    @Override
    public String toString() {
      long bitmap = this.bitmap();

      int[] arities = arities(bitmap);

      final StringBuilder bldr = new StringBuilder();
      bldr.append('[');

      for (byte i = 0; i < arities[PATTERN_DATA_SINGLETON]; i++) {
        final byte pos = -1; // TODO: recoverMask(dataMap, (byte) (i + 1));
        bldr.append(String.format("@%d<#%d,#%d>", pos, Objects.hashCode(getSingletonKey(i)),
            Objects.hashCode(getSingletonValue(i))));

        if (!((i + 1) == arities[PATTERN_DATA_SINGLETON])) {
          bldr.append(", ");
        }
      }

      if (arities[PATTERN_DATA_SINGLETON] > 0 && arities[PATTERN_DATA_COLLECTION] > 0) {
        bldr.append(", ");
      }

      for (byte i = 0; i < arities[PATTERN_DATA_COLLECTION]; i++) {
        final byte pos = -1; // TODO: recoverMask(collMap, (byte) (i + 1));
        bldr.append(String.format("@%d<#%d,#%d>", pos, Objects.hashCode(getCollectionKey(i)),
            Objects.hashCode(getCollectionValue(i))));

        if (!((i + 1) == arities[PATTERN_DATA_COLLECTION])) {
          bldr.append(", ");
        }
      }

      if (arities[PATTERN_DATA_COLLECTION] > 0 && arities[PATTERN_NODE] > 0) {
        bldr.append(", ");
      }

      for (byte i = 0; i < arities[PATTERN_NODE]; i++) {
        final byte pos = -1; // TODO: recoverMask(nodeMap, (byte) (i + 1));
        bldr.append(String.format("@%d: %s", pos, getNode(i)));

        if (!((i + 1) == arities[PATTERN_NODE])) {
          bldr.append(", ");
        }
      }

      bldr.append(']');
      return bldr.toString();
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
      CompactSetMultimapNode<?, ?> that = (CompactSetMultimapNode<?, ?>) other;
      if (bitmap() != that.bitmap()) {
        return false;
      }

      return _do_rangecompareObjectRegion(this, that, arrayBase, slotArity());
    }

    // TODO: return abstract instead of compact node
    static final <K, V> CompactSetMultimapNode nodeOf1x0(final AtomicReference<Thread> mutator,
        final long bitmap, final Object slot0) {
      return new SetMultimap0To1Node<>(mutator, bitmap, slot0);
    }

    // TODO: return abstract instead of compact node
    static final <K, V> CompactSetMultimapNode nodeOf0x1(final AtomicReference<Thread> mutator,
        final long bitmap, final K key1, final V val1) {
      return new SetMultimap1To0Node<>(mutator, bitmap, key1, val1);
    }

    // TODO: return abstract instead of compact node
    static final <K, V> CompactSetMultimapNode nodeOf0x2(final AtomicReference<Thread> mutator,
        final long bitmap, final K key1, final V val1, final K key2, final V val2) {
      return new SetMultimap2To0Node<>(mutator, bitmap, key1, val1, key2, val2);
    }

    // TODO: return abstract instead of compact node
    static final <K, V> CompactSetMultimapNode nodeOf4x0(final AtomicReference<Thread> mutator,
        final long bitmap, final Object slot0, final Object slot1, final Object slot2,
        final Object slot3) {
      return new SetMultimap0To4Node<>(mutator, bitmap, slot0, slot1, slot2, slot3);
    }

    // TODO: return abstract instead of compact node
    static final <K, V> CompactSetMultimapNode nodeOf2x0(final AtomicReference<Thread> mutator,
        final long bitmap, final Object slot0, final Object slot1) {
      return new SetMultimap0To2Node<>(mutator, bitmap, slot0, slot1);
    }

    // TODO: return abstract instead of compact node
    static final <K, V> CompactSetMultimapNode nodeOf2x1(final AtomicReference<Thread> mutator,
        final long bitmap, final K key1, final V val1, final Object slot0, final Object slot1) {
      return new SetMultimap1To2Node<>(mutator, bitmap, key1, val1, slot0, slot1);
    }

  }

  private static abstract class AbstractHashCollisionNode<K, V>
      extends CompactSetMultimapNode<K, V> {

    // TODO: remove constructor and stored properties within CompactSetMultimapNode
    AbstractHashCollisionNode() {
      super(null, 0L);
    }

    static final <K, V, VS extends io.usethesource.capsule.Set.Immutable<V>> AbstractHashCollisionNode<K, V> of(
        final int hash, final K key0, final VS valColl0, final K key1, final VS valColl1) {
      return new HashCollisionNode<>(hash, key0, valColl0, key1, valColl1);
    }

    private static final RuntimeException UOE_BOILERPLATE = new UnsupportedOperationException(
        "TODO: CompactSetMultimapNode -> AbstractSetMultimapNode");

    private static final Supplier<RuntimeException> UOE_FACTORY =
        () -> new UnsupportedOperationException(
            "TODO: CompactSetMultimapNode -> AbstractSetMultimapNode");

    @Override
    CompactSetMultimapNode<K, V> copyAndSetSingletonValue(AtomicReference<Thread> mutator,
        long doubledBitpos, V val) {
      throw UOE_FACTORY.get();
    }

    @Override
    CompactSetMultimapNode<K, V> copyAndSetCollectionValue(AtomicReference<Thread> mutator,
        long doubledBitpos, AbstractSetNode<V> valColl) {
      throw UOE_FACTORY.get();
    }

    // @Override
    // CompactSetMultimapNode<K, V> copyAndSetNode(AtomicReference<Thread> mutator, long
    // doubledBitpos,
    // CompactSetMultimapNode<K, V> node) {
    // throw UOE_FACTORY.get();
    // }

    @Override
    CompactSetMultimapNode<K, V> copyAndInsertSingleton(AtomicReference<Thread> mutator,
        long doubledBitpos, K key, V val) {
      throw UOE_FACTORY.get();
    }

    // @Override
    // CompactSetMultimapNode<K, V> copyAndMigrateFromSingletonToCollection(
    // AtomicReference<Thread> mutator, long doubledBitpos, K key, AbstractSetNode<V> valColl) {
    // throw UOE_FACTORY.get();
    // }

    @Override
    CompactSetMultimapNode<K, V> copyAndRemoveSingleton(AtomicReference<Thread> mutator,
        long doubledBitpos) {
      throw UOE_FACTORY.get();
    }

    @Override
    CompactSetMultimapNode<K, V> copyAndRemoveCollection(AtomicReference<Thread> mutator,
        long doubledBitpos) {
      throw UOE_FACTORY.get();
    }

    // @Override
    // CompactSetMultimapNode<K, V> copyAndMigrateFromSingletonToNode(AtomicReference<Thread>
    // mutator,
    // long doubledBitpos, CompactSetMultimapNode<K, V> node) {
    // throw UOE_FACTORY.get();
    // }

    @Override
    CompactSetMultimapNode<K, V> copyAndMigrateFromNodeToSingleton(AtomicReference<Thread> mutator,
        long doubledBitpos, CompactSetMultimapNode<K, V> node) {
      throw UOE_FACTORY.get();
    }

    // @Override
    // CompactSetMultimapNode<K, V> copyAndMigrateFromCollectionToNode(AtomicReference<Thread>
    // mutator,
    // long doubledBitpos, CompactSetMultimapNode<K, V> node) {
    // throw UOE_FACTORY.get();
    // }

    @Override
    CompactSetMultimapNode<K, V> copyAndMigrateFromNodeToCollection(AtomicReference<Thread> mutator,
        long doubledBitpos, CompactSetMultimapNode<K, V> node) {
      throw UOE_FACTORY.get();
    }

    @Override
    CompactSetMultimapNode<K, V> copyAndUpdateBitmaps(AtomicReference<Thread> mutator,
        long bitmap) {
      throw UOE_FACTORY.get();
    }

    // @Override
    // CompactSetMultimapNode<K, V> copyAndInsertCollection(AtomicReference<Thread> mutator,
    // long doubledBitpos, K key, AbstractSetNode<V> valColl) {
    // throw UOE_FACTORY.get();
    // }

    // @Override
    // CompactSetMultimapNode<K, V> copyAndRemoveSingleton(AtomicReference<Thread> mutator,
    // long doubledBitpos, long updatedBitmap) {
    // throw UOE_FACTORY.get();
    // }

    @Override
    CompactSetMultimapNode<K, V> copyAndMigrateFromCollectionToSingleton(
        AtomicReference<Thread> mutator, long doubledBitpos, K key, V val) {
      throw UOE_FACTORY.get();
    }

    // @Override
    // long bitmap() {
    // throw UOE_FACTORY.get();
    // }
    //
    // @Override
    // int emptyArity() {
    // throw UOE_FACTORY.get();
    // }

    @Override
    Type typeOfSingleton() {
      throw UOE_FACTORY.get();
    }

    @Override
    int patternOfSingleton() {
      throw UOE_FACTORY.get();
    }

    @Override
    long bitmap() {
      throw UOE_FACTORY.get();
    }
  }

  private static final class HashCollisionNode<K, V> extends AbstractHashCollisionNode<K, V> {

    private final int hash;
    private final List<Entry<K, io.usethesource.capsule.Set.Immutable<V>>> collisionContent;

    HashCollisionNode(final int hash, final K key0,
        final io.usethesource.capsule.Set.Immutable<V> valColl0, final K key1,
        final io.usethesource.capsule.Set.Immutable<V> valColl1) {
      this(hash, Arrays.asList(entryOf(key0, valColl0), entryOf(key1, valColl1)));
    }

    HashCollisionNode(final int hash,
        final List<Map.Entry<K, io.usethesource.capsule.Set.Immutable<V>>> collisionContent) {
      this.hash = hash;
      this.collisionContent = collisionContent;

      // NOTE: sorting ensures that SINGLETONs occur before COLLECTIONs
      collisionContent.sort((o1, o2) -> o1.getValue().size() - o2.getValue().size());
    }

    private static final RuntimeException UOE = new UnsupportedOperationException();

    private static final Supplier<RuntimeException> UOE_NOT_YET_IMPLEMENTED_FACTORY =
        () -> new UnsupportedOperationException("Not yet implemented @ HashCollisionNode.");

    @Override
    byte sizePredicate() {
      return SIZE_MORE_THAN_ONE;
    }

    @Override
    boolean hasNodes() {
      return false;
    }

    @Override
    int nodeArity() {
      return 0;
    }

    @Override
    CompactSetMultimapNode<K, V> getNode(int index) {
      throw UOE;
    }

    @Override
    boolean hasPayload(EitherSingletonOrCollection.Type type) {
      switch (type) {
        case SINGLETON:
          return collisionContent.stream()
              .filter(kImmutableSetEntry -> kImmutableSetEntry.getValue().size() == 1).findAny()
              .isPresent();
        case COLLECTION:
          return collisionContent.stream()
              .filter(kImmutableSetEntry -> kImmutableSetEntry.getValue().size() >= 2).findAny()
              .isPresent();
      }
      throw new RuntimeException();
    }

    @Override
    int payloadArity(EitherSingletonOrCollection.Type type) {
      switch (type) {
        case SINGLETON:
          return (int) collisionContent.stream()
              .filter(kImmutableSetEntry -> kImmutableSetEntry.getValue().size() == 1).count();
        case COLLECTION:
          return (int) collisionContent.stream()
              .filter(kImmutableSetEntry -> kImmutableSetEntry.getValue().size() >= 2).count();
      }
      throw new RuntimeException();
    }

    @Override
    K getSingletonKey(int index) {
      return collisionContent.stream()
          .filter(kImmutableSetEntry -> kImmutableSetEntry.getValue().size() == 1).skip(index)
          .findAny().get().getKey();
    }

    @Override
    V getSingletonValue(int index) {
      return collisionContent.stream()
          .filter(kImmutableSetEntry -> kImmutableSetEntry.getValue().size() == 1).skip(index)
          .findAny().get().getValue().stream().findAny().get();
    }

    @Override
    K getCollectionKey(int index) {
      return collisionContent.stream()
          .filter(kImmutableSetEntry -> kImmutableSetEntry.getValue().size() >= 2).skip(index)
          .findAny().get().getKey();
    }

    @Override
    AbstractSetNode<V> getCollectionValue(int index) {
      io.usethesource.capsule.Set.Immutable<V> result = collisionContent.stream()
          .filter(kImmutableSetEntry -> kImmutableSetEntry.getValue().size() >= 2).skip(index)
          .findAny().get().getValue();

      return specSetToNode(result);
    }

    @Override
    boolean hasSlots() {
      return true;
    }

    @Override
    int slotArity() {
      return collisionContent.size() * 2;
    }

    @Override
    Object getSlot(int index) {
      if (index % 2 == 0) {
        return collisionContent.get(index / 2).getKey();
      } else {
        Object nextVal = collisionContent.get(index / 2).getValue();

        if (nextVal instanceof TrieSet_5Bits_Spec0To8) {
          return specSetToNode((TrieSet_5Bits_Spec0To8) nextVal);
        } else {
          return nextVal;
        }
      }
    }

    @Override
    public final int[] arities() {
      int[] arities = new int[4];

      arities[0] = 32 - payloadArity(SINGLETON) - payloadArity(COLLECTION);
      arities[1] = payloadArity(SINGLETON);
      arities[2] = payloadArity(COLLECTION);
      arities[3] = 0; // PATTERN_NODE

      return arities;
    }

    @Override
    boolean containsKey(K key, int keyHash, int shift, EqualityComparator<Object> cmp) {
      return collisionContent.stream().filter(entry -> cmp.equals(key, entry.getKey())).findAny()
          .isPresent();
    }

    @Override
    boolean containsTuple(K key, V val, int keyHash, int shift, EqualityComparator<Object> cmp) {
      return collisionContent.stream()
          .filter(entry -> cmp.equals(key, entry.getKey())
              && entry.getValue().containsEquivalent(val, cmp.toComparator()))
          .findAny().isPresent();
    }

    @Override
    Optional<AbstractSetNode<V>> findByKey(K key, int keyHash, int shift,
        EqualityComparator<Object> cmp) {
      throw UOE_NOT_YET_IMPLEMENTED_FACTORY.get();
    }

    @Override
    CompactSetMultimapNode<K, V> inserted(AtomicReference<Thread> mutator, K key, V val,
        int keyHash, int shift, SetMultimapResult<K, V> details, EqualityComparator<Object> cmp) {
      Optional<Map.Entry<K, io.usethesource.capsule.Set.Immutable<V>>> optionalTuple =
          collisionContent.stream().filter(entry -> cmp.equals(key, entry.getKey())).findAny();

      if (optionalTuple.isPresent()) {
        // contains key

        io.usethesource.capsule.Set.Immutable<V> values = optionalTuple.get().getValue();

        if (values.containsEquivalent(val, cmp.toComparator())) {
          // contains key and value
          details.unchanged();
          return this;

        } else {
          // contains key but not value

          Function<Entry<K, io.usethesource.capsule.Set.Immutable<V>>, Entry<K, io.usethesource.capsule.Set.Immutable<V>>> substitutionMapper =
              (kImmutableSetEntry) -> {
                if (kImmutableSetEntry == optionalTuple.get()) {
                  io.usethesource.capsule.Set.Immutable<V> updatedValues =
                      values.__insertEquivalent(val, cmp.toComparator());
                  return entryOf(key, updatedValues);
                } else {
                  return kImmutableSetEntry;
                }
              };

          List<Map.Entry<K, io.usethesource.capsule.Set.Immutable<V>>> updatedCollisionContent =
              collisionContent.stream().map(substitutionMapper).collect(Collectors.toList());

          // TODO not all API uses EqualityComparator
          // TODO does not check that remainder is unmodified
          assert updatedCollisionContent.size() == collisionContent.size();
          assert updatedCollisionContent.contains(optionalTuple.get()) == false;
          // assert updatedCollisionContent.contains(entryOf(key, values.__insertEquivalent(val,
          // cmp.toComparator())));
          assert updatedCollisionContent.stream()
              .filter(entry -> cmp.equals(key, entry.getKey())
                  && entry.getValue().containsEquivalent(val, cmp.toComparator()))
              .findAny().isPresent();

          details.modified();
          return new HashCollisionNode<K, V>(hash, updatedCollisionContent);
        }
      } else {
        // does not contain key

        Stream.Builder<Map.Entry<K, io.usethesource.capsule.Set.Immutable<V>>> builder =
            Stream.<Map.Entry<K, io.usethesource.capsule.Set.Immutable<V>>>builder()
                .add(entryOf(key, specSetOf(val)));

        collisionContent.forEach(builder::accept);

        List<Map.Entry<K, io.usethesource.capsule.Set.Immutable<V>>> updatedCollisionContent =
            builder.build().collect(Collectors.toList());

        // TODO not all API uses EqualityComparator
        assert updatedCollisionContent.size() == collisionContent.size() + 1;
        assert updatedCollisionContent.containsAll(collisionContent);
        // assert updatedCollisionContent.contains(entryOf(key, specSetOf(val)));
        assert updatedCollisionContent.stream().filter(entry -> cmp.equals(key, entry.getKey())
            && Objects.equals(specSetOf(val), entry.getValue())).findAny().isPresent();

        details.modified();
        return new HashCollisionNode<K, V>(hash, updatedCollisionContent);
      }
    }

    @Override
    CompactSetMultimapNode<K, V> updated(AtomicReference<Thread> mutator, K key, V val, int keyHash,
        int shift, SetMultimapResult<K, V> details, EqualityComparator<Object> cmp) {
      Optional<Map.Entry<K, io.usethesource.capsule.Set.Immutable<V>>> optionalTuple =
          collisionContent.stream().filter(entry -> cmp.equals(key, entry.getKey())).findAny();

      if (optionalTuple.isPresent()) {
        // contains key -> replace val anyways

        io.usethesource.capsule.Set.Immutable<V> values = optionalTuple.get().getValue();

        Function<Map.Entry<K, io.usethesource.capsule.Set.Immutable<V>>, Map.Entry<K, io.usethesource.capsule.Set.Immutable<V>>> substitutionMapper =
            (kImmutableSetEntry) -> {
              if (kImmutableSetEntry == optionalTuple.get()) {
                io.usethesource.capsule.Set.Immutable<V> updatedValues = values
                    .__insertEquivalent(val, cmp.toComparator());
                return entryOf(key, updatedValues);
              } else {
                return kImmutableSetEntry;
              }
            };

        List<Map.Entry<K, io.usethesource.capsule.Set.Immutable<V>>> updatedCollisionContent =
            collisionContent.stream().map(substitutionMapper).collect(Collectors.toList());

        if (values.size() == 1) {
          details.updated(values.stream().findAny().get()); // unbox singleton
        } else {
          details.updated(specSetToNode(values));
        }

        return new HashCollisionNode<K, V>(hash, updatedCollisionContent);
      } else {
        // does not contain key

        Stream.Builder<Map.Entry<K, io.usethesource.capsule.Set.Immutable<V>>> builder =
            Stream.<Map.Entry<K, io.usethesource.capsule.Set.Immutable<V>>>builder()
                .add(entryOf(key, specSetOf(val)));

        collisionContent.forEach(builder::accept);

        List<Map.Entry<K, io.usethesource.capsule.Set.Immutable<V>>> updatedCollisionContent =
            builder.build().collect(Collectors.toList());

        details.modified();
        return new HashCollisionNode<K, V>(hash, updatedCollisionContent);
      }
    }

    @Override
    CompactSetMultimapNode<K, V> removed(AtomicReference<Thread> mutator, K key, V val, int keyHash,
        int shift, SetMultimapResult<K, V> details, EqualityComparator<Object> cmp) {
      Optional<Map.Entry<K, io.usethesource.capsule.Set.Immutable<V>>> optionalTuple =
          collisionContent.stream().filter(entry -> cmp.equals(key, entry.getKey())).findAny();

      if (optionalTuple.isPresent()) {
        // contains key

        io.usethesource.capsule.Set.Immutable<V> values = optionalTuple.get().getValue();

        if (values.containsEquivalent(val, cmp.toComparator())) {
          // contains key and value -> remove mapping

          final List<Map.Entry<K, io.usethesource.capsule.Set.Immutable<V>>> updatedCollisionContent;

          if (values.size() == 1) {
            updatedCollisionContent = collisionContent.stream()
                .filter(kImmutableSetEntry -> kImmutableSetEntry != optionalTuple.get())
                .collect(Collectors.toList());
          } else {
            Function<Map.Entry<K, io.usethesource.capsule.Set.Immutable<V>>, Map.Entry<K, io.usethesource.capsule.Set.Immutable<V>>> substitutionMapper =
                (kImmutableSetEntry) -> {
                  if (kImmutableSetEntry == optionalTuple.get()) {
                    io.usethesource.capsule.Set.Immutable<V> updatedValues =
                        values.__removeEquivalent(val, cmp.toComparator());
                    return entryOf(key, updatedValues);
                  } else {
                    return kImmutableSetEntry;
                  }
                };

            updatedCollisionContent =
                collisionContent.stream().map(substitutionMapper).collect(Collectors.toList());
          }

          details.updated(val);
          return new HashCollisionNode<K, V>(hash, updatedCollisionContent);
        }
      }

      details.unchanged();
      return this;
    }

    @Override
    State stateOfSingleton() {
      throw UOE_NOT_YET_IMPLEMENTED_FACTORY.get();
    }
  }

  /**
   * Iterator skeleton that uses a fixed stack in depth.
   */
  private static abstract class AbstractSetMultimapIterator<K, V> {

    private static final int MAX_DEPTH = 7;

    protected int currentValueSingletonCursor;
    protected int currentValueSingletonLength;
    protected int currentValueCollectionCursor;
    protected int currentValueCollectionLength;
    protected AbstractSetMultimapNode<K, V> currentValueNode;

    private int currentStackLevel = -1;
    private final int[] nodeCursorsAndLengths = new int[MAX_DEPTH * 2];

    AbstractSetMultimapNode<K, V>[] nodes = new AbstractSetMultimapNode[MAX_DEPTH];

    AbstractSetMultimapIterator(AbstractSetMultimapNode<K, V> rootNode) {
      int nodeArity = rootNode.nodeArity();
      if (nodeArity != 0) {
        currentStackLevel = 0;

        nodes[0] = rootNode;
        nodeCursorsAndLengths[0] = 0;
        nodeCursorsAndLengths[1] = nodeArity;
      }

      int emptyArity = rootNode.emptyArity();
      if (emptyArity + nodeArity < 32) {
        currentValueNode = rootNode;
        currentValueSingletonCursor = 0;
        currentValueSingletonLength = rootNode.payloadArity(SINGLETON);
        currentValueCollectionCursor = 0;
        currentValueCollectionLength = rootNode.payloadArity(COLLECTION);
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
          final AbstractSetMultimapNode<K, V> nextNode =
              nodes[currentStackLevel].getNode(nodeCursor);
          nodeCursorsAndLengths[currentCursorIndex]++;

          int nodeArity = nextNode.nodeArity();
          if (nodeArity != 0) {
            /*
             * put node on next stack level for depth-first traversal
             */
            final int nextStackLevel = ++currentStackLevel;
            final int nextCursorIndex = nextStackLevel * 2;
            final int nextLengthIndex = nextCursorIndex + 1;

            nodes[nextStackLevel] = nextNode;
            nodeCursorsAndLengths[nextCursorIndex] = 0;
            nodeCursorsAndLengths[nextLengthIndex] = nodeArity;
          }

          // int emptyArity = nextNode.emptyArity();
          // if (emptyArity + nodeArity < 32) {
          if (nextNode.hasPayload(SINGLETON) || nextNode.hasPayload(COLLECTION)) {
            // if (payloadAritySingleton != 0 || payloadArityCollection != 0) {
            /*
             * found next node that contains values
             */
            currentValueNode = nextNode;
            currentValueSingletonCursor = 0;
            currentValueSingletonLength = nextNode.payloadArity(SINGLETON);
            currentValueCollectionCursor = 0;
            currentValueCollectionLength = nextNode.payloadArity(COLLECTION);
            return true;
          }
        } else {
          currentStackLevel--;
        }
      }

      return false;
    }

    public boolean hasNext() {
      if (currentValueSingletonCursor < currentValueSingletonLength
          || currentValueCollectionCursor < currentValueCollectionLength) {
        return true;
      } else {
        return searchNextValueNode();
      }
    }

    public void remove() {
      throw new UnsupportedOperationException();
    }
  }

  protected static class SetMultimapKeyIterator<K, V> extends AbstractSetMultimapIterator<K, V>
      implements Iterator<K> {

    SetMultimapKeyIterator(AbstractSetMultimapNode<K, V> rootNode) {
      super(rootNode);
    }

    @Override
    public K next() {
      if (!hasNext()) {
        throw new NoSuchElementException();
      } else {
        // TODO: check case distinction
        if (currentValueSingletonCursor < currentValueSingletonLength) {
          return currentValueNode.getSingletonKey(currentValueSingletonCursor++);
        } else {
          return currentValueNode.getCollectionKey(currentValueCollectionCursor++);
        }
      }
    }

  }

  /**
   * Iterator skeleton that uses a fixed stack in depth.
   */
  private static abstract class AbstractSetMultimapIteratorLowLevel<K, V> {

    private static final int MAX_DEPTH = 7;

    protected AbstractSetMultimapNode<K, V> payloadNode;
    protected long payloadOffset;
    protected long payloadOutOfBounds;

    private int stackLevel = -1;
    private final long[] stackOfOffsetsAndOutOfBounds = new long[MAX_DEPTH * 2];
    private final AbstractSetMultimapNode<K, V>[] stackOfNodes =
        new AbstractSetMultimapNode[MAX_DEPTH];

    AbstractSetMultimapIteratorLowLevel(AbstractSetMultimapNode<K, V> rootNode) {
      // int nodeArity = rootNode.nodeArity();
      // int anyTupleArity = 32 - nodeArity - rootNode.emptyArity();

      int[] arities = rootNode.arities();

      int nodeArity = arities[PATTERN_NODE];
      int anyTupleArity = 32 - nodeArity - arities[PATTERN_EMPTY];

      long offsetPayload = CompactSetMultimapNode.arrayBase;
      long lengthPayload = anyTupleArity * 2 * addressSize;

      long offsetNodes = offsetPayload + lengthPayload;
      long lengthNodes = nodeArity * addressSize;

      long offsetOutOfBounds = offsetNodes + lengthNodes;

      if (nodeArity != 0) {
        stackLevel = 0;

        stackOfNodes[0] = rootNode;
        stackOfOffsetsAndOutOfBounds[0] = offsetNodes;
        stackOfOffsetsAndOutOfBounds[1] = offsetOutOfBounds;
      }

      if (anyTupleArity != 0) {
        payloadNode = rootNode;
        payloadOffset = offsetPayload;
        payloadOutOfBounds = offsetNodes;
      }
    }

    /*
     * search for next node that contains values
     */
    private boolean searchNextValueNode() {
      while (stackLevel >= 0) {
        final int currentCursorIndex = stackLevel * 2;
        final int currentLengthIndex = currentCursorIndex + 1;

        final long nodeCursorAddress = stackOfOffsetsAndOutOfBounds[currentCursorIndex];
        final long nodeLengthAddress = stackOfOffsetsAndOutOfBounds[currentLengthIndex];

        if (nodeCursorAddress < nodeLengthAddress) {
          final AbstractSetMultimapNode<K, V> nextNode =
              getFromObjectRegionAndCast(stackOfNodes[stackLevel], nodeCursorAddress);
          stackOfOffsetsAndOutOfBounds[currentCursorIndex] += addressSize;

          // int nodeArity = nextNode.nodeArity();
          // int anyTupleArity = 32 - nodeArity - nextNode.emptyArity();

          int[] arities = nextNode.arities();

          int nodeArity = arities[PATTERN_NODE];
          int anyTupleArity = 32 - nodeArity - arities[PATTERN_EMPTY];

          long offsetPayload = CompactSetMultimapNode.arrayBase;
          long lengthPayload = anyTupleArity * 2 * addressSize;

          long offsetNodes = offsetPayload + lengthPayload;
          long lengthNodes = nodeArity * addressSize;

          long offsetOutOfBounds = offsetNodes + lengthNodes;

          if (nodeArity != 0) {
            /*
             * put node on next stack level for depth-first traversal
             */
            final int nextStackLevel = ++stackLevel;
            final int nextCursorIndex = nextStackLevel * 2;
            final int nextLengthIndex = nextCursorIndex + 1;

            stackOfNodes[nextStackLevel] = nextNode;
            stackOfOffsetsAndOutOfBounds[nextCursorIndex] = offsetNodes;
            stackOfOffsetsAndOutOfBounds[nextLengthIndex] = offsetOutOfBounds;
          }

          if (anyTupleArity != 0) {
            /*
             * found next node that contains values
             */
            payloadNode = nextNode;
            payloadOffset = offsetPayload;
            payloadOutOfBounds = offsetNodes;

            return true;
          }
        } else {
          stackLevel--;
        }
      }

      return false;
    }

    public boolean hasNext() {
      if (payloadOffset < payloadOutOfBounds) {
        return true;
      } else {
        return searchNextValueNode();
      }
    }

    public void remove() {
      throw new UnsupportedOperationException();
    }
  }

  /**
   * Iterator skeleton that uses a fixed stack in depth.
   */
  private static abstract class AbstractSetMultimapIteratorHistogram<K, V> {

    private static final int MAX_DEPTH = 7;

    protected AbstractSetMultimapNode<K, V> payloadNode;
    protected int payloadCursorX;
    protected int payloadCursorY;
    protected long payloadOffset;

    protected int[] histogram;
    protected int payloadRemaining;

    private int stackLevel = -1;
    private final long[] stackOfOffsetsAndOutOfBounds = new long[MAX_DEPTH * 2];
    private final AbstractSetMultimapNode<K, V>[] stackOfNodes =
        new AbstractSetMultimapNode[MAX_DEPTH];

    AbstractSetMultimapIteratorHistogram(AbstractSetMultimapNode<K, V> rootNode) {
      int[] arities = rootNode.arities();

      int nodeArity = arities[PATTERN_NODE];
      int anyTupleArity = 32 - nodeArity - arities[PATTERN_EMPTY];

      long offsetPayload = CompactSetMultimapNode.arrayBase;
      long lengthPayload = anyTupleArity * 2 * addressSize;

      if (nodeArity != 0) {
        stackLevel = 0;

        long offsetNodes = offsetPayload + lengthPayload;
        long lengthNodes = nodeArity * addressSize;

        stackOfNodes[0] = rootNode;
        stackOfOffsetsAndOutOfBounds[0] = offsetNodes;
        stackOfOffsetsAndOutOfBounds[1] = offsetNodes + lengthNodes;
      }

      if (anyTupleArity != 0) {
        payloadRemaining = anyTupleArity;

        payloadNode = rootNode;
        payloadCursorX = PATTERN_DATA_SINGLETON;
        payloadCursorY = 0;

        payloadOffset = offsetPayload;
      }

      histogram = arities;
    }

    private boolean searchNextPayloadCategory() {
      while (histogram[++payloadCursorX] == 0) {
        ;
      }
      payloadCursorY = 0;

      return true;
    }

    /*
     * search for next node that contains values
     */
    private boolean searchNextValueNode() {
      while (stackLevel >= 0) {
        final int currentCursorIndex = stackLevel * 2;
        final int currentLengthIndex = currentCursorIndex + 1;

        final long nodeCursorAddress = stackOfOffsetsAndOutOfBounds[currentCursorIndex];
        final long nodeLengthAddress = stackOfOffsetsAndOutOfBounds[currentLengthIndex];

        if (nodeCursorAddress < nodeLengthAddress) {
          final AbstractSetMultimapNode<K, V> nextNode =
              getFromObjectRegionAndCast(stackOfNodes[stackLevel], nodeCursorAddress);
          stackOfOffsetsAndOutOfBounds[currentCursorIndex] += addressSize;

          int[] arities = nextNode.arities();

          int nodeArity = arities[PATTERN_NODE];
          int anyTupleArity = 32 - nodeArity - arities[PATTERN_EMPTY];

          long offsetPayload = CompactSetMultimapNode.arrayBase;
          long lengthPayload = anyTupleArity * 2 * addressSize;

          if (nodeArity != 0) {
            /*
             * put node on next stack level for depth-first traversal
             */
            final int nextStackLevel = ++stackLevel;
            final int nextCursorIndex = nextStackLevel * 2;
            final int nextLengthIndex = nextCursorIndex + 1;

            long offsetNodes = offsetPayload + lengthPayload;
            long lengthNodes = nodeArity * addressSize;

            stackOfNodes[nextStackLevel] = nextNode;
            stackOfOffsetsAndOutOfBounds[nextCursorIndex] = offsetNodes;
            stackOfOffsetsAndOutOfBounds[nextLengthIndex] = offsetNodes + lengthNodes;
          }

          if (anyTupleArity != 0) {
            /*
             * found next node that contains values
             */
            histogram = arities;
            payloadRemaining = anyTupleArity;

            payloadNode = nextNode;
            payloadCursorX = PATTERN_DATA_SINGLETON;
            payloadCursorY = 0;

            payloadOffset = offsetPayload;

            return true;
          }
        } else {
          stackLevel--;
        }
      }

      return false;
    }

    public boolean hasNext() {
      if (payloadCursorY < histogram[payloadCursorX]) {
        return true;
      } else if (payloadRemaining != 0) {
        return searchNextPayloadCategory();
      } else {
        return searchNextValueNode();
      }
    }

    public void remove() {
      throw new UnsupportedOperationException();
    }
  }

  protected static class SetMultimapKeyIteratorHistogram<K, V>
      extends AbstractSetMultimapIteratorHistogram<K, V> implements Iterator<K> {

    SetMultimapKeyIteratorHistogram(AbstractSetMultimapNode<K, V> rootNode) {
      super(rootNode);
    }

    @Override
    public K next() {
      if (!hasNext()) {
        throw new NoSuchElementException();
      } else {
        switch (payloadCursorX) {
          case PATTERN_DATA_SINGLETON:
          case PATTERN_DATA_COLLECTION:
            long nextOffset = payloadOffset;

            payloadCursorY += 1;
            payloadRemaining -= 1;
            payloadOffset = nextOffset + 2 * addressSize;

            return getFromObjectRegionAndCast(payloadNode, nextOffset);
          default:
            throw new IllegalStateException();
        }
      }
    }

  }

  protected static class SetMultimapKeyIteratorLowLevel<K, V>
      extends AbstractSetMultimapIteratorLowLevel<K, V> implements Iterator<K> {

    SetMultimapKeyIteratorLowLevel(AbstractSetMultimapNode<K, V> rootNode) {
      super(rootNode);
    }

    @Override
    public K next() {
      if (!hasNext()) {
        throw new NoSuchElementException();
      } else {
        long nextOffset = payloadOffset;

        K nextKey = getFromObjectRegionAndCast(payloadNode, nextOffset);
        payloadOffset = nextOffset + 2 * addressSize;

        return nextKey;
      }
    }

  }

  protected static class SetMultimapNativeTupleIteratorLowLevel<K, V>
      extends AbstractSetMultimapIteratorLowLevel<K, V> implements Iterator<Map.Entry<K, Object>> {

    SetMultimapNativeTupleIteratorLowLevel(AbstractSetMultimapNode<K, V> rootNode) {
      super(rootNode);
    }

    @Override
    public Map.Entry<K, Object> next() {
      if (!hasNext()) {
        throw new NoSuchElementException();
      } else {
        if (payloadNode instanceof HashCollisionNode) {
          int slotIndex = (int) ((payloadOffset - CompactSetMultimapNode.arrayBase) / addressSize);

          K nextKey = (K) payloadNode.getSlot(slotIndex);
          Object nextVal = payloadNode.getSlot(slotIndex + 1);

          payloadOffset = payloadOffset + 2 * addressSize;

          return entryOf(nextKey, nextVal);
        } else {
          long nextOffset = payloadOffset;

          K nextKey = getFromObjectRegionAndCast(payloadNode, nextOffset);
          nextOffset += addressSize;
          Object nextVal = getFromObjectRegion(payloadNode, nextOffset);
          nextOffset += addressSize;

          payloadOffset = nextOffset;

          return entryOf(nextKey, nextVal);
        }
      }
    }

  }

  private static class FlatteningIterator<K, V> implements Iterator<Map.Entry<K, V>> {

    final Iterator<Entry<K, Object>> entryIterator;

    K lastKey = null;
    Iterator<V> lastIterator = Collections.emptyIterator();

    public FlatteningIterator(Iterator<Entry<K, Object>> entryIterator) {
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

    @Override
    public Entry<K, V> next() {
      assert hasNext();

      if (lastIterator.hasNext()) {
        return entryOf(lastKey, lastIterator.next());
      } else {
        lastKey = null;

        Entry<K, Object> nextEntry = entryIterator.next();

        Object singletonOrSet = nextEntry.getValue();
        assert !(singletonOrSet instanceof io.usethesource.capsule.Set.Immutable);

        if (singletonOrSet instanceof AbstractSetNode) {
          AbstractSetNode set = (AbstractSetNode) singletonOrSet;

          lastKey = nextEntry.getKey();
          lastIterator = set.iterator();

          return entryOf(lastKey, lastIterator.next());
        } else {
          return (Map.Entry<K, V>) nextEntry;
        }
      }
    }

  }

  protected static class SetMultimapValueIterator<K, V> extends AbstractSetMultimapIterator<K, V>
      implements Iterator<AbstractSetNode<V>> {

    SetMultimapValueIterator(AbstractSetMultimapNode<K, V> rootNode) {
      super(rootNode);
    }

    @Override
    public AbstractSetNode<V> next() {
      if (!hasNext()) {
        throw new NoSuchElementException();
      } else {
        // TODO: check case distinction
        if (currentValueSingletonCursor < currentValueSingletonLength) {
          return specSetNodeOf(currentValueNode.getSingletonValue(currentValueSingletonCursor++));
        } else {
          return currentValueNode.getCollectionValue(currentValueCollectionCursor++);
        }
      }
    }

  }

  protected static class SetMultimapNativeTupleIterator<K, V>
      extends AbstractSetMultimapIterator<K, V> implements Iterator<Map.Entry<K, Object>> {

    SetMultimapNativeTupleIterator(AbstractSetMultimapNode<K, V> rootNode) {
      super(rootNode);
    }

    @Override
    public Map.Entry<K, Object> next() {
      if (!hasNext()) {
        throw new NoSuchElementException();
      } else {
        // TODO: check case distinction
        if (currentValueSingletonCursor < currentValueSingletonLength) {
          final K currentKey = currentValueNode.getSingletonKey(currentValueSingletonCursor);
          final Object currentValue =
              currentValueNode.getSingletonValue(currentValueSingletonCursor);
          currentValueSingletonCursor++;

          return AbstractSpecialisedImmutableMap.entryOf(currentKey, currentValue);
        } else {
          final K currentKey = currentValueNode.getCollectionKey(currentValueCollectionCursor);
          final Object currentValue =
              currentValueNode.getCollectionValue(currentValueCollectionCursor);
          currentValueCollectionCursor++;

          return AbstractSpecialisedImmutableMap.entryOf(currentKey, currentValue);
        }
      }
    }

  }

  protected static class SetMultimapTupleIterator<K, V, T> extends AbstractSetMultimapIterator<K, V>
      implements Iterator<T> {

    final BiFunction<K, V, T> tupleOf;

    K currentKey = null;
    V currentValue = null;
    Iterator<V> currentSetIterator = Collections.emptyIterator();

    SetMultimapTupleIterator(AbstractSetMultimapNode<K, V> rootNode,
        final BiFunction<K, V, T> tupleOf) {
      super(rootNode);
      this.tupleOf = tupleOf;
    }

    @Override
    public boolean hasNext() {
      if (currentSetIterator.hasNext()) {
        return true;
      } else {
        if (super.hasNext()) {
          // TODO: check case distinction
          if (currentValueSingletonCursor < currentValueSingletonLength) {
            currentKey = currentValueNode.getSingletonKey(currentValueSingletonCursor);
            currentSetIterator = Collections
                .singleton(currentValueNode.getSingletonValue(currentValueSingletonCursor))
                .iterator();
            currentValueSingletonCursor++;
          } else {
            currentKey = currentValueNode.getCollectionKey(currentValueCollectionCursor);
            currentSetIterator =
                currentValueNode.getCollectionValue(currentValueCollectionCursor).iterator();
            currentValueCollectionCursor++;
          }

          return true;
        } else {
          return false;
        }
      }
    }

    @Override
    public T next() {
      if (!hasNext()) {
        throw new NoSuchElementException();
      } else {
        currentValue = currentSetIterator.next();
        return tupleOf.apply(currentKey, currentValue);
      }
    }

  }

  /**
   * Iterator that first iterates over inlined-values and then continues depth first recursively.
   */
  private static class TrieSetMultimap_BleedingEdgeNodeIterator<K, V>
      implements Iterator<AbstractSetMultimapNode<K, V>> {

    final Deque<Iterator<? extends AbstractSetMultimapNode<K, V>>> nodeIteratorStack;

    TrieSetMultimap_BleedingEdgeNodeIterator(AbstractSetMultimapNode<K, V> rootNode) {
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
    public AbstractSetMultimapNode<K, V> next() {
      if (!hasNext()) {
        throw new NoSuchElementException();
      }

      AbstractSetMultimapNode<K, V> innerNode = nodeIteratorStack.peek().next();

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

  static final class TransientTrieSetMultimap_BleedingEdge<K, V>
      implements SetMultimap.Transient<K, V> {

    private final EqualityComparator<Object> cmp;

    final private AtomicReference<Thread> mutator;
    private AbstractSetMultimapNode<K, V> rootNode;
    private int hashCode;
    private int cachedSize;

    TransientTrieSetMultimap_BleedingEdge(
        TrieSetMultimap_HHAMT_Specialized_Path_Interlinked<K, V> trieSetMultimap_BleedingEdge) {
      this.cmp = trieSetMultimap_BleedingEdge.cmp;
      this.mutator = new AtomicReference<Thread>(Thread.currentThread());
      this.rootNode = trieSetMultimap_BleedingEdge.rootNode;
      this.hashCode = trieSetMultimap_BleedingEdge.hashCode;
      this.cachedSize = trieSetMultimap_BleedingEdge.cachedSize;
      if (DEBUG) {
        assert checkHashCodeAndSize(hashCode, cachedSize);
      }
    }

    private boolean checkHashCodeAndSize(final int targetHash, final int targetSize) {
      int hash = 0;
      int size = 0;

      for (Iterator<Map.Entry<K, V>> it = entryIterator(); it.hasNext(); ) {
        final Map.Entry<K, V> entry = it.next();
        final K key = entry.getKey();
        final V val = entry.getValue();

        hash += key.hashCode() ^ val.hashCode();
        size += 1;
      }

      return hash == targetHash && size == targetSize;
    }

    @Override
    public boolean containsKey(final Object o) {
      try {
        final K key = (K) o;
        return rootNode.containsKey(key, transformHashCode(key.hashCode()), 0, cmp);
      } catch (ClassCastException unused) {
        return false;
      }
    }

    @Override
    public boolean containsValue(final Object o) {
      for (Iterator<V> iterator = valueIterator(); iterator.hasNext(); ) {
        if (cmp.equals(iterator.next(), o)) {
          return true;
        }
      }
      return false;
    }

    @Override
    public boolean containsEntry(final Object o0, final Object o1) {
      try {
        final K key = (K) o0;
        final V val = (V) o1;
        final Optional<AbstractSetNode<V>> result =
            rootNode.findByKey(key, transformHashCode(key.hashCode()), 0, cmp);

        if (result.isPresent()) {
          return result.get().contains(val, val.hashCode(), 0);
        } else {
          return false;
        }
      } catch (ClassCastException unused) {
        return false;
      }
    }

    @Override
    public io.usethesource.capsule.Set.Immutable<V> get(final Object o) {
      try {
        final K key = (K) o;
        final Optional<AbstractSetNode<V>> result =
            rootNode.findByKey(key, transformHashCode(key.hashCode()), 0, cmp);

        if (result.isPresent()) {
          return setFromNode(result.get());
        } else {
          return null;
        }
      } catch (ClassCastException unused) {
        return null;
      }
    }

    @Override
    public boolean __insert(final K key, final V val) {
      if (mutator.get() == null) {
        throw new IllegalStateException("Transient already frozen.");
      }

      final int keyHash = key.hashCode();
      final SetMultimapResult<K, V> details = SetMultimapResult.unchanged();

      final CompactSetMultimapNode<K, V> newRootNode =
          rootNode.inserted(mutator, key, val, transformHashCode(keyHash), 0, details, cmp);

      if (details.isModified()) {

        final int valHashNew = val.hashCode();
        rootNode = newRootNode;
        hashCode += (keyHash ^ valHashNew);
        cachedSize += 1;

        if (DEBUG) {
          assert checkHashCodeAndSize(hashCode, cachedSize);
        }
        return true;

      }

      if (DEBUG) {
        assert checkHashCodeAndSize(hashCode, cachedSize);
      }
      return false;
    }

    @Override
    public boolean union(final SetMultimap<? extends K, ? extends V> setMultimap) {
      boolean modified = false;

      for (Map.Entry<? extends K, ? extends V> entry : setMultimap.entrySet()) {
        modified |= this.__insert(entry.getKey(), entry.getValue());
      }

      return modified;
    }

    @Override
    public boolean __remove(final K key, final V val) {
      if (mutator.get() == null) {
        throw new IllegalStateException("Transient already frozen.");
      }

      final int keyHash = key.hashCode();
      final SetMultimapResult<K, V> details = SetMultimapResult.unchanged();

      final CompactSetMultimapNode<K, V> newRootNode =
          rootNode.removed(mutator, key, val, transformHashCode(keyHash), 0, details, cmp);

      if (details.isModified()) {
        assert details.hasReplacedValue();
        final int valHash = details.getReplacedValue().hashCode();

        rootNode = newRootNode;
        hashCode = hashCode - (keyHash ^ valHash);
        cachedSize = cachedSize - 1;

        if (DEBUG) {
          assert checkHashCodeAndSize(hashCode, cachedSize);
        }
        return true;
      }

      if (DEBUG) {
        assert checkHashCodeAndSize(hashCode, cachedSize);
      }

      return false;
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
    public Iterator<K> keyIterator() {
      return new TransientSetMultimapKeyIterator<>(this);
    }

    @Override
    public Iterator<V> valueIterator() {
      return valueCollectionsStream().flatMap(AbstractSetNode::stream).iterator();
    }

    @Override
    public Iterator<Map.Entry<K, V>> entryIterator() {
      return new TransientSetMultimapTupleIterator<>(this,
          AbstractSpecialisedImmutableMap::entryOf);
    }

    @Override
    public <T> Iterator<T> tupleIterator(final BiFunction<K, V, T> tupleOf) {
      return new TransientSetMultimapTupleIterator<>(this, tupleOf);
    }

    private Spliterator<AbstractSetNode<V>> valueCollectionsSpliterator() {
      /*
       * TODO: specialize between mutable / SetMultimap.Immutable<K, V> ({@see Spliterator.IMMUTABLE})
       */
      int characteristics = Spliterator.NONNULL | Spliterator.SIZED | Spliterator.SUBSIZED;
      return Spliterators.spliterator(new SetMultimapValueIterator<>(rootNode), size(),
          characteristics);
    }

    private Stream<AbstractSetNode<V>> valueCollectionsStream() {
      boolean isParallel = false;
      return StreamSupport.stream(valueCollectionsSpliterator(), isParallel);
    }

    public static class TransientSetMultimapKeyIterator<K, V> extends SetMultimapKeyIterator<K, V> {

      final TransientTrieSetMultimap_BleedingEdge<K, V> collection;
      K lastKey;

      public TransientSetMultimapKeyIterator(
          final TransientTrieSetMultimap_BleedingEdge<K, V> collection) {
        super(collection.rootNode);
        this.collection = collection;
      }

      @Override
      public K next() {
        return lastKey = super.next();
      }

      @Override
      public void remove() {
        throw new UnsupportedOperationException();
      }
    }

    public static class TransientSetMultimapValueIterator<K, V>
        extends SetMultimapValueIterator<K, V> {

      final TransientTrieSetMultimap_BleedingEdge<K, V> collection;

      public TransientSetMultimapValueIterator(
          final TransientTrieSetMultimap_BleedingEdge<K, V> collection) {
        super(collection.rootNode);
        this.collection = collection;
      }

      @Override
      public AbstractSetNode<V> next() {
        return super.next();
      }

      @Override
      public void remove() {
        throw new UnsupportedOperationException();
      }
    }

    public static class TransientSetMultimapTupleIterator<K, V, T>
        extends SetMultimapTupleIterator<K, V, T> {

      final TransientTrieSetMultimap_BleedingEdge<K, V> collection;

      public TransientSetMultimapTupleIterator(
          final TransientTrieSetMultimap_BleedingEdge<K, V> collection,
          final BiFunction<K, V, T> tupleOf) {
        super(collection.rootNode, tupleOf);
        this.collection = collection;
      }

      @Override
      public T next() {
        return super.next();
      }

      @Override
      public void remove() {
        // TODO: test removal at iteration rigorously
        collection.__remove(currentKey, currentValue);
      }
    }

    @Override
    public Set<K> keySet() {
      Set<K> keySet = null;

      if (keySet == null) {
        keySet = new AbstractSet<K>() {
          @Override
          public Iterator<K> iterator() {
            return TransientTrieSetMultimap_BleedingEdge.this.keyIterator();
          }

          @Override
          public int size() {
            return TransientTrieSetMultimap_BleedingEdge.this.sizeDistinct();
          }

          @Override
          public boolean isEmpty() {
            return TransientTrieSetMultimap_BleedingEdge.this.isEmpty();
          }

          @Override
          public void clear() {
            throw new UnsupportedOperationException();
          }

          @Override
          public boolean contains(Object k) {
            return TransientTrieSetMultimap_BleedingEdge.this.containsKey(k);
          }
        };
      }

      return keySet;
    }

    @Override
    public Collection<V> values() {
      Collection<V> values = null;

      if (values == null) {
        values = new AbstractCollection<V>() {
          @Override
          public Iterator<V> iterator() {
            return TransientTrieSetMultimap_BleedingEdge.this.valueIterator();
          }

          @Override
          public int size() {
            return TransientTrieSetMultimap_BleedingEdge.this.size();
          }

          @Override
          public boolean isEmpty() {
            return TransientTrieSetMultimap_BleedingEdge.this.isEmpty();
          }

          @Override
          public void clear() {
            throw new UnsupportedOperationException();
          }

          @Override
          public boolean contains(Object v) {
            return TransientTrieSetMultimap_BleedingEdge.this.containsValue(v);
          }
        };
      }

      return values;
    }

    @Override
    public Set<java.util.Map.Entry<K, V>> entrySet() {
      Set<java.util.Map.Entry<K, V>> entrySet = null;

      if (entrySet == null) {
        entrySet = new AbstractSet<java.util.Map.Entry<K, V>>() {
          @Override
          public Iterator<java.util.Map.Entry<K, V>> iterator() {
            return new Iterator<Map.Entry<K, V>>() {
              private final Iterator<Map.Entry<K, V>> i = entryIterator();

              @Override
              public boolean hasNext() {
                return i.hasNext();
              }

              @Override
              public Map.Entry<K, V> next() {
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
            return TransientTrieSetMultimap_BleedingEdge.this.size();
          }

          @Override
          public boolean isEmpty() {
            return TransientTrieSetMultimap_BleedingEdge.this.isEmpty();
          }

          @Override
          public void clear() {
            throw new UnsupportedOperationException();
          }

          @Override
          public boolean contains(Object k) {
            return TransientTrieSetMultimap_BleedingEdge.this.containsKey(k);
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

      if (other instanceof TransientTrieSetMultimap_BleedingEdge) {
        TransientTrieSetMultimap_BleedingEdge<?, ?> that =
            (TransientTrieSetMultimap_BleedingEdge<?, ?>) other;

        if (this.cachedSize != that.cachedSize) {
          return false;
        }

        if (this.hashCode != that.hashCode) {
          return false;
        }

        return rootNode.equals(that.rootNode);
      } else if (other instanceof SetMultimap) {
        SetMultimap that = (SetMultimap) other;

        if (this.size() != that.size()) {
          return false;
        }

        for (
            Iterator<Map.Entry> it = that.entrySet().iterator(); it.hasNext(); ) {
          Map.Entry entry = it.next();

          try {
            final K key = (K) entry.getKey();
            final Optional<AbstractSetNode<V>> result =
                rootNode.findByKey(key, transformHashCode(key.hashCode()), 0, cmp);

            if (!result.isPresent()) {
              return false;
            } else {
              final AbstractSetNode<V> valColl = (AbstractSetNode<V>) entry.getValue();

              if (!cmp.equals(result.get(), valColl)) {
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
    public SetMultimap.Immutable<K, V> freeze() {
      if (mutator.get() == null) {
        throw new IllegalStateException("Transient already frozen.");
      }

      mutator.set(null);
      return new TrieSetMultimap_HHAMT_Specialized_Path_Interlinked<K, V>(cmp, rootNode, hashCode,
          cachedSize);
    }
  }

  private abstract static class DataLayoutHelper extends CompactSetMultimapNode<Object, Object> {

    private static final long[] arrayOffsets =
        arrayOffsets(DataLayoutHelper.class, new String[]{"slot0", "slot1"});

    public final Object slot0 = null;

    public final Object slot1 = null;

    private DataLayoutHelper() {
      super(null, 0L);
    }

  }

}
