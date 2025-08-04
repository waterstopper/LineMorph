import org.llesha.Pipeline
import org.llesha.expr.Expr

/**
 * @author al.kononov
 */
object TestFactory {
    fun parseWithLoad(input: String): Expr {
        return Pipeline.eval("""
            load("file")
            load("lib")
            load("natives")
            load("ops")
            load("string")
            
        """.trimIndent()+input)
    }
}