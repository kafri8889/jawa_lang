package jawa.lang.util

import jawa.lang.Token

interface Transformer<T, U> {

    /**
     * Transform given [value] to [U]
     */
    fun transform(value: T): U?
}

/**
 * Transform given [Token] to math operator.
 *
 * e.g: [Token.Plus] will be transformed to `+`.
 */
class MathTokenTransformer: Transformer<Token, String> {
    override fun transform(value: Token): String? {
        return when (value) {
            Token.Plus -> "+"
            Token.Minus -> "-"
            Token.Multiply -> "*"
            Token.Divide -> "/"
            Token.Pow -> "^"
            else -> null
        }
    }
}
