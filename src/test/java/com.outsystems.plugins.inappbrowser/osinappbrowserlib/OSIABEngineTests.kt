package com.outsystems.plugins.inappbrowser.osinappbrowserlib

import com.outsystems.plugins.inappbrowser.osinappbrowserlib.helpers.OSIABRouterSpy
import com.outsystems.plugins.inappbrowser.osinappbrowserlib.models.OSIABCustomTabsOptions
import org.junit.Test
import org.junit.Assert.*

class OSIABEngineTests {
    private val url = "https://www.outsystems.com/"

    @Test
    fun test_open_externalBrowserWithoutIssues_doesOpenBrowser() {
        makeSUT(true).openExternalBrowser(url) { result ->
            assertTrue(result)
        }
    }

    @Test
    fun test_open_externalBrowserWithIssues_doesNotOpenBrowser() {
        makeSUT(false).openExternalBrowser(url) { result ->
            assertFalse(result)
        }
    }
    @Test
    fun test_open_customTabsWithoutIssues_doesOpenBrowser() {
        makeSUT(true).openCustomTabs(url) { result ->
            assertTrue(result)
        }
    }

    @Test
    fun test_open_customTabsWithIssues_doesNotOpenBrowser() {
        makeSUT(false).openCustomTabs(url) { result ->
            assertFalse(result)
        }
    }

    private fun makeSUT(shouldOpenBrowser: Boolean): OSIABEngine {
        val externalBrowserRouterSpy = OSIABRouterSpy<Unit>(shouldOpenBrowser)
        val customTabsRouterSpy = OSIABRouterSpy<OSIABCustomTabsOptions>(shouldOpenBrowser)
        return OSIABEngine(externalBrowserRouterSpy, customTabsRouterSpy)
    }
}
