import kotlin.math.sqrt

@Suppress("unused")
data class Int2(val x: Int, val y: Int) {

    fun length(): Double = sqrt((x * x + y * y).toDouble())

    operator fun plus(other: Int2): Int2 = Int2(this.x + other.x, this.y + other.y)
    operator fun minus(other: Int2): Int2 = Int2(this.x - other.x, this.y - other.y)
    operator fun times(scalar: Int): Int2 = Int2(this.x * scalar, this.y * scalar)
    operator fun div(scalar: Int): Int2 {
        require(scalar != 0) { "Cannot divide Int2 by 0" }
        return Int2(this.x / scalar, this.y / scalar)
    }

    fun asLinearCombinationOf(a: Int2, b: Int2): Int2? {
        val aNum = this.x * b.y - this.y * b.x
        val aDenom = a.x * b.y - a.y * b.x
        val timesAOK = aNum % aDenom == 0
        val bNum = this.x * a.y - this.y * a.x
        val bDenom = a.y * b.x - a.x * b.y
        val timesBOK = bNum % bDenom == 0

        if (timesAOK && timesBOK) {
            val timesA = aNum / aDenom
            val timesB = bNum / bDenom
            return Int2(timesA, timesB)
        }
        return null
    }

    override fun toString(): String = "Int2(x=$x, y=$y)"
    fun toDouble2(): Double2 = Double2(x.toDouble(), y.toDouble())
    fun toPair(): Pair<Int, Int> = Pair(x, y)


    fun rotateRight(): Int2 = Int2(-y, x)
    fun rotateLeft(): Int2 = Int2(y, -x)

    companion object Directions {
        val compass4 = listOf(
            Int2(1, 0),  // East
            Int2(0, -1), // North
            Int2(-1, 0), // West
            Int2(0, 1)   // South
        )

        val neighbors8 = listOf(
            Int2(1, 0),   // East
            Int2(1, -1),  // Northeast
            Int2(0, -1),  // North
            Int2(-1, -1), // Northwest
            Int2(-1, 0),  // West
            Int2(-1, 1),  // Southwest
            Int2(0, 1),   // South
            Int2(1, 1)    // Southeast
        )
    }
}