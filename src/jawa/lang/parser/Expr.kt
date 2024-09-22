package jawa.lang.parser

import jawa.lang.lexer.Token

sealed class Expr: Node {
    abstract fun <T> accept(visitor: Visitor<T>): T

    interface Visitor<T> {
        fun visitBinaryExpr(expr: BinaryExpr): T
        fun visitUnaryExpr(expr: UnaryExpr): T
        fun visitStringExpr(expr: StringExpr): T
        fun visitBooleanExpr(expr: BooleanExpr): T
        fun visitCharExpr(expr: CharExpr): T
        fun visitNumberExpr(expr: NumberExpr): T
        fun visitAbsExpr(expr: AbsExpr): T
    }
}

class BinaryExpr(
    val left: Expr,
    val operator: Token,
    val right: Expr
): Expr() {
    override fun <T> accept(visitor: Visitor<T>): T {
        return visitor.visitBinaryExpr(this)
    }
}

class UnaryExpr(
    val operator: Token,
    val operand: Expr
): Expr() {
    override fun <T> accept(visitor: Visitor<T>): T {
        return visitor.visitUnaryExpr(this)
    }
}

class StringExpr(val value: String): Expr() {
    override fun <T> accept(visitor: Visitor<T>): T {
        return visitor.visitStringExpr(this)
    }
}

class NumberExpr(val value: Number): Expr() {
    override fun <T> accept(visitor: Visitor<T>): T {
        return visitor.visitNumberExpr(this)
    }
}

class BooleanExpr(val value: Boolean): Expr() {
    override fun <T> accept(visitor: Visitor<T>): T {
        return visitor.visitBooleanExpr(this)
    }
}

class CharExpr(val value: Char): Expr() {
    override fun <T> accept(visitor: Visitor<T>): T {
        return visitor.visitCharExpr(this)
    }
}

class AbsExpr(val expression: Expr) : Expr() {
    override fun <R> accept(visitor: Visitor<R>): R {
        return visitor.visitAbsExpr(this)
    }
}
