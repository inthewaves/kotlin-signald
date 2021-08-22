package org.inthewaves.kotlinsignald.protocolgen

import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.STAR
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.TypeVariableName
import com.squareup.kotlinpoet.asClassName
import kotlinx.datetime.Clock
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Required
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import java.io.File
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.system.exitProcess

/**
 * Every structure / type is generated in the same way, with special handling for request and response types.
 * However, sometimes we require the need to add extra properties or modifiers, so a [SpecialTypeHandler] modifies
 * the builder after all the common steps have been applied
 */
typealias SpecialTypeHandler = TypeSpec.Builder.(
    version: SignaldProtocolVersion,
    className: ClassName,
    structureDetails: Structure,
    primaryConstructorBuilder: FunSpec.Builder?,
    actionInfo: ProtocolGenerator.ActionInfo?
) -> Unit

class ProtocolGenerator(
    private val protocolJsonFile: File,
    generatedFilesRootDir: File,
    private val packageName: PackageName
) {

    private val protocolDoc: SignaldProtocolDocument

    private val genFilesDir: File

    init {
        if (!protocolJsonFile.exists()) {
            System.err.println("specified [$protocolJsonFile] as the protocol.json, but it doesn't exist")
            exitProcess(1)
        }

        val protocolJson: String = protocolJsonFile.readText()

        protocolDoc = try {
            Json.decodeFromString(protocolJson)
        } catch (e: SerializationException) {
            println("specified [$protocolJsonFile] as the protocol.json, but it is invalid")
            exitProcess(1)
        }

        genFilesDir = generatedFilesRootDir

        val sep = File.separatorChar
        val acceptablePaths = setOf(
            "gen${sep}kotlin",
            "commonMain${sep}kotlin",
            "commonMain${sep}generated"
        )

        if (!acceptablePaths.any { genFilesDir.absolutePath.contains(it) }) {
            println("error: expected one of $acceptablePaths in the path, but was given $genFilesDir")
            exitProcess(1)
        }
        if (genFilesDir.exists() && !genFilesDir.deleteRecursively()) {
            println("warning: can't delete $genFilesDir")
        }
        if (!genFilesDir.mkdirs()) {
            println("can't make $genFilesDir")
            exitProcess(1)
        }
    }

    private val clientProtocolPackage = GenUtil.getClientProtocolPackage(packageName)

    private val signaldJsonClassName = ClassName(clientProtocolPackage, "SignaldJson")

    private fun writeTypeSpecFile(className: ClassName, typeSpec: TypeSpec, dirToWriteTo: File) {
        require(className.simpleName == typeSpec.name) {
            "bad name: got class name ${className.canonicalName} ($className) vs typeSpec ${typeSpec.name} ($typeSpec)"
        }
        FileSpec.builder(className.packageName, className.simpleName)
            .indent("    ")
            .addType(typeSpec)
            .build()
            .writeTo(dirToWriteTo)
    }

    private fun writePropertySpecFile(className: ClassName, propertySpec: PropertySpec, dirToWriteTo: File) {
        require(className.simpleName == propertySpec.name) {
            "bad name: got class name ${className.canonicalName} ($className) vs typeSpec ${propertySpec.name} ($propertySpec)"
        }
        FileSpec.builder(className.packageName, className.simpleName)
            .indent("    ")
            .addProperty(propertySpec)
            .build()
            .writeTo(dirToWriteTo)
    }

    data class ActionInfo(
        /**
         * The response wrapper type
         */
        val responseWrapperType: ClassName,
        val responseDataType: ClassName,
        val actionName: SignaldActionName
    )

    private fun getSubscriptionResponseClassName(version: SignaldProtocolVersion) =
        ClassName(GenUtil.getStructuresPackage(packageName, version), "SubscriptionResponse")

    private val extraTypes: Versioned<Map<SignaldType, Structure>>
        get() = mapOf(
            SignaldProtocolVersion.v1 to mapOf(
                SignaldType("ExceptionWrapper") to Structure(
                    fields = mapOf(
                        "message" to Structure.Field(SignaldType("string")),
                        "unexpected" to Structure.Field(SignaldType("boolean"))
                    ),
                    doc = "An incoming message representing an error that can be sent by signald after a v1 subscribe " +
                            "request. This is not documented in the client protocol; however, as the signald socket can " +
                            "send it anyway, we add this here for type safety purposes.\n\n" +
                            """
                        Example: 
                        {
                            "type":"ExceptionWrapper",
                            "version":"v1",
                            "data":{"message":"org.signal.libsignal.metadata.ProtocolDuplicateMessageException"},
                            "error":true
                        }
                    """.trimIndent()
                ),
                SignaldType(getSubscriptionResponseClassName(SignaldProtocolVersion.v1).simpleName) to Structure(
                    fields = mapOf(
                        "messages" to Structure.Field(
                            type = SignaldType(getClientMessageWrapperClassName(SignaldProtocolVersion.v1).simpleName),
                            version = SignaldProtocolVersion.v1,
                            list = true,
                            doc = "Messages that have been sent by the socket during the (un)subscribe submission."
                        ),
                    ),
                    doc = "Responses from the subscribe / unsubscribe endpoint. The protocol describes these as " +
                            "empty responses, but race conditions can occur. This response can contain messages sent " +
                            "before the (un)subscribe acknowledgement message from signald."
                ),
            )
        )

    private val extraRequestWrappers: Versioned<Map<SignaldActionName, Action>>
        get() = mapOf(
            SignaldProtocolVersion.v1 to mapOf(
                UNEXPECTED_ERROR_ACTION_NAME to Action(
                    request = SignaldType.UNUSED,
                    response = SignaldType.JSON_OBJECT_TYPE,
                )
            )
        )

    private val socketCommunicatorClassName = ClassName(clientProtocolPackage, "SocketCommunicator")

    private val signaldExceptionClassName = ClassName(clientProtocolPackage, "SignaldException")

    private val requestFailedExceptionClassName = ClassName(clientProtocolPackage, "RequestFailedException")

    fun generateSignaldProtocol() {
        println(
            "Generating signald protocol from $protocolJsonFile,\n" +
                    "version ${protocolDoc.version.version} (doc version ${protocolDoc.docVersion} " +
                    "into $genFilesDir"
        )

        val jsonProperty = PropertySpec.builder(signaldJsonClassName.simpleName, Json::class)
            .initializer("%M { encodeDefaults = false }", MemberName("kotlinx.serialization.json", "Json"))
            .addKdoc("The [Json] instance used to serialize and deserialize signald requests and responses.")
            .build()
        writePropertySpecFile(signaldJsonClassName, jsonProperty, genFilesDir)

        val signaldExceptionTypeSpec = TypeSpec.classBuilder(signaldExceptionClassName)
            .superclass(ClassName("kotlin", "Exception"))
            .addModifiers(KModifier.EXPECT, KModifier.OPEN)
            .addFunction(FunSpec.constructorBuilder().build())
            .addFunction(FunSpec.constructorBuilder()
                .addParameter("message", String::class.asClassName().copy(nullable = true))
                .build()
            )
            .addFunction(FunSpec.constructorBuilder()
                .addParameter("message", String::class.asClassName().copy(nullable = true))
                .addParameter("cause", ClassName("kotlin", "Throwable").copy(nullable = true))
                .build()
            )
            .addFunction(FunSpec.constructorBuilder()
                .addParameter("cause", ClassName("kotlin", "Throwable").copy(nullable = true))
                .build()
            )
            .build()
        writeTypeSpecFile(signaldExceptionClassName, signaldExceptionTypeSpec, genFilesDir)

        val requestFailedExceptionTypeSpec = TypeSpec.classBuilder(requestFailedExceptionClassName).apply {
            addModifiers(KModifier.OPEN)
            superclass(signaldExceptionClassName)
            addKdoc(
                "%L",
                "An exception that is thrown if " +
                        "the resulting JSON can't be deserialized or " +
                        "the socket returns an error response."
            )
            addProperty(
                PropertySpec.builder("isRateLimitException", Boolean::class)
                    .getter(
                        FunSpec.getterBuilder()
                            .addStatement(
                                "return message?.let·{ it.contains(%S) || it.contains(%S) } ?: false",
                                "RateLimitException",
                                "Rate limit exceeded: 413"
                            )
                            .build()
                    )
                    .build()
            )
            val propertiesList = listOf(
                PropertySpec.builder("responseJsonString", String::class.asClassName().copy(nullable = true))
                    .addKdoc("%L", "The raw JSON string from the socket that caused the error.")
                    .initializer("responseJsonString")
                    .build(),
                PropertySpec.builder("errorBody", JsonObject::class.asClassName().copy(nullable = true))
                    .initializer("errorBody")
                    .build(),
                PropertySpec.builder("errorType", String::class.asClassName().copy(nullable = true))
                    .initializer("errorType")
                    .build(),
                PropertySpec.builder("exception", String::class.asClassName().copy(nullable = true))
                    .initializer("exception")
                    .build(),
            )
            val primaryConstructorParams: List<ParameterSpec> =
                propertiesList.asSequence()
                    .map { ParameterSpec(it.name, it.type) }
                    .plus(
                        listOf(
                            ParameterSpec("extraMessage", String::class.asClassName().copy(nullable = true)),
                            ParameterSpec("cause", Throwable::class.asClassName().copy(nullable = true))
                        )
                    )
                    .map {
                        it.toBuilder().apply { if (it.type.isNullable) defaultValue("null") }.build()
                    }
                    .toList()

            addProperties(propertiesList)

            primaryConstructor(
                FunSpec.constructorBuilder()
                    .addParameters(primaryConstructorParams)
                    .build()
            )
            addSuperclassConstructorParameter(
                "getErrorMessageString(%L)",
                primaryConstructorParams.joinToString { it.name }
            )
            addSuperclassConstructorParameter("cause")

            addType(
                TypeSpec.companionObjectBuilder()
                    .addFunction(
                        FunSpec.builder("getErrorMessageString")
                            .addModifiers(KModifier.PRIVATE)
                            .addParameters(primaryConstructorParams)
                            .addCode(
                                """
                        return buildString { 
                            append("Request failed")
                            if (extraMessage != null) {
                                append(" (")
                                append(extraMessage)
                                append(") ")
                            }
                            if (cause?.message != null) {
                                append(" (cause: ")
                                append(cause.message)
                                append(") ")
                            }
                            if (exception != null) {
                                append(" (exception: ")
                                append(exception)
                                append(")")
                            }
                            if (errorType != null) {
                                append(", error type ")
                                append(errorType)
                            }
                            val errorMessage = errorBody?.get("message")
                            if (errorMessage != null) {
                                append(": ")
                                append(errorMessage)
                            }
                        }
                    """.trimIndent()
                            )
                            .build()
                    )
                    .build()
            )
        }.build()
        writeTypeSpecFile(requestFailedExceptionClassName, requestFailedExceptionTypeSpec, genFilesDir)

        val socketCommunicatorTypeSpec = TypeSpec.interfaceBuilder(socketCommunicatorClassName)
            .addKdoc(
                "%L",
                "An interface to facilitate communication with signald socket. The implementation might close socket " +
                    "connections after making a request, in which case, the [$SOCKET_COMM_READLINE_FUN_NAME] " +
                    "function will not be supported."
            )
            .addFunction(
                FunSpec.builder(SOCKET_COMM_SUBMIT_FUN_NAME)
                    .addModifiers(KModifier.ABSTRACT)
                    .addKdoc(
                        "%L",
                        "Sends the [request] to the socket as a single line of JSON (line terminated with \\n), and " +
                                "returns the JSON response from signald."
                    )
                    .addKdoc(
                        "\n%L",
                        "@throws [${signaldExceptionClassName.simpleName}] if an " +
                            "I/O error occurs during socket communication")
                    .addAnnotation(
                        AnnotationSpec.builder(ClassName("kotlin", "Throws"))
                            .addMember("%T::class", signaldExceptionClassName)
                            .build()
                    )
                    .addParameter("request", String::class)
                    .returns(String::class)
                    .build()
            )
            .addFunction(
                FunSpec.builder(SOCKET_COMM_READLINE_FUN_NAME)
                    .addModifiers(KModifier.ABSTRACT)
                    .addKdoc(
                        "%L",
                        "Reads a JSON message from the socket, blocking until a message is received or " +
                                "returning null if the socket closes. Might not be supported by the implementation."
                    )
                    .addKdoc(
                        "\n%L",
                        "@throws [${signaldExceptionClassName.simpleName}] if an " +
                            "I/O error occurs during socket communication"
                    )
                    .addKdoc(
                        "\n%L",
                        "@throws [${UnsupportedOperationException::class.simpleName}] the communicator " +
                            "doesn't support this operation (can happen if the communicator closes connections after " +
                            "a request is handled).")
                    .addAnnotation(
                        AnnotationSpec.builder(ClassName("kotlin", "Throws"))
                            .addMember("%T::class", signaldExceptionClassName)
                            .build()
                    )
                    .returns(String::class.asClassName().copy(nullable = true))
                    .build()
            )
            .build()
        writeTypeSpecFile(socketCommunicatorClassName, socketCommunicatorTypeSpec, genFilesDir)

        val protocolVersions: Set<SignaldProtocolVersion> = protocolDoc.types.keys
            .asSequence()
            .map { SignaldProtocolVersion(it.name.lowercase()) }
            .plus(protocolDoc.types.keys.map { SignaldProtocolVersion(it.name.lowercase()) })
            .toSet()

        for (protocolVersion in protocolVersions) {
            if (protocolVersion in protocolDoc.types) {
                writeTypeSpecFile(
                    protocolVersion.getResponseSealedClassName(packageName),
                    createSealedClassTypeSpecBuilder(
                        responseInterfaceClassName = protocolVersion.getResponseSealedClassName(packageName),
                    ).build(),
                    genFilesDir
                )
            }

            if (protocolVersion in protocolDoc.actions) {
                val (responseWrapperClassName, _, responseWrapperTypeVar, responseDataTypeVar) =
                    createJsonMessageWrapperInfo(protocolVersion)

                writeTypeSpecFile(
                    protocolVersion.getRequestSealedClassName(packageName),
                    createSealedClassTypeSpecBuilder(
                        responseInterfaceClassName = protocolVersion.getRequestSealedClassName(packageName),
                        typeVariables = arrayOf(responseWrapperTypeVar, responseDataTypeVar),
                    ).apply {
                        addKdoc(
                            "%L",
                            "A base class for requests. This class is only used for serializing requests to the signald " +
                                    "socket; the [${responseWrapperTypeVar.name}] type variable represents the response " +
                                    "JSON structure."
                        )

                        val responseWrapperSerializerProperty = createSerializerProperty(
                            RESPONSE_WRAPPER_SERIALIZER_PROPERTY_NAME, responseWrapperTypeVar, override = false
                        )
                        val responseDataSerializerProperty = createSerializerProperty(
                            RESPONSE_DATA_SERIALIZER_PROPERTY_NAME, responseDataTypeVar, override = false
                        )
                        addProperty(responseWrapperSerializerProperty)
                        addProperty(responseDataSerializerProperty)

                        val responseVerificationFunSpec = createResponseResolveFunSpec(
                            protocolVersion,
                            responseDataTypeVar,
                            ResponseResolveFunCreationParams.Abstract
                        )
                        addFunction(responseVerificationFunSpec)

                        val versionPropertySpec = PropertySpec.builder("version", String::class)
                            .addAnnotation(Required::class)
                            .addKdoc(
                                "%L",
                                // TODO: Look into the @EncodeDefault annotation in upcoming kotlinx.serialization 1.3.0
                                //  https://github.com/Kotlin/kotlinx.serialization/pull/1528
                                "The version to include in the request. As this class won't be used to " +
                                        "deserialize the response, the [Required] annotation is being used to " +
                                        "force this field to be serialized"
                            )
                            .initializer("%S", protocolVersion.name.lowercase())
                            .build()
                        addProperty(versionPropertySpec)

                        val idPropertySpec = PropertySpec.builder("id", String::class)
                            .addAnnotation(Required::class)
                            .addKdoc(
                                "%L",
                                // TODO: Look into the @EncodeDefault annotation in upcoming kotlinx.serialization 1.3.0
                                //  https://github.com/Kotlin/kotlinx.serialization/pull/1528
                                "The id to include in the request. This is expected to be present in the response JSON."
                            )
                            .initializer("%T.System.now().toEpochMilliseconds().toString()", Clock::class)
                            .build()
                        addProperty(idPropertySpec)

                        val idParameterSpec = ParameterSpec.builder("id", String::class)
                            .addKdoc(
                                "The id to include in the request. " +
                                        "This is expected to be present in the response JSON"
                            )
                            .defaultValue("this.%N", idPropertySpec)
                            .build()
                        addFunction(
                            FunSpec.builder(BASE_RESPONSE_SUBMIT_FUN_NAME)
                                .returns(responseDataTypeVar)
                                .addParameter("socketCommunicator", socketCommunicatorClassName)
                                .addAnnotation(
                                    AnnotationSpec.builder(ClassName("kotlin", "Throws"))
                                        .addMember("%T::class", requestFailedExceptionClassName)
                                        .build()
                                )
                                .addKdoc(
                                    "%L",
                                    "@throws ${requestFailedExceptionTypeSpec.name} if the signald socket " +
                                            "sends a bad or error response, or unable to serialize our request"
                                )
                                //.addKdoc(
                                //    "\n%L",
                                //    "@throws IOException if an I/O error occurs during socket communication"
                                //)
                                .addStatement("return $BASE_RESPONSE_SUBMIT_FUN_NAME(socketCommunicator, id)")
                                .build()
                        )
                        addFunction(
                            FunSpec.builder(BASE_RESPONSE_SUBMIT_FUN_NAME)
                                .addModifiers(KModifier.OPEN, KModifier.INTERNAL)
                                .returns(responseDataTypeVar)
                                .addAnnotation(
                                    AnnotationSpec.builder(ClassName("kotlin", "Throws"))
                                        .addMember("%T::class", requestFailedExceptionClassName)
                                        .build()
                                )
                                .addKdoc(
                                    "%L",
                                    "Marked as internal so tests can access. Normal API consumers should use " +
                                            "the one-parameter overload."
                                )
                                .addKdoc(
                                    "\n\n%L",
                                    "@throws ${requestFailedExceptionTypeSpec.name} if the signald socket " +
                                            "sends a bad or error response, or unable to serialize our request"
                                )
                                //.addKdoc(
                                //    "\n%L",
                                //    "@throws IOException if an I/O error occurs during socket communication"
                               // )
                                .addParameter("socketCommunicator", socketCommunicatorClassName)
                                .addParameter(idParameterSpec)
                                .addCode(
                                    """
                                    val requestJson = try { 
                                        %T.encodeToString(serializer(%L, %L), this)
                                    } catch (e: %T) {
                                        throw %T(
                                            responseJsonString = null, 
                                            cause = e, 
                                            extraMessage = "failed to serialize our request"
                                        )
                                    }
                                    
                                """.trimIndent(),
                                    signaldJsonClassName,
                                    responseWrapperSerializerProperty.name, responseDataSerializerProperty.name,
                                    SerializationException::class,
                                    requestFailedExceptionClassName
                                )
                                // Use * as the type variable since the server can send an error (and types are erased
                                // anyway)
                                .addCode(
                                    """
                                    val responseJson = socketCommunicator.%L(requestJson)
                                    val response: %T<%T> = try {
                                        %T.decodeFromString(%T.serializer(%L), responseJson)
                                    } catch (e: %T) {
                                        throw %T(responseJsonString = responseJson, cause = e)
                                    }
                                    
                                """.trimIndent(),
                                    SOCKET_COMM_SUBMIT_FUN_NAME,
                                    // val response: %T<%T> = try {
                                    responseWrapperClassName, STAR,
                                    signaldJsonClassName, responseWrapperClassName, responseDataSerializerProperty.name,
                                    // } catch (e: %T) {
                                    SerializationException::class,
                                    requestFailedExceptionClassName
                                )
                                .beginControlFlow(
                                    "if (response is %T)",
                                    UNEXPECTED_ERROR_ACTION_NAME.asClassName(packageName, protocolVersion)
                                )
                                .addStatement(
                                    "throw %T(responseJsonString = responseJson," +
                                            " errorBody = response.data," +
                                            " errorType = response.errorType," +
                                            " exception = response.exception," +
                                            " extraMessage = %S)",
                                    requestFailedExceptionClassName,
                                    "unexpected error"
                                )
                                .endControlFlow()
                                .beginControlFlow("if (response.id != %N)", idParameterSpec)
                                .addStatement(
                                    "throw %T(responseJsonString = responseJson, extraMessage = %P)",
                                    requestFailedExceptionClassName,
                                    "response has unexpected ID: \${response.id} (expected \$${idParameterSpec.name})"
                                )
                                .endControlFlow()
                                .beginControlFlow(
                                    "if (response.version != null && response.version != %N)",
                                    versionPropertySpec
                                )
                                .addStatement(
                                    "throw %T(responseJsonString = responseJson, extraMessage = %P)",
                                    requestFailedExceptionClassName,
                                    "response has unexpected version: " +
                                            "\${response.version} (expected \$${versionPropertySpec.name})"
                                )
                                .endControlFlow()
                                .beginControlFlow("if (!response.isSuccessful)")
                                .addStatement(
                                    "throw %T(responseJsonString = responseJson," +
                                            " errorBody = response.error," +
                                            " errorType = response.errorType," +
                                            " exception = response.exception)",
                                    requestFailedExceptionClassName
                                )
                                .endControlFlow()
                                .addCode("return %N(response) ?: ", responseVerificationFunSpec)
                                .addCode(
                                    "throw %T(responseJsonString = %L, extraMessage = %P)",
                                    requestFailedExceptionClassName,
                                    "responseJson",
                                    "response failed verification " +
                                            "(wrapper type: \${response::class.simpleName}, " +
                                            "data type: \${response.data!!::class.simpleName})"
                                )
                                .build()
                        )
                    }.build(),
                    genFilesDir
                )
            }
        }

        val actionRequestTypes = linkedMapOf<SignaldProtocolVersion, Map<SignaldType, ActionInfo>>()
        val actionResponseTypes = linkedMapOf<SignaldProtocolVersion, MutableSet<SignaldType>>()
        for ((version: SignaldProtocolVersion, actionMap: Map<SignaldActionName, Action>) in protocolDoc.actions) {
            val requestTypesToActionMap = actionRequestTypes.getOrPut(version) { linkedMapOf() }
                    as MutableMap<SignaldType, ActionInfo>
            val responseTypes: MutableSet<SignaldType> = actionResponseTypes.getOrPut(version) { linkedSetOf() }

            val structuresPackage = GenUtil.getStructuresPackage(packageName, version)
            val (jsonMsgWrapperClassName, jsonMsgWrapperTypeSpec) = createJsonMessageWrapperInfo(version)
            writeTypeSpecFile(jsonMsgWrapperClassName, jsonMsgWrapperTypeSpec, genFilesDir)

            val emptyResponseClassName = ClassName(structuresPackage, "EmptyResponse")
            val emptyResponseSpec = TypeSpec.objectBuilder(emptyResponseClassName)
                .superclass(version.getResponseSealedClassName(packageName))
                .addAnnotation(Serializable::class)
                .addKdoc("%L", "For requests that don't expect a response, representing {}.")
                .build()
            writeTypeSpecFile(emptyResponseClassName, emptyResponseSpec, genFilesDir)

            val allActionsForThisVersion: Sequence<Map.Entry<SignaldActionName, Action>> =
                actionMap.asSequence().plus(extraRequestWrappers[version]?.asSequence() ?: emptySequence())
            for ((actionName, actionDetail) in allActionsForThisVersion) {
                check(actionDetail.request !in requestTypesToActionMap) {
                    "Request ${actionDetail.request} is used for multiple actions"
                }

                val responseToUse = if (
                    version == SignaldProtocolVersion.v1 &&
                    (actionName.name == "subscribe" || actionName.name == "unsubscribe")
                ) {
                    // deal with race conditions with the ack message in subscribe and unsubscribe
                    SignaldType(getSubscriptionResponseClassName(version).simpleName)
                } else {
                    actionDetail.response
                }

                val responseClassName: ClassName = if (responseToUse != null) {
                    // Add it to the responseTypes set so that it can be recognized as a response later.
                    responseTypes.add(responseToUse)
                    when {
                        responseToUse.name.equals("string", ignoreCase = true) -> {
                            // add_server in v1 sets response to just a raw String
                            String::class.asClassName()
                        }
                        responseToUse == SignaldType.JSON_OBJECT_TYPE -> {
                            JsonObject::class.asClassName()
                        }
                        else -> {
                            ClassName(structuresPackage, responseToUse.name)
                        }
                    }
                } else {
                    emptyResponseClassName
                }

                val requestClassName = actionName.asClassName(packageName, version)
                requestTypesToActionMap[actionDetail.request] =
                    ActionInfo(requestClassName, responseClassName, actionName)

                val typeSpec = TypeSpec.classBuilder(requestClassName).apply {
                    if (actionDetail.request != SignaldType.UNUSED) {
                        val requestStructureClassName = ClassName(
                            GenUtil.getStructuresPackage(packageName, version),
                            actionDetail.request.name
                        )
                        addKdoc(
                            "%L",
                            "This class only represents the response from signald for the request. " +
                                "Make a request by creating an instance of [${requestStructureClassName.canonicalName}] " +
                                "and then calling its `${BASE_RESPONSE_SUBMIT_FUN_NAME}` function."
                        )
                    }

                    superclass(jsonMsgWrapperClassName.parameterizedBy(responseClassName))
                    addModifiers(KModifier.DATA)
                    addAnnotation(Serializable::class)
                    addAnnotation(
                        AnnotationSpec.builder(SerialName::class)
                            .addMember("%S", actionName)
                            .build()
                    )

                    val primaryConstructorBuilder = FunSpec.constructorBuilder()
                        .addModifiers(KModifier.PRIVATE)
                    for (propertySpecToOverride in jsonMsgWrapperTypeSpec.propertySpecs) {
                        // Don't override open classes, as that will cause serialization to detect duplicate keys.
                        // Only override abstract classes.
                        if (
                            KModifier.FINAL in propertySpecToOverride.modifiers ||
                            KModifier.ABSTRACT !in propertySpecToOverride.modifiers
                        ) {
                            continue
                        }

                        val responsePropertyType = jsonMsgWrapperTypeSpec.typeVariables.find { it.name == "Response" }!!
                            .let { typeVar ->
                                if (propertySpecToOverride.type.copy(nullable = true) == typeVar.copy(nullable = true)) {
                                    responseClassName.copy(nullable = true)
                                } else {
                                    propertySpecToOverride.type
                                }
                            }
                        val parameter = ParameterSpec.builder(propertySpecToOverride.name, responsePropertyType)
                            .apply { if (propertySpecToOverride.type.isNullable) defaultValue("null") }
                            .build()
                        val property = PropertySpec.builder(propertySpecToOverride.name, responsePropertyType)
                            .addModifiers(KModifier.OVERRIDE)
                            .initializer(propertySpecToOverride.name)
                            .build()
                        primaryConstructorBuilder.addParameter(parameter)
                        addProperty(property)
                    }
                    primaryConstructor(primaryConstructorBuilder.build())
                }.build()
                writeTypeSpecFile(requestClassName, typeSpec, genFilesDir)
            }
        }

        // Sometimes, we may need to modify structure, e.g. subclass something or create nested classes
        val specialTypeHandlers = getSpecialTypeHandler()

        for ((version: SignaldProtocolVersion, typeMapForVersion: Map<SignaldType, Structure>) in protocolDoc.types) {
            val structurePackage = GenUtil.getStructuresPackage(packageName, version)
            val requestTypes: Map<SignaldType, ActionInfo> = actionRequestTypes[version] ?: emptyMap()
            val responseTypes: Set<SignaldType> = actionResponseTypes[version] ?: emptySet()

            val sequence = typeMapForVersion.asSequence()
                .plus(extraTypes[version]?.asSequence() ?: emptySequence())
            for ((structureTypeName: SignaldType, structureDetails: Structure) in sequence) {
                val className = ClassName(structurePackage, structureTypeName.name)
                val typeSpec = TypeSpec.classBuilder(className).apply {
                    val isDataClass = structureDetails.fields.isNotEmpty()
                    if (isDataClass) {
                        addModifiers(KModifier.DATA)
                    }
                    addAnnotation(Serializable::class)

                    check(structureTypeName !in responseTypes || structureTypeName !in requestTypes) {
                        "$structureTypeName is used for both a response and a request"
                    }
                    if (structureTypeName in responseTypes) {
                        // TODO: Use @EncodeDefault in kotlinx.serialization 1.3.0 where applicable.
                        superclass(version.getResponseSealedClassName(packageName))
                    }

                    val responseActionInfo: ActionInfo?
                    if (structureTypeName in requestTypes) {
                        responseActionInfo = requestTypes[structureTypeName]!!
                        val (actionWrapperType, actionResponseDataType, actionName) = responseActionInfo
                        superclass(
                            version.getRequestSealedClassName(packageName).parameterizedBy(
                                actionWrapperType,
                                actionResponseDataType
                            )
                        )
                        addProperty(
                            createSerializerProperty(
                                RESPONSE_WRAPPER_SERIALIZER_PROPERTY_NAME, actionWrapperType, override = true
                            )
                        )
                        addProperty(
                            createSerializerProperty(
                                RESPONSE_DATA_SERIALIZER_PROPERTY_NAME, actionResponseDataType, override = true
                            )
                        )
                        addFunction(
                            createResponseResolveFunSpec(
                                version, actionResponseDataType,
                                ResponseResolveFunCreationParams.Override(actionWrapperType)
                            )
                        )
                        addAnnotation(AnnotationSpec.builder(SerialName::class).addMember("%S", actionName).build())
                    } else {
                        responseActionInfo = null
                    }

                    if (structureDetails.doc != null) {
                        addKdoc("%L", structureDetails.doc)
                    }

                    if (structureDetails.deprecated) {
                        require(structureDetails.removalDate != null) {
                            "expected deprecated structure to have a removal date: $structureTypeName [$structureDetails]"
                        }
                        val date = Instant.ofEpochSecond(structureDetails.removalDate)
                            .atZone(ZoneId.of("GMT"))
                            .format(DateTimeFormatter.RFC_1123_DATE_TIME)
                        addAnnotation(
                            AnnotationSpec.builder(Deprecated::class)
                                .addMember("\"Will be removed on $date\"")
                                .build()
                        )
                    }

                    val constructorBuilder: FunSpec.Builder?
                    if (isDataClass) {
                        constructorBuilder = FunSpec.constructorBuilder()
                        for ((fieldName, fieldDetail) in structureDetails.fields) {
                            val propertyName = fieldName.snakeDashToCamelCase()
                            val type = fieldDetail
                                .getTypeName(packageName, fieldName)
                                .copy(nullable = !fieldDetail.list && !fieldDetail.required)

                            val parameter = ParameterSpec.builder(propertyName, type).apply {
                                if (!fieldDetail.required) {
                                    if (!fieldDetail.list) {
                                        defaultValue("null")
                                    } else {
                                        defaultValue("%M()", MemberName("kotlin.collections", "emptyList"))
                                    }
                                }
                            }.build()
                            val property = PropertySpec.builder(propertyName, type).apply {
                                initializer(propertyName)
                                if (fieldName.isSnakeDashCase) {
                                    addAnnotation(
                                        AnnotationSpec.builder(SerialName::class)
                                            .addMember("%S", fieldName)
                                            .build()
                                    )
                                }
                                val kdoc = buildString {
                                    if (fieldDetail.doc != null) {
                                        append(fieldDetail.doc)
                                    }
                                    if (fieldDetail.example != null) {
                                        if (length != 0) {
                                            appendLine()
                                        }
                                        append("Example: ")
                                        append(fieldDetail.example)
                                    }
                                }
                                if (kdoc.isNotBlank()) {
                                    addKdoc("%L", kdoc)
                                }
                            }.build()

                            constructorBuilder.addParameter(parameter)
                            addProperty(property)
                        }
                        primaryConstructor(constructorBuilder.build())
                    } else {
                        constructorBuilder = null
                    }

                    specialTypeHandlers[version]
                        ?.get(structureTypeName)
                        ?.invoke(
                            this,
                            version,
                            className,
                            structureDetails,
                            constructorBuilder,
                            responseActionInfo
                        )
                }.build()
                writeTypeSpecFile(className, typeSpec, genFilesDir)
            }
        }
        println("Generated signald classes for ${protocolDoc.version.version}")
    }

    sealed class ResponseResolveFunCreationParams {
        object Abstract : ResponseResolveFunCreationParams()
        class Override(val responseWrapperType: TypeName) : ResponseResolveFunCreationParams()
    }

    private fun createResponseResolveFunSpec(
        version: SignaldProtocolVersion,
        responseDataType: TypeName,
        funCreationParams: ResponseResolveFunCreationParams,
    ): FunSpec {
        return FunSpec.builder(RESPONSE_RESOLVE_FUN_NAME).apply {
            val jsonMessageWrapperInfo = createJsonMessageWrapperInfo(version)
            val funParam =
                ParameterSpec.builder("responseWrapper", jsonMessageWrapperInfo.className.parameterizedBy(STAR))
                    .build()
            addParameter(funParam)
            returns(responseDataType.copy(nullable = true))
            when (funCreationParams) {
                is ResponseResolveFunCreationParams.Abstract -> {
                    addModifiers(KModifier.ABSTRACT)
                    addKdoc(
                        "A function to resolve the response body by verifying the type of the response and returning a " +
                                "non-null value iff the wrapper and data is the right type. This is desirable due to type " +
                                "erasure."
                    )
                }
                is ResponseResolveFunCreationParams.Override -> {
                    addModifiers(KModifier.OVERRIDE)
                    addCode(
                        """
                        return if (%N is %T && %N.data is %T) { 
                            %N.data 
                        } else {
                            null
                        }
                    """.trimIndent(),
                        funParam, funCreationParams.responseWrapperType,
                        funParam, responseDataType,
                        funParam
                    )
                }
            }
        }.build()
    }

    private fun createSerializerProperty(propertyName: String, type: TypeName, override: Boolean): PropertySpec {
        return PropertySpec.builder(propertyName, KSerializer::class.asClassName().parameterizedBy(type)).apply {
            addModifiers(KModifier.PROTECTED)
            if (override) {
                addModifiers(KModifier.OVERRIDE)
                getter(
                    FunSpec.getterBuilder().apply {
                        if (type.hasBuiltinCompanionSerializer) {
                            if (type.isNullable) {
                                addStatement(
                                    "return %T.%M().%M",
                                    // If we don't do this copy, it will use T?.serializer, which is wrong.
                                    type.copy(nullable = false),
                                    MemberName("kotlinx.serialization.builtins", "serializer"),
                                    MemberName("kotlinx.serialization.builtins", "nullable")
                                )
                            } else {
                                addStatement(
                                    "return %T.%M()",
                                    type,
                                    MemberName("kotlinx.serialization.builtins", "serializer"),
                                )
                            }
                        } else {
                            addStatement("return %T.serializer()", type)
                        }
                    }.build()
                )
            } else {
                addModifiers(KModifier.ABSTRACT)
            }
        }.build()
    }

    data class JsonMessageWrapperInfo(
        val className: ClassName,
        val typeSpec: TypeSpec,
        val responseWrapperTypeVar: TypeVariableName,
        val responseDataTypeVar: TypeVariableName,
    )

    /**
     * See
     * https://github.com/thefinn93/signald/blob/488fc612a2cd29f29920cc5ff21011c758182377/src/main/java/io/finn/signald/JsonMessageWrapper.java
     */
    private fun createJsonMessageWrapperInfo(version: SignaldProtocolVersion): JsonMessageWrapperInfo {
        val jsonMessageWrapperClassName = ClassName(GenUtil.getRequestsPackage(packageName, version), "JsonMessageWrapper")
        val responseTypeVariable = TypeVariableName("Response", KModifier.OUT)

        data class ParamInfo(val typeSpec: TypeName, val isAbstract: Boolean)

        val jsonMessageWrapperFieldsMap = mapOf(
            "id" to ParamInfo(String::class.asClassName().copy(nullable = true), isAbstract = false),
            // omit `type` field, since it will be used by kotlinx.serialization to discriminate between subclasses
            // TODO: Use @JsonClassDiscriminator in kotlinx.serialization 1.3.0 to make it more clear that the type
            //  field is used as a discriminator.
            "data" to ParamInfo(responseTypeVariable.copy(nullable = true), isAbstract = true),
            "error" to ParamInfo(JsonObject::class.asClassName().copy(nullable = true), isAbstract = false),
            "exception" to ParamInfo(String::class.asClassName().copy(nullable = true), isAbstract = false),
            "error_type" to ParamInfo(String::class.asClassName().copy(nullable = true), isAbstract = false),
            "version" to ParamInfo(String::class.asClassName().copy(nullable = true), isAbstract = false),
        )
        val jsonMessageWrapperTypeSpec = TypeSpec.classBuilder(jsonMessageWrapperClassName).apply {
            addTypeVariable(responseTypeVariable)
            addModifiers(KModifier.SEALED)
            addAnnotation(Serializable::class)
            addKdoc("%L", "Encapsulates the response schema from the signald socket.")

            for ((fieldName, paramInfo) in jsonMessageWrapperFieldsMap) {
                val propertyName = fieldName.snakeDashToCamelCase()
                val property = PropertySpec.builder(propertyName, paramInfo.typeSpec).apply {
                    addModifiers(if (paramInfo.isAbstract) KModifier.ABSTRACT else KModifier.OPEN)
                    if (fieldName.isSnakeDashCase) {
                        addAnnotation(
                            AnnotationSpec.builder(SerialName::class)
                                .addMember("%S", fieldName)
                                .build()
                        )
                    }
                    if (!paramInfo.isAbstract && paramInfo.typeSpec.isNullable) {
                        initializer("null")
                    }
                }.build()
                addProperty(property)
            }

            addProperty(
                PropertySpec.builder("isSuccessful", Boolean::class.asClassName(), KModifier.FINAL)
                    .getter(
                        FunSpec.getterBuilder()
                            .addStatement(
                                "return this !is %T && data != null && error == null && " +
                                        "errorType == null && exception == null",
                                UNEXPECTED_ERROR_ACTION_NAME.asClassName(packageName, version)
                            )
                            .build()
                    )
                    .build()
            )
        }.build()

        val responseTypeVar = TypeVariableName("ResponseData")
        val responseWrapperTypeVar = TypeVariableName(
            "ResponseWrapper",
            jsonMessageWrapperClassName.parameterizedBy(responseTypeVar)
        )
        return JsonMessageWrapperInfo(
            jsonMessageWrapperClassName,
            jsonMessageWrapperTypeSpec,
            responseWrapperTypeVar,
            responseTypeVar
        )
    }

    fun getClientMessageWrapperClassName(version: SignaldProtocolVersion) =
        ClassName(GenUtil.getStructuresPackage(packageName, version), "ClientMessageWrapper")

    /**
     * @return A map of special type handlers that act on a class builder. This will be called after the entire
     * class has been configured.
     */
    private fun getSpecialTypeHandler(): Versioned<Map<SignaldType, SpecialTypeHandler>> {
        val clientMessageWrapperSubclassHandler: SpecialTypeHandler = { version, className, _, constructorBuilder, _ ->
            require(constructorBuilder != null)

            val clientMessageWrapperClassName = getClientMessageWrapperClassName(version)

            if (version != SignaldProtocolVersion.v1) {
                System.err.println(
                    "Warning: Version is different (expected v1; got $version for $className --- schema should be verified"
                )
            }
            superclass(clientMessageWrapperClassName)
            addAnnotation(AnnotationSpec.builder(SerialName::class).addMember("%S", className.simpleName).build())

            // Move the properties into a nested data class.
            val properties = propertySpecs.toMutableList()
            propertySpecs.clear()

            val innerDataClassName = className.nestedClass("Data")
            val innerDataClass = TypeSpec.classBuilder(innerDataClassName).apply {
                superclass(clientMessageWrapperClassName.nestedClass("Data"))
                addModifiers(KModifier.DATA)
                addAnnotation(Serializable::class)
                addProperties(properties)
                primaryConstructor(constructorBuilder.build())
            }.build()
            addType(innerDataClass)

            primaryConstructor(
                FunSpec.constructorBuilder()
                    .addParameter(
                        ParameterSpec.builder("version", String::class.asClassName().copy(nullable = true))
                            .defaultValue("null")
                            .build()
                    )
                    .addParameter(ParameterSpec.builder("data", innerDataClassName).build())
                    .addParameter(
                        ParameterSpec.builder("error", Boolean::class.asClassName().copy(nullable = true))
                            .defaultValue("false")
                            .build()
                    )
                    .build()
            )

            addProperty(
                PropertySpec.builder("version", String::class.asClassName().copy(nullable = true))
                    .initializer("version")
                    .addModifiers(KModifier.OVERRIDE)
                    .build()
            )
            addProperty(
                PropertySpec.builder("data", innerDataClassName)
                    .initializer("data")
                    .addModifiers(KModifier.OVERRIDE)
                    .build()
            )
            addProperty(
                PropertySpec.builder("error", Boolean::class.asClassName().copy(nullable = true))
                    .initializer("error")
                    .addModifiers(KModifier.OVERRIDE)
                    .build()
            )
        }

        val subscribeUnsubscribeRequestHandler: SpecialTypeHandler = { version: SignaldProtocolVersion,
                                                                       _,
                                                                       _,
                                                                       _,
                                                                       actionInfo: ActionInfo?
            ->
            require(actionInfo != null)

            addFunction(
                FunSpec.builder(BASE_RESPONSE_SUBMIT_FUN_NAME).apply {
                    addModifiers(KModifier.OVERRIDE)
                    addParameter("socketCommunicator", socketCommunicatorClassName)
                    addParameter("id", String::class)
                    returns(getSubscriptionResponseClassName(version))
                    beginControlFlow("try")
                    addStatement("return super.${BASE_RESPONSE_SUBMIT_FUN_NAME}(socketCommunicator, id)")
                    endControlFlow()
                    beginControlFlow("catch (originalException: %T)", requestFailedExceptionClassName)
                    addComment("Because of race conditions where an incoming message can be sent / broadcasted through")
                    addComment("the socket before we receive the ${actionInfo.actionName} acknowledgement message,")
                    addComment("we parse and store all incoming messages until we get the ack.")
                    addCode(
                        """
                        if (originalException.cause !is %T) {
                            throw originalException
                        }
                        var rawJsonResponse = originalException.responseJsonString ?: throw originalException
                        val pendingChatMessages = mutableListOf<%T>()

                        for (i in 0 until 10) {
                            val incomingMessage: ClientMessageWrapper = try {
                                %T.decodeFromString(ClientMessageWrapper.serializer(), rawJsonResponse)
                            } catch (e: SerializationException) {
                                val nextResponse: %T<*> = try {
                                    SignaldJson.decodeFromString(
                                        JsonMessageWrapper.serializer(${RESPONSE_DATA_SERIALIZER_PROPERTY_NAME}),
                                        rawJsonResponse
                                    )
                                } catch (secondException: SerializationException) {
                                    throw RequestFailedException(
                                        responseJsonString = rawJsonResponse,
                                        extraMessage = "failed to get incoming messages during ${actionInfo.actionName}",
                                        cause = secondException
                                    )
                                }
                                if (nextResponse.id == id && $RESPONSE_RESOLVE_FUN_NAME(nextResponse) != null) {
                                    return %T(pendingChatMessages)
                                }
                                throw RequestFailedException(
                                    responseJsonString = rawJsonResponse,
                                    extraMessage = "failed to get incoming messages during ${actionInfo.actionName}",
                                    cause = e
                                )
                            }
                            pendingChatMessages.add(incomingMessage)
                            rawJsonResponse = socketCommunicator.${SOCKET_COMM_READLINE_FUN_NAME}() 
                                ?: throw %T(extraMessage = %S, cause = originalException)
                        }

                        throw %T(
                            extraMessage = %S,
                            cause = originalException
                        )
                    """.trimIndent(),
                        // if (originalException.cause !is %T) {
                        SerializationException::class,
                        // val pendingChatMessages = mutableListOf<%T>()
                        getClientMessageWrapperClassName(version),
                        // %T.serializer(ClientMessageWrapper.serializer(), rawJsonResponse)
                        signaldJsonClassName,
                        // val nextResponse: %T<*> = try {
                        createJsonMessageWrapperInfo(version).className,
                        // return %T(pendingChatMessages)
                        getSubscriptionResponseClassName(version),
                        // ?: throw %T(extraMessage = %S, cause = originalException)
                        requestFailedExceptionClassName, "unable to read line from socket",
                        // throw %T(
                        //     extraMessage = %S,
                        //     cause = originalException
                        // )
                        requestFailedExceptionClassName, "too many messages; didn't see subscribe acknowledgement"
                    )
                    endControlFlow()
                }.build()
            )
        }

        return mapOf(
            SignaldProtocolVersion.v0 to mapOf(
                SignaldType("JsonStickerPackOperationMessage") to { _, _, _, _, _ ->
                    addKdoc(
                        "\n%L",
                        "https://github.com/signalapp/Signal-Android/blob/44d014c4459e9ac34b74800002fa86b402d0501c/" +
                                "libsignal/service/src/main/java/org/whispersystems/signalservice/api/messages/" +
                                "multidevice/StickerPackOperationMessage.java"
                    )
                },
            ),
            SignaldProtocolVersion.v1 to mapOf(
                SignaldType("SubscribeRequest") to subscribeUnsubscribeRequestHandler,
                SignaldType("UnsubscribeRequest") to subscribeUnsubscribeRequestHandler,
                SignaldType(getClientMessageWrapperClassName(SignaldProtocolVersion.v1).simpleName) to { _,
                                                                                                         className: ClassName,
                                                                                                         _,
                                                                                                         _,
                                                                                                         _
                    ->
                    modifiers.remove(KModifier.DATA)
                    addModifiers(KModifier.SEALED)
                    primaryConstructor(null)
                    addType(
                        TypeSpec.classBuilder("Data")
                            .addModifiers(KModifier.SEALED)
                            .addAnnotation(Serializable::class)
                            .build()
                    )

                    for (index in propertySpecs.indices) {
                        propertySpecs[index] = propertySpecs[index]
                            .let { spec ->
                                spec.toBuilder(
                                    type = if (spec.name == "data") {
                                        className.nestedClass("Data")
                                    } else {
                                        spec.type
                                    }
                                )
                            }
                            .initializer(null)
                            .addModifiers(KModifier.ABSTRACT)
                            .build()
                    }

                    // TODO: Use @JsonClassDiscriminator in kotlinx.serialization 1.3.0 to make it more clear that the type
                    //  field is used as a discriminator.
                    propertySpecs.removeIf { it.name == "type" }
                    addKdoc(
                        "%L",
                        "\nNote that the `type` field has been removed. kotlinx.serialization uses that as a discriminator"
                    )
                },
                SignaldType("IncomingMessage") to clientMessageWrapperSubclassHandler,
                SignaldType("ListenerState") to clientMessageWrapperSubclassHandler,
                SignaldType("ExceptionWrapper") to clientMessageWrapperSubclassHandler,
            )
        )
    }

    private fun createSealedClassTypeSpecBuilder(
        responseInterfaceClassName: ClassName,
        vararg typeVariables: TypeVariableName
    ): TypeSpec.Builder = TypeSpec.classBuilder(responseInterfaceClassName)
        .addAnnotation(Serializable::class)
        .addModifiers(KModifier.SEALED)
        .apply { typeVariables.forEach(::addTypeVariable) }

    companion object {
        const val RESPONSE_RESOLVE_FUN_NAME = "getTypedResponseOrNull"
        const val RESPONSE_WRAPPER_SERIALIZER_PROPERTY_NAME = "responseWrapperSerializer"
        const val RESPONSE_DATA_SERIALIZER_PROPERTY_NAME = "responseDataSerializer"
        const val SOCKET_COMM_SUBMIT_FUN_NAME = "submit"
        const val SOCKET_COMM_READLINE_FUN_NAME = "readLine"
        const val BASE_RESPONSE_SUBMIT_FUN_NAME = "submit"
        val UNEXPECTED_ERROR_ACTION_NAME = SignaldActionName("unexpected_error")
    }
}
