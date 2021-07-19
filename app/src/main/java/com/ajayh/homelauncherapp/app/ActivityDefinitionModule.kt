package com.ajayh.homelauncherapp.app

import com.ajayh.homelauncherapp.app.activity.AppModule
import com.ajayh.homelauncherapp.app.activity.MainActivity
import com.ajayh.homelauncherapp.app.di.AppViewModelsModule
import dagger.Module
import dagger.android.ContributesAndroidInjector

/**
 * Created by ajay.harwani
 */
@Module
abstract class ActivityDefinitionModule {

    @ContributesAndroidInjector(modules = [AppViewModelsModule::class, AppModule::class])
    abstract fun contributeMainActivity(): MainActivity

}