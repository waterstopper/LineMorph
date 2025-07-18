package org.llesha.eval

import org.llesha.expr.Expr
import org.llesha.expr.Void
import org.llesha.native.read
import org.llesha.native.write
import org.llesha.type.MString

class NativeMethod(params: List<Param>, val behavior: (List<Expr>) -> Expr) : Method(params) {
    override fun call(args: List<Expr>, defs: Definitions): Expr {
        return behavior(args)
    }

    companion object {
        fun initNativeMethods(defs: Definitions) {
            val print = createNativeMethod(
                { (li) -> println(li); Void() },
                "print", "arg"
            )
            defs.addMethod(print)

            val load = createNativeMethod(
                { (li) -> read(li.toString()); Void() },
                "load", "codePath"
            )
            defs.addMethod(load)

            val write = createNativeMethod(
                { (arg, path) ->
                    write(path.toString(), arg.toString()); Void() },
                "write", "arg", "to", "path"
            )
            defs.addMethod(write)

            val read = createNativeMethod(
                { (li) -> MString(read(li.toString())) },
                "read", "path"
            )
            defs.addMethod(read)

            val json = createNativeMethod({ (li) -> read(li.toString()); Void() },
                "json", "arg")
            defs.addMethod(json)

            val parseJson = createNativeMethod({ (li) -> read(li.toString()); Void() },
                "parseJson", "string")
            defs.addMethod(parseJson)
        }
    }
}