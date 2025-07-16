package org.llesha.code.text

import com.github.h0tk3y.betterParse.grammar.parseToEnd
import org.llesha.MorphGrammar
import org.llesha.eval.Definitions
import org.llesha.expr.Assignment
import org.llesha.expr.Call
import org.llesha.expr.CodeText
import org.llesha.expr.Expr

/**
 * @author al.kononov
 */
object CodeTextConverter {
    fun textToCodeText(expr: CodeText, defs: Definitions): Expr {
        val terms = expr.text.split(" ", "\t ").filter { it.isNotBlank() }
        return textToCodeText(terms, defs)
    }

    fun textToCodeText(terms: List<String>, defs: Definitions): Expr {
        val words = terms.filterIndexed { index, _ -> index % 2 == 0 }
        val refs = terms.filterIndexed { index, _ -> index % 2 != 0 }

        if (words.last() == "as") {
            return Assignment(refs.last(), textToCodeText(terms.dropLast(2), defs))
        }

        val method = defs.method(words.first(), refs.size)
        method.validate(words)

        val grammar = MorphGrammar()
        val exprs = refs.map { grammar.parseToEnd(it) }
        return Call(method.name, exprs as List<Expr>)
    }
}