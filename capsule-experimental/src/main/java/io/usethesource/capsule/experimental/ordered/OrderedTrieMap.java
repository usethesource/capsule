/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.capsule.experimental.ordered;

import java.lang.ref.SoftReference;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;

import io.usethesource.capsule.api.experimental.Map;
import io.usethesource.capsule.util.ArrayUtils;
import io.usethesource.capsule.util.iterator.SupplierIterator;

import static java.lang.System.arraycopy;

/**
 * Immutable insertion-ordered map implemented as a hash trie.
 *
 * NOTE: this is currently the sketch of an implementation that is not yet fully implemented.
 */
public final class OrderedTrieMap<K, V> implements Map.Immutable<K, V> {

  private static final Node EMPTY_NODE = new BitmapIndexedNode(0, 0, new Object[]{});

  private static final OrderedTrieMap EMPTY_MAP = new OrderedTrieMap(EMPTY_NODE, 0, 0);

  private static final boolean DEBUG = false;

  private final Node<K, V> rootNode;
  private final int cachedSize;

  /*
   * This field and subsequent IDs stored in nodes are not part of the object identity.
   */
  private final int nextSequenceId;

  private static final boolean INSERTION_ORDER_CACHING_ENABLED = true;

  private static final int INSERTION_ORDER_CACHING_THRESHOLD = 8;

  SoftReference<ImmutablePayloadTuple<K, V>[]> cachedInsertionOrderSequence;

  private OrderedTrieMap(Node<K, V> rootNode, int cachedSize, int nextSequenceId) {
    this.rootNode = rootNode;
    this.cachedSize = cachedSize;

    this.nextSequenceId = nextSequenceId;

    if (DEBUG) {
      assert checkSize(cachedSize);
    }
  }

  public static final <K, V> OrderedTrieMap<K, V> of() {
    return (OrderedTrieMap<K, V>) EMPTY_MAP;
  }

  private boolean checkSize(final int targetSize) {
    int size = 0;

    for (Iterator<?> it = new ValueIterator<>(rootNode); it.hasNext(); size++) {
    }

    return size == targetSize;
  }

  @Deprecated
  public java.util.Map.Entry<K, V> getLastEntry() {
    ImmutablePayloadTuple<K, V>[] sortedEntries = getAndCacheSortedEntryArray();
    return sortedEntries[cachedSize - 1];
  }

  private static final <K, V> K extractKey(final ImmutablePayloadTuple<K, V> tuple) {
    return tuple.getKey();
  }

  public static final int transformHashCode(final int hash) {
    return hash;
  }

  @Override
  public boolean contains(final Object key) {
    try {
      final int keyHash = key.hashCode();

      return rootNode.containsKey(key, transformHashCode(keyHash), 0);
    } catch (ClassCastException unused) {
      return false;
    }
  }

