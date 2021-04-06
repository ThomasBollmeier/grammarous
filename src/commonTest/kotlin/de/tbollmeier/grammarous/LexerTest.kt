package de.tbollmeier.grammarous

import kotlin.test.*

class LexerTest {

    private lateinit var cut: Lexer

    @BeforeTest
    fun setUp() {
        val grammar = LexerGrammar(caseSensitive = false, whiteSpace = setOf(' ', '\t', '\n')).apply {

            defineString("SINGLE_QUOTED_STR", "'", "'", "@")

            defineComment("LINE_COMMENT", "--", "\n")

            defineToken("ASSIGN", "<-")
            defineToken("IDENT", "[a-z][a-z0-9_]*")
            defineToken("NUMBER", "\\d+")

        }

        cut = LexerFactory.createLexer(grammar)
    }

    @Test
    fun next() {

        val code = """
            question <- 'O@'Rly?'
            answer <- 42 -- the answer to everything
        """.trimIndent()

        val charStream = createStringCharStream(code)
        val tokenStream = cut.scan(charStream)

        var tokens = mutableListOf<Token>()

        while (true) {
            tokens.add(tokenStream.next() ?: break)
        }

        assertEquals(6, tokens.size)
        assertEquals("IDENT", tokens[0].type)
        assertEquals("ASSIGN", tokens[1].type)
        assertEquals("SINGLE_QUOTED_STR", tokens[2].type)
        assertEquals("IDENT", tokens[3].type)
        assertEquals("ASSIGN", tokens[4].type)
        assertEquals("NUMBER", tokens[5].type)

    }

}