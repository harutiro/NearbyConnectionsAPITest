package net.harutiro.nearbyconnectionsapitest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import net.harutiro.nearbyconnectionsapitest.ui.theme.NearbyConnectionsAPITestTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NearbyConnectionsAPITestTheme {
                val viewModel = MainViewModel(this)
                MainScreen(viewModel)
            }
        }
    }
}