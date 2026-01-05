package com.ft.architectcoders.test.data

import com.ft.architectcoders.data.datasource.DEFAULT_REGION
import com.ft.architectcoders.data.datasource.MovieLocalDataSource
import com.ft.architectcoders.data.datasource.MovieRemoteDataSource
import com.ft.architectcoders.data.datasource.RegionDataSource
import com.ft.architectcoders.data.repository.gemini.GeminiRepository
import com.ft.architectcoders.data.repository.movie.MovieRepository
import com.ft.architectcoders.data.repository.movie.MovieRepositoryImpl
import com.ft.architectcoders.data.repository.region.RegionRepository
import com.ft.architectcoders.domain.CineVerseLocation
import com.ft.architectcoders.domain.Result
import com.ft.architectcoders.domain.model.AiReview
import com.ft.architectcoders.domain.model.Cast
import com.ft.architectcoders.domain.model.Movie
import com.ft.architectcoders.domain.model.MovieVideo
import com.ft.architectcoders.test.sampleAiReview
import com.ft.architectcoders.test.sampleMovies
import com.ft.architectcoders.test.successResult
import com.ft.architectcoders.usecases.FetchMoviesUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

fun buildMoviesRepositoryWith(
    localData: List<Movie> = emptyList(),
    remoteData: List<Movie> = emptyList(),
): MovieRepository {
    val localDataSource =
        FakeLocalDataSource().apply {
            inMemoryMovies.value = localData
        }

    val remoteDataSource = FakeRemoteDataSource().apply { movies = remoteData }

    return MovieRepositoryImpl(remoteDataSource, localDataSource)
}

class FakeLocalDataSource : MovieLocalDataSource {
    val inMemoryMovies = MutableStateFlow<List<Movie>>(emptyList())

    override val movies: Flow<List<Movie>>
        get() = inMemoryMovies

    override fun findMovieById(id: Int): Flow<Movie?> = inMemoryMovies.map { it.firstOrNull { movie -> movie.id == id } }

    override suspend fun countMovies(): Int {
        return inMemoryMovies.value.size
    }

    override suspend fun saveMovies(movies: List<Movie>) {
        inMemoryMovies.value = movies
    }

    override suspend fun isEmpty(): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun clearMovies() {
        TODO("Not yet implemented")
    }

    override suspend fun updateAiReview(
        movieId: Int,
        review: AiReview,
    ) {
        TODO("Not yet implemented")
    }
}

class FakeRemoteDataSource : MovieRemoteDataSource {
    var movies = sampleMovies(1, 2, 3, 4, 5)

    override suspend fun fetchPopularMovies(): Result<List<Movie>> = successResult(movies)

    override suspend fun findMovieById(id: Int): Result<Movie> = successResult(movies.first { it.id == id })

    override suspend fun fetchMovieCredits(movieId: Int): Result<List<Cast>> {
        return successResult(emptyList())
    }

    override suspend fun fetchMovieVideos(movieId: Int): Result<List<MovieVideo>> {
        return successResult(emptyList())
    }

    override suspend fun searchMovies(query: String): Result<List<Movie>> {
        TODO("Not yet implemented")
    }

    override suspend fun discoverMovies(
        genres: List<Int>?,
        excludeGenres: List<Int>?,
        sortBy: String,
        minVoteAverage: Float?,
        yearFrom: Int?,
        yearTo: Int?,
    ): Result<List<Movie>> = successResult(movies)
}

// TODO: Ver si lo puedo usar en el futuro
class FakeRegionRepository(
    val fakeRegionDataSource: FakeRegionDataSource,
) : RegionRepository {
    override suspend fun findLastRegion(): String = fakeRegionDataSource.region
}

class FakeFetchMoviesUseCase(private val movieRepository: MovieRepository) : FetchMoviesUseCase {
    override fun invoke(): Flow<List<Movie>> = movieRepository.movies
}

class FakeRegionDataSource : RegionDataSource {
    var region: String = DEFAULT_REGION

    override suspend fun findLastRegion(): String = region

    override suspend fun CineVerseLocation.toRegion(): String {
        TODO("Not yet implemented")
    }
}

class FakeGeminiRepository : GeminiRepository {
    override fun getMovieReview(
        movieId: Int,
        title: String,
        overview: String,
    ): Flow<Result<AiReview>> {
        return flowOf(successResult(sampleAiReview()))
    }
}
