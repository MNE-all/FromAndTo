@file:Suppress("DEPRECATION")

package com.mne4.fromandto.Models

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.util.Log
import android.widget.ImageView
import java.io.InputStream
import java.net.URL


class DownloadImageTask(bmImage: ImageView) :  AsyncTask<String?, Void?, Bitmap?>() {
    var bmImage: ImageView

    init {
        this.bmImage = bmImage
    }

    protected override fun doInBackground(vararg params: String?): Bitmap? {
        val urldisplay = params[0]
        var mIcon11: Bitmap? = null
        try {
            val IS: InputStream = URL(urldisplay).openStream()
            mIcon11 = BitmapFactory.decodeStream(IS)
        } catch (e: Exception) {
            e.message?.let { Log.e("Ошибка передачи изображения", it) }
            e.printStackTrace()
        }
        return mIcon11
    }

    override fun onPostExecute(result: Bitmap?) {
        bmImage.setImageBitmap(result)
    }
}