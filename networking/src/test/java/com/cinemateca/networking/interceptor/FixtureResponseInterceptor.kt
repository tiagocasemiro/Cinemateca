package com.cinemateca.networking.interceptor

import java.io.IOException
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody

internal class FixtureResponseInterceptor(
    private val fixtureLoader: (String) -> String,
) : Interceptor {
    private var responseConfiguration: FixtureResponse? = null

    var interceptedRequestCount: Int = 0
        private set

    fun respondWith(
        fixture: String,
        expectedRequest: ExpectedRequest,
        statusCode: Int = 200,
    ) {
        check(responseConfiguration == null) {
            "The previous fixture response was not consumed"
        }
        responseConfiguration = FixtureResponse(
            fixture = fixture,
            expectedRequest = expectedRequest,
            statusCode = statusCode,
        )
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        interceptedRequestCount += 1
        val request = chain.request()
        val configuration = checkNotNull(responseConfiguration) {
            "Configure a fixture response before executing the request"
        }
        responseConfiguration = null

        val inconsistencies = configuration.expectedRequest.validate(request)
        if (inconsistencies.isNotEmpty()) {
            throw InconsistentRequestException(inconsistencies)
        }

        val body = fixtureLoader(configuration.fixture)
        return Response.Builder()
            .request(request)
            .protocol(Protocol.HTTP_1_1)
            .code(configuration.statusCode)
            .message(configuration.statusCode.httpMessage())
            .header("Content-Type", JSON_MEDIA_TYPE.toString())
            .body(body.toResponseBody(JSON_MEDIA_TYPE))
            .build()
    }

    private data class FixtureResponse(
        val fixture: String,
        val expectedRequest: ExpectedRequest,
        val statusCode: Int,
    )

    private companion object {
        val JSON_MEDIA_TYPE = "application/json; charset=utf-8".toMediaType()
    }
}

internal data class ExpectedRequest(
    val method: String = "GET",
    val encodedPath: String,
    val query: Map<String, String> = emptyMap(),
    val headers: Map<String, String> = emptyMap(),
) {
    fun validate(request: okhttp3.Request): List<String> {
        val inconsistencies = mutableListOf<String>()

        if (request.method != method) {
            inconsistencies += "method expected <$method> but was <${request.method}>"
        }
        if (request.url.encodedPath != encodedPath) {
            inconsistencies +=
                "path expected <$encodedPath> but was <${request.url.encodedPath}>"
        }

        val actualQueryNames = request.url.queryParameterNames
        val missingQueryNames = query.keys - actualQueryNames
        val unexpectedQueryNames = actualQueryNames - query.keys
        if (missingQueryNames.isNotEmpty()) {
            inconsistencies += "missing query parameters <$missingQueryNames>"
        }
        if (unexpectedQueryNames.isNotEmpty()) {
            inconsistencies += "unexpected query parameters <$unexpectedQueryNames>"
        }
        query.forEach { (name, expectedValue) ->
            val actualValue = request.url.queryParameter(name)
            if (actualValue != expectedValue) {
                inconsistencies +=
                    "query <$name> expected <$expectedValue> but was <$actualValue>"
            }
        }

        headers.forEach { (name, expectedValue) ->
            val actualValue = request.header(name)
            if (actualValue != expectedValue) {
                inconsistencies +=
                    "header <$name> expected <$expectedValue> but was <$actualValue>"
            }
        }

        return inconsistencies
    }
}

internal class InconsistentRequestException(
    val inconsistencies: List<String>,
) : IOException(
    inconsistencies.joinToString(
        prefix = "Inconsistent request:\n- ",
        separator = "\n- ",
    ),
)

private fun Int.httpMessage(): String = when (this) {
    in 200..299 -> "OK"
    400 -> "Bad Request"
    401 -> "Unauthorized"
    404 -> "Not Found"
    500 -> "Internal Server Error"
    else -> "HTTP $this"
}
