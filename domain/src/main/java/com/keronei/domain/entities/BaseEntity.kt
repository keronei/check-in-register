package com.keronei.domain.entities

import java.lang.reflect.Field

abstract class BaseEntity {
    abstract fun getValue(field: Field) : Any
}