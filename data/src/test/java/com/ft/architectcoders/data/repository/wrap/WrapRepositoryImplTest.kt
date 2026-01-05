package com.ft.architectcoders.data.repository.wrap

import com.ft.architectcoders.data.datasource.GeminiAiService
import com.ft.architectcoders.data.datasource.WrapAiResult
import com.ft.architectcoders.domain.Result
import com.ft.architectcoders.domain.error.AppError
import com.ft.architectcoders.domain.model.TmdbGenres
import com.ft.architectcoders.domain.model.WrapGenreCount
import com.ft.architectcoders.domain.model.WrapGeminiInput
import com.ft.architectcoders.domain.model.WrapPeriod
import com.ft.architectcoders.domain.model.WrapStats
import com.ft.architectcoders.domain.model.WrapTotals
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever

@RunWith(MockitoJUnitRunner::class)
class WrapRepositoryImplTest {

    @Mock
    lateinit var geminiAiService: GeminiAiService

    @Test
    fun `generateWrap returns AI result when Gemini succeeds`() = runTest {
        val aiResult = WrapAiResult(
            funnyProfileSummary = "AI generated summary",
            archetypeName = "Explorador",
            archetypeTagline = "AI tagline",
            archetypeBullets = listOf("AI bullet 1", "AI bullet 2", "AI bullet 3"),
            shareText = "AI share text",
            genresLine = "AI genres",
            moviesLine = "AI movies",
            aiLine = null,
            actorLine = null,
            directorLine = null,
        )
        whenever(geminiAiService.generateCineverseWrap(any())).thenReturn(
            Result.Success(aiResult),
        )

        val repository = createRepository()
        val result = repository.generateWrap(sampleInput())

        assertTrue(result is Result.Success)
        val wrap = (result as Result.Success).data
        assertEquals("AI generated summary", wrap.funnyProfileSummary)
        assertEquals("Explorador", wrap.archetype.name)
        assertTrue(wrap.generatedByAi)
    }

    @Test
    fun `generateWrap returns deterministic fallback when Gemini fails`() = runTest {
        whenever(geminiAiService.generateCineverseWrap(any())).thenReturn(
            Result.Error(AppError.UnknownError(message = "Gemini error")),
        )

        val repository = createRepository()
        val input = sampleInput(
            topGenres = listOf(
                WrapGenreCount(TmdbGenres.THRILLER, TmdbGenres.nameForId(TmdbGenres.THRILLER)!!, 10),
                WrapGenreCount(TmdbGenres.MYSTERY, TmdbGenres.nameForId(TmdbGenres.MYSTERY)!!, 5),
            ),
        )
        val result = repository.generateWrap(input)

        assertTrue(result is Result.Success)
        val wrap = (result as Result.Success).data
        assertFalse(wrap.generatedByAi)
        // Should get thriller-based archetype
        assertEquals("Adicto al Plot Twist", wrap.archetype.name)
    }

    @Test
    fun `Deterministic fallback generates comedy archetype for comedy genre`() = runTest {
        whenever(geminiAiService.generateCineverseWrap(any())).thenReturn(
            Result.Error(AppError.UnknownError(message = "Gemini error")),
        )

        val repository = createRepository()
        val input = sampleInput(
            topGenres = listOf(WrapGenreCount(TmdbGenres.COMEDY, TmdbGenres.nameForId(TmdbGenres.COMEDY)!!, 10)),
        )
        val result = repository.generateWrap(input)

        assertTrue(result is Result.Success)
        val wrap = (result as Result.Success).data
        assertEquals("Cazador de Risas", wrap.archetype.name)
    }

    @Test
    fun `Deterministic fallback generates sci-fi archetype for sci-fi genre`() = runTest {
        whenever(geminiAiService.generateCineverseWrap(any())).thenReturn(
            Result.Error(AppError.UnknownError(message = "Gemini error")),
        )

        val repository = createRepository()
        val input = sampleInput(
            topGenres = listOf(WrapGenreCount(TmdbGenres.SCIFI, TmdbGenres.nameForId(TmdbGenres.SCIFI)!!, 10)),
        )
        val result = repository.generateWrap(input)

        assertTrue(result is Result.Success)
        val wrap = (result as Result.Success).data
        assertEquals("Astronauta Nocturno", wrap.archetype.name)
    }

    @Test
    fun `Deterministic fallback generates eclectic archetype for unknown genres`() = runTest {
        whenever(geminiAiService.generateCineverseWrap(any())).thenReturn(
            Result.Error(AppError.UnknownError(message = "Gemini error")),
        )

        val repository = createRepository()
        val input = sampleInput(
            topGenres = listOf(WrapGenreCount(TmdbGenres.DOCUMENTARY, "Documentales", 10)),
        )
        val result = repository.generateWrap(input)

        assertTrue(result is Result.Success)
        val wrap = (result as Result.Success).data
        assertEquals("Explorador Ecléctico", wrap.archetype.name)
    }

    @Test
    fun `Deterministic fallback includes section copy`() = runTest {
        whenever(geminiAiService.generateCineverseWrap(any())).thenReturn(
            Result.Error(AppError.UnknownError(message = "Gemini error")),
        )

        val repository = createRepository()
        val input = sampleInput(
            topGenres = listOf(
                WrapGenreCount(TmdbGenres.DRAMA, TmdbGenres.nameForId(TmdbGenres.DRAMA)!!, 10),
                WrapGenreCount(TmdbGenres.ACTION, TmdbGenres.nameForId(TmdbGenres.ACTION)!!, 5),
            ),
        )
        val result = repository.generateWrap(input)

        assertTrue(result is Result.Success)
        val wrap = (result as Result.Success).data
        assertNotNull(wrap.sectionCopy.genresLine)
        assertTrue(wrap.sectionCopy.genresLine!!.contains("Drama"))
    }

    @Test
    fun `Deterministic fallback shareText includes archetype and hashtag`() = runTest {
        whenever(geminiAiService.generateCineverseWrap(any())).thenReturn(
            Result.Error(AppError.UnknownError(message = "Gemini error")),
        )

        val repository = createRepository()
        val input = sampleInput(
            topGenres = listOf(WrapGenreCount(TmdbGenres.DRAMA, TmdbGenres.nameForId(TmdbGenres.DRAMA)!!, 10)),
        )
        val result = repository.generateWrap(input)

        assertTrue(result is Result.Success)
        val wrap = (result as Result.Success).data
        assertTrue(wrap.shareText.contains("#Cineverso60s"))
        assertTrue(wrap.shareText.contains(wrap.archetype.name))
    }

    private fun createRepository() = WrapRepositoryImpl(
        geminiAiService = geminiAiService,
    )

    private fun sampleInput(
        topGenres: List<WrapGenreCount> = listOf(WrapGenreCount(TmdbGenres.DRAMA, TmdbGenres.nameForId(TmdbGenres.DRAMA)!!, 10)),
    ) = WrapGeminiInput(
        period = WrapPeriod("2025-01-01", "2025-01-31"),
        profileName = "Test",
        profileRegion = "US",
        stats = WrapStats(
            period = WrapPeriod("2025-01-01", "2025-01-31"),
            totals = WrapTotals(10, 5, 2),
            topGenres = topGenres,
            topMovies = emptyList(),
            favoritesHighlights = emptyList(),
            topActors = emptyList(),
            topDirectors = emptyList(),
            aiSearchHighlights = null,
        ),
    )
}
