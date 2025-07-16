package org.llesha.eval

import org.llesha.expr.Annotation
import org.llesha.expr.Expr

/**
 * @author al.kononov
 */
class Function(
    val name: String,
    val parameters: List<String>,
    val annotations: List<Annotation>,
    val body: List<Expr>
) : Expr() {
    override fun toString(): String {
        return "${
            annotations.joinToString(
                separator = "\n",
                postfix = "\n"
            )
        }fn $name(${parameters.joinToString(", ")}) {${body.joinToString("\n", "\n", "\n")}}"
    }

    override fun eval(defs: Definitions): Any {
        TODO("Not yet implemented")
    }
}