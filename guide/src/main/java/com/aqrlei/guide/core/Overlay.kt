package com.aqrlei.guide.core

import android.view.View
import android.view.animation.Animation

/**
 * Created by AqrLei on 2019-08-20
 */
class Overlay {

    var layoutResId: Int = 0
    var backgroundColor: Int = 0
    var enterAnimation: Animation? = null
    var exitAnimation: Animation? = null
    var onOverlayInflatedListener: OnOverlayInflatedListener? = null

    internal var overlayView: View? = null
    internal var guideController: Controller? = null
    fun setLayoutResId(block: () -> Int) {
        layoutResId = block()
    }

    fun setBackgroundColor(block: () -> Int) {
        backgroundColor = block()
    }

    fun setEnterAnimation(block: () -> Animation) {
        enterAnimation = block()
    }

    fun setExitAnimation(block: () -> Animation) {
        exitAnimation = block()
    }

    internal fun notifyOverlayInflatedListener(view: View, controller: Controller) {
        overlayView = view
        guideController = controller
        onOverlayInflatedListener?.onOverlayInflated(view, controller)
    }

    fun onOverlayInflatedListener(block: (View, Controller) -> Unit) {
        onOverlayInflatedListener = object :
            OnOverlayInflatedListener {
            override fun onOverlayInflated(view: View, controller: Controller) {
                block(view, controller)
            }
        }
    }

    interface OnOverlayInflatedListener {
        fun onOverlayInflated(view: View, controller: Controller)
    }
}