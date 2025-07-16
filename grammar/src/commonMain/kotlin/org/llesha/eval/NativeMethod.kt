package org.llesha.eval

class NativeMethod(params: List<Param>): Method(params) {
    companion object {
        fun initNativeMethods(defs: Definitions) {
            val print = createNativeMethod("print", "arg")
            defs.addMethod(print)

            val load = createNativeMethod("load", "codePath")
            defs.addMethod(load)

            val save = createNativeMethod("write", "arg", "to", "path")
            defs.addMethod(save)

            val read = createNativeMethod("read", "arg", "from", "path")
            defs.addMethod(read)

            val json = createNativeMethod("json", "arg")
            defs.addMethod(json)
        }
    }
}