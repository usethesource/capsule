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

import java.util.concurrent.atomic.AtomicReference;

import io.usethesource.capsule.TrieMap_Heterogeneous_BleedingEdge.CompactMapNode;

public class TrieMap_Heterogeneous_BleedingEdge_Specializations {

  protected static class Map0To3Node_Heterogeneous_BleedingEdge extends CompactMapNode {

    static final int payloadArity = 0;

    static final int untypedSlotArity = 3;

    static final long rareBase = arrayBase + 0 * 4 /* TODO: sizeOf(ts.ds) */;

    static final long arrayOffsetLast = rareBase + 2 * addressSize;

    static final long nodeBase = rareBase + 3 * addressSize;

    private final Object slot0;
    private final Object slot1;
    private final Object slot2;

    protected Map0To3Node_Heterogeneous_BleedingEdge(final AtomicReference<Thread> mutator,
        final byte nodeMap, final byte dataMap, final Object slot0, final Object slot1,
        final Object slot2) {
      super(mutator, nodeMap, dataMap);
      this.slot0 = slot0;
      this.slot1 = slot1;
      this.slot2 = slot2;
    }
  }

  protected static class Map3To0Node_Heterogeneous_BleedingEdge extends CompactMapNode {

    static final int payloadArity = 3;

    static final int untypedSlotArity = 0;

    static final long rareBase = arrayBase + 6 * 4 /* TODO: sizeOf(ts.ds) */;

    static final long arrayOffsetLast = rareBase;

    static final long nodeBase = rareBase;

    private final int key1;
    private final int val1;
    private final int key2;
    private final int val2;
    private final int key3;
    private final int val3;

