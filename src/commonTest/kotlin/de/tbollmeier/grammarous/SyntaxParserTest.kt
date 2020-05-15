package de.tbollmeier.grammarous

import kotlin.test.*
import kotlin.time.measureTime

class CalcGrammar : Grammar() {

    init {

        defineRule("expr",
            rule("term", "term"),
            many(
                oneOf(
                    terminal("PLUS", "op"),
                    terminal("MINUS", "op")),
                rule("term", "term")))

        defineRule("term",
            rule("factor", "fact"),
            many(
                oneOf(
                    terminal("MULT", "op"),
                    terminal("DIV", "op")),
                rule("factor", "fact")))

        transform("term") {
            ast -> if (ast.children.size == 1) {
                val child = ast.children[0]
                child.id = ""
                child
            } else {
                ast
            }
        }

        defineRule("factor", oneOf(
            terminal("IDENT"),
            terminal("NUMBER"),
            sequence(
                terminal("LPAR"),
                rule("expr"),
                terminal("RPAR"))))

        transform("factor") {
            ast -> if (ast.children.size == 1) {
                ast.children[0]
            } else {
                ast.children[2]
            }
        }

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

        assertTrue(result is Result.Success, (result as? Result.Failure)?.message)

        val ast = result.value

        println(AstJsonFormatter().toJson(ast))

    }

}