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
    fun Long.toM() = MNumber(this)
    fun Expr.toNum(): Long = if (this is MNumber) this.number else throw EvalException("Expected number")
    fun Expr.toBool(): Boolean = if (this is MBool) this.bool else throw EvalException("Expected bool")
    fun Map<String, Type>.toM() = MMap(this)
    fun List<Expr>.toM() = MList(this)
}