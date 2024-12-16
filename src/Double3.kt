import kotlin.math.abs
import kotlin.math.sqrt

@Suppress("unused")
data class Double3(var x: Double, var y: Double, var z: Double) {

    fun length(): Double = sqrt((x * x + y * y + z * z))

    operator fun plus(other: Double3): Double3 = Double3(this.x + other.x, this.y + other.y, this.z + other.z)
    operator fun minus(other: Double3): Double3 = Double3(this.x - other.x, this.y - other.y, this.z - other.z)
    operator fun times(scalar: Double): Double3 = Double3(this.x * scalar, this.y * scalar, this.z * scalar)
    operator fun div(scalar: Double): Double3 {
        require(abs(scalar) > 0.0000001) { "Cannot divide Double3 by value too close to 0: $scalar" }
        return Double3(this.x / scalar, this.y / scalar, this.z / scalar)
    }

    fun asLinearCombinationOf(a: Double3, b: Double3, c: Double3): Double3? {
        val matrixDet = a.x * (b.y * c.z - b.z * c.y) - a.y * (b.x * c.z - b.z * c.x) + a.z * (b.x * c.y - b.y * c.x)

        // Check if the determinant is 0 (the vectors must be linearly independent)
        if (abs(matrixDet) > 0.0000001) {

            val detA =
                this.x * (b.y * c.z - b.z * c.y) - this.y * (b.x * c.z - b.z * c.x) + this.z * (b.x * c.y - b.y * c.x)
            val detB =
                a.x * (this.y * c.z - this.z * c.y) - a.y * (this.x * c.z - this.z * c.x) + a.z * (this.x * c.y - this.y * c.x)
            val detC =
                a.x * (b.y * this.z - b.z * this.y) - a.y * (b.x * this.z - b.z * this.x) + a.z * (b.x * this.y - b.y * this.x)

            // Compute the coefficients for the linear combination
            val coeffA = detA / matrixDet
            val coeffB = detB / matrixDet
            val coeffC = detC / matrixDet
            return Double3(coeffA, coeffB, coeffC)
        }
//        else{
//            val abCross = Double3(
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
//                    return Double3(coeffA, coeffB, 0)  // No need for coeffC since c isn't needed in this case
//                }
//            }
//        }
        return null
    }

    override fun toString(): String = "Double3(x=$x, y=$y, z=$z)"
    fun toTriple(): Triple<Double, Double, Double> = Triple(x, y, z)
}