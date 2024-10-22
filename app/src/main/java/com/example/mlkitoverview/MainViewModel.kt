package com.example.mlkitoverview

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okio.IOException
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
                    val originalBitmap = BitmapFactory.decodeFile(file.absolutePath)
                    originalBitmap?.let {
                        compressBitmap(it,1024,1024)
                    }
                } ?: emptyList()

                _bitmaps.value = loadBitmaps
            }
        }
    }
    // Helper function to compress the bitmap
    private fun compressBitmap(bitmap: Bitmap, reqWidth: Int, reqHeight: Int): Bitmap {
        val aspectRatio = bitmap.width.toFloat() / bitmap.height.toFloat()
        var width = reqWidth
        var height = reqHeight

        if (bitmap.width > bitmap.height) {
            width = reqWidth
            height = (width / aspectRatio).toInt()
        } else {
            height = reqHeight
            width = (height * aspectRatio).toInt()
        }

        return Bitmap.createScaledBitmap(bitmap, width, height, true)
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
                LoadImages()
                Log.d("saveImage", "Image saved successfully: ${file.absolutePath}")
            }catch (e:Exception){
                Log.e("saveImage", "Error saving image: ${e.message}")
            }
        }
    }

    fun openImageFromBitmap(context: Context, bitmap: Bitmap) {
        // Save the bitmap to a file and get the file reference
        val imageFile = saveBitmapToFile(context, bitmap)

        if (imageFile != null && imageFile.exists()) {
            // Use FileProvider to get a content URI
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider", // Ensure this matches the provider in your manifest
                imageFile
            )

            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "image/*") // Open with any app that supports images
                flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } else {
            Toast.makeText(context, "Failed to save image", Toast.LENGTH_SHORT).show()
        }
    }

    // Helper function to save the Bitmap to a file
    fun saveBitmapToFile(context: Context, bitmap: Bitmap): File? {
        val directoryName = "CameraApp"
        val directory = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), directoryName)

        if (!directory.exists()) {
            directory.mkdirs()
        }

        val file = File(directory, "${System.currentTimeMillis()}BySam.jpg")
        return try {
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
            }
            file
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

}