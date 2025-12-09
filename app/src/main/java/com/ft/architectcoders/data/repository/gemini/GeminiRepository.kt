package com.ft.architectcoders.data.repository.gemini

import com.ft.architectcoders.Result
import com.ft.architectcoders.domain.model.AiReview
import kotlinx.coroutines.flow.Flow

interface GeminiRepository {
    fun getMovieReview(
        movieId: Int,
        title: String,
        overview: String,
    ): Flow<Result<AiReview>>
}
