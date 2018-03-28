package com.tg17.service

import com.tg17.dal.Users
import com.tg17.model.User
import kotlinx.coroutines.experimental.CoroutineDispatcher
import kotlinx.coroutines.experimental.async
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SchemaUtils.createMissingTablesAndColumns
import org.jetbrains.exposed.sql.transactions.transaction

class UserService constructor(val dispatcher: CoroutineDispatcher) {
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

    suspend fun create(user: User) = async(dispatcher) {
        transaction {
            Users.insert { Users.of(user, it) }
        }
    }.await()

    suspend fun updateById(user: User, id: Int) = async(dispatcher) {
        transaction {
            Users.update({ Users.id eq id }) {
                Users.of(user, it)
            }
        }
    }.await()

    suspend fun getById(id: Int) = async(dispatcher) {
        transaction {
            Users.map(Users.select { Users.id eq id }.first())
        }
    }.await()

    suspend fun deleteById(id: Int) = async(dispatcher) {
        transaction {
            Users.deleteWhere { Users.id eq id }
        }
    }.await()

    suspend fun getAll(): List<User> = async(dispatcher) {
        transaction {
            Users.selectAll().map(Users::map)
        }
    }.await()


}