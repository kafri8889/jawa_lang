package jawa.lang

sealed class Expr: Node {
    abstract fun <T> accept(visitor: ExprVisitor<T>): T
}

interface ExprVisitor<T> {
    fun visitBinaryExpr(expr: BinaryExpr): T
    fun visitUnaryExpr(expr: UnaryExpr): T
    fun visitStringExpr(expr: StringExpr): T
    fun visitIntExpr(expr: IntExpr): T
    fun visitVariableDeclarationExpr(expr: VariableDeclarationExpr): T
}

class BinaryExpr(
    val left: Expr,
    val operator: Token,
    val right: Expr
): Expr() {
    override fun <T> accept(visitor: ExprVisitor<T>): T {
        return visitor.visitBinaryExpr(this)
    }
}

class UnaryExpr(
    val operator: Token,
    val operand: Expr
): Expr() {
    override fun <T> accept(visitor: ExprVisitor<T>): T {
        return visitor.visitUnaryExpr(this)
    }
}

class StringExpr(val value: String): Expr() {
    override fun <T> accept(visitor: ExprVisitor<T>): T {
        return visitor.visitStringExpr(this)
    }
}

class IntExpr(val value: Int): Expr() {
    override fun <T> accept(visitor: ExprVisitor<T>): T {
        return visitor.visitIntExpr(this)
    }
}

class VariableDeclarationExpr(
    /**
     * Variable name
     */
    val identifier: String,
    /**
     * Variable type ([Keywords.immutableVariable] or [Keywords.mutableVariable])
     */
    val type: String,
//    /**
//     * Variable data type
//     */
//    val dataType: String,
    val expr: Expr
): Expr() {
    override fun <T> accept(visitor: ExprVisitor<T>): T {
        return visitor.visitVariableDeclarationExpr(this)
    }
}
