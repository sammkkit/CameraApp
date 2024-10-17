package com.example.mlkitoverview

import android.util.Log
import android.widget.Toast
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext

@Composable
fun CameraScreen() {
    val context = LocalContext.current
    var permissionGranted by remember { mutableStateOf(false) }
    RequestPermission { granted ->
        permissionGranted = granted
    }
    if(permissionGranted){

        CameraPreview(
            onImageCaptured = {
                // Handle the captured image URI
//                Toast.makeText(context,"Image Captured",Toast.LENGTH_SHORT).show()
                Log.d("cameraTAG","Captured and saved to ${it?.path}")
            },
            onError = {
                // Handle the error
                Log.d("cameraTAG","Error${it.message}")
            }
        )
    }else{
        Text(text = "Camera permission not granted")
    }
}
