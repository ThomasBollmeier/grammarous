package de.tbollmeier.grammarous

class LexerGrammar(
        private val caseSensitive: Boolean,
        private val whiteSpace: Set<Char>
) {
    private val tokenTypes = mutableListOf<TokenType>()
    private val stringTypes = mutableListOf<StringType>()
    private val commentTypes = mutableListOf<CommentType>()

    fun defineToken(name: String, pattern: String) {
        tokenTypes.add(TokenType(name, Regex(pattern)))
    }

    fun defineString(name: String, begin: String, end: String, escape: String) {
        stringTypes.add(StringType(name, begin, end, escape))
    }

    fun defineComment(name: String, begin: String, end: String) {
        commentTypes.add(CommentType(name, begin, end))
    }

}

class TokenType(
        val name: String,
        val regex: Regex
)

class StringType(
        val name: String,
        val begin: String,
        val end: String,
        val escape: String
)

class CommentType(
        val name: String,
        val begin: String,
        val end: String,
)
