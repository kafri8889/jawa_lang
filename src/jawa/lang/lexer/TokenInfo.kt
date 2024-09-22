package jawa.lang.lexer

data class TokenInfo(
    val value: Any?,
    /**
     * Token type
     */
    val token: Token,
    /**
     * Line position
     */
    val line: Int,
    /**
     * Start position of the value
     */
    val startPos: Int,
    /**
     * Start position of the value
     */
    val endPos: Int
) {
    fun prettyPrint(): String {
        val valueStr = value?.toString() ?: "null"
        return """
        |Token:
        |  Type:     $token
        |  Value:    $valueStr
        |  Line:     $line
        |  StartPos: $startPos
        |  EndPos:   $endPos
        """.trimMargin()
    }
}
