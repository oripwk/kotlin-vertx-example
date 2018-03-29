package com.oripwk

import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.oripwk.client.AlbumClient
import com.oripwk.controllers.UserController
import com.oripwk.service.UserService
import io.vertx.config.ConfigRetriever
import io.vertx.config.ConfigRetrieverOptions
import io.vertx.config.ConfigStoreOptions
import io.vertx.core.http.HttpServer
import io.vertx.core.json.Json
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.Router
import io.vertx.ext.web.client.WebClient
import io.vertx.kotlin.core.json.json
import io.vertx.kotlin.core.json.obj
import io.vertx.kotlin.coroutines.CoroutineVerticle
import io.vertx.kotlin.coroutines.awaitResult


class App : CoroutineVerticle() {
    override suspend fun start() {
        Json.mapper.registerModule(KotlinModule())

        val config = awaitResult<JsonObject> {
            val hocon = ConfigStoreOptions()
                    .setConfig(json { obj("path" to "conf/application.conf") })
                    .setType("file")
                    .setFormat("hocon")
            ConfigRetriever.create(vertx, ConfigRetrieverOptions().addStore(hocon)).getConfig(it)
        }

        val ws = WebClient.create(vertx)
        val router = Router.router(vertx)
        val userService = UserService(vertx, config.getJsonObject("db").getJsonObject("sql"))
        val albumClient = AlbumClient(ws, config.getJsonObject("albums"))
        val controller = UserController(userService, albumClient, config.getJsonObject("sqs"))
        com.oripwk.routes.Router(controller, router)

        awaitResult<HttpServer> {
            vertx.createHttpServer()
                    .requestHandler(router::accept)
                    .listen(8080)
        }
    }
}

