package com.example.pokedex.common.data.remote.model

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.content.ContextCompat
import androidx.palette.graphics.Palette
import coil.ImageLoader
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import coil.request.SuccessResult
import androidx.palette.graphics.Target


import com.example.pokedex.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.security.SecureRandom
import kotlin.math.pow
import kotlin.math.round
import kotlin.random.Random

class CommonFunctions {
    suspend fun getDominantColorFromImage(
        context: Context,
        imageUrl: String?,
        index: Int = 1,
        target: Int = 1,
    ): Pair<Color?, Color?> {
        return withContext(Dispatchers.IO) {
            val imageLoader = ImageLoader(context)
            val request = if (imageUrl?.takeLast(3) == "svg") {
                ImageRequest.Builder(context)
                    .data(imageUrl)
                    .decoderFactory(SvgDecoder.Factory()) // SVG usa decoder específico
                    .build()
            } else {
                ImageRequest.Builder(context)
                    .data(imageUrl)
                    .build()
            }

            return@withContext try {
                val result = (imageLoader.execute(request) as SuccessResult).drawable
                if (result is BitmapDrawable) {
                    val bitmap = result.bitmap
                    val mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
                    val rndColor = getRandomColor().toArgb()
                    val palette = Palette.from(mutableBitmap).generate()

                    val targets = arrayOf(
                        Target.LIGHT_VIBRANT,
                        Target.VIBRANT,
                        Target.MUTED,
                        Target.DARK_MUTED,
                        Target.DARK_VIBRANT
                    )

                    val validTarget = targets.getOrNull(target - 1) ?: Target.VIBRANT

                    val selectedColor: Int = when (index) {
                        1 -> palette.getDominantColor(rndColor)
                        2 -> palette.getVibrantColor(rndColor)
                        3 -> palette.getMutedColor(rndColor)
                        4 -> palette.getDarkMutedColor(rndColor)
                        5 -> palette.getDarkVibrantColor(rndColor)
                        6 -> palette.getLightMutedColor(rndColor)
                        7 -> palette.getLightVibrantColor(rndColor)
                        8 -> palette.getColorForTarget(targets[target - 1], rndColor)
                        9 -> palette.getSwatchForTarget(targets[target - 1])?.rgb ?: rndColor
                        else -> palette.getColorForTarget(
                            validTarget,
                            rndColor
                        )
                    }

                    var textColorAlphared: Color
                    var valid = true
                    var textIndex = 1
                    var textTarget: Int
                    textColorAlphared = Color.Black
                    val colorWithAlpha = Color(selectedColor).copy(1.0f)
                    while (textIndex < 10 && valid) {
                        textTarget = 1
                        while (textTarget < 6 && valid) {
                            textColorAlphared = when (textIndex) {
                                1 -> Color(palette.getDominantColor(rndColor))
                                2 -> Color(palette.getVibrantColor(rndColor))
                                3 -> Color(palette.getMutedColor(rndColor))
                                4 -> Color(palette.getDarkMutedColor(rndColor))
                                5 -> Color(palette.getDarkVibrantColor(rndColor))
                                6 -> Color(palette.getLightMutedColor(rndColor))
                                7 -> Color(palette.getLightVibrantColor(rndColor))
                                8 -> Color(
                                    palette.getColorForTarget(
                                        targets[textTarget - 1],
                                        rndColor
                                    )
                                )

                                9 -> Color(
                                    palette.getSwatchForTarget(targets[textTarget - 1])?.rgb
                                        ?: rndColor
                                )

                                else -> Color(
                                    palette.getColorForTarget(
                                        validTarget,
                                        Color.Black.toArgb()
                                    )
                                )
                            }
                            if (calculateContrast(
                                    textColorAlphared.toArgb(),
                                    colorWithAlpha.toArgb()
                                ) >= 4.5f
                            ) {
                                valid = false
                            }
                            textTarget++
                        }
                        textIndex++
                    }
                    if (calculateContrast(
                            textColorAlphared.toArgb(),
                            colorWithAlpha.toArgb()
                        ) < 4.5f
                    ) {
                        val pokemonId = imageUrl?.split("/")?.last { it.isNotEmpty() }
                        Log.w(
                            "Contraste Baixo",
                            "Contraste baixo encontrado, aplicando negativo na img ${pokemonId}"
                        )
                        textColorAlphared = getNegativeColor(textColorAlphared)
                    }
                    Pair(colorWithAlpha, textColorAlphared)
                } else {
                    Pair(null, null)
                }

            } catch (e: Exception) {
                //Log.e("PokeListViewModel", "Erro ao obter cor da imagem: ${e.message}")
                Pair(null, null)
            }
        }
    }

