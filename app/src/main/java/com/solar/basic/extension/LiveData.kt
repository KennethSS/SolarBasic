package com.solar.basic.extension

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Observer

inline fun <X,Y> LiveData<X>.map(crossinline block: (X) -> Y): LiveData<Y> {
    return MediatorLiveData<Y>().apply {
        addSource(this@map) {
            this.value = block.invoke(it)
        }
    }
}

inline fun <X, Y> LiveData<X>.switchMap(crossinline block: (X?) -> LiveData<Y>): LiveData<Y> {
    return MediatorLiveData<Y>().apply {
        addSource(this@switchMap, object : Observer<X> {
            var source: LiveData<Y>? = null
            override fun onChanged(x: X?) {
                val newLiveData = block.invoke(x)
                if (source === newLiveData) {
                    return
                }
                source?.let { source ->
                    removeSource(source)
                }
                source = newLiveData
                source?.let { source ->
                    addSource(source) { y -> value = y }
                }
            }
        })
    }
}