package de.tbollmeier.grammarous

interface Parser {
    fun parse(tokens: StreamBuffer<Token>): Result<List<Ast>>
}

typealias AstTransformFn = (Ast) -> Ast

class TokenParser(private val tokenType: String,
                  private val grammar: Grammar,
                  private val id: String = "") : Parser {

    override fun parse(tokens: StreamBuffer<Token>): Result<List<Ast>> {

        val token = tokens.peek()
        return if (token != null) {
            if (token.type == tokenType) {
                tokens.next()
                var ast = Ast(tokenType, token.lexeme, id)
                val transformer = grammar.getTransform(tokenType)
                if (transformer != null) {
                    ast = transformer.invoke(ast)
                    ast.id = id
                }
                Result.Success(listOf(ast))
            } else {
                Result.Failure("Unexpected token $tokenType")
            }
        } else {
            Result.Failure("End of input")
        }
    }

}

class SequenceParser(private vararg val elements: Parser) : Parser {

    override fun parse(tokens: StreamBuffer<Token>): Result<List<Ast>> {

        val astNodes = mutableListOf<Ast>()
        tokens.startChange()

        for (element in elements) {
            when (val parseResult = element.parse(tokens)) {
                is Result.Success -> {
                    astNodes.addAll(parseResult.value)
                }
                is Result.Failure -> {
                    tokens.undoChange()
                    return parseResult
                }
            }
        }

        tokens.commitChange()
        return Result.Success(astNodes)
    }

}

class OneOfParser(private vararg val elements: Parser) : Parser {

    override fun parse(tokens: StreamBuffer<Token>): Result<List<Ast>> {

        for (element in elements) {
            tokens.startChange()
            when (val parseResult = element.parse(tokens)) {
                is Result.Success -> {
                    tokens.commitChange()
                    return parseResult
                }
                is Result.Failure -> {
                    tokens.undoChange()
                }
            }
        }

        return Result.Failure("No matching alternative found for token ${tokens.peek()?.lexeme}")
    }

}

open class MinMaxParser(private val parser: Parser,
                        private val minOcc: Int,
                        private val maxOcc: Int = -1) : Parser {

    override fun parse(tokens: StreamBuffer<Token>): Result<List<Ast>> {

        val astNodes = mutableListOf<Ast>()
        tokens.startChange()

        for (i in 1..minOcc) {
            when (val parseResult = parser.parse(tokens)) {
                is Result.Success -> {
                    astNodes.addAll(parseResult.value)
                }
                is Result.Failure -> {
                    tokens.undoChange()
                    return parseResult
                }
            }
        }

        if (maxOcc == -1) {
            loop@ while (true) {
                tokens.startChange()
                when (val parseResult = parser.parse(tokens)) {
                    is Result.Success -> {
                        tokens.commitChange()
                        astNodes.addAll(parseResult.value)
                    }
                    is Result.Failure -> {
                        tokens.undoChange()
                        break@loop
                    }
                }
            }
        } else {
            loop@ for (i in (minOcc+1)..maxOcc) {
                tokens.startChange()
                when (val parseResult = parser.parse(tokens)) {
                    is Result.Success -> {
                        tokens.commitChange()
                        astNodes.addAll(parseResult.value)
                    }
                    is Result.Failure -> {
                        tokens.undoChange()
                        break@loop
                    }
                }
            }
        }

        tokens.commitChange()

        return Result.Success(astNodes)
    }

}

class RuleParser(private val name: String,
                 private val grammar: Grammar,
                 private val id: String="") : Parser {

    override fun parse(tokens: StreamBuffer<Token>): Result<List<Ast>> {

        val parser = grammar.getRule(name)
        val transformer = grammar.getTransform(name)

        return when (val parseResult = parser.parse(tokens)) {
            is Result.Success -> {
                var ast = Ast(name, "", id)
                for (child in parseResult.value) {
                    ast.addChild(child)
                }
                if (transformer != null) {
                    ast = transformer.invoke(ast)
                    ast.id = id
                }
                Result.Success(listOf(ast))
            }
            is Result.Failure -> parseResult
        }

    }

}