  @Override
  public boolean containsValue(final Object o) {
    for (Iterator<V> iterator = valueIterator(); iterator.hasNext(); ) {
      if (iterator.next().equals(o)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public java.util.Optional<V> apply(K key) {
    final int keyHash = key.hashCode();
    return rootNode.find(key, transformHashCode(keyHash), 0).map(java.util.Map.Entry::getValue);
  }

  private V get(final Object key) {
    try {
      final int keyHash = key.hashCode();
      final Optional<V> result =
          rootNode.find(key, transformHashCode(keyHash), 0).map(java.util.Map.Entry::getValue);

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
  public OrderedTrieMap<K, V> insert(K key, V val) {
    final int keyHash = key.hashCode();
    final UpdateReport report = new UpdateReport();

    final ImmutablePayloadTuple<K, V> payloadTuple =
        ImmutablePayloadTuple.of(nextSequenceId, key, val, transformHashCode(keyHash));

    final Node<K, V> newRootNode =
        rootNode.updated(payloadTuple, transformHashCode(keyHash), 0, report);

    if (report.isTrieModified()) {
      // invalidate cache
      cachedInsertionOrderSequence = null;

      if (report.isTrieElementReplaced()) {
        return new OrderedTrieMap<>(newRootNode, cachedSize, nextSequenceId);
      } else {
        return new OrderedTrieMap<>(newRootNode, cachedSize + 1, nextSequenceId + 1);
      }
    }

    return this;
  }

  @Override
  public OrderedTrieMap<K, V> remove(K key) {
    final int keyHash = key.hashCode();
    final UpdateReport report = new UpdateReport();

    final Node<K, V> newRootNode = rootNode.removed(key, transformHashCode(keyHash), 0, report);

    if (report.isTrieModified()) {
      // invalidate cache
      cachedInsertionOrderSequence = null;

      return new OrderedTrieMap<>(newRootNode, cachedSize - 1, nextSequenceId);
    }

    return this;
  }

  @Override
  public long size() {
    return cachedSize;
  }

  @Override
  public boolean isEmpty() {
    return cachedSize == 0;
  }

  /**
   * Key iterator that ignores insertion order and consequently yields better performance.
   */
  public Iterator<K> keyIterator() {
    return new KeyIterator<>(rootNode);
  }

  /**
   * Key iterator that ignores insertion order and consequently yields better performance.
   */
  public Iterator<V> valueIterator() {
    return new ValueIterator<>(rootNode);
  }

  @Override
  public Iterator<java.util.Map.Entry<K, V>> entryIterator() {
    return (Iterator) new EntryIterator<>(rootNode);
  }

  public Iterator<K> orderedKeyIterator() {
    return new ForwardKeyIterator<>(getAndCacheSortedEntryArray());
  }

  public Iterator<V> orderedValueIterator() {
    return new ForwardElementIterator<>(getAndCacheSortedEntryArray());
  }

  public Iterator<ImmutablePayloadTuple<K, V>> unorderedTupleIterator() {
    return new EntryIterator<>(rootNode);
  }

  public Iterator<? super ImmutablePayloadTuple<K, V>> orderedEntryIterator() {
    return new ForwardEntryIterator<>(getAndCacheSortedEntryArray());
  }

  public Iterator<K> reverseOrderedKeyIterator() {
    return new ReverseKeyIterator<>(getAndCacheSortedEntryArray());
  }

  public Iterator<V> reverseOrderedValueIterator() {
    return new ReverseValueIterator<>(getAndCacheSortedEntryArray());
  }

  public Iterator<? super ImmutablePayloadTuple<K, V>> reverseOrderedEntryIterator() {
    return new ReverseEntryIterator<>(getAndCacheSortedEntryArray());
  }

  // @Override
  // public Set<Object> keySet() {
  // Set<Object> keySet = null;
  //
  // if (keySet == null) {
  // keySet = new AbstractSet<Object>() {
  // @Override
  // public Iterator<Object> iterator() {
  // return OrderedTrieMap.this.orderedKeyIterator();
  // }
  //
  // @Override
  // public int size() {
  // return OrderedTrieMap.this.size();
  // }
  //
  // @Override
  // public boolean isEmpty() {
  // return OrderedTrieMap.this.isEmpty();
  // }
  //
  // @Override
  // public void clear() {
  // OrderedTrieMap.this.clear();
  // }
  //
  // @Override
  // public boolean contains(Object key) {
  // return OrderedTrieMap.this.containsKey(key);
  // }
  // };
  // }
  //
  // return keySet;
  // }

  // @Override
  // public Collection<Property> values() {
  // Collection<Property> values = null;
  //
  // if (values == null) {
  // values = new AbstractCollection<Property>() {
  // @Override
  // public Iterator<Property> iterator() {
  // return OrderedTrieMap.this.orderedValueIterator();
  // }
  //
  // @Override
  // public int size() {
  // return OrderedTrieMap.this.size();
  // }
  //
  // @Override
  // public boolean isEmpty() {
  // return OrderedTrieMap.this.isEmpty();
  // }
  //
  // @Override
  // public void clear() {
  // OrderedTrieMap.this.clear();
  // }
  //
  // @Override
  // public boolean contains(Object value) {
  // return OrderedTrieMap.this.containsValue(value);
  // }
  // };
  // }
  //
  // return values;
  // }

  // @Override
  // public Set<java.util.Map.Entry<Object, Property>> entrySet() {
  // Set<java.util.Map.Entry<Object, Property>> entrySet = null;
  //
  // if (entrySet == null) {
  // entrySet = new AbstractSet<java.util.Map.Entry<Object, Property>>() {
  // @Override
  // public Iterator<java.util.Map.Entry<Object, Property>> iterator() {
  // return OrderedTrieMap.this.entryIterator();
  // }
  //
  // @Override
  // public int size() {
  // return OrderedTrieMap.this.size();
  // }
  //
  // @Override
  // public boolean isEmpty() {
  // return OrderedTrieMap.this.isEmpty();
  // }
  //
  // @Override
  // public void clear() {
  // OrderedTrieMap.this.clear();
  // }
  //
  // @Override
  // public boolean contains(Object key) {
  // return OrderedTrieMap.this.containsKey(key);
  // }
  // };
  // }
  //
  // return entrySet;
  // }

  @Override
  public int hashCode() {
    int hash = 0;

    for (Iterator<java.util.Map.Entry<K, V>> it = entryIterator(); it.hasNext(); ) {
      final java.util.Map.Entry<K, V> entry = it.next();
      hash += entry.hashCode();
    }

    return hash;
  }

  @Override
  public boolean equals(final Object other) {
    if (other == this) {
      return true;
    }
    if (other == null) {
      return false;
    }
    if (getClass() != other.getClass()) {
      return false;
    }

    OrderedTrieMap that = (OrderedTrieMap) other;

    if (this.cachedSize != that.cachedSize) {
      return false;
    }

    return rootNode.equals(that.rootNode);
  }

  @Override
  public String toString() {
    final StringBuilder bldr = new StringBuilder();
    bldr.append('{');

    if (cachedSize > 1) {
      Iterator<java.util.Map.Entry<K, V>> it = entryIterator();

      // append head
      bldr.append(it.next());

      // append tail
      while (it.hasNext()) {
        bldr.append(", ");
        bldr.append(it.next());
      }
    }

    bldr.append('}');
    return bldr.toString();
  }

  private ImmutablePayloadTuple<K, V>[] toSortedEntryArray() {
    final ImmutablePayloadTuple<K, V>[] arr = new ImmutablePayloadTuple[cachedSize];

    Iterator<ImmutablePayloadTuple<K, V>> it = unorderedTupleIterator();

    for (int i = 0; i < cachedSize; i++) {
      assert it.hasNext();
      arr[i] = it.next();
    }

    Arrays.sort(arr, ImmutablePayloadTuple.ASCENDING_COMPARATOR);

    return arr;
  }

  private ImmutablePayloadTuple<K, V>[] getAndCacheSortedEntryArray() {
    ImmutablePayloadTuple<K, V>[] arr =
        cachedInsertionOrderSequence == null ? null : cachedInsertionOrderSequence.get();

    if (arr == null) {
      arr = toSortedEntryArray();

      if (INSERTION_ORDER_CACHING_ENABLED && cachedSize > INSERTION_ORDER_CACHING_THRESHOLD) {
        cachedInsertionOrderSequence =
            new SoftReference<OrderedTrieMap.ImmutablePayloadTuple<K, V>[]>(arr);
      }
    }

    return arr;
  }

  private static final class UpdateReport {

    private boolean isModified;
    private boolean isElementReplaced;

    // // update: neither element, nor element count changed
    public UpdateReport() {
      this.isModified = false;
      this.isElementReplaced = false;
    }

    // update: inserted/removed single element, element count changed
    public void setTrieModified() {
      this.isModified = true;
      this.isElementReplaced = false;
    }

    public boolean isTrieModified() {
      return isModified;
    }

    public void setTrieElementReplaced() {
      this.isModified = true;
      this.isElementReplaced = true;
    }

    public boolean isTrieElementReplaced() {
      return isElementReplaced;
    }
  }

  static interface Node<K, V> {

    boolean containsKey(final Object key, final int keyHash, final int shift);

    // boolean containsValue(final V val, final int keyHash, final int shift);

    Optional<java.util.Map.Entry<K, V>> find(final Object key, final int keyHash, final int shift);

    Node<K, V> updated(ImmutablePayloadTuple<K, V> payloadTuple, final int keyHash, final int shift,
        final UpdateReport report);

    Node<K, V> removed(final Object key, final int keyHash, final int shift,
        final UpdateReport report);

    boolean hasNodes();

    int nodeArity();

    Node<K, V> getNode(final int index);

    boolean hasElements();

    int elementArity();

    ImmutablePayloadTuple<K, V> getElement(final int index);

    Object getKey(final int index);

    int getSequenceId(final int index);

    static final byte SIZE_EMPTY = 0b00;
    static final byte SIZE_ONE = 0b01;
    static final byte SIZE_MORE_THAN_ONE = 0b10;

    /**
     * Abstract predicate over a node's size. Value can be either {@value #SIZE_EMPTY},
     * {@value #SIZE_ONE}, or {@value #SIZE_MORE_THAN_ONE}.
     *
     * @return size predicate
     */
    byte sizePredicate();

  }

  private static final class BitmapIndexedNode<K, V> implements Node<K, V> {

    private final int nodeMap;
    private final int dataMap;

    private final Object[] nodes;

    private BitmapIndexedNode(final int nodeMap, final int dataMap, final Object[] nodes) {

      this.nodeMap = nodeMap;
      this.dataMap = dataMap;

      this.nodes = nodes;

      if (DEBUG) {

        assert (java.lang.Integer.bitCount(dataMap)
            + java.lang.Integer.bitCount(nodeMap) == nodes.length);

        for (int i = 0; i < elementArity(); i++) {
          assert ((nodes[i] instanceof Node) == false);
        }
        for (int i = elementArity(); i < nodes.length; i++) {
          assert ((nodes[i] instanceof Node) == true);
        }
      }

      assert nodeInvariant();
    }

    static final <K, V> BitmapIndexedNode<K, V> newElementSingleton(int dataMap,
        java.util.Map.Entry<K, V> element0) {
      return new BitmapIndexedNode<>(0, dataMap, new Object[]{element0});
    }

    static final <K, V> BitmapIndexedNode<K, V> newElementTuple(int dataMap,
        java.util.Map.Entry<K, V> element0, java.util.Map.Entry<K, V> element1) {
      return new BitmapIndexedNode<>(0, dataMap, new Object[]{element0, element1});
    }

    static final <K, V> BitmapIndexedNode<K, V> newSubnodeSingleton(int nodeMap,
        Node<K, V> subNode) {
      return new BitmapIndexedNode<>(nodeMap, 0, new Object[]{subNode});
    }

    @Deprecated
      // Only used in nodeInvariant()
    int arity() {
      return elementArity() + nodeArity();
    }

    @Deprecated
      // Only used in nodeInvariant()
    int size() {
      final Iterator<?> it = new ValueIterator<>(this);

      int size = 0;
      while (it.hasNext()) {
        size += 1;
        it.next();
      }

      return size;
    }

    boolean nodeInvariant() {
      boolean inv1 = (size() - elementArity() >= 2 * (arity() - elementArity()));
      boolean inv2 = (this.arity() == 0) ? sizePredicate() == SIZE_EMPTY : true;
      boolean inv3 =
          (this.arity() == 1 && elementArity() == 1) ? sizePredicate() == SIZE_ONE : true;
      boolean inv4 = (this.arity() >= 2) ? sizePredicate() == SIZE_MORE_THAN_ONE : true;

      boolean inv5 = (this.nodeArity() >= 0) && (this.elementArity() >= 0)
          && ((this.elementArity() + this.nodeArity()) == this.arity());

      return inv1 && inv2 && inv3 && inv4 && inv5;
    }

    @Override
    public ImmutablePayloadTuple<K, V> getElement(final int index) {
      return (ImmutablePayloadTuple<K, V>) nodes[index];
    }

    @Override
    public K getKey(final int index) {
      return extractKey(getElement(index));
    }

    @Override
    public int getSequenceId(final int index) {
      return getElement(index).sequenceId;
    }

    @Override
    public Node<K, V> getNode(final int index) {
      return (Node<K, V>) nodes[nodes.length - 1 - index];
    }

    @Override
    public boolean hasElements() {
      return dataMap != 0;
    }

    @Override
    public int elementArity() {
      return (dataMap == 0) ? 0 : java.lang.Integer.bitCount(dataMap);
    }

    @Override
    public boolean hasNodes() {
      return nodeMap != 0;
    }

    @Override
    public int nodeArity() {
      return (nodeMap == 0) ? 0 : java.lang.Integer.bitCount(nodeMap);
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 0;
      result = prime * result + (nodeMap);
      result = prime * result + (dataMap);
      result = prime * result + Arrays.hashCode(nodes);
      return result;
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
      BitmapIndexedNode that = (BitmapIndexedNode) other;
      if (nodeMap != that.nodeMap) {
        return false;
      }
      if (dataMap != that.dataMap) {
        return false;
      }
      if (!ArrayUtils.equals(nodes, that.nodes)) {
        return false;
      }
      return true;
    }

    /**
     * @return 0 <= mask <= 2^BIT_PARTITION_SIZE - 1
     */
    private static byte recoverMask(int bitmap, byte i_th) {
      assert 1 <= i_th && i_th <= 32;

      int map = bitmap;
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
      final StringBuilder bldr = new StringBuilder();
      bldr.append('[');

      for (byte i = 0; i < elementArity(); i++) {
        final byte pos = recoverMask(dataMap, (byte) (i + 1));
        bldr.append(String.format("@%d<#%d>", pos, Objects.hashCode(extractKey(getElement(i)))));

        if (!((i + 1) == elementArity())) {
          bldr.append(", ");
        }
      }

      if (elementArity() > 0 && nodeArity() > 0) {
        bldr.append(", ");
      }

      for (byte i = 0; i < nodeArity(); i++) {
        final byte pos = recoverMask(nodeMap, (byte) (i + 1));
        bldr.append(String.format("@%d: %s", pos, getNode(i)));

        if (!((i + 1) == nodeArity())) {
          bldr.append(", ");
        }
      }

      bldr.append(']');
      return bldr.toString();
    }

    @Override
    public byte sizePredicate() {
      if (this.nodeArity() == 0) {
        switch (this.elementArity()) {
          case 0:
            return SIZE_EMPTY;
          case 1:
            return SIZE_ONE;
          default:
            return SIZE_MORE_THAN_ONE;
        }
      } else {
        return SIZE_MORE_THAN_ONE;
      }
    }

    Node<K, V> copyAndSetNode(final int bitpos, final Node<K, V> node) {
      final int idx = this.nodes.length - 1 - index(nodeMap, bitpos);

      final Object[] newNodes = new Object[nodes.length];

      // copy 'nodes' and update 1 element(s) at position 'idx'
      arraycopy(nodes, 0, newNodes, 0, nodes.length);
      newNodes[idx] = node;

      return new BitmapIndexedNode<>(nodeMap, dataMap, newNodes);
    }

    Node<K, V> copyAndInsertValue(final int bitpos, final ImmutablePayloadTuple<K, V> element) {
      final int idx = index(dataMap, bitpos);

      final Object[] newNodes = new Object[nodes.length + 1];

      // copy 'nodes' and insert 1 element(s) at position 'idx'
      arraycopy(nodes, 0, newNodes, 0, idx);
      newNodes[idx] = element;
      arraycopy(nodes, idx, newNodes, idx + 1, nodes.length - idx);

      return new BitmapIndexedNode<>(nodeMap, dataMap | bitpos, newNodes);
    }

    Node<K, V> copyAndSetValue(final int bitpos, final ImmutablePayloadTuple<K, V> element) {
      final int idx = index(dataMap, bitpos);

      final Object[] newNodes = new Object[nodes.length];

      // copy 'nodes' and set element(s) at position 'idx'
      arraycopy(nodes, 0, newNodes, 0, nodes.length);
      newNodes[idx] = element;

      return new BitmapIndexedNode<>(nodeMap, dataMap, newNodes);
    }

    Node<K, V> copyAndRemoveValue(final int bitpos) {
      final int idx = index(dataMap, bitpos);

      final Object[] newNodes = new Object[nodes.length - 1];

      // copy 'nodes' and remove 1 element(s) at position 'idx'
      arraycopy(nodes, 0, newNodes, 0, idx);
      arraycopy(nodes, idx + 1, newNodes, idx, nodes.length - idx - 1);

      return new BitmapIndexedNode<>(nodeMap, dataMap ^ bitpos, newNodes);
    }

    Node<K, V> copyAndMigrateFromInlineToNode(final int bitpos, final Node<K, V> node) {

      final int idxOld = index(dataMap, bitpos);
      final int idxNew = nodes.length - 1 - index(nodeMap, bitpos);

      final Object[] newNodes = new Object[nodes.length];

      // copy 'nodes' and remove 1 element(s) at position 'idxOld' and
      // insert 1 element(s) at position 'idxNew'
      assert idxOld <= idxNew;
      arraycopy(nodes, 0, newNodes, 0, idxOld);
      arraycopy(nodes, idxOld + 1, newNodes, idxOld, idxNew - idxOld);
      newNodes[idxNew] = node;
      arraycopy(nodes, idxNew + 1, newNodes, idxNew + 1, nodes.length - idxNew - 1);

      return new BitmapIndexedNode<>(nodeMap | bitpos, dataMap ^ bitpos, newNodes);
    }

    Node<K, V> copyAndMigrateFromNodeToInline(final int bitpos, final Node<K, V> node) {

      final int idxOld = nodes.length - 1 - index(nodeMap, bitpos);
      final int idxNew = index(dataMap, bitpos);

      final Object[] newNodes = new Object[nodes.length];

      // copy 'nodes' and remove 1 element(s) at position 'idxOld' and
      // insert 1 element(s) at position 'idxNew'
      assert idxOld >= idxNew;
      arraycopy(nodes, 0, newNodes, 0, idxNew);
      newNodes[idxNew] = node.getElement(0);
      arraycopy(nodes, idxNew, newNodes, idxNew + 1, idxOld - idxNew);
      arraycopy(nodes, idxOld + 1, newNodes, idxOld + 1, nodes.length - idxOld - 1);

      return new BitmapIndexedNode<>(nodeMap ^ bitpos, dataMap | bitpos, newNodes);
    }

    @Override
    public boolean containsKey(final Object key, final int keyHash, final int shift) {
      final int mask = mask(keyHash, shift);
      final int bitpos = bitpos(mask);

      if ((dataMap & bitpos) != 0) {
        final int index = index(dataMap, mask, bitpos);
        return getKey(index).equals(key);
      }

      if ((nodeMap & bitpos) != 0) {
        final int index = index(nodeMap, mask, bitpos);
        return getNode(index).containsKey(key, keyHash, shift + bitPartitionSize());
      }

      return false;
    }

    // @Override
    // public boolean containsValue(final ImmutableMapEntry<K, V> element, final int keyHash, final
    // int shift) {
    // final int mask = mask(keyHash, shift);
    // final int bitpos = bitpos(mask);
    //
    // if ((dataMap & bitpos) != 0) {
    // final int index = index(dataMap, mask, bitpos);
    // return getElement(index).equals(element);
    // }
    //
    // if ((nodeMap & bitpos) != 0) {
    // final int index = index(nodeMap, mask, bitpos);
    // return getNode(index).containsValue(element, keyHash, shift + bitPartitionSize());
    // }
    //
    // return false;
    // }

    @Override
    public Optional<java.util.Map.Entry<K, V>> find(final Object key, final int keyHash,
        final int shift) {
      final int mask = mask(keyHash, shift);
      final int bitpos = bitpos(mask);

      if ((dataMap & bitpos) != 0) {
        final int index = index(dataMap, mask, bitpos);
        if (getKey(index).equals(key)) {
          return Optional.of(getElement(index));
        }

        return Optional.empty();
      }

      if ((nodeMap & bitpos) != 0) {
        final int index = index(nodeMap, mask, bitpos);
        return getNode(index).find(key, keyHash, shift + bitPartitionSize());
      }

      return Optional.empty();
    }

    @Override
    public Node<K, V> updated(ImmutablePayloadTuple<K, V> newTuple, final int keyHash,
        final int shift, final UpdateReport report) {

      final int mask = mask(keyHash, shift);
      final int bitpos = bitpos(mask);

      if ((dataMap & bitpos) != 0) { // inplace value
        final int dataIndex = index(dataMap, bitpos);
        final ImmutablePayloadTuple<K, V> currentTuple = getElement(dataIndex);

        if (currentTuple.getKey().equals(newTuple.getKey())) {
          // update mapping
          report.setTrieElementReplaced();
          return copyAndSetValue(bitpos, currentTuple.withUpdatedValue(newTuple.getValue()));
        } else {
          final int currentKeyHash = getKey(dataIndex).hashCode();
          final int currentSequenceId = getSequenceId(dataIndex);

          final Node<K, V> subNodeNew = mergeTwoElements(currentTuple,
              transformHashCode(currentKeyHash), newTuple, keyHash, shift + bitPartitionSize());

          report.setTrieModified();
          return copyAndMigrateFromInlineToNode(bitpos, subNodeNew);
        }
      } else if ((nodeMap & bitpos) != 0) { // node (not value)
        final int nodeIndex = index(nodeMap, bitpos);

        final Node<K, V> subNode = getNode(nodeIndex);
        final Node<K, V> subNodeNew =
            subNode.updated(newTuple, keyHash, shift + bitPartitionSize(), report);

        if (report.isTrieModified()) {
          return copyAndSetNode(bitpos, subNodeNew);
        } else {
          return this;
        }
      } else {
        // no value
        report.setTrieModified();
        return copyAndInsertValue(bitpos, newTuple);
      }
    }

    @Override
    public Node<K, V> removed(final Object key, final int keyHash, final int shift,
        final UpdateReport report) {
      final int mask = mask(keyHash, shift);
      final int bitpos = bitpos(mask);

      if ((dataMap & bitpos) != 0) { // inplace value
        final int dataIndex = index(dataMap, bitpos);

        if (getKey(dataIndex).equals(key)) {
          report.setTrieModified();

          if (this.elementArity() == 2 && this.nodeArity() == 0) {
            /*
             * Create new node with remaining pair. The new node will a) either become the new root
             * returned, or b) unwrapped and inlined during returning.
             */
            final int newDataMap = (shift == 0) ? dataMap ^ bitpos : bitpos(mask(keyHash, 0));

            return BitmapIndexedNode.newElementSingleton(newDataMap, getElement(1 - dataIndex));
          } else {
            return copyAndRemoveValue(bitpos);
          }
        } else {
          return this;
        }
      } else if ((nodeMap & bitpos) != 0) { // node (not value)
        final int nodeIndex = index(nodeMap, bitpos);

        final Node<K, V> subNode = getNode(nodeIndex);
        final Node<K, V> subNodeNew =
            subNode.removed(key, keyHash, shift + bitPartitionSize(), report);

        if (!report.isTrieModified()) {
          return this;
        }

        if (subNodeNew.sizePredicate() == SIZE_ONE) {
          if (this.elementArity() == 0 && this.nodeArity() == 1) {
            // escalate (singleton or empty) result
            return subNodeNew;
          } else {
            // inline value (move to front)
            return copyAndMigrateFromNodeToInline(bitpos, subNodeNew);
          }
        } else {
          assert subNode.sizePredicate() == SIZE_MORE_THAN_ONE;

          // modify current node (set replacement node)
          return copyAndSetNode(bitpos, subNodeNew);
        }
      } else {
        // no value
        return this;
      }
    }

    /*************************/
    /*** UTILITY FUNCTIONS ***/
    /*************************/

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
      return 1 << mask;
    }

    static final int index(final int bitmap, final int bitpos) {
      return java.lang.Integer.bitCount(bitmap & (bitpos - 1));
    }

    static final int index(final int bitmap, final int mask, final int bitpos) {
      return (bitmap == -1) ? mask : index(bitmap, bitpos);
    }

    static final <K, V> Node<K, V> mergeTwoElements(final ImmutablePayloadTuple<K, V> element0,
        final int keyHash0, final ImmutablePayloadTuple<K, V> element1, final int keyHash1,
        final int shift) {
      Object key0 = extractKey(element0);
      Object key1 = extractKey(element1);
      assert !(key0.equals(key1));

      if (shift >= hashCodeLength()) {
        return new HashCollisionNode<>(keyHash0, element0, element1);
      }

      final int mask0 = mask(keyHash0, shift);
      final int mask1 = mask(keyHash1, shift);

      if (mask0 != mask1) {
        // both nodes fit on same level
        final int dataMap = bitpos(mask0) | bitpos(mask1);

        if (mask0 < mask1) {
          return BitmapIndexedNode.newElementTuple(dataMap, element0, element1);
        } else {
          return BitmapIndexedNode.newElementTuple(dataMap, element1, element0);
        }
      } else {
        final Node<K, V> node =
            mergeTwoElements(element0, keyHash0, element1, keyHash1, shift + bitPartitionSize());
        // values fit on next level
        final int nodeMap = bitpos(mask0);

        return BitmapIndexedNode.newSubnodeSingleton(nodeMap, node);
      }
    }

  }

  private static final class HashCollisionNode<K, V> implements Node<K, V> {

    private final int hash;
    private final ImmutablePayloadTuple<K, V>[] elements;

    private HashCollisionNode(final int hash, final ImmutablePayloadTuple<K, V> element0,
        final ImmutablePayloadTuple<K, V> element1) {
      this.hash = hash;

      this.elements = newElementArray(element0, element1);
    }

    private HashCollisionNode(final int hash, final ImmutablePayloadTuple<K, V>[] elements) {
      if (elements.length <= 2) {
        throw new IllegalArgumentException("At least two elements are required.");
      }
      this.hash = hash;
      this.elements = elements;
    }

    /*
     * TODO: find a right place for this utility method.
     */
    @SafeVarargs
    private static final <K, V> ImmutablePayloadTuple<K, V>[] newElementArray(
        ImmutablePayloadTuple<K, V>... elements) {
      return elements;
    }

    /*
     * TODO: find a right place for this utility method.
     */
    private static final <K, V> ImmutablePayloadTuple<K, V>[] newElementArray(int size) {
      return new ImmutablePayloadTuple[size];
    }

    @Override
    public boolean containsKey(final Object key, final int keyHash, final int shift) {
      if (this.hash == keyHash) {
        for (ImmutablePayloadTuple<K, V> e : elements) {
          if (extractKey(e).equals(key)) {
            return true;
          }
        }
      }
      return false;
    }

    // @Override
    // public boolean containsValue(final Property element, final int keyHash, final int shift) {
    // if (this.hash == keyHash) {
    // for (Property e : elements)
    // if (e.equals(element))
    // return true;
    // }
    // return false;
    // }

    @Override
    public Optional<java.util.Map.Entry<K, V>> find(final Object key, final int keyHash,
        final int shift) {
      if (this.hash == keyHash) {
        for (ImmutablePayloadTuple<K, V> e : elements) {
          if (extractKey(e).equals(key)) {
            return Optional.of(e);
          }
        }
      }
      return Optional.empty();
    }

    @Override
    public Node<K, V> updated(ImmutablePayloadTuple<K, V> newTuple, final int keyHash,
        final int shift, final UpdateReport report) {
      assert this.hash == keyHash;

      int indexOfKey = -1;

      for (int i = 0; i < elementArity() && indexOfKey == -1; i++) {
        final ImmutablePayloadTuple<K, V> currentTuple = getElement(i);

        if (currentTuple.getKey().equals(newTuple.getKey())) {
          indexOfKey = i;
        }
      }

      if (indexOfKey == -1) {
        // insert
        final ImmutablePayloadTuple<K, V>[] extendedElements = newElementArray(elements.length + 1);
        arraycopy(elements, 0, extendedElements, 0, elements.length);
        extendedElements[elements.length] = newTuple;

        report.setTrieModified();
        return new HashCollisionNode<>(keyHash, extendedElements);
      } else {
        // replace
        final ImmutablePayloadTuple<K, V>[] extendedElements = newElementArray(elements.length);
        arraycopy(elements, 0, extendedElements, 0, elements.length);
        extendedElements[indexOfKey] = newTuple;

        report.setTrieElementReplaced();
        return new HashCollisionNode<>(keyHash, extendedElements);
      }
    }

    @Override
    public Node<K, V> removed(final Object key, final int keyHash, final int shift,
        final UpdateReport report) {
      assert this.hash == keyHash;

      int indexOfKey = -1;

      for (int i = 0; i < elementArity() && indexOfKey == -1; i++) {
        if (getKey(i).equals(key)) {
          indexOfKey = i;
        }
      }

      if (indexOfKey == -1) {
        return this;
      } else {
        if (elements.length == 2) {
          /*
           * Create root node with singleton element. This node will be a) either be the new root
           * returned, or b) unwrapped and inlined.
           */
          final int dataMap = BitmapIndexedNode.bitpos(BitmapIndexedNode.mask(hash, 0));
          /*
           * TODO: create Utils class for utility functions that are currenlty in BitmapIndexedNode
           * (see usage above)
           */

          report.setTrieModified();
          return BitmapIndexedNode.newElementSingleton(dataMap, elements[1 - indexOfKey]);
        } else {
          final ImmutablePayloadTuple<K, V>[] reducedElements =
              newElementArray(elements.length - 1);
          arraycopy(elements, 0, reducedElements, 0, indexOfKey);
          arraycopy(elements, indexOfKey + 1, reducedElements, indexOfKey,
              elements.length - indexOfKey - 1);

          report.setTrieModified();
          return new HashCollisionNode<>(keyHash, reducedElements);
        }
      }
    }

    @Override
    public boolean hasElements() {
      return true;
    }

    @Override
    public int elementArity() {
      return elements.length;
    }

    @Override
    public boolean hasNodes() {
      return false;
    }

    @Override
    public int nodeArity() {
      return 0;
    }

    @Override
    public byte sizePredicate() {
      return SIZE_MORE_THAN_ONE;
    }

    @Override
    public ImmutablePayloadTuple<K, V> getElement(final int index) {
      return elements[index];
    }

    @Override
    public Object getKey(final int index) {
      return extractKey(getElement(index));
    }

    @Override
    public int getSequenceId(final int index) {
      return elements[index].sequenceId;
    }

    @Override
    public Node<K, V> getNode(int index) {
      throw new UnsupportedOperationException(
          "Hash collision nodes are leaf nodes, without further sub-trees.");
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 0;
      result = prime * result + hash;
      result = prime * result + Arrays.hashCode(elements);
      return result;
    }

    @Override
    public boolean equals(Object other) {
      if (null == other) {
        return false;
      }
      if (this == other) {
        return true;
      }
      if (getClass() != other.getClass()) {
        return false;
      }

      HashCollisionNode that = (HashCollisionNode) other;

      if (hash != that.hash) {
        return false;
      }

      if (elementArity() != that.elementArity()) {
        return false;
      }

      /*
       * Linear scan for each element, because of arbitrary element order.
       */
      outerLoop:
      for (ImmutablePayloadTuple<K, V> e1 : elements) {
        innerLoop:
        for (ImmutablePayloadTuple<K, V> e2 : that.elements) {
          if (e1.equals(e2)) {
            continue outerLoop;
          }
        }
        return false;
      }

      return true;
    }
  }

  /**
   * Iterator skeleton that uses a fixed stack in depth.
   */
  private static abstract class AbstractIterator<K, V> {

    private static final int MAX_DEPTH = 7;

    protected int currentValueCursor;
    protected int currentValueLength;
    protected Node<K, V> currentValueNode;

    private int currentStackLevel = -1;
    private final int[] nodeCursorsAndLengths = new int[MAX_DEPTH * 2];

    Node<K, V>[] nodes = new Node[MAX_DEPTH];

    AbstractIterator(Node<K, V> rootNode) {
      if (rootNode.hasNodes()) {
        currentStackLevel = 0;

        nodes[0] = rootNode;
        nodeCursorsAndLengths[0] = 0;
        nodeCursorsAndLengths[1] = rootNode.nodeArity();
      }

      if (rootNode.hasElements()) {
        currentValueNode = rootNode;
        currentValueCursor = 0;
        currentValueLength = rootNode.elementArity();
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
          final Node<K, V> nextNode = nodes[currentStackLevel].getNode(nodeCursor);
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

          if (nextNode.hasElements()) {
            /*
             * found next node that contains values
             */
            currentValueNode = nextNode;
            currentValueCursor = 0;
            currentValueLength = nextNode.elementArity();
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

  private static final class KeyIterator<K, V> extends AbstractIterator<K, V>
      implements Iterator<K> {

    KeyIterator(Node<K, V> rootNode) {
      super(rootNode);
    }

    @Override
    public K next() {
      if (!hasNext()) {
        throw new NoSuchElementException();
      } else {
        return currentValueNode.getElement(currentValueCursor++).getKey();
      }
    }
  }

  private static final class ValueIterator<K, V> extends AbstractIterator<K, V>
      implements Iterator<V> {

    ValueIterator(Node<K, V> rootNode) {
      super(rootNode);
    }

    @Override
    public V next() {
      if (!hasNext()) {
        throw new NoSuchElementException();
      } else {
        return currentValueNode.getElement(currentValueCursor++).getValue();
      }
    }
  }

  private static final class EntryIterator<K, V> extends AbstractIterator<K, V>
      implements Iterator<ImmutablePayloadTuple<K, V>> {

    EntryIterator(Node<K, V> rootNode) {
      super(rootNode);
    }

    @Override
    public ImmutablePayloadTuple<K, V> next() {
      if (!hasNext()) {
        throw new NoSuchElementException();
      } else {
        return currentValueNode.getElement(currentValueCursor++);

        // return new ImmutableMapEntry<K, V>(currentValueNode.getSequenceId(currentValueCursor),
        // currentValueNode.getKey(currentValueCursor),
        // currentValueNode.getVal(currentValueCursor++));
      }
    }
  }

  protected static class ImmutablePayloadTuple<K, V>
      implements java.util.Map.Entry<K, V>, Comparable<ImmutablePayloadTuple<K, V>> {

    private final int sequenceId;
    private final K key;
    private final V val;

    private ImmutablePayloadTuple(final int sequenceId, final K key, final V val) {
      this.sequenceId = sequenceId;
      this.key = key;
      this.val = val;
    }

    static final <K, V> ImmutablePayloadTuple<K, V> of(final int sequenceId, final K key,
        final V val, final int keyHash) {
      return new ImmutablePayloadTuple<K, V>(sequenceId, key, val);
    }

    @Override
    public K getKey() {
      return key;
    }

    @Override
    public V getValue() {
      return val;
    }

    @Override
    public V setValue(V value) {
      throw new UnsupportedOperationException();
    }

    // public ImmutableMapEntry<K, V> withUpdatedKey(K key) {
    // return new ImmutableMapEntry<>(sequenceId, key, val);
    // }

    public ImmutablePayloadTuple<K, V> withUpdatedValue(V val) {
      return new ImmutablePayloadTuple<>(sequenceId, key, val);
    }

    @Override
    public int compareTo(ImmutablePayloadTuple<K, V> other) {
      return sequenceId - other.sequenceId;
    }

    @Override
    public int hashCode() {
      return getKey().hashCode() ^ getValue().hashCode();
    }

    @Override
    public boolean equals(Object other) {
      if (null == other) {
        return false;
      }
      if (this == other) {
        return true;
      }
      if (getClass() != other.getClass()) {
        return false;
      }

      ImmutablePayloadTuple that = (ImmutablePayloadTuple) other;

      return Objects.equals(key, that.key) && Objects.equals(val, that.val);
    }

    @Override
    public String toString() {
      return String.format("%s=%s", getKey(), getValue());
    }

    protected static final Comparator<? super ImmutablePayloadTuple<?, ?>> ASCENDING_COMPARATOR =
        (o1, o2) -> o1.sequenceId - o2.sequenceId;

    protected static final Comparator<? super ImmutablePayloadTuple<?, ?>> DESCENDING_COMPARATOR =
        (o1, o2) -> o2.sequenceId - o1.sequenceId;

  }

  private static abstract class AbstractForwardOrderArrayIterator<E, R> implements Iterator<R> {

    final E[] values;
    final int end;
    int currentIndex;

    public AbstractForwardOrderArrayIterator(final E[] values, int start, int end) {
      assert start >= 0 && start <= end && end < values.length;

      this.values = values;
      this.end = end;
      this.currentIndex = start;
    }

    @Override
    public boolean hasNext() {
      return currentIndex <= end;
    }

    @Override
    public void remove() {
      throw new UnsupportedOperationException();
    }
  }

  private static final class ForwardKeyIterator<K, V>
      extends AbstractForwardOrderArrayIterator<ImmutablePayloadTuple<K, V>, K> {

    ForwardKeyIterator(ImmutablePayloadTuple<K, V>[] arr) {
      super(arr, 0, arr.length - 1);
    }

    @Override
    public K next() {
      if (!hasNext()) {
        throw new NoSuchElementException();
      } else {
        return values[currentIndex++].getKey();
      }
    }
  }

  private static final class ForwardElementIterator<K, V>
      extends AbstractForwardOrderArrayIterator<ImmutablePayloadTuple<K, V>, V> {

    ForwardElementIterator(ImmutablePayloadTuple<K, V>[] arr) {
      super(arr, 0, arr.length - 1);
    }

    @Override
    public V next() {
      if (!hasNext()) {
        throw new NoSuchElementException();
      } else {
        return values[currentIndex++].getValue();
      }
    }
  }

  private static final class ForwardEntryIterator<K, V> extends
      AbstractForwardOrderArrayIterator<ImmutablePayloadTuple<K, V>, ImmutablePayloadTuple<K, V>> {

    ForwardEntryIterator(ImmutablePayloadTuple<K, V>[] arr) {
      super(arr, 0, arr.length - 1);
    }

    @Override
    public ImmutablePayloadTuple<K, V> next() {
      if (!hasNext()) {
        throw new NoSuchElementException();
      } else {
        return values[currentIndex++];
      }
    }
  }

  private static abstract class AbstractReversedOrderArrayIterator<E, R> implements Iterator<R> {

    final E[] values;
    final int end;
    int currentIndex;

    public AbstractReversedOrderArrayIterator(final E[] values, int start, int end) {
      assert end >= 0 && end <= start && start < values.length;

      this.values = values;
      this.end = end;
      this.currentIndex = start;
    }

    @Override
    public boolean hasNext() {
      return currentIndex >= end;
    }

    @Override
    public void remove() {
      throw new UnsupportedOperationException();
    }
  }

  private static final class ReverseKeyIterator<K, V>
      extends AbstractReversedOrderArrayIterator<ImmutablePayloadTuple<K, V>, K> {

    ReverseKeyIterator(ImmutablePayloadTuple<K, V>[] arr) {
      super(arr, arr.length - 1, 0);
    }

    @Override
    public K next() {
      if (!hasNext()) {
        throw new NoSuchElementException();
      } else {
        return values[currentIndex--].getKey();
      }
    }
  }

  private static final class ReverseValueIterator<K, V>
      extends AbstractReversedOrderArrayIterator<ImmutablePayloadTuple<K, V>, V> {

    ReverseValueIterator(ImmutablePayloadTuple<K, V>[] arr) {
      super(arr, arr.length - 1, 0);
    }

    @Override
    public V next() {
      if (!hasNext()) {
        throw new NoSuchElementException();
      } else {
        return values[currentIndex--].getValue();
      }
    }
  }

  private static final class ReverseElementIterator<K, V> extends
      AbstractReversedOrderArrayIterator<ImmutablePayloadTuple<K, V>, ImmutablePayloadTuple<K, V>> {

    ReverseElementIterator(ImmutablePayloadTuple<K, V>[] arr) {
      super(arr, arr.length - 1, 0);
    }

    @Override
    public ImmutablePayloadTuple<K, V> next() {
      if (!hasNext()) {
        throw new NoSuchElementException();
      } else {
        return values[currentIndex--];
      }
    }
  }

  private static final class ReverseEntryIterator<K, V> extends
      AbstractReversedOrderArrayIterator<ImmutablePayloadTuple<K, V>, ImmutablePayloadTuple<K, V>> {

    ReverseEntryIterator(ImmutablePayloadTuple<K, V>[] arr) {
      super(arr, arr.length - 1, 0);
    }

    @Override
    public ImmutablePayloadTuple<K, V> next() {
      if (!hasNext()) {
        throw new NoSuchElementException();
      } else {
        return values[currentIndex--];
      }
    }
  }

  @Override
  public SupplierIterator<K, V> iterator() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Map.Immutable<K, V> insertAll(Map<? extends K, ? extends V> map) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Map.Immutable<K, V> asImmutable() {
    return this;
  }

  @Override
  public boolean isTransientSupported() {
    return false;
  }

  @Override
  public Map.Transient<K, V> asTransient() {
    throw new UnsupportedOperationException("Transient is not supported.");
  }

  @Override
  public java.util.Map<K, V> asJdkCollection() {
    // TODO Auto-generated method stub
    return null;
  }

}
