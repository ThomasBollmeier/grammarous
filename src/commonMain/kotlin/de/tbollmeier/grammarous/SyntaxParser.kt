package de.tbollmeier.grammarous

class SyntaxParser(private val grammar: Grammar) {

    fun parse(tokens: Stream<Token>) : Result<Ast> {

        if (grammar.startRuleName == null) return Result.Failure("No start rule defined")

        val buffer = StreamBuffer(tokens)
        val parseResult = grammar.rule(grammar.startRuleName!!).parse(buffer)

        if (buffer.hasNext()) {
            return Result.Failure("Unexpected token '${buffer.peek()?.lexeme}'")
        }

        return when (parseResult) {
            is Result.Success -> {
                if (parseResult.value.size == 1) {
                    Result.Success(parseResult.value[0])
                } else {
                    Result.Failure("No AST could be created")
                }
            }
            is Result.Failure -> Result.Failure(parseResult.message)
        }
    }

}