fun main() {
    val directions = listOf(
        Int2( 1, 0),
        Int2( 0,-1),
        Int2(-1, 0),
        Int2( 0, 1),
    )
    fun findEnds(grid: InfiniteGrid2d<Int>, current:Int2): Set<Int2>{
        if (grid[current]==9) return setOf(current)
        val results: MutableSet<Int2> = mutableSetOf()
        directions.forEach{ direction ->
            val nextPos = current + direction
            if (grid[nextPos] != grid[current]+1) return@forEach
            results.addAll( findEnds(grid, nextPos))
        }
        return results
    }
    fun part1(input: String): Int {
        val areaMap = InfiniteGrid2d(-1)
        val starts = emptySet<Int2>().toMutableSet()
        input.lines().forEachIndexed { y, line ->
            line.forEachIndexed{ x, c ->
                areaMap[x,y]=c.digitToInt()
                if (c=='0') starts.add(Int2(x,y))
            }
        }
        return starts.sumOf { start ->
            val results = findEnds(areaMap, start)
            results.size
        }
    }

    fun findPaths(grid: InfiniteGrid2d<Int>, current:Int2): Long{
        if (grid[current]==9) return 1
        var results: Long = 0
        directions.forEach{ direction ->
            val nextPos = current + direction
            if (grid[nextPos] != grid[current]+1) return@forEach
            results += findPaths(grid, nextPos)
        }
        return results
    }
    fun part2(input: String): Long {
        val areaMap = InfiniteGrid2d(-1)
        val starts = emptySet<Int2>().toMutableSet()
        input.lines().forEachIndexed { y, line ->
            line.forEachIndexed{ x, c ->
                areaMap[x,y]=c.digitToInt()
                if (c=='0') starts.add(Int2(x,y))
            }
        }
        return starts.sumOf { start ->
            val results: Long = findPaths(areaMap, start)
            results
        }
    }


    val samplesAndTargets: List<Triple<String, Int?, Long?>> = listOf(
        Triple(
            """
89010123
78121874
87430965
96549874
45678903
32019012
01329801
10456732
""",
            36, 81
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
//    // Or read a large test input from the `src/Day10_test.txt` file:
//    val testInput = readInput("Day10_test")
//    check(part1(testInput) == 1)

    // download input (if needed) into day10.txt.  return list of lines
    val input = loadAndReadInput(10, 2024)
    println("part 1 answer: ${part1(input)}")
    println("part 2 answer: ${part2(input)}")
}
