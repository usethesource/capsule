/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.capsule.util.iterator;

import java.util.Iterator;
import java.util.function.Supplier;

public interface SupplierIterator<K, V> extends Iterator<K>, Supplier<V> {

}
