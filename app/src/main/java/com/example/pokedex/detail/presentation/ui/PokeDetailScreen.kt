package com.example.pokedex.detail.presentation.ui

import android.content.Context
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.example.pokedex.common.data.remote.model.CommonFunctions
import com.example.pokedex.detail.presentation.PokeDetailViewModel
import com.example.pokedex.list.presentation.ui.LoadingScreen
import com.example.pokedex.list.presentation.ui.PokeErrorImage


@Composable
fun PokeDetailScreen(
    pokeId: String,
    viewModel: PokeDetailViewModel,
) {
    val context = LocalContext.current
    val uiPokeDto by viewModel.uiPokeDto.collectAsState()
    val pokeIdInt = pokeId.toIntOrNull()

    DisposableEffect(Unit) {
        onDispose {
            viewModel.clearState()
        }
    }

    LaunchedEffect(pokeIdInt) {
        pokeIdInt?.let {
            viewModel.fetchPokemonData(it)
        }
    }


    PokeDetailContent(
        pokemonDetailUiState = uiPokeDto,
        context = context,
    )
}

@Composable
private fun PokeDetailContent(
    pokemonDetailUiState: PokemonDetailUiState,
    context: Context
) {
    val color = pokemonDetailUiState.color ?: Color.Transparent
    val textColor = pokemonDetailUiState.textColor
    val pokemon = pokemonDetailUiState.PokeDetail
    val pokeImg = pokemonDetailUiState.image

    when {
        pokemonDetailUiState.isLoading -> {
            LoadingScreen()
        }

        pokemonDetailUiState.isError -> {
            PokeErrorImage()
        }

        else -> {
            Box(
                modifier = Modifier
                    .background(color)
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
                        text = pokemon?.name.orEmpty()
                            .replaceFirstChar { it.uppercase() },
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold, color = textColor ?: Color.Black
                    )
                    Text(
                        text = "#${pokemon?.pokeId.toString()?.padStart(4, '0')}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold, color = textColor ?: Color.Black,
                        modifier = Modifier.padding(8.dp)
                    )


                    if (pokeImg.endsWith("svg") == true) {
                        AsyncImage(
                            model = ImageRequest.Builder(context).data(pokeImg)
                                .decoderFactory(SvgDecoder.Factory()).build(),
                            contentDescription = null,
                            modifier = Modifier.height(300.dp), contentScale = ContentScale.Fit
                        )
                    } else {
                        AsyncImage(
                            model = pokeImg,
                            contentDescription = null,
                            modifier = Modifier.height(300.dp), contentScale = ContentScale.Fit
                        )
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        pokemon?.typesPok?.let { types ->
                            types.forEachIndexed { index, typeSlot ->
                                Box(
                                    modifier = Modifier
                                        .weight(0.7f)
                                        .height(34.dp)
                                        .clip(RoundedCornerShape(24.dp))
                                        .background(
                                            CommonFunctions().getTypeColor(
                                                typeSlot.type.name,
                                                context
                                            )
                                        )
                                        .padding(4.dp),
                                    contentAlignment = Alignment.Center // Centraliza o conteúdo
                                ) {
                                    Text(
                                        text = typeSlot.type.name,
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = textColor ?: Color.Black
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
                                text = "${((pokemon?.weight ?: 0) / 10.0)} KG",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = textColor ?: Color.Black
                            )
                            Text(text = "Weight", color = textColor ?: Color.Black)
                        }
                        Column(
                            modifier = Modifier.padding(start = 16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "${((pokemon?.height ?: 0) / 10.0)} M",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = textColor ?: Color.Black
                            )
                            Text(text = "Height", color = textColor ?: Color.Black)
                        }
                    }


                    val stats: List<Pair<String, Int>> = listOf(
                        "HP" to (pokemon?.baseStats?.getOrNull(0)?.baseStat
                            ?: 0),
                        "ATK" to (pokemon?.baseStats?.getOrNull(1)?.baseStat
                            ?: 0),
                        "DEF" to (pokemon?.baseStats?.getOrNull(2)?.baseStat
                            ?: 0),
                        "SPD" to (pokemon?.baseStats?.getOrNull(5)?.baseStat
                            ?: 0),
                        "XP" to (pokemon?.baseExperience ?: 0)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Base Stats", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = textColor ?: Color.Black)
                    Spacer(modifier = Modifier.height(8.dp))

                    stats.forEach { (name, value) ->
                        StatBar(
                            statName = name,
                            currentValue = value,
                            textColor = textColor?:Color.Black,
                            color = CommonFunctions().getBarColor(name, context),
                            maxValue = if (name == "XP") 1000 else 300
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StatBar(statName: String, currentValue: Int, color: Color, maxValue: Int, textColor: Color) {
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
                fontWeight = FontWeight.SemiBold, color=textColor
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
                style = MaterialTheme.typography.bodyMedium.copy(color = textColor),
                modifier = Modifier.align(Alignment.Center), fontWeight = FontWeight.Bold
            )
        }
    }
}


