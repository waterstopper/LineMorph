import org.llesha.Pipeline
import kotlin.test.Test

class GrammarTest {
    @Test
    fun testGrammar() {
        val input = """
            # a
            a <- `a    
            a <- 232
            
            
            b <- true
            : b as 232
            @ArgList() fn a() {}""".trimIndent()

        Pipeline.parse(input)

        Pipeline.eval(input)
    }

    @Test
    fun testSingleExpressions() {
        val exprs = listOf(
            ":b as 2",
            "a <- `a",
            "`abc",
            "231432",
            """"a"""",
            "2022-12-23"
        )

        exprs.forEach { Pipeline.parse(it) }
    }
}