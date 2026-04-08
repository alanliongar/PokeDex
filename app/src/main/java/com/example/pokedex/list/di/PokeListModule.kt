package com.example.pokedex.list.di

import com.example.pokedex.common.data.local.PokedexDataBase
import com.example.pokedex.common.data.local.PokemonDao
import com.example.pokedex.di.PokeApiRetrofit
import com.example.pokedex.list.data.local.LocalDataSource
import com.example.pokedex.list.data.local.PokeListLocalDataSource
import com.example.pokedex.list.data.remote.PokeListRemoteDataSource
import com.example.pokedex.list.data.remote.PokeListService
import com.example.pokedex.list.data.remote.RemoteDataSource
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit


@Module
@InstallIn(SingletonComponent::class)
class PokeDetailModule{

    @Provides
    fun providesPokemonDao(room: PokedexDataBase): PokemonDao {
        return room.getPokemonDao()
    }

    @Provides
    fun providePokeApiService(
        @PokeApiRetrofit retrofit: Retrofit,
    ): PokeListService{
        return retrofit.create(PokeListService::class.java)
    }


}

@Module
@InstallIn(ViewModelComponent::class)
interface PokedexModuleBinding {

    @Binds
    fun bindLocalDataSource(impl: PokeListLocalDataSource): LocalDataSource

    @Binds
    fun bindRemoteDataSource(impl: PokeListRemoteDataSource): RemoteDataSource
}