package jawa.lang

val keywords: HashMap<String, Token> = hashMapOf(
    "fun" to Token.Fun,
    "konst" to Token.Const,
    "tetap" to Token.Immutable,
    "ubah" to Token.Mutable,
    "true" to Token.True,
    "false" to Token.False,
)
