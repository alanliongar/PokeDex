package com.example.pokedex.battle.di

import com.example.pokedex.battle.data.remote.AIPokeBattleRemoteDataSource
import com.example.pokedex.battle.data.remote.RemoteDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
interface AIPokeBattleModuleBinding {

    @Binds
    fun bindsRemoteDataSource(impl: AIPokeBattleRemoteDataSource): RemoteDataSource
}