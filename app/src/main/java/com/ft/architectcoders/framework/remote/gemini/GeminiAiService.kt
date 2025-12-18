package com.ft.architectcoders.framework.remote.gemini

import com.ft.architectcoders.ui.common.Result
import com.ft.architectcoders.domain.model.AiReview

interface GeminiAiService {
    suspend fun generateMovieReview(
        title: String,
        overview: String,
    ): Result<AiReview>
}
