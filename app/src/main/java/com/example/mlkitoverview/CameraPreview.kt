package com.example.mlkitoverview

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.MediaStoreOutputOptions
import androidx.camera.video.Recorder
import androidx.camera.video.VideoCapture
import androidx.camera.video.VideoRecordEvent
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.util.Consumer
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.delay
import java.io.File
import java.util.concurrent.Executor
import java.util.concurrent.Executors


@Composable
fun CameraPreview(
    controller:LifecycleCameraController,
    modifier: Modifier=Modifier
){
    val lifecycleOwner = LocalLifecycleOwner.current
    AndroidView(
        factory = {
            PreviewView(it).apply {
                this.controller = controller
                controller.bindToLifecycle(lifecycleOwner)
            }
        },
        modifier = modifier
    )
}

//
//@Composable
//fun CameraPreview(
//    onImageCaptured: (Uri?) -> Unit,
//    onError: (ImageCaptureException) -> Unit
//){
//    val context = LocalContext.current
//    val lifcycleOwner = LocalLifecycleOwner.current
//    val executer = Executors.newSingleThreadExecutor()
//    var cameraProviderFuture = ProcessCameraProvider.getInstance(context)
//    val imageCapture = remember { ImageCapture.Builder().build()}
//    val previewView1 = remember {
//        PreviewView(context)
//    }
//    var isCaptured by remember { mutableStateOf(false) }
//    var frontCamera by remember { mutableStateOf(false) }
//    val alpha by animateDpAsState(
//        targetValue = if (isCaptured) 40.dp else 20.dp,
//        animationSpec = tween(durationMillis = 100)
//    )
//    val videoCapture: VideoCapture<Recorder> ? = remember { null }
//    var isVideoRecording by remember { mutableStateOf(false) }
//    val alphaVideo by animateDpAsState(
//        targetValue = if (isVideoRecording) 40.dp else 20.dp,
//        animationSpec = tween(durationMillis = 300)
//    )
//    val mediaPlayer = remember {
//        MediaPlayer.create(context, R.raw.camera_shutter_sound) // Add sound file in res/raw
//    }
//    LaunchedEffect(frontCamera) {
////        delay(200)
//        val cameraProvider = cameraProviderFuture.get()
//        val cameraSelector = CameraSelector.Builder()
//            .requireLensFacing(
//                if (frontCamera) CameraSelector.LENS_FACING_FRONT
//                else CameraSelector.LENS_FACING_BACK
//            )
//            .build()
//
//        val preview = Preview.Builder().build().also {
//            it.setSurfaceProvider(previewView1.surfaceProvider)
//        }
//        cameraProvider.unbindAll()
//        cameraProvider.bindToLifecycle(
//            lifcycleOwner,
//            cameraSelector,
//            preview,
//            imageCapture
//        )
//
//    }
//    Box(modifier = Modifier.fillMaxSize()) {
//        // Camera Preview
//        AndroidView(
//            modifier = Modifier.fillMaxSize(),
//            factory = { previewView1 }
//        )
//
//        // Camera Flip Button at Top Left
//        Button(
//            onClick = { frontCamera = !frontCamera },
//            modifier = Modifier
//                .padding(top = 48.dp, start = 32.dp)
//                .align(Alignment.TopStart)
//        ) {
//            Icon(
//                painter = painterResource(R.drawable.camera_flip),
//                contentDescription = null,
//                modifier = Modifier.size(24.dp)
//            )
//        }
//
//        // Video and Capture Buttons at the Bottom
//        Row(
//            modifier = Modifier
//                .align(Alignment.BottomCenter)
//                .padding(bottom = 80.dp),
//            horizontalArrangement = Arrangement.Center
//        ) {
//            // Video Button
//            Button(
//                onClick = {
//
//                },
//                modifier = Modifier.padding(start = 16.dp)
//            ) {
//                Icon(
//                    painter = painterResource(R.drawable.video_camera),
//                    contentDescription = null,
//                    modifier = Modifier.size(alphaVideo)
//                )
//            }
//
//            // Capture Button
//            Button(
//                onClick = {
//                    isCaptured = true
//                    mediaPlayer.start()
//                    captureImage(
//                        context, imageCapture, executer, { uri ->
//                            onImageCaptured(uri)
//                            isCaptured = false
//                        },
//                        onError
//                    )
//                },
//                modifier = Modifier.padding(start = 16.dp)
//            ) {
//                Icon(
//                    painter = painterResource(R.drawable.capture),
//                    contentDescription = null,
//                    modifier = Modifier.size(alpha)
//                )
//            }
//        }
//    }
//
//
//}
//fun captureImage(
//    context: Context,
//    imageCapture: ImageCapture,
//    executer: Executor,
//    onImageCaptured: (Uri?) -> Unit,
//    onError: (ImageCaptureException) -> Unit
//){
//    val directoryName  = "AndroidProject"
//    val directory = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), directoryName)
//    if (!directory.exists()) {
//        directory.mkdirs() // Create the directory
//    }
////    val file = File(context.externalMediaDirs.first(),"${System.currentTimeMillis()}BySam.jpg")
//    val file = File(directory, "${System.currentTimeMillis()}BySam.jpg")
//    val output = ImageCapture.OutputFileOptions.Builder(file).build()
//    imageCapture.takePicture(
//        output,
//        executer,
//        object: ImageCapture.OnImageSavedCallback{
//            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
////                MediaScannerConnection.scanFile(context, arrayOf(file.absolutePath), null, null) // Scan the file
//
//                onImageCaptured(Uri.fromFile(file))
//            }
//
//            override fun onError(exception: ImageCaptureException) {
//                onError(exception)
//            }
//
//        }
//    )
//}

