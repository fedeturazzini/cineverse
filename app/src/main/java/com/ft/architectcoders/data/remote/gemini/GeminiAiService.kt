package com.ft.architectcoders.data.remote.gemini

import com.ft.architectcoders.domain.model.AiReview

interface GeminiAiService {
    suspend fun generateMovieReview(
        title: String,
        overview: String,
    ): AiReview
}
