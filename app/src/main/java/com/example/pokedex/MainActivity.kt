package com.example.pokedex

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.pokedex.ui.theme.PokeDexTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //enableEdgeToEdge()
        setContent {
            PokeDexTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                ) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Composable
fun PokeCard() {
    Column(
        modifier = Modifier
            .width(100.dp) // Aumentei para facilitar a visualização
            .height(100.dp)
            .padding(8.dp), // Para adicionar espaço interno
        verticalArrangement = Arrangement.Center, // Centraliza verticalmente
        horizontalAlignment = Alignment.CenterHorizontally // Centraliza horizontalmente
    ) {
        Image(
            imageVector = Icons.Default.Star,
            contentDescription = "Star Icon",
            modifier = Modifier.size(40.dp) // Controla o tamanho da imagem
        )
        Spacer(modifier = Modifier.height(8.dp)) // Adiciona espaço entre a imagem e o texto
        Text(text = "Star")
    }
}

@Composable
fun PokeList(){

}

@Composable
fun PokeDexApp() {

}

@Preview(showBackground = false)
@Composable
fun GreetingPreview() {
    PokeCard()
    /*PokeDexTheme {
        Greeting("Android")
    }*/
}