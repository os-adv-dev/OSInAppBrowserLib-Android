package com.outsystems.plugins.inappbrowser.osinappbrowserlib.helpers

import android.content.ComponentName
import android.content.Context
import androidx.browser.customtabs.CustomTabsSession
import kotlinx.coroutines.CoroutineScope

class OSIABCustomTabsSessionHelperMock: OSIABCustomTabsSessionHelperInterface {
    private val componentName = "OSIABTestComponent"

    override suspend fun generateNewCustomTabsSession(
        context: Context,
        lifecycleScope: CoroutineScope,
        customTabsSessionCallback: (CustomTabsSession?) -> Unit
    ) {
        customTabsSessionCallback(CustomTabsSession.createMockSessionForTesting(ComponentName(context, componentName)))
    }
}