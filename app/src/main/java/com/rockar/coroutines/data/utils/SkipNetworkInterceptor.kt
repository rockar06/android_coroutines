package com.rockar.coroutines.data.utils

import com.google.gson.Gson
import okhttp3.*

private val MOCK_TITLES = listOf(
    "Hello, coroutines!",
    "My favourite feature",
    "Async made easy",
    "Coroutines are easy",
    "Implement coroutines as posible"
)

class SkipNetworkInterceptor : Interceptor {

    private var lastResult: String = ""
    private val gson = Gson()
    private var attempts = 0

    override fun intercept(chain: Interceptor.Chain): Response {
        pretendToBlockForNetworkRequest()
        return if (throwRandomError()) {
            makeErrorResult(chain.request())
        } else {
            makeOkResult(chain.request())
        }
    }

    private fun pretendToBlockForNetworkRequest() = Thread.sleep(500)

    private fun throwRandomError() = attempts++ % 5 == 0

    private fun makeErrorResult(request: Request): Response {
        return Response.Builder()
            .code(500)
            .request(request)
            .protocol(Protocol.HTTP_1_1)
            .message("It was a bad day")
            .body(
                ResponseBody.create(
                MediaType.get("application/json"),
                gson.toJson(mapOf("cause" to "not sure"))))
            .build()
    }

    private fun makeOkResult(request: Request): Response {
        var nextResult = lastResult
        while (nextResult == lastResult) {
            nextResult = MOCK_TITLES.random()
        }
        lastResult = nextResult
        return Response.Builder()
            .code(200)
            .request(request)
            .protocol(Protocol.HTTP_1_1)
            .message("OK")
            .body(ResponseBody.create(
                MediaType.get("application/json"),
                gson.toJson(nextResult)))
            .build()
    }
}
