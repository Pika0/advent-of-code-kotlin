fun main() {
    fun part1(input: String): Long {
        val lines = input.lines()
        fun isValid2(targetValue: Long, remainingParts: MutableList<Long>, soFar:Long): Boolean{
            if(remainingParts.size==0){
                return soFar==targetValue
            }
            if (soFar > targetValue) return false
            val nextNum = remainingParts.removeLast()
            //try multiplying
            run {
                val newSoFar = soFar * nextNum
                if (isValid2(targetValue, remainingParts, newSoFar)) return true
            }
            run {
                val newSoFar = soFar + nextNum
                if (isValid2(targetValue, remainingParts, newSoFar)) return true
            }
            remainingParts.addLast(nextNum) //now put it back
            return false
        }
        fun isValid(targetValue: Long, parts: List<Long>): Boolean{
            val remainingParts = parts.asReversed().toMutableList()
            val firstValue = remainingParts.removeLast()
            return isValid2(targetValue, remainingParts, firstValue)
        }
        var total=0L
        lines.forEach{ line ->
            val (targetStr, numStr) = line.split(": ")
            val target = targetStr.toLong()
            val nums = numStr.split(" ").map{it.toLong()}
            if (isValid(target, nums)) total += target
        }
        return total
        //348 too low
        //oh, not how many but their sum
        //882304362421
    }

    fun part2(input: String): Long {
        val lines = input.lines()
        fun isValid2(targetValue: Long, remainingParts: MutableList<Long>, soFar:Long): Boolean{
            if(remainingParts.size==0){
                return soFar==targetValue
            }
            if (soFar > targetValue) return false
            val nextNum = remainingParts.removeLast()

            run { //try multiplying
                val newSoFar = soFar * nextNum
                if (isValid2(targetValue, remainingParts, newSoFar)) return true
            }
            run { //try adding
                val newSoFar = soFar + nextNum
                if (isValid2(targetValue, remainingParts, newSoFar)) return true
            }
            run { //try concatenating
                val newSoFar = "$soFar$nextNum".toLong()
                if (isValid2(targetValue, remainingParts, newSoFar)) return true
            }
            remainingParts.addLast(nextNum) //now put it back
            return false
        }
        fun isValid(targetValue: Long, parts: List<Long>): Boolean{
            val remainingParts = parts.asReversed().toMutableList()
            val firstValue = remainingParts.removeLast()
            return isValid2(targetValue, remainingParts, firstValue)
        }
        var total=0L
        lines.forEach{ line ->
            val (targetStr, numStr) = line.split(": ")
            val target = targetStr.toLong()
            val nums = numStr.split(" ").map{it.toLong()}
            if (isValid(target, nums)) total += target
        }
        return total
    }




    val samplesAndTargets: List<Triple<String, Long?, Long?>> = listOf(
        Triple(
            """
190: 10 19
3267: 81 40 27
83: 17 5
156: 15 6
7290: 6 8 6 15
161011: 16 10 13
192: 17 8 14
21037: 9 7 18 13
292: 11 6 16 20
""",
            3749, 11387
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
    }

//
//    // Or read a large test input from the `src/Day07_test.txt` file:
//    val testInput = readInput("Day07_test")
//    check(part1(testInput) == 1)

    // download input (if needed) into day07.txt.  return list of lines
    val input = loadAndReadInput(7, 2024)
    println("part 1 answer: ${part1(input)}")
    println("part 2 answer: ${part2(input)}")
}
