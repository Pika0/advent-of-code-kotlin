import kotlin.io.path.Path
import kotlin.io.path.readText

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


    fun charForDir(dir: Pair<Int, Int>): Char {
        return when (dir) {
            Pair(0, -1) -> '^'
            Pair(0, 1) -> 'v'
            Pair(-1, 0) -> '<'
            Pair(1, 0) -> '>'
            else -> throw IllegalArgumentException("Invalid direction. No char representation for: $dir")
        }
    }



    fun part2(@Suppress("UNUSED_PARAMETER") input: String): Int {
        //time for attempt 3!

        @Suppress("NAME_SHADOWING")
        val input : String = run{
            val inputFileName = "src/day06_input_david.txt"
            Path(inputFileName).readText().trim()
        }


        val lines = input.lines()
        val grid = InfiniteGrid('·')
        var startX = -1
        var startY = -1
        val startDir = Pair(0, -1)
        lines.withIndex().forEach { (y, line) ->
            line.withIndex().forEach { (x, c) ->
                if (c == '^') {
                    startX = x
                    startY = y
                } else if (c == '#') {
                    grid[x, y] = '#'
                }
            }
        }
        grid.updateBounds(0 to 0)

        //load correct answer from algorithm#3's solution
        @Suppress("NAME_SHADOWING")
        val correctObstacleLocations : MutableSet<Pair<Int,Int>> = run{
//            val inputFileName = "src/day06_correct_locations.txt"
            val inputFileName = "src/day06_davids_found.txt"
            val input = Path(inputFileName).readText().trim()
            val lines = input.lines()
            lines.map{ line ->
                val parts = line.split(" ")
                val nums = parts.map{it.toInt()}
                nums[0] to nums[1]
            }.toMutableSet()
        }






        var posX = startX
        var posY = startY
        var dir = startDir

        val newObstaclePossibilities = InfiniteGrid('·')
        var obstacleNum = 0
        var obstaclesPrinted = 0
        fun testFakeObstacle(startX: Int, startY: Int, startDir: Pair<Int, Int>, fakeX: Int, fakeY: Int) {
            obstacleNum += 1
            var testPosX = startX
            var testPosY = startY
            val testGrid = grid.copy()
            testGrid[fakeX, fakeY] = '#' //place fake obstacle

            fun printFirstFew(looped: Boolean) {
                obstaclesPrinted += 1
//                if (obstaclesPrinted !in 20..<30) return
                println("--- obstacle num $obstacleNum ---:")
                println("  obstacle at $fakeX, $fakeY")
                println("  end at $testPosX, $testPosY")
                println("  looped: $looped")
                testGrid.printWithIndex { x, y, printC ->
                    if (x == fakeX && y == fakeY) 'o'
                    else if (printC == '.') grid[x, y]
                    else printC
                }
            }

            var testDir = rotateRight(startDir)

            while (testPosX >= -1 && testPosX <= testGrid.maxX && testPosY >= -1 && testPosY <= testGrid.maxY) {
                //is our direction on this space already? then it's a loop!
                val c = charForDir(testDir)
                if (c == testGrid[testPosX, testPosY]) {
                    if ( Pair(fakeX, fakeY) !in correctObstacleLocations ){
                        printFirstFew(true)
                        print("   ^^^ missed obstacle found at $fakeX,$fakeY")
                        if (obstaclesPrinted>10) {
                            throw IllegalStateException("10 missed obstacles found")
                        }
                    }
//                    correctObstacleLocations.remove (Pair(fakeX, fakeY))

                    newObstaclePossibilities[fakeX, fakeY] = 'o'
                    return
                }

                //check if we can step, then step-or-turn
                val nextX = testPosX + testDir.x
                val nextY = testPosY + testDir.y
                when {
                    testGrid[nextX, nextY] == '#' -> {
                        testDir = rotateRight(testDir)
                    }

                    else -> {
                        // mark the space in case we reach it later
                        testGrid[testPosX, testPosY] = c
                        testPosX = nextX
                        testPosY = nextY
                    }
                }
            }
            //left/escaped the field
//            printFirstFew(false)
            return
        }


        while (posX >= -1 && posX <= grid.maxX && posY >= -1 && posY <= grid.maxY) {
            // mark the current space, check if we can step, then step-or-turn
            grid[posX, posY] = 'X'

            val nextX = posX + dir.x
            val nextY = posY + dir.y
            when {
                grid[nextX, nextY] == '#' -> {
                    dir = rotateRight(dir)
                }

                else -> {
                    if ((nextX != startX || nextY != startY) //cannot place an obstacle on the start
                        && (nextX >= grid.minX && nextX <= grid.maxX && nextY >= grid.minY && nextY <= grid.maxY) //within grid?
                        && grid[nextX, nextY]!='X') { //cannot place an obstacle where we've already stepped
                        testFakeObstacle(posX, posY, dir, nextX, nextY)
                    }
                    posX = nextX
                    posY = nextY
                }
            }
        }


        var total = 0
        newObstaclePossibilities.forEach { (x,y), space ->
            if (x == startX && y == startY) return@forEach
            if ('o' == space) total += 1
        }
//        newObstaclePossibilities.printWithIndex { x, y, c ->
//            if (newObstaclePossibilities[x, y] == 'o') 'o'
//            else if (x == startX && y == startY) 'S'
//            else if (grid[x, y] == 'X') '+'
//            else if (grid[x, y] == '#') '#'
//            else c
//        }
        grid.printWithIndex { x, y, c ->
            if (newObstaclePossibilities[x, y] == 'o') 'o'
            else if (x == startX && y == startY) 'S'
            else if (c == 'X') '+'
            else c
        }
//        correctObstacleLocations
        println("${correctObstacleLocations.size} remaining: ")
        correctObstacleLocations.take(10).forEach{
            print(it)
        }

        return total
        //1267 too low
        //1289 too low
        //1501 too high
        //1502 ??
        //1499 ??
    }

//    val samplesAndTargets: List<Triple<String, Int?, Int?>> = listOf(
//        Triple(
//            """
//....#.....
//.........#
//..........
//..#.......
//.......#..
//..........
//.#..^.....
//........#.
//#.........
//......#...""",
//            41, 6
//        ),
//        Triple(
//            """
//.........
//.#.......
//#.......#
//.#.....#.
//........#
//#........
//...^...#.
//.........""",
//            null, 2
//        ),
//        Triple(
//            """
//....#....
//...#....#
//.......#.
//.........
//.........
//......#..
//...^.....
//.........""",
//            null, 1
//        ),
//    )
//    samplesAndTargets.withIndex().forEach { (index, sample) ->
//        val (inputWithNewline, p1Target, p2Target) = sample
//        val input = inputWithNewline.trim('\n')
//        if (p1Target != null) {
//            val check1 = part1(input)
//            check(check1 == p1Target) {
//                println("sample input[$index] part-1:  $check1 instead of $p1Target")
//            }
//        }
//        if (p2Target != null) {
//            val check2 = part2(input)
//            check(check2 == p2Target) {
//                println("sample input[$index] part-2:  $check2 instead of $p2Target")
//            }
//        }
//    }

//
//    // Or read a large test input from the `src/Day06_test.txt` file:
//    val testInput = readInput("Day06_test")
//    check(part1(testInput) == 1)

    // download input (if needed) into day06.txt.  return list of lines
    val input = loadAndReadInput(6, 2024)
    println("part 1 answer: ${part1(input)}")
    println("part 2 answer: ${part2(input)}")
}
