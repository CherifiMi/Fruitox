package com.example.fruitox.data

import com.example.fruitox.model.Fruit
import retrofit2.http.GET
import retrofit2.http.Path

interface FruitApi {
    @GET("/api/fruit/{name}")
    suspend fun getFruit(@Path("name") name: String): Fruit
}