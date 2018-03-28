package com.tg17.routes

import com.tg17.controllers.UserController
import com.tg17.extentions.Route.coroutineHandler
import io.vertx.ext.web.Router
import io.vertx.ext.web.handler.BodyHandler

class Router constructor(controller: UserController, router: Router) {

    init {
        router.route().handler(BodyHandler.create())

        router.post("/user").coroutineHandler { controller.create(it) }
        router.post("/user").coroutineHandler { controller.updateById(it) }
        router.get("/user").coroutineHandler { controller.getAll(it) }
        router.get("/user/:id").coroutineHandler { controller.getById(it) }
        router.delete("/user/:id").coroutineHandler { controller.deleteById(it) }
    }
}