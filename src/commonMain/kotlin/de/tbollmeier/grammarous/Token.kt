package de.tbollmeier.grammarous

const val ERROR_TOKEN = "ERROR_TOKEN"

class Token(
    val type: String,
    val startPosition: SourcePosition,
    val lexeme: String = ""
) {
    val endPosition = lexeme.drop(1).fold(startPosition) { pos, ch -> pos.advance(ch) }
}