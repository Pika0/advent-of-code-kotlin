fun main() {
    fun makeDesign(towels:List<String>, design:String, designSoFar:String=""):Boolean{
        if(designSoFar.length>design.length) return false
        if(designSoFar==design) return true
        if(designSoFar != design.substring(0,designSoFar.length)) return false
//        print("$designSoFar|")
//        println(design.drop(designSoFar.length))
        for(towel in towels){
            val designExtended = designSoFar+towel
            if (makeDesign(towels, design, designExtended)) return true
        }
        return false
    }
    fun part1(input: String): Long {
        val (towelsStr, designsStr) = input.split("\n\n")
        val towels=towelsStr.split(", ")
        val designs=designsStr.lines()
        return designs.sumOf { design ->
            if (makeDesign(towels, design)) 1L
            else 0L
        }
    }
    @Suppress("LocalVariableName")
    val makeDesignMultiple_memo = mutableMapOf<Pair<String,String>,Long>()
    fun makeDesignMultiple(towels:List<String>, design:String, designSoFar:String=""):Long{
        val key = designSoFar to design
        return makeDesignMultiple_memo.getOrPut(key) {
            if (designSoFar.length > design.length) return@getOrPut 0
            if (designSoFar == design) return@getOrPut 1
            if (designSoFar != design.substring(0, designSoFar.length)) return@getOrPut 0
//        print("$designSoFar|")
//        println(design.drop(designSoFar.length))
            var total = 0L
            for (towel in towels) {
                val designExtended = designSoFar + towel
                total += makeDesignMultiple(towels, design, designExtended)
            }
            return@getOrPut total
        }
    }

    fun part2(input: String): Long {
        val (towelsStr, designsStr) = input.split("\n\n")
        val towels=towelsStr.split(", ")
        val designs=designsStr.lines()
        return designs.sumOf { design ->
            makeDesignMultiple(towels, design)
        }
    }


    val samplesAndTargets: List<Triple<String, Long?, Long?>> = listOf(
        Triple(
            """
r, wr, b, g, bwu, rb, gb, br

brwrr
bggr
gbbr
rrbgbr
ubwu
bwurrg
brgr
bbrgwb
""",
            6, 16
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
//    // Or read a large test input from the `src/Day19_test.txt` file:
//    val testInput = readInput("Day19_test")
//    check(part1(testInput) == 1)

    // download input (if needed) into day19.txt.  return list of lines
    val input = loadAndReadInput(19, 2024)
    val part1Ans = part1(input)
    println("part 1 answer: $part1Ans")
//    check(part1Ans==) //check while refactoring
    val part2Ans = part2(input)
    println("part 2 answer: $part2Ans")
//    check(part2Ans==) //check while refactoring
}
