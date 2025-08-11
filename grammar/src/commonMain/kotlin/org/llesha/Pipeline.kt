package org.llesha

import com.github.h0tk3y.betterParse.grammar.parseToEnd
import com.github.h0tk3y.betterParse.st.LiftToSyntaxTreeOptions
import com.github.h0tk3y.betterParse.st.SyntaxTree
import com.github.h0tk3y.betterParse.st.liftToSyntaxTreeGrammar
import org.llesha.eval.Definitions
import org.llesha.expr.Expr
import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport

/**
 * @author al.kononov
 */
@OptIn(ExperimentalJsExport::class)
@JsExport
object Pipeline {
    val grammar = MorphGrammar()

    fun parse(input: String): List<Expr> {
        return grammar.parseToEnd(input)
    }

    fun eval(input: String): Expr {
        val ast = grammar.parseToEnd(input)
        val defs = Definitions.init()

        val result = ast.map {
            val statementEval = it.eval(defs)
            println(statementEval)
            statementEval
        }
        return result.last()
    }

    fun evalWithPos(input: String): Expr {
        val ast: SyntaxTree<List<Expr>> = grammar
            .liftToSyntaxTreeGrammar(LiftToSyntaxTreeOptions(retainSeparators = false))
            .parseToEnd(input)
        val defs = Definitions.init()

        val result = ast.item.withIndex().map {
            try {
                val statementEval = it.value.eval(defs)
                println(it.value)
                statementEval
            } catch (e: Exception) {
                throw Exception(
                    "${e.message} at ${ast.children[it.index].range}: ${input.substring(ast.children[it.index].range)}",
                    e
                )
            }
        }
        return result.last()
    }
}
