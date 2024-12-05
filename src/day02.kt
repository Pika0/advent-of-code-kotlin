import kotlin.math.abs

fun main() {
    fun part1(lines: List<String>): Int {
        return lines.map{ line->
            val levels = line.split(" ").map{it.toInt()}
            val correct = if (levels[1] < levels[0]){
                //decreasing
                levels.windowed(2).all(){ pair ->
                    val a = pair[0]
                    val b = pair[1]
                    if (b>=a) false
                    else if (b<a-3) false
                    else true
                }
            }
            else{
                //increasing
                levels.windowed(2).all(){ pair ->
                    val a = pair[0]
                    val b = pair[1]
                    if (b<=a) false
                    else if (b>a+3) false
                    else true
                }
            }
            if (correct) 1 else 0
        }.sum()

    }
    fun isCorrect(levels: List<Int>): Boolean {
        var descending = 0
        var ascending = 0
        if (levels.size<3) return true
        if (levels.size==3){
            if (abs(levels[0]-levels[1])<=3) return true
            if (abs(levels[1]-levels[2])<=3) return true
            if (abs(levels[0]-levels[2])<=3) return true
            return false
        }
        if (levels[0]>levels[1]) descending += 1 else ascending += 1
        if (levels[1]>levels[2]) descending += 1 else ascending += 1
        if (levels[2]>levels[3]) descending += 1 else ascending += 1

        var errorFound=false
        if (ascending>=2){
            //ascending
            var i=0
            while( i < levels.size - 1){
                val a = levels[i]
                val b = levels[i+1]
                if (a<b && b<=a+3) {
                    //no error here
                    i += 1
                    continue
                }
                // OK, this->next is error.

                if(errorFound) return false //oh, this is second error
                errorFound = true
                //But do we remove this or next?
                if (i+2 >= levels.size) return true //at the end (correct by: remove the last item)

                //ok if remove next?
                val c = levels[i+2]
                //remove next item?
                if (a<c && c<=a+3){
                    //correct by: remove the next item
                    i += 2 //skip the next->double-next check because the next is removed
                    continue
                }
                //at the start?
                if (i==0) {
                    //removed first item to fix first->second. now keep checking for more errors
                    i += 1
                    continue
                }
                //only remove this if prev->next is good
                val z = levels[i-1]
                if (z<b && b<=z+3){
                    //prev->next is good, remove a
                    i += 1
                    continue
                }

                return false //no fix for this error
            }
            return true //correct!

        }
        else{
            //descending
            var i=0
            while( i < levels.size - 1){
                val a = levels[i]
                val b = levels[i+1]
                if (a>b && b>=a-3) {
                    //no error here
                    i += 1
                    continue
                }
                // OK, this->next is error.

                if(errorFound) return false //oh, this is second error
                errorFound = true
                //But do we remove this or next?
                if (i+2 >= levels.size) return true //at the end (correct by: remove the last item)

                //ok if remove next?
                val c = levels[i+2]
                //remove next item?
                if (a>c && c>=a-3){
                    //correct by: remove the next item
                    i += 2 //skip the next->double-next check because the next is removed
                    continue
                }
                //at the start?
                if (i==0) {
                    //removed first item to fix first->second. now keep checking for more errors
                    i += 1
                    continue
                }
                //only remove this if prev->next is good
                val z = levels[i-1]
                if (z>b && b>=z-3){
                    //prev->next is good, remove a
                    i += 1
                    continue
                }

                return false //no fix for this error
            }
            return true //correct!
        }
    }
    fun part2(lines: List<String>): Int {
        return lines.map{ line->
            val levels = line.split(" ").map{it.toInt()}

            if (isCorrect(levels)) 1 else 0
        }.sum()
    }

    // Test if implementation meets criteria from the description, like:
    check(part1(("7 6 4 2 1\n" +
            "1 2 7 8 9\n" +
            "9 7 6 2 1\n" +
            "1 3 2 4 5\n" +
            "8 6 4 4 1\n" +
            "1 3 6 7 9").lines()) == 2)
    check(part2(("7 6 4 2 1\n" +
            "1 2 7 8 9\n" +
            "9 7 6 2 1\n" +
            "1 3 2 4 5\n" +
            "8 6 4 4 1\n" +
            "1 3 6 7 9").lines()) == 4)
//
//    // Or read a large test input from the `src/Day02_test.txt` file:
//    val testInput = readInput("Day02_test")
//    check(part1(testInput) == 1)

    // Read the input from the `src/Day01.txt` file.
    val input = loadAndReadInput(2, 2024)
    part1(input).println()
    part2(input).println()
}
