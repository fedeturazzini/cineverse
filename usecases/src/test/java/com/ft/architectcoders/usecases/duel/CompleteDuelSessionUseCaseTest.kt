package com.ft.architectcoders.usecases.duel

import com.ft.architectcoders.data.repository.duel.DuelRepository
import com.ft.architectcoders.data.repository.taste.TasteRepository
import com.ft.architectcoders.domain.Result
import com.ft.architectcoders.test.sampleDuelSession
import com.ft.architectcoders.test.sampleTasteFingerprint
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@RunWith(MockitoJUnitRunner::class)
class CompleteDuelSessionUseCaseTest {

    @Mock
    lateinit var duelRepository: DuelRepository

    @Mock
    lateinit var tasteRepository: TasteRepository

    private lateinit var useCase: CompleteDuelSessionUseCase

    @Before
    fun setUp() {
        useCase = CompleteDuelSessionUseCaseImpl(
            duelRepository = duelRepository,
            tasteRepository = tasteRepository,
        )
    }

    @Test
    fun `invoke saves session and generates fingerprint`() = runTest {
        // Given
        val session = sampleDuelSession(choicesCount = 10)
        val sessionId = 123L
        val fingerprint = sampleTasteFingerprint(sessionId = sessionId)
        
        whenever(duelRepository.saveDuelSession(any())).thenReturn(sessionId)
        whenever(tasteRepository.generateFingerprint(any())).thenReturn(Result.Success(fingerprint))

        // When
        val result = useCase(session)

        // Then
        assertTrue(result is Result.Success)
        assertEquals(fingerprint, (result as Result.Success).data)
        verify(duelRepository).saveDuelSession(any())
        verify(duelRepository).markSessionCompleted(sessionId)
        verify(tasteRepository).generateFingerprint(any())
    }

    @Test
    fun `invoke marks session as completed`() = runTest {
        // Given
        val session = sampleDuelSession(isCompleted = false)
        val sessionId = 456L
        val fingerprint = sampleTasteFingerprint()
        
        whenever(duelRepository.saveDuelSession(any())).thenReturn(sessionId)
        whenever(tasteRepository.generateFingerprint(any())).thenReturn(Result.Success(fingerprint))

        // When
        useCase(session)

        // Then
        verify(duelRepository).markSessionCompleted(sessionId)
    }

    @Test
    fun `invoke returns error when fingerprint generation fails`() = runTest {
        // Given
        val session = sampleDuelSession()
        val sessionId = 789L
        val error = com.ft.architectcoders.domain.error.AppError.UnknownError(message = "AI error")
        
        whenever(duelRepository.saveDuelSession(any())).thenReturn(sessionId)
        whenever(tasteRepository.generateFingerprint(any())).thenReturn(Result.Error(error))

        // When
        val result = useCase(session)

        // Then
        assertTrue(result is Result.Error)
        assertEquals(error, (result as Result.Error).error)
    }
}

