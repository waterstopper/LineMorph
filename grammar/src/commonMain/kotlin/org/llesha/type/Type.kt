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
    abstract fun size(): Int
}

class MBool(val bool: Boolean) : Type() {
    override fun toString(): String = bool.toString()

}

class MNumber(val number: Long) : Type() {
    override fun toString() = number.toString()

}

class MDate(val date: Instant) : Type() {
    override fun toString(): String = date.toString()

}

class MTime(val date: Instant) : Type() {
    override fun toString(): String = date.toString()
}

data class MString(val string: String) : ContainerType() {
    override fun toString(): String = string

    override fun index(index: Expr): Expr {
        if (index !is MNumber) {
            throw EvalException("Expected number as List index")
        }
        return MString(string[index.number.toInt()].toString())
    }

    override fun size(): Int = string.length

    override fun toStr() = """"$string""""
}

data class MList(val list: List<Expr>) : ContainerType() {
    override fun toString(): String = list.joinToString(separator = ",", prefix = "[", postfix = "]")
    override fun toStr(): String = list.joinToString(separator = ",", prefix = "[", postfix = "]") { it.toStr() }

    override fun index(index: Expr): Expr {
        if (index !is MNumber) {
            throw EvalException("Expected number as List index")
        }
        return list[index.number.toInt()]
    }

    override fun size(): Int = list.size
}

class MMap(val map: Map<String, Type>) : ContainerType() {
    override fun toString(): String =
        map.entries.joinToString(prefix = "{", postfix = "}", separator = ",") { """"${it.key}":${it.value}""" }

    override fun toStr(): String =
        map.entries.joinToString(prefix = "{", postfix = "}", separator = ",") { """"${it.key}":${it.value.toStr()}""" }

    override fun index(index: Expr): Expr {
        when (index) {
            is MNumber -> return map[index.number.toString()] ?: throw EvalException("Key $index not found in $this")
            is MString -> return map[index.string] ?: throw EvalException("Key $index not found in $this")
            else -> throw EvalException("Expected number or string as map key")
        }
    }

    override fun size(): Int = map.size
}

class Func(val name: String, val argNum: Int): Type() {
    override fun toString(): String = "$name@$argNum"
}