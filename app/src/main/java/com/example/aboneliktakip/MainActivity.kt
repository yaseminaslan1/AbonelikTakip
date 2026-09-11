package com.example.aboneliktakip

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.aboneliktakip.ui.theme.AbonelikTakipTheme
import com.example.aboneliktakip.viewmodel.SubscriptionViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            AbonelikTakipTheme {
                val viewModel: SubscriptionViewModel = viewModel()
                HomeScreen(viewModel = viewModel)
            }
        }
    }
}