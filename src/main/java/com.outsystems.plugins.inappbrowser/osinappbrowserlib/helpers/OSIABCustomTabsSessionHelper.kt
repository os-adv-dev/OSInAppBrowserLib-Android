package com.outsystems.plugins.inappbrowser.osinappbrowserlib.helpers

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import androidx.browser.customtabs.CustomTabsCallback
import androidx.browser.customtabs.CustomTabsClient
import androidx.browser.customtabs.CustomTabsService
import androidx.browser.customtabs.CustomTabsServiceConnection
import androidx.browser.customtabs.CustomTabsSession

class OSIABCustomTabsSessionHelper : OSIABCustomTabsSessionHelperInterface {
    companion object {
        const val CHROME_PACKAGE_NAME = "com.android.chrome"
    }

    private fun getDefaultCustomTabsPackageName(context: Context): String? {
        val activityIntent = Intent(Intent.ACTION_VIEW, Uri.parse("http://"))
        val resolvedActivityList =
            context.packageManager.queryIntentActivities(activityIntent, PackageManager.MATCH_ALL)
        var result: String? = null

        for (info in resolvedActivityList) {
            val serviceIntent = Intent().apply {
                action = CustomTabsService.ACTION_CUSTOM_TABS_CONNECTION
                `package` = info.activityInfo.packageName
            }
            if (context.packageManager.resolveService(serviceIntent, 0) != null) {
                if (info.activityInfo.packageName != CHROME_PACKAGE_NAME) {
                    result = info.activityInfo.packageName
                    break
                }
            }
        }

        val packageName = CustomTabsClient.getPackageName(
            context,
            resolvedActivityList.map { it.activityInfo.packageName },
            false
        )

        return packageName
    }

    private fun initializeCustomTabsSession(
        context: Context,
        packageName: String,
        onEventReceived: (Int) -> Unit,
        customTabsSessionCallback: (CustomTabsSession?) -> Unit
    ) {
        CustomTabsClient.bindCustomTabsService(
            context,
            packageName,
            object : CustomTabsServiceConnection() {
                override fun onCustomTabsServiceConnected(
                    name: ComponentName,
                    client: CustomTabsClient
                ) {
                    client.warmup(0L)
                    customTabsSessionCallback(client.newSession(CustomTabsCallbackImpl {
                        onEventReceived(
                            it
                        )
                    }))
                }

                override fun onServiceDisconnected(name: ComponentName) {
                    customTabsSessionCallback(null)
                }
            }
        )
    }

    private inner class CustomTabsCallbackImpl(private val onEventReceived: (Int) -> Unit) :
        CustomTabsCallback() {
        override fun onNavigationEvent(navigationEvent: Int, extras: Bundle?) {
            super.onNavigationEvent(navigationEvent, extras)
            onEventReceived(navigationEvent)
        }
    }

    /**
     * Generates a new CustomTabsSession instance
     * @param context Context to use when initializing the CustomTabsSession
     * @param onEventReceived Callback to send the session events (e.g. navigation finished)
     */
    override suspend fun generateNewCustomTabsSession(
        context: Context,
        onEventReceived: (Int) -> Unit,
        customTabsSessionCallback: (CustomTabsSession?) -> Unit
    ) {
        val packageName = getDefaultCustomTabsPackageName(context)
        packageName?.let { packageName ->
            initializeCustomTabsSession(
                context,
                packageName,
                onEventReceived,
                customTabsSessionCallback
            )
        } ?: customTabsSessionCallback(null)
    }
}