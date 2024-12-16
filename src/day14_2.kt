import kotlin.io.path.Path
import kotlin.io.path.readText
import kotlin.math.abs
import kotlin.math.pow

fun main() {
    class Bot(var loc:Int2, val vel:Int2) {
        fun posAfterSeconds(seconds: Int, width: Int, height: Int): Int2 {
            var x = (loc.x + vel.x * seconds) % width
            var y = (loc.y + vel.y * seconds) % height
            if (x < 0) x += width
            if (y < 0) y += height
            check(x in 0..<width)
            check(y in 0..<height)
            return Int2(x, y)
        }

        fun advance(width: Int, height: Int) {
            var x = (loc.x + vel.x) % width
            var y = (loc.y + vel.y) % height
            if (x < 0) x += width
            if (y < 0) y += height
            check(x in 0..<width)
            check(y in 0..<height)
            this.loc = Int2(x, y)
        }
    }
    fun part1(input: String, width: Int =101, height: Int =103): Long {
        val lines = input.lines()
        val bots: List<Bot> = lines.map { line ->
            val firstPairText = line.substringAfter("p=").substringBefore(" v")
            val secondPairText = line.substringAfter("v=")
            val pos = firstPairText.split(",").map { it.toInt() }
            val vel = secondPairText.split(",").map { it.toInt() }
            val bot = Bot(Int2(pos[0], pos[1]), Int2(vel[0], vel[1]))
            bot
        }
        //3x3 -> 1,1
        //5x5 -> 2,2
        //11,7 -> 5,3
        val quadrantDivisions = Int2((width - 1) / 2, (height - 1) / 2) //center point
        val quadrantSums = mutableListOf(0, 0, 0, 0)
        bots.forEach { bot ->
            val pos = bot.posAfterSeconds(100, width, height)
            var quadrant = 0
            if (pos.x == quadrantDivisions.x) return@forEach
            if (pos.x > quadrantDivisions.x) quadrant += 1
            if (pos.y == quadrantDivisions.y) return@forEach
            if (pos.y > quadrantDivisions.y) quadrant += 2
            quadrantSums[quadrant] += 1
        }
        return quadrantSums.fold(1) { a, b -> a * b }
    }

    fun part2(input: String, width: Int =101, height: Int =103): Long {
        val lines = input.lines()
        val bots: List<Bot> = lines.map { line ->
            val firstPairText = line.substringAfter("p=").substringBefore(" v")
            val secondPairText = line.substringAfter("v=")
            val pos = firstPairText.split(",").map { it.toInt() }
            val vel = secondPairText.split(",").map { it.toInt() }
            val bot = Bot(Int2(pos[0], pos[1]), Int2(vel[0], vel[1]))
            bot
        }
        fun drawBots(){
            val grid=InfiniteGrid(0)
            bots.forEach{bot->
                grid[bot.loc.x,bot.loc.y] += 1
            }
            grid.print { i ->
                if (i==0) '.'
                else if (i>9) 'X'
                else i.digitToChar()
            }
        }

        println("number of bots: ${bots.size}")
        //bots: picScore on image
        //2872: 5196
        //338: 191
        //500: 396 (130 next lowest)
        //check using 4th, nontraining image:
        //338: 383 (predicted 449) oh well. This one did have some skinny next, which lowered the score. And it did still work.
        //predictedPicScore:
        // empty space is filled with some bots: the area & 0.01441 bots/empty-cell
        // for the together bots, some have 4 neighbors, but some have less.
        //   the portion having more neighbors is larger in larger images. the .pow(1.18) takes care of this.
        //   along with a little scaling (0.9) afterward.
        val predictedPicScore = ((bots.size.toDouble() - (height * width - bots.size).toDouble() * 0.01441).pow(1.18) *0.9).toInt()
        var seconds = 0
        var bestScore = predictedPicScore/2
        while (true) {
            seconds += 1
            bots.forEach { it.advance(width, height) }
            val pictureScore: Int = bots.mapIndexed { index, bot ->
                bots.drop(index+1).sumOf { bot2 ->
                    if (bot.loc.x == bot2.loc.x && bot.loc.y == bot2.loc.y) 0
                    else if (abs(bot.loc.x - bot2.loc.x) < 2 && abs(bot.loc.y - bot2.loc.y) < 2) 1
                    else 0 as Int //cast is needed to help type of sumOf
                }
            }.sum()
            if (pictureScore>bestScore){
                bestScore=pictureScore
                drawBots()
                println("second $seconds: picture score: $pictureScore")
            }
            //              7709
            if (seconds > 10000) break
            if (bestScore>predictedPicScore*3/4) break
        }
        return seconds.toLong()
        //19
        //928 +909
        //1029 +101
        //2241:5196
    }



//
//    // Or read a large test input from the `src/Day14_test.txt` file:
//    val testInput = readInput("Day14_test")
//    check(part1(testInput) == 1)

    var input = ""
//    input = loadAndReadInput(14, 2024)
//    println("part 2 answer: ${part2(input)}")
    // alternate pic provided at https://www.reddit.com/r/adventofcode/comments/1he00zu/year_2024_day_14_part_2_is_that_a_tree_in_this/
//    input = Path("src/day14_pic1.bots").readText().trim()
//    println("part 2 answer: ${part2(input)}")
    // alternate pic provided at https://www.reddit.com/r/adventofcode/comments/1he00zu/year_2024_day_14_part_2_is_that_a_tree_in_this/m202bot/
//    input = Path("src/day14_pic2.bots").readText().trim()
//    println("part 2 answer: ${part2(input)}")
    // alternate pic provided at https://www.reddit.com/r/adventofcode/comments/1he00zu/year_2024_day_14_part_2_is_that_a_tree_in_this/m2054di/
    input = Path("src/day14_pic3.bots").readText().trim()
    println("part 2 answer: ${part2(input)}")
}