    protected Map3To0Node_Heterogeneous_BleedingEdge(final AtomicReference<Thread> mutator,
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

  protected static class Map0To1Node_Heterogeneous_BleedingEdge extends CompactMapNode {

    static final int payloadArity = 0;

    static final int untypedSlotArity = 1;

    static final long rareBase = arrayBase + 0 * 4 /* TODO: sizeOf(ts.ds) */;

    static final long arrayOffsetLast = rareBase;

    static final long nodeBase = rareBase + 1 * addressSize;

    private final Object slot0;

    protected Map0To1Node_Heterogeneous_BleedingEdge(final AtomicReference<Thread> mutator,
        final byte nodeMap, final byte dataMap, final Object slot0) {
      super(mutator, nodeMap, dataMap);
      this.slot0 = slot0;
    }
  }

  protected static class Map1To9Node_Heterogeneous_BleedingEdge extends CompactMapNode {

    static final int payloadArity = 1;

    static final int untypedSlotArity = 9;

    static final long rareBase = arrayBase + 2 * 4 /* TODO: sizeOf(ts.ds) */;

    static final long arrayOffsetLast = rareBase + 8 * addressSize;

    static final long nodeBase = rareBase + 9 * addressSize;

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

    protected Map1To9Node_Heterogeneous_BleedingEdge(final AtomicReference<Thread> mutator,
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

  protected static class Map2To8Node_Heterogeneous_BleedingEdge extends CompactMapNode {

    static final int payloadArity = 2;

    static final int untypedSlotArity = 8;

    static final long rareBase = arrayBase + 4 * 4 /* TODO: sizeOf(ts.ds) */;

    static final long arrayOffsetLast = rareBase + 7 * addressSize;

    static final long nodeBase = rareBase + 8 * addressSize;

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

    protected Map2To8Node_Heterogeneous_BleedingEdge(final AtomicReference<Thread> mutator,
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

  protected static class Map2To5Node_Heterogeneous_BleedingEdge extends CompactMapNode {

    static final int payloadArity = 2;

    static final int untypedSlotArity = 5;

    static final long rareBase = arrayBase + 4 * 4 /* TODO: sizeOf(ts.ds) */;

    static final long arrayOffsetLast = rareBase + 4 * addressSize;

    static final long nodeBase = rareBase + 5 * addressSize;

    private final int key1;
    private final int val1;
    private final int key2;
    private final int val2;
    private final Object slot0;
    private final Object slot1;
    private final Object slot2;
    private final Object slot3;
    private final Object slot4;

    protected Map2To5Node_Heterogeneous_BleedingEdge(final AtomicReference<Thread> mutator,
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

  protected static class Map1To11Node_Heterogeneous_BleedingEdge extends CompactMapNode {

    static final int payloadArity = 1;

    static final int untypedSlotArity = 11;

    static final long rareBase = arrayBase + 2 * 4 /* TODO: sizeOf(ts.ds) */;

    static final long arrayOffsetLast = rareBase + 10 * addressSize;

    static final long nodeBase = rareBase + 11 * addressSize;

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

    protected Map1To11Node_Heterogeneous_BleedingEdge(final AtomicReference<Thread> mutator,
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

  protected static class Map4To3Node_Heterogeneous_BleedingEdge extends CompactMapNode {

    static final int payloadArity = 4;

    static final int untypedSlotArity = 3;

    static final long rareBase = arrayBase + 8 * 4 /* TODO: sizeOf(ts.ds) */;

    static final long arrayOffsetLast = rareBase + 2 * addressSize;

    static final long nodeBase = rareBase + 3 * addressSize;

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

    protected Map4To3Node_Heterogeneous_BleedingEdge(final AtomicReference<Thread> mutator,
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

  protected static class Map1To4Node_Heterogeneous_BleedingEdge extends CompactMapNode {

    static final int payloadArity = 1;

    static final int untypedSlotArity = 4;

    static final long rareBase = arrayBase + 2 * 4 /* TODO: sizeOf(ts.ds) */;

    static final long arrayOffsetLast = rareBase + 3 * addressSize;

    static final long nodeBase = rareBase + 4 * addressSize;

    private final int key1;
    private final int val1;
    private final Object slot0;
    private final Object slot1;
    private final Object slot2;
    private final Object slot3;

    protected Map1To4Node_Heterogeneous_BleedingEdge(final AtomicReference<Thread> mutator,
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

  protected static class Map5To4Node_Heterogeneous_BleedingEdge extends CompactMapNode {

    static final int payloadArity = 5;

    static final int untypedSlotArity = 4;

    static final long rareBase = arrayBase + 10 * 4 /* TODO: sizeOf(ts.ds) */;

    static final long arrayOffsetLast = rareBase + 3 * addressSize;

    static final long nodeBase = rareBase + 4 * addressSize;

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

    protected Map5To4Node_Heterogeneous_BleedingEdge(final AtomicReference<Thread> mutator,
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

  protected static class Map2To7Node_Heterogeneous_BleedingEdge extends CompactMapNode {

    static final int payloadArity = 2;

    static final int untypedSlotArity = 7;

    static final long rareBase = arrayBase + 4 * 4 /* TODO: sizeOf(ts.ds) */;

    static final long arrayOffsetLast = rareBase + 6 * addressSize;

    static final long nodeBase = rareBase + 7 * addressSize;

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

    protected Map2To7Node_Heterogeneous_BleedingEdge(final AtomicReference<Thread> mutator,
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

  protected static class Map0To2Node_Heterogeneous_BleedingEdge extends CompactMapNode {

    static final int payloadArity = 0;

    static final int untypedSlotArity = 2;

    static final long rareBase = arrayBase + 0 * 4 /* TODO: sizeOf(ts.ds) */;

    static final long arrayOffsetLast = rareBase + 1 * addressSize;

    static final long nodeBase = rareBase + 2 * addressSize;

    private final Object slot0;
    private final Object slot1;

    protected Map0To2Node_Heterogeneous_BleedingEdge(final AtomicReference<Thread> mutator,
        final byte nodeMap, final byte dataMap, final Object slot0, final Object slot1) {
      super(mutator, nodeMap, dataMap);
      this.slot0 = slot0;
      this.slot1 = slot1;
    }
  }

  protected static class Map8To0Node_Heterogeneous_BleedingEdge extends CompactMapNode {

    static final int payloadArity = 8;

    static final int untypedSlotArity = 0;

    static final long rareBase = arrayBase + 16 * 4 /* TODO: sizeOf(ts.ds) */;

    static final long arrayOffsetLast = rareBase;

    static final long nodeBase = rareBase;

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

    protected Map8To0Node_Heterogeneous_BleedingEdge(final AtomicReference<Thread> mutator,
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

  protected static class Map2To10Node_Heterogeneous_BleedingEdge extends CompactMapNode {

    static final int payloadArity = 2;

    static final int untypedSlotArity = 10;

    static final long rareBase = arrayBase + 4 * 4 /* TODO: sizeOf(ts.ds) */;

    static final long arrayOffsetLast = rareBase + 9 * addressSize;

    static final long nodeBase = rareBase + 10 * addressSize;

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

    protected Map2To10Node_Heterogeneous_BleedingEdge(final AtomicReference<Thread> mutator,
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

  protected static class Map0To9Node_Heterogeneous_BleedingEdge extends CompactMapNode {

    static final int payloadArity = 0;

    static final int untypedSlotArity = 9;

    static final long rareBase = arrayBase + 0 * 4 /* TODO: sizeOf(ts.ds) */;

    static final long arrayOffsetLast = rareBase + 8 * addressSize;

    static final long nodeBase = rareBase + 9 * addressSize;

    private final Object slot0;
    private final Object slot1;
    private final Object slot2;
    private final Object slot3;
    private final Object slot4;
    private final Object slot5;
    private final Object slot6;
    private final Object slot7;
    private final Object slot8;

    protected Map0To9Node_Heterogeneous_BleedingEdge(final AtomicReference<Thread> mutator,
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

  protected static class Map4To1Node_Heterogeneous_BleedingEdge extends CompactMapNode {

    static final int payloadArity = 4;

    static final int untypedSlotArity = 1;

    static final long rareBase = arrayBase + 8 * 4 /* TODO: sizeOf(ts.ds) */;

    static final long arrayOffsetLast = rareBase;

    static final long nodeBase = rareBase + 1 * addressSize;

    private final int key1;
    private final int val1;
    private final int key2;
    private final int val2;
    private final int key3;
    private final int val3;
    private final int key4;
    private final int val4;
    private final Object slot0;

    protected Map4To1Node_Heterogeneous_BleedingEdge(final AtomicReference<Thread> mutator,
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

  protected static class Map5To6Node_Heterogeneous_BleedingEdge extends CompactMapNode {

    static final int payloadArity = 5;

    static final int untypedSlotArity = 6;

    static final long rareBase = arrayBase + 10 * 4 /* TODO: sizeOf(ts.ds) */;

    static final long arrayOffsetLast = rareBase + 5 * addressSize;

    static final long nodeBase = rareBase + 6 * addressSize;

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

    protected Map5To6Node_Heterogeneous_BleedingEdge(final AtomicReference<Thread> mutator,
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

  protected static class Map1To6Node_Heterogeneous_BleedingEdge extends CompactMapNode {

    static final int payloadArity = 1;

    static final int untypedSlotArity = 6;

    static final long rareBase = arrayBase + 2 * 4 /* TODO: sizeOf(ts.ds) */;

    static final long arrayOffsetLast = rareBase + 5 * addressSize;

    static final long nodeBase = rareBase + 6 * addressSize;

    private final int key1;
    private final int val1;
    private final Object slot0;
    private final Object slot1;
    private final Object slot2;
    private final Object slot3;
    private final Object slot4;
    private final Object slot5;

    protected Map1To6Node_Heterogeneous_BleedingEdge(final AtomicReference<Thread> mutator,
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

  protected static class Map3To6Node_Heterogeneous_BleedingEdge extends CompactMapNode {

    static final int payloadArity = 3;

    static final int untypedSlotArity = 6;

    static final long rareBase = arrayBase + 6 * 4 /* TODO: sizeOf(ts.ds) */;

    static final long arrayOffsetLast = rareBase + 5 * addressSize;

    static final long nodeBase = rareBase + 6 * addressSize;

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

    protected Map3To6Node_Heterogeneous_BleedingEdge(final AtomicReference<Thread> mutator,
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

  protected static class Map4To7Node_Heterogeneous_BleedingEdge extends CompactMapNode {

    static final int payloadArity = 4;

    static final int untypedSlotArity = 7;

    static final long rareBase = arrayBase + 8 * 4 /* TODO: sizeOf(ts.ds) */;

    static final long arrayOffsetLast = rareBase + 6 * addressSize;

    static final long nodeBase = rareBase + 7 * addressSize;

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

    protected Map4To7Node_Heterogeneous_BleedingEdge(final AtomicReference<Thread> mutator,
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

  protected static class Map2To3Node_Heterogeneous_BleedingEdge extends CompactMapNode {

    static final int payloadArity = 2;

    static final int untypedSlotArity = 3;

    static final long rareBase = arrayBase + 4 * 4 /* TODO: sizeOf(ts.ds) */;

    static final long arrayOffsetLast = rareBase + 2 * addressSize;

    static final long nodeBase = rareBase + 3 * addressSize;

    private final int key1;
    private final int val1;
    private final int key2;
    private final int val2;
    private final Object slot0;
    private final Object slot1;
    private final Object slot2;

    protected Map2To3Node_Heterogeneous_BleedingEdge(final AtomicReference<Thread> mutator,
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

  protected static class Map4To8Node_Heterogeneous_BleedingEdge extends CompactMapNode {

    static final int payloadArity = 4;

    static final int untypedSlotArity = 8;

    static final long rareBase = arrayBase + 8 * 4 /* TODO: sizeOf(ts.ds) */;

    static final long arrayOffsetLast = rareBase + 7 * addressSize;

    static final long nodeBase = rareBase + 8 * addressSize;

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

    protected Map4To8Node_Heterogeneous_BleedingEdge(final AtomicReference<Thread> mutator,
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

  protected static class Map0To14Node_Heterogeneous_BleedingEdge extends CompactMapNode {

    static final int payloadArity = 0;

    static final int untypedSlotArity = 14;

    static final long rareBase = arrayBase + 0 * 4 /* TODO: sizeOf(ts.ds) */;

    static final long arrayOffsetLast = rareBase + 13 * addressSize;

    static final long nodeBase = rareBase + 14 * addressSize;

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

    protected Map0To14Node_Heterogeneous_BleedingEdge(final AtomicReference<Thread> mutator,
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

  protected static class Map6To1Node_Heterogeneous_BleedingEdge extends CompactMapNode {

    static final int payloadArity = 6;

    static final int untypedSlotArity = 1;

    static final long rareBase = arrayBase + 12 * 4 /* TODO: sizeOf(ts.ds) */;

    static final long arrayOffsetLast = rareBase;

    static final long nodeBase = rareBase + 1 * addressSize;

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

    protected Map6To1Node_Heterogeneous_BleedingEdge(final AtomicReference<Thread> mutator,
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

  protected static class Map4To5Node_Heterogeneous_BleedingEdge extends CompactMapNode {

    static final int payloadArity = 4;

    static final int untypedSlotArity = 5;

    static final long rareBase = arrayBase + 8 * 4 /* TODO: sizeOf(ts.ds) */;

    static final long arrayOffsetLast = rareBase + 4 * addressSize;

    static final long nodeBase = rareBase + 5 * addressSize;

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

    protected Map4To5Node_Heterogeneous_BleedingEdge(final AtomicReference<Thread> mutator,
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

  protected static class Map0To13Node_Heterogeneous_BleedingEdge extends CompactMapNode {

    static final int payloadArity = 0;

    static final int untypedSlotArity = 13;

    static final long rareBase = arrayBase + 0 * 4 /* TODO: sizeOf(ts.ds) */;

    static final long arrayOffsetLast = rareBase + 12 * addressSize;

    static final long nodeBase = rareBase + 13 * addressSize;

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

    protected Map0To13Node_Heterogeneous_BleedingEdge(final AtomicReference<Thread> mutator,
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

  protected static class Map3To4Node_Heterogeneous_BleedingEdge extends CompactMapNode {

    static final int payloadArity = 3;

    static final int untypedSlotArity = 4;

    static final long rareBase = arrayBase + 6 * 4 /* TODO: sizeOf(ts.ds) */;

    static final long arrayOffsetLast = rareBase + 3 * addressSize;

    static final long nodeBase = rareBase + 4 * addressSize;

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

    protected Map3To4Node_Heterogeneous_BleedingEdge(final AtomicReference<Thread> mutator,
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

  protected static class Map2To1Node_Heterogeneous_BleedingEdge extends CompactMapNode {

    static final int payloadArity = 2;

    static final int untypedSlotArity = 1;

    static final long rareBase = arrayBase + 4 * 4 /* TODO: sizeOf(ts.ds) */;

    static final long arrayOffsetLast = rareBase;

    static final long nodeBase = rareBase + 1 * addressSize;

    private final int key1;
    private final int val1;
    private final int key2;
    private final int val2;
    private final Object slot0;

    protected Map2To1Node_Heterogeneous_BleedingEdge(final AtomicReference<Thread> mutator,
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

  protected static class Map6To3Node_Heterogeneous_BleedingEdge extends CompactMapNode {

    static final int payloadArity = 6;

    static final int untypedSlotArity = 3;

    static final long rareBase = arrayBase + 12 * 4 /* TODO: sizeOf(ts.ds) */;

    static final long arrayOffsetLast = rareBase + 2 * addressSize;

    static final long nodeBase = rareBase + 3 * addressSize;

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

    protected Map6To3Node_Heterogeneous_BleedingEdge(final AtomicReference<Thread> mutator,
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

  protected static class Map1To12Node_Heterogeneous_BleedingEdge extends CompactMapNode {

    static final int payloadArity = 1;

    static final int untypedSlotArity = 12;

    static final long rareBase = arrayBase + 2 * 4 /* TODO: sizeOf(ts.ds) */;

    static final long arrayOffsetLast = rareBase + 11 * addressSize;

    static final long nodeBase = rareBase + 12 * addressSize;

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

    protected Map1To12Node_Heterogeneous_BleedingEdge(final AtomicReference<Thread> mutator,
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

  protected static class Map4To2Node_Heterogeneous_BleedingEdge extends CompactMapNode {

    static final int payloadArity = 4;

    static final int untypedSlotArity = 2;

    static final long rareBase = arrayBase + 8 * 4 /* TODO: sizeOf(ts.ds) */;

    static final long arrayOffsetLast = rareBase + 1 * addressSize;

    static final long nodeBase = rareBase + 2 * addressSize;

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

    protected Map4To2Node_Heterogeneous_BleedingEdge(final AtomicReference<Thread> mutator,
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

  protected static class Map0To5Node_Heterogeneous_BleedingEdge extends CompactMapNode {

    static final int payloadArity = 0;

    static final int untypedSlotArity = 5;

    static final long rareBase = arrayBase + 0 * 4 /* TODO: sizeOf(ts.ds) */;

    static final long arrayOffsetLast = rareBase + 4 * addressSize;

    static final long nodeBase = rareBase + 5 * addressSize;

    private final Object slot0;
    private final Object slot1;
    private final Object slot2;
    private final Object slot3;
    private final Object slot4;

    protected Map0To5Node_Heterogeneous_BleedingEdge(final AtomicReference<Thread> mutator,
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

  protected static class Map0To8Node_Heterogeneous_BleedingEdge extends CompactMapNode {

    static final int payloadArity = 0;

    static final int untypedSlotArity = 8;

    static final long rareBase = arrayBase + 0 * 4 /* TODO: sizeOf(ts.ds) */;

    static final long arrayOffsetLast = rareBase + 7 * addressSize;

    static final long nodeBase = rareBase + 8 * addressSize;

    private final Object slot0;
    private final Object slot1;
    private final Object slot2;
    private final Object slot3;
    private final Object slot4;
    private final Object slot5;
    private final Object slot6;
    private final Object slot7;

    protected Map0To8Node_Heterogeneous_BleedingEdge(final AtomicReference<Thread> mutator,
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

  protected static class Map7To0Node_Heterogeneous_BleedingEdge extends CompactMapNode {

    static final int payloadArity = 7;

    static final int untypedSlotArity = 0;

    static final long rareBase = arrayBase + 14 * 4 /* TODO: sizeOf(ts.ds) */;

    static final long arrayOffsetLast = rareBase;

    static final long nodeBase = rareBase;

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

    protected Map7To0Node_Heterogeneous_BleedingEdge(final AtomicReference<Thread> mutator,
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

  protected static class Map0To7Node_Heterogeneous_BleedingEdge extends CompactMapNode {

    static final int payloadArity = 0;

    static final int untypedSlotArity = 7;

    static final long rareBase = arrayBase + 0 * 4 /* TODO: sizeOf(ts.ds) */;

    static final long arrayOffsetLast = rareBase + 6 * addressSize;

    static final long nodeBase = rareBase + 7 * addressSize;

    private final Object slot0;
    private final Object slot1;
    private final Object slot2;
    private final Object slot3;
    private final Object slot4;
    private final Object slot5;
    private final Object slot6;

    protected Map0To7Node_Heterogeneous_BleedingEdge(final AtomicReference<Thread> mutator,
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

  protected static class Map1To0Node_Heterogeneous_BleedingEdge extends CompactMapNode {

    static final int payloadArity = 1;

    static final int untypedSlotArity = 0;

    static final long rareBase = arrayBase + 2 * 4 /* TODO: sizeOf(ts.ds) */;

    static final long arrayOffsetLast = rareBase;

    static final long nodeBase = rareBase;

    private final int key1;
    private final int val1;

    protected Map1To0Node_Heterogeneous_BleedingEdge(final AtomicReference<Thread> mutator,
        final byte nodeMap, final byte dataMap, final int key1, final int val1) {
      super(mutator, nodeMap, dataMap);
      this.key1 = key1;
      this.val1 = val1;
    }
  }

  protected static class Map5To0Node_Heterogeneous_BleedingEdge extends CompactMapNode {

    static final int payloadArity = 5;

    static final int untypedSlotArity = 0;

    static final long rareBase = arrayBase + 10 * 4 /* TODO: sizeOf(ts.ds) */;

    static final long arrayOffsetLast = rareBase;

    static final long nodeBase = rareBase;

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

    protected Map5To0Node_Heterogeneous_BleedingEdge(final AtomicReference<Thread> mutator,
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

  protected static class Map2To2Node_Heterogeneous_BleedingEdge extends CompactMapNode {

    static final int payloadArity = 2;

    static final int untypedSlotArity = 2;

    static final long rareBase = arrayBase + 4 * 4 /* TODO: sizeOf(ts.ds) */;

    static final long arrayOffsetLast = rareBase + 1 * addressSize;

    static final long nodeBase = rareBase + 2 * addressSize;

    private final int key1;
    private final int val1;
    private final int key2;
    private final int val2;
    private final Object slot0;
    private final Object slot1;

    protected Map2To2Node_Heterogeneous_BleedingEdge(final AtomicReference<Thread> mutator,
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

  protected static class Map2To9Node_Heterogeneous_BleedingEdge extends CompactMapNode {

    static final int payloadArity = 2;

    static final int untypedSlotArity = 9;

    static final long rareBase = arrayBase + 4 * 4 /* TODO: sizeOf(ts.ds) */;

    static final long arrayOffsetLast = rareBase + 8 * addressSize;

    static final long nodeBase = rareBase + 9 * addressSize;

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

    protected Map2To9Node_Heterogeneous_BleedingEdge(final AtomicReference<Thread> mutator,
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

  protected static class Map0To10Node_Heterogeneous_BleedingEdge extends CompactMapNode {

    static final int payloadArity = 0;

    static final int untypedSlotArity = 10;

    static final long rareBase = arrayBase + 0 * 4 /* TODO: sizeOf(ts.ds) */;

    static final long arrayOffsetLast = rareBase + 9 * addressSize;

    static final long nodeBase = rareBase + 10 * addressSize;

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

    protected Map0To10Node_Heterogeneous_BleedingEdge(final AtomicReference<Thread> mutator,
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

  protected static class Map6To2Node_Heterogeneous_BleedingEdge extends CompactMapNode {

    static final int payloadArity = 6;

    static final int untypedSlotArity = 2;

    static final long rareBase = arrayBase + 12 * 4 /* TODO: sizeOf(ts.ds) */;

    static final long arrayOffsetLast = rareBase + 1 * addressSize;

    static final long nodeBase = rareBase + 2 * addressSize;

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

    protected Map6To2Node_Heterogeneous_BleedingEdge(final AtomicReference<Thread> mutator,
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

  protected static class Map0To16Node_Heterogeneous_BleedingEdge extends CompactMapNode {

    static final int payloadArity = 0;

    static final int untypedSlotArity = 16;

    static final long rareBase = arrayBase + 0 * 4 /* TODO: sizeOf(ts.ds) */;

    static final long arrayOffsetLast = rareBase + 15 * addressSize;

    static final long nodeBase = rareBase + 16 * addressSize;

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

    protected Map0To16Node_Heterogeneous_BleedingEdge(final AtomicReference<Thread> mutator,
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

  protected static class Map6To4Node_Heterogeneous_BleedingEdge extends CompactMapNode {

    static final int payloadArity = 6;

    static final int untypedSlotArity = 4;

    static final long rareBase = arrayBase + 12 * 4 /* TODO: sizeOf(ts.ds) */;

    static final long arrayOffsetLast = rareBase + 3 * addressSize;

    static final long nodeBase = rareBase + 4 * addressSize;

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

    protected Map6To4Node_Heterogeneous_BleedingEdge(final AtomicReference<Thread> mutator,
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

  protected static class Map2To6Node_Heterogeneous_BleedingEdge extends CompactMapNode {

    static final int payloadArity = 2;

    static final int untypedSlotArity = 6;

    static final long rareBase = arrayBase + 4 * 4 /* TODO: sizeOf(ts.ds) */;

    static final long arrayOffsetLast = rareBase + 5 * addressSize;

    static final long nodeBase = rareBase + 6 * addressSize;

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

    protected Map2To6Node_Heterogeneous_BleedingEdge(final AtomicReference<Thread> mutator,
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

  protected static class Map3To3Node_Heterogeneous_BleedingEdge extends CompactMapNode {

    static final int payloadArity = 3;

    static final int untypedSlotArity = 3;

    static final long rareBase = arrayBase + 6 * 4 /* TODO: sizeOf(ts.ds) */;

    static final long arrayOffsetLast = rareBase + 2 * addressSize;

    static final long nodeBase = rareBase + 3 * addressSize;

    private final int key1;
    private final int val1;
    private final int key2;
    private final int val2;
    private final int key3;
    private final int val3;
    private final Object slot0;
    private final Object slot1;
    private final Object slot2;

    protected Map3To3Node_Heterogeneous_BleedingEdge(final AtomicReference<Thread> mutator,
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

  protected static class Map1To10Node_Heterogeneous_BleedingEdge extends CompactMapNode {

    static final int payloadArity = 1;

    static final int untypedSlotArity = 10;

    static final long rareBase = arrayBase + 2 * 4 /* TODO: sizeOf(ts.ds) */;

    static final long arrayOffsetLast = rareBase + 9 * addressSize;

    static final long nodeBase = rareBase + 10 * addressSize;

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

    protected Map1To10Node_Heterogeneous_BleedingEdge(final AtomicReference<Thread> mutator,
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

  protected static class Map1To7Node_Heterogeneous_BleedingEdge extends CompactMapNode {

    static final int payloadArity = 1;

    static final int untypedSlotArity = 7;

    static final long rareBase = arrayBase + 2 * 4 /* TODO: sizeOf(ts.ds) */;

    static final long arrayOffsetLast = rareBase + 6 * addressSize;

    static final long nodeBase = rareBase + 7 * addressSize;

    private final int key1;
    private final int val1;
    private final Object slot0;
    private final Object slot1;
    private final Object slot2;
    private final Object slot3;
    private final Object slot4;
    private final Object slot5;
    private final Object slot6;

    protected Map1To7Node_Heterogeneous_BleedingEdge(final AtomicReference<Thread> mutator,
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

  protected static class Map2To4Node_Heterogeneous_BleedingEdge extends CompactMapNode {

    static final int payloadArity = 2;

    static final int untypedSlotArity = 4;

    static final long rareBase = arrayBase + 4 * 4 /* TODO: sizeOf(ts.ds) */;

    static final long arrayOffsetLast = rareBase + 3 * addressSize;

    static final long nodeBase = rareBase + 4 * addressSize;

    private final int key1;
    private final int val1;
    private final int key2;
    private final int val2;
    private final Object slot0;
    private final Object slot1;
    private final Object slot2;
    private final Object slot3;

    protected Map2To4Node_Heterogeneous_BleedingEdge(final AtomicReference<Thread> mutator,
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

  protected static class Map0To0Node_Heterogeneous_BleedingEdge extends CompactMapNode {

    static final int payloadArity = 0;

    static final int untypedSlotArity = 0;

    static final long rareBase = arrayBase + 0 * 4 /* TODO: sizeOf(ts.ds) */;

    static final long arrayOffsetLast = rareBase;

    static final long nodeBase = rareBase;

    protected Map0To0Node_Heterogeneous_BleedingEdge(final AtomicReference<Thread> mutator,
        final byte nodeMap, final byte dataMap) {
      super(mutator, nodeMap, dataMap);

    }
  }

  protected static class Map3To1Node_Heterogeneous_BleedingEdge extends CompactMapNode {

    static final int payloadArity = 3;

    static final int untypedSlotArity = 1;

    static final long rareBase = arrayBase + 6 * 4 /* TODO: sizeOf(ts.ds) */;

    static final long arrayOffsetLast = rareBase;

    static final long nodeBase = rareBase + 1 * addressSize;

    private final int key1;
    private final int val1;
    private final int key2;
    private final int val2;
    private final int key3;
    private final int val3;
    private final Object slot0;

    protected Map3To1Node_Heterogeneous_BleedingEdge(final AtomicReference<Thread> mutator,
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

  protected static class Map2To11Node_Heterogeneous_BleedingEdge extends CompactMapNode {

    static final int payloadArity = 2;

    static final int untypedSlotArity = 11;

    static final long rareBase = arrayBase + 4 * 4 /* TODO: sizeOf(ts.ds) */;

    static final long arrayOffsetLast = rareBase + 10 * addressSize;

    static final long nodeBase = rareBase + 11 * addressSize;

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

    protected Map2To11Node_Heterogeneous_BleedingEdge(final AtomicReference<Thread> mutator,
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

  protected static class Map5To5Node_Heterogeneous_BleedingEdge extends CompactMapNode {

    static final int payloadArity = 5;

    static final int untypedSlotArity = 5;

    static final long rareBase = arrayBase + 10 * 4 /* TODO: sizeOf(ts.ds) */;

    static final long arrayOffsetLast = rareBase + 4 * addressSize;

    static final long nodeBase = rareBase + 5 * addressSize;

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

    protected Map5To5Node_Heterogeneous_BleedingEdge(final AtomicReference<Thread> mutator,
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

  protected static class Map1To8Node_Heterogeneous_BleedingEdge extends CompactMapNode {

    static final int payloadArity = 1;

    static final int untypedSlotArity = 8;

    static final long rareBase = arrayBase + 2 * 4 /* TODO: sizeOf(ts.ds) */;

    static final long arrayOffsetLast = rareBase + 7 * addressSize;

    static final long nodeBase = rareBase + 8 * addressSize;

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

    protected Map1To8Node_Heterogeneous_BleedingEdge(final AtomicReference<Thread> mutator,
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

  protected static class Map1To5Node_Heterogeneous_BleedingEdge extends CompactMapNode {

    static final int payloadArity = 1;

    static final int untypedSlotArity = 5;

    static final long rareBase = arrayBase + 2 * 4 /* TODO: sizeOf(ts.ds) */;

    static final long arrayOffsetLast = rareBase + 4 * addressSize;

    static final long nodeBase = rareBase + 5 * addressSize;

    private final int key1;
    private final int val1;
    private final Object slot0;
    private final Object slot1;
    private final Object slot2;
    private final Object slot3;
    private final Object slot4;

    protected Map1To5Node_Heterogeneous_BleedingEdge(final AtomicReference<Thread> mutator,
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

  protected static class Map3To5Node_Heterogeneous_BleedingEdge extends CompactMapNode {

    static final int payloadArity = 3;

    static final int untypedSlotArity = 5;

    static final long rareBase = arrayBase + 6 * 4 /* TODO: sizeOf(ts.ds) */;

    static final long arrayOffsetLast = rareBase + 4 * addressSize;

    static final long nodeBase = rareBase + 5 * addressSize;

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

    protected Map3To5Node_Heterogeneous_BleedingEdge(final AtomicReference<Thread> mutator,
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

  protected static class Map3To8Node_Heterogeneous_BleedingEdge extends CompactMapNode {

    static final int payloadArity = 3;

    static final int untypedSlotArity = 8;

    static final long rareBase = arrayBase + 6 * 4 /* TODO: sizeOf(ts.ds) */;

    static final long arrayOffsetLast = rareBase + 7 * addressSize;

    static final long nodeBase = rareBase + 8 * addressSize;

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

    protected Map3To8Node_Heterogeneous_BleedingEdge(final AtomicReference<Thread> mutator,
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

  protected static class Map0To15Node_Heterogeneous_BleedingEdge extends CompactMapNode {

    static final int payloadArity = 0;

    static final int untypedSlotArity = 15;

    static final long rareBase = arrayBase + 0 * 4 /* TODO: sizeOf(ts.ds) */;

    static final long arrayOffsetLast = rareBase + 14 * addressSize;

    static final long nodeBase = rareBase + 15 * addressSize;

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

    protected Map0To15Node_Heterogeneous_BleedingEdge(final AtomicReference<Thread> mutator,
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

  protected static class Map1To1Node_Heterogeneous_BleedingEdge extends CompactMapNode {

    static final int payloadArity = 1;

    static final int untypedSlotArity = 1;

    static final long rareBase = arrayBase + 2 * 4 /* TODO: sizeOf(ts.ds) */;

    static final long arrayOffsetLast = rareBase;

    static final long nodeBase = rareBase + 1 * addressSize;

    private final int key1;
    private final int val1;
    private final Object slot0;

    protected Map1To1Node_Heterogeneous_BleedingEdge(final AtomicReference<Thread> mutator,
        final byte nodeMap, final byte dataMap, final int key1, final int val1,
        final Object slot0) {
      super(mutator, nodeMap, dataMap);
      this.key1 = key1;
      this.val1 = val1;
      this.slot0 = slot0;
    }
  }

  protected static class Map5To1Node_Heterogeneous_BleedingEdge extends CompactMapNode {

    static final int payloadArity = 5;

    static final int untypedSlotArity = 1;

    static final long rareBase = arrayBase + 10 * 4 /* TODO: sizeOf(ts.ds) */;

    static final long arrayOffsetLast = rareBase;

    static final long nodeBase = rareBase + 1 * addressSize;

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

    protected Map5To1Node_Heterogeneous_BleedingEdge(final AtomicReference<Thread> mutator,
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

  protected static class Map3To7Node_Heterogeneous_BleedingEdge extends CompactMapNode {

    static final int payloadArity = 3;

    static final int untypedSlotArity = 7;

    static final long rareBase = arrayBase + 6 * 4 /* TODO: sizeOf(ts.ds) */;

    static final long arrayOffsetLast = rareBase + 6 * addressSize;

    static final long nodeBase = rareBase + 7 * addressSize;

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

    protected Map3To7Node_Heterogeneous_BleedingEdge(final AtomicReference<Thread> mutator,
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

  protected static class Map7To1Node_Heterogeneous_BleedingEdge extends CompactMapNode {

    static final int payloadArity = 7;

    static final int untypedSlotArity = 1;

    static final long rareBase = arrayBase + 14 * 4 /* TODO: sizeOf(ts.ds) */;

    static final long arrayOffsetLast = rareBase;

    static final long nodeBase = rareBase + 1 * addressSize;

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

    protected Map7To1Node_Heterogeneous_BleedingEdge(final AtomicReference<Thread> mutator,
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

  protected static class Map4To6Node_Heterogeneous_BleedingEdge extends CompactMapNode {

    static final int payloadArity = 4;

    static final int untypedSlotArity = 6;

    static final long rareBase = arrayBase + 8 * 4 /* TODO: sizeOf(ts.ds) */;

    static final long arrayOffsetLast = rareBase + 5 * addressSize;

    static final long nodeBase = rareBase + 6 * addressSize;

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

    protected Map4To6Node_Heterogeneous_BleedingEdge(final AtomicReference<Thread> mutator,
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

  protected static class Map1To3Node_Heterogeneous_BleedingEdge extends CompactMapNode {

    static final int payloadArity = 1;

    static final int untypedSlotArity = 3;

    static final long rareBase = arrayBase + 2 * 4 /* TODO: sizeOf(ts.ds) */;

    static final long arrayOffsetLast = rareBase + 2 * addressSize;

    static final long nodeBase = rareBase + 3 * addressSize;

    private final int key1;
    private final int val1;
    private final Object slot0;
    private final Object slot1;
    private final Object slot2;

    protected Map1To3Node_Heterogeneous_BleedingEdge(final AtomicReference<Thread> mutator,
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

  protected static class Map5To3Node_Heterogeneous_BleedingEdge extends CompactMapNode {

    static final int payloadArity = 5;

    static final int untypedSlotArity = 3;

    static final long rareBase = arrayBase + 10 * 4 /* TODO: sizeOf(ts.ds) */;

    static final long arrayOffsetLast = rareBase + 2 * addressSize;

    static final long nodeBase = rareBase + 3 * addressSize;

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

    protected Map5To3Node_Heterogeneous_BleedingEdge(final AtomicReference<Thread> mutator,
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

  protected static class Map3To10Node_Heterogeneous_BleedingEdge extends CompactMapNode {

    static final int payloadArity = 3;

    static final int untypedSlotArity = 10;

    static final long rareBase = arrayBase + 6 * 4 /* TODO: sizeOf(ts.ds) */;

    static final long arrayOffsetLast = rareBase + 9 * addressSize;

    static final long nodeBase = rareBase + 10 * addressSize;

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

    protected Map3To10Node_Heterogeneous_BleedingEdge(final AtomicReference<Thread> mutator,
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

  protected static class Map4To4Node_Heterogeneous_BleedingEdge extends CompactMapNode {

    static final int payloadArity = 4;

    static final int untypedSlotArity = 4;

    static final long rareBase = arrayBase + 8 * 4 /* TODO: sizeOf(ts.ds) */;

    static final long arrayOffsetLast = rareBase + 3 * addressSize;

    static final long nodeBase = rareBase + 4 * addressSize;

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

    protected Map4To4Node_Heterogeneous_BleedingEdge(final AtomicReference<Thread> mutator,
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

  protected static class Map0To12Node_Heterogeneous_BleedingEdge extends CompactMapNode {

    static final int payloadArity = 0;

    static final int untypedSlotArity = 12;

    static final long rareBase = arrayBase + 0 * 4 /* TODO: sizeOf(ts.ds) */;

    static final long arrayOffsetLast = rareBase + 11 * addressSize;

    static final long nodeBase = rareBase + 12 * addressSize;

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

    protected Map0To12Node_Heterogeneous_BleedingEdge(final AtomicReference<Thread> mutator,
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

  protected static class Map3To2Node_Heterogeneous_BleedingEdge extends CompactMapNode {

    static final int payloadArity = 3;

    static final int untypedSlotArity = 2;

    static final long rareBase = arrayBase + 6 * 4 /* TODO: sizeOf(ts.ds) */;

    static final long arrayOffsetLast = rareBase + 1 * addressSize;

    static final long nodeBase = rareBase + 2 * addressSize;

    private final int key1;
    private final int val1;
    private final int key2;
    private final int val2;
    private final int key3;
    private final int val3;
    private final Object slot0;
    private final Object slot1;

    protected Map3To2Node_Heterogeneous_BleedingEdge(final AtomicReference<Thread> mutator,
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

  protected static class Map1To13Node_Heterogeneous_BleedingEdge extends CompactMapNode {

    static final int payloadArity = 1;

    static final int untypedSlotArity = 13;

    static final long rareBase = arrayBase + 2 * 4 /* TODO: sizeOf(ts.ds) */;

    static final long arrayOffsetLast = rareBase + 12 * addressSize;

    static final long nodeBase = rareBase + 13 * addressSize;

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

    protected Map1To13Node_Heterogeneous_BleedingEdge(final AtomicReference<Thread> mutator,
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

  protected static class Map1To14Node_Heterogeneous_BleedingEdge extends CompactMapNode {

    static final int payloadArity = 1;

    static final int untypedSlotArity = 14;

    static final long rareBase = arrayBase + 2 * 4 /* TODO: sizeOf(ts.ds) */;

    static final long arrayOffsetLast = rareBase + 13 * addressSize;

    static final long nodeBase = rareBase + 14 * addressSize;

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

    protected Map1To14Node_Heterogeneous_BleedingEdge(final AtomicReference<Thread> mutator,
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

  protected static class Map3To9Node_Heterogeneous_BleedingEdge extends CompactMapNode {

    static final int payloadArity = 3;

    static final int untypedSlotArity = 9;

    static final long rareBase = arrayBase + 6 * 4 /* TODO: sizeOf(ts.ds) */;

    static final long arrayOffsetLast = rareBase + 8 * addressSize;

    static final long nodeBase = rareBase + 9 * addressSize;

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

    protected Map3To9Node_Heterogeneous_BleedingEdge(final AtomicReference<Thread> mutator,
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

  protected static class Map4To0Node_Heterogeneous_BleedingEdge extends CompactMapNode {

    static final int payloadArity = 4;

    static final int untypedSlotArity = 0;

    static final long rareBase = arrayBase + 8 * 4 /* TODO: sizeOf(ts.ds) */;

    static final long arrayOffsetLast = rareBase;

    static final long nodeBase = rareBase;

    private final int key1;
    private final int val1;
    private final int key2;
    private final int val2;
    private final int key3;
    private final int val3;
    private final int key4;
    private final int val4;

    protected Map4To0Node_Heterogeneous_BleedingEdge(final AtomicReference<Thread> mutator,
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

  protected static class Map2To12Node_Heterogeneous_BleedingEdge extends CompactMapNode {

    static final int payloadArity = 2;

    static final int untypedSlotArity = 12;

    static final long rareBase = arrayBase + 4 * 4 /* TODO: sizeOf(ts.ds) */;

    static final long arrayOffsetLast = rareBase + 11 * addressSize;

    static final long nodeBase = rareBase + 12 * addressSize;

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

    protected Map2To12Node_Heterogeneous_BleedingEdge(final AtomicReference<Thread> mutator,
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

  protected static class Map6To0Node_Heterogeneous_BleedingEdge extends CompactMapNode {

    static final int payloadArity = 6;

    static final int untypedSlotArity = 0;

    static final long rareBase = arrayBase + 12 * 4 /* TODO: sizeOf(ts.ds) */;

    static final long arrayOffsetLast = rareBase;

    static final long nodeBase = rareBase;

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

    protected Map6To0Node_Heterogeneous_BleedingEdge(final AtomicReference<Thread> mutator,
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

  protected static class Map0To6Node_Heterogeneous_BleedingEdge extends CompactMapNode {

    static final int payloadArity = 0;

    static final int untypedSlotArity = 6;

    static final long rareBase = arrayBase + 0 * 4 /* TODO: sizeOf(ts.ds) */;

    static final long arrayOffsetLast = rareBase + 5 * addressSize;

    static final long nodeBase = rareBase + 6 * addressSize;

    private final Object slot0;
    private final Object slot1;
    private final Object slot2;
    private final Object slot3;
    private final Object slot4;
    private final Object slot5;

    protected Map0To6Node_Heterogeneous_BleedingEdge(final AtomicReference<Thread> mutator,
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

  protected static class Map5To2Node_Heterogeneous_BleedingEdge extends CompactMapNode {

    static final int payloadArity = 5;

    static final int untypedSlotArity = 2;

    static final long rareBase = arrayBase + 10 * 4 /* TODO: sizeOf(ts.ds) */;

    static final long arrayOffsetLast = rareBase + 1 * addressSize;

    static final long nodeBase = rareBase + 2 * addressSize;

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

    protected Map5To2Node_Heterogeneous_BleedingEdge(final AtomicReference<Thread> mutator,
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

  protected static class Map1To2Node_Heterogeneous_BleedingEdge extends CompactMapNode {

    static final int payloadArity = 1;

    static final int untypedSlotArity = 2;

    static final long rareBase = arrayBase + 2 * 4 /* TODO: sizeOf(ts.ds) */;

    static final long arrayOffsetLast = rareBase + 1 * addressSize;

    static final long nodeBase = rareBase + 2 * addressSize;

    private final int key1;
    private final int val1;
    private final Object slot0;
    private final Object slot1;

    protected Map1To2Node_Heterogeneous_BleedingEdge(final AtomicReference<Thread> mutator,
        final byte nodeMap, final byte dataMap, final int key1, final int val1, final Object slot0,
        final Object slot1) {
      super(mutator, nodeMap, dataMap);
      this.key1 = key1;
      this.val1 = val1;
      this.slot0 = slot0;
      this.slot1 = slot1;
    }
  }

  protected static class Map2To0Node_Heterogeneous_BleedingEdge extends CompactMapNode {

    static final int payloadArity = 2;

    static final int untypedSlotArity = 0;

    static final long rareBase = arrayBase + 4 * 4 /* TODO: sizeOf(ts.ds) */;

    static final long arrayOffsetLast = rareBase;

    static final long nodeBase = rareBase;

    private final int key1;
    private final int val1;
    private final int key2;
    private final int val2;

    protected Map2To0Node_Heterogeneous_BleedingEdge(final AtomicReference<Thread> mutator,
        final byte nodeMap, final byte dataMap, final int key1, final int val1, final int key2,
        final int val2) {
      super(mutator, nodeMap, dataMap);
      this.key1 = key1;
      this.val1 = val1;
      this.key2 = key2;
      this.val2 = val2;
    }
  }

  protected static class Map0To4Node_Heterogeneous_BleedingEdge extends CompactMapNode {

    static final int payloadArity = 0;

    static final int untypedSlotArity = 4;

    static final long rareBase = arrayBase + 0 * 4 /* TODO: sizeOf(ts.ds) */;

    static final long arrayOffsetLast = rareBase + 3 * addressSize;

    static final long nodeBase = rareBase + 4 * addressSize;

    private final Object slot0;
    private final Object slot1;
    private final Object slot2;
    private final Object slot3;

    protected Map0To4Node_Heterogeneous_BleedingEdge(final AtomicReference<Thread> mutator,
        final byte nodeMap, final byte dataMap, final Object slot0, final Object slot1,
        final Object slot2, final Object slot3) {
      super(mutator, nodeMap, dataMap);
      this.slot0 = slot0;
      this.slot1 = slot1;
      this.slot2 = slot2;
      this.slot3 = slot3;
    }
  }

  protected static class Map0To11Node_Heterogeneous_BleedingEdge extends CompactMapNode {

    static final int payloadArity = 0;

    static final int untypedSlotArity = 11;

    static final long rareBase = arrayBase + 0 * 4 /* TODO: sizeOf(ts.ds) */;

    static final long arrayOffsetLast = rareBase + 10 * addressSize;

    static final long nodeBase = rareBase + 11 * addressSize;

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

    protected Map0To11Node_Heterogeneous_BleedingEdge(final AtomicReference<Thread> mutator,
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

  protected static class Map7To2Node_Heterogeneous_BleedingEdge extends CompactMapNode {

    static final int payloadArity = 7;

    static final int untypedSlotArity = 2;

    static final long rareBase = arrayBase + 14 * 4 /* TODO: sizeOf(ts.ds) */;

    static final long arrayOffsetLast = rareBase + 1 * addressSize;

    static final long nodeBase = rareBase + 2 * addressSize;

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

    protected Map7To2Node_Heterogeneous_BleedingEdge(final AtomicReference<Thread> mutator,
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

}
