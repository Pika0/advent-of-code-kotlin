@file:Suppress("FunctionName")

package benchmarks

import loadAndReadInput
import org.openjdk.jmh.annotations.*
import java.util.concurrent.*

/*run this file's benchmarks with
./gradlew benchmark  -Pbenchmark.filter=Day11_benchmark
 */

@Suppress("ClassName")
@State(Scope.Benchmark)
@Fork(1)
@Warmup(iterations = 0) //0.5s each if timed
//@BenchmarkMode(Mode.AverageTime)
//@Measurement(iterations = 1, time = 2, timeUnit = TimeUnit.SECONDS)
@BenchmarkMode(Mode.SingleShotTime)
@Measurement(iterations = 1)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
open class Day11_benchmark {

    private var inputFileString = ""

    @Setup
    fun setUp() {
        inputFileString = loadAndReadInput(11, 2024)
    }

    @Benchmark
    fun part2a_initial(): Long {
        fun stoneTransform(stone: Long): List<Long> {
            if (stone == 0L) return listOf(1)
            val s = stone.toString()
            if (s.length % 2 == 0) {
                return listOf(
                    s.substring(0, s.length / 2).toLong(),
                    s.substring(s.length / 2).toLong()
                )
            }
            return listOf(stone * 2024)
        }
        @Suppress("LocalVariableName")
        val stonesFromBlinks_memo = hashMapOf<Pair<Long, Int>, Long>()
        fun stonesFromBlinks(stone: Long, numberOfBlinks: Int): Long{
            if (numberOfBlinks==0) return 1
            val key = Pair(stone, numberOfBlinks)
            if (key in stonesFromBlinks_memo) return stonesFromBlinks_memo[key]!!
            val nextStones = stoneTransform(stone)
            val numStones = nextStones.sumOf{nextStone-> stonesFromBlinks(nextStone, numberOfBlinks-1) }
            stonesFromBlinks_memo[key] = numStones
            return numStones
        }
        val lines = inputFileString.lines()
        val stones = lines[0].split(" ").map { it.toLong() }

        val numBlink = 75
        val ans = stones.sumOf{ stone -> stonesFromBlinks(stone, numBlink)}
        check(ans == 205913561055242) {"incorrect answer!"}
        return ans
    }

    @Benchmark
    fun part2b_keyBeforeZero(): Long {
        fun stoneTransform(stone: Long): List<Long> {
            if (stone == 0L) return listOf(1)
            val s = stone.toString()
            if (s.length % 2 == 0) {
                return listOf(
                    s.substring(0, s.length / 2).toLong(),
                    s.substring(s.length / 2).toLong()
                )
            }
            return listOf(stone * 2024)
        }
        @Suppress("LocalVariableName")
        val stonesFromBlinks_memo = hashMapOf<Pair<Long, Int>, Long>()
        fun stonesFromBlinks(stone: Long, numberOfBlinks: Int): Long{
            val key = Pair(stone, numberOfBlinks)
            if (key in stonesFromBlinks_memo) return stonesFromBlinks_memo[key]!!
            if (numberOfBlinks==0) return 1
            val nextStones = stoneTransform(stone)
            val numStones = nextStones.sumOf{nextStone-> stonesFromBlinks(nextStone, numberOfBlinks-1) }
            stonesFromBlinks_memo[key] = numStones
            return numStones
        }
        val lines = inputFileString.lines()
        val stones = lines[0].split(" ").map { it.toLong() }

        val numBlink = 75
        val ans = stones.sumOf{ stone -> stonesFromBlinks(stone, numBlink)}
        check(ans == 205913561055242) {"incorrect answer!"}
        return ans
    }

    @Benchmark
    fun part2c_memoTransform(): Long {
        @Suppress("LocalVariableName")
        val stoneTransform_memo = hashMapOf<Long, List<Long>>()
        fun stoneTransform(stone: Long): List<Long> {
            return stoneTransform_memo.getOrPut(stone) {
                if (stone == 0L) return@getOrPut listOf(1)
                val s = stone.toString()
                if (s.length % 2 == 0) {
                    return@getOrPut listOf(
                        s.substring(0, s.length / 2).toLong(),
                        s.substring(s.length / 2).toLong()
                    )
                }
                return@getOrPut listOf(stone * 2024)
            }
        }
        @Suppress("LocalVariableName")
        val stonesFromBlinks_memo = hashMapOf<Pair<Long, Int>, Long>()
        fun stonesFromBlinks(stone: Long, numberOfBlinks: Int): Long{
            val key = Pair(stone, numberOfBlinks)
            if (key in stonesFromBlinks_memo) return stonesFromBlinks_memo[key]!!
            if (numberOfBlinks==0) return 1
            val nextStones = stoneTransform(stone)
            val numStones = nextStones.sumOf{nextStone-> stonesFromBlinks(nextStone, numberOfBlinks-1) }
            stonesFromBlinks_memo[key] = numStones
            return numStones
        }
        val lines = inputFileString.lines()
        val stones = lines[0].split(" ").map { it.toLong() }

        val numBlink = 75
        val ans = stones.sumOf{ stone -> stonesFromBlinks(stone, numBlink)}
        check(ans == 205913561055242) {"incorrect answer!"}
        return ans
    }

