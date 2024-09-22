package jawa.lang.parser

class VariableDeclaration(
    /**
     * Variable name
     */
    val identifier: String,
    /**
     * Variable type (mutable or immutable)
     */
    val isMutable: Boolean,
    /**
     * Variable data type
     */
    val dataType: String,
    val expr: Expr
): Node {
    interface Visitor<T> {
        fun visitVariableDeclaration(declaration: VariableDeclaration): T
    }

    fun accept(visitor: Visitor<Any>) {
        visitor.visitVariableDeclaration(this)
    }
}
