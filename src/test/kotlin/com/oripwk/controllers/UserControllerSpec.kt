package com.oripwk.controllers

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import com.oripwk.client.AlbumClient
import com.oripwk.model.User
import com.oripwk.service.UserService
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.RoutingContext
import kotlinx.coroutines.experimental.runBlocking
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import org.mockito.Mockito

object UserControllerSpec : Spek({
    on("getting first album") {
        it("should pass") {
            // TODO fix
            runBlocking {
                val userService: UserService = mock()
                val albumClient: AlbumClient = mock()
                val config: JsonObject = mock()
                val ctx: RoutingContext = mock()
                whenever(userService.getAll()).thenReturn(listOf(User(1, "name", "email", "album")))
                val controller = UserController(userService, albumClient, config)
                controller.getAll(ctx)
                Mockito.verify(ctx)
            }
        }
    }
})
