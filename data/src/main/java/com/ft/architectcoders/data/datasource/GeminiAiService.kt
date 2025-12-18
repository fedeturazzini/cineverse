package com.ft.architectcoders.data.datasource

import com.ft.architectcoders.domain.Result
import com.ft.architectcoders.domain.model.AiReview

interface GeminiAiService {
    suspend fun generateMovieReview(
        title: String,
        overview: String,
    ): Result<AiReview>
}