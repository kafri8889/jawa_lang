package jawa.lang.environment

class Environment {

    private val variables = hashMapOf<String, Variable?>()

    fun setVariable(name: String, value: Variable?) {
        variables[name] = value
    }

    fun getVariable(name: String): Variable? = variables[name]

    fun hasVariable(name: String): Boolean = variables.containsKey(name)

}