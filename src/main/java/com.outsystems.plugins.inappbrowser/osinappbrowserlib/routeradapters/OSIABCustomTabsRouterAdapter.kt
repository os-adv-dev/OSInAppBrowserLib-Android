package com.outsystems.plugins.inappbrowser.osinappbrowserlib.routeradapters

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.browser.customtabs.CustomTabsClient
import androidx.browser.customtabs.CustomTabsIntent
import androidx.browser.customtabs.CustomTabsService
import androidx.browser.customtabs.CustomTabsServiceConnection
import androidx.browser.customtabs.CustomTabsSession
import com.outsystems.plugins.inappbrowser.osinappbrowserlib.OSIABRouter
import com.outsystems.plugins.inappbrowser.osinappbrowserlib.canOpenURL
import com.outsystems.plugins.inappbrowser.osinappbrowserlib.models.OSIABAnimation
import com.outsystems.plugins.inappbrowser.osinappbrowserlib.models.OSIABCustomTabsOptions
import com.outsystems.plugins.inappbrowser.osinappbrowserlib.models.OSIABViewStyle

class OSIABCustomTabsRouterAdapter(private val context: Context) : OSIABRouter<OSIABCustomTabsOptions, Boolean> {
    private var customTabsSession: CustomTabsSession? = null
    private val CHROME_PACKAGE_NAME = "com.android.chrome"

    private fun getDefaultCustomTabsPackageName(): String? {
        val activityIntent = Intent(Intent.ACTION_VIEW, Uri.parse("http://"))
        val resolvedActivityList = context.packageManager.queryIntentActivities(activityIntent, 0)
        val packagesSupportingCustomTabs = mutableListOf<String>()

        for (info in resolvedActivityList) {
            val serviceIntent = Intent().apply {
                action = CustomTabsService.ACTION_CUSTOM_TABS_CONNECTION
                `package` = info.activityInfo.packageName
            }
            if (context.packageManager.resolveService(serviceIntent, 0) != null) {
                packagesSupportingCustomTabs.add(info.activityInfo.packageName)
            }
        }

        return if (packagesSupportingCustomTabs.isNotEmpty()) {
            packagesSupportingCustomTabs[0]
        } else {
            CHROME_PACKAGE_NAME
        }
    }

    fun initializeCustomTabsSession() {
        CustomTabsClient.bindCustomTabsService(context, getDefaultCustomTabsPackageName(), object : CustomTabsServiceConnection() {
            override fun onCustomTabsServiceConnected(name: ComponentName, client: CustomTabsClient) {
                client.warmup(0L)
                customTabsSession = client.newSession(null)
            }

            override fun onServiceDisconnected(name: ComponentName) {
                customTabsSession = null
            }
        })
    }

    override fun handleOpen(url: String, options: OSIABCustomTabsOptions?, completionHandler: (Boolean) -> Unit) {
        try {
            val uri = Uri.parse(url)
            if (!context.canOpenURL(uri)) {
                completionHandler(false)
                return
            }

            val builder = CustomTabsIntent.Builder(customTabsSession)

            options?.let {
                builder.setShowTitle(it.showTitle)
                builder.setUrlBarHidingEnabled(it.hideToolbarOnScroll)

                when (it.startAnimation) {
                    OSIABAnimation.FADE_IN -> builder.setStartAnimations(context, android.R.anim.fade_in, android.R.anim.fade_out)
                    OSIABAnimation.FADE_OUT -> builder.setStartAnimations(context, android.R.anim.fade_out, android.R.anim.fade_in)
                    OSIABAnimation.SLIDE_IN_LEFT -> builder.setStartAnimations(context, android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                    OSIABAnimation.SLIDE_OUT_RIGHT -> builder.setStartAnimations(context, android.R.anim.slide_out_right, android.R.anim.slide_in_left)
                }

                when (it.exitAnimation) {
                    OSIABAnimation.FADE_IN -> builder.setExitAnimations(context, android.R.anim.fade_in, android.R.anim.fade_out)
                    OSIABAnimation.FADE_OUT -> builder.setExitAnimations(context, android.R.anim.fade_out, android.R.anim.fade_in)
                    OSIABAnimation.SLIDE_IN_LEFT -> builder.setExitAnimations(context, android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                    OSIABAnimation.SLIDE_OUT_RIGHT -> builder.setExitAnimations(context, android.R.anim.slide_out_right, android.R.anim.slide_in_left)
                }

                when (it.viewStyle) {
                    OSIABViewStyle.BOTTOM_SHEET -> {
                        it.bottomSheetOptions?.let { bottomSheetOptions ->
                            if (bottomSheetOptions.isFixed) {
                                builder.setInitialActivityHeightPx(bottomSheetOptions.height, CustomTabsIntent.ACTIVITY_HEIGHT_FIXED)
                            }
                            else {
                                builder.setInitialActivityHeightPx(bottomSheetOptions.height)
                            }
                        }
                    }
                    OSIABViewStyle.FULL_SCREEN -> {
                        // Full screen style is the default
                    }
                }
            }

            val customTabsIntent = builder.build()
            customTabsIntent.launchUrl(context, uri)
            completionHandler(true)
        } catch (e: Exception) {
            completionHandler(false)
        }
    }
}
