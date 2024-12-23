import kotlin.system.measureNanoTime

fun main() {
    //each robot has to go back to 'A' between very next-level button
    //it takes 1-2 buttons to get the next robot to it's correct next button.
    // if it were 3, routing could matter, but since it's 2, either order is fine.
    // EDIT: turns out that's not correct somehow...

    @Suppress("KotlinConstantConditions")
    fun stepsToTypeOnNumpad(password:String): String {
        //  x012x
        //y
        //0  789
        //1  456
        //2  123
        //3   0A

        //REDO: order of arrows, b/c moving closer to A is easier for earlier robots
        // first, check is the LL-corner must be avoided.
        // else, < goes first. then v. then ^ or >.

        var x = 2
        var y = 3
        val steps = StringBuilder()
        password.forEach { c ->
            if (c !in "0123456789A") throw IllegalArgumentException("cannot type $c on numpad!")

            //first, avoid gap if going to 0/A
            if (x == 0 && c == '0') {
                x += 1
                steps.append('>')
            }
            if (x == 0 && c == 'A') {
                x += 2
                steps.append(">>")
            }
            //first, avoid gap if going from 0/A. in 1 go, to avoid getting split by other </>
            if (y == 3 && c == '1') {
                y -= 1
                steps.append('^')
            }
            if (y == 3 && c == '4') {
                y -= 2
                steps.append("^^")
            }
            if (y == 3 && c == '7') {
                y -= 3
                steps.append("^^^")
            }


            if (x == 2 && c in "1470258") {
                x -= 1
                steps.append('<')
            }
            if (x == 1 && c in "147") {
                x -= 1
                steps.append('<')
            }
            if (y == 0 && c in "0A123456") {
                y += 1
                steps.append('v')
            }
            if (y == 1 && c in "0A123") {
                y += 1
                steps.append('v')
            }
            if (y == 2 && c in "0A") {
                y += 1
                steps.append('v')
            }

            if (x == 0 && c in "0258A369") {
                x += 1
                steps.append('>')
            }
            if (x == 1 && c in "A369") {
                x += 1
                steps.append('>')
            }
            if (y == 3 && c in "123456789") {
                y -= 1
                steps.append('^')
            }
            if (y == 2 && c in "456789") {
                y -= 1
                steps.append('^')
            }
            if (y == 1 && c in "789") {
                y -= 1
                steps.append('^')
            }

            steps.append('A')
        }
        return steps.toString()
    }

    @Suppress("unused")
    fun typeOnNumpad(steps:String): String {
        //  x012x
        //y
        //0  789
        //1  456
        //2  123
        //3   0A
        var x = 2
        var y = 3
        val numpad = InfiniteGrid2d('.')
        numpad[0,0]='7'
        numpad[1,0]='8'
        numpad[2,0]='9'
        numpad[0,1]='4'
        numpad[1,1]='5'
        numpad[2,1]='6'
        numpad[0,2]='1'
        numpad[1,2]='2'
        numpad[2,2]='3'
        numpad[1,3]='0'
        numpad[2,3]='A'
        var password = ""
        steps.forEach { step ->
            when(step){
                '<' -> x -= 1
                '>' -> x += 1
                '^' -> y -= 1
                'v' -> y += 1
                'A' -> password += numpad[x,y]
                else -> throw IllegalArgumentException("unknown command $step in typeOnNumpad")
            }
        }
        return password
    }
    @Suppress("KotlinConstantConditions")
    fun stepsToTypeOnArrowPad(password:String): String {
        //  x012x
        //y
        //0   ^A
        //1  <v>
        //REDO: assign arrows in order, further from A first, b/c moving closer to A is easier.
        // first check if gap must be avoided.
        // else:  first <, then v, then ^/>
        var x = 2
        var y = 0
        val steps = StringBuilder()
        password.forEach { c ->
            if (c !in "<v>^A") throw IllegalArgumentException("cannot type $c on arrowPad!")
            //first, avoid gap if going to/from '<'
            if(y==0 && c=='<') {y+=1; steps.append('v') }
            if(x==0 && y==1 && c=='^') {x+=1; steps.append('>') }
            if(x==0 && y==1 && c=='A') {x+=2; steps.append(">>") }


            if(x==2 && c in "^v<") {x-=1; steps.append('<') }
            if(x==1 && c in "<") {x-=1; steps.append('<') }

            if(y==0 && c in "<v>>") {y+=1; steps.append('v') }

            if(y==1 && c in "^A") {y-=1; steps.append('^') }

            if(x==0 && c in "^v>A") {x+=1; steps.append('>') }
            if(x==1 && c in ">A") {x+=1; steps.append('>') }

            steps.append('A')
        }
        return steps.toString()
    }
    fun typeOnArrowPad(steps:String): String {
        //  x012x
        //y
        //0  789
        //1  456
        //2  123
        //3   0A

        var x = 2
        var y = 0
        var password = ""
        val arrowPad = InfiniteGrid2d('.')
        arrowPad[1,0]='^'
        arrowPad[2,0]='A'
        arrowPad[0,1]='<'
        arrowPad[1,1]='v'
        arrowPad[2,1]='>'
        steps.forEach { step ->
            when(step){
                '<' -> x -= 1
                '>' -> x += 1
                '^' -> y -= 1
                'v' -> y += 1
                'A' -> password += arrowPad[x,y]
                else -> throw IllegalArgumentException("unknown command $step in typeOnNumpad")
            }
        }
        return password
    }


    fun part1(input: String): Long {
        val passwords = input.lines()
        return passwords.sumOf { password ->
            println("steps to type $password:")
            val steps1 = stepsToTypeOnNumpad(password)
            println("   $steps1")
            val steps2 = stepsToTypeOnArrowPad(steps1)
            println("   $steps2")
            val steps3 = stepsToTypeOnArrowPad(steps2)
            println("   $steps3")
            val code = password.dropLast(1).toLong()
            return@sumOf steps3.length.toLong() * code
        }
    }

    fun part2(input: String): Long {
        //idea: instead of keeping track of the whole string, keep track of the repeats
        // number of times you do <path> from A to A again.
        // every time you do that path, the bot(s) before you(etc) will have to do the same inputs
        //      from when everyone(you+before) hits A to when everyone(you+before) hits A. that's the alignment point.
        //alt idea: go from the end backward, and memoize each step  including number of robots left.
        //  careful: get memo point correct - don't ignore important state.
        //      maybe just include all robot position this-one+before, and let the memo-key figure it out.
        //      or only memo on same point as above - self+before all on A's. (or before all on A's)
        //
        val passwords = input.lines()
        return passwords.sumOf { password ->
            var step = stepsToTypeOnNumpad(password)
            repeat(25){
                println("    step ${it+1}/25...")
                step = stepsToTypeOnArrowPad(step)
            }
            val code = password.dropLast(1).toLong()
            return@sumOf step.length.toLong() * code
        }
        //out of memory error
        //fix: increase memory of the JVM (by about 100x)
        // also (preview from _2): this is not always selecting the best path. teehee!
    }

    //    my long: v<<A>>^AvA^A v<<A>>^AAv<A<A>>^AAvAA<^A>Av<A >^AA<A>Av<A<A>>^AAAvA<^A>A
    //   my try 2: v<<A>>^AvA^A v<<A>>^AA<vA<A>>^AAvAA<^A>A<vA ^>AA<A>Av<<A>A^>AAA<Av>A^A
    // their long: <v<A>>^AvA^A  <vA<  AA>>^AAv A<^A>AAvA^A<vA >^AA<A>A<v<A>A>^AAAvA<^A>A

    println("for 379A:")
    print("my long string types:    ")
    val med1 = typeOnArrowPad("v<<A>>^AA<vA<A>>^AAvAA<^A>A<vA")
    println(med1)
    print("their long string types: ")
    val med2 = typeOnArrowPad("<vA<AA>>^AAvA<^A>AAvA^A<vA")
    println(med2 )

    print("my short string types:    ")
    val short1 = typeOnArrowPad(med1)
    println(short1)
    print("their short string types: ")
    val short2 = typeOnArrowPad(med2)
    println(short2 )

    val samplesAndTargets: List<Triple<String, Long?, Long?>> = listOf(
        Triple(
            """
029A
980A
179A
456A
379A
""",
            126384, null
        ),
    )
    samplesAndTargets.withIndex().forEach { (index, sample) ->
        println("example $index:")
        val (inputWithNewline, p1Target, p2Target) = sample
        val input = inputWithNewline.trim('\n')
        if (p1Target != null) {
            var check1: Long
            val time = measureNanoTime{
                check1 = part1(input)
            }
            println("  part 1 [ms]: ${time/1_000_000.0}")
            check(check1 == p1Target) {
                println("  part-1:  $check1 instead of $p1Target")
            }
        }
        if (p2Target != null) {
            var check2: Long
            val time = measureNanoTime{
                check2 = part2(input)
            }
            println("  part 2 [ms]: ${time/1_000_000.0}")
            check(check2 == p2Target) {
                println("  part-2:  $check2 instead of $p2Target")
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
    println("real input now:")
    var part1Ans: Long
    val time1 = measureNanoTime{
        part1Ans = part1(input)
    }
    println("part 1 [ms]: ${time1/1_000_000.0}")
    println("part 1 answer: $part1Ans")
//    check(part1Ans==) //do check while refactoring

    var part2Ans: Long
    val time2 = measureNanoTime{
        part2Ans = part2(input)
    }
    println("part 2 [ms]: ${time2/1_000_000.0}")
    println("part 2 answer: $part2Ans")
//    check(part2Ans==) //do check while refactoring
}
