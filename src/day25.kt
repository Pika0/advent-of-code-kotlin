import kotlin.system.measureNanoTime

fun main() {
    fun part1(input: String): Long {
        val locksAndKeys = input.split("\n\n")
        val locks = mutableListOf<List<Int>>()
        val keys = mutableListOf<List<Int>>()
        locksAndKeys.forEach { lockOrKey ->
            var lines = lockOrKey.lines()
            val isLock = lines[0][0] == '#'
            lines = lines.drop(1).dropLast(1)
            val values = mutableListOf<Int>(0, 0, 0, 0, 0)
            lines.forEach { line ->
                line.forEachIndexed { index, c ->
                    if (c == '#') values[index] += 1
                }
            }
            if(isLock) locks.add(values)
            else keys.add(values)
        }
        println("${keys.size} keys, and ${locks.size} locks")
        val fittingHeight = locksAndKeys[0].lines().size-2
        println("fitting height: $fittingHeight")

        return keys.sumOf { key ->
            locks.count { lock ->
                val fits = (0..<5).all{
                    key[it]+lock[it]<=fittingHeight
                }
                if(fits) println("$key fits into $lock")
                fits
            }.toLong()
        }.toLong()
    }

    fun part2(input: String): Long {
        val lines = input.lines()
        return lines.size.toLong()
    }


    val samplesAndTargets: List<Triple<String, Long?, Long?>> = listOf(
        Triple(
            """
#####
.####
.####
.####
.#.#.
.#...
.....

#####
##.##
.#.##
...##
...#.
...#.
.....

.....
#....
#....
#...#
#.#.#
#.###
#####

.....
.....
#.#..
###..
###.#
###.#
#####

.....
.....
.....
#....
#.#..
#.#.#
#####

""",
            3, null
        ),
    )
    samplesAndTargets.withIndex().forEach { (index, sample) ->
        println("--example $index--")
        val (inputWithNewline, p1Target, p2Target) = sample
        val input = inputWithNewline.trim('\n')
        if (p1Target != null) {
            println(" part 1")
            var check1: Long
            val time = measureNanoTime{
                check1 = part1(input)
            }
            println(" [ms]: ${time/1_000_000.0}")
            check(check1 == p1Target) {
                println(" 1:  $check1 instead of $p1Target")
            }
        }
        if (p2Target != null) {
            println(" part 2")
            var check2: Long
            val time = measureNanoTime{
                check2 = part2(input)
            }
            println(" [ms]: ${time/1_000_000.0}")
            check(check2 == p2Target) {
                println(" 2:  $check2 instead of $p2Target")
            }
        }
        println("  passed")
    }

//
//    // Or read a large test input from the `src/Day25_test.txt` file:
//    val testInput = readInput("Day25_test")
//    check(part1(testInput) == 1)

    // download input (if needed) into day25.txt.  return list of lines
    val input = loadAndReadInput(25, 2024)
    println("--real input now--")
    println("part 1:")
    var part1Ans: Long
    val time1 = measureNanoTime{
        part1Ans = part1(input)
    }
    println("  [ms]: ${time1/1_000_000.0}")
    println("  answer: $part1Ans")
//    check(part1Ans==) //do check while refactoring

    println("part 2:")
    var part2Ans: Long
    val time2 = measureNanoTime{
        part2Ans = part2(input)
    }
    println("  [ms]: ${time2/1_000_000.0}")
    println("  answer: $part2Ans")
//    check(part2Ans==) //do check while refactoring
}
