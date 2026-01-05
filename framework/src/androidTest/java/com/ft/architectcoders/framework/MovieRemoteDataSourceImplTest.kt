package com.ft.architectcoders.framework

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.ft.architectcoders.data.repository.region.RegionRepository
import com.ft.architectcoders.domain.Result
import com.ft.architectcoders.framework.remote.tmdb.TmdbService
import com.ft.architectcoders.framework.rules.MockWebServerRule
import com.ft.architectcoders.framework.rules.enqueueResponse
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import retrofit2.Retrofit
import retrofit2.create

@RunWith(AndroidJUnit4::class)
class MovieRemoteDataSourceImplTest {
    @get:Rule
    val mockWebServerRule = MockWebServerRule()

    private lateinit var tmdbService: TmdbService
    private lateinit var dataSource: MovieRemoteDataSourceImpl

    private val fakeRegionRepository =
        object : RegionRepository {
            override suspend fun findLastRegion(): String = "US"
        }

    private val json = Json { ignoreUnknownKeys = true }

    @Before
    fun setUp() {
        tmdbService =
            Retrofit.Builder()
                .baseUrl(mockWebServerRule.server.url("/"))
                .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
                .build()
                .create()

        dataSource =
            MovieRemoteDataSourceImpl(
                tmdbService = tmdbService,
                regionRepository = fakeRegionRepository,
            )
    }

    @Test
    fun fetchPopularMovies_returnsMoviesFromJson() =
        runTest {
            // Given
            mockWebServerRule.server.enqueueResponse("popular_movies.json")

            // When
            val result = dataSource.fetchPopularMovies()

            // Then
            assertTrue(result is Result.Success)
            val movies = (result as Result.Success).data
            assertEquals(3, movies.size)
            assertEquals("Fight Club", movies[0].title)
            assertEquals(550, movies[0].id)
            assertEquals("Pulp Fiction", movies[1].title)
            assertEquals(680, movies[1].id)
            assertEquals("Forrest Gump", movies[2].title)
            assertEquals(13, movies[2].id)
        }

    @Test
    fun fetchPopularMovies_mapsPostersCorrectly() =
        runTest {
            // Given
            mockWebServerRule.server.enqueueResponse("popular_movies.json")

            // When
            val result = dataSource.fetchPopularMovies()

            // Then
            assertTrue(result is Result.Success)
            val movies = (result as Result.Success).data
            assertEquals(
                "https://image.tmdb.org/t/p/w185//pB8BM7pdSp6B6Ih7QZ4DrQ3PmJK.jpg",
                movies[0].poster,
            )
        }

    @Test
    fun fetchPopularMovies_returnsErrorOnServerError() =
        runTest {
            // Given
            mockWebServerRule.server.enqueueResponse("popular_movies.json", code = 500)

            // When
            val result = dataSource.fetchPopularMovies()

            // Then
            assertTrue(result is Result.Error)
        }

    @Test
    fun fetchPopularMovies_requestIncludesRegion() =
        runTest {
            // Given
            mockWebServerRule.server.enqueueResponse("popular_movies.json")

            // When
            dataSource.fetchPopularMovies()

            // Then
            val request = mockWebServerRule.server.takeRequest()
            assertTrue(request.path!!.contains("region=US"))
        }
}
