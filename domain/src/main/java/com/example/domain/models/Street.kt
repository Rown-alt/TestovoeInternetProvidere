package com.example.domain.models

import com.google.gson.annotations.SerializedName

data class Street(
    @SerializedName("street_id" ) var streetId : String,
    @SerializedName("street"    ) var street   : String
)
