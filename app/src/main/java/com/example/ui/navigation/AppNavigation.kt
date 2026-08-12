package com.example.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.data.model.GenerationEntity
import com.example.ui.screens.CreateScreen
import com.example.ui.screens.DetailScreen
import com.example.ui.screens.GalleryScreen
import com.example.ui.screens.PresetsScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberPurple
import com.example.ui.theme.CyberTextSecondary
import com.example.ui.viewmodel.Img2ImgViewModel

@Composable
fun AppNavigation(viewModel: Img2ImgViewModel) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var selectedDetailEntity by remember { mutableStateOf<GenerationEntity?>(null) }

    Scaffold(
        bottomBar = {
            if (selectedDetailEntity == null) {
                NavigationBar(
                    containerColor = com.example.ui.theme.CyberDarkSurface,
                    tonalElevation = 8.dp
                ) {
                    NavigationBarItem(
                        selected = (selectedTab == 0),
                        onClick = { selectedTab = 0 },
                        icon = { Icon(Icons.Default.AutoAwesome, contentDescription = "Studio") },
                        label = { Text("Studio") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.White,
                            selectedTextColor = CyberPurple,
                            indicatorColor = CyberPurple,
                            unselectedIconColor = CyberTextSecondary,
                            unselectedTextColor = CyberTextSecondary
                        )
                    )

                    NavigationBarItem(
                        selected = (selectedTab == 1),
                        onClick = { selectedTab = 1 },
                        icon = { Icon(Icons.Default.PhotoLibrary, contentDescription = "Galeri") },
                        label = { Text("Galeri") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.White,
                            selectedTextColor = CyberPurple,
                            indicatorColor = CyberPurple,
                            unselectedIconColor = CyberTextSecondary,
                            unselectedTextColor = CyberTextSecondary
                        )
                    )

                    NavigationBarItem(
                        selected = (selectedTab == 2),
                        onClick = { selectedTab = 2 },
                        icon = { Icon(Icons.Default.Brush, contentDescription = "Gaya") },
                        label = { Text("Gaya") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.White,
                            selectedTextColor = CyberPurple,
                            indicatorColor = CyberPurple,
                            unselectedIconColor = CyberTextSecondary,
                            unselectedTextColor = CyberTextSecondary
                        )
                    )

                    NavigationBarItem(
                        selected = (selectedTab == 3),
                        onClick = { selectedTab = 3 },
                        icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
                        label = { Text("Pengaturan") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.White,
                            selectedTextColor = CyberPurple,
                            indicatorColor = CyberPurple,
                            unselectedIconColor = CyberTextSecondary,
                            unselectedTextColor = CyberTextSecondary
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        val modifier = Modifier.padding(innerPadding)

        if (selectedDetailEntity != null) {
            DetailScreen(
                entity = selectedDetailEntity!!,
                viewModel = viewModel,
                onBack = { selectedDetailEntity = null }
            )
        } else {
            when (selectedTab) {
                0 -> CreateScreen(
                    viewModel = viewModel,
                    onNavigateToGallery = { selectedTab = 1 }
                )
                1 -> GalleryScreen(
                    viewModel = viewModel,
                    onSelectDetail = { selectedDetailEntity = it },
                    onNavigateToCreate = { selectedTab = 0 }
                )
                2 -> PresetsScreen(
                    viewModel = viewModel,
                    onUsePreset = { selectedTab = 0 }
                )
                3 -> SettingsScreen(viewModel = viewModel)
            }
        }
    }
}
