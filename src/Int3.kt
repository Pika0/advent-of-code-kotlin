import kotlin.math.sqrt

@Suppress("unused")
data class Int3(val x: Int, val y: Int, val z: Int) {

    fun length(): Double = sqrt((x * x + y * y + z * z).toDouble())

    operator fun plus(other: Int3): Int3 = Int3(this.x + other.x, this.y + other.y, this.z + other.z)
    operator fun minus(other: Int3): Int3 = Int3(this.x - other.x, this.y - other.y, this.z - other.z)
    operator fun times(scalar: Int): Int3 = Int3(this.x * scalar, this.y * scalar, this.z * scalar)
    operator fun div(scalar: Int): Int3 {
        require(scalar != 0) { "Cannot divide Int3 by 0" }
        return Int3(this.x / scalar, this.y / scalar, this.z / scalar)
    }

    fun asLinearCombinationOf(a: Int3, b: Int3, c: Int3): Int3? {
        val matrixDet = a.x * (b.y * c.z - b.z * c.y) - a.y * (b.x * c.z - b.z * c.x) + a.z * (b.x * c.y - b.y * c.x)

        // Check if the determinant is 0 (the vectors must be linearly independent)
        if (matrixDet != 0) {

            val detA =
                this.x * (b.y * c.z - b.z * c.y) - this.y * (b.x * c.z - b.z * c.x) + this.z * (b.x * c.y - b.y * c.x)
            val detB =
                a.x * (this.y * c.z - this.z * c.y) - a.y * (this.x * c.z - this.z * c.x) + a.z * (this.x * c.y - this.y * c.x)
            val detC =
                a.x * (b.y * this.z - b.z * this.y) - a.y * (b.x * this.z - b.z * this.x) + a.z * (b.x * this.y - b.y * this.x)

            if (detA % matrixDet != 0 || detB % matrixDet != 0 || detC % matrixDet != 0) return null
            // Compute the coefficients for the linear combination
            val coeffA = detA / matrixDet
            val coeffB = detB / matrixDet
            val coeffC = detC / matrixDet
            return Int3(coeffA, coeffB, coeffC)
        }
//        else{
//            val abCross = Int3(
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
//                    return Int3(coeffA, coeffB, 0)  // No need for coeffC since c isn't needed in this case
//                }
//            }
//        }
        return null
    }

    override fun toString(): String = "Int3(x=$x, y=$y, z=$z)"
    fun toDouble3(): Double3 = Double3(x.toDouble(), y.toDouble(), z.toDouble())
    fun toTriple(): Triple<Int, Int, Int> = Triple(x, y, z)

    companion object Directions {
        val axes6 = listOf(
            Int3(1, 0, 0),   // Positive X
            Int3(-1, 0, 0),   // Negative X
            Int3(0, 1, 0),   // Positive Y
            Int3(0, -1, 0),   // Negative Y
            Int3(0, 0, 1),   // Positive Z
            Int3(0, 0, -1)    // Negative Z
        )

        val neighbors26 = listOf(
            Int3(1, 0, 0), Int3(-1, 0, 0), Int3(0, 1, 0), // Axes
            Int3(0, -1, 0), Int3(0, 0, 1), Int3(0, 0, -1), // Axes
            Int3(1, 1, 0), Int3(-1, -1, 0), Int3(1, -1, 0), Int3(-1, 1, 0),  // XY plane diagonals
            Int3(1, 0, 1), Int3(-1, 0, -1), Int3(1, 0, -1), Int3(-1, 0, 1),  // XZ plane diagonals
            Int3(0, 1, 1), Int3(0, -1, -1), Int3(0, 1, -1), Int3(0, -1, 1),  // YZ plane diagonals
            Int3(1, 1, 1), Int3(-1, -1, -1), Int3(1, -1, 1), Int3(-1, 1, -1),  // 3D diagonals
            Int3(1, -1, -1), Int3(-1, 1, 1), Int3(1, 1, -1), Int3(-1, -1, 1)   // 3D diagonals
        )
    }
}