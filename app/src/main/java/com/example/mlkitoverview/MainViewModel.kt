package com.example.mlkitoverview

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

class MainViewModel:ViewModel() {
    private val _bitmaps = MutableStateFlow<List<Bitmap>>(emptyList())
    val bitmaps = _bitmaps.asStateFlow()

    init {
        LoadImages()
    }
    fun addBitmap(bitmap: Bitmap){
        saveImage(bitmap)
        LoadImages()
//        _bitmaps.value += bitmap
    }
    private fun LoadImages(){
        val directoryName = "CameraApp"
        val directory = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),directoryName)
        viewModelScope.launch(Dispatchers.IO) {
            if(directory.exists()){
                val imageFiles = directory.listFiles{
                    file -> file.extension.equals("jpg", ignoreCase = true)
                }
                val loadBitmaps = imageFiles?.mapNotNull {file->
                    BitmapFactory.decodeFile(file.absolutePath)
                } ?: emptyList()

                _bitmaps.value = loadBitmaps
            }
        }
    }
    private fun saveImage(
        bitmap: Bitmap
    ){
        val directoryName = "CameraApp"
        val directory = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),directoryName)
        if(!directory.exists()){
            directory.mkdirs()
        }
        val file = File(directory, "${System.currentTimeMillis()}BySam.jpg")
        viewModelScope.launch(Dispatchers.IO) {
            try {
                FileOutputStream(file).use {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
                }
                Log.d("saveImage", "Image saved successfully: ${file.absolutePath}")
            }catch (e:Exception){
                Log.e("saveImage", "Error saving image: ${e.message}")
            }
        }
    }
}