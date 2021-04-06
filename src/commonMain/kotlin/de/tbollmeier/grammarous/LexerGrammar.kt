package de.tbollmeier.grammarous

class LexerGrammar(
        val caseSensitive: Boolean = true,
        val whiteSpace: Set<Char> = setOf(' ', '\t', '\n', '\r')
) {
    private val _tokenTypes = mutableListOf<TokenType>()
    private val _stringTypes = mutableListOf<StringType>()
    private val _commentTypes = mutableListOf<CommentType>()

    val tokenTypes: List<TokenType>
        get() = _tokenTypes
    val stringTypes: List<StringType>
        get() = _stringTypes
    val commentTypes: List<CommentType>
        get() = _commentTypes

    fun defineKeyword(content: String, name: String = "") {
        val keywordName = if (name.isEmpty()) content.toUpperCase() else name
        val regex = if (caseSensitive) {
            Regex("^$content")
        } else {
            createCaseInsensitiveRegex(content)
        }
        _tokenTypes.add(0, TokenType(keywordName, regex))
    }

    private fun createCaseInsensitiveRegex(content: String): Regex {

        var pattern = "^"

        for (ch in content) {
            val lower = ch.toLowerCase()
            val upper = ch.toUpperCase()
            pattern += "($lower|$upper)"
        }

        return Regex(pattern)
    }

    fun defineToken(name: String, pattern: String) {
        _tokenTypes.add(TokenType(name, Regex("^$pattern")))
    }

    fun defineString(name: String, begin: String, end: String, escape: String?=null) {
        _stringTypes.add(StringType(name, begin, end, escape))
    }

    fun defineComment(name: String, begin: String, end: String) {
        _commentTypes.add(CommentType(name, begin, end))
    }

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
