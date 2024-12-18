import kotlin.math.sqrt

@Suppress("unused")
data class Long3(val x: Long, val y: Long, val z: Long) {

    fun length(): Double = sqrt((x * x + y * y + z * z).toDouble())

    operator fun plus(other: Long3): Long3 = Long3(this.x + other.x, this.y + other.y, this.z + other.z)
    operator fun minus(other: Long3): Long3 = Long3(this.x - other.x, this.y - other.y, this.z - other.z)
    operator fun times(scalar: Long): Long3 = Long3(this.x * scalar, this.y * scalar, this.z * scalar)
    operator fun div(scalar: Long): Long3 {
        require(scalar != 0L) { "Cannot divide Long3 by 0" }
        return Long3(this.x / scalar, this.y / scalar, this.z / scalar)
    }

    operator fun div(scalar: Double): Double3 {
        require(scalar != 0.0) { "Cannot divide Long3 by 0" }
        return Double3(this.x.toDouble() / scalar, this.y.toDouble() / scalar, this.z.toDouble() / scalar)
    }

    fun asLinearCombinationOf(a: Long3, b: Long3, c: Long3): Long3? {
        val matrixDet = a.x * (b.y * c.z - b.z * c.y) - a.y * (b.x * c.z - b.z * c.x) + a.z * (b.x * c.y - b.y * c.x)

        // Check if the determinant is 0 (the vectors must be linearly independent)
        if (matrixDet != 0L) {

            val detA =
                this.x * (b.y * c.z - b.z * c.y) - this.y * (b.x * c.z - b.z * c.x) + this.z * (b.x * c.y - b.y * c.x)
            val detB =
                a.x * (this.y * c.z - this.z * c.y) - a.y * (this.x * c.z - this.z * c.x) + a.z * (this.x * c.y - this.y * c.x)
            val detC =
                a.x * (b.y * this.z - b.z * this.y) - a.y * (b.x * this.z - b.z * this.x) + a.z * (b.x * this.y - b.y * this.x)

            if (detA % matrixDet != 0L || detB % matrixDet != 0L || detC % matrixDet != 0L) return null
            // Compute the coefficients for the linear combination
            val coeffA = detA / matrixDet
            val coeffB = detB / matrixDet
            val coeffC = detC / matrixDet
            return Long3(coeffA, coeffB, coeffC)
        }
//        else{
//            val abCross = Long3(
//                a.y * b.z - a.z * b.y,
//                a.z * b.x - a.x * b.z,
//                a.x * b.y - a.y * b.x
//            )
//
//            val targetProjection = this.x * abCross.x + this.y * abCross.y + this.z * abCross.z
//
//            // If projection is zero, the target lies in the span of (a, b) and is still solvable
//            if (targetProjection == 0) {
//                // Use a similar method for two-dimensional solutions (solving for coefficients of a and b)
//                val abDet = a.x * b.y - a.y * b.x
//                if (abDet != 0) {
//                    val coeffA = (this.x * b.y - this.y * b.x) / abDet
//                    val coeffB = (a.x * this.y - a.y * this.x) / abDet
//                    return Long3(coeffA, coeffB, 0)  // No need for coeffC since c isn't needed in this case
//                }
//            }
//        }
        return null
    }

    override fun toString(): String = "Long3(x=$x, y=$y, z=$z)"
    fun toDouble3(): Double3 = Double3(x.toDouble(), y.toDouble(), z.toDouble())
    fun toTriple(): Triple<Long, Long, Long> = Triple(x, y, z)
}