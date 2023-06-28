#!/bin/zsh

# Usage:
# `./benchmark.zsh` ......... benchmark run-time performance and memory footprint
# `./benchmark.zsh time` .... benchmark run-time performance only
# `./benchmark.zsh space` ... benchmark memory footprint only

set -euo pipefail

benchmark_selector=${1:-}

export PARAM_SIZE="8,2048,1048576"
export PARAM_VALUE_FACTORY="VF_CAPSULE,VF_SCALA,VF_CLOJURE"
export MEMORY_MEASURER_AGENT="$(./gradlew memoryMeasurerLocation --quiet)"

export JMH_JAR=`find $(pwd) -name "*-jmh.jar"`

####
# MEASURE RUNTIMES
##################################

if [[ -z "$benchmark_selector" || "$benchmark_selector" == "time" ]]; then
  java -jar $JMH_JAR \
    "JmhSetBenchmarks.time(Lookup$|Insert$|Remove$|Iteration|EqualsWorstCase)$" \
    -f 1 -i 5 -wi 5 \
    -p producer=PURE_INTEGER \
    -p size=$PARAM_SIZE \
    -p valueFactoryFactory=$PARAM_VALUE_FACTORY \
    -rf text -rff log-jmh-persistent-collections-time.txt

  # further benchmarks: SubsetOf|(Union|Subtract|Intersect)RealDuplicate
fi

###
# MEASURE MEMORY FOOTPRINT
#################################

if [[ -z "$benchmark_selector" || "$benchmark_selector" == "space" ]]; then
  java -javaagent:$MEMORY_MEASURER_AGENT -jar $JMH_JAR \
    "JmhSetBenchmarks.footprint$" \
    -f 1 -bm ss \
    -p producer=PURE_INTEGER \
    -p size=$PARAM_SIZE \
    -p valueFactoryFactory=$PARAM_VALUE_FACTORY \
    -prof io.usethesource.capsule.jmh.profiler.MemoryFootprintProfiler \
    -rf text -rff log-jmh-persistent-collections-space.txt
fi
