@Suppress("MemberVisibilityCanBePrivate","unused")
class InfiniteGrid<T>(private val defaultValue: T) {

    private val grid = mutableMapOf<Pair<Int, Int>, T>()
    var minX: Int = 0
    var maxX: Int = 0
    var minY: Int = 0
    var maxY: Int = 0
    private var minMaxSet = false

    fun copy(): InfiniteGrid<T> {
        val ng = InfiniteGrid(defaultValue)
        ng.minX = minX
        ng.maxX = maxX
        ng.minY = minY
        ng.maxY = maxY
        ng.minMaxSet = minMaxSet
        grid.forEach{ (key, value) ->
            ng[key]=value
        }
        return ng
    }

    // Accessor to get a value from the grid. If the value is not set, return the default value.
    operator fun get(x: Int, y: Int): T {
        return grid.getOrPut(Pair(x, y)) { defaultValue }
    }
    // Accessor to get a value from the grid by coordinates (x, y) pair
    operator fun get(position: Pair<Int, Int>): T {
        return grid.getOrPut(position) { defaultValue }
    }
    // Accessor to get a value from the grid by coordinates (x, y) pair
    operator fun get(position: Int2): T {
        return grid.getOrPut(position.toPair()) { defaultValue }
    }

    // Mutator to set a value at a specific coordinate
    operator fun set(x: Int, y: Int, value: T) {
        set(Pair(x, y), value)
    }
    // Mutator to set a value at a specific coordinate
    operator fun set(position: Int2, value: T) {
        set(position.toPair(), value)
    }
    // Mutator to set a value at a specific coordinate (x, y) pair
    operator fun set(position: Pair<Int, Int>, value: T) {
        grid[position] = value
        updateBounds(position)
    }

    // Method to update the min and max bounds after a set operation
    fun updateBounds(position: Pair<Int, Int>) {
        val (x, y) = position
        if (!minMaxSet){
            minX = x
            maxX = x
            minY = y
            maxY = y
            minMaxSet = true
        }
        else {
            if (x < minX) minX = x
            if (x > maxX) maxX = x
            if (y < minY) minY = y
            if (y > maxY) maxY = y
        }
    }

    // ForEach method to iterate over the whole grid area within bounds
    fun forEach(action: (Pair<Int, Int>, T) -> Unit) {
        if (minMaxSet) {
            for (x in minX..maxX) {
                for (y in minY..maxY) {
                    val value = get(Pair(x, y))
                    action(Pair(x, y), value)
                }
            }
        }
    }

    fun print(){
        println("--- grid size: ${maxX-minX+1} , ${maxY-minY+1} ---")
        for (y in minY..maxY){
            for (x in minX..maxX){
                print(this[x, y])
            }
            println("")
        }
    }
    fun print(characterMapper: (T) -> Char) {
        println("--- grid size: ${maxX - minX+1}, ${maxY - minY+1} ---")
        for (y in minY..maxY) {
            for (x in minX..maxX) {
                print( characterMapper( this[x, y] ) )
            }
            println("")  // Move to the next line after each row
        }
    }

    fun printWithIndex(characterMapper: (Int,Int,T) -> T) {
        println("--- grid size: ${maxX - minX+1}, ${maxY - minY+1} ---")
        for (y in minY..maxY) {
            for (x in minX..maxX) {
                print( characterMapper( x, y, this[x, y] ) )
            }
            println("")  // Move to the next line after each row
        }

    }

    override fun toString(): String{
        return "InfiniteGrid(${defaultValue.toString()}){${maxX-minX+1}x${maxY-minY+1}}"
    }
}