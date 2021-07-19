package com.ajayh.homelauncherapp.sdk.app.utils

import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.annotation.WorkerThread

object DrawableWrappersFactory {

    @WorkerThread
    fun createDrawableWrapper(drawable: Drawable): Drawable =
        when (drawable) {
            is BitmapDrawable -> {
                val wrapper = ComparableBitmapDrawable(drawable)
                wrapper.calculateImageHash()
                wrapper
            }
            else -> drawable
        }
}