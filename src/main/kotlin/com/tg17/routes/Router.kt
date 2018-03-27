package com.tg17.routes

import com.tg17.controllers.Controller
import io.vertx.ext.web.Route
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.experimental.launch

class Router constructor(private val controller: Controller, router: Router) {

    init {
        router.get("/movie/:id").coroutineHandler { controller.getMovie(it) }
        router.post("/rateMovie/:id").coroutineHandler { controller.rateMovie(it) }
        router.get("/getRating/:id").coroutineHandler { controller.getRating(it) }
    }
}

fun Route.coroutineHandler(fn: suspend (RoutingContext) -> Unit): Route {
    return handler { ctx ->
        launch(ctx.vertx().dispatcher()) {
            try {
                fn(ctx)
            } catch (e: Exception) {
                ctx.fail(e)
            }
        }
    }
}