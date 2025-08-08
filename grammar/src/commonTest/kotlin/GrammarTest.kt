import org.llesha.Pipeline
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

        val res = TestFactory.parseWithLoad(input)
        assertEquals("[233131,32,232]", res.toString())
    }

    @Test
    fun testSignature() {
        val input = """a <- split@2
            b <- apply(split@2, "text", "e")
        """.trimIndent()

        val res = TestFactory.parseWithLoad(input)
        assertEquals("[t,xt]", res.toString())
    }

    @Test
    fun varargTest() {
        val input = """fn vararg(*abc) {}""".trimIndent()

        val res = TestFactory.parseWithLoad(input)
    }

    @Test
    fun testGrammar() {
        val input = """
            # a
            a <- `a
            a <- 232
            
            
            b <- true
            #^ b as 232
            @ArgList() fn a() {}""".trimIndent()

        Pipeline.parse(input)

        Pipeline.eval(input)
    }

    @Test
    fun testSingleExpressions() {
        val exprs = listOf(
            "^b as 2",
            "a <- `a",
            """a <- {"a":1}""",
            """a <- a["231432"][1]""",
            """a <- {}["231432"][1]""",
            """a <- {"s":21,"a":{"d":[1,2,a]}}[1][qwer]""",
            """a <- "a"""",
            "a <- 2022-12-23"
        )

        exprs.forEach { println(Pipeline.parse(it)) }
    }
}