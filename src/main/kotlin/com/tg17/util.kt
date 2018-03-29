package com.tg17

import io.vertx.core.Future
import io.vertx.core.Handler
import io.vertx.core.Vertx
import io.vertx.kotlin.coroutines.awaitResult
import kotlinx.coroutines.experimental.async

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