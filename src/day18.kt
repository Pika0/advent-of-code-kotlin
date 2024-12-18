import java.util.*
import kotlin.math.abs

fun main() {
    fun part1(input: String): Long {
        val steps = InfiniteGrid2d(-2L)
        val start = Int2(0, 0)
        var goal = Int2(6, 6)
        val limit=if(input.length>200) 1024 else 12

        input.lines().take(limit).forEach { line ->
            val (x, y) = line.split(",").map { it.toInt() }
            steps[x, y] = -1
            if (x > 6 || y > 6) goal = Int2(70, 70)
        }
        for (y in 0.. goal.y) {
            for (x in 0..goal.x) {
                if (steps[x,y]==-2L) steps[x,y]=999999
            }
        }
        steps.print {
            when (it) {
                -2L -> 'X'
                -1L -> '#'
                999999L -> '.'
                0L -> '0'
                else -> it.toString().last()
            }
        }
        val queue = PriorityQueue<Int2> { a, b ->
            val lengthA = steps[a] + abs(a.x - goal.x)
            val lengthB = steps[b] + abs(b.x - goal.x)
            (lengthA - lengthB).toInt()
        }
        queue.add(start)
        steps[start]=0
//        while(queue.size>0) {
        while(true) {
            if (queue.size == 0) {

                steps.print {
                    when (it) {
                        -2L -> 'X'
                        -1L -> '#'
                        999999L -> '.'
                        0L -> '0'
                        else -> it.toString().last()
                    }
                }
                throw IllegalStateException("out of spaces to check!")
            }
            val current = queue.first()
            val currentSteps = steps[current]
            queue.remove(current)

            for (dir in Int2.compass4) {
                val neighbor = current + dir
                if (steps[neighbor] == -2L) continue //external wall
                if (steps[neighbor] == -1L) continue //internal wall
                if (steps[neighbor] <= currentSteps + 1) continue //already this good or better
                steps[neighbor] = currentSteps + 1
                if(neighbor==goal) return steps[goal]
                if (neighbor !in queue) queue.add(neighbor)
            }

        }
    }

    fun part2(input: String): String {
        val walls = InfiniteGrid2d(-2L)
        val start = Int2(0, 0)
        var goal = Int2(6, 6)
        val limit=if(input.length>200) 1024 else 12

        var blocks = input.lines().map { line ->
            val (x, y) = line.split(",").map { it.toInt() }
            Int2(x,y)
        }.toMutableList()
        blocks.take(6).forEach{ (x,y) ->
            if (x > 6 || y > 6) goal = Int2(70, 70)
        }
        for (y in 0.. goal.y) {
            for (x in 0..goal.x) {
                walls[x,y]=999999
            }
        }

        fun canEscape(wallsGrid:InfiniteGrid2d<Long>): Boolean {
            val steps = wallsGrid.copy()
            val queue = PriorityQueue<Int2> { a, b ->
                val lengthA = steps[a] + abs(a.x - goal.x)
                val lengthB = steps[b] + abs(b.x - goal.x)
                (lengthA - lengthB).toInt()
            }
            queue.add(start)
            steps[start] = 0
            while (queue.size>0) {
                val current = queue.first()
                val currentSteps = steps[current]
                queue.remove(current)

                for (dir in Int2.compass4) {
                    val neighbor = current + dir
                    if (steps[neighbor] == -2L) continue //external wall
                    if (steps[neighbor] == -1L) continue //internal wall
                    if (steps[neighbor] <= currentSteps + 1) continue //already this good or better
                    steps[neighbor] = currentSteps + 1
                    if (neighbor == goal) {
//                        steps.print {
//                            when (it) {
//                                -2L -> 'X'
//                                -1L -> '#'
//                                999999L -> '.'
//                                0L -> '0'
//                                else -> it.toString().last()
//                            }
//                        }
                        return true
                    }
                    if (neighbor !in queue) queue.add(neighbor)
                }

            }
            return false
        }

        blocks.take(limit).forEach{ (x,y) ->
            walls[x, y] = -1
        }
        blocks = blocks.drop(limit).toMutableList()
        var block = blocks.first()
        while(canEscape(walls)){
            block = blocks.first()
            blocks.remove(block)
            walls[block]=-1
        }
        return "${block.x},${block.y}"
    }


    val samplesAndTargets: List<Triple<String, Long?, String?>> = listOf(
        Triple(
            """
5,4
4,2
4,5
3,0
2,1
6,3
2,4
1,5
0,6
3,3
2,6
5,1
1,2
5,5
2,5
6,5
1,4
0,4
6,4
1,1
6,1
1,0
0,5
1,6
2,0
""",
            22, "6,1"
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
//    // Or read a large test input from the `src/Day18_test.txt` file:
//    val testInput = readInput("Day18_test")
//    check(part1(testInput) == 1)

    // download input (if needed) into day18.txt.  return list of lines
    val input = loadAndReadInput(18, 2024)
    val part1Ans = part1(input)
    println("part 1 answer: $part1Ans")
//    check(part1Ans==) //check while refactoring
    val part2Ans = part2(input)
    println("part 2 answer: $part2Ans")
//    check(part2Ans==) //check while refactoring
}
