package org.llesha.eval

import org.llesha.exception.EvalException
import org.llesha.expr.Expr
import org.llesha.type.Type

/**
 * @author al.kononov
 */
class Definitions(val variables: MutableMap<String, Type>, val methods: MutableMap<Pair<String, Int>, Method>) {
    override fun toString(): String = variables.toString()

    fun variable(name: String): Type = variables[name] ?: throw EvalException("Unknown variable $name")

    fun method(name: String, paramsCount: Int) =
        methods[name to paramsCount] ?: throw EvalException("Unknown method $name")

    fun addMethod(method: Method) {
        if (methods.containsKey(method.signature)) {
            throw EvalException("Method ${method.name} already exists")
        }
        methods[method.signature] = method
    }

    fun addVariable(name: String, type: Type) {
        variables[name] = type
    }

    fun addArgs(args: List<Expr>, method: Method): Definitions {
        val methodDefs = Definitions(variables.toMutableMap(), methods.toMutableMap())
        method.params.zip(args).forEach { (param, arg) ->
            if(arg !is Type) {
                throw EvalException("Expected Type as argument")
            }
            methodDefs.addVariable(param.name, arg)
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

data class Param(val name: String, val word: String)