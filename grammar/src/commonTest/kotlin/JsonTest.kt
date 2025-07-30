import org.llesha.Utils
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * @author al.kononov
 */
class JsonTest {
    @Test
    fun testParse() {
        val expressions = listOf(
            "{}",
            "[]",
            "true",
            "false",
            """""""",
            """"123"""",
            """"abc"""",
            """"{}"""",
            """"[]"""",
            "1",
            "1000000",
            """{"a":[1,"abc",[{}]]}""",
            """{"a":[1,"abc",[{}]],"b":false}"""
        )
        expressions.forEach {
            assertEquals(it, Utils.parseJson(it).toStr())
        }
    }
}