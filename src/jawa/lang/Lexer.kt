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
        } while (input[currentIndex].isDigit())

        return TokenInfo(IntLiteral(num.toInt()), Token.Literal, currentLine, startPos, currentPosition)
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

        // Move pos from closing " to the string value
        currentIndex++
        currentPosition++

        return TokenInfo(StringLiteral(string), Token.Literal, currentLine, startPos, currentPosition)
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

        val token = if (id in Keywords.keywordValues) Token.Keyword else Token.Identifier

        return TokenInfo(id, token, currentLine, startPos, currentPosition)
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
                else -> {
                    var token: Token? = null

                    if (currentInput == '+' || currentInput == '-' || currentInput == '*' || currentInput == '/') {
                        token = when (currentInput) {
                            '+' -> Token.Plus
                            '-' -> Token.Minus
                            '*' -> Token.Multiply
                            '/' -> Token.Divide
                            else -> throw UnsupportedOperationException("Unsupported operator $currentInput in index $currentIndex")
                        }
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