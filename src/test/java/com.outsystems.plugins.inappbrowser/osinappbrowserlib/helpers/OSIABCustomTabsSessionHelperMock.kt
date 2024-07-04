package com.outsystems.plugins.inappbrowser.osinappbrowserlib.helpers

/*class OSIABCustomTabsSessionHelperMock: OSIABCustomTabsSessionHelperInterface {
    private val componentName = "OSIABTestComponent"
    var eventToReturn: Int? = null

    override suspend fun generateNewCustomTabsSession(context: Context, onEventReceived: (Int) -> Unit): CustomTabsSession {
        if (eventToReturn != null) {
            onEventReceived(eventToReturn!!)
        }
        return CustomTabsSession.createMockSessionForTesting(ComponentName(context, componentName))
    }
}*/