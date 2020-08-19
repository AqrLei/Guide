package com.aqrlei.guide.lifecycle

/**
 * Created by AqrLei on 2019-08-23
 */
class ActivityFragmentLifecycle : Lifecycle {
    private val lifecycleListenerList = mutableListOf<LifecycleListener>()
    private var lifecycleStateEnum: LifecycleStateEnum =
        LifecycleStateEnum.NONE
    override fun addLifecycleListener(lifecycleListener: LifecycleListener) {
        lifecycleListenerList.add(lifecycleListener)
        when (lifecycleStateEnum) {
            LifecycleStateEnum.START -> lifecycleListener.onStart()
            LifecycleStateEnum.STOP -> lifecycleListener.onStop()
            LifecycleStateEnum.DESTROY -> lifecycleListener.onDestroy()
            else -> {
            }
        }
    }

    override fun removeLifecycleListener(lifecycleListener: LifecycleListener) {
        lifecycleListenerList.remove(lifecycleListener)
    }

    override fun removeAllLifecycleListener() {
        lifecycleListenerList.clear()
    }

    fun onStart() {
        lifecycleStateEnum =
            LifecycleStateEnum.START
        lifecycleListenerList.forEach { it.onStart() }
    }

    fun onStop() {
        lifecycleStateEnum =
            LifecycleStateEnum.STOP
        lifecycleListenerList.forEach { it.onStop() }
    }

    fun onDestroy() {
        lifecycleStateEnum =
            LifecycleStateEnum.DESTROY
        lifecycleListenerList.forEach { it.onDestroy() }
    }
}