package com.outsystems.plugins.inappbrowser.osinappbrowserlib.routeradapters

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import androidx.browser.customtabs.CustomTabsSession
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import com.outsystems.plugins.inappbrowser.osinappbrowserlib.OSIABClosable
import com.outsystems.plugins.inappbrowser.osinappbrowserlib.OSIABEvents
import com.outsystems.plugins.inappbrowser.osinappbrowserlib.OSIABRouter
import com.outsystems.plugins.inappbrowser.osinappbrowserlib.canOpenURL
import com.outsystems.plugins.inappbrowser.osinappbrowserlib.helpers.OSIABCustomTabsEvent
import com.outsystems.plugins.inappbrowser.osinappbrowserlib.helpers.OSIABCustomTabsEventBus
import com.outsystems.plugins.inappbrowser.osinappbrowserlib.helpers.OSIABCustomTabsSessionHelper
import com.outsystems.plugins.inappbrowser.osinappbrowserlib.helpers.OSIABCustomTabsSessionHelperInterface
import com.outsystems.plugins.inappbrowser.osinappbrowserlib.models.OSIABAnimation
import com.outsystems.plugins.inappbrowser.osinappbrowserlib.models.OSIABCustomTabsOptions
import com.outsystems.plugins.inappbrowser.osinappbrowserlib.models.OSIABViewStyle
import com.outsystems.plugins.inappbrowser.osinappbrowserlib.views.OSIABCustomTabsControllerActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class OSIABCustomTabsRouterAdapter(
    private val context: Context,
    private val lifecycleOwner: LifecycleOwner,
    private val lifecycleScope: CoroutineScope,
    private val customTabsSessionHelper: OSIABCustomTabsSessionHelperInterface = OSIABCustomTabsSessionHelper(),
    private val options: OSIABCustomTabsOptions,
    private val onBrowserPageLoaded: () -> Unit,
    private val onBrowserFinished: () -> Unit
) : OSIABRouter<Boolean>, OSIABClosable {

    // for the browserPageLoaded event, which we only want to trigger on the first URL loaded in the CustomTabs instance
    private var isFirstLoad = true

    override fun close() {
        val intent = Intent(context, OSIABCustomTabsControllerActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_NO_ANIMATION or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            putExtra(OSIABCustomTabsControllerActivity.ACTION_CLOSE_CUSTOM_TABS, true)
        }

        context.startActivity(intent)
    }

    private fun buildCustomTabsIntent(customTabsSession: CustomTabsSession?): CustomTabsIntent {
        val builder = CustomTabsIntent.Builder(customTabsSession)

        builder.setShowTitle(options.showTitle)
        builder.setUrlBarHidingEnabled(options.hideToolbarOnScroll)

        when (options.startAnimation) {
            OSIABAnimation.FADE_IN -> builder.setStartAnimations(
                context,
                android.R.anim.fade_in,
                android.R.anim.fade_out
            )

            OSIABAnimation.FADE_OUT -> builder.setStartAnimations(
                context,
                android.R.anim.fade_out,
                android.R.anim.fade_in
            )

            OSIABAnimation.SLIDE_IN_LEFT -> builder.setStartAnimations(
                context,
                android.R.anim.slide_in_left,
                android.R.anim.slide_out_right
            )

            OSIABAnimation.SLIDE_OUT_RIGHT -> builder.setStartAnimations(
                context,
                android.R.anim.slide_out_right,
                android.R.anim.slide_in_left
            )
        }

        when (options.exitAnimation) {
            OSIABAnimation.FADE_IN -> builder.setExitAnimations(
                context,
                android.R.anim.fade_out,
                android.R.anim.fade_in
            )

            OSIABAnimation.FADE_OUT -> builder.setExitAnimations(
                context,
                android.R.anim.fade_in,
                android.R.anim.fade_out
            )

            OSIABAnimation.SLIDE_IN_LEFT -> builder.setExitAnimations(
                context,
                android.R.anim.slide_out_right,
                android.R.anim.slide_in_left
            )

            OSIABAnimation.SLIDE_OUT_RIGHT -> builder.setExitAnimations(
                context,
                android.R.anim.slide_in_left,
                android.R.anim.slide_out_right
            )
        }

        if (options.viewStyle == OSIABViewStyle.BOTTOM_SHEET) {
            options.bottomSheetOptions?.let { bottomSheetOptions ->
                if (bottomSheetOptions.isFixed) {
                    builder.setInitialActivityHeightPx(
                        bottomSheetOptions.height,
                        CustomTabsIntent.ACTIVITY_HEIGHT_FIXED
                    )
                } else {
                    builder.setInitialActivityHeightPx(bottomSheetOptions.height)
                }
            }
        }

        builder.setBackgroundInteractionEnabled(true)

        return builder.build()
    }

    override fun handleOpen(url: String, completionHandler: (Boolean) -> Unit) {
        lifecycleScope.launch {
            try {
                val uri = Uri.parse(url)
                if (!context.canOpenURL(uri)) {
                    completionHandler(false)
                    return@launch
                }

                customTabsSessionHelper.generateNewCustomTabsSession(
                    context,
                    onEventReceived = { event ->
                        when (event) {
                            OSIABEvents.BrowserPageLoaded -> {
                                if (isFirstLoad) {
                                    onBrowserPageLoaded()
                                    isFirstLoad = false
                                }
                            }
                            OSIABEvents.BrowserFinished -> {
                                onBrowserFinished()
                            }
                        }
                    },
                    customTabsSessionCallback = {
                        val customTabsIntent = buildCustomTabsIntent(it)
                        registerEventBus(customTabsIntent, uri, completionHandler)

                        // This will force close any previous custom tabs activities.
                        // The event bus will detect when to launch the new custom tabs instance
                        close()
                    }
                )
            } catch (e: Exception) {
                completionHandler(false)
            }
        }
    }

    private fun registerEventBus(
        customTabsIntent: CustomTabsIntent,
        uri: Uri,
        completionHandler: (Boolean) -> Unit
    ) {
        val observer = Observer<OSIABCustomTabsEvent> { value ->
            if (value.action == OSIABCustomTabsControllerActivity.ACTION_CUSTOM_TABS_READY) {
                try {
                    customTabsIntent.launchUrl(value.context, uri)
                    completionHandler(true)
                } catch (e: Exception) {
                    completionHandler(false)
                }

                // Remove the observer after handling the launch
                OSIABCustomTabsEventBus.event.removeObservers(lifecycleOwner)
            } else if(value.action == OSIABCustomTabsControllerActivity.ACTION_CUSTOM_TABS_DESTROYED) {
                // Relaunch Custom Tabs Controller
                val customTabsControllerIntent = Intent(context, OSIABCustomTabsControllerActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(customTabsControllerIntent)
            }
        }

        OSIABCustomTabsEventBus.event.observe(lifecycleOwner, observer)
    }
}
