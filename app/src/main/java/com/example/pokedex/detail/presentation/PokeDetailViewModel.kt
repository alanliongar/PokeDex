package com.example.pokedex.detail.presentation

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.pokedex.common.data.remote.PokeRetrofitClient
import com.example.pokedex.common.data.remote.model.CommonFunctions
import com.example.pokedex.detail.data.PokeDetailService
import com.example.pokedex.detail.presentation.ui.PokemonDetailUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class PokeDetailViewModel(
    private val pokeDetailService: PokeDetailService, private val context: Context
) : ViewModel() {
    private val _uiPokeDto = MutableStateFlow<PokemonDetailUiState>(PokemonDetailUiState())
    val uiPokeDto: StateFlow<PokemonDetailUiState> = _uiPokeDto

    fun setImageAndColor(image: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _uiPokeDto.value = _uiPokeDto.value.copy(
                image = image, color = CommonFunctions().getDominantColorFromImage(
                    context, image
                )
            )
        }
    }

    fun fetchPokemonData(pokeId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                if (_uiPokeDto.value.PokeDetail == null) {
                    _uiPokeDto.value = PokemonDetailUiState(isLoading = true, isError = false)
                    val pokeDetail = pokeDetailService.getPokemonDetail(pokeId)
                    if (pokeDetail.isSuccessful) {
                        val body = pokeDetail.body()
                        if (body != null) {
                            val img = CommonFunctions().getRandomPokeImg(pokeId)
                            val color = CommonFunctions().getDominantColorFromImage(context, img)
                            _uiPokeDto.value = PokemonDetailUiState(
                                PokeDetail = body,
                                color = color,
                                image = img
                            )
                        } else {
                            _uiPokeDto.value = PokemonDetailUiState(isError = true)
                        }
                    } else {
                        _uiPokeDto.value = PokemonDetailUiState(isError = true)
                        Log.e(
                            "PokeDetailViewModel",
                            "Erro ao buscar Detalhe do pokemon id ${pokeId}: ${
                                pokeDetail.errorBody()?.string()
                            }"
                        )
                    }
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
                _uiPokeDto.value = PokemonDetailUiState(isError = true, isLoading = false)
            } finally {
                _uiPokeDto.value = _uiPokeDto.value.copy(isLoading = false)
            }
        }

        // Código antigo
        /*if (pokeId != null) {
            viewModelScope.launch(Dispatchers.IO) {
                _uiPokeDto.value = PokemonDetailUiState(isLoading = true)
                try {
                    val pokeDetail = pokeDetailService.getPokemonDetail(pokeId)
                    if (pokeDetail.isSuccessful) {
                        val body = pokeDetail.body()
                        if (body != null) {
                            _uiPokeDto.value =
                                _uiPokeDto.value.copy(
                                    PokeDetail = body,
                                    isLoading = false,
                                    isError = false
                                )
                            val img = _uiPokeDto.value.image
                            setImageAndColor(img)
                        } else {
                            _uiPokeDto.value = PokemonDetailUiState(isError = true)
                        }
                    } else {
                        _uiPokeDto.value = PokemonDetailUiState(isError = true)
                        Log.e(
                            "PokeDetailViewModel",
                            "Erro ao buscar Detalhe do pokemon id ${pokeId}: ${
                                pokeDetail.errorBody()?.string()
                            }"
                        )
                    }
                } catch (ex: Exception) {
                    Log.e("PokeDetailViewModel", "Exception: ${ex.message}")
                    _uiPokeDto.value = PokemonDetailUiState(isError = true)
                } finally {
                    _uiPokeDto.value = _uiPokeDto.value.copy(isLoading = false)
                }
            }
        } else {
            //Erro quando nao consigo um ID válido
            _uiPokeDto.value = _uiPokeDto.value.copy(isLoading = false, isError = true)
        }*/
    }

    fun clearState() {
        viewModelScope.launch(Dispatchers.IO) {
            delay(15)/*
            * tempinho maroto - mas por algum motivo ainda tem erro com isso aqui,
            * tela fazendo loading infinitamente em alguns casos, não encontrei um
            * padrão do problema, então nao sei o motivo.
            *
            * Além disso, tentei colocar (com ajuda das IA da vida) o mutex e o fetchJob
            * pra tentar lidar com esse problema, não resolveu e realmente não sei o que está havendo
            * preciso estudar funções assincronas.
            * */
            _uiPokeDto.value = PokemonDetailUiState()
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>, extras: CreationExtras
            ): T {
                val context =
                    extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as Context
                val pokeDetailService =
                    PokeRetrofitClient.retrofitInstance.create(PokeDetailService::class.java)
                return PokeDetailViewModel(pokeDetailService, context) as T
            }
        }
    }
}