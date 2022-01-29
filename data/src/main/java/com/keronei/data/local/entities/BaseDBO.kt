package com.keronei.data.local.entities

import java.lang.reflect.Field

abstract class BaseDBO {
    abstract fun getValue(field: Field) : Any
}