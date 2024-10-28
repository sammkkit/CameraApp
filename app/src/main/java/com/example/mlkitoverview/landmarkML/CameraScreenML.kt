package com.example.mlkitoverview.landmarkML

import android.content.Context
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.mlkitoverview.Data.TfLiteLandmarkClassifier
import com.example.mlkitoverview.Domain.Classification

@Composable
fun CameraScreenML(
    context: Context
){
    var classification by remember {
        mutableStateOf(emptyList<Classification>())
    }
    Log.d("CameraScreenML", "Initial classification list: $classification")
    val analyzer = remember {
        LandmarkImageAnalyzer(
            classifier = TfLiteLandmarkClassifier(context),
            onResult = {
                classification = it
                Log.d("CameraScreenML", "Classification result updated: $classification")
            }
        )
    }
    val controller = remember {
        LifecycleCameraController(context).apply {
            setEnabledUseCases(
                CameraController.IMAGE_CAPTURE or
                        CameraController.VIDEO_CAPTURE or CameraController.IMAGE_ANALYSIS
            )
            cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            setImageAnalysisAnalyzer(
                ContextCompat.getMainExecutor(context),
                analyzer
            )
            Log.d("CameraScreenML", "Camera controller successfully initialized.")
        }
    }
    Box(
        modifier = Modifier.fillMaxSize()

    ){
        CameraPreviewMl(controller,Modifier.fillMaxSize())
        Column (
            modifier = Modifier.padding(top = 35.dp).fillMaxWidth().align(Alignment.TopCenter)
        ){
            val highestClassification = classification.maxByOrNull { it.score }
            highestClassification?.let {
                Text(
                    text = "${it.name} - ${it.score.times(100)}%",
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Blue)
                        .padding(8.dp),
                    textAlign = TextAlign.Center,
                    fontSize = 20.sp,
                    color = Color.Red
                )
                Log.d("CameraScreenML", "Displaying classification: ${it.name} - ${it.score}")
            }
        }

    }
}