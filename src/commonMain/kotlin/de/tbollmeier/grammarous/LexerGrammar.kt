package de.tbollmeier.grammarous

class LexerGrammar(
        val caseSensitive: Boolean,
        val whiteSpace: Set<Char>
) {
    private val _keywordTypes = mutableMapOf<String, String>()
    private val _tokenTypes = mutableListOf<TokenType>()
    private val _stringTypes = mutableListOf<StringType>()
    private val _commentTypes = mutableListOf<CommentType>()

    val keywordTypes: Map<String, String>
        get() = _keywordTypes
    val tokenTypes: List<TokenType>
        get() = _tokenTypes
    val stringTypes: List<StringType>
        get() = _stringTypes
    val commentTypes: List<CommentType>
        get() = _commentTypes

    fun defineKeyword(content: String, name: String = "") {
        val keywordName = if (name.isEmpty()) content.toUpperCase() else name
        if (caseSensitive) {
            _keywordTypes[content] = keywordName
        } else {
            _keywordTypes[content.toUpperCase()] = keywordName
        }
    }

    fun defineToken(name: String, pattern: String) {
        _tokenTypes.add(TokenType(name, Regex(pattern)))
    }

    fun defineString(name: String, begin: String, end: String, escape: String?=null) {
        _stringTypes.add(StringType(name, begin, end, escape))
    }

    fun defineComment(name: String, begin: String, end: String) {
        _commentTypes.add(CommentType(name, begin, end))
    }

    class KeywordType(
        val name: String,
        val content: String
    )

    class TokenType(
        val name: String,
        val regex: Regex
    )

    class StringType(
        val name: String,
        val begin: String,
        val end: String,
        val escape: String?
    )

    class CommentType(
        val name: String,
        val begin: String,
        val end: String,
    )

}
