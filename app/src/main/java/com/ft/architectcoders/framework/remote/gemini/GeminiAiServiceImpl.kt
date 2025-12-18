package com.ft.architectcoders.framework.remote.gemini

import com.ft.architectcoders.Result
import com.ft.architectcoders.data.error.toGeminiResult
import com.ft.architectcoders.domain.model.AiReview
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.generationConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GeminiAiServiceImpl(private val apiKey: String) : GeminiAiService {
    companion object {
        private const val MODEL_NAME = "gemini-2.0-flash-lite"
    }

    private val model =
        GenerativeModel(
            modelName = MODEL_NAME,
            apiKey = apiKey,
            generationConfig =
                generationConfig {
                    temperature = 0.7f
                    topK = 40
                    topP = 0.95f
                    maxOutputTokens = 200
                },
        )

    override suspend fun generateMovieReview(
        title: String,
        overview: String,
    ): Result<AiReview> {
        return withContext(Dispatchers.IO) {
            try {
                val prompt =
                    """
                    Eres un crítico de cine experto. Analiza esta película y proporciona:
                    1. Una puntuación del 1 al 5 (con decimales, ej: 4.5)
                    2. Una frase corta y cautivadora sobre la película (máximo 80 caracteres)

                    Película: $title
                    Sinopsis: $overview

                    Responde SOLO en este formato exacto:
                    RATING: [número]
                    QUOTE: [frase]
                    """.trimIndent()

                val response = model.generateContent(prompt)
                val text = response.text ?: throw Exception("No response from AI")

                Result.Success(parseAiResponse(text))
            } catch (e: Exception) {
                e.toGeminiResult()
            }
        }
    }

    private fun parseAiResponse(response: String): AiReview {
        val lines = response.lines()
        val rating =
            lines
                .find { it.startsWith("RATING:") }
                ?.substringAfter("RATING:")
                ?.trim()
                ?.toFloatOrNull() ?: 4.0f

        val quote =
            lines
                .find { it.startsWith("QUOTE:") }
                ?.substringAfter("QUOTE:")
                ?.trim() ?: "Una película fascinante"

        return AiReview(
            rating = rating.coerceIn(0f, 5f),
            quote = quote.take(100),
        )
    }
}
