package com.tg17.controllers

import com.amazonaws.services.sqs.AmazonSQSAsyncClientBuilder
import com.amazonaws.services.sqs.model.SendMessageRequest
import com.amazonaws.services.sqs.model.SendMessageResult
import com.tg17.awaitResult
import com.tg17.client.AlbumClient
import com.tg17.model.User
import com.tg17.service.UserService
import io.netty.handler.codec.http.HttpHeaderNames.CONTENT_TYPE
import io.netty.handler.codec.http.HttpHeaderValues.APPLICATION_JSON
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.RoutingContext
import io.vertx.kotlin.core.json.Json
import io.vertx.kotlin.core.json.array

class UserController(
        private val userService: UserService,
        private val albumClient: AlbumClient,
        private val queueUrl: String
) {

    private val sqs = AmazonSQSAsyncClientBuilder.standard().build()

    suspend fun create(ctx: RoutingContext) {
        val userJson = ctx.bodyAsJson
        val user = userJson.mapTo(User::class.java)
        awaitResult<SendMessageRequest, SendMessageResult> { sqs.sendMessageAsync(queueUrl, userJson.encode(), it) }
        userService.create(user)
        ctx.response().end()
    }

    suspend fun updateById(ctx: RoutingContext) {
        val id = ctx.request().getParam("id").toInt()
        val user = ctx.bodyAsJson.mapTo(User::class.java)
        userService.updateById(user, id)
        ctx.response().end()
    }

    suspend fun getById(ctx: RoutingContext) {
        val id = ctx.request().getParam("id").toInt()
        val user = userService.getById(id)
        val albumTitle = albumClient.getFirstAlbum(1).getString("title")
        val userWithAlbumTitle = user.copy(albumTitle = albumTitle)
        ctx.response()
                .putHeader(CONTENT_TYPE, APPLICATION_JSON)
                .end(JsonObject.mapFrom(userWithAlbumTitle).encode())

    }

    suspend fun deleteById(ctx: RoutingContext) {
        val id = ctx.request().getParam("id").toInt()
        userService.deleteById(id)
        ctx.response().end()
    }

    suspend fun getAll(ctx: RoutingContext) {
        val users = userService.getAll()
        val jsons = Json.array(users.map(JsonObject::mapFrom))
        ctx.response()
                .putHeader(CONTENT_TYPE, APPLICATION_JSON)
                .end(jsons.encode())
    }

}