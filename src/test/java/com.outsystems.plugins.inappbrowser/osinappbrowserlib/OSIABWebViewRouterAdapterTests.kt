package com.outsystems.plugins.inappbrowser.osinappbrowserlib

import android.content.Context
import android.content.Intent
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import com.outsystems.plugins.inappbrowser.osinappbrowserlib.helpers.OSIABFlowHelperMock
import com.outsystems.plugins.inappbrowser.osinappbrowserlib.models.OSIABWebViewOptions
import com.outsystems.plugins.inappbrowser.osinappbrowserlib.routeradapters.OSIABWebViewRouterAdapter
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito
import org.mockito.Mockito.doThrow
import org.mockito.Mockito.mock
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class OSIABWebViewRouterAdapterTests {
    private val lifecycle = LifecycleRegistry(mock(LifecycleOwner::class.java))
    private val lifecycleOwner = mock(LifecycleOwner::class.java)

    private val url = "https://www.outsystems.com/"
    private val options = OSIABWebViewOptions()

    init {
        lifecycle.currentState = Lifecycle.State.RESUMED
        Mockito.`when`(lifecycleOwner.lifecycle).thenReturn(lifecycle)
    }

    @Test
    fun test_handleOpen_notAbleToOpenIt_returnsFalse() {
        runTest(StandardTestDispatcher()) {
            val context = mockContext(ableToOpenURL = false)
            val sut = OSIABWebViewRouterAdapter(
                context = context,
                lifecycleOwner = lifecycleOwner,
                lifecycleScope = this,
                options = options,
                flowHelper = OSIABFlowHelperMock(),
                onBrowserPageLoaded = {}, // do nothing
                onBrowserFinished = {}, // do nothing
            )

            sut.handleOpen(url) {
                assertFalse(it)
            }
        }
    }

    @Test
    fun test_handleOpen_ableToOpenIt_returnsTrue_and_when_browserPageLoads_then_browserPageLoadedTriggered() =
        runTest(StandardTestDispatcher()) {
            val context = mockContext(ableToOpenURL = true)
            val sut = OSIABWebViewRouterAdapter(
                context = context,
                lifecycleOwner = lifecycleOwner,
                lifecycleScope = this,
                options = options,
                flowHelper = OSIABFlowHelperMock(),
                onBrowserPageLoaded = {
                    assertTrue(true) // onBrowserPageLoaded was called
                },
                onBrowserFinished = {
                    fail()
                }
            )
            sut.handleOpen(url) {
                assertTrue(it)
            }
        }

    @Test
    fun test_handleOpen_ableToOpenIt_returnsTrue_and_when_browserFinished_then_browserFinishedTriggered() =
        runTest(StandardTestDispatcher()) {
            val context = mockContext(ableToOpenURL = true)
            val flowHelperMock = OSIABFlowHelperMock().apply { event = OSIABEvents.BrowserFinished }
            val sut = OSIABWebViewRouterAdapter(
                context = context,
                lifecycleOwner = lifecycleOwner,
                lifecycleScope = this,
                options = options,
                flowHelper = flowHelperMock,
                onBrowserPageLoaded = {
                    fail()
                },
                onBrowserFinished = {
                    assertTrue(true) // onBrowserFinished was called
                }
            )
            sut.handleOpen(url) {
                assertTrue(it)
            }
        }

    private fun mockContext(ableToOpenURL: Boolean = false): Context {
        val context = mock(Context::class.java)
        if (!ableToOpenURL) {
            doThrow(RuntimeException("Unable to open URL")).`when`(context).startActivity(any(Intent::class.java))
        }
        return context
    }

}
