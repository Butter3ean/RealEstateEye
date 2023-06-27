package com.example.realestateeye.providers

import android.content.Context
import android.net.Uri
import android.os.Environment
import androidx.core.content.FileProvider
import com.example.realestateeye.R
import java.io.File

class ComposeFileProvider : FileProvider(
    R.xml.file_paths
) {
    companion object {
        fun getImageUri(context: Context): Uri {
            val imagePath = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)?.path + "/selected_image.jpg"
            return Uri.parse(imagePath)
        }
    }



}















//    companion object {
//        fun getImageUri(context: Context): Uri {
//            val directory = File(context.cacheDir, "images")
//            directory.mkdirs()
//
//            val file = File.createTempFile(
//                "selected_image_",
//                ".jpg",
//                directory
//            )
//            val authority = context.packageName + ".fileprovider"
//            return getUriForFile(
//                context,
//                authority,
//                file
//            )
//        }

//    }