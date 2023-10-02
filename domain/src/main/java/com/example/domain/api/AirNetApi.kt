package com.example.domain.api

import com.example.domain.models.House
import com.example.domain.models.Street
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface AirNetApi {
    @GET("v2/utils/get/allStreets/")
    suspend fun getAllStreets(): Result<List<Street>>

    @GET("v2/utils/get/houses/{street_id}")
    suspend fun getHouseById(@Query("street_id") streetId: String): Result<List<House>>
}