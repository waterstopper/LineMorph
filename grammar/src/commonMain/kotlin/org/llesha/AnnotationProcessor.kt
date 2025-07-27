package org.llesha

import org.llesha.eval.NamedParam
import org.llesha.eval.NamedParams
import org.llesha.expr.Annotation

/**
 * @author al.kononov
 */
object AnnotationProcessor {
    fun init(): MutableList<Annotation> {
        return mutableListOf()
    }

    fun MutableList<Annotation>.withArgList(arg: String): MutableList<Annotation> {
        this.add(Annotation("ArgList", listOf(arg)))
        return this
    }

    fun processAsText(fnName: String, parameters: List<String>, asText: Annotation): NamedParams {
        if (asText.args.size + 1 != parameters.size) {
            throw Exception("Expected ${parameters.size} params in @AsText, got ${asText.args.size + 1}")
        }
        val result = mutableListOf(NamedParam(parameters.first(), fnName))
        asText.args.indices.forEach {
            result.add(NamedParam(parameters[it + 1], asText.args[it]))
        }
        return NamedParams(result)
    }
}