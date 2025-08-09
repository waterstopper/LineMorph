import org.llesha.Pipeline
import org.llesha.expr.Expr
import kotlin.test.assertTrue

/**
 * @author al.kononov
 */
object TestFactory {
    fun parseWithLoad(input: String): Expr {
        return Pipeline.evalWithPos(
            """
            load("file")
            load("lib")
            load("natives")
            load("ops")
            load("string")
            
        """.trimIndent() + input
        )
    }

    fun failWithLoad(input: String, msg: String) {
        try {
            parseWithLoad(input)
        } catch (e: Exception) {
            assertTrue(e.message!!.contains(msg))
        }
    }
}