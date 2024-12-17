fun main() {
    fun part1(input: String): Int {
        val lines: List<String> = input.lines()
        val grid = InfiniteGrid2d('.')
        lines.withIndex().forEach{ (y, line) ->
            line.withIndex().forEach { (x,c) ->
                grid[x,y]=c
            }
        }
        val directions = listOf(
            Pair( 0, -1),  // North
            Pair(-1, -1),  // West-North
            Pair(-1,  0),  // West
            Pair(-1,  1),  // West-South
            Pair( 0,  1),  // South
            Pair( 1,  1),  // East-South
            Pair( 1,  0),  // East
            Pair( 1, -1),  // East-North
        )
        var total=0
        grid.forEach{ (x,y),c ->
            if (c!='X') return@forEach
            directions.forEach nextDirection@{ (dx,dy) ->
                if (grid[x+dx,y+dy]!='M') return@nextDirection
                if (grid[x+dx*2,y+dy*2]!='A') return@nextDirection
                if (grid[x+dx*3,y+dy*3]!='S') return@nextDirection
                total += 1
            }
        }

        return total
    }

    fun part2(input: String): Int {
        val lines = input.lines()
        val grid = InfiniteGrid2d('.')
        lines.withIndex().forEach{ (y, line) ->
            line.withIndex().forEach { (x,c) ->
                grid[x,y]=c
            }
        }
        var total=0
        grid.forEach{ (x,y),c ->
            if (c!='A') return@forEach

            if(
                ((grid[x-1,y-1]=='M' && grid[x+1,y+1]=='S') || (grid[x-1,y-1]=='S' && grid[x+1,y+1]=='M'))
                &&
                ((grid[x+1,y-1]=='M' && grid[x-1,y+1]=='S') || (grid[x+1,y-1]=='S' && grid[x-1,y+1]=='M'))
            ) {
                total += 1
            }
        }

        return total
    }

//    // Test if implementation meets criteria from the description, like:
    check(part1("""MMMSXXMASM
MSAMXMSMSA
AMXSXMAAMM
MSAMASMSMX
XMASAMXAMM
XXAMMXXAMA
SMSMSASXSS
SAXAMASAAA
MAMMMXMMMM
MXMXAXMASX""") == 18)
    val r= part2("""MMMSXXMASM
MSAMXMSMSA
AMXSXMAAMM
MSAMASMSMX
XMASAMXAMM
XXAMMXXAMA
SMSMSASXSS
SAXAMASAAA
MAMMMXMMMM
MXMXAXMASX""")
    check( r== 9){
        println("incorrect value: $r")
    }
//
//    // Or read a large test input from the `src/Day04_test.txt` file:
//    val testInput = readInput("Day04_test")
//    check(part1(testInput) == 1)

    // download input (if needed) into day04.txt.  return list of lines
    val input = loadAndReadInput(4, 2024)
    part1(input).println()
    part2(input).println()
}
