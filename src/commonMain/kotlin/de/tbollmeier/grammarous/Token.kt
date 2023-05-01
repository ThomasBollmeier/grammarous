package de.tbollmeier.grammarous

const val ERROR_TOKEN = "ERROR_TOKEN"

class Token(
    val type: String,
    val position: SourcePosition,
    val lexeme: String = ""
)