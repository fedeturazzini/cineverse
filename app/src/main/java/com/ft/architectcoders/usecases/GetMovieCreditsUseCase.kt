package com.ft.architectcoders.usecases

import com.ft.architectcoders.data.repository.movie.MovieRepository
import com.ft.architectcoders.domain.model.Cast
import kotlinx.coroutines.flow.Flow

interface GetMovieCreditsUseCase {
    operator fun invoke(movieId: Int): Flow<List<Cast>>
}

class GetMovieCreditsUseCaseImpl(private val movieRepository: MovieRepository) : GetMovieCreditsUseCase {
    override fun invoke(movieId: Int): Flow<List<Cast>> = movieRepository.getMovieCredits(movieId)
}
