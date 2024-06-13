package com.outsystems.plugins.inappbrowser.osinappbrowserlib

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.outsystems.plugins.inappbrowser.osinappbrowserlib.models.OSIABCustomTabsOptions
import com.outsystems.plugins.inappbrowser.osinappbrowserlib.routeradapters.OSIABCustomTabsRouterAdapter
import com.outsystems.plugins.inappbrowser.osinappbrowserlib.routeradapters.OSIABExternalBrowserRouterAdapter

class OSIABEngine(
    private val externalBrowserRouter: OSIABRouter<Unit, Boolean>,
    private val customTabsRouter: OSIABRouter<OSIABCustomTabsOptions, Boolean>
) {

    init {
        if(customTabsRouter is OSIABCustomTabsRouterAdapter) {
            customTabsRouter.initializeCustomTabsSession()
        }
    }

    /**
     * Trigger the external browser to open the passed `url`.
     * @param url URL to be opened.
     * @param completionHandler The callback with the result of opening the url using the External Browser.
     * @return Indicates if the operation was successful or not.
     */
    fun openExternalBrowser(url: String, completionHandler: (Boolean) -> Unit) {
        return externalBrowserRouter.handleOpen(url, null, completionHandler)
    }

    /**
     * Trigger the Custom Tabs to open the passed `url`.
     * @param url URL to be opened.
     * @param options Customization options to apply to the browser.
     * @param completionHandler The callback with the result of opening the url using Custom Tabs.
     * @return Indicates if the operation was successful or not.
     */
    fun openCustomTabs(url: String, options: OSIABCustomTabsOptions? = null, completionHandler: (Boolean) -> Unit) {
        return customTabsRouter.handleOpen(url, options, completionHandler)
    }
}

fun Context.canOpenURL(uri: Uri): Boolean {
    val intent = Intent(Intent.ACTION_VIEW, uri)
    return intent.resolveActivity(packageManager) != null
}