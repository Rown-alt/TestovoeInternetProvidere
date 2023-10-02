package com.example.domain.repository

import com.example.domain.models.Street
import com.example.domain.api.AirNetApi
import com.example.domain.models.House

class RepositoryImpl(private val api: AirNetApi): Repository {
    override suspend fun getAllStreets(): Result<List<Street>> {
        return api.getAllStreets()
    }

    override suspend fun getHouseById(streetId: String): Result<List<House>> {
        return api.getHouseById(streetId)
    }
}