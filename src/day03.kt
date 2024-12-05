fun main() {
    fun part1(lines: List<String>): Int {
        val regex = Regex("""mul\((\d+),(\d+)\)""")
        return lines.sumOf{ line ->
            regex.findAll(line).sumOf{match ->
                match.groupValues[1].toInt() * match.groupValues[2].toInt()
            }
        }
    }

    fun part2(linesList: List<String>): Int {
        val input = linesList.joinToString("\n")
        var enabled = true
        var position = 0
        var total = 0
        val stopOrMultiplyPattern = Regex("""mul\((\d+),(\d+)\)|don't\(\)""")
        val doPattern = Regex("""do\(\)""")
        while (position<input.length){
            if (enabled){
                val match = stopOrMultiplyPattern.find(input, position)
                when {
                    match==null -> break
                    match.value == "don't()" -> enabled = false
                    else -> total += match.groupValues[1].toInt() * match.groupValues[2].toInt()
                }
                position = match.range.last
            }
            else{
                val match = doPattern.find(input, position)
                when {
                    match==null -> break
                    else -> enabled = true
                }
                position = match.range.last
            }
        }
        return total
    }

//    // Test if implementation meets criteria from the description, like:
//    check(part1(listOf("test_input")) == 1)
//
//    // Or read a large test input from the `src/Day03_test.txt` file:
//    val testInput = readInput("Day03_test")
//    check(part1(testInput) == 1)

    // download input (if needed) into day03.txt.  return list of lines
    val input = loadAndReadInput(3, 2024)
    part1(input).println()
    part2(input).println()
}
