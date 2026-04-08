package com.example.pokedex.list.presentation.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.example.pokedexsimple.R
import com.example.pokedex.common.data.remote.model.CommonFunctions
import com.example.pokedex.common.ui.GifImage
import com.example.pokedex.common.ui.LoadingScreen
import com.example.pokedex.common.ui.PokeErrorImage
import com.example.pokedex.common.ui.PokeTitleImage
import com.example.pokedex.list.presentation.PokeListViewModel
import kotlin.random.Random

@Composable
fun PokeListScreen(
    navController: NavHostController,
    pokeListViewModel: PokeListViewModel,
) {
    val pokemons by pokeListViewModel.uiPokemonsList.collectAsState()
    val selectedPokemons = pokeListViewModel.selectedPokemons.collectAsState().value


    Box(modifier = Modifier.fillMaxSize()) {
        PokeListContent(
            pokeListUiState = pokemons,
            onClick = { pokeItemClicked ->
                navController.navigate("pokemonDetail/${pokeItemClicked.id}")
            }, onLoadMore = {
                pokeListViewModel.loadMorePokemons()
            }, onSelectionChange = { pokemon, isSelected ->
                pokeListViewModel.toggleSelection(pokemon, isSelected)
            },selectedPokemons = selectedPokemons
        )

        if (selectedPokemons.size == 2) {
            IconButton(
                onClick = {
                    navController.navigate("battle_screen/${selectedPokemons[0].name}/${selectedPokemons[1].name}")
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
                    .size(130.dp)
            ) {
                Image(
                    painter = painterResource(id = if (isSystemInDarkTheme()) R.drawable.sword_battle_dark else R.drawable.sword_battle),
                    contentDescription = "Check the battle",
                    modifier = Modifier.size(100.dp)
                )
            }
        }
    }

}

@Composable
private fun PokeListContent(
    pokeListUiState: PokeListUiState,
    onClick: (PokemonUiData) -> Unit,
    onSelectionChange: (PokemonUiData, Boolean) -> Unit,
    selectedPokemons: List<PokemonUiData>,
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
                onSelectionChange = onSelectionChange,
                selectedPokemons = selectedPokemons,
            )
        }
    }
}



@Composable
fun PokeGrid(
    pokeListUiState: PokeListUiState,
    selectedPokemons: List<PokemonUiData>,
    onCardClick: (PokemonUiData) -> Unit,
    onSelectionChange: (PokemonUiData, Boolean) -> Unit,
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
            PokeCard(pokemonUiData = pokemon,
                onClick = onCardClick,
                isSelected = selectedPokemons.contains(pokemon),
                onSelectionChange = onSelectionChange
            )
        }

        /*onSelectionChange: (PokemonUiData, Boolean) -> Unit*/

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
    isSelected: Boolean,
    onClick: (PokemonUiData) -> Unit,
    onSelectionChange: (PokemonUiData, Boolean) -> Unit,
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
            .fillMaxWidth().padding(5.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(color = cardColor)
            .border(2.dp, cardColor, RoundedCornerShape(16.dp))
            .height(220.dp)
            .clickable { onClick.invoke(pokemonUiData) },
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .width(150.dp)
                .height(150.dp)
                .clickable { onClick(pokemonUiData) }
        ) {
            if (isLoading) {
                LoadingScreen()
            }else{
                if (LocalInspectionMode.current) {
                    Image(
                        painter = painterResource(id = R.drawable.floragato),
                        contentDescription = pokemonUiData.name,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.fillMaxSize()
                    )
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
                Image(
                    painter = painterResource(
                        if (isSelected) {
                            if (isSystemInDarkTheme()) R.drawable.sword_selected_dark else R.drawable.sword_selected
                        } else {
                            if (isSystemInDarkTheme()) R.drawable.sword_unselected_dark else R.drawable.sword_unselected
                        }
                    ),
                    contentDescription = "Selecionado",
                    modifier = Modifier
                        .size(60.dp)
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
                        .clickable {
                            onSelectionChange(
                                pokemonUiData,
                                !isSelected
                            )
                        }
                )


            }
    }
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
