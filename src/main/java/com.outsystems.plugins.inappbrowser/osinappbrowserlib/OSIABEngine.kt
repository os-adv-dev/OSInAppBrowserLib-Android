package com.outsystems.plugins.inappbrowser.osinappbrowserlib

class OSIABEngine<ExternalBrowser : OSIABRouter<Boolean>, CustomTabsBrowser : OSIABRouter<Boolean>> {
    /**
     * Trigger the external browser to open the passed `url`.
     * @param url URL to be opened.
     * @param completionHandler The callback with the result of opening the url using the External Browser.
     * @return Indicates if the operation was successful or not.
     */
    fun openExternalBrowser(url: String, routerDelegate: ExternalBrowser, completionHandler: (Boolean) -> Unit) {
        return routerDelegate.handleOpen(url, completionHandler)
    }

    /**
     * Trigger the Custom Tabs to open the passed `url`.
     * @param url URL to be opened.
     * @param completionHandler The callback with the result of opening the url using Custom Tabs.
     * @return Indicates if the operation was successful or not.
     */
    fun openCustomTabs(url: String, routerDelegate: CustomTabsBrowser, completionHandler: (Boolean) -> Unit) {
        return routerDelegate.handleOpen(url, completionHandler)
    }
}