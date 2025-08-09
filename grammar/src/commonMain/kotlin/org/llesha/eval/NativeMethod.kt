package org.llesha.eval

import org.llesha.AnnotationProcessor.init
import org.llesha.AnnotationProcessor.withArgList
import org.llesha.CastUtils.anyToM
import org.llesha.CastUtils.asCont
import org.llesha.CastUtils.asFunc
import org.llesha.CastUtils.toBool
import org.llesha.CastUtils.toM
import org.llesha.CastUtils.toNum
import org.llesha.Utils
import org.llesha.Utils.compare
import org.llesha.Utils.listWithHead
import org.llesha.Utils.plus
import org.llesha.Utils.subListOrEmpty
import org.llesha.exception.EvalException
import org.llesha.expr.Annotation
import org.llesha.expr.Expr
import org.llesha.expr.Void
import org.llesha.native.delete
import org.llesha.native.exists
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
            createAndAddUnary("not", { (a) -> (!a.toBool()).toM() }, defs)
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
                { (li) ->
                    println(read(li.toString()))
                    MString(read(li.toString())) },
                emptyList(),
                "read", "path"
            )
            defs.addMethod(read)

            val exists = createNativeMethod(
                { (path) -> exists(path.toString()).toM() },
                emptyList(),
                "exists", "path"
            )
            defs.addMethod(exists)

            val delete = createNativeMethod(
                { (path) -> delete(path.toString()).toM() },
                emptyList(),
                "delete", "path"
            )
            defs.addMethod(delete)
        }

        private fun loadLib(defs: Definitions) {

        }

        private fun loadString(defs: Definitions) {
            val indexed = NativeMethod(
                emptyList(),
                Params.of("indexed", "cont")
            ) { (cont) ->
                when (val liOrStr = cont.asCont()) {
                    is MString -> liOrStr.string.withIndex().map { MList.of(it.index.toM(), it.value.toM()) }.toM()
                    is MList -> liOrStr.list.withIndex().map { MList.of(it.index.toM(), it.value) }.toM()
                    else -> throw EvalException("Invalid cont: $liOrStr, expected String or List")
                }
            }
            defs.addMethod(indexed)

            val forEach = NativeMethod(
                emptyList(),
                VarargParams(listOf("for-each", "cont"), "*args")
            ) { args ->
                val fn = args[0].asFunc()
                args[1].asCont().iterable()
                    .forEach { fn.evalWithArgs(defs, args.subListOrEmpty(2).listWithHead(it.anyToM())) }
                Void()
            }
            defs.addMethod(forEach)


            val find = createNativeMethod(
                { (arg, text) -> MString(Regex(arg.toString()).find(text.toString())!!.value) },
                init().withArgList("text"),
                "find", "arg", "in", "text"
            )
            defs.addMethod(find)

            val json = createNativeMethod(
                { (li) ->
                    li.toStr().toM()
                },
                emptyList(),
                "json", "arg"
            )
            defs.addMethod(json)

            val parseJson = createNativeMethod(
                { (li) -> Utils.parseJson(li.toString()) },
                emptyList(),
                "parse-json", "string"
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
            createAndAddUnary("isLazy", { (arg) -> (arg is MLazy).toM() }, defs)
            createAndAddUnary("isNumber", { (arg) -> (arg is MNumber).toM() }, defs)
            createAndAddUnary("isString", { (arg) -> (arg is MString).toM() }, defs)
            createAndAddUnary("isList", { (arg) -> (arg is MList).toM() }, defs)
            createAndAddUnary("isMap", { (arg) -> (arg is MMap).toM() }, defs)
            createAndAddUnary("isBool", { (arg) -> (arg is MBool).toM() }, defs)
            createAndAddUnary("isFunc", { (arg) -> (arg is Func).toM() }, defs)

            val apply = NativeMethod(emptyList(), VarargParams(listOf("apply", "func"), "args")) { li ->
                (li.first() as Func).evalWithArgs(defs, li.subList(1, li.size))
            }
            defs.addMethod(apply)

            val ifElse = NativeMethod(
                emptyList(),
                Params.of("if-else", "cond", "ifTrue", "ifFalse")
            ) { (cond, ifTrue, ifFalse) ->
                if (cond.toBool()) ifTrue else ifFalse
            }
            defs.addMethod(ifElse)

            val lazy = NativeMethod(
                emptyList(),
                Params.of("lazy", "arg")
            ) { (arg) ->
                MLazy.cons(arg)
            }
            defs.addMethod(lazy)

            val size = createNativeMethod(
                { (li) -> li.asCont().size().toM() },
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


            val filter = NativeMethod(
                emptyList(),
                VarargParams(listOf("filter","func", "cont"), "*args"),
            ) { args ->
                val fn = args[0].asFunc()
                args[1].asCont().iterable().filter { fn.evalWithArgs(defs, args.subListOrEmpty(2)
                    .listWithHead(it.anyToM())).toBool() }.toM()
            }
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

            val assert = NativeMethod(
                emptyList(),
                Params.of("assert", "bool")
            ) { (bool) ->
                if (!bool.toBool()) {
                    throw EvalException("Assert failed")
                }
                Void()
            }
            defs.addMethod(assert)
        }

        private fun createAndAddUnary(
            name: String,
            behavior: (List<Expr>) -> Expr,
            defs: Definitions
        ) {
            val method = NativeMethod(emptyList(), Params.of(name, "arg"), behavior)
            defs.addMethod(method)
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