package de.tbollmeier.grammarous

data class SourcePosition(
    val line: Int,
    val column: Int
) {

    companion object {
        fun first(pos1: SourcePosition?, pos2: SourcePosition?): SourcePosition? {
            return if (pos1 == null) {
                pos2
            } else if (pos2 == null) {
                pos1
            } else {
                if (pos1.line < pos2.line) {
                    pos1
                } else if (pos1.line > pos2.line) {
                    pos2
                } else {
                    if (pos1.column <= pos2.column) {
                        pos1
                    } else {
                        pos2
                    }
                }
            }
        }

        fun last(pos1: SourcePosition?, pos2: SourcePosition?): SourcePosition? {
            return if (pos1 == null) {
                pos2
            } else if (pos2 == null) {
                pos1
            } else {
                if (pos1.line > pos2.line) {
                    pos1
                } else if (pos1.line < pos2.line) {
                    pos2
                } else {
                    if (pos1.column >= pos2.column) {
                        pos1
                    } else {
                        pos2
                    }
                }
            }
        }
    }
    fun advance(ch: Char): SourcePosition {
        return if (ch != '\n')
            SourcePosition(line, column + 1)
        else
            SourcePosition(line + 1, 1)
    }
}
