package com.outsystems.plugins.inappbrowser.osinappbrowserlib.helpers

import android.content.Context
import androidx.browser.customtabs.CustomTabsSession
import kotlinx.coroutines.CoroutineScope

interface OSIABCustomTabsSessionHelperInterface {

    /**
     * Generates a new CustomTabsSession instance
     * @param context Context to use when initializing the CustomTabsSession
     * @param lifecycleScope Coroutine scope to use to post browser events
     * @param customTabsSessionCallback Callback to send the session instance (null if failed)
     */
    suspend fun generateNewCustomTabsSession(
        context: Context,
        lifecycleScope: CoroutineScope,
        customTabsSessionCallback: (CustomTabsSession?) -> Unit
    )
}