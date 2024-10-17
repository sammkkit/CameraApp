package com.example.mlkitoverview

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

@Composable
fun RequestPermission(onResult: (Boolean)->Unit) {
    val context = LocalContext.current

    val isPermissionGranted = remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context,Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        )

    }

    val requestLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = {
            isPermissionGranted.value = it
            onResult(it)
        }
    )
    LaunchedEffect(Unit) {
        if(!isPermissionGranted.value) {
            requestLauncher.launch(Manifest.permission.CAMERA)
        }else{
            onResult(true)
        }
    }

}