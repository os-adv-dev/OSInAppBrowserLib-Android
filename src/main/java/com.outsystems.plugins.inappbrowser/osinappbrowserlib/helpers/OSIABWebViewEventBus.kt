package com.outsystems.plugins.inappbrowser.osinappbrowserlib.helpers

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.outsystems.plugins.inappbrowser.osinappbrowserlib.views.OSIABWebViewActivity

object OSIABWebViewEventBus {
    private val _event = object: MutableLiveData<OSIABWebViewActivity>() {
        override fun observe(owner: LifecycleOwner, observer: Observer<in OSIABWebViewActivity>) {
            super.observe(owner) { value ->
                if (value != null) {
                    observer.onChanged(value)
                    postValue(null)
                }
            }
        }
    }
    val event: LiveData<OSIABWebViewActivity> = _event

    fun postEvent(event: OSIABWebViewActivity) {
        _event.postValue(event)
    }


}