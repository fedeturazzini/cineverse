package com.ft.architectcoders.data.datasource

/**
 * Result from Gemini AI for the Cineverso Wrap generation.
 */
data class WrapAiResult(
    val funnyProfileSummary: String,
    val archetypeName: String,
    val archetypeTagline: String,
    val archetypeBullets: List<String>,
    val shareText: String,
    val genresLine: String?,
    val moviesLine: String?,
    val aiLine: String?,
    val actorLine: String?,
    val directorLine: String?,
)

