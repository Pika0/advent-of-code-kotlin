fun main() {
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
        val numStones = nextStones.sumOf{stone-> stonesFromBlinks(stone, numberOfBlinks-1) }
        stonesFromBlinks_memo[key] = numStones
        return numStones
    }


    fun part1(input: String): Long {
        val lines = input.lines()
        var stones = lines[0].split(" ").map { it.toLong() }


        val numBlink = 25
        repeat(numBlink) {
            val newStones: List<Long> = buildList {
                stones.forEach { stone ->
                    stoneTransform(stone).forEach { add(it) }
                }
            }
            stones = newStones
        }
        return stones.size.toLong()
    }

    fun part2(input: String): Long {
        val lines = input.lines()
        val stones = lines[0].split(" ").map { it.toLong() }

        val numBlink = 75
        return stones.sumOf{ stone -> stonesFromBlinks(stone, numBlink)}
    }


    val samplesAndTargets: List<Triple<String, Long?, Long?>> = listOf(
        Triple(
            """
125 17
""",
            55312, null
        ),
    )

    samplesAndTargets.withIndex().forEach { (index, sample) ->
        val (inputWithNewline, p1Target, p2Target) = sample
        val input = inputWithNewline.trim('\n')
        if (p1Target != null) {
            val check1 = part1(input)
            check(check1 == p1Target) {
                println("sample input[$index] part-1:  $check1 instead of $p1Target")
            }
        }
        if (p2Target != null) {
            val check2 = part2(input)
            check(check2 == p2Target) {
                println("sample input[$index] part-2:  $check2 instead of $p2Target")
            }
        }
    }

//
//    // Or read a large test input from the `src/Day11_test.txt` file:
//    val testInput = readInput("Day11_test")
//    check(part1(testInput) == 1)

    // download input (if needed) into day11.txt.  return list of lines
    val input = loadAndReadInput(11, 2024)
    println("part 1 answer: ${part1(input)}")
    println("part 2 answer: ${part2(input)}")
}
