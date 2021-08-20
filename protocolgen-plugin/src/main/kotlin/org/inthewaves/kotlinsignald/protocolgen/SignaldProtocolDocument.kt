package org.inthewaves.kotlinsignald.protocolgen

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.asClassName
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import java.util.UUID

typealias Versioned<T> = Map<SignaldProtocolVersion, T>

@Serializable
@JvmInline
value class SignaldProtocolVersion(val name: String) {
    companion object {
        val v1 = SignaldProtocolVersion("v1")
        val v0 = SignaldProtocolVersion("v0")
    }

    fun getResponseSealedClassName(pkg: PackageName) =
        ClassName(GenUtil.getStructuresPackage(pkg, this), "SignaldResponseBody${name.uppercase()}")

    fun getRequestSealedClassName(pkg: PackageName) =
        ClassName(GenUtil.getStructuresPackage(pkg, this), "SignaldRequestBody${name.uppercase()}")
}

@Serializable
@JvmInline
value class SignaldType(val name: String) {
    override fun toString() = name
    companion object {
        val UNUSED = SignaldType("___unused")
        /**
         * For a type that isn't documented in the protocol but is still sent anyway (message subscribe).
         * This allows us to serialize the error no matter what structure the resulting JSON is.
         */
        val JSON_OBJECT_TYPE = SignaldType("kotlinx.serialization.json.JsonObject")
    }

    fun asSignaldClassName(pkg: PackageName, isStructure: Boolean, version: SignaldProtocolVersion): ClassName {
        require(asBuiltinTypeNameOrNull(pkg) == null) {
            "trying to make a signald class name ut of a builtin type ($name -> ${asBuiltinTypeNameOrNull(pkg)})"
        }
        return ClassName(
            if (isStructure) GenUtil.getStructuresPackage(pkg, version) else GenUtil.getRequestsPackage(pkg, version),
            name
        )
    }

    @OptIn(ExperimentalStdlibApi::class)
    fun asBuiltinTypeNameOrNull(pkg: PackageName): TypeName? = when (name.lowercase()) {
        "string" -> String::class.asClassName()
        "boolean" -> Boolean::class.asClassName()
        "long" -> Long::class.asClassName()
        "int", "integer" -> Int::class.asClassName()
        "object" -> {
            // Put something that will definitely prevent compiling
            ClassName(GenUtil.getClientProtocolPackage(pkg), "TODO-FixThisUp")
        }
        "uuid" -> UUID::class.asClassName()
        "map" -> {
            // TODO: The only data type where Map<String, Boolean> is used is in JsonSentTranscriptMessage's
            //  unidentifiedStatus field. There exists a to-do note in the signald source code to include the
            //  the types of the map.
            Map::class.asClassName()
                .parameterizedBy(String::class.asClassName(), Boolean::class.asClassName())
        }
        JSON_OBJECT_TYPE.name.lowercase() -> JsonObject::class.asClassName()
        else -> null
    }
}

@Serializable
@JvmInline
value class SignaldActionName(val name: String) {
    override fun toString() = name
    val nameWithCamelCase: String get() = name.snakeDashToCamelCase().capitalizeFirst()
    fun asClassName(pkg: PackageName, version: SignaldProtocolVersion) =
        ClassName(GenUtil.getRequestsPackage(pkg, version), nameWithCamelCase)
}

@Serializable
data class SignaldProtocolDocument(
    @SerialName("doc_version")
    val docVersion: String,
    val version: Version,
    val info: String,
    val types: Versioned<Map<SignaldType, Structure>>,
    val actions: Versioned<Map<SignaldActionName, Action>>
) {
    @Serializable
    data class Version(
        val name: String,
        val version: String,
        val branch: String,
        val commit: String,
    ) {
        val kDocVersionLine: String
            get() = "Generated from signald version ${version}"
    }
}

/**
 * https://gitlab.com/signald/signald/-/blob/main/src/main/java/io/finn/signald/clientprotocol/v1/ProtocolRequest.java#L147
 */
@Serializable
data class Structure(
    val fields: Map<String, Field>,
    val doc: String? = null,
    val deprecated: Boolean = false,
    @SerialName("removal_date")
    val removalDate: Long? = null,
) {
    @Serializable
    data class Field(
        val type: SignaldType,
        val version: SignaldProtocolVersion? = null,
        val list: Boolean = false,
        val doc: String? = null,
        val example: String? = null,
        val required: Boolean = false,
    ) {
        @OptIn(ExperimentalStdlibApi::class)
        fun getTypeName(pkg: PackageName, fieldName: String): TypeName {
            val builtInTypeName = type.asBuiltinTypeNameOrNull(pkg)

            val baseType: TypeName = if (
                fieldName.lowercase().endsWith("uuid") ||
                fieldName.lowercase().endsWith("guid")
            ) {
                UUID::class.asClassName()
            } else if (builtInTypeName != null) {
                builtInTypeName
            } else {
                require(version != null) { "version is not null despite not being a primitive type:\n$this" }
                ClassName(GenUtil.getStructuresPackage(pkg, version), type.name)
            }

            return if (list) {
                List::class.asClassName().parameterizedBy(baseType)
            } else {
                baseType
            }
        }
    }
}

@Serializable
data class Action(
    val request: SignaldType,
    val response: SignaldType? = null,
    val doc: String? = null,
    val deprecated: Boolean = false,
    @SerialName("removal_date")
    val removalDate: Long? = null
)
