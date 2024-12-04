package com.example.pokedex.detail
//Migrar essa bussanha pra FAKES
import android.content.Context
import android.util.Log
import com.example.pokedex.common.data.remote.model.CommonFunctions
import com.example.pokedex.common.data.remote.model.PokeDto
import com.example.pokedex.detail.data.PokeDetailService
import com.example.pokedex.detail.presentation.PokeDetailViewModel
import com.example.pokedex.detail.presentation.ui.PokemonDetailUiState
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.MockedStatic
import org.mockito.Mockito
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import retrofit2.Response

class PokeDetailViewModelTestWithFakes {
    private val context: Context = mock()
    private val commonFunctions = mock<CommonFunctions>()
    private val pokeDetailService = mock<PokeDetailService>()

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
        PokeDetailViewModel(
            context = context,
            viewModelDetailDispatcher = testDispatcher,
            commonFunctions = commonFunctions,
            pokeDetailService = pokeDetailService
        )
    }

    @Test
    fun `Given fresh viewmodel When collecting pokemon detail Then assert isloading state`() =
        runTest {
            // Given: Criação do objeto fake do PokeDto
            val fakePokeDto = PokeDto(
                name = "Bulbasaur",
                weight = 69,
                height = 7,
                pokeId = 1,
                baseExperience = 64,
                baseStats = listOf(
                    PokeDto.Stat(baseStat = 45),
                    PokeDto.Stat(baseStat = 49),
                    PokeDto.Stat(baseStat = 49)
                ),
                typesPok = listOf(
                    PokeDto.TypeSlot(
                        slot = 1,
                        type = PokeDto.Type(name = "Grass")
                    ),
                    PokeDto.TypeSlot(
                        slot = 2,
                        type = PokeDto.Type(name = "Poison")
                    )
                )
            )

            // Mockando a resposta da API
            whenever(pokeDetailService.getPokemonDetail(1)).thenReturn(Response.success(fakePokeDto))
            whenever(commonFunctions.getRandomPokeImg(1)).thenReturn("image1")
            whenever(
                commonFunctions.getDominantColorFromImage(
                    context = eq(context), // Usamos `eq` para context porque ele é específico
                    imageUrl = eq("image"), // Para imageUrl também usamos `eq` já que o valor é específico
                    index = any(), // Para index, usamos any() já que ele não é específico
                    target = any() // Matcher personalizado para validar o range
                )
            ).thenReturn(Pair(null, null))
            // When: Chamada para disparar a coleta do detalhe do Pokémon

            // Captura o estado do StateFlow
            var result: PokemonDetailUiState? = null

            backgroundScope.launch(testDispatcher) {
                underTest.fetchPokemonData(1)
                result = underTest.uiPokeDto.drop(0).first() // Captura o estado inicial emitido
            }

            // Then: Verifica se o estado inicial é `isLoading = true`
            val expected = PokemonDetailUiState(
                isLoading = true,
            )
            assertEquals(expected, result)
        }
}