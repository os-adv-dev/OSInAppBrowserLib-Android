package com.outsystems.plugins.inappbrowser.osinappbrowserlib

/*
@RunWith(RobolectricTestRunner::class)
class OSIABCustomTabsRouterAdapterTests {
    private val activityName = "OSIABTestActivity"
    private val packageName = "com.outsystems.plugins.inappbrowser.osinappbrowserlib"
    private val uri = Uri.parse("https://www.outsystems.com/")
    private val options = OSIABCustomTabsOptions()

    @Test
    fun test_handleOpen_withValidURL_launchesCustomTab() {
        runTest(StandardTestDispatcher()) {
            val context = mockContext(useValidURL = true, ableToOpenURL = true)
            val sut = OSIABCustomTabsRouterAdapter(
                context = context,
                lifecycleScope = this,
                customTabsSessionHelper = OSIABCustomTabsSessionHelperMock(),
                options = options,
                {},
                {}
            )

            sut.handleOpen(uri.toString()) { success ->
                assertTrue(success)
            }
        }
    }


    @Test
    fun test_handleOpen_withInvalidURL_returnsFalse() {
        runTest(StandardTestDispatcher()) {
            val context = mockContext(useValidURL = false, ableToOpenURL = false)
            val sut = OSIABCustomTabsRouterAdapter(
                context = context,
                lifecycleScope = this,
                customTabsSessionHelper = OSIABCustomTabsSessionHelperMock(),
                options = options,
                {},
                {}
            )

            sut.handleOpen("invalid_url") { success ->
                assertFalse(success)
            }
        }
    }


    @Test
    fun test_handleOpen_withValidURLButException_returnsFalse() {
        runTest(StandardTestDispatcher()) {
            val context = mock(Context::class.java)
            val packageManager = mock(android.content.pm.PackageManager::class.java)
            `when`(context.packageManager).thenReturn(packageManager)

            val sut = OSIABCustomTabsRouterAdapter(
                context = context,
                lifecycleScope = this,
                customTabsSessionHelper = OSIABCustomTabsSessionHelperMock(),
                options = options,
                {},
                {}
            )

            `when`(packageManager.resolveActivity(any(Intent::class.java), anyInt())).thenReturn(
                ResolveInfo()
            )

            doThrow(RuntimeException("Exception")).`when`(context)
                .startActivity(any(Intent::class.java))

            sut.handleOpen(uri.toString()) { success ->
                assertFalse(success)
            }
        }
    }

    @Test
    fun test_handleOpen_withValidURL_launchesCustomTab_when_browserPageLoaded_then_browserPageLoadedTriggered() {
        runTest(StandardTestDispatcher()) {
            val context = mockContext(useValidURL = true, ableToOpenURL = true)
            val sut = OSIABCustomTabsRouterAdapter(
                context = context,
                lifecycleScope = this,
                customTabsSessionHelper = OSIABCustomTabsSessionHelperMock().apply {
                    eventToReturn = CustomTabsCallback.NAVIGATION_FINISHED
                },
                options = options,
                onBrowserPageLoaded = {
                    assertTrue(true) // onBrowserPageLoaded was called
                },
                onBrowserFinished = {
                    fail()
                }
            )

            sut.handleOpen(uri.toString()) { success ->
                assertTrue(success)
            }
        }
    }

    @Test
    fun test_handleOpen_withValidURL_launchesCustomTab_when_browserFinished_then_browserFinishedTriggered() {
        runTest(StandardTestDispatcher()) {
            val context = mockContext(useValidURL = true, ableToOpenURL = true)
            val sut = OSIABCustomTabsRouterAdapter(
                context = context,
                lifecycleScope = this,
                customTabsSessionHelper = OSIABCustomTabsSessionHelperMock().apply {
                    eventToReturn = CustomTabsCallback.TAB_HIDDEN
                },
                options = options,
                onBrowserPageLoaded = {
                    fail()
                },
                onBrowserFinished = {
                    assertTrue(true) // onBrowserFinished was called
                }
            )

            sut.handleOpen(uri.toString()) { success ->
                assertTrue(success)
            }
        }
    }

    private fun mockContext(useValidURL: Boolean, ableToOpenURL: Boolean = false): Context {
        val context = mock(Context::class.java)
        val packageManager = mock(android.content.pm.PackageManager::class.java)

        val resolveInfo = if (useValidURL && ableToOpenURL) {
            val resolveInfo = ResolveInfo()
            val activityInfo = ActivityInfo()
            val applicationInfo = ApplicationInfo()

            applicationInfo.packageName = packageName

            activityInfo.applicationInfo = applicationInfo
            activityInfo.packageName = packageName
            activityInfo.name = activityName

            resolveInfo.activityInfo = activityInfo
            resolveInfo
        } else null

        `when`(context.packageManager).thenReturn(packageManager)
        `when`(packageManager.resolveActivity(any(Intent::class.java), anyInt())).thenReturn(resolveInfo)

        return context
    }
}
*/