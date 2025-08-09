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
            assert(eq())
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
                testCase(li[1], li[0])
            }

            @ArgList(case)
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
    fun testTypeChecks() {
        val input = """
            fn identity(arg) {
                return arg
            }
            
            assert(isLazy(lazy(1)))
            assert(isLazy(lazy("")))
            assert(isLazy(lazy([])))
            assert(isLazy(lazy(identity({}))))
            assert(isLazy(identity(lazy(identity({})))))
            
            assert(isNumber(1))
            assert(isNumber(100))
            assert(isNumber([1][0]))
            assert(isNumber({"a":1}["a"]))
            assert(isNumber(identity({"a":1}["a"])))
            
            assert(isString(""))
            assert(isString("∆ª˚ª•ª∆“…∫¶§ƒ"))
            assert(isString(["str"][0]))
            assert(isString({"a":"b"}["a"]))
            assert(isString(identity({"a":"b"}["a"])))
            
            assert(isList([]))
            assert(isList([1,2,3]))
            assert(isList([[],1,2][0]))
            assert(isList({"a":[1,2,3]}["a"]))
            assert(isList(identity({"a":[1,2,3]}["a"])))

            assert(isMap({}))
            assert(isMap({"a":1, "b": []}))
            assert(isMap([{},1,2][0]))
            assert(isMap({"a":{}}["a"]))
            assert(isMap(identity({"a":{}}["a"])))
            
            assert(isBool(true))
            assert(isBool(false))
            assert(isBool([true,1,2][0]))
            assert(isBool({"a":false}["a"]))
            assert(isBool(identity({"a":false}["a"])))
            
            assert(isFunc(abc@2))
            assert(isFunc(identity@1))
            assert(isFunc(identity@*))
            assert(isFunc(identity(identity@*)))
            """.trimIndent()

        TestFactory.parseWithLoad(input)
    }

    @Test
    fun testFilter() {

    }

    @Test
    fun testIndexed() {
        val input = """
            str <- "abc"
            indexed <- indexed(str)
            assert(eq([[0,"a"],[1,"b"],[2,"c"]],indexed))
            
            li <- [0,1,2]
            indexedLi <- indexed(li)
            assert(eq([[0,0],[1,1],[2,2]],indexedLi))
        """.trimIndent()

        TestFactory.parseWithLoad(input)

        val failIndexed = """
            num <- 1
            indexed <- indexed(num)
        """.trimIndent()

        TestFactory.failWithLoad(failIndexed, "Expected container")
    }

    @Test
    fun testParseJson() {
        val input = """
            fn parse(arg) {
                parsed <- json(arg)
                unparsed <- parse-json(parsed)
                assert(eq(arg, unparsed))
            }
            parse(true)
            parse(false)

            parse(1)
            parse(100)
            parse([100][0])

            parse("")
            parse("®ˆ£ºª£ˆ£™´£™˚ºçøπ´®ƒ∑")
            parse("123")
            
            parse([])
            parse([1,2,3])
            parse([1,2,3, {}, [1,2,3], ""])
            
            parse({})
            parse({"a": [], "b":"", "c":{"d":1}, "e":{}})
        """.trimIndent()

        TestFactory.parseWithLoad(input)
    }
}