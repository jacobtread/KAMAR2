@file:Suppress("NOTHING_TO_INLINE")

package com.jacobtread.kamar2.utils

import org.w3c.dom.Element
import org.w3c.dom.Node
import org.w3c.dom.NodeList

inline fun NodeList.first(): Node? = this.item(0)

inline fun Element.getElementByName(name: String): Node {
    return this.getElementsByTagName(name).first()
        ?: throw DeserializationException()
}
inline fun Element.getElementByNameOrNull(name: String): Node? = this.getElementsByTagName(name).first()
inline fun Node.text(): String = this.textContent
inline fun Node.number(): Int = this.textContent.toIntOrNull() ?: throw DeserializationException()