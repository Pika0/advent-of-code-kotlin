import org.apache.commons.math3.util.ArithmeticUtils.pow

fun main() {
    class CPU(var registerA:Long, var registerB:Long, var registerC:Long, var program:List<Int>){
        var instructionPointer=0
        fun run(): List<Int> {
            val out = mutableListOf<Int>()
            while (instructionPointer < program.size) {
                val instr = program[instructionPointer]
                val literal = program[instructionPointer + 1]
                val combo = when (literal) {
                    0 -> 0L
                    1 -> 1L
                    2 -> 2L
                    3 -> 3L
                    4 -> registerA
                    5 -> registerB
                    6 -> registerC
                    else -> throw IllegalArgumentException("unknown opcode $literal")
                }
                when (instr) {
                    0 -> registerA /= pow(2, combo)
                    1 -> registerB = registerB xor literal.toLong()
                    2 -> registerB = combo % 8
                    3 -> if (registerA != 0L) instructionPointer = literal - 2
                    4 -> registerB = registerB xor registerC
                    5 -> out.add((combo % 8).toInt())
                    6 -> registerB = registerA / pow(2, combo)
                    7 -> registerC = registerA / pow(2, combo)
                    else -> throw IllegalArgumentException("unknown instruction $instr")
                }
                instructionPointer += 2
            }
            return out
        }
        @Suppress("unused")
        fun pp(){
            println("translated program:")
            println("  original program: ${program.joinToString(",")}")
            println("  registers: $registerA, $registerB, $registerC")
            program.chunked(2).forEachIndexed { index,(instrCode, literal) ->
                val combo = when (literal) {
                    0 -> '0'
                    1 -> '1'
                    2 -> '2'
                    3 -> '3'
                    4 -> 'A'
                    5 -> 'B'
                    6 -> 'C'
                    else -> throw IllegalArgumentException("unknown opcode $literal")
                }
                val instr = when (instrCode) {
                    0 -> "A /= pow(2, $combo)"
                    1 -> "B = B xor $literal"
                    2 -> "B = $combo % 8"
                    3 -> "if (A != 0) jump to $literal"
                    4 -> "B = B xor C"
                    5 -> "print($combo % 8)"
                    6 -> "B = A / pow(2, $combo)"
                    7 -> "C = A / pow(2, $combo)"
                    else -> throw IllegalArgumentException("unknown instruction $instrCode")
                }
                val index2 = (index*2).toString().padStart(3,' ')
                println("   $index2: $instrCode,$literal: $instr")
            }
        }
    }
    fun part1(input: String): String {
        val (registers, programStr) = input.split("\n\n")
        val (a,b,c) = registers.lines().map{
            it.substringAfter(": ").toLong()
        }
        val program = programStr.substringAfter(": ").split(",").map{it.toInt()}
        val device = CPU(a,b,c,program)
//        device.pp()
        val output = device.run()
        return output.joinToString(",")
        //0,0,0,0,0,0,0,0,0  incorrect
    }


    // each instr depends on parts of A further to the left/larger
    // -> each printed val depends on what vals will be printed after it.
    // -> each instr depends on instr to the right.
    // so, work on finding it from the right-side first, which has no dependencies.
    // the right-most instr is the last instr, and is made from the most-significant part of the A register

    //some instr may have multiple valid options. but since lefter instr depend on them, only 1 will be correct.
    //thus, a recursive solver for trying all the options
    fun findSolution(instr: List<Int>, prevVal:Long=0, numComplete:Int=0):Long?{
        print(prevVal.toString(8).padStart(10,' '))
        print(": ")
        print("${instr.dropLast(numComplete)} | ${instr.takeLast(numComplete)}")
        println()

        if(numComplete==instr.size) return prevVal
        //first call: prevVal=0, numComplete=0
        val largeA = prevVal*8
        repeat(8){ smallA ->
            val a = largeA+smallA
            val device = CPU(a,0,0,instr)
            val output = device.run()
            if (output == instr.takeLast(numComplete+1)){
                //worked! try the next val now!
                val finalA = findSolution(instr, a, numComplete+1)
                if(finalA!=null) return finalA
            }

        }
        return null
    }
    @Suppress("UNUSED_VARIABLE")
    val mySequence = sequence {
        var current = 0L

        while (true) {
            yield(current)
            val highestPowerOf2 = java.lang.Long.highestOneBit(current)
            val step = if (highestPowerOf2<=5) 1 else highestPowerOf2/8
            current += step
        }
    }
    @Suppress("UNUSED_VARIABLE")
    fun part2(input: String): Long {
        val (registers, programStr) = input.split("\n\n")
//        val (_,b,c) = registers.lines().map{
//            it.substringAfter(": ").toLong()
//        }
        val program = programStr.substringAfter(": ").split(",").map{it.toInt()}
        return findSolution(program)!!



//        for(newA in mySequence.take(100)){
//            val device = CPU(newA,b,c,program)
//            val output = device.run()
//            val outputStr = output.joinToString(",").padStart(program.size*2,' ')
//            val paddedNewA = newA.toString().padStart(8,' ')
//            print("a=$paddedNewA-> ")
//            println(outputStr)
//
//        }
//        return program.joinToString("").let{ "0${it}" }.reversed().toLong(8)

    }

    val samplesAndTargets: List<Triple<String, String?, Long?>> = listOf(
        Triple(
            """
Register A: 729
Register B: 0
Register C: 0

Program: 0,1,5,4,3,0
""",
            "4,6,3,5,6,3,5,2,1,0", null
        ),
        Triple(
            """
Register A: 2024
Register B: 0
Register C: 0

Program: 0,3,5,4,3,0
""",
            null, 117440
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
//    // Or read a large test input from the `src/Day17_test.txt` file:
//    val testInput = readInput("Day17_test")
//    check(part1(testInput) == 1)

    // download input (if needed) into day17.txt.  return list of lines
    val input = loadAndReadInput(17, 2024)
    val part1Ans = part1(input)
    println("part 1 answer: $part1Ans")
//    check(part1Ans==) //check while refactoring
    val part2Ans = part2(input)
    println("part 2 answer: $part2Ans")
//    check(part2Ans==) //check while refactoring
}
