package com.example.ui.screens

import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.ui.components.ImageComparisonSlider
import androidx.compose.ui.graphics.asImageBitmap
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberGold
import com.example.ui.theme.CyberGreen
import com.example.ui.theme.CyberPink
import com.example.ui.theme.CyberPurple
import com.example.ui.theme.CyberPurpleLight
import com.example.ui.theme.CyberTextSecondary
import com.example.ui.viewmodel.Img2ImgViewModel
import com.example.ui.viewmodel.UiGenerationState

@Composable
fun CreateScreen(
    viewModel: Img2ImgViewModel,
    onNavigateToGallery: () -> Unit
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    val selectedUri by viewModel.selectedImageUri.collectAsState()
    val selectedBitmap by viewModel.selectedBitmap.collectAsState()
    val promptText by viewModel.prompt.collectAsState()
    val negativePromptText by viewModel.negativePrompt.collectAsState()
    val currentStyle by viewModel.selectedStyle.collectAsState()
    val denoisingVal by viewModel.denoisingStrength.collectAsState()
    val selectedRatio by viewModel.aspectRatio.collectAsState()
    val currentEngine by viewModel.engineMode.collectAsState()
    val isUncensored by viewModel.isUncensoredMode.collectAsState()
    val genState by viewModel.generationState.collectAsState()

    var showNegativePrompt by remember { mutableStateOf(false) }

    // Launcher for image picker
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            viewModel.onImageSelected(uri, null)
        }
    }

    val styleOptions = listOf(
        "General Art",
        "Anime / Manga",
        "Cyberpunk",
        "Realistic Portrait",
        "Fantasy Oil Painting",
        "3D Render",
        "Dark Gothic",
        "Watercolor"
    )

    val aspectRatios = listOf("1:1", "9:16", "16:9", "4:3", "3:4")

    val samplePrompts = listOf(
        "Ubah menjadi karakter anime futuristik dengan aura glowing",
        "Transformasi ke lukisan minyak Renaissance klasik",
        "Jadikan latar belakang kota cyberpunk malam hari penuh neon",
        "Mode photorealistic 8k studio lighting portrait",
        "Gaya patung 3D render digital dengan warna neon cerah"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
            .verticalScroll(scrollState)
    ) {
        // Banner Header
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                CyberPurple.copy(alpha = 0.3f),
                                CyberCyan.copy(alpha = 0.2f)
                            )
                        )
                    )
                    .padding(16.dp)
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = CyberPurpleLight,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Img2Img AI Studio",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }

                        Surface(
                            color = CyberPink.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(20.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, CyberPink)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LockOpen,
                                    contentDescription = null,
                                    tint = CyberPink,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Bebas & Uncensored",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = CyberPink
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Ubah foto apa saja menjadi artwork AI kustom tanpa batas.",
                        fontSize = 12.sp,
                        color = CyberTextSecondary
                    )
                }
            }
        }

        // 1. Gambar Input Section
        Text(
            text = "1. Pilihan Gambar Masukan (Input Image)",
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                if (selectedUri == null && selectedBitmap == null) {
                    // Empty Input Placeholder
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .border(
                                1.dp,
                                Brush.linearGradient(listOf(CyberPurple, CyberCyan)),
                                RoundedCornerShape(12.dp)
                            )
                            .clickable { galleryLauncher.launch("image/*") },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.AddPhotoAlternate,
                                contentDescription = "Pilih Foto",
                                tint = CyberPurpleLight,
                                modifier = Modifier.size(44.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Pilih Foto dari Galeri",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Text(
                                text = "atau gunakan sampel bawaan di bawah",
                                fontSize = 11.sp,
                                color = CyberTextSecondary
                            )
                        }
                    }
                } else {
                    // Preview Input Image
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(12.dp))
                    ) {
                        if (selectedBitmap != null) {
                            Image(
                                bitmap = selectedBitmap!!.asImageBitmap(),
                                contentDescription = "Input Preview",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else if (selectedUri != null) {
                            AsyncImage(
                                model = selectedUri,
                                contentDescription = "Input Preview",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }

                        // Clear Image Button
                        IconButton(
                            onClick = { viewModel.onImageSelected(null, null) },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(8.dp)
                                .background(Color.Black.copy(alpha = 0.7f), CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Hapus Gambar",
                                tint = Color.White
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Quick Preset Samples
                Text(
                    text = "Atau coba sampel cepat:",
                    fontSize = 12.sp,
                    color = CyberTextSecondary,
                    modifier = Modifier.padding(bottom = 6.dp)
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = {
                            val bmp = BitmapFactory.decodeResource(context.resources, R.drawable.sample_portrait_1786555887961)
                            viewModel.onImageSelected(null, bmp)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Model Portrait", fontSize = 11.sp)
                    }

                    Button(
                        onClick = {
                            val bmp = BitmapFactory.decodeResource(context.resources, R.drawable.sample_cyberpunk_1786555901455)
                            viewModel.onImageSelected(null, bmp)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Cyberpunk Night", fontSize = 11.sp)
                    }
                }
            }
        }

        // 2. Preset Style Chips
        Text(
            text = "2. Pilih Gaya Seni (Preset Style)",
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            items(styleOptions) { style ->
                FilterChip(
                    selected = (style == currentStyle),
                    onClick = { viewModel.setStyle(style) },
                    label = { Text(style, fontSize = 12.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = CyberPurple,
                        selectedLabelColor = Color.White
                    )
                )
            }
        }

        // 3. Prompt Input
        Text(
            text = "3. Deskripsi Transformasi (Prompt)",
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        OutlinedTextField(
            value = promptText,
            onValueChange = { viewModel.setPrompt(it) },
            placeholder = { Text("Contoh: Ubah gaya rambut jadi neon cyan, tambahkan kacamata cyberpunk...", fontSize = 12.sp) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = CyberPurple,
                unfocusedBorderColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            trailingIcon = {
                IconButton(onClick = {
                    viewModel.setPrompt(samplePrompts.random())
                }) {
                    Icon(
                        imageVector = Icons.Default.Psychology,
                        contentDescription = "Ide Prompt",
                        tint = CyberCyan
                    )
                }
            }
        )

        // Negative Prompt Collapsible
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clickable { showNegativePrompt = !showNegativePrompt }
                .padding(vertical = 4.dp)
        ) {
            Icon(
                imageVector = if (showNegativePrompt) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                contentDescription = null,
                tint = CyberTextSecondary
            )
            Text(
                text = "Negative Prompt (Hal yang ingin dihindari)",
                fontSize = 12.sp,
                color = CyberTextSecondary
            )
        }

        AnimatedVisibility(visible = showNegativePrompt) {
            OutlinedTextField(
                value = negativePromptText,
                onValueChange = { viewModel.setNegativePrompt(it) },
                placeholder = { Text("Contoh: blurry, bad anatomy, low quality", fontSize = 11.sp) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp, bottom = 12.dp),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = CyberPink,
                    unfocusedBorderColor = MaterialTheme.colorScheme.surfaceVariant
                )
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 4. Parameter Controls (Denoising Strength, Aspect Ratio, Engine)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Pengaruh Gambar Asli vs Prompt",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp
                    )
                    Text(
                        text = "${(denoisingVal * 100).toInt()}%",
                        color = CyberPurpleLight,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }

                Slider(
                    value = denoisingVal,
                    onValueChange = { viewModel.setDenoising(it) },
                    valueRange = 0.1f..1.0f,
                    colors = SliderDefaults.colors(
                        thumbColor = CyberPurple,
                        activeTrackColor = CyberPurple
                    )
                )

                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Mirip Asli (Soft)", fontSize = 10.sp, color = CyberTextSecondary)
                    Text("Ubah Total (Bold)", fontSize = 10.sp, color = CyberTextSecondary)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Aspect Ratio Selector
                Text(
                    text = "Rasio Gambar (Aspect Ratio)",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    aspectRatios.forEach { ratio ->
                        FilterChip(
                            selected = (selectedRatio == ratio),
                            onClick = { viewModel.setAspectRatio(ratio) },
                            label = { Text(ratio, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = CyberCyan,
                                selectedLabelColor = Color.Black
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Engine & Uncensored Toggle
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        Text(
                            text = "Mode Uncensored (Kreasi Bebas)",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp
                        )
                        Text(
                            text = "Tanpa batasan filter ketat",
                            fontSize = 10.sp,
                            color = CyberTextSecondary
                        )
                    }

                    Switch(
                        checked = isUncensored,
                        onCheckedChange = { viewModel.toggleUncensored(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = CyberPink,
                            checkedTrackColor = CyberPink.copy(alpha = 0.4f)
                        )
                    )
                }
            }
        }

        // 5. Action Generate Button
        Button(
            onClick = { viewModel.generateImage() },
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Unspecified)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.horizontalGradient(listOf(CyberPurple, CyberCyan)),
                        RoundedCornerShape(16.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.FlashOn,
                        contentDescription = null,
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "HASILKAN GAMBAR AI",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color.White
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Loading & Result Display
        when (val state = genState) {
            is UiGenerationState.Loading -> {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(color = CyberPurple)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = state.progressMessage,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
            }

            is UiGenerationState.Error -> {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    colors = CardDefaults.cardColors(containerColor = CyberPink.copy(alpha = 0.15f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Error Generasi",
                            fontWeight = FontWeight.Bold,
                            color = CyberPink,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = state.message,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
            }

            is UiGenerationState.Success -> {
                val entity = state.generation
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Hasil AI Selesai!",
                                fontWeight = FontWeight.Bold,
                                color = CyberGreen,
                                fontSize = 15.sp
                            )

                            IconButton(onClick = { viewModel.toggleFavorite(entity) }) {
                                Icon(
                                    imageVector = if (entity.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                    contentDescription = "Favorit",
                                    tint = if (entity.isFavorite) CyberPink else CyberTextSecondary
                                )
                            }
                        }

                        Text(
                            text = "Geser slider untuk melihat perbandingan Asli vs AI:",
                            fontSize = 11.sp,
                            color = CyberTextSecondary,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        // Interactive Comparison Slider Component
                        ImageComparisonSlider(
                            originalImage = entity.originalImagePath,
                            generatedImage = entity.generatedImagePath,
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1f)
                                .clip(RoundedCornerShape(12.dp))
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Button(
                                onClick = onNavigateToGallery,
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                            ) {
                                Text("Lihat di Galeri", fontSize = 12.sp)
                            }

                            Button(
                                onClick = { viewModel.generateImage() },
                                colors = ButtonDefaults.buttonColors(containerColor = CyberPurple)
                            ) {
                                Text("Buat Lagi", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            UiGenerationState.Idle -> {
                // Idle state, no extra UI needed
            }
        }
    }
}
