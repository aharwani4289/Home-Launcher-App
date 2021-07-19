package com.ajayh.homelauncherapp.sdk.app.utils

import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.DrawableWrapper
import android.util.Size
import androidx.annotation.WorkerThread
import java.nio.ByteBuffer
import java.security.MessageDigest

class ComparableBitmapDrawable(private val source: BitmapDrawable) : DrawableWrapper(source) {

    companion object {
        private const val HASH_ALGORITHM = "MD5"
    }

    private val size: Size = Size(source.bitmap.width, source.bitmap.height)
    private var drawableHash: String? = null

    @WorkerThread
    fun calculateImageHash() {
        val digest = MessageDigest.getInstance(HASH_ALGORITHM)
        val buffer = ByteBuffer.allocate(source.bitmap.byteCount)
        source.bitmap.copyPixelsToBuffer(buffer)
        drawableHash = digest.digest(buffer.array()).joinToString("") { "%02x".format(it) }
        buffer.clear()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ComparableBitmapDrawable) return false

        if (other.size.width != size.width || other.size.height != size.height) return false

        val currentHash = drawableHash
        val otherHash = other.drawableHash
        if (currentHash != null && otherHash != null) {
            return currentHash == otherHash
        }
        return false
    }

    override fun hashCode(): Int {
        return if (drawableHash != null) drawableHash.hashCode() else super.hashCode()
    }
}