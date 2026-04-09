package com.example.pokedex.common.ui

import android.os.Build.VERSION.SDK_INT
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import coil.compose.rememberAsyncImagePainter
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.request.ImageRequest
import coil.size.Size
import com.example.pokedexsimple.R

@Composable
fun PokeTitleImage(
    navHostController: NavHostController? = null,
    showSearchAction: Boolean = false
) {
    var isSearchExpanded by rememberSaveable { mutableStateOf(false) }
    var searchText by rememberSaveable { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(90.dp),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.pokedex_logo),
                contentDescription = "Imagem escrito Pokedex",
                modifier = Modifier
                    .width(250.dp)
                    .height(90.dp),
                contentScale = ContentScale.FillBounds
            )

            when {
                navHostController != null -> {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Voltar",
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .padding(start = 12.dp)
                            .size(28.dp)
                            .clickable { navHostController.popBackStack() }
                    )
                }

                showSearchAction -> {
                    SearchTopAction(
                        isExpanded = isSearchExpanded,
                        searchText = searchText,
                        onSearchTextChange = { searchText = it },
                        onToggle = { isSearchExpanded = !isSearchExpanded },
                        onClose = {
                            isSearchExpanded = false
                            searchText = ""
                        },
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .padding(start = 12.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun SearchTopAction(
    isExpanded: Boolean,
    searchText: String,
    onSearchTextChange: (String) -> Unit,
    onToggle: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val animatedWidth by animateDpAsState(
        targetValue = if (isExpanded) 220.dp else 40.dp,
        label = "search_width"
    )

    Surface(
        modifier = modifier
            .width(animatedWidth)
            .height(40.dp),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f),
        tonalElevation = 4.dp,
        shadowElevation = 4.dp
    ) {
        AnimatedContent(
            targetState = isExpanded,
            label = "search_content"
        ) { expanded ->
            if (expanded) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(start = 10.dp, end = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    BasicTextField(
                        value = searchText,
                        onValueChange = onSearchTextChange,
                        singleLine = true,
                        textStyle = LocalTextStyle.current.copy(
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 14.sp
                        ),
                        modifier = Modifier.weight(1f),
                        decorationBox = { innerTextField ->
                            Box(
                                contentAlignment = Alignment.CenterStart
                            ) {
                                if (searchText.isBlank()) {
                                    Text(
                                        text = "Buscar...",
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontSize = 14.sp
                                    )
                                }
                                innerTextField()
                            }
                        }
                    )

                    IconButton(
                        onClick = onClose,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Fechar busca",
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable { onToggle() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Abrir busca",
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }
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
                .width(300.dp)
                .height(400.dp),
            contentScale = ContentScale.FillHeight
        )
        Text(
            fontSize = 24.sp,
            modifier = Modifier.padding(16.dp),
            color = Color.Red,
            text = "Something went wrong!",
            fontWeight = FontWeight.Bold
        )
        Text(
            fontSize = 16.sp,
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

@Preview(showBackground = false)
@Composable()
private fun LoadingScreenPreview() {
    MaterialTheme {
        LoadingScreen()
    }
}