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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
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
import com.example.pokedexsimple.R
import com.example.pokedex.common.data.remote.model.CommonFunctions
import com.example.pokedex.list.presentation.PokeListViewModel
import kotlin.random.Random

@Composable
fun PokeListScreen(
    navController: NavHostController,
    pokeListViewModel: PokeListViewModel,
) {
    val pokemons by pokeListViewModel.uiPokemonsList.collectAsState()
    PokeListContent(
        pokeListUiState = pokemons,
        onClick = { pokeItemClicked ->
            navController.navigate("pokemonDetail/${pokeItemClicked.id}")
        }, onLoadMore = {
            pokeListViewModel.loadMorePokemons()
        }
    )
}

@Composable
private fun PokeListContent(
    pokeListUiState: PokeListUiState,
    onClick: (PokemonUiData) -> Unit,
    onLoadMore: () -> Unit
) {
    val isLoading = pokeListUiState.isInitialLoading
    val isError = pokeListUiState.isError
    val errorMessage = pokeListUiState.errorMessage
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        if (isError) {
            PokeErrorImage(errorMessage)
        } else if (isLoading) {
            PokeTitleImage()
            LoadingScreen()
        } else {
            PokeTitleImage()
            PokeGrid(
                pokeListUiState = pokeListUiState,
                onCardClick = onClick,
                onLoadMore = onLoadMore,
            )
        }
    }
}

@Composable
fun PokeGrid(
    pokeListUiState: PokeListUiState,
    onCardClick: (PokemonUiData) -> Unit,
    onLoadMore: () -> Unit
) {
    val gridState = rememberLazyGridState()

    val shouldLoadMore = remember {
        derivedStateOf {
            val layoutInfo = gridState.layoutInfo
            val totalItems = layoutInfo.totalItemsCount
            val lastVisibleItem = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0

            totalItems > 0 && lastVisibleItem >= totalItems - 6
        }
    }

    LaunchedEffect(shouldLoadMore.value) {
        if(shouldLoadMore.value){
            onLoadMore()
        }
    }

    LazyVerticalGrid(
        state = gridState,
        columns = GridCells.Fixed(2),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Adiciona os cards dos Pokémon
        items(items = pokeListUiState.pokemonUiDataList,
            key = { it.id?.toString() ?: it.name.orEmpty() }
            ) { pokemon ->
            PokeCard(pokemon, onClick = onCardClick)
        }

        if(pokeListUiState.isAppending){
            item(span = { GridItemSpan(2) }){
                LoadingMoreFooter()
            }
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
    var cardColor by remember { mutableStateOf(Color.Transparent) }
    var textCardColor by remember { mutableStateOf(Color.Transparent) }
    var imageUrl by remember { mutableStateOf("") }


    LaunchedEffect(pokemonUiData) {
        isLoading = true
        pokemonUiData.imageUrl?.let { imageUrl = it }
        val dominantColorCardAndText = CommonFunctions().getDominantColorFromImage(
            context, imageUrl,
            //VALORES DO CARD
            index = CommonFunctions().inequalRandom(), //Métodó da paleta
            target = (1..5).random(),//métodó do target
            //VALORES DO CARD
        )
        textCardColor = dominantColorCardAndText.second ?: pokemonUiData.color ?: Color.Gray
        cardColor = dominantColorCardAndText.first ?: pokemonUiData.color ?: Color.Gray
        isLoading = false
    }
    Column(
        modifier = Modifier
            .padding(16.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(color = cardColor)
            .border(2.dp, cardColor, RoundedCornerShape(16.dp))
            .height(200.dp)
            .clickable { onClick.invoke(pokemonUiData) },
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
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
            color = textCardColor,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
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
                modifier = Modifier
                    .size(240.dp)
                    .clip(CircleShape)
                    .scale(1.0f)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Loading...",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF302697)
            )
        }
    }
}


@Composable
fun GifImage(
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    if (LocalInspectionMode.current) {
        Image(
            painter = painterResource(R.drawable.loadingpokemon),
            contentDescription = null,
            modifier = modifier
        )
        return
    }

    val imageLoader = ImageLoader.Builder(context)
        .components {
            if (SDK_INT >= 28) {
                add(ImageDecoderDecoder.Factory())
            } else {
                add(GifDecoder.Factory())
            }
        }
        .build()

    Image(
        painter = rememberAsyncImagePainter(
            model = ImageRequest.Builder(context)
                .data(R.drawable.loadingpokemon)
                .size(Size.ORIGINAL)
                .build(),
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

@Composable
fun LoadingMoreFooter() {
    val footerType = remember { Random.nextInt(8) }

    when (footerType) {
        0 -> LoadingFooterClassic()
        1 -> LoadingFooterWithText()
        2 -> LoadingFooterCardStyle()
        3 -> LoadingFooterMinimalist()
        4 -> LoadingFooterWithProgress()
        5 -> LoadingFooterGameFeel()
        6 -> LoadingFooterFade()
        7 -> LoadingFooterHorizontal()
    }
}

@Composable
private fun LoadingFooterClassic() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        GifImage(
            modifier = Modifier.size(80.dp)
        )
    }
}

@Composable
private fun LoadingFooterWithText() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        GifImage(
            modifier = Modifier.size(80.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Loading more Pokémon...",
            fontSize = 14.sp,
            color = Color.Gray
        )
    }
}

@Composable
private fun LoadingFooterCardStyle() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                GifImage(
                    modifier = Modifier.size(70.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Catching more Pokémon..."
                )
            }
        }
    }
}

@Composable
private fun LoadingFooterMinimalist() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        GifImage(
            modifier = Modifier.size(50.dp)
        )
    }
}

@Composable
private fun LoadingFooterWithProgress() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        GifImage(
            modifier = Modifier.size(60.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        LinearProgressIndicator(
            modifier = Modifier.fillMaxWidth(0.5f)
        )
    }
}

@Composable
private fun LoadingFooterGameFeel() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        GifImage(
            modifier = Modifier.size(90.dp)
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Searching in tall grass...",
            fontSize = 13.sp,
            color = Color.Gray
        )
    }
}

@Composable
private fun LoadingFooterFade() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp)
            .alpha(0.7f),
        contentAlignment = Alignment.Center
    ) {
        GifImage(
            modifier = Modifier.size(70.dp)
        )
    }
}

@Composable
private fun LoadingFooterHorizontal() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        GifImage(
            modifier = Modifier.size(50.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(text = "Loading more...")
    }
}

@Preview (showBackground = false)
@Composable()
private fun LoadingScreenPreview() {
    MaterialTheme{
        LoadingScreen()
    }
}