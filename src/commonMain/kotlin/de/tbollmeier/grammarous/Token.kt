package de.tbollmeier.grammarous

class Token(
    val type: String,
    val position: SourcePosition,
    val lexeme: String = ""
)