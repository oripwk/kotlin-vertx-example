package com.tg17

import io.vertx.core.AsyncResult
import io.vertx.core.Handler
import kotlin.coroutines.experimental.suspendCoroutine

/**
 * Converts Vert.x callbacks to Kotlin coroutines. Example:
 * Given an async API such as `mongoClient.find("books", query, { res -> ??? })` we can convert
 * the call to a coroutine and use it sequentially:
 *   val result = suspendAsync { mongoClient.find("books", query, it) }
 */
inline suspend fun <T> suspendAsync(crossinline callback: (Handler<AsyncResult<T>>) -> Unit) = suspendCoroutine<T> { cont ->
    callback(Handler { result: AsyncResult<T> ->
        if (result.succeeded()) {
            cont.resume(result.result())
        } else {
            cont.resumeWithException(result.cause())
        }
    })
}
