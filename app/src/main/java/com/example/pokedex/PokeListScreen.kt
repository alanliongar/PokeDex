package com.example.pokedex

import android.content.Context
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import coil.compose.AsyncImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun PokeListScreen(context: Context, navController: NavHostController) {
    val pokemonList = remember { mutableStateListOf<PokemonListDto>() }
    val pokemons = remember { mutableStateListOf<Pokemon>() }
    val isLoading = remember { mutableStateOf(true) }

    val pokeApiService =
        PokeRetrofitClient.retrofitInstance.create(PokeApiService::class.java)

    //Obter a lista dos primeiros 20 pokemons
    LaunchedEffect(Unit) {
        pokeApiService.getPokemonList().enqueue(object : Callback<PokeResponse> {
            override fun onResponse(
                call: Call<PokeResponse>,
                response: Response<PokeResponse>
            ) {
                if (response.isSuccessful) {
                    val pokemonResponse = response.body()
                    pokemonResponse?.results?.let {
                        pokemonList.addAll(it)
                        Log.d(
                            "Pokedexxx",
                            "Lista de Pokémons recebida: ${pokemonList.joinToString { it.name }}"
                        )

                        // Preenche a lista de objetos Pokémon
                        it.forEach { pokemonNamesDto ->
                            pokemonNamesDto.name?.let { name ->
                                pokemons.add(Pokemon(name = name))
                            }
                        }
                    }
                } else {
                    Log.e(
                        "Pokedexxx",
                        "Request error::${response.errorBody()?.string()}"
                    )
                }
            }

            override fun onFailure(call: Call<PokeResponse>, t: Throwable) {
                Log.e("Pokedexxx", "Network error::${t.message}")
            }
        })
    }

    // Monitora o estado de preenchimento da lista
    val isPokemonListReady by remember {
        derivedStateOf { pokemons.isNotEmpty() }
    }

    // Executa o LaunchedEffect somente quando a lista estiver preenchida
    LaunchedEffect(isPokemonListReady) {
        if (isPokemonListReady) {
            Log.d("Pokedexxx", "LaunchedEffect iniciado")
            withContext(Dispatchers.IO) {
                pokemons.forEach { pokemon ->
                    try {
                        Log.d("Pokedexxx", "Buscando dados para: ${pokemon.name}")

                        val idResponse = pokeApiService.getPokemonId(pokemon.name)
                        val speciesAndColorResponse =
                            pokeApiService.getPokemonColorAndSpecie(pokemon.name)
                        if (idResponse.isSuccessful) {
                            val idBody = idResponse.body()
                            pokemon.id = idBody?.id
                            pokemon.imageUrl =
                                "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/home/${pokemon.id}" + ".png"

                            Log.d("Pokedexxx", "ID de ${pokemon.name}: ${pokemon.id}")
                        } else {
                            Log.e(
                                "Pokedexxx",
                                "Erro ao buscar ID para ${pokemon.name}: ${
                                    idResponse.errorBody()?.string()
                                }"
                            )
                        }

                        if (speciesAndColorResponse.isSuccessful) {
                            val body = speciesAndColorResponse.body()
                            body?.let {
                                Log.d("Pokedexxx", "Resposta recebida: ${it}")

                                pokemon.color =
                                    getColorValue(context, it.color.name)
                                pokemon.specie = it.specie

                                Log.d(
                                    "Pokedexxx",
                                    "Espécie e cor de ${pokemon.name}: ${pokemon.specie}, ${pokemon.color}"
                                )
                            }
                        } else {
                            Log.e(
                                "Pokedexxx",
                                "Erro ao buscar espécie e cor para ${pokemon.name}: ${
                                    speciesAndColorResponse.errorBody()?.string()
                                }"
                            )
                        }
                    } catch (e: Exception) {
                        Log.e(
                            "Pokedexxx",
                            "Erro na chamada para ${pokemon.name}: ${e.message}",
                            e
                        )
                    }
                }
            }
            isLoading.value = false
        } else {
            Log.e("Pokedexxx", "A lista de pokémons está vazia!")
        }
    }

    PokeListContent(
        context = context,
        isLoading = isLoading,
        pokemonList = pokemonList,
        pokemons = pokemons
    ) { pokeItemClicked ->
        navController.navigate("pokemonDetail/${pokeItemClicked.id}")
    }
}

