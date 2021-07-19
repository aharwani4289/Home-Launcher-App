package com.ajayh.homelauncherapp.app

import android.app.Application
import android.content.Context
import com.ajayh.homelauncherapp.app.di.ViewModelFactoryModule
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

/**
 * Created by ajay.harwani
 */
@Singleton
@Component(modules = [AndroidSupportInjectionModule::class,
    ActivityDefinitionModule::class,
    ViewModelFactoryModule::class])
interface AppComponent : AndroidInjector<HomeLauncherApplication?> {
    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder

        @BindsInstance
        fun context(context: Context): Builder

        fun build(): AppComponent
    }

}