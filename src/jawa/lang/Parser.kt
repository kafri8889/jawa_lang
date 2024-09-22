package jawa.lang

class Parser(private val tokenInfos: List<TokenInfo>) {
    private var currentIndex = 0

    /**
     * Check if current token is start of math expression
     *
     * How:
     * - if token start with number followed by operator
     * - if token start with operator and then number
     */
    private fun isMathExpression(): Boolean {
        if (
            ((tokenInfos.getOrNull(currentIndex)?.token == Token.Literal &&
            tokenInfos.getOrNull(currentIndex + 1)?.token in Token.mathOperators)) ||
            ((tokenInfos.getOrNull(currentIndex)?.token in Token.mathOperators &&
            tokenInfos.getOrNull(currentIndex + 1)?.token == Token.Literal)) ||
            tokenInfos.getOrNull(currentIndex)?.token == Token.OpenParent ||
            tokenInfos.getOrNull(currentIndex)?.token == Token.Abs
        ) return true

        return false
    }

    fun parse(): Expr {
        return parseMathExpression()
        return when {
            isMathExpression() -> parseMathExpression()
            else -> throw ParseException(
                "Declaration not supported"
            )
        }
    }

    /**
     * Parse arithmetic
     */
    private fun parseMathExpression(): Expr {
        return expr()
    }

    private fun expr(): Expr {
        var node = term()
        while (currentIndex < tokenInfos.size) {
            val tokenInfo = tokenInfos[currentIndex]

            when {
                tokenInfo.token == Token.Plus || tokenInfo.token == Token.Minus -> {
                    currentIndex++
                    node = BinaryExpr(node, tokenInfo.token, term())
                }
                else -> return node
            }
        }

        return node
    }

    private fun term(): Expr {
        var node = power()
        while (currentIndex < tokenInfos.size) {
            val tokenInfo = tokenInfos[currentIndex]

            when {
                tokenInfo.token.match(Token.Divide, Token.Multiply) -> {
                    currentIndex++
                    node = BinaryExpr(node, tokenInfo.token, power())
                }
                else -> return node
            }
        }

        return node
    }

    private fun power(): Expr {
        var node = factor()
        while (currentIndex < tokenInfos.size) {
            val tokenInfo = tokenInfos[currentIndex]

            when {
                tokenInfo.token.match(Token.Pow) -> {
                    currentIndex++
                    node = BinaryExpr(node, tokenInfo.token, factor())
                }
                else -> return node
            }
        }

        return node
    }

    private fun factor(): Expr {
        val tokenInfo = tokenInfos[currentIndex]

        return when (tokenInfo.token) {
            Token.OpenParent -> {
                currentIndex++ // Open braced
                val node = expr()
                currentIndex++ // Close braced
                node
            }
            Token.Abs -> {
                currentIndex++ // Open abs
                val node = expr()
                currentIndex++ // Close abs
                AbsExpr(node)
            }
            Token.Minus -> {
                currentIndex++
                UnaryExpr(tokenInfo.token, factor())
            }
            Token.Literal -> {
                currentIndex++
                when (tokenInfo.value) {
                    is IntLiteral -> NumberExpr(tokenInfo.value.value)
                    is LongLiteral -> NumberExpr(tokenInfo.value.value)
                    is CharLiteral -> CharExpr(tokenInfo.value.value)
                    is BooleanLiteral -> BooleanExpr(tokenInfo.value.value)
                    else -> throw ParseException("Illegal literal \"${tokenInfo.value}\" in line ${tokenInfo.line} at position ${tokenInfo.startPos} until ${tokenInfo.endPos}")
                }
            }
            else -> throw ParseException("Illegal token \"${tokenInfo.token}\" in line ${tokenInfo.line} at position ${tokenInfo.startPos} until ${tokenInfo.endPos}")
        }
    }
}
