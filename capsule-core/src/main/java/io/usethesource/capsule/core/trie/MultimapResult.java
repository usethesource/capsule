/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.capsule.core.trie;

import java.util.EnumSet;

public interface MultimapResult<K, V, C> {

  Modification getModificationEffect();

  EnumSet<Modification> getModificationDetails();

  void modified(Modification modificationEffect, EnumSet<Modification> modificationDetails);

  void modified(Modification modificationEffect, EnumSet<Modification> modificationDetails,
      int sizeDelta);

  void modified(Modification modificationEffect, EnumSet<Modification> modificationDetails,
      C evictedPayload);

  @Deprecated
  default boolean isModified() {
    throw new UnsupportedOperationException();
  }

  @Deprecated
  default void modified() {
    throw new UnsupportedOperationException();
  }

  @Deprecated
  default void updatedSingle(V currentVal) {
    throw new UnsupportedOperationException();
  }

  @Deprecated
  default void updatedMultiple(C currentCollVal) {
    throw new UnsupportedOperationException();
  }

  enum Modification {
    NOTHING,

    INSERTED_PAYLOAD,
    INSERTED_KEY,
    INSERTED_VALUE,
    INSERTED_VALUE_COLLECTION,
    INSERTED_KEY_VALUE,
    INSERTED_KEY_VALUE_COLLECTION,

    REPLACED_PAYLOAD,
    REPLACED_VALUE,
    REPLACED_VALUE_COLLECTION,

    REMOVED_PAYLOAD,
    REMOVED_KEY,
    REMOVED_VALUE,
    REMOVED_VALUE_COLLECTION,
    REMOVED_KEY_VALUE,
    REMOVED_KEY_VALUE_COLLECTION
  }
}
