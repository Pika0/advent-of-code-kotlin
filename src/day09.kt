fun main() {
    fun part1(input: String): Long {
        val inputsVals = input.lines()[0].map(Char::digitToInt)
        val data = mutableListOf<Int>()
        var dataBlockID=0
        var modeIsData=true
        inputsVals.forEach{ blockLen ->
            repeat(blockLen){
                if (modeIsData) data.add(dataBlockID)
                else data.add(-1)

            }
            if (modeIsData) dataBlockID  += 1
            modeIsData = !modeIsData
        }


        var placingIndex = 0
        var takingIndex = data.size-1
        while (placingIndex < takingIndex){
            if (data[placingIndex] != -1){
                placingIndex += 1
                continue
            }
            if (data[takingIndex] == -1){
                takingIndex -= 1
                continue
            }
            data[placingIndex] = data[takingIndex]
            data[takingIndex] = -1
        }

        return data.takeWhile { it>=0 }.mapIndexed{ index, value ->
            index.toLong() * value.toLong()
        }.sum()

    }

    data class File(val size: Int, val id: Int?)

    fun part2(input: String): Long {
        val inputsVals = input.lines()[0].map(Char::digitToInt)
        val data = mutableListOf<File>()
        var dataBlockID=0
        var modeIsData=true
        inputsVals.forEach{ blockLen ->
            if (modeIsData) {
                data.add( File( blockLen,  dataBlockID) )
                dataBlockID  += 1
            }
            else{
                data.add( File( blockLen,  null) )
            }
            modeIsData = !modeIsData
        }

        var takingIndex = data.size-1
        //from the right, try to move each group
        while (takingIndex >= 1){
            //find next group to be moved
            if(data[takingIndex].id==null){
                takingIndex -= 1
                continue
            }

            //target location from the left
            var placingIndex = 0
            while (placingIndex<takingIndex){

                if (data[placingIndex].id != null){ //space is already filled
                    placingIndex += 1
                    continue
                }
                if (data[placingIndex].size < data[takingIndex].size){ //space is too small
                    placingIndex += 1
                    continue
                }

                //it's ready to move
                val spacesLeftover = data[placingIndex].size - data[takingIndex].size
                data[placingIndex] = data[takingIndex]
                data[takingIndex] = File(data[takingIndex].size, null)
                if (spacesLeftover > 0) {
                    data.add(placingIndex+1, File(spacesLeftover, null))
                    takingIndex += 1 //shift it as well
                }
                break

            }
            //it's now moved or no valid location was found
            takingIndex -= 1 //move on to the next one

        }//end: while trying to move each group from the right

        var fileSysIndex: Long = 0
        return data.sumOf { file ->
            if (file.id == null) {
                fileSysIndex += file.size
                0
            } else {
                (0..<file.size).sumOf {
                    val r = fileSysIndex * file.id
                    fileSysIndex += 1
                    r
                }
            }
        }


    }


    val samplesAndTargets: List<Triple<String, Long?, Long?>> = listOf(
        Triple(
            """
2333133121414131402
""",
            1928, 2858
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
//    // Or read a large test input from the `src/Day09_test.txt` file:
//    val testInput = readInput("Day09_test")
//    check(part1(testInput) == 1)

    // download input (if needed) into day09.txt.  return list of lines
    val input = loadAndReadInput(9, 2024)
    println("part 1 answer: ${part1(input)}")
    println("part 2 answer: ${part2(input)}")
}
