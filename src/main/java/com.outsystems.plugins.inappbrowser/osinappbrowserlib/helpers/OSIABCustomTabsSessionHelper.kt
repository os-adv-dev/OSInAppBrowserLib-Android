package com.outsystems.plugins.inappbrowser.osinappbrowserlib.helpers

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import androidx.browser.customtabs.CustomTabsCallback
import androidx.browser.customtabs.CustomTabsClient
import androidx.browser.customtabs.CustomTabsServiceConnection
import androidx.browser.customtabs.CustomTabsSession

class OSIABCustomTabsSessionHelper: OSIABCustomTabsSessionHelperInterface {
    private fun getDefaultCustomTabsPackageName(context: Context): String? {
        val activityIntent = Intent(Intent.ACTION_VIEW, Uri.parse("http://"))
        val resolvedActivityList = context.packageManager.queryIntentActivities(activityIntent, PackageManager.MATCH_ALL)
        return CustomTabsClient.getPackageName(
            context,
            resolvedActivityList.map { it.activityInfo.packageName },
            false
        )
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
                override fun onCustomTabsServiceConnected(name: ComponentName, client: CustomTabsClient) {
                    client.warmup(0L)
                    customTabsSessionCallback(client.newSession(CustomTabsCallbackImpl {
                        onEventReceived(it)
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

    override suspend fun generateNewCustomTabsSession(
        context: Context,
        onEventReceived: (Int) -> Unit,
        customTabsSessionCallback: (CustomTabsSession?) -> Unit
    ) {
        val packageName = getDefaultCustomTabsPackageName(context)
        packageName?.let {
            initializeCustomTabsSession(
                context,
                it,
                onEventReceived,
                customTabsSessionCallback
            )
        } ?: customTabsSessionCallback(null)
    }
}