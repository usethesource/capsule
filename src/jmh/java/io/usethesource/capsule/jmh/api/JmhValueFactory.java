/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.capsule.jmh.api;

public interface JmhValueFactory {

  RuntimeException FACTORY_NOT_YET_IMPLEMENTED_EXCEPTION =
      new UnsupportedOperationException("Not yet implemented.");

  // default JmhSet set() {
  // return setBuilder().done();
  // }

  default JmhSet.Builder setBuilder() {
    throw FACTORY_NOT_YET_IMPLEMENTED_EXCEPTION;
  }

  // default JmhMap map() {
  // return mapBuilder().done();
  // }

  default JmhMap.Builder mapBuilder() {
    throw FACTORY_NOT_YET_IMPLEMENTED_EXCEPTION;
  }

  // default JmhSetMultimap setMulimap() {
  // return setMultimapBuilder().done();
  // }

  default JmhSetMultimap.Builder setMultimapBuilder() {
    throw FACTORY_NOT_YET_IMPLEMENTED_EXCEPTION;
  }

}
