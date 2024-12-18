import kotlin.math.sqrt

@Suppress("unused")
data class Double2(val x: Double, val y: Double) {

    fun length(): Double = sqrt((x * x + y * y))

    operator fun plus(other: Double2): Double2 = Double2(this.x + other.x, this.y + other.y)
    operator fun minus(other: Double2): Double2 = Double2(this.x - other.x, this.y - other.y)
    operator fun times(scalar: Double): Double2 = Double2(this.x * scalar, this.y * scalar)
    operator fun div(scalar: Double): Double2 {
        require(scalar != 0.0) { "Cannot divide Double2 by 0" }
        return Double2(this.x / scalar, this.y / scalar)
    }

    fun  asLinearCombinationOf(a:Double2, b:Double2): Double2 {
        val aNum = this.x * b.y - this.y * b.x
        val aDenom = a.x * b.y - a.y * b.x
        val bNum = this.x * a.y - this.y * a.x
        val bDenom = a.y * b.x - a.x * b.y

        val timesA = aNum / aDenom
        val timesB = bNum / bDenom
        return Double2(timesA, timesB)
    }

    override fun toString(): String = "Double2(x=$x, y=$y)"
    fun toPair(): Pair<Double,Double> = Pair(x, y)
}