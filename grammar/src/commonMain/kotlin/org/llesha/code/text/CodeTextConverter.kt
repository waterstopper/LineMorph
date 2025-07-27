package org.llesha.code.text

import com.github.h0tk3y.betterParse.grammar.parseToEnd
import org.llesha.MorphGrammar
import org.llesha.eval.Definitions
import org.llesha.eval.NamedParams
import org.llesha.exception.EvalException
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
        if (method.params !is NamedParams) {
            throw EvalException("Textified method should have an annotation @AsText")
        }
        if (!method.params.validate(words)) {
            throw EvalException("Invalid words $words for ${method.name()} method. Expected: ${method.params}")
        }

        val grammar = MorphGrammar()
        val exprs = refs.map { grammar.parseToEnd(it) }
        return Call(method.name(), exprs as List<Expr>)
    }
}