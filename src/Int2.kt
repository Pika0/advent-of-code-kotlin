import kotlin.math.sqrt

@Suppress("unused")
data class Int2(var x: Int, var y: Int) {

    fun length(): Double = sqrt((x * x + y * y).toDouble())

    operator fun plus(other: Int2): Int2 = Int2(this.x + other.x, this.y + other.y)
    operator fun minus(other: Int2): Int2 = Int2(this.x - other.x, this.y - other.y)
    operator fun times(scalar: Int): Int2 = Int2(this.x * scalar, this.y * scalar)
    operator fun div(scalar: Int): Int2 {
        require(scalar != 0) { "Cannot divide Int2 by 0" }
        return Int2(this.x / scalar, this.y / scalar)
    }

    override fun toString(): String = "Int2(x=$x, y=$y)"
    fun toPair(): Pair<Int,Int> = Pair(x, y)
}