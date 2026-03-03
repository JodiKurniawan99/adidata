package com.example.adidata

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.adidata.ui.theme.AdidataTheme

class MainActivity : ComponentActivity() {
    private val occupationFormViewModel: OccupationFormViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AdidataTheme {
                OccupationFormScreen(viewModel = occupationFormViewModel)
            }
        }
    }
}
