package com.outsystems.plugins.inappbrowser.osinappbrowserlib.helpers

import android.content.ComponentName
import android.content.Context
import androidx.browser.customtabs.CustomTabsSession
import com.outsystems.plugins.inappbrowser.osinappbrowserlib.OSIABEvents

class OSIABCustomTabsSessionHelperMock: OSIABCustomTabsSessionHelperInterface {
    private val componentName = "OSIABTestComponent"
    var eventToReturn: OSIABEvents? = null

    override suspend fun generateNewCustomTabsSession(
        context: Context, 
        onEventReceived: (OSIABEvents) -> Unit,
        customTabsSessionCallback: (CustomTabsSession?) -> Unit
    ) {
        if (eventToReturn != null) {
            onEventReceived(eventToReturn!!)
        }
        customTabsSessionCallback(CustomTabsSession.createMockSessionForTesting(ComponentName(context, componentName)))
    }
}