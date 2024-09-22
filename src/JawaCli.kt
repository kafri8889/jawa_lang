import jawa.lang.*
import jawa.lang.visitors.CommonVisitor

class JawaCli {

    private val environment = Environment()

    fun fromString(sourceCode: String) {
        val tokens = Lexer(sourceCode).tokenize()
        tokens.forEach { println(it.prettyPrint()) }
        println("out: ${Parser(tokens).parse().accept(CommonVisitor(environment))}")
    }

    // Math test:
    // - 10 + (2 * |5 - 10|^2) => 60
    // -

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