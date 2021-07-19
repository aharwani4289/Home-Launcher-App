package com.ajayh.homelauncherapp.sdk.app.model

import android.graphics.drawable.Drawable

sealed class ContentItem {

    data class Application(
        val appName: String,
        val packageName: String,
        val appBanner: Drawable?,
        val isMandatoryApp: Boolean
    ) : ContentItem()

}