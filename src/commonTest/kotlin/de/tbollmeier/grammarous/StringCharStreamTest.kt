package de.tbollmeier.grammarous

import kotlin.test.*

class StringCharStreamTest {

    @Test
    fun consumeAll() {

        val expected = "(λ (x y) (+ x y))"
        var actual = ""
        val cut = createStringCharStream(expected)

        while (true) {
            val ch = cut.next()
            if (ch != null) {
                actual += ch
            } else {
                break
            }
        }

        assertEquals(expected, actual)

        println(actual)

    }

}