package org.llesha

import com.github.h0tk3y.betterParse.grammar.parseToEnd
import org.llesha.eval.Definitions
import org.llesha.expr.Expr

/**
 * @author al.kononov
 */
object Pipeline {
    val grammar = MorphGrammar()

    fun parse(input: String): List<Expr> {
        return grammar.parseToEnd(input);
    }

    fun eval(input: String): Expr {
        val grammar = MorphGrammar()
        val ast = grammar.parseToEnd(input)
        val defs = Definitions.init()

        val result = ast.map {
            val statementEval = it.eval(defs)
            println(statementEval)
            statementEval
        }
        return result.last()
    }
}