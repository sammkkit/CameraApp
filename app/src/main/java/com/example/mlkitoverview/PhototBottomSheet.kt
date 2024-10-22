package com.example.mlkitoverview

import android.content.Context
import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import java.io.ByteArrayOutputStream

@Composable
fun PhotoBottomSheetContent(
    bitmaps: List<Bitmap>,
    context: Context,
    mainViewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    if (bitmaps.isEmpty()) {
        Box(modifier = Modifier.fillMaxWidth().height(400.dp)) {
            Text(
                text = "No photos",
                modifier = Modifier.align(Alignment.Center)
            )
        }
    } else {
        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalItemSpacing = 16.dp,
            contentPadding = PaddingValues(16.dp),
            modifier = modifier,
        ) {

            items(bitmaps.reversed()) { bitmap ->
                val imagePainter = rememberAsyncImagePainter(
                    ImageRequest.Builder(LocalContext.current)
                        .data(bitmap) // Assuming you want to show the first image
                        .apply {
                            crossfade(true) // Enable smooth transition
                            placeholder(R.drawable.gallery) // Optional placeholder
                            error(R.drawable.capture) // Optional error image
                        }
                        .build()
                )
                Box (
                    modifier = Modifier
                        .size(200.dp,200.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.Red)
                        .clickable {
                            mainViewModel.openImageFromBitmap(context, bitmap)
                        }

                ){
                    Image(
                        painter = imagePainter, // Load the first image
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxSize()
                        ,
                        contentScale = ContentScale.None
                    )
                }

            }

        }
    }


}