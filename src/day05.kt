fun main() {
    fun changeIsValid(rules: HashMap<Int, MutableList<Int>>, changePages: List<Int>): Boolean {
        //it's just a triple loop.
        // for each page in the change list,
        // check the associated rules(pages that should be AFTER)
        //  against the PREVIOUS pages, looking for rejects
        changePages.withIndex().forEach{ (index,page)->
            if (page in rules){
                val laterPages = rules[page]!!
                laterPages.forEach { laterPage ->
                    for (earlierPage in changePages.subList(0,index)){
                        if (laterPage==earlierPage) return false
                    }
                }
            }
        }
        return true
    }

    fun part1(input: String): Int {
        val (rulesLines, changesLines) = input.split("\n\n").map{it.lines()}
        val rules = HashMap<Int, MutableList<Int>>()

        rulesLines.forEach { ruleText ->
            val (first, second) = ruleText.split("|").map { it.toInt() }
            val l: MutableList<Int> = rules.getOrPut(first){ mutableListOf() }
            l.add(second)
        }
        return changesLines.sumOf{ changes ->
            val pages = changes.split(",").map{it.toInt()}
            if (changeIsValid(rules, pages))  pages[pages.size/2] else 0
        }
    }

    fun sortToValid(rules: HashMap<Int, MutableList<Int>>, changePages: List<Int>): List<Int> {
        val fixedChanges = changePages.sortedWith { a, b ->
            if ((a in rules) && (b in rules[a]!!)) {
                1
            }
            else if (b in rules && a in rules[b]!!) {
                -1
            }
            else 0

        }
        return fixedChanges
    }

    fun part2(input: String): Int {
        val (rulesLines, changesLines) = input.split("\n\n").map{it.lines()}
        val rules = HashMap<Int, MutableList<Int>>()

        rulesLines.forEach { ruleText ->
            val (first, second) = ruleText.split("|").map { it.toInt() }
            val l: MutableList<Int> = rules.getOrPut(first){ mutableListOf() }
            l.add(second)
        }
        return changesLines.sumOf{ changes ->
            val pages = changes.split(",").map{it.toInt()}
            if (changeIsValid(rules, pages)){
                0
            } else {
                val fixed = sortToValid(rules, pages)
                fixed[fixed.size / 2]
            }
        }
    }

    // Test if implementation meets criteria from the description, like:
    val sampleInput="""47|53
97|13
97|61
97|47
75|29
61|13
75|53
29|13
97|29
53|29
61|53
97|53
61|29
47|13
75|47
97|75
47|61
75|61
47|29
75|13
53|13

75,47,61,53,29
97,61,53,29,13
75,29,13
75,97,47,61,53
61,13,29
97,13,75,29,47"""
    val check1 = part1(sampleInput)
    check ( check1 == 143) {
        println("check1=$check1")
    }
    val check2 = part2(sampleInput)
    check(check2 == 123){
        println("check2=$check2")
    }



    // download input (if needed) into day05.txt.  return list of lines
    val input = loadAndReadInput(5, 2024)
    part1(input).println()
    part2(input).println()
}
