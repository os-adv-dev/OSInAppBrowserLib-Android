package com.outsystems.plugins.inappbrowser.osinappbrowserlib.routeradapters

import android.content.Context
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import com.outsystems.plugins.inappbrowser.osinappbrowserlib.OSIABRouter

class OSIABCustomTabsRouterAdapter(private val context: Context) : OSIABRouter<Boolean> {

    override fun handleOpen(url: String, completionHandler: (Boolean) -> Unit) {
        val uri = Uri.parse(url)
        val customTabsIntent = CustomTabsIntent.Builder().build()
        try {
            customTabsIntent.launchUrl(context, uri)
            completionHandler(true)
        } catch (e: Exception) {
            completionHandler(false)
        }
    }
}
