package com.ft.architectcoders.data.repository.gemini

import com.ft.architectcoders.domain.Result
import com.ft.architectcoders.data.datasource.MovieLocalDataSource
import com.ft.architectcoders.domain.model.AiReview
import com.ft.architectcoders.data.datasource.GeminiAiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow

class GeminiRepositoryImpl(
    private val geminiAiService: GeminiAiService,
    private val localDataSource: MovieLocalDataSource,
) : GeminiRepository {
    override fun getMovieReview(
        movieId: Int,
        title: String,
        overview: String,
    ): Flow<Result<AiReview>> =
        flow {
            val localMovie = localDataSource.findMovieById(movieId).firstOrNull()

            val aiReview =
                localMovie?.aiRating?.let { rating ->
                    localMovie.aiQuote?.let { quote ->
                        AiReview(rating, quote)
                    }
                }

            if (aiReview != null) {
                emit(Result.Success(aiReview))
            } else {
                val result = geminiAiService.generateMovieReview(title, overview)
                when (result) {
                    is Result.Success -> {
                        localDataSource.updateAiReview(movieId, result.data)
                        emit(result)
                    }
                    is Result.Error -> emit(result)
                    is Result.Loading -> emit(result)
                }
            }
        }
}
