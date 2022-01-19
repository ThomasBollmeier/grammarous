package de.tbollmeier.grammarous

import kotlin.test.*

class ParserTest {

    @BeforeTest
    fun setUp() {
    }

    @Test
    fun success() {

        val g = Grammar()

        val parser = SequenceParser(
            TokenParser(TokenType.LET, g),
            TokenParser(TokenType.IDENTIFIER, g),
            TokenParser(TokenType.ASSIGN, g),
            TokenParser(TokenType.INT, g)
        )

        val pos = SourcePosition(1, 1)
        val tokens = listOf(
            Token(TokenType.LET, pos, "let"),
            Token(TokenType.IDENTIFIER, pos, "answer"),
            Token(TokenType.ASSIGN, pos, "="),
            Token(TokenType.INT, pos, "42")
        )
        val buffer = StreamBuffer(ListStream(tokens))

        val parseResult = parser.parse(buffer)

        assertTrue(parseResult is Result.Success)

        val astNodes = parseResult.value
        assertEquals(4, astNodes.size)

        for ((idx, token) in tokens.withIndex()) {
            assertEquals(token.type, astNodes[idx].name)
            assertEquals(token.lexeme, astNodes[idx].value)
        }

    }

    @Test
    fun failure() {

        val g = Grammar()

        val parser = SequenceParser(
            TokenParser(TokenType.LET, g),
            TokenParser(TokenType.IDENTIFIER, g),
            TokenParser(TokenType.ASSIGN, g),
            TokenParser(TokenType.INT, g)
        )

        val pos = SourcePosition(1, 1)
        val tokens = listOf(
            Token(TokenType.LET, pos, "let"),
            Token(TokenType.INT, pos, "42"),
            Token(TokenType.ASSIGN, pos, "="),
            Token(TokenType.IDENTIFIER, pos, "answer")
        )
        val buffer = StreamBuffer(ListStream(tokens))

        val parseResult = parser.parse(buffer)

        assertTrue(parseResult is Result.Failure)

        val errorMessage = parseResult.message
        assertEquals("Unexpected token INT", errorMessage)

        assertEquals(tokens[0], buffer.peek())

    }

}