import jawa.lang.*

class JawaCli {

    private val environment = Environment()

    fun fromString(sourceCode: String) {
        val tokens = Lexer(sourceCode).tokenize()
        tokens.forEach { println(it.prettyPrint()) }
        println("out: ${Parser(tokens).parse().accept(CommonVisitor(environment))}")
    }

    fun run() {
        while (true) {
            print(">>> ")
            val input = readln()
            if (input.equals("exit", true)) break

            val tokens = Lexer(input).tokenize()
//            tokens.forEach { println(it.prettyPrint()) }
            println("${Parser(tokens).parse().accept(CommonVisitor(environment))}")
//            println("tes: ${MathParser(tokens).parse().accept(CommonVisitor())}")
        }
    }

}