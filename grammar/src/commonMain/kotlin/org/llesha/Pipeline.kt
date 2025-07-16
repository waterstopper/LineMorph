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

    fun eval(input: String) {
        val grammar = MorphGrammar()
        val ast = grammar.parseToEnd(input)
        val defs = Definitions.init()

//        ast.forEach {
//            println(it.eval(defs))
//        }
    }
}