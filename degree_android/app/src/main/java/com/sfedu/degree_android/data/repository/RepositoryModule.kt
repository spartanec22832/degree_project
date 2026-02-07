package com.sfedu.degree_android.data.repository

import com.sfedu.degree_android.domain.repository.AuthRepository
import com.sfedu.degree_android.domain.repository.FavoritesRepository
import com.sfedu.degree_android.domain.repository.InteractionRepository
import com.sfedu.degree_android.domain.repository.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import com.sfedu.degree_android.domain.repository.PlacesRepository

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Binds @Singleton
    abstract fun bindUserRepository(impl: UserRepositoryImpl): UserRepository

    @Binds @Singleton
    abstract fun bindPlacesRepository(impl: PlacesRepositoryImpl): PlacesRepository

    @Binds @Singleton
    abstract fun bindFavoritesRepository(
        impl: FavoritesRepositoryImpl
    ): FavoritesRepository

    @Binds @Singleton
    abstract fun bindInteractionRepository(
        impl: InteractionRepositoryImpl
    ): InteractionRepository


}
