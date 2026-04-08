package com.example.pokedex.di

import com.example.pokedex.common.data.remote.model.CommonFunctions
import com.example.pokedex.common.data.remote.model.CommonFunctionsContract
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
abstract class CommonFunctionsModule {

    @Binds
    abstract fun bindCommonFunctions(impl: CommonFunctions): CommonFunctionsContract
}