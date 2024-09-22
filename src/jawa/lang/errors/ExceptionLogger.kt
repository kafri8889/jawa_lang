package jawa.lang.errors

import jawa.lang.cli.JawaCli

class ExceptionLogger(
    private val cliClient: JawaCli.CliClient? = null
) {

    private val exceptions = mutableListOf<Exception>()

    /**
     * @param throwException if `true`, print the exception message to Cli through [cliClient]
     */
    fun add(exception: Exception, throwException: Boolean = false) {
        if (throwException) cliClient?.print(exception.message ?: "")
        exceptions.add(exception)
    }

    fun clear() {
        exceptions.clear()
    }

}