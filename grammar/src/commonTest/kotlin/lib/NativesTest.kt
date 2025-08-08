package lib

import TestFactory
import kotlin.test.Test

/**
 * @author al.kononov
 */
class NativesTest {
    @Test
    fun testApply() {

    }

    @Test
    fun testIfElse() {
        val input = """
            b <- if-else(true, 1, 2)
        """.trimIndent()
        TestFactory.parseWithLoad(input)
    }

    @Test
    fun testSize() {
        val inp = """
            fn testCases(li) {
                print("cases")
                print(li)
                size <- li[0]
                arg <- li[1]
                for-each(testCase@2, li[1], li[0])
            }

            fn testCase(case, size) {
                print(case)
                assert(eq(size, size(case)))
            }

            cases <- [["", [], {}], ["a", [1], [""], {"a":[1,2, {}]}], ["[]", "{}"], ["abc", [{}, {}, {}]], [{"a":[], "b":[], "c":{"d":1}, "e":{}}]]
            withIndex <- indexed(cases)
            for-each(testCases@1, withIndex)
        """.trimIndent()

        TestFactory.parseWithLoad(inp)

        val altInp = """
            @ArgList(li)
            fn testCases(li) {
                print("cases")
                print(li)
                size <- li[0]
                arg <- li[1]
                for-each(testCase@2, li[1], li[0])
            }

            fn testCase(case, size) {
                print(case)
                assert(eq(size, size(case)))
            }

            cases <- [["", [], {}], ["a", [1], [""], {"a":[1,2, {}]}], ["[]", "{}"], ["abc", [{}, {}, {}]], [{"a":[], "b":[], "c":{"d":1}, "e":{}}]]
            withIndex <- indexed(cases)
            testCases(withIndex)
        """.trimIndent()

        TestFactory.parseWithLoad(altInp)
    }

    @Test
    fun testAssert() {

    }
}