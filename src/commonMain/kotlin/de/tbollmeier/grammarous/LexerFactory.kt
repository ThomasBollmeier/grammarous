package de.tbollmeier.grammarous

object LexerFactory {

    fun createLexer(grammar: LexerGrammar): Lexer = LexerImpl(grammar)

}