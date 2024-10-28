package com.example.mlkitoverview.Navigation


import android.content.Context
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.mlkitoverview.CameraScreen
import com.example.mlkitoverview.landmarkML.CameraScreenML

@Composable
fun NavGraph(
    context: Context,
    navController: NavHostController
) {
    NavHost(
        navController = navController ,
        startDestination = "camera_screen"
    ) {
        composable("camera_screen") {
            CameraScreen(context,navController)
        }
        composable("ml_screen") {
            CameraScreenML(context)
        }
    }
}