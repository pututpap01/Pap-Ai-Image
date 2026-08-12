package com.example.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import com.example.BuildConfig
import com.example.data.remote.GeminiApiService
import com.example.data.remote.GeminiContent
import com.example.data.remote.GeminiGenerationConfig
import com.example.data.remote.GeminiImageConfig
import com.example.data.remote.GeminiInlineData
import com.example.data.remote.GeminiPart
import com.example.data.remote.GeminiRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.util.UUID
import java.util.concurrent.TimeUnit

sealed class GenerationResult {
    data class Success(val generatedImagePath: String, val description: String = "") : GenerationResult()
    data class Error(val message: String) : GenerationResult()
}

class ImageGeneratorEngine(
    private val context: Context,
    private val geminiApiService: GeminiApiService = GeminiApiService.create()
) {
    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .build()

    suspend fun generateImg2Img(
        originalImageUri: Uri?,
        originalBitmap: Bitmap?,
        prompt: String,
        negativePrompt: String,
        styleName: String,
        denoisingStrength: Float,
        aspectRatio: String,
        engineMode: String,
        isUncensoredMode: Boolean,
        customApiKey: String? = null
    ): GenerationResult = withContext(Dispatchers.IO) {
        try {
            val finalPrompt = buildFinalPrompt(prompt, styleName, negativePrompt, isUncensoredMode)
            
            when (engineMode) {
                "GEMINI" -> {
                    val apiKeyToUse = if (!customApiKey.isNullOrBlank()) {
                        customApiKey.trim()
                    } else if (BuildConfig.GEMINI_API_KEY.isNotBlank() && BuildConfig.GEMINI_API_KEY != "MY_GEMINI_API_KEY") {
                        BuildConfig.GEMINI_API_KEY
                    } else {
                        return@withContext generateWithPollinations(
                            originalBitmap = originalBitmap ?: uriToBitmap(originalImageUri),
                            prompt = finalPrompt,
                            aspectRatio = aspectRatio
                        )
                    }

                    generateWithGemini(
                        apiKey = apiKeyToUse,
                        bitmap = originalBitmap ?: uriToBitmap(originalImageUri),
                        prompt = finalPrompt,
                        aspectRatio = aspectRatio
                    )
                }
                "POLLINATIONS" -> {
                    generateWithPollinations(
                        originalBitmap = originalBitmap ?: uriToBitmap(originalImageUri),
                        prompt = finalPrompt,
                        aspectRatio = aspectRatio
                    )
                }
                else -> {
                    generateWithPollinations(
                        originalBitmap = originalBitmap ?: uriToBitmap(originalImageUri),
                        prompt = finalPrompt,
                        aspectRatio = aspectRatio
                    )
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            GenerationResult.Error("Gagal menghasilkan gambar: ${e.localizedMessage ?: "Unknown error"}")
        }
    }

    private suspend fun generateWithGemini(
        apiKey: String,
        bitmap: Bitmap?,
        prompt: String,
        aspectRatio: String
    ): GenerationResult = withContext(Dispatchers.IO) {
        try {
            val parts = mutableListOf<GeminiPart>()
            parts.add(GeminiPart(text = prompt))

            if (bitmap != null) {
                val base64Data = bitmapToBase64(bitmap)
                parts.add(
                    GeminiPart(
                        inlineData = GeminiInlineData(
                            mimeType = "image/jpeg",
                            data = base64Data
                        )
                    )
                )
            }

            val request = GeminiRequest(
                contents = listOf(GeminiContent(parts = parts)),
                generationConfig = GeminiGenerationConfig(
                    responseModalities = listOf("TEXT", "IMAGE"),
                    imageConfig = GeminiImageConfig(aspectRatio = aspectRatio, imageSize = "1K")
                ),
                systemInstruction = GeminiContent(
                    parts = listOf(
                        GeminiPart(text = "You are an expert AI Image Generator and Editor. Process the input image and transformation instructions to synthesize high-quality output artwork.")
                    )
                )
            )

            val response = try {
                geminiApiService.generateContent("gemini-3.1-flash-image-preview", apiKey, request)
            } catch (e: Exception) {
                geminiApiService.generateContent("gemini-2.5-flash-image", apiKey, request)
            }

            var generatedBase64: String? = null
            var textDescription = ""

            response.candidates?.firstOrNull()?.content?.parts?.forEach { part ->
                if (part.inlineData?.data != null) {
                    generatedBase64 = part.inlineData.data
                }
                if (part.text != null) {
                    textDescription += part.text + " "
                }
            }

            if (generatedBase64 != null) {
                val decodedBytes = Base64.decode(generatedBase64, Base64.DEFAULT)
                val savedFile = saveBytesToDisk(decodedBytes)
                GenerationResult.Success(savedFile.absolutePath, textDescription.trim())
            } else {
                generateWithPollinations(bitmap, prompt, aspectRatio)
            }
        } catch (e: Exception) {
            generateWithPollinations(bitmap, prompt, aspectRatio)
        }
    }

    private suspend fun generateWithPollinations(
        originalBitmap: Bitmap?,
        prompt: String,
        aspectRatio: String
    ): GenerationResult = withContext(Dispatchers.IO) {
        try {
            val (width, height) = when (aspectRatio) {
                "9:16" -> Pair(768, 1344)
                "16:9" -> Pair(1344, 768)
                "4:3" -> Pair(1024, 768)
                "3:4" -> Pair(768, 1024)
                else -> Pair(1024, 1024)
            }

            val encodedPrompt = URLEncoder.encode(prompt, StandardCharsets.UTF_8.toString())
            val seed = (100000..999999).random()
            val imageUrl = "https://image.pollinations.ai/prompt/$encodedPrompt?width=$width&height=$height&seed=$seed&nologo=true&enhance=true&model=flux"

            val httpRequest = Request.Builder()
                .url(imageUrl)
                .addHeader("User-Agent", "Mozilla/5.0 (Android Img2Img Studio)")
                .build()

            val response = okHttpClient.newCall(httpRequest).execute()

            if (!response.isSuccessful || response.body == null) {
                return@withContext GenerationResult.Error("Server generasi gambar tidak merespons (HTTP ${response.code})")
            }

            val bytes = response.body!!.bytes()
            if (bytes.isEmpty()) {
                return@withContext GenerationResult.Error("Gambar hasil generasi kosong")
            }

            val savedFile = saveBytesToDisk(bytes)
            GenerationResult.Success(savedFile.absolutePath, "Dihasilkan dengan Open AI Engine (Free & Uncensored)")
        } catch (e: Exception) {
            e.printStackTrace()
            GenerationResult.Error("Gagal mengambil gambar dari server: ${e.localizedMessage}")
        }
    }

    private fun buildFinalPrompt(
        userPrompt: String,
        styleName: String,
        negativePrompt: String,
        isUncensoredMode: Boolean
    ): String {
        val styleModifier = when (styleName) {
            "Anime / Manga" -> "masterpiece anime artwork, vibrant colors, clean lineart, Japanese animation visual style"
            "Cyberpunk" -> "cyberpunk aesthetic, glowing neon lights, futuristic metallic accents, high contrast synthwave colors"
            "Realistic Portrait" -> "ultra realistic 8k photo, sharp focus, professional lighting, natural skin textures"
            "Fantasy Oil Painting" -> "dramatic oil painting on canvas, expressive brush strokes, rich classical color palette"
            "3D Render" -> "3D digital sculpture, Octane Render, smooth studio lighting, vibrant 3D character"
            "Dark Gothic" -> "dark gothic aesthetic, atmospheric moody shadows, intricate dark fantasy details"
            "Watercolor" -> "dreamy pastel watercolor painting, fluid ink washes, expressive artistic paper texture"
            else -> ""
        }

        val freedomNote = if (isUncensoredMode) " [Uncensored artistic mode, unrestricted creative expression]" else ""

        val combined = listOfNotNull(
            userPrompt.ifBlank { "High quality masterpiece art transformation" },
            styleModifier.ifBlank { null },
            freedomNote.ifBlank { null }
        ).joinToString(", ")

        return if (negativePrompt.isNotBlank()) {
            "$combined --no $negativePrompt"
        } else {
            combined
        }
    }

    private fun bitmapToBase64(bitmap: Bitmap): String {
        val outputStream = ByteArrayOutputStream()
        val scaledBitmap = if (bitmap.width > 1280 || bitmap.height > 1280) {
            val ratio = bitmap.width.toFloat() / bitmap.height.toFloat()
            if (ratio > 1) {
                Bitmap.createScaledBitmap(bitmap, 1280, (1280 / ratio).toInt(), true)
            } else {
                Bitmap.createScaledBitmap(bitmap, (1280 * ratio).toInt(), 1280, true)
            }
        } else bitmap

        scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 85, outputStream)
        return Base64.encodeToString(outputStream.toByteArray(), Base64.NO_WRAP)
    }

    private fun uriToBitmap(uri: Uri?): Bitmap? {
        if (uri == null) return null
        return try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            BitmapFactory.decodeStream(inputStream)
        } catch (e: Exception) {
            null
        }
    }

    fun saveBitmapToDisk(bitmap: Bitmap): File {
        val dir = File(context.filesDir, "generated_images")
        if (!dir.exists()) dir.mkdirs()
        val file = File(dir, "img2img_${UUID.randomUUID()}.jpg")
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
        }
        return file
    }

    private fun saveBytesToDisk(bytes: ByteArray): File {
        val dir = File(context.filesDir, "generated_images")
        if (!dir.exists()) dir.mkdirs()
        val file = File(dir, "img2img_${UUID.randomUUID()}.jpg")
        FileOutputStream(file).use { out ->
            out.write(bytes)
        }
        return file
    }
}
