package ru.nikstep.redink.github.util

import com.github.kittinunf.fuel.core.FuelError
import com.github.kittinunf.fuel.core.Request
import com.github.kittinunf.fuel.core.Response
import com.github.kittinunf.result.Result
import mu.KLogger
import java.io.ByteArrayOutputStream

fun <T : Any> KLogger.responseInfo(triple: Triple<Request, Response, Result<T, FuelError>>) {
    val request = triple.first
    val response = triple.second
    this.info {
        response.statusCode.toString() +
                " " + response.responseMessage +
                " " + request.method.value +
                " " + request.path +
                if (response.statusCode in 400..499) " \n" + request.getBody() else ""
    }
}


private fun Request.getBody(): String = ByteArrayOutputStream().apply {
    bodyCallback?.invoke(request, this, 0)
}.toString()