package com.example.puzzle_game

import android.content.Context
import android.widget.BaseAdapter
import android.content.res.AssetManager
import android.view.ViewGroup
import android.view.LayoutInflater
import com.example.puzzle_game.R
import android.os.AsyncTask
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect
import android.util.Log
import android.view.View
import android.widget.ImageView
import java.io.IOException

class ImageAdapter(private val mContext: Context) : BaseAdapter() {
    val am: AssetManager = mContext.assets
    private var files: Array<String>? = null



    override fun getCount(): Int = files!!.size

    override fun getItem(position: Int): Any? {
        return null
    }

    override fun getItemId(position: Int): Long = 0

    // create a new ImageView for each item referenced by the Adapter
    override fun getView(position: Int, view: View?, viewGroup: ViewGroup?): View {
        val v = LayoutInflater.from(mContext).inflate(R.layout.grid_element, null)

        val imageView = v.findViewById<ImageView>(R.id.gridImageview)
        imageView.post {
            object : AsyncTask<Any?, Any?, Any>() {
                private var bitmap: Bitmap? = null

                //A verfifier poition
                override fun doInBackground(vararg poition: Any?): Any? {
                    // Toast.makeText(mContext,position.toString(),Toast.LENGTH_LONG).show()
                    bitmap = getPicFromAsset(imageView, files!![position])

                    Log.d("files!![position  ] :", files!![position])
                    return null
                }

                override fun onPostExecute(result: Any?) {
                    super.onPostExecute(result)
                    imageView.setImageBitmap(bitmap)
                }

            }.execute()
        }
        return v
    }

    private fun getPicFromAsset(imageView: ImageView, assetName: String): Bitmap? {
        // Get the dimensions of the View
        val targetW = imageView.width
        val targetH = imageView.height
        return if (targetW == 0 || targetH == 0) {
            // view has no dimensions set
            null
        } else try {
            val `is` = am.open("img/$assetName")
            // Get the dimensions of the bitmap
            val bmOptions = BitmapFactory.Options()
            bmOptions.inJustDecodeBounds = true
            BitmapFactory.decodeStream(`is`, Rect(-1, -1, -1, -1), bmOptions)
            val photoW = bmOptions.outWidth
            val photoH = bmOptions.outHeight

            // Determine how much to scale down the image
            val scaleFactor = Math.min(photoW / targetW, photoH / targetH)
            `is`.reset()

            // Decode the image file into a Bitmap sized to fill the View
            bmOptions.inJustDecodeBounds = false
            bmOptions.inSampleSize = scaleFactor
            bmOptions.inPurgeable = true
            BitmapFactory.decodeStream(`is`, Rect(-1, -1, -1, -1), bmOptions)
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    init {
        // am = mContext.assets
        try {
            files = am.list("img")
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}