    @Benchmark
    fun part2d_memoBlinksUsesPut(): Long {
        @Suppress("LocalVariableName")
        val stoneTransform_memo = hashMapOf<Long, List<Long>>()
        fun stoneTransform(stone: Long): List<Long> {
            return stoneTransform_memo.getOrPut(stone) {
                if (stone == 0L) return@getOrPut listOf(1)
                val s = stone.toString()
                if (s.length % 2 == 0) {
                    return@getOrPut listOf(
                        s.substring(0, s.length / 2).toLong(),
                        s.substring(s.length / 2).toLong()
                    )
                }
                return@getOrPut listOf(stone * 2024)
            }
        }
        @Suppress("LocalVariableName")
        val stonesFromBlinks_memo = hashMapOf<Pair<Long, Int>, Long>()
        fun stonesFromBlinks(stone: Long, numberOfBlinks: Int): Long{
            return stonesFromBlinks_memo.getOrPut(Pair(stone, numberOfBlinks)){
                if (numberOfBlinks==0) return@getOrPut  1
                val nextStones = stoneTransform(stone)
                val numStones = nextStones.sumOf{nextStone-> stonesFromBlinks(nextStone, numberOfBlinks-1) }
                return@getOrPut numStones
            }
        }
        val lines = inputFileString.lines()
        val stones = lines[0].split(" ").map { it.toLong() }

        val numBlink = 75
        val ans = stones.sumOf{ stone -> stonesFromBlinks(stone, numBlink)}
        check(ans == 205913561055242) {"incorrect answer!"}
        return ans
    }


    @Benchmark
    fun part2e_inlineStoneTransform(): Long {
        @Suppress("LocalVariableName")
        val stoneTransform_memo = hashMapOf<Long, List<Long>>()
        @Suppress("LocalVariableName")
        val stonesFromBlinks_memo = hashMapOf<Pair<Long, Int>, Long>()
        fun stonesFromBlinks(stone: Long, numberOfBlinks: Int): Long{
            return stonesFromBlinks_memo.getOrPut(Pair(stone, numberOfBlinks)){
                if (numberOfBlinks==0) return@getOrPut  1
                val nextStones = stoneTransform_memo.getOrPut(stone) transformStone@{
                    if (stone == 0L) return@transformStone listOf(1)
                    val s = stone.toString()
                    if (s.length % 2 == 0) {
                        return@transformStone listOf(
                            s.substring(0, s.length / 2).toLong(),
                            s.substring(s.length / 2).toLong()
                        )
                    }
                    return@transformStone listOf(stone * 2024)
                }
                val numStones = nextStones.sumOf{nextStone-> stonesFromBlinks(nextStone, numberOfBlinks-1) }
                return@getOrPut numStones
            }
        }
        val lines = inputFileString.lines()
        val stones = lines[0].split(" ").map { it.toLong() }

        val numBlink = 75
        val ans = stones.sumOf{ stone -> stonesFromBlinks(stone, numBlink)}
        check(ans == 205913561055242) {"incorrect answer!"}
        return ans
    }

    @Benchmark
    fun part2f_partialStoneTransformNoLists(): Long {
        @Suppress("LocalVariableName")
        val stoneTransform_memo = hashMapOf<Long, Pair<Long,Long>>()
        @Suppress("LocalVariableName")
        val stonesFromBlinks_memo = hashMapOf<Pair<Long, Int>, Long>()
        fun stonesFromBlinks(stone: Long, numberOfBlinks: Int): Long{
            return stonesFromBlinks_memo.getOrPut(Pair(stone, numberOfBlinks)){
                if (numberOfBlinks==0) return@getOrPut  1
                if (stone == 0L) return@getOrPut stonesFromBlinks(1, numberOfBlinks-1)

                val s = stone.toString()
                if (s.length % 2 == 0) {
                    val (s1, s2) = stoneTransform_memo.getOrPut(stone) transformStone@{
                        return@transformStone Pair(
                            s.substring(0, s.length / 2).toLong(),
                            s.substring(s.length / 2).toLong()
                        )
                    }
                    return@getOrPut stonesFromBlinks(s1, numberOfBlinks-1) + stonesFromBlinks(s2, numberOfBlinks-1)
                }
                return@getOrPut stonesFromBlinks(stone * 2024, numberOfBlinks-1)

            }
        }
        val lines = inputFileString.lines()
        val stones = lines[0].split(" ").map { it.toLong() }

        val numBlink = 75
        val ans = stones.sumOf{ stone -> stonesFromBlinks(stone, numBlink)}
        check(ans == 205913561055242) {"incorrect answer!"}
        return ans
    }

