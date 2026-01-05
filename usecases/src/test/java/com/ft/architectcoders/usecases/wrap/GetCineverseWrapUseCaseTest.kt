package com.ft.architectcoders.usecases.wrap

import com.ft.architectcoders.data.repository.wrap.WrapRepository
import com.ft.architectcoders.domain.Result
import com.ft.architectcoders.domain.error.AppError
import com.ft.architectcoders.domain.model.CineverseWrap
import com.ft.architectcoders.domain.model.Profile
import com.ft.architectcoders.domain.model.WrapArchetype
import com.ft.architectcoders.domain.model.WrapPeriod
import com.ft.architectcoders.domain.model.WrapSectionCopy
import com.ft.architectcoders.domain.model.WrapStats
import com.ft.architectcoders.domain.model.WrapTotals
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever

@RunWith(MockitoJUnitRunner::class)
class GetCineverseWrapUseCaseTest {

    @Mock
    lateinit var buildWrapStatsUseCase: BuildWrapStatsUseCase

    @Mock
    lateinit var wrapRepository: WrapRepository

    @Test
    fun `Returns Empty when stats result is empty`() = runTest {
        whenever(buildWrapStatsUseCase()).thenReturn(
            WrapStatsResult(
                stats = null,
                profile = sampleProfile(),
                isEmpty = true,
            ),
        )

        val useCase = createUseCase()
        val result = useCase()

        assertTrue(result is Result.Success)
        val data = (result as Result.Success).data
        assertTrue(data is CineverseWrapResult.Empty)
    }

    @Test
    fun `Returns Empty when total interactions below threshold`() = runTest {
        val stats = sampleStats(moviesViewed = 1, favorites = 0, aiSearchSessions = 0)
        whenever(buildWrapStatsUseCase()).thenReturn(
            WrapStatsResult(
                stats = stats,
                profile = sampleProfile(),
                isEmpty = false,
            ),
        )

        val useCase = createUseCase()
        val result = useCase()

        assertTrue(result is Result.Success)
        val data = (result as Result.Success).data
        assertTrue(data is CineverseWrapResult.Empty)
        assertEquals(2, (data as CineverseWrapResult.Empty).interactionsNeeded)
    }

    @Test
    fun `Returns Success with wrap when enough data`() = runTest {
        val stats = sampleStats(moviesViewed = 10, favorites = 5, aiSearchSessions = 2)
        whenever(buildWrapStatsUseCase()).thenReturn(
            WrapStatsResult(
                stats = stats,
                profile = sampleProfile(),
                isEmpty = false,
            ),
        )
        whenever(wrapRepository.generateWrap(any())).thenReturn(
            Result.Success(sampleWrap()),
        )

        val useCase = createUseCase()
        val result = useCase()

        assertTrue(result is Result.Success)
        val data = (result as Result.Success).data
        assertTrue(data is CineverseWrapResult.Success)
    }

    @Test
    fun `Returns Error when repository fails`() = runTest {
        val stats = sampleStats(moviesViewed = 10, favorites = 5, aiSearchSessions = 2)
        whenever(buildWrapStatsUseCase()).thenReturn(
            WrapStatsResult(
                stats = stats,
                profile = sampleProfile(),
                isEmpty = false,
            ),
        )
        whenever(wrapRepository.generateWrap(any())).thenReturn(
            Result.Error(AppError.UnknownError(message = "Test error")),
        )

        val useCase = createUseCase()
        val result = useCase()

        assertTrue(result is Result.Error)
    }

    @Test
    fun `Profile name and region are passed to Gemini input when available`() = runTest {
        val stats = sampleStats(moviesViewed = 10, favorites = 5, aiSearchSessions = 2)
        val profile = Profile(
            name = "Test User",
            region = "AR",
        )
        whenever(buildWrapStatsUseCase()).thenReturn(
            WrapStatsResult(
                stats = stats,
                profile = profile,
                isEmpty = false,
            ),
        )

        var capturedInput: com.ft.architectcoders.domain.model.WrapGeminiInput? = null
        whenever(wrapRepository.generateWrap(any())).thenAnswer { invocation ->
            capturedInput = invocation.getArgument(0)
            Result.Success(sampleWrap())
        }

        val useCase = createUseCase()
        useCase()

        assertEquals("Test User", capturedInput?.profileName)
        assertEquals("AR", capturedInput?.profileRegion)
    }

    @Test
    fun `Blank profile name is passed as null`() = runTest {
        val stats = sampleStats(moviesViewed = 10, favorites = 5, aiSearchSessions = 2)
        val profile = Profile(
            name = "",
            region = "",
        )
        whenever(buildWrapStatsUseCase()).thenReturn(
            WrapStatsResult(
                stats = stats,
                profile = profile,
                isEmpty = false,
            ),
        )

        var capturedInput: com.ft.architectcoders.domain.model.WrapGeminiInput? = null
        whenever(wrapRepository.generateWrap(any())).thenAnswer { invocation ->
            capturedInput = invocation.getArgument(0)
            Result.Success(sampleWrap())
        }

        val useCase = createUseCase()
        useCase()

        assertEquals(null, capturedInput?.profileName)
        assertEquals(null, capturedInput?.profileRegion)
    }

    private fun createUseCase() = GetCineverseWrapUseCaseImpl(
        buildWrapStatsUseCase = buildWrapStatsUseCase,
        wrapRepository = wrapRepository,
    )

    private fun sampleProfile() = Profile(
        name = "Test",
        region = "US",
    )

    private fun sampleStats(
        moviesViewed: Int = 0,
        favorites: Int = 0,
        aiSearchSessions: Int = 0,
    ) = WrapStats(
        period = WrapPeriod("2025-01-01", "2025-01-31"),
        totals = WrapTotals(moviesViewed, favorites, aiSearchSessions),
        topGenres = emptyList(),
        topMovies = emptyList(),
        favoritesHighlights = emptyList(),
        topActors = emptyList(),
        topDirectors = emptyList(),
        aiSearchHighlights = null,
    )

    private fun sampleWrap() = CineverseWrap(
        period = WrapPeriod("2025-01-01", "2025-01-31"),
        funnyProfileSummary = "Test summary",
        archetype = WrapArchetype("Test", "Tagline", listOf("1", "2", "3")),
        sectionCopy = WrapSectionCopy(null, null, null, null, null),
        shareText = "Share",
        stats = sampleStats(10, 5, 2),
        generatedByAi = true,
    )
}
