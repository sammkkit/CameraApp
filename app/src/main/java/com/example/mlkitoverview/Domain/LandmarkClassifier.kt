package com.example.mlkitoverview.Domain

import android.graphics.Bitmap

interface LandmarkClassifier {

    fun classify(bitmap: Bitmap,rotation:Int): List<Classification>
}