package jawa.lang

interface Node

interface Literal<T>: Node {
    val value: T
}

data class IntLiteral(
    override val value: Int
): Literal<Int>

data class DoubleLiteral(
    override val value: Double
): Literal<Double>

data class StringLiteral(
    override val value: String
): Literal<String>

data class BooleanLiteral(
    override val value: Boolean
): Literal<Boolean>

data class VariableDeclaration(
    /**
     * Variable name (identifier)
     */
    val name: String,
    val expression: Expr,
    /**
     * Variable type [Keywords.immutableVariable] or [Keywords.mutableVariable]
     */
    val type: Token = Token.Keyword
): Node