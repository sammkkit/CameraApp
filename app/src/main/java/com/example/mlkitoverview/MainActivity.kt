package com.example.mlkitoverview

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BlendMode
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.camera.core.CameraInfo
import androidx.camera.core.CameraSelector
import androidx.camera.core.CameraSelector.LensFacing
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mlkitoverview.ui.theme.MLkitOverviewTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MLkitOverviewTheme {
                val scope = rememberCoroutineScope()
                var isPermissionGranted by remember { mutableStateOf(true) }
                RequestPermission { permission->
                    isPermissionGranted = permission
                }
                if(isPermissionGranted == false){
                    //do something
                }
                val scaffoldState = rememberBottomSheetScaffoldState()
                val controller = remember {
                    LifecycleCameraController(applicationContext).apply {
                        setEnabledUseCases(
                            CameraController.IMAGE_CAPTURE or
                                    CameraController.VIDEO_CAPTURE
                        )
                        cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                    }
                }

                val viewModel :MainViewModel = viewModel()
                val bitmaps by viewModel.bitmaps.collectAsState()
                var isFlipped by remember { mutableStateOf(false) }
                val rotationAngle by animateFloatAsState(
                    targetValue = if (isFlipped) 180f else 0f
                )
                var isAnimatingBorder by remember { mutableStateOf(false) }
                BottomSheetScaffold(
                    scaffoldState = scaffoldState,
                    sheetPeekHeight = 0.dp,
                    modifier = Modifier.fillMaxSize(),
                    sheetContent = {
                        PhotoBottomSheetContent(
                            bitmaps = bitmaps,applicationContext,
                            mainViewModel = viewModel,
                            Modifier.height(500.dp),

                        )
                    }
                ) { padding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding)
                            .border(
                                2.dp,
                                if (isAnimatingBorder) Color.Red else Color.White
                                , RoundedCornerShape(40.dp))
                    ) {
                        CameraPreview(
                            controller = controller,
                            modifier = Modifier
                                .fillMaxSize()

                        )
                        IconButton(
                            onClick = {
//                                frontCamera.value = !frontCamera.value
                                isFlipped = !isFlipped
                                controller.cameraSelector =
                                    if (controller.cameraSelector == CameraSelector.DEFAULT_FRONT_CAMERA) {
                                        CameraSelector.DEFAULT_BACK_CAMERA
                                    } else {
                                        CameraSelector.DEFAULT_FRONT_CAMERA
                                    }
                            },
                            modifier = Modifier
                                .offset(26.dp,48.dp)
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.camera_flip),
                                contentDescription = null,
                                modifier = Modifier.size(32.dp).rotate(rotationAngle),

                            )
                        }
                        Row (
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.BottomCenter)
                                .padding(bottom = 70.dp),
                            horizontalArrangement = Arrangement.SpaceAround
                        ){
                            IconButton(
                                onClick = {
                                    scope.launch {
                                        scaffoldState.bottomSheetState.expand()
                                    }
                                }
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.gallery),
                                    contentDescription = null,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                            IconButton(
                                onClick = {
                                    MediaPlayer.create(applicationContext, R.raw.camera_shutter_sound).start()
                                    isAnimatingBorder = true
                                    TakePhoto(
                                        context = applicationContext,
                                        controller = controller
                                    ) { bitmap ->
                                        viewModel.addBitmap(bitmap)
                                    }
                                    scope.launch {
                                        delay(1000)
                                        isAnimatingBorder = false
                                    }
                                }
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.capture),
                                    contentDescription = null,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                            IconButton(
                                onClick = {

                                }
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.video_camera),
                                    contentDescription = null,
                                    modifier = Modifier.size(32.dp)
                                )
                            }

                        }

                    }

                }
            }
        }
    }
}

private fun TakePhoto(
    context: Context,
    controller: LifecycleCameraController,
    onPhotoTaken: (Bitmap)->Unit
){
    controller.takePicture(
        ContextCompat.getMainExecutor(context),
        object : ImageCapture.OnImageCapturedCallback() {
            override fun onCaptureSuccess(image: ImageProxy) {
                super.onCaptureSuccess(image)
                val bitmap = image.toBitmap()
                onPhotoTaken(bitmap)
            }

            override fun onError(exception: ImageCaptureException) {
                super.onError(exception)
                Log.e("capture", "onError in image capture: $exception")
            }
        }
    )

}
