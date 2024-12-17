fun main() {
    val directions = listOf(
        Int2( 0, -1),  // North
        Int2(-1,  0),  // West
        Int2( 0,  1),  // South
        Int2( 1,  0),  // East
    )
    fun rotateRight(dir: Int2): Int2 {
        return Int2(-dir.y, dir.x)
    }
    @Suppress("unused")
    fun rotateLeft(dir: Int2): Int2 {
        return Int2(dir.y, -dir.x)
    }
    data class DirectionalStorage<T>(var east:T, var north:T, var west:T, var south:T){
        operator fun get(dir:Int2): T{
            return when(dir){
                Int2(0,-1) -> north
                Int2(0,1) -> south
                Int2(-1,0) -> west
                Int2(1,0) -> east
                else -> throw IllegalArgumentException("cannot retrieve directional data for unknown direction: $dir")
            }
        }
        operator fun set(dir:Int2, newVal: T){
            when(dir){
                Int2(0,-1) -> north=newVal
                Int2(0,1) -> south=newVal
                Int2(-1,0) -> west=newVal
                Int2(1,0) -> east=newVal
                else -> throw IllegalArgumentException("cannot retrieve directional data for unknown direction: $dir")
            }
        }
    }

    fun part1(input: String): Long {
        val lines = input.lines()
        val height=lines.size
        val width = lines[0].length
        val map = InfiniteGrid2d('.')
        lines.forEachIndexed{y, line ->
            line.forEachIndexed{x, c ->
                map[x,y]=c
            }
        }
        val areaIDMap = InfiniteGrid2d(-1)
        val areaCellsByID=mutableMapOf<Int,MutableList<Int2>>()
        fun exploreAndLabelArea(x:Int,y:Int, areaLabel:Char, id: Int){
            if (areaIDMap[x,y]!=-1) return
            if (x<0 || y<0 || x>map.maxX || y>map.maxY) return
            if (map[x,y]!=areaLabel) return
            areaIDMap[x,y] = id
            areaCellsByID.getOrPut(id){mutableListOf()}.add(Int2(x,y))
            directions.forEach{direction ->
                exploreAndLabelArea(x+direction.x,y+direction.y, areaLabel, id)
            }
        }
        var nextID=1
        repeat(height){y->
            repeat(width) nextCell@{x->
                if (areaIDMap[x,y]!=-1) return@nextCell
                exploreAndLabelArea(x,y, map[x,y], nextID)
                nextID+=1
            }
        }
        areaIDMap.print()

        var sum: Long = 0
        areaCellsByID.map { (id,cells) ->
            @Suppress("RemoveRedundantCallsOfConversionMethods") //req for overload resolution of sumOf
            val numFences: Int = cells.sumOf { cell:Int2->
                directions.sumOf { direction:Int2 ->
                    if(areaIDMap[cell+direction]!=id) 1.toInt()
                    else 0.toInt()
                }
            }
            println("id $id has $numFences fences and ${cells.size} area, totalling ${numFences * cells.size} cost")
            sum += numFences * cells.size
        }


        return sum //25 min
    }

    fun part2(input: String): Long {
        val lines = input.lines()
        val height=lines.size
        val width = lines[0].length
        val map = InfiniteGrid2d('.')
        lines.forEachIndexed{y, line ->
            line.forEachIndexed{x, c ->
                map[x,y]=c
            }
        }
        val areaIDMap = InfiniteGrid2d(-1)
        val areaCellsByID=mutableMapOf<Int,MutableList<Int2>>()
        fun exploreAndLabelArea(x:Int,y:Int, areaLabel:Char, id: Int){
            if (areaIDMap[x,y]!=-1) return
            if (x<0 || y<0 || x>map.maxX || y>map.maxY) return
            if (map[x,y]!=areaLabel) return
            areaIDMap[x,y] = id
            areaCellsByID.getOrPut(id){mutableListOf()}.add(Int2(x,y))
            directions.forEach{direction ->
                exploreAndLabelArea(x+direction.x,y+direction.y, areaLabel, id)
            }
        }
        var nextID=1
        repeat(height){y->
            repeat(width) nextCell@{x->
                if (areaIDMap[x,y]!=-1) return@nextCell
                exploreAndLabelArea(x,y, map[x,y], nextID)
                nextID+=1
            }
        }
        if(lines.size<50) areaIDMap.print()

        @Suppress("BooleanLiteralArgument")
        val fenceCounted =
            InfiniteGrid2d(DirectionalStorage(false, false, false, false))
        map.forEach{(x,y),_ ->
            @Suppress("BooleanLiteralArgument")
            fenceCounted[x,y] = DirectionalStorage(false, false, false, false)
        }
        fun countThisFenceLoop(xStart:Int, yStart:Int, fenceSideStart:Int2, id:Int): Int{
            var numberOfFenceSides = 0
            //how to join the start with the end?
            // the number of turns is the number of sides, so we don't need to!
            var currentInnerSpace = Int2(xStart, yStart)
            var fenceSide = fenceSideStart
            while(currentInnerSpace.x != xStart || currentInnerSpace.y != yStart || fenceSide!=fenceSideStart || numberOfFenceSides==0){
                //mark this space+dir as checked
                fenceCounted[currentInnerSpace][fenceSide]=true
                //checking clockwise
                val clockwiseDir = rotateRight(fenceSide)
                //next spot without turning
                val nextInnerSpace = currentInnerSpace + clockwiseDir
                val nextOuterSpace = nextInnerSpace + fenceSide
                if(areaIDMap[nextInnerSpace]==id && areaIDMap[nextOuterSpace]!=id){
                    //continuing straight
                    currentInnerSpace = nextInnerSpace
                    continue
                }
                if(areaIDMap[nextInnerSpace]==id && areaIDMap[nextOuterSpace]==id){
                    //turn left
                    currentInnerSpace = nextOuterSpace
                    fenceSide = rotateLeft(fenceSide)
                    numberOfFenceSides += 1 //new side
                    continue
                }
                if(areaIDMap[nextInnerSpace]!=id){
                    //turn right
                    // currentInnerSpace  stays the same
                    fenceSide = rotateRight(fenceSide)
                    numberOfFenceSides += 1 //new side
                    continue
                }
            }//loop connected!
            return numberOfFenceSides
        }
        fun lookForFenceLoopsAndCount(x:Int, y:Int, id:Int): Int{
            // each adjacent side
            var fenceSides = 0
            directions.forEach{possibleFenceSide->
                //still unchecked?
//                val temfences = fenceCounted[x,y]
                if(fenceCounted[x,y][possibleFenceSide]) return@forEach
                //then check!
//                val tempAreaMapCell = areaIDMap[x+possibleFenceSide.x, y+possibleFenceSide.y]
                if(areaIDMap[x+possibleFenceSide.x, y+possibleFenceSide.y]!=id){
                    val fenceSidesThis  = countThisFenceLoop(x,y,possibleFenceSide,id)
                    fenceSides += fenceSidesThis
                }
            }
//            println("counting fences in $id at $x,$y: $fenceSides")
            return fenceSides
        }
        var sum: Long = 0
        areaCellsByID.map { (id,cells) ->
            var numFences = 0
            cells.forEach{cell ->
                numFences += lookForFenceLoopsAndCount(cell.x, cell.y, id)
            }

            println("id $id has $numFences sides and ${cells.size} area, totalling ${numFences * cells.size} cost")
            sum += numFences * cells.size
        }


        return sum
    }


    val samplesAndTargets: List<Triple<String, Long?, Long?>> = listOf(
        Triple(
            """
AAAA
BBCD
BBCC
EEEC
""",
            140, 80
        ),
        Triple(
            """
OOOOO
OXOXO
OOOOO
OXOXO
OOOOO

""",
            772, 436
        ),
        Triple(
            """
RRRRIICCFF
RRRRIICCCF
VVRRRCCFFF
VVRCCCJFFF
VVVVCJJCFE
VVIVCCJJEE
VVIIICJJEE
MIIIIIJJEE
MIIISIJEEE
MMMISSJEEE
""",
            1930, 1206
        ),
        Triple(
            """
EEEEE
EXXXX
EEEEE
EXXXX
EEEEE
""",
            null, 236
        ),
        Triple(
            """
AAAAAA
AAABBA
AAABBA
ABBAAA
ABBAAA
AAAAAA
""",
            null, 368
        ),
        Triple(
            """
AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
AAABBAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
AAABBAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
ABBAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
ABBAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
""",
            68628, 6776
        ),
        Triple(
            """
AAAA
ABBA
ABBA
AAAA
""",
            320, 112
        ),
        Triple(
            """
AAAA
AABA
ABBA
AABA
""",
            328, 176
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
//    // Or read a large test input from the `src/Day12_test.txt` file:
//    val testInput = readInput("Day12_test")
//    check(part1(testInput) == 1)

    // download input (if needed) into day12.txt.  return list of lines
    val input = loadAndReadInput(12, 2024)
    println("part 1 answer: ${part1(input)}")
    println("part 2 answer: ${part2(input)}")
}
