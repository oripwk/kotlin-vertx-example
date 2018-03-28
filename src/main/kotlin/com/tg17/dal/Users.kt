package com.tg17.dal

import com.tg17.model.User
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.statements.UpdateBuilder

object Users : Table() {

    val id = integer("id").primaryKey().autoIncrement()
    val name = varchar("name", length = 50)
    val email = varchar("email", length = 50)

    fun map(row: ResultRow) = User(
            id = row[Users.id],
            name = row[Users.name],
            email = row[Users.email]
    )

    fun of(user: User, st: UpdateBuilder<Int>) {
        st[id] = user.id
        st[name] = user.name
        st[email] = user.email
    }
}