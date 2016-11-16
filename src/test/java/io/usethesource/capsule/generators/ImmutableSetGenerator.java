package io.usethesource.capsule.generators;

import com.pholser.junit.quickcheck.generator.java.util.CollectionGenerator;

import io.usethesource.capsule.api.deprecated.ImmutableSet;

@SuppressWarnings("rawtypes")
public abstract class ImmutableSetGenerator<T extends ImmutableSet> extends CollectionGenerator<T> {
  protected ImmutableSetGenerator(Class<T> type) {
    super(type);
  }
}
