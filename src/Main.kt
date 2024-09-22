import jawa.lang.visitors.AstPrinter
import jawa.lang.Lexer
import jawa.lang.Parser

fun main() {
    val cli = JawaCli()

//    testAstPrinter()

    cli.run()
//    cli.fromString(JawaStream(File("assets/tes.jawa")).asString())
}

fun testAstPrinter() {
    println(Parser(Lexer("3 + 2 - 1").tokenize()).parse().accept(AstPrinter()))
    println(Parser(Lexer("10 + (2 * |5 - 10|^2)").tokenize()).parse().accept(AstPrinter()))
}
