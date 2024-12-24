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
    fun usedByShort():String {
        return usedBy.sortedWith( compareBy<Gate> { it.bitPosition}.thenBy { it.name } ).map {
            buildString {
                append(it.name)
                append("(")
                append(it.shortType())
                append(")")
            }
        }.joinToString(",")
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
    override val usedBy: MutableSet<Gate> = mutableSetOf() //nullibility error if this line is after 'init'
    init {
        if (type !in listOf("AND","OR","XOR")) throw IllegalArgumentException("unknown type ($type) for gate")
        in1.addUser(this)
        in2.addUser(this)
    }
    override fun toString() = "$name: $value (${in1.name} $type ${in2.name})"
    fun shortType():String{
        return when(type){
            "AND" -> "&"
            "OR" -> "|"
            "XOR" -> "^"
            else -> throw IllegalStateException("unknown type ($type) for gate")
        }
    }

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

        val nameSwaps = mutableMapOf<String,String>(
//            "fsh" to "gds", makes a loop
            //fsh helps make z22
//            "fsh" to "z21" incorrect
            //both gate for z21 has something else connected
            //the inputs for z21 should be nsp and tqh
            // the gates using those  are currently outputting  vqn(&) and gds(^)
            "z21" to "gds",
            // z15 is missing a source: snp is missing entirely. cpp is there instead.
//            "snp" to "cpp",   Nope. actually, z15 was on the wrong gate.
            "z15" to "fph",
            // fix z30?
            "wrk" to "jrs",
            "cqk" to "z34",
        )
        nameSwaps.keys.toList().forEach { keyName ->
            val otherName = nameSwaps[keyName]!!
            nameSwaps[otherName]=keyName
        }

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
                var name = parts[4]
                if( name in nameSwaps) name = nameSwaps[name]!!

                val s = Gate(name, type, in1, in2)
                s.update()
                signalsByName[name] = s
                if(name[0]=='z') zSignals.add(s)
                else middleSignals.add(s)
                false
            }
        }
        //this bitPosition and error method was a good idea, but
        // the swapped wires are actually close to each other. :(
        (xSignals+ySignals+zSignals).forEach { signal ->
            val bitNum = signal.name.substring(1).toDouble()
            signal.bitPosition = bitNum
        }
        repeat(20){
            middleSignals.forEach { gate ->
                val ps = gate.usedBy.map{it.bitPosition}.toMutableList()
                ps.add(gate.in1.bitPosition)
                ps.add(gate.in2.bitPosition)
                gate.bitPosition = ps.median()!!
            }
        }
        var errorsAndSignals = (middleSignals+zSignals).map { gate ->
            val positionErrors = gate.usedBy.map{abs(it.bitPosition-gate.bitPosition)}.toMutableList()
            positionErrors.add(abs(gate.in1.bitPosition-gate.bitPosition))
            positionErrors.add(abs(gate.in2.bitPosition-gate.bitPosition))
            val averageError = positionErrors.average()
//            println("${gate.name} error: $averageError")
            averageError to gate
        }
        errorsAndSignals = errorsAndSignals.sortedBy { -it.first }

        errorsAndSignals.take(20).forEach { (averageError, gate) ->
            println("${gate.name} error: $averageError")
            val ps = gate.usedBy.map{it.bitPosition}.toMutableList()
            ps.add(gate.in1.bitPosition)
            ps.add(gate.in2.bitPosition)
            println("  positions: $ps")
        }

        if(errorsAndSignals.size<8){
            return errorsAndSignals
                .take(4)
                .map{it.second.name}
                .sorted()
                .joinToString(",")
        }
//        else{
//            return errorsAndSignals
//                .take(8)
//                .map{it.second.name}
//                .sorted()
//                .joinToString(",")
//        }
        //incorrect:    ccp,fph,fsh,ksw,nsj,ptm,swb,z45
        //incorrect: bsb,fph,fsh,kmk,nns,swb,vqn,z45
        val printedSignals = mutableSetOf<Signal>()
//        val unprintedSignals = (middleSignals.toMutableList())
        var i=0
        var i0 = i.toString().padStart(2,'0')
        while("x$i0" in signalsByName){
            val xSig = signalsByName["x$i0"]!!
            val ySig = signalsByName["y$i0"]!!
            print("x$i0 y$i0 :")
            printedSignals.add(xSig)
            printedSignals.add(ySig)
            val shared = (xSig.usedBy.intersect(ySig.usedBy))
            for (signal in shared.sortedBy { it.type }){
                print(" [${signal.name}(${signal.shortType()})>${signal.usedByShort()}]")
                printedSignals.add(signal)
            }
            val other = (xSig.usedBy + ySig.usedBy) - shared
            for (signal in other){
                if(signal.in1 in printedSignals && signal.in2 in printedSignals) {
                    print(" other: [${signal.in1.name}&${signal.in2.name} > ${signal.name} (> ${signal.usedByShort()})]")
                    printedSignals.add(signal)
                }
                else{
                    print(" other: [${signal.in1.name}&${signal.in2.name} > ${signal.name} (> ${signal.usedByShort()})]")
                }
                throw IllegalStateException("What even is this section?")
            }
            println()
//            print("    then: ")
            val nextRow = (xSig.usedBy + ySig.usedBy).flatMap { it.usedBy } - (xSig.usedBy + ySig.usedBy)
            for (signal in nextRow.sortedBy { it.usedBy.size }){
                if(signal.in1 in printedSignals && signal.in2 in printedSignals) {
                    if (signal.name[0] == 'z') println("           ${signal.in1.name}&${signal.in2.name} > ${signal.name}")
                    else println("  [${signal.in1.name}${signal.shortType()}${signal.in2.name} > ${signal.name} (> ${signal.usedByShort()})]")
                    printedSignals.add(signal)
                }
                else if (i!=0){
                    println(" signal w/o source:  [${signal.in1.name}&${signal.in2.name} > ${signal.name} (> ${signal.usedByShort()})]")
                    printedSignals.add(signal)
                }
            }

//            println()

            i+=1
            i0 = i.toString().padStart(2,'0')
        }
        println("signals complete.")
        for (signal in (signalsByName.values - printedSignals)){
            when{
                signal is Gate -> println(" leftover signal: [${signal.in1.name}&${signal.in2.name} > ${signal.name} > ${signal.usedByShort()}]")
                else -> println(" leftover signal: ${signal.name} > ${signal.usedByShort()}]")
            }
        }

        return nameSwaps.keys.sorted().joinToString(",")


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

fun List<Double>.median(): Double? {
    if (this.isEmpty()) return null // Return null for an empty list

    val sortedValues = this.sorted()
    val size = sortedValues.size

    return if (size % 2 == 1) {
        sortedValues[size / 2]
    } else {
        val mid1 = sortedValues[size / 2 - 1]
        val mid2 = sortedValues[size / 2]
        (mid1 + mid2) / 2
    }
}