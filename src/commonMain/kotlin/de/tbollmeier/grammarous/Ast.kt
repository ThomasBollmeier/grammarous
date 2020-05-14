package de.tbollmeier.grammarous

class Ast(val name: String, val value: String, var id: String="") {

    private var children = mutableListOf<Ast>()

    fun addChild(child: Ast) {
        children.add(child)
    }

}