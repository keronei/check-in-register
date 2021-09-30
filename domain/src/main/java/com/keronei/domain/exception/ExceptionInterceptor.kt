package com.keronei.domain.exception

interface ExceptionInterceptor {
    fun handleException(exception: Exception): Failure?
}