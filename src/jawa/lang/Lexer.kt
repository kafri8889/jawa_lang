package jawa.lang

class Lexer(private val input: String) {

    /**
     * Pointer position in [currentLine]
     */
    private var currentPosition = 0

    /**
     * Pointer position from start (`0`) to end of [input] (last index of [input])
     */
    private var currentIndex = 0
    private var currentLine = 0

    private fun parseNumberLiteral(): TokenInfo {
        var num = ""
        val startPos = currentPosition

        do {
            num += input[currentIndex]
            currentIndex++
            currentPosition++

            if (currentIndex >= input.length) break
        } while (input[currentIndex].isDigit() || input[currentIndex].equals('L', true))

        val literal: Literal<out Number> = when {
            num.endsWith("L", ignoreCase = true) -> LongLiteral(num.filter { it.isDigit() }.toLong())
            else -> IntLiteral(num.toInt())
        }

        return TokenInfo(literal, Token.Literal, currentLine, startPos, currentPosition)
    }

    private fun parseStringLiteral(): TokenInfo {
        var string = ""
        val startPos = currentPosition

        // Move pos from opening " to the string value
        currentIndex++
        currentPosition++

        while (currentIndex < input.length && input[currentIndex] != '"') {
            string += input[currentIndex]
            currentIndex++
            currentPosition++
        }

        // Move pos from closing "
        currentIndex++
        currentPosition++

        return TokenInfo(StringLiteral(string), Token.Literal, currentLine, startPos, currentPosition)
    }

    private fun parseCharLiteral(): TokenInfo {
        var string = ""
        val startPos = currentPosition

        // Move pos from opening ' to the char value
        currentIndex++
        currentPosition++

        while (currentIndex < input.length && input[currentIndex] != '\'') {
            string += input[currentIndex]
            currentIndex++
            currentPosition++
        }

        // Move pos from closing '
        currentIndex++
        currentPosition++

        if (string.length > 1) throw ParseException("Too many characters in a character literal \"$string\"")
        if (string.isBlank()) throw ParseException("Empty char literal")

        return TokenInfo(CharLiteral(string[0]), Token.Literal, currentLine, startPos, currentPosition)
    }

    private fun checkWhiteSpace(): TokenInfo? {
        val startPos = currentPosition

        currentIndex++
        currentPosition++
        if (input[currentIndex] == 'n') {
            return TokenInfo(null, Token.NewLine, currentLine, startPos, currentPosition)
        }

        return null
    }

    private fun parseIdentifier(): TokenInfo {
        var id = ""
        val startPos = currentPosition

        while (currentIndex < input.length && !input[currentIndex].isWhitespace()) {
            id += input[currentIndex]
            currentPosition++
            currentIndex++
        }

        var token = keywords[id] ?: Token.Identifier
        val value = when (token) {
            Token.True -> BooleanLiteral(true)
            Token.False -> BooleanLiteral(false)
            else -> id
        }

        token = when (token) {
            Token.True, Token.False -> Token.Literal
            else -> token
        }

        return TokenInfo(value, token, currentLine, startPos, currentPosition)
    }

    fun tokenize(): List<TokenInfo> {
        val tokens = mutableListOf<TokenInfo>()

        while (currentIndex < input.length) {
            val currentInput = input[currentIndex]

            when {
                currentInput.isLetter() -> tokens.add(parseIdentifier())
                currentInput == '\\' -> checkWhiteSpace()?.let {
                    tokens.add(it)

                    currentLine++
                    currentIndex++
                    currentPosition = 0
                }
                // Recognize comment
                input.startsWith("//", currentIndex) -> {
                    val startPos = currentPosition

                    // Skip "//"
                    currentPosition += 2
                    currentIndex += 2

                    var comment = ""
                    while (currentIndex < input.length && input[currentIndex] != '\n') {
                        comment += input[currentIndex]
                        currentPosition++
                        currentIndex++
                    }

                    tokens.add(TokenInfo(comment, Token.Comment, currentLine, startPos, currentPosition))

                    currentLine++
                    currentPosition = 0
                }
                // Recognize comment
                input.startsWith("/*", currentIndex) -> {
                    val startPos = currentPosition

                    // Skip "/*"
                    currentPosition += 2
                    currentIndex += 2

                    var comment = ""
                    while (currentIndex < input.length) {
                        if (input[currentIndex] == '*') {
                            if (currentIndex + 1 < input.length && input[currentIndex + 1] == '/') {
                                currentIndex += 2
                                currentPosition += 2
                                break
                            }
                        }

                        comment += input[currentIndex]
                        currentPosition++
                        currentIndex++
                    }

                    tokens.add(TokenInfo(comment, Token.Comment, currentLine, startPos, currentPosition))

                    currentLine++
                    currentPosition = 0
                }
                currentInput.isDigit() -> tokens.add(parseNumberLiteral())
                currentInput == '"' -> tokens.add(parseStringLiteral())
                currentInput == '\'' -> tokens.add(parseCharLiteral())
                else -> {
                    var token: Token? = null

                    if (
                        currentInput == '+' ||
                        currentInput == '-' ||
                        currentInput == '*' ||
                        currentInput == '/' ||
                        currentInput == '^' ||
                        currentInput == '&'
                    ) {
                        token = when (currentInput) {
                            '+' -> Token.Plus
                            '-' -> Token.Minus
                            '*' -> Token.Multiply
                            '/' -> Token.Divide
                            '^' -> Token.Pow
                            '&' -> Token.And
                            else -> throw UnsupportedOperationException("Unsupported operator $currentInput in index $currentIndex")
                        }
                    }

                    if (currentInput == '|') {
                        token = Token.Abs
                    }

                    if (currentInput == '(') {
                        token = Token.OpenParent
                    }

                    if (currentInput == ')') {
                        token = Token.ClosedParent
                    }

                    if (currentInput == ';') {
                        token = Token.Eof
                    }

                    if (currentInput == '=') {
                        token = Token.Equals
                    }

                    if (token != null) tokens.add(
                        TokenInfo(null, token, currentLine, currentPosition, currentPosition)
                    )

                    currentIndex++
                    currentPosition++
                }
            }
        }

        return tokens
    }
}