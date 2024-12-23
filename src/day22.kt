import kotlin.system.measureNanoTime

fun main() {
    fun mix(secretNumber:Long,mixinValue:Long):Long{
        return secretNumber xor mixinValue
    }
    fun prune(secretNumber:Long):Long{
        return secretNumber % 16777216L
    }
    fun advanceSecretNumber(secretNumber: Long):Long{
        //step 1
        @Suppress("NAME_SHADOWING")
        var secretNumber = prune(mix(secretNumber,secretNumber*64))
        //step 2
        secretNumber = prune(mix(secretNumber, secretNumber/32))
        //step 3
        secretNumber = prune(mix(secretNumber, secretNumber*2048))
        return secretNumber
    }


    fun part1(input: String): Long {
        val day1SecretNumbers = input.lines().map{it.toLong()}
        return day1SecretNumbers.sumOf { secretNumber0 ->
            var secretNumber = secretNumber0
//            println("$index: $secretNumber")
            repeat(2000){
                secretNumber = advanceSecretNumber(secretNumber)
            }
//            println("    >2000: $secretNumber")
            secretNumber
        }

    }

    data class ChangeSequence(val s1 :Int, val s2 :Int, val s3 :Int, val s4 :Int){
        fun addNew(newVal:Int): ChangeSequence{
            return ChangeSequence(s2, s3, s4, newVal)
        }
    }

    fun part2(input: String): Long {
        val day1SecretNumbers = input.lines().map{it.toLong()}
        val totalBananasForEachSequence = mutableMapOf<ChangeSequence,Int>()
        day1SecretNumbers.forEach { secretNumber0 ->
            var secretNumber = secretNumber0
            val initialChangeSequence = mutableListOf<Int>()
            val sequencesHitForThisMonkey = mutableSetOf<ChangeSequence>()
            repeat(3){
                val secretNumberNext = advanceSecretNumber(secretNumber)
                val priceChange = (secretNumberNext%10 - secretNumber%10).toInt()
                initialChangeSequence.add(priceChange)
                secretNumber = secretNumberNext
            }
            var changeSequence: ChangeSequence
            run {
                val secretNumberNext = advanceSecretNumber(secretNumber)
                val newPrice = (secretNumberNext%10).toInt()
                val priceChange = (newPrice - secretNumber.toInt()%10)
                initialChangeSequence.add(priceChange)
                secretNumber = secretNumberNext
                changeSequence = ChangeSequence(
                    initialChangeSequence[0],
                    initialChangeSequence[1],
                    initialChangeSequence[2],
                    initialChangeSequence[3]
                )
                if (changeSequence !in sequencesHitForThisMonkey) {
                    sequencesHitForThisMonkey.add(changeSequence)
                    totalBananasForEachSequence[changeSequence] = totalBananasForEachSequence.getOrElse(changeSequence){0} + newPrice
                }
            }
            repeat(2000-3){
                val secretNumberNext = advanceSecretNumber(secretNumber)
                val newPrice = (secretNumberNext%10).toInt()
                val priceChange = (newPrice - secretNumber.toInt()%10)
                changeSequence = changeSequence.addNew(priceChange)
                if(changeSequence !in sequencesHitForThisMonkey){
                    sequencesHitForThisMonkey.add(changeSequence)
                    totalBananasForEachSequence[changeSequence] = totalBananasForEachSequence.getOrElse(changeSequence){0} + newPrice
                }
                secretNumber = secretNumberNext
            }

        }

        return totalBananasForEachSequence.maxOf { (_,numBananas) ->
            numBananas
        }.toLong()
    }


    val samplesAndTargets: List<Triple<String, Long?, Long?>> = listOf(
        Triple(
            """
1
10
100
2024
""",
            37327623, null
        ),
        Triple(
            """
1
2
3
2024
""",
            null, 23
        ),
    )
    samplesAndTargets.withIndex().forEach { (index, sample) ->
        println("--example $index--")
        val (inputWithNewline, p1Target, p2Target) = sample
        val input = inputWithNewline.trim('\n')
        if (p1Target != null) {
            println(" part 1")
            var check1: Long
            val time = measureNanoTime{
                check1 = part1(input)
            }
            println(" [ms]: ${time/1_000_000.0}")
            check(check1 == p1Target) {
                println(" 1:  $check1 instead of $p1Target")
            }
        }
        if (p2Target != null) {
            println(" part 2")
            var check2: Long
            val time = measureNanoTime{
                check2 = part2(input)
            }
            println(" [ms]: ${time/1_000_000.0}")
            check(check2 == p2Target) {
                println(" 2:  $check2 instead of $p2Target")
            }
        }
        println("  passed")
    }

//
//    // Or read a large test input from the `src/Day21_test.txt` file:
//    val testInput = readInput("Day21_test")
//    check(part1(testInput) == 1)

    // download input (if needed) into day21.txt.  return list of lines
    val input = loadAndReadInput(22, 2024)
    println("--real input now--")
    println("part 1:")
    var part1Ans: Long
    val time1 = measureNanoTime{
        part1Ans = part1(input)
    }
    println("  [ms]: ${time1/1_000_000.0}")
    println("  answer: $part1Ans")
    check(part1Ans==17163502021L) //do check while refactoring

    println("part 2:")
    var part2Ans: Long
    val time2 = measureNanoTime{
        part2Ans = part2(input)
    }
    println("  [ms]: ${time2/1_000_000.0}")
    println("  answer: $part2Ans")
//    check(part2Ans==) //do check while refactoring
}


