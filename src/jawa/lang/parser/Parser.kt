package jawa.lang.parser

import jawa.lang.errors.ParseException
import jawa.lang.lexer.Token
import jawa.lang.lexer.TokenInfo

class Parser(private val tokenInfos: List<TokenInfo>) {
    private var currentIndex = 0
    private var currentTokenInfo = tokenInfos[0]

    /**
     * Get next token after [currentIndex]
     */
    private fun advance() {
        if (++currentIndex >= tokenInfos.size) return
        currentTokenInfo = tokenInfos[currentIndex]
    }

    /**
     * Get next token after [currentIndex] without move to the next token
     */
    private fun lookahead(offset: Int = 1): TokenInfo? {
        return tokenInfos.getOrNull(currentIndex + offset)
    }

    /**
     * Consume token and check if current token is [token] and advance it, if not, throw error
     */
    private fun consume(token: Token) {
        if (currentTokenInfo.token == token) {
            advance()
            return
        }

        throw ParseException("Expected token $token, but found ${currentTokenInfo.token}")
    }

    /**
     * `identifier = Expr`
     */
    private fun isVariableDeclarationType1(): Boolean {
        return currentTokenInfo.token == Token.Identifier && lookahead()?.token == Token.Equals
    }

    /**
     * `identifier: Type = Expr`
     */
    private fun isVariableDeclarationType2(): Boolean {
        return currentTokenInfo.token == Token.Identifier &&
                lookahead()?.token == Token.Colon &&
                lookahead(2)?.token == Token.Identifier &&
                lookahead(3)?.token == Token.Equals
    }

    /**
     * `(val/var) identifier = Expr`
     */
    private fun isVariableDeclarationType3(): Boolean {
        return (currentTokenInfo.token == Token.Mutable || currentTokenInfo.token == Token.Immutable) &&
                lookahead()?.token == Token.Identifier &&
                lookahead(2)?.token == Token.Equals
    }

    /**
     * `(val/var) identifier: Type = Expr`
     */
    private fun isVariableDeclarationType4(): Boolean {
        return currentTokenInfo.token.match(Token.Mutable, Token.Immutable) &&
                lookahead()?.token == Token.Identifier &&
                lookahead(2)?.token == Token.Colon &&
                lookahead(3)?.token == Token.Identifier &&
                lookahead(4)?.token == Token.Equals
    }

    /**
     * Check if current token is start of math expression
     *
     * How:
     * - if token start with number followed by operator
     * - if token start with operator and then number
     */
    private fun isMathExpression(): Boolean {
        return currentTokenInfo.token == Token.Literal ||
               currentTokenInfo.token == Token.OpenParent ||
               currentTokenInfo.token == Token.Abs ||
               currentTokenInfo.token == Token.Minus ||
               currentTokenInfo.token in Token.mathOperators
    }

    /**
     * Check if current token is start of variable declaration
     *
     * How:
     * - if token start with identifier
     * - if token start with mutable variable keyword or immutable variable keyword
     */
    private fun isVariableDeclaration(): Boolean {
        return isVariableDeclarationType1() ||
               isVariableDeclarationType2() ||
               isVariableDeclarationType3() ||
               isVariableDeclarationType4()
    }

    fun parse(): Node {
        return when {
            isVariableDeclaration() -> parseVariableDeclaration()
            isMathExpression() -> parseMathExpression()
            currentTokenInfo.token == Token.Identifier -> VariableAccess(currentTokenInfo.value?.toString() ?: "")
            else -> throw ParseException(
                "Declaration not supported"
            )
        }
    }

