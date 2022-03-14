# The Capsule Hash Trie Collections Library

## Status

![capsule build status](https://github.com/usethesource/capsule/actions/workflows/build.yaml/badge.svg)

## Synopsis

Capsule aims to become a full-fledged (immutable) collections library for Java 11+ that is solely built around persistent tries. The library is designed for standalone use and for being embedded in domain-specific languages. Capsule still has to undergo some incubation before it can ship as a well-rounded collection library. Nevertheless, the code is stable and performance is solid. Feel free to use it and let us know about your experiences!

# Getting Started

Binary builds of Capsule are deployed in the usethesource repository. In case you use Maven for dependency management, you have to add another repository location to your pom.xml file:

```
<repositories>
  <repository>
    <id>usethesource</id>
    <url>https://releases.usethesource.io/maven/</url>
  </repository>
</repositories>
```

Furthermore, you have to declare Capsule as a dependency.

To obtain the latest release for Java 11+, insert the following snippet in your `pom.xml` file:

```
<dependency>
  <groupId>io.usethesource</groupId>
  <artifactId>capsule</artifactId>
  <version>0.7.1</version>
</dependency>
```

To obtain the latest available version for Java 8, insert the following snippet in your `pom.xml` file:

```
<dependency>
  <groupId>io.usethesource</groupId>
  <artifactId>capsule</artifactId>
  <version>0.6.4</version>
</dependency>
```

Snippets for other build tools and dependency management systems may vary slightly.

# Background: Efficient Immutable Data Structures on the JVM
The standard libraries of recent Java Virtual Machine languages, such as Clojure or Scala, contain scalable and well-performing immutable collection data structures that are implemented as Hash-Array Mapped Tries (HAMTs). HAMTs already feature efficient lookup, insert, and delete operations, however due to their tree-based nature their memory footprints and the runtime performance of iteration and equality checking lag behind array-based counterparts.

We introduce CHAMP (Compressed Hash-Array Mapped Prefix-tree), an evolutionary improvement over HAMTs. The new design increases the overall performance of immutable sets and maps. Furthermore, its resulting general purpose design increases cache locality and features a canonical representation.

# References and Further Readings

## Talks
* [JVM Language Summit 2016 - Efficient and Expressive Immutable Collections (Speaker: Michael Steindorfer)](https://www.youtube.com/watch?v=pUXeNAeyY34)
* [JVM Language Summit 2017 - Lightweight Relations (Speaker: Michael Steindorfer)](https://www.youtube.com/watch?v=D8Y294vHdqI)
* [Clojure/west 2016 - Hash Maps: More Room on the Bottom (Speaker: Peter Schuck)](https://www.youtube.com/watch?v=GibNOQVelFY)

## Publications
* [PhD Thesis: Efficient Immutable Collections (2017)](https://michael.steindorfer.name/publications/phd-thesis-efficient-immutable-collections)
* [Paper: Optimizing Hash-Array Mapped Tries for Fast and Lean Immutable JVM Collections (OOPSLA 2015)](https://michael.steindorfer.name/publications/oopsla15.pdf)
* [Paper: To-Many or To-One? All-in-One! Efficient Purely Functional Multi-maps with Type-Heterogeneous Hash-Tries (PLDI 2018)](https://michael.steindorfer.name/publications/pldi18.pdf)
* [Paper: Towards a Software Product Line of Trie-Based Collections (Short Paper, GPCE 2016)](https://michael.steindorfer.name/publications/gpce16.pdf)
* [Paper: Code Specialization for Memory Efficient Hash Tries (Short Paper, GPCE 2014)](https://michael.steindorfer.name/publications/gpce14.pdf)
