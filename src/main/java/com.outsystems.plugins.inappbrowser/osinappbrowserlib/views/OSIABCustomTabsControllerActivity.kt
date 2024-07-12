package com.outsystems.plugins.inappbrowser.osinappbrowserlib.views

import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.outsystems.plugins.inappbrowser.osinappbrowserlib.OSIABEvents
import com.outsystems.plugins.inappbrowser.osinappbrowserlib.OSIABEvents.OSIABCustomTabsEvent
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class OSIABCustomTabsControllerActivity: AppCompatActivity() {
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
            sendCustomTabsEvent(
                OSIABCustomTabsEvent(ACTION_CUSTOM_TABS_READY, this@OSIABCustomTabsControllerActivity)
            )
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        window.setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)

        if (intent.getBooleanExtra(ACTION_CLOSE_CUSTOM_TABS, false)) {
            finish()
        }
        else {
            sendCustomTabsEvent(
                OSIABCustomTabsEvent(ACTION_CUSTOM_TABS_READY, this@OSIABCustomTabsControllerActivity)
            )
        }
    }

    override fun onDestroy() {
        sendCustomTabsEvent(
            OSIABCustomTabsEvent(ACTION_CUSTOM_TABS_DESTROYED, this@OSIABCustomTabsControllerActivity)
        )
        super.onDestroy()
    }

    /** Responsible for sending events using Kotlin Flows.
     * @param event object to broadcast to the event bus
     */
    @OptIn(DelicateCoroutinesApi::class)
    private fun sendCustomTabsEvent(event: OSIABCustomTabsEvent) {
        // Use GlobalScope so we can send destroyed event without relying on this activity lifecycle
        GlobalScope.launch(Dispatchers.IO) {
            OSIABEvents.postEvent(event)
        }
    }
}