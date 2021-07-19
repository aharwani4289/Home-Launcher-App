package com.ajayh.homelauncherapp.app.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.Factory
import javax.inject.Inject
import javax.inject.Provider

/**
 *
 * Created by ajay.harwani
 */
class ViewModelProviderFactory @Inject constructor(
    private val providers: Map<Class<out ViewModel>, @JvmSuppressWildcards Provider<ViewModel>>
) : Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        var provider: Provider<out ViewModel>? = providers[modelClass]
        if (provider == null) {
            provider = providers.entries.find { modelClass.isAssignableFrom(it.key) }?.value
        }
        @Suppress("UNCHECKED_CAST")
        return provider?.get() as T
            ?: throw IllegalArgumentException("Unknown view model binding $modelClass")
    }
}