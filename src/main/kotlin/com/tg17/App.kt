package com.tg17

import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.tg17.client.AlbumClient
import com.tg17.controllers.UserController
import com.tg17.service.UserService
import io.vertx.core.http.HttpServer
import io.vertx.core.json.Json
import io.vertx.ext.web.Router
import io.vertx.ext.web.client.WebClient
import io.vertx.kotlin.coroutines.CoroutineVerticle
import io.vertx.kotlin.coroutines.awaitResult


class App : CoroutineVerticle() {
    override suspend fun start() {
        Json.mapper.registerModule(KotlinModule())

        val ws = WebClient.create(vertx)
        val router = Router.router(vertx)
        val userService = UserService(vertx)
        val albumClient = AlbumClient(ws)
        val controller = UserController(userService, albumClient, "https://eu-west-1.queue.amazonaws.com/750035664134/test")
        com.tg17.routes.Router(controller, router)

        awaitResult<HttpServer> {
            vertx.createHttpServer()
                    .requestHandler(router::accept)
                    .listen(8080)
        }
    }
}

