package com.cinemateca.repository

interface DomainMapperResponse<T : Any> {
    fun mapToDomain(): T
}
