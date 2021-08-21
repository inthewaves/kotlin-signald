package org.inthewaves.kotlinsignald.protocolgen

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.asClassName

/**
 * Classes whose serializers can be accessed via the companion object, e.g. `String.serializer()` (i.e. the
 * [kotlinx.serialization.builtins.serializer] extension functions). Note that all of the classnames in this are
 * not nullable
 *
 * @see kotlinx.serialization.builtins
 */
private val CLASSES_WITH_COMPANION_SERIALIZERS: Set<ClassName> = setOf(
    // Pair::class.asClassName(),
    // Map.Entry::class.asClassName(),
    // Triple::class.asClassName(),
    Char::class.asClassName(),
    // CharArray::class.asClassName(),
    Byte::class.asClassName(),
    // ByteArray::class.asClassName(),
    Short::class.asClassName(),
    // ShortArray::class.asClassName(),
    Int::class.asClassName(),
    // IntArray::class.asClassName(),
    Long::class.asClassName(),
    // LongArray::class.asClassName(),
    Float::class.asClassName(),
    // FloatArray::class.asClassName(),
    Double::class.asClassName(),
    // DoubleArray::class.asClassName(),
    Boolean::class.asClassName(),
    // BooleanArray::class.asClassName(),
    Unit::class.asClassName(),
    String::class.asClassName(),
)

/**
 * If this is true, then the serializers can be accessed via the companion object, e.g. `String.serializer()` (i.e. the
 * [kotlinx.serialization.builtins.serializer] extension functions). Note that all of the classnames in this are
 * not nullable
 *
 * @see kotlinx.serialization.builtins
 */
val TypeName.hasBuiltinCompanionSerializer: Boolean
    get() = this.copy(nullable = false) in CLASSES_WITH_COMPANION_SERIALIZERS
