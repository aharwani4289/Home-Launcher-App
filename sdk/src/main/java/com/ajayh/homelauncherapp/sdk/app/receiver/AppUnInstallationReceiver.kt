package com.ajayh.homelauncherapp.sdk.app.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class AppUnInstallationReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {

            try {
                val uninstallRequest = intent?.getParcelableExtra<Intent>(Intent.EXTRA_INTENT)
                uninstallRequest?.let {
                    it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context?.startActivity(it)
                }
            } catch (exception: Exception) {

            }
        }
    }