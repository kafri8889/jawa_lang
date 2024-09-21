package jawa.lang

class Parser(private val tokenInfos: List<TokenInfo>) {
    private var currentIndex = 0

    /**
     * Check if current token is start of variable declaration
     *
     * How:
     * - if token start with keyword [Keywords.immutableVariable] or [Keywords.mutableVariable] and followed
     *   by [Token.Identifier], then
     */
    fun isVariableDeclaration(): Boolean {
        if (
            tokenInfos.getOrNull(currentIndex)?.token == Token.Keyword &&
            tokenInfos.getOrNull(currentIndex + 1)?.token == Token.Identifier &&
            tokenInfos.getOrNull(currentIndex + 2)?.token == Token.Equals
        ) return true

        return false
    }

    /**
     * Check if current token is start of math expression
     *
     * How:
     * - if token start with number followed by operator
     * - if token start with operator and then number
     */
    fun isMathExpression(): Boolean {
        if (
            (tokenInfos.getOrNull(currentIndex)?.token == Token.Literal &&
            tokenInfos.getOrNull(currentIndex + 1)?.token in Token.mathOperators) ||
            (tokenInfos.getOrNull(currentIndex)?.token in Token.mathOperators &&
            tokenInfos.getOrNull(currentIndex + 1)?.token == Token.Literal)
        ) return true

        return false
    }

    fun parse(): Expr {
        return when {
            isVariableDeclaration() -> parseVariableDeclaration()
            isMathExpression() -> parseMathExpression()
            else -> throw ParseException(
                "Declaration not supported"
            )
        }
    }

    // todo: hapus
    fun parseVariableDeclaration(): Expr {
        var variableType = ""
        var variableName = ""

        if (tokenInfos.getOrNull(currentIndex)?.token == Token.Keyword) {
            variableType = tokenInfos[currentIndex].value.toString()

            if (variableType != Keywords.immutableVariable.identifier && variableType != Keywords.mutableVariable.identifier) {
                throw ParseException(
                    "Illegal keyword \"${variableType}\" for variable declaration"
                )
            }

            currentIndex++
        }

        if (tokenInfos.getOrNull(currentIndex)?.token == Token.Identifier) {
            variableName = tokenInfos[currentIndex].value.toString()

            currentIndex++
        }

        if (tokenInfos.getOrNull(currentIndex)?.token == Token.Equals) currentIndex++

        if (isMathExpression()) {
            return VariableDeclarationExpr(variableName, variableType, parseMathExpression())
        }

        return VariableDeclarationExpr(variableName, variableType, parseMathExpression())
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
        var node = factor()
        while (currentIndex < tokenInfos.size) {
            val tokenInfo = tokenInfos[currentIndex]

            when {
                tokenInfo.token == Token.Divide || tokenInfo.token == Token.Multiply -> {
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
            Token.Minus -> {
                currentIndex++
                UnaryExpr(tokenInfo.token, factor())
            }
            Token.Literal -> {
                currentIndex++
                when (tokenInfo.value) {
                    is IntLiteral -> IntExpr(tokenInfo.value.value)
                    else -> throw ParseException("Illegal literal \"${tokenInfo.value}\" in line ${tokenInfo.line} at position ${tokenInfo.startPos} until ${tokenInfo.endPos}")
                }
            }
            else -> throw ParseException("Illegal token \"${tokenInfo.token}\" in line ${tokenInfo.line} at position ${tokenInfo.startPos} until ${tokenInfo.endPos}")
        }
    }
}

class MathParser(private val tokenInfos: List<TokenInfo>) {
    private var currentIndex = 0

    fun printTree(node: Node): String {
        return StringBuilder().apply {
            when (node) {
                is IntLiteral -> append(node.value)
                is UnaryExpr -> {
                    append("(")
                    append(node.operator)
                    append(printTree(node.operand))
                    append(")")
                }

                is BinaryExpr -> {
                    append("(")
                    append(node.operator)
                    append(" ")
                    append(printTree(node.left))
                    append(" ")
                    append(printTree(node.right))
                    append(")")
                }
            }
        }.toString()
    }

    fun parse(): Expr {
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
        var node = factor()
        while (currentIndex < tokenInfos.size) {
            val tokenInfo = tokenInfos[currentIndex]

            when {
                tokenInfo.token == Token.Divide || tokenInfo.token == Token.Multiply -> {
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

        return when {
            tokenInfo.token == Token.OpenParent -> {
                currentIndex++ // Open braced
                val node = expr()
                currentIndex++ // Close braced
                node
            }
            tokenInfo.token == Token.Minus -> {
                currentIndex++
                UnaryExpr(tokenInfo.token, factor())
            }
            tokenInfo.token == Token.Literal -> {
                currentIndex++
                when (tokenInfo.value) {
                    is IntLiteral -> IntExpr(tokenInfo.value.value)
                    else -> throw ParseException("Illegal literal \"${tokenInfo.value}\" in line ${tokenInfo.line} at position ${tokenInfo.startPos} until ${tokenInfo.endPos}")
                }
            }
            else -> throw ParseException("Illegal token \"${tokenInfo.token}\" in line ${tokenInfo.line} at position ${tokenInfo.startPos} until ${tokenInfo.endPos}")
        }
    }
}