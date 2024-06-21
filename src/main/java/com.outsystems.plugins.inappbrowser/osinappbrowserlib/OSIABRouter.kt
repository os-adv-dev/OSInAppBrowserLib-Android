package com.outsystems.plugins.inappbrowser.osinappbrowserlib

interface OSIABRouter<OptionsType, ReturnType> {

    /**
     * Handles opening the passed `url`.
     * @param url URL to be opened.
     * @param completionHandler The callback with the result of opening the url.
     */
    fun handleOpen(url: String, options: OptionsType? = null, callbackID: String? = null, completionHandler: (ReturnType) -> Unit)

}