package com.aqrlei.guide.core

import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.RectF
import android.view.View
import android.view.ViewGroup
import kotlin.math.max
import kotlin.math.min

/**
 * Created by AqrLei on 2019-08-20
 */
class Hole {
    var holeView: View? = null
    var paddingLeft: Int = 0
    var paddingTop: Int = 0
    var paddingRight: Int = 0
    var paddingBottom: Int = 0

    var shape: Shape =
        Shape.RECTANGLE

    /**
     * [Hole.Shape.CIRCLE] radius 为半径
     * [Hole.Shape.RECTANGLE] radius 为顶角弧度
     * */
    var radius: Int = 0

    var onClickListener: OnHoleClickListener? = null

    var onHighlightDrewListener: OnHighlightDrewListener? = null

    internal var holeCanvas: Canvas? = null
    internal var holeRectF: RectF? = null
    fun notifyClickListener(controller: Controller?) {
        onClickListener?.onHoleClickListener(controller, holeView)
    }

    fun notifyHighlightDrewListener(canvas: Canvas, rectF: RectF) {
        holeCanvas = canvas
        holeRectF = rectF
        onHighlightDrewListener?.onHighlightDrew(canvas, rectF)
    }

    fun setHoleView(block: () -> View) {
        holeView = block()
    }

    fun setPadding(padding: Int) {
        setPadding(padding, padding, padding, padding)
    }

    fun setPadding(block: () -> Int) {
        val padding = block()
        setPadding(padding, padding, padding, padding)
    }

    fun setPadding(left: Int, top: Int, right: Int, bottom: Int) {
        paddingLeft = left
        paddingTop = top
        paddingRight = right
        paddingBottom = bottom
    }

    fun setShape(block: () -> Shape) {
        this.shape = block()
    }

    fun setRadius(block: () -> Int) {
        this.radius = block()
    }

    fun setOnClickListener(block: (controller: Controller?, v: View?) -> Unit) {
        onClickListener = object :
            OnHoleClickListener {
            override fun onHoleClickListener(controller: Controller?, view: View?) {
                block(controller, view)
            }
        }
    }


    fun onHighlightDrewListener(block: (Canvas, RectF) -> Unit) {
        onHighlightDrewListener = object :
            OnHighlightDrewListener {
            override fun onHighlightDrew(canvas: Canvas, rectF: RectF) {
                block(canvas, rectF)
            }
        }
    }

    fun getRectF(target: View?): RectF? {
        holeView ?: return null
        target ?: return null
        return if (target == holeView) {
            val resultRect = Rect()
            holeView!!.getHitRect(resultRect)
            getTargetResultRect(resultRect, true)
        } else {
            val tempRect = getNotSameAsHoleTempResult(target, holeView!!)
            getTargetResultRect(tempRect, false)
        }
    }

    private fun getNotSameAsHoleTempResult(target: View, holeView: View): Rect {
        val resultRect = Rect()
        val tempRect = Rect()
        val hostViewRect = Rect()
        target.getHitRect(hostViewRect)
        var tempView = holeView
        while (tempView != target) {
            tempView.getHitRect(tempRect)
            resultRect.left += if (tempRect.left % hostViewRect.right == 0) 0 else tempRect.left
            resultRect.top += if (tempRect.top % hostViewRect.bottom == 0) 0 else tempRect.top
            tempView = (tempView.parent as? ViewGroup) ?: tempView
        }
        tempView.getHitRect(tempRect)
        resultRect.bottom = resultRect.top + holeView.height
        resultRect.right = resultRect.left + holeView.width
        return resultRect
    }

    private fun getTargetResultRect(tempRect: Rect, sameAsHole: Boolean): RectF {
        return RectF(tempRect).apply {
            if (!sameAsHole) {
                when {
                    paddingLeft > 0 -> {
                        left = max(0F, left - paddingLeft)
                    }
                    paddingTop > 0 -> {
                        top = max(0F, top - paddingTop)
                    }

                    paddingRight > 0 -> {
                        right = min(tempRect.right.toFloat(), right + paddingRight)
                    }
                    paddingBottom > 0 -> {
                        bottom = min(tempRect.bottom.toFloat(), bottom + paddingBottom)
                    }
                }
            }
        }
    }

    enum class Shape {
        CIRCLE, RECTANGLE, OVAL
    }

    interface OnHighlightDrewListener {
        fun onHighlightDrew(canvas: Canvas, rectF: RectF)
    }

    interface OnHoleClickListener {
        fun onHoleClickListener(controller: Controller?, view: View?)
    }

}