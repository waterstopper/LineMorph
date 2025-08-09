package org.llesha.type

import org.llesha.CastUtils.toM
import org.llesha.Utils
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

abstract class ContainerType : Type() {
    abstract fun index(index: Expr): Expr
    abstract fun size(): Int
    abstract fun iterable(): Iterable<Expr>
}

data class MBool(val bool: Boolean) : Type() {
    override fun toString(): String = bool.toString()

}

data class MNumber(val number: Long) : Type() {
    override fun toString() = number.toString()

}

data class MDate(val date: Instant) : Type() {
    override fun toString(): String = date.toString()
}

data class MTime(val date: Instant) : Type() {
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
    override fun iterable(): Iterable<Expr> = string.map { it.toM() }.asIterable()

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
    override fun iterable(): Iterable<Expr> = list.asIterable()

    companion object {
        fun of(vararg args: Expr): MList {
            return MList(args.toList())
        }
    }
}

data class MMap(val map: Map<String, Type>) : ContainerType() {
    override fun toString(): String =
        map.entries.joinToString(prefix = "{", postfix = "}", separator = ",") { """"${it.key}":${it.value}""" }

    override fun toStr(): String =
        map.entries.joinToString(prefix = "{", postfix = "}", separator = ",") { """"${it.key}":${it.value.toStr()}""" }

    override fun index(index: Expr): Expr {
        return when (index) {
            is MNumber -> map[index.number.toString()] ?: throw EvalException("Key $index not found in $this")
            is MString -> map[index.string] ?: throw EvalException("Key $index not found in $this")
            else -> throw EvalException("Expected number or string as map key")
        }
    }

    override fun size(): Int = map.size
    override fun iterable(): Iterable<Expr> = map.entries.map { MList.of(it.key.toM(), it.value) }.asIterable()
}

class Func(val name: String, val argNum: Int) : Type() {
    override fun toString(): String = "$name@${argNum + 1}"

    fun evalWithArgs(defs: Definitions, args: List<Expr>): Expr {
        val method = defs.method(this)
        return Utils.evalMethod(method, args, defs)
    }
}

class MLazy(val expr: Expr) : Type() {
    override fun toString(): String = expr.toString()

    companion object {
        fun cons(arg: Expr): MLazy {
            if (arg !is MLazy) {
                return MLazy(arg)
            }
            return arg
        }
    }
}