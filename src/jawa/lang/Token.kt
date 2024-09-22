package jawa.lang

enum class Token {
    // Keyword
    Fun,
    Const,
    Mutable,
    Immutable,
    True,
    False,

    // Identifier (variable name, function, or class)
    Identifier,

    // Operator
    Plus,
    Minus,
    Multiply,
    Divide,
    Equals,
    Pow,
    Abs,
    And,
    Or,
    Exclamation,

    // Literal (string, num, boolean, etc)
    Literal,

    // Separator/punctuator
    OpenParent,
    ClosedParent,
    Semicolon,

    // Other
    Comment,
    Eof,  // End of line
    NewLine;  // \n

    /**
     * Return `true` if this token match any of [other]
     */
    fun match(vararg other: Token): Boolean {
        return this in other
    }

    companion object {
        val mathOperators = listOf(Plus, Minus, Multiply, Divide, Pow)
    }
}

