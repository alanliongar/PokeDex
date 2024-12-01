package com.example.pokedex.list

import com.example.pokedex.list.data.PokeListRepository
import com.example.pokedex.list.presentation.PokeListViewModel
import android.content.Context
import com.example.pokedex.common.data.model.Pokemon
import com.example.pokedex.list.presentation.ui.PokeListUiState
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class PokeListViewModelTestMockWithoutTurbine {
    private val repository: PokeListRepository = mock()
    private val context: Context = mock()

    @OptIn(ExperimentalCoroutinesApi::class)
    private val testDispatcher = UnconfinedTestDispatcher(TestCoroutineScheduler())

    private val underTest by lazy {
        PokeListViewModel(
            repository = repository,
            context = context,
            coroutineDispatcher = testDispatcher
        )
    }

    @Test
    fun `Given fresh viewmodel When collecting pokemon list Then asset isloading state`() {
        runTest {
            val pokemon = emptyList<Pokemon>()
            whenever(
                repository.getPokeList(
                    context = context,
                    page = 1
                )
            ).thenReturn(Result.success(pokemon))
            whenever(repository.getPokeCount()).thenReturn(1)


            var result: PokeListUiState? = null
            backgroundScope.launch(testDispatcher) {
                result = underTest.uiPokemonsList.first()
            }
            val expected = PokeListUiState(isLoading = true)
            assertEquals(expected, result)
        }
    }


}