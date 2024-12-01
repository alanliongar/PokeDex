package com.example.pokedex.list.data.local

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.PictureDrawable
import coil.ImageLoader
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.example.pokedex.common.data.local.PokemonDao
import com.example.pokedex.common.data.local.PokemonEntity
import com.example.pokedex.common.data.model.Pokemon
import java.io.File

class PokeListLocalDataSource(
    private val dao: PokemonDao
) {

    suspend fun updateLocalPokemonsList(pokemons: List<Pokemon>, context: Context, page: Int) {
        val pokemonsEntities = pokemons.map {
            insertOrUpdatePokemon(
                pokemon = PokemonEntity(
                    id = it.id,
                    name = it.name,
                    image1 = it.image[0],
                    image2 = it.image[1],
                    image3 = it.image[2],
                    color = it.color,
                    page = page
                ), context = context
            )
        }
    }

    suspend fun getPokeCount(): Int {
        return dao.getPokeCount()
    }


    suspend fun getPokemonList(page: Int): List<Pokemon> {
        val pokemonsEntities = dao.getThisPagePokemons(page = page)
        return pokemonsEntities.map {
            Pokemon(
                id = it.id,
                name = it.name,
                image = listOf(it.image1, it.image2, it.image3),
                color = it.color,
                page = it.page
            )
        }
    }

    suspend fun insertOrUpdatePokemon(pokemon: PokemonEntity, context: Context) {
        val existingPokemon = dao.getPokemonByName(pokemon.name)
        if (existingPokemon != null) {
            // Verifique se as imagens mudaram
            if (existingPokemon.image1 != pokemon.image1 ||
                existingPokemon.image2 != pokemon.image2 ||
                existingPokemon.image3 != pokemon.image3
            ) {
                // Exclua imagens antigas
                deleteUnusedImages(
                    listOf(
                        existingPokemon.image1,
                        existingPokemon.image2,
                        existingPokemon.image3
                    )
                )
                // Baixe e salve as novas imagens
                downloadAndSavePokemonImages(pokemon, context)
            }
        } else {
            // Novo Pokémon, baixe as imagens
            downloadAndSavePokemonImages(pokemon, context)
        }
        // Insira ou atualize o Pokémon no banco
        dao.insertPokemon(pokemon)
    }

    suspend fun downloadAndSavePokemonImages(
        pokemon: PokemonEntity,
        context: Context
    ): PokemonEntity {
        // Mapeie os caminhos locais para cada imagem
        val imagePaths = listOf(
            "${context.cacheDir}/pokemon_${pokemon.id}_1.png",
            "${context.cacheDir}/pokemon_${pokemon.id}_2.png",
            "${context.cacheDir}/pokemon_${pokemon.id}_3.png"
        )

        // Mapeie URLs existentes da entidade
        val imageUrls = listOf(
            pokemon.image1,
            pokemon.image2,
            pokemon.image3
        )

        // Baixe e salve as imagens
        val downloadedPaths = imageUrls.mapIndexed { index, url ->
            saveImage(url, imagePaths[index], context)
        }

        // Retorne a entidade atualizada com os novos caminhos
        return pokemon.copy(
            image1 = downloadedPaths[0],
            image2 = downloadedPaths[1],
            image3 = downloadedPaths[2]
        )
    }

    suspend fun saveImage(imageUrl: String, path: String, context: Context): String {
        val file = File(path)

        // Use Coil para baixar a imagem
        val imageLoader = ImageLoader(context)
        val request = if (imageUrl.takeLast(3) == "svg") {
            ImageRequest.Builder(context)
                .data(imageUrl)
                .decoderFactory(SvgDecoder.Factory()) // SVG usa decoder específico
                .allowHardware(false)
                .build()
        } else {
            ImageRequest.Builder(context)
                .data(imageUrl)
                .allowHardware(false)
                .build()
        }
        val result = (imageLoader.execute(request) as SuccessResult).drawable
        file.outputStream().use { outputStream ->
            when (result) {
                is BitmapDrawable -> {
                    result.bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                }

                is PictureDrawable -> {
                    // Converta PictureDrawable para Bitmap
                    val bitmap = Bitmap.createBitmap(
                        result.intrinsicWidth,
                        result.intrinsicHeight,
                        Bitmap.Config.ARGB_8888
                    )
                    val canvas = Canvas(bitmap)
                    result.draw(canvas)
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                }

                else -> throw IllegalArgumentException("Formato de imagem desconhecido")
            }
        }
        return file.absolutePath // Retorna o caminho salvo
    }

    fun deleteUnusedImages(paths: List<String>) {
        paths.forEach { path ->
            val file = File(path)
            if (file.exists()) {
                file.delete()
            }
        }
    }

}