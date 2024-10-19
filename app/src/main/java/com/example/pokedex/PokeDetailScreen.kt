package com.example.pokedex

import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.material3.Text
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


@Composable
fun PokeDetailScreen(pokeId: String, context: Context) {
    PokeDetailContent(pokeId = pokeId, context = context)
}

@Composable
private fun PokeDetailContent(pokeId: String, context: Context) {
    val pokemonDetail = remember { mutableStateOf<PokeDto?>(null) }
    val pokeName = remember { mutableStateOf<String>("") }
    val imgDetail = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/home/$pokeId.png"
    val pokeColor = remember { mutableStateOf<String>("") }
    val isLoading = remember { mutableStateOf<Boolean>(true) }
    var pokemColor: Int? = null
    val pokeApiService =
        PokeRetrofitClient.retrofitInstance.create(PokeApiService::class.java)
    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            val pokeDetail = pokeApiService.getPokemonDetail(pokeId.toInt())
            if (pokeDetail.isSuccessful) {
                val body = pokeDetail.body()
                Log.d("PagodeBranco", "Response body: $body")
                body?.let {
                    Log.d("Pokedexxxx", "Resposta recebida para o pokemon ${it.name}")
                    val colorDetail = pokeApiService.getPokemonColorAndSpecie(it.name)
                    if (colorDetail.isSuccessful) {
                        pokeColor.value = colorDetail.body()?.color?.name.toString()
                        pokemColor = getColorValue(context = context, pokeColor.value)
                    } else {
                    }
                    pokemonDetail.value = it
                    pokeName.value = pokemonDetail.value?.name.toString()
                    Log.d(
                        "Pokedexxxx",
                        "Nome do pokemon: ${pokemonDetail.value?.name.toString()}"
                    )
                }
            } else {
                Log.e(
                    "Pokedexxxx",
                    "Erro ao buscar Detalhe do pokemon id ${pokeId}: ${
                        pokeDetail.errorBody()?.string()
                    }"
                )
            }
        }
        isLoading.value = false
    }

    if (isLoading.value) {
        CircularProgressIndicator()
    } else {
        Column(modifier = Modifier.fillMaxSize()) {
            Text(
                text = pokemonDetail.value?.name.orEmpty(),
                style = MaterialTheme.typography.bodyLarge
            )
            AsyncImage(model = imgDetail, contentDescription = null)
            Text(text = "Weight: ${((pokemonDetail.value?.weight ?: 0) / 10.0)} KG")
            Text(text = "Height: ${((pokemonDetail.value?.height ?: 0) / 10.0)} M")

            val stats: List<Pair<String, Int>> = listOf(
                "HP" to (pokemonDetail.value?.baseStats?.getOrNull(0)?.baseStat ?: 0),
                "Attack" to (pokemonDetail.value?.baseStats?.getOrNull(1)?.baseStat ?: 0),
                "Defense" to (pokemonDetail.value?.baseStats?.getOrNull(2)?.baseStat ?: 0),
                "Speed" to (pokemonDetail.value?.baseStats?.getOrNull(5)?.baseStat ?: 0),
                "Experiência" to (pokemonDetail.value?.baseExperience ?: 0)
            )

            // Pega o primeiro par (stat) da lista
            stats.forEach { (name, value) ->
                StatBar(
                    statName = name,
                    currentValue = value,
                    maxValue = if (name == "Experiência") 1000 else 300
                )
            }
        }
    }


}

@Composable
fun StatBar(statName: String, currentValue: Int, maxValue: Int) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(text = statName, style = MaterialTheme.typography.bodySmall)
        LinearProgressIndicator(
            progress = currentValue / maxValue.toFloat(),
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .clip(
                    RoundedCornerShape(4.dp)
                )
        )
        Text(text = "$currentValue/$maxValue", style = MaterialTheme.typography.bodyMedium)
    }
}


/*Column(modifier = Modifier.fillMaxSize()) {
    Text(text = pokemonDetail.value?.name.toString())
    Text(
        text = "Weight: " + (((pokemonDetail.value?.weight)?.toFloat()
            ?: null)?.div(10)).toString() + " KG"
    )
    Text(
        text = "Height: " + (((pokemonDetail.value?.height)?.toFloat()
            ?: null)?.div(10)).toString() + " M"
    )
    pokemonDetail.value?.baseStats?.getOrNull(0)
        ?.let { Text(text = "Health: ${it.baseStat}") }
    pokemonDetail.value?.baseStats?.getOrNull(1)
        ?.let { Text(text = "Attack: ${it.baseStat}") }
    pokemonDetail.value?.baseStats?.getOrNull(2)
        ?.let { Text(text = "Defense: ${it.baseStat}") }
    pokemonDetail.value?.baseStats?.getOrNull(5)
        ?.let { Text(text = "Speed: ${it.baseStat}") }
    Text(text = "Base Experience: " + pokemonDetail.value?.baseExperience.toString())
}
}
}*/

/*
@Preview(showBackground = true)
@Composable
private fun PokeDetailPreview(pokeId: String) {
    PokeDetailContent(pokeId)
}*/
