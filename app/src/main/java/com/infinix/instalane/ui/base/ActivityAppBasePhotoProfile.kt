package com.infinix.instalane.ui.base

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.tbruyelle.rxpermissions3.RxPermissions
import java.io.*
import java.util.*


open class ActivityAppBasePhotoProfile : ActivityAppBase() {

    var currentPhotoPath : String?=null
    var uriFile : Uri? = null
    var fileSelected : File? = null
    lateinit var onPhotoBitmap : (Bitmap)->Unit

    fun openCamera(onPhotoBitmap: (Bitmap)->Unit) {
        this.onPhotoBitmap = onPhotoBitmap
        val disposePermission = RxPermissions(this)
            .request(
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ).subscribe { granted: Boolean? ->
                if (granted != null && granted) {
                    dispatchTakePictureIntent()
                }
            }
    }

    fun openGallery(onPhotoBitmap: (Bitmap)->Unit) {
        this.onPhotoBitmap = onPhotoBitmap

        try {
            fileSelected = createImageFile()
        } catch (ex: IOException) {
            Log.e("IOException", "exception: ${ex.message}")
        }

        val disposePermission = RxPermissions(this)
            .request(
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ).subscribe { granted: Boolean? ->
                if (granted != null && granted) {
                    val pickPhoto = Intent(
                        Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    )
                    resultLauncherGallery.launch(pickPhoto)

                }
            }
    }

    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            try {
                fileSelected = createImageFile()
            } catch (ex: IOException) {
                Log.e("IOException", "exception: ${ex.message}")
            }
            if (fileSelected != null) {
                val photoURI = FileProvider.getUriForFile(
                    this,
                    applicationContext.packageName.toString() + ".provider",
                    fileSelected!!
                )
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                resultLauncherCamera.launch(takePictureIntent)
            }
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File? {

        val imageFileName = "ProfilePhoto"
        var image : File?

        try {
            val destPath = File(getExternalFilesDir(null)!!.absolutePath)
            image = File.createTempFile(imageFileName, ".jpg", destPath)
            currentPhotoPath = image?.absolutePath
            return image
        } catch (ex: IOException) {

        }

        try {
            val storageDir = File(Environment.getExternalStorageDirectory().absolutePath)
            image = File.createTempFile(imageFileName, ".jpg", storageDir)
        } catch (ex: IOException) {
            val storageDir: File = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            image = File.createTempFile(imageFileName, ".jpg", storageDir)
        }

        currentPhotoPath = image?.absolutePath
        return image
    }


    private var resultLauncherCamera = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            Glide.with(this).asBitmap().load(currentPhotoPath)
                .apply(RequestOptions.overrideOf(400).centerCrop()).into(target)
        }
    }

    private var resultLauncherGallery = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            uriFile = result.data?.data

            if (uriFile!=null && fileSelected!=null){
                val inputStream: InputStream = contentResolver.openInputStream(uriFile!!)!!
                val fileOutputStream = FileOutputStream(fileSelected)
                copyStream(inputStream, fileOutputStream)
                fileOutputStream.close()
                inputStream.close()
            }

            Glide.with(this).asBitmap().load(uriFile)
                .apply(RequestOptions.overrideOf(400).centerCrop()).into(target)
        }
    }

    @Throws(IOException::class)
    open fun copyStream(input: InputStream, output: OutputStream) {
        val buffer = ByteArray(1024)
        var bytesRead: Int
        while (input.read(buffer).also { bytesRead = it } != -1) {
            output.write(buffer, 0, bytesRead)
        }
    }

    private val target = object : CustomTarget<Bitmap>() {
        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
            onPhotoBitmap.invoke(resource)
            /*
            val byteArrayOutputStream = ByteArrayOutputStream()
            resource.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
            val byteArray: ByteArray = byteArrayOutputStream.toByteArray()
            uriFile = Base64.encodeToString(byteArray, Base64.DEFAULT)
            Log.i("BASE64", uriFile)

             */
        }

        override fun onLoadCleared(placeholder: Drawable?) {}
    }

}