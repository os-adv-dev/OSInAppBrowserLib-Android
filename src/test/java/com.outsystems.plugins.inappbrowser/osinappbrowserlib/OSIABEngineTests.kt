package com.outsystems.plugins.inappbrowser.osinappbrowserlib

import com.outsystems.plugins.inappbrowser.osinappbrowserlib.helpers.OSIABRouterSpy
import org.junit.Test
import org.junit.Assert.*

class OSIABEngineTests {
    private val url = "https://www.outsystems.com/"

    @Test
    fun test_open_externalBrowserWithoutIssues_doesOpenBrowser() {
        val routerSpy = OSIABRouterSpy(true)
        makeSUT().openExternalBrowser(url, routerSpy) { result ->
            assertTrue(result)
        }
    }

    @Test
    fun test_open_externalBrowserWithIssues_doesNotOpenBrowser() {
        val routerSpy = OSIABRouterSpy(false)
        makeSUT().openExternalBrowser(url, routerSpy) { result ->
            assertFalse(result)
        }
    }

    @Test
    fun test_open_customTabsWithoutIssues_doesOpenBrowser() {
        val routerSpy = OSIABRouterSpy(true)
        makeSUT().openCustomTabs(url, routerSpy) { result ->
            assertTrue(result)
        }
    }

    @Test
    fun test_open_customTabsWithIssues_doesNotOpenBrowser() {
        val routerSpy = OSIABRouterSpy(false)
        makeSUT().openCustomTabs(url, routerSpy) { result ->
            assertFalse(result)
        }
    }

    private fun makeSUT(): OSIABEngine<OSIABRouterSpy, OSIABRouterSpy> {
        return OSIABEngine()
    }
}
