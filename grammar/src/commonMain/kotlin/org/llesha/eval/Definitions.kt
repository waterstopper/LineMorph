package org.llesha.eval

import org.llesha.exception.EvalException
import org.llesha.expr.Expr
import org.llesha.type.Func
import org.llesha.type.Type

/**
 * @author al.kononov
 */
class Definitions(val variables: MutableMap<String, Type>, val methods: MutableMap<String, Method>) {
    override fun toString(): String = variables.toString()

    fun variable(name: String): Type = variables[name] ?: throw EvalException("Unknown variable $name")

    fun method(fn: Func) =
        methods[fn.toString()] ?: throw EvalException("Unknown method $fn")

    fun method(name: String, paramsCount: Int) =
        methods["$name@$paramsCount"] ?: methods["$name@*"] ?: throw EvalException("Unknown method $name")

    fun addMethod(method: Method) {
        if (methods.containsKey(method.signature())) {
            throw EvalException("Method ${method.name()} already exists")
        }
        methods[method.signature()] = method
    }

    fun addVariable(name: String, type: Type) {
        variables[name] = type
    }

    fun addArgs(args: List<Expr>, method: Method): Definitions {
        val methodDefs = Definitions(variables.toMutableMap(), methods.toMutableMap())

        method.params.names().zip(args).forEach { (param, arg) ->
            if (arg !is Type) {
                throw EvalException("Expected Type as argument")
            }
            methodDefs.addVariable(param, arg)
        }
        return methodDefs
    }

    companion object {
        fun init(): Definitions {
            val defs = Definitions(mutableMapOf(), mutableMapOf())
            NativeMethod.initNativeMethods(defs)

            return defs
        }
    }
}

open class Params(val params: List<String>) {
    open fun name() = params.first()
    open fun signature(): String = name() + "@" + params.size

    open fun names(): List<String> = params

    override fun toString(): String = params.joinToString(prefix = "(", postfix = ")")

    companion object {
        fun of(vararg params: String): Params {
            return Params(params.toList())
        }
    }
}

class VarargParams(required: List<String>) : Params(required) {
    override fun signature() = name() + "@*"
}

class NamedParams(val namedParams: List<NamedParam>) : Params(emptyList()) {
    override fun name() = namedParams.first().link
    override fun signature(): String = name() + "@" + namedParams.size
    override fun names(): List<String> = namedParams.map { it.name }

    override fun toString(): String = namedParams.joinToString(", ")

    fun validate(words: List<String>): Boolean {
        return namedParams.subList(1, namedParams.size).zip(words).all { it.first.link == it.second }
    }
}

data class NamedParam(val name: String, val link: String) {
    override fun toString(): String = name
}