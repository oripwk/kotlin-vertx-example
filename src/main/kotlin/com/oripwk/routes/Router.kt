package com.oripwk.routes

import com.oripwk.controllers.UserController
import com.oripwk.extentions.Route.coroutineHandler
import io.vertx.ext.web.Router
import io.vertx.ext.web.handler.BodyHandler

class Router(controller: UserController, router: Router) {

    init {
        router.route().handler(BodyHandler.create())

        router.post("/user").coroutineHandler { controller.create(it) }
        router.post("/user").coroutineHandler { controller.updateById(it) }
        router.get("/user").coroutineHandler { controller.getAll(it) }
        router.get("/user/:id").coroutineHandler { controller.getById(it) }
        router.delete("/user/:id").coroutineHandler { controller.deleteById(it) }
    }
}