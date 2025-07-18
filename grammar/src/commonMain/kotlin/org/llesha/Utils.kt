package org.llesha

import org.llesha.expr.Expr
import org.llesha.expr.Ref
import kotlin.time.Instant

/**
 * @author al.kononov
 */
object Utils {
    val YEAR_DATE_REGEX = Regex("(\\d\\d\\d\\d)-(\\d\\d)-(\\d\\d)")

    fun dateToInstant(date: String): Instant {
        return Instant.parse("${date}T00:00:00Z")
    }

    fun argsToParams(args: List<Expr>): List<String> {
        return args.map {
            if (it !is Ref) {
                throw IllegalArgumentException("Argument is not an identifier")
            }
            it.name
        }
    }
}