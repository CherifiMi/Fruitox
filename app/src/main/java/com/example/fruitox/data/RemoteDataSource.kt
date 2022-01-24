package com.example.fruitox.data

import com.example.fruitox.model.Fruit
import javax.inject.Inject

class RemoteDataSource @Inject constructor(private val fruitApi: FruitApi) {


    suspend fun getFruit(name: String): Fruit{
        return fruitApi.getFruit(name)
    }
}