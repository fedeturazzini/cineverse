package com.ft.architectcoders.framework.remote.gemini

import com.ft.architectcoders.Result
import com.ft.architectcoders.domain.model.AiReview

interface GeminiAiService {
    suspend fun generateMovieReview(
        title: String,
        overview: String,
    ): Result<AiReview>
}
