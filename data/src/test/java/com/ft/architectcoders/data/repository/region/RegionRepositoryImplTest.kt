package com.ft.architectcoders.data.repository.region

import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock

class RegionRepositoryImplTest {
    @Test
    fun `findLastRegion calls RegionDataSource`() =
        runTest {
            // Given
            val expectedRegion = "US"
            val repository =
                RegionRepositoryImpl(
                    mock { onBlocking { findLastRegion() } doReturn expectedRegion },
                )

            // When
            val result = repository.findLastRegion()

            // Then
            assertEquals(expectedRegion, result)
        }
}
