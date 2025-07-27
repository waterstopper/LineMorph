package org.llesha.type

import org.llesha.eval.Definitions
import org.llesha.exception.EvalException
import org.llesha.expr.Expr
import kotlin.time.Instant

/**
 * @author al.kononov
 */
abstract class Type : Expr() {
    override fun eval(defs: Definitions): Expr = this
}

abstract class ContainerType() : Type() {
    abstract fun index(index: Expr): Expr
}

class MBool(val boolean: Boolean) : Type() {
    override fun toString(): String = boolean.toString()

}

class MNumber(val number: Long) : Type() {
    override fun toString() = number.toString()

}

data class MString(val string: String) : Type() {
    override fun toString(): String = string

}

class MDate(val date: Instant) : Type() {
    override fun toString(): String = date.toString()

}

class MTime(val date: Instant) : Type() {
    override fun toString(): String = date.toString()
}

data class MList(val list: List<Expr>) : ContainerType() {
    override fun toString(): String = list.toString()

    override fun index(index: Expr): Expr {
        if (index !is MNumber) {
            throw EvalException("Expected number as List index")
        }
        return list[index.number.toInt()];
    }
}

class MMap(val map: Map<String, Expr>) : ContainerType() {
    override fun toString(): String = map.toString()

    override fun index(index: Expr): Expr {
        when (index) {
            is MNumber -> return map[index.number.toString()] ?: throw EvalException("Key $index not found in $this")
            is MString -> return map[index.string] ?: throw EvalException("Key $index not found in $this")
            else -> throw EvalException("Expected number or string as map key")
        }
    }
}