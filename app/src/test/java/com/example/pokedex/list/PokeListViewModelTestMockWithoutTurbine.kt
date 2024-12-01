package com.example.pokedex.list

import com.example.pokedex.list.data.PokeListRepository
import com.example.pokedex.list.presentation.PokeListViewModel
import android.content.Context
import org.mockito.kotlin.mock

class PokeListViewModelTestMockWithoutTurbine {
    private val repository: PokeListRepository = mock()
    private val context: Context = mock()

    private val underTest by lazy{
        PokeListViewModel(
            repository = repository,
            context = context
        )
    }





}