package com.ajayh.homelauncherapp.sdk.app.repo

import android.graphics.drawable.Drawable
import com.ajayh.homelauncherapp.sdk.app.model.ContentItem

interface AppsRepository {

    suspend fun deleteApp(packageName: String)

    suspend fun getInstalledApps(query: String?): List<ContentItem.Application>

    fun getApplicationInfo(packageName: String): ContentItem.Application?

    fun isInstalledApp(packageName: String): Boolean

    fun getInstalledAppName(packageName: String): String

    fun getAppIcon(packageName: String): Drawable?

    fun isSystemApp(packageName: String): Boolean
}