package ru.nikstep.redink.util

import com.github.kittinunf.fuel.core.FuelError
import com.github.kittinunf.fuel.core.Request
import com.github.kittinunf.fuel.core.Response
import com.github.kittinunf.result.Result
import mu.KLogger
import java.io.ByteArrayOutputStream

internal fun <T : Any> KLogger.logged(
    request: Request,
    action: () -> Triple<Request, Response, Result<T, FuelError>>
): Triple<Request, Response, Result<T, FuelError>> {
    this.requestInfo(request)
    val triple = action()
    this.responseInfo(triple)
    return triple
}

private fun KLogger.requestInfo(request: Request) {
    this.info { "Sending ${request.method.value} ${request.path}" }
}

private fun <T : Any> KLogger.responseInfo(triple: Triple<Request, Response, Result<T, FuelError>>) {
    this.info {
        triple.apply {
            val request = first
            val response = second
            val requestBody = if (response.statusCode in 400..499) " \n" + request.getBody() else ""
            """${response.statusCode} ${response.responseMessage} ${request.method.value} ${request.path}
            | $requestBody""".trimMargin()
        }
    }
}


private fun Request.getBody(): String = ByteArrayOutputStream().apply {
    bodyCallback?.invoke(request, this, 0)
}.toString()