@Composable
private fun PokeListContent(
    isLoading: MutableState<Boolean>,
    pokemonList: MutableList<PokemonListDto>,
    pokemons: MutableList<Pokemon>,
    context: Context, onClick: (Pokemon) -> Unit
) {
    if (isLoading.value) {
        CircularProgressIndicator()
    } else {
        Column {
            PokeTitleImage()
            PokeGrid(pokemonList = pokemons, onClick = onClick)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PokeListScreenPreview(modifier: Modifier = Modifier) {
//só mantendo o padrão
}

@Composable
private fun PokeCard(
    pokemon: Pokemon, onClick: (Pokemon) -> Unit
) {//definir a ação de clique dentro dessa função
    var isLoading by remember { mutableStateOf(true) }
    var color by remember { mutableStateOf(Color.Transparent) }
    var imageUrl by remember { mutableStateOf("") }


    // Carrega os dados do Pokémon ao inicializar
    LaunchedEffect(pokemon) {
        isLoading = true
        pokemon.color?.let { color = Color(it).copy(alpha = 0.45f) }
        pokemon.imageUrl?.let { imageUrl = it }
        isLoading = false
    }

    // Estrutura do Card
    Column(
        modifier = Modifier
            .padding(16.dp)// Espaçamento interno
            .clip(RoundedCornerShape(16.dp))
            .background(color = color)
            .border(2.dp, color, RoundedCornerShape(16.dp))
            .height(200.dp) // Define a altura do Card
            .clickable { onClick.invoke(pokemon) },
        // Cor de fundo baseada no Pokémon
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Exibe indicador de carregamento se necessário
        if (isLoading) {
            CircularProgressIndicator()
        } else {
            // Exibe a imagem do Pokémon
            AsyncImage(
                model = imageUrl,
                contentDescription = pokemon.name,
                contentScale = ContentScale.Fit,  // Mantém a proporção original
                modifier = Modifier
                    .width(150.dp)  // Define a largura da imagem
                    .height(150.dp) // Define a altura da imagem
            )
        }

        // Espaço entre a imagem e o nome
        Spacer(modifier = Modifier.height(8.dp))

        // Exibe o nome do Pokémon
        Text(
            text = pokemon.name.replaceFirstChar { it.uppercase() },
            color = Color.Black,
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun PokeGrid(pokemonList: List<Pokemon>, onClick: (Pokemon) -> Unit) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(0.dp),
        verticalArrangement = Arrangement.spacedBy(3.dp),
        horizontalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        items(pokemonList.size) { index ->
            PokeCard(pokemonList[index], onClick = onClick)
        }
    }
}

@Composable
private fun PokeTitleImage() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.pokedex_logo),
            contentDescription = "Imagem escrito Pokedex",
            modifier = Modifier
                .width(250.dp)
                .height(90.dp),
            contentScale = ContentScale.FillBounds
        )
    }
}

private fun getColorValue(context: Context, colorName: String): Int {
    val resources = context.resources
    return when (colorName) {
        "red" -> resources.getColor(R.color.red)
        "blue" -> resources.getColor(R.color.blue)
        "green" -> resources.getColor(R.color.green)
        "yellow" -> resources.getColor(R.color.yellow)
        "purple" -> resources.getColor(R.color.purple)
        "pink" -> resources.getColor(R.color.pink)
        "brown" -> resources.getColor(R.color.brown)
        "gray" -> resources.getColor(R.color.gray)
        "black" -> resources.getColor(R.color.black)
        "white" -> resources.getColor(R.color.white)
        "teal_200" -> resources.getColor(R.color.teal_200)
        "teal_700" -> resources.getColor(R.color.teal_700)
        "purple_200" -> resources.getColor(R.color.purple_200)
        "purple_500" -> resources.getColor(R.color.purple_500)
        "purple_700" -> resources.getColor(R.color.purple_700)
        else -> resources.getColor(R.color.black) // default color
    }
}