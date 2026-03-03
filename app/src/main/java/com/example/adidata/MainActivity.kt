package com.example.adidata

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.adidata.occupation.OccupationFormRoute
import com.example.adidata.ui.theme.AdidataTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AdidataTheme {
                OccupationFormRoute()
            }
        }
    }
}
