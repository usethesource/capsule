/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.capsule.jmh;

import io.usethesource.capsule.jmh.api.JmhValue;

public enum ElementProducer {

  PDB_INTEGER {
    @Override
    public JmhValue createFromInt(int value) {
      return new PureIntegerWithCustomHashCode(value);
    }
  },
  PURE_INTEGER {
    @Override
    public JmhValue createFromInt(int value) {
      return new PureInteger(value);
    }
  },
  SLEEPING_INTEGER {
    @Override
    public JmhValue createFromInt(int value) {
      return new SleepingInteger(value);
    }
  },
  COUNTING_INTEGER {
    @Override
    public JmhValue createFromInt(int value) {
      return new CountingInteger(value);
    }
  };

  public abstract JmhValue createFromInt(int value);

}
