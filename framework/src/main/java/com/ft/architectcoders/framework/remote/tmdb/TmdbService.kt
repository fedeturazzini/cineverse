package com.ft.architectcoders.framework.remote.tmdb

import com.ft.architectcoders.framework.remote.tmdb.dto.RemoteCredits
import com.ft.architectcoders.framework.remote.tmdb.dto.RemoteMovie
import com.ft.architectcoders.framework.remote.tmdb.dto.RemoteResult
import com.ft.architectcoders.framework.remote.tmdb.dto.RemoteVideos
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

    @GET("movie/{id}/credits")
    suspend fun getMovieCredits(
        @Path("id") id: Int,
    ): RemoteCredits

    @GET("movie/{id}/videos")
    suspend fun getMovieVideos(
        @Path("id") id: Int,
    ): RemoteVideos

    @GET("search/movie")
    suspend fun searchMovies(
        @Query("query") query: String,
        @Query("region") region: String,
        @Query("page") page: Int = 1,
    ): RemoteResult
}
