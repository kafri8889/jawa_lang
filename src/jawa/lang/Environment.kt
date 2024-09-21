package jawa.lang

class Environment {

    private val variables = mutableMapOf<String, Any?>()

    fun setVariable(name: String, value: Any?) {
        variables[name] = value
    }

    fun getVariable(name: String): Any? = variables[name]

    fun hasVariable(name: String): Boolean = variables.containsKey(name)

}