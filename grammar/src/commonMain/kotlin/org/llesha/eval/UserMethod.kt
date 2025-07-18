package org.llesha.eval

import org.llesha.expr.Annotation
import org.llesha.expr.Expr
import org.llesha.expr.Statement

/**
 * @author al.kononov
 */
class UserMethod(
    override val name: String,
    val parameters: List<String>,
    val annotations: List<Annotation>,
    val body: List<Statement>
) : Method(emptyList()) {
    override fun toString(): String {
        return "${
            annotations.joinToString(
                separator = "\n",
                postfix = "\n"
            )
        }fn $name(${parameters.joinToString(", ")}) {${body.joinToString("\n", "\n", "\n")}}"
    }

    override fun call(args: List<Expr>, defs: Definitions): Expr {
        TODO("Not yet implemented")
    }

    override fun eval(defs: Definitions): Expr {
        TODO("Not yet implemented")
    }
}