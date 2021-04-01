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
        private var done = false
        private var charBufSize = 1
        private var charBuf = ""

        init {

            charBufSize = calcCharBufSize(grammar)

        }

        private fun calcCharBufSize(grammar: LexerGrammar): Int {

            var ret = 0

            for (st in grammar.stringTypes) {
                var size = maxOf(st.begin.length, st.escape.length + st.end.length)
                if (size > ret) {
                    ret = size
                }
            }

            for (ct in grammar.commentTypes) {
                var size = maxOf(ct.begin.length, ct.end.length)
                if (size > ret) {
                    ret = size
                }
            }

            return ret
        }

        override fun hasNext(): Boolean {
            return !done
        }

        override fun next(): Token? {
            return if (!done) {
                val token = getNextToken()
                if (token == null) {
                    done = true
                }
                token
            } else {
                null
            }
        }

        private fun getNextToken(): Token? {
            return null
        }

    }

}