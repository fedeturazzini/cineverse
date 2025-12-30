package com.ft.architectcoders.usecases

import com.ft.architectcoders.domain.Result
import com.ft.architectcoders.test.sampleMovie
import com.ft.architectcoders.test.successResult
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock

class FindMovieByIdUseCaseImplTest {

    @Test
    fun `Invoke should return movie from repository`() = runTest {
        // Given
        val movie = sampleMovie(1)

        // When
        val useCase = FindMovieByIdUseCaseImpl(mock {
            on { findMovieById(1) } doReturn flowOf(successResult(movie))
        })

        // Then
        val result = useCase.invoke(1).first()

        assertEquals(successResult(movie), result)
    }

}