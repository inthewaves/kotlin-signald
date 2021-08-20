package org.inthewaves.kotlinsignald.clientprotocol.v1.structures

import kotlinx.serialization.Serializable

/**
 * For requests that don't expect a response, representing {}.
 */
@Serializable
public object EmptyResponse : SignaldResponseBodyV1()
