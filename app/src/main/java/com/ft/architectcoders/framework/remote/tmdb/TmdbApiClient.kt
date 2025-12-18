package com.ft.architectcoders.framework.remote.tmdb

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.create

object TmdbApiClient {
    private const val BASE_URL = "https://api.themoviedb.org/3/"

    private val json =
        Json {
            ignoreUnknownKeys = true
        }

    fun build(apiKey: String): TmdbService {
        val okHttpClient =
            OkHttpClient.Builder()
                .addInterceptor { chain -> apiKeyAsQuery(chain, apiKey) }
                .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create()
    }

    private fun apiKeyAsQuery(
        chain: Interceptor.Chain,
        apiKey: String,
    ) = chain.proceed(
        chain.request()
            .newBuilder()
            .url(
                chain
                    .request()
                    .url
                    .newBuilder()
                    .addQueryParameter("api_key", apiKey)
                    .build(),
            )
            .build(),
    )
}
