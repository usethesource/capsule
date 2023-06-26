/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.criterion;

/// *******************************************************************************
// * Copyright (c) 2015 CWI
// * All rights reserved. This program and the accompanying materials
// * are made available under the terms of the Eclipse Public License v1.0
// * which accompanies this distribution, and is available at
// * http://www.eclipse.org/legal/epl-v10.html
// *
// * Contributors:
// *
// * * Michael Steindorfer - Michael.Steindorfer@cwi.nl - CWI
// *******************************************************************************/
// package nl.cwi.swat.jmh_dscg_benchmarks;
//
// import static java.util.Arrays.stream;
// import static java.util.stream.Collectors.collectingAndThen;
// import static java.util.stream.Collectors.joining;
// import static java.util.stream.Collectors.toMap;
//
// import java.io.IOException;
// import java.lang.reflect.Field;
// import java.nio.file.Files;
// import java.nio.file.Path;
// import java.nio.file.Paths;
// import java.nio.file.StandardOpenOption;
// import java.util.Arrays;
// import java.util.Collection;
// import java.util.Collections;
// import java.util.HashSet;
// import java.util.LinkedHashMap;
// import java.util.LinkedHashSet;
// import java.util.List;
// import java.util.Map;
// import java.util.Set;
// import java.util.function.Function;
// import java.util.stream.Collector;
//
// import org.openjdk.jmh.annotations.Param;
//
// import ch.usi.overseer.OverHpc;
//
// public class OverseerUtils {
//
// private static final boolean DO_START_STOP = true; //
/// System.getProperties().containsKey("overseer.utils.doStartStop");
//
// private static final Set<String> PERF_EVENTS;
//
// private static final String PERF_OUTPUT_SEPARATOR = System.getProperty(
// "overseer.utils.output.separator", ",");
//
// private static final String PERF_OUTPUT_FILE = System.getProperty("overseer.utils.output.file",
// "perf_events.log");
//
// static {
// List<String> eventList = Arrays.asList(System.getProperty("overseer.utils.events",
// "LLC_REFERENCES,LLC_MISSES").split(","));
//
// Set<String> eventSet = new LinkedHashSet<>(eventList);
//
// PERF_EVENTS = Collections.unmodifiableSet(eventSet);
//
//// try {
//// Path outputFilePath = Paths.get(PERF_OUTPUT_FILE);
//// Files.delete(outputFilePath);
//// } catch (IOException e) {
//// // TODO Auto-generated catch block
//// e.printStackTrace();
//// }
// }
//
// private static long[] longResults = new long[PERF_EVENTS.size()];
// private static OverHpc oHpc = OverHpc.getInstance();
//
// private static Map<String, String> setupParamBindings = Collections.emptyMap();
//
// public static void setup(final Class benchmarkClazz, final Object benchmarkInstance) {
//// System.out.println("OVERSEER [SETUP]");
//
// setupParamBindings = caputureJmhBenchmarkParamsAndValues(benchmarkClazz, benchmarkInstance);
//
// Collection<String> availableEvents = Collections.unmodifiableCollection(Arrays.asList(oHpc
// .getAvailableEventsString().split("\n")));
//
// Collection<String> unavailableEvents = new HashSet<>(PERF_EVENTS);
// unavailableEvents.removeAll(availableEvents);
//
//// System.out.println(" AVAILABLE EVENTS: " + String.join(",", availableEvents));
//// System.out.println();
//// System.out.println(" USED EVENTS: " + String.join(",", PERF_EVENTS));
//// System.out.println();
//// System.out.println("UNAVAILABLE EVENTS: " + String.join(",", unavailableEvents));
//// System.out.println();
//
// // msteindorfer: disabled, because it can only check events without
// // options
// // ASSERT_RESULT(availableEvents.containsAll(EVENTS));
//
// ASSERT_RESULT(oHpc.initEvents(String.join(",", PERF_EVENTS)));
// ASSERT_RESULT(oHpc.bindEventsToThread());
//
// if (!DO_START_STOP) {
//// System.out.println("OVERSEER [RECORD ON]");
// ASSERT_RESULT(oHpc.start());
// }
// }
//
// public static void doRecord(boolean doEnable) {
// if (DO_START_STOP) {
// int tid = oHpc.getThreadId();
//
// if (doEnable) {
//// System.out.println("OVERSEER [RECORD ON]");
// ASSERT_RESULT(oHpc.start());
// } else {
//// System.out.println("OVERSEER [RECORD OFF]");
// ASSERT_RESULT(oHpc.stop());
// for (int i = 0; i < PERF_EVENTS.size(); i++) {
// longResults[i] = oHpc.getEventFromThread(tid, i);
// }
//
// // intermediate results
// printResults();
// }
// }
// }
//
// public static void tearDown() {
//// System.out.println("OVERSEER [TEAR DOWN]");
//
// if (!DO_START_STOP) {
// int tid = oHpc.getThreadId();
//
//// System.out.println("OVERSEER [RECORD OFF]");
// ASSERT_RESULT(oHpc.stop());
// for (int i = 0; i < PERF_EVENTS.size(); i++) {
// longResults[i] = oHpc.getEventFromThread(tid, i);
// }
// }
//
// ASSERT_RESULT(oHpc.logToFile("overseer.log"));
//
// OverHpc.shutdown();
//
// // final results
// // printResults();
// }
//
// private static void printResults() {
//// System.out.println();
//// for (int i = 0; i < PERF_EVENTS.size(); i++) {
//// System.out.println(PERF_EVENTS.toArray()[i] + ": " + String.format("%,d", longResults[i]));
//// }
//// System.out.println();
//
// Map<String, Object> paramBindingPlusPerfEvents = new LinkedHashMap<>();
// paramBindingPlusPerfEvents.putAll(setupParamBindings);
//
// // TODO: replace longResults with a map from String -> Long
// for (int i = 0; i < PERF_EVENTS.size(); i++) {
// paramBindingPlusPerfEvents.put((String) PERF_EVENTS.toArray()[i], longResults[i]);
// }
//
//// paramBindingPlusPerfEvents.forEach((key, value) -> System.out.println(key + " " + value));
//
// try {
// Path outputFilePath = Paths.get(PERF_OUTPUT_FILE);
//
// if (!Files.exists(outputFilePath)) {
// Files.createFile(outputFilePath);
//
// String outputFileHeader = String.join(PERF_OUTPUT_SEPARATOR,
// paramBindingPlusPerfEvents.keySet());
//
// Files.write(outputFilePath, Collections.singleton(outputFileHeader),
// StandardOpenOption.APPEND);
// }
//
// String measurements = paramBindingPlusPerfEvents.values().stream().map(String::valueOf)
// .collect(joining(PERF_OUTPUT_SEPARATOR));
//
// Files.write(outputFilePath, Collections.singleton(measurements),
// StandardOpenOption.APPEND);
// } catch (IOException e) {
// // TODO Auto-generated catch block
// e.printStackTrace();
// }
// }
//
// private static void ASSERT_RESULT(boolean result) {
// if (!result) {
// throw new RuntimeException("Problem with overseer library setup.");
// }
// }
//
// //
// // Collecting pair of @Param field names and values.
// //
//
// private static Map<String, String> caputureJmhBenchmarkParamsAndValues(final Class
/// benchmarkClazz, final Object benchmarkInstance) {
// Function<Field, String> getFieldValueAsString = (field) -> {
// try {
// return field.get(benchmarkInstance).toString();
// } catch (IllegalArgumentException | IllegalAccessException e) {
// return "???";
// }
// };
//
// final Field[] declaredFields = benchmarkClazz.getDeclaredFields();
//
//// System.out.println(benchmarkClazz);
//// System.out.println(benchmarkInstance.getClass());
//// System.out.println("Declared fields:");
//// Arrays.asList(declaredFields).forEach(field -> System.out.println(field.getName()));
//
// final Map<String, String> paramBindings = stream(declaredFields)
// .filter(field -> field.isAnnotationPresent(Param.class))
// .collect(toUnmodifiableMap(Field::getName, getFieldValueAsString));
//
//// System.out.println("Params with annotations:");
//// paramBindings.forEach((key, value) -> System.out.println(key + " " + value));
//
// return paramBindings;
// }
//
// /*
// * see
/// http://stackoverflow.com/questions/22601660/java-8-lambda-specify-map-type-and-make-it-unmodifiable
// */
// public static <T, K, U> Collector<T, ?, Map<K, U>> toUnmodifiableMap(
// Function<? super T, ? extends K> keyMapper,
// Function<? super T, ? extends U> valueMapper) {
// return collectingAndThen(toMap(keyMapper, valueMapper), Collections::unmodifiableMap);
// }
//
// }
