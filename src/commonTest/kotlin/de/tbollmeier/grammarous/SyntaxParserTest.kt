package de.tbollmeier.grammarous

import kotlin.test.*

class CalcGrammar : Grammar() {

    init {

        defineRule("expr",
            rule("term"),
            many(
                oneOf(
                    terminal("PLUS"),
                    terminal("MINUS")),
                rule("term")))

        transform("expr", this::transformOperations)

        defineRule("term",
            rule("factor"),
            many(
                oneOf(
                    terminal("MULT"),
                    terminal("DIV")),
                rule("factor")))

        transform("term", this::transformOperations)

        defineRule("factor", oneOf(
            terminal("IDENT"),
            terminal("NUMBER"),
            sequence(
                terminal("LPAR"),
                rule("expr"),
                terminal("RPAR"))))

        transform("factor", this::transformFactor)

    }

    private fun transformOperations(ast: Ast) : Ast {
        return if (ast.children.size == 1) {
            val child = ast.children[0]
            child.id = ""
            child
        } else {
            val numOperators = (ast.children.size - 1) / 2
            var result = createBinOp(ast.children[1], ast.children[0], ast.children[2])
            for (i in 2..numOperators) {
                val idx = 1 + 2 * (i - 1)
                result = createBinOp(ast.children[idx], result, ast.children[idx + 1])
            }
            return result
        }
    }

    private fun transformFactor(ast: Ast) : Ast {
        return if (ast.children.size == 1) {
            ast.children[0]
        } else {
            ast.children[1]
        }
    }

    private fun createBinOp(op: Ast, left: Ast, right: Ast) : Ast {
        val result = Ast("binop")
        result.attrs["operator"] = op.value
        left.id = ""
        result.addChild(left)
        right.id = ""
        result.addChild(right)
        return result
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
            Token("MINUS", pos, "-"),
            Token("LPAR", pos, "("),
            Token("NUMBER", pos, "2"),
            Token("PLUS", pos, "+"),
            Token("NUMBER", pos, "3"),
            Token("RPAR", pos, ")"),
            Token("MULT", pos, "*"),
            Token("IDENT", pos, "factor")))

        val parser = SyntaxParser(CalcGrammar())

        val result = parser.parse(tokens)

        assertTrue(result is Result.Success, (result as? Result.Failure)?.message)

        val ast = result.value

        println(AstXmlFormatter().toXml(ast))

    }

}