package com.example.puzzle_game

import android.Manifest
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.example.puzzle_game.R
import android.content.res.AssetManager
import android.widget.GridView
import com.example.puzzle_game.ImageAdapter
import android.widget.AdapterView.OnItemClickListener
import android.widget.AdapterView
import android.content.Intent
import com.example.puzzle_game.PuzzleActivity
import android.widget.Toast
import android.provider.MediaStore
import android.support.v4.content.FileProvider
import com.example.puzzle_game.MainActivity
import kotlin.Throws
import android.support.v4.content.ContextCompat
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.os.Environment
import android.app.Activity
import android.view.View
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    var mCurrentPhotoPath: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val am = assets
        try {
            val files = am.list("img")
            val grid = findViewById<GridView>(R.id.grid)
            grid.adapter = ImageAdapter(this)
            grid.onItemClickListener = OnItemClickListener { adapterView, view, i, l ->
                val intent = Intent(applicationContext, PuzzleActivity::class.java)
                intent.putExtra("assetName", files!![i % files.size])
                startActivity(intent)
            }
        } catch (e: IOException) {
            Toast.makeText(this, e.localizedMessage, Toast.LENGTH_SHORT)
        }
    }

    fun onImageFromCameraClick(view: View?) {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(packageManager) != null) {
            var photoFile: File? = null
            try {
                photoFile = createImageFile()
            } catch (e: IOException) {
                Toast.makeText(this, e.message, Toast.LENGTH_LONG)
            }
            if (photoFile != null) {
                val photoUri = FileProvider.getUriForFile(
                    this,
                    applicationContext.packageName + ".fileprovider",
                    photoFile
                )
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
            }
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File? {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // permission not granted, initiate request
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE
            )
        } else {
            // Create an image file name
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
            val imageFileName = "JPEG_" + timeStamp + "_"
            val storageDir =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            val image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",  /* suffix */
                storageDir /* directory */
            )
            mCurrentPhotoPath = image.absolutePath // save this to use in the intent
            return image
        }
        return null
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    onImageFromCameraClick(View(this))
                }
                return
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            val intent = Intent(this, PuzzleActivity::class.java)
            intent.putExtra("mCurrentPhotoPath", mCurrentPhotoPath)
            startActivity(intent)
        }
        if (requestCode == REQUEST_IMAGE_GALLERY && resultCode == RESULT_OK) {
            val uri = data.data
            val intent = Intent(this, PuzzleActivity::class.java)
            intent.putExtra("mCurrentPhotoUri", uri.toString())
            startActivity(intent)
        }
    }

    fun onImageFromGalleryClick(view: View?) {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                REQUEST_PERMISSION_READ_EXTERNAL_STORAGE
            )
        } else {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            startActivityForResult(intent, REQUEST_IMAGE_GALLERY)
        }
    }

    companion object {
        private const val REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE = 2
        private const val REQUEST_IMAGE_CAPTURE = 1
        const val REQUEST_PERMISSION_READ_EXTERNAL_STORAGE = 3
        const val REQUEST_IMAGE_GALLERY = 4
    }
}