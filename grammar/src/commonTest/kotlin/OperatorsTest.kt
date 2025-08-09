import kotlin.test.Test

/**
 * @author al.kononov
 */
class OperatorsTest {
    @Test
    fun testLogic() {
        val input = """
            assert-not(false)
            assert-not-not(true)

            assert-or(true, false)
            assert-not-or(false, false)

            assert-and(true, true)
            assert-and(not(false), true)
            assert-not-and(false, false)

            assert-leq(1, 2)
            assert-leq(2, 2)
            assert-geq(2, 2)
            assert-geq(3, 2)

            assert-not-leq(2, 1)
            assert-not-geq(2, 3)

            assert-lt(1, 2)
            assert-gt(3, 2)

            assert-not-lt(2, 1)
            assert-not-gt(2, 3)
        """.trimIndent()

        TestFactory.parseWithLoad(input)
    }
}