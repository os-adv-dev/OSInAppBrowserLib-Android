package com.outsystems.plugins.inappbrowser.osinappbrowserlib.views

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import com.outsystems.plugins.inappbrowser.osinappbrowserlib.helpers.OSIABCustomTabsEvent
import com.outsystems.plugins.inappbrowser.osinappbrowserlib.helpers.OSIABCustomTabsEventBus


class OSIABCustomTabsControllerActivity: Activity() {
    companion object {
        const val ACTION_CUSTOM_TABS_DESTROYED = "com.outsystems.plugins.inappbrowser.osinappbrowserlib.ACTION_CUSTOM_TABS_DESTROYED"
        const val ACTION_CUSTOM_TABS_READY = "com.outsystems.plugins.inappbrowser.osinappbrowserlib.ACTION_CUSTOM_TABS_READY"
        const val ACTION_CLOSE_CUSTOM_TABS = "com.outsystems.plugins.inappbrowser.osinappbrowserlib.ACTION_CLOSE_CUSTOM_TABS"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)

        if (intent.getBooleanExtra(ACTION_CLOSE_CUSTOM_TABS, false)) {
            finish()
        }
        else {
            OSIABCustomTabsEventBus.postEvent(OSIABCustomTabsEvent(ACTION_CUSTOM_TABS_READY, this))
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if (intent.getBooleanExtra(ACTION_CLOSE_CUSTOM_TABS, false)) {
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        OSIABCustomTabsEventBus.postEvent(OSIABCustomTabsEvent(ACTION_CUSTOM_TABS_DESTROYED, this))
    }
}