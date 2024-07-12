package com.outsystems.plugins.inappbrowser.osinappbrowserlib

import android.content.Context
import com.outsystems.plugins.inappbrowser.osinappbrowserlib.views.OSIABWebViewActivity
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

sealed class OSIABEvents {
    data object BrowserPageLoaded : OSIABEvents()
    data object BrowserFinished : OSIABEvents()

    data class OSIABCustomTabsEvent(val action: String, val context: Context) : OSIABEvents()
    data class OSIABWebViewEvent(val activity: OSIABWebViewActivity) : OSIABEvents()

    companion object {
        private val _events = MutableSharedFlow<OSIABEvents>()
        val events = _events.asSharedFlow()

        suspend fun postEvent(event: OSIABEvents) {
            _events.emit(event)
        }
    }

}