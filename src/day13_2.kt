import kotlin.io.path.Path
import kotlin.io.path.readText

fun main() {


    fun part1(input: String): Long {
        val machineTexts = input.split("\n\n")
        return machineTexts.sumOf { machineText->
            val (lineA, lineB, lineTarget) = machineText.split("\n")
            val buttonA = Int2(
                lineA.substringAfter("X").substringBefore(",").toInt(),
                lineA.substringAfter("Y").toInt()
            )
            val buttonB = Int2(
                lineB.substringAfter("X").substringBefore(",").toInt(),
                lineB.substringAfter("Y").toInt()
            )
            val prize = Int2(
                lineTarget.substringAfter("X=").substringBefore(",").toInt(),
                lineTarget.substringAfter("Y=").toInt()
            )
//            fun findXY(): Int2? {
//                repeat(100) { timesA ->
//                    repeat(100) { timesB ->
//                        val pos = buttonA * timesA + buttonB * timesB
//                        if (pos == prize) return Int2(timesA, timesB)
//                    }
//                }
//                return null
//            }

            var (timesa, timesb) = prize.asLinearCombinationOf(buttonA, buttonB) ?: Int2(0,0)
//            println("   -> $timesa, $timesb")
            timesa *= 3
            (timesa+timesb).toLong()
        }
    }

    fun part2(input: String): Long {
        val machineTexts = input.split("\n\n")
        return machineTexts.withIndex().sumOf { (index,machineText) ->
            val (lineA, lineB, lineTarget) = machineText.split("\n")
            val buttonA = Long2(
                lineA.substringAfter("X").substringBefore(",").toLong(),
                lineA.substringAfter("Y").toLong()
            )
            val buttonB = Long2(
                lineB.substringAfter("X").substringBefore(",").toLong(),
                lineB.substringAfter("Y").toLong()
            )
            val prize = Long2(
                10000000000000L+lineTarget.substringAfter("X=").substringBefore(",").toLong(),
                10000000000000L+lineTarget.substringAfter("Y=").toLong()
            )

            val (timesa, timesb) = prize.asLinearCombinationOf(buttonA, buttonB) ?: Long2(0,0)
//            println("   -> $timesa, $timesb")
            val cost = (timesa*3+timesb)
            println("machine ${index+1}:")
            println("   button A: $buttonA button B: $buttonB prize: $prize")
            println("   #a:$timesa #b:$timesb  \$cost:$cost")
            cost
        }

        //9223372036854775807 <- max of long:
        //10000000000000 <- value added to each
        //
        // 23199824392440 too low
        //102255878088512 √
    }


    val samplesAndTargets: List<Triple<String, Long?, Long?>> = listOf(
        Triple(
            """
Button A: X+94, Y+34
Button B: X+22, Y+67
Prize: X=8400, Y=5400

Button A: X+26, Y+66
Button B: X+67, Y+21
Prize: X=12748, Y=12176

Button A: X+17, Y+86
Button B: X+84, Y+37
Prize: X=7870, Y=6450

Button A: X+69, Y+23
Button B: X+27, Y+71
Prize: X=18641, Y=10279
""",
            480, null
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
//    // Or read a large test input from the `src/Day13_test.txt` file:
//    val testInput = readInput("Day13_test")
//    check(part1(testInput) == 1)

    // download input (if needed) into day13.txt.  return list of lines

//    val input = loadAndReadInput(13, 2024)
    val input = Path("src/day13_input2.txt").readText().trim()
//    val part1Ans = part1(input)
//    println("part 1 answer: $part1Ans")
//    check(part1Ans==28059L)
    val part2Ans = part2(input)
    println("part 2 answer: $part2Ans")
    check(part2Ans==104958599303720){"part 2 incorrect"}//what my code says for input2
//    check(part2Ans==102255878088512L){"part 2 incorrect"}//my input

}
