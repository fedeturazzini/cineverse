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
import com.ft.architectcoders.usecases.marathon.GenerateMarathonPlanUseCase
import com.ft.architectcoders.usecases.marathon.GenerateMarathonPlanUseCaseImpl
import com.ft.architectcoders.usecases.marathon.GetMarathonByIdUseCase
import com.ft.architectcoders.usecases.marathon.GetMarathonByIdUseCaseImpl
import com.ft.architectcoders.usecases.marathon.GetMarathonHistoryUseCase
import com.ft.architectcoders.usecases.marathon.GetMarathonHistoryUseCaseImpl
import com.ft.architectcoders.usecases.marathon.GetMarathonThemesUseCase
import com.ft.architectcoders.usecases.marathon.GetMarathonThemesUseCaseImpl
import com.ft.architectcoders.usecases.marathon.SaveMarathonUseCase
import com.ft.architectcoders.usecases.marathon.SaveMarathonUseCaseImpl
import com.ft.architectcoders.usecases.mood.BuildMoodProfileUseCase
import com.ft.architectcoders.usecases.mood.BuildMoodProfileUseCaseImpl
import com.ft.architectcoders.usecases.mood.GetMoodRecommendationsUseCase
import com.ft.architectcoders.usecases.mood.GetMoodRecommendationsUseCaseImpl
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val useCasesModule =
    module {
        factoryOf(::FetchMovieUseCaseImpl) { bind<FetchMoviesUseCase>() }
        factoryOf(::FindMovieByIdUseCaseImpl) { bind<FindMovieByIdUseCase>() }
        factoryOf(::GetMovieCreditsUseCaseImpl) { bind<GetMovieCreditsUseCase>() }
        factoryOf(::GetMovieVideosUseCaseImpl) { bind<GetMovieVideosUseCase>() }
        factoryOf(::ToggleFavoriteMovieUseCaseImpl) { bind<ToggleFavoriteMovieUseCase>() }
        factoryOf(::GetDuelCandidatesUseCaseImpl) { bind<GetDuelCandidatesUseCase>() }
        factoryOf(::SubmitDuelChoiceUseCaseImpl) { bind<SubmitDuelChoiceUseCase>() }
        factoryOf(::CompleteDuelSessionUseCaseImpl) { bind<CompleteDuelSessionUseCase>() }
        factoryOf(::GetLastFingerprintUseCaseImpl) { bind<GetLastFingerprintUseCase>() }
        factoryOf(::BuildMoodProfileUseCaseImpl) { bind<BuildMoodProfileUseCase>() }
        factoryOf(::GetMoodRecommendationsUseCaseImpl) { bind<GetMoodRecommendationsUseCase>() }
        factoryOf(::GetMarathonThemesUseCaseImpl) { bind<GetMarathonThemesUseCase>() }
        factoryOf(::GenerateMarathonPlanUseCaseImpl) { bind<GenerateMarathonPlanUseCase>() }
        factoryOf(::SaveMarathonUseCaseImpl) { bind<SaveMarathonUseCase>() }
        factoryOf(::GetMarathonHistoryUseCaseImpl) { bind<GetMarathonHistoryUseCase>() }
        factoryOf(::GetMarathonByIdUseCaseImpl) { bind<GetMarathonByIdUseCase>() }
    }