    private fun parseVariableDeclaration(): Node {
        var identifier = ""
        var isMutable = true
        var dataType = ""
        var expr: Expr? = null

        when {
            isVariableDeclarationType1() -> {
                identifier = currentTokenInfo.value.toString()
                consume(Token.Identifier)
                consume(Token.Equals)
                expr = parseMathExpression()
            }
            isVariableDeclarationType2() -> {
                identifier = currentTokenInfo.value.toString()
                consume(Token.Identifier)
                consume(Token.Colon)
                dataType = currentTokenInfo.value.toString()
                consume(Token.Identifier)
                consume(Token.Equals)
                expr = parseMathExpression()
            }
            isVariableDeclarationType3() -> {
                if (currentTokenInfo.token.match(Token.Mutable, Token.Immutable)) {
                    // Consume
                    isMutable = currentTokenInfo.token == Token.Mutable
                    advance()
                }

                identifier = currentTokenInfo.value.toString()
                consume(Token.Identifier)
                consume(Token.Equals)
                expr = parseMathExpression()
            }
            isVariableDeclarationType4() -> {
                if (currentTokenInfo.token.match(Token.Mutable, Token.Immutable)) {
                    // Consume
                    isMutable = currentTokenInfo.token == Token.Mutable
                    advance()
                }

                identifier = currentTokenInfo.value.toString()
                consume(Token.Identifier)
                consume(Token.Colon)
                dataType = currentTokenInfo.value.toString()
                consume(Token.Identifier)
                consume(Token.Equals)
                expr = parseMathExpression()
            }
        }

        if (expr == null) throw ParseException("Expecting an expression")

        return VariableDeclaration(identifier, isMutable, dataType, expr)
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
            when {
                currentTokenInfo.token.match(Token.Minus, Token.Plus) -> {
                    val operator = currentTokenInfo.token
                    advance()
                    node = BinaryExpr(node, operator, term())
                }
                else -> return node
            }
        }

        return node
    }

    private fun term(): Expr {
        var node = power()
        while (currentIndex < tokenInfos.size) {
            when {
                currentTokenInfo.token.match(Token.Divide, Token.Multiply) -> {
                    val operator = currentTokenInfo.token
                    advance()
                    node = BinaryExpr(node, operator, power())
                }
                else -> return node
            }
        }

        return node
    }

    private fun power(): Expr {
        var node = factor()
        while (currentIndex < tokenInfos.size) {
            when {
                currentTokenInfo.token.match(Token.Pow) -> {
                    val operator = currentTokenInfo.token
                    advance()
                    node = BinaryExpr(node, operator, factor())
                }
                else -> return node
            }
        }

        return node
    }

    private fun factor(): Expr {
        return when (currentTokenInfo.token) {
            Token.OpenParent -> {
                advance() // Open braced
                val node = expr()
                consume(Token.ClosedParent) // Ensure to consume the closing parenthesis
                node
            }
            Token.Abs -> {
                advance() // Open abs
                val node = expr()
                consume(Token.Abs) // Ensure to consume the closing abs
                AbsExpr(node)
            }
            Token.Minus -> {
                advance()
                UnaryExpr(Token.Minus, factor())
            }
            Token.Literal -> {
                val node = when (currentTokenInfo.value) {
                    is IntLiteral -> NumberExpr((currentTokenInfo.value as IntLiteral).value)
                    is LongLiteral -> NumberExpr((currentTokenInfo.value as LongLiteral).value)
                    is CharLiteral -> CharExpr((currentTokenInfo.value as CharLiteral).value)
                    is BooleanLiteral -> BooleanExpr((currentTokenInfo.value as BooleanLiteral).value)
                    is StringLiteral -> StringExpr((currentTokenInfo.value as StringLiteral).value)
                    else -> throw ParseException(
                        "Illegal literal \"${currentTokenInfo.value}\" in line ${currentTokenInfo.line} " +
                                "at position ${currentTokenInfo.startPos} until ${currentTokenInfo.endPos}"
                    )
                }
                advance()
                node
            }
            else -> throw ParseException(
                "Illegal token \"${currentTokenInfo.token}\" in line ${currentTokenInfo.line} " +
                        "at position ${currentTokenInfo.startPos} until ${currentTokenInfo.endPos}"
            )
        }
    }
}
