package com.ft.architectcoders.usecases.di

import com.ft.architectcoders.usecases.FetchMovieUseCaseImpl
import com.ft.architectcoders.usecases.FetchMoviesUseCase
import com.ft.architectcoders.usecases.FindMovieByIdUseCase
import com.ft.architectcoders.usecases.FindMovieByIdUseCaseImpl
import com.ft.architectcoders.usecases.GetMovieCreditsUseCase
import com.ft.architectcoders.usecases.GetMovieCreditsUseCaseImpl
import com.ft.architectcoders.usecases.GetMovieVideosUseCase
import com.ft.architectcoders.usecases.GetMovieVideosUseCaseImpl
import com.ft.architectcoders.usecases.ToggleFavoriteMovieUseCase
import com.ft.architectcoders.usecases.ToggleFavoriteMovieUseCaseImpl
import com.ft.architectcoders.usecases.duel.CompleteDuelSessionUseCase
import com.ft.architectcoders.usecases.duel.CompleteDuelSessionUseCaseImpl
import com.ft.architectcoders.usecases.duel.GetDuelCandidatesUseCase
import com.ft.architectcoders.usecases.duel.GetDuelCandidatesUseCaseImpl
import com.ft.architectcoders.usecases.duel.GetLastFingerprintUseCase
import com.ft.architectcoders.usecases.duel.GetLastFingerprintUseCaseImpl
import com.ft.architectcoders.usecases.duel.SubmitDuelChoiceUseCase
import com.ft.architectcoders.usecases.duel.SubmitDuelChoiceUseCaseImpl
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val useCasesModule = module {
    factoryOf(::FetchMovieUseCaseImpl) { bind<FetchMoviesUseCase>() }
    factoryOf(::FindMovieByIdUseCaseImpl) { bind<FindMovieByIdUseCase>() }
    factoryOf(::GetMovieCreditsUseCaseImpl) { bind<GetMovieCreditsUseCase>() }
    factoryOf(::GetMovieVideosUseCaseImpl) { bind<GetMovieVideosUseCase>() }
    factoryOf(::ToggleFavoriteMovieUseCaseImpl) { bind<ToggleFavoriteMovieUseCase>() }
    factoryOf(::GetDuelCandidatesUseCaseImpl) { bind<GetDuelCandidatesUseCase>() }
    factoryOf(::SubmitDuelChoiceUseCaseImpl) { bind<SubmitDuelChoiceUseCase>() }
    factoryOf(::CompleteDuelSessionUseCaseImpl) { bind<CompleteDuelSessionUseCase>() }
    factoryOf(::GetLastFingerprintUseCaseImpl) { bind<GetLastFingerprintUseCase>() }
}
