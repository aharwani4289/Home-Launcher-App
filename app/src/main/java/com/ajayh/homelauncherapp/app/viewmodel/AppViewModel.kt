package com.ajayh.homelauncherapp.app.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ajayh.homelauncherapp.sdk.app.model.ContentItem.Application
import com.ajayh.homelauncherapp.sdk.app.repo.AppsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AppViewModel @Inject constructor(
    private val appsRepository: AppsRepository
) : ViewModel() {
    private val mAppList: MutableLiveData<List<Application>> = MutableLiveData()
    val appList: LiveData<List<Application>>
        get() = mAppList

    fun getApps() {
        viewModelScope.launch(Dispatchers.IO) {
            val apps = appsRepository.loadInstalledApps()
            withContext(Dispatchers.Main) {
                mAppList.value = apps
            }
        }
    }
}