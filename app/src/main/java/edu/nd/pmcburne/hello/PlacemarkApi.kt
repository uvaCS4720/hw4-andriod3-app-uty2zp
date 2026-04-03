package edu.nd.pmcburne.hello

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface PlacemarkApi {
    @GET("placemarks.json")
    suspend fun getPlacemarks(): List<Placemark>
}

object RetrofitInstance {
    val api: PlacemarkApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://www.cs.virginia.edu/~wxt4gm/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PlacemarkApi::class.java)
    }
}