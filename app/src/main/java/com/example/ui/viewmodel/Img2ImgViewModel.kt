package com.example.ui.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.GenerationEntity
import com.example.data.repository.GenerationRepository
import com.example.data.repository.GenerationResult
import com.example.data.repository.ImageGeneratorEngine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed class UiGenerationState {
    object Idle : UiGenerationState()
    data class Loading(val progressMessage: String) : UiGenerationState()
    data class Success(val generation: GenerationEntity) : UiGenerationState()
    data class Error(val message: String) : UiGenerationState()
}

class Img2ImgViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: GenerationRepository
    private val generatorEngine: ImageGeneratorEngine

    init {
        val dao = AppDatabase.getDatabase(application).generationDao()
        repository = GenerationRepository(dao)
        generatorEngine = ImageGeneratorEngine(application)
    }

    // Input States
    val selectedImageUri = MutableStateFlow<Uri?>(null)
    val selectedBitmap = MutableStateFlow<Bitmap?>(null)
    val prompt = MutableStateFlow("")
    val negativePrompt = MutableStateFlow("")
    val selectedStyle = MutableStateFlow("General Art")
    val denoisingStrength = MutableStateFlow(0.75f)
    val aspectRatio = MutableStateFlow("1:1")
    val engineMode = MutableStateFlow("POLLINATIONS") // Default Pollinations for free uncensored open engine
    val isUncensoredMode = MutableStateFlow(true)
    val customApiKey = MutableStateFlow("")

    // Generation UI State
    private val _generationState = MutableStateFlow<UiGenerationState>(UiGenerationState.Idle)
    val generationState: StateFlow<UiGenerationState> = _generationState.asStateFlow()

    // Gallery Filter Query
    val gallerySearchQuery = MutableStateFlow("")
    val galleryFilterFavoriteOnly = MutableStateFlow(false)

    // DB Flow
    val generations: StateFlow<List<GenerationEntity>> = combine(
        repository.allGenerations,
        gallerySearchQuery,
        galleryFilterFavoriteOnly
    ) { list, query, favOnly ->
        list.filter { entity ->
            val matchesQuery = query.isBlank() || entity.prompt.contains(query, ignoreCase = true) || entity.styleName.contains(query, ignoreCase = true)
            val matchesFav = !favOnly || entity.isFavorite
            matchesQuery && matchesFav
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun onImageSelected(uri: Uri?, bitmap: Bitmap? = null) {
        selectedImageUri.value = uri
        selectedBitmap.value = bitmap
    }

    fun setPrompt(text: String) {
        prompt.value = text
    }

    fun setNegativePrompt(text: String) {
        negativePrompt.value = text
    }

    fun setStyle(style: String) {
        selectedStyle.value = style
    }

    fun setDenoising(value: Float) {
        denoisingStrength.value = value
    }

    fun setAspectRatio(ratio: String) {
        aspectRatio.value = ratio
    }

    fun setEngine(engine: String) {
        engineMode.value = engine
    }

    fun toggleUncensored(enabled: Boolean) {
        isUncensoredMode.value = enabled
    }

    fun setCustomApiKey(key: String) {
        customApiKey.value = key
    }

    fun generateImage() {
        if (prompt.value.isBlank() && selectedBitmap.value == null && selectedImageUri.value == null) {
            _generationState.value = UiGenerationState.Error("Masukkan petunjuk deskripsi atau pilih foto masukan terlebih dahulu.")
            return
        }

        viewModelScope.launch {
            _generationState.value = UiGenerationState.Loading("Sedang memproses gambar dengan AI Engine...")

            val originalPath = if (selectedBitmap.value != null) {
                val savedOriginal = generatorEngine.saveBitmapToDisk(selectedBitmap.value!!)
                savedOriginal.absolutePath
            } else {
                selectedImageUri.value?.toString() ?: ""
            }

            val result = generatorEngine.generateImg2Img(
                originalImageUri = selectedImageUri.value,
                originalBitmap = selectedBitmap.value,
                prompt = prompt.value,
                negativePrompt = negativePrompt.value,
                styleName = selectedStyle.value,
                denoisingStrength = denoisingStrength.value,
                aspectRatio = aspectRatio.value,
                engineMode = engineMode.value,
                isUncensoredMode = isUncensoredMode.value,
                customApiKey = customApiKey.value
            )

            when (result) {
                is GenerationResult.Success -> {
                    val newEntity = GenerationEntity(
                        title = if (prompt.value.isNotBlank()) prompt.value.take(30) else "Karya Img2Img AI",
                        prompt = prompt.value,
                        negativePrompt = negativePrompt.value,
                        originalImagePath = originalPath,
                        generatedImagePath = result.generatedImagePath,
                        styleName = selectedStyle.value,
                        denoisingStrength = denoisingStrength.value,
                        aspectRatio = aspectRatio.value,
                        engineUsed = engineMode.value,
                        timestamp = System.currentTimeMillis()
                    )

                    val savedId = repository.insert(newEntity)
                    val insertedEntity = newEntity.copy(id = savedId)

                    _generationState.value = UiGenerationState.Success(insertedEntity)
                }
                is GenerationResult.Error -> {
                    _generationState.value = UiGenerationState.Error(result.message)
                }
            }
        }
    }

    fun toggleFavorite(entity: GenerationEntity) {
        viewModelScope.launch {
            repository.update(entity.copy(isFavorite = !entity.isFavorite))
        }
    }

    fun deleteGeneration(entity: GenerationEntity) {
        viewModelScope.launch {
            repository.delete(entity)
        }
    }

    fun resetState() {
        _generationState.value = UiGenerationState.Idle
    }
}
