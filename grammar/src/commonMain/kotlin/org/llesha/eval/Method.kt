package org.llesha.eval

import org.llesha.exception.EvalException

abstract class Method(val params: List<Param>) {
    fun validate(words: List<String>) {
        val isValid = params.map { e -> e.word }.zip(words).all { it.first == it.second }
        if (!isValid) {
            throw EvalException("Invalid words $words for $name method. Expected: ${params.map { e -> e.word }}")
        }
    }

    val name: String get() = params.first().word
    val signature: Pair<String, Int> get() = params.last().word to params.size - 1

    companion object {
        fun createNativeMethod(vararg words: String): NativeMethod {
            if (words.size % 2 == 1) {
                throw IllegalArgumentException("Words must have an even number of length")
            }
            val params = mutableListOf<Param>()
            val it = words.iterator()

            while (it.hasNext()) {
                val word = it.next()
                val name = it.next()
                params.add(Param(name, word))
            }

            return NativeMethod(params)
        }
    }
}