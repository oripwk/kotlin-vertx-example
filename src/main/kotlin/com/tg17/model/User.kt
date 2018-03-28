package com.tg17.model

data class User(
        val id: Int,
        val name: String,
        val email: String,
        val data: String? = null
)