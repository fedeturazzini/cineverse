package com.ft.architectcoders.ui.screens.home

import app.cash.turbine.test
import com.ft.architectcoders.data.repository.region.RegionRepository
import com.ft.architectcoders.test.sampleMovies
import com.ft.architectcoders.test.testrules.CoroutinesTestRule
import com.ft.architectcoders.usecases.FetchMoviesUseCase
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class HomeViewModelTest {

    @get:Rule
    val coroutinesTestRule = CoroutinesTestRule()

    @Mock
    lateinit var fetchMoviesUseCase: FetchMoviesUseCase

    @Mock
    lateinit var regionRepository: RegionRepository

    @Test
    fun `Initial state is loading`() = runTest {
        // Given
        val viewModel = HomeViewModel(fetchMoviesUseCase, regionRepository)

        // When
        viewModel.state.test {
            // Then
            assertEquals(UiState(isLoading = true), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `Movies are not requested if permission not granted`() = runTest {
        // Given
        val viewModel = HomeViewModel(fetchMoviesUseCase, regionRepository)

        // When
        viewModel.state.test {
            awaitItem()

            // Then
            verify(fetchMoviesUseCase, never())()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `Movies are requested when permission is granted`() = runTest {
        // Given
        val movies = sampleMovies(1, 2, 3)
        whenever(fetchMoviesUseCase()).thenReturn(flowOf(movies))
        whenever(regionRepository.findLastRegion()).thenReturn("ES")
        val viewModel = HomeViewModel(fetchMoviesUseCase, regionRepository)

        viewModel.state.test {
            assertEquals(UiState(isLoading = true), awaitItem())

            // When
            viewModel.permissionGranted()

            // Then
            val successState = awaitItem()
            assertEquals(movies, successState.movies)
            assertEquals("ES", successState.region)
            assertEquals(false, successState.isLoading)
            assertEquals(null, successState.error)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `Region is fetched from repository on success`() = runTest {
        // Given
        val movies = sampleMovies(1, 2, 3)
        whenever(fetchMoviesUseCase()).thenReturn(flowOf(movies))
        whenever(regionRepository.findLastRegion()).thenReturn("US")
        val viewModel = HomeViewModel(fetchMoviesUseCase, regionRepository)

        viewModel.state.test {
            awaitItem()

            // When
            viewModel.permissionGranted()

            // Then
            val successState = awaitItem()
            assertEquals("US", successState.region)
            verify(regionRepository).findLastRegion()

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `Error state shows error message`() = runTest {
        // Given
        val errorMessage = "Network error"
        whenever(fetchMoviesUseCase()).thenReturn(
            flow { throw RuntimeException(errorMessage) }
        )
        val viewModel = HomeViewModel(fetchMoviesUseCase, regionRepository)

        viewModel.state.test {
            awaitItem()

            // When
            viewModel.permissionGranted()

            // Then
            val errorState = awaitItem()
            assertEquals(false, errorState.isLoading)
            assertNotNull(errorState.error)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `UseCase is invoked after permission granted`() = runTest {
        // Given
        val movies = sampleMovies(1, 2, 3)
        whenever(fetchMoviesUseCase()).thenReturn(flowOf(movies))
        whenever(regionRepository.findLastRegion()).thenReturn("ES")
        val viewModel = HomeViewModel(fetchMoviesUseCase, regionRepository)

        viewModel.state.test {
            awaitItem()

            // When
            viewModel.permissionGranted()
            awaitItem()

            // Then
            verify(fetchMoviesUseCase)()

            cancelAndIgnoreRemainingEvents()
        }
    }
}
