package jawa.lang.errors

class ParseException(
    override val message: String?,
    override val cause: Throwable? = null
): Exception()

fun highlightError(input: String, line: Int, startPos: Int, endPos: Int): String {
    val lines = input.split('\n')
    val errorLine = lines.getOrNull(line) ?: return "Error: Line $line not found"

    val highlight = " ".repeat(startPos) + "^".repeat(endPos - startPos + 1)
    return "$errorLine\n$highlight"
}