    @Benchmark
    fun part2g_eExceptMemoBlinksNoPut(): Long {
        @Suppress("LocalVariableName")
        val stoneTransform_memo = hashMapOf<Long, List<Long>>()
        @Suppress("LocalVariableName")
        val stonesFromBlinks_memo = hashMapOf<Pair<Long, Int>, Long>()
        fun stonesFromBlinks(stone: Long, numberOfBlinks: Int): Long{
            val key = Pair(stone, numberOfBlinks)
            val numStones =
                if (key in stonesFromBlinks_memo)  stonesFromBlinks_memo[key]!!
                else if (numberOfBlinks==0)   1
                else {
                    val nextStones = stoneTransform_memo.getOrPut(stone) transformStone@{
                        if (stone == 0L) return@transformStone listOf(1)
                        val s = stone.toString()
                        if (s.length % 2 == 0) {
                            return@transformStone listOf(
                                s.substring(0, s.length / 2).toLong(),
                                s.substring(s.length / 2).toLong()
                            )
                        }
                        return@transformStone listOf(stone * 2024)
                    }
                    nextStones.sumOf { nextStone -> stonesFromBlinks(nextStone, numberOfBlinks - 1) }
                }
            stonesFromBlinks_memo[key] = numStones
            return numStones
        }
        val lines = inputFileString.lines()
        val stones = lines[0].split(" ").map { it.toLong() }

        val numBlink = 75
        val ans = stones.sumOf{ stone -> stonesFromBlinks(stone, numBlink)}
        check(ans == 205913561055242) {"incorrect answer!"}
        return ans
    }

/* singleShotTime(100 each)
Benchmark                                            Mode  Cnt   Score   Error  Units
Day11_benchmark.part2a_initial                         ss  100  37.301 ± 3.393  ms/op
Day11_benchmark.part2b_keyBeforeZero                   ss  100  38.124 ± 4.644  ms/op
Day11_benchmark.part2c_memoTransform                   ss  100  32.227 ± 4.668  ms/op
Day11_benchmark.part2d_memoBlinksUsesPut               ss  100  33.753 ± 2.533  ms/op
Day11_benchmark.part2e_inlineStoneTransform            ss  100  27.012 ± 2.619  ms/op
Day11_benchmark.part2f_partialStoneTransformNoLists    ss  100  29.214 ± 2.266  ms/op
Day11_benchmark.part2g_eExceptMemoBlinksNoPut          ss  100  31.514 ± 3.351  ms/op

AverageTime(2s)
Benchmark                                            Mode  Cnt   Score   Error  Units
Day11_benchmark.part2a_initial                       avgt       41.061          ms/op
Day11_benchmark.part2b_keyBeforeZero                 avgt       38.652          ms/op
Day11_benchmark.part2c_memoTransform                 avgt       34.620          ms/op
Day11_benchmark.part2d_memoBlinksUsesPut             avgt       32.144          ms/op
Day11_benchmark.part2e_inlineStoneTransform          avgt       29.639          ms/op
Day11_benchmark.part2f_partialStoneTransformNoLists  avgt       31.450          ms/op
Day11_benchmark.part2g_eExceptMemoBlinksNoPut        avgt       32.874          ms/op

singleShot(1)
Benchmark                                            Mode  Cnt   Score   Error  Units
Day11_benchmark.part2a_initial                         ss       95.744          ms/op
Day11_benchmark.part2b_keyBeforeZero                   ss       94.892          ms/op
Day11_benchmark.part2c_memoTransform                   ss       82.893          ms/op
Day11_benchmark.part2d_memoBlinksUsesPut               ss       84.654          ms/op
Day11_benchmark.part2e_inlineStoneTransform            ss       98.081          ms/op
Day11_benchmark.part2f_partialStoneTransformNoLists    ss       66.229          ms/op
Day11_benchmark.part2g_eExceptMemoBlinksNoPut          ss       85.021          ms/op
Benchmark                                            Mode  Cnt    Score   Error  Units
Day11_benchmark.part2a_initial                         ss       103.259          ms/op
Day11_benchmark.part2b_keyBeforeZero                   ss       121.805          ms/op
Day11_benchmark.part2c_memoTransform                   ss       101.113          ms/op
Day11_benchmark.part2d_memoBlinksUsesPut               ss        97.105          ms/op
Day11_benchmark.part2e_inlineStoneTransform            ss        97.467          ms/op
Day11_benchmark.part2f_partialStoneTransformNoLists    ss        67.966          ms/op
Day11_benchmark.part2g_eExceptMemoBlinksNoPut          ss        97.008          ms/op
Benchmark                                            Mode  Cnt    Score   Error  Units
Day11_benchmark.part2a_initial                         ss        96.187          ms/op
Day11_benchmark.part2b_keyBeforeZero                   ss       106.302          ms/op
Day11_benchmark.part2c_memoTransform                   ss        99.784          ms/op
Day11_benchmark.part2d_memoBlinksUsesPut               ss        96.116          ms/op
Day11_benchmark.part2e_inlineStoneTransform            ss        97.661          ms/op
Day11_benchmark.part2f_partialStoneTransformNoLists    ss        67.672          ms/op
Day11_benchmark.part2g_eExceptMemoBlinksNoPut          ss        96.848          ms/op




 */


}