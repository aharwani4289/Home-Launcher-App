package com.ajayh.homelauncherapp.app.di

import androidx.lifecycle.ViewModel
import com.ajayh.homelauncherapp.app.viewmodel.AppViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

/**
 * Created by ajay.harwani
 */
@Module
abstract class AppViewModelsModule {

    @Binds
    @IntoMap
    @ViewModelKey(AppViewModel::class)
    abstract fun bindAppViewModel(viewModel: AppViewModel): ViewModel
}