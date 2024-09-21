package jawa.lang

data class Keyword(
    val identifier: String,
    val token: Token = Token.Keyword
)

object Keywords {

    val function = Keyword(
        identifier = "fun"
    )

    /**
     * Constant variable
     */
    val const = Keyword(
        identifier = "konst"
    )

    val immutableVariable = Keyword(
        identifier = "tetap"
    )

    val mutableVariable = Keyword(
        identifier = "ubah"
    )

    val values = listOf(
        function,
        const,
        immutableVariable,
        mutableVariable
    )

    val keywordValues = values.map { it.identifier }

}
