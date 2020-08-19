package com.aqrlei.guide.core

import android.graphics.Canvas
import android.graphics.RectF
import android.view.View

/**
 * Created by AqrLei on 2019-08-20
 */
class GuidePage {

    var isDisplayOnWindow: Boolean = false
    internal var overlay: Overlay? = null

    internal var hole: Hole? = null

    internal var everywhereCancelable: Boolean = true

    private var overlayWithHoleDrawListener: OverlayWithHoleDrawListener? = null

    fun overlay(block: Overlay.() -> Unit) {
        overlay = Overlay().apply(block)
    }

    fun hole(block: Hole.() -> Unit) {
        hole = Hole().apply(block)
    }

    fun everywhereCancelable(block: () -> Boolean) {
        everywhereCancelable = block()
    }

    internal fun notifyHoleClickListener(controller: Controller){
        hole?.notifyClickListener(controller)
    }
    internal fun notifyOverlayWithHoleDrawListener(
        guideController: Controller?,
        overlayView: View?,
        guideCanvas: Canvas?,
        holeRectF: RectF?,
        highlightDrew: Boolean = false,
        overlayInflated: Boolean = false) {
        overlayWithHoleDrawListener?.overlayWithHoleListener(
            guideController,
            overlayView,
            guideCanvas,
            holeRectF)
        if (highlightDrew) {
            hole?.notifyHighlightDrewListener(guideCanvas!!, holeRectF!!)
        }
        if (overlayInflated) {
            overlay?.notifyOverlayInflatedListener(overlayView!!, guideController!!)
        }
    }

    fun overlayWithHoleDrawListener(block: (Controller?, View?, Canvas?, RectF?) -> Unit) {
        overlayWithHoleDrawListener = object :
            OverlayWithHoleDrawListener {
            override fun overlayWithHoleListener(
                guideController: Controller?,
                overlayView: View?,
                guideCanvas: Canvas?,
                holeRectF: RectF?) {
                block(guideController, overlayView, guideCanvas, holeRectF)
            }
        }
    }

    interface OverlayWithHoleDrawListener {
        fun overlayWithHoleListener(
            guideController: Controller?,
            overlayView: View?,
            guideCanvas: Canvas?,
            holeRectF: RectF?)
    }
}