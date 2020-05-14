package de.tbollmeier.grammarous

class ListStream<T>(private var items: List<T> = listOf()) : Stream<T> {

    private var curIdx = 0

    fun init(items: List<T>) {
        this.items = items
        curIdx = 0
    }

    override fun hasNext() = curIdx < items.size

    override fun next() : T? {
        return if (curIdx < items.size) {
            val result = items[curIdx]
            curIdx++
            result
        } else null
    }

}
