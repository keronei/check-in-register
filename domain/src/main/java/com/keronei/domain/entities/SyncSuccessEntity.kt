package com.keronei.domain.entities

data class SyncSuccessEntity(
    val syncedSuccessfully: Boolean,
    val recordsUploaded: Int
)