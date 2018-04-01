package com.oripwk.controllers

import com.oripwk.client.AlbumClient
import com.oripwk.model.User
import com.oripwk.service.UserService
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.RoutingContext
import kotlinx.coroutines.experimental.runBlocking
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import io.mockk.*

object UserControllerSpec : Spek({
    on("getting first album") {
        it("should pass") {
            val userService: UserService = mockk()
            val albumClient: AlbumClient = mockk()
            val config: JsonObject = mockk(relaxed = true)
            val ctx: RoutingContext = mockk(relaxed = true)
            coEvery { userService.getAll() } returns listOf(User(1, "name", "email", "album"))
            val controller = UserController(userService, albumClient, config)
            runBlocking { controller.getAll(ctx) }
            verify { ctx.response() }
        }
    }
})
