package de.tbollmeier.grammarous

import kotlin.test.*

class LexerTest {

    private lateinit var cut: Lexer

    @BeforeTest
    fun setUp() {
        val grammar = LexerGrammar(caseSensitive = false, whiteSpace = setOf(' ', '\t', '\n')).apply {

            defineString("SINGLE_QUOTED", "'", "'", "\\")
            defineComment("LINE_COMMENT", "//", "\n")

        }

        cut = LexerFactory.createLexer(grammar)
    }

    @Test
    fun next() {

        val code = """
            question = 'O\'Rly?'
            answer = 42//a comment
        """.trimIndent()

        val charStream = createStringCharStream(code)
        val tokenStream = cut.scan(charStream)

        while (true) {
            val token = tokenStream.next() ?: break
            println("${token.type}: ${token.lexeme}")
        }

    }

}