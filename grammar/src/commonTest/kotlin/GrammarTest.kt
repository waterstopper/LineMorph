import org.llesha.Pipeline
import kotlin.test.Test

class GrammarTest {
    @Test
    fun fileTest() {
        val input = """
            print(read("/Users/waterstop/Desktop/a.txt"))
        """.trimIndent()

        Pipeline.eval(input)
    }

    @Test
    fun testGrammar() {
        val input = """
            # a
            a <- `a    
            a <- 232
            
            
            b <- true
            ^ b as 232
            @ArgList() fn a() {}""".trimIndent()

        Pipeline.parse(input)

        Pipeline.eval(input)
    }

    @Test
    fun testSingleExpressions() {
        val exprs = listOf(
            "^b as 2",
            "a <- `a",
            "a <- {\"a\":1}",
            "a <- a[\"231432\"][1]",
            "a <- {}[\"231432\"][1]",
            "a <- {\"s\":21,\"a\":{\"d\":[1,2,a]}}[1][qwer]",
            """a <- "a"""",
            "a <- 2022-12-23"
        )

        exprs.forEach { println(Pipeline.parse(it)) }
    }
}