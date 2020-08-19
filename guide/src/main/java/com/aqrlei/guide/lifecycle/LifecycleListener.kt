package com.aqrlei.guide.lifecycle

import java.io.Serializable

/**
 * Created by AqrLei on 2019-08-23
 */
interface LifecycleListener : Serializable {
    fun onStart()

    fun onStop()

    fun onDestroy()
}

open class SimpleLifecycleListener :
    LifecycleListener {
    override fun onStart() {}

    override fun onStop() {}

    override fun onDestroy() {}
}