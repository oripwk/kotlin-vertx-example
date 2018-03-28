package com.tg17

import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.tg17.controllers.UserController
import com.tg17.service.UserService
import io.vertx.core.http.HttpServer
import io.vertx.core.json.Json
import io.vertx.ext.web.Router
import io.vertx.kotlin.coroutines.CoroutineVerticle
import io.vertx.kotlin.coroutines.awaitResult
import io.vertx.kotlin.coroutines.dispatcher


class App : CoroutineVerticle() {
    override suspend fun start() {
        Json.mapper.registerModule(KotlinModule())

        val router = Router.router(vertx)
        val userService = UserService(vertx.dispatcher())
        val controller = UserController(userService)
        com.tg17.routes.Router(controller, router)

        awaitResult<HttpServer> {
            vertx.createHttpServer()
                    .requestHandler(router::accept)
                    .listen(8080)
        }
    }
}

