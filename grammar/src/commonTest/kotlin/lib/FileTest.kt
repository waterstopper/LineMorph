package lib

import TestFactory
import kotlin.test.Test

/**
 * @author al.kononov
 */
class FileTest {
    @Test
    fun testFile() {
        val input = """
            path <- "a.txt"
            content <- "abc"

            assert-not-exists(path)
            write(content, path)
            assert-eq(content, read(path))
            assert-exists(path)
            delete(path)
            assert-not-exists(path)
        """.trimIndent()

        TestFactory.parseWithLoad(input)
    }
}