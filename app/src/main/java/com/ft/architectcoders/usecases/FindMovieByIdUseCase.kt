package com.ft.architectcoders.usecases

import com.ft.architectcoders.Result
import com.ft.architectcoders.data.repository.movie.MovieRepository
import com.ft.architectcoders.domain.model.Movie
import kotlinx.coroutines.flow.Flow

interface FindMovieByIdUseCase {
    operator fun invoke(id: Int): Flow<Result<Movie>>
}

class FindMovieByIdUseCaseImpl(private val movieRepository: MovieRepository) : FindMovieByIdUseCase {
    override fun invoke(id: Int): Flow<Result<Movie>> = movieRepository.findMovieById(id)
}
