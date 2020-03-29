package com.example.chessboardgame

class Cell(
    var x: Int = 0,
    var y: Int = 0,
    var dist: Int = 0,
    var assignImage: Boolean = false,
    var parent: Cell? = null
) {

    constructor(x: Int, y: Int, dist: Int, parent: Cell?) : this() {
        this.x = x
        this.y = y
        this.dist = dist
        this.parent = parent
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false
        val node = o as Cell
        if (x != node.x) return false
        return if (y != node.y) false else dist == node.dist
    }

    override fun hashCode(): Int {
        var result = x
        result = 31 * result + y
        result = 31 * result + dist
        return result
    }
}

