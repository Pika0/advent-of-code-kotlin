import org.apache.commons.math3.util.ArithmeticUtils.pow
import kotlin.math.abs
import kotlin.system.measureNanoTime


interface Signal {
    val name:String
    var value: Boolean?
    val ready: Boolean
        get() = value != null
    val usedBy: MutableSet<Gate>
    fun addUser(newUser:Gate){
        usedBy.add(newUser)
        if(ready) newUser.update()
    }
    fun update(){
        usedBy.forEach { it.update() }
    }
    var bitPosition: Double
}
data class Input(override val name:String, override var value:Boolean?): Signal{
    override val usedBy: MutableSet<Gate> = mutableSetOf()
    override var bitPosition: Double = 0.0

    override fun toString() = "$name: $value"
}
data class Gate(override val name:String, val type:String, val in1:Signal, val in2:Signal): Signal {
    init {
        if (type !in listOf("AND","OR","XOR")) throw IllegalArgumentException("unknown type ($type) for gate")
        in1.addUser(this)
        in2.addUser(this)
    }
    override fun toString() = "$name: $value (${in1.name} $type ${in2.name})"

    override val usedBy: MutableSet<Gate> = mutableSetOf()
    override var bitPosition: Double = 0.0
    override var value: Boolean? = null
    override fun update() {
        if (!ready && in1.ready && in2.ready) {
            value = when (type) {
                "AND" -> in1.value!! && in2.value!!
                "OR" -> in1.value!! || in2.value!!
                "XOR" -> in1.value!! xor in2.value!!
                else -> throw IllegalStateException("unknown type ($type) for gate")
            }
            super.update() //propagate update to dependants
        }
    }
}

fun main() {
    fun part1(input: String): Long {
        val signalsByName = mutableMapOf<String,Signal>()
        val (ins, gates) = input.split("\n\n")
        ins.lines().forEach{line->
            val name = line.substringBefore(": ")
            val s = Input( name, line.substringAfter(": ")=="1" )
            signalsByName[name]=s
        }
        var gateLines = gates.lines()
        while(gateLines.isNotEmpty()) {
            gateLines = gateLines.filter { line ->
                val parts = line.split(" ")
                //example: x00 AND y00 -> z00
                val in1Name = parts[0]
                if (in1Name !in signalsByName) return@filter true
                val in2Name = parts[2]
                if (in2Name !in signalsByName) return@filter true

                val in1 = signalsByName[in1Name]!!
                val in2 = signalsByName[in2Name]!!
                val type = parts[1]
                val name = parts[4]
                val s = Gate(name, type, in1, in2)
                s.update()
                signalsByName[name] = s
                false
            }
        }

//        signalsByName.toList().sortedBy { (name,_) -> name }.forEach {
//            println(it.second)
//        }
        return signalsByName
            .filter { (name,_) -> name[0]=='z' }
            .toList().sumOf{ (name,signal) ->
            if(signal.value!!)
                pow(2L,(name.substring(1).toLong()))
            else
                0L
        }
    }

    fun part2(input: String): String {
        val signalsByName = mutableMapOf<String, Signal>()
        val xSignals = mutableSetOf<Input>()
        val ySignals = mutableSetOf<Input>()
        val zSignals = mutableSetOf<Gate>()
        val middleSignals = mutableSetOf<Gate>()
        val (ins, gates) = input.split("\n\n")
        ins.lines().forEach { line ->
            val name = line.substringBefore(": ")
            val s = Input(name, line.substringAfter(": ") == "1")
            signalsByName[name] = s
            if(name[0]=='x') xSignals.add(s)
            else if(name[0]=='y') ySignals.add(s)
        }
        var gateLines = gates.lines()
        while (gateLines.isNotEmpty()) {
            gateLines = gateLines.filter { line ->
                val parts = line.split(" ")
                //example: x00 AND y00 -> z00
                val in1Name = parts[0]
                if (in1Name !in signalsByName) return@filter true
                val in2Name = parts[2]
                if (in2Name !in signalsByName) return@filter true

                val in1 = signalsByName[in1Name]!!
                val in2 = signalsByName[in2Name]!!
                val type = parts[1]
                val name = parts[4]
                val s = Gate(name, type, in1, in2)
                s.update()
                signalsByName[name] = s
                if(name[0]=='z') zSignals.add(s)
                else middleSignals.add(s)
                false
            }
        }
        (xSignals+ySignals+zSignals).forEach { signal ->
            val bitNum = signal.name.substring(1).toDouble()
            signal.bitPosition = bitNum
        }
        repeat(10){
            middleSignals.forEach { gate ->
                val ps = gate.usedBy.map{it.bitPosition}.toMutableList()
                ps.add(gate.in1.bitPosition)
                ps.add(gate.in2.bitPosition)
                gate.bitPosition = ps.average()
            }
        }
        middleSignals.forEach { gate ->
            val ps = gate.usedBy.map{abs(it.bitPosition-gate.bitPosition)}.toMutableList()
            ps.add(abs(gate.in1.bitPosition-gate.bitPosition))
            ps.add(abs(gate.in2.bitPosition-gate.bitPosition))
            val averageDistance = ps.average()
            print("${gate.name}: $averageDistance")
        }
//        fun tryAdd(x:Long, y:Long){
//            //unready everything
//            signalsByName.forEach { (_, signal) ->
//                signal.value = null
//            }
//            //assign new values
//            xSignals.forEach { signal ->
//                val bitNum = signal.name.substring(1).toInt()
//                signal.value = (x shr bitNum) and 1 == 1L
//                signal.update()
//            }
//            ySignals.forEach { signal ->
//                val bitNum = signal.name.substring(1).toInt()
//                signal.value = (y shr bitNum) and 1 == 1L
//                signal.update()
//            }
//            //updates propagate upward, so everything should be ready now.
//
//        }
//        fun readZs():Long {
//            return signalsByName
//                .filter { (name, _) -> name[0] == 'z' }
//                .toList().sumOf { (name, signal) ->
//                    if (signal.value!!)
//                        pow(2L, (name.substring(1).toLong()))
//                    else
//                        0L
//                }
//        }
//
//        signalsByName.toList().sortedBy { (name, _) -> name }.forEach {
//            println(it.second)
//        }
//        return readZs()
        return "TODO"
    }


    val samplesAndTargets: List<Triple<String, Long?, String?>> = listOf(
        Triple(
            """
x00: 1
x01: 1
x02: 1
y00: 0
y01: 1
y02: 0

x00 AND y00 -> z00
x01 XOR y01 -> z01
x02 OR y02 -> z02

""",
            4, null
        ),
        Triple(
            """
x00: 1
x01: 0
x02: 1
x03: 1
x04: 0
y00: 1
y01: 1
y02: 1
y03: 1
y04: 1

ntg XOR fgs -> mjb
y02 OR x01 -> tnw
kwq OR kpj -> z05
x00 OR x03 -> fst
tgd XOR rvg -> z01
vdt OR tnw -> bfw
bfw AND frj -> z10
ffh OR nrd -> bqk
y00 AND y03 -> djm
y03 OR y00 -> psh
bqk OR frj -> z08
tnw OR fst -> frj
gnj AND tgd -> z11
bfw XOR mjb -> z00
x03 OR x00 -> vdt
gnj AND wpb -> z02
x04 AND y00 -> kjc
djm OR pbm -> qhw
nrd AND vdt -> hwm
kjc AND fst -> rvg
y04 OR y02 -> fgs
y01 AND x02 -> pbm
ntg OR kjc -> kwq
psh XOR fgs -> tgd
qhw XOR tgd -> z09
pbm OR djm -> kpj
x03 XOR y03 -> ffh
x00 XOR y04 -> ntg
bfw OR bqk -> z06
nrd XOR fgs -> wpb
frj XOR qhw -> z04
bqk OR frj -> z07
y03 OR x01 -> nrd
hwm AND bqk -> z03
tgd XOR rvg -> z12
tnw OR pbm -> gnj
""",
            2024, null
        ),
        Triple(
            """
x00: 0
x01: 1
x02: 0
x03: 1
x04: 0
x05: 1
y00: 0
y01: 0
y02: 1
y03: 1
y04: 0
y05: 1

x00 AND y00 -> z05
x01 AND y01 -> z02
x02 AND y02 -> z01
x03 AND y03 -> z03
x04 AND y04 -> z04
x05 AND y05 -> z00

""",
            null, "z00,z01,z02,z05"
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
//    // Or read a large test input from the `src/Day24_test.txt` file:
//    val testInput = readInput("Day24_test")
//    check(part1(testInput) == 1)

    // download input (if needed) into day24.txt.  return list of lines
    val input = loadAndReadInput(24, 2024)
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
