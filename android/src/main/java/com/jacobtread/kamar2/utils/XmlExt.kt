@file:Suppress("NOTHING_TO_INLINE")

package com.jacobtread.kamar2.utils

import org.w3c.dom.Element
import org.w3c.dom.Node
import org.w3c.dom.NodeList

inline fun NodeList.first(): Node? = this.item(0)

inline fun NodeList.forEach(each: (Node) -> Unit) {
    val length = length
    for (i in 0 until length) {
        val node = item(i)
        each(node)
    }
}

inline fun NodeList.forEachIndexed(each: (Int, Node) -> Unit) {
    val length = length
    for (i in 0 until length) {
        val node = item(i)
        each(i, node)
    }
}

inline fun NodeList.firstOrNull(each: (Node) -> Boolean): Node? {
    val length = length
    for (i in 0 until length) {
        val node = item(i)
        if (each(node)) return node
    }
    return null
}


operator fun NodeList.iterator(): Iterator<Node> {
    return NodeListIterator(this)
}

class NodeListIterator(private val nodeList: NodeList) : Iterator<Node> {
    private var index = 0
    override fun hasNext(): Boolean = index < nodeList.length
    override fun next(): Node = nodeList.item(index) ?: throw NoSuchElementException()
}


inline operator fun NodeList.get(index: Int): Node? = item(index)

fun Node.getChildByName(name: String): Node {
    if (!hasChildNodes()) throw DeserializationException()
    val children = childNodes
    return children.firstOrNull { it.nodeName == name } ?: throw DeserializationException()
}

fun Node.getChildrenByNames(vararg names: String): Array<Node> {
    if (!hasChildNodes()) throw DeserializationException()
    val children = childNodes
    val out = arrayOfNulls<Node>(names.size)
    children.forEach {
        names.forEachIndexed { index, name ->
            if (it.nodeName == name) out[index] = it
        }
    }
    if (out.contains(null)) throw DeserializationException()
    @Suppress("UNCHECKED_CAST")
    return out as Array<Node>
}



fun Node.getElementsByTag(tag: String): List<Node> {
    val out = ArrayList<Node>()
    childNodes.forEach { if (it.nodeName == tag) out.add(it) }
    return out
}

inline fun Element.getElementByName(name: String): Node {
    return this.getElementsByTagName(name).first()
        ?: throw DeserializationException()
}

inline fun Element.getElementByNameOrNull(name: String): Node? = this.getElementsByTagName(name).first()
inline fun Node.text(): String = this.textContent
inline fun Node.number(): Int = this.textContent.toIntOrNull() ?: throw DeserializationException()