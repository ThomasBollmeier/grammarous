package de.tbollmeier.grammarous

fun createLexer(grammar: LexerGrammar) : Lexer = LexerImpl(grammar)

private class LexerImpl(private val grammar: LexerGrammar) : Lexer {

    override fun scan(characters: Stream<Char>): Stream<Token> {
        return TokenStream(grammar, characters)
    }

    private enum class Mode {
        NORMAL,
        STRING,
        COMMENT
    }

    data class CharInfo(val char: Char, val sourcePos: SourcePosition)

    class CharStreamWithPosInfo(private val charStream: Stream<Char>) {

        private var line = 1
        private var column = 1
        private var lastPos: SourcePosition? = null

        fun hasNext() = charStream.hasNext()

        fun next(): CharInfo? {
            val ch = charStream.next()
            return if (ch != null) {
                lastPos = SourcePosition(line, column)
                when (ch) {
                    '\n' -> {
                        line++
                        column = 1
                    }
                    else -> column++
                }
                CharInfo(ch, lastPos!!)
            } else {
                null
            }
        }
    }

    class TokenStream(
        private val grammar: LexerGrammar,
        charStream: Stream<Char>
    ) : Stream<Token> {

        private val characters = CharStreamWithPosInfo(charStream)
        private var mode = Mode.NORMAL
        private var curStringType: LexerGrammar.StringType? = null
        private var curCommentType: LexerGrammar.CommentType? = null
        private var done = false
        private var tokens = mutableListOf<Token>()

        override fun hasNext(): Boolean {
            return !done || tokens.isNotEmpty()
        }

        override fun next(): Token? {
            return if (!done) {
                var result: Token? = null
                if (tokens.isEmpty()) {
                    tokens.addAll(getNextTokens())
                }
                if (tokens.isNotEmpty()) {
                    result = tokens[0]
                    tokens.removeFirst()
                } else {
                    done = true
                }
                result
            } else if (tokens.isNotEmpty()) {
                val result = tokens[0]
                tokens.removeFirst()
                result
            } else {
                null
            }
        }

        private fun getNextTokens(): List<Token> {
            while (!done) {
                val tokens = when (mode) {
                    Mode.NORMAL -> {
                        val buffer = readNextChars()
                        findTokens(buffer)
                    }
                    Mode.STRING -> readString()
                    Mode.COMMENT -> skipComment()
                }
                if (tokens.isNotEmpty()) {
                    return tokens
                }
            }
            return emptyList()
        }

        private fun skipComment(): List<Token> {

            var s = ""
            val ct = curCommentType!!

            while (!done) {

                val charInfo = characters.next()
                if (charInfo == null) {
                    done = true
                    break
                }
                s += charInfo.char

                if (s.endsWith(ct.end)) {
                    break
                }

            }

            curCommentType = null
            mode = Mode.NORMAL

            return emptyList()
        }

        private fun readString(): List<Token> {

            var s = ""
            val positions = mutableListOf<SourcePosition>()
            val st = curStringType!!

            while (!done) {
                val charInfo = characters.next()
                if (charInfo == null) {
                    done = true
                    curStringType = null
                    mode = Mode.NORMAL
                    return emptyList()
                }
                s += charInfo.char
                positions.add(charInfo.sourcePos)

                if (s.endsWith(st.end)) {
                    if (st.escape != null) {
                        val escapedEnd = st.escape + st.end
                        if (!s.endsWith(escapedEnd)) {
                            break
                        }
                    } else {
                        break
                    }
                }
            }

            curStringType = null
            mode = Mode.NORMAL

            val sourcePos = if (positions.isNotEmpty()) {
                val (line, column) = positions[0]
                SourcePosition(line, column - st.begin.length)
            } else SourcePosition(0, 0)

            return listOf(Token(st.name, sourcePos,st.begin + s))
        }

        private fun findTokens(buffer: List<CharInfo>): List<Token> {

            return if (buffer.isNotEmpty()) {

                val tokens = mutableListOf<Token>()
                var remainingBuffer = buffer
                var remaining = getString(remainingBuffer)

                while (remaining.isNotEmpty()) {

                    var maxTokenType: LexerGrammar.TokenType? = null
                    var maxLexeme = ""

                    for (tt in grammar.tokenTypes) {
                        val matchResult = tt.regex.find(remaining)
                        if (matchResult != null) {
                            val lexeme = matchResult.value
                            if (lexeme.length > maxLexeme.length) {
                                maxTokenType = tt
                                maxLexeme = lexeme
                            }
                        }
                    }

                    if (maxTokenType != null) {
                        tokens.add(Token(maxTokenType.name, remainingBuffer[0].sourcePos, maxLexeme))
                        remainingBuffer = remainingBuffer.drop(maxLexeme.length)
                        remaining = getString(remainingBuffer)
                    } else {
                        tokens.add(Token(ERROR_TOKEN, remainingBuffer[0].sourcePos, remaining))
                        remainingBuffer = emptyList()
                        remaining = ""
                        done = true
                    }

                }

                return tokens
            } else {
                emptyList()
            }

        }

        private fun readNextChars(): List<CharInfo> {

            val ret = mutableListOf<CharInfo>()

            val nonWSCharInfo = skipWhiteSpace()

            if (nonWSCharInfo != null) {

                ret.add(nonWSCharInfo)

                val stringType = checkForChangeToStringMode(getString(ret))
                if (stringType != null) {
                    mode = Mode.STRING
                    curStringType = stringType
                    return ret.dropLast(stringType.begin.length)
                }

                val commentType = checkForChangeToCommentMode(getString(ret))
                if (commentType != null) {
                    mode = Mode.COMMENT
                    curCommentType = commentType
                    return ret.dropLast(commentType.begin.length)
                }

            } else {
                return ret
            }

            while (!done) {

                val charInfo = characters.next()

                if (charInfo == null) {
                    done = true
                    break
                }

                if (charInfo.char in grammar.whiteSpace) {
                    break
                }

                ret.add(charInfo)

                val stringType = checkForChangeToStringMode(getString(ret))
                if (stringType != null) {
                    mode = Mode.STRING
                    curStringType = stringType
                    return ret.dropLast(stringType.begin.length)
                }

                val commentType = checkForChangeToCommentMode(getString(ret))
                if (commentType != null) {
                    mode = Mode.COMMENT
                    curCommentType = commentType
                    return ret.dropLast(commentType.begin.length)
                }

            }

            return ret
        }

        private fun getString(charInfoList: List<CharInfo>) =
            charInfoList.map { it.char }.joinToString(separator = "")

        private fun checkForChangeToStringMode(s: String): LexerGrammar.StringType? {
            for (st in grammar.stringTypes) {
                if (s.endsWith(st.begin)) {
                    return st
                }
            }
            return null
        }

        private fun checkForChangeToCommentMode(s: String): LexerGrammar.CommentType? {
            for (ct in grammar.commentTypes) {
                if (s.endsWith(ct.begin)) {
                    return ct
                }
            }
            return null
        }

        private fun skipWhiteSpace(): CharInfo? {
            do {
                val charInfo = characters.next() ?: return null
                if (charInfo.char !in grammar.whiteSpace) {
                    return charInfo
                }
            } while (true)
        }

    }

}