package com.outsystems.plugins.inappbrowser.osinappbrowserlib

import com.outsystems.plugins.inappbrowser.osinappbrowserlib.helpers.OSIABRouterSpy
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class OSIABEngineTests {
    private val url = "https://www.outsystems.com/"

    @Test
    fun test_open_externalBrowserWithoutIssues_doesOpenBrowser() {
        val router = makeSUT(true)
        OSIABEngine().openCustomTabs(router, url) { result ->
            assertTrue(result)
        }
    }

    @Test
    fun test_open_externalBrowserWithIssues_doesNotOpenBrowser() {
        val router = makeSUT(false)
        OSIABEngine().openCustomTabs(router, url) { result ->
            assertFalse(result)
        }
    }
    @Test
    fun test_open_customTabsWithoutIssues_doesOpenBrowser() {
        val router = makeSUT(true)
        OSIABEngine().openCustomTabs(router, url) { result ->
            assertTrue(result)
        }
    }

    @Test
    fun test_open_customTabsWithIssues_doesNotOpenBrowser() {
        val router = makeSUT(false)
        OSIABEngine().openCustomTabs(router, url) { result ->
            assertFalse(result)
        }
    }

    private fun makeSUT(shouldOpenBrowser: Boolean): OSIABRouter<Boolean> {
        return OSIABRouterSpy(shouldOpenBrowser)
    }
}
