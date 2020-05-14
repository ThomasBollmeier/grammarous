package de.tbollmeier.grammarous

interface Lexer {
    fun scan(characters: Stream<Char>) : Stream<Token>
}