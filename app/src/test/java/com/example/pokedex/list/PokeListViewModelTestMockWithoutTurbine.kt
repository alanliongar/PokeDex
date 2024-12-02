package com.example.pokedex.list

import com.example.pokedex.list.data.PokeListRepository
import com.example.pokedex.list.presentation.PokeListViewModel
import android.content.Context
import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.example.pokedex.common.data.model.Pokemon
import com.example.pokedex.common.data.remote.model.CommonFunctions
import com.example.pokedex.list.presentation.ui.PokeListUiState
import com.example.pokedex.list.presentation.ui.PokemonUiData
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.MockedStatic
import org.mockito.Mockito
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import java.util.concurrent.CountDownLatch

class PokeListViewModelTestMockWithoutTurbine {
    private val repository: PokeListRepository = mock()
    private val context: Context = mock()
    private val commonFunctions = mock<CommonFunctions>()

    private lateinit var mockedLog: MockedStatic<Log>

    @Before
    fun setupMockLog() {
        mockedLog = Mockito.mockStatic(Log::class.java)
        mockedLog.`when`<Any> { Log.d(Mockito.anyString(), Mockito.anyString()) }.thenReturn(0)
        mockedLog.`when`<Any> { Log.e(Mockito.anyString(), Mockito.anyString()) }.thenReturn(0)
    }

    @After
    fun tearDownMockLog() {
        mockedLog.close() // Libera o mock após cada teste
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private val testDispatcher = UnconfinedTestDispatcher(TestCoroutineScheduler())

    private val underTest by lazy {
        PokeListViewModel(
            repository = repository,
            context = context,
            coroutineDispatcher = testDispatcher,
            commonFunctions = commonFunctions
        )
    }

    @Test
    fun `Given fresh viewmodel When collecting pokemon list Then asset isloading state`() {
        runTest {
            //Given
            val pokemon = emptyList<Pokemon>()
            whenever(
                repository.getPokeList(
                    context = context,
                    page = 1
                )
            ).thenReturn(Result.success(pokemon))

            //When
            var result: PokeListUiState? = null
            backgroundScope.launch(testDispatcher) {
                result = underTest.uiPokemonsList.drop(0).first()
            }

            //Then
            val expected = PokeListUiState(isLoading = true)
            assertEquals(expected, result)
        }
    }

    @Test
    fun `Given fresh viewmodel When collecting API result with data Then assert result data`() {
        runTest {
            // Given
            val pokemon = listOf(
                Pokemon(
                    id = 1,
                    name = "name",
                    image = listOf("image", "image", "image"),
                    color = 1,
                    page = 1
                )
            )
            whenever(repository.getPokeList(context = context, page = 1)).thenReturn(
                Result.success(pokemon)
            )
            whenever(
                commonFunctions.getDominantColorFromImage(context,"image")
            ).thenReturn(Pair(Color(1), Color(1)))

            val expected = PokeListUiState(
                pokemonUiDataList = listOf(
                    PokemonUiData(
                        id = pokemon[0].id,
                        name = pokemon[0].name,
                        imageUrl = pokemon[0].image[0],
                        color = Color(1)
                    )
                )
            )
            // When
            var result: PokeListUiState? = null
            backgroundScope.launch(testDispatcher) {
                result = underTest.uiPokemonsList.drop(1).first()
            }
            assertEquals(expected, result)
        }
    }
}