package com.example.mlkitoverview.landmarkML

import android.graphics.Bitmap
import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.example.mlkitoverview.Domain.Classification
import com.example.mlkitoverview.Domain.LandmarkClassifier

class LandmarkImageAnalyzer(
    private val classifier: LandmarkClassifier,
    private val onResult: (List<Classification>) -> Unit
) : ImageAnalysis.Analyzer {

    private var frameSkip = 0

    override fun analyze(image: ImageProxy) {
        if (frameSkip % 10 == 0) {
            val rotationDegrees = image.imageInfo.rotationDegrees
            val bitmap = image.toBitmap().crop(321,321)
            Log.d("LandmarkImageAnalyzer", "Running classification on frame")
            val results = classifier.classify(bitmap, rotationDegrees)
            Log.d("LandmarkImageAnalyzer", "Classification results: $results")
            onResult(results)
        }
        frameSkip++
        image.close()

    }
}
fun Bitmap.crop(needHeight:Int,needWidth:Int):Bitmap{
    val x = (width - needWidth) / 2
    val y = (height - needHeight) / 2
    return Bitmap.createBitmap(this, x, y, needWidth, needHeight)

}