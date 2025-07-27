import org.llesha.Pipeline
import org.llesha.type.MList
import org.llesha.type.MString
import kotlin.test.Test
import kotlin.test.assertEquals

class GrammarTest {
    @Test
    fun fileTest() {
        val input = """
            @AsText(by, from)
            fn select(type, groupType, input) {
                groups <- split(input, groupType)
                return find(type, groups)
            }

            select("\\d+", "\n", "ds233131dsaddq\nd32ds\n232ds233dsa")
        """.trimIndent()

        val res = Pipeline.eval(input)
        assertEquals(MList(listOf(MString("233131"), MString("32"), MString("232"))), res)
        println(res)
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