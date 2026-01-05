package com.ft.architectcoders.usecases

import com.ft.architectcoders.test.sampleMovies
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.flowOf
import org.junit.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock

class FetchMovieUseCaseImplTest {
    @Test
    fun `Invoke calls repository`() {
        // Given
        val movieFlow = flowOf(sampleMovies(1, 2, 3, 4))

        // When
        val useCase =
            FetchMovieUseCaseImpl(
                mock {
                    on { movies } doReturn movieFlow
                },
            )

        val result = useCase.invoke()

        // Then
        assertEquals(movieFlow, result)
    }
}
