package com.keronei.domain.entities

data class UserEntity(
    val id: String,
    val phoneNumber: String,
    val canCheckIn: Boolean
)