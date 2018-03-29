package com.tg17.service

import com.tg17.dal.Users
import com.tg17.execBlocking
import com.tg17.model.User
import io.vertx.core.Vertx
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SchemaUtils.createMissingTablesAndColumns
import org.jetbrains.exposed.sql.transactions.transaction

class UserService(val vx: Vertx) {
    init {
        Database.connect(
                url = "jdbc:mysql://127.0.0.1/kotlin_vertx",
                user = "root",
                password = "password",
                driver = "com.mysql.jdbc.Driver"
        )
        transaction {
            createMissingTablesAndColumns(Users)
        }
    }

    suspend fun create(user: User): Unit = execBlocking(vx) {
        transaction {
            Users.insert { Users.of(user, it) }
        }
    }

    suspend fun updateById(user: User, id: Int): Unit = execBlocking(vx) {
        transaction {
            Users.update({ Users.id eq id }) {
                Users.of(user, it)
            }
        }
    }

    suspend fun getById(id: Int): User = execBlocking(vx) {
        transaction {
            Users.map(Users.select { Users.id eq id }.first())
        }
    }

    suspend fun deleteById(id: Int): Unit = execBlocking(vx) {
        transaction {
            Users.deleteWhere { Users.id eq id }
        }
    }

    suspend fun getAll(): List<User> = execBlocking(vx) {
        transaction {
            Users.selectAll().map(Users::map)
        }
    }
}