package org.llesha

import kotlinx.serialization.json.*
import org.llesha.CastUtils.toM
import org.llesha.eval.*
import org.llesha.expr.Expr
import org.llesha.expr.Ref
import org.llesha.type.*
import kotlin.time.Instant

/**
 * @author al.kononov
 */
object Utils {
    val YEAR_DATE_REGEX = Regex("(\\d\\d\\d\\d)-(\\d\\d)-(\\d\\d)")
    val SIGNATURE_REGEX = Regex("([a-zA-Z]([\\w-]*\\w)?)@(\\d+|\\*)")

    fun List<Expr>.replaceElem(index: Int, elem: Expr): List<Expr> {
        val mutList = this.toMutableList()
        mutList[index] = elem
        return mutList
    }

    fun dateToInstant(date: String): Instant {
        return Instant.parse("${date}T00:00:00Z")
    }

    fun argsToParams(args: List<Expr>): Params {
        if (args.isEmpty()) {
            return Params(emptyList())
        }
        if (args.last() is Vararg) {
            return VarargParams(refsToString(args.subList(0, args.lastIndex)), (args.last() as Vararg).name)
        }
        return Params(refsToString(args))
    }

    fun refsToString(refs: List<Expr>): List<String> = refs.map {
        if (it !is Ref) {
            throw IllegalArgumentException("Argument is not an identifier")
        }
        it.name
    }

    fun Expr.compare(other: Expr): Int {
        assertSameType(other)
        return when (this) {
            is MBool -> this.bool.compareTo((other as MBool).bool)
            is MNumber -> this.number.compareTo((other as MNumber).number)
            is MString -> this.string.compareTo((other as MString).string)
            is MList -> this.list.size.compareTo((other as MList).list.size)
            is MMap -> this.map.size.compareTo((other as MMap).map.size)
            else -> throw IllegalArgumentException("Argument is not comparable")
        }
    }

    private fun Expr.assertSameType(other: Expr) {
        if (other::class != this::class) {
            throw IllegalArgumentException("Expected instances of same types for comparison")
        }
    }

    fun parseJson(value: String): Expr {
        val json = Json.parseToJsonElement(value)
        return parseJsonElem(json)
    }

    fun parseJsonElem(json: JsonElement): Type {
        return when (json) {
            is JsonObject -> MMap(json.mapValues { parseJsonElem(it.value) })
            is JsonArray -> MList(json.map { parseJsonElem(it) })
            is JsonPrimitive -> {
                if (json.isString) {
                    return MString(json.content)
                }
                if (json.content == "true") {
                    return MBool(true)
                }
                if (json.content == "false") {
                    return MBool(false)
                }
                return MNumber(json.content.toLong())
            }

            else -> throw IllegalArgumentException("Unknown json type")
        }
    }

    fun Expr.plus(other: Expr): Expr {
        if (this is MList) {
            return MList(this.list + other)
        }
        if (this is MString) {
            return MString(this.string + other.toString())
        }
        assertSameType(other)
        return when (this) {
            is MNumber -> MNumber(this.number.plus((other as MNumber).number))
            else -> throw IllegalArgumentException("Unsupported operation plus for types")
        }
    }

    fun Expr.minus(other: Expr): Expr {
        if (this is MList) {
            return (this.list - other).toM()
        }
        if (this is MMap) {
            return (this.map - other.toString()).toM()
        }
        assertSameType(other)
        return when (this) {
            is MNumber -> MNumber(this.number.minus((other as MNumber).number))
            else -> throw IllegalArgumentException("Unsupported operation minus for types")
        }
    }

    fun <T> List<T>.subListOrEmpty(fromIndex: Int): List<T> {
        if (lastIndex < fromIndex)
            return mutableListOf()
        return subList(fromIndex, size)
    }

    fun <T> List<T>.listWithHead(head: T): List<T> {
        val res = mutableListOf(head)
        res.addAll(this)
        return res
    }

    fun evalMethod(method: Method, args: List<Expr>, defs: Definitions): Expr {
        val evaluatedArgs = if (method.isLazy()) args.map { MLazy.cons(it) } else args.map { it.eval(defs) }
        val argList = method.annotations["ArgList"]

        if (argList != null) {
            val listArg = argList.args.first()
            val listIndex = method.params.names().indexOf(listArg)
            if (evaluatedArgs[listIndex] is MList) {
                return MList((evaluatedArgs[listIndex] as MList).list.map {
                    method.call(evaluatedArgs.replaceElem(listIndex, it), defs)
                })
            }
        }

        return method.call(evaluatedArgs, defs)
    }
}
