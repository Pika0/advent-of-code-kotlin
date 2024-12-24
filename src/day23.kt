import kotlin.system.measureNanoTime

fun main() {
    fun part1(input: String): Long {
        val lines = input.lines()
        val connectionPairs = mutableSetOf<Pair<String,String>>()
        val connectionsFromEach = mutableMapOf<String,MutableSet<String>>()
        val computerNames = mutableSetOf<String>()
        var total = 0L
        lines.forEach { line ->
            val (c1,c2) = line.split("-")
            //look for a common connection
            val c1Connections = connectionsFromEach.getOrElse(c1){emptySet()}
            val c2Connections = connectionsFromEach.getOrElse(c2){emptySet()}
            val common = c1Connections.intersect(c2Connections)
            common.forEach { c3 ->
                if(c1[0]=='t' || c2[0]=='t' || c3[0]=='t') total += 1
            }

            //now add this connection
            connectionsFromEach.getOrPut(c1){mutableSetOf()}.add(c2)
            connectionsFromEach.getOrPut(c2){mutableSetOf()}.add(c1)
            computerNames.add(c1)
            computerNames.add(c2)
            connectionPairs.add(c1 to c2)
            connectionPairs.add(c2 to c1)
        }
        return total
    }

    fun part2(input: String): String {
        val lines = input.lines()
        val connectionPairs = mutableSetOf<Pair<String,String>>()
        val connectionsFromEach = mutableMapOf<String,MutableSet<String>>()
        val lanParties = mutableSetOf<MutableSet<String>>()
        lines.forEach { line ->
            val (c1,c2) = line.split("-")

            //now add this connection
            connectionsFromEach.getOrPut(c1){mutableSetOf()}.add(c2)
            connectionsFromEach.getOrPut(c2){mutableSetOf()}.add(c1)
            connectionPairs.add(c1 to c2)
            connectionPairs.add(c2 to c1)
        }
        connectionsFromEach.forEach{ (c1, c1Connections) ->
            println("$c1: $c1Connections")
            val usedConnections = mutableSetOf<String>()

            lanParties.forEach { lanParty ->
                if(lanParty.all { c2 -> c2 in c1Connections }){
                    usedConnections += lanParty
                    lanParty.add(c1)
                }
            }
            val remainingConnections = (c1Connections - usedConnections).toMutableSet()
            //make new ≥2-person lan parties, adding others if they also connect.
            while(remainingConnections.size>0){
                val c2 = remainingConnections.first()
                remainingConnections.remove(c2)
                val newParty = mutableSetOf(c1,c2)
                remainingConnections.forEach { c3 ->
                    val c3Connections = connectionsFromEach.getOrElse(c3){emptySet()}
                    if(newParty.all { it in c3Connections }){
                        newParty.add(c3)
                    }
                }
                remainingConnections -= newParty
                lanParties.add(newParty)
            }


        }
        println("lan parties:")
        val parties3OrMore = lanParties.filter{it.size>=3}
        val partiesAndPasswords = parties3OrMore.map{ party ->
            val password = party.toList().sorted().joinToString(",")
            party to password
        }
        partiesAndPasswords.sortedBy { it.second }.forEach { (party, password) ->
            println("  $party $password") }
        return partiesAndPasswords.maxByOrNull { it.second.length }!!.second
    }


    val samplesAndTargets: List<Triple<String, Long?, String?>> = listOf(
        Triple(
            """
kh-tc
qp-kh
de-cg
ka-co
yn-aq
qp-ub
cg-tb
vc-aq
tb-ka
wh-tc
yn-cg
kh-ub
ta-co
de-co
tc-td
tb-wq
wh-td
ta-ka
td-qp
aq-cg
wq-ub
ub-vc
de-ta
wq-aq
wq-vc
wh-yn
ka-de
kh-ta
co-tc
wh-qp
tb-vc
td-yn

""",
            7, "co,de,ka,ta"
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
            var check2: String
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
//    // Or read a large test input from the `src/Day23_test.txt` file:
//    val testInput = readInput("Day23_test")
//    check(part1(testInput) == 1)

    // download input (if needed) into day23.txt.  return list of lines
    val input = loadAndReadInput(23, 2024)
    println("--real input now--")
    println("part 1:")
    var part1Ans: Long
    val time1 = measureNanoTime{
        part1Ans = part1(input)
    }
    println("  [ms]: ${time1/1_000_000.0}")
    println("  answer: $part1Ans")
//    check(part1Ans==) //do check while refactoring

    println("part 2:")
    var part2Ans: String
    val time2 = measureNanoTime{
        part2Ans = part2(input)
    }
    println("  [ms]: ${time2/1_000_000.0}")
    println("  answer: $part2Ans")
//    check(part2Ans==) //do check while refactoring
}
