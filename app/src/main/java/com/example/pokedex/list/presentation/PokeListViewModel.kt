package com.example.pokedex.list.presentation

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.pokedex.PokedexApplication
import com.example.pokedex.common.data.remote.PokeRetrofitClient
import com.example.pokedex.common.data.remote.model.CommonFunctions
import com.example.pokedex.list.data.PokeListRepository
import com.example.pokedex.list.presentation.ui.PokemonUiData
import com.example.pokedex.list.data.remote.PokeListService
import com.example.pokedex.list.presentation.ui.PokeListUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.net.UnknownHostException

class PokeListViewModel(
    private val repository: PokeListRepository,
    private val context: Context
) : ViewModel() {
    private var currentPage: Int = 1
    private val _uiPokemonsList = MutableStateFlow<PokeListUiState>(PokeListUiState())
    val uiPokemonsList: StateFlow<PokeListUiState> = _uiPokemonsList


    init {
        fetchPokemonList()
    }

    fun loadMorePokemons() {
        fetchPokemonList()
    }

    private fun fetchPokemonList() {
        _uiPokemonsList.value = PokeListUiState(isLoading = true)
        viewModelScope.launch(Dispatchers.IO) {
            val response = repository.getPokeList(context = context, page = currentPage)
            if (response.isSuccess) {
                currentPage++
                //_uiPokemonsList.value = emptyList() //Não precisa do equivalente nesse caso, pq a lista default ja é vazia
                val pokemonResponse = response.getOrNull()
                if (pokemonResponse != null) {
                    val pokeUiDataList = pokemonResponse.map { PokeListDto ->
                        val pokeImgRand = PokeListDto.image.random()
                        PokemonUiData(
                            name = PokeListDto.name,
                            id = PokeListDto.id,
                            imageUrl = pokeImgRand,
                            color = CommonFunctions().getDominantColorFromImage(
                                context,
                                pokeImgRand
                            ).first
                        )
                    }
                    _uiPokemonsList.value =
                        PokeListUiState(pokemonUiDataList = pokeUiDataList)
                } else {
                    _uiPokemonsList.value =
                        PokeListUiState(
                            isError = true,
                            errorMessage = "Empty internet request"
                        )
                }
            } else {
                val ex = response.exceptionOrNull()
                if (ex is UnknownHostException) {
                    _uiPokemonsList.value =
                        PokeListUiState(
                            isError = true,
                            errorMessage = "Internet request not successful"
                        )
                } else {
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
                val pokeListService =
                    PokeRetrofitClient.retrofitInstance.create(PokeListService::class.java)
                val application = checkNotNull(extras[APPLICATION_KEY])
                return PokeListViewModel(
                    repository = (application as PokedexApplication).repository,
                    context = context
                ) as T
            }
        }
    }
}