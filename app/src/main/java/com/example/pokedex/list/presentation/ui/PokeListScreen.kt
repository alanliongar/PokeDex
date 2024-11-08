package com.example.pokedex.list.presentation.ui

import android.os.Build.VERSION.SDK_INT
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import coil.size.Size
import com.example.pokedex.R
import com.example.pokedex.common.data.remote.model.CommonFunctions
import com.example.pokedex.list.presentation.PokeListViewModel

@Composable
fun PokeListScreen(
    navController: NavHostController,
    pokeListViewModel: PokeListViewModel
) {
    val pokemons by pokeListViewModel.uiPokemonsList.collectAsState()

    PokeListContent(
        pokeListUiState = pokemons,
    ) { pokeItemClicked ->
        navController.navigate("pokemonDetail/${pokeItemClicked.id}")
    }
}

@Composable
private fun PokeListContent(
    pokeListUiState: PokeListUiState,
    onClick: (PokemonUiData) -> Unit
) {
    val isLoading = pokeListUiState.isLoading
    val isError = pokeListUiState.isError
    val errorMessage = pokeListUiState.errorMessage
    if (isError) {
        PokeErrorImage(errorMessage)
    } else if (isLoading) {
        Column {
            PokeTitleImage()
            LoadingScreen()
        }
    } else {
        Column {
            PokeTitleImage()
            PokeGrid(pokeListUiState = pokeListUiState, onClick = onClick)
        }
    }
}

@Composable
private fun PokeGrid(
    pokeListUiState: PokeListUiState,
    onClick: (PokemonUiData) -> Unit
) {
    val listPokemonUiData = pokeListUiState.pokemonUiDataList
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(0.dp),
        verticalArrangement = Arrangement.spacedBy(3.dp),
        horizontalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        items(listPokemonUiData.size) { index ->
            PokeCard(listPokemonUiData[index], onClick = onClick)
        }
    }
}


@Composable
private fun PokeCard(
    pokemonUiData: PokemonUiData,
    onClick: (PokemonUiData) -> Unit
) {
    val context = LocalContext.current
    var isLoading by remember { mutableStateOf(true) }
    var color by remember { mutableStateOf(Color.Transparent) }
    var imageUrl by remember { mutableStateOf("") }


    LaunchedEffect(pokemonUiData) {
        isLoading = true
        pokemonUiData.imageUrl?.let { imageUrl = it }
        val dominantColor = CommonFunctions().getDominantColorFromImage(context, imageUrl)
        color = dominantColor ?: pokemonUiData.color ?: Color.Gray
        isLoading = false
    }
    // Estrutura do Card
    Column(
        modifier = Modifier
            .padding(16.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(color = color)
            .border(2.dp, color, RoundedCornerShape(16.dp))
            .height(200.dp)
            .clickable { onClick.invoke(pokemonUiData) },
        // Cor de fundo baseada no Pokémon
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Exibe indicador de carregamento se necessário
        if (isLoading) {
            LoadingScreen()
        } else {
            if (imageUrl.endsWith(".svg")) {
                val imageLoader = ImageLoader.Builder(LocalContext.current).components {
                    add(SvgDecoder.Factory())
                }
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(imageUrl)
                        .decoderFactory(SvgDecoder.Factory())
                        .build(),
                    contentDescription = if (imageUrl.endsWith(".svg")) null else pokemonUiData.name,
                    modifier = Modifier
                        .width(150.dp)
                        .height(150.dp),
                    contentScale = ContentScale.Fit
                )
            } else {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = pokemonUiData.name,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .width(150.dp)
                        .height(150.dp)
                )
            }
        }

        // Espaço entre a imagem e o nome
        Spacer(modifier = Modifier.height(8.dp))

        // Exibe o nome do Pokémon
        Text(
            text = pokemonUiData.name.toString().replaceFirstChar { it.uppercaseChar() },
            color = Color.Black,
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun PokeErrorImage(errorMsg: String? = null) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            fontSize = 36.sp,
            modifier = Modifier.padding(16.dp),
            color = Color.Red,
            text = "Ohh No!",
            fontWeight = FontWeight.Bold
        )
        Image(
            painter = painterResource(id = R.drawable.error_background),
            contentDescription = "Imagem de erro",
            modifier = Modifier
                .width(432.dp)
                .height(577.dp),
            contentScale = ContentScale.FillHeight
        )
        Text(
            fontSize = 32.sp,
            modifier = Modifier.padding(16.dp),
            color = Color.Red,
            text = "Something went wrong!",
            fontWeight = FontWeight.Bold
        )
        Text(
            fontSize = 20.sp,
            modifier = Modifier.padding(16.dp),
            color = Color.Blue,
            text = errorMsg ?: "Go back and try again",
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun LoadingScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            GifImage(
                modifier = Modifier.size(500.dp) // Tamanho do GIF
            )
            Text("Loading...", fontSize = 24.sp)
        }
    }
}

@Composable
fun GifImage(
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val imageLoader = ImageLoader.Builder(context)
        .components {
            if (SDK_INT >= 30) {
                add(ImageDecoderDecoder.Factory())
            } else {
                add(GifDecoder.Factory())
            }
        }
        .build()
    Image(
        painter = rememberAsyncImagePainter(
            ImageRequest.Builder(context).data(data = R.drawable.loadingpokemon).apply {
                size(Size.ORIGINAL)
            }.build(),
            imageLoader = imageLoader
        ),
        contentDescription = null,
        modifier = modifier
    )
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