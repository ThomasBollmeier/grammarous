package de.tbollmeier.grammarous

class AstXmlFormatter : AstVisitor {

    private var xml = ""
    private var offset = 0
    private var tabSize = 2

    fun toXml(ast: Ast, tabSize: Int = 2) : String {
        xml = ""
        offset = 0
        this.tabSize = tabSize
        ast.accept(this)
        return xml
    }

    override fun enter(ast: Ast) {
        var text = "<${ast.name}"
        if (ast.id.isNotEmpty()) {
            text += " id=\"${ast.id}\""
        }
        for ((name, value) in ast.attrs.entries) {
            text += " $name=\"$value\""
        }
        text += if (ast.children.isEmpty() && ast.value.isEmpty()) {
            "/>"
        } else {
            ">"
        }
        if (ast.value.isNotEmpty()) {
            text += ast.value
        }
        if (ast.children.isEmpty() && ast.value.isNotEmpty()) {
            text += "</${ast.name}>"
        }
        writeLine(text)
        offset += tabSize
    }

    override fun exit(ast: Ast) {
        offset -= tabSize
        if (ast.children.isNotEmpty()) {
            writeLine("</${ast.name}>")
        }
    }

    private fun writeLine(text: String) {
        var line = ""
        for (i in 1..offset) {
            line += " "
        }
        line += text + "\n"
        xml += line
    }

}