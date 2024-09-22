package jawa.lang

import jawa.lang.lexer.Token

val keywords: HashMap<String, Token> = hashMapOf(
    "fun" to Token.Fun,
    "const" to Token.Const,
    "val" to Token.Immutable,
    "var" to Token.Mutable,
    "true" to Token.True,
    "false" to Token.False,
)
