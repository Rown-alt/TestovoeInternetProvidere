package com.example.domain.models

import com.google.gson.annotations.SerializedName

data class House(
    @SerializedName("house_id" ) var houseId : String,
    @SerializedName("house"    ) var house   : String
)
