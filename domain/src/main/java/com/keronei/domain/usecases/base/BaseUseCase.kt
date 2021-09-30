package com.keronei.domain.usecases.base

interface BaseUseCase<in Params, out T> {
    suspend operator fun invoke (params : Params) : T
}