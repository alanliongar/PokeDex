package com.example.pokedex

import android.content.Context
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.random.Random


@Composable
fun PokeDetailScreen(pokeId: String, context: Context) {
    PokeDetailContent(pokeId = pokeId, context = context)
}

@Composable
private fun PokeDetailContent(pokeId: String, context: Context) {
    val pokemonDetail = remember { mutableStateOf<PokeDto?>(null) }
    val pokeName = remember { mutableStateOf<String>("") }

    val imageUrls = listOf(
        "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/dream-world/$pokeId.svg",
        "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/$pokeId.png",
        "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/home/$pokeId.png"
    )


    val imgDetail = remember { mutableStateOf(imageUrls[Random.nextInt(imageUrls.size)]) }
    //val imgDetail = remember { mutableStateOf("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/dream-world/$pokeId.svg") }
    val pokeColor = remember { mutableStateOf<Color>(Color.White) }
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
                        pokeColor.value = Color(
                            getColorValue(
                                context = context,
                                colorDetail.body()?.color?.name.toString()
                            )
                        ).copy(alpha = 0.6f)
                        Log.d("PagodePreto", "Cor do pokemon: ${pokemColor}")
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
        Box(
            modifier = Modifier
                .background(pokeColor.value)
                .fillMaxSize()
        )
        {
            Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = pokemonDetail.value?.name.orEmpty()
                            .replaceFirstChar { it.uppercase() },
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "#${pokeId.padStart(4, '0')}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(8.dp)
                    )


                    if (imgDetail.value.endsWith("svg")) {
                        AsyncImage(
                            model = ImageRequest.Builder(context).data(imgDetail.value)
                                .decoderFactory(SvgDecoder.Factory()).build(),
                            contentDescription = null,
                            modifier = Modifier.height(400.dp), contentScale = ContentScale.Fit
                        )
                    } else {
                        AsyncImage(
                            model = imgDetail.value,
                            contentDescription = null,
                            modifier = Modifier.height(400.dp), contentScale = ContentScale.Fit
                        )
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        pokemonDetail.value?.typesPok?.let { types ->
                            types.forEachIndexed { index, typeSlot ->
                                Box(
                                    modifier = Modifier
                                        .weight(0.7f)
                                        .height(34.dp)
                                        .clip(RoundedCornerShape(24.dp))
                                        .background(getTypeColor(typeSlot.type.name, context))
                                        .padding(4.dp),
                                    contentAlignment = Alignment.Center // Centraliza o conteúdo
                                ) {
                                    Text(
                                        text = typeSlot.type.name,
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                                if (index < types.size - 1) {
                                    Spacer(modifier = Modifier.width(8.dp))
                                }
                            }
                        }
                    }


                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Column(
                            modifier = Modifier.padding(end = 16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "${((pokemonDetail.value?.weight ?: 0) / 10.0)} KG",
                                fontSize = 24.sp, fontWeight = FontWeight.SemiBold
                            )
                            Text(text = "Weight")
                        }
                        Column(
                            modifier = Modifier.padding(start = 16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "${((pokemonDetail.value?.height ?: 0) / 10.0)} M",
                                fontSize = 24.sp, fontWeight = FontWeight.SemiBold
                            )
                            Text(text = "Height")
                        }
                    }




                    val stats: List<Pair<String, Int>> = listOf(
                        "HP" to (pokemonDetail.value?.baseStats?.getOrNull(0)?.baseStat
                            ?: 0),
                        "ATK" to (pokemonDetail.value?.baseStats?.getOrNull(1)?.baseStat
                            ?: 0),
                        "DEF" to (pokemonDetail.value?.baseStats?.getOrNull(2)?.baseStat
                            ?: 0),
                        "SPD" to (pokemonDetail.value?.baseStats?.getOrNull(5)?.baseStat
                            ?: 0),
                        "XP" to (pokemonDetail.value?.baseExperience ?: 0)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text="Base Stats", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(8.dp))

                    stats.forEach { (name, value) ->
                        StatBar(
                            statName = name,
                            currentValue = value, color = getBarColor(name, context),
                            maxValue = if (name == "XP") 1000 else 300
                        )
                    }
                }
            }

    }

}

@Composable
private fun StatBar(statName: String, currentValue: Int, color: Color, maxValue: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Column(
            modifier = Modifier.weight(0.3f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = statName,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Box(
            modifier = Modifier
                .weight(0.7f)
                .height(24.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.LightGray)
        ) {
            // Parte carregada da barra
            Box(
                modifier = Modifier
                    .fillMaxWidth(currentValue / maxValue.toFloat())
                    .height(24.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(color)
            )

            Text(
                text = "$currentValue / $maxValue",
                style = MaterialTheme.typography.bodyMedium.copy(color = Color.White),
                modifier = Modifier.align(Alignment.Center), fontWeight = FontWeight.Bold
            )
        }
    }
}

private fun getBarColor(barName: String, context: Context): Color {
    return when (barName) {
        "HP" -> Color(ContextCompat.getColor(context, R.color.hp_bar))
        "ATK" -> Color(ContextCompat.getColor(context, R.color.atk_bar))
        "DEF" -> Color(ContextCompat.getColor(context, R.color.def_bar))
        "XP" -> Color(ContextCompat.getColor(context, R.color.exp_bar))
        "SPD" -> Color(ContextCompat.getColor(context, R.color.spd_bar))
        else -> Color.White
    }
}

private fun getTypeColor(type: String, context: Context): Color {
    return when (type) {
        "fire" -> Color(ContextCompat.getColor(context, R.color.type_fire))
        "water" -> Color(ContextCompat.getColor(context, R.color.type_water))
        "grass" -> Color(ContextCompat.getColor(context, R.color.type_grass))
        "electric" -> Color(ContextCompat.getColor(context, R.color.type_electric))
        "ice" -> Color(ContextCompat.getColor(context, R.color.type_ice))
        "fighting" -> Color(ContextCompat.getColor(context, R.color.type_fighting))
        "poison" -> Color(ContextCompat.getColor(context, R.color.type_poison))
        "ground" -> Color(ContextCompat.getColor(context, R.color.type_ground))
        "flying" -> Color(ContextCompat.getColor(context, R.color.type_flying))
        "psychic" -> Color(ContextCompat.getColor(context, R.color.type_psychic))
        "bug" -> Color(ContextCompat.getColor(context, R.color.type_bug))
        "rock" -> Color(ContextCompat.getColor(context, R.color.type_rock))
        "ghost" -> Color(ContextCompat.getColor(context, R.color.type_ghost))
        "dragon" -> Color(ContextCompat.getColor(context, R.color.type_dragon))
        "dark" -> Color(ContextCompat.getColor(context, R.color.type_dark))
        "steel" -> Color(ContextCompat.getColor(context, R.color.type_steel))
        "fairy" -> Color(ContextCompat.getColor(context, R.color.type_fairy))
        else -> Color(ContextCompat.getColor(context, R.color.type_normal))
    }
}
