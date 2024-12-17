@Suppress("MemberVisibilityCanBePrivate","unused")
class InfiniteGrid3d<T>(private val defaultValue: T) {

    private val grid = mutableMapOf<Triple<Int, Int, Int>, T>()
    var minX: Int = 0
    var maxX: Int = 0
    var minY: Int = 0
    var maxY: Int = 0
    var minZ: Int = 0
    var maxZ: Int = 0
    private var minMaxSet = false

    fun copy(): InfiniteGrid3d<T> {
        val ng = InfiniteGrid3d(defaultValue)
        ng.minX = minX
        ng.maxX = maxX
        ng.minY = minY
        ng.maxY = maxY
        ng.minZ = minZ
        ng.maxZ = maxZ
        ng.minMaxSet = minMaxSet
        grid.forEach { (key, value) ->
            ng[key] = value
        }
        return ng
    }

    // Accessor to get a value from the grid. If the value is not set, return the default value.
    operator fun get(x: Int, y: Int, z: Int): T {
        return grid.getOrElse(Triple(x, y, z)) { defaultValue }
    }

    // Accessor to get a value from the grid by coordinates (x, y) pair
    operator fun get(position: Triple<Int, Int, Int>): T {
        return grid.getOrElse(position) { defaultValue }
    }

    // Accessor to get a value from the grid by coordinates (x, y) pair
    operator fun get(position: Int3): T {
        return grid.getOrElse(position.toTriple()) { defaultValue }
    }

    // Mutator to set a value at a specific coordinate
    operator fun set(x: Int, y: Int, z: Int, value: T) {
        set(Triple(x, y, z), value)
    }

    // Mutator to set a value at a specific coordinate
    operator fun set(position: Int3, value: T) {
        set(position.toTriple(), value)
    }

    // Mutator to set a value at a specific coordinate (x, y) pair
    operator fun set(position: Triple<Int, Int, Int>, value: T) {
        grid[position] = value
        updateBounds(position)
    }

    // Method to update the min and max bounds after a set operation
    fun updateBounds(position: Triple<Int, Int, Int>) {
        val (x, y, z) = position
        if (!minMaxSet) {
            minX = x
            maxX = x
            minY = y
            maxY = y
            minZ = z
            maxZ = z
            minMaxSet = true
        } else {
            if (x < minX) minX = x
            if (x > maxX) maxX = x
            if (y < minY) minY = y
            if (y > maxY) maxY = y
            if (y < minZ) minZ = z
            if (y > maxZ) maxZ = z
        }
    }

    // ForEach method to iterate over the whole grid area within bounds
    fun forEach(action: (Triple<Int, Int, Int>, T) -> Unit) {
        if (minMaxSet) {
            for (x in minX..maxX) {
                for (y in minY..maxY) {
                    for (z in minZ..maxZ) {
                        val value = get(Triple(x, y, z))
                        action(Triple(x, y, z), value)
                    }
                }
            }
        }
    }

    fun printAll() = printAllWithIndex { _, _, _, c -> c }

    fun printAll(characterMapper: (T) -> T) {
        printAllWithIndex { _, _, _, c -> characterMapper(c) }
    }

    fun printAllWithIndex(characterMapper: (Int, Int, Int, T) -> T) {
        println("--- grid size: ${maxX - minX + 1} , ${maxY - minY + 1} , ${maxZ - minZ + 1} ---")
        for (z in minZ..maxZ) {
            println("  layer $z (from $minZ to $maxZ)")
            for (y in minY..maxY) {
                print("    ")
                for (x in minX..maxX) {
                    print(characterMapper(x, y, z, this[x, y, z]))
                }
                println("")
            }
        }
    }

    fun print2dSlice(z:Int) = print2dSlice(z) { _, _, _, c -> c }
    fun print2dSlice(z:Int, characterMapper: (T) -> T) {
        print2dSlice(z) { _, _, _, c -> characterMapper(c) }
    }
    fun print2dSlice(z:Int, characterMapper: (Int, Int, Int, T) -> T) {
        println("--- grid size: ${maxX - minX + 1} , ${maxY - minY + 1} , ${maxZ - minZ + 1} ---")
        println("slice:  layer $z (from $minZ to $maxZ)")
        for (y in minY..maxY) {
            print("    ")
            for (x in minX..maxX) {
                print(characterMapper(x, y, z, this[x, y, z]))
            }
            println("")
        }
    }

    override fun toString(): String {
        return "InfiniteGrid3d(${defaultValue.toString()}){${maxX - minX + 1}x${maxY - minY + 1}x${maxZ - minZ + 1}}"
    }
}