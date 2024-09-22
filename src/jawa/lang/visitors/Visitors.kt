package jawa.lang.visitors

import jawa.lang.*
import kotlin.math.abs
import kotlin.math.pow

/**
 * Common visitor
 *
 * Supported operation:
 * - String concatination
 * - arithmetic operation
 * - variable declaration
 */
class CommonVisitor(private val environment: Environment): Expr.Visitor<Any> {
    override fun visitBinaryExpr(expr: BinaryExpr): Any {
        val left = expr.left.accept(this)
        val right = expr.right.accept(this)

        return when (expr.operator) {
            Token.Plus -> {
                when {
                    left is Int && right is Int -> left + right
                    left is Int && right is Long -> left + right
                    left is Int && right is Float -> left + right
                    left is Int && right is Double -> left + right

                    left is Long && right is Int -> left + right
                    left is Long && right is Long -> left + right
                    left is Long && right is Float -> left + right
                    left is Long && right is Double -> left + right

                    left is Float && right is Int -> left + right
                    left is Float && right is Long -> left + right
                    left is Float && right is Float -> left + right
                    left is Float && right is Double -> left + right

                    left is Double && right is Int -> left + right
                    left is Double && right is Long -> left + right
                    left is Double && right is Float -> left + right
                    left is Double && right is Double -> left + right

                    left is Char && right is Int -> left + right

                    left is Boolean && right is Boolean -> (if (left) 1 else 0 + if (right) 1 else 0) >= 1

                    left is String && right is Int -> left + right
                    left is String && right is String -> left + right
                    else -> throw ParseException(
                        "Cannot applied plus operator on type ${left::class.simpleName} and ${right::class.simpleName}"
                    )
                }
            }
            Token.Minus -> {
                when {
                    left is Int && right is Int -> left - right
                    left is Int && right is Long -> left - right
                    left is Int && right is Float -> left - right
                    left is Int && right is Double -> left - right

                    left is Long && right is Int -> left - right
                    left is Long && right is Long -> left - right
                    left is Long && right is Float -> left - right
                    left is Long && right is Double -> left - right

                    left is Float && right is Int -> left - right
                    left is Float && right is Long -> left - right
                    left is Float && right is Float -> left - right
                    left is Float && right is Double -> left - right

                    left is Double && right is Int -> left - right
                    left is Double && right is Long -> left - right
                    left is Double && right is Float -> left - right
                    left is Double && right is Double -> left - right

                    left is Char && right is Int -> left - right

                    left is Boolean && right is Boolean -> (if (left) 1 else 0 - if (right) 1 else 0) >= 1

                    else -> throw ParseException(
                        "Cannot applied minus operator on type ${left::class.simpleName} and ${right::class.simpleName}"
                    )
                }
            }
            Token.Multiply -> {
                when {
                    left is Int && right is Int -> left * right
                    left is Int && right is Long -> left * right
                    left is Int && right is Float -> left * right
                    left is Int && right is Double -> left * right

                    left is Long && right is Int -> left * right
                    left is Long && right is Long -> left * right
                    left is Long && right is Float -> left * right
                    left is Long && right is Double -> left * right

                    left is Float && right is Int -> left * right
                    left is Float && right is Long -> left * right
                    left is Float && right is Float -> left * right
                    left is Float && right is Double -> left * right

                    left is Double && right is Int -> left * right
                    left is Double && right is Long -> left * right
                    left is Double && right is Float -> left * right
                    left is Double && right is Double -> left * right

                    left is Char && right is Int -> left.toString().repeat(right)
                    left is Int && right is Char -> right.toString().repeat(left)

                    left is Boolean && right is Boolean -> left && right

                    left is String && right is Int -> left.repeat(right)

                    else -> throw ParseException(
                        "Cannot applied multiply operator on type ${left::class.simpleName} and ${right::class.simpleName}"
                    )
                }
            }
            Token.Divide -> {
                when {
                    left is Int && right is Int -> left / right
                    left is Int && right is Long -> left / right
                    left is Int && right is Float -> left / right
                    left is Int && right is Double -> left / right

                    left is Long && right is Int -> left / right
                    left is Long && right is Long -> left / right
                    left is Long && right is Float -> left / right
                    left is Long && right is Double -> left / right

                    left is Float && right is Int -> left / right
                    left is Float && right is Long -> left / right
                    left is Float && right is Float -> left / right
                    left is Float && right is Double -> left / right

                    left is Double && right is Int -> left / right
                    left is Double && right is Long -> left / right
                    left is Double && right is Float -> left / right
                    left is Double && right is Double -> left / right

                    else -> throw ParseException(
                        "Cannot applied divide operator on type ${left::class.simpleName} and ${right::class.simpleName}"
                    )
                }
            }
            Token.Pow -> {
                when {
                    left is Int && right is Int -> left.toDouble().pow(right).toInt()
                    left is Int && right is Long -> left.toDouble().pow(right.toDouble()).toInt()
                    left is Int && right is Float -> left.toDouble().pow(right.toDouble()).toInt()
                    left is Int && right is Double -> left.toDouble().pow(right).toInt()

                    left is Long && right is Int -> left.toDouble().pow(right.toDouble()).toLong()
                    left is Long && right is Long -> left.toDouble().pow(right.toDouble()).toLong()
                    left is Long && right is Float -> left.toDouble().pow(right.toDouble()).toLong()
                    left is Long && right is Double -> left.toDouble().pow(right.toDouble()).toLong()

                    left is Float && right is Int -> left.pow(right)
                    left is Float && right is Long -> left.pow(right.toFloat())
                    left is Float && right is Float -> left.pow(right.toFloat())
                    left is Float && right is Double -> left.pow(right.toFloat())

                    left is Double && right is Int -> left.toDouble().pow(right)
                    left is Double && right is Long -> left.toDouble().pow(right.toDouble())
                    left is Double && right is Float -> left.toDouble().pow(right.toDouble())
                    left is Double && right is Double -> left.toDouble().pow(right.toDouble())

                    else -> throw ParseException(
                        "Cannot applied pow operator on type ${left::class.simpleName} and ${right::class.simpleName}"
                    )
                }
            }
            Token.And -> {
                when {
                    left is Int && right is Int -> left and right

                    left is Long && right is Long -> left and right

                    else -> throw ParseException(
                        "Cannot applied and operator on type ${left::class.simpleName} and ${right::class.simpleName}"
                    )
                }
            }
            else -> throw UnsupportedOperationException("Operator ${expr.operator} not supported for binary evaluation")
        }
    }

