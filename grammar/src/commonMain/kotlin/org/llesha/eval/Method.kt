package org.llesha.eval

import org.llesha.Utils.replaceElem
import org.llesha.expr.Annotation
import org.llesha.expr.Expr
import org.llesha.expr.Statement
import org.llesha.type.MList

abstract class Method(annotations: List<Annotation>, val params: Params) : Statement() {
    val annotations: Map<String, Annotation> = annotations.associateBy { it.name }

    protected abstract fun callRaw(args: List<Expr>, defs: Definitions): Expr

    fun isLazy(): Boolean {
        if (signature() == "lazy@2")
            return true
        return annotations.containsKey("LazyArgs")
    }

    fun call(args: List<Expr>, defs: Definitions): Expr {
        val methodDefs = defs.addArgs(args, this)
        return callRaw(args, methodDefs)
    }

    override fun eval(defs: Definitions): Expr {
        TODO("Not yet implemented")
    }

    open fun name(): String = params.name()
    open fun signature(): String = params.signature()

    companion object {
        fun createNativeMethod(
            behavior: (List<Expr>) -> Expr,
            annotations: List<Annotation>,
            vararg words: String
        ): NativeMethod {
            if (words.size % 2 == 1) {
                throw IllegalArgumentException("Words must have an even number of length")
            }
            val params = mutableListOf<NamedParam>()
            val it = words.iterator()

            while (it.hasNext()) {
                val link = it.next()
                val name = it.next()
                params.add(NamedParam(name, link))
            }

            return NativeMethod(annotations, NamedParams(params), behavior)
        }
    }
}