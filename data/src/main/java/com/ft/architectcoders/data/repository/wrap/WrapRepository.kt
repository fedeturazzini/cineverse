package com.ft.architectcoders.data.repository.wrap

import com.ft.architectcoders.domain.Result
import com.ft.architectcoders.domain.model.CineverseWrap
import com.ft.architectcoders.domain.model.WrapGeminiInput

interface WrapRepository {
    suspend fun generateWrap(input: WrapGeminiInput): Result<CineverseWrap>
}

