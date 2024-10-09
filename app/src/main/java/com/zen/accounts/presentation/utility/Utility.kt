package com.zen.accounts.presentation.utility

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.platform.LocalContext
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.canhub.cropper.CropImageView
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.delay

object Utility {
    fun imageCropLauncher(
        launcher: ActivityResultLauncher<CropImageContractOptions>,
        includeGallery : Boolean = false
    ) {
        val options = CropImageContractOptions(null, CropImageOptions().apply {
            imageSourceIncludeCamera = !includeGallery
            imageSourceIncludeGallery = includeGallery
            cropShape = CropImageView.CropShape.RECTANGLE
            fixAspectRatio = true
        })

        launcher.launch(options)
    }

    @Composable
    fun imagePicker(
        onResult: (Uri) -> Unit
    ): ActivityResultLauncher<CropImageContractOptions> {
        val context = LocalContext.current
        return rememberLauncherForActivityResult(contract = CropImageContract()) { result ->
            if(result.isSuccessful && result.uriContent != null) {
                onResult(result.uriContent!!)
            } else {
                context.toast("Something went wrong. Please try again later.")
            }
        }
    }

    fun getUserDocRef(uid: String) : DocumentReference {
        val db = FirebaseFirestore.getInstance()
        return db.document("Users/$uid")
    }

    fun getStorageRef(uid: String) : StorageReference {
        val storage = FirebaseStorage.getInstance()
        return storage.getReference("User/$uid")
    }

    fun showSnackBar(snackBarState: MutableState<Boolean>) {
        main {
            if(!snackBarState.value) {
                snackBarState.value = true
                delay(2500)
                snackBarState.value = false
            }
        }
    }
}