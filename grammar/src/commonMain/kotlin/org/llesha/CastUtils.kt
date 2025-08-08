package org.llesha

import org.llesha.exception.EvalException
import org.llesha.expr.Expr
import org.llesha.type.*

/**
 * @author al.kononov
 */
object CastUtils {
    fun Boolean.toM() = MBool(this)
    fun String.toM() = MString(this)
    fun Char.toM() = MString(this.toString())
    fun Long.toM() = MNumber(this)
    fun Int.toM() = MNumber(this.toLong())

    fun Any.anyToM(): Expr = when (this) {
        is Expr -> this
        is Boolean -> this.toM()
        is String -> this.toM()
        is Char -> this.toM()
        is Long -> this.toM()
        is Int -> this.toM()
        is Map<*, *> -> (this as Map<String, Type>).toM()
        is List<*> -> (this as List<Expr>).toM()
        else -> throw EvalException("Cannot convert $this to any type")
    }

    fun Map<String, Type>.toM() = MMap(this)
    fun List<Expr>.toM() = MList(this)

    fun Expr.toNum(): Long = if (this is MNumber) this.number else throw EvalException("Expected number")
    fun Expr.toInt(): Int = if (this is MNumber) this.number.toInt() else throw EvalException("Expected number")
    fun Expr.toBool(): Boolean = if (this is MBool) this.bool else throw EvalException("Expected bool")

    fun Expr.asCont(): ContainerType = if (this is ContainerType) this else throw EvalException("Expected container")
    fun Expr.asFunc(): Func = if (this is Func) this else throw EvalException("Expected func")
}