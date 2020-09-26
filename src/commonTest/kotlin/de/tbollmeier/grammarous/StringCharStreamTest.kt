package de.tbollmeier.grammarous

import kotlin.test.*

class StringCharStreamTest {

    @Test
    fun consumeAll() {

        val expected = "(λ (x y) (+ x y))"
        var actual = ""
        val cut = createStringCharStream(expected)

        while (cut.hasNext()) {
            val ch = cut.next()
            actual += ch
        }

        assertEquals(expected, actual)

        println(actual)

    }

}