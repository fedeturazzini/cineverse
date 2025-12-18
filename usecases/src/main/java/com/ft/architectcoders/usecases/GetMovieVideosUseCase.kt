package com.ft.architectcoders.usecases

import com.ft.architectcoders.data.repository.movie.MovieRepository
import com.ft.architectcoders.domain.model.MovieVideo
import kotlinx.coroutines.flow.Flow

interface GetMovieVideosUseCase {
    operator fun invoke(movieId: Int): Flow<List<MovieVideo>>
}

class GetMovieVideosUseCaseImpl(private val movieRepository: MovieRepository) : GetMovieVideosUseCase {
    override fun invoke(movieId: Int): Flow<List<MovieVideo>> = movieRepository.getMovieVideos(movieId)
}
