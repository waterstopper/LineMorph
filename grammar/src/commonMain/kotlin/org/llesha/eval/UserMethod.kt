package org.llesha.eval

import org.llesha.expr.Annotation
import org.llesha.expr.Expr
import org.llesha.expr.Statement

/**
 * @author al.kononov
 */
class UserMethod(
    annotations: List<Annotation>,
    val body: List<Statement>,
    params: Params
) : Method(annotations, params) {
    override fun name(): String = params.name()

    override fun toString(): String {
        return "${
            annotations.values.joinToString(separator = "\n", postfix = "\n")
        }fn ${name()}($params) {${body.joinToString("\n", "\n", "\n")}}"
    }

    override fun callRaw(args: List<Expr>, defs: Definitions): Expr {
        return body.map { it.eval(defs) }.last()
    }

    override fun eval(defs: Definitions): Expr {
        defs.addMethod(this)
        return this
    }

    companion object {
        fun cons(
            parameters: Params,
            annotations: List<Annotation>,
            body: List<Statement>
        ): UserMethod {
            val annotationsMap = annotations.associateBy { it.name }
            val params = if (annotationsMap.containsKey("AsText")) {
                parameters.asNamed(annotationsMap["AsText"]!!.args)
            } else parameters

            return UserMethod(annotations, body, params)
        }
    }
}