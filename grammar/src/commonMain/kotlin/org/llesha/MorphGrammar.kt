package org.llesha

import com.github.h0tk3y.betterParse.combinators.*
import com.github.h0tk3y.betterParse.grammar.Grammar
import com.github.h0tk3y.betterParse.grammar.parser
import com.github.h0tk3y.betterParse.lexer.literalToken
import com.github.h0tk3y.betterParse.lexer.regexToken
import com.github.h0tk3y.betterParse.parser.Parser
import org.llesha.eval.Function
import org.llesha.expr.*
import org.llesha.type.*

class MorphGrammar() : Grammar<List<Expr>>() {

    // LITERAL
    private val trueToken by literalToken("true")
    private val falseToken by literalToken("false")
    private val assignToken by literalToken("<-")
    private val lParenToken by literalToken("(")
    private val rParenToken by literalToken(")")
    private val lBraceToken by literalToken("{")
    private val rBraceToken by literalToken("}")
    private val commaToken by literalToken(",")
    private val dotToken by literalToken(".")
    private val semicolonToken by literalToken(";")
    private val atToken by literalToken("@")
    private val fnToken by literalToken("fn")

    // SEPARATORS
    private val ws by regexToken("[\\t ]+", ignore = true)
    private val comment by regexToken("#.*(\\n[\\t ]*)*", ignore = true)
    private val codeTextPattern by regexToken(":.*")
    private val codeText by codeTextPattern map { CodeText(it.text.substring(1)) }

    private val lineBreak by regexToken("(\\n[\\t ]*)+")
    private val repeatedSeparator by lineBreak or comment or semicolonToken
    private val statementSeparator by oneOrMore(repeatedSeparator)

    // EXPRS
    private val string by regexToken("\"[\\s\\S]*\"")
    private val stringOne by regexToken("`[\\S]*")
    private val stringValue by string or stringOne map {
        MString(
            if (it.text.startsWith("\"")) it.text.removeSurrounding(
                "\""
            ) else it.text.drop(1)
        )
    }
    private val dateYear by regexToken(Utils.YEAR_DATE_REGEX)
    private val numberToken by regexToken("\\d+")

    private val bool: Parser<MBool> by trueToken or falseToken map { MBool(it.text.startsWith("t")) }
    private val date: Parser<MDate> by dateYear map { MDate(Utils.dateToInstant(it.text)) }
    private val number: Parser<MNumber> by numberToken map { MNumber(it.text.toLong()) }
    private val ident by regexToken("[a-zA-Z]([\\w-]*\\w)?")
    private val field: Parser<Field> by ident and -dotToken and ident map { Field(it.t1.text, it.t2.text) }

    private val type: Parser<Type> by date or number or stringValue or bool

    private val args by optional(separated(parser(this::expr), commaToken)) map { it?.terms ?: emptyList() }

    private val annotation by -atToken and ident and -lParenToken and args and -rParenToken map {
        Annotation(
            it.t1.text,
            Utils.argsToParams(it.t2)
        )
    }
    private val call: Parser<Call> by ident and lParenToken and optional(args) and rParenToken map {
        Call(
            it.t1.text,
            it.t3 ?: emptyList()
        )
    }
    private val function: Parser<Function> by zeroOrMore(annotation) and -fnToken and
            ident and -lParenToken and args and -rParenToken and
            -lBraceToken and optional(separated(parser(this::expr), statementSeparator)) and -rBraceToken map {
        Function(it.t2.text, Utils.argsToParams(it.t3), it.t1, it.t4?.terms ?: emptyList())
    }

    private val assignment: Parser<Assignment> by ident and -assignToken and parser(this::expr) map
            { Assignment(it.t1.text, it.t2) }
    private val ref: Parser<Ref> by ident map { Ref(it.text) }
    private val expr: Parser<Expr> by field or call or assignment or ref or type or codeText

    override val rootParser: Parser<List<Expr>> by
    separated(expr or function, statementSeparator) map { it.terms }
}