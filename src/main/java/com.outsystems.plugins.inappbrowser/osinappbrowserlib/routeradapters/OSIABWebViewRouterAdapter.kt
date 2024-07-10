package com.outsystems.plugins.inappbrowser.osinappbrowserlib.routeradapters

import android.content.Context
import android.content.Intent
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import com.outsystems.plugins.inappbrowser.osinappbrowserlib.OSIABClosable
import com.outsystems.plugins.inappbrowser.osinappbrowserlib.OSIABEvents
import com.outsystems.plugins.inappbrowser.osinappbrowserlib.OSIABRouter
import com.outsystems.plugins.inappbrowser.osinappbrowserlib.helpers.OSIABFlowHelperInterface
import com.outsystems.plugins.inappbrowser.osinappbrowserlib.helpers.OSIABWebViewEventBus
import com.outsystems.plugins.inappbrowser.osinappbrowserlib.models.OSIABWebViewOptions
import com.outsystems.plugins.inappbrowser.osinappbrowserlib.views.OSIABWebViewActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference

class OSIABWebViewRouterAdapter(
    private val context: Context,
    private val lifecycleOwner: LifecycleOwner,
    private val lifecycleScope: CoroutineScope,
    private val options: OSIABWebViewOptions,
    private val flowHelper: OSIABFlowHelperInterface,
    private val onBrowserPageLoaded: () -> Unit,
    private val onBrowserFinished: () -> Unit
) : OSIABRouter<Boolean>, OSIABClosable {

    companion object {
        const val WEB_VIEW_URL_EXTRA = "WEB_VIEW_URL_EXTRA"
        const val WEB_VIEW_OPTIONS_EXTRA = "WEB_VIEW_OPTIONS_EXTRA"
    }

    private var webViewActivityRef: WeakReference<OSIABWebViewActivity>? = null

    private fun setWebViewActivity(activity: OSIABWebViewActivity?) {
        webViewActivityRef = if (activity == null) {
            null
        } else {
            WeakReference(activity)
        }
    }

    private fun getWebViewActivity(): OSIABWebViewActivity? {
        return webViewActivityRef?.get()
    }

    override fun close() {
        getWebViewActivity()?.finish()
        setWebViewActivity(null)
    }

    /**
     * Handles opening the passed `url` in the WebView.
     * @param url URL to be opened.
     * @param completionHandler The callback with the result of opening the url.
     */
    override fun handleOpen(url: String, completionHandler: (Boolean) -> Unit) {
        lifecycleScope.launch {
            try {
                // Ensure existing WebView instance is closed
                close()

                val observer = Observer<OSIABWebViewActivity> { value ->
                    setWebViewActivity(value)
                    completionHandler(true)
                    OSIABWebViewEventBus.event.removeObservers(lifecycleOwner)
                }

                OSIABWebViewEventBus.event.observe(lifecycleOwner, observer)

                context.startActivity(
                    Intent(
                        context, OSIABWebViewActivity::class.java
                    ).apply {
                        putExtra(WEB_VIEW_URL_EXTRA, url)
                        putExtra(WEB_VIEW_OPTIONS_EXTRA, options)
                    }
                )

                // Collect the browser events
                flowHelper.listenToEvents(lifecycleScope) { event ->
                    when (event) {
                        is OSIABEvents.BrowserPageLoaded -> {
                            onBrowserPageLoaded()
                        }

                        is OSIABEvents.BrowserFinished -> {
                            onBrowserFinished()
                        }
                    }
                }

            } catch (e: Exception) {
                completionHandler(false)
            }
        }
    }
}