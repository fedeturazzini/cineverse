package com.ft.architectcoders.data.repository.duel

import com.ft.architectcoders.data.datasource.DuelLocalDataSource
import com.ft.architectcoders.data.datasource.MovieLocalDataSource
import com.ft.architectcoders.data.datasource.MovieRemoteDataSource
import com.ft.architectcoders.domain.Result
import com.ft.architectcoders.domain.model.DuelSession
import com.ft.architectcoders.domain.model.Movie
import kotlinx.coroutines.flow.firstOrNull

class DuelRepositoryImpl(
    private val movieRemoteDataSource: MovieRemoteDataSource,
    private val movieLocalDataSource: MovieLocalDataSource,
    private val duelLocalDataSource: DuelLocalDataSource,
) : DuelRepository {

    override suspend fun getDuelCandidates(count: Int): Result<List<Movie>> {
        val localMovies = movieLocalDataSource.movies.firstOrNull() ?: emptyList()

        return if (localMovies.size >= count) {
            Result.Success(localMovies.shuffled().take(count))
        } else {
            when (val remoteResult = movieRemoteDataSource.fetchPopularMovies()) {
                is Result.Success -> {
                    movieLocalDataSource.saveMovies(remoteResult.data)
                    Result.Success(remoteResult.data.shuffled().take(count))
                }
                is Result.Error -> remoteResult
                is Result.Loading -> remoteResult
            }
        }
    }

    override suspend fun saveDuelSession(session: DuelSession): Long {
        return duelLocalDataSource.saveDuelSession(session)
    }

    override suspend fun markSessionCompleted(sessionId: Long) {
        duelLocalDataSource.markSessionCompleted(sessionId)
    }
}

