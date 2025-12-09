package com.ft.architectcoders.data.datasource.remote.gemini

import com.ft.architectcoders.Result
import com.ft.architectcoders.domain.model.AiReview

interface GeminiAiService {
    suspend fun generateMovieReview(
        title: String,
        overview: String,
    ): Result<AiReview>
}
