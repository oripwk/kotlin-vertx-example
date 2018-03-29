package com.tg17.client

import io.vertx.core.buffer.Buffer
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.client.HttpResponse
import io.vertx.ext.web.client.WebClient
import io.vertx.kotlin.core.json.get
import io.vertx.kotlin.coroutines.awaitResult

class AlbumClient(private val ws: WebClient) {
    suspend fun getFirstAlbum(userId: Int): JsonObject {
        val response = awaitResult<HttpResponse<Buffer>> {
            ws.getAbs("https://jsonplaceholder.typicode.com/albums")
                    .addQueryParam("userId", userId.toString())
                    .send(it)
        }
        return response.bodyAsJsonArray()[0]
    }
}