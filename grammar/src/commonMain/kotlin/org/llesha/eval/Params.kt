package org.llesha.eval

import org.llesha.expr.Expr

open class Params(val params: List<String>) {
    open fun name() = params.first()
    open fun signature(): String = name() + "@" + params.size

    open fun names(): List<String> = params.subList(1, params.size)

    override fun toString(): String = params.joinToString(prefix = "(", postfix = ")")

    fun asNamed(links: List<String>): NamedParams {
        if (params.size - 2 != links.size) {
            throw Exception("Expected ${params.size - 2} params in @AsText, got ${links.size}")
        }

        val res = mutableListOf(NamedParam(params[1], params[0]))
        params.subList(2, params.size).zip(links).forEach { (param, link) -> res.add(NamedParam(param, link)) }
        return NamedParams(res)
    }


    companion object {
        fun of(vararg params: String): Params {
            return Params(params.toList())
        }
    }
}


class Vararg(val name: String) : Expr() {
    override fun eval(defs: Definitions): Expr {
        return this
    }
}

class VarargParams(required: List<String>, val vararg: String) : Params(required) {
    override fun signature() = name() + "@*"

    override fun toString(): String = params.joinToString() + ", $vararg"
}

class NamedParams(val namedParams: List<NamedParam>) : Params(emptyList()) {
    override fun name() = namedParams.first().link
    override fun signature(): String = name() + "@" + (namedParams.size + 1)
    override fun names(): List<String> = namedParams.map { it.name }

    override fun toString(): String = namedParams.joinToString(", ")

    fun validate(words: List<String>): Boolean {
        return namedParams.subList(1, namedParams.size).zip(words).all { it.first.link == it.second }
    }
}

data class NamedParam(val name: String, val link: String) {
    override fun toString(): String = name
}