package com.oripwk

import com.amazonaws.AmazonWebServiceRequest
import com.amazonaws.handlers.AsyncHandler
import io.vertx.core.AsyncResult
import io.vertx.core.Future
import io.vertx.core.Handler
import io.vertx.core.Vertx
import io.vertx.kotlin.coroutines.awaitResult
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.delay
import java.lang.Exception

suspend fun <T> execBlocking(vx: Vertx, fn: () -> T): T = async {
    val handler = Handler { future: Future<T> ->
        try {
            future.complete(fn())
        } catch (t: Throwable) {
            future.fail(t)
        }
    }
    awaitResult<T> { vx.executeBlocking(handler, it) }
}.await()

suspend fun <T> retry(times: Int, interval: Int, fn: () -> T?): T? {
    return try {
        fn()
    } catch (t: Throwable) {
        if (times == 0) {
            throw t
        }
        delay(interval)
        retry(times - 1, interval, fn)
    }
}


suspend fun <REQ : AmazonWebServiceRequest, RES> awaitResult(block: (AsyncHandler<REQ, RES>) -> Unit): RES = awaitResult { it ->

    fun <T> asyncResultSuccess(result: T) = object : AsyncResult<T> {
        override fun succeeded() = true
        override fun result(): T = result
        override fun failed(): Boolean = false
        override fun cause(): Throwable? = null
    }

    fun <T> asyncResultFailure(e: Exception) = object : AsyncResult<T> {
        override fun succeeded() = false
        override fun result(): T? = null
        override fun failed(): Boolean = true
        override fun cause(): Throwable? = e
    }

    block(object : AsyncHandler<REQ, RES> {
        override fun onSuccess(request: REQ, result: RES) = it.handle(asyncResultSuccess(result))
        override fun onError(e: Exception) = it.handle(asyncResultFailure(e))
    })
}
