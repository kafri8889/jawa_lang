import jawa.lang.cli.JawaCli
import jawa.lang.lexer.Lexer
import jawa.lang.parser.Expr
import jawa.lang.parser.Parser
import jawa.lang.visitors.AstPrinter

fun main() {
    val cli = JawaCli()

//    testAstPrinter()

    cli.run()
//    cli.fromString(JawaStream(File("assets/tes.jawa")).asString())
}

fun testAstPrinter() {
    println((Parser(Lexer("3 + 2 - 1").tokenize()).parse() as Expr).accept(AstPrinter()))
    println((Parser(Lexer("10 + (2 * |5 - 10|^2)").tokenize()).parse() as Expr).accept(AstPrinter()))
}
