package org.llesha.expr

import org.llesha.eval.Definitions
import org.llesha.exception.EvalException
import org.llesha.type.Type

/**
 * @author al.kononov
 */
abstract class Expr {
    abstract fun eval(defs: Definitions): Any
}

data class Ref(val name: String) : Expr() {
    override fun toString(): String = name

    override fun eval(defs: Definitions): Any = defs.variable(name)
}

data class Annotation(val name: String, val args: List<String>) {
    override fun toString(): String = "@$name(${args.joinToString(", ")})"
}

data class Field(val variable: String, val field: String) : Expr() {
    override fun toString(): String = "$variable.$field"

    override fun eval(defs: Definitions): Any {
        TODO("Not yet implemented")
    }
}

data class CodeText(val text: String) : Expr() {
    override fun toString() = ":$text"

    override fun eval(defs: Definitions): Any {
        TODO("Not yet implemented")
    }
}

data class Call(val fnName: String, val args: List<Expr>) : Expr() {
    override fun toString(): String = "$fnName(${args.joinToString()})"

    override fun eval(defs: Definitions): Any {
        TODO()
    }
}

data class Assignment(val left: String, val right: Expr) : Expr() {
    override fun toString(): String = "$left <- $right"

    override fun eval(defs: Definitions): Any {
        val value = right.eval(defs)
        if (value !is Type) {
            throw EvalException("Expected type at the right-side for $this, got $value")
        }
        defs.addVariable(left, value)
        return value
    }
}