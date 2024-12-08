
private val <A, B> Pair<A, B>.x: A
    get() {
        return first
    }
private val <A, B> Pair<A, B>.y: B
    get() {
        return second
    }

fun main() {

    fun rotateRight(dir: Pair<Int, Int>): Pair<Int, Int> {
        return Pair(-dir.y, dir.x)
    }

    @Suppress("unused")
    fun rotateLeft(dir: Pair<Int, Int>): Pair<Int, Int> {
        return Pair(dir.y, -dir.x)
    }

    fun part1(input: String): Int {
        val lines = input.lines()
        val grid = InfiniteGrid('.')
        var posX = -1
        var posY = -1
        var dir = Pair(0, -1)
        lines.withIndex().forEach { (y, line) ->
            line.withIndex().forEach { (x, c) ->
                if (c == '^') {
                    posX = x
                    posY = y
                }
                grid[x, y] = c
            }
        }
        while (posX >= 0 && posX <= grid.maxX && posY >= 0 && posY <= grid.maxY) {
            // mark the current space, check if we can step, then step-or-turn
            grid[posX, posY] = 'X'
            val nextX = posX + dir.x
            val nextY = posY + dir.y
            when {
                grid[nextX, nextY] == '#' -> {
                    dir = rotateRight(dir)
                }

                else -> {
                    posX = nextX
                    posY = nextY
                }
            }
        }
        var total = 0
        grid.forEach { _, c ->
            if (c == 'X') total += 1
        }
//        grid.print()
        return total
    }


    @Suppress("unused")
    fun charForDir(dir: Pair<Int, Int>): Char {
        return when (dir) {
            Pair(0, -1) -> '^'
            Pair(0, 1) -> 'v'
            Pair(-1, 0) -> '<'
            Pair(1, 0) -> '>'
            else -> throw IllegalArgumentException("Invalid direction. No char representation for: $dir")
        }
    }
    val emptySpaceChar='·'

    fun tryEscapeDidLoop(grid: InfiniteGrid<Char>, startX: Int, startY: Int, startDir: Pair<Int,Int>): Boolean {
        var lifetime = 25000
        var posX = startX
        var posY = startY
        var dir = startDir
        while (lifetime>0 && posX>=grid.minX && posX<=grid.maxX && posY>=grid.minY && posY<=grid.maxY){
            lifetime -= 1
            //check if we can step, then step-or-turn
            val nextX = posX + dir.x
            val nextY = posY + dir.y

            if (grid[nextX, nextY] == '#') {
                dir = rotateRight(dir)
            } else {
                posX = nextX
                posY = nextY
            }
        }
        @Suppress("RedundantIf", "RedundantSuppression")
        if (lifetime<=0) return true
        return false
    }
    fun part2(input: String): Int {
        //time for attempt 3!

        val lines = input.lines()
        val grid = InfiniteGrid(emptySpaceChar)
        var startX = -1
        var startY = -1
        val startDir = Pair(0, -1)
        lines.withIndex().forEach { (y, line) ->
            line.withIndex().forEach { (x, c) ->
                if (c == '^') {
                    startX = x
                    startY = y
                    grid.updateBounds(x to y)
                } else if (c == '#') {
                    grid[x, y] = '#'
                }
            }
        }
        grid.updateBounds(0 to 0)


        var total = 0
        grid.forEach{ (x,y),c ->
            if (c=='#') return@forEach
            if (x==startX && y==startY) return@forEach
            grid[x,y]='#'
            if (tryEscapeDidLoop(grid, startX, startY, startDir)) {
                total += 1
                grid[x,y]='o'
                println("Pair($x,$y),")
            }
            else {
                grid[x, y] = emptySpaceChar
            }

        }


//        newObstaclePossibilities.forEach { (x,y), space ->
//            if (x == startX && y == startY) return@forEach
//            if ('o' == space) total += 1
//        }

//        newObstaclePossibilities.printWithIndex { x, y, c ->
//            if (newObstaclePossibilities[x, y] == 'o') 'o'
//            else if (x == startX && y == startY) 'S'
//            else if (grid[x, y] == 'X') '+'
//            else if (grid[x, y] == '#') '#'
//            else c
//        }
        grid.printWithIndex { x, y, c ->
            if (c == 'o') 'o'
            else if (x == startX && y == startY) 'S'
            else if (c == 'X') '+'
            else c
        }
        return total
        //1267 too low
        //1289 too low
        //1501 too high
        //1502 ??
        //1499 ??
        //algorithm 3 - brutest force
        //1444 ??
        //1443 correct!
    }

    val samplesAndTargets: List<Triple<String, Int?, Int?>> = listOf(
        Triple(
            """
....#.....
.........#
..........
..#.......
.......#..
..........
.#..^.....
........#.
#.........
......#...""",
            41, 6
        ),
        Triple(
            """
.........
.#.......
#.......#
.#.....#.
........#
#........
...^...#.
.........""",
            null, 2
        ),
        Triple(
            """
....#....
...#....#
.......#.
.........
.........
......#..
...^.....
.........""",
            null, 1
        ),
        Triple(
            """
........#
......#..
....#...#
...^.#...
.......#.""",
            null, 0
        ),
        Triple(
            """
..#.......
..........
......#...
.#..^.....
....##....
#.........
...#......""",
            null, 1
        ),
        Triple(
            """
.....#..
......#.
.#......
......#.
........
........
.^...#..
........
""",
            null, 2
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
//    // Or read a large test input from the `src/Day06_test.txt` file:
//    val testInput = readInput("Day06_test")
//    check(part1(testInput) == 1)

    // download input (if needed) into day06.txt.  return list of lines
    val input = loadAndReadInput(6, 2024)
    println("part 1 answer: ${part1(input)}")
    println("part 2 answer: ${part2(input)}")
}
