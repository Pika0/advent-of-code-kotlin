fun main() {
    fun part1(lines: List<String>): Int {
        val list1 = mutableListOf<Int>() //alternate: arrayListOf<Int>()
        val list2 = mutableListOf<Int>() // or maybe even: linkedListOf<Int>()
        lines.forEach{
            val vals = it.split(" ")
            list1.add(vals.first().toInt())
            list2.add(vals.last().toInt())
        }
        list1.sort()
        list2.sort()

        val z = list1.zip(list2){  a,b ->
            val c = a-b
            if (c < 0) -c
            else c
        }
        val sum = z.fold(0) { a, b -> a + b }

        return sum
    }

    fun part2(lines: List<String>): Int {
        val list1 = mutableListOf<Int>() //
        val list2 = HashMap<Int, Int>() //
        var sum = 0
        lines.forEach{
            val vals = it.split(" ")
            val first = vals.first().toInt()
            list1.add(first)

            val second = vals.last().toInt()
            list2[second] = list2.getOrDefault(second,0) +1
        }

        list1.forEach{
            sum += it*list2.getOrDefault(it,0)
        }
        return sum
    }

//    // Test if implementation meets criteria from the description, like:
//    check(part1(listOf("test_input")) == 1)
//
//    // Or read a large test input from the `src/Day01_test.txt` file:
//    val testInput = readInput("Day01_test")
//    check(part1(testInput) == 1)

    // Read the input from the `src/Day01.txt` file.
    val input = loadAndReadInput(1, 2024)
    part1(input).println()
    part2(input).println()
}
