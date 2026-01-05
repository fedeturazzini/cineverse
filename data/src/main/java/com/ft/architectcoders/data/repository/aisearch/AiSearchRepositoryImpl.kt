package com.ft.architectcoders.data.repository.aisearch

import com.ft.architectcoders.data.datasource.AiSearchLocalDataSource
import com.ft.architectcoders.data.datasource.GeminiAiService
import com.ft.architectcoders.data.datasource.MovieRemoteDataSource
import com.ft.architectcoders.domain.Result
import com.ft.architectcoders.domain.model.AiSearchSession
import com.ft.architectcoders.domain.model.AiSearchTurnResult
import com.ft.architectcoders.domain.model.AiSearchUserSignals
import com.ft.architectcoders.domain.model.ChatMessage
import com.ft.architectcoders.domain.model.ChatRole
import com.ft.architectcoders.domain.model.Movie

class AiSearchRepositoryImpl(
    private val geminiAiService: GeminiAiService,
    private val movieRemoteDataSource: MovieRemoteDataSource,
    private val localDataSource: AiSearchLocalDataSource,
) : AiSearchRepository {

    override suspend fun processUserMessage(
        session: AiSearchSession,
        userMessage: String,
        userSignals: AiSearchUserSignals,
    ): Result<AiSearchTurnResult> {
        val userTurnCount = session.messages.count { it.role == ChatRole.USER }
        val turnIndex = userTurnCount + 1

        val updatedMessages = session.messages + ChatMessage(
            role = ChatRole.USER,
            content = userMessage,
        )

        val geminiResult = geminiAiService.generateAiSearchResponse(
            conversationHistory = updatedMessages,
            turnIndex = turnIndex,
            region = userSignals.region,
            topGenreIds = userSignals.topGenreIds,
            avoidMovieIds = userSignals.avoidMovieIds,
        )

        return when (geminiResult) {
            is Result.Success -> {
                val response = geminiResult.data

                if (response.isOutOfScope) {
                    return Result.Success(
                        AiSearchTurnResult(
                            assistantMessage = response.assistantMessage,
                            movies = emptyList(),
                            detectedPreferences = null,
                            isOutOfScope = true,
                            finalBullets = null,
                        )
                    )
                }

                val movies = fetchMoviesFromPlan(response.tmdbQueryPlan, userSignals)

                Result.Success(
                    AiSearchTurnResult(
                        assistantMessage = response.assistantMessage,
                        movies = movies,
                        detectedPreferences = response.detectedPreferences,
                        isOutOfScope = false,
                        finalBullets = response.finalBullets,
                    )
                )
            }
            is Result.Error -> {
                Result.Error(geminiResult.error)
            }
            is Result.Loading -> Result.Loading
        }
    }

    private suspend fun fetchMoviesFromPlan(
        plan: com.ft.architectcoders.data.datasource.TmdbQueryPlan?,
        userSignals: AiSearchUserSignals,
    ): List<Movie> {
        if (plan == null) {
            return emptyList()
        }

        val result = when (plan.type) {
            "SEARCH" -> {
                val query = plan.searchQuery ?: return emptyList()
                movieRemoteDataSource.searchMovies(query)
            }
            else -> {
                movieRemoteDataSource.discoverMovies(
                    genres = plan.genres,
                    excludeGenres = plan.excludeGenres,
                    sortBy = plan.sortBy ?: "popularity.desc",
                    minVoteAverage = plan.minVoteAverage,
                    yearFrom = plan.yearFrom,
                    yearTo = plan.yearTo,
                )
            }
        }

        return when (result) {
            is Result.Success -> result.data
                .filter { it.id !in userSignals.avoidMovieIds }
                .take(20)
            else -> emptyList()
        }
    }

    override suspend fun saveSession(session: AiSearchSession): Long {
        return localDataSource.saveSession(session)
    }

    override suspend fun getSessionById(id: Long): AiSearchSession? {
        return localDataSource.getSessionById(id)
    }
}

