# The Capsule Hash Trie Collections Library
Capsule aims to become a full-fledged (immutable) collections library that is solely built around persistent tries.

Capsule was recently extracted from the [usethesource/rascal-value](https://github.com/usethesource/rascal-value) project and still has to undergo some incubation before it can ship as a well-rounded collection library. Nevertheless, the code is stable and performance is already solid. Feel free to use it and let us about your experiences!

More extensive tests and performance benchmarks will be added soon. The preliminary API for the immutable interfaces will be reworked as soon as possible as well.

# Background: Efficient Immutable Data Structures on the JVM
The standard libraries of recent Java Virtual Machine languages, such as Clojure or Scala, contain scalable and well-performing immutable collection data structures that are implemented as Hash-Array Mapped Tries (HAMTs). HAMTs already feature efficient lookup, insert, and delete operations, however due to their tree-based nature their memory footprints and the runtime performance of iteration and equality checking lag behind array-based counterparts.

We introduce CHAMP (Compressed Hash-Array Mapped Prefix-tree), an evolutionary improvement over HAMTs. The new design increases the overall performance of immutable sets and maps. Furthermore, its resulting general purpose design increases cache locality and features a canonical representation. 

# References and Further Readings
* [Paper: Optimizing Hash-Array Mapped Tries for Fast and Lean Immutable JVM Collections (OOPSLA 2015)](http://michael.steindorfer.name/publications/oopsla15.pdf)
* [Paper: Fast and Lean Immutable Multi-Maps on the JVM based on Heterogeneous Hash-Array Mapped Tries (Draft, 2016)](https://arxiv.org/abs/1608.01036)
* [Paper: Towards a Software Product Line of Trie-Based Collections (Draft, 2016)](http://michael.steindorfer.name/drafts/gpce16.pdf)
* [Paper: Code Specialization for Memory Efficient Hash Tries (Short Paper, GPCE 2014)](http://michael.steindorfer.name/publications/gpce14.pdf)
