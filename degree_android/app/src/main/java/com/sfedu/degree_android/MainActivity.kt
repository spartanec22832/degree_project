package com.sfedu.degree_android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.sfedu.degree_android.ui.screens.main.MainScreen
import com.sfedu.degree_android.ui.theme.Degree_androidTheme
import dagger.hilt.android.AndroidEntryPoint
import com.yandex.mapkit.MapKitFactory


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Degree_androidTheme {
                MainScreen()
            }
        }
    }
    override fun onStart() {
        super.onStart()
        MapKitFactory.getInstance().onStart()
    }

    override fun onStop() {
        MapKitFactory.getInstance().onStop()
        super.onStop()
    }
}

