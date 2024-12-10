

fun main() {
    fun part1(input: String): Int {
        val lines = input.lines()
        val height = lines.size
        val width = lines[0].length
        val antennaeLocations = hashMapOf<Char,List<Int2>>()
        lines.withIndex().forEach{ (y, line) ->
            line.withIndex().forEach eachChar@{ (x,c) ->
                if (c=='.') return@eachChar
                if (c !in antennaeLocations) antennaeLocations[c] = emptyList()
                antennaeLocations[c] = antennaeLocations[c]!! + Int2(x,y)
            }
        }
        val antinodeMap = mutableSetOf<Int2>()
        antennaeLocations.forEach{ (_,positions) ->
            // every combination pair of matching antennae
            positions.withIndex().forEach{(i1, a)->
                positions.drop(i1+1).forEach { b ->
                    val frombToa = a-b
                    val antinode1 = a+frombToa
                    if (antinode1.x in 0..<width && antinode1.y in 0..<height){
                        antinodeMap.add(antinode1)
                    }
                    val antinode2 = b - frombToa
                    if (antinode2.x in 0..<width && antinode2.y in 0..<height) {
                        antinodeMap.add(antinode2)
                    }
                }
            }
        }
//        antinodeMap.print() // when it was an InfiniteGrid

        return antinodeMap.size
    }

    fun part2(input: String): Int {
        val lines = input.lines()
        val height = lines.size
        val width = lines[0].length
        val antennaeLocations = hashMapOf<Char,List<Int2>>()
        lines.withIndex().forEach{ (y, line) ->
            line.withIndex().forEach eachChar@{ (x,c) ->
                if (c=='.') return@eachChar
                if (c !in antennaeLocations) antennaeLocations[c] = emptyList()
                antennaeLocations[c] = antennaeLocations[c]!! + Int2(x,y)
            }
        }
        val antinodeMap = mutableSetOf<Int2>()
        antennaeLocations.forEach{ (_,positions) ->
            // every combination pair of matching antennae
            positions.withIndex().forEach{(i1, a)->
                positions.drop(i1+1).forEach { b ->
                    val frombToa = a-b
                    var antinode1 = a
                    while (antinode1.x in 0..<width && antinode1.y in 0..<height){
                        antinodeMap.add(antinode1)
                        antinode1 += frombToa
                    }
                    var antinode2 = b
                    while (antinode2.x in 0..<width && antinode2.y in 0..<height) {
                        antinodeMap.add(antinode2)
                        antinode2 -= frombToa
                    }
                }
            }
        }
//        antinodeMap.print() // when it was an InfiniteGrid

        return antinodeMap.size
    }


    val samplesAndTargets: List<Triple<String, Int?, Int?>> = listOf(
        Triple(
            """
............
........0...
.....0......
.......0....
....0.......
......A.....
............
............
........A...
.........A..
............
............
""",
            14, 34
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
//    // Or read a large test input from the `src/Day08_test.txt` file:
//    val testInput = readInput("Day08_test")
//    check(part1(testInput) == 1)

    // download input (if needed) into day08.txt.  return list of lines
    val input = loadAndReadInput(8, 2024)
    println("part 1 answer: ${part1(input)}")
    println("part 2 answer: ${part2(input)}")
}
