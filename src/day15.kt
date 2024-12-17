fun main() {
    fun dirFromChar(c:Char):Int2{
        return when(c){
            '^'->Int2( 0,-1)
            'v'->Int2( 0, 1)
            '<'->Int2(-1, 0)
            '>'->Int2( 1, 0)
            else -> throw IllegalArgumentException("no direction for unknown char $c")
        }
    }
    fun charFromDir(dir:Int2):Char{
        return when(dir){
            Int2( 0,-1)->'^'
            Int2( 0, 1)->'v'
            Int2(-1, 0)->'<'
            Int2( 1, 0)->'>'
            else -> throw IllegalArgumentException("no char for unknown direction $dir")
        }
    }
    fun part1(input: String): Long {
        var botLoc = Int2(-1, -1)
        val (map, roboMoves) = run {
            val (mapStr, roboMovesStr) = input.split("\n\n")
            val map = InfiniteGrid2d('.')
            mapStr.lines().forEachIndexed { y, line ->
                line.forEachIndexed { x, c ->
                    map[x, y] = c
                    if (c == '@') botLoc = Int2(x, y)
                }
            }
            val roboMoves = roboMovesStr.toList().filter { it != '\n' }
            return@run Pair(map, roboMoves)
        }
        check(botLoc != Int2(-1, -1)) { "bot not found in map" }
        @Suppress("FunctionName")
        fun tryPush_IsSuccess(objectLoc: Int2, dir: Int2): Boolean {
            if (map[objectLoc] == '#') return false //cannot push walls
            if (map[objectLoc] == '.') return true //empty space: previous push attempt OK
            val targetLocation = objectLoc + dir
            if (tryPush_IsSuccess(targetLocation, dir)) {
                map[targetLocation] = map[objectLoc]
                map[objectLoc] = '.'
                return true
            } else {
                return false
            }
        }
//        map.print()
//        println("start")
        roboMoves.forEach { moveChar ->
            val moveDir = dirFromChar(moveChar)
            if (tryPush_IsSuccess(botLoc, moveDir)) {
                botLoc += moveDir
            }
//            map.print()
//            println("dir: $moveChar")
        }
//        map.print()
        var total = 0L
        map.forEach { (x, y), c ->
            if (c == 'O') {
                total += x + y * 100
            }
        }
        return total
    }

    fun part2(input: String): Long {
        var botLoc = Int2(-1, -1)
        val (map, roboMoves) = run {
            val (mapStr, roboMovesStr) = input.split("\n\n")
            val map = InfiniteGrid2d('.')
            mapStr.lines().forEachIndexed { y, line ->
                line.forEachIndexed { x, c ->
                    when (c) {
                        '#' -> {
                            map[x * 2, y] = '#'; map[x * 2 + 1, y] = '#'
                        }

                        'O' -> {
                            map[x * 2, y] = '['; map[x * 2 + 1, y] = ']'
                        }

                        '.' -> {
                            map[x * 2, y] = '.'; map[x * 2 + 1, y] = '.'
                        }

                        '@' -> {
                            map[x * 2, y] = '@'; map[x * 2 + 1, y] = '.'; botLoc = Int2(x * 2, y)
                        }
                        else -> throw IllegalArgumentException("unknown char in map: $c")
                    }
                }
            }
            val roboMoves = roboMovesStr.toList().filter { it != '\n' }
            return@run Pair(map, roboMoves)
        }
        check(botLoc != Int2(-1, -1)) { "bot not found in map" }
        fun canPush(objectLoc: Int2, dir: Int2): Boolean {
            val objChar = map[objectLoc]

            val dirChar = charFromDir(dir)
            return when {
                objChar == '#' -> false // cannot push walls
                objChar == '.' -> true  // empty space: previous push attempt OK
                objChar == '@' -> canPush(objectLoc + dir, dir) //the single robot

                objChar == '[' && (dirChar == '^' || dirChar == 'v') -> {
                    canPush(objectLoc + dir, dir) && canPush(objectLoc + Int2(1, 0) + dir, dir)
                }

                objChar == '[' && dirChar == '>' -> canPush(objectLoc + dir * 2, dir)
                objChar == '[' && dirChar == '<' -> throw IllegalStateException("canPush left box left?")

                objChar == ']' && (dirChar == '^' || dirChar == 'v') -> {
                    canPush(objectLoc + dir, dir) && canPush(objectLoc + Int2(-1, 0) + dir, dir)
                }

                objChar == ']' && dirChar == '<' -> canPush(objectLoc + dir * 2, dir)
                objChar == ']' && dirChar == '>' -> throw IllegalStateException("canPush right box right?")

                else -> throw IllegalStateException("unknown push at $objectLoc inDir $dir")
            }
        }

        fun doPush(objectLoc: Int2, dir: Int2) {
            val objChar = map[objectLoc]
            val dirChar = charFromDir(dir)
            when {
                objChar == '#' -> throw IllegalStateException("pushing a wall") // cannot push walls
                objChar == '.' -> {}  // nothing happens
                objChar == '@' -> { //the single robot
                    doPush(objectLoc + dir, dir)
                    map[objectLoc] = '.'
                    map[objectLoc + dir] = '@'
                }

                objChar == '[' && (dirChar == '^' || dirChar == 'v') -> {
                    doPush(objectLoc + dir, dir)
                    doPush(objectLoc + Int2(1, 0) + dir, dir)
                    map[objectLoc] = '.'
                    map[objectLoc + Int2(1, 0)] = '.'
                    map[objectLoc + dir] = '['
                    map[objectLoc + dir + Int2(1, 0)] = ']'
                }
                objChar == '[' && dirChar == '>' -> {
                    doPush(objectLoc + Int2(1, 0) + dir, dir)
                    map[objectLoc] = '.'
                    map[objectLoc + Int2(1, 0)] = '.'
                    map[objectLoc + dir] = '['
                    map[objectLoc + dir + Int2(1, 0)] = ']'
                }

                objChar == '[' && dirChar == '<' -> throw IllegalStateException("pushing left box left?")

                objChar == ']' && (dirChar == '^' || dirChar == 'v') -> {
                    doPush(objectLoc + dir, dir)
                    doPush(objectLoc + Int2(-1, 0) + dir, dir)
                    map[objectLoc] = '.'
                    map[objectLoc + Int2(-1, 0)] = '.'
                    map[objectLoc + dir] = ']'
                    map[objectLoc + dir + Int2(-1, 0)] = '['
                }
                objChar == ']' && dirChar == '<' -> {
                    doPush(objectLoc + Int2(-1, 0) + dir, dir)
                    map[objectLoc] = '.'
                    map[objectLoc + Int2(-1, 0)] = '.'
                    map[objectLoc + dir] = ']'
                    map[objectLoc + dir + Int2(-1, 0)] = '['
                }

                objChar == ']' && dirChar == '>' -> throw IllegalStateException("pushing right box right?")

                else -> throw IllegalStateException("unknown push at $objectLoc inDir $dir")
            }
        }
//        println("start:")
//        map.print()
        @Suppress("UNUSED_ANONYMOUS_PARAMETER")
        roboMoves.forEachIndexed { moveNum, moveChar ->
            val moveDir = dirFromChar(moveChar)
//            println("$moveNum next dir: $moveChar")
            if (canPush(botLoc, moveDir)) {
                doPush(botLoc, moveDir)
                botLoc += moveDir
            }
//            map.print()
        }
        map.print()
        var total = 0L
        map.forEach { (x, y), c ->
            if (c == '[') {
                total += x + y * 100
            }
        }
        return total
        //1359393 too high
    }


    val samplesAndTargets: List<Triple<String, Long?, Long?>> = listOf(
        Triple(
            """
########
#..O.O.#
##@.O..#
#...O..#
#.#.O..#
#...O..#
#......#
########

<^^>>>vv<v>>v<<
""",
            2028, null
        ),
        Triple(
            """
##########
#..O..O.O#
#......O.#
#.OO..O.O#
#..O@..O.#
#O#..O...#
#O..O..O.#
#.OO.O.OO#
#....O...#
##########

<vv>^<v^>v>^vv^v>v<>v^v<v<^vv<<<^><<><>>v<vvv<>^v^>^<<<><<v<<<v^vv^v>^
vvv<<^>^v^^><<>>><>^<<><^vv^^<>vvv<>><^^v>^>vv<>v<<<<v<^v>^<^^>>>^<v<v
><>vv>v^v^<>><>>>><^^>vv>v<^^^>>v^v^<^^>v^^>v^<^v>v<>>v^v^<v>v^^<^^vv<
<<v<^>>^^^^>>>v^<>vvv^><v<<<>^^^vv^<vvv>^>v<^^^^v<>^>vvvv><>>v^<<^^^^^
^><^><>>><>^^<<^^v>>><^<v>^<vv>>v>>>^v><>^v><<<<v>>v<v<v>vvv>^<><<>^><
^>><>^v<><^vvv<^^<><v<<<<<><^v<<<><<<^^<v<^^^><^>>^<v^><<<^>>^v<v^v<v^
>^>>^v>vv>^<<^v<>><<><<v<<v><>v<^vv<<<>^^v^>^^>>><<^v>>v^v><^^>>^<>vv^
<><^^>^^^<><vvvvv^v<v<<>^v<v>v<<^><<><<><<<^^<<<^<<>><<><^^^>^^<>^>v<>
^^>vv<^v^v<vv>^<><v<^v>^^^>>>^^vvv^>vvv<>>>^<^>>>>>^<<^v>^vvv<>^<><<v>
v^^>>><<^^<>>^v^<v^vv<>v^<<>^<^v^v><^<<<><<^<v><v<>vv>>v><v^<vv<>v^<<^

""",
            10092, 9021
        ),
        Triple(
            """
########
#......#
#..O...#
#..OOO.#
#..@O..#
#.OOO..#
#......#
#......#
########

^^>>vvv<<<

""",
            3228, 3254
        ),
        Triple(
            """
########
#......#
#..O...#
#..OOO.#
#..@O..#
#.OOOO.#
#...OO.#
#......#
########

<^>v>^v>^>>vv

""",
            null, 5090
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
        println("example $index: passed")
    }

//
//    // Or read a large test input from the `src/Day15_test.txt` file:
//    val testInput = readInput("Day15_test")
//    check(part1(testInput) == 1)

    // download input (if needed) into day15.txt.  return list of lines
    val input = loadAndReadInput(15, 2024)
    val part1Ans = part1(input)
    println("part 1 answer: $part1Ans")
//    check(part1Ans==) //check while refactoring
    val part2Ans = part2(input)
    println("part 2 answer: $part2Ans")
//    check(part2Ans==) //check while refactoring
}
