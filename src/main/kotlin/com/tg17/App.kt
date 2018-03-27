package com.tg17

import com.tg17.controllers.Controller
import io.vertx.core.http.HttpServer
import io.vertx.ext.jdbc.JDBCClient
import io.vertx.ext.sql.SQLConnection
import io.vertx.ext.web.Router
import io.vertx.kotlin.core.json.json
import io.vertx.kotlin.core.json.obj
import io.vertx.kotlin.coroutines.CoroutineVerticle
import io.vertx.kotlin.coroutines.awaitResult


class App : CoroutineVerticle() {
    override suspend fun start() {
        val client = JDBCClient.createShared(vertx, json {
            obj(
                    "url" to "jdbc:hsqldb:mem:test?shutdown=true",
                    "driver_class" to "org.hsqldb.jdbcDriver",
                    "max_pool_size-loop" to 30
            )
        })

        // Populate database
        val statements = listOf(
                "CREATE TABLE MOVIE (ID VARCHAR(16) PRIMARY KEY, TITLE VARCHAR(256) NOT NULL)",
                "CREATE TABLE RATING (ID INTEGER IDENTITY PRIMARY KEY, value INTEGER, MOVIE_ID VARCHAR(16))",
                "INSERT INTO MOVIE (ID, TITLE) VALUES 'starwars', 'Star Wars'",
                "INSERT INTO MOVIE (ID, TITLE) VALUES 'indianajones', 'Indiana Jones'",
                "INSERT INTO RATING (VALUE, MOVIE_ID) VALUES 1, 'starwars'",
                "INSERT INTO RATING (VALUE, MOVIE_ID) VALUES 5, 'starwars'",
                "INSERT INTO RATING (VALUE, MOVIE_ID) VALUES 9, 'starwars'",
                "INSERT INTO RATING (VALUE, MOVIE_ID) VALUES 10, 'starwars'",
                "INSERT INTO RATING (VALUE, MOVIE_ID) VALUES 4, 'indianajones'",
                "INSERT INTO RATING (VALUE, MOVIE_ID) VALUES 7, 'indianajones'",
                "INSERT INTO RATING (VALUE, MOVIE_ID) VALUES 3, 'indianajones'",
                "INSERT INTO RATING (VALUE, MOVIE_ID) VALUES 9, 'indianajones'"
        )
        val connection = awaitResult<SQLConnection> { client.getConnection(it) }
        connection.use {
            for (statement in statements) {
                awaitResult<Void> { connection.execute(statement, it) }
            }
        }

        val router = Router.router(vertx)

        val controller = Controller(client)
        com.tg17.routes.Router(controller, router)

        awaitResult<HttpServer> {
            vertx.createHttpServer()
                    .requestHandler(router::accept)
                    .listen(config.getInteger("http.port", 8080), it)
        }
    }
}

