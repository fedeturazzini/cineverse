package com.ft.architectcoders.usecases.duel

import com.ft.architectcoders.data.repository.taste.TasteRepository
import com.ft.architectcoders.domain.model.TasteFingerprint
import kotlinx.coroutines.flow.Flow

interface GetLastFingerprintUseCase {
    operator fun invoke(): Flow<TasteFingerprint?>
}

class GetLastFingerprintUseCaseImpl(
    private val tasteRepository: TasteRepository,
) : GetLastFingerprintUseCase {
    override fun invoke(): Flow<TasteFingerprint?> {
        return tasteRepository.getLastFingerprint()
    }
}
