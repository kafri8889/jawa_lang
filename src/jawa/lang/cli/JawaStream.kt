package jawa.lang.cli

import java.io.File

class JawaStream(private val jawaFile: File) {

    fun asString(): String {
        return jawaFile.bufferedReader().use { it.readText() }
    }

}