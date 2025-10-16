package com.ft.architectcoders.data.remote.tmdb

import com.ft.architectcoders.data.remote.tmdb.dto.RemoteMovie
import com.ft.architectcoders.data.remote.tmdb.dto.RemoteResult
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TmdbService {
    @GET("discover/movie?sort_by=popularity.desc")
    suspend fun getPopularMovies(
        @Query("region") region: String,
    ): RemoteResult

    @GET("movie/{id}")
    suspend fun getMovieById(
        @Path("id") id: Int,
    ): RemoteMovie
}
