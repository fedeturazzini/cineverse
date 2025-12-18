package com.ft.architectcoders.usecases

import com.ft.architectcoders.data.repository.movie.MovieRepository
import com.ft.architectcoders.domain.model.Movie

interface ToggleFavoriteMovieUseCase {
    suspend operator fun invoke(movie: Movie)
}

class ToggleFavoriteMovieUseCaseImpl(private val movieRepository: MovieRepository) : ToggleFavoriteMovieUseCase {
    override suspend fun invoke(movie: Movie) = movieRepository.toggleFavorite(movie)
}
