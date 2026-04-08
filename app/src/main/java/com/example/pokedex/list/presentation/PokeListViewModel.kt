package com.example.pokedex.list.presentation

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokedex.common.data.remote.model.CommonFunctions
import com.example.pokedex.common.data.remote.model.CommonFunctionsContract
import com.example.pokedex.di.DispatchersIO
import com.example.pokedex.list.data.PokeListRepository
import com.example.pokedex.list.presentation.ui.PokeListUiState
import com.example.pokedex.list.presentation.ui.PokemonUiData
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.net.UnknownHostException
import javax.inject.Inject

@HiltViewModel
class PokeListViewModel @Inject constructor(
    private val repository: PokeListRepository,
    @ApplicationContext private val context: Context,
    @DispatchersIO val coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val commonFunctions: CommonFunctionsContract = CommonFunctions()
) : ViewModel() {
    private var currentPage: Int = 1
    private var isRequestInFlight = false
    private var endReached = false
    private val _uiPokemonsList = MutableStateFlow(PokeListUiState())
    val uiPokemonsList: StateFlow<PokeListUiState> = _uiPokemonsList
    private val _selectedPokemons = MutableStateFlow<List<PokemonUiData>>(emptyList())

    val selectedPokemons: StateFlow<List<PokemonUiData>> = _selectedPokemons

    fun loadMorePokemons() {
        Log.d(
            "PokeDexDoAlan",
            "ANTES do if | currentPage=$currentPage | inFlight=$isRequestInFlight | endReached=$endReached"
        )

        if (isRequestInFlight || endReached) return

        currentPage++

        Log.d(
            "PokeDexDoAlan",
            "DEPOIS do if | vou buscar página $currentPage"
        )

        fetchPokemonList(isAppend = true)
    }

    init {
        fetchPokemonList(isAppend = false)
    }

    private fun fetchPokemonList(isAppend: Boolean = false) {
        if (isRequestInFlight) return
        isRequestInFlight = true

        _uiPokemonsList.value = _uiPokemonsList.value.copy(
            isInitialLoading = !isAppend,
            isAppending = isAppend,
            isError = false
        )

        viewModelScope.launch(coroutineDispatcher) {
            try {
                val result = repository.getPokeList(context = context, page = currentPage)
                if (result.isSuccess) {
                    val pokemonResponse = result.getOrNull()

                    if (pokemonResponse != null) {
                        val pokeUiDataList = pokemonResponse.map { pokeListDto ->
                            val pokeImgRand = pokeListDto.image.random()
                            PokemonUiData(
                                name = pokeListDto.name,
                                id = pokeListDto.id,
                                imageUrl = pokeImgRand,
                                color = commonFunctions.getDominantColorFromImage(
                                    context,
                                    pokeImgRand,
                                    index = 1,
                                    target = 1
                                ).first
                            )
                        }

                        if (pokeUiDataList.size < 12) {
                            endReached = true
                        }

                        _uiPokemonsList.value = _uiPokemonsList.value.copy(
                            pokemonUiDataList = if (isAppend) {
                                _uiPokemonsList.value.pokemonUiDataList + pokeUiDataList
                            } else {
                                pokeUiDataList
                            },
                            isInitialLoading = false,
                            isAppending = false,
                            isError = false
                        )
                    } else {
                        _uiPokemonsList.value = _uiPokemonsList.value.copy(
                            isInitialLoading = false,
                            isAppending = false,
                            isError = true,
                            errorMessage = "Empty internet request"
                        )
                    }
                } else {
                    val ex = result.exceptionOrNull()

                    _uiPokemonsList.value = _uiPokemonsList.value.copy(
                        isInitialLoading = false,
                        isAppending = false,
                        isError = true,
                        errorMessage = when (ex) {
                            is UnknownHostException -> "Internet request not successful"
                            null -> "Something went wrong"
                            else -> ex.message ?: "Something went wrong"
                        }
                    )
                }
            } finally {
                isRequestInFlight = false
            }
        }
    }

    fun toggleSelection(pokemon: PokemonUiData, isSelected: Boolean) {
        _selectedPokemons.value = if (isSelected) {
            if (_selectedPokemons.value.size < 2) {
                _selectedPokemons.value + pokemon
            } else {
                _selectedPokemons.value
            }
        } else {
            _selectedPokemons.value - pokemon
        }
    }

    /*companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>,
                extras: CreationExtras
            ): T {
                val context =
                    extras[APPLICATION_KEY] as Context
                val application = checkNotNull(extras[APPLICATION_KEY])
                return PokeListViewModel(
                    repository = (application as PokedexApplication).repository,
                    context = context
                ) as T
            }
        }
    }*/
}