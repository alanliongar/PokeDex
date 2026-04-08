package com.example.pokedex.di

import android.app.Application
import androidx.room.Room
import com.example.pokedex.common.data.local.PokedexDataBase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

@Module
@InstallIn(SingletonComponent::class)
class PokedexModule {

    @Provides
    fun providesPokedexDatabase(application: Application): PokedexDataBase {
        return Room.databaseBuilder(
            application.applicationContext,
            PokedexDataBase::class.java,
            "pokedex-localDatabasee"
        ).build()
    }

    @Provides
    @DispatchersIO
    fun providesCoroutineDispatchersIO(): CoroutineDispatcher {
        return Dispatchers.IO
    }

    @Provides
    @DispatchersMain
    fun providesCoroutineDispatchersMain(): CoroutineDispatcher {
        return Dispatchers.Main
    }

}

