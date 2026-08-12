package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.ui.navigation.AppNavigation
import com.example.ui.theme.Img2ImgTheme
import com.example.ui.viewmodel.Img2ImgViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: Img2ImgViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Img2ImgTheme {
                AppNavigation(viewModel = viewModel)
            }
        }
    }
}
