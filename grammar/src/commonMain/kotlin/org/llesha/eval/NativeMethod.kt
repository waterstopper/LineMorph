package org.llesha.eval

import org.llesha.AnnotationProcessor.init
import org.llesha.AnnotationProcessor.withArgList
import org.llesha.CastUtils.toBool
import org.llesha.CastUtils.toM
import org.llesha.CastUtils.toNum
import org.llesha.Utils.compare
import org.llesha.Utils.plus
import org.llesha.exception.EvalException
import org.llesha.expr.Annotation
import org.llesha.expr.Expr
import org.llesha.expr.Void
import org.llesha.native.read
import org.llesha.native.write
import org.llesha.type.*

class NativeMethod(annotations: List<Annotation>, params: Params, val behavior: (List<Expr>) -> Expr) :
    Method(annotations, params) {
    override fun toString(): String = "${params.name()}(${params.params.size})"

    override fun callRaw(args: List<Expr>, defs: Definitions): Expr {
        return behavior(args)
    }

    companion object {
        fun initNativeMethods(defs: Definitions) {
            val load = createNativeMethod(
                { (li) ->
                    when (li.toString()) {
                        "ops" -> loadOps(defs)
                        "file" -> loadFile(defs)
                        "lib" -> loadLib(defs)
                        "string" -> loadString(defs)
                        "natives" -> loadNatives(defs)
                    }
                    Void()
                },
                emptyList(),
                "load", "codePath"
            )
            defs.addMethod(load)
        }

        private fun loadOps(defs: Definitions) {
            createAndAddBinary("and", { (a, b) -> (a.toBool() && b.toBool()).toM() }, defs, "by")
            createAndAddBinary("or", { (a, b) -> (a.toBool() || b.toBool()).toM() }, defs, "by")
            createAndAddBinary("eq", { (a, b) -> (a == b).toM() }, defs)
            createAndAddBinary("neq", { (a, b) -> (a != b).toM() }, defs)
            createAndAddBinary("leq", { (a, b) -> (a.compare(b) <= 0).toM() }, defs)
            createAndAddBinary("geq", { (a, b) -> (a.compare(b) >= 0).toM() }, defs)
            createAndAddBinary("gt", { (a, b) -> (a.compare(b) > 0).toM() }, defs)
            createAndAddBinary("lt", { (a, b) -> (a.compare(b) < 0).toM() }, defs)
            createAndAddBinary("plus", { (a, b) -> a.plus(b) }, defs, "to")
            createAndAddBinary("minus", { (a, b) -> (a == b).toM() }, defs, "with")
            createAndAddBinary("mul", { (a, b) -> (a.toNum() * b.toNum()).toM() }, defs, "by")
            createAndAddBinary("div", { (a, b) -> (a.toNum() / b.toNum()).toM() }, defs, "by")
            createAndAddBinary("mod", { (a, b) -> (a.toNum() % b.toNum()).toM() }, defs, "by")
        }

        private fun loadFile(defs: Definitions) {
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
        }

        private fun loadLib(defs: Definitions) {

        }

        private fun loadString(defs: Definitions) {
            val find = createNativeMethod(
                { (arg, text) -> MString(Regex(arg.toString()).find(text.toString())!!.value) },
                init().withArgList("text"),
                "find", "arg", "in", "text"
            )
            defs.addMethod(find)

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
                    MList(text.toString().split(separator.toString()).map { MString(it) })
                },
                emptyList(),
                "split", "text", "with", "separator"
            )
            defs.addMethod(split)
        }

        private fun loadNatives(defs: Definitions) {
            val apply = NativeMethod(emptyList(), VarargParams(listOf("apply", "func"))) { li ->
                val method = defs.method(li.first() as Func)
                method.callRaw(li.subList(1, li.size), defs)
            }
            defs.addMethod(apply)

            val size = createNativeMethod(
                { (li) ->
                    if (li is ContainerType)
                        li.size()
                    else throw EvalException("Cannot get size of value")
                    Void()
                },
                emptyList(),
                "size", "container"
            )
            defs.addMethod(size)

            val print = createNativeMethod(
                { (li) -> println(li); Void() },
                emptyList(),
                "print", "arg"
            )
            defs.addMethod(print)

            val filter = createNativeMethod(
                { (li, func) ->
                    //
                    Void()
                },
                emptyList(),
                "filter", "list", "by", "func"
            )
            defs.addMethod(filter)

            val unwrap = createNativeMethod(
                { (json) ->
                    if (json !is MMap) throw EvalException("Unwrap json must be a MMap")
                    json.map.entries.forEach { (k, v) -> defs.addVariable(k, v) }
                    Void()
                },
                emptyList(),
                "unwrap", "json"
            )
            defs.addMethod(unwrap)

            val wrap = NativeMethod(
                emptyList(),
                Params.of("wrap")
            ) { _ -> defs.variables.toM() }
            defs.addMethod(wrap)
        }

        private fun createAndAddBinary(
            name: String,
            behavior: (List<Expr>) -> Expr,
            defs: Definitions,
            linkWord: String = "and"
        ) {
            val method = createNativeMethod(behavior, init(), name, "a", linkWord, "b")
            defs.addMethod(method)
        }
    }
}