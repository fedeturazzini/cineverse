package com.ft.architectcoders.ui.screens.home

import app.cash.turbine.test
import com.ft.architectcoders.domain.model.Movie
import com.ft.architectcoders.test.data.FakeFetchMoviesUseCase
import com.ft.architectcoders.test.data.FakeRegionDataSource
import com.ft.architectcoders.test.data.FakeRegionRepository
import com.ft.architectcoders.test.data.buildMoviesRepositoryWith
import com.ft.architectcoders.test.sampleMovies
import com.ft.architectcoders.test.testrules.CoroutinesTestRule
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

class HomeIntegrationTests {

    @get:Rule
    val coroutinesTestRule = CoroutinesTestRule()

    @Test
    fun `Data is loaded from server when local data source is empty`() = runTest  {
        val remoteData = sampleMovies(1,2,3)

        val viewModel = buildViewModelWith(remoteData = remoteData)

        viewModel.state.test {
            assertEquals(UiState(isLoading = true), awaitItem())
            
            // When
            viewModel.permissionGranted()

            // Then
            val successState = awaitItem()
            assertEquals(remoteData, successState.movies)
            assertEquals(false, successState.isLoading)
        }
    }

    @Test
    fun `Data is loaded from local source when available`() = runTest {
        val localData = sampleMovies(1, 2)
        val viewModel = buildViewModelWith(localData = localData)

        viewModel.state.test {
            assertEquals(UiState(isLoading = true), awaitItem())

            // When
            viewModel.permissionGranted()

            // Then
            val successState = awaitItem()
            assertEquals(localData, successState.movies)
            assertEquals(false, successState.isLoading)
        }

    }
}


private fun buildViewModelWith(
    localData: List<Movie> = emptyList(),
    remoteData: List<Movie> = emptyList()
): HomeViewModel {
    val fetchMoviesUseCase =
        FakeFetchMoviesUseCase(buildMoviesRepositoryWith(localData, remoteData))
    val regionRepository = FakeRegionRepository(FakeRegionDataSource())
    return HomeViewModel(fetchMoviesUseCase, regionRepository)
}
