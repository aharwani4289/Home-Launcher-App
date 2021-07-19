package com.ajayh.homelauncherapp.app.di

import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module

/**
 * Created by ajay.harwani
 */
@Module
abstract class ViewModelFactoryModule {
    @Binds
    abstract fun bindViewModelFactory(viewModelFactory: ViewModelProviderFactory?): ViewModelProvider.Factory?
}