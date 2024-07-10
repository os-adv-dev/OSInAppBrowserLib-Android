package com.outsystems.plugins.inappbrowser.osinappbrowserlib

import android.content.Context
import android.content.Intent
import android.net.Uri

class OSIABEngine {

    private var activeRouter: OSIABRouter<Boolean>? = null

    /**
     * Trigger the external browser to open the passed `url`.
     * @param externalBrowserRouter Router responsible for handling the external browser opening logic.
     * @param url URL to be opened.
     * @param completionHandler The callback with the result of opening the url using the External Browser.
     * @return Indicates if the operation was successful or not.
     */
    fun openExternalBrowser(
        externalBrowserRouter: OSIABRouter<Boolean>,
        url: String,
        completionHandler: (Boolean) -> Unit
    ) {
        return externalBrowserRouter.handleOpen(url, completionHandler)
    }

    /**
     * Trigger the Custom Tabs to open the passed `url`.
     * @param customTabsRouter Router responsible for handling the Custom Tabs (system browser) opening logic.
     * @param url URL to be opened
     * @param completionHandler The callback with the result of opening the url using Custom Tabs.
     * @return Indicates if the operation was successful or not.
     */
    fun openCustomTabs(
        customTabsRouter: OSIABRouter<Boolean>,
        url: String,
        completionHandler: (Boolean) -> Unit
    ) {
        activeRouter = customTabsRouter
        return customTabsRouter.handleOpen(url, completionHandler)
    }

    /**
     * Trigger the WebView to open the passed `url`.
     * @param webViewRouter Router responsible for handling the WebView opening logic.
     * @param url URL to be opened.
     * @param completionHandler The callback with the result of opening the url using the WebView.
     */
    fun openWebView(
        webViewRouter: OSIABRouter<Boolean>,
        url: String,
        completionHandler: (Boolean) -> Unit
    ) {
        activeRouter = webViewRouter
        return webViewRouter.handleOpen(url, completionHandler)
    }

    /**
     * Closes the currently opened view if there is one.
     * @param completionHandler The callback with the result of closing the opened view.
     */
    fun close(completionHandler: (Boolean) -> Unit) {
        if(activeRouter is OSIABClosable) {
            (activeRouter as OSIABClosable).close()
            activeRouter = null
            completionHandler(true)
        }
        else {
            completionHandler(false)
        }
    }
}

fun Context.canOpenURL(uri: Uri): Boolean {
    val intent = Intent(Intent.ACTION_VIEW, uri)
    return intent.resolveActivity(packageManager) != null
}
