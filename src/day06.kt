@file:Suppress("NAME_SHADOWING")

private val <A, B> Pair<A, B>.x: A
    get() {
        return first
    }
private val <A, B> Pair<A, B>.y: B
    get() {
        return second
    }

fun main() {

    fun rotateRight(dir: Pair<Int,Int>): Pair<Int,Int>{
        return Pair(-dir.y, dir.x)
    }
    fun rotateLeft(dir: Pair<Int,Int>): Pair<Int,Int>{
        return Pair(dir.y, -dir.x)
    }
    fun part1(input: String): Int {
        val lines = input.lines()
        val grid=InfiniteGrid{'.'}
        var posX = -1
        var posY = -1
        var dir = Pair(0, -1)
        lines.withIndex().forEach{(y,line) ->
            line.withIndex().forEach { (x,c) ->
                if (c=='^') {
                    posX = x
                    posY = y
                }
                grid[x,y]=c
            }
        }
        while (posX>=0 && posX<=grid.maxX && posY>=0 && posY<=grid.maxY){
            // mark the current space, check if we can step, then step-or-turn
            grid[posX,posY]='X'
            val nextX = posX + dir.x
            val nextY = posY + dir.y
            when{
                grid[nextX,nextY]=='#' -> {
                    dir = rotateRight(dir)
                }
                else -> {
                    posX = nextX
                    posY = nextY
                }
            }
        }
        var total=0
        grid.forEach{_,c ->
            if (c=='X') total += 1
        }
//        grid.print()
        return total
    }



    fun charForDir(dir: Pair<Int,Int>): Char{
        return when (dir) {
            Pair( 0,-1) -> '^'
            Pair( 0, 1) -> 'v'
            Pair(-1, 0) -> '<'
            Pair( 1, 0) -> '>'
            else -> throw IllegalArgumentException("Invalid direction. No char representation for: $dir")
        }
    }
    fun part2(input: String): Int {
        //this time each square won't be 'visited' or 'not'.
        // each square will contain
        // -1,['#']: obstacle in input
        // -1,['o']: putting new obstacle here makes loop
        // 0,[dirChars]: stepped on in main path
        // n>0,[dirChars]: stepped on after placing nth new/temp obstacle
        val lines = input.lines()
        val grid = InfiniteGrid { HashMap<Int, MutableSet<Char>>() }
        var startX = -1
        var startY = -1
        var posX = -1
        var posY = -1
        var dir = Pair(0, -1)
        lines.withIndex().forEach{(y,line) ->
            line.withIndex().forEach { (x,c) ->
                if (c=='^') {
                    posX = x
                    startX = x
                    posY = y
                    startY = y
                    grid[x, y].getOrPut(0){mutableSetOf()}.add('^')
                }
                else if (c=='#') {
                    grid[x, y].getOrPut(-1){mutableSetOf()}.add('#')
                }
            }
        }
        grid.updateBounds(0 to 0)
        grid.updateBounds(lines.last().length-1 to lines.size-1)

        var obstacleNum=0
        while (posX>=0 && posX<=grid.maxX && posY>=0 && posY<=grid.maxY){
            // mark the current space
            grid[posX,posY].getOrPut(0){mutableSetOf()}.add(charForDir(dir))

            //now check the space ahead
            val nextX = posX + dir.first
            val nextY = posY + dir.second
            //obstacle? then turn
            if ('#' in grid[nextX,nextY].getOrPut(-1){mutableSetOf()} ){
                dir = rotateRight(dir)
                continue
            }
            //check if putting an obstacle in the next space would make a loop
            val obstacleStartX=posX
            val obstacleStartY=posY
            val obstacleStartDir=dir
            grid[nextX,nextY].getValue(-1).add('#') //place temp obstacle
            obstacleNum += 1
            var makesLoop=false
            while (posX>=0 && posX<=grid.maxX && posY>=0 && posY<=grid.maxY){
                val c = charForDir(dir)
                //have we looped?
                if (c in grid[posX,posY].getOrPut(obstacleNum){mutableSetOf()}){
                    makesLoop=true
                    break
                }
                //no, so mark
                grid[posX,posY].getValue(obstacleNum).add(c)
                //keep going
                val nextX = posX + dir.first
                val nextY = posY + dir.second
                if ('#' in grid[nextX,nextY].getOrPut(-1){mutableSetOf()} ){ //turn if blocked
                    dir = rotateRight(dir)
                    continue
                }
                else { //else step
                    posX = nextX
                    posY = nextY
                }
            }
            grid[nextX,nextY].getValue(-1).remove('#') //remove temp obstacle
            if (makesLoop)  grid[nextX,nextY].getValue(-1).add('o') //place temp obstacle
            //reset our position
            posX = obstacleStartX
            posY = obstacleStartY
            dir = obstacleStartDir

            //now take the step forwards
            posX = nextX
            posY = nextY
        }
        var total=0
        grid.forEach{_,space ->
            if ('o' in space.getOrPut(-1){mutableSetOf()}) total += 1
        }
        if ('o' in grid[startX, startY].getOrPut(-1){mutableSetOf()}) total -= 1
        grid.print{
            val it = it.getOrPut(-1){mutableSetOf()}
            if ('o' in it) 'o'
            else if ('#' in it) '#'
//            else if (it.size==1) it.first()
            else if (it.size==1 && it.first()=='<') '╴'
            else if (it.size==1 && it.first()=='v') '╷'
            else if (it.size==1 && it.first()=='>') '╶'
            else if (it.size==1 && it.first()=='^') '╵'
            else if (it.size == 2 && it.containsAll(setOf('^', 'v'))) '│'
            else if (it.size == 2 && it.containsAll(setOf('<', '>'))) '─'
            else if (it.size == 2 && it.containsAll(setOf('^', '<'))) '┘'
            else if (it.size == 2 && it.containsAll(setOf('<', 'v'))) '┐'
            else if (it.size == 2 && it.containsAll(setOf('v', '>'))) '┌'
            else if (it.size == 2 && it.containsAll(setOf('>', '^'))) '└'
            else if (it.size == 3 && it.containsAll(setOf('^', '<', '>'))) '┴'
            else if (it.size == 3 && it.containsAll(setOf('^', '<', 'v'))) '┤'
            else if (it.size == 3 && it.containsAll(setOf('<', 'v', '>'))) '┬'
            else if (it.size == 3 && it.containsAll(setOf('v', '>', '^'))) '├'
            else if (it.size == 4 && it.containsAll(setOf('v', '>', '^', '<'))) '+'
            else if (it.isEmpty()) '·'
            else '?'
        }
        return total
        //1267 too low
        //1289 too low
        //1501 too high
    }
    val samplesAndResults: List<Triple<String, Int?, Int?>> = listOf(
        Triple("""
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
            41,6),
        Triple("""
.........
.#.......
........#
#........
..^....#.
.........""",
            null,1)
    )
    samplesAndResults.withIndex().forEach{ (index, sample) ->
        val (inputWithNewline, p1Target, p2Target) = sample
        val input = inputWithNewline.trim('\n')
        if (p1Target!=null) {
            val check1 = part1(input)
            check(check1 == p1Target) {
                println("sample input[${index}] part-1:  $check1 instead of ${p1Target}")
            }
        }
        if (p2Target!=null) {
            val check2 = part2(input)
            check(check2 == p2Target) {
                println("sample input[${index}] part-2:  $check2 instead of ${p2Target}")
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
