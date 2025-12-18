package com.ft.architectcoders.usecases

import com.ft.architectcoders.data.repository.movie.MovieRepository
import com.ft.architectcoders.domain.model.Movie
import kotlinx.coroutines.flow.Flow

interface FetchMoviesUseCase {
    operator fun invoke(): Flow<List<Movie>>
}

class FetchMovieUseCaseImpl(private val movieRepository: MovieRepository) : FetchMoviesUseCase {
    override fun invoke() = movieRepository.movies
}
