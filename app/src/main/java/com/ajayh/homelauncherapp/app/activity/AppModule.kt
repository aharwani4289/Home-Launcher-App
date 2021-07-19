package com.ajayh.homelauncherapp.app.activity

import android.content.Context
import com.ajayh.homelauncherapp.sdk.app.repo.AppsRepository
import com.ajayh.homelauncherapp.sdk.app.repo.AppsRepositoryImpl
import dagger.Module
import dagger.Provides

/**
 * Created by ajay.harwani
 */
@Module
class AppModule {

    @Provides
    fun providesAppRepository(
        context: Context
    ): AppsRepository {
        return AppsRepositoryImpl(
            context
        )
    }

}