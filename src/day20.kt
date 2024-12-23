import java.util.*
import kotlin.math.abs

fun main() {

    fun part1(input: String, timeToSave: Int = 100): Long {
        val lines = input.lines()
        //first, path from start to end
        //next, path from end to start
        //next, try all possible cheat locations (just brute for all x-y-dir locations) and
        //   see how much time you save by comparing:
        //     distance-from-start to cheat + cheat to distance_from_end
        //     compare to: best possible noncheat time

        // ==== step 1: parse inputs ====
        val distanceFromStart = InfiniteGrid2d(-1)
        val distanceFromGoal = InfiniteGrid2d(-1)
        var start = Int2(-1,-1)
        var goal = Int2(-1,-1)
        lines.forEachIndexed { y, line ->
            line.forEachIndexed { x, c ->
                when (c) {
                    '.' -> {
                        distanceFromStart[x, y] = 99999
                        distanceFromGoal[x, y] = 99999
                    }

                    '#' -> {
                        distanceFromStart[x, y] = -1
                        distanceFromGoal[x, y] = -1
                    }

                    'S' -> {
                        distanceFromStart[x, y] = 0
                        distanceFromGoal[x, y] = 99999
                        start = Int2(x, y)
                    }

                    'E' -> {
                        distanceFromStart[x, y] = 99999
                        distanceFromGoal[x, y] = 0
                        goal = Int2(x, y)
                    }

                    else -> throw IllegalArgumentException("unknown char '$c' found in input map at $x,$y")
                }
            }
        }
        check(start != Int2(-1,-1)){"start not found!"}
        check(goal != Int2(-1,-1)){"goal not found!"}

        // ==== step 2: distance from start ====
        val fromStartQueue = PriorityQueue<Int2> { a, b ->
            distanceFromStart[a] - distanceFromStart[b]
        }
        fromStartQueue.add(start)

        while(fromStartQueue.size>0) {
            val currentSpace = fromStartQueue.first()
            val currentSteps = distanceFromStart[currentSpace]
            fromStartQueue.remove(currentSpace)

            for (dir in Int2.compass4) {
                val nextSpace = currentSpace + dir
                if (distanceFromStart[nextSpace] == -1) continue // wall
                if (distanceFromStart[nextSpace] <= currentSteps + 1) continue //already this good or better
                distanceFromStart[nextSpace] = currentSteps + 1
                if(nextSpace==goal) continue
                if (nextSpace !in fromStartQueue) fromStartQueue.add(nextSpace)
            }
        }


        // ==== step 3: distance from goal ====
        val fromGoalQueue = PriorityQueue<Int2> { a, b ->
            distanceFromGoal[a] - distanceFromGoal[b]
        }
        fromGoalQueue.add(goal)

        while(fromGoalQueue.size>0) {
            val currentSpace = fromGoalQueue.first()
            val currentSteps = distanceFromGoal[currentSpace]
            fromGoalQueue.remove(currentSpace)

            for (dir in Int2.compass4) {
                val nextSpace = currentSpace + dir
                if (distanceFromGoal[nextSpace] == -1) continue // wall
                if (distanceFromGoal[nextSpace] <= currentSteps + 1) continue //already this good or better
                distanceFromGoal[nextSpace] = currentSteps + 1
                if(nextSpace==start) continue
                if (nextSpace !in fromGoalQueue) fromGoalQueue.add(nextSpace)
            }
        }
        check(distanceFromGoal[start]==distanceFromStart[goal])
        val noncheatDistance = distanceFromGoal[start]
        println("noncheat distance = $noncheatDistance")

        // ==== step 3: try every cheat location ====
        val numberOfCheatsBySavings = mutableMapOf<Int,Int>()
        distanceFromStart.forEach { (x,y), startDistance ->
            if (startDistance==-1) return@forEach //invalid cheat start
            val cheatStart = Int2(x,y)
            Int2.compass4.forEach eachDir@{ dir ->
                //straight
                var cheatEnd = cheatStart + (dir*2)
                var goalDistance = distanceFromGoal[cheatEnd]
                if (goalDistance!=-1){ //valid cheat end?
                    val pathDistance = startDistance+2+goalDistance
                    val distanceSavings = noncheatDistance-pathDistance
                    if (distanceSavings>0) {
                        numberOfCheatsBySavings[distanceSavings] = 1 + numberOfCheatsBySavings.getOrElse(distanceSavings){0}
                    }
                }
                //diagonal (45° right turn, compared to straight)
                cheatEnd = cheatStart + dir + dir.rotateRight()
                goalDistance = distanceFromGoal[cheatEnd]
                if (goalDistance!=-1){ //valid cheat end?
                    val pathDistance = startDistance+2+goalDistance
                    val distanceSavings = noncheatDistance-pathDistance
                    if (distanceSavings>=timeToSave) {
                        numberOfCheatsBySavings[distanceSavings] = 1 + numberOfCheatsBySavings.getOrElse(distanceSavings){0}
                    }
                }

            }
        }
        println("$numberOfCheatsBySavings")

        return numberOfCheatsBySavings.filter { (key, _) -> key >= timeToSave }.values.sum().toLong()
    }

    fun part2(input: String, timeToSave: Int = 100): Long {
        val lines = input.lines()
        //first, path from start to end
        //next, path from end to start
        //next, try all possible cheat locations (just brute for all x-y-dir locations) and
        //   see how much time you save by comparing:
        //     distance-from-start to cheat + cheat to distance_from_end
        //     compare to: best possible noncheat time

        // ==== step 1: parse inputs ====
        val distanceFromStart = InfiniteGrid2d(-1)
        val distanceFromGoal = InfiniteGrid2d(-1)
        var start = Int2(-1,-1)
        var goal = Int2(-1,-1)
        lines.forEachIndexed { y, line ->
            line.forEachIndexed { x, c ->
                when (c) {
                    '.' -> {
                        distanceFromStart[x, y] = 99999
                        distanceFromGoal[x, y] = 99999
                    }

                    '#' -> {
                        distanceFromStart[x, y] = -1
                        distanceFromGoal[x, y] = -1
                    }

                    'S' -> {
                        distanceFromStart[x, y] = 0
                        distanceFromGoal[x, y] = 99999
                        start = Int2(x, y)
                    }

                    'E' -> {
                        distanceFromStart[x, y] = 99999
                        distanceFromGoal[x, y] = 0
                        goal = Int2(x, y)
                    }

                    else -> throw IllegalArgumentException("unknown char '$c' found in input map at $x,$y")
                }
            }
        }
        check(start != Int2(-1,-1)){"start not found!"}
        check(goal != Int2(-1,-1)){"goal not found!"}

        // ==== step 2: distance from start ====
        val fromStartQueue = PriorityQueue<Int2> { a, b ->
            distanceFromStart[a] - distanceFromStart[b]
        }
        fromStartQueue.add(start)

        while(fromStartQueue.size>0) {
            val currentSpace = fromStartQueue.first()
            val currentSteps = distanceFromStart[currentSpace]
            fromStartQueue.remove(currentSpace)

            for (dir in Int2.compass4) {
                val nextSpace = currentSpace + dir
                if (distanceFromStart[nextSpace] == -1) continue // wall
                if (distanceFromStart[nextSpace] <= currentSteps + 1) continue //already this good or better
                distanceFromStart[nextSpace] = currentSteps + 1
                if(nextSpace==goal) continue
                if (nextSpace !in fromStartQueue) fromStartQueue.add(nextSpace)
            }
        }


        // ==== step 3: distance from goal ====
        val fromGoalQueue = PriorityQueue<Int2> { a, b ->
            distanceFromGoal[a] - distanceFromGoal[b]
        }
        fromGoalQueue.add(goal)

        while(fromGoalQueue.size>0) {
            val currentSpace = fromGoalQueue.first()
            val currentSteps = distanceFromGoal[currentSpace]
            fromGoalQueue.remove(currentSpace)

            for (dir in Int2.compass4) {
                val nextSpace = currentSpace + dir
                if (distanceFromGoal[nextSpace] == -1) continue // wall
                if (distanceFromGoal[nextSpace] <= currentSteps + 1) continue //already this good or better
                distanceFromGoal[nextSpace] = currentSteps + 1
                if(nextSpace==start) continue
                if (nextSpace !in fromGoalQueue) fromGoalQueue.add(nextSpace)
            }
        }
        check(distanceFromGoal[start]==distanceFromStart[goal])
        val noncheatDistance = distanceFromGoal[start]
        println("noncheat distance = $noncheatDistance")

        // ==== step 3: try every cheat location ====
        val numberOfCheatsBySavings = mutableMapOf<Int,Int>()
        distanceFromStart.forEach { (startX,startY), startDistance ->
            if (startDistance==-1) return@forEach //invalid cheat start
            val cheatStart = Int2(startX,startY)
            for(endY in -20..20){
                val yDist = abs(endY)
                for(endX in (-20+yDist)..(20-yDist)) {
                    val cheatDistance = abs(endY)+abs(endX)
                    if( cheatDistance>20) continue //cheat too long
                    val cheatEnd = cheatStart + Int2(endX, endY)
                    val goalDistance = distanceFromGoal[cheatEnd]
                    if (goalDistance==-1) continue //end point invalid
                    val pathDistance = startDistance+cheatDistance+goalDistance
                    val distanceSavings = noncheatDistance-pathDistance
                    if (distanceSavings>=timeToSave) {
                        numberOfCheatsBySavings[distanceSavings] = 1 + numberOfCheatsBySavings.getOrElse(distanceSavings){0}
                    }

                }
            }

        }
        println("$numberOfCheatsBySavings")

        return numberOfCheatsBySavings.filter { (key, _) -> key >= timeToSave }.values.sum().toLong()
    }


    val samplesAndTargets: List<Triple<String, Int, Pair<Long?, Long?>>> = listOf(
        Triple(
            """
###############
#...#...#.....#
#.#.#.#.#.###.#
#S#...#.#.#...#
#######.#.#.###
#######.#.#...#
#######.#.###.#
###..E#...#...#
###.#######.###
#...###...#...#
#.#####.#.###.#
#.#...#.#.#...#
#.#.#.#.#.#.###
#...#...#...###
###############
""",
            50,
            Pair(1, 285)
        ),
        Triple(
            """
#########################################
#...#.............#.....#.....#.....#...#
###.#.###.#########.###.###.#####.###.#.#
#...#...#.#.#.....#...#...#.#.........#.#
#..##.###.#.#####.#####.#.#.#.#####.#.#.#
#.......#.....#.#.....#.#...#...#...#.#.#
#.###########.#.#.####.####.#.###########
#.#.#...#...#.....#.................#...#
#.#.#.#.#.#.###.#.#.###.#########.#####.#
#.....#...#.....#...#.........#...#.#.#.#
#####.#####.#####.#.#.#.#.#######.#.#.#.#
#.....#.........#.#.#...#...#...#.#...#.#
#.#########.#######.#####.#.##..###.###.#
#...#.......#.....#.#...#.#...#.....#...#
#.###.###########.#.###.#.#.###.#######.#
#.#.#.............#.....#.#...#...#.....#
###.#.#####.#####.#.###.#.#####.#####.###
#...#.#.........#.#...#...#...#.#.....#.#
###.###.#.#########.#####.###.#.#.#.#.#.#
#S#.#...#.#.....#.....#.........#.#.#..E#
#.#.#.#########.#.#########.#.###.#####.#
#.....#.........#...#.#...#.#.....#...#.#
###.#####..##.#.#####.#.###.#####.###.###
#.#.#...#.#.#.#.#...#...#...#.........#.#
#.#.###.###.#.#.#.#####.####.##.#.#####.#
#.#.#.#.#.#...#.........#.#...#.#.#...#.#
#.#.#.#.#.#####.###.#.#.#.###.#.###.###.#
#...#.......#...#...#.#.#.........#.#...#
#######.#####.#####.###.#.#.#####.#.###.#
#.............#.....#.#.#.#.....#.......#
###############.#####.#.#########.#.#.###
#.....#...#.#.........#.#...#...#.#.#.#.#
#.#.#.#.#.#.###.#########.###.###.#####.#
#.#.#.#.#...........#.#.............#...#
###.#.#.###.#######.#.#.#.###.###.#.#.###
#...#...#...#.#...#.#...#...#.#.#.#.#...#
###.#.#######.#.#.#.###.#####.#..##.#.###
#.#.#...#.....#.#.#.......#.#.#...#.....#
#.#.#####.###.#.#.#.#.#####.#####.###.#.#
#.....#.....#.......#.............#...#.#
#########################################
""",
            30,
            Pair(0, 299)
        ),
    )
    samplesAndTargets.withIndex().forEach { (index, sample) ->
        val (inputWithNewline, timeToSave, targets) = sample
        val (p1Target, p2Target) = targets
        val input = inputWithNewline.trim('\n')
        if (p1Target != null) {
            val check1 = part1(input, timeToSave)
            check(check1 == p1Target) {
                println("sample input[$index] part-1:  $check1 instead of $p1Target")
            }
        }
        if (p2Target != null) {
            val check2 = part2(input, timeToSave)
            check(check2 == p2Target) {
                println("sample input[$index] part-2:  $check2 instead of $p2Target")
            }
        }
        println("example $index: passed")
    }

//
//    // Or read a large test input from the `src/Day20_test.txt` file:
//    val testInput = readInput("Day20_test")
//    check(part1(testInput) == 1)

    // download input (if needed) into day20.txt.  return list of lines
    val input = loadAndReadInput(20, 2024)
    val part1Ans = part1(input)
    println("part 1 answer: $part1Ans")
//    check(part1Ans==) //check while refactoring
    val part2Ans = part2(input)
    println("part 2 answer: $part2Ans")
//    check(part2Ans==) //check while refactoring
}
