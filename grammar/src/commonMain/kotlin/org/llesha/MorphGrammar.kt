package org.llesha

import com.github.h0tk3y.betterParse.combinators.*
import com.github.h0tk3y.betterParse.grammar.Grammar
import com.github.h0tk3y.betterParse.grammar.parser
import com.github.h0tk3y.betterParse.lexer.literalToken
import com.github.h0tk3y.betterParse.lexer.regexToken
import com.github.h0tk3y.betterParse.parser.Parser
import org.llesha.eval.UserMethod
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
    private val lBracketToken by literalToken("[")
    private val rBracketToken by literalToken("]")
    private val commaToken by literalToken(",")
    private val dotToken by literalToken(".")
    private val semicolonToken by literalToken(";")
    private val atToken by literalToken("@")
    private val fnToken by literalToken("fn")
    private val colonToken by literalToken(":")

    // SEPARATORS
    private val ws by regexToken("[\\t ]+", ignore = true)
    private val comment by regexToken("#.*(\\n[\\t ]*)*", ignore = true)
    private val codeTextPattern by regexToken("\\^.*")
    private val codeText by codeTextPattern map { CodeText(it.text.substring(1)) }

    private val lineBreak by regexToken("(\\n[\\t ]*)+")
    private val repeatedSeparator by lineBreak or comment or semicolonToken
    private val statementSeparator by oneOrMore(repeatedSeparator)

    // the regex "[^\\"]*(\\["nrtbf\\][^\\"]*)*" matches:
    // "               – opening double quote,
    // [^\\"]*         – any number of not escaped characters, nor double quotes
    // (
    //   \\["nrtbf\\]  – backslash followed by special character (\", \n, \r, \\, etc.)
    //   [^\\"]*       – and any number of non-special characters
    // )*              – repeating as a group any number of times
    // "               – closing double quote
    private val stringLiteral by regexToken("\"[^\\\\\"]*(\\\\[\"nrtbf\\\\][^\\\\\"]*)*\"")
    // EXPRS
    private val stringOne by regexToken("`[\\S]*")
    private val stringValue by stringLiteral or stringOne map {
        MString(
            if (it.text.startsWith("\"")) {
                it.text.substring(1, it.text.lastIndex).replace("\\\"", "\"")
                    .replace("\\n", "\n")
                    .replace("\\r", "\r")
                    .replace("\\t", "\t")
                    .replace("\\b", "\b")
                    .replace("\\f", "\u000C")
                    .replace("\\\\", "\\")
            } else it.text.drop(1)
        )
    }
    private val dateYear by regexToken(Utils.YEAR_DATE_REGEX)
    private val numberToken by regexToken("\\d+")

    private val bool: Parser<MBool> by trueToken or falseToken map { MBool(it.text.startsWith("t")) }
    private val date: Parser<MDate> by dateYear map { MDate(Utils.dateToInstant(it.text)) }
    private val number: Parser<MNumber> by numberToken map { MNumber(it.text.toLong()) }
    private val ident by regexToken("[a-zA-Z]([\\w-]*\\w)?")
//    private val field: Parser<Field> by
    private val indexed: Parser<Indexed> by parser(this::nonIndexed) and oneOrMore(-lBracketToken and parser(this::expr) and -rBracketToken) map { Indexed(it.t1, it.t2) }

    private val jsonPrimitiveValue: Parser<Type> = bool or stringValue or number
    private val jsonObject: Parser<MMap> = (-lBraceToken and
            separated(stringValue and -colonToken and parser(this::expr), commaToken, true) and
            -rBraceToken)
        .map {
            MMap(it.terms.associate { (key, v) -> Pair(key.string, v) })
        }
    private val jsonArray: Parser<MList> = (-lBracketToken and
            separated(parser(this::expr), commaToken, true) and
            -rBracketToken)
        .map { MList(it.terms) }
    private val jsonValue: Parser<Type> = jsonPrimitiveValue or jsonObject or jsonArray

    private val type: Parser<Type> by date or jsonValue

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
    private val function: Parser<UserMethod> by zeroOrMore(annotation) and -fnToken and
            ident and -lParenToken and args and -rParenToken and
            -lBraceToken and optional(separated(parser(this::statement), statementSeparator)) and -rBraceToken map {
        UserMethod(it.t2.text, Utils.argsToParams(it.t3), it.t1, it.t4?.terms ?: emptyList())
    }

    private val assignment: Parser<Assignment> by ident and -assignToken and parser(this::expr) map
            { Assignment(it.t1.text, it.t2) }
    private val ref: Parser<Ref> by ident map { Ref(it.text) }
    private val nonIndexed: Parser<Expr> by call or ref or type
    private val expr: Parser<Expr> by  indexed or nonIndexed
    private val statement: Parser<Statement> by call or assignment or codeText

    override val rootParser: Parser<List<Statement>> by
    separated(statement or function, statementSeparator) map { it.terms }
}