///**
// * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
// * All rights reserved.
// *
// * This file is licensed under the BSD 2-Clause License, which accompanies this project
// * and is available under https://opensource.org/licenses/BSD-2-Clause.
// */
//package io.usethesource.capsule.generators;
//
//import static io.usethesource.capsule.util.collection.AbstractSpecialisedImmutableMap.entryOf;
//
//import java.util.Map;
//import java.util.function.BiFunction;
//import java.util.function.Function;
//
//import io.usethesource.capsule.DefaultTrieSetMultimap;
//import io.usethesource.capsule.api.deprecated.ImmutableSetMultimap;
//import io.usethesource.capsule.api.deprecated.ImmutableSetMultimapAsImmutableSetView;
//
//@SuppressWarnings({"rawtypes", "unchecked"})
//public class SetGeneratorMultimapAsSetView extends
//    AbstractSetGenerator<ImmutableSetMultimapAsImmutableSetView<Integer, Integer, Map.Entry<Integer, Integer>>> {
//
//  public SetGeneratorMultimapAsSetView() {
//    super((Class) ImmutableSetMultimapAsImmutableSetView.class);
//  }
//
//  @Override
//  protected final ImmutableSetMultimapAsImmutableSetView<Integer, Integer, Map.Entry<Integer, Integer>> empty() {
//    final ImmutableSetMultimap<Integer, Integer> multimap = DefaultTrieSetMultimap.of();
//
//    final BiFunction<Integer, Integer, Map.Entry<Integer, Integer>> tupleOf =
//        (first, second) -> entryOf(first, second);
//
//    final BiFunction<Map.Entry<Integer, Integer>, Integer, Integer> tupleElementAt =
//        (tuple, position) -> {
//          switch (position) {
//            case 0:
//              return tuple.getKey();
//            case 1:
//              return tuple.getValue();
//            default:
//              throw new IllegalStateException();
//          }
//        };
//
//    final Function<Map.Entry<Integer, Integer>, Boolean> tupleChecker = (argument) -> true;
//
//    return new ImmutableSetMultimapAsImmutableSetView(multimap, tupleOf, tupleElementAt,
//        tupleChecker);
//  }
//
//}
