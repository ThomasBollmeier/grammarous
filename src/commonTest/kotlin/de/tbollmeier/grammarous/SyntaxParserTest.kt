package de.tbollmeier.grammarous

import kotlin.test.*

class CalcGrammar : Grammar() {

    init {

        defineRule("expr", sequence(
            rule("term", "term"),
            many(sequence(
                oneOf(
                    terminal("PLUS", "op"),
                    terminal("MINUS", "op")),
                rule("term", "term")))))

        defineRule("term", sequence(
            rule("factor", "fact"),
            many(sequence(
                oneOf(
                    terminal("MULT", "op"),
                    terminal("DIV", "op")),
                rule("factor", "fact")))))

        defineRule("factor", oneOf(
            terminal("IDENT"),
            terminal("NUMBER"),
            sequence(
                terminal("LPAR"),
                rule("expr"),
                terminal("RPAR"))))

    }

}

class SyntaxParserTest {

    @BeforeTest
    fun setUp() {
    }

    @Test
    fun success() {

        val pos = SourcePosition(1, 1)
        val tokens = ListStream(listOf(
            Token("NUMBER", pos, "1"),
            Token("PLUS", pos, "+"),
            Token("NUMBER", pos, "2"),
            Token("MULT", pos, "*"),
            Token("NUMBER", pos, "3")))

        val parser = SyntaxParser(CalcGrammar())

        val result = parser.parse(tokens)

        assertTrue(result is Result.Success)

        val ast = result.value

    }

}