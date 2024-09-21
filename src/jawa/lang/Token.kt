package jawa.lang

enum class Token {
    // Keyword
    Keyword,

    // Identifier (variable name, function, or class)
    Identifier,

    // Operator
    Plus,
    Minus,
    Multiply,
    Divide,
    Equals,
    And,
    Or,

    // Literal (string or num)
    Literal,

    // Separator/punctuator
    OpenParent,
    ClosedParent,
    Semicolon,

    // Other
    Comment,
    Eof,  // End of line
    NewLine;  // \n

    companion object {
        val mathOperators = listOf(Plus, Minus, Multiply, Divide)
    }
}

