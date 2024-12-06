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
        val grid=InfiniteGrid('.')
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
        //this time each square won't be 'visited' or not.
        // each square will contain which direction(s) the guard was going when he stepped on it
        val lines = input.lines()
        val grid=InfiniteGrid<Set<Char>>(emptySet())
        var posX = -1
        var posY = -1
        var dir = Pair(0, -1)
        lines.withIndex().forEach{(y,line) ->
            line.withIndex().forEach { (x,c) ->
                if (c=='^') {
                    posX = x
                    posY = y
                    grid[x, y] = setOf('^')
                }
                else if (c=='#') {
                    grid[x, y] = setOf('#')
                }
            }
        }
        //every time you turn right (and at the start) you need to back-path
        //every time you walk past an obstacle on your left,
        //       do you need to back-track an alternate come-fom path from the right? yes
        //       recursively? yes
        //       For how long? until you back into a barrel or off the side
        //              (impossible I think: or perhaps get to a space that's already marked with your path.)
        fun backtrack(posX: Int, posY: Int, dir: Pair<Int, Int>){
            var posX = posX
            var posY = posY
            while (posX>=0 && posX<=grid.maxX && posY>=0 && posY<=grid.maxY) {
                //check prev square
                val prevX = posX - dir.x
                val prevY = posY - dir.y
                if ('#' in grid[prevX, prevY]) return
                if (!(prevX>=0 && prevX<=grid.maxX && prevY>=0 && prevY<=grid.maxY)) return
                //step back & mark
                posX = prevX
                posY = prevY
                grid[posX, posY] =
                    grid[posX, posY] + charForDir(dir) //+ sign makes a new list including the new object??
                //check to the left for an obstacle, and fork if so
                val leftDir = rotateLeft(dir)
                val leftX = posX + leftDir.x
                val leftY = posY + leftDir.y
                if ('#' in grid[leftX, leftY]) {
                    grid[posX, posY] = grid[posX, posY] + charForDir(leftDir)
                    backtrack(posX, posY, leftDir)
                }
            }


        }
        backtrack(posX, posY, dir)

        while (posX>=0 && posX<=grid.maxX && posY>=0 && posY<=grid.maxY){
            // mark the current space
            grid[posX,posY] = grid[posX,posY] + charForDir(dir)

            //check: obstacle to the left? -> backtrack from the right
            val leftDir = rotateLeft(dir)
            val leftX = posX + leftDir.x
            val leftY = posY + leftDir.y
            if ('#' in grid[leftX, leftY]) {
                grid[posX, posY] = grid[posX, posY] + charForDir(leftDir)
                backtrack(posX, posY, leftDir)
            }


            //now check the space ahead
            val nextX = posX + dir.first
            val nextY = posY + dir.second
            //obstacle? then turn
            if ('#' in grid[nextX,nextY] ){
                dir = rotateRight(dir)
                backtrack(posX, posY, dir)
                continue
            }
            //check if putting an obstacle in the next space would make a loop
            //AKA, if we've been on this space before but going to the right

            if (charForDir(rotateRight(dir)) in grid[posX,posY] ){
                grid[nextX,nextY] = grid[nextX,nextY] + 'o'
            }

            //now take the step forwards
            posX = nextX
            posY = nextY
        }
        var total=0
        grid.forEach{_,space ->
            if ('o' in space) total += 1
        }
        grid.print{
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
    }

    var sampleInput="""....#.....
.........#
..........
..#.......
.......#..
..........
.#..^.....
........#.
#.........
......#..."""
    val check1 = part1(sampleInput)
    check ( check1 == 41 ) {
        println("check1=$check1")
    }
    sampleInput=""".........
.#.......
........#
#........
..^....#.
........."""
    val check2 = part2(sampleInput)
    check(check2 == 6 ){
        println("check2=$check2")
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
