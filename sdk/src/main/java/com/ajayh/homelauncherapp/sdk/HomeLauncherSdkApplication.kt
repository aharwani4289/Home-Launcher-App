package com.ajayh.homelauncherapp.sdk

import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication

open class HomeLauncherSdkApplication : DaggerApplication() {

    companion object {
        lateinit var appComponent: SdkAppComponent
    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication?> {
        appComponent = DaggerSdkAppComponent.builder().application(this).context(this).build()
        return appComponent
    }
}