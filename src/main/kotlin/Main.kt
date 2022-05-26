import kotlin.system.exitProcess

fun main() {
    while (true) {
        println(
            "1. Add matrices\n" +
                    "2. Multiply matrix by a constant\n" +
                    "3. Multiply matrices\n" +
                    "4. Transpose matrix\n" +
                    "5. Calculate a determinant\n" +
                    "6. Inverse matrix\n" +
                    "0. Exit"
        )
        print("Your choice: ")
        startMenu()
    }
}

fun startMenu() {
    when (readln()) {
        "1" -> add()
        "2" -> multKf()
        "3" -> multiply()
        "4" -> transpose()
        "5" -> CalcDet(matrixInput()).showResult()
        "6" -> CalcDet(matrixInput()).showInverse()
        "0" -> exitProcess(0)
    }
}

fun transpose() {
    println(
        "1. Main diagonal\n" +
                "2. Side diagonal\n" +
                "3. Vertical line\n" +
                "4. Horizontal line"
    )
    print("Your choice: ")
    when (readln()) {
        "1" -> mainDiag()
        "2" -> sideDiag()
        "3" -> vertical()
        "4" -> horizontal()
    }
}

fun sideDiag() {
    val matrix = matrixInput()
    val transMatrix = MutableList(matrix.size) { MutableList(matrix[0].size) { 0.0 } }
    for ((xTrans, x) in (matrix[0].size - 1 downTo 0).withIndex()) {
        for (y in matrix[0].size - 1 downTo 0) {
            matrix[y][x].also { transMatrix[xTrans][matrix[0].size - 1 - y] = it }
        }
    }
    Matrix(transMatrix).show(transMatrix)

}

fun horizontal() {
    val matrix = matrixInput()
    println("The result is:")
    matrix.reversed().forEach { println(it.joinToString(" ")) }
}

fun vertical() {
    val matrix = matrixInput()
    println("The result is:")
    matrix.forEach { println(it.reversed().joinToString(" ")) }
}

fun mainDiag() {
    val matrix = matrixInput()
    val transMatrix = MutableList(matrix.size) { MutableList(matrix[0].size) { 0.0 } }
    for (x in 0 until matrix.size) {
        for (y in 0 until matrix[x].size) {
            transMatrix[y][x] = matrix[x][y]
        }
    }
    Matrix(transMatrix).show(transMatrix)
}

fun matrixInput(str: String = ""): MutableList<MutableList<Double>> {
    print("Enter size of $str matrix:")
    val (rows, cols) = readln().split(" ").map { it.toInt() }
    println("Enter $str matrix:")
    return MutableList(rows) { readln().split(" ").map { it.toDouble() }.toMutableList() }
}

fun add() {
    val first = matrixInput("first")
    val second = matrixInput("second")
    Matrix(first).add(second)
}

fun multiply() {
    val first = matrixInput("first")
    val second = matrixInput("second")
    Matrix(first).multiply(second)
}

class Matrix(private val aMatrix: MutableList<MutableList<Double>>) {

    fun add(bMatrix: MutableList<MutableList<Double>>) {
        val suMatrix = mutableListOf<MutableList<Double>>()
        if (aMatrix.size != bMatrix.size) return error()
        val size = aMatrix.size
        for (i in 0 until size) {
            if (aMatrix[i].size != bMatrix[i].size) return error()
            suMatrix.add(aMatrix[i].zip(bMatrix[i]) { xv, yv -> xv + yv }.toMutableList())
        }
        show(suMatrix)
    }

    fun show(mList: MutableList<MutableList<Double>>) {
        println("The result is:")
        mList.forEach { println(it.joinToString(" ")) }
        println()
    }

    fun multiply(bMatrix: MutableList<MutableList<Double>>) {
        if (aMatrix[0].size != bMatrix.size) return error()
        val suMatrix = MutableList(aMatrix.size) { MutableList(bMatrix[0].size) { 0.0 } }
        for (x in 0 until aMatrix.size) {
            for (y in 0 until bMatrix[0].size) {
                for (k in 0 until aMatrix[0].size)
                    suMatrix[x][y] += aMatrix[x][k] * bMatrix[k][y]
            }
        }
        show(suMatrix)
    }
}

fun multKf() {
    val matrix = matrixInput()
    println("Enter constant:")
    val kf = readln().toDouble()
    for (i in 0 until matrix.size) {
        matrix[i].forEach { print("${it * kf} ") }
        println()
    }
}

fun error() {
    println("The operation cannot be performed.")
}

class CalcDet(private val mat: MutableList<MutableList<Double>>) {
    private val nDim = mat.size

    private fun getCofactor(
        mat: MutableList<MutableList<Double>>, temp: MutableList<MutableList<Double>>,
        p: Int, q: Int, n: Int
    ) {
        var i = 0
        var j = 0

        // Looping for each element
        // of the matrix
        for (row in 0 until n) {
            for (col in 0 until n) {
                // Copying into temporary matrix
                // only those element which are
                // not in given row and column
                if (row != p && col != q) {
                    temp[i][j++] = mat[row][col]
                    // Row is filled, so increase
                    // row index and reset col index
                    if (j == n - 1) {
                        j = 0
                        i++
                    }
                }
            }
        }
    }

    /* Recursive function for finding determinant
	of matrix. n is current dimension of mat[][]. */
    private fun determinant(mat: MutableList<MutableList<Double>>, n: Int): Double {
        var d = 0.0 // Initialize result

        // Base case : if matrix
        // contains single element
        if (n == 1) return mat[0][0]

        // To store cofactors
        val temp = MutableList(nDim + 1) { MutableList(nDim + 1) { 0.0 } }

        // To store sign multiplier
        var sign = 1.0

        // Iterate for each element of first row
        for (f in 0 until n) {
            // Getting Cofactor of mat[0][f]
            getCofactor(mat, temp, 0, f, n)
            d += (sign * mat[0][f]
                    * determinant(temp, n - 1))

            // terms are to be added
            // with alternate sign
            sign = -sign
        }
        return d
    }

    private fun adjoint(
        mat: MutableList<MutableList<Double>>,
        adj: MutableList<MutableList<Double>>
    ) {
        if (nDim == 1) {
            adj[0][0] = 1.0
            return
        }
        // temp is used to store cofactors of A[][]
        var sign: Int
        val temp = MutableList(nDim) { MutableList(nDim) { 0.0 } }
        for (i in 0 until nDim) {
            for (j in 0 until nDim) {
                // Get cofactor of A[i][j]
                getCofactor(mat, temp, i, j, nDim)

                // sign of adj[j][i] positive if sum of row
                // and column indexes is even.
                sign = if ((i + j) % 2 == 0) 1 else -1

                // Interchanging rows and columns to get the
                // transpose of the cofactor matrix
                adj[j][i] = sign * determinant(temp, nDim - 1)
            }
        }
    }

    private fun inverse(mat: MutableList<MutableList<Double>>, inverse: MutableList<MutableList<Double>>): Boolean {
        // Find determinant of A[][]
        val det = determinant(mat, nDim)
        if (det == 0.0) {
            println("This matrix doesn't have an inverse.")
            return false
        }

        // Find adjoint
        val adj = MutableList(nDim) { MutableList(nDim) { 0.0 } }
        adjoint(mat, adj)

        // Find Inverse using formula "inverse(A) = adj(A)/det(A)"
        for (i in 0 until nDim) for (j in 0 until nDim) inverse[i][j] = adj[i][j] / det
        return true
    }


    fun showInverse() {
        val inv = MutableList(mat.size) { MutableList(mat.size) { 0.0 } } // To store inverse of A[][]
        if (inverse(mat, inv)) {
            inv.forEach { println(it.joinToString(" ")) }
        }

    }

    fun showResult() {
        println(
            "The result is : \n"
                    + determinant(mat, mat.size)
        )
    }
}