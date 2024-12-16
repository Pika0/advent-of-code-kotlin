import kotlin.math.abs

fun main() {
    class Bot(var loc:Int2, val vel:Int2) {
        fun posAfterSeconds(seconds: Int, width: Int, height: Int): Int2 {
            var x = (loc.x + vel.x * seconds) % width
            var y = (loc.y + vel.y * seconds) % height
            if (x < 0) x += width
            if (y < 0) y += height
            check(0 <= x && x < width)
            check(0 <= y && y < height)
            return Int2(x, y)
        }

        fun advance(width: Int, height: Int) {
            var x = (loc.x + vel.x) % width
            var y = (loc.y + vel.y) % height
            if (x < 0) x += width
            if (y < 0) y += height
            check(0 <= x && x < width)
            check(0 <= y && y < height)
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
        var seconds = 0
        var bestScore = 300
        while (true) {
            seconds += 1
            bots.forEach { it.advance(width, height) }
            @Suppress("USELESS_CAST")
            val pictureScore: Int = bots.mapIndexed { index, bot ->
                bots.drop(index+1).sumOf { bot2 ->
                    if (bot.loc.x == bot2.loc.x || bot.loc.y == bot2.loc.y) 0
                    else if (abs(bot.loc.x - bot2.loc.x) < 2 && abs(bot.loc.y - bot2.loc.y) < 2) 1
                    else 0 as Int //CAST IS NEEDED despite 'code analysis', to help with type of sumOf
                }
            }.sum()
            if (pictureScore>bestScore){
                bestScore=pictureScore
                drawBots()
                println("second $seconds: picture score: $pictureScore")
            }
            //              7709
            if (seconds > 10000) break
            if (bestScore>700) break
        }
        return seconds.toLong()
    }


    val samplesAndTargets: List<Triple<Triple<String,Int?,Int?>, Long?, Long?>> = listOf(
        Triple(
            Triple("""
p=0,4 v=3,-3
p=6,3 v=-1,-3
p=10,3 v=-1,2
p=2,0 v=2,-1
p=0,0 v=1,3
p=3,0 v=-2,-2
p=7,6 v=-1,-3
p=3,0 v=-1,-2
p=9,3 v=2,3
p=7,3 v=-1,2
p=2,4 v=2,-3
p=9,5 v=-3,-3
""",11,7),
            12, null
        ),
    )
    samplesAndTargets.withIndex().forEach { (index, sample) ->
        val (s1, p1Target, p2Target) = sample
        val (inputWithNewline, width, height) = s1
        val input = inputWithNewline.trim('\n')
        if(p1Target==null && p2Target==null){
            throw IllegalArgumentException("example $index: <no solution given>")
        }
        if (p1Target != null) {
            val check1 = if (width != null && height != null) {
                part1(input, width, height)
            } else {
                part1(input)
            }
            check(check1 == p1Target) {
                println("sample input[$index] part-1:  $check1 instead of $p1Target")
            }
        }
        if (p2Target != null) {
            val check2 = if (width != null && height != null) {
                part2(input, width, height)
            } else {
                part2(input)
            }
            check(check2 == p2Target) {
                println("sample input[$index] part-2:  $check2 instead of $p2Target")
            }
        }
        println("example $index: passed")
    }

//
//    // Or read a large test input from the `src/Day14_test.txt` file:
//    val testInput = readInput("Day14_test")
//    check(part1(testInput) == 1)

    // download input (if needed) into day14.txt.  return list of lines
    val input = loadAndReadInput(14, 2024)
    println("part 1 answer: ${part1(input)}")
    println("part 2 answer: ${part2(input)}")
}
