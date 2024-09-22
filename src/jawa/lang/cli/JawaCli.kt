package jawa.lang.cli

import jawa.lang.environment.Environment
import jawa.lang.errors.ExceptionLogger
import jawa.lang.lexer.Lexer
import jawa.lang.lexer.TokenInfo
import jawa.lang.parser.Expr
import jawa.lang.parser.Parser
import jawa.lang.parser.VariableAccess
import jawa.lang.parser.VariableDeclaration
import jawa.lang.visitors.CommonExpressionVisitor
import jawa.lang.visitors.VariableDeclarationVisitor

class JawaCli {

    private val environment = Environment()
    private val client = object : CliClient {
        override fun print(string: String) {
            println(string)
        }

    }
    private val exceptionLogger = ExceptionLogger(client)

    private fun parse(tokens: List<TokenInfo>) {
        val parsedNode = Parser(tokens).parse()

        when (parsedNode) {
            is Expr -> try {
                println(parsedNode.accept(CommonExpressionVisitor()))
            } catch (e: Exception) {
                exceptionLogger.add(e, true)
            }
            is VariableDeclaration -> try {
                parsedNode.accept(VariableDeclarationVisitor(environment))
            } catch (e: Exception) {
                exceptionLogger.add(e, true)
            }
            is VariableAccess -> {
                if (environment.hasVariable(parsedNode.identifier)) {
                    println(environment.getVariable(parsedNode.identifier)!!.value)
                } else exceptionLogger.add(
                    exception = RuntimeException("Unresolved reference: ${parsedNode.identifier}"),
                    throwException = true
                )
            }
        }
    }

    fun fromString(sourceCode: String) {
        sourceCode.lines().forEach { line ->
            val tokens = Lexer(line).tokenize()
//        tokens.forEach { println(it.prettyPrint()) }
            parse(tokens)
        }
    }

    // Math test:
    // - 10 + (2 * |5 - 10|^2) => 60
    // -

    fun run() {
        exceptionLogger.clear()

        println()
        println("Jawa CLI")

        while (true) {
            print(">>> ")
            val input = readln()
            if (input.equals("exit", true)) break

            val tokens = Lexer(input).tokenize()
//            tokens.forEach { println(it.prettyPrint()) }
            parse(tokens)
        }
    }

    interface CliClient {
        /**
         * Print given [string] to console
         */
        fun print(string: String)
    }

}