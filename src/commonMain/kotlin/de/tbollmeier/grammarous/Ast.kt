package de.tbollmeier.grammarous

open class Ast(val name: String, val value: String = "", var id: String="") {

    var children = mutableListOf<Ast>()
        private set

    val attrs = mutableMapOf<String, String>()

    fun accept(visitor: AstVisitor) {
        visitor.enter(this)
        children.forEach { it.accept(visitor) }
        visitor.exit(this)
    }

    fun addChild(child: Ast) {
        children.add(child)
    }

    fun getChildrenByName(name: String) = children.filter { it.name == name }

    fun getChildrenById(id: String) = children.filter { it.id == id }

    fun moveChildrenByName(sourceAst: Ast, name: String) = moveChildrenBy(sourceAst) { it.name == name }

    fun moveChildrenById(sourceAst: Ast, id: String) = moveChildrenBy(sourceAst) { it.id == id }

    private fun moveChildrenBy(sourceAst: Ast, predicateFn: (Ast) -> Boolean) {
        for (child in sourceAst.children.filter(predicateFn)) {
            child.id = ""
            addChild(child)
        }
    }

}

interface AstVisitor {
    fun enter(ast: Ast)
    fun exit(ast: Ast)
}