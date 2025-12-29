package com.ft.architectcoders.usecases

import com.ft.architectcoders.data.repository.movie.MovieRepository
import com.ft.architectcoders.domain.Result
import com.ft.architectcoders.domain.model.Cast
import kotlinx.coroutines.flow.Flow

interface GetMovieCreditsUseCase {
    operator fun invoke(movieId: Int): Flow<Result<List<Cast>>>
}

class GetMovieCreditsUseCaseImpl(private val movieRepository: MovieRepository) : GetMovieCreditsUseCase {
    override fun invoke(movieId: Int): Flow<Result<List<Cast>>> = movieRepository.getMovieCredits(movieId)
}
