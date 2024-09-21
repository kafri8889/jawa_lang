package jawa.lang

/**
 * Common visitor
 *
 * Supported operation:
 * - String concatination
 * - arithmetic operation
 * - variable declaration
 */
class CommonVisitor(private val environment: Environment): ExprVisitor<Any> {
    override fun visitBinaryExpr(expr: BinaryExpr): Any {
        val left = expr.left.accept(this)
        val right = expr.right.accept(this)

        return when (expr.operator) {
            Token.Plus -> {
                when {
                    left is Int && right is Int -> left + right
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
                    else -> throw ParseException(
                        "Cannot applied minus operator on type ${left::class.simpleName} and ${right::class.simpleName}"
                    )
                }
            }
            Token.Multiply -> {
                when {
                    left is Int && right is Int -> left * right
                    left is String && right is Int -> left.repeat(right)
                    else -> throw ParseException(
                        "Cannot applied multiply operator on type ${left::class.simpleName} and ${right::class.simpleName}"
                    )
                }
            }
            Token.Divide -> {
                when {
                    left is Int && right is Int -> left / right
                    else -> throw ParseException(
                        "Cannot applied divide operator on type ${left::class.simpleName} and ${right::class.simpleName}"
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
            else -> throw ParseException(
                "\"${expr.operator}\" operator not supported for unary expression"
            )
        }
    }

    override fun visitStringExpr(expr: StringExpr): Any {
        return expr.value
    }

    override fun visitIntExpr(expr: IntExpr): Any {
        return expr.value
    }

    override fun visitVariableDeclarationExpr(expr: VariableDeclarationExpr): Any {
        val value = expr.expr.accept(this)

        environment.setVariable(expr.identifier, value)

        return value
    }
}
