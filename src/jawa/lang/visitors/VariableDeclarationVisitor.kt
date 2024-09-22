package jawa.lang.visitors

import jawa.lang.environment.Environment
import jawa.lang.environment.Variable
import jawa.lang.errors.ParseException
import jawa.lang.parser.VariableDeclaration

class VariableDeclarationVisitor(private val environment: Environment): VariableDeclaration.Visitor<Any> {
    override fun visitVariableDeclaration(declaration: VariableDeclaration): Any {
        val value = declaration.expr.accept(CommonExpressionVisitor())

        if (environment.hasVariable(declaration.identifier)) {
            if (environment.getVariable(declaration.identifier)!!.isMutable) {
                environment.setVariable(declaration.identifier, Variable(value, true))
            } else throw ParseException("Immutable variable \"${declaration.identifier}\" cannot be reassigned")
        } else environment.setVariable(declaration.identifier, Variable(value, declaration.isMutable))

        return Any()
    }
}