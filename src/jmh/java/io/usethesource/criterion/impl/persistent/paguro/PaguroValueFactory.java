/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.criterion.impl.persistent.paguro;

import io.usethesource.criterion.api.JmhMap;
import io.usethesource.criterion.api.JmhSet;
import io.usethesource.criterion.api.JmhValueFactory;

public class PaguroValueFactory implements JmhValueFactory {

  @Override
  public JmhSet.Builder setBuilder() {
    return new PaguroSetBuilder();
  }

  @Override
  public JmhMap.Builder mapBuilder() {
    return new PaguroMapBuilder();
  }

  @Override
  public String toString() {
    return "VF_PAGURO";
  }

}
