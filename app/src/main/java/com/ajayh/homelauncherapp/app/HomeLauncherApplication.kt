package com.ajayh.homelauncherapp.app

import com.ajayh.homelauncherapp.sdk.HomeLauncherSdkApplication
import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication

class HomeLauncherApplication : HomeLauncherSdkApplication() {

    companion object {
        lateinit var appComponent: AppComponent
    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication?> {
        super.applicationInjector()
        appComponent = DaggerAppComponent.builder().application(this).context(this).build()
        return appComponent
    }

}