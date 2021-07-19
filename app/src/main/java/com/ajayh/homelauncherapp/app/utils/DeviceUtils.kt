package com.ajayh.homelauncherapp.app.utils

import android.content.Context
import com.ajayh.homelauncherapp.R

object DeviceUtils {

    fun isTabletDevice(context: Context?): Boolean {
        return context?.resources?.getBoolean(R.bool.isTablet)
            ?: false
    }
}
