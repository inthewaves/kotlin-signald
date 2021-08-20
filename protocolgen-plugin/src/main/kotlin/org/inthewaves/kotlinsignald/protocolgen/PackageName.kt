package org.inthewaves.kotlinsignald.protocolgen

@JvmInline
value class PackageName(val name: String) {
    override fun toString(): String = name
}
