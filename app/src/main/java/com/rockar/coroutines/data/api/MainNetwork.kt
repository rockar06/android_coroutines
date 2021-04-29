package com.rockar.coroutines.data.api

import retrofit2.Call
import retrofit2.http.GET

interface MainNetwork {

    @GET("/next/title")
    fun fetchNextTitleAsync(): Call<String>

    @GET("/next/title")
    suspend fun fetchNextTitle(): String
}
