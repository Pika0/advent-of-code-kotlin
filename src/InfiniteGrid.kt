@Suppress("MemberVisibilityCanBePrivate","unused")
class InfiniteGrid<T>(private val defaultValue: T) {

    private val grid = mutableMapOf<Pair<Int, Int>, T>()
    private var minX: Int? = null
    private var maxX: Int? = null
    private var minY: Int? = null
    private var maxY: Int? = null

    // Accessor to get a value from the grid. If the value is not set, return the default value.
    operator fun get(x: Int, y: Int): T {
        return grid.getOrDefault(Pair(x, y), defaultValue)
    }
    // Accessor to get a value from the grid by coordinates (x, y) pair
    operator fun get(position: Pair<Int, Int>): T {
        return grid.getOrDefault(position, defaultValue)
    }

    // Mutator to set a value at a specific coordinate
    operator fun set(x: Int, y: Int, value: T) {
        set(Pair(x, y), value)
    }
    // Mutator to set a value at a specific coordinate (x, y) pair
    operator fun set(position: Pair<Int, Int>, value: T) {
        grid[position] = value
        updateBounds(position)
    }

    // Method to update the min and max bounds after a set operation
    fun updateBounds(position: Pair<Int, Int>) {
        val (x, y) = position
        if (minX == null || x < minX!!) minX = x
        if (maxX == null || x > maxX!!) maxX = x
        if (minY == null || y < minY!!) minY = y
        if (maxY == null || y > maxY!!) maxY = y
    }

    // ForEach method to iterate over the whole grid area within bounds
    fun forEach(action: (Pair<Int, Int>, T) -> Unit) {
        if (minX != null && maxX != null && minY != null && maxY != null) {
            for (x in minX!!..maxX!!) {
                for (y in minY!!..maxY!!) {
                    val value = get(Pair(x, y))
                    action(Pair(x, y), value)
                }
            }
        }
    }
}