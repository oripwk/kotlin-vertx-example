package com.tg17.controllers

import io.vertx.ext.jdbc.JDBCClient
import io.vertx.ext.sql.ResultSet
import io.vertx.ext.sql.SQLConnection
import io.vertx.ext.sql.UpdateResult
import io.vertx.ext.web.RoutingContext
import io.vertx.kotlin.core.json.array
import io.vertx.kotlin.core.json.get
import io.vertx.kotlin.core.json.json
import io.vertx.kotlin.core.json.obj
import io.vertx.kotlin.coroutines.awaitResult

class MovieController constructor(private val client: JDBCClient)  {

    suspend fun getMovie(ctx: RoutingContext) {
        val id = ctx.pathParam("id")
        val result = awaitResult<ResultSet> { client.queryWithParams("SELECT TITLE FROM MOVIE WHERE ID=?", json { array(id) }, it) }
        if (result.rows.size == 1) {
            ctx.response().end(json {
                obj("id" to id, "title" to result.rows[0]["TITLE"]).encode()
            })
        } else {
            ctx.response().setStatusCode(404).end()
        }
    }

    suspend fun rateMovie(ctx: RoutingContext) {
        val movie = ctx.pathParam("id")
        val rating = Integer.parseInt(ctx.queryParam("getRating")[0])
        val connection = awaitResult<SQLConnection> { client.getConnection(it) }
        connection.use {
            val result = awaitResult<ResultSet> { connection.queryWithParams("SELECT TITLE FROM MOVIE WHERE ID=?", json { array(movie) }, it) }
            if (result.rows.size == 1) {
                awaitResult<UpdateResult> { connection.updateWithParams("INSERT INTO RATING (VALUE, MOVIE_ID) VALUES ?, ?", json { array(rating, movie) }, it) }
                ctx.response().setStatusCode(200).end()
            } else {
                ctx.response().setStatusCode(404).end()
            }
        }
    }

    suspend fun getRating(ctx: RoutingContext) {
        val id = ctx.pathParam("id")
        val result = awaitResult<ResultSet> { client.queryWithParams("SELECT AVG(VALUE) AS VALUE FROM RATING WHERE MOVIE_ID=?", json { array(id) }, it) }
        ctx.response().end(json {
            obj("id" to id, "getRating" to result.rows[0]["VALUE"]).encode()
        })
    }

}
