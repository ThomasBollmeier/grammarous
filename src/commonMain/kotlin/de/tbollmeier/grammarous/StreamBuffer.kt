package de.tbollmeier.grammarous

class StreamBuffer<T>(private val stream: Stream<T>) {

    private val elements = mutableListOf<T>()
    private val changes = mutableListOf<Int>()
    private var curIdx: Int = 0

    fun hasNext() : Boolean {
        updateBuffer()
        return curIdx < elements.size
    }

    fun next() : T? {
        updateBuffer()
        val result = if (hasNext()) elements[curIdx] else null
        if (result != null) {
            curIdx++
        }
        return result
    }

    fun peek(offset: Int = 0) : T? {
        updateBuffer(offset)
        val idx = curIdx + offset
        return if (idx < elements.size) elements[idx] else null
    }

    fun startChange() {
        changes.add(curIdx)
    }

    fun commitChange() {
        if (changes.isNotEmpty()) {
            changes.removeAt(changes.lastIndex)
        } else {
            elements.drop(curIdx)
            curIdx = 0
        }
    }

    fun undoChange() {
        if (changes.isNotEmpty()) {
            curIdx = changes.last()
            changes.removeAt(changes.lastIndex)
        } else {
            curIdx = 0
        }
    }

    private fun updateBuffer(offset: Int = 0) {
        while (curIdx + offset >= elements.size) {
            if (!stream.hasNext()) {
                break
            }
            elements.add(stream.next()!!)
        }
    }

}