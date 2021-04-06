package de.tbollmeier.grammarous

import kotlin.test.*

fun createCalcGrammar(): Grammar {

    fun createBinOp(op: Ast, left: Ast, right: Ast) : Ast {
        val result = Ast("binop")
        result.attrs["operator"] = op.value
        left.id = ""
        result.addChild(left)
        right.id = ""
        result.addChild(right)
        return result
    }

    fun transformOperations(ast: Ast) : Ast {
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

    fun transformFactor(ast: Ast) : Ast {
        return if (ast.children.size == 1) {
            ast.children[0]
        } else {
            ast.children[1]
        }
    }

    return grammar {

        ruleDef("expr") {
            rule("term")
            many {
                oneOf {
                    terminal("PLUS")
                    terminal("MINUS")
                }
                rule("term")
            }
        } transformBy ::transformOperations

        ruleDef("term") {
            rule("factor")
            many {
                oneOf {
                    terminal("MULT")
                    terminal("DIV")
                }
                rule("factor")
            }
        } transformBy ::transformOperations

        ruleDef("factor") {
            oneOf {
                terminal("IDENT")
                terminal("NUMBER")
                sequence {
                    terminal("LPAR")
                    rule("expr")
                    terminal("RPAR")
                }
            }
        } transformBy ::transformFactor

    }

}


class DslTest {

    private lateinit var lexer: Lexer

    @BeforeTest
    fun setUp() {

        val lexerGrammar = LexerGrammar().apply {

            defineToken("IDENT", "[a-z]+")
            defineToken("NUMBER", "\\d+")

            defineToken("PLUS", "\\+")
            defineToken("MINUS", "-")
            defineToken("MULT", "\\*")
            defineToken("DIV", "/")
            defineToken("LPAR", "\\(")
            defineToken("RPAR", "\\)")

        }

        lexer = LexerFactory.createLexer(lexerGrammar)

    }

    @Test
    fun success() {

        val code = "1 - ((2 + 3) * factor)"
        val tokenStream = lexer.scan(createStringCharStream(code))

        val parser = SyntaxParser(createCalcGrammar())

        val result = parser.parse(tokenStream)

        assertTrue(result is Result.Success, (result as? Result.Failure)?.message)

        val ast = result.value

        println(AstXmlFormatter().toXml(ast))

    }

}