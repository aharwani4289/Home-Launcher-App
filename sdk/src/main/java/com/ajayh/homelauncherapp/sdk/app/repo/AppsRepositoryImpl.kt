package com.ajayh.homelauncherapp.sdk.app.repo

import android.app.PendingIntent.getBroadcast
import android.content.*
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import com.ajayh.homelauncherapp.sdk.app.model.Action
import com.ajayh.homelauncherapp.sdk.app.model.ContentItem.Application
import com.ajayh.homelauncherapp.sdk.app.model.IntentAction
import com.ajayh.homelauncherapp.sdk.app.receiver.AppUnInstallationReceiver
import com.ajayh.homelauncherapp.sdk.app.utils.DrawableWrappersFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentSkipListMap

class AppsRepositoryImpl constructor(
    private val context: Context
) : AppsRepository {

    companion object {
        val TAG = AppsRepositoryImpl::class.qualifiedName
    }

    private val listOfAppsInstalled = ArrayList<String>()
    private val listOfAppsUninstalled = ArrayList<String>()

    private val observers = ConcurrentSkipListMap<String, (() -> Unit)>()

    private val packageManager = context.packageManager

    init {
        val receiver = PackageInstalledReceiver(
            observers, listOfAppsInstalled, listOfAppsUninstalled
        )
        val intentFilter = IntentFilter().apply {
            addAction(Intent.ACTION_PACKAGE_ADDED)
            addAction(Intent.ACTION_PACKAGE_REMOVED)
            addDataScheme("package")
        }
        context.registerReceiver(receiver, intentFilter)
    }

    override suspend fun loadInstalledApps(): List<Application> {
        val allInstalledApps = getAllInstalledMobileApps()
        val userApps = allInstalledApps.map { mapApplicationInfoToApplication(it) }
        return userApps.sortedBy { it.appName }
    }

    override suspend fun deleteApp(packageName: String) {
        val intent = Intent(context, AppUnInstallationReceiver::class.java).apply {
            action = Intent.ACTION_DEFAULT
        }
        packageManager?.packageInstaller?.uninstall(
            packageName,
            getBroadcast(context, 0, intent, 0).intentSender
        )
    }

    override fun getApplicationInfo(packageName: String) = getAppInfo(packageName)?.let {
        mapApplicationInfoToApplication(it)
    }


    private fun mapApplicationInfoToApplication(appInfo: ApplicationInfo): Application {
        return Application(
            getInstalledAppName(appInfo.packageName), appInfo.packageName, getAppIcon(appInfo),
//            getAction(getDeepLinkUrl(appInfo, remoteIndicator), appInfo.packageName),
            (appInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0
        )
    }

    private fun getAppInfo(packageName: String) = try {
        packageManager?.getApplicationInfo(packageName, 0)
    } catch (exception: PackageManager.NameNotFoundException) {
        null
    }

    override fun getAppIcon(packageName: String) = getAppInfo(packageName)?.let { getAppIcon(it) }

    private fun getAppIcon(appInfo: ApplicationInfo): Drawable? {
        val launcherIntent = packageManager.getLeanbackLaunchIntentForPackage(appInfo.packageName)
        val icon = try {
            launcherIntent?.let { packageManager.getActivityBanner(it) } ?: appInfo.loadBanner(
                packageManager
            )
            ?: launcherIntent?.let { packageManager.getActivityIcon(it) } ?: appInfo.loadIcon(
                packageManager
            )
            ?: launcherIntent?.let { packageManager.getActivityLogo(it) } ?: appInfo.loadLogo(
                packageManager
            )
        } catch (exception: PackageManager.NameNotFoundException) {
            null
        }
        return icon?.let { DrawableWrappersFactory.createDrawableWrapper(it) }
    }

    override fun isSystemApp(packageName: String): Boolean =
        getAppInfo(packageName)?.let { (it.flags and ApplicationInfo.FLAG_SYSTEM) != 0 } ?: false

    private fun getAction(deepLinkUrl: String, packageName: String) =
        getReferralAction(deepLinkUrl, packageName) ?: launchAppAction(packageName)

    private fun getReferralAction(deepLinkUrl: String, packageName: String): Action? {
        if (deepLinkUrl.isNotEmpty()) {
            val redirectIntent = Intent.parseUri(deepLinkUrl, 0)
            redirectIntent.action = Intent.ACTION_VIEW
            redirectIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            redirectIntent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES)
            redirectIntent.`package` = packageName
            val activity = context.packageManager.queryIntentActivities(
                redirectIntent,
                PackageManager.MATCH_ALL
            )
            if (activity.isNotEmpty()) {
                return InstalledAppIntentAction(context, redirectIntent)
            }
        }
        return null
    }

    private inner class InstalledAppIntentAction(
        val context: Context,
        val intent: Intent
    ) : IntentAction(intent) {

        override fun invoke() {
            try {
                context.startActivity(intent)
            } catch (exception: ActivityNotFoundException) {

            }
        }
    }

    private fun launchAppAction(packageName: String): Action? {
        val intent = packageManager.getLeanbackLaunchIntentForPackage(packageName)
        return intent?.let {
            it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            it.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES)
            InstalledAppIntentAction(context, it)
        } ?: run {
            null
        }
    }

    override fun isInstalledApp(packageName: String) =
        packageManager?.getInstalledPackages(0)?.find { it.packageName == packageName } != null

    override fun getInstalledAppName(packageName: String) = try {
        packageManager?.let {
            it.getApplicationLabel(it.getApplicationInfo(packageName, PackageManager.GET_META_DATA))
                .toString()
        } ?: ""
    } catch (exception: PackageManager.NameNotFoundException) {
        ""
    }

    private fun getAllInstalledMobileApps(): List<ApplicationInfo> =
        packageManager?.getInstalledApplications(PackageManager.GET_META_DATA).orEmpty()
            .asSequence()
            .sortedBy { it.name }
            .filterNotNull()
            .filter { isMobileApp(it.packageName) }
            .toList()

    private fun isMobileApp(packageName: String) =
        packageManager?.getLaunchIntentForPackage(packageName) != null


    private class PackageInstalledReceiver(
        val observers: Map<String, () -> Unit>,
        val listOfAppsInstalled: ArrayList<String>,
        val listOfAppsUninstalled: ArrayList<String>
    ) : BroadcastReceiver() {

        override fun onReceive(context: Context?, intent: Intent?) {
            intent?.let { packageChangeIntent ->
                when (packageChangeIntent.action) {
                    Intent.ACTION_PACKAGE_REMOVED -> {
                        val isUpdate =
                            packageChangeIntent.extras?.getBoolean(Intent.EXTRA_REPLACING) ?: false
                        val packageName = packageChangeIntent.dataString?.split(":")?.get(1) ?: ""
                        if (!isUpdate) {
                            MainScope().launch(Dispatchers.IO) {
                                listOfAppsUninstalled.add(packageName)
                                observers.values.forEach { it.invoke() }
                            }
                            //appEvent.invoke(packageName, true)
                        }
                    }
                    Intent.ACTION_PACKAGE_ADDED -> {
                        val packageName = intent.dataString?.split(":")?.get(1) ?: ""
                        val isUpdate =
                            packageChangeIntent.extras?.getBoolean(Intent.EXTRA_REPLACING) ?: false
                        if (!isUpdate) {
                            listOfAppsInstalled.add(packageName)
                        }
                        observers.values.forEach { it.invoke() }
                    }
                }
            }
        }
    }
}