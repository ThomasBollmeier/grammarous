package de.tbollmeier.grammarous

class AstJsonFormatter : AstVisitor {

    private var json = ""
    private var offset = 0
    private var tabSize = 2
    private var firstChildInfo = mutableListOf<Boolean>()

    fun toJson(ast: Ast, tabSize: Int = 2) : String {
        json = ""
        offset = 0
        this.tabSize = tabSize
        ast.accept(this)
        return json
    }

    override fun enter(ast: Ast) {
        val isFirstChild = firstChildInfo.isEmpty() || firstChildInfo.last()
        if (isFirstChild) {
            if (firstChildInfo.isNotEmpty()) {
                firstChildInfo.set(firstChildInfo.size - 1, false)
            }
            writeLine("{")
        } else {
            writeLine(",{")
        }
        offset += tabSize
        writeLine("\"name\": \"${ast.name}\",")
        writeLine("\"value\": \"${ast.value}\",")
        writeLine("\"id\": \"${ast.id}\",")
        for ((name, value) in ast.attrs.entries) {
            writeLine("\"$name\": \"$value\",")
        }
        writeLine("\"children\": [")
        firstChildInfo.add(true)
    }

    override fun exit(ast: Ast) {
        writeLine("]")
        offset -= tabSize
        firstChildInfo.removeAt(firstChildInfo.size - 1)
        writeLine("}")
    }

    private fun writeLine(text: String) {
        var line = ""
        for (i in 1..offset) {
            line += " "
        }
        line += text + "\n"
        json += line
    }

}