package de.tbollmeier.grammarous

class LexerImpl(private val grammar: LexerGrammar) : Lexer {

    override fun scan(characters: Stream<Char>): Stream<Token> {
        return TokenStream(grammar, characters)
    }

    private enum class Mode {
        NORMAL,
        STRING,
        COMMENT
    }

    class TokenStream(
        private val grammar: LexerGrammar,
        private val characters: Stream<Char>
    ) : Stream<Token> {

        private var mode = Mode.NORMAL
        private var curStringType: LexerGrammar.StringType? = null
        private var curCommentType: LexerGrammar.CommentType? = null
        private var done = false
        private var tokens = mutableListOf<Token>()
        private val dummySrcPos = SourcePosition(0, 0)

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

                val ch = characters.next()
                if (ch == null) {
                    done = true
                    break
                }
                s += ch

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
            val st = curStringType!!

            while (!done) {
                val ch = characters.next()
                if (ch == null) {
                    done = true
                    curStringType = null
                    mode = Mode.NORMAL
                    return emptyList()
                }
                s += ch

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

            return listOf(Token(st.name, dummySrcPos, st.begin + s))
        }

        private fun findTokens(s: String): List<Token> {
            return if (s.isNotEmpty()) {

                var tokens = mutableListOf<Token>()
                var remaining = s

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
                        tokens.add(Token(maxTokenType.name, dummySrcPos, maxLexeme))
                        remaining = remaining.drop(maxLexeme.length)
                    } else {
                        throw RuntimeException("No tokens found in \"$remaining\"")
                    }

                }

                return tokens
            } else {
                emptyList()
            }

        }

        private fun readNextChars(): String {

            var ret = ""

            val nonWSChar = skipWhiteSpace()

            if (nonWSChar != null) {

                ret += nonWSChar

                val stringType = checkForChangeToStringMode(ret)
                if (stringType != null) {
                    ret = ret.dropLast(stringType.begin.length)
                    mode = Mode.STRING
                    curStringType = stringType
                    return ret
                }

                val commentType = checkForChangeToCommentMode(ret)
                if (commentType != null) {
                    ret = ret.dropLast(commentType.begin.length)
                    mode = Mode.COMMENT
                    curCommentType = commentType
                    return ret
                }

            } else {
                return ret
            }

            while (!done) {

                val ch = characters.next()

                if (ch == null) {
                    done = true
                    break
                }

                if (ch in grammar.whiteSpace) {
                    break
                }

                ret += ch

                val stringType = checkForChangeToStringMode(ret)
                if (stringType != null) {
                    ret = ret.dropLast(stringType.begin.length)
                    mode = Mode.STRING
                    curStringType = stringType
                    break
                }

                val commentType = checkForChangeToCommentMode(ret)
                if (commentType != null) {
                    ret = ret.dropLast(commentType.begin.length)
                    mode = Mode.COMMENT
                    curCommentType = commentType
                    break
                }

            }

            return ret
        }

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

        private fun skipWhiteSpace(): Char? {
            do {
                val ch = characters.next()
                if (ch == null) {
                    done = true
                    return null
                } else if (ch !in grammar.whiteSpace) {
                    return ch
                }
            } while (true)
        }

    }

}