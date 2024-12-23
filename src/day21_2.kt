import kotlin.system.measureNanoTime


class ButtonPresser{
    //  x012x
    //y
    //0  789
    //1  456
    //2  123
    //3   0A

    fun stepsToTypeOnNumPad(password:String, numRobots: Int): Long {
        if(numRobots==0) return password.length.toLong()

        var previousButton='A'
        return password.sumOf { currentButton ->
            val stepsToPressList = stepsToTypeOnNumPad_movements[Pair(previousButton, currentButton)]!!
            previousButton = currentButton
            //stepsToPress ends with A.
            //I need to do these steps. Tell the previous bot/human to press these steps on my remote.
            val numPresses = stepsToPressList.minOfOrNull { stepsToPress ->
                stepsToTypeOnArrowPad(stepsToPress, numRobots - 1)
            }!!
            numPresses
        }

    }



    //  x012x
    //y
    //0   ^A
    //1  <v>
    @Suppress("PrivatePropertyName")
    private val stepsToTypeOnArrowPad_memo = mutableMapOf<Pair<String,Int>,Long>()
    private fun stepsToTypeOnArrowPad(password: String, numRobots: Int): Long {
        if (numRobots == 0) return password.length.toLong() //the human!
        return stepsToTypeOnArrowPad_memo.getOrPut(Pair(password, numRobots)) {

            var previousButton = 'A'
            return@getOrPut password.sumOf { currentButton ->
                val stepsToPressList = stepsToTypeOnArrowPad_movements[Pair(previousButton, currentButton)]!!
                previousButton = currentButton
                //stepsToPress ends with A.
                //I need to do these steps. Tell the previous bot/human to press these steps on my remote.
                val numPresses = stepsToPressList.minOfOrNull { stepsToPress ->
                    stepsToTypeOnArrowPad(stepsToPress, numRobots - 1)
                }!!
                numPresses
            }
        }
    }


    companion object{
        val stepsToTypeOnNumPad_movements = mutableMapOf<Pair<Char,Char>,List<String>>()
        init {
            //  x012x
            //y
            //0  789
            //1  456
            //2  123
            //3   0A
            val buttonList="987654321A0"
            for (cFrom in buttonList) {
                val xFrom = 2-buttonList.indexOf(cFrom)%3
                val yFrom = buttonList.indexOf(cFrom)/3
                for (cTo in buttonList) {
                    val key = Pair(cFrom,cTo)
                    val xTo = 2-buttonList.indexOf(cTo)%3
                    val yTo = buttonList.indexOf(cTo)/3

                    val left = if (xFrom - xTo < 0) "" else "<".repeat(xFrom - xTo)
                    val right = if (xTo-xFrom < 0) "" else ">".repeat(xTo-xFrom)
                    val up = if (yFrom-yTo < 0) "" else "^".repeat(yFrom-yTo)
                    val down = if (yTo-yFrom < 0) "" else "v".repeat(yTo-yFrom)

                    if (cFrom in "147" && cTo in "0A")
                        stepsToTypeOnNumPad_movements[key] = listOf(right+down+"A")
                    else if (cFrom in "0A" && cTo in "147")
                        stepsToTypeOnNumPad_movements[key] = listOf(up+left+"A")
                    else stepsToTypeOnNumPad_movements[key] = listOf(
                        left+ right + up + down  + "A",
                        up + down + left+ right  + "A")
                }
            }
        }

        val stepsToTypeOnArrowPad_movements = mutableMapOf<Pair<Char, Char>, List<String>>()
        init {
            //  x012x
            //y
            //0   ^A
            //1  <v>
            val buttonList = ">v<A^"
            for (cFrom in buttonList) {
                val xFrom = 2 - buttonList.indexOf(cFrom) % 3
                val yFrom = 1 - buttonList.indexOf(cFrom) / 3
                for (cTo in buttonList) {
                    val key = Pair(cFrom, cTo)
                    val xTo = 2 - buttonList.indexOf(cTo) % 3
                    val yTo = 1- buttonList.indexOf(cTo) / 3

                    val left = if (xFrom - xTo < 0) "" else "<".repeat(xFrom - xTo)
                    val right = if (xTo-xFrom < 0) "" else ">".repeat(xTo-xFrom)
                    val up = if (yFrom-yTo < 0) "" else "^".repeat(yFrom-yTo)
                    val down = if (yTo-yFrom < 0) "" else "v".repeat(yTo-yFrom)

                    if (cFrom in "<" && cTo in "^A")
                        stepsToTypeOnArrowPad_movements[key] = listOf(right + up + "A")
                    else if (cFrom in "^A" && cTo in "<")
                        stepsToTypeOnArrowPad_movements[key] = listOf(down + left + "A")
                    else stepsToTypeOnArrowPad_movements[key] = listOf(
                        left+ right + up + down  + "A",
                        up + down + left+ right  + "A")
                }
            }
        }
    }
}

fun main() {


    fun part1(input: String): Long {
        val passwords = input.lines()
        val robot = ButtonPresser()
        return passwords.sumOf { password ->
            val steps = robot.stepsToTypeOnNumPad(password, 2+1)
            val code = password.dropLast(1).toLong()
            return@sumOf steps * code
        }
    }

    fun part2(input: String): Long {
        val passwords = input.lines()
        val robot = ButtonPresser()
        return passwords.sumOf { password ->
            val steps = robot.stepsToTypeOnNumPad(password, 25+1)
            val code = password.dropLast(1).toLong()
            print("  $password: ")
            print("$steps".padStart(15,' '))
            print(" steps x ")
            print("$code".padStart(5,' '))
            print(" = ")
            println("${steps * code}".padStart(20,' '))
            return@sumOf steps * code
        }
        // 132929214388818 too high
        // 116821732384052
    }


    val samplesAndTargets: List<Triple<String, Long?, Long?>> = listOf(
        Triple(
            """
029A
980A
179A
456A
379A
""",
            126384, 154115708116294L
        ),
    )
    samplesAndTargets.withIndex().forEach { (index, sample) ->
        println("--example $index--")
        val (inputWithNewline, p1Target, p2Target) = sample
        val input = inputWithNewline.trim('\n')
        if (p1Target != null) {
            println(" part 1")
            var check1: Long
            val time = measureNanoTime{
                check1 = part1(input)
            }
            println(" [ms]: ${time/1_000_000.0}")
            check(check1 == p1Target) {
                println(" 1:  $check1 instead of $p1Target")
            }
        }
        if (p2Target != null) {
            println(" part 2")
            var check2: Long
            val time = measureNanoTime{
                check2 = part2(input)
            }
            println(" [ms]: ${time/1_000_000.0}")
            check(check2 == p2Target) {
                println(" 2:  $check2 instead of $p2Target")
            }
        }
        println("  passed")
    }

//
//    // Or read a large test input from the `src/Day21_test.txt` file:
//    val testInput = readInput("Day21_test")
//    check(part1(testInput) == 1)

    // download input (if needed) into day21.txt.  return list of lines
    val input = loadAndReadInput(21, 2024)
    println("--real input now--")
    println("part 1:")
    var part1Ans: Long
    val time1 = measureNanoTime{
        part1Ans = part1(input)
    }
    println("  [ms]: ${time1/1_000_000.0}")
    println("  answer: $part1Ans")
    check(part1Ans==94284L) //do check while refactoring

    println("part 2:")
    var part2Ans: Long
    val time2 = measureNanoTime{
        part2Ans = part2(input)
    }
    println("  [ms]: ${time2/1_000_000.0}")
    println("  answer: $part2Ans")
//    check(part2Ans==) //do check while refactoring
}