    fun inequalRandom(): Int {
        val chance = (0..99).random() // Número entre 0 e 99
        return if (chance < 90) { // 80% de chance
            (1..3).random()
        } else { // 20% de chance
            (4..9).random()
        }
    }

    fun calculateOffset(page: Int): Int {
        val limit = 12
        if (page >= 1) {
            return ((page - 1) * limit)
        } else {
            return 0
        }
    }

    private fun calculateContrast(foreground: Int, background: Int): Double {
        val luminance1 = calculateLuminance(Color(foreground))
        val luminance2 = calculateLuminance(Color(background))
        val lighter = maxOf(luminance1, luminance2)
        val darker = minOf(luminance1, luminance2)
        return (lighter + 0.00005) / (darker + 0.00005)
    }

    private fun getNegativeColor(color: Color): Color {
        val red = 255 - (color.red * 255).toInt()
        val green = 255 - (color.green * 255).toInt()
        val blue = 255 - (color.blue * 255).toInt()

        return Color(red / 255f, green / 255f, blue / 255f, color.alpha)
    }

    fun getRandomPokeImg(pokeId: Int): String {
        val urls = listOf(
            "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/dream-world/${pokeId}.svg",
            "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/${pokeId}.png",
            "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/home/${pokeId}.png"
        )
        return urls.random()
    }

    fun generateUniqueFileName(): String {
        val random = SecureRandom()
        val randomValue = random.nextInt(999999) // Valor aleatório entre 0 e 999999
        return "img_${System.currentTimeMillis()}_$randomValue.png"
    }


    suspend fun downloadAndSaveImageWithCoil(context: Context, imageUrl: String): String {
        // Cria um nome único para o arquivo
        val fileName = generateUniqueFileName()
        val file = File(context.cacheDir, fileName)

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
            (result as android.graphics.drawable.BitmapDrawable).bitmap.compress(
                android.graphics.Bitmap.CompressFormat.PNG,
                100,
                outputStream
            )
        }

        return file.absolutePath // Retorna o caminho local da imagem salva
    }


    fun getTypeColor(type: String, context: Context): Color {
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

    fun getBarColor(barName: String, context: Context): Color {
        return when (barName) {
            "HP" -> Color(ContextCompat.getColor(context, R.color.hp_bar))
            "ATK" -> Color(ContextCompat.getColor(context, R.color.atk_bar))
            "DEF" -> Color(ContextCompat.getColor(context, R.color.def_bar))
            "XP" -> Color(ContextCompat.getColor(context, R.color.exp_bar))
            "SPD" -> Color(ContextCompat.getColor(context, R.color.spd_bar))
            else -> Color.White
        }
    }

    private fun getRandomColor(): Color {
        var red: Int = 0
        var green: Int = 0
        var blue: Int = 0
        var luminance: Double = 1.0
        while (luminance > 0.5) {
            red = Random.nextInt(0, 128) // Valor entre 0 e 255
            green = Random.nextInt(0, 128)
            blue = Random.nextInt(0, 128)
            luminance = calculateLuminance(Color(red, green, blue))
        }
        return Color(red, green, blue)
    }

    private fun calculateLuminance(color: Color): Double {
        // Converte os componentes da cor para o intervalo de 0 a 1
        val r = color.red
        val g = color.green
        val b = color.blue

        // Aplica a transformação para cada componente
        val rLuminance = if (r <= 0.03928) r / 12.92 else ((r + 0.055) / 1.055).pow(2.4)
        val gLuminance = if (g <= 0.03928) g / 12.92 else ((g + 0.055) / 1.055).pow(2.4)
        val bLuminance = if (b <= 0.03928) b / 12.92 else ((b + 0.055) / 1.055).pow(2.4)

        // Calcula a luminância relativa (percebida)
        return round((0.2126 * rLuminance + 0.7152 * gLuminance + 0.0722 * bLuminance) * 100) / 100.0
    }
}