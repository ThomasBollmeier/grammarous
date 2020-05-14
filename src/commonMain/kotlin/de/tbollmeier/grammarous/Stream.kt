package de.tbollmeier.grammarous

interface Stream<T> {
    fun hasNext(): Boolean
    fun next(): T?
}
