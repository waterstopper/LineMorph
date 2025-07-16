package org.llesha.type

import org.llesha.eval.Definitions
import org.llesha.expr.Expr
import kotlin.time.Instant

/**
 * @author al.kononov
 */
abstract class Type : Expr() {
    override fun eval(defs: Definitions): Any = this
}

abstract class ContainerType() : Type() {}

class MBool(val boolean: Boolean) : Type() {
    override fun toString(): String = boolean.toString()

}

class MNumber(val number: Long) : Type() {
    override fun toString() = number.toString()

}

class MString(val string: String) : Type() {
    override fun toString(): String = "`$string"

}

class MDate(val date: Instant) : Type() {
    override fun toString(): String = date.toString()

}

class MTime(val date: Instant) : Type() {
    override fun toString(): String = date.toString()
}

class MList(val list: List<Any>) : ContainerType() {
    override fun toString(): String = list.toString()
}

class MMap(val map: Map<String, Any>) : ContainerType() {
    override fun toString(): String = map.toString()
}