package org.llesha.expr

import org.llesha.eval.Definitions
import org.llesha.exception.EvalException
import org.llesha.type.ContainerType
import org.llesha.type.Type

/**
 * @author al.kononov
 */
abstract class Expr {
    open fun toStr() = toString()
    abstract fun eval(defs: Definitions): Expr
}

class Void : Expr() {
    override fun toString(): String = "Void"

    override fun eval(defs: Definitions): Expr {
        return this
    }
}

abstract class Statement : Expr()

class ReturnStatement(val value: Expr) : Statement() {
    override fun toString(): String = "return $value"

    override fun eval(defs: Definitions): Expr {
         return value.eval(defs)
    }

}

data class Ref(val name: String) : Expr() {
    override fun toString(): String = name

    override fun eval(defs: Definitions): Expr = defs.variable(name)
}

data class Annotation(val name: String, val args: List<String>) {
    override fun toString(): String = "@$name(${args.joinToString(", ")})"
}

data class Indexed(val variable: Expr, val properties: List<Expr>) : Expr() {
    override fun toString(): String =
        "$variable${properties.joinToString(separator = "][", prefix = "[", postfix = "]")}"

    override fun eval(defs: Definitions): Expr {
        var container = variable.eval(defs)
        for (prop in properties) {
            container = indexOne(container, prop, defs)
        }
        return container
    }

    private fun indexOne(container: Expr, property: Expr, defs: Definitions): Expr {
        if (container !is ContainerType) {
            throw EvalException("Only maps and lists can be indexed")
        }
        return container.index(property.eval(defs))
    }

}

data class CodeText(val text: String) : Statement() {
    override fun toString() = ":$text"

    override fun eval(defs: Definitions): Expr {
        TODO("Not yet implemented")
    }
}

data class Call(val fnName: String, val args: List<Expr>) : Statement() {
    override fun toString(): String = "$fnName(${args.joinToString()})"

    override fun eval(defs: Definitions): Expr {
        val method = defs.method(fnName, args.size)
        val evaluatedArgs = args.map { it.eval(defs) }
        val methodDefs = defs.addArgs(evaluatedArgs, method)

        return method.call(evaluatedArgs, methodDefs)
    }
}

data class Assignment(val left: String, val right: Expr) : Statement() {
    override fun toString(): String = "$left <- $right"

    override fun eval(defs: Definitions): Expr {
        val value = right.eval(defs)
        if (value !is Type) {
            throw EvalException("Expected type at the right-side for $this, got $value")
        }
        defs.addVariable(left, value)
        return value
    }
}