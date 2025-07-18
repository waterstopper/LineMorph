package org.llesha.eval

import org.llesha.exception.EvalException
import org.llesha.expr.Expr
import org.llesha.expr.Statement

abstract class Method(val params: List<Param>): Statement() {
    abstract fun call(args: List<Expr>, defs: Definitions) : Expr

    override fun eval(defs: Definitions): Expr {
        TODO("Not yet implemented")
    }

    fun validate(words: List<String>) {
        val isValid = params.map { e -> e.word }.zip(words).all { it.first == it.second }
        if (!isValid) {
            throw EvalException("Invalid words $words for $name method. Expected: ${params.map { e -> e.word }}")
        }
    }

    open val name: String get() = params.first().word
    val signature: Pair<String, Int> get() = params.first().word to params.size

    companion object {
        fun createNativeMethod(behavior: (List<Expr>) -> Expr, vararg words: String): NativeMethod {
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

            return NativeMethod(params, behavior)
        }
    }
}