package com.example.pokedex.battle.di

import com.example.pokedex.battle.data.model.AndroidBattlePromptProvider
import com.example.pokedex.battle.data.model.BattlePromptProvider
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class PromptModule {


    @Binds
    @Singleton
    abstract fun bindBattlePromptProvider(impl: AndroidBattlePromptProvider): BattlePromptProvider
}