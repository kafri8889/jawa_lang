package jawa.lang.visitors

import jawa.lang.parser.*
import jawa.lang.util.MathTokenTransformer

class AstPrinter: Expr.Visitor<Any> {

    override fun visitBinaryExpr(expr: BinaryExpr): Any {
        val left = expr.left.accept(this)
        val operator = MathTokenTransformer().transform(expr.operator)
        val right = expr.right.accept(this)

        return "($operator $left $right)"
    }

    override fun visitUnaryExpr(expr: UnaryExpr): Any {
        val operator = MathTokenTransformer().transform(expr.operator)
        val operand = expr.operand.accept(this)

        return "($operator$operand)"
    }

    override fun visitStringExpr(expr: StringExpr): Any {
        return expr.value
    }

    override fun visitBooleanExpr(expr: BooleanExpr): Any {
        return expr.value
    }

    override fun visitCharExpr(expr: CharExpr): Any {
        return expr.value
    }

    override fun visitNumberExpr(expr: NumberExpr): Any {
        return expr.value.toString()
    }

    override fun visitAbsExpr(expr: AbsExpr): Any {
        val value = expr.expression.accept(this)

        return "|$value|"
    }

}
