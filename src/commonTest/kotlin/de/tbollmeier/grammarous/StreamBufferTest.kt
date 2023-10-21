package de.tbollmeier.grammarous

import kotlin.test.*

class StreamBufferTest {

    private lateinit var buffer : StreamBuffer<Int>

    @BeforeTest
    fun setUp() {
        buffer = StreamBuffer(ListStream(listOf(1, 2, 3, 4, 5)))
    }

    @Test
    fun hasNext() {
        assertTrue(buffer.hasNext())
        repeat(4) {
            buffer.next()
        }
        assertTrue(buffer.hasNext())
        buffer.next()
        assertFalse(buffer.hasNext())
    }

    @Test
    fun peek() {
        assertEquals(1, buffer.peek())
        assertEquals(5, buffer.peek(4))
        assertEquals(null, buffer.peek(5))
        assertEquals(1, buffer.next())
    }

    @Test
    fun peekMany() {
        assertEquals(listOf(1, 2, 3), buffer.peekMany(3))
        buffer.next()
        buffer.next()
        buffer.next()
        assertEquals(listOf(4, 5), buffer.peekMany(3))
    }

    @Test
    fun change() {
        assertEquals(1, buffer.peek())
        buffer.next() // --> 1
        buffer.startChange()
        buffer.next() // --> 2
        buffer.startChange()
        buffer.next() // --> 3
        buffer.next() // --> 4
        buffer.commitChange()
        assertEquals(5, buffer.next())
        assertFalse(buffer.hasNext())
        buffer.undoChange()
        assertEquals(2, buffer.peek())
    }

}