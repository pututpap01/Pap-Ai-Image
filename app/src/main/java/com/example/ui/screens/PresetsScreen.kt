package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberPink
import com.example.ui.theme.CyberPurple
import com.example.ui.theme.CyberTextSecondary
import com.example.ui.viewmodel.Img2ImgViewModel

data class PresetStyleItem(
    val name: String,
    val description: String,
    val samplePrompt: String,
    val categoryTag: String
)

@Composable
fun PresetsScreen(
    viewModel: Img2ImgViewModel,
    onUsePreset: () -> Unit
) {
    val presets = listOf(
        PresetStyleItem(
            name = "Anime / Manga",
            description = "Mengubah foto menjadi seni animasi Jepang berwarna warni dengan garis bersih.",
            samplePrompt = "masterpiece anime art style, vibrant colors, clean lineart, Japanese animation visual aesthetic, Makoto Shinkai style",
            categoryTag = "Populer"
        ),
        PresetStyleItem(
            name = "Cyberpunk",
            description = "Transformasi ke gaya kota masa depan penuh dengan efek lampu neon dan elemen cybernetic.",
            samplePrompt = "cyberpunk aesthetic, glowing neon lights, futuristic metallic armor, high contrast synthwave colors, sci-fi concept art",
            categoryTag = "Futuristik"
        ),
        PresetStyleItem(
            name = "Realistic Portrait",
            description = "Meningkatkan ketajaman foto menjadi kualitas studio fotografi profesional 8k.",
            samplePrompt = "ultra realistic 8k photograph, sharp focus, professional studio lighting, portrait lens 85mm, natural textures",
            categoryTag = "Fotografi"
        ),
        PresetStyleItem(
            name = "Fantasy Oil Painting",
            description = "Lukisan minyak klasik ala Renaissance dengan pencahayaan dramatis dan tekstur kanvas.",
            samplePrompt = "dramatic Renaissance oil painting on canvas, expressive brush strokes, rich classical color palette, epic fantasy masterwork",
            categoryTag = "Seni Klasik"
        ),
        PresetStyleItem(
            name = "3D Render",
            description = "Membuat efek karakter 3D kartun bergaya animasi Pixar / Octane Render.",
            samplePrompt = "3D digital sculpture, Octane Render, smooth volumetric lighting, vibrant 3D character animation style",
            categoryTag = "Digital 3D"
        ),
        PresetStyleItem(
            name = "Dark Gothic",
            description = "Nuansa gelap misterius ala kastil gothic dengan detail arsitektur kuno.",
            samplePrompt = "dark gothic aesthetic, moody atmospheric shadows, intricate dark fantasy details, Victorian gothic mood",
            categoryTag = "Gothic"
        ),
        PresetStyleItem(
            name = "Watercolor",
            description = "Seni cat air lembut dengan efek cipratan tinta dan warna pastel.",
            samplePrompt = "dreamy pastel watercolor painting, fluid ink washes, expressive artistic paper texture, soft aesthetic colors",
            categoryTag = "Artistic"
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Brush,
                contentDescription = null,
                tint = CyberPurple,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Koleksi Preset & Gaya AI",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        Text(
            text = "Pilih gaya favoritmu untuk langsung diterapkan ke Generator Img2Img",
            fontSize = 12.sp,
            color = CyberTextSecondary,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            items(presets) { preset ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = preset.name,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onBackground
                            )

                            Surface(
                                color = CyberCyan.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = preset.categoryTag,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = CyberCyan,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = preset.description,
                            fontSize = 12.sp,
                            color = CyberTextSecondary
                        )

                        Spacer(modifier = Modifier.height(8.dp))
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Prompt: \"${preset.samplePrompt}\"",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(8.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = {
                                viewModel.setStyle(preset.name)
                                viewModel.setPrompt(preset.samplePrompt)
                                onUsePreset()
                            },
                            modifier = Modifier.align(Alignment.End),
                            colors = ButtonDefaults.buttonColors(containerColor = CyberPurple),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Gunakan Gaya Ini", fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }
}
