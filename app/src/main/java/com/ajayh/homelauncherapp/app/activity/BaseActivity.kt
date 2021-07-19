package com.ajayh.homelauncherapp.app.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.*
import dagger.android.AndroidInjection
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import javax.inject.Inject

abstract class BaseActivity : AppCompatActivity(), HasAndroidInjector {

    @JvmField
    @Inject
    var androidInjector: DispatchingAndroidInjector<Any>? = null

    @JvmField
    @Inject
    var factory: ViewModelProvider.Factory? = null

    override fun androidInjector(): AndroidInjector<Any>? {
        return androidInjector
    }

    protected fun <T : ViewModel?> getViewModel(modelClass: Class<T>): T {
        val provider = factory?.let { ViewModelProvider(this, it)  } ?: ViewModelProvider(this)
        val name = modelClass.canonicalName
        return if (name == null) provider[modelClass] else provider[name, modelClass]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
    }
}
