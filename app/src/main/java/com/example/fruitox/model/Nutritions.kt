package com.example.fruitox.model


import com.google.gson.annotations.SerializedName

data class Nutritions(
    @SerializedName("calories")
    val calories: Int,
    @SerializedName("carbohydrates")
    val carbohydrates: Double,
    @SerializedName("fat")
    val fat: Double,
    @SerializedName("protein")
    val protein: Double,
    @SerializedName("sugar")
    val sugar: Double
)