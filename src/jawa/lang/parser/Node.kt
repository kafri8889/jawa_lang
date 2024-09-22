package jawa.lang.parser

interface Node

interface Literal<T>: Node {
    val value: T
}

data class IntLiteral(
    override val value: Int
): Literal<Int>

data class LongLiteral(
    override val value: Long
): Literal<Long>

data class FloatLiteral(
    override val value: Float
): Literal<Float>

data class DoubleLiteral(
    override val value: Double
): Literal<Double>

data class StringLiteral(
    override val value: String
): Literal<String>

data class CharLiteral(
    override val value: Char
): Literal<Char>

data class BooleanLiteral(
    override val value: Boolean
): Literal<Boolean>

data class VariableAccess(
    val identifier: String
): Node
