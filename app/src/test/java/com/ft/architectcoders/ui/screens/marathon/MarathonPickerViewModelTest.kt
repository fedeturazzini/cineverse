package com.ft.architectcoders.ui.screens.marathon

import app.cash.turbine.test
import com.ft.architectcoders.domain.model.MarathonThemeId
import com.ft.architectcoders.test.sampleMarathonTheme
import com.ft.architectcoders.test.testrules.CoroutinesTestRule
import com.ft.architectcoders.usecases.marathon.GetMarathonThemesUseCase
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNull
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class MarathonPickerViewModelTest {
    @get:Rule
    val coroutinesTestRule = CoroutinesTestRule()

    @Mock
    lateinit var getMarathonThemesUseCase: GetMarathonThemesUseCase

    @Test
    fun `Initial state contains themes`() = runTest {
        val themes = listOf(
            sampleMarathonTheme(MarathonThemeId.LAUGH),
            sampleMarathonTheme(MarathonThemeId.CRY),
        )
        whenever(getMarathonThemesUseCase()).thenReturn(themes)

        val viewModel = createViewModel()

        viewModel.state.test {
            val state = awaitItem()
            assertEquals(2, state.themes.size)
            assertNull(state.selectedThemeId)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onThemeSelected updates selectedThemeId`() = runTest {
        val themes = listOf(
            sampleMarathonTheme(MarathonThemeId.LAUGH),
            sampleMarathonTheme(MarathonThemeId.ADRENALINE),
        )
        whenever(getMarathonThemesUseCase()).thenReturn(themes)

        val viewModel = createViewModel()

        viewModel.state.test {
            awaitItem() // Initial state

            viewModel.onThemeSelected(MarathonThemeId.ADRENALINE)

            val state = awaitItem()
            assertEquals(MarathonThemeId.ADRENALINE, state.selectedThemeId)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `clearSelection resets selectedThemeId`() = runTest {
        val themes = listOf(sampleMarathonTheme(MarathonThemeId.LAUGH))
        whenever(getMarathonThemesUseCase()).thenReturn(themes)

        val viewModel = createViewModel()

        viewModel.state.test {
            awaitItem()

            viewModel.onThemeSelected(MarathonThemeId.LAUGH)
            awaitItem()

            viewModel.clearSelection()

            val state = awaitItem()
            assertNull(state.selectedThemeId)
            cancelAndIgnoreRemainingEvents()
        }
    }

    private fun createViewModel() = MarathonPickerViewModel(
        getMarathonThemesUseCase = getMarathonThemesUseCase,
    )
}

