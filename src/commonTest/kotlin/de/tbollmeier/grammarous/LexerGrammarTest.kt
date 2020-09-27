package de.tbollmeier.grammarous

import kotlin.test.*

class LexerGrammarTest {

    private lateinit var cut: LexerGrammar

    @BeforeTest
    fun setUp() {
        cut = LexerGrammar(caseSensitive = false, whiteSpace = setOf(' ', '\t', '\n'))
    }

}