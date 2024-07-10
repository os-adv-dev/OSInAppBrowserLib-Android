package com.outsystems.plugins.inappbrowser.osinappbrowserlib.helpers

import android.content.Context
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer

object OSIABCustomTabsEventBus {
    private val _event = object: MutableLiveData<OSIABCustomTabsEvent>() {
        override fun observe(owner: LifecycleOwner, observer: Observer<in OSIABCustomTabsEvent>) {
            super.observe(owner) { value ->
                if (value != null) {
                    observer.onChanged(value)
                    postValue(null)
                }
            }
        }
    }
    val event: LiveData<OSIABCustomTabsEvent> = _event

    fun postEvent(event: OSIABCustomTabsEvent) {
        _event.postValue(event)
    }


}

data class OSIABCustomTabsEvent(val action: String, val context: Context)