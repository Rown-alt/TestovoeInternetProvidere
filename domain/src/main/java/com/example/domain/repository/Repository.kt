package com.example.domain.repository

import com.example.domain.models.House
import com.example.domain.models.Street
import retrofit2.http.Path
import retrofit2.http.Query

interface Repository {
    suspend fun getAllStreets(): Result<List<Street>>
    suspend fun getHouseById(@Query("street_id") streetId: String): Result<List<House>>
}