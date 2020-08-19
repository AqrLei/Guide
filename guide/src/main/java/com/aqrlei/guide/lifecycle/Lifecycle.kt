package com.aqrlei.guide.lifecycle

/**
 * Created by AqrLei on 2019-08-23
 */
interface Lifecycle {
    fun addLifecycleListener(lifecycleListener: LifecycleListener)
    fun removeLifecycleListener(lifecycleListener: LifecycleListener)
    fun removeAllLifecycleListener()
}