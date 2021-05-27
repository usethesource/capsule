/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.capsule.core.trie;

import java.util.Optional;

public interface MultimapResult<K, V, C> {

  static <K, V, C> MultimapResultImpl<K, V, C> unchanged() {
    return new MultimapResultImpl<>();
  }

  Modification getModificationEffect();

  boolean containsModification(Modification m);

  void modified(Modification modificationEffect, int modificationDetails);

  void modified(Modification modificationEffect, int modificationDetails, int sizeDelta);

  void modified(Modification modificationEffect, int modificationDetails, C evictedPayload);

  Optional<C> getEvictedPayload();

  Optional<Integer> sizeDelta();
  
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
    REMOVED_KEY_VALUE_COLLECTION;

    public static int flag(Modification a) {
      return 1 << a.ordinal();
    }

    public static int flag(Modification a, Modification b) {
      return 1 << a.ordinal() | 1 << b.ordinal();
    }

    public static int flag(Modification a, Modification b, Modification c) {
      return 1 << a.ordinal() | 1 << b.ordinal() | 1  << c.ordinal();
    }

    public static boolean isSet(int flags, Modification test) {
      return (flags & flag(test)) != 0;
    }
  }

}
