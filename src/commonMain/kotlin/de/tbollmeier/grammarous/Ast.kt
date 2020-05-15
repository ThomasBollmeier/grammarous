package de.tbollmeier.grammarous

open class Ast(val name: String, val value: String, var id: String="") {

    var children = mutableListOf<Ast>()
        private set

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

    fun moveChildrenByName(dest: Ast, name: String) = moveChildrenBy(dest) { it.name == name }

    fun moveChildrenById(dest: Ast, id: String) = moveChildrenBy(dest) { it.id == id }

    private fun moveChildrenBy(dest: Ast, predicateFn: (Ast) -> Boolean) {
        for (child in children.filter(predicateFn)) {
            child.id = ""
            dest.addChild(child)
        }
    }

}

interface AstVisitor {
    fun enter(ast: Ast)
    fun exit(ast: Ast)
}