    override fun visitUnaryExpr(expr: UnaryExpr): Any {
        val operand = expr.operand.accept(this)

        return when (expr.operator) {
            Token.Plus -> {
                when {
                    operand is Int -> operand
                    else -> throw ParseException(
                        "Cannot applied \"${expr.operator}\" operator with operand \"${operand}\""
                    )
                }
            }
            Token.Minus -> {
                when {
                    operand is Int -> -operand
                    else -> throw ParseException(
                        "Cannot applied \"${expr.operator}\" operator with operand \"${operand}\""
                    )
                }
            }
            Token.Exclamation -> {
                when {
                    operand is Boolean -> !operand
                    else -> throw ParseException(
                        "Cannot applied \"${expr.operator}\" operator with operand \"${operand}\""
                    )
                }
            }
            else -> throw ParseException(
                "\"${expr.operator}\" operator not supported for unary expression"
            )
        }
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
        return expr.value
    }

    override fun visitAbsExpr(expr: AbsExpr): Any {
        return when (val value = expr.expression.accept(this)) {
            is Int -> abs(value)
            else -> throw ParseException("Cannot apply abs operator to type ${value::class.simpleName}")
        }
    }

    override fun visitVariableDeclarationExpr(expr: VariableDeclarationExpr): Any {
        val value = expr.expr.accept(this)

        environment.setVariable(expr.identifier, value)

        return value
    }
}
