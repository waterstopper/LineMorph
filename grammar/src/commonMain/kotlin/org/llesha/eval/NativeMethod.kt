package org.llesha.eval

import org.llesha.AnnotationProcessor.init
import org.llesha.AnnotationProcessor.withArgList
import org.llesha.expr.Annotation
import org.llesha.expr.Expr
import org.llesha.expr.Void
import org.llesha.native.read
import org.llesha.native.write
import org.llesha.type.MList
import org.llesha.type.MString

class NativeMethod(annotations: List<Annotation>, params: NamedParams, val behavior: (List<Expr>) -> Expr) :
    Method(annotations, params) {
    override fun toString(): String = "${params.name()}(${params.params.size})"

    override fun callRaw(args: List<Expr>, defs: Definitions): Expr {
        return behavior(args)
    }

    companion object {
        fun initNativeMethods(defs: Definitions) {
            val print = createNativeMethod(
                { (li) -> println(li); Void() },
                emptyList(),
                "print", "arg"
            )
            defs.addMethod(print)

            val load = createNativeMethod(
                { (li) -> read(li.toString()); Void() },
                emptyList(),
                "load", "codePath"
            )
            defs.addMethod(load)

            val write = createNativeMethod(
                { (arg, path) ->
                    write(path.toString(), arg.toString()); Void()
                },
                emptyList(),
                "write", "arg", "to", "path"
            )
            defs.addMethod(write)

            val read = createNativeMethod(
                { (li) -> MString(read(li.toString())) },
                emptyList(),
                "read", "path"
            )
            defs.addMethod(read)

            val json = createNativeMethod(
                { (li) -> read(li.toString()); Void() },
                emptyList(),
                "json", "arg"
            )
            defs.addMethod(json)

            val parseJson = createNativeMethod(
                { (li) -> read(li.toString()); Void() },
                emptyList(),
                "parseJson", "string"
            )
            defs.addMethod(parseJson)

            val split = createNativeMethod(
                { (text, separator) ->
                    MList(
                        text.toString()
                            .split(separator.toString())
                            .map { MString(it) })
                },
                emptyList(),
                "split", "text", "with", "separator"
            )
            defs.addMethod(split)

            val find = createNativeMethod(
                { (arg, text) ->
                    MString(Regex(arg.toString()).find(text.toString())!!.value)
                },
                init().withArgList("text"),
                "find", "arg", "in", "text"
            )
            defs.addMethod(find)
        }
    }
}