package com.zen.accounts.data.repositoryImpl

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import com.zen.accounts.presentation.utility.async
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedOutputStream
import java.io.File
import javax.inject.Inject

class MediaStoreRepository @Inject constructor(
    @ApplicationContext val context : Context
) {
    suspend fun saveImageToStorage(uri: Uri) : Deferred<Bitmap?> {
        return async {
            val resolver = context.contentResolver
            try {
                val bitmap = BitmapFactory.decodeStream(
                    resolver.openInputStream(uri)
                )
                val contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                val values = ContentValues().apply {
                    put(
                        MediaStore.Images.Media.RELATIVE_PATH,
                        "Android/media/com.zen.accounts/profile"
                    )
                    put(MediaStore.Images.Media.DISPLAY_NAME, "profile.jpg")
                    put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                    put(MediaStore.Images.Media.IS_PENDING, true)
                }

                val folder = File(Environment.getExternalStorageDirectory(), "Android/media/com.zen.accounts/profile")
                val alreadyCreatedFile = File(folder, "profile.jpg")
                if(alreadyCreatedFile.exists()) {
                    alreadyCreatedFile.delete()
                }

                val fileUri = resolver.insert(contentUri, values)
                    ?: throw java.lang.Exception("Something went wrong")

                return@async withContext(Dispatchers.IO) {
                    val os = BufferedOutputStream(resolver.openOutputStream(fileUri))
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os)
                    os.close()
                    values.put(MediaStore.Images.Media.IS_PENDING, false)
                    resolver.update(fileUri, values, null, null)
                    bitmap
                }
            } catch (e: Exception) {
                null
            }
        }
    }
}