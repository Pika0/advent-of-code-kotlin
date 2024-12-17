import java.util.*
import kotlin.math.abs

fun main() {
    fun rotateRight(dir: Int2): Int2 {
        return Int2(-dir.y, dir.x)
    }

    fun rotateLeft(dir: Int2): Int2 {
        return Int2(dir.y, -dir.x)
    }

    class MazeSquareBests(val costEast :Int, val costNorth :Int, val costWest :Int, val costSouth :Int){
        fun copyWithChange(dir:Int2,value:Int):MazeSquareBests{
            return when(dir){
                Int2( 1, 0) -> MazeSquareBests(    value, costNorth, costWest, costSouth)
                Int2( 0,-1) -> MazeSquareBests( costEast,     value, costWest, costSouth)
                Int2(-1, 0) -> MazeSquareBests( costEast, costNorth,    value, costSouth)
                Int2( 0, 1) -> MazeSquareBests( costEast, costNorth, costWest,     value)
                else -> throw IllegalArgumentException("cannot update MazeSquareBests with dir $dir")
            }
        }
        fun costForDir(dir:Int2):Int{
            return when(dir){
                Int2( 1, 0) -> costEast
                Int2( 0,-1) -> costNorth
                Int2(-1, 0) -> costWest
                Int2( 0, 1) -> costSouth
                else -> throw IllegalArgumentException("cannot get MazeSquareBests.costForDir: $dir")
            }
        }
    }

    fun part1(input: String): Long {
        val lines = input.lines()
        val maze = InfiniteGrid2d('.')
        var start = Int2(-1,-1)
        var goal = Int2(-1,-1)
        lines.forEachIndexed { y, line ->
            line.forEachIndexed { x, c ->
                when (c) {
                    'S' -> {
                        start = Int2(x, y)
                        maze[x, y] = '.'
                    }

                    'E' -> {
                        goal = Int2(x, y)
                        maze[x, y] = '.'
                    }

                    '.', '#' -> maze[x, y] = c
                    else -> throw IllegalArgumentException("unknown maze char: $c")
                }
            }
        }
        check(start != Int2(-1,-1))
        check(goal != Int2(-1,-1))
        val startDir= Int2(1,0)
        maze.printWithIndex { x, y, c ->
            if (x==start.x && y==start.y) 'S'
            else if (x==goal.x && y==goal.y) 'E'
            else c
        }


        val mazeCosts = InfiniteGrid2d(MazeSquareBests(999999,999999,999999,999999))

        class MazeState(val position:Int2, val dir:Int2, val cost:Int){
            val goalDist = abs(position.x-goal.x)+abs(position.y-goal.y)
            val compareScore = cost+10*goalDist
            override fun toString():String{
                return "MazeState($position,$dir,$cost,$compareScore)"
            }
        }

        val statesToCheck: TreeSet<MazeState> = TreeSet<MazeState> (
            compareBy<MazeState> { it.compareScore }
                .thenBy{ it.position.x }
                .thenBy{ it.position.y }
                .thenBy{ it.dir.x }
                .thenBy{ it.dir.y }
                .thenBy{ it.cost } )
        statesToCheck.add( MazeState(start, startDir, 0) )
        var currentState : MazeState
        while(true){
            if(statesToCheck.size==0) throw IllegalStateException("statelist empty!")
            currentState = statesToCheck.first()
            statesToCheck.remove(currentState)

            val bestPreviousCostForLoc = mazeCosts[currentState.position].costForDir(currentState.dir)
            if(currentState.cost>=bestPreviousCostForLoc) continue
            mazeCosts[currentState.position] = mazeCosts[currentState.position].copyWithChange(currentState.dir, currentState.cost)

//            maze.printWithIndex { x, y, c ->
//                if (x==currentState.position.x && y==currentState.position.y) '+'
//                else if (x==start.x && y==start.y) 'S'
//                else if (x==goal.x && y==goal.y) 'E'
//                else c
//            }
            if(currentState.position==goal) break

            fun addIfBetter(state:MazeState){
                val currentBestCostForLoc = mazeCosts[state.position].costForDir(state.dir)
                //add if better cost for the square
                if(currentBestCostForLoc<=state.cost) return
                //mark cost+1 to prevent worse costs from being added, but still allow this state to continue once selected
                mazeCosts[state.position] = mazeCosts[state.position].copyWithChange(state.dir, state.cost+1)
                statesToCheck.add(state)
            }
            //new options:
            addIfBetter( //turn left
                MazeState(currentState.position, rotateLeft(currentState.dir), currentState.cost+1000)
            )
            addIfBetter( //turn right
                MazeState(currentState.position, rotateRight(currentState.dir), currentState.cost+1000)
            )
            val fw1Pos = currentState.position+currentState.dir
            if(maze[fw1Pos]!='#') addIfBetter(
                MazeState(fw1Pos, currentState.dir, currentState.cost+1)
            )
        }
        println("found exit? $goal")
        println(" fin at: $currentState")

        return currentState.cost.toLong()
    }

    fun part2(input: String): Long {
        val lines = input.lines()
        val maze = InfiniteGrid2d('.')
        var start = Int2(-1,-1)
        var goal = Int2(-1,-1)
        lines.forEachIndexed { y, line ->
            line.forEachIndexed { x, c ->
                when (c) {
                    'S' -> {
                        start = Int2(x, y)
                        maze[x, y] = '.'
                    }

                    'E' -> {
                        goal = Int2(x, y)
                        maze[x, y] = '.'
                    }

                    '.', '#' -> maze[x, y] = c
                    else -> throw IllegalArgumentException("unknown maze char: $c")
                }
            }
        }
        check(start != Int2(-1,-1))
        check(goal != Int2(-1,-1))
        val startDir= Int2(1,0)
        maze.printWithIndex { x, y, c ->
            if (x==start.x && y==start.y) 'S'
            else if (x==goal.x && y==goal.y) 'E'
            else c
        }


        val mazeCosts = InfiniteGrid2d(MazeSquareBests(999999,999999,999999,999999))

        class MazeState(val position:Int2, val dir:Int2, val cost:Int){
            val goalDist = abs(position.x-goal.x)+abs(position.y-goal.y)
            val compareScore = cost+10*goalDist
            override fun toString():String{
                return "MazeState($position,$dir,$cost,$compareScore)"
            }
        }

        val statesToCheck: TreeSet<MazeState> = TreeSet<MazeState> (
            compareBy<MazeState> { it.compareScore }
                .thenBy{ it.position.x }
                .thenBy{ it.position.y }
                .thenBy{ it.dir.x }
                .thenBy{ it.dir.y }
                .thenBy{ it.cost } )
        statesToCheck.add( MazeState(start, startDir, 0) )
        val goalStates = mutableListOf<MazeState>()

        while(statesToCheck.size>0){//check all path options...
            val currentState = statesToCheck.first()
            statesToCheck.remove(currentState)

            val bestPreviousCostForLoc = mazeCosts[currentState.position].costForDir(currentState.dir)
            if(currentState.cost>=bestPreviousCostForLoc) continue
            mazeCosts[currentState.position] = mazeCosts[currentState.position].copyWithChange(currentState.dir, currentState.cost)

//            maze.printWithIndex { x, y, c ->
//                if (x==currentState.position.x && y==currentState.position.y) '+'
//                else if (x==start.x && y==start.y) 'S'
//                else if (x==goal.x && y==goal.y) 'E'
//                else c
//            }
            if(currentState.position.x==goal.x && currentState.position.y==goal.y) {
                goalStates.add( currentState)
                continue
            }

            fun addIfBetter(state:MazeState){
                val currentBestCostForLoc = mazeCosts[state.position].costForDir(state.dir)
                //add if better cost for the square
                if(currentBestCostForLoc<=state.cost) return
                //mark cost+1 to prevent worse costs from being added, but still allow this state to continue once selected
                mazeCosts[state.position] = mazeCosts[state.position].copyWithChange(state.dir, state.cost+1)
                statesToCheck.add(state)
            }
            //new options:
            addIfBetter( //turn left
                MazeState(currentState.position, rotateLeft(currentState.dir), currentState.cost+1000)
            )
            addIfBetter( //turn right
                MazeState(currentState.position, rotateRight(currentState.dir), currentState.cost+1000)
            )
            val fw1Pos = currentState.position+currentState.dir
            if(maze[fw1Pos]!='#') addIfBetter(
                MazeState(fw1Pos, currentState.dir, currentState.cost+1)
            )
        }
        check(goalStates.size>0){"goalState not found"}
        goalStates.sortBy { it.cost }

        //traverse in reverse - descend the cost path back to the start
        fun traverseReverse(state: MazeState){
            val costAtLoc = mazeCosts[state.position].costForDir(state.dir)
            if(costAtLoc>state.cost) return //not this way!
            else if (costAtLoc<state.cost) {
                //can happen from rotating back, so just skip
//                throw IllegalStateException("found better best path?")
                return
            }

            maze[state.position]='O'
            traverseReverse( //turn left
                MazeState(state.position, rotateLeft(state.dir), state.cost-1000)
            )
            traverseReverse( //turn right
                MazeState(state.position, rotateRight(state.dir), state.cost-1000)
            )
            //backup
            val fw1Pos = state.position-state.dir
            if(maze[fw1Pos]!='#') traverseReverse(
                MazeState(fw1Pos, state.dir, state.cost-1)
            )
        }
        traverseReverse(goalStates.first())
        maze.print()
        var total = 0L

        @Suppress("UNUSED_DESTRUCTURED_PARAMETER_ENTRY")
        maze.forEach { (x,y), c ->
            if(c=='O') total += 1
            else if (c=='S' || c=='E'){
                println("$c still exists at end (but it's OK)")
                total += 1
            }
        }

        return total
    }


    val samplesAndTargets: List<Triple<String, Long?, Long?>> = listOf(
        Triple(
            """
###############
#.......#....E#
#.#.###.#.###.#
#.....#.#...#.#
#.###.#####.#.#
#.#.#.......#.#
#.#.#####.###.#
#...........#.#
###.#.#####.#.#
#...#.....#.#.#
#.#.#.###.#.#.#
#.....#...#.#.#
#.###.#.#.#.#.#
#S..#.....#...#
###############
""",
            7036, 45
        ),
        Triple(
            """
#################
#...#...#...#..E#
#.#.#.#.#.#.#.#.#
#.#.#.#...#...#.#
#.#.#.#.###.#.#.#
#...#.#.#.....#.#
#.#.#.#.#.#####.#
#.#...#.#.#.....#
#.#.#####.#.###.#
#.#.#.......#...#
#.#.###.#####.###
#.#.#...#.....#.#
#.#.#.#####.###.#
#.#.#.........#.#
#.#.#.#########.#
#S#.............#
#################

""",
            11048, 64
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


//    // Or read a large test input from the `src/Day16_test.txt` file:
//    val testInput = readInput("Day16_test")
//    check(part1(testInput) == 1)

    // download input (if needed) into day16.txt.  return list of lines
    val input = loadAndReadInput(16, 2024)
    val part1Ans = part1(input)
    println("part 1 answer: $part1Ans")
//    check(part1Ans==) //check while refactoring
    val part2Ans = part2(input)
    println("part 2 answer: $part2Ans")
//    check(part2Ans==) //check while refactoring
}
