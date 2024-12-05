package com.example.pokedex.list.presentation

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.pokedex.PokedexApplication
import com.example.pokedex.common.data.remote.model.CommonFunctions
import com.example.pokedex.list.data.PokeListRepository
import com.example.pokedex.list.presentation.ui.PokemonUiData
import com.example.pokedex.list.presentation.ui.PokeListUiState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.net.UnknownHostException

class PokeListViewModel(
    private val repository: PokeListRepository,
    private val context: Context,
    private val coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val commonFunctions: CommonFunctions = CommonFunctions()
) : ViewModel() {
    private var currentPage: Int = 1
    private val _uiPokemonsList = MutableStateFlow(PokeListUiState())
    val uiPokemonsList: StateFlow<PokeListUiState> = _uiPokemonsList

    init {
        fetchPokemonList()
    }

    fun loadMorePokemons() {
        currentPage++
        fetchPokemonList()
    }

    private fun fetchPokemonList() {
        _uiPokemonsList.value = _uiPokemonsList.value.copy(isLoading = true)
        viewModelScope.launch(coroutineDispatcher) {
            val result = repository.getPokeList(context = context, page = currentPage)
            if (result.isSuccess) {
                val pokemonResponse = result.getOrNull()
                if (pokemonResponse != null) {
                    val pokeUiDataList = pokemonResponse.map { PokeListDto ->
                        val pokeImgRand = PokeListDto.image.random()
                        PokemonUiData(
                            name = PokeListDto.name,
                            id = PokeListDto.id,
                            imageUrl = pokeImgRand,
                            color = commonFunctions.getDominantColorFromImage(
                                context,
                                pokeImgRand,index=1,target=1
                            ).first
                        )
                    }
                    _uiPokemonsList.value = _uiPokemonsList.value.copy(
                        pokemonUiDataList = _uiPokemonsList.value.pokemonUiDataList + pokeUiDataList,
                        isLoading = false,
                        isError = false
                    )
                } else {
                    _uiPokemonsList.value = _uiPokemonsList.value.copy(
                        pokemonUiDataList = _uiPokemonsList.value.pokemonUiDataList + emptyList(),
                        isLoading = false,
                        isError = true,
                        errorMessage = "Empty internet request"
                    )
                }
            } else {
                val ex = result.exceptionOrNull()
                if (ex is UnknownHostException) {
                    _uiPokemonsList.value = _uiPokemonsList.value.copy(
                        isLoading = false,
                        isError = true,
                        errorMessage = "Internet request not successful",
                        pokemonUiDataList = _uiPokemonsList.value.pokemonUiDataList + emptyList()
                    )
                } else {
                    if (ex != null) {
                        _uiPokemonsList.value = _uiPokemonsList.value.copy(
                            isLoading = false,
                            isError = true,
                            errorMessage = ex.message ?: "Something went wrong",
                            pokemonUiDataList = _uiPokemonsList.value.pokemonUiDataList + emptyList()
                        )
                    } else {
                        _uiPokemonsList.value = _uiPokemonsList.value.copy(
                            isLoading = false,
                            isError = true,
                            errorMessage = "Something went wrong",
                            pokemonUiDataList = _uiPokemonsList.value.pokemonUiDataList + emptyList()
                        )
                    }
                }
            }
        }
    }


    companion object {
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
    }
}