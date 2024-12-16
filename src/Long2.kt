import kotlin.math.sqrt

@Suppress("unused")
data class Long2(var x: Long, var y: Long) {

    fun length(): Double = sqrt((x * x + y * y).toDouble())

    operator fun plus(other: Long2): Long2 = Long2(this.x + other.x, this.y + other.y)
    operator fun minus(other: Long2): Long2 = Long2(this.x - other.x, this.y - other.y)
    operator fun times(scalar: Long): Long2 = Long2(this.x * scalar, this.y * scalar)
    operator fun div(scalar: Long): Long2 {
        require(scalar != 0L) { "Cannot divide Long2 by 0" }
        return Long2(this.x / scalar, this.y / scalar)
    }

    fun  asLinearCombinationOf(a:Long2, b:Long2): Long2? {
        val aNum = this.x * b.y - this.y * b.x
        val aDenom = a.x * b.y - a.y * b.x
        val timesAOK = aNum % aDenom==0L
        val bNum = this.x * a.y - this.y * a.x
        val bDenom = a.y * b.x - a.x * b.y
        val timesBOK = bNum % bDenom==0L

        if(timesAOK && timesBOK) {
            val timesA = aNum / aDenom
            val timesB = bNum / bDenom
            return Long2(timesA, timesB)
        }
        return null
    }

    override fun toString(): String = "Long2(x=$x, y=$y)"
    fun toDouble2(): Double2 = Double2(x.toDouble(), y.toDouble())
    fun toPair(): Pair<Long,Long> = Pair(x, y